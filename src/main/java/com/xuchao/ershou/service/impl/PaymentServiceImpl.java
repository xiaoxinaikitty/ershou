package com.xuchao.ershou.service.impl;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuchao.ershou.mapper.PaymentMapper;
import com.xuchao.ershou.model.dto.AliPayDTO;
import com.xuchao.ershou.model.entity.Payment;
import com.xuchao.ershou.service.PaymentService;
import com.xuchao.ershou.config.AliPayConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.xuchao.ershou.model.vo.OrderVO;
import com.xuchao.ershou.service.OrderService;

import java.util.List;
import java.util.Map;

/**
 * 支付服务实现类
 */
@Slf4j
@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements PaymentService {

    @Autowired
    private AliPayConfig aliPayConfig;
    
    @Autowired
    private OrderService orderService;

    @Override
    public boolean create(Payment payment) {
        return save(payment);
    }

    @Override
    public String pay(AliPayDTO aliPayDTO) {
        AlipayTradePagePayResponse response;
        try {
            // 发起支付请求
            response = Factory.Payment.Page()
                    .pay(aliPayDTO.getSubject(), aliPayDTO.getTraceNo(), aliPayDTO.getTotalAmount(), "");
            log.info("支付宝支付请求成功: {}", response.getBody());
            return response.getBody();
        } catch (Exception e) {
            log.error("支付宝支付请求异常", e);
            throw new RuntimeException("支付请求失败", e);
        }
    }

    @Override
    public String getPayUrl(AliPayDTO aliPayDTO) {
        try {
            // 打印私钥信息用于排查问题
            log.info("当前私钥信息: {}", aliPayConfig.getPrivateKey() == null ? "null" : "长度为" + aliPayConfig.getPrivateKey().length());
            
            // 检查Factory.getOptions是否已经初始化 - 修改这部分代码
            try {
                // Factory.getOptions()方法不存在，直接进行重新初始化配置
                log.info("重新初始化支付宝SDK配置...");
                // 重新初始化SDK配置
                Config config = new Config();
                config.protocol = "https";
                config.gatewayHost = aliPayConfig.getGateway();
                config.signType = "RSA2";
                config.appId = aliPayConfig.getAppId();
                config.merchantPrivateKey = aliPayConfig.getAppPrivateKey();
                config.alipayPublicKey = aliPayConfig.getAlipayPublicKey();
                config.notifyUrl = aliPayConfig.getNotifyUrl();
                Factory.setOptions(config);
                log.info("支付宝SDK配置重新初始化完成");
                log.info("网关地址: {}", config.gatewayHost);
            } catch (Exception e) {
                log.error("重新初始化支付宝SDK配置失败", e);
            }
            
            // 使用支付宝SDK创建支付链接并设置同步返回URL
            AlipayTradePagePayResponse response = Factory.Payment.Page()
                    .optional("product_code", "FAST_INSTANT_TRADE_PAY")
                    .optional("timeout_express", "15m")
                    .optional("goods_type", "0")
                    .optional("quit_url", aliPayConfig.getReturnUrl())
                    .pay(aliPayDTO.getSubject(), aliPayDTO.getTraceNo(), aliPayDTO.getTotalAmount(), aliPayConfig.getReturnUrl());
            
            // 返回支付HTML表单
            String payHtml = response.getBody();
            
            // 检查和修正HTML中可能出现的URL格式错误（双https问题）
            payHtml = payHtml.replace("https://https//", "https://");
            payHtml = payHtml.replace("https//", "https://");
            
            // 修正沙箱环境中的alipays协议链接，防止WebView尝试打开它们
            if (payHtml.contains("alipays://")) {
                log.info("检测到alipays协议链接，进行修正");
                // 移除iframe中的alipays协议链接
                payHtml = payHtml.replaceAll("<iframe[^>]*src=\"alipays://[^\"]*\"[^>]*>", "<iframe style=\"display:none;\" src=\"about:blank\">");
                // 移除a标签中的alipays协议链接
                payHtml = payHtml.replaceAll("href=\"alipays://[^\"]*\"", "href=\"javascript:void(0);\"");
            }
            
            // 禁用可能触发支付宝调用的JavaScripts
            payHtml = payHtml.replaceAll("alipays://", "about:blank");
            
            // 添加特殊脚本，阻止页面动态生成alipays链接
            String blockAlipaysScript = "<script type=\"text/javascript\">\n" +
                "// 以更兼容的方式拦截alipays协议\n" +
                "if(!window._patchApplied) {\n" +
                "  try {\n" +
                "    // 使用事件监听器拦截页面跳转\n" +
                "    window.addEventListener('beforeunload', function(e) {\n" +
                "      var currentUrl = window.location.href;\n" +
                "      // 如果是支付宝应用链接，阻止跳转\n" +
                "      if(currentUrl && currentUrl.indexOf('alipays://') === 0) {\n" +
                "        console.log('拦截alipays跳转: ' + currentUrl);\n" +
                "        e.preventDefault();\n" +
                "        e.returnValue = '';\n" +
                "        return false;\n" +
                "      }\n" +
                "    });\n" +
                "    \n" +
                "    // 安全地尝试拦截方法，不强制修改不可重定义的属性\n" +
                "    try {\n" +
                "      var _originalAssign = window.location.assign;\n" +
                "      window.location.assign = function(url) {\n" +
                "        if(url && url.indexOf('alipays://') === 0) {\n" +
                "          console.log('拦截alipays assign: ' + url);\n" +
                "          return;\n" +
                "        }\n" +
                "        return _originalAssign.apply(this, arguments);\n" +
                "      };\n" +
                "    } catch(e) {\n" +
                "      console.log('无法重定义location.assign方法');\n" +
                "    }\n" +
                "    \n" +
                "    try {\n" +
                "      var _originalReplace = window.location.replace;\n" +
                "      window.location.replace = function(url) {\n" +
                "        if(url && url.indexOf('alipays://') === 0) {\n" +
                "          console.log('拦截alipays replace: ' + url);\n" +
                "          return;\n" +
                "        }\n" +
                "        return _originalReplace.apply(this, arguments);\n" +
                "      };\n" +
                "    } catch(e) {\n" +
                "      console.log('无法重定义location.replace方法');\n" +
                "    }\n" +
                "    \n" +
                "    // 定期检查和清理页面上的alipays链接\n" +
                "    setInterval(function() {\n" +
                "      // 移除所有alipays链接\n" +
                "      var alipaysLinks = document.querySelectorAll('a[href^=\"alipays://\"]');\n" +
                "      for(var i = 0; i < alipaysLinks.length; i++) {\n" +
                "        alipaysLinks[i].href = \"javascript:void(0);\";\n" +
                "        alipaysLinks[i].setAttribute('disabled', 'disabled');\n" +
                "        alipaysLinks[i].onclick = function(e) { e.preventDefault(); return false; };\n" +
                "      }\n" +
                "      \n" +
                "      // 移除所有alipays iframe\n" +
                "      var alipaysIframes = document.querySelectorAll('iframe[src^=\"alipays://\"]');\n" +
                "      for(var i = 0; i < alipaysIframes.length; i++) {\n" +
                "        alipaysIframes[i].style.display = \"none\";\n" +
                "        alipaysIframes[i].src = \"about:blank\";\n" +
                "      }\n" +
                "    }, 500);\n" +
                "    \n" +
                "    window._patchApplied = true;\n" +
                "  } catch(e) {\n" +
                "    console.log('应用拦截补丁失败: ' + e.message);\n" +
                "  }\n" +
                "}\n" +
                "</script>";
            
            // 在HTML的head标签中注入阻止脚本
            if (payHtml.contains("<head>")) {
                payHtml = payHtml.replace("<head>", "<head>" + blockAlipaysScript);
            } else if (payHtml.contains("<html>")) {
                payHtml = payHtml.replace("<html>", "<html><head>" + blockAlipaysScript + "</head>");
            } else {
                payHtml = "<html><head>" + blockAlipaysScript + "</head>" + payHtml + "</html>";
            }
            
            // 打印完整的支付HTML表单用于调试
            log.info("支付表单内容长度: {}", payHtml.length());
            
            // 记录生成的URL信息
            if (payHtml.contains("gateway.do")) {
                int startIndex = payHtml.indexOf("gateway.do");
                int endIndex = Math.min(startIndex + 100, payHtml.length());
                log.info("支付URL片段: {}", payHtml.substring(Math.max(0, startIndex - 50), endIndex));
            }
            
            log.info("生成支付宝支付表单成功");
            return payHtml;
        } catch (Exception e) {
            log.error("生成支付宝支付表单异常", e);
            
            // 提供更详细的错误信息
            String errorMessage = "支付请求失败: " + e.getMessage();
            if (e.getCause() != null) {
                errorMessage += " (原因: " + e.getCause().getMessage() + ")";
            }
            
            // 返回简单的HTML错误页面
            String errorHtml = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>支付错误</title>\n" +
                "    <style>\n" +
                "        body { font-family: Arial, sans-serif; text-align: center; padding-top: 50px; }\n" +
                "        .error-icon { color: #ff0000; font-size: 60px; margin-bottom: 20px; }\n" +
                "        .error-message { font-size: 18px; margin-bottom: 20px; }\n" +
                "        .error-detail { color: #666; margin-bottom: 30px; }\n" +
                "        .back-button { background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 4px; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"error-icon\">❌</div>\n" +
                "    <div class=\"error-message\">支付初始化失败</div>\n" +
                "    <div class=\"error-detail\">错误详情: 支付宝配置异常，请联系客服</div>\n" +
                "    <a href=\"javascript:history.back()\" class=\"back-button\">返回</a>\n" +
                "</body>\n" +
                "</html>";
            
            // 记录错误并返回错误页面，不抛出异常，避免影响前端
            return errorHtml;
        }
    }

    @Override
    public String handleNotify(Map<String, String> params) {
        try {
            // 验证支付宝回调请求的签名
            if (Factory.Payment.Common().verifyNotify(params)) {
                // 获取交易状态
                String tradeStatus = params.get("trade_status");
                
                // 商户订单号
                String traceNo = params.get("out_trade_no");
                
                // 支付宝交易号
                String alipayTradeNo = params.get("trade_no");
                
                // 交易金额
                String totalAmount = params.get("total_amount");
                
                // 交易时间
                String gmtPayment = params.get("gmt_payment");
                
                log.info("支付宝回调 - 商户订单号: {}, 支付宝交易号: {}, 交易金额: {}, 交易状态: {}, 交易时间: {}", 
                        traceNo, alipayTradeNo, totalAmount, tradeStatus, gmtPayment);
                
                // 支付成功处理
                if ("TRADE_SUCCESS".equals(tradeStatus)) {
                    // 更新支付状态
                    int updated = getBaseMapper().updateState(traceNo, "收款", gmtPayment, alipayTradeNo);
                    if (updated > 0) {
                        log.info("订单 {} 支付状态更新成功", traceNo);
                    } else {
                        log.warn("订单 {} 支付状态更新失败", traceNo);
                    }
                    
                    // 返回success给支付宝，表示我们已经处理成功
                    return "success";
                }
            } else {
                log.warn("支付宝回调验签失败");
            }
        } catch (Exception e) {
            log.error("支付宝回调处理异常", e);
        }
        return "fail";
    }

    @Override
    public List<OrderVO> getPendingPaymentList(Long userId) {
        log.info("获取用户{}的待付款订单列表", userId);
        try {
            // 获取用户所有订单
            List<OrderVO> allOrders = orderService.getOrderList(userId);
            
            // 筛选出待付款订单(状态为0表示待付款)
            return allOrders.stream()
                    .filter(order -> order.getOrderStatus() != null && order.getOrderStatus() == 0)
                    .toList();
        } catch (Exception e) {
            log.error("获取待付款订单列表异常", e);
            throw new RuntimeException("获取待付款订单列表失败", e);
        }
    }

    @Override
    public List<OrderVO> getWaitingShipmentList(Long userId) {
        log.info("获取用户{}的待发货订单列表", userId);
        try {
            // 获取用户所有订单
            List<OrderVO> allOrders = orderService.getOrderList(userId);
            
            // 筛选出待发货订单(状态为1表示已付款待发货)
            return allOrders.stream()
                    .filter(order -> order.getOrderStatus() != null && order.getOrderStatus() == 1)
                    .toList();
        } catch (Exception e) {
            log.error("获取待发货订单列表异常", e);
            throw new RuntimeException("获取待发货订单列表失败", e);
        }
    }
} 