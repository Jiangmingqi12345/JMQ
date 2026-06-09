package com.sangeng.mapper;

import com.sangeng.pojo.Project;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 项目数据访问接口
 * 作用：定义项目数据的CRUD操作
 * 说明：对应数据库中的project表，由MyBatis自动生成实现类
 */
public interface ProjectDao {
    /**
     * 根据ID查询项目
     * @param id 项目ID
     * @return 项目对象，如果找不到返回null
     */
    Project findById(Integer id);
    
    /**
     * 查询所有开放的项目，支持分页和名称搜索
     * @param offset 分页偏移量，从0开始
     * @param limit 每页条数
     * @param name 项目名称搜索关键字，可为空
     * @return 项目列表，包含符合条件的开放项目
     */
    List<Project> findAllOpen(@Param("offset") int offset, @Param("limit") int limit, @Param("name") String name);
    
    /**
     * 插入项目
     * @param p 项目对象，包含要插入的项目信息
     * @return 影响的行数，1表示成功，0表示失败
     */
    int insert(Project p);
    
    /**
     * 更新项目信息
     * @param p 项目对象，包含要更新的项目信息
     * @return 影响的行数，1表示成功，0表示失败
     */
    int update(Project p);
    
    /**
     * 增加项目的已选学生数
     * @param projectId 项目ID
     * @return 影响的行数，1表示成功，0表示失败
     */
    int increaseSelectedCount(@Param("projectId") Integer projectId);
    
    /**
     * 根据ID删除项目
     * @param id 项目ID
     * @return 影响的行数，1表示成功，0表示失败
     */
    int deleteById(@Param("id") Integer id);
    
    /**
     * 根据教师ID查询项目
     * @param teacherId 教师ID
     * @return 项目列表，包含该教师发布的所有项目
     */
    List<Project> findByTeacher(@Param("teacherId") Integer teacherId);
    
    /**
     * 查询所有项目，支持分页和名称搜索
     * @param offset 分页偏移量，从0开始
     * @param limit 每页条数
     * @param name 项目名称搜索关键字，可为空
     * @return 项目列表，包含符合条件的所有项目
     */
    List<Project> findAll(@Param("offset") int offset, @Param("limit") int limit, @Param("name") String name);
    
    /**
     * 更新项目状态
     * @param id 项目ID
     * @param status 项目状态，0-未发布，1-已发布，2-已结束
     * @return 影响的行数，1表示成功，0表示失败
     */
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
    
    /**
     * 更新项目的已选学生数
     * @param id 项目ID
     * @param delta 变化量，可以是正数（增加）或负数（减少）
     * @return 影响的行数，1表示成功，0表示失败
     */
    int updateSelectedCount(@Param("id") Integer id, @Param("delta") Integer delta);
}