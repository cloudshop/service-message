package com.sdyk.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.MessageProperties;
import com.sdyk.constants.Constants;
import com.sdyk.packet.msg.Msg;
import com.sdyk.session.SessionManager;
import com.sdyk.vo.SessionVo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.*;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by taylor on 2018/4/23.
 */
public class BusinessThreadUtil {
    private static final Logger logger = Logger.getLogger(BusinessThreadUtil.class.getName());
    //CPU核数4-10倍
    private final static int size=4;
    private  static ExecutorService workerThreadService = Executors.newFixedThreadPool(size);
    public static void doBusiness(ChannelHandlerContext ctx,String msgStr) {
        //异步线程池处理
        workerThreadService.submit( () -> {
            handlerWebSocketFrame(ctx,msgStr);
        });
    }

    public static void handlerWebSocketFrame(ChannelHandlerContext ctx,String msgStr) {
        long start = System.currentTimeMillis();
        try {
            // 返回应答消息
            Msg basePacket = null;

            if (msgStr != null) {
                logger.info("服务端收到：" + msgStr);
                basePacket = JSON.parseObject(msgStr, new TypeReference<Msg>() {
                });
                Date timestamp=new Date();
                if (basePacket != null && basePacket.getType().equals("chat")) {
                    if (basePacket.getRequest() != null && basePacket.getReceived() == null && basePacket.getRequest().equals("request")) {
                        //save消息
                        basePacket.setTimestamp(timestamp);
                        basePacket.setRequest("request");

                        RabbitMqUtil.sendQueen("", Constants.IM_CHAT_LIST_QUEUE+Constants.IM_HOST, MessageProperties.PERSISTENT_TEXT_PLAIN, JSON.toJSONString(basePacket).getBytes());
                        RabbitMqUtil.sendQueen("", Constants.IM_MSG_SAVE_AND_ACK_QUEUE, MessageProperties.PERSISTENT_TEXT_PLAIN, JSON.toJSONString(basePacket).getBytes());
                        Msg ackMsg = new Msg();
                        ackMsg.setAckId(basePacket.getAckId());
                        ackMsg.setTimestamp(basePacket.getTimestamp());
                        ackMsg.setReceived("received");
                        ackMsg.setPacketType("msg");
                        ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(ackMsg)));
                        String ackId=basePacket.getAckId();
                        basePacket.setAckId(null);
                        proccessRoute(basePacket);
                    } else if (basePacket.getReceived() != null && basePacket.getReceived().equals("received")) {
                        //接受者已收到，删除离线消息
                        RabbitMqUtil.sendQueen("", Constants.IM_MSG_SAVE_AND_ACK_QUEUE, new AMQP.BasicProperties.Builder().expiration(String.valueOf(Constants.TIME_DELAY_SECOND)).deliveryMode(Constants.DELIVERYMODE_PERSISTENT).build(), JSON.toJSONString(basePacket).getBytes());
                    }

                    return;
                }

                if (basePacket != null && basePacket.getType().equals("groupchat")) {
                    if (basePacket.getRequest() != null && basePacket.getRequest().equals("request")) {
                        //save消息
                        basePacket.setRequest("request");
                        basePacket.setAckId(null);
                        basePacket.setTimestamp(timestamp);

                        RabbitMqUtil.sendQueen("", Constants.IM_MSG_SAVE_AND_ACK_QUEUE, MessageProperties.PERSISTENT_TEXT_PLAIN, JSON.toJSONString(basePacket).getBytes());
                        Msg ackMsg = new Msg();
                        ackMsg.setAckId(basePacket.getAckId());
                        ackMsg.setReceived("received");
                        ackMsg.setPacketType("msg");
                        ackMsg.setTimestamp(basePacket.getTimestamp());
                        ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(ackMsg)));
                        proccessRouteByGroup(basePacket);
                    } else if (basePacket.getReceived() != null && basePacket.getReceived().equals("received")) {
                        //接受者已收到，删除离线消息
                        RabbitMqUtil.sendQueen("", Constants.IM_MSG_SAVE_AND_ACK_QUEUE, new AMQP.BasicProperties.Builder().expiration(String.valueOf(Constants.TIME_DELAY_SECOND)).deliveryMode(Constants.DELIVERYMODE_PERSISTENT).build(), JSON.toJSONString(basePacket).getBytes());
                    }

                }

            }

        } catch (Exception e) {
            logger.info("发送数据异常" + e);
            e.printStackTrace();
        }
    }

    private static List<String> proccessRouteByGroup(Msg msgVo) {
        List<String> list = new ArrayList<String>();
        try {
            List<String> groupUserList = RedisUtils.lRangeAll(Constants.IM_GROUP_MEMBERS + msgVo.getTo());
            if (groupUserList != null && groupUserList.size() > 0) {
                RabbitMqUtil.sendQueen("", Constants.IM_MSG_PUSH_NOTICE_QUEUE, MessageProperties.PERSISTENT_TEXT_PLAIN, JSON.toJSONString(msgVo).getBytes());

                String from = msgVo.getTo();
                Iterator<String> iterator = groupUserList.iterator();
                while (iterator.hasNext()) {
                    String user = iterator.next().replaceAll("\"", "");
                    String hostIdentity = BalanceRouteUtil.getBalanceRouteHostIdentity(user);
                    if (hostIdentity != null && !"".equals(hostIdentity)) {
                        List<String> onLineUserList = RedisUtils.scan(Constants.IM_ONLINE_PREFIX_ + hostIdentity + "_" + user, Integer.MAX_VALUE);
                        if (onLineUserList != null&& onLineUserList.size()>0) {
                            //如果接收方在连接在发送这连接的服务器
                            Iterator<String> onLineUserIterator = onLineUserList.iterator();
                            while (onLineUserIterator.hasNext()) {
                                Object obj = RedisUtils.get(onLineUserIterator.next());
                                if (obj != null) {
                                    SessionVo sessionVo = (SessionVo) obj;
                                    if (hostIdentity.equals(Constants.HOST_IDENTITY)) {
                                        io.netty.channel.Channel nettyChannel = SessionManager.getChannel(sessionVo.getChannelId());
                                        String msgResultStr = JSON.toJSONString(msgVo);
                                        if (nettyChannel != null) {
                                            nettyChannel.writeAndFlush(new TextWebSocketFrame(msgResultStr));
                                        }
                                    } else {
                                        String queneName = Constants.IM_MSG_QUEUE + hostIdentity;
                                        msgVo.setTo(sessionVo.getUserName());
                                        RabbitMqUtil.sendQueen("", queneName, MessageProperties.PERSISTENT_TEXT_PLAIN, JSON.toJSONString(msgVo).toString().getBytes("UTF-8"));
                                    }
                                }
                            }

                        }
                    }

                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return list;
    }

    private  static void proccessRoute(Msg msgVo) {
        try {
            String user = msgVo.getTo();
            String hostIdentity = BalanceRouteUtil.getBalanceRouteHostIdentity(user);
            if (hostIdentity != null && !"".equals(hostIdentity)) {
                RabbitMqUtil.sendQueen("", Constants.IM_MSG_PUSH_NOTICE_QUEUE, MessageProperties.PERSISTENT_TEXT_PLAIN, JSON.toJSONString(msgVo).getBytes());
                List<String> onLineUserList = RedisUtils.scan(Constants.IM_ONLINE_PREFIX_ + hostIdentity + "_" + user, Integer.MAX_VALUE);
                if (onLineUserList != null && onLineUserList.size()>0) {
                    //如果接收方在连接在发送这连接的服务器
                    Iterator<String> onLineUserIterator = onLineUserList.iterator();
                    while (onLineUserIterator.hasNext()) {
                        Object obj = RedisUtils.get(onLineUserIterator.next());
                        if (hostIdentity.equals(Constants.HOST_IDENTITY)) {
                            if (obj != null) {
                                SessionVo sessionVo = (SessionVo) obj;
                                io.netty.channel.Channel nettyChannel = SessionManager.getChannel(sessionVo.getChannelId());
                                String msgResultStr = JSON.toJSONString(msgVo);
                                logger.info("推送消息：    "+msgResultStr);
                                if (nettyChannel != null) {
                                    TextWebSocketFrame s=new TextWebSocketFrame(msgResultStr);
                                    nettyChannel.writeAndFlush(s);
                                }
                            }
                        } else {
                            if (obj != null) {
                                SessionVo sessionVo = (SessionVo) obj;
                                msgVo.setTo(sessionVo.getUserName());
                                String queneName = Constants.IM_MSG_QUEUE + hostIdentity;
                                RabbitMqUtil.sendQueen("", queneName, MessageProperties.PERSISTENT_TEXT_PLAIN, JSON.toJSONString(msgVo).toString().getBytes("UTF-8"));
                            }
                        }
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
