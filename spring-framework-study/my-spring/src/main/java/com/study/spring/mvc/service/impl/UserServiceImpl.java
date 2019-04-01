package com.study.spring.mvc.service.impl;

import com.study.spring.framework.annotation.Service;
import com.study.spring.mvc.model.User;
import com.study.spring.mvc.service.UserService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author cnxqin
 * @desc 用户服务（模拟）
 * @date 2019/03/31 00:02
 */
@Service
public class UserServiceImpl<T extends User> implements UserService<T> {

    private static List<User> USER_LIST = new ArrayList<>();

    static {
        USER_LIST.add(new User(1L,"cxq","12345","陈钦松","M",18,"湖北"));
        USER_LIST.add(new User(2L,"cfy","67890","陈肥羊","M",30,"上海"));
        USER_LIST.add(new User(3L,"lpc","13579","雷楼某","F",25,"北京"));
    }

    @Override
    public List queryAllUser() {
        return USER_LIST;
    }

    @Override
    public T getUserById(Long id) {
        if(USER_LIST.isEmpty()){ return null;}
        Optional<User> optional = USER_LIST.stream()
                .filter(user -> user.getId() == id).findFirst();
        if(optional.isPresent()){
            return (T)optional.get();
        }
        return null;
    }

    @Override
    public User addUser(User user) {
        Long id = 1l;
        if(!USER_LIST.isEmpty()){
            List<User> users = USER_LIST.stream().sorted(Comparator.comparing(User::getId).reversed())
                    .collect(Collectors.toList());  //按ID逆序
            id += 1;
        }
        user.setId(id);
        USER_LIST.add(user);
        return user;
    }

    @Override
    public T deleteUserById(Long id) {
        if(USER_LIST.isEmpty()){ return null;}
        Optional<User> optional = USER_LIST.stream()
                .filter(user -> user.getId() == id).findFirst();
        if(optional.isPresent()){
            USER_LIST.remove(optional.get());
            return (T)optional.get();
        }
        return null;
    }

    @Override
    public User updateAddr(Long id, String addr) {
        if(USER_LIST.isEmpty()){ return null;}
        Optional<User> optional = USER_LIST.stream()
                .filter(user -> user.getId() == id).findFirst();
        if(optional.isPresent()){
            optional.get().setAddr(addr);
            return (T)optional.get();
        }
        return null;
    }

    @Override
    public Integer getCount() {
        return USER_LIST.size();
    }

    @Override
    public String login(User user) {
        if(USER_LIST.isEmpty()){ return "登录失败，用户列表为空！";}

        Optional<User> optional = USER_LIST.stream()
                .filter(object -> object.getAccount().equals(user.getAccount())
                    && object.getPassword().equals(user.getPassword())).findFirst();
        if(optional.isPresent()){
            return "1";
        }
        return "登录失败，用户名或密码不存在！";
    }
}
