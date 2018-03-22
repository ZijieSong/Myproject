package com.nowcoder.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.util.List;
import java.util.Set;

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

    public long lpush(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.lpush(key,value);
        }catch (Exception e){
            logger.error("添加元素失败"+e.getMessage());
        }finally {
            if(jedis!=null) {
                jedis.close();
            }
        }
        return 0;
    }

    public List<String> brpop(int timeout,String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.brpop(timeout,key);
        }catch (Exception e){
            logger.error("移除失败"+e.getMessage());
        }finally {
            if(jedis!=null)
                jedis.close();
        }
        return null;
    }

    public long zadd(String key, double count, String value){
        Jedis jedis = null;
        try{
            jedis=pool.getResource();
            return jedis.zadd(key,count,value);
        }catch (Exception e){
            logger.error("添加zadd失败"+e.getMessage());
        }finally {
            if(jedis!=null)
                jedis.close();
        }
        return 0;
    }

    public Double zscore(String key, String value){
        Jedis jedis =null;
        try {
            jedis = pool.getResource();
            return jedis.zscore(key,value);
        }catch (Exception e){
            logger.error("zscore失败"+e.getMessage());
        }finally {
            if(jedis!=null)
                jedis.close();
        }
        return null;
    }

    public Long zcard(String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.zcard(key);
        }catch (Exception e){
            logger.error("zcard失败"+e.getMessage());
        }finally {
            if(jedis!=null)
                jedis.close();
        }
        return null;
    }

    public Set<String> zrevrange(String key, int start, int end){
        Jedis jedis =null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrange(key,start,end);
        }catch (Exception e){
            logger.error("zrange failed"+e.getMessage());
        }finally {
            if(jedis!=null)
                jedis.close();
        }
        return null;
    }

    public Jedis getJedis(){
        return pool.getResource();
    }

    public Transaction multi(Jedis jedis){
        try{
            return jedis.multi();
        }catch (Exception e){
            logger.error("开启事务失败"+e.getMessage());
        }
        return null;
    }

    public List<Object> exec(Transaction tx, Jedis jedis){
        try{
            return tx.exec();
        }catch (Exception e){
            logger.error("执行事务失败");
            tx.discard();
        }finally {
            if(tx!=null){
                try {
                    tx.close();
                } catch (IOException e) {
                    logger.error("关闭失败"+e.getMessage());
                }
            }
            if(jedis!=null){
                jedis.close();
            }
        }
        return null;
    }


}
