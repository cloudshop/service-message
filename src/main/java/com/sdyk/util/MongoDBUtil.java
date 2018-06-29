package com.sdyk.util;

import com.alibaba.fastjson.JSON;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.sdyk.constants.Constants;
import com.sdyk.vo.MongoDbPageInfo;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by taylor on 2017/11/28.
 */
public enum MongoDBUtil {
    /**
     * 定义一个枚举的元素，它代表此类的一个实例
     */
    instance;

    private MongoClient mongoClient;

    static {
        System.out.println("===============MongoDBUtil初始化========================");

        String ip= Constants.MONGODB_IP;
        Integer port= Constants.MONGODB_PORT;
        //连接池设置为300个连接,默认为100
        Integer connectionsNum= Constants.MONGODB_CONNECTION_NUM;
        //连接超时，推荐>3000毫秒
        Integer connectTimeout= Constants.MONGODB_CONNECT_TIME_OUT;
        //最大等待时间
        Integer maxWaitTime= Constants.MONGODB_MAX_WAIT_TIME;
        //套接字超时时间，0无限制
        Integer socketTimeout= Constants.MONGODB_SOCKET_TIME_OUT;
        //线程队列数，如果连接线程排满了队列就会抛出“Out of semaphores to get db”错误。
        Integer threadsAllowedToBlockForConnectionMultiplier= Constants.MONGODB_THREADS_ALLOWED_TO_BLOCK_FOR_CONNECTION_MULTIPLIER;
        // 从配置文件中获取属性值

        ServerAddress serverAddress=new ServerAddress(ip,port);
        MongoCredential.createCredential(Constants.MONGODB_USER_NAME,Constants.MONGODB_DB_NAME,Constants.MONGODB_PASSWORD.toCharArray());
        List<MongoCredential> list=new ArrayList<MongoCredential>();
        list.add(MongoCredential.createCredential(Constants.MONGODB_USER_NAME,Constants.MONGODB_DB_NAME,Constants.MONGODB_PASSWORD.toCharArray()));
        instance.mongoClient = new MongoClient(serverAddress, list);
        // 大部分用户使用mongodb都在安全内网下，但如果将mongodb设为安全验证模式，就需要在客户端提供用户名和密码：
//         boolean auth = db.authenticate(myUserName, myPassword);
        MongoClientOptions.Builder options = new MongoClientOptions.Builder();
        // options.autoConnectRetry(true);// 自动重连true
        // options.maxAutoConnectRetryTime(10); // the maximum auto connect retry time
        options.connectionsPerHost(connectionsNum);// 连接池设置为300个连接,默认为100
        options.connectTimeout(connectTimeout);// 连接超时，推荐>3000毫秒
        options.maxWaitTime(maxWaitTime); //
        options.socketTimeout(socketTimeout);// 套接字超时时间，0无限制
        options.threadsAllowedToBlockForConnectionMultiplier(threadsAllowedToBlockForConnectionMultiplier);// 线程队列数，如果连接线程排满了队列就会抛出“Out of semaphores to get db”错误。
        options.writeConcern(WriteConcern.SAFE);//
        options.build();
    }

    // ------------------------------------共用方法---------------------------------------------------

    /**
     * 获取DB实例 - 指定DB
     *
     * @param dbName
     * @return
     */
    public MongoDatabase getDB(String dbName) {
        if (dbName != null && !"".equals(dbName)) {
            MongoDatabase database = mongoClient.getDatabase(dbName);
            return database;
        }
        return null;
    }

    /**
     * 获取collection对象 - 指定Collection
     *
     * @param collName
     * @return
     */
    public MongoCollection<DBObject> getCollection(String collName) {
        if (null == collName || "".equals(collName)) {
            return null;
        }
        MongoDatabase database = mongoClient.getDatabase(Constants.MONGODB_DB_NAME);
        MongoCollection<DBObject> collection =database.getCollection(collName, DBObject.class);
        return collection;
    }

    /**
     * 保存
     * @param obj
     * @param collectionName
     */
    public void save(Object obj,String collectionName){
        if(obj!=null){
            MongoCollection<DBObject> collection =getCollection(collectionName);
//            System.out.println(obj.toString());
//            System.out.println(JSON.toJSONString(obj));
            DBObject bson = (DBObject)com.mongodb.util.JSON.parse(JSON.toJSONString(obj));
            collection.insertOne(bson);
        }
    }

    /**
     * 保存所有
     * @param objList
     * @param collectionName
     */
    public void saveAll(List<Object> objList,String collectionName){
        if(objList!=null){
            Iterator<Object>  iterator=objList.iterator();
            MongoCollection<DBObject> collection = getCollection(collectionName);
            List<DBObject> list=new ArrayList();
            while(iterator.hasNext()){
                Object obj=iterator.next();
                DBObject bson = (DBObject) com.mongodb.util.JSON.parse(JSON.toJSONString(obj));
                list.add(bson);
            }
            collection.insertMany(list);
        }
    }


    /**
     * 查询DB下的所有表名
     */
    public List<String> getAllCollections(String dbName) {
        MongoIterable<String> colls = getDB(dbName).listCollectionNames();
        List<String> _list = new ArrayList<String>();
        for (String s : colls) {
            _list.add(s);
        }
        return _list;
    }

    /**
     * 获取所有数据库名称列表
     *
     * @return
     */
    public MongoIterable<String> getAllDBNames() {
        MongoIterable<String> s = mongoClient.listDatabaseNames();
        return s;
    }

