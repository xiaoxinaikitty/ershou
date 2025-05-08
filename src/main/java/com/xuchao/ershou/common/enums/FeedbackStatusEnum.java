package com.xuchao.ershou.common.enums;

import lombok.Getter;

/**
 * 用户反馈状态枚举
 */
@Getter
public enum FeedbackStatusEnum {
    
    UNPROCESSED(0, "未处理"),
    PROCESSING(1, "处理中"),
    PROCESSED(2, "已处理");

    private final Integer code;
    private final String desc;

    FeedbackStatusEnum(Integer code, String desc) {
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
        for (FeedbackStatusEnum statusEnum : FeedbackStatusEnum.values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum.getDesc();
            }
        }
        return null;
    }
} 