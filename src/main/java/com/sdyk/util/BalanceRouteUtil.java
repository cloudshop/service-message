package com.sdyk.util;

import com.sdyk.constants.Constants;
import com.sdyk.hash.MongodbMsgShard;
import com.sdyk.hash.NodeVo;
import com.sdyk.hash.Shard;
import com.sdyk.vo.ServerInfo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

/**
 * Created by taylor on 2018/4/16.
 */
public class BalanceRouteUtil {
    static Shard sh = null;
    static MongodbMsgShard mongodbMsgShard=null;
    static List<NodeVo> shards = new ArrayList<NodeVo>();
    static Map<String,String> map=new HashMap();
    public static void loadResource(List<ServerInfo> list) {


        if (list != null && list.size() > 0) {



            for (int i = 0; i < list.size(); i++) {
                NodeVo s1 = new NodeVo(list.get(i).getIdentity(), list.get(i).getIp());
                shards.add(s1);
            }
            sh = new Shard(shards);
        }


    }

    public static String getBalanceRoute(String userName) {
        return sh.keyToNode(userName);
    }
    public static String getBalanceRouteHostIdentity(String userName) {
        return sh.keyToNodeHostIdentity(userName);
    }

    public static void main(String[] args) {
        List<ServerInfo> ips = new ArrayList<ServerInfo>();
            ServerInfo no=new ServerInfo();
            no.setIdentity("machine1");
            no.setIp("socketmachine1.315free.com");

//        ServerInfo no1=new ServerInfo();
//        no1.setIdentity("machine"+2);
//        no1.setIp("10.0.0.66");
//            ips.add(no);
        ips.add(no);
        BalanceRouteUtil.loadResource(ips);
//
//        for(int i=0;i<100000000;i++){
//            if(BalanceRouteUtil.getBalanceRoute(25fcc8f095374136ab7524f0fc8ae21b)==null){
//                System.out.println("异常"+BalanceRouteUtil.getBalanceRoute(UUIDUtil.getUUID()));
//            }
//
//        }

        System.out.println(BalanceRouteUtil.getBalanceRoute("8d028e4b702b4054bee18844c219b329")+":::"+BalanceRouteUtil.getBalanceRouteHostIdentity("8d028e4b702b4054bee18844c219b329"));
    }
}
