package com.sdyk.util;

import com.sdyk.config.SystemInitConfig;
import com.sdyk.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by taylor on 2018/4/2.
 */
public class RedisUtils {
    private static final Logger Log = LoggerFactory.getLogger(RedisUtils.class);
    private static JedisPool pool = null;
    private RedisUtils(){

    }
    public static JedisPool init(){
        if(pool==null) {
            Log.info("读取redis配置文件结束................:host" + Constants.REDIS_HOST + ";:port" + Constants.REDIS_PORT + ";auth:" + Constants.REDIS_AUTH + ";dataBase:" + Constants.REDIS_DATABASE + ";maxActive:" + Constants.REDIS_MAXACTIVE + ";maxIdle:" + Constants.REDIS_MAXIDLE + ";testOnBorrow:" + Constants.REDIS_TESTONBORROW + ";timeOut:" + Constants.REDIS_TIMEOUT);


            Log.info("redis连接池 start................");
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(Integer.valueOf(Constants.REDIS_MAXACTIVE));
            config.setMaxIdle(Integer.valueOf(Constants.REDIS_MAXIDLE));
            config.setMaxWaitMillis(Integer.valueOf(Constants.REDIS_TIMEOUT));
            pool = new JedisPool(config, Constants.REDIS_HOST, Integer.valueOf(Constants.REDIS_PORT), Integer.valueOf(Constants.REDIS_TIMEOUT), Constants.REDIS_AUTH, Integer.valueOf(Constants.REDIS_DATABASE));
        }
        return pool;
    }



    /*** <p>Description: 返回资源 </p>
     * @author gj
     * @date  2017年1月5日
     * @param
     */
    public static void returnResource(JedisPool pool, Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }



    /*** <p>Description: 获取jedis 实例</p>
     * @author gj
     * @date  2017年1月5日
     * @param
     */
    public static Jedis getJedis() throws Exception {
        Jedis jedis = null;
        pool = init();
        jedis = pool.getResource();
        return jedis;
    }

    public static Long incr(String key) {
        Long result = 0l;
        Jedis jedis = null;
        try {
            jedis= getJedis();
            result = jedis.incr(key);//.getBytes()

        } catch(Exception e){
            if(jedis != null){
                jedis.close();
            }
            e.printStackTrace();
        } finally {
            returnResource(pool, jedis);
        }

        return result;
    }
    public static Long decr(String key) {
        Jedis jedis = null;
        Long result = 0l;
        try {
            jedis= getJedis();
            result = jedis.decr(key);//.getBytes()
        } catch (Exception e) {
            if(jedis != null){
                jedis.close();
            }
            e.printStackTrace();
        }finally{
            returnResource(pool, jedis);
        }
        return result;
    }

    /*** <p>Description: 得到值</p>
     * @author gj
     * @date  2017年1月5日
     * @param key
     */
    public static Object get(String key){
        byte[] value = null;
        Jedis jedis = null;
        try {
            jedis= getJedis();
            value = jedis.get(key.getBytes());
        } catch(Exception e){
            if(jedis != null){
                jedis.close();
            }
            e.printStackTrace();
        } finally {
            returnResource(pool, jedis);
        }
        return unserialize(value);
    }
    /*** <p>Description: 得到值</p>
     * @author gj
     * @date  2017年1月5日
     * @param key
     */
    public static String getString(String key){
        String value = null;
        Jedis jedis = null;
        try {
            jedis= getJedis();
            value = jedis.get(key);
        } catch(Exception e){
            if(jedis != null){
                jedis.close();
            }
            e.printStackTrace();
        } finally {
            returnResource(pool, jedis);
        }
        return value;
    }
    /*** <p>Description: 获取list所有值</p>
     * @author gj
     * @date
     * @param key
     */
    public static List<String> lRangeAll(String key){
        List<String> values = null;
        Jedis jedis = null;
        try {
            jedis= getJedis();
            values = jedis.lrange(key,0,-1);
        } catch(Exception e){
            if(jedis != null){
                jedis.close();
            }
            e.printStackTrace();
        } finally {
            returnResource(pool, jedis);
        }
        return values;
    }

