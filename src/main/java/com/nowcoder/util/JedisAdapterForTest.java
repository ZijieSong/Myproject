package com.nowcoder.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nowcoder.model.User;
import org.aspectj.lang.annotation.Before;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

public class JedisAdapterForTest {
    public static void print(int index, Object obj){
        System.out.println(index+","+obj.toString());
    }
    public static void main(String[] args) {
        Jedis jedis = new Jedis("redis://localhost:6379/9");
        jedis.flushDB();
        jedis.set("hello","world");
        print(1,jedis.get("hello"));
        jedis.rename("hello","hello2");
        print(2,jedis.get("hello2"));
        jedis.setex("hello3",5,"worddd");

        jedis.set("pv","100");
        jedis.incr("pv");
        print(3,jedis.get("pv"));
        jedis.incrBy("pv",5);
        jedis.decrBy("pv",3);
        print(4,jedis.keys("*"));


        //list
        String listName = "list";
        jedis.del(listName);
        for(int i =0; i<10; i++){
            jedis.lpush(listName,"a"+String.valueOf(i));
        }
        print(5,jedis.lrange(listName,0,12));
        jedis.lpop(listName);
        print(6,jedis.lrange(listName,1,12));
        print(7,jedis.lindex(listName,3));
        jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER,"a4","xx");
        jedis.linsert(listName, BinaryClient.LIST_POSITION.BEFORE,"a4","bb");
        print(8,jedis.lrange(listName,0,12));
        print(9,jedis.keys("*"));

        String userKey = "userxx";
        jedis.hset(userKey,"name","Jim");
        jedis.hset(userKey,"age","12");
        jedis.hset(userKey,"phone","1123123");
        print(10,jedis.hget(userKey,"name"));
        print(11,jedis.hgetAll(userKey));
        jedis.hdel(userKey,"phone");
        print(12,jedis.hgetAll(userKey));
        print(13,jedis.hexists(userKey,"phone"));
        print(14,jedis.hkeys(userKey));
        print(15,jedis.hvals(userKey));
        jedis.hsetnx(userKey,"email","222@qq.com");
        print(16,jedis.hgetAll(userKey));

        String likeKey1 = "commentLike1";
        String likeKey2 ="commentLike2";
        for(int i =0; i<10 ;i++){
            jedis.sadd(likeKey1,String.valueOf(i));
            jedis.sadd(likeKey2,String.valueOf(i*i));
        }
        print(17,jedis.smembers(likeKey1));
        print(18,jedis.sunion(likeKey1,likeKey2));
        print(19,jedis.sdiff(likeKey1,likeKey2));
        print(20,jedis.sismember(likeKey1,"12"));
        print(21,jedis.scard(likeKey1));
        print(22,jedis.srandmember(likeKey1,2));

        String rankkey = "rankkey";
        jedis.zadd(rankkey,15,"jim");
        jedis.zadd(rankkey,60,"bee");
        jedis.zadd(rankkey,90,"lee");
        jedis.zadd(rankkey,75,"lucy");
        jedis.zadd(rankkey,80,"mei");

        print(23,jedis.zcard(rankkey));
        print(24,jedis.zcount(rankkey,61,100));
        print(25,jedis.zscore(rankkey,"mei"));
        print(26,jedis.zincrby(rankkey,2,"mei"));
        print(27,jedis.zrevrange(rankkey,0,2));

        for(Tuple tuple: jedis.zrevrangeByScoreWithScores(rankkey,"80","10")){
            print(28,tuple.getElement()+":"+tuple.getScore());
        }
        print(29,jedis.zrank(rankkey,"mei"));

        String setKey = "zset";
        jedis.zadd(setKey,1,"a");
        jedis.zadd(setKey,1,"b");
        jedis.zadd(setKey,1,"c");
        jedis.zadd(setKey,1,"d");
        jedis.zadd(setKey,1,"e");
        print(30,jedis.zlexcount(setKey,"-","+"));
        print(30,jedis.zlexcount(setKey,"-","+"));
        jedis.zremrangeByLex(setKey,"[d","+");
        print(31,jedis.zrange(setKey,0,10));

        //连接池
        JedisPool jedisPool = new JedisPool("redis://localhost:6379/9");
        for(int i =0; i<100; i++){
            Jedis jedisPoolResource = jedisPool.getResource();
            print(32,jedis.get("pv"));
            jedisPoolResource.close();
        }

        //缓存
        User user = new User();
        user.setPassword("ppp");
        user.setHeadUrl("asd.png");
        user.setSalt("salt");
        user.setName("abc");
        user.setId(1);
        jedis.set("user1", JSONObject.toJSONString(user));
        String value = jedis.get("user1");
        User user1 = JSON.parseObject(value,User.class);

        print(32,jedis.keys("*"));

        print(33,jedis.scard("abc"));
    }
}
