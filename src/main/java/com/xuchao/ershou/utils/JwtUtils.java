package com.xuchao.ershou.utils;

import com.xuchao.ershou.common.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Slf4j
@Component
public class JwtUtils {

    @Value("${jwt.secret:your_default_secret_key_at_least_32_bytes_long}")
    private String secret;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    // JWT密钥
    private static Key KEY;
    
    @PostConstruct
    public void init() {
        log.info("初始化JWT密钥，使用配置的secret");
        byte[] keyBytes = secret.getBytes();
        KEY = Keys.hmacShaKeyFor(keyBytes);
    }
    
    // Token过期时间（24小时）
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;
    
    // Token前缀
    private static final String TOKEN_PREFIX = "Bearer ";
    
    // HTTP头部Authorization字段名
    private static final String HEADER_STRING = "Authorization";

    /**
     * 生成JWT令牌
     * @param userId 用户ID
     * @param username 用户名
     * @return JWT令牌
     */
    public static String generateToken(Integer userId, String username) {
        if (KEY == null) {
            log.error("JWT密钥未初始化，无法生成Token");
            throw new IllegalStateException("JWT密钥未初始化");
        }
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(KEY)
                .compact();
    }
    
    /**
     * 解析JWT令牌
     * @param token JWT令牌
     * @return 载荷
     */
    public static Claims parseToken(String token) {
        if (KEY == null) {
            log.error("JWT密钥未初始化，无法解析Token");
            return null;
        }
        
        if (StringUtils.hasText(token) && token.startsWith(TOKEN_PREFIX)) {
            token = token.replace(TOKEN_PREFIX, "");
        }
        
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("JWT解析失败: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 从请求中获取JWT令牌
     * @param request HTTP请求
     * @return JWT令牌
     */
    public static String getTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        if (StringUtils.hasText(token) && token.startsWith(TOKEN_PREFIX)) {
            return token;
        }
        return null;
    }
    
    /**
     * 从请求中获取用户ID
     * @param request HTTP请求
     * @return 用户ID
     */
    public Integer getUserId(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (!StringUtils.hasText(token)) {
            return null;
        }
        
        try {
            // 先尝试使用JwtUtils解析
            Claims claims = parseToken(token);
            
            if (claims != null) {
                Object userId = claims.get("userId");
                if (userId != null) {
                    if (userId instanceof Integer) {
                        return (Integer) userId;
                    } else if (userId instanceof Long) {
                        return ((Long) userId).intValue();
                    } else {
                        return Integer.valueOf(userId.toString());
                    }
                }
                return null;
            }
            
            // 如果JwtUtils解析失败，尝试使用JwtUtil解析
            if (jwtUtil != null) {
                try {
                    Long userId = jwtUtil.getUserIdFromToken(token.replace(TOKEN_PREFIX, ""));
                    return userId != null ? userId.intValue() : null;
                } catch (Exception e) {
                    log.warn("使用JwtUtil解析用户ID失败: {}", e.getMessage());
                    return null;
                }
            }
            
            return null;
        } catch (Exception e) {
            log.error("从请求中获取用户ID失败", e);
            return null;
        }
    }
    
    /**
     * 从请求中获取用户名
     * @param request HTTP请求
     * @return 用户名
     */
    public String getUsername(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (!StringUtils.hasText(token)) {
            return null;
        }
        
        try {
            // 先尝试使用JwtUtils解析
            Claims claims = parseToken(token);
            if (claims != null) {
                return claims.get("username", String.class);
            }
            
            // 如果JwtUtils解析失败，尝试使用JwtUtil解析
            if (jwtUtil != null) {
                try {
                    return jwtUtil.getUsernameFromToken(token.replace(TOKEN_PREFIX, ""));
                } catch (Exception e) {
                    log.warn("使用JwtUtil解析用户名失败: {}", e.getMessage());
                    return null;
                }
            }
            
            return null;
        } catch (Exception e) {
            log.error("从请求中获取用户名失败", e);
            return null;
        }
    }
    
    /**
     * 验证JWT令牌是否有效
     * @param token JWT令牌
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        if (KEY == null) {
            log.error("JWT密钥未初始化，无法验证Token");
            return false;
        }
        
        if (StringUtils.hasText(token) && token.startsWith(TOKEN_PREFIX)) {
            token = token.replace(TOKEN_PREFIX, "");
        }
        
        try {
            log.info("JwtUtils验证token: {}", token);
            Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.warn("JwtUtils验证失败，尝试使用JwtUtil验证");
            
            // 如果JwtUtils验证失败，尝试使用JwtUtil验证
            if (jwtUtil != null) {
                try {
                    return jwtUtil.validateToken(token);
                } catch (Exception ex) {
                    log.error("JwtUtil验证也失败: {}", ex.getMessage());
                    return false;
                }
            }
            
            log.error("JwtUtils验证失败: {}", e.getMessage(), e);
            return false;
        }
    }
} 