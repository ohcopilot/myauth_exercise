package com.karl.mysecurity.api;

import com.alibaba.fastjson2.JSONObject;
import com.karl.mysecurity.entity.ResponseResult;
import com.karl.mysecurity.service.MyAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UserApi {
    private static final Logger logger = LoggerFactory.getLogger(UserApi.class);
    private final MyAuthService myAuthService;

    public UserApi(MyAuthService myAuthService) {
        this.myAuthService = myAuthService;
    }

    @PostMapping("/login")
    public ResponseResult login(@RequestBody JSONObject jsonObject) {
        logger.info("登录请求参数，{}", jsonObject.toJSONString());
        JSONObject result = myAuthService.login(jsonObject.getString("username"), jsonObject.getString("password"));
        if(result.getString("code").equals("0")){
            return ResponseResult.ok(result.getString("data"));
        }else {
            return ResponseResult.error(result.getString("msg"));
        }
    }

    @RequestMapping("/logout")
    public ResponseResult logout(@RequestBody JSONObject jsonObject) {
        logger.info("登出请求参数，{}", jsonObject.toJSONString());
        JSONObject result = myAuthService.logout();
        return ResponseResult.ok(result);
    }
}