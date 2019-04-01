package com.study.spring.mvc.service;

import com.study.spring.mvc.model.User;

import java.util.List;

/**
 * @author cnxqin
 * @desc 用户服务接口
 * @date 2019/03/31 00:00
 */
public interface UserService<T extends User> {

    List<T> queryAllUser();

    T getUserById(Long id);

    User addUser(User user);

    T deleteUserById(Long id);

    User updateAddr(Long id, String addr);

    Integer getCount();

    String login(User user);

}
