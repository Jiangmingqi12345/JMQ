package com.sangeng.service.impl;

import com.sangeng.mapper.ProjectDao;
import com.sangeng.mapper.ThesisDao;
import com.sangeng.mapper.UserDao;
import com.sangeng.pojo.Thesis;
import com.sangeng.service.ThesisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 论文服务实现类
 * 实现了ThesisService接口中定义的所有方法
 * @Service注解：将该类标记为Spring的服务组件，由Spring容器管理
 */
@Service
public class ThesisServiceImpl implements ThesisService {

    /**
     * @Autowired注解：自动注入ThesisDao实例
     * 用于操作论文相关的数据
     */
    @Autowired
    private ThesisDao thesisDao;
    
    /**
     * @Autowired注解：自动注入UserDao实例
     * 用于操作用户相关的数据
     */
    @Autowired
    private UserDao userDao;
    
    /**
     * @Autowired注解：自动注入ProjectDao实例
     * 用于操作项目相关的数据
     */
    @Autowired
    private ProjectDao projectDao;

    /**
     * 学生上传论文方法实现
     * @param thesis 论文对象，包含论文的基本信息和文件路径
     * @return int，返回影响的行数，1表示成功，0表示失败
     * @Transactional注解：标记该方法需要事务管理
     * 业务逻辑：
     * 1. 设置论文状态为uploaded（已上传）
     * 2. 设置上传时间为当前时间
     * 3. 调用DAO层的insert方法插入论文记录
     */
    @Transactional
    @Override
    public int uploadThesis(Thesis thesis) {
        // 设置论文状态为已上传
        thesis.setStatus("uploaded");
        // 设置上传时间为当前时间
        thesis.setUploadTime(new java.util.Date());
        // 调用DAO层插入方法，返回影响行数
        return thesisDao.insert(thesis);
    }

    /**
     * 教师审核论文方法实现（重载方法1）
     * @param thesisId 论文ID
     * @param status 审核状态（PASS-通过，REJECT-拒绝）
     * @return int，返回影响的行数，1表示成功，0表示失败
     * @Transactional注解：标记该方法需要事务管理
     * 业务逻辑：调用重载方法reviewThesis(Integer thesisId, String status, String reviewReason)，审核理由为null
     */
    @Transactional
    @Override
    public int reviewThesis(Integer thesisId, String status) {
        // 调用重载方法，审核理由为null
        return reviewThesis(thesisId, status, null);
    }
    
    /**
     * 教师审核论文方法实现（重载方法2）
     * @param thesisId 论文ID
     * @param status 审核状态（PASS-通过，REJECT-拒绝）
     * @param reviewReason 审核理由
     * @return int，返回影响的行数，1表示成功，0表示失败
     * @Transactional注解：标记该方法需要事务管理
     * 业务逻辑：
     * 1. 查询当前论文状态（调试用）
     * 2. 更新论文状态和审核理由
     * 3. 查询更新后的论文状态（调试用）
     * 4. 返回更新结果
     */
    @Transactional
    @Override
    public int reviewThesis(Integer thesisId, String status, String reviewReason) {
        // 添加调试日志（生产环境中应使用日志框架，如Log4j或SLF4J）
        System.out.println("开始评审论文，ID：" + thesisId + "，状态：" + status + "，评审理由：" + reviewReason);
        
        // 查询当前状态（调试用）
        Thesis currentThesis = thesisDao.findById(thesisId);
        System.out.println("当前状态：" + currentThesis.getStatus());
        
        // 更新状态、评审理由，但保持原有分数、等级和评审意见不变
        // 调用6个参数的updateStatus方法，传递原有值
        int result = thesisDao.updateStatus(thesisId, status, reviewReason, 
                                           currentThesis.getScore(), 
                                           currentThesis.getLevel(), 
                                           currentThesis.getReviewComment());
        System.out.println("更新结果：" + result);
        
        // 查询更新后的状态（调试用）
        Thesis updatedThesis = thesisDao.findById(thesisId);
        System.out.println("更新后状态：" + updatedThesis.getStatus());
        System.out.println("更新后分数：" + updatedThesis.getScore() + "，更新后等级：" + updatedThesis.getLevel() + "，更新后评审意见：" + updatedThesis.getReviewComment());
        
        // 返回更新结果
        return result;
    }
    
