package com.sangeng.pojo;

import java.util.Date;

/**
 * 用户实体类
 * 作用：表示系统中的用户信息，包含学生、教师、管理员三种角色
 * 说明：对应数据库中的user表
 */
public class User {
    /**
     * 用户ID
     * 作用：唯一标识一个用户
     * 说明：数据库主键，自增生成
     */
    private Integer id;
    
    /**
     * 用户名
     * 作用：用户登录时使用的账号
     * 说明：系统内唯一，不允许重复
     */
    private String username;
    
    /**
     * 密码
     * 作用：用户登录时使用的密码
     * 说明：存储在数据库中应该加密，当前项目未加密处理
     */
    private String password;
    
    /**
     * 角色
     * 作用：标识用户的角色类型
     * 说明：取值范围为 STUDENT（学生）、TEACHER（教师）、ADMIN（管理员）
     */
    private String role; // STUDENT / TEACHER / ADMIN
    
    /**
     * 真实姓名
     * 作用：用户的真实姓名
     * 说明：用于显示和身份标识
     */
    private String name;
    
    /**
     * 邮箱
     * 作用：用户的联系方式
     * 说明：用于通知和验证
     */
    private String email;
    
    /**
     * 专业
     * 作用：用户所属的专业
     * 说明：主要用于学生用户，教师和管理员可能为空
     */
    private String major;
    
    /**
     * 创建时间
     * 作用：记录用户账号的创建时间
     * 说明：自动生成，不需要手动设置
     */
    private Date createTime;
    
    /**
     * 获取真实姓名
     * @return 真实姓名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置真实姓名
     * @param name 真实姓名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取用户ID
     * @return 用户ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置用户ID
     * @param id 用户ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取用户名
     * @return 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取密码
     * @return 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取角色
     * @return 角色
     */
    public String getRole() {
        return role;
    }

    /**
     * 设置角色
     * @param role 角色
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * 获取邮箱
     * @return 邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置邮箱
     * @param email 邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取专业
     * @return 专业
     */
    public String getMajor() {
        return major;
    }

    /**
     * 设置专业
     * @param major 专业
     */
    public void setMajor(String major) {
        this.major = major;
    }

    /**
     * 获取创建时间
     * @return 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}

