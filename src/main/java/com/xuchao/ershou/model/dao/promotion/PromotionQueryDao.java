package com.xuchao.ershou.model.dao.promotion;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 查询营销活动的请求DAO
 */
@Data
public class PromotionQueryDao {

    /**
     * 活动标题(模糊查询)
     */
    private String title;

    /**
     * 活动类型(1促销活动 2折扣 3满减 4优惠券)
     */
    private Integer promotionType;

    /**
     * 状态(0下线 1上线)
     */
    private Integer status;

    /**
     * 开始时间范围(起)
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTimeBegin;

    /**
     * 开始时间范围(止)
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTimeEnd;

    /**
     * 结束时间范围(起)
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTimeBegin;

    /**
     * 结束时间范围(止)
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTimeEnd;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;
} 