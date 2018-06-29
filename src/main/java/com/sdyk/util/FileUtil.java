package com.sdyk.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.sdyk.config.SystemInitConfig;
import com.sdyk.constants.Constants;
import com.sdyk.packet.msg.Msg;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by taylor on 2018/4/28.
 */
public class FileUtil {
    public static void WriteStringToFile(String filePath) {
        try {
            // 构建指定文件
            File file = new File(filePath);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(filePath);
            String str="{ackId:\"10000\",from:\"test\",to:\"admin\",message:\"我是test\",request:\"request\",type:\"chat\",packetTye:\"msg\",msgType:\"text\"}/r/n";
            for(int i=0;i<100000;i++) {
                fos.write(str.getBytes());
            }
            fos.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static void readFile(String filePath){
        int len=0;
        StringBuffer str=new StringBuffer("");
        File file=new File(filePath);
        try {
            FileInputStream is=new FileInputStream(file);
            InputStreamReader isr= new InputStreamReader(is);
            BufferedReader in= new BufferedReader(isr);
            String line=null;
            while( (line=in.readLine())!=null ) {
                // 处理换行符的问题
                if(len != 0) {
                    str.append("\r\n"+line);
                }
                else {
                    str.append(line);
                }
                len++;
            }
            in.close();
            is.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println( str.toString());
    }
    public static void main(String[] args) {
//        String projectPath=new File("").getAbsolutePath();
//        String property_filenames=projectPath + "/etc/file";//"etc/im.properties";
//        File file=new File(property_filenames);
//        if(!file.exists()){
//            file.mkdir();
//            System.out.println("a");
//        }
//        String fileName=property_filenames+"/"+UUIDUtil.getIncId()+".txt";
//        WriteStringToFile(fileName);
//
        String a="";
        long start = System.currentTimeMillis();
        for(int k=0;k<10;k++) {

            System.out.println(UUIDUtil.getIncId()+":"+UUIDUtil.getIncId().length());

        }
        long end = System.currentTimeMillis();
        System.out.println((end - start));
//        for(int i=0;i<10;i++) {
//            long start=System.currentTimeMillis();
//            fileName = property_filenames + "/" + UUIDUtil.getIncId() + ".txt";
//            WriteStringToFile(fileName);
//            long end = System.currentTimeMillis();
//            System.out.println((end - start));
//        }
//        readFile(fileName);
//
//
//        File[] s=file.listFiles();
//        for(int i=0;i<s.length;i++){
//            System.out.println(s[i].getAbsoluteFile());
//        }


//        String str="{ackId:\"10000\",from:\"test\",to:\"admin\",message:\"我是test\",request:\"request\",type:\"chat\",packetTye:\"msg\",msgType:\"text\"}";
//        Msg msg = null;
////        SystemInitConfig.init();
////        MongoDBUtil.instance.save(msg,Constants.MONGODB_MSG_CLLECTION_NAME);
//        List<Object> obj=new ArrayList<>();
//        long start=System.currentTimeMillis();
//        for(int i=0;i<100000;i++){
//            try {
//
//                msg = JSON.parseObject(str, new TypeReference<Msg>() {
//                });
////                obj.add(msg);
//
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        long end=System.currentTimeMillis();
//        System.out.println((end-start));
//        SystemInitConfig.init();
//        String str="{ackId:\"10000\",from:\"test\",to:\"admin\",message:\"我是test\",request:\"request\",type:\"chat\",packetTye:\"msg\",msgType:\"text\"}";
//        MongoDBUtil.instance.save(str,Constants.MONGODB_MSG_CLLECTION_NAME);
//        long start=System.currentTimeMillis();
//        MongoDBUtil.instance.save(str,Constants.MONGODB_MSG_CLLECTION_NAME);
//        long end=System.currentTimeMillis();
//        System.out.println((end-start));
    }
}
