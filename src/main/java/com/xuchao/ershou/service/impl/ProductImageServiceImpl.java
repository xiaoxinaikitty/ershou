package com.xuchao.ershou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.mapper.ProductImageMapper;
import com.xuchao.ershou.mapper.ProductMapper;
import com.xuchao.ershou.model.dao.product.ProductImageAddDao;
import com.xuchao.ershou.model.entity.Product;
import com.xuchao.ershou.model.entity.ProductImage;
import com.xuchao.ershou.model.vo.ProductImageVO;
import com.xuchao.ershou.service.ProductImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 商品图片服务实现类
 */
@Service
public class ProductImageServiceImpl implements ProductImageService {

    private static final Logger logger = LoggerFactory.getLogger(ProductImageServiceImpl.class);

    @Autowired
    private ProductImageMapper productImageMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Value("${file.upload.path}")
    private String uploadPath;
    
    @Autowired
    private String urlPrefix;
    
    @Override
    @Transactional
    public ProductImageVO addProductImage(Long userId, ProductImageAddDao productImageAddDao) {
        // 参数校验
        if (productImageAddDao == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品图片信息不能为空");
        }
        
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        Long productId = productImageAddDao.getProductId();
        
        // 查询商品是否存在
        Product product = productMapper.selectProductById(productId);
        if (product == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "商品不存在");
        }
        
        // 校验当前用户是否为商品发布者
        if (!product.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限为他人发布的商品添加图片");
        }
        
        // 创建商品图片对象
        ProductImage productImage = new ProductImage();
        BeanUtils.copyProperties(productImageAddDao, productImage);
        
        // 如果当前图片是主图，则需要将该商品的其他图片设置为非主图
        if (productImage.getIsMain() != null && productImage.getIsMain() == 1) {
            productImageMapper.updateOtherImagesNonMain(productId);
        }
        
