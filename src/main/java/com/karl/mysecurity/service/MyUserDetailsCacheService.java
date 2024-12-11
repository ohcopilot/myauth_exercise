package com.karl.mysecurity.service;

import com.karl.mysecurity.entity.MyUserDetail;
import com.karl.mysecurity.utils.RedisUtils;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsCacheService {

    public void setUserDetails(String key, MyUserDetail userDetails) {
        RedisUtils.set(key, userDetails);
    }

    public MyUserDetail getUserDetails(String key) {
        return (MyUserDetail) RedisUtils.get(key);
    }

    public void removeUserDetails(String key) {
        RedisUtils.del(key);
    }

}
