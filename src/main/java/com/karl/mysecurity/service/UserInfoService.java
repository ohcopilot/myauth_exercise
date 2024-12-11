package com.karl.mysecurity.service;

import com.karl.mysecurity.entity.MyUser;
import com.karl.mysecurity.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserInfoService {

    private final UserMapper userMapper;

    @Autowired
    public UserInfoService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public MyUser getUserByUsername(String username) {
        return userMapper.getUserByUsername(username);
    }
}
