package com.karl.mysecurity.service;

import com.karl.mysecurity.entity.MyRole;
import com.karl.mysecurity.entity.MyUser;
import com.karl.mysecurity.mapper.RoleMapper;
import com.karl.mysecurity.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.stream.Collectors;

@Service
public class UserInfoService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    @Autowired
    public UserInfoService(UserMapper userMapper, RoleMapper roleMapper) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
    }

    public MyUser getUserByUsername(String username) {
        MyUser user =  userMapper.getUserByUsername(username);
        if (user != null) {
            System.out.println(user.getId());
            user.setRoles(new HashSet<>(roleMapper.selectCustom(user.getId())));
        }
        return user;
    }
}