    /*** <p>Description: 设置list值</p>
     * @author gj
     * @date
     * @param key
     */
    public static Long rPush(String key,String value){
        Long returnValue = null;
        Jedis jedis = null;
        try {
            jedis= getJedis();
            returnValue = jedis.rpush(key,value);
        } catch(Exception e){
            if(jedis != null){
                jedis.close();
            }
            e.printStackTrace();
        } finally {
            returnResource(pool, jedis);
        }
        return returnValue;
    }

    /*** <p>Description: 自增</p>
     * @author gj
     * @date  2017年1月5日
     * @param key
     */
    public static String getIncr(String key){
        String value = null;
        Jedis jedis = null;
        try {
            jedis= getJedis();
            value = jedis.get(key);
        } catch(Exception e){
            if(jedis != null){
                jedis.close();
            }
            e.printStackTrace();
        } finally {
            returnResource(pool, jedis);
        }
        return value;
    }

    /*** <p>Description: 设置键值</p>
     * @author gj
     * @date  2017年1月5日
     * @param key value
     */
    public static String set(String key,Object value){
        Jedis jedis = null;
        String ans = null;
        try {
            jedis = getJedis();

            byte keyByte[]=key.getBytes();
            byte valueByte[]=serialize(value);
            ans = jedis.set(keyByte,valueByte);
        } catch (Exception e) {
            //释放redis对象
            if(jedis != null){
                jedis.close();
            }
            e.printStackTrace();
        } finally {
            //返还到连接池
            returnResource(pool, jedis);
        }
        return ans;
    }
    public static long zadd(String key,long score,String member){
        Jedis jedis = null;
        String ans = null;
        try {
            jedis = getJedis();
            jedis.zadd(key.getBytes(),score,member.getBytes());
        } catch (Exception e) {
            //释放redis对象
            if(jedis != null){
                jedis.close();
            }
            e.printStackTrace();
        } finally {
            //返还到连接池
            returnResource(pool, jedis);
        }
        return 0;

    }

    public static long zRem(String key,String member){
        Jedis jedis = null;
        String ans = null;
        try {
            jedis = getJedis();
            jedis.zrem(key,member);
        } catch (Exception e) {
            //释放redis对象
            if(jedis != null){
                jedis.close();
            }
            e.printStackTrace();
        } finally {
            //返还到连接池
            returnResource(pool, jedis);
        }
        return 0;

    }
    public static long zRank(String key,String member){
        Jedis jedis = null;
        try {
            jedis = getJedis();

            return  jedis.zrank(key.getBytes(),member.getBytes());
        } catch (Exception e) {
            //释放redis对象
            if(jedis != null){
                jedis.close();
            }
            e.printStackTrace();
        } finally {
            //返还到连接池
            returnResource(pool, jedis);
        }
        return -1;
    }


    public static Set<String> zRevRange(String key,long start,long end){
        Jedis jedis = null;
        try {
            jedis = getJedis();

            return  jedis.zrevrange(key,start,end);
        } catch (Exception e) {
            //释放redis对象
            if(jedis != null){
                jedis.close();
            }
            e.printStackTrace();
        } finally {
            //返还到连接池
            returnResource(pool, jedis);
        }
        return null;

    }

    /*** <p>Description: 设置键值 并同时设置有效期</p>
     * @author gj
     * @date  2017年1月5日
     * @param key seconds秒数 value
     */
    public static String setex(String key,int seconds,String value){
        Jedis jedis = null;
        String ans = null;
        try {
            jedis = getJedis();
            byte keyByte[]=key.getBytes();
            byte valueByte[]=serialize(value);
            ans = jedis.setex(keyByte,seconds,valueByte);
        } catch (Exception e) {
            //释放redis对象
            if(jedis != null){
                jedis.close();
            }
            e.printStackTrace();
        } finally {
            //返还到连接池
            returnResource(pool, jedis);
        }
        return ans;
    }

