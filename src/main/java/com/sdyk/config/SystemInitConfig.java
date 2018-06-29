package com.sdyk.config;


import com.sdyk.constants.Constants;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 * Created by taylor on 2018/4/12.
 */
public class SystemInitConfig {
    private static final Logger logger = Logger.getLogger(SystemInitConfig.class.getName());
    public static String getConfigFilePath(){
        String configFilePath="";
        String projectPath=new File("").getAbsolutePath();
//        projectPath=projectPath + "/etc/im.properties";
        String property_filenames=projectPath + "/etc/im.properties";//"etc/im.properties";
        Properties defProps = new Properties();

        try {
            defProps.load(new FileReader(property_filenames));
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Set<String> prop_keys = defProps.stringPropertyNames();
        String proFilesActive=defProps.getProperty("profiles.active");
//        Constants.HOST_IDENTITY=defProps.getProperty("host.identity");
        if(proFilesActive!=null){
            if(proFilesActive.equals("dev")){
                configFilePath=projectPath + "/etc/im-dev.properties";
            }else if(proFilesActive.equals("local")){
                configFilePath=projectPath + "/etc/im-local.properties";
            } else if(proFilesActive.equals("test")){
                configFilePath=projectPath + "/etc/im-test.properties";
            } else if(proFilesActive.equals("pro")){
                configFilePath=projectPath + "/etc/im-pro.properties";
            }
        }
        return configFilePath;
    }
    public static void init(){
        String property_filenames=getConfigFilePath();
        if(property_filenames.equals("")){
            System.err.println("未找到配置文件，系统退出");
            System.exit(-1);
        }
        Properties defProps = new Properties();

        try {
            defProps.load(new FileReader(property_filenames));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Set<String> prop_keys = defProps.stringPropertyNames();
        Iterator<String> iterator=prop_keys.iterator();
        while(iterator.hasNext()){
            String key=iterator.next();
            logger.info(key+":"+defProps.getProperty(key));
        }


        Constants.REDIS_HOST = defProps.getProperty("redis.host");
        Constants.REDIS_PORT = defProps.getProperty("redis.port");
        Constants.REDIS_AUTH = defProps.getProperty("redis.auth");
        Constants.REDIS_DATABASE = defProps.getProperty("redis.dataBase");
        Constants.REDIS_MAXACTIVE = defProps.getProperty("redis.maxActive");
        Constants.REDIS_MAXIDLE = defProps.getProperty("redis.maxIdle");
        Constants.REDIS_TESTONBORROW = defProps.getProperty("redis.testOnBorrow");
        Constants.REDIS_TIMEOUT = defProps.getProperty("redis.timeOut");

        Constants.REDIS_REMOVE_KEYS=defProps.getProperty("redis.remove.keys");


        Constants.RABBITMQ_HOST = defProps.getProperty("rabbitmq.host");
        Constants.RABBITMQ_USERNAME = defProps.getProperty("rabbitmq.username");
        Constants.RABBITMQ_PASSWORD = defProps.getProperty("rabbitmq.password");
        Constants.RABBITMQ_PORT = defProps.getProperty("rabbitmq.port");
        Constants.RABBITMQ_VIRTUALHOST = defProps.getProperty("rabbitmq.virtualHost");
        Constants.RABBITMQ_PUBLISHERCONFIRMS = defProps.getProperty("rabbitmq.publisherConfirms");

        Constants.HOST_IDENTITY=defProps.getProperty("host.identity");
        Constants.IM_HOST = defProps.getProperty("im.host");
        Constants.IM_PORT = defProps.getProperty("im.port");
        Constants.IM_CHECK_TIME = Integer.valueOf(defProps.getProperty("im.heart.time"));

        Constants.MONGODB_IP= defProps.getProperty("mongodb.ip");
        Constants.MONGODB_PORT= Integer.valueOf(defProps.getProperty("mongodb.port"));
        Constants.MONGODB_DB_NAME= defProps.getProperty("mongodb.dbName");
        Constants.MONGODB_CONNECTION_NUM= Integer.valueOf(defProps.getProperty("mongodb.connectionsNum"));
        Constants.MONGODB_CONNECT_TIME_OUT= Integer.valueOf(defProps.getProperty("mongodb.connectTimeout"));
        Constants.MONGODB_MAX_WAIT_TIME= Integer.valueOf(defProps.getProperty("mongodb.maxWaitTime"));
        Constants.MONGODB_SOCKET_TIME_OUT= Integer.valueOf(defProps.getProperty("mongodb.socketTimeout"));
        Constants.MONGODB_THREADS_ALLOWED_TO_BLOCK_FOR_CONNECTION_MULTIPLIER= Integer.valueOf(defProps.getProperty("mongodb.threadsAllowedToBlockForConnectionMultiplier"));
        Constants.MONGODB_USER_NAME= defProps.getProperty("mongodb.userName");
        Constants.MONGODB_PASSWORD= defProps.getProperty("mongodb.password");

        Constants.ZOOKEEPER_HOST= defProps.getProperty("zookeeper.host");
        Constants.ZOOKEEPER_PORT= defProps.getProperty("zookeeper.port");

        Constants.CONNECTION_DRIVER= defProps.getProperty("jdbc.driver");
        Constants.CONNECTION_URL= defProps.getProperty("jdbc.url");
        Constants.CONNECTION_USER= defProps.getProperty("jdbc.user");
        Constants.CONNECTION_PASSWORD= defProps.getProperty("jdbc.password");

    }
}
