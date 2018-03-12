package com.nowcoder.service;

import antlr.StringUtils;
import com.nowcoder.dao.LoginTicketDao;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import com.nowcoder.util.WendaUtil;
import org.apache.ibatis.annotations.Select;
import org.omg.PortableInterceptor.SUCCESSFUL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;

import static antlr.StringUtils.*;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotEmpty;


@Service
public class UserService {
    @Autowired
    UserDAO userDAO;

    @Autowired
    LoginTicketDao loginTicketDao;

    public User getUser(int id){
        return userDAO.selectById(id);
    }

    public Map<String, String> register(String username, String password){
        Map<String,String> map = new HashMap<>();
        if(isBlank(username)){
            map.put("msg","用户名不能为空");
            return map;
        }
        if(isBlank(password)){
            map.put("msg","密码不能为空");
            return map;
        }
        User user = userDAO.selectByName(username);
        if(user!=null){
            map.put("msg","用户已存在");
            return map;
        }
        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png",
                new Random().nextInt(1000)));
        user.setPassword(WendaUtil.MD5(password+user.getSalt()));
        userDAO.addUser(user);
        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }

    public Map<String,String> login(String username, String password){
        Map<String,String> map = new HashMap<>();
        if(org.apache.commons.lang.StringUtils.isBlank(username)){
            map.put("msg","用户名不能为空");
            return map;
        }
        if(org.apache.commons.lang.StringUtils.isBlank(password)){
            map.put("msg","密码不能为空");
            return map;
        }
        User user = userDAO.selectByName(username);
        if(user==null){
            map.put("msg","用户不存在");
            return map;
        }
        if(!WendaUtil.MD5(password+user.getSalt()).equals(user.getPassword())){
            map.put("msg","密码错误");
            return map;
        }
        //如果登录成功，下发一个T票
        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }

    public String addLoginTicket(int userId){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime()+1000*3600*24);
        loginTicket.setExpired(date);
        loginTicket.setStatus(0);
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
        loginTicketDao.addTicket(loginTicket);
        return loginTicket.getTicket();
    }

    public void logout(String ticket){
        loginTicketDao.updateStatus(1,ticket);
    }

}
