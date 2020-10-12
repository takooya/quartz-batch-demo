package com.takooya.controller;

import com.takooya.mybatis.mapper.UserMapper;
import com.takooya.mybatis.dao.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserMapper userMapper;

    @GetMapping("/addUser")
    public int addUser(String name, int age) {
        User user = new User();
        user.setAge(age);
        user.setName(name);
        return userMapper.insertOne(user);
    }
}
