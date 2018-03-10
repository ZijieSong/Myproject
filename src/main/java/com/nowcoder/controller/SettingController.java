package com.nowcoder.controller;

import com.nowcoder.service.wendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SettingController {
    @Autowired
    wendaService wenda;
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    @ResponseBody
    public String setting(){
        return wenda.getMsg(1);
    }
}
