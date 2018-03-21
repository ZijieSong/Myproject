package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.User;
import org.apache.commons.lang.StringUtils;
import com.nowcoder.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    UserService userService;
    @Autowired
    EventProducer eventProducer;

    private static final Logger logger = LoggerFactory.getLogger(LoggerFactory.class);

    @RequestMapping(path = {"/reg"},method = RequestMethod.POST)
    public String reg(Model model,
                      @RequestParam("username") String username,
                      @RequestParam("password") String password,
                      @RequestParam(value = "next",required = false) String next,
                      HttpServletResponse response){
        Map<String,String> map = userService.register(username,password);
        try {
            if(map.containsKey("ticket")){
                Cookie cookie = new Cookie("ticket",map.get("ticket"));
                cookie.setPath("/");
                response.addCookie(cookie);
                if(StringUtils.isNotBlank(next)){
                    return "redirect:"+next;
                }
                return "redirect:/";
            }
            else{
                model.addAttribute("msg",map.get("msg"));
                return "login";
            }
        }catch (Exception e){
            logger.error("注册异常"+e.getMessage());
            return "login";
        }
    }

    @RequestMapping(path = {"/reglogin"},method = RequestMethod.GET)
    public String reglogin(Model model,
                           @RequestParam(value = "next",required = false) String next){
        model.addAttribute("next",next);
        return "login";
    }

    @RequestMapping(path = "/login",method = RequestMethod.POST)
    public String login(Model model,
                        @RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value = "next",required = false) String next,
                        @RequestParam(value = "rememberme",defaultValue = "false") boolean rememberme,
                        HttpServletResponse response){
        try {
            Map<String, Object> map = userService.login(username, password);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", (String)map.get("ticket"));
                cookie.setPath("/");
                if(rememberme){
                    cookie.setMaxAge(3600*24*5);
                }
                response.addCookie(cookie);

                eventProducer.fireEvent(new EventModel(EventType.LOGIN)
                        .setActorId(((User)map.get("user")).getId())
                        .setExts("email","songzijie123@sjtu.edu.cn")
                        .setExts("username",username));

                if(StringUtils.isNotBlank(next)){
                    return "redirect:"+next;
                }
                return "redirect:/";
            } else {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
        }catch (Exception e){
            logger.error("登陆错误"+e.getMessage());
            return "login";
        }
    }

    @RequestMapping(path = {"/logout"},method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/";
    }
}
