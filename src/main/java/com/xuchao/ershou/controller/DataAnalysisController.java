package com.xuchao.ershou.controller;

import com.xuchao.ershou.common.Result;
import com.xuchao.ershou.model.vo.analysis.*;
import com.xuchao.ershou.service.DataAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/data/analysis")
public class DataAnalysisController {

    @Autowired
    private DataAnalysisService dataAnalysisService;

    @GetMapping("/product/category")
    public Result<List<CategoryAnalysisVO>> getProductCategoryAnalysis() {
        return Result.success(dataAnalysisService.getProductCategoryAnalysis());
    }

    @GetMapping("/product/price/range")
    public Result<List<PriceRangeAnalysisVO>> getProductPriceRangeAnalysis() {
        return Result.success(dataAnalysisService.getProductPriceRangeAnalysis());
    }

    @GetMapping("/product/condition")
    public Result<List<ConditionAnalysisVO>> getProductConditionAnalysis() {
        return Result.success(dataAnalysisService.getProductConditionAnalysis());
    }

    @GetMapping("/product/status")
    public Result<List<StatusAnalysisVO>> getProductStatusAnalysis() {
        return Result.success(dataAnalysisService.getProductStatusAnalysis());
    }

    @GetMapping("/product/trend")
    public Result<List<TrendAnalysisVO>> getProductTrend(
            @RequestParam(required = false, defaultValue = "30") Integer days) {
        return Result.success(dataAnalysisService.getProductTrend(days));
    }

    @GetMapping("/user/register/trend")
    public Result<List<TrendAnalysisVO>> getUserRegisterTrend(
            @RequestParam(required = false, defaultValue = "30") Integer days) {
        return Result.success(dataAnalysisService.getUserRegisterTrend(days));
    }

    @GetMapping("/order/trend")
    public Result<List<TrendAnalysisVO>> getOrderTrend(
            @RequestParam(required = false, defaultValue = "30") Integer days) {
        return Result.success(dataAnalysisService.getOrderTrend(days));
    }

    @GetMapping("/order/amount/trend")
    public Result<List<AmountTrendAnalysisVO>> getOrderAmountTrend(
            @RequestParam(required = false, defaultValue = "30") Integer days) {
        return Result.success(dataAnalysisService.getOrderAmountTrend(days));
    }

    @GetMapping("/order/status")
    public Result<List<StatusAnalysisVO>> getOrderStatusAnalysis() {
        return Result.success(dataAnalysisService.getOrderStatusAnalysis());
    }

    @GetMapping("/user/active")
    public Result<List<UserActiveAnalysisVO>> getUserActiveAnalysis(
            @RequestParam(required = false, defaultValue = "30") Integer days) {
        return Result.success(dataAnalysisService.getUserActiveAnalysis(days));
    }

    @GetMapping("/summary")
    public Result<DataSummaryVO> getDataSummary() {
        return Result.success(dataAnalysisService.getDataSummary());
    }
    
    @GetMapping("/product/hot")
    public Result<List<HotProductVO>> getHotProducts(
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        return Result.success(dataAnalysisService.getHotProducts(limit));
    }
    
    @GetMapping("/custom")
    public Result<CustomAnalysisVO> getCustomAnalysis(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(dataAnalysisService.getCustomAnalysis(startDate, endDate));
    }
} 