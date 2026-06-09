package com.sangeng.mapper;

import com.sangeng.pojo.Selection;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 选题数据访问接口
 * 作用：定义选题数据的CRUD操作
 * 说明：对应数据库中的selection表，由MyBatis自动生成实现类
 */
public interface SelectionDao {
    /**
     * 根据学生ID和项目ID查询选题记录
     * @param studentId 学生ID
     * @param projectId 项目ID
     * @return 选题对象，如果找不到返回null，用于检查学生是否已选该项目
     */
    Selection findByStudentProject(@Param("studentId") Integer studentId, @Param("projectId") Integer projectId);
    
    /**
     * 插入选题记录
     * @param s 选题对象，包含学生ID、项目ID和申请理由等信息
     * @return 影响的行数，1表示成功，0表示失败
     */
    int insert(Selection s);
    
    /**
     * 更新选题状态
     * @param id 选题ID
     * @param status 新状态，如PENDING、APPROVED、REJECTED
     * @return 影响的行数，1表示成功，0表示失败
     */
    int updateStatus(@Param("id") Integer id, @Param("status") String status);
    
    /**
     * 根据学生ID查询所有选题记录
     * @param studentId 学生ID
     * @return 选题列表，包含该学生的所有选题申请
     */
    List<Selection> findByStudent(@Param("studentId") Integer studentId);
    
    /**
     * 根据ID查询选题记录
     * @param id 选题ID
     * @return 选题对象，如果找不到返回null
     */
    Selection findById(@Param("id") Integer id);
    
    /**
     * 根据项目ID查询所有选题记录
     * @param projectId 项目ID
     * @return 选题列表，包含该项目的所有申请
     */
    List<Selection> findByProject(@Param("projectId") Integer projectId);
    
    /**
     * 根据教师ID查询所有相关选题记录
     * @param teacherId 教师ID
     * @return 选题列表，包含该教师负责项目的所有选题申请
     */
    List<Selection> findByTeacher(@Param("teacherId") Integer teacherId);
    
    /**
     * 根据状态查询所有选题记录
     * @param status 选题状态，如PENDING、APPROVED、REJECTED
     * @return 选题列表，包含所有该状态的选题申请
     */
    List<Selection> findAllByStatus(@Param("status") String status);
    
    /**
     * 查询学生是否有已批准的选题记录
     * @param studentId 学生ID
     * @return 已批准的选题记录，如果没有返回null
     */
    Selection findApprovedByStudent(@Param("studentId") Integer studentId);
    
    /**
     * 查询学生是否有待审核的选题记录
     * @param studentId 学生ID
     * @return 待审核的选题记录列表
     */
    List<Selection> findPendingByStudent(@Param("studentId") Integer studentId);
    
    /**
     * 更新学生的选题申请
     * @param studentId 学生ID
     * @param projectId 项目ID
     * @param reason 申请理由
     * @param selectTime 申请时间
     * @return 影响的行数
     */
    int updateSelection(@Param("studentId") Integer studentId, @Param("projectId") Integer projectId, @Param("reason") String reason, @Param("selectTime") Date selectTime);
    
    /**
     * 查询所有选题记录
     * @return 选题列表，包含系统中所有选题申请
     */
    List<Selection> findAll();
    
    /**
     * 根据ID删除选题记录
     * @param id 选题ID
     * @return 影响的行数，1表示成功，0表示失败
     */
    int deleteById(@Param("id") Integer id);
    
    /**
     * 根据项目ID删除所有相关选题记录
     * @param projectId 项目ID
     * @return 影响的行数，成功时返回删除的记录数
     */
    int deleteByProjectId(@Param("projectId") Integer projectId);
}