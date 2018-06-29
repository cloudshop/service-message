package com.sdyk.packet;

import java.io.Serializable;

/**
 * Created by taylor on 2018/4/26.
 */
public class BasePacket implements Serializable {
    //msg:消息   ping:心跳包
    private String packetType;

    public String getPacketType() {
        return packetType;
    }

    public void setPacketType(String packetType) {
        this.packetType = packetType;
    }
}
