package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.*;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.FollowService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class FollowController {
    @Autowired
    HostHolder hostHolder;
    @Autowired
    FollowService followService;
    @Autowired
    UserService userService;
    @Autowired
    QuestionService questionService;
    @Autowired
    EventProducer eventProducer;
    @Autowired
    CommentService commentService;

    @RequestMapping(value = {"/followUser"},method = RequestMethod.POST)
    @ResponseBody
    public String followUser(@RequestParam("userId") int userId){
        if(hostHolder.getUser()==null)
            return WendaUtil.getJSONString(999);
        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.EntityType_user,userId);

        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUser().getId())
                .setEntityType(EntityType.EntityType_user)
                .setEntityId(userId).setEntityOwnerId(userId));

        return WendaUtil.getJSONString(ret?0:1,String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(),EntityType.EntityType_user)));
    }

    @RequestMapping(value = {"/unfollowUser"},method = RequestMethod.POST)
    @ResponseBody
    public String unfollowUser(@RequestParam("userId") int userId){
        if(hostHolder.getUser()==null)
            return WendaUtil.getJSONString(999);
        boolean res = followService.unfollow(hostHolder.getUser().getId(),EntityType.EntityType_user,userId);

        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostHolder.getUser().getId())
                .setEntityType(EntityType.EntityType_user)
                .setEntityId(userId).setEntityOwnerId(userId));

        return WendaUtil.getJSONString(res?0:1,String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(),EntityType.EntityType_user)));
    }

    @RequestMapping(value = {"/followQuestion"},method = RequestMethod.POST)
    @ResponseBody
    public String followQuestion(@RequestParam("questionId") int questionId){
        if(hostHolder.getUser()==null)
            return WendaUtil.getJSONString(999);
        Question question = questionService.selectById(questionId);
        if(question==null)
            return WendaUtil.getJSONString(1,"question not found");
        boolean res = followService.follow(hostHolder.getUser().getId(),EntityType.EntityType_question,questionId);

        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUser().getId())
                .setEntityType(EntityType.EntityType_question)
                .setEntityId(questionId).setEntityOwnerId(question.getUserId()));

        Map<String,Object> info = new HashMap<>();
        info.put("headUrl",hostHolder.getUser().getHeadUrl());
        info.put("id",hostHolder.getUser().getId());
        info.put("name",hostHolder.getUser().getName());
        info.put("count",followService.getFollowerCount(EntityType.EntityType_question,questionId));

        return WendaUtil.getJSONString(res?0:1, info);
    }

    @RequestMapping(value = {"/unfollowQuestion"},method = RequestMethod.POST)
    @ResponseBody
    public String unfollowQuestion(@RequestParam("questionId") int questionId){
        if(hostHolder.getUser()==null)
            return WendaUtil.getJSONString(999);
        Question question = questionService.selectById(questionId);
        if(question==null)
            return WendaUtil.getJSONString(1,"question not found");
        Boolean res = followService.unfollow(hostHolder.getUser().getId(),EntityType.EntityType_question,questionId);

        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostHolder.getUser().getId())
                .setEntityType(EntityType.EntityType_question)
                .setEntityId(questionId).setEntityOwnerId(question.getUserId()));

        Map<String, Object> info = new HashMap<>();
        info.put("id",hostHolder.getUser().getId());
        info.put("count",followService.getFollowerCount(EntityType.EntityType_question,questionId));

        return WendaUtil.getJSONString(res?0:1,info);
    }

    private List<ViewObject> getUserDetailList(int localUserId, List<Integer> userIds){
        List<ViewObject> vos = new ArrayList<>();
        for(Integer userId: userIds){
            ViewObject vo = new ViewObject();
            User user = userService.getUser(userId);
            if(user == null)
                continue;
            vo.set("user",user);
            vo.set("followerCount",followService.getFollowerCount(EntityType.EntityType_user,userId));
            vo.set("followeeCount",followService.getFolloweeCount(userId,EntityType.EntityType_user));
            vo.set("commentCount",commentService.getUserCommentCount(userId));
            if(localUserId!=0){
                vo.set("followed",followService.isFollower(localUserId,EntityType.EntityType_user,userId));
            }else {
                vo.set("followed",false);
            }

            vos.add(vo);
        }
        return vos;
    }

    @RequestMapping(value = {"/user/{uid}/followers"},method = RequestMethod.GET)
    public String followers(@PathVariable("uid") int userId,
                            Model model){
        List<Integer> followersId = followService.getFollowers(EntityType.EntityType_user,userId,0,10);
        if(hostHolder.getUser()!=null)
            model.addAttribute("followers",getUserDetailList(hostHolder.getUser().getId(),followersId));
        else
            model.addAttribute("followers",getUserDetailList(0,followersId));

        model.addAttribute("curUser",userService.getUser(userId));
        model.addAttribute("followerCount",followService.getFollowerCount(EntityType.EntityType_user,userId));

        return "followers";
    }

    @RequestMapping(value = {"/user/{uid}/followees"},method = RequestMethod.GET)
    public String followees(@PathVariable("uid") int userId,
                            Model model){
        List<Integer> followeesIdList = followService.getFollowees(userId,EntityType.EntityType_user,0,10);
        if(hostHolder.getUser()!=null)
            model.addAttribute("followees",getUserDetailList(hostHolder.getUser().getId(),followeesIdList));
        else
            model.addAttribute("followees",getUserDetailList(0,followeesIdList));

        model.addAttribute("curUser",userService.getUser(userId));
        model.addAttribute("followeeCount",followService.getFolloweeCount(userId,EntityType.EntityType_user));

        return "followees";
    }
}
