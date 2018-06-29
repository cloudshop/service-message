package com.sdyk.util;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JGPushUtil {

    public static final String APP_KEY = "ff847414e6e91074b2c25f05";

    public static final String MASTER_SECRET = "6367b928500d8668c2600f8a";

    public static final String ALERT = "JPush Test - alert";

    public static final String MSG_CONTENT = "JPush Test - msgContent";

    public static final JPushClient jPushClient = new JPushClient(MASTER_SECRET, APP_KEY);

    /**
     * 极光推送
     *
     * @param appId  设备号
     * @param alert  通知正文
     * @param title  通知标题
     * @param exeras 参数
     * @return
     */
    public static PushResult send(String appId, String alert, String title, Map<String, String> exeras) {
        PushPayload p = PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.alias(appId))
                .setNotification(Notification.newBuilder()
                        .setAlert(alert)
                        .addPlatformNotification(AndroidNotification.newBuilder().addExtras(exeras).setTitle(title).build())
                        .addPlatformNotification(IosNotification.newBuilder().incrBadge(1).addExtras(exeras).build()).build())
                .setOptions(Options.newBuilder()
                        .build())
                .build();
        PushResult result = null;
        try {
            result = jPushClient.sendPush(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 极光推送
     *
     * @param appIds  设备号list
     * @param alert  通知正文
     * @param title  通知标题
     * @param exeras 参数
     * @return
     */
    public static PushResult sendList(List<String> appIds, String alert, String title, Map<String, String> exeras) {
        PushPayload p = PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.alias(appIds))
                .setNotification(Notification.newBuilder()
                        .setAlert(alert)
                        .addPlatformNotification(AndroidNotification.newBuilder().addExtras(exeras).setTitle(title).build())
                        .addPlatformNotification(IosNotification.newBuilder().incrBadge(1).addExtras(exeras).build()).build())
                .build();
        PushResult result = null;
        try {
            result = jPushClient.sendPush(p);
        } catch (APIConnectionException e) {
            e.printStackTrace();
        } catch (APIRequestException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {
//        for(int i=0;i<100;i++){
            Map<String, String> map = new HashMap();
            map.put("type", "1");
            map.put("code", "105");
            map.put("param", UUIDUtil.getUUID());
            long start=System.currentTimeMillis();
            List list=new ArrayList<>();
            list.add("DCAB3BE7CF0840A1A23DCCF250E1AF2E");
            PushResult p = send("DCAB3BE7CF0840A1A23DCCF250E1AF2E", "happy"+UUIDUtil.getIncId(), "testhappy", map);
            long end=System.currentTimeMillis();
            System.out.println(JSON.toJSONString(p)+"::::::"+(end-start));
//        }

//        PushPayload p1= PushPayload.newBuilder()
//                .setPlatform(Platform.ios())
//                .setAudience(Audience.tag_and("354039071411292", "866673023124834"))
//                .setNotification(Notification.newBuilder()
//                        .addPlatformNotification(IosNotification.newBuilder()
//                                .setAlert(ALERT)
//                                .setBadge(5)
//                                .setSound("happy")
//                                .addExtra("from", "三点一刻")
//                                .build())
//                        .build())
//                .setMessage(Message.content(MSG_CONTENT))
//                .setOptions(Options.newBuilder()
//                        .setApnsProduction(true)
//                        .build())
//                .build();
//        System.out.println(p1.toJSON().toString());

    }
}