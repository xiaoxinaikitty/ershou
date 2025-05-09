package com.xuchao.ershou.utils;

import com.xuchao.ershou.model.entity.User;

/**
 * 用户信息ThreadLocal工具类
 * 用于在线程上下文中存储和获取当前登录用户的信息
 */
public class UserHolder {
    private static final ThreadLocal<User> userThreadLocal = new ThreadLocal<>();

    /**
     * 保存用户信息到线程上下文
     * @param user 用户信息
     */
    public static void saveUser(User user) {
        userThreadLocal.set(user);
    }

    /**
     * 获取当前登录用户信息
     * @return 用户信息
     */
    public static User getUser() {
        return userThreadLocal.get();
    }

    /**
     * 清除当前线程中的用户信息
     */
    public static void removeUser() {
        userThreadLocal.remove();
    }
} 