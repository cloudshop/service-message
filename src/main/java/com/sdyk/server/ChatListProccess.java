package com.sdyk.server;

import cn.jpush.api.push.PushResult;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCursor;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.sdyk.constants.Constants;
import com.sdyk.packet.msg.Msg;
import com.sdyk.util.JGPushUtil;
import com.sdyk.util.MongoDBUtil;
import com.sdyk.util.RabbitMqUtil;
import com.sdyk.util.RedisUtils;
import com.sdyk.vo.Group;
import com.sdyk.vo.IMUserInfo;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by taylor on 2018/4/14.
 */
public class ChatListProccess {
    private static int size = 4;
    private ExecutorService workerThreadService = Executors.newFixedThreadPool(size);

    private final Logger Log = Logger.getLogger(RabbitMqUtil.class);
    private String queneName = Constants.IM_CHAT_LIST_QUEUE+Constants.IM_HOST;


    public void start() {
        new Thread() {
            public void run() {
                try {
                    Channel channel = RabbitMqUtil.getChannel();
                    channel.queueDeclare(queneName, false, false, true, null);

                    // 创建队列消费者
                    QueueingConsumer consumer = new QueueingConsumer(channel);

                    // 设置最大服务消息接收数量
                    int prefetchCount = 1;
                    try {
                        channel.basicQos(prefetchCount);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    boolean ack = false; // 是否自动确认消息被成功消费
                    //String queneName = Constants.IM_CHAT_LIST_QUEUE+Constants.IM_HOST;
                    try {
                        channel.basicConsume(queneName, ack, consumer); // 指定消费队列
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    while (true) {
                        // nextDelivery是一个阻塞方法（内部实现其实是阻塞队列的take方法）
                        QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                        if (delivery != null) {
                            String message = new String(delivery.getBody());
//                            final QueueingConsumer.Delivery finalDelivery = delivery;
                            workerThreadService.submit(() -> {
                                chatInfoProccess(channel, delivery.getEnvelope().getDeliveryTag(),message);
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

    private void chatInfoProccess(Channel channel,long deliveryTag,String message) {
        try {
            Msg msgVo = null;
            msgVo = JSON.parseObject(message, new TypeReference<Msg>() {
            });
            long time=System.currentTimeMillis();
            if (msgVo.getType() != null && msgVo.getType().equals("chat") && msgVo.getReceived() == null) {
//                long fromRank=RedisUtils.zRank(Constants.IM_CHAT_LIST_PREFIX_+msgVo.getFrom(),msgVo.getTo());
//
//                if(fromRank>=0){
                    RedisUtils.zadd(Constants.IM_CHAT_LIST_PREFIX_+msgVo.getFrom(), time, msgVo.getTo());
                    RedisUtils.set(Constants.IM_CHAT_INFO_PREFIX_+msgVo.getFrom()+"_"+msgVo.getTo() ,JSON.toJSON(msgVo));
//                }
//                long toRank=RedisUtils.zRank(Constants.IM_CHAT_LIST_PREFIX_+msgVo.getTo(),msgVo.getFrom());

//                if(toRank>=0){
                    RedisUtils.zadd(Constants.IM_CHAT_LIST_PREFIX_+msgVo.getTo(), time, msgVo.getFrom());
                    RedisUtils.set(Constants.IM_CHAT_INFO_PREFIX_+msgVo.getTo()+"_"+msgVo.getFrom() ,JSON.toJSON(msgVo));
//                }

            } else {


            }
        } catch (Exception e) {
            Log.error("聊天列表异常：" + message, e);
        }finally {
            try {
                channel.basicAck(deliveryTag, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
