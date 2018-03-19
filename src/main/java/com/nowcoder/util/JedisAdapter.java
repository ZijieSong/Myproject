package com.nowcoder.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class JedisAdapter implements InitializingBean{
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);
    private JedisPool pool;
    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("redis://localhost:6379/10");
    }
    //添加
    public long sadd(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.sadd(key,value);
        }catch (Exception e){
            logger.error("添加失败"+e.getMessage());
        }finally {
            if(jedis!=null)
                jedis.close();
        }
        return 0;
    }
    //删除
    public long srem(String key, String value){
        Jedis jedis =null;
        try{
            jedis = pool.getResource();
            return jedis.srem(key,value);
        }catch (Exception e){
            logger.error("删除失败"+e.getMessage());
        }finally {
            if(jedis!=null) {
                jedis.close();
            }
        }
        return 0;
    }
    //求数量
    public long scard(String key){
        Jedis jedis =null;
        try{
            jedis = pool.getResource();
            return jedis.scard(key);
        }catch (Exception e){
            logger.error("查询失败"+e.getMessage());
        }finally {
            if(jedis!=null) {
                jedis.close();
            }
        }
        return 0;
    }
    //判断是否存在一个value
    public boolean sismember(String key, String value){
        Jedis jedis =null;
        try{
            jedis = pool.getResource();
            return jedis.sismember(key,value);
        }catch (Exception e){
            logger.error("判断失败"+e.getMessage());
        }finally {
            if(jedis!=null) {
                jedis.close();
            }
        }
        return false;
    }
}
