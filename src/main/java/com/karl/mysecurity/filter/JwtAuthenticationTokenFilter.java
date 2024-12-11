package com.karl.mysecurity.filter;

import com.karl.mysecurity.component.JwtProvider;
import com.karl.mysecurity.entity.MyUserDetail;
import com.karl.mysecurity.service.MyUserDetailsCacheService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final MyUserDetailsCacheService myUserDetailsCacheService;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);

    @Autowired
    public JwtAuthenticationTokenFilter(JwtProvider jwtProvider, MyUserDetailsCacheService myUserDetailsCacheService) {
        this.jwtProvider = jwtProvider;
        this.myUserDetailsCacheService = myUserDetailsCacheService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 拿到Authorization请求头内的信息
        logger.info("进入jwt过滤器");
        String authToken = jwtProvider.getToken(request);
        if(StringUtils.isNotEmpty(authToken)) {
            logger.info("拿到有效token");
            // 拿到token里面的登录账号
            String loginAccount = jwtProvider.getLoginAccount(authToken);
            if(StringUtils.isNotEmpty(loginAccount) && SecurityContextHolder.getContext().getAuthentication() == null) {
                logger.info("拿到有效用户名");
                MyUserDetail userDetail = myUserDetailsCacheService.getUserDetails(loginAccount);
                if(userDetail != null  && jwtProvider.validateToken(authToken, userDetail)) {
                    logger.info("拿到有效用户信息缓存");
                    // 组装authentication对象，构造参数是Principal Credentials 与 Authorities
                    // 后面的拦截器里面会用到 grantedAuthorities 方法
                    logger.info(userDetail.getUsername());
                    logger.info(userDetail.getPassword());
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetail, userDetail.getPassword(), userDetail.getAuthorities());
                    logger.info(authentication.getName());
                    logger.info(authentication.getPrincipal().toString()                                                                                                                                                                                                                                                              );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
