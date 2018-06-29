package com.sdyk.db;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.mongodb.BasicDBObject;
import com.sdyk.constants.Constants;
import com.sdyk.packet.msg.Msg;
import com.sdyk.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by taylor on 2018/4/17.
 */
public class Msgdb {
    private static final Logger log = LoggerFactory.getLogger(Msgdb.class);

    public static Msg saveMsg(String msgJson) throws Exception {
        Msg msg = null;
        try {


            msg = JSON.parseObject(msgJson, new TypeReference<Msg>() {
            });
            msg.setTimestamp(new Date());
            msg.setRequest("request");
//            msg.setMsgId(UUIDUtil.getIncId());
            MongoDBUtil.instance.save(msg, Constants.MONGODB_MSG_CLLECTION_NAME);
            MongoDBUtil.instance.save(msg, Constants.MONGODB_MSG_HISTORY);
//            RabbitMqUtil.sendQueen("",Constants.IM_MSG_PUSH_NOTICE_QUEUE,MessageProperties.PERSISTENT_TEXT_PLAIN,JSON.toJSONString(msg).getBytes());

//            DataSourceUtils.updateLastChatInfo(msg.getTimestamp(),msg.getMessage(),msg.getFrom(),msg.getTo());
//            DataSourceUtils.updateLastChatInfo(msg.getTimestamp(),msg.getMessage(),msg.getTo(),msg.getFrom());
//            RedisUtils.set(Constants.IM_CHAT_INFO_PREFIX_+msg.getFrom()+"_"+msg.getTo() ,JSON.toJSON(msg));
//            RedisUtils.set(Constants.IM_CHAT_INFO_PREFIX_+msg.getTo()+"_"+msg.getFrom() ,JSON.toJSON(msg));
        } catch (Exception e) {
            log.info("保存数据异常" + msgJson, e);
            throw new Exception(e);
        }
        return msg;
    }

    public static int delMsg(String to, String msgId) throws Exception {
        int count = 0;
        try {
            BasicDBObject fromObj = new BasicDBObject();
//            String msgIdArray[] = msgIds.split(",");

            if (msgId != null && msgId.length() > 0) {

                fromObj.append("msgId", msgId);
                fromObj.append("to", to);
                count = MongoDBUtil.instance.delete(Constants.MONGODB_MSG_CLLECTION_NAME, fromObj);
            }
        } catch (Exception e) {
            log.info("删除用户" + to + "的聊天记录" + msgId + "异常", e);
            throw new Exception(e);
        }
        return count;
    }

    public static long getCount( String to, String msgId) {
        BasicDBObject fromObj = new BasicDBObject();
        fromObj.append("msgId", msgId);
        fromObj.append("to", to);
        return MongoDBUtil.instance.getCount(Constants.MONGODB_MSG_CLLECTION_NAME, fromObj);
    }

    public static Msg saveAllMsg(String msgJson) throws Exception {
        Msg msg = null;
        try {
            msg = JSON.parseObject(msgJson, new TypeReference<Msg>() {
            });
//            msg.setMsgId(UUIDUtil.getIncId());
            String groupId = msg.getTo();
            msg.setTimestamp(new Date());
            Object obj = RedisUtils.lRangeAll(Constants.IM_GROUP_MEMBERS + msg.getTo());
            if (obj != null) {
                List<String> members = (List) obj;
                List<Object> msgList = new ArrayList<Object>();
                for (int i = 0; i < members.size(); i++) {
                    String member=members.get(i).replaceAll("\"","");
                    if(!member.equals(msg.getFrom())) {
                        Msg cloneMsg = (Msg) msg.clone();
                        cloneMsg.setRequest("request");
                        cloneMsg.setTo(member);
                        cloneMsg.setFrom(groupId);
//                        msgList.add(cloneMsg);
                        msgList.add(cloneMsg);
//                        RabbitMqUtil.sendQueen("",Constants.IM_MSG_PUSH_NOTICE_QUEUE,MessageProperties.PERSISTENT_TEXT_PLAIN,JSON.toJSONString(cloneMsg).getBytes());
                    }
                }
                if(msgList.size()>0){
                    MongoDBUtil.instance.saveAll(msgList, Constants.MONGODB_MSG_CLLECTION_NAME);
                }

            }
            msg.setTo(groupId);
            MongoDBUtil.instance.save(msg, Constants.MONGODB_MSG_HISTORY);
        } catch (Exception e) {
            log.info("保存数据异常" + msgJson, e);
            throw new Exception(e);
        }
        return msg;
    }


}
