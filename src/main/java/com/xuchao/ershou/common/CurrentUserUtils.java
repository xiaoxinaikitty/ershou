package com.xuchao.ershou.common;

import com.xuchao.ershou.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 当前登录用户工具类
 */
@Component
public class CurrentUserUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        CurrentUserUtils.applicationContext = applicationContext;
    }

    private static UserService getUserService() {
        if (applicationContext == null) {
            throw new IllegalStateException("ApplicationContext is not initialized");
        }
        return applicationContext.getBean(UserService.class);
    }

    /**
     * 获取当前登录用户的ID
     * @return 用户ID
     */
    public static Long getCurrentUserId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return (Long) request.getAttribute("userId");
    }

    /**
     * 获取当前登录用户的用户名
     * @return 用户名
     */
    public static String getCurrentUsername() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return (String) request.getAttribute("username");
    }

    /**
     * 判断当前用户是否为管理员
     * @param userId 用户ID
     * @return 如果是管理员返回 true，否则返回 false
     */
    public static boolean isAdmin(Long userId) {
        String userRole = getUserService().getUserRoleById(userId);
        // 确保角色值与数据库中存储的值一致
        return "系统管理员".equals(userRole);
    }
}