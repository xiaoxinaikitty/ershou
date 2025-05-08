package com.xuchao.ershou.common.enums;

import lombok.Getter;

/**
 * 用户反馈优先级枚举
 */
@Getter
public enum FeedbackPriorityEnum {
    
    NORMAL(0, "普通"),
    IMPORTANT(1, "重要"),
    URGENT(2, "紧急");

    private final Integer code;
    private final String desc;

    FeedbackPriorityEnum(Integer code, String desc) {
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
        for (FeedbackPriorityEnum priorityEnum : FeedbackPriorityEnum.values()) {
            if (priorityEnum.getCode().equals(code)) {
                return priorityEnum.getDesc();
            }
        }
        return null;
    }
} 