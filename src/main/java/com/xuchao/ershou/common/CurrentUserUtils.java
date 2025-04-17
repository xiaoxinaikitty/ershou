package com.xuchao.ershou.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 当前登录用户工具类
 */
public class CurrentUserUtils {

    /**
     * 获取当前登录用户的ID
     * @return 用户ID，如果未登录则返回null
     */
    public static Long getCurrentUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        return (Long) request.getAttribute("userId");
    }

    /**
     * 获取当前登录用户的用户名
     * @return 用户名，如果未登录则返回null
     */
    public static String getCurrentUsername() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        return (String) request.getAttribute("username");
    }
}