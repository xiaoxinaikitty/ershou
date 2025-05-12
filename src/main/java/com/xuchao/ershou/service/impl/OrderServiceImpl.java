package com.xuchao.ershou.service.impl;

import com.xuchao.ershou.common.ErrorCode;
import com.xuchao.ershou.exception.BusinessException;
import com.xuchao.ershou.mapper.*;
import com.xuchao.ershou.model.dao.order.OrderCreateDao;
import com.xuchao.ershou.model.dao.order.OrderCancelDao;
import com.xuchao.ershou.model.dto.OrderConfirmReceiptRequest;
import com.xuchao.ershou.model.dto.OrderNotifyShipmentRequest;
import com.xuchao.ershou.model.dto.OrderPayRequest;
import com.xuchao.ershou.model.entity.*;
import com.xuchao.ershou.model.vo.OrderAddressVO;
import com.xuchao.ershou.model.vo.OrderVO;
import com.xuchao.ershou.service.OrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    
    @Autowired
    private TransactionRecordMapper transactionRecordMapper;
    
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
    
    @Override
    public List<OrderVO> getOrderList(Long userId) {
        // 查询用户的订单列表
        List<Order> orders = orderMapper.selectOrdersByUserId(userId);

        // 转换为视图对象
        return orders.stream().map(order -> {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(order, orderVO);

            // 查询并设置订单地址
            OrderAddress orderAddress = orderAddressMapper.selectByOrderId(order.getOrderId());
            if (orderAddress != null) {
                OrderAddressVO addressVO = new OrderAddressVO();
                BeanUtils.copyProperties(orderAddress, addressVO);
                orderVO.setAddress(addressVO);
            }

            return orderVO;
        }).collect(Collectors.toList());
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

    @Override
    @Transactional
    public OrderVO cancelOrder(Long userId, OrderCancelDao orderCancelDao) {
        // 参数校验
        if (orderCancelDao == null || orderCancelDao.getOrderId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "订单信息不能为空");
        }
        
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户未登录");
        }
        
        // 查询订单是否存在
        Order order = orderMapper.selectOrderById(orderCancelDao.getOrderId());
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "订单不存在");
        }
        
        // 验证是否是买家本人操作
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权操作此订单");
        }
        
        // 检查订单状态是否可取消（只有待付款、待发货状态可以取消）
        if (order.getOrderStatus() != 0 && order.getOrderStatus() != 1) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "当前订单状态不可取消");
        }
        
        // 取消订单，更新状态为已取消
        Integer previousStatus = order.getOrderStatus();
        order.setOrderStatus(4); // 4表示已取消
        int result = orderMapper.updateOrder(order);
        
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "取消订单失败");
        }
        
        // 如果订单已支付，还需处理退款逻辑（这里暂不实现）
        
        // 记录订单状态变更历史
        OrderStatusHistory statusHistory = new OrderStatusHistory();
        statusHistory.setOrderId(order.getOrderId());
        statusHistory.setPreviousStatus(previousStatus);
        statusHistory.setCurrentStatus(4); // 已取消
        statusHistory.setOperatorId(userId);
        statusHistory.setOperatorType(1); // 买家操作
        statusHistory.setRemark(orderCancelDao.getRemark()); // 取消原因
        
        result = orderStatusHistoryMapper.insertOrderStatusHistory(statusHistory);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "记录订单状态变更失败");
        }
        
        // 订单取消后，需要恢复商品状态为在售
        Product product = productMapper.selectProductById(order.getProductId());
        if (product != null && product.getStatus() == 2) {
            product.setStatus(1); // 改回在售状态
            productMapper.updateProduct(product);
        }
        
        // 转换为视图对象返回
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        
        // 设置商品标题
        if (product != null) {
            orderVO.setProductTitle(product.getTitle());
        }
        
        // 查询并设置订单地址
        OrderAddress orderAddress = orderAddressMapper.selectByOrderId(order.getOrderId());
        if (orderAddress != null) {
            OrderAddressVO addressVO = new OrderAddressVO();
            BeanUtils.copyProperties(orderAddress, addressVO);
            orderVO.setAddress(addressVO);
        }
        
        return orderVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO payOrder(OrderPayRequest request) {
        // 1. 参数校验
        if (request == null || request.getOrderId() == null || request.getUserId() == null 
                || request.getPaymentType() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 2. 获取订单信息
        Order order = orderMapper.selectOrderById(request.getOrderId());
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "订单不存在");
        }
        
        // 3. 验证订单状态
        if (order.getOrderStatus() != 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "订单状态错误");
        }
        
        // 4. 验证订单所属用户
        if (!order.getUserId().equals(request.getUserId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        // 5. 更新订单状态
        order.setOrderStatus(1); // 更新为待发货状态
        order.setPaymentType(request.getPaymentType());
        order.setPaymentTime(LocalDateTime.now());
        orderMapper.updateOrder(order);
        
        // 6. 记录订单状态变更
        OrderStatusHistory statusHistory = new OrderStatusHistory();
        statusHistory.setOrderId(order.getOrderId());
        statusHistory.setPreviousStatus(0);
        statusHistory.setCurrentStatus(1);
        statusHistory.setOperatorId(request.getUserId());
        statusHistory.setOperatorType(1);
        statusHistory.setRemark("用户完成支付");
        orderStatusHistoryMapper.insertOrderStatusHistory(statusHistory);
        
        // 7. 记录交易流水
        TransactionRecord transaction = new TransactionRecord();
        transaction.setOrderId(order.getOrderId());
        transaction.setUserId(request.getUserId());
        transaction.setTransactionType(1);
        transaction.setTransactionNo(request.getTransactionNo());
        transaction.setAmount(order.getPaymentAmount());
        transaction.setStatus(1);
        transaction.setPaymentChannel(request.getPaymentChannel());
        transaction.setRemark("订单支付");
        transaction.setCreatedTime(LocalDateTime.now());
        transactionRecordMapper.insertTransactionRecord(transaction);
        
        // 8. 返回更新后的订单信息
        return convertOrderToVO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO confirmReceipt(OrderConfirmReceiptRequest request) {
        // 1. 参数校验
        if (request == null || request.getOrderId() == null || request.getUserId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 2. 获取订单信息
        Order order = orderMapper.selectOrderById(request.getOrderId());
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "订单不存在");
        }
        
        // 3. 验证订单状态
        if (order.getOrderStatus() != 2) { // 只有待收货状态的订单可以确认收货
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "当前订单状态无法确认收货");
        }
        
        // 4. 验证订单所属用户
        if (!order.getUserId().equals(request.getUserId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        // 5. 更新订单状态
        Integer previousStatus = order.getOrderStatus();
        order.setOrderStatus(3); // 更新为已完成状态
        order.setReceivedTime(LocalDateTime.now());
        orderMapper.updateOrder(order);
        
        // 6. 记录订单状态变更
        OrderStatusHistory statusHistory = new OrderStatusHistory();
        statusHistory.setOrderId(order.getOrderId());
        statusHistory.setPreviousStatus(previousStatus);
        statusHistory.setCurrentStatus(3);
        statusHistory.setOperatorId(request.getUserId());
        statusHistory.setOperatorType(1); // 买家操作
        statusHistory.setRemark("买家确认收货");
        orderStatusHistoryMapper.insertOrderStatusHistory(statusHistory);
        
        // 7. 返回更新后的订单信息
        return convertOrderToVO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO notifyShipment(OrderNotifyShipmentRequest request) {
        // 1. 参数校验
        if (request == null || request.getOrderId() == null || request.getSellerId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 2. 获取订单信息
        Order order = orderMapper.selectOrderById(request.getOrderId());
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "订单不存在");
        }
        
        // 3. 验证订单状态
        if (order.getOrderStatus() != 1) { // 只有待发货状态的订单可以通知收货
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "当前订单状态无法发货");
        }
        
        // 4. 验证卖家权限
        if (!order.getSellerId().equals(request.getSellerId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权操作此订单");
        }
        
        // 5. 更新订单状态
        Integer previousStatus = order.getOrderStatus();
        order.setOrderStatus(2); // 更新为待收货状态
        order.setDeliveryTime(LocalDateTime.now());
        orderMapper.updateOrder(order);
        
        // 6. 记录订单状态变更
        OrderStatusHistory statusHistory = new OrderStatusHistory();
        statusHistory.setOrderId(order.getOrderId());
        statusHistory.setPreviousStatus(previousStatus);
        statusHistory.setCurrentStatus(2);
        statusHistory.setOperatorId(request.getSellerId());
        statusHistory.setOperatorType(2); // 卖家操作
        statusHistory.setRemark("卖家已发货");
        orderStatusHistoryMapper.insertOrderStatusHistory(statusHistory);
        
        // 7. 返回更新后的订单信息
        return convertOrderToVO(order);
    }

    /**
     * 将订单实体转换为视图对象
     */
    private OrderVO convertOrderToVO(Order order) {
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        
        // 设置商品标题
        Product product = productMapper.selectProductById(order.getProductId());
        if (product != null) {
            orderVO.setProductTitle(product.getTitle());
        }
        
        // 设置地址信息
        OrderAddress orderAddress = orderAddressMapper.selectByOrderId(order.getOrderId());
        if (orderAddress != null) {
            OrderAddressVO addressVO = new OrderAddressVO();
            BeanUtils.copyProperties(orderAddress, addressVO);
            orderVO.setAddress(addressVO);
        }
        
        return orderVO;
    }

    @Override
    public int countOrdersByStatus(Long userId, Integer status) {
        // 使用现有的orderMapper实现订单数量统计
        return orderMapper.countOrdersByUserIdAndStatus(userId, status);
    }
}