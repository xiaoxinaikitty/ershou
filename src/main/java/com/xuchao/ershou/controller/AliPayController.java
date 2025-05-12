package com.xuchao.ershou.controller;

import com.xuchao.ershou.common.Result;
import com.xuchao.ershou.mapper.OrderMapper;
import com.xuchao.ershou.model.dto.AliPayDTO;
import com.xuchao.ershou.model.entity.Order;
import com.xuchao.ershou.model.entity.Payment;
import com.xuchao.ershou.service.PaymentService;
import com.xuchao.ershou.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 支付宝支付控制器
 */
@Slf4j
@RestController
@RequestMapping("/alipay")
public class AliPayController {

    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    /**
     * 创建支付订单
     * @param orderId 订单ID
     * @return 支付表单HTML
     */
    @GetMapping("/pay")
    @ResponseBody
    public String pay(@RequestParam("orderId") Long orderId, HttpServletRequest request, HttpServletResponse response) {
        // 设置响应类型为HTML
        response.setContentType("text/html;charset=UTF-8");
        
        // 获取当前用户ID，如果获取不到则默认为游客支付
        Integer userId;
        try {
            userId = jwtUtils.getUserId(request);
            log.info("用户[{}]请求支付订单[{}]", userId, orderId);
        } catch (Exception e) {
            log.warn("无法获取用户ID，将作为游客支付: {}", e.getMessage());
            userId = 0; // 默认游客ID
        }
        
        try {
            // 根据订单ID查询订单信息
            Order order = orderMapper.selectOrderById(orderId);
            if (order == null) {
                log.error("订单[{}]不存在", orderId);
                return "<html><body><h1>订单不存在</h1><p>请返回APP重新创建订单</p></body></html>";
            }
            
            // 检查订单金额，如果为0或者负数，直接返回错误信息
            if (order.getPaymentAmount() == null || order.getPaymentAmount().compareTo(BigDecimal.ZERO) <= 0) {
                log.error("订单[{}]金额异常: {}", orderId, order.getPaymentAmount());
                return "<html><body><h1>订单金额异常</h1><p>订单金额必须大于0</p><p>请返回APP重新创建订单</p></body></html>";
            }
            
            // 生成商户订单号(使用UUID去掉-号)
            String traceNo = UUID.randomUUID().toString().replace("-", "");
            
            // 创建支付记录
            Payment payment = new Payment();
            payment.setUserId(userId);
            payment.setName("闲转商品购买 - " + order.getOrderNo());
            payment.setNo(traceNo);
            payment.setTotalPrice(order.getPaymentAmount());
            payment.setState("未收款");
            payment.setCreateTime(new Date());
            
            // 保存支付记录
            paymentService.create(payment);
            
            // 创建支付参数
            AliPayDTO aliPayDTO = new AliPayDTO();
            aliPayDTO.setTraceNo(traceNo);
            // 确保金额格式正确，保留两位小数
            aliPayDTO.setTotalAmount(order.getPaymentAmount().setScale(2, RoundingMode.HALF_UP).toString());
            // 设置商品名称，确保长度不超过支付宝限制
            String subject = "闲转商品购买 - " + order.getOrderNo();
            if (subject.length() > 128) {
                subject = subject.substring(0, 128);
            }
            aliPayDTO.setSubject(subject);
            // 可选：设置商品描述
            aliPayDTO.setBody("二手商品交易");
            
            // 调用支付接口，获取支付表单HTML
            String payHtml = paymentService.getPayUrl(aliPayDTO);
            log.info("生成订单[{}]的支付表单成功", orderId);
            return payHtml;
        } catch (Exception e) {
            log.error("支付请求异常", e);
            return "<html><body><h1>支付请求失败</h1><p>" + e.getMessage() + "</p><p>请返回APP重试</p></body></html>";
        }
    }
    
    /**
     * 支付宝异步通知接口
     * @param request 请求
     * @return 处理结果
     */
    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {
        // 获取支付宝回调参数
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (String name : parameterMap.keySet()) {
            String[] values = parameterMap.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        
        log.info("支付宝异步通知参数：{}", params);
        
        // 处理支付宝回调
        return paymentService.handleNotify(params);
    }
    
    /**
     * 支付宝同步回调接口
     * @param request 请求
     * @return 处理结果
     */
    @GetMapping("/return")
    @ResponseBody
    public String returnPage(HttpServletRequest request, HttpServletResponse response) {
        // 设置响应类型为HTML
        response.setContentType("text/html;charset=UTF-8");
        
        // 设置跨域响应头，避免前端WebView跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        
        // 获取支付宝回调参数
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (String name : parameterMap.keySet()) {
            String[] values = parameterMap.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        
        log.info("支付宝同步回调参数：{}", params);
        
        // 添加JavaScript脚本，支持WebView自动关闭和跳转
        String redirectScript = "<script type=\"text/javascript\">\n" +
                "// 尝试通知APP支付成功\n" +
                "try {\n" +
                "    // 延迟3秒后自动关闭页面\n" +
                "    setTimeout(function() {\n" +
                "        window.location.href = 'ershou://payment/success';\n" +
                "    }, 3000);\n" +
                "} catch(e) {\n" +
                "    console.log('通知APP失败: ' + e.message);\n" +
                "}\n" +
                "</script>";
        
        // 返回HTML页面
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>支付结果</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            display: flex;\n" +
                "            flex-direction: column;\n" +
                "            align-items: center;\n" +
                "            justify-content: center;\n" +
                "            height: 100vh;\n" +
                "            margin: 0;\n" +
                "            background-color: #f5f5f5;\n" +
                "        }\n" +
                "        .success-icon {\n" +
                "            color: #4CAF50;\n" +
                "            font-size: 60px;\n" +
                "            margin-bottom: 20px;\n" +
                "        }\n" +
                "        .message {\n" +
                "            font-size: 24px;\n" +
                "            margin-bottom: 30px;\n" +
                "        }\n" +
                "    </style>\n" +
                redirectScript +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"success-icon\">✓</div>\n" +
                "    <div class=\"message\">支付成功</div>\n" +
                "    <p>订单已支付完成，请返回APP查看订单详情</p>\n" +
                "    <button onclick=\"window.location.href='ershou://payment/success'\" style=\"padding: 10px 20px; background-color: #4CAF50; color: white; border: none; border-radius: 4px; cursor: pointer; margin-top: 20px;\">\n" +
                "        返回APP\n" +
                "    </button>\n" +
                "</body>\n" +
                "</html>";
    }
} 