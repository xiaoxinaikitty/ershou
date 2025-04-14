package com.xuchao.ershou.service.impl;

import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.mapper.RecycleMethodMapper;
import com.xuchao.ershou.mapper.UserMapper;
import com.xuchao.ershou.model.dao.product.RecycleMethodAddDao;
import com.xuchao.ershou.model.entity.RecycleMethod;
import com.xuchao.ershou.model.entity.User;
import com.xuchao.ershou.model.vo.RecycleMethodVO;
import com.xuchao.ershou.service.RecycleMethodService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商品交易方式服务实现类
 */
@Service
public class RecycleMethodServiceImpl implements RecycleMethodService {

    @Autowired
    private RecycleMethodMapper recycleMethodMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    @Transactional
    public RecycleMethodVO addRecycleMethod(Long userId, RecycleMethodAddDao methodAddDao) {
        // 参数校验
        if (methodAddDao == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "交易方式信息不能为空");
        }
        
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        // 检查用户权限（只有管理员可以添加交易方式）
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        
        if (!"系统管理员".equals(user.getRole())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限添加交易方式");
        }
        
        // 检查方式名称是否已存在
        String methodName = methodAddDao.getMethodName();
        int existCount = recycleMethodMapper.checkMethodNameExists(methodName);
        if (existCount > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "方式名称已存在");
        }
        
        // 创建交易方式对象并设置属性
        RecycleMethod recycleMethod = new RecycleMethod();
        recycleMethod.setMethodName(methodAddDao.getMethodName());
        recycleMethod.setMethodDesc(methodAddDao.getMethodDesc());
        
        // 插入数据库
        int result = recycleMethodMapper.insertRecycleMethod(recycleMethod);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "添加交易方式失败");
        }
        
        // 转换为视图对象返回
        RecycleMethodVO methodVO = new RecycleMethodVO();
        BeanUtils.copyProperties(recycleMethod, methodVO);
        
        return methodVO;
    }
}