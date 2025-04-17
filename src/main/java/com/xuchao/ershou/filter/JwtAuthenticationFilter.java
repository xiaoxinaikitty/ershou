package com.xuchao.ershou.filter; // 换成你自己的

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xuchao.ershou.common.BaseResponse;
import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.common.JwtUtil;
import com.xuchao.ershou.common.ResultUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // 不需要token的白名单路径
    private final List<String> whiteList = Arrays.asList(
            "/user/login",
            "/user/register",
            "/user/admin",
            "/error",
            "/images/**",  // 图片资源路径
            "/product/image/upload",  // 添加商品图片上传接口
            "/product/image/add-by-url",  // 通过URL添加商品图片接口
            "/file/upload",  // 通用文件上传接口
            "/files/**"  // 文件访问路径
    );

    public JwtAuthenticationFilter(JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();

        // 1. 白名单路径放行
        if (isWhiteListPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. 获取请求头中的token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            handleUnauthorized(response);
            return;
        }

        // 3. 提取token并验证
        String token = authHeader.substring(7);
        try {
            if (!jwtUtil.validateToken(token)) {
                handleUnauthorized(response);
                return;
            }
            
            // 4. 从token中提取用户信息并设置到请求属性中
            Long userId = jwtUtil.getUserIdFromToken(token);
            String username = jwtUtil.getUsernameFromToken(token);
            
            request.setAttribute("userId", userId);
            request.setAttribute("username", username);
            
            // 5. 验证通过，放行
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            handleUnauthorized(response);
        }
    }

    private boolean isWhiteListPath(String path) {
        return whiteList.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private void handleUnauthorized(HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        BaseResponse<Object> baseResponse = ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR);
        response.getWriter().write(objectMapper.writeValueAsString(baseResponse));
    }
}