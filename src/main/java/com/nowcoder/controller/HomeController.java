package com.nowcoder.controller;

import com.nowcoder.model.Question;
import com.nowcoder.model.ViewObject;
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

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    UserService userService;
    @Autowired
    QuestionService questionService;

    @RequestMapping(path = {"/user/{userId}"},method = RequestMethod.GET)
    public String userIndex(Model model,
                            @PathVariable("userId") int userId){
        List<ViewObject> vos = getViewObject(userId,0,1);
        model.addAttribute("vos",vos);
        return "index";
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
            vos.add(vo);
        }
        return vos;
    }
}