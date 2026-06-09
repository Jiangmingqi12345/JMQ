package com.sangeng.service;

import com.sangeng.pojo.Thesis;
import java.util.List;

/**
 * 论文服务接口
 * 定义了论文相关的业务逻辑方法
 * 在SSM架构中，Service层负责处理业务逻辑，调用DAO层进行数据操作
 */
public interface ThesisService {

    /**
     * 学生上传论文
     * @param thesis 论文对象，包含论文的基本信息和文件路径
     * @return int，返回影响的行数，1表示成功，0表示失败
     * 业务逻辑：学生上传论文，创建论文记录，设置初始状态为已上传
     */
    int uploadThesis(Thesis thesis);

    /**
     * 教师审核论文（重载方法1）
     * @param thesisId 论文ID
     * @param status 审核状态（PASS-通过，REJECT-拒绝）
     * @return int，返回影响的行数，1表示成功，0表示失败
     * 业务逻辑：教师审核论文，更新论文状态
     */
    int reviewThesis(Integer thesisId, String status);
    
    /**
     * 教师审核论文（重载方法2）
     * @param thesisId 论文ID
     * @param status 审核状态（PASS-通过，REJECT-拒绝）
     * @param reviewReason 审核理由
     * @return int，返回影响的行数，1表示成功，0表示失败
     * 业务逻辑：教师审核论文，更新论文状态并添加审核理由
     */
    int reviewThesis(Integer thesisId, String status, String reviewReason);
    
    /**
     * 教师审核论文（重载方法3）
     * @param thesisId 论文ID
     * @param status 审核状态（PASS-通过，REJECT-拒绝）
     * @param reviewReason 审核理由
     * @param score 评审分数，0-100分
     * @param level 评审等级，如EXCELLENT、GOOD、AVERAGE、PASS、FAIL
     * @param reviewComment 评审意见，详细的评审内容
     * @return int，返回影响的行数，1表示成功，0表示失败
     * 业务逻辑：教师审核论文，更新论文状态、评审理由、分数、等级和评审意见
     */
    int reviewThesis(Integer thesisId, String status, String reviewReason, Integer score, String level, String reviewComment);

    /**
     * 根据学生ID查询该学生的所有论文
     * @param studentId 学生ID
     * @return List<Thesis>，包含该学生的所有论文列表
     */
    List<Thesis> findByStudent(Integer studentId);

    /**
     * 查询所有待审核和审核中的论文
     * @return List<Thesis>，包含所有待审核和审核中的论文列表
     * 业务逻辑：查询状态为UPLOADED（已上传）或REVIEWING（审核中）的论文
     */
    List<Thesis> findAllReviewing();
    
    /**
     * 查询所有论文
     * @return List<Thesis>，包含所有论文列表
     */
    List<Thesis> findAll();
    
    /**
     * 根据ID查询论文详情
     * @param id 论文ID
     * @return Thesis对象，包含论文的详细信息
     */
    Thesis findById(Integer id);
    
    /**
     * 更新论文信息
     * @param thesis 论文对象，包含更新后的论文信息
     * @return int，返回影响的行数，1表示成功，0表示失败
     * 业务逻辑：更新论文的基本信息和状态
     */
    int updateThesis(Thesis thesis);
}