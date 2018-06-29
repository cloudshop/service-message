package com.sdyk.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.sdyk.config.SystemInitConfig;
import com.sdyk.constants.Constants;
import com.sdyk.packet.BasePacket;
import com.sdyk.packet.heart.Heart;
import com.sdyk.packet.msg.Msg;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.redis.RedisArrayAggregator;
import org.java_websocket.WebSocket;
import org.java_websocket.client.DefaultSSLWebSocketClientFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import javax.net.ssl.*;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * Created by taylor on 2018/4/18.
 */
public class Test {
    public static int aa = 0;

    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };


    /**
     * Trust every server - dont check for any certificate
     */
    private static void trustAllHosts(WebSocketClient appClient) {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }


            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }


            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        }};


        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            appClient.setWebSocketFactory(new DefaultSSLWebSocketClientFactory(sc));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        SystemInitConfig.init();
//        List<Object> msgList = new ArrayList<Object>();
//
//        for(int i=0;i<10000000;i++) {
//            long time=System.currentTimeMillis();
//            String str = "{\"msgId\":\"" + i + "\",\"ackId\":\"" + i + "\",from:\"admin\",to:\"test\",message:\"我是test" + i + "\",request:\"request\",type:\"chat\",packetTye:\"msg\",msgType:\"text\",timestamp:\""+time+"\"}";
//
//            Msg msg = null;
//            msg = JSON.parseObject(str, new TypeReference<Msg>() {
//            });
//
//            msgList.add(msg);
//            if(i%50000==0) {
//                MongoDBUtil.instance.saveAll(msgList, Constants.MONGODB_MSG_HISTORY);
//                System.out.println("保存成功");
//                msgList= new ArrayList<Object>();
//            }
//
//        }
        String str = "\"{\"" + "\"{\"";
        System.out.println(str);
        System.out.println(str.replace("\"{", "{"));

//        for (int i=0;i<10000;i++) {
//        String str="{ackId:\"10000\",from:\"test\",to:\"admin\",message:\"我是test\",request:\"request\",type:\"chat\",packetTye:\"msg\",msgType:\"text\"}";
//        System.out.println((str.getBytes().length*100000));

//                    try {
//                        WebSocketClient client=null;
//                        client = new WebSocketClient(new URI("wss://testsocketmachine1.315free.com/ws?admin"),new Draft_17()) {
//
//                            @Override
//                            public void onOpen(ServerHandshake arg0) {
//                                System.out.println("打开链接");
//                            }
//
//                            @Override
//                            public void onMessage(String arg0) {
//                                System.out.println("::::::::::::::::"+arg0);
//                            }
//
//                            @Override
//                            public void onError(Exception arg0) {
//                                arg0.printStackTrace();
//                                System.out.println("发生错误已关闭");
//                            }
//
//                            @Override
//                            public void onClose(int arg0, String arg1, boolean arg2) {
//                                System.out.println("链接已关闭");
//                            }
//
//                            @Override
//                            public void onMessage(ByteBuffer bytes) {
//                                try {
//                                    System.out.println(new String(bytes.array(),"utf-8"));
//                                } catch (UnsupportedEncodingException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//
//                        };
//                        trustAllHosts(client);
//                        client.connectBlocking();
////                        while(!client.getReadyState().equals(WebSocket.READYSTATE.OPEN)){
////                            System.out.println("还没有打开");
////                        }
////                        System.out.println("打开了");
//                        long start=System.currentTimeMillis();
//                        int j=0;
//                        for(int i=0;i<10000000;i++) {
//                            j=i;
//                            String str="{\"msgId\":\""+i+"\",\"ackId\":\""+i+"\",from:\"admin\",to:\"test\",message:\"我是test"+i+"\",request:\"request\",type:\"chat\",packetTye:\"msg\",msgType:\"text\"}";
//
//                            client.send(str);
//                            Thread.sleep(10);
//                        }
//                        long end=System.currentTimeMillis();
//                        System.out.println(j+"耗时"+(end-start));
//
//                    } catch (URISyntaxException e) {
//                        e.printStackTrace();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }


//            }.start();


//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        try {
//            Thread.sleep(10000000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

//        String msgStr="{\"ackId\":\"95a46cd8-f039-4a04-83cb-98cdb3940a60\",\"packetType\":\"ping\",\"ping\":\"ping\"}";
//        BasePacket basePacket= JSON.parseObject(msgStr, new TypeReference<BasePacket>() {
//        });
//        if(msgStr!=null&& !"".equals(msgStr) && msgStr.indexOf(Constants.HEAR_PING)>=0) {
//            System.out.println("222222222");
//            //心跳检测包
//            if (basePacket != null && basePacket.getPacketType() != null && basePacket.getPacketType().equals("ping")) {
//                Heart heart = JSON.parseObject(msgStr, new TypeReference<Heart>() {
//                });
//                heart.setPing(null);
//                heart.setPong("pong");
//                System.out.println("222222222");
//                return;
//            }
//        }
        SystemInitConfig.init();
        RedisUtils.set("IM_TMP_SSSSS", "sssssssss");
        for (int i = 1; i < 15; i++) {
            RedisUtils.zadd("IM_TMP_TEST", System.currentTimeMillis() + (100 * i), "IM_TMP_TEST11111111111" + i);
        }
        Set<String> strSet = RedisUtils.zRevRange("IM_TMP_TEST", 0, 20);
        System.out.println(strSet.size());
        Iterator<String> iterator = strSet.iterator();
        while (iterator.hasNext()) {
            String value = iterator.next();
            System.out.println(value);
        }
        System.out.println();
        long start = System.currentTimeMillis();
        long rank = RedisUtils.zRank("IM_TMP_TEST", "IM_TMP_TEST11111111111" + 12);
        long zrem = RedisUtils.zRem("IM_TMP_TEST", "IM_TMP_TEST11111111111" + 12);

        long end = System.currentTimeMillis();

        System.out.println("))))))))))))))" + (end - start));
//        System.out.println(zrem+">>>>>>>>>"+rank);
        Set<String> strSet1 = RedisUtils.zRevRange("IM_TMP_TEST", 0, 20);
        System.out.println(strSet1.size());
        Iterator<String> iterator1 = strSet1.iterator();
        while (iterator1.hasNext()) {
            String value = iterator1.next();
            System.out.println(value);
        }

    }
}
