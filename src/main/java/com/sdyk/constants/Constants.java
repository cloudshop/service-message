package com.sdyk.constants;

/**
 * Created by taylor on 2018/4/2.
 */
public class Constants {
    public  static String HOST_IDENTITY="";
    public static String REDIS_REMOVE_KEYS="";
    //系统初始化配置数据
    public static String REDIS_HOST="";
    public static String REDIS_PORT="";
    public static String REDIS_AUTH="";
    public static String REDIS_DATABASE="";
    public static String REDIS_MAXACTIVE="";
    public static String REDIS_MAXIDLE="";
    public static String REDIS_TESTONBORROW="";
    public static String REDIS_TIMEOUT="";


    public static String RABBITMQ_HOST="";
    public static String RABBITMQ_USERNAME="";
    public static String RABBITMQ_PASSWORD="";
    public static String RABBITMQ_PORT="";
    public static String RABBITMQ_VIRTUALHOST="";
    public static String RABBITMQ_PUBLISHERCONFIRMS="";



    public static String IM_HOST="";
    public static String IM_PORT="";
    public static int IM_CHECK_TIME=5;

    public static String IM_ONLINE_PREFIX_="IM_ONLINE_PREFIX_";

    public static String IM_CHAT_INFO_PREFIX_="IM_CHAT_INFO_PREFIX_";

    public static String IM_MSG_QUEUE="IM_MSG_QUEUE_";
    public static String IM_MSG_SAVE_AND_ACK_QUEUE="IM_MSG_SAVE_AND_ACK_QUEUE";
    public static String IM_MSG_PUSH_NOTICE_QUEUE="IM_MSG_PUSH_NOTICE_QUEUE";
    public static String IM_CHAT_LIST_QUEUE="IM_CHAT_LIST_QUEUE";


    public static String MONGODB_IP;
    public static int MONGODB_PORT;
    public static String MONGODB_DB_NAME;
    public static int MONGODB_CONNECTION_NUM;
    public static int MONGODB_CONNECT_TIME_OUT;
    public static int MONGODB_MAX_WAIT_TIME;
    public static int MONGODB_SOCKET_TIME_OUT;
    public static int MONGODB_THREADS_ALLOWED_TO_BLOCK_FOR_CONNECTION_MULTIPLIER;
    public static String MONGODB_USER_NAME;
    public static String MONGODB_PASSWORD;


    public static String ZOOKEEPER_HOST;
    public static String ZOOKEEPER_PORT;

    public static String IM_USER_MOBILE_TYPE_NO="IM_USER_MOBILE_TYPE_NO";
    public static String MONGODB_MSG_CLLECTION_NAME="IM_MSG_OFFLINE";

    public static String MONGODB_MSG_HISTORY="IM_MSG_HISTORY";

    public static String IM_GROUP="IM_GROUP_";
    public static String IM_GROUP_MEMBERS="IM_GROUP_MEMBERS_";

    public static String LOGIN_STATUS="LOGIN_STATUS_";

    public static String LOGIN_INFO="LOGIN_INFO_";

    public static long TIME_DELAY_SECOND=3*1000L;

    public static int DELIVERYMODE_PERSISTENT=2;

    public static String ZOOKEEPER_NETTY_SERVER_NODE="nettyServer";

    public static String HEAR_PING="\"packetType\":\"ping\"";

    public static String CONNECTION_DRIVER;
    public static String CONNECTION_URL;
    public static String CONNECTION_USER;
    public static String CONNECTION_PASSWORD;


    public final static String IM_CHAT_LIST_PREFIX_="IM_CHAT_LIST_PREFIX_";

}
