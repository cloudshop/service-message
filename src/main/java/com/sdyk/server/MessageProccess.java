package com.sdyk.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.rabbitmq.client.*;
import com.sdyk.constants.Constants;
import com.sdyk.db.Msgdb;
import com.sdyk.packet.msg.Msg;
import com.sdyk.util.RabbitMqUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * Created by taylor on 2018/4/14.
 */
public class MessageProccess {
    private static int size = 4;
    private ExecutorService workerThreadService = Executors.newFixedThreadPool(size);

    private final Logger Log = Logger.getLogger(RabbitMqUtil.class);
    private String queneName = Constants.IM_MSG_SAVE_AND_ACK_QUEUE;



    public void start() {
        new Thread() {
            public void run() {
                Channel channel = RabbitMqUtil.getChannel();
                try {
                    channel.queueDeclare(queneName, false, false, true, null);
//                    channel = connection.createChannel();
                    // 创建队列消费者
                    QueueingConsumer consumer = new QueueingConsumer(channel);

                    // 设置最大服务消息接收数量
                    int prefetchCount = 1;
                    channel.basicQos(prefetchCount);

                    boolean ack = false; // 是否自动确认消息被成功消费

                    channel.basicConsume(queneName, ack, consumer); // 指定消费队列
                    // nextDelivery是一个阻塞方法（内部实现其实是阻塞队列的take方法）
                    while (true) {
                        QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                        if (delivery != null) {
                            String message = new String(delivery.getBody());
                            workerThreadService.submit(() -> {
                                process(channel,delivery.getEnvelope().getDeliveryTag(),message);
                            });
//                            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                        }
                    }
                } catch (IOException e) {
                    Log.error("保存单聊、群聊、ack异常：", e);
                } catch (InterruptedException e) {
                    Log.error("保存单聊、群聊、ack异常：", e);
                }
            }
        }.start();
    }

    public void process(Channel channel,long deliveryTag,String message) {
        try {
            Msg msgVo = JSON.parseObject(message, new TypeReference<Msg>() {
            });
            if (msgVo.getType() != null && msgVo.getType().equals("chat") && msgVo.getReceived() == null) {
                msgVo.setAckId(null);
                Msgdb.saveMsg(JSON.toJSONString(msgVo));
            } else if (msgVo.getType() != null && msgVo.getType().equals("groupchat") && msgVo.getReceived() == null) {
                msgVo.setAckId(null);
                Msgdb.saveAllMsg(JSON.toJSONString(msgVo));
            } else if (msgVo.getReceived() != null && msgVo.getReceived().equals("received")) {
                String msgId = "";
                if (msgVo.getMsgId() != null && !"".equals(msgVo.getMsgId())) {
                    String msgIdArray[] = msgVo.getMsgId().split(",");
                    for (int i = 0; i < msgIdArray.length; i++) {
//                                        System.out.println("ack  确认进入.....");
                        if (msgIdArray[i] != null) {
                            long count = Msgdb.getCount(msgVo.getTo(), msgIdArray[i]);
                            if (count > 0) {
                                Msgdb.delMsg(msgVo.getTo(), msgVo.getMsgId());
                            } else {
                                msgVo.setMsgId(msgIdArray[i]);
                                if (msgVo.getProccessSum() == null) {
                                    msgVo.setProccessSum(1);
                                } else {
                                    msgVo.setProccessSum(msgVo.getProccessSum() + 1);
                                }
                                //处理十次之后还是没有保存发送记录，丢弃该ack
                                if (msgVo.getProccessSum() < 10) {
                                    RabbitMqUtil.sendQueen("", queneName, new AMQP.BasicProperties.Builder().expiration(String.valueOf(Constants.TIME_DELAY_SECOND)).deliveryMode(Constants.DELIVERYMODE_PERSISTENT).build(), JSON.toJSONString(msgVo).getBytes());
                                }

                            }
                        }
                    }
                }
            }


        } catch (Exception e) {
            Log.error("保存单聊、群聊、ack异常：", e);
        }finally {
//            deliveryTag=delivery.getEnvelope().getDeliveryTag()
            try {
                channel.basicAck(deliveryTag, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
