package com.sangeng.mapper;

import com.sangeng.pojo.Thesis;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 论文数据访问接口
 * 作用：定义论文数据的CRUD操作
 * 说明：对应数据库中的thesis表，由MyBatis自动生成实现类
 */
public interface ThesisDao {
    /**
     * 根据ID查询论文
     * @param id 论文ID
     * @return 论文对象，如果找不到返回null
     */
    Thesis findById(Integer id);
    
    /**
     * 根据学生ID查询论文列表
     * @param studentId 学生ID
     * @return 论文列表，包含该学生上传的所有论文
     */
    List<Thesis> findByStudent(@Param("studentId") Integer studentId);
    
    /**
     * 查询所有待评审的论文
     * @return 论文列表，包含所有状态为UPLOADED或REVIEWING的论文
     */
    List<Thesis> findAllReviewing();
    
    /**
     * 查询所有论文
     * @return 论文列表，包含系统中所有论文
     */
    List<Thesis> findAll();
    
    /**
     * 插入论文记录
     * @param t 论文对象，包含学生ID、文件名、文件路径等信息
     * @return 影响的行数，1表示成功，0表示失败
     */
    int insert(Thesis t);
    
    /**
     * 更新论文状态、评审理由、分数、等级和评审意见
     * @param id 论文ID
     * @param status 新状态，如UPLOADED、REVIEWING、PASSED、REJECTED
     * @param reviewReason 评审理由，仅当状态为REJECTED时需要填写
     * @param score 评审分数，0-100分
     * @param level 评审等级，如EXCELLENT、GOOD、AVERAGE、PASS、FAIL
     * @param reviewComment 评审意见，详细的评审内容
     * @return 影响的行数，1表示成功，0表示失败
     */
    int updateStatus(@Param("id") Integer id, @Param("status") String status, @Param("reviewReason") String reviewReason,
                    @Param("score") Integer score, @Param("level") String level, @Param("reviewComment") String reviewComment);
    
    /**
     * 更新论文信息
     * @param thesis 论文对象，包含要更新的论文信息
     * @return 影响的行数，1表示成功，0表示失败
     */
    int update(Thesis thesis);
}