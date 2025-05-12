package com.xuchao.ershou.service.impl;

import com.xuchao.ershou.mapper.*;
import com.xuchao.ershou.model.vo.analysis.*;
import com.xuchao.ershou.service.DataAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DataAnalysisServiceImpl implements DataAnalysisService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductFavoriteMapper favoriteMapper;

    @Autowired
    private CategoryMapper categoryMapper;
    
    @Autowired
    private DataAnalysisMapper dataAnalysisMapper;

    @Override
    public List<CategoryAnalysisVO> getProductCategoryAnalysis() {
        List<Map<String, Object>> categoryData = dataAnalysisMapper.getCategoryStatistics();
        int total = categoryData.stream().mapToInt(map -> Integer.parseInt(map.get("count").toString())).sum();
        
        List<CategoryAnalysisVO> result = new ArrayList<>();
        for (Map<String, Object> data : categoryData) {
            CategoryAnalysisVO vo = new CategoryAnalysisVO();
            int categoryId = Integer.parseInt(data.get("categoryId").toString());
            vo.setCategoryId(categoryId);
            vo.setCategoryName("分类" + categoryId);
            vo.setCount(Integer.parseInt(data.get("count").toString()));
            vo.setPercentage(total > 0 ? vo.getCount() * 100.0 / total : 0.0);
            result.add(vo);
        }
        
        return result;
    }

    private String getCategoryName(int categoryId) {
        return "分类" + categoryId;
    }

    @Override
    public List<PriceRangeAnalysisVO> getProductPriceRangeAnalysis() {
        try {
            // 定义价格区间
            List<PriceRangeAnalysisVO> priceRanges = new ArrayList<>();
            priceRanges.add(new PriceRangeAnalysisVO("0-100元", 0.0, 100.0, 0, 0.0));
            priceRanges.add(new PriceRangeAnalysisVO("100-500元", 100.0, 500.0, 0, 0.0));
            priceRanges.add(new PriceRangeAnalysisVO("500-1000元", 500.0, 1000.0, 0, 0.0));
            priceRanges.add(new PriceRangeAnalysisVO("1000-2000元", 1000.0, 2000.0, 0, 0.0));
            priceRanges.add(new PriceRangeAnalysisVO("2000-5000元", 2000.0, 5000.0, 0, 0.0));
            priceRanges.add(new PriceRangeAnalysisVO("5000元以上", 5000.0, Double.MAX_VALUE, 0, 0.0));
            
            // 查询各个价格区间的商品数量
            List<Map<String, Object>> priceData = dataAnalysisMapper.getPriceRangeStatistics();
            
            // 处理查询结果
            int total = 0;
            for (Map<String, Object> data : priceData) {
                double price = 0.0;
                int count = 0;
                try {
                    price = Double.parseDouble(data.get("price").toString());
                    count = Integer.parseInt(data.get("count").toString());
                } catch (Exception e) {
                    continue;
                }
                total += count;
                
                for (PriceRangeAnalysisVO range : priceRanges) {
                    if (price >= range.getMinPrice() && price < range.getMaxPrice()) {
                        range.setCount(range.getCount() + count);
                        break;
                    }
                }
            }
            
            // 计算百分比
            for (PriceRangeAnalysisVO range : priceRanges) {
                range.setPercentage(total > 0 ? range.getCount() * 100.0 / total : 0.0);
            }
            
            return priceRanges;
        } catch (Exception e) {
            // 返回空结果而不是null，避免前端报错
            return new ArrayList<>();
        }
    }

    @Override
    public List<ConditionAnalysisVO> getProductConditionAnalysis() {
        try {
            List<Map<String, Object>> conditionData = dataAnalysisMapper.getConditionStatistics();
            int total = conditionData.stream().mapToInt(map -> {
                try {
                    return Integer.parseInt(map.get("count").toString());
                } catch (Exception e) {
                    return 0;
                }
            }).sum();
            
            List<ConditionAnalysisVO> result = new ArrayList<>();
            for (Map<String, Object> data : conditionData) {
                try {
                    ConditionAnalysisVO vo = new ConditionAnalysisVO();
                    int condition = Integer.parseInt(data.get("conditionLevel").toString());
                    vo.setCondition(condition);
                    vo.setConditionDesc(getConditionDesc(condition));
                    vo.setCount(Integer.parseInt(data.get("count").toString()));
                    vo.setPercentage(total > 0 ? vo.getCount() * 100.0 / total : 0.0);
                    result.add(vo);
                } catch (Exception e) {
                    continue;
                }
            }
            
            return result;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private String getConditionDesc(int condition) {
        if (condition >= 9) return "几乎全新";
        if (condition >= 7) return "品相良好";
        if (condition >= 5) return "正常使用痕迹";
        if (condition >= 3) return "明显使用痕迹";
        return "品相一般";
    }

    @Override
    public List<StatusAnalysisVO> getProductStatusAnalysis() {
        try {
            List<Map<String, Object>> statusData = dataAnalysisMapper.getStatusStatistics();
            int total = statusData.stream().mapToInt(map -> {
                try {
                    return Integer.parseInt(map.get("count").toString());
                } catch (Exception e) {
                    return 0;
                }
            }).sum();
            
            List<StatusAnalysisVO> result = new ArrayList<>();
            for (Map<String, Object> data : statusData) {
                try {
                    StatusAnalysisVO vo = new StatusAnalysisVO();
                    int status = Integer.parseInt(data.get("status").toString());
                    vo.setStatus(status);
                    vo.setStatusDesc(getProductStatusDesc(status));
                    vo.setCount(Integer.parseInt(data.get("count").toString()));
                    vo.setPercentage(total > 0 ? vo.getCount() * 100.0 / total : 0.0);
                    result.add(vo);
                } catch (Exception e) {
                    continue;
                }
            }
            
            return result;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private String getProductStatusDesc(int status) {
        switch (status) {
            case 0: return "下架";
            case 1: return "在售";
            case 2: return "已售";
            default: return "未知";
        }
    }

    @Override
    public List<TrendAnalysisVO> getProductTrend(Integer days) {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(days - 1);
            
            List<Map<String, Object>> trendData = dataAnalysisMapper.getProductTrend(startDate, endDate);
            
            return generateTrendData(startDate, endDate, trendData);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<TrendAnalysisVO> getUserRegisterTrend(Integer days) {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(days - 1);
            
            List<Map<String, Object>> trendData = dataAnalysisMapper.getUserRegisterTrend(startDate, endDate);
            
            return generateTrendData(startDate, endDate, trendData);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<TrendAnalysisVO> getOrderTrend(Integer days) {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(days - 1);
            
            List<Map<String, Object>> trendData = dataAnalysisMapper.getOrderTrend(startDate, endDate);
            
            return generateTrendData(startDate, endDate, trendData);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private List<TrendAnalysisVO> generateTrendData(LocalDate startDate, LocalDate endDate, List<Map<String, Object>> trendData) {
        try {
            // 创建日期到数量的映射
            Map<LocalDate, Integer> dateCountMap = new HashMap<>();
            for (Map<String, Object> data : trendData) {
                try {
                    String dateStr = data.get("date").toString();
                    LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
                    int count = Integer.parseInt(data.get("count").toString());
                    dateCountMap.put(date, count);
                } catch (Exception e) {
                    continue;
                }
            }
            
            // 生成连续的日期数据
            List<TrendAnalysisVO> result = new ArrayList<>();
            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                TrendAnalysisVO vo = new TrendAnalysisVO();
                vo.setDate(currentDate);
                vo.setCount(dateCountMap.getOrDefault(currentDate, 0));
                result.add(vo);
                currentDate = currentDate.plusDays(1);
            }
            
            return result;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<AmountTrendAnalysisVO> getOrderAmountTrend(Integer days) {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(days - 1);
            
            List<Map<String, Object>> trendData = dataAnalysisMapper.getOrderAmountTrend(startDate, endDate);
            
            return generateOrderAmountTrendData(startDate, endDate, trendData);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<StatusAnalysisVO> getOrderStatusAnalysis() {
        try {
            List<Map<String, Object>> statusData = dataAnalysisMapper.getOrderStatusStatistics();
            int total = statusData.stream().mapToInt(map -> {
                try {
                    return Integer.parseInt(map.get("count").toString());
                } catch (Exception e) {
                    return 0;
                }
            }).sum();
            
            List<StatusAnalysisVO> result = new ArrayList<>();
            for (Map<String, Object> data : statusData) {
                try {
                    StatusAnalysisVO vo = new StatusAnalysisVO();
                    int status = Integer.parseInt(data.get("status").toString());
                    vo.setStatus(status);
                    vo.setStatusDesc(getOrderStatusDesc(status));
                    vo.setCount(Integer.parseInt(data.get("count").toString()));
                    vo.setPercentage(total > 0 ? vo.getCount() * 100.0 / total : 0.0);
                    result.add(vo);
                } catch (Exception e) {
                    continue;
                }
            }
            
            return result;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private String getOrderStatusDesc(int status) {
        switch (status) {
            case 0: return "已取消";
            case 1: return "待付款";
            case 2: return "待发货";
            case 3: return "待收货";
            case 4: return "已完成";
            default: return "未知";
        }
    }

    @Override
    public List<UserActiveAnalysisVO> getUserActiveAnalysis(Integer days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        
        // 获取活跃用户及其活动数据
        List<Map<String, Object>> activeUserData = dataAnalysisMapper.getActiveUserData(startDate, endDate);
        
        List<UserActiveAnalysisVO> result = new ArrayList<>();
        for (Map<String, Object> data : activeUserData) {
            UserActiveAnalysisVO vo = new UserActiveAnalysisVO();
            vo.setUserId(Integer.parseInt(data.get("userId").toString()));
            vo.setUsername(data.get("username").toString());
            vo.setProductCount(Integer.parseInt(data.get("productCount").toString()));
            vo.setOrderCount(Integer.parseInt(data.get("orderCount").toString()));
            vo.setFavoriteCount(Integer.parseInt(data.get("favoriteCount").toString()));
            
            // 活跃度评分：发布商品数*0.5 + 下单数*0.3 + 收藏数*0.2
            double activityScore = vo.getProductCount() * 0.5 + vo.getOrderCount() * 0.3 + vo.getFavoriteCount() * 0.2;
            vo.setActivityScore(activityScore);
            
            result.add(vo);
        }
        
        // 按活跃度降序排序
        return result.stream()
                .sorted((a, b) -> Double.compare(b.getActivityScore(), a.getActivityScore()))
                .collect(Collectors.toList());
    }

    @Override
    public DataSummaryVO getDataSummary() {
        try {
            DataSummaryVO summary = new DataSummaryVO();
            
            // 获取总用户数
            summary.setTotalUsers(dataAnalysisMapper.getTotalUserCount());
            
            // 获取总商品数
            summary.setTotalProducts(dataAnalysisMapper.getTotalProductCount());
            
            // 获取总订单数和总金额
            Map<String, Object> orderSummary = dataAnalysisMapper.getOrderSummary();
            summary.setTotalOrders(Integer.parseInt(orderSummary.get("totalOrders").toString()));
            summary.setTotalOrderAmount(Double.parseDouble(orderSummary.get("totalAmount").toString()));
            
            // 获取活跃卖家数
            summary.setActiveSellUsers(dataAnalysisMapper.getActiveSellUserCount());
            
            // 获取活跃买家数
            summary.setActiveBuyUsers(dataAnalysisMapper.getActiveBuyUserCount());
            
            // 获取过去30天新增用户数
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(29); // 最近30天
            summary.setNewUsersLast30Days(dataAnalysisMapper.getNewUserCount(startDate, endDate));
            
            // 获取过去30天新增商品数
            summary.setNewProductsLast30Days(dataAnalysisMapper.getNewProductCount(startDate, endDate));
            
            // 获取过去30天新增订单数和金额
            Map<String, Object> recentOrderSummary = dataAnalysisMapper.getRecentOrderSummary(startDate, endDate);
            summary.setNewOrdersLast30Days(Integer.parseInt(recentOrderSummary.get("totalOrders").toString()));
            summary.setNewOrderAmountLast30Days(Double.parseDouble(recentOrderSummary.get("totalAmount").toString()));
            
            return summary;
        } catch (Exception e) {
            // 返回空的数据摘要对象，避免前端报错
            return new DataSummaryVO();
        }
    }

    @Override
    public List<HotProductVO> getHotProducts(Integer limit) {
        // 获取热门商品数据
        List<Map<String, Object>> hotProductData = dataAnalysisMapper.getHotProducts(limit);
        
        List<HotProductVO> result = new ArrayList<>();
        for (Map<String, Object> data : hotProductData) {
            try {
                HotProductVO vo = new HotProductVO();
                vo.setProductId(Integer.parseInt(data.get("productId").toString()));
                vo.setTitle(data.get("title").toString());
                // 设置默认图片路径
                vo.setMainImageUrl("default_product_image.jpg");
                vo.setPrice(Double.parseDouble(data.get("price").toString()));
                vo.setViewCount(Integer.parseInt(data.get("viewCount").toString()));
                vo.setFavoriteCount(Integer.parseInt(data.get("favoriteCount").toString()));
                vo.setSoldCount(Integer.parseInt(data.get("soldCount").toString()));
                
                // 热度评分：浏览量*0.4 + 收藏数*0.3 + 销量*0.3
                double hotScore = vo.getViewCount() * 0.4 + vo.getFavoriteCount() * 0.3 + vo.getSoldCount() * 0.3;
                vo.setHotScore(hotScore);
                
                result.add(vo);
            } catch (Exception e) {
                // 忽略数据异常，继续处理下一条
                continue;
            }
        }
        
        // 按热度降序排序
        return result.stream()
                .sorted((a, b) -> Double.compare(b.getHotScore(), a.getHotScore()))
                .collect(Collectors.toList());
    }
    
    // 获取商品主图
    private String getProductMainImage(int productId) {
        // 返回默认图片路径
        return "default_product_image.jpg";
    }

    @Override
    public CustomAnalysisVO getCustomAnalysis(LocalDate startDate, LocalDate endDate) {
        CustomAnalysisVO result = new CustomAnalysisVO();
        result.setStartDate(startDate);
        result.setEndDate(endDate);
        
        // 获取该日期范围内的用户注册数
        result.setNewUsers(dataAnalysisMapper.getNewUserCount(startDate, endDate));
        
        // 获取该日期范围内的新增商品数
        result.setNewProducts(dataAnalysisMapper.getNewProductCount(startDate, endDate));
        
        // 获取该日期范围内的订单数和金额
        Map<String, Object> orderSummary = dataAnalysisMapper.getRecentOrderSummary(startDate, endDate);
        result.setNewOrders(Integer.parseInt(orderSummary.get("totalOrders").toString()));
        result.setOrderAmount(Double.parseDouble(orderSummary.get("totalAmount").toString()));
        
        // 获取该日期范围内的分类统计
        List<Map<String, Object>> categoryData = dataAnalysisMapper.getCategoryStatisticsByDateRange(startDate, endDate);
        int total = categoryData.stream().mapToInt(map -> Integer.parseInt(map.get("count").toString())).sum();
        
        List<CategoryAnalysisVO> categoryAnalysis = new ArrayList<>();
        for (Map<String, Object> data : categoryData) {
            CategoryAnalysisVO vo = new CategoryAnalysisVO();
            int categoryId = Integer.parseInt(data.get("categoryId").toString());
            vo.setCategoryId(categoryId);
            vo.setCategoryName("分类" + categoryId);
            vo.setCount(Integer.parseInt(data.get("count").toString()));
            vo.setPercentage(total > 0 ? vo.getCount() * 100.0 / total : 0.0);
            categoryAnalysis.add(vo);
        }
        result.setCategoryAnalysis(categoryAnalysis);
        
        // 获取该日期范围内的商品状态统计
        List<Map<String, Object>> productStatusData = dataAnalysisMapper.getStatusStatisticsByDateRange(startDate, endDate);
        total = productStatusData.stream().mapToInt(map -> Integer.parseInt(map.get("count").toString())).sum();
        
        List<StatusAnalysisVO> productStatusAnalysis = new ArrayList<>();
        for (Map<String, Object> data : productStatusData) {
            StatusAnalysisVO vo = new StatusAnalysisVO();
            int status = Integer.parseInt(data.get("status").toString());
            vo.setStatus(status);
            vo.setStatusDesc(getProductStatusDesc(status));
            vo.setCount(Integer.parseInt(data.get("count").toString()));
            vo.setPercentage(total > 0 ? vo.getCount() * 100.0 / total : 0.0);
            productStatusAnalysis.add(vo);
        }
        result.setProductStatusAnalysis(productStatusAnalysis);
        
        // 获取该日期范围内的订单状态统计
        List<Map<String, Object>> orderStatusData = dataAnalysisMapper.getOrderStatusStatisticsByDateRange(startDate, endDate);
        total = orderStatusData.stream().mapToInt(map -> Integer.parseInt(map.get("count").toString())).sum();
        
        List<StatusAnalysisVO> orderStatusAnalysis = new ArrayList<>();
        for (Map<String, Object> data : orderStatusData) {
            StatusAnalysisVO vo = new StatusAnalysisVO();
            int status = Integer.parseInt(data.get("status").toString());
            vo.setStatus(status);
            vo.setStatusDesc(getOrderStatusDesc(status));
            vo.setCount(Integer.parseInt(data.get("count").toString()));
            vo.setPercentage(total > 0 ? vo.getCount() * 100.0 / total : 0.0);
            orderStatusAnalysis.add(vo);
        }
        result.setOrderStatusAnalysis(orderStatusAnalysis);
        
        // 获取该日期范围内的各项趋势数据
        List<Map<String, Object>> userRegisterTrendData = dataAnalysisMapper.getUserRegisterTrend(startDate, endDate);
        result.setUserRegisterTrend(generateTrendData(startDate, endDate, userRegisterTrendData));
        
        List<Map<String, Object>> productTrendData = dataAnalysisMapper.getProductTrend(startDate, endDate);
        result.setProductTrend(generateTrendData(startDate, endDate, productTrendData));
        
        List<Map<String, Object>> orderTrendData = dataAnalysisMapper.getOrderTrend(startDate, endDate);
        result.setOrderTrend(generateTrendData(startDate, endDate, orderTrendData));
        
        List<Map<String, Object>> orderAmountTrendData = dataAnalysisMapper.getOrderAmountTrend(startDate, endDate);
        result.setOrderAmountTrend(generateOrderAmountTrendData(startDate, endDate, orderAmountTrendData));
        
        return result;
    }

    private List<AmountTrendAnalysisVO> generateOrderAmountTrendData(LocalDate startDate, LocalDate endDate, List<Map<String, Object>> trendData) {
        try {
            // 创建日期到数据的映射
            Map<LocalDate, AmountTrendAnalysisVO> dateDataMap = new HashMap<>();
            for (Map<String, Object> data : trendData) {
                try {
                    String dateStr = data.get("date").toString();
                    LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
                    double amount = Double.parseDouble(data.get("amount").toString());
                    int count = Integer.parseInt(data.get("count").toString());
                    
                    AmountTrendAnalysisVO vo = new AmountTrendAnalysisVO();
                    vo.setDate(date);
                    vo.setAmount(amount);
                    vo.setCount(count);
                    dateDataMap.put(date, vo);
                } catch (Exception e) {
                    continue;
                }
            }
            
            // 生成连续的日期数据
            List<AmountTrendAnalysisVO> result = new ArrayList<>();
            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                if (dateDataMap.containsKey(currentDate)) {
                    result.add(dateDataMap.get(currentDate));
                } else {
                    result.add(new AmountTrendAnalysisVO(currentDate, 0.0, 0));
                }
                currentDate = currentDate.plusDays(1);
            }
            
            return result;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
} 