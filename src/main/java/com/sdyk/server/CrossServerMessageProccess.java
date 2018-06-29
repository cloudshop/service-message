package com.sdyk.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.rabbitmq.client.*;
import com.sdyk.constants.Constants;
import com.sdyk.packet.msg.Msg;
import com.sdyk.session.SessionManager;
import com.sdyk.util.BalanceRouteUtil;
import com.sdyk.util.RabbitMqUtil;
import com.sdyk.util.RedisUtils;
import com.sdyk.vo.SessionVo;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by taylor on 2018/4/14.
 */
public class CrossServerMessageProccess {
    private static int size = 4;
    private ExecutorService workerThreadService = Executors.newFixedThreadPool(size);
    private final Logger Log = Logger.getLogger(RabbitMqUtil.class);

    private String queneName = Constants.IM_MSG_QUEUE + Constants.HOST_IDENTITY;


    public void start() {
        new Thread() {
            public void run() {
                Channel channel = RabbitMqUtil.getChannel();
                try {
                    channel.queueDeclare(queneName, false, false, true, null);
                    // 创建队列消费者
                    QueueingConsumer consumer = new QueueingConsumer(channel);

                    // 设置最大服务消息接收数量
                    int prefetchCount = 1;
                    channel.basicQos(prefetchCount);

                    boolean ack = false; // 是否自动确认消息被成功消费
                    String queneName = Constants.IM_MSG_QUEUE + Constants.HOST_IDENTITY;
                    channel.basicConsume(queneName, ack, consumer); // 指定消费队列
                    while (true) {
                        QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                        if (delivery != null) {
                            String message = new String(delivery.getBody());
                            workerThreadService.submit(() -> {
                                crossServerMessage( channel,delivery.getEnvelope().getDeliveryTag(),message);
                            });

                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }.start();
    }

    public void crossServerMessage(Channel channel,long deliveryTag,String message) {
        try {
            // nextDelivery是一个阻塞方法（内部实现其实是阻塞队列的take方法）

            Msg msgVo = null;
            msgVo = JSON.parseObject(message, new TypeReference<Msg>() {
            });
            String user = msgVo.getTo();
            String hostIdentity = BalanceRouteUtil.getBalanceRouteHostIdentity(user);
            if (hostIdentity != null && !"".equals(hostIdentity)) {

                List<String> onLineUserList = RedisUtils.scan(Constants.IM_ONLINE_PREFIX_ + hostIdentity + "_" + user, Integer.MAX_VALUE);
                if (onLineUserList != null) {
                    //如果接收方在连接在发送这连接的服务器

                    Iterator<String> onLineUserIterator = onLineUserList.iterator();
                    while (onLineUserIterator.hasNext()) {
                        Object obj = RedisUtils.get(onLineUserIterator.next());
                        if (hostIdentity.equals(Constants.HOST_IDENTITY)) {
                            if (obj != null) {
                                SessionVo sessionVo = (SessionVo) obj;
                                msgVo.setRequest("request");
                                msgVo.setTimestamp(new Date());
                                io.netty.channel.Channel nettyChannel = SessionManager.getChannel(sessionVo.getChannelId());
                                String msgResultStr = JSON.toJSONString(msgVo);
                                if (nettyChannel != null) {
                                    nettyChannel.writeAndFlush(new TextWebSocketFrame(msgResultStr));
                                }
                            }
                        }
                    }

                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //deliveryTag=delivery.getEnvelope().getDeliveryTag()
            try {
                channel.basicAck(deliveryTag, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
