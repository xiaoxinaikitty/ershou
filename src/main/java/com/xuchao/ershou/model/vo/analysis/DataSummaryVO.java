package com.xuchao.ershou.model.vo.analysis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据摘要VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataSummaryVO {
    private Integer totalUsers;
    private Integer totalProducts;
    private Integer totalOrders;
    private Double totalOrderAmount;
    private Integer activeSellUsers;
    private Integer activeBuyUsers;
    private Integer newUsersLast30Days;
    private Integer newProductsLast30Days;
    private Integer newOrdersLast30Days;
    private Double newOrderAmountLast30Days;
} 