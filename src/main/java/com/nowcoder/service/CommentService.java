package com.nowcoder.service;

import com.nowcoder.dao.CommentDao;
import com.nowcoder.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    CommentDao commentDao;

    @Autowired
    SensitiveService sensitiveService;

    public List<Comment> getCommentsByEntity(int entityId, int entityType){
        return commentDao.selectByEntity(entityId,entityType);
    }

    public int addComment(Comment comment){
        //过滤
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveService.filter(comment.getContent()));
        return commentDao.addComment(comment)>0?comment.getId():0;
    }

    public int getCommentCount(int entityId,int entityType){
        return commentDao.getCommentCount(entityId,entityType);
    }

    public boolean deleteCommentById(int id){
        return commentDao.updateStatusById(1,id)>0;
    }

    public boolean deleteCommentByEntity(int entityId, int entityType){
        return commentDao.updateStatusByEntity(1,entityId,entityType)>0;
    }

    public Comment getCommentById(int commentId){
        return commentDao.getCommentById(commentId);
    }
}
