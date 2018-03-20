package com.nowcoder.async;

import com.alibaba.fastjson.JSON;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;

import static javax.swing.UIManager.get;

@Service
public class EventConsumer implements InitializingBean,ApplicationContextAware{
    @Autowired
    JedisAdapter jedisAdapter;

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private ApplicationContext applicationContext;
    private Map<EventType,List<EventHandler>> config = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if(beans!=null){
            for(Map.Entry<String,EventHandler> entry: beans.entrySet()){
                for(EventType eventType: entry.getValue().getSupportEventTypes()){
                    if(!config.containsKey(eventType)){
                        config.put(eventType,new ArrayList<>());
                    }
                    config.get(eventType).add(entry.getValue());
                }
            }
        }

        Thread thread  = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    String key = RedisKeyUtil.getEventqueueKey();
                    List<String> values = jedisAdapter.brpop(0,key);

                    for(String value : values){
                        if(value.equals(key))
                            continue;
                        EventModel model = JSON.parseObject(value,EventModel.class);
                        if(!config.containsKey(model.getEventType())){
                            logger.error("无法识别的类型");
                            continue;
                        }
                        List<EventHandler> handlers = config.get(model.getEventType());
                        for(EventHandler handler:handlers){
                            handler.doHandle(model);
                        }
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
