package com.karl.mysecurity;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.karl.mysecurity.entity.MyRole;
import com.karl.mysecurity.mapper.RoleMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MysecurityApplicationTests {
@Autowired
private RoleMapper roleMapper;

    @Test
    void contextLoads() {
        MyRole role = new MyRole();
        role.setName("Test");
        roleMapper.insert(role);
        List<MyRole> roles = roleMapper.selectList(null);
        roles.forEach(System.out::println);

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("name", "Test");
        System.out.println(roleMapper.selectOne(queryWrapper).getName());
        UpdateWrapper<MyRole> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("name", "Test").set("description", "Test");
        roleMapper.update(role, updateWrapper);
        roleMapper.selectCustom().stream().forEach(System.out::println);

    }

}