        // 插入数据库
        int result = productImageMapper.insertProductImage(productImage);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图片添加失败");
        }
        
        // 转换为视图对象返回
        ProductImageVO productImageVO = new ProductImageVO();
        BeanUtils.copyProperties(productImage, productImageVO);
        
        return productImageVO;
    }
    
    @Override
    @Transactional
    public ProductImageVO uploadProductImage(Long userId, ProductImageAddDao productImageAddDao, MultipartFile imageFile) {
        // 参数校验
        if (productImageAddDao == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品图片信息不能为空");
        }
        
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        if (imageFile == null || imageFile.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片文件不能为空");
        }
        
        // 校验文件类型
        String originalFilename = imageFile.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        if (!isValidImageExtension(fileExtension)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "仅支持jpg、jpeg、png、gif格式的图片");
        }
        
        Long productId = productImageAddDao.getProductId();
        
        // 查询商品是否存在
        Product product = productMapper.selectProductById(productId);
        if (product == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "商品不存在");
        }
        
        // 校验当前用户是否为商品发布者
        if (!product.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限为他人发布的商品添加图片");
        }
        
        try {
            // 生成唯一文件名
            String uniqueFileName = UUID.randomUUID().toString() + "." + fileExtension;
            
            // 确保上传目录存在
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 构建文件保存路径
            Path filePath = Paths.get(uploadPath, uniqueFileName);
            
            // 保存文件
            Files.copy(imageFile.getInputStream(), filePath);
            
            // 生成文件访问URL
            String imageUrl = urlPrefix + "/" + uniqueFileName;
            
            // 创建商品图片对象
            ProductImage productImage = new ProductImage();
            BeanUtils.copyProperties(productImageAddDao, productImage);
            productImage.setImageUrl(imageUrl);
            
            // 如果当前图片是主图，则需要将该商品的其他图片设置为非主图
            if (productImage.getIsMain() != null && productImage.getIsMain() == 1) {
                productImageMapper.updateOtherImagesNonMain(productId);
            }
            
            // 插入数据库
            int result = productImageMapper.insertProductImage(productImage);
            if (result <= 0) {
                // 删除已上传的文件
                Files.deleteIfExists(filePath);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图片添加失败");
            }
            
            // 转换为视图对象返回
            ProductImageVO productImageVO = new ProductImageVO();
            BeanUtils.copyProperties(productImage, productImageVO);
            
            return productImageVO;
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图片上传失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public ProductImageVO addProductImageByUrl(Long userId, ProductImageAddDao productImageAddDao) {
        // 参数校验
        if (productImageAddDao == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品图片信息不能为空");
        }
        
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        String imageUrl = productImageAddDao.getImageUrl();
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片URL不能为空");
        }
        
        // 简单校验URL格式
        if (!isValidImageUrl(imageUrl)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无效的图片URL，请提供以http://或https://开头的有效URL");
        }
        
        Long productId = productImageAddDao.getProductId();
        
        // 查询商品是否存在
        Product product = productMapper.selectProductById(productId);
        if (product == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "商品不存在");
        }
        
        // 校验当前用户是否为商品发布者
        if (!product.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限为他人发布的商品添加图片");
        }
        
        // 创建商品图片对象
        ProductImage productImage = new ProductImage();
        BeanUtils.copyProperties(productImageAddDao, productImage);
        // 使用提供的URL作为图片URL
        productImage.setImageUrl(imageUrl);
        
        // 如果当前图片是主图，则需要将该商品的其他图片设置为非主图
        if (productImage.getIsMain() != null && productImage.getIsMain() == 1) {
            productImageMapper.updateOtherImagesNonMain(productId);
        }
        
        // 插入数据库
        int result = productImageMapper.insertProductImage(productImage);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图片添加失败");
        }
        
        // 转换为视图对象返回
        ProductImageVO productImageVO = new ProductImageVO();
        BeanUtils.copyProperties(productImage, productImageVO);
        
        return productImageVO;
    }
    
    @Override
    @Transactional
    public boolean deleteProductImage(Long userId, Long productId, Long imageId) {
        // 参数校验
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        if (productId == null || productId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品ID无效");
        }
        
        if (imageId == null || imageId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片ID无效");
        }
        
        // 查询商品是否存在
        Product product = productMapper.selectProductById(productId);
        if (product == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "商品不存在");
        }
        
        // 校验当前用户是否为商品发布者
        if (!product.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限删除他人发布的商品图片");
        }
        
        // 查询图片是否存在
        QueryWrapper<ProductImage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("image_id", imageId).eq("product_id", productId);
        ProductImage productImage = productImageMapper.selectOne(queryWrapper);
        
        if (productImage == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "图片不存在或不属于该商品");
        }
        
        // 尝试删除物理文件
        try {
            String imageUrl = productImage.getImageUrl();
            if (imageUrl != null && imageUrl.startsWith(urlPrefix)) {
                String fileName = imageUrl.substring(urlPrefix.length() + 1);
                Path filePath = Paths.get(uploadPath, fileName);
                Files.deleteIfExists(filePath);
            }
        } catch (IOException e) {
            // 记录日志但不影响后续操作
        }
        
        // 执行删除
        int result = productImageMapper.deleteById(imageId);
        
        // 如果删除的是主图，需要重新设置一个主图
        if (productImage.getIsMain() != null && productImage.getIsMain() == 1) {
            productImageMapper.setNewMainImage(productId);
        }
        
        return result > 0;
    }
    
    @Override
    @Transactional
    public List<ProductImageVO> batchAddProductImagesByUrl(Long userId, ProductImageAddDao productImageAddDao) {
        // 参数校验
        if (productImageAddDao == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品图片信息不能为空");
        }
        
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        List<String> imageUrls = productImageAddDao.getImageUrls();
        // 添加日志
        logger.info("批量添加商品图片，用户ID: {}, 商品ID: {}, 图片URL列表: {}", 
                  userId, productImageAddDao.getProductId(), imageUrls);
        
        // DTO的getImageUrls()方法已经处理了imageUrl到imageUrls的转换
        // 此时imageUrls如果为空，说明没有提供任何有效的图片URL
        if (imageUrls.isEmpty()) {
            logger.error("图片URL列表为空，用户ID: {}, 商品ID: {}", 
                       userId, productImageAddDao.getProductId());
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请提供至少一个有效的图片URL");
        }
        
        Long productId = productImageAddDao.getProductId();
        
        // 查询商品是否存在
        Product product = productMapper.selectProductById(productId);
        if (product == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "商品不存在");
        }
        
        // 校验当前用户是否为商品发布者
        if (!product.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限为他人发布的商品添加图片");
        }
        
        // 批量处理图片URL
        List<ProductImageVO> results = new ArrayList<>();
        boolean hasSetMainImage = false;
        
        // DTO已经过滤了无效的URL，并且如果imageUrl有值但imageUrls为空，则会被自动添加到imageUrls中
        List<String> validImageUrls = productImageAddDao.getImageUrls();
        logger.info("开始处理图片URL列表，有效URL数量: {}", validImageUrls.size());
        
        for (int i = 0; i < validImageUrls.size(); i++) {
            String imageUrl = validImageUrls.get(i);
            
            // URL格式校验已经在DTO的getImageUrls()中过滤过空值和null，此处只需校验URL格式
            if (!isValidImageUrl(imageUrl)) {
                logger.warn("跳过无效URL格式: {}", imageUrl);
                continue; // 跳过无效URL
            }
            
            // 创建商品图片对象
            ProductImage productImage = new ProductImage();
            productImage.setProductId(productId);
            
            // 第一张图片设为主图（如果指定了isMain=1）
            if (i == 0 && productImageAddDao.getIsMain() != null && productImageAddDao.getIsMain() == 1) {
                productImage.setIsMain(1);
                hasSetMainImage = true;
                
                // 将该商品的其他图片设置为非主图
                productImageMapper.updateOtherImagesNonMain(productId);
            } else {
                productImage.setIsMain(0);
            }
            
            // 设置图片排序（按照列表顺序递增）
            int sortOrder = productImageAddDao.getSortOrder() != null ? 
                            productImageAddDao.getSortOrder() + i : i;
            productImage.setSortOrder(sortOrder);
            
            // 使用提供的URL作为图片URL
            productImage.setImageUrl(imageUrl);
            
            // 插入数据库
            int result = productImageMapper.insertProductImage(productImage);
            if (result <= 0) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图片添加失败");
            }
            
            // 转换为视图对象添加到结果列表
            ProductImageVO productImageVO = new ProductImageVO();
            BeanUtils.copyProperties(productImage, productImageVO);
            results.add(productImageVO);
        }
        
        return results;
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
     * 检查文件扩展名是否为有效的图片格式
     */
    private boolean isValidImageExtension(String extension) {
        if (extension == null || extension.isEmpty()) {
            return false;
        }
        return extension.equals("jpg") || extension.equals("jpeg") || 
               extension.equals("png") || extension.equals("gif");
    }
    
    /**
     * 检查URL是否为有效的图片URL
     */
    private boolean isValidImageUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        
        // 简单校验URL格式，必须以http://或https://开头
        return url.startsWith("http://") || url.startsWith("https://");
    }
}
