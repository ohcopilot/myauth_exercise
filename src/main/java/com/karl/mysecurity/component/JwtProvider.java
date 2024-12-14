package com.karl.mysecurity.component;

import com.karl.mysecurity.entity.MyUserDetail;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtProvider {
    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Value("${jwt.requestHeader}")
    private String requestHeader;

    public String getToken(HttpServletRequest request) {
        String authToken = request.getHeader(requestHeader);
        if(StringUtils.isNotEmpty(authToken) && authToken.startsWith(tokenHead)) {
            // 去掉token前缀(Bearer )，拿到真实token
            authToken = authToken.substring(tokenHead.length()+1);
            return authToken;
        }
        return "";
    }

    private String assembleToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(claims.get("USERNAME").toString())
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public String generateToken(MyUserDetail userDetail){
        Map<String, Object> claims = new HashMap<>();
        claims.put("USERID",userDetail.getId());
        claims.put("USERNAME",userDetail.getUsername());
        claims.put("USERROLES",userDetail.getAuthorities());
        return assembleToken(claims);
    }

    /**
     * 生成token的过期时间
     */
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    private Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public String getLoginAccount(String token) {
        Claims claims = getClaims(token);
        if(claims!=null){
            return claims.getSubject();
        }
        return "";
    }

    public boolean validateToken(String token, MyUserDetail userDetail) {
        Claims claims = getClaims(token);
        return claims.getSubject().equals(userDetail.getUsername()) && !isTokenExpired(claims);
    }

    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    public String refreshToken(String token) {
        Claims claims = getClaims(token);
        claims.setExpiration(generateExpirationDate());
        return assembleToken(claims);
    }


}
