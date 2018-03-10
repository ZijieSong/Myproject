package com.nowcoder.controller;

import com.nowcoder.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

//@Controller
public class IndexController {
    private static final Logger log = LoggerFactory.getLogger(IndexController.class);
    //这里注释的是地址
    @RequestMapping(path = {"/","/index"})
    //responsebody返回的是简单的字符串而不是模板
    @ResponseBody
    public String index(HttpSession httpSession){
        log.info("VISIT Home");
        return "Hello NowCoder"+httpSession.getAttribute("msg");
    }

    @RequestMapping(path = {"/profile/{groupId}/{userId}"})
    @ResponseBody
    public String profile(@PathVariable("groupId") String groupId,
                          @PathVariable("userId") int userId,
                          @RequestParam(value = "type",defaultValue = "zz") String type,
                          @RequestParam(value = "key",required = false) Integer key){
        return String.format("groupId is: %s, userId is: %d, type: %s. key: %d",groupId,userId,type,key);
    }

    @RequestMapping(path = {"/vm"}, method = RequestMethod.GET)
    public String template(Model model){
        model.addAttribute("value1","vvv1");
        List<String> colors = Arrays.asList(new String[]{"red","blue","white"});
        model.addAttribute("getColors",colors);
        Map<String, String> map = new HashMap<>();
        for(int i =1; i<4;i++){
            map.put(String.valueOf(i),String.valueOf(i*i));
        }
        model.addAttribute("getMap",map);
        model.addAttribute("people",new User("LEE"));
        return "home";
    }

    @RequestMapping(path = "/request", method = RequestMethod.GET)
    @ResponseBody
    public String request(Model model,
                          HttpServletResponse response,
                          HttpServletRequest request,
                          HttpSession session,
                          @CookieValue("JSESSIONID") String cvalue){
        StringBuilder sb = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String name = headerNames.nextElement();
            sb.append(name+": "+request.getHeader(name)+"<br>");
        }
        sb.append(request.getMethod()+"<br>");
        sb.append(request.getQueryString()+"<br>");
        sb.append(request.getPathInfo()+"<br>");
        sb.append(request.getRequestURI()+"<br>");

        if(request.getCookies()!=null)
            for(Cookie cookie: request.getCookies())
                sb.append("cookie: "+cookie.getName()+"value: "+cookie.getValue()+"<br>");
        sb.append(cvalue+"<br>");

        response.addHeader("nowcorder","hello");
        response.addCookie(new Cookie("username","nowcorder"));

        return sb.toString();
    }

    @RequestMapping(path = "/redirect/{code}", method = RequestMethod.GET)
    public RedirectView redirect(@PathVariable("code") int code,
                                 HttpSession httpSession){
        httpSession.setAttribute("msg","from redirect");
        RedirectView red = new RedirectView("/",true);
        if(code==301)
            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        return red;
    }

    @RequestMapping(path = "/admin",method = RequestMethod.GET)
    @ResponseBody
    public String admin(@RequestParam("key") String key){
        if(key.equals("admin"))
            return "Hello admin";
        throw new IllegalArgumentException("error login");
    }

    @ExceptionHandler()
    @ResponseBody
    public String error(Exception e){
        return "error: "+e.getMessage();
    }
}
