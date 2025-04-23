package com.xuchao.ershou.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xuchao.ershou.common.BaseResponse;
import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.common.ResultUtils;
import com.xuchao.ershou.model.dto.CreateTransactionDTO;
import com.xuchao.ershou.model.dto.TransactionQueryDTO;
import com.xuchao.ershou.model.entity.WalletTransaction;
import com.xuchao.ershou.model.vo.PageResult;
import com.xuchao.ershou.service.WalletTransactionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 钱包交易记录接口
 */
@RestController
@RequestMapping("/api/wallet/transaction")
public class WalletTransactionController {

    @Resource
    private WalletTransactionService walletTransactionService;

    /**
     * 创建钱包交易记录
     *
     * @param createTransactionDTO 创建交易记录参数
     * @return 交易记录ID
     */
    @PostMapping("/create")
    public BaseResponse<Long> createTransaction(@RequestBody CreateTransactionDTO createTransactionDTO) {
        // 参数校验
        if (createTransactionDTO == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        if (createTransactionDTO.getAccountId() == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(), "账户ID不能为空");
        }
        if (createTransactionDTO.getTransactionType() == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(), "交易类型不能为空");
        }
        if (createTransactionDTO.getTransactionAmount() == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(), "交易金额不能为空");
        }
        if (createTransactionDTO.getBeforeBalance() == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(), "交易前余额不能为空");
        }
        if (createTransactionDTO.getAfterBalance() == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(), "交易后余额不能为空");
        }
        
        try {
            // 调用服务创建交易记录
            WalletTransaction transaction = walletTransactionService.createTransaction(createTransactionDTO);
            return ResultUtils.success(transaction.getTransactionId());
        } catch (Exception e) {
            // 异常处理
            return ResultUtils.error(ErrorCode.OPERATION_ERROR.getCode(), e.getMessage());
        }
    }
    
    /**
     * 查询钱包交易记录列表
     *
     * @param accountId 钱包账户ID
     * @param startTime 查询起始时间
     * @param endTime 查询结束时间
     * @param transactionType 交易类型
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @return 交易记录列表
     */
    @GetMapping("/list")
    public BaseResponse<PageResult<WalletTransaction>> getTransactionList(
            @RequestParam("accountId") Long accountId,
            @RequestParam(value = "startTime", required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(value = "endTime", required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(value = "transactionType", required = false) Integer transactionType,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        
        // 参数校验
        if (accountId == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR.getCode(), "账户ID不能为空");
        }
        
        // 校验页码和每页记录数
        if (pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize <= 0 || pageSize > 100) {
            pageSize = 10;
        }
        
        try {
            // 构建查询参数
            TransactionQueryDTO queryDTO = new TransactionQueryDTO();
            queryDTO.setAccountId(accountId);
            queryDTO.setStartTime(startTime);
            queryDTO.setEndTime(endTime);
            queryDTO.setTransactionType(transactionType);
            queryDTO.setPageNum(pageNum);
            queryDTO.setPageSize(pageSize);
            
            // 调用服务查询交易记录
            IPage<WalletTransaction> page = walletTransactionService.queryTransactionList(queryDTO);
            
            // 构建分页结果
            PageResult<WalletTransaction> pageResult = new PageResult<>(
                    page.getCurrent(),
                    page.getSize(),
                    page.getTotal(),
                    page.getRecords()
            );
            
            return ResultUtils.success(pageResult);
        } catch (Exception e) {
            // 异常处理
            return ResultUtils.error(ErrorCode.OPERATION_ERROR.getCode(), e.getMessage());
        }
    }
} 