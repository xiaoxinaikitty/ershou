package com.xuchao.ershou.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.service.ImageAuditService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.xuchao.ershou.config.BaiduAiConfig;

/**
 * 百度AI图片内容审核服务实现
 * 使用HttpClient直接调用百度AI开放平台API
 */
@Service
@Slf4j
public class BaiduImageAuditServiceImpl implements ImageAuditService {

    private static final String AUTH_URL = "https://aip.baidubce.com/oauth/2.0/token";
    private static final String CENSOR_URL = "https://aip.baidubce.com/rest/2.0/solution/v1/img_censor/v2/user_defined";
    
    @Autowired
    private BaiduAiConfig baiduAiConfig;
    
    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    private String accessToken;
    private long tokenExpireTime;
    
    private final Gson gson = new Gson();
    private final HttpClient httpClient = HttpClientBuilder.create().build();

    /**
     * 获取百度AI接口的访问令牌
     * @return 访问令牌
     */
    private synchronized String getAccessToken() {
        // 如果令牌有效，直接返回
        if (accessToken != null && System.currentTimeMillis() < tokenExpireTime) {
            return accessToken;
        }
        
        try {
            log.info("开始获取百度AI访问令牌...");
            
            // 验证API Key和Secret是否有效
            if (!StringUtils.hasText(baiduAiConfig.getApiKey()) || !StringUtils.hasText(baiduAiConfig.getSecretKey())) {
                log.error("百度AI配置无效: apiKey或secretKey为空");
                return null;
            }
            
            // 构建获取令牌URL
            String getTokenUrl = AUTH_URL + "?grant_type=client_credentials" +
                    "&client_id=" + baiduAiConfig.getApiKey() + "&client_secret=" + baiduAiConfig.getSecretKey();
            
            HttpPost httpPost = new HttpPost(getTokenUrl);
            httpPost.setHeader("Content-Type", "application/json");
            
            HttpResponse response = httpClient.execute(httpPost);
            String result = EntityUtils.toString(response.getEntity());
            
            log.debug("令牌响应: {}", result);
            
            // 解析响应
            JsonObject jsonObject = gson.fromJson(result, JsonObject.class);
            
            if (jsonObject.has("error")) {
                log.error("获取令牌失败: {}", jsonObject.get("error_description").getAsString());
                return null;
            }
            
            // 提取令牌和过期时间
            accessToken = jsonObject.get("access_token").getAsString();
            int expiresIn = jsonObject.get("expires_in").getAsInt();
            
            // 设置过期时间，比官方时间提前5分钟过期以确保安全
            tokenExpireTime = System.currentTimeMillis() + (expiresIn - 300) * 1000L;
            
            log.info("成功获取百度AI访问令牌");
            return accessToken;
        } catch (Exception e) {
            log.error("获取百度AI访问令牌出错: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 审核图片URL
     * @param imageUrl 图片URL
     * @return 是否通过审核
     */
    @Override
    public boolean auditImageUrl(String imageUrl) {
        try {
            log.info("开始审核图片URL: {}", imageUrl);
            
            // 验证URL是否为图片格式
            if (!isValidImageUrl(imageUrl)) {
                log.warn("非图片URL格式，跳过审核: {}", imageUrl);
                return true;
            }
            
            // 如果是开发环境并且启用了模拟模式，直接返回通过
            if (isDevEnvironmentAndMockEnabled()) {
                log.info("开发环境模拟审核模式，图片URL: {} 直接通过审核", imageUrl);
                return true;
            }
            
            // 判断是否为内部URL (localhost, 127.0.0.1, 192.168.xxx.xxx等)
            if (isInternalUrl(imageUrl)) {
                log.info("检测到内部URL，将下载图片后使用二进制内容进行审核");
                // 下载图片并使用二进制内容审核
                return downloadAndAuditImage(imageUrl);
            }

            // 获取访问令牌
            String token = getAccessToken();
            if (token == null) {
                log.error("无法获取百度AI访问令牌，跳过审核");
                return true;
            }
            
            // 构建请求URL
            String requestUrl = CENSOR_URL + "?access_token=" + token;
            
            // 构建请求体
            Map<String, String> params = new HashMap<>();
            params.put("imgUrl", imageUrl);
            
            // 发送请求
            HttpPost httpPost = new HttpPost(requestUrl);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            
            // 将参数编码为表单格式
            StringBuilder postBody = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (postBody.length() > 0) {
                    postBody.append("&");
                }
                postBody.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            
            httpPost.setEntity(new StringEntity(postBody.toString(), "UTF-8"));
            
            // 发送请求并获取响应
            HttpResponse response = httpClient.execute(httpPost);
            String result = EntityUtils.toString(response.getEntity(), "UTF-8");
            
            log.debug("图片审核响应: {}", result);
            
            // 解析响应
            return parseAndCheckResponse(result);
            
        } catch (Exception e) {
            log.error("图片URL审核过程中发生异常: {}", e.getMessage(), e);
            // 发生异常时返回通过，避免阻塞业务
            return true;
        }
    }

    /**
     * 审核图片流
     * @param imageStream 图片输入流
     * @return 是否通过审核
     */
    @Override
    public boolean auditImageStream(InputStream imageStream) {
        try {
            log.info("开始审核图片流");
            
            // 如果是开发环境并且启用了模拟模式，直接返回通过
            if (isDevEnvironmentAndMockEnabled()) {
                log.info("开发环境模拟审核模式，图片流直接通过审核");
                return true;
            }
            
            // 获取访问令牌
            String token = getAccessToken();
            if (token == null) {
                log.error("无法获取百度AI访问令牌，跳过审核");
                return true;
            }
            
            // 读取图片流为字节数组
            byte[] imageBytes = IOUtils.toByteArray(imageStream);
            
            // 对字节数组进行Base64编码
            String base64Image = Base64.encodeBase64String(imageBytes);
            
            // 构建请求URL
            String requestUrl = CENSOR_URL + "?access_token=" + token;
            
            // 构建请求体
            Map<String, String> params = new HashMap<>();
            params.put("image", base64Image);
            
            // 发送请求
            HttpPost httpPost = new HttpPost(requestUrl);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            
            // 将参数编码为表单格式
            StringBuilder postBody = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (postBody.length() > 0) {
                    postBody.append("&");
                }
                postBody.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            
            httpPost.setEntity(new StringEntity(postBody.toString(), "UTF-8"));
            
            // 发送请求并获取响应
            HttpResponse response = httpClient.execute(httpPost);
            String result = EntityUtils.toString(response.getEntity(), "UTF-8");
            
            log.debug("图片审核响应: {}", result);
            
            // 解析响应
            return parseAndCheckResponse(result);
            
        } catch (IOException e) {
            log.error("读取图片流失败: {}", e.getMessage(), e);
            // 发生异常时返回通过，避免阻塞业务
            return true;
        } catch (Exception e) {
            log.error("图片流审核过程中发生异常: {}", e.getMessage(), e);
            // 发生异常时返回通过，避免阻塞业务
            return true;
        }
    }
    
    /**
     * 判断是否开发环境并且启用了模拟模式
     */
    private boolean isDevEnvironmentAndMockEnabled() {
        return ("dev".equals(activeProfile) || "development".equals(activeProfile)) && baiduAiConfig.isMock();
    }
    
    /**
     * 解析并检查响应
     */
    private boolean parseAndCheckResponse(String responseString) {
        try {
            if (responseString == null || responseString.isEmpty()) {
                log.warn("审核响应为空");
                return true;
            }
            
            // 解析JSON响应
            JsonObject responseJson = gson.fromJson(responseString, JsonObject.class);
            
            // 检查是否存在错误码
            if (responseJson.has("error_code")) {
                int errorCode = responseJson.get("error_code").getAsInt();
                String errorMsg = responseJson.has("error_msg") ? 
                    responseJson.get("error_msg").getAsString() : "未知错误";
                log.error("图片审核API返回错误: code={}, msg={}", errorCode, errorMsg);
                // 服务接口错误时返回通过，避免阻塞业务
                return true;
            }
            
            // 检查是否包含必要字段
            if (!responseJson.has("conclusionType") || !responseJson.has("conclusion")) {
                log.error("审核结果缺少必要字段: {}", responseString);
                return true;
            }
            
            // 解析审核结果
            int conclusionType = responseJson.get("conclusionType").getAsInt();
            String conclusion = responseJson.get("conclusion").getAsString();
            
            log.info("审核结论: {}({})", conclusion, conclusionType);
            
            // conclusionType: 1-合规，2-不合规，3-疑似，4-审核失败
            // 只有明确不合规的情况下返回false
            if (conclusionType == 2) {
                log.warn("图片审核未通过，结论: {}", conclusion);
                
                // 获取详细的不合规原因
                if (responseJson.has("data") && !responseJson.get("data").isJsonNull()) {
                    JsonArray dataArray = null;
                    try {
                        dataArray = responseJson.getAsJsonArray("data");
                    } catch (Exception e) {
                        log.warn("解析data数组失败: {}", e.getMessage());
                    }
                    
                    if (dataArray != null && dataArray.size() > 0) {
                        StringBuilder reasons = new StringBuilder();
                        for (JsonElement element : dataArray) {
                            if (element.isJsonNull() || !element.isJsonObject()) {
                                continue;
                            }
                            
                            JsonObject item = element.getAsJsonObject();
                            
                            // 添加字段存在性检查
                            if (item.has("msg") && !item.get("msg").isJsonNull()) {
                                reasons.append(item.get("msg").getAsString());
                            } else {
                                reasons.append("未知原因");
                            }
                            
                            reasons.append("(可能性: ");
                            
                            // 添加空值检查，有些返回可能没有probability字段
                            if (item.has("probability") && !item.get("probability").isJsonNull()) {
                                reasons.append(item.get("probability").getAsDouble());
                            } else {
                                reasons.append("未知");
                            }
                            
                            reasons.append(") ");
                        }
                        log.warn("不合规原因: {}", reasons.toString());
                    }
                }
                
                return false;
            }
            
            log.info("图片审核通过，结论: {}", conclusion);
            return true;
        } catch (Exception e) {
            log.error("解析审核结果时发生异常: {}, 原始响应: {}", e.getMessage(), responseString, e);
            // 发生异常时返回通过，避免阻塞业务
            return true;
        }
    }
    
    /**
     * 验证URL是否为图片URL
     */
    private boolean isValidImageUrl(String url) {
        if (!StringUtils.hasText(url)) {
            return false;
        }
        
        // 检查URL是否以常见图片扩展名结尾
        String lowerCaseUrl = url.toLowerCase();
        return lowerCaseUrl.endsWith(".jpg") || 
               lowerCaseUrl.endsWith(".jpeg") || 
               lowerCaseUrl.endsWith(".png") || 
               lowerCaseUrl.endsWith(".gif") || 
               lowerCaseUrl.endsWith(".bmp") || 
               lowerCaseUrl.endsWith(".webp");
    }
    
    /**
     * 判断URL是否为内部地址
     */
    private boolean isInternalUrl(String url) {
        if (url == null) {
            return false;
        }
        
        String lowerUrl = url.toLowerCase();
        return lowerUrl.contains("localhost") || 
               lowerUrl.contains("127.0.0.1") || 
               lowerUrl.contains("192.168.") || 
               lowerUrl.contains("10.") || 
               lowerUrl.contains("172.16.") || 
               lowerUrl.contains("172.17.") || 
               lowerUrl.contains("172.18.") || 
               lowerUrl.contains("172.19.") || 
               lowerUrl.contains("172.20.") || 
               lowerUrl.contains("172.21.") || 
               lowerUrl.contains("172.22.") || 
               lowerUrl.contains("172.23.") || 
               lowerUrl.contains("172.24.") || 
               lowerUrl.contains("172.25.") || 
               lowerUrl.contains("172.26.") || 
               lowerUrl.contains("172.27.") || 
               lowerUrl.contains("172.28.") || 
               lowerUrl.contains("172.29.") || 
               lowerUrl.contains("172.30.") || 
               lowerUrl.contains("172.31.");
    }
    
    /**
     * 下载图片并审核
     */
    private boolean downloadAndAuditImage(String imageUrl) {
        try {
            log.info("开始下载图片: {}", imageUrl);
            
            // 创建HTTP客户端
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet(imageUrl);
            HttpResponse response = client.execute(httpGet);
            
            // 检查响应状态
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                log.error("下载图片失败，状态码: {}", statusCode);
                return true;
            }
            
            // 读取图片数据
            byte[] imageData = IOUtils.toByteArray(response.getEntity().getContent());
            
            // 使用图片二进制数据进行审核
            try (InputStream imageStream = new java.io.ByteArrayInputStream(imageData)) {
                return auditImageStream(imageStream);
            }
            
        } catch (Exception e) {
            log.error("下载并审核图片时发生异常: {}", e.getMessage(), e);
            // 发生异常时返回通过，避免阻塞业务
            return true;
        }
    }
} 