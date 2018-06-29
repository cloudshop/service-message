package com.sdyk;

import com.sdyk.channel.ChildChannelHandler;
import com.sdyk.config.SystemInitConfig;
import com.sdyk.constants.Constants;
import com.sdyk.server.ChatListProccess;
import com.sdyk.server.CrossServerMessageProccess;
import com.sdyk.server.MessageProccess;
import com.sdyk.server.PushNoticeProccess;
import com.sdyk.util.*;
import com.sdyk.vo.ServerInfo;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.apache.log4j.Logger;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by taylor on 2018/4/14.
 */
public class ServerIM  {
    private final Logger logger = Logger.getLogger(ServerIM.class);
    private static final int BOSS_GROUP_SIZE = Runtime.getRuntime().availableProcessors();
    public static void main(String[] args) {
        SystemInitConfig.init();
//        MongoDBUtil.instance.getAllCollections(Constants.MONGODB_DB_NAME);
//        RabbitMqUtil.init();
        ZookeeperUtil.init();
        MessageProccess messageProccess=new MessageProccess();
        messageProccess.start();

        CrossServerMessageProccess crossServerMessageProccess=new CrossServerMessageProccess();
        crossServerMessageProccess.start();

        PushNoticeProccess pushNoticeProccess=new PushNoticeProccess();
        pushNoticeProccess.start();


        ChatListProccess chatListProccess=new ChatListProccess();
        chatListProccess.start();


        new ServerIM().initNetty();



    }

    /**
     * 重启时删除重启前的缓存数据
     */
    public void removeCache(){
        List<String> listKeys=null;
        listKeys=RedisUtils.scan(Constants.IM_ONLINE_PREFIX_ + Constants.HOST_IDENTITY +"_",Integer.MAX_VALUE);
        while(listKeys.size()>0){
            logger.info("key count ::::::::::::::"+listKeys.size());
            Iterator iterator=listKeys.iterator();
            while(iterator.hasNext()){
                Object keyObj=iterator.next();
                if(keyObj!=null){
                    String keys=keyObj.toString();
                    RedisUtils.del(keys);
                    logger.info("start del cache :"+keys);
                }
            }
            listKeys=RedisUtils.scan(Constants.IM_ONLINE_PREFIX_ + Constants.HOST_IDENTITY +"_",Integer.MAX_VALUE);
        }



    }

