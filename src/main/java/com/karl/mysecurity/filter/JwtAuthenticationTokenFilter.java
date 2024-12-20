package com.karl.mysecurity.filter;

import com.karl.mysecurity.component.JwtProvider;
import com.karl.mysecurity.entity.MyUser;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final MyUserDetailsCacheService myUserDetailsCacheService;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);

    public JwtAuthenticationTokenFilter(MyUserDetailsCacheService myUserDetailsCacheService, JwtProvider jwtProvider) {
        this.myUserDetailsCacheService = myUserDetailsCacheService;
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 拿到Authorization请求头内的信息
        String authToken = jwtProvider.getToken(request);
        logger.info("进入jwt过滤器,携带token为：{}",authToken);
        if(StringUtils.isNotEmpty(authToken)) {
            // 拿到token里面的登录账号
            String loginAccount = jwtProvider.getLoginAccount(authToken);
            logger.info("拿到有效token，账户为{}",loginAccount);
            if(StringUtils.isNotEmpty(loginAccount) && SecurityContextHolder.getContext().getAuthentication() == null) {
                MyUserDetail userDetail = myUserDetailsCacheService.getUserDetails(loginAccount);
                if(userDetail != null  && jwtProvider.validateToken(authToken, userDetail)) {
                    logger.info("拿到有效用户信息缓存{},{}",loginAccount,userDetail.toString());
                    // 组装authentication对象，构造参数是Principal Credentials 与 Authorities
                    // 后面的拦截器里面会用到 grantedAuthorities 方法
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
