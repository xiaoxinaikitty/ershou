package com.xuchao.ershou.controller;

import com.xuchao.ershou.common.BaseResponse;
import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.common.ResultUtils;
import com.xuchao.ershou.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@RestController
public class FileController {
    
    @Value("${file.upload.path}")
    private String uploadPath;
    
    @Value("${file.upload.url.prefix}")
    private String urlPrefix;
    
    /**
     * 上传图片
     * @param file 图片文件
     * @param type 图片类型(可选): product-商品图片, avatar-头像, promotion-营销活动图片
     * @return 图片URL
     */
    @PostMapping("/upload/image")
    public BaseResponse<String> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", defaultValue = "other") String type) {
        
        // 验证文件是否为空
        if (file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "上传文件不能为空");
        }
        
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "只能上传图片文件");
        }
        
        try {
            // 获取原始文件名和扩展名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            // 创建保存目录 - 按日期和类型分类存储
            String dateDir = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String savePath = uploadPath + File.separator + type + File.separator + dateDir;
            File saveDir = new File(savePath);
            if (!saveDir.exists() && !saveDir.mkdirs()) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建目录失败");
            }
            
            // 生成新的文件名，防止覆盖同名文件
            String newFilename = UUID.randomUUID().toString().replace("-", "") + extension;
            File dest = new File(savePath + File.separator + newFilename);
            
            // 保存文件
            file.transferTo(dest);
            
            // 返回文件访问路径
            String fileUrl = urlPrefix + "/" + type + "/" + dateDir + "/" + newFilename;
            
            return ResultUtils.success(fileUrl);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败：" + e.getMessage());
        }
    }
} 