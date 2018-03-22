package com.nowcoder.controller;

import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Question;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.FollowService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    UserService userService;
    @Autowired
    QuestionService questionService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    FollowService followService;
    @Autowired
    CommentService commentService;

    @RequestMapping(path = {"/user/{userId}"},method = RequestMethod.GET)
    public String userIndex(Model model,
                            @PathVariable("userId") int userId){
        List<ViewObject> vos = getViewObject(userId,0,10);
        model.addAttribute("vos",vos);

        ViewObject vo = new ViewObject();
        if(hostHolder.getUser()!=null){
            vo.set("followed",followService.isFollower(hostHolder.getUser().getId(), EntityType.EntityType_user,userId));
        }else{
            vo.set("followed",false);
        }
        vo.set("user",userService.getUser(userId));
        vo.set("followerCount",followService.getFollowerCount(EntityType.EntityType_user,userId));
        vo.set("followeeCount",followService.getFolloweeCount(userId,EntityType.EntityType_user));
        vo.set("commentCount",commentService.getUserCommentCount(userId));
        model.addAttribute("profileUser",vo);

        return "profile";
    }

    @RequestMapping(path = {"/","/index"},method = RequestMethod.GET)
    public String index(Model model){
        List<ViewObject> vos = getViewObject(0,0,10);
        model.addAttribute("vos",vos);
        return "index";
    }

    public List<ViewObject> getViewObject(int userId, int offset, int limit){
        List<Question> questions = questionService.getLastestQuestions(userId,offset,limit);
        List<ViewObject> vos = new ArrayList<>();
        for(Question question: questions){
            ViewObject vo =new ViewObject();
            vo.set("question",question);
            vo.set("user",userService.getUser(question.getUserId()));
            vo.set("followCount",followService.getFollowerCount(EntityType.EntityType_question,question.getId()));
            vos.add(vo);
        }
        return vos;
    }

    @RequestMapping(path = "/fortest",method = RequestMethod.GET)
    @ResponseBody
    public String forTest(){
        return userService.getUser(3).getName();
    }
}