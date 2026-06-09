package com.sangeng.service;

import com.sangeng.pojo.Project;
import java.util.List;

/**
 * 项目服务接口
 * 定义了项目相关的业务逻辑方法
 * 在SSM架构中，Service层负责处理业务逻辑，调用DAO层进行数据操作
 */
public interface ProjectService {
    
    /**
     * 分页查询开放状态的项目，支持按名称搜索
     * @param page 当前页码，从1开始
     * @param size 每页显示的记录数
     * @param name 项目名称关键词，用于模糊搜索
     * @return List<Project>，包含符合条件的项目列表
     * 业务逻辑：查询状态为开放的项目，支持分页和模糊搜索
     */
    List<Project> listOpen(int page, int size, String name);
    
    /**
     * 根据项目ID查询项目详情
     * @param id 项目ID
     * @return Project对象，包含项目的详细信息
     * 业务逻辑：根据主键ID查询项目的完整信息
     */
    Project getById(Integer id);
    
    /**
     * 创建新项目
     * @param p Project对象，包含项目的创建信息
     * @return boolean，创建成功返回true，失败返回false
     * 业务逻辑：教师发布新项目，设置初始状态和已选人数
     */
    boolean create(Project p);
    
    /**
     * 更新项目信息
     * @param p Project对象，包含更新后的项目信息
     * @return boolean，更新成功返回true，失败返回false
     * 业务逻辑：修改项目的基本信息，如标题、描述、最大学生数等
     */
    boolean update(Project p);
    
    /**
     * 删除项目
     * @param id 项目ID
     * @return boolean，删除成功返回true，失败返回false
     * 业务逻辑：删除指定ID的项目，通常需要先检查是否有学生已选
     */
    boolean delete(Integer id);
    
    /**
     * 根据教师ID查询该教师发布的所有项目
     * @param teacherId 教师ID
     * @return List<Project>，包含该教师发布的所有项目
     * 业务逻辑：用于教师查看自己发布的项目列表
     */
    List<Project> findByTeacher(Integer teacherId);
    
    /**
     * 分页查询所有项目，支持按名称搜索
     * @param page 当前页码，从1开始
     * @param size 每页显示的记录数
     * @param name 项目名称关键词，用于模糊搜索
     * @return List<Project>，包含符合条件的所有项目
     * 业务逻辑：用于管理员查看所有项目，支持分页和模糊搜索
     */
    List<Project> findAll(int page, int size, String name);
    
    /**
     * 更新项目状态
     * @param projectId 项目ID
     * @param status 状态值（0-关闭，1-开放）
     * @return boolean，更新成功返回true，失败返回false
     * 业务逻辑：开启或关闭项目的选题功能
     */
    boolean updateStatus(Integer projectId, Integer status);
    
    /**
     * 更新项目的已选人数
     * @param projectId 项目ID
     * @param delta 变化量（正数表示增加，负数表示减少）
     * @return boolean，更新成功返回true，失败返回false
     * 业务逻辑：当学生选题或取消选题时，更新项目的已选人数
     */
    boolean updateSelectedCount(Integer projectId, Integer delta);
}