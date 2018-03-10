package com.nowcoder.service;

import org.springframework.stereotype.Service;

@Service
public class wendaService {
    public String getMsg(int userID){
        return "userID is: "+userID;
    }
}
