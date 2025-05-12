package com.xuchao.ershou.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 图片类型枚举
 */
@Getter
public enum ImageTypeEnum {
    
    CAROUSEL(1, "轮播图"),
    DISPLAY(2, "展示图");
    
    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;
    
    ImageTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static String getDescByCode(Integer code) {
        if (code == null) {
            return "";
        }
        for (ImageTypeEnum typeEnum : values()) {
            if (typeEnum.getCode().equals(code)) {
                return typeEnum.getDesc();
            }
        }
        return "";
    }
} 