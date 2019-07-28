package com.npcgo.zuul.tools;


import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


public class JedisUtil  {
    protected Logger log = LoggerFactory.getLogger(getClass());


    private static int maxtotal;

    private static int maxwaitmillis;

    private static int retry_num;


    private static String  host;

    private static int port;

    private JedisUtil() {}
    private static JedisPool jedisPool;



    /*读取jedis.properties配置文件*/
    static{
        ResourceBundle rb = ResourceBundle.getBundle("jedis");
        maxtotal = Integer.parseInt(rb.getString("maxtotal"));
        maxwaitmillis = Integer.parseInt(rb.getString("maxwaitmillis"));
        host = rb.getString("host");
        port = Integer.parseInt(rb.getString("port"));
    }

    /*创建连接池*/
    static{
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(maxtotal);
        jedisPoolConfig.setMaxWaitMillis(maxwaitmillis);
        jedisPool = new JedisPool(jedisPoolConfig,host,port);
    }

    /*获取jedis*/
    public static Jedis getJedis(){
        return jedisPool.getResource();
    }

    /*关闭Jedis*/
    public static void close(Jedis jedis){
        if(jedis!=null){
            jedis.close();
        }
    }
}
