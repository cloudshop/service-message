package com.sdyk.packet.heart;

import com.sdyk.packet.BasePacket;

import java.io.Serializable;

/**
 * Created by taylor on 2018/4/26.
 */
public class Heart extends BasePacket implements Serializable {
    private String ackId;
    private String ping;
    private String pong;

    public String getAckId() {
        return ackId;
    }

    public void setAckId(String ackId) {
        this.ackId = ackId;
    }

    public String getPing() {
        return ping;
    }

    public void setPing(String ping) {
        this.ping = ping;
    }

    public String getPong() {
        return pong;
    }

    public void setPong(String pong) {
        this.pong = pong;
    }
}
