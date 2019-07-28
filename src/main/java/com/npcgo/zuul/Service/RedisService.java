package com.npcgo.zuul.Service;

import com.npcgo.zuul.tools.JedisUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.io.UnsupportedEncodingException;


@Service
public class RedisService {


   // @Resource(type = org.springframework.data.redis.core.RedisTemplate.class)
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    private static Logger logger = LoggerFactory.getLogger(RedisService.class);

    private Jedis jedis;
    public  Boolean ValidToken(String token) {
        Boolean iResult=false;

        logger.info("access:"+token);
//        iResult=redisTemplate.hasKey(token);
//        jedis=new Jedis("192.168.31.105",6379);
        jedis= JedisUtil.getJedis();

        if (jedis.exists("access:"+token))
        {
            try {
                Claims body = Jwts.parser().setSigningKey("test-secret".getBytes("UTF-8"))
                        .parseClaimsJws(token).getBody();

                String provider=(String) body.get("provider");
                logger.info("provider" + provider);
                iResult=true;
            }catch ( UnsupportedEncodingException e) {
                iResult=false;
            }
            catch (Exception ex)
            {
                iResult=false;
                logger.error(ex.getMessage());
            }
            finally {

            }
        }

        return  iResult;
    }
    public Integer appendKey(String key,String value)
    {
        Integer iRet=-1;
        try {

            iRet=  redisTemplate.opsForValue().append(key,value);


        }catch(Exception ex)
        {
            logger.error(ex.getMessage());
        }
       return iRet;

    }





}
