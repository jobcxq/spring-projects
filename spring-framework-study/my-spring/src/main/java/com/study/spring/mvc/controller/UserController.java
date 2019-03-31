package com.study.spring.mvc.controller;

import com.study.spring.framework.annotation.Autowired;
import com.study.spring.framework.annotation.Controller;
import com.study.spring.framework.annotation.RequestMapping;
import com.study.spring.framework.annotation.RequestParam;
import com.study.spring.mvc.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author cnxqin
 * @desc
 * @date 2019/03/30 23:55
 */
@Controller
@RequestMapping("/user")
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @RequestMapping("/sayHello")
    public String sayHello(@RequestParam("msg") String msg){
        msg = "hello " + msg + " !";
        logger.info(msg);
        return msg;
    }


    @RequestMapping("/queryAllUser")
    public String queryAllUser(){
        return userService.queryAllUser().toString();
    }

}
