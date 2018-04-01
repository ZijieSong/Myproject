package com.nowcoder.controller;

import com.nowcoder.model.EntityType;
import com.nowcoder.model.Question;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.FollowService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.SearchService;
import com.nowcoder.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchController {
    @Autowired
    SearchService searchService;
    @Autowired
    QuestionService questionService;
    @Autowired
    FollowService followService;
    @Autowired
    UserService userService;



    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    @RequestMapping(value = {"/search"},method = RequestMethod.GET)
    public String search(Model model,
                         @RequestParam("q") String keyword,
                         @RequestParam(value = "offset",defaultValue = "0") int offset,
                         @RequestParam(value = "count",defaultValue = "10") int count){
        try {
            List<Question> questions = searchService.searchQuestion(keyword, offset, count, "<em>", "</em>");
            List<ViewObject> vos = new ArrayList<>();
            for(Question question : questions){
                Question q = questionService.selectById(question.getId());
                if(question.getTitle()!=null)
                    q.setTitle(question.getTitle());
                if(question.getContent()!=null)
                    q.setContent(question.getContent());
                ViewObject vo = new ViewObject();
                vo.set("question",q);
                vo.set("followCount",followService.getFollowerCount(EntityType.EntityType_question,q.getId()));
                vo.set("user",userService.getUser(q.getUserId()));
                vos.add(vo);
            }
            model.addAttribute("vos",vos);
            model.addAttribute("keyword",keyword);
        }catch (Exception e){
            logger.error("查询失败"+e.getMessage());
        }
        return "result";
    }

}
