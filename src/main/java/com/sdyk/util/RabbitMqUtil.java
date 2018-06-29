package com.sdyk.util;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.sdyk.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by taylor on 2018/4/2.
 */
public class RabbitMqUtil {
    private static final Logger Log = LoggerFactory.getLogger(RabbitMqUtil.class);
//    public static Channel channel;
    public static ConnectionFactory factory;
    private RabbitMqUtil(){

    }
    public static  void init() {
        try {
            Log.info("rabbitmq创建连接开始................");
            //创建一个连接工厂 connection factory
             factory = new ConnectionFactory();
            //设置rabbitmq-server服务IP地址
            factory.setHost(Constants.RABBITMQ_HOST);
            factory.setUsername(Constants.RABBITMQ_USERNAME);
            factory.setPassword(Constants.RABBITMQ_PASSWORD);
            factory.setPort(Integer.valueOf(Constants.RABBITMQ_PORT));
            factory.setVirtualHost(Constants.RABBITMQ_VIRTUALHOST);

//            channel = factory.newConnection().createChannel();

            Log.info("rabbitmq创建连接结束................");
        } catch (Exception e) {
            e.printStackTrace();
            Log.info("连接rabbitmq异常："+e);
        }
    }
    private static Channel channel=null;

    public static Channel getChannel() {
        Channel channel=null;
        try {
            if(channel==null) {
                init();
                channel = factory.newConnection().createChannel();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return channel;
    }
    public static void deClareQueue(String queueName){
        Channel channel=getChannel();
        try {
            channel.queueDeclare(queueName, false, false, false, null);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
    }
    public static void sendQueen(String exChange, String queneName, AMQP.BasicProperties var3, byte queneMsg[]){
        if(channel==null){
            channel=getChannel();
        }
        try {
            channel.basicPublish(exChange, queneName, var3, queneMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
