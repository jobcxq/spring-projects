package com.study.spring.mvc.service.impl;

import com.study.spring.framework.annotation.Service;
import com.study.spring.mvc.model.User;
import com.study.spring.mvc.service.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cnxqin
 * @desc
 * @date 2019/03/31 00:02
 */
@Service
public class UserServiceImpl<T extends User> implements UserService<T> {

    private static List<User> MOCK_USER_LIST = new ArrayList<>();

    static {
        MOCK_USER_LIST.add(new User(1L,"cxq","12345","陈钦松","M",18,"湖北"));
        MOCK_USER_LIST.add(new User(2L,"cfy","67890","陈肥羊","M",30,"上海"));
        MOCK_USER_LIST.add(new User(3L,"lpc","13579","雷楼某","F",25,"北京"));
    }

    @Override
    public List queryAllUser() {
        return MOCK_USER_LIST;
    }

    @Override
    public T getUserById(Long id) {
        return null;
    }
}
