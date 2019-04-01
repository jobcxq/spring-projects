package com.study.spring.mvc.controller;

import com.study.spring.framework.annotation.Autowired;
import com.study.spring.framework.annotation.Controller;
import com.study.spring.framework.annotation.RequestMapping;
import com.study.spring.framework.annotation.RequestParam;
import com.study.spring.mvc.model.User;
import com.study.spring.mvc.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
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
    public List<User> queryAllUser(){
        return userService.queryAllUser();
    }

    @RequestMapping("/queryAllUser")
    public User getUserById(HttpServletRequest request, @RequestParam("id") Long id){
        return userService.getUserById(id);
    }


}