    /**
     * 教师审核论文方法实现（重载方法3）
     * @param thesisId 论文ID
     * @param status 审核状态（PASS-通过，REJECT-拒绝）
     * @param reviewReason 审核理由
     * @param score 评审分数，0-100分
     * @param level 评审等级，如EXCELLENT、GOOD、AVERAGE、PASS、FAIL
     * @param reviewComment 评审意见，详细的评审内容
     * @return int，返回影响的行数，1表示成功，0表示失败
     * @Transactional注解：标记该方法需要事务管理
     * 业务逻辑：
     * 1. 添加调试日志
     * 2. 查询当前论文状态（调试用）
     * 3. 更新论文状态、评审理由、分数、等级和评审意见
     * 4. 查询更新后的论文状态（调试用）
     * 5. 返回更新结果
     */
    @Transactional
    @Override
    public int reviewThesis(Integer thesisId, String status, String reviewReason, Integer score, String level, String reviewComment) {
        // 添加详细的调试日志（生产环境中应使用日志框架，如Log4j或SLF4J）
        System.out.println("---------------------- 开始评审论文 ----------------------");
        System.out.println("论文ID：" + thesisId);
        System.out.println("评审状态：" + status);
        System.out.println("评审理由：" + reviewReason);
        System.out.println("评审分数：" + score);
        System.out.println("评审等级：" + level);
        System.out.println("评审意见：" + reviewComment);
        
        // 查询当前论文信息（调试用）
        Thesis currentThesis = thesisDao.findById(thesisId);
        System.out.println("当前论文信息：");
        System.out.println("  当前状态：" + currentThesis.getStatus());
        System.out.println("  当前分数：" + currentThesis.getScore());
        System.out.println("  当前等级：" + currentThesis.getLevel());
        System.out.println("  当前评审意见：" + currentThesis.getReviewComment());
        System.out.println("  当前评审理由：" + currentThesis.getReviewReason());
        
        // 更新状态、评审理由、分数、等级和评审意见
        System.out.println("---------------------- 执行数据库更新 ----------------------");
        int result = thesisDao.updateStatus(thesisId, status, reviewReason, score, level, reviewComment);
        System.out.println("数据库更新结果：" + result + " 行受影响");
        
        // 查询更新后的论文信息（调试用）
        System.out.println("---------------------- 查询更新后信息 ----------------------");
        Thesis updatedThesis = thesisDao.findById(thesisId);
        System.out.println("更新后论文信息：");
        System.out.println("  更新后状态：" + updatedThesis.getStatus());
        System.out.println("  更新后分数：" + updatedThesis.getScore());
        System.out.println("  更新后等级：" + updatedThesis.getLevel());
        System.out.println("  更新后评审意见：" + updatedThesis.getReviewComment());
        System.out.println("  更新后评审理由：" + updatedThesis.getReviewReason());
        System.out.println("---------------------- 评审结束 ----------------------");
        
        // 返回更新结果
        return result;
    }

    /**
     * 根据学生ID查询该学生的所有论文
     * @param studentId 学生ID
     * @return List<Thesis>，包含该学生的所有论文列表
     */
    @Override
    public List<Thesis> findByStudent(Integer studentId) {
        // 调用DAO层的findByStudent方法
        return thesisDao.findByStudent(studentId);
    }

    /**
     * 查询所有待审核和审核中的论文
     * @return List<Thesis>，包含所有待审核和审核中的论文列表
     * 业务逻辑：查询状态为uploaded（已上传）或reviewing（审核中）的论文
     */
    @Override
    public List<Thesis> findAllReviewing() {
        // 调用DAO层的findAllReviewing方法
        return thesisDao.findAllReviewing();
    }
    
    /**
     * 查询所有论文
     * @return List<Thesis>，包含所有论文列表
     */
    @Override
    public List<Thesis> findAll() {
        // 调用DAO层的findAll方法
        return thesisDao.findAll();
    }
    
    /**
     * 根据ID查询论文详情
     * @param id 论文ID
     * @return Thesis对象，包含论文的详细信息
     */
    @Override
    public Thesis findById(Integer id) {
        // 调用DAO层的findById方法
        return thesisDao.findById(id);
    }
    
    /**
     * 更新论文信息
     * @param thesis 论文对象，包含更新后的论文信息
     * @return int，返回影响的行数，1表示成功，0表示失败
     * @Transactional注解：标记该方法需要事务管理
     */
    @Transactional
    @Override
    public int updateThesis(Thesis thesis) {
        // 调用DAO层的update方法，返回影响行数
        return thesisDao.update(thesis);
    }
}