package com.sdyk.packet.msg;


import com.sdyk.packet.BasePacket;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Created by taylor on 2018/4/14.
 */
public class Msg extends BasePacket implements Serializable,Cloneable {
    //消息id
    private String msgId;
    //发送者
    private String from;
    //接受者
    private String to;
    //消息主题
    private String subject;
    //消息内容
    private String message;
    //file(文件)、text(文本)、vedio(视频)、voice(音频)、map(地图)、expression(表情)
    private String msgType;
    //chat、groupchat,pubSub
    private String type;
    //时间
    private Date timestamp;

    private String request;
    private String received;
    private String ackId;

    private Integer proccessSum;

    private Map attributeMap;

    public Map getAttributeMap() {
        return attributeMap;
    }

    public void setAttributeMap(Map attributeMap) {
        this.attributeMap = attributeMap;
    }

    public Integer getProccessSum() {
        return proccessSum;
    }

    public void setProccessSum(Integer proccessSum) {
        this.proccessSum = proccessSum;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getReceived() {
        return received;
    }

    public void setReceived(String received) {
        this.received = received;
    }

    public String getAckId() {
        return ackId;
    }

    public void setAckId(String ackId) {
        this.ackId = ackId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public Object clone() {
        Msg stu = null;
        try{
            stu = (Msg)super.clone();
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return stu;
    }
}
