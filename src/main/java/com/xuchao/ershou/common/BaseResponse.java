package com.xuchao.ershou.common;

import lombok.Data;

@Data
public class BaseResponse<T> {
    private int code;
    private T data;
    private String message;
}