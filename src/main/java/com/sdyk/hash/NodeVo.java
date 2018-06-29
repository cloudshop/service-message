package com.sdyk.hash;

/**
 * Created by taylor on 2018/4/16.
 */
public class NodeVo {
    String name;
    String ip;
    public NodeVo(String name, String ip) {
        this.name = name;
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return this.name+"-"+this.ip;
    }
}
