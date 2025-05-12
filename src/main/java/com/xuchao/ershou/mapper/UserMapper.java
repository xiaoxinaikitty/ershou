package com.xuchao.ershou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuchao.ershou.model.entity.User;
import com.xuchao.ershou.model.entity.UserAddress;
import com.xuchao.ershou.model.dao.user.UserPageQueryDao;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.time.LocalDate;
import java.util.Map;

public interface UserMapper extends BaseMapper<User> {
    int insertUser(User user);
    
    int countByUsername(String username);
    
    User selectUserByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
    User selectAdminByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
    
    int insertUserAddress(UserAddress userAddress);
    
    /**
     * 根据用户ID查询用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    User getUserById(@Param("userId") Long userId);
    
    /**
     * 根据手机号查询用户信息
     * @param phone 手机号
     * @return 用户信息
     */
    User getUserByPhone(@Param("phone") String phone);
    
    /**
     * 根据条件查询用户列表
     * @param queryParams 查询参数
     * @return 用户列表
     */
    List<User> listUsers(UserPageQueryDao queryParams);
    
    /**
     * 根据条件统计用户数量
     * @param queryParams 查询参数
     * @return 用户数量
     */
    Long countUsers(UserPageQueryDao queryParams);
    
    /**
     * 统计所有用户数量
     * @return 用户总数
     */
    Long countAllUsers();
    
    /**
     * 根据锁定状态统计用户数量
     * @param isLocked 是否锁定
     * @return 用户数量
     */
    Long countUsersByLockState(@Param("isLocked") boolean isLocked);
    
    /**
     * 根据角色统计用户数量
     * @param role 角色名称
     * @return 用户数量
     */
    Long countUsersByRole(@Param("role") String role);
    
    /**
     * 根据用户ID查询用户角色
     * @param userId 用户ID
     * @return 用户角色
     */
    String selectUserRoleById(@Param("userId") Long userId);

    /**
     * 获取用户注册趋势
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 注册趋势数据
     */
    List<Map<String, Object>> getUserRegisterTrend(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 获取活跃用户数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 活跃用户数据
     */
    List<Map<String, Object>> getActiveUserData(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 获取总用户数
     * @return 总用户数
     */
    Integer getTotalUserCount();

    /**
     * 获取活跃卖家数
     * @return 活跃卖家数
     */
    Integer getActiveSellUserCount();

    /**
     * 获取活跃买家数
     * @return 活跃买家数
     */
    Integer getActiveBuyUserCount();

    /**
     * 获取新增用户数
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 新增用户数
     */
    Integer getNewUserCount(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}