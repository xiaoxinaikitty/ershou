package com.xuchao.ershou.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 营销活动类型枚举
 */
@Getter
public enum PromotionTypeEnum {
    
    PROMOTION(1, "促销活动"),
    DISCOUNT(2, "折扣"),
    FULL_REDUCTION(3, "满减"),
    COUPON(4, "优惠券");
    
    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;
    
    PromotionTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static String getDescByCode(Integer code) {
        if (code == null) {
            return "";
        }
        for (PromotionTypeEnum typeEnum : values()) {
            if (typeEnum.getCode().equals(code)) {
                return typeEnum.getDesc();
            }
        }
        return "";
    }
} 