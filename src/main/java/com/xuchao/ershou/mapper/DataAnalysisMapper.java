package com.xuchao.ershou.mapper;

import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 数据分析相关的数据库操作接口
 */
public interface DataAnalysisMapper {
    
    // ProductMapper 分析相关方法
    List<Map<String, Object>> getCategoryStatistics();
    
    List<Map<String, Object>> getPriceRangeStatistics();
    
    List<Map<String, Object>> getConditionStatistics();
    
    List<Map<String, Object>> getStatusStatistics();
    
    List<Map<String, Object>> getProductTrend(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    List<Map<String, Object>> getHotProducts(@Param("limit") Integer limit);
    
    Integer getTotalProductCount();
    
    Integer getNewProductCount(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    List<Map<String, Object>> getCategoryStatisticsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    List<Map<String, Object>> getStatusStatisticsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // UserMapper 分析相关方法
    List<Map<String, Object>> getUserRegisterTrend(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    List<Map<String, Object>> getActiveUserData(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    Integer getTotalUserCount();
    
    Integer getActiveSellUserCount();
    
    Integer getActiveBuyUserCount();
    
    Integer getNewUserCount(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // OrderMapper 分析相关方法
    List<Map<String, Object>> getOrderTrend(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    List<Map<String, Object>> getOrderAmountTrend(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    List<Map<String, Object>> getOrderStatusStatistics();
    
    Map<String, Object> getOrderSummary();
    
    Map<String, Object> getRecentOrderSummary(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    List<Map<String, Object>> getOrderStatusStatisticsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
} 