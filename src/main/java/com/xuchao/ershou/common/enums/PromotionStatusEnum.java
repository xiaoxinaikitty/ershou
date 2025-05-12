package com.xuchao.ershou.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 营销活动状态枚举
 */
@Getter
public enum PromotionStatusEnum {
    
    OFFLINE(0, "下线"),
    ONLINE(1, "上线");
    
    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;
    
    PromotionStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static String getDescByCode(Integer code) {
        if (code == null) {
            return "";
        }
        for (PromotionStatusEnum statusEnum : values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum.getDesc();
            }
        }
        return "";
    }
    
    /**
     * 判断状态码是否有效
     * @param code 状态码
     * @return 是否有效
     */
    public static boolean isValidStatus(Integer code) {
        if (code == null) {
            return false;
        }
        for (PromotionStatusEnum statusEnum : values()) {
            if (statusEnum.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }
} 