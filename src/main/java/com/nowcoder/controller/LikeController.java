package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.Comment;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.LikeService;
import com.nowcoder.util.WendaUtil;
import com.sun.webkit.dom.EntityImpl;
import org.apache.catalina.Host;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LikeController {
    @Autowired
    LikeService likeService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    EventProducer eventProducer;
    @Autowired
    CommentService commentService;

    @RequestMapping(value = {"/like"},method = RequestMethod.POST)
    @ResponseBody
    public String like(@RequestParam("commentId") int commentId){
        if(hostHolder.getUser()==null){
            return WendaUtil.getJSONString(999);
        }

        Comment comment = commentService.getCommentById(commentId);


        eventProducer.fireEvent(new EventModel(EventType.LIKE)
                .setExts("questionId", String.valueOf(comment.getEntityId()))
                .setEntityOwnerId(comment.getUserId())
                .setActorId(hostHolder.getUser().getId())
                .setEntityId(commentId)
                .setEntityType(EntityType.EntityType_comment));

        long likeCount = likeService.like(hostHolder.getUser().getId(), EntityType.EntityType_comment,commentId);
        return WendaUtil.getJSONString(0,String.valueOf(likeCount));
    }

    @RequestMapping(value = {"/dislike"},method = RequestMethod.POST)
    @ResponseBody
    public String disLike(@RequestParam("commentId") int commentId){
        if(hostHolder.getUser() == null)
            return WendaUtil.getJSONString(999);
        long likeCount = likeService.disLike(hostHolder.getUser().getId(),EntityType.EntityType_comment,commentId);
        return WendaUtil.getJSONString(0,String.valueOf(likeCount));
    }
}
