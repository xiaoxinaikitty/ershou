package com.xuchao.ershou.service.impl;

import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.mapper.OrderAddressMapper;
import com.xuchao.ershou.mapper.OrderMapper;
import com.xuchao.ershou.mapper.OrderStatusHistoryMapper;
import com.xuchao.ershou.mapper.ProductMapper;
import com.xuchao.ershou.mapper.UserMapper;
import com.xuchao.ershou.model.dao.order.OrderCreateDao;
import com.xuchao.ershou.model.entity.Order;
import com.xuchao.ershou.model.entity.OrderAddress;
import com.xuchao.ershou.model.entity.OrderStatusHistory;
import com.xuchao.ershou.model.entity.Product;
import com.xuchao.ershou.model.entity.User;
import com.xuchao.ershou.model.vo.OrderAddressVO;
import com.xuchao.ershou.model.vo.OrderVO;
import com.xuchao.ershou.service.OrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderAddressMapper orderAddressMapper;
    
    @Autowired
    private OrderStatusHistoryMapper orderStatusHistoryMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    @Transactional
    public OrderVO createOrder(Long userId, OrderCreateDao orderCreateDao) {
        // 参数校验
        if (orderCreateDao == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "订单信息不能为空");
        }
        
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        // 检查商品是否存在且在售
        Product product = productMapper.selectProductById(orderCreateDao.getProductId());
        if (product == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "商品不存在");
        }
        
        if (product.getStatus() != 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品已下架或已售出");
        }
        
        // 检查卖家是否存在
        User seller = userMapper.selectById(orderCreateDao.getSellerId());
        if (seller == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "卖家不存在");
        }
        
        // 校验商品价格是否合理（前端传来的金额与数据库中的商品价格是否一致）
        if (product.getPrice().compareTo(orderCreateDao.getOrderAmount()) != 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品价格有变动，请刷新页面");
        }
        
        // 生成唯一订单号 (时间戳+随机数)
        String orderNo = generateOrderNo();
        
        // 创建订单对象并设置属性
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setSellerId(orderCreateDao.getSellerId());
        order.setProductId(orderCreateDao.getProductId());
        order.setOrderAmount(orderCreateDao.getOrderAmount());
        order.setPaymentAmount(orderCreateDao.getPaymentAmount());
        order.setOrderStatus(0); // 初始状态：待付款
        order.setPaymentType(orderCreateDao.getPaymentType());
        order.setDeliveryType(orderCreateDao.getDeliveryType());
        order.setDeliveryFee(orderCreateDao.getDeliveryFee());
        order.setRemark(orderCreateDao.getRemark());
        
        // 保存订单到数据库
        int result = orderMapper.insertOrder(order);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建订单失败");
        }
        
        // 保存订单收货地址
        OrderAddress orderAddress = new OrderAddress();
        orderAddress.setOrderId(order.getOrderId());
        orderAddress.setReceiverName(orderCreateDao.getAddress().getReceiverName());
        orderAddress.setReceiverPhone(orderCreateDao.getAddress().getReceiverPhone());
        orderAddress.setProvince(orderCreateDao.getAddress().getProvince());
        orderAddress.setCity(orderCreateDao.getAddress().getCity());
        orderAddress.setDistrict(orderCreateDao.getAddress().getDistrict());
        orderAddress.setDetailAddress(orderCreateDao.getAddress().getDetailAddress());
        
        result = orderAddressMapper.insertOrderAddress(orderAddress);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存收货地址失败");
        }
        
        // 记录订单状态变更历史
        OrderStatusHistory statusHistory = new OrderStatusHistory();
        statusHistory.setOrderId(order.getOrderId());
        statusHistory.setPreviousStatus(null); // 新创建的订单没有前一状态
        statusHistory.setCurrentStatus(0); // 待付款
        statusHistory.setOperatorId(userId);
        statusHistory.setOperatorType(1); // 买家操作
        statusHistory.setRemark("创建订单");
        
        result = orderStatusHistoryMapper.insertOrderStatusHistory(statusHistory);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "记录订单状态变更失败");
        }
        
        // 更新商品状态为已售（如果是线下交易的话，可以直接修改状态）
        if (orderCreateDao.getPaymentType() == 2) {
            product.setStatus(2); // 已售
            productMapper.updateProduct(product);
        }
        
        // 转换为视图对象返回
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        
        // 设置商品标题
        orderVO.setProductTitle(product.getTitle());
        
        // 设置地址信息
        OrderAddressVO addressVO = new OrderAddressVO();
        BeanUtils.copyProperties(orderAddress, addressVO);
        orderVO.setAddress(addressVO);
        
        return orderVO;
    }
    
    /**
     * 生成唯一订单号
     * 格式：年月日时分秒+6位随机数
     * @return 订单号
     */
    private String generateOrderNo() {
        LocalDateTime now = LocalDateTime.now();
        String timeStr = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        
        // 生成6位随机数
        String randomStr = String.valueOf((int)((Math.random() * 9 + 1) * 100000));
        
        return timeStr + randomStr;
    }
}