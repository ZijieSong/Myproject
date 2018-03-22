package com.nowcoder.service;

import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


@Service
public class FollowService {
    @Autowired
    JedisAdapter jedisAdapter;

    public boolean follow(int userId, int entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);

        Date date = new Date();
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        tx.zadd(followerKey,date.getTime(),String.valueOf(userId));
        tx.zadd(followeeKey,date.getTime(),String.valueOf(entityId));
        List<Object> res = jedisAdapter.exec(tx,jedis);

        return res.size()==2&&(long)res.get(0)>0&&(long)res.get(1)>0;
    }

    public boolean unfollow (int userId, int entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);

        Date date = new Date();
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        tx.zrem(followerKey,String.valueOf(userId));
        tx.zrem(followeeKey,String.valueOf(entityId));
        List<Object> res = jedisAdapter.exec(tx,jedis);

        return res.size()==2&&(long)res.get(0)>0&&(long)res.get(1)>0;
    }

    public List<Integer> getUserIdsFromSet(Set<String> userIds){
        List<Integer> ids = new ArrayList<>();
        for(String str:userIds){
            ids.add(Integer.parseInt(str));
        }
        return ids;
    }

    public List<Integer> getFollowers(int entityType, int entityId, int offset, int count){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);

        return getUserIdsFromSet(jedisAdapter.zrevrange(followerKey,offset,count+offset));
    }

    public List<Integer> getFollowees(int userId, int entityType, int offset, int count){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);

        return getUserIdsFromSet(jedisAdapter.zrevrange(followeeKey,offset,offset+count));
    }

    public long getFollowerCount(int entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

        return jedisAdapter.zcard(followerKey);
    }

    public long getFolloweeCount(int userId, int entityType){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);

        return jedisAdapter.zcard(followeeKey);
    }

    public boolean isFollower(int userId, int entityType, int entityId){
        String followerKeys = RedisKeyUtil.getFollowerKey(entityType, entityId);

        return jedisAdapter.zscore(followerKeys,String.valueOf(userId))!=null;
    }
}
