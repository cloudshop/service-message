package com.sdyk.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.sdyk.constants.Constants;
import com.sdyk.packet.BasePacket;
import com.sdyk.packet.heart.Heart;
import com.sdyk.packet.msg.Msg;
import com.sdyk.session.SessionManager;
import com.sdyk.util.*;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.*;
import org.apache.log4j.Logger;

/**
 * Created by taylor on 2018/4/14.
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<io.netty.handler.codec.http.websocketx.TextWebSocketFrame> {
    private static final Logger logger = Logger.getLogger(WebSocketServerHandshaker.class.getName());

    /**
     * channel 通道 action 活跃的 当客户端主动链接服务端的链接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 添加
//        logger.info("客户端与服务端连接开启：" + ctx.channel().id());
    }

    /**
     * channel 通道 Inactive 不活跃的 当客户端主动断开服务端的链接后，这个通道就是不活跃的。也就是说客户端与服务端关闭了通信通道并且不可以传输数据
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //关闭session
        SessionManager.close(ctx.channel().id().toString());
    }

    /**
     * 接收客户端发送的消息 channel 通道 Read 读 简而言之就是从通道中读取数据，也就是服务端接收客户端发来的数据。但是这个数据在不进行解码时它是ByteBuf类型的
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, io.netty.handler.codec.http.websocketx.TextWebSocketFrame textWebSocketFrame) throws Exception {
        String msgStr = textWebSocketFrame.text();
        if(msgStr!=null&& !"".equals(msgStr) && msgStr.indexOf(Constants.HEAR_PING)>=0){
            BasePacket basePacket=JSON.parseObject(msgStr, new TypeReference<BasePacket>() {
            });
            //心跳检测包
            if(basePacket!=null && basePacket.getPacketType()!=null && basePacket.getPacketType().equals("ping")){
                Heart heart=JSON.parseObject(msgStr, new TypeReference<Heart>() {});
                heart.setPing(null);
                heart.setPong("pong");
                ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(heart)));
                return;
            }
        }
        if (msgStr != null) {
            //为前端去除\"
            msgStr=msgStr.replace("\\\"","\"").replace("\"{","{").replace("}\"","}");
            BusinessThreadUtil.doBusiness(ctx, msgStr);
        }


    }
    /**
     * channel 通道 Read 读取 Complete 完成 在通道读取完成后会在这个方法里通知，对应可以做刷新操作 ctx.flush()
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
    /**
     * exception 异常 Caught 抓住 抓住异常，当发生异常的时候，可以做一些相应的处理，比如打印日志、关闭链接
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        SessionManager.close(ctx.channel().id().toString());
        ctx.close();
    }


}
