package com.sangeng.pojo;

import java.util.Date;

/**
 * 选题实体类
 * 作用：表示学生的毕业设计选题申请记录
 * 说明：对应数据库中的selection表，记录学生选择毕业设计项目的过程
 */
public class Selection {
    /**
     * 选题ID
     * 作用：唯一标识一个选题记录
     * 说明：数据库主键，自增生成
     */
    private Integer id;
    
    /**
     * 学生ID
     * 作用：关联到申请选题的学生
     * 说明：外键，关联到user表的id字段，对应role为STUDENT的用户
     */
    private Integer studentId;
    
    /**
     * 项目ID
     * 作用：关联到学生选择的毕业设计项目
     * 说明：外键，关联到project表的id字段
     */
    private Integer projectId;
    
    /**
     * 选题状态
     * 作用：标识选题申请的当前状态
     * 说明：取值范围为 PENDING（待审核）、APPROVED（已通过）、REJECTED（已驳回）
     */
    private String status; // PENDING/APPROVED/REJECTED
    
    /**
     * 选题时间
     * 作用：记录学生提交选题申请的时间
     * 说明：系统自动生成，不需要手动设置
     */
    private Date selectTime;
    
    /**
     * 申请理由
     * 作用：学生提交选题申请时填写的理由
     * 说明：用于教师审核时参考
     */
    private String reason; // 申请理由
    
    /**
     * 项目名称
     * 作用：显示项目名称，方便前端展示
     * 说明：非数据库字段，通过关联查询获取
     */
    private String projectName;
    
    /**
     * 学生姓名
     * 作用：显示学生姓名，方便前端展示
     * 说明：非数据库字段，通过关联查询获取
     */
    private String studentName;
    
    /**
     * 获取学生ID
     * @return 学生ID
     */
    public Integer getStudentId() {
        return studentId;
    }

    /**
     * 设置学生ID
     * @param studentId 学生ID
     */
    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    /**
     * 获取选题ID
     * @return 选题ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置选题ID
     * @param id 选题ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取项目ID
     * @return 项目ID
     */
    public Integer getProjectId() {
        return projectId;
    }

    /**
     * 设置项目ID
     * @param projectId 项目ID
     */
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    /**
     * 获取选题状态
     * @return 选题状态
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置选题状态
     * @param status 选题状态
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取选题时间
     * @return 选题时间
     */
    public Date getSelectTime() {
        return selectTime;
    }

    /**
     * 设置选题时间
     * @param selectTime 选题时间
     */
    public void setSelectTime(Date selectTime) {
        this.selectTime = selectTime;
    }

    /**
     * 获取申请理由
     * @return 申请理由
     */
    public String getReason() {
        return reason;
    }

    /**
     * 设置申请理由
     * @param reason 申请理由
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * 获取项目名称
     * @return 项目名称
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * 设置项目名称
     * @param projectName 项目名称
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * 获取学生姓名
     * @return 学生姓名
     */
    public String getStudentName() {
        return studentName;
    }

    /**
     * 设置学生姓名
     * @param studentName 学生姓名
     */
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}


