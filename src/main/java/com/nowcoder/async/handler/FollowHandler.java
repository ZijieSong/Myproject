package com.nowcoder.async.handler;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class FollowHandler implements EventHandler{
    @Autowired
    UserService userService;
    @Autowired
    MessageService messageService;

    @Override
    public void doHandle(EventModel model) {
        Message message = new Message();
        message.setToId(model.getEntityOwnerId());
        message.setFromId(WendaUtil.SYSTEM_USERID);
        message.setCreatedDate(new Date());
        User user = userService.getUser(model.getActorId());
        if(model.getEntityType() == EntityType.EntityType_question) {
            message.setContent("用户"+user.getName()+"关注了您的问题: "+
                    "http://127.0.0.1:8080/question/"+model.getEntityId());
        }else if(model.getEntityType() == EntityType.EntityType_user){
            message.setContent("用户"+user.getName()+"关注了您: " +
                    "http://127.0.0.1:8080/user/"+model.getActorId());
        }
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.FOLLOW);
    }
}
