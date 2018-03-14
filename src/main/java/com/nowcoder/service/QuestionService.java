package com.nowcoder.service;

import com.nowcoder.dao.QuestionDAO;
import com.nowcoder.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class QuestionService {
    @Autowired
    QuestionDAO questionDAO;

    @Autowired
    SensitiveService sensitiveService;

    public int addQuestion(Question question){
        //过滤html标签
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        //过滤敏感词
        question.setTitle(sensitiveService.filter(question.getTitle()));
        question.setContent(sensitiveService.filter(question.getContent()));

        return questionDAO.addQuestion(question)>0?question.getId():0;
    }

    public Question selectById(int id){
        return questionDAO.selectById(id);
    }

    public List<Question> getLastestQuestions(int userId,int offset, int limit){
        return questionDAO.selectLatestQuestions(userId,offset,limit);
    }
}
