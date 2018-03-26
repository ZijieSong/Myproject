package com.nowcoder.async.handler;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.Feed;
import com.nowcoder.model.Question;
import com.nowcoder.model.User;
import com.nowcoder.service.FeedService;
import com.nowcoder.service.FollowService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FeedHandler implements EventHandler {
    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Autowired
    FeedService feedService;

    @Autowired
    FollowService followService;

    @Autowired
    JedisAdapter jedisAdapter;

    private String getData(EventModel model){
        Map<String,String> map = new HashMap<>();
        User user = userService.getUser(model.getActorId());
        if(user == null)
            return null;
        map.put("userId",String.valueOf(user.getId()));
        map.put("userName",user.getName());
        map.put("userHead",user.getHeadUrl());

        if(model.getEventType() == EventType.COMMENT ||
                (model.getEventType() == EventType.FOLLOW && model.getEntityType() == EntityType.EntityType_question)){
            Question question = questionService.selectById(model.getEntityId());
            if(question == null)
                return null;
            map.put("questionId",String.valueOf(question.getId()));
            map.put("questionTitle",question.getTitle());

            return JSONObject.toJSONString(map);
        }
        return null;
    }

    @Override
    public void doHandle(EventModel model) {
        //for test
        Random random = new Random();
        model.setActorId(1+random.nextInt(10));

        Feed feed = new Feed();
        feed.setCreatedDate(new Date());
        feed.setType(model.getEventType().getValue());
        feed.setUserId(model.getActorId());
        feed.setData(getData(model));
        if(feed.getData()==null)
            return;

        feedService.addFeed(feed);

        //push
        List<Integer> followers = followService.getFollowers(EntityType.EntityType_user,model.getActorId(),0,Integer.MAX_VALUE);
        followers.add(0);
        for(Integer follower:followers){
            String timelineKey = RedisKeyUtil.getTimeLineKey(follower);
            jedisAdapter.lpush(timelineKey,String.valueOf(feed.getId()));
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(new EventType[]{EventType.COMMENT,EventType.FOLLOW});
    }
}
