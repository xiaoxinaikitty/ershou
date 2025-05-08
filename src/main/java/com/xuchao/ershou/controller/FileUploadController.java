package com.xuchao.ershou.controller;

import com.xuchao.ershou.common.BaseResponse;
import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.common.ResultUtils;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.service.ImageAuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 通用文件上传控制器
 */
@RestController
@RequestMapping("/file")
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    // 从配置文件中获取文件上传路径
    @Value("${file.upload.path}")
    private String uploadPath;
    
    // 从配置文件中获取文件访问URL前缀
    @Value("${file.upload.url.prefix}")
    private String fileUrlPrefix;
    
    @Autowired
    private ImageAuditService imageAuditService;

    // 允许的文件类型
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "zip", "rar"
    );
    
    // 图片类型列表，需要进行审核
    private static final List<String> IMAGE_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "gif"
    );

    /**
     * 通用文件上传接口
     *
     * @param file 上传的文件
     * @return 文件访问URL和相关信息
     */
    @PostMapping("/upload")
    public BaseResponse<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件不能为空");
        }

        try {
            // 获取原始文件名和扩展名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);

            // 验证文件类型
            if (!isValidFileExtension(fileExtension)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的文件类型，仅支持：" + String.join(", ", ALLOWED_EXTENSIONS));
            }

            // 如果是图片文件，进行简单的内容审核
            if (isImageFile(fileExtension)) {
                logger.info("审核上传图片: {}", originalFilename);
                try {
                    boolean isImageSafe = imageAuditService.auditImageStream(file.getInputStream());
                    if (!isImageSafe) {
                        throw new BusinessException(ErrorCode.FORBIDDEN, "图片内容违规，请上传合规的图片");
                    }
                    logger.info("图片审核通过");
                } catch (Exception e) {
                    logger.error("图片审核出错", e);
                    // 审核出错时，允许上传继续，不阻断流程
                }
            }

            // 生成唯一文件名
            String uniqueFileName = UUID.randomUUID().toString() + "." + fileExtension;

            // 确保目录存在
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (!created) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "无法创建文件上传目录: " + uploadPath);
                }
            }

            // 构建文件保存路径
            Path filePath = Paths.get(uploadPath, uniqueFileName);

            // 保存文件
            Files.copy(file.getInputStream(), filePath);

            // 生成文件访问URL
            String fileUrl = fileUrlPrefix + "/" + uniqueFileName;

            // 构建返回结果
            Map<String, String> result = new HashMap<>();
            result.put("fileName", originalFilename);
            result.put("fileUrl", fileUrl);
            result.put("fileSize", String.valueOf(file.getSize()));
            result.put("fileType", file.getContentType());

            return ResultUtils.success(result);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(dotIndex + 1).toLowerCase();
    }
    
    /**
     * 检查文件扩展名是否为允许上传的类型
     */
    private boolean isValidFileExtension(String extension) {
        if (extension == null || extension.isEmpty()) {
            return false;
        }
        return ALLOWED_EXTENSIONS.contains(extension.toLowerCase());
    }
    
    /**
     * 检查文件是否为图片类型
     */
    private boolean isImageFile(String extension) {
        if (extension == null || extension.isEmpty()) {
            return false;
        }
        return IMAGE_EXTENSIONS.contains(extension.toLowerCase());
    }
} 