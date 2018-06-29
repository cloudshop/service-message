package com.sdyk.server;

import cn.jpush.api.push.PushResult;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCursor;
import com.rabbitmq.client.*;
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
import java.util.concurrent.*;

/**
 * Created by taylor on 2018/4/14.
 */
public class PushNoticeProccess {
    private static int size = 4;
    private ExecutorService workerThreadService = Executors.newFixedThreadPool(size);

    private final Logger Log = Logger.getLogger(RabbitMqUtil.class);
    private String queneName = Constants.IM_MSG_PUSH_NOTICE_QUEUE;


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
                    String queneName = Constants.IM_MSG_PUSH_NOTICE_QUEUE;
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
                                push(channel, delivery.getEnvelope().getDeliveryTag(),message);
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

    private void push(Channel channel,long deliveryTag,String message) {
        try {

//            Log.info("delivery:::::" + message);
//            if (delivery != null) {
            Msg msgVo = null;
            msgVo = JSON.parseObject(message, new TypeReference<Msg>() {
            });
            if (msgVo.getType() != null && msgVo.getType().equals("chat") && msgVo.getReceived() == null) {
                BasicDBObject toquery = new BasicDBObject();
                toquery.append("user", msgVo.getTo());
                DBObject toobj = MongoDBUtil.instance.findOne(Constants.IM_USER_MOBILE_TYPE_NO, toquery);
                BasicDBObject fromquery = new BasicDBObject();
                fromquery.append("user", msgVo.getTo());
                DBObject fromobj = MongoDBUtil.instance.findOne(Constants.IM_USER_MOBILE_TYPE_NO, fromquery);
                IMUserInfo fromVo = null;
                IMUserInfo toVo = null;
                if (toobj != null) {
                    String strObj = toobj.toString();
                    toVo = JSON.parseObject(strObj, new TypeReference<IMUserInfo>() {
                    });

                }
                if (fromobj != null) {
                    String strObj = fromobj.toString();
                    fromVo = JSON.parseObject(strObj, new TypeReference<IMUserInfo>() {
                    });

                }
                if (toVo != null && toVo.getMobileTypeNo() != null && !"".equals(toVo.getMobileTypeNo())) {
                    long start = System.currentTimeMillis();
                    Map<String, String> exeras = new HashMap<String, String>();
                    exeras.put("to", msgVo.getTo());
                    exeras.put("from", msgVo.getFrom());
                    exeras.put("packetType", "msg");
                    exeras.put("msgType", msgVo.getMsgType());
                    exeras.put("type", "groupchat");
                    Log.info("进入发送通知5");
                    if (msgVo.getMsgType() != null) {
                        String msgStr = "";
//                                                file(文件)、text(文本)、vedio(视频)、voice(音频)、map(地图)、expression(表情)
                        if (msgVo.getMsgType().equals("text")) {
                            msgStr = msgVo.getMessage();
                        } else if (msgVo.getMsgType().equals("file")) {
                            msgStr = "[文件]";
                        } else if (msgVo.getMsgType().equals("vedio")) {
                            msgStr = "[视频]";
                        } else if (msgVo.getMsgType().equals("voice")) {
                            msgStr = "[语音]";
                        } else if (msgVo.getMsgType().equals("map")) {
                            msgStr = "[地图]";
                        }
                        PushResult p = JGPushUtil.send(toVo.getMobileTypeNo(), msgStr, fromVo.getNickName(), exeras);
                        long end = System.currentTimeMillis();
                        Log.info(JSON.toJSONString(p) + "耗时:" + (end - start));
                    }
                }
            } else {

                List<String> groupUserList = RedisUtils.lRangeAll(Constants.IM_GROUP_MEMBERS + msgVo.getTo());
                BasicDBList condList = new BasicDBList();
                BasicDBObject queryFilter = new BasicDBObject();
                if (groupUserList != null && groupUserList.size() > 0) {
                    Iterator<String> iterator = groupUserList.iterator();
                    while (iterator.hasNext()) {
                        String user = iterator.next().replaceAll("\"", "");
                        if (!user.equals(msgVo.getFrom())) {
                            BasicDBObject toquery = new BasicDBObject();
                            toquery.append("user", user);
                            condList.add(toquery);
                        }
                    }
                    if (condList != null && condList.size() > 0) {
                        queryFilter.put("$or", condList);
                        MongoCursor<DBObject> toobjList = MongoDBUtil.instance.findAllByCondition(Constants.IM_USER_MOBILE_TYPE_NO, queryFilter, null);
                        Object groupObj = RedisUtils.getString(Constants.IM_GROUP + msgVo.getTo());

                        List<String> pushList = new ArrayList<String>();
                        while (toobjList.hasNext()) {
                            //传入类参数，反回该类对象
                            DBObject toobj = toobjList.next();
                            if (toobj != null) {
                                IMUserInfo toVo = null;
                                if (toobj != null) {
                                    String strObj = toobj.toString();
                                    toVo = JSON.parseObject(strObj, new TypeReference<IMUserInfo>() {
                                    });
                                    if (toVo != null && toVo.getMobileTypeNo() != null && !"".equals(toVo.getMobileTypeNo())) {
                                        pushList.add(toVo.getMobileTypeNo());
                                    }

                                }

                            }
                        }
                        Group fromVo = null;
                        if (groupObj != null) {
                            String strObj = groupObj.toString();
                            strObj = strObj.replace("\\\"", "\"").replace("\"{", "{").replace("}\"", "}");
                            fromVo = JSON.parseObject(strObj, new TypeReference<Group>() {
                            });

                        }
                        if (pushList != null && pushList.size() > 0) {
                            long start = System.currentTimeMillis();
                            Map<String, String> exeras = new HashMap<String, String>();
                            exeras.put("to", msgVo.getTo());
                            exeras.put("from", msgVo.getFrom());
                            exeras.put("packetType", "msg");
                            exeras.put("msgType", msgVo.getMsgType());
                            exeras.put("type", "groupchat");
                            if (msgVo.getMsgType() != null) {
                                String msgStr = "";
//                                                file(文件)、text(文本)、vedio(视频)、voice(音频)、map(地图)、expression(表情)
                                if (msgVo.getMsgType().equals("text")) {
                                    msgStr = msgVo.getMessage();
                                } else if (msgVo.getMsgType().equals("file")) {
                                    msgStr = "[文件]";
                                } else if (msgVo.getMsgType().equals("vedio")) {
                                    msgStr = "[视频]";
                                } else if (msgVo.getMsgType().equals("voice")) {
                                    msgStr = "[语音]";
                                } else if (msgVo.getMsgType().equals("map")) {
                                    msgStr = "[地图]";
                                }
                                PushResult p = JGPushUtil.sendList(pushList, msgStr, fromVo.getGroupName(), exeras);
                                long end = System.currentTimeMillis();
                                Log.info(JSON.toJSONString(p) + "耗时:" + (end - start));
                            }

                        }

                    }
                }

            }


//            }
        } catch (Exception e) {
            Log.error("推送通知处理异常：" + message, e);
        }finally {
            try {
                channel.basicAck(deliveryTag, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
