package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.dao.QuestionDAO;
import com.nowcoder.model.*;
import com.nowcoder.service.*;
import com.nowcoder.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    QuestionService questionService;
    @Autowired
    UserService userService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    CommentService commentService;
    @Autowired
    LikeService likeService;
    @Autowired
    FollowService followService;
    @Autowired
    EventProducer eventProducer;

    @RequestMapping(value = "/question/add",method = RequestMethod.POST)
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title,
                              @RequestParam("content") String content){
        try {
            Question question = new Question();
            question.setCreatedDate(new Date());
            question.setCommentCount(0);
            question.setTitle(title);
            question.setContent(content);
            if(hostHolder.getUser()==null){
                question.setUserId(WendaUtil.ANONYMOUS_USERID);
            }else{
                question.setUserId(hostHolder.getUser().getId());
            }
            if(questionService.addQuestion(question)>0) {
                eventProducer.fireEvent(new EventModel(EventType.ADD_QUESTION)
                        .setActorId(question.getUserId())
                        .setEntityId(question.getId())
                        .setExts("title",question.getTitle())
                        .setExts("content",question.getContent()));

                return WendaUtil.getJSONString(0);
            }
        }catch (Exception e){
            logger.error("增加题目失败"+e.getMessage());
        }
        return WendaUtil.getJSONString(1,"失败");
    }

    @RequestMapping(value = "/question/{questionId}",method = RequestMethod.GET)
    public String questionDetail(Model model,
                                 @PathVariable("questionId") int questionId){
        Question question = questionService.selectById(questionId);
        model.addAttribute("question",question);
        List<Comment> comments = commentService.getCommentsByEntity(questionId, EntityType.EntityType_question);
        List<ViewObject> vos = new ArrayList<>();
        for(Comment comment: comments){
            ViewObject vo = new ViewObject();
            vo.set("comment",comment);
            //设置赞踩功能
            //先设置喜欢与否的标识
            if(hostHolder.getUser()==null){
                vo.set("liked",0);
            }else{
                vo.set("liked",likeService.getLikeStatus(hostHolder.getUser().getId(),EntityType.EntityType_comment,comment.getId()));
            }
            //再设置该评论的喜欢度是多少
            vo.set("likeCount", likeService.getLikeCount(EntityType.EntityType_comment,comment.getId()));
            vo.set("user",userService.getUser(comment.getUserId()));
            vos.add(vo);
        }
        model.addAttribute("comments",vos);

        List<Integer> followerIds = followService.getFollowers(EntityType.EntityType_question,questionId,0,20);
        List<ViewObject> followUsers = new ArrayList<>();
        for(Integer followerId:followerIds){
            User user = userService.getUser(followerId);
            if(user == null)
                continue;
            ViewObject vo = new ViewObject();
            vo.set("id",user.getId());
            vo.set("name",user.getName());
            vo.set("headUrl",user.getHeadUrl());
            followUsers.add(vo);
        }
        model.addAttribute("followUsers",followUsers);
        if(hostHolder.getUser()!=null) {
            model.addAttribute("followed", followService.isFollower(hostHolder.getUser().getId(),EntityType.EntityType_question,questionId));
        }else{
            model.addAttribute("followed",false);
        }

        return "detail";
    }
}