    public void initNetty() {
        //删除该台程序的登录缓存记录
        removeCache();
        //启动netty
        new Thread() {
            public void run() {
                new ServerIM().run();
            }
        }.start();

        //启动当前netty服务器   心跳检测
        try {
            Thread.sleep(Constants.IM_CHECK_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread() {
            public void run() {
                //创建父节点
                ZookeeperUtil.createNodePerSistent("/"+Constants.ZOOKEEPER_NETTY_SERVER_NODE,Constants.ZOOKEEPER_NETTY_SERVER_NODE);
                ZookeeperUtil.createNodeEphemeral("/"+Constants.ZOOKEEPER_NETTY_SERVER_NODE+"/"+Constants.HOST_IDENTITY+"#"+Constants.IM_HOST+"#"+Constants.IM_PORT,
                        Constants.HOST_IDENTITY+"#"+Constants.IM_HOST+"#"+Constants.IM_PORT);
                while(true) {
                    try {
                        List<ServerInfo> ips=new ArrayList<ServerInfo>();
                        List<String> list=ZookeeperUtil.getChildrens("/nettyServer");//RedisUtils.scan(Constants.IM_HOST_KEY_PREFIX_+"*",Integer.MAX_VALUE);
//                        System.out.println("获取当前服务:"+list.size());
                        for(int i=0;i<list.size();i++){
                            Object obj=list.get(i);

                            if(obj!=null){
//                                logger.info("netty服务器地址:"+obj.toString());
                                //list.get(i).toString()
                                String temp[]=obj.toString().split("#");

                                ServerInfo vo= new ServerInfo();
                                vo.setIdentity(temp[0]);
                                vo.setIp(temp[1]);
                                vo.setPort(temp[2]);
                                ips.add(vo);
//                                System.out.println("当前注册:"+JSON.toJSON(ips).toString());
                            }else{
                             logger.error("im注册异常。。。。。。。。。。。。。。。。。error error error error error error");
                            }
                        }
//                        System.out.println(list.size());
                        BalanceRouteUtil.loadResource(ips);
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
    public void run() {
        logger.info("===========================Netty端口启动========");
        logger.info("处理器数量>>>>>>>>>:"+BOSS_GROUP_SIZE);
        // Boss线程：由这个线程池提供的线程是boss种类的，用于创建、连接、绑定socket， （有点像门卫）然后把这些socket传给worker线程池。
        // 在服务器端每个监听的socket都有一个boss线程来处理。在客户端，只有一个boss线程来处理所有的socket。
        EventLoopGroup bossGroup = new NioEventLoopGroup(BOSS_GROUP_SIZE, new DefaultThreadFactory("server1", true));
        // Worker线程：Worker线程执行所有的异步I/O，即处理操作
        EventLoopGroup workGroup = new NioEventLoopGroup(BOSS_GROUP_SIZE, new DefaultThreadFactory("server2", true));
        try {
            // ServerBootstrap 启动NIO服务的辅助启动类,负责初始话netty服务器，并且开始监听端口的socket请求
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workGroup);
            // 设置非阻塞,用它来建立新accept的连接,用于构造serversocketchannel的工厂类
            b.channel(NioServerSocketChannel.class);
//            b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
//            b.option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT);
//            b.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
//            b.childOption(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT);
            b.option(ChannelOption.SO_BACKLOG, 1024);
//            b.childOption(ChannelOption.SO_KEEPALIVE, true);
            b.childOption(ChannelOption.TCP_NODELAY, true);
            // ChildChannelHandler 对出入的数据进行的业务操作,其继承ChannelInitializer
            b.childHandler(new ChildChannelHandler());

            logger.info("服务端开启等待客户端连接 ... ...");

            //linux kill指令，程序需要做的工作
            SignalHandler handler = new SignalHandler() {
                public void handle(Signal signal) {
                    System.exit(-1);
                }
            };
            Signal.handle(new Signal("TERM"), handler);  // kill -15 common kill
            Signal.handle(new Signal("INT"), handler);   // Ctrl c

            //程序退出时需要做的工作
            Runtime runtime = Runtime.getRuntime();
            Thread thread = new Thread(new ShutDownListener());
            runtime.addShutdownHook(thread);

            Channel ch = b.bind(7397).sync().channel();
            ch.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

}
//程序退出时需要做的工作
class ShutDownListener implements Runnable
{
    private final Logger logger = Logger.getLogger(ShutDownListener.class);
    @Override
    public void run()
    {
        try {
            //删除用户在线状态
            logger.info("删除当前机器登录用户状态及信息start......");
            removeCache();
            logger.info("删除当前机器登录用户状态及信息end......");
            logger.info("关闭zookeeper连接start......");
            ZookeeperUtil.getZk().close();
            logger.info("关闭zookeeper连接end......");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 删除用户在线状态
     */
    public void removeCache(){
        List<String> listKeys=null;
        listKeys=RedisUtils.scan(Constants.IM_ONLINE_PREFIX_ + Constants.HOST_IDENTITY +"_",Integer.MAX_VALUE);
        while(listKeys.size()>0){
            logger.info("key count ::::::::::::::"+listKeys.size());
            Iterator iterator=listKeys.iterator();
            while(iterator.hasNext()){
                Object keyObj=iterator.next();
                if(keyObj!=null){
                    String keys=keyObj.toString();
                    RedisUtils.del(keys);
                    logger.info("del cache :"+keys);
                }
            }
            listKeys=RedisUtils.scan(Constants.IM_ONLINE_PREFIX_ + Constants.HOST_IDENTITY +"_",Integer.MAX_VALUE);
        }



    }
}
