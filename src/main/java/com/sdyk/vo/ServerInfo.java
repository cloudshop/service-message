package com.sdyk.vo;

import java.io.Serializable;

/**
 * Created by taylor on 2018/4/14.
 */
public class ServerInfo implements Serializable{
    private String ip;
    private String port;
    private String identity;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    @Override
    public String toString() {
        return "{" +
                "ip:" + ip +
                ", port:" + port +
                ", identity:" + identity +
                "}";
    }
}
