package com.xuchao.ershou.config;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * 支付宝支付配置类
 */
@Data
@Slf4j
@Component
@ConfigurationProperties(prefix = "alipay")
public class AliPayConfig {
    private String appId;
    private String appPrivateKey;
    private String alipayPublicKey;
    private String notifyUrl;
    private String returnUrl;
    private String gatewayUrl;

    @PostConstruct
    public void init() {
        // 先进行配置检查
        log.info("开始初始化支付宝SDK配置...");
        if (appId == null || appId.isEmpty()) {
            log.error("支付宝配置错误: appId为空");
        }
        if (appPrivateKey == null || appPrivateKey.isEmpty()) {
            log.error("支付宝配置错误: appPrivateKey为空");
        } else {
            log.info("支付宝私钥长度: {}", appPrivateKey.length());
        }
        if (alipayPublicKey == null || alipayPublicKey.isEmpty()) {
            log.error("支付宝配置错误: alipayPublicKey为空");
        }
        if (notifyUrl == null || notifyUrl.isEmpty()) {
            log.error("支付宝配置错误: notifyUrl为空");
        }
        if (returnUrl == null || returnUrl.isEmpty()) {
            log.error("支付宝配置错误: returnUrl为空");
        }
        if (gatewayUrl == null || gatewayUrl.isEmpty()) {
            log.error("支付宝配置错误: gatewayUrl为空");
        }

        try {
            // 设置参数（全局只需设置一次）
            Config config = new Config();
            config.protocol = "https";
            // 修正网关地址格式，只设置主机名称，不要包含协议前缀
            config.gatewayHost = "openapi-sandbox.dl.alipaydev.com"; // 沙箱环境网关
            config.signType = "RSA2";
            config.appId = this.appId;
            config.merchantPrivateKey = this.appPrivateKey;
            config.alipayPublicKey = this.alipayPublicKey;
            config.notifyUrl = this.notifyUrl;
            // 注意：支付宝Java SDK 中的 Config 类并没有 format 和 charset 属性
            // 这些参数会在请求时自动设置为 JSON 和 UTF-8
            
            // 关闭热更新，避免沙箱环境的问题
            config.ignoreSSL = true;
            // returnUrl需要在支付API调用时单独设置，这里不能直接设置到config对象
            
            Factory.setOptions(config);
            log.info("=======支付宝SDK初始化成功=======");
            log.info("=======应用ID: {}=======", this.appId);
            log.info("=======网关: {}=======", config.gatewayHost);
            log.info("=======异步通知URL: {}=======", this.notifyUrl);
            log.info("=======同步返回URL: {}=======", this.returnUrl);
        } catch (Exception e) {
            log.error("初始化支付宝SDK配置失败", e);
        }
    }
    
    // 提供获取私钥的方法，用于调试
    public String getPrivateKey() {
        return this.appPrivateKey;
    }
    
    // 获取支付宝网关
    public String getGateway() {
        // 修正网关地址，确保格式正确，删除多余的https://
        return "openapi-sandbox.dl.alipaydev.com";
    }
} 