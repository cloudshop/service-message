package com.sdyk.util;


import com.sdyk.constants.Constants;
import org.apache.log4j.Logger;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

/**
 * Created by taylor on 2018/4/18.
 */
public class ZookeeperUtil {
    private static ZooKeeper zk = null;
    private final static Logger logger = Logger.getLogger(ZookeeperUtil.class);
    public static  void init() {
        try {
            zk = new ZooKeeper(Constants.ZOOKEEPER_HOST+":" + Constants.ZOOKEEPER_PORT,
                    5000, new Watcher() {
                // 监控所有被触发的事件
                public void process(WatchedEvent event) {
                    logger.info("已经触发了" + event.getType() + "事件！");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static boolean createNodePerSistent(String nodeName,String nodeData){
        if(zk==null){
            init();
        }
        Stat s = null;
        try {
            s = zk.exists(nodeName, true);
            if (s == null) {
                zk.create(nodeName, nodeData.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                        CreateMode.PERSISTENT);
            }else{
                logger.info("该节点:"+nodeName+"::::"+nodeData+"已创建");
            }
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean createNodeEphemeral(String nodeName,String nodeData){
        if(zk==null){
            init();
        }
        Stat s = null;
        try {
            // 创建一个子目录节点
            s = zk.exists(nodeName, true);
            if(s== null){
                zk.create(nodeName, nodeData.getBytes(),
                        ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            }else{
                logger.info("该节点:"+nodeName+"::::"+nodeData+"已创建");
            }

        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static List<String> getChildrens(String nodeName){
        if(zk==null){
            init();
        }
        try {
            // 创建一个子目录节点
           return  zk.getChildren(nodeName,true);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ZooKeeper getZk() {
        return zk;
    }

    public static void main(String[] args) {

    }
}
