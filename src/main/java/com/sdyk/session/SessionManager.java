package com.sdyk.session;

import com.sdyk.constants.Constants;
import com.sdyk.util.RedisUtils;
import com.sdyk.vo.SessionVo;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by taylor on 2018/4/14.
 */
public class SessionManager {
    private static final Logger logger = Logger.getLogger(SessionManager.class.getName());
    public static  ConcurrentHashMap<String, Channel> connectionsByFrom = new ConcurrentHashMap<String, Channel>(100000);


    public static boolean create(String channelId,Channel channel,SessionVo sessionVo){
        try {
            connectionsByFrom.put(channelId, channel);
            sessionVo.setUserName(sessionVo.getUserId());
            sessionVo.setInHost(Constants.IM_HOST);
            sessionVo.setChannelId(channelId);
            RedisUtils.set(Constants.IM_ONLINE_PREFIX_ + Constants.HOST_IDENTITY +"_"+sessionVo.getUserId()+"_"+sessionVo.getResource(),sessionVo);
            RedisUtils.set(Constants.IM_ONLINE_PREFIX_ + Constants.HOST_IDENTITY +"_"+channelId,sessionVo);
            logger.info("用户:"+sessionVo.getUserName()+"接入,登录客户端为:"+sessionVo.getResource());
        }catch(Exception e){
            return false;
        }
        return true;
    }

    public static boolean close(String channelId){
        try {
            Object obj=RedisUtils.get(Constants.IM_ONLINE_PREFIX_ + Constants.HOST_IDENTITY +"_"+channelId);
            if(obj!=null){
                SessionVo sessionVo=(SessionVo)obj;
                RedisUtils.del(Constants.IM_ONLINE_PREFIX_ + Constants.HOST_IDENTITY +"_"+channelId);
                RedisUtils.del(Constants.IM_ONLINE_PREFIX_ + Constants.HOST_IDENTITY +"_"+sessionVo.getUserId()+"_"+sessionVo.getResource());
                connectionsByFrom.remove(channelId);
                logger.info("用户:"+sessionVo.getUserId()+"离开,登录客户端为:"+sessionVo.getResource());
            }

        }catch(Exception e){
            return false;
        }
        return true;
    }

    public static boolean start(){
        try {
            ExecutorService sessionExcutor = Executors.newFixedThreadPool(8);
            //将单聊的消息缓存至redis
            for (int i = 0; i < 8; i++) {
                final int index = i;
                sessionExcutor.execute(new Runnable() {
                    public void run() {

                    }
                });
            }
        }catch(Exception e){
            return false;
        }
        return true;
    }
    public static Channel getChannel(String channelId){
        return connectionsByFrom.get(channelId);
    }

    public static int count=0;
}
