package com.nowcoder.service;

import com.nowcoder.dao.MessageDao;
import com.nowcoder.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    MessageDao messageDao;
    @Autowired
    SensitiveService sensitiveService;

    public int addMessage(Message message){
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveService.filter(message.getContent()));
        return messageDao.addMessage(message)>0?message.getId():0;
    }

    public List<Message> getConversationDetail(String conversationId,int offset,int limit){
        return messageDao.getConversationDetail(conversationId,offset,limit);
    }

    public List<Message> getMessageList(int userId, int offset,int limit){
        return messageDao.getMessageList(userId,offset, limit);
    }

    public int getUnreadCount(int toId, String conversationId){
        return messageDao.unreadCount(toId,conversationId);
    }

    public boolean setHasRead(int toId,String conversationId){
        return messageDao.updateHasRead(1,toId,conversationId)>0;
    }
}
