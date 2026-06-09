package com.sangeng.mapper;

import com.sangeng.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户数据访问接口
 * 作用：定义用户数据的CRUD操作
 * 说明：对应数据库中的user表，由MyBatis自动生成实现类
 */
public interface UserDao {
    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户对象，如果找不到返回null
     */
    User findById(Integer id);
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户对象，如果找不到返回null
     */
    User findByUsername(@Param("username") String username);
    
    /**
     * 插入用户
     * @param user 用户对象，包含要插入的用户信息
     * @return 影响的行数，1表示成功，0表示失败
     */
    int insert(User user);
    
    /**
     * 更新用户信息
     * @param user 用户对象，包含要更新的用户信息
     * @return 影响的行数，1表示成功，0表示失败
     */
    int update(User user);
    
    /**
     * 查询所有用户
     * @return 用户列表，包含所有用户信息
     */
    List<User> findAll();
    
    /**
     * 根据角色查询用户
     * @param role 角色名称，如STUDENT、TEACHER、ADMIN
     * @return 用户列表，包含指定角色的所有用户
     */
    List<User> findByRole(@Param("role") String role);
    
    /**
     * 根据ID删除用户
     * @param id 用户ID
     * @return 影响的行数，1表示成功，0表示失败
     */
    int deleteById(@Param("id") Integer id);
}