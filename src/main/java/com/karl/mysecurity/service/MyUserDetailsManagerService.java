package com.karl.mysecurity.service;

import com.alibaba.fastjson2.JSONObject;
import com.karl.mysecurity.entity.MyUser;
import com.karl.mysecurity.entity.MyUserDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class MyUserDetailsManagerService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(MyUserDetailsManagerService.class);
    @Autowired
    private UserInfoService userInfoService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //logger.info(new BCryptPasswordEncoder().encode("123456"));
        logger.info("校验登录用户名为{}", username);
        MyUser user = userInfoService.getUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        logger.info(JSONObject.toJSONString(user));
        MyUserDetail myUserDetail = MyUserDetail.build(user);
        logger.info(myUserDetail.getUsername());
        logger.info(myUserDetail.getPassword());
        // 后续添加角色权限信息
        return myUserDetail;
    }
}
