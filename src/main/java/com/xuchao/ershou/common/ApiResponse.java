package com.xuchao.ershou.common;

import lombok.Data;

/**
 * API通用响应封装
 */
@Data
public class ApiResponse<T> {
    /**
     * 状态码
     */
    private Integer status;
    
    /**
     * 提示信息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus(200);
        response.setMessage("success");
        response.setData(data);
        return response;
    }
    
    /**
     * 成功响应（带自定义消息）
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus(200);
        response.setMessage(message);
        response.setData(data);
        return response;
    }
    
    /**
     * 失败响应
     */
    public static <T> ApiResponse<T> error(Integer status, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus(status);
        response.setMessage(message);
        response.setData(null);
        return response;
    }
} 