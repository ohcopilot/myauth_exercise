package com.karl.mysecurity.service;

import com.alibaba.fastjson2.JSONObject;
import com.karl.mysecurity.component.JwtProvider;
import com.karl.mysecurity.entity.MyUserDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class MyAuthService {

    private static final Logger logger = LoggerFactory.getLogger(MyAuthService.class);
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final MyUserDetailsCacheService myUserDetailsCacheService;

    public MyAuthService(AuthenticationManager authenticationManager,JwtProvider jwtProvider,MyUserDetailsCacheService myUserDetailsCacheService) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.myUserDetailsCacheService = myUserDetailsCacheService;
    }

    public JSONObject login(String username, String password) {
        JSONObject result = new JSONObject();
        result.put("code", 0);
        result.put("msg", "");
        result.put("data", "");
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        if (authentication.isAuthenticated()) {
            logger.info("验证通过，{}", authentication.getName());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            MyUserDetail userDetail =(MyUserDetail) authentication.getPrincipal();
            myUserDetailsCacheService.setUserDetails(username, userDetail);
            String token = jwtProvider.generateToken(userDetail);
            result.put("data", token);
        }else{
            logger.error("验证未通过");
            result.put("code", 1);
            result.put("msg", "验证未通过");
        }
        return result;
    }

    public JSONObject logout() {
        JSONObject result = new JSONObject();
        result.put("code", 0);
        result.put("msg", "");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            MyUserDetail userDetail = (MyUserDetail) authentication.getPrincipal();
            if(myUserDetailsCacheService.getUserDetails(userDetail.getUsername())!=null){
                myUserDetailsCacheService.removeUserDetails(userDetail.getUsername());
            }
            SecurityContextHolder.getContext().setAuthentication(null);
        }
        return result;
    }
}
