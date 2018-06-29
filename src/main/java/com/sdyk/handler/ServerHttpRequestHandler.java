package com.sdyk.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.sdyk.session.SessionManager;
import com.sdyk.util.RedisUtils;
import com.sdyk.vo.SessionVo;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * Created by taylor on 2018/4/24.
 */
public class ServerHttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final Logger logger = Logger.getLogger(WebSocketServerHandshaker.class.getName());
    private final String wsUri;
    public ServerHttpRequestHandler(String wsUri) {
        this.wsUri = wsUri;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        Channel incoming = ctx.channel();
        logger.info("Client:" + incoming.remoteAddress() + "异常");
        // 当出现异常就关闭连接
        cause.printStackTrace();
        SessionManager.close(ctx.channel().id().toString());
        ctx.close();

    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
//        // 如果HTTP解码失败，返回HHTP异常
//        if (!req.getDecoderResult().isSuccess() || (!"websocket".equals(req.headers().get("Upgrade")))) {
//            sendHttpResponse(ctx, req,
//                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
//            return;
//        }
//        //获取url后置参数
//        HttpMethod method = req.getMethod();
//        String uri = req.getUri();
//        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
//        Map<String, List<String>> parameters = queryStringDecoder.parameters();
////        System.out.println(parameters.get("request").get(0));
//        if (method == HttpMethod.GET && "/webssss".equals(uri)) {
//            //....处理
//            ctx.attr(AttributeKey.valueOf("type")).set("anzhuo");
//        } else if (method == HttpMethod.GET && "/websocket".equals(uri)) {
//            //...处理
//            ctx.attr(AttributeKey.valueOf("type")).set("live");
//        }
        // 构造握手响应返回
//        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
//                "ws://" + req.headers().get(HttpHeaders.Names.HOST) + uri, null, false);
//        handshaker = wsFactory.newHandshaker(req);
//        if (handshaker == null) {
//            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
//        } else {
//            handshaker.handshake(ctx.channel(), req);
            Channel channel = ctx.channel();
            String requestUri = req.uri();

            if (requestUri.indexOf("?") >= 0) {
                String uis[] = requestUri.split("\\?");
                req.setUri("/ws");

                if (uis != null && uis.length > 0) {
                    if (uis[1].indexOf("admin") >= 0 || uis[1].indexOf("test") >= 0) {
                        SessionVo sessionVo = new SessionVo();
                        sessionVo.setUserId(uis[1]);
                        sessionVo.setUserName(uis[1]);
                        sessionVo.setResource("pc");
                        sessionVo.setMobileDeivceNum("DCAB3BE7CF0840A1A23DCCF250E1AF2E");
                        SessionManager.create(ctx.channel().id().toString(), channel, sessionVo);
                        ctx.fireChannelRead(req.retain());
                        ctx.channel().writeAndFlush(new TextWebSocketFrame("{\"code\":\"10000\",\"message\":\"登录成功\"}"));

                    } else {
                        Object obj = RedisUtils.get(uis[1]);
                        if (obj != null) {
                            SessionVo sessionVo = JSON.parseObject(obj.toString(), new TypeReference<SessionVo>() {
                            });
                            SessionManager.create(ctx.channel().id().toString(), channel, sessionVo);
                            ctx.fireChannelRead(req.retain());
                            logger.info("登录成功。。。。。。。。。"+sessionVo.getUserName()+":"+sessionVo.getUserName());
                            ctx.channel().writeAndFlush(new TextWebSocketFrame("{\"code\":\"10000\",\"message\":\"登录成功\"}"));
                        } else {
                            logger.info(JSON.toJSON(obj)+"登录失败1>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+uis[1]);
                            ctx.channel().writeAndFlush(new TextWebSocketFrame("{\"code\":\"4002\",\"message\":\"permission denied(没有权限)\"}"));
                            ctx.close();
                        }
                    }
                } else {
                    logger.info("登录失败2>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    ctx.channel().writeAndFlush(new TextWebSocketFrame("{\"code\":\"4002\",\"message\":\"permission denied(没有权限)\"}"));
                    ctx.close();
                }
            } else {
                logger.info("登录失败3>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                ctx.channel().writeAndFlush(new TextWebSocketFrame("{\"code\":\"4002\",\"message\":\"permission denied(没有权限)\"}"));
                ctx.close();
            }


//        }
    }
//    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse res) {
//        // 返回应答给客户端
//        if (res.getStatus().code() != 200) {
//            ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
//            res.content().writeBytes(buf);
//            buf.release();
//        }
//        // 如果是非Keep-Alive，关闭连接
//        ChannelFuture f = ctx.channel().writeAndFlush(res);
//        if (!HttpHeaders.isKeepAlive(req) || res.getStatus().code() != 200) {
//            f.addListener(ChannelFutureListener.CLOSE);
//        }
//    }
}