    /*** <p>模糊查询</p>
     */
    public static List<Object> keys(String key){
        if(key.indexOf("*")<=0){
            key=key+"*";
        }
        Set<byte[]> value = null;
        Jedis jedis = null;
        List<Object> list=new ArrayList<Object>();
        try {
            jedis= getJedis();
            value = jedis.keys(key.getBytes());
            if(value!=null && value.size()>0){
                Iterator<byte[]> iterator=value.iterator();
                while(iterator.hasNext()){
                    byte[] childKeyByte=iterator.next();
                    if(childKeyByte!=null && childKeyByte.length>0) {
                        String childKey=new String(childKeyByte);
//                        Object childKey = unserialize(childKeyByte);
                        byte val[]=jedis.get(childKey.getBytes());
                        if(val!=null){
                            list.add(unserialize(val));
                        }

                    }
                }
            }
        } catch(Exception e){
            if(jedis != null){
                jedis.close();
            }
            e.printStackTrace();
        } finally {
            returnResource(pool, jedis);
        }
        return list;
    }
    /*** <p>模糊查询</p>
     */
    public static List<String> scan(String key,Integer count){
        if(key.indexOf("*")<=0){
            key=key+"*";
        }
        Set<byte[]> value = null;
        Jedis jedis = null;
        List<String> retList = new ArrayList<String>();
        try {
            jedis= getJedis();

            ScanParams scanParams = new ScanParams();
            // 设置每次scan个数
            scanParams.count(count);
            String scanRet = "0";

            ScanResult ret = jedis.scan(scanRet, scanParams.match((key+"*").getBytes()));
//            scanRet = ret.getStringCursor();
            retList.addAll(ret.getResult());
            Log.info("::::::::::::::::::::"+retList.size());
        } catch(Exception e){
            if(jedis != null){
                jedis.close();
            }
            e.printStackTrace();
        } finally {
            returnResource(pool, jedis);
        }
        return retList;
    }
    //    public static void main(final String[] args){
//        List<Object> l= RedisUtils.scan("ssssss");
//        System.out.println(l.size());
//    }
    public static void main(String[] args) {
//    // TODO Auto-generated method stub
//    try {long start = System.currentTimeMillis();Process process = Runtime.getRuntime().exec(new String[] {"wmic","cpu","get","ProcessorId"});
//        process.getOutputStream().close();Scanner sc = new Scanner(process.getInputStream());String property = sc.next();String serial = sc.next();
//        System.out.println(property +":"+ serial);
//        System.out.println("time:"+ (System.currentTimeMillis() - start));
//    } catch (IOException e) {// TODO Auto-generated catch blocke.printStackTrace();
//        e.printStackTrace();;
//    }
        SystemInitConfig.init();
        Jedis jRedis = null;
        try {
            jRedis = getJedis();
        } catch (Exception e) {
            e.printStackTrace();
        }
        jRedis.publish("JRedisChat","my name is chenLong");
        jRedis.publish("JRedisChat","Hello chenLong!");
    }
    /*** <p>Description: 删除某个键</p>
     * @author wenquan
     * @date  2017年1月5日
     * @param keys
     */
    public static Long del(String...keys){

        Jedis jedis = null;
        Long res = 0l;
        try {
            jedis = getJedis();
            res = jedis.del(keys);

        } catch (Exception e) {
            if(jedis != null){
                jedis.close();
            }
            e.printStackTrace();
        } finally {
            //返还到连接池
            returnResource(pool, jedis);
        }
        return res;
    }

    public static  byte[] serialize(Object object) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
            // 序列化
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch (Exception e) {

        }
        return null;
    }

    public static Object unserialize( byte[] bytes) {
        ByteArrayInputStream bais = null;
        try {
            // 反序列化
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {

        }
        return null;
    }

}
