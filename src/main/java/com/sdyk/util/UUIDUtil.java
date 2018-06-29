package com.sdyk.util;

import java.util.UUID;

/**
 * Created by taylor on 2017/11/22.
 */
public class UUIDUtil {
    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 此种id生成策略比uuid快12倍以上
     * @return
     */
    public static String getIncId(){return SnowflakeIdUtil.nextId()+"";}
    public static void main(String[] args) throws Throwable {
        String tmp=UUIDUtil.getIncId();
        System.out.println(tmp+":"+tmp.length());
    }
}
