package com.sdyk.channel;


import com.sdyk.handler.TextWebSocketFrameHandler;
import com.sdyk.handler.ServerHttpRequestHandler;
import com.sdyk.session.SessionManager;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.apache.log4j.Logger;

/**
 * Created by taylor on 2018/4/14.
 */
public class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
    private final Logger logger = Logger.getLogger(ChildChannelHandler.class);
    @Override
    public void initChannel(SocketChannel channel) throws Exception {
// 设置30秒没有读到数据，则触发一个READER_IDLE事件。
// pipeline.addLast(new IdleStateHandler(30, 0, 0));
// 在管道中添加我们自己的接收数据实现方法
        ChannelPipeline pipeline = channel.pipeline();
//        logger.info("第" + SessionManager.count + "接入");
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>child 登录成功>>>>>>>>>>>>>>>>>>>>>");
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(64*1024*1024));
        // 以下两行代码为了解决半包读问题
//        pipeline.addLast(new LineBasedFrameDecoder(64*1024*1024));
//        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new ServerHttpRequestHandler("/ws"));
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        pipeline.addLast(new TextWebSocketFrameHandler());
    }
}
