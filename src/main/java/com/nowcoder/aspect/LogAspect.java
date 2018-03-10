package com.nowcoder.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

@Aspect
@Component
public class LogAspect {
    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);
    @Before("execution(* com.nowcoder.controller.*Controller.*(..))")
    public void before(JoinPoint joinPoint){
        StringBuilder sb = new StringBuilder();
        for(Object obj: joinPoint.getArgs())
            sb.append("Arg: "+obj+"|");
        log.info("before"+sb.toString());
    }
    @After("execution(* com.nowcoder.controller.IndexController.*(..))")
    public void after(){
        log.info("after"+new Date());
    }
}
