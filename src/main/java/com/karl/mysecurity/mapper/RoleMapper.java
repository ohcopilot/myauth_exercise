package com.karl.mysecurity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.karl.mysecurity.entity.MyRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<MyRole> {
    @Select("SELECT * FROM role")
    List<MyRole> selectCustom();
}
