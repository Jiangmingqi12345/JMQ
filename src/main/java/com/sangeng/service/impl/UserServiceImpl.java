package com.sangeng.service.impl;

import com.sangeng.mapper.UserDao;
import com.sangeng.pojo.User;
import com.sangeng.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * 用户服务实现类
 * 实现了UserService接口中定义的所有方法
 * @Service注解：将该类标记为Spring的服务组件，由Spring容器管理
 */
@Service
public class UserServiceImpl implements UserService {
    
    /**
     * @Autowired注解：自动注入UserDao实例
     * 在SSM架构中，Service层通过依赖注入调用DAO层的方法
     */
    @Autowired
    private UserDao userDao;
    
    /**
     * 用户登录方法实现
     * @param username 用户名
     * @param password 密码
     * @return User对象，如果登录成功返回用户信息，否则返回null
     * 业务逻辑：
     * 1. 根据用户名查询用户信息
     * 2. 验证密码是否匹配
     * 3. 返回验证结果
     */
    @Override
    public User login(String username, String password) {
        // 调用DAO层方法，根据用户名查询用户
        User u = userDao.findByUsername(username);
        // 验证用户是否存在且密码匹配
        if (u != null && u.getPassword().equals(password)) return u;
        return null;
    }
    
    /**
     * 根据用户ID查询用户信息实现
     * @param id 用户ID
     * @return User对象，包含用户详细信息
     */
    @Override
    public User findById(Integer id){ 
        // 直接调用DAO层的findById方法
        return userDao.findById(id); 
    }
    
    /**
     * 根据用户名查询用户信息实现
     * @param username 用户名
     * @return User对象，包含用户详细信息
     */
    @Override
    public User findByUsername(String username) {
        // 直接调用DAO层的findByUsername方法
        return userDao.findByUsername(username);
    }
    
    /**
     * 用户注册方法实现
     * @param user 用户对象，包含注册信息
     * @return boolean，注册成功返回true，失败返回false
     * @Transactional注解：标记该方法需要事务管理
     * 业务逻辑：
     * 1. 检查用户名是否已存在
     * 2. 设置默认角色（如果未指定）
     * 3. 设置创建时间
     * 4. 插入用户信息到数据库
     */
    @Transactional
    @Override
    public boolean register(User user) {
        // 检查用户名是否已存在
        if (userDao.findByUsername(user.getUsername()) != null) {
            return false;
        }
        // 设置默认值
        if (user.getRole() == null) {
            user.setRole("STUDENT"); // 默认学生角色
        }
        // 设置创建时间为当前时间
        user.setCreateTime(new java.util.Date());
        // 调用DAO层插入方法，返回影响行数
        return userDao.insert(user) > 0;
    }
    
    /**
     * 更新用户密码方法实现
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return boolean，更新成功返回true，失败返回false
     * @Transactional注解：标记该方法需要事务管理
     * 业务逻辑：
     * 1. 根据用户ID查询用户信息
     * 2. 验证旧密码是否正确
     * 3. 更新密码
     * 4. 保存更新后的用户信息
     */
    @Transactional
    @Override
    public boolean updatePassword(Integer userId, String oldPassword, String newPassword) {
        // 根据用户ID查询用户信息
        User user = userDao.findById(userId);
        // 检查用户是否存在
        if (user == null) {
            return false;
        }
        // 验证旧密码是否正确
        if (!user.getPassword().equals(oldPassword)) {
            return false;
        }
        // 设置新密码
        user.setPassword(newPassword);
        // 调用DAO层更新方法，返回影响行数
        return userDao.update(user) > 0;
    }
    
    /**
     * 更新用户信息方法实现
     * @param user 用户对象，包含更新后的用户信息
     * @return boolean，更新成功返回true，失败返回false
     * @Transactional注解：标记该方法需要事务管理
     * 业务逻辑：
     * 1. 根据用户ID查询现有用户信息
     * 2. 检查用户是否存在
     * 3. 复制允许修改的字段到现有用户对象
     * 4. 保存更新后的用户信息
     */
    @Transactional
    @Override
    public boolean updateUserInfo(User user) {
        // 只更新允许修改的字段，先查询现有用户信息
        User existingUser = userDao.findById(user.getId());
        // 检查用户是否存在
        if (existingUser == null) {
            return false;
        }
        // 复制可修改字段到现有用户对象
        existingUser.setName(user.getName()); // 更新姓名
        existingUser.setEmail(user.getEmail()); // 更新邮箱
        existingUser.setMajor(user.getMajor()); // 更新专业
        existingUser.setRole(user.getRole()); // 更新角色
        // 如果提供了密码，更新密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(user.getPassword());
        }
        // 调用DAO层更新方法，返回影响行数
        return userDao.update(existingUser) > 0;
    }
    
    /**
     * 查询所有用户方法实现
     * @return List<User>，包含所有用户的列表
     */
    @Override
    public List<User> findAll() {
        // 直接调用DAO层的findAll方法
        return userDao.findAll();
    }
    
    /**
     * 根据角色查询用户方法实现
     * @param role 角色类型（STUDENT-学生，TEACHER-教师，ADMIN-管理员）
     * @return List<User>，包含指定角色的用户列表
     */
    @Override
    public List<User> findByRole(String role) {
        // 直接调用DAO层的findByRole方法
        return userDao.findByRole(role);
    }
    
    /**
     * 删除用户方法实现
     * @param userId 用户ID
     * @return boolean，删除成功返回true，失败返回false
     * @Transactional注解：标记该方法需要事务管理
     * 注意：当前该方法尚未实现，暂时返回false
     */
    @Transactional
    @Override
    public boolean deleteUser(Integer userId) {
        // 这里需要扩展UserDao，暂时返回false
        return false;
    }
}