    /**
     * 删除一个数据库
     */
    public void dropDB(String dbName) {
        getDB(dbName).drop();
    }

    /**
     * 查找对象 - 根据主键_id
     *
     * @param id
     * @return
     */
    public DBObject findById(String  collectionName, String id) {
        MongoCollection<DBObject> collection =getCollection(collectionName);
        ObjectId _idobj = null;
        try {
            _idobj = new ObjectId(id);
        } catch (Exception e) {
            return null;
        }
        DBObject myDoc = collection.find(Filters.eq("_id", _idobj)).first();
        return myDoc;
    }

    /** 统计数 */
    public long getCount(String collectionName,Bson filter) {
        MongoCollection<DBObject> collection =getCollection(collectionName);
        long count =  collection.count(filter);
        return count;
    }

    /** 条件查询 */
    public MongoCursor<DBObject> find(String collectionName, BasicDBObject query) {
        MongoCollection<DBObject> collection =getCollection(collectionName);
        return collection.find(query).iterator();
    }

    /** 条件查询 */
    public MongoCursor<DBObject> findAllByCondition(String collectionName, BasicDBObject query,Bson orderBy) {
        MongoCollection<DBObject> collection =getCollection(collectionName);
        return collection.find(query).sort(orderBy).iterator();
    }
    /** 条件查询 */
    public DBObject findOne(String collectionName, BasicDBObject query) {
        MongoCollection<DBObject> collection =getCollection(collectionName);
        return collection.find(query).first();
    }
    /** 分页查询 */
    public MongoCursor<DBObject> findByPage(String collectionName, Bson filter, int pageNo, int pageSize,Bson orderBy) {
        MongoCollection<DBObject> collection =getCollection(collectionName);
        if(orderBy==null){
            orderBy = new BasicDBObject("_id", 1);
        }
        return collection.find(filter).sort(orderBy).skip((pageNo - 1) * pageSize).limit(pageSize).iterator();
    }

    /** 分页查询 */
    public MongoDbPageInfo findByPageInfo(String collectionName, Bson filter, int pageNo, int pageSize, Bson orderBy) {
        MongoCollection<DBObject> collection =getCollection(collectionName);
        if(orderBy==null){
            orderBy = new BasicDBObject("_id", 1);
        }
        long total=getCount(collectionName,filter);
        MongoDbPageInfo pageInfo=new MongoDbPageInfo();
        pageInfo.setPageNum(pageNo);
        pageInfo.setPageSize(pageSize);
        pageInfo.setTotal(total);

        MongoCursor<DBObject> dbObjectMongoCursor=collection.find(filter).sort(orderBy).skip((pageNo - 1) * pageSize).limit(pageSize).iterator();
        List<String> list=new ArrayList<String>();
        if(dbObjectMongoCursor!=null) {
            while (dbObjectMongoCursor.hasNext()) {
                //传入类参数，反回该类对象
                DBObject object = dbObjectMongoCursor.next();
                String json = object.toString();
                if (json != null) {
                    list.add(json);
                }
            }
        }
        pageInfo.setList(list);


        int pages=(int)Math.ceil(total/pageSize);
//        List navigatepageNums=new ArrayList<>();
//        for(int i=0;i<pages;i++){
//            navigatepageNums.add(i+1);
//        }
        pageInfo.setPages(pages);
//        pageInfo.setNavigatepageNums(navigatepageNums);
        return pageInfo;
    }

    /**
     * 通过ID删除
     *
     * @param id
     * @return
     */
    public int deleteById(String collectionName, String id) {
        MongoCollection<DBObject> collection =getCollection(collectionName);
        int count = 0;
        ObjectId _id = null;
        try {
            _id = new ObjectId(id);
        } catch (Exception e) {
            return 0;
        }
        Bson filter = Filters.eq("_id", _id);
        DeleteResult deleteResult = collection.deleteOne(filter);
        count = (int) deleteResult.getDeletedCount();
        return count;
    }


    /**
     * 通过条件删除
     * @return
     */
    public int delete(String collectionName, Bson filter) {
        MongoCollection<DBObject> collection =getCollection(collectionName);
        int count = 0;
        DeleteResult deleteResult = collection.deleteOne(filter);
        count = (int) deleteResult.getDeletedCount();
        return count;
    }
    /**
     * FIXME
     *
     * @param id
     * @param newdoc
     * @return
     */
    public DBObject updateById(String collectionName, String id, DBObject newdoc) {
        MongoCollection<DBObject> collection =getCollection(collectionName);
        ObjectId _idobj = null;
        try {
            _idobj = new ObjectId(id);
        } catch (Exception e) {
            return null;
        }
        Bson filter = Filters.eq("_id", _idobj);
        // coll.replaceOne(filter, newdoc); // 完全替代
        collection.updateOne(filter, new Document("$set", newdoc));
        return newdoc;
    }
    /**
     * FIXME
     *
     * @return
     */
    public DBObject update(String collectionName, DBObject newdoc,Bson filter) {
        MongoCollection<DBObject> collection =getCollection(collectionName);

        collection.updateOne(filter, new Document("$set", newdoc));
        return newdoc;
    }
    public void dropCollection(String dbName, String collName) {
        getDB(dbName).getCollection(collName).drop();
    }

    /**
     * 关闭Mongodb
     */
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
        }
    }
}
