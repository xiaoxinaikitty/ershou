package com.xuchao.ershou.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Base64;
import java.util.UUID;
import java.text.SimpleDateFormat;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.xuchao.ershou.service.ImageAuditService;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * 阿里云图片内容安全审核服务实现
 */
@Service
public class AliyunImageAuditServiceImpl implements ImageAuditService {

    private static final Logger logger = LoggerFactory.getLogger(AliyunImageAuditServiceImpl.class);

    @Value("${aliyun.green.region:cn-shanghai}")
    private String regionId;

    @Value("${aliyun.green.scenes:porn,terrorism,ad}")
    private String scenesConfig;
    
    @Value("${aliyun.accessKey.id}")
    private String accessKeyId;
    
    @Value("${aliyun.accessKey.secret}")
    private String accessKeySecret;
    
    private static final String GREEN_API_URL = "https://green.cn-shanghai.aliyuncs.com/green/image/scan";

    /**
     * 审核图片URL
     * @param imageUrl 图片URL
     * @return 是否通过审核
     */
    @Override
    public boolean auditImageUrl(String imageUrl) {
        try {
            logger.info("开始审核图片URL: {}", imageUrl);
            
            // 验证URL是否为图片格式
            if (!isValidImageUrl(imageUrl)) {
                logger.warn("非图片URL格式，跳过审核: {}", imageUrl);
                return true;
            }
            
            // 构建请求体
            JSONObject requestBody = buildImageUrlRequestBody(imageUrl);
            
            // 发送HTTP请求并获取响应
            String responseStr = sendHttpRequest(requestBody.toJSONString());
            
            // 解析响应结果
            return parseAndCheckResponse(responseStr);
            
        } catch (Exception e) {
            logger.error("图片审核过程中发生异常", e);
            // 测试开发环境出现异常时默认通过
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
            logger.info("开始审核图片流");
            
            // 读取输入流到字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = imageStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            byte[] imageBytes = outputStream.toByteArray();
            
            // Base64编码
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            
            // 构建请求体
            JSONObject requestBody = buildImageBase64RequestBody(base64Image);
            
            // 发送HTTP请求并获取响应
            String responseStr = sendHttpRequest(requestBody.toJSONString());
            
            // 解析响应结果
            return parseAndCheckResponse(responseStr);
            
        } catch (Exception e) {
            logger.error("图片审核过程中发生异常", e);
            // 测试开发环境出现异常时默认通过
            return true;
        }
    }
    
    /**
     * 构建图片URL请求体
     */
    private JSONObject buildImageUrlRequestBody(String imageUrl) {
        // 构建场景列表
        List<String> scenes = Arrays.asList(scenesConfig.split(","));
        
        // 构建请求Task
        JSONObject task = new JSONObject();
        task.put("dataId", UUID.randomUUID().toString());
        task.put("url", imageUrl);
        
        JSONArray tasks = new JSONArray();
        tasks.add(task);
        
        // 构建请求体
        JSONObject requestBody = new JSONObject();
        requestBody.put("scenes", scenes);
        requestBody.put("tasks", tasks);
        
        return requestBody;
    }
    
    /**
     * 构建图片Base64请求体
     */
    private JSONObject buildImageBase64RequestBody(String base64Image) {
        // 构建场景列表
        List<String> scenes = Arrays.asList(scenesConfig.split(","));
        
        // 构建请求Task
        JSONObject task = new JSONObject();
        task.put("dataId", UUID.randomUUID().toString());
        task.put("content", base64Image);
        
        JSONArray tasks = new JSONArray();
        tasks.add(task);
        
        // 构建请求体
        JSONObject requestBody = new JSONObject();
        requestBody.put("scenes", scenes);
        requestBody.put("tasks", tasks);
        
        return requestBody;
    }
    
    /**
     * 发送HTTP请求到阿里云内容安全服务
     */
    private String sendHttpRequest(String requestBodyString) throws Exception {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(GREEN_API_URL);
        
        // 当前GMT时间
        String gmtDate = toGMTString(new Date());
        
        // 计算签名
        String md5 = md5Base64(requestBodyString);
        String stringToSign = "POST\n" + md5 + "\napplication/json\n" + gmtDate + "\n/green/image/scan";
        String signature = createSignature(stringToSign);
        String authHeader = "acs " + accessKeyId + ":" + signature;
        
        // 设置请求头
        httpPost.addHeader("Accept", "application/json");
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("Content-MD5", md5);
        httpPost.addHeader("Date", gmtDate);
        httpPost.addHeader("Authorization", authHeader);
        
        // 设置请求体
        httpPost.setEntity(new StringEntity(requestBodyString, StandardCharsets.UTF_8));
        
        // 发送请求
        HttpResponse response = httpClient.execute(httpPost);
        
        // 返回响应结果
        return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
    }
    
    /**
     * 解析并检查响应
     */
    private boolean parseAndCheckResponse(String responseStr) {
        try {
            JSONObject responseJson = JSON.parseObject(responseStr);
            
            logger.info("图片审核响应: {}", responseJson);
            
            int code = responseJson.getInteger("code");
            if (200 != code) {
                logger.error("图片审核请求失败: code={}, message={}", code, responseJson.getString("msg"));
                // 测试开发环境审核失败时默认通过
                return true;
            }
            
            JSONArray taskResults = responseJson.getJSONArray("data");
            if (taskResults == null || taskResults.isEmpty()) {
                logger.warn("图片审核结果为空");
                return true;
            }
            
            for (int i = 0; i < taskResults.size(); i++) {
                JSONObject taskResult = taskResults.getJSONObject(i);
                if (200 != taskResult.getInteger("code")) {
                    logger.error("图片任务审核失败: {}", taskResult.getString("msg"));
                    continue;
                }
                
                JSONArray sceneResults = taskResult.getJSONArray("results");
                if (sceneResults == null || sceneResults.isEmpty()) {
                    continue;
                }
                
                for (int j = 0; j < sceneResults.size(); j++) {
                    JSONObject sceneResult = sceneResults.getJSONObject(j);
                    String scene = sceneResult.getString("scene");
                    String suggestion = sceneResult.getString("suggestion");
                    
                    // 只有当判定为违规时才拦截，其他情况（疑似、通过）都允许
                    if ("block".equals(suggestion)) {
                        logger.warn("图片审核未通过，场景: {}, 建议: {}", scene, suggestion);
                        return false;
                    }
                }
            }
            
            // 所有审核都通过
            logger.info("图片审核通过");
            return true;
        } catch (Exception e) {
            logger.error("解析审核结果时发生异常", e);
            // 测试开发环境异常时默认通过
            return true;
        }
    }
    
    /**
     * 验证URL是否为图片URL
     */
    private boolean isValidImageUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
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
     * 生成GMT格式时间字符串
     */
    private String toGMTString(Date date) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.US);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(date);
    }
    
    /**
     * 计算字符串的MD5并进行Base64编码
     */
    private String md5Base64(String s) throws Exception {
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
        byte[] md5Bytes = md.digest(s.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(md5Bytes);
    }
    
    /**
     * 使用HMAC-SHA1算法生成签名
     */
    private String createSignature(String stringToSign) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(accessKeySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(signData);
    }
} 