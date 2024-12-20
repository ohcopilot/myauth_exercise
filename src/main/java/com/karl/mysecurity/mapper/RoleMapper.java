package com.karl.mysecurity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.karl.mysecurity.entity.MyRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<MyRole> {
    @Select("SELECT role.* FROM role,user_role WHERE role.id=user_role.roleid AND user_role.userid=#{userid}")
    List<MyRole> selectCustom(Long userid);
}
