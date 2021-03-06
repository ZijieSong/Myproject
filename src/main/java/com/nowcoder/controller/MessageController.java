package com.nowcoder.controller;

import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Message;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;
    @Autowired
    HostHolder hostHolder;

    @RequestMapping(value = "/msg/addMessage",method = RequestMethod.POST)
    @ResponseBody
    public String addMessage(@RequestParam("toName") String toName,
                             @RequestParam("content") String content){
        try{
            Message message = new Message();
            message.setContent(content);
            message.setCreatedDate(new Date());
            if(hostHolder.getUser()==null)
                return WendaUtil.getJSONString(999,"未登录");
            message.setFromId(hostHolder.getUser().getId());
            if(userService.getUserByName(toName)==null)
                return WendaUtil.getJSONString(1,"没有该用户");
            message.setToId(userService.getUserByName(toName).getId());
            messageService.addMessage(message);
            return WendaUtil.getJSONString(0);
        }catch (Exception e){
            logger.error("添加消息失败"+e.getMessage());
            return WendaUtil.getJSONString(1,"发送信息失败");
        }
    }

    @RequestMapping(value = "/msg/detail",method = RequestMethod.GET)
    public String getConversationDetail(Model model,
                                        @RequestParam("conversationId") String conversationId){
        try{
            List<Message> messageList = messageService.getConversationDetail(conversationId,0,10);
            List<ViewObject> vos = new ArrayList<>();
            for(Message message: messageList){
                ViewObject vo = new ViewObject();
                vo.set("message",message);
                vo.set("user",userService.getUser(message.getFromId()));
                vos.add(vo);
            }
            model.addAttribute("messages",vos);

            messageService.setHasRead(hostHolder.getUser().getId(),conversationId);
        }catch (Exception e){
            logger.error("读取会话失败"+e.getMessage());
        }
        return "letterDetail";
    }

    @RequestMapping(value = "/msg/list",method = RequestMethod.GET)
    public String getMessageList(Model model){
        try {
            if (hostHolder.getUser() == null)
                return "redirect:/reglogin";
            int localUserId = hostHolder.getUser().getId();
            List <Message> messageList = messageService.getMessageList(localUserId,0,10);
            List<ViewObject> vos = new ArrayList<>();
            for(Message message: messageList){
                ViewObject vo = new ViewObject();
                vo.set("message",message);
                int targetId = (message.getFromId()==localUserId?message.getToId():message.getFromId());
                vo.set("user",userService.getUser(targetId));
                vo.set("unread",messageService.getUnreadCount(localUserId,message.getConversationId()));
                vos.add(vo);
            }
            model.addAttribute("conversations",vos);
        }catch (Exception e){
            logger.error("获取信息列表失败"+e.getMessage());
        }
        return "letter";
    }
}
