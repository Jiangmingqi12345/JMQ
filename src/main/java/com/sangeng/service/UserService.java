package com.sangeng.service;

import com.sangeng.pojo.User;
import java.util.List;

/**
 * 用户服务接口
 * 定义了用户相关的业务逻辑方法
 * 在SSM架构中，Service层负责处理业务逻辑，调用DAO层进行数据操作
 */
public interface UserService {
    
    /**
     * 用户登录方法
     * @param username 用户名
     * @param password 密码
     * @return User对象，如果登录成功则返回用户信息，否则返回null
     * 业务逻辑：根据用户名和密码查询用户，验证登录凭证
     */
    User login(String username, String password);
    
    /**
     * 根据用户ID查询用户信息
     * @param id 用户ID
     * @return User对象，包含用户的详细信息
     */
    User findById(Integer id);
    
    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return User对象，包含用户的详细信息
     * 业务逻辑：用于验证用户名是否已存在，或根据用户名获取用户信息
     */
    User findByUsername(String username);
    
    /**
     * 用户注册方法
     * @param user 用户对象，包含注册信息
     * @return boolean，注册成功返回true，失败返回false
     * 业务逻辑：检查用户名是否已存在，密码加密，保存用户信息
     */
    boolean register(User user);
    
    /**
     * 更新用户密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return boolean，更新成功返回true，失败返回false
     * 业务逻辑：验证旧密码是否正确，加密新密码，更新密码信息
     */
    boolean updatePassword(Integer userId, String oldPassword, String newPassword);
    
    /**
     * 更新用户信息
     * @param user 用户对象，包含更新后的用户信息
     * @return boolean，更新成功返回true，失败返回false
     * 业务逻辑：更新用户的基本信息，但不包括密码
     */
    boolean updateUserInfo(User user);
    
    /**
     * 查询所有用户
     * @return List<User>，包含所有用户的列表
     * 业务逻辑：用于管理员查看所有用户信息
     */
    List<User> findAll();
    
    /**
     * 根据角色查询用户
     * @param role 角色类型（student-学生，teacher-教师，admin-管理员）
     * @return List<User>，包含指定角色的用户列表
     * 业务逻辑：用于按角色筛选用户，如查看所有学生或所有教师
     */
    List<User> findByRole(String role);
    
    /**
     * 删除用户
     * @param userId 用户ID
     * @return boolean，删除成功返回true，失败返回false
     * 业务逻辑：根据用户ID删除用户信息，通常只有管理员有权限
     */
    boolean deleteUser(Integer userId);
}