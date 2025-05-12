package com.xuchao.ershou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuchao.ershou.model.entity.Order;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.time.LocalDate;
import java.util.Map;

/**
 * 订单Mapper接口
 */
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 插入订单
     * @param order 订单信息
     * @return 影响的行数
     */
    int insertOrder(Order order);
    
    /**
     * 根据订单ID查询订单详情
     * @param orderId 订单ID
     * @return 订单信息
     */
    Order selectOrderById(@Param("orderId") Long orderId);

    /**
     * 根据用户ID查询订单列表
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> selectOrdersByUserId(@Param("userId") Long userId);
    
    /**
     * 更新订单信息
     * @param order 订单更新信息
     * @return 影响的行数
     */
    int updateOrder(Order order);

    /**
     * 根据卖家ID查询订单列表
     * @param sellerId 卖家ID
     * @return 订单列表
     */
    List<Order> selectOrdersBySellerId(@Param("sellerId") Long sellerId);
    
    /**
     * 根据用户ID和订单状态统计订单数量
     * @param userId 用户ID
     * @param status 订单状态（可为null，表示查询所有状态）
     * @return 订单数量
     */
    int countOrdersByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 获取订单趋势
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 订单趋势数据
     */
    List<Map<String, Object>> getOrderTrend(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 获取订单金额趋势
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 订单金额趋势数据
     */
    List<Map<String, Object>> getOrderAmountTrend(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * 获取订单状态统计
     * @return 订单状态统计数据
     */
    List<Map<String, Object>> getOrderStatusStatistics();
    
    /**
     * 获取订单汇总信息
     * @return 订单汇总信息
     */
    Map<String, Object> getOrderSummary();
    
    /**
     * 获取指定日期范围内的订单汇总信息
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 订单汇总信息
     */
    Map<String, Object> getRecentOrderSummary(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * 获取指定日期范围内的订单状态统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 订单状态统计数据
     */
    List<Map<String, Object>> getOrderStatusStatisticsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}