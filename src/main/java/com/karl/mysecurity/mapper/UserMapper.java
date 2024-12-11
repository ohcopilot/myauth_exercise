package com.karl.mysecurity.mapper;

import com.karl.mysecurity.entity.MyUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    public MyUser getUserByUsername(String username);
}
