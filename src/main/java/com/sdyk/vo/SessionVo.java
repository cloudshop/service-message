package com.sdyk.vo;

import java.io.Serializable;

/**
 * Created by taylor on 2018/4/14.
 */
public class SessionVo implements Serializable{
    private String userName;
    private String inHost;
    private String channelId;
    private String userId;
    private String resource;
    private String mobileDeivceNum;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getMobileDeivceNum() {
        return mobileDeivceNum;
    }

    public void setMobileDeivceNum(String mobileDeivceNum) {
        this.mobileDeivceNum = mobileDeivceNum;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getInHost() {
        return inHost;
    }

    public void setInHost(String inHost) {
        this.inHost = inHost;
    }
}
