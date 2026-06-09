package com.sangeng.service.impl;

import com.sangeng.mapper.ProjectDao;
import com.sangeng.pojo.Project;
import com.sangeng.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

/**
 * 项目服务实现类
 * 实现了ProjectService接口中定义的所有方法
 * @Service注解：将该类标记为Spring的服务组件，由Spring容器管理
 */
@Service
public class ProjectServiceImpl implements ProjectService {
    
    /**
     * @Autowired注解：自动注入ProjectDao实例
     * 在SSM架构中，Service层通过依赖注入调用DAO层的方法
     */
    @Autowired
    private ProjectDao projectDao;
    
    /**
     * 分页查询开放状态的项目，支持按名称搜索
     * @param page 当前页码，从1开始
     * @param size 每页显示的记录数
     * @param name 项目名称关键词，用于模糊搜索
     * @return List<Project>，包含符合条件的项目列表
     * 业务逻辑：
     * 1. 计算分页偏移量offset = (page - 1) * size
     * 2. 调用DAO层的findAllOpen方法查询开放状态的项目
     */
    @Override
    public List<Project> listOpen(int page, int size, String name) {
        // 计算分页偏移量，用于SQL的LIMIT子句
        int offset = (page - 1) * size;
        // 调用DAO层方法，查询开放状态的项目
        return projectDao.findAllOpen(offset, size, name);
    }
    
    /**
     * 根据项目ID查询项目详情
     * @param id 项目ID
     * @return Project对象，包含项目的详细信息
     */
    @Override
    public Project getById(Integer id) {
        // 直接调用DAO层的findById方法
        return projectDao.findById(id);
    }
    
    /**
     * 创建新项目
     * @param p Project对象，包含项目的创建信息
     * @return boolean，创建成功返回true，失败返回false
     * @Transactional注解：标记该方法需要事务管理
     * 业务逻辑：
     * 1. 设置项目的默认值（如果未提供）
     * 2. 设置项目的创建时间为当前时间
     * 3. 调用DAO层的insert方法插入项目信息
     */
    @Override
    @Transactional
    public boolean create(Project p) {
        // 设置默认状态：0-关闭，1-开放
        if (p.getStatus() == null) {
            p.setStatus(0);
        }
        // 设置默认最大学生数：1
        if (p.getMaxStudents() == null) {
            p.setMaxStudents(1);
        }
        // 设置默认已选人数：0
        if (p.getSelectedCount() == null) {
            p.setSelectedCount(0);
        }
        // 设置创建时间为当前时间
        p.setCreateTime(new Date());
        // 调用DAO层插入方法，返回影响行数
        return projectDao.insert(p) > 0;
    }
    
    /**
     * 更新项目信息
     * @param p Project对象，包含更新后的项目信息
     * @return boolean，更新成功返回true，失败返回false
     * @Transactional注解：标记该方法需要事务管理
     */
    @Override
    @Transactional
    public boolean update(Project p) {
        // 直接调用DAO层的update方法，返回影响行数
        return projectDao.update(p) > 0;
    }
    
    /**
     * 删除项目
     * @param id 项目ID
     * @return boolean，删除成功返回true，失败返回false
     * @Transactional注解：标记该方法需要事务管理
     */
    @Override
    @Transactional
    public boolean delete(Integer id) {
        // 调用DAO层的deleteById方法，返回影响行数
        return projectDao.deleteById(id) > 0;
    }
    
    /**
     * 根据教师ID查询该教师发布的所有项目
     * @param teacherId 教师ID
     * @return List<Project>，包含该教师发布的所有项目
     */
    @Override
    public List<Project> findByTeacher(Integer teacherId) {
        // 直接调用DAO层的findByTeacher方法
        return projectDao.findByTeacher(teacherId);
    }
    
    /**
     * 分页查询所有项目，支持按名称搜索
     * @param page 当前页码，从1开始
     * @param size 每页显示的记录数
     * @param name 项目名称关键词，用于模糊搜索
     * @return List<Project>，包含符合条件的所有项目
     * 业务逻辑：
     * 1. 计算分页偏移量offset = (page - 1) * size
     * 2. 调用DAO层的findAll方法查询所有项目
     */
    @Override
    public List<Project> findAll(int page, int size, String name) {
        // 计算分页偏移量，用于SQL的LIMIT子句
        int offset = (page - 1) * size;
        // 调用DAO层方法，查询所有项目
        return projectDao.findAll(offset, size, name);
    }
    
    /**
     * 更新项目状态
     * @param projectId 项目ID
     * @param status 状态值（0-关闭，1-开放）
     * @return boolean，更新成功返回true，失败返回false
     * @Transactional注解：标记该方法需要事务管理
     */
    @Override
    @Transactional
    public boolean updateStatus(Integer projectId, Integer status) {
        // 调用DAO层的updateStatus方法，返回影响行数
        return projectDao.updateStatus(projectId, status) > 0;
    }
    
    /**
     * 更新项目的已选人数
     * @param projectId 项目ID
     * @param delta 变化量（正数表示增加，负数表示减少）
     * @return boolean，更新成功返回true，失败返回false
     * @Transactional注解：标记该方法需要事务管理
     */
    @Override
    @Transactional
    public boolean updateSelectedCount(Integer projectId, Integer delta) {
        // 调用DAO层的updateSelectedCount方法，返回影响行数
        return projectDao.updateSelectedCount(projectId, delta) > 0;
    }
}