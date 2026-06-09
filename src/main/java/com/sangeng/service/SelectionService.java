package com.sangeng.service;

import com.sangeng.pojo.Selection;
import java.util.List;

/**
 * 选题服务接口
 * 定义了选题相关的业务逻辑方法
 * 在SSM架构中，Service层负责处理业务逻辑，调用DAO层进行数据操作
 */
public interface SelectionService {
    
    /**
     * 学生申请选题
     * @param studentId 学生ID
     * @param projectId 项目ID
     * @param reason 选题理由
     * @return boolean，申请成功返回true，失败返回false
     * 业务逻辑：学生选择项目，创建选题记录，设置初始状态为待审核
     */
    boolean apply(Integer studentId, Integer projectId, String reason);
    
    /**
     * 教师批准选题申请
     * @param selectionId 选题记录ID
     * @return boolean，批准成功返回true，失败返回false
     * 业务逻辑：教师同意学生的选题申请，更新选题状态为已通过，并更新项目的已选人数
     */
    boolean approve(Integer selectionId);
    
    /**
     * 教师拒绝选题申请
     * @param selectionId 选题记录ID
     * @param reason 拒绝理由
     * @return boolean，拒绝成功返回true，失败返回false
     * 业务逻辑：教师拒绝学生的选题申请，更新选题状态为已拒绝，并记录拒绝理由
     */
    boolean reject(Integer selectionId, String reason);
    
    /**
     * 学生取消选题
     * @param selectionId 选题记录ID
     * @param studentId 学生ID
     * @return boolean，取消成功返回true，失败返回false
     * 业务逻辑：学生取消已申请的选题，删除选题记录，并更新项目的已选人数
     */
    boolean cancel(Integer selectionId, Integer studentId);
    
    /**
     * 根据选题ID查询选题详情
     * @param selectionId 选题记录ID
     * @return Selection对象，包含选题的详细信息
     */
    Selection findById(Integer selectionId);
    
    /**
     * 根据学生ID查询该学生的所有选题记录
     * @param studentId 学生ID
     * @return List<Selection>，包含该学生的所有选题记录
     */
    List<Selection> findByStudent(Integer studentId);
    
    /**
     * 根据教师ID查询该教师所有项目的选题记录
     * @param teacherId 教师ID
     * @return List<Selection>，包含该教师所有项目的选题记录
     */
    List<Selection> findByTeacher(Integer teacherId);
    
    /**
     * 根据项目ID查询该项目的所有选题记录
     * @param projectId 项目ID
     * @return List<Selection>，包含该项目的所有选题记录
     */
    List<Selection> findByProject(Integer projectId);
    
    /**
     * 根据状态查询所有选题记录
     * @param status 选题状态（0-待审核，1-通过，2-拒绝）
     * @return List<Selection>，包含指定状态的选题记录
     */
    List<Selection> findAllByStatus(String status);
    
    /**
     * 查询所有选题记录
     * @return List<Selection>，包含所有选题记录
     */
    List<Selection> findAll();
}