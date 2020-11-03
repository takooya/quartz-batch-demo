package com.takooya.controller;

import com.takooya.common.RequestResult;
import com.takooya.enums.ResultEnum;
import com.takooya.exception.BusinessException;
import com.takooya.mybatis.dao.User;
import com.takooya.mybatis.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author takooya
 */
@Slf4j
@RestController
@RequestMapping("user")
public class UserController {
    private final UserMapper userMapper;

    public UserController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @PostMapping("/add")
    public RequestResult addUser(@RequestBody User user) {
        int i = userMapper.insertOne(user);
        if (i != 1) {
            throw new BusinessException(ResultEnum.USER_ADD_FAIL);
        }
        return RequestResult.success(user);
    }
}
