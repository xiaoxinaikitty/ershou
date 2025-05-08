package com.xuchao.ershou.common.enums;

import lombok.Getter;

/**
 * 用户反馈类型枚举
 */
@Getter
public enum FeedbackTypeEnum {
    
    FEATURE_SUGGESTION(1, "功能建议"),
    EXPERIENCE_ISSUE(2, "体验问题"),
    PRODUCT_RELATED(3, "商品相关"),
    LOGISTICS_RELATED(4, "物流相关"),
    OTHER(5, "其他");

    private final Integer code;
    private final String desc;

    FeedbackTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取描述
     */
    public static String getDescByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (FeedbackTypeEnum typeEnum : FeedbackTypeEnum.values()) {
            if (typeEnum.getCode().equals(code)) {
                return typeEnum.getDesc();
            }
        }
        return null;
    }
} 