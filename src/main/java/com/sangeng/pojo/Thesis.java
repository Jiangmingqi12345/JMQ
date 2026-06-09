package com.sangeng.pojo;

import java.util.Date;

/**
 * 论文实体类
 * 作用：表示学生上传的毕业设计论文信息
 * 说明：对应数据库中的thesis表，包含论文的基本信息、状态和评审结果
 */
public class Thesis {
    /**
     * 论文ID
     * 作用：唯一标识一篇论文
     * 说明：数据库主键，自增生成
     */
    private Integer id;
    
    /**
     * 学生ID
     * 作用：关联到上传论文的学生
     * 说明：外键，关联到user表的id字段，对应role为STUDENT的用户
     */
    private Integer studentId;
    
    /**
     * 文件名
     * 作用：存储论文文件的原始名称
     * 说明：用于下载时显示给用户
     */
    private String fileName;
    
    /**
     * 文件路径
     * 作用：存储论文文件在服务器上的存储路径
     * 说明：用于服务器内部访问文件
     */
    private String filePath;
    
    /**
     * 上传时间
     * 作用：记录论文上传的时间
     * 说明：系统自动生成，不需要手动设置
     */
    private Date uploadTime;
    
    /**
     * 论文状态
     * 作用：标识论文的当前状态
     * 说明：取值范围为 UPLOADED（已上传）、REVIEWING（审核中）、PASSED（已通过）、REJECTED（已驳回）
     */
    private String status; // UPLOADED 已上传 REVIEWING 审核中 PASSED 通过 REJECTED 驳回
    
    /**
     * 论文标题
     * 作用：存储论文的标题
     * 说明：由学生上传时填写
     */
    private String title;
    
    /**
     * 项目ID
     * 作用：关联到论文所属的毕业设计项目
     * 说明：外键，关联到project表的id字段
     */
    private Integer projectId;
    
    /**
     * 评审理由
     * 作用：存储教师评审论文时填写的评审意见
     * 说明：只有当状态为REJECTED时，该字段才有值
     */
    private String reviewReason; // 评审理由
    
    /**
     * 评审分数
     * 作用：存储教师评审论文时给出的分数
     * 说明：取值范围0-100，默认0
     */
    private Integer score; // 评审分数
    
    /**
     * 评审等级
     * 作用：存储教师评审论文时给出的等级
     * 说明：取值范围为EXCELLENT(优秀)、GOOD(良好)、AVERAGE(中等)、PASS(及格)、FAIL(不及格)
     */
    private String level; // 评审等级
    
    /**
     * 评审意见
     * 作用：存储教师评审论文时给出的详细评审意见
     * 说明：教师评审论文时填写的详细意见，学生可以查看
     */
    private String reviewComment; // 评审意见
    
    /**
     * 学生姓名
     * 作用：显示学生姓名，方便前端展示
     * 说明：非数据库字段，通过关联查询获取
     */
    // 非数据库字段，用于返回给前端
    private String studentName;
    
    /**
     * 项目名称
     * 作用：显示项目名称，方便前端展示
     * 说明：非数据库字段，通过关联查询获取
     */
    private String projectName;
    
    /**
     * 文件大小
     * 作用：存储论文文件的大小
     * 说明：单位为字节，用于前端显示文件大小
     */
    private Long fileSize;
    
    /**
     * 文件类型
     * 作用：存储论文文件的类型
     * 说明：例如PDF、DOCX等，用于前端显示文件类型
     */
    private String fileType;

    /**
     * 获取论文ID
     * @return 论文ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置论文ID
     * @param id 论文ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

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
     * 获取文件名
     * @return 文件名
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * 设置文件名
     * @param fileName 文件名
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * 获取文件路径
     * @return 文件路径
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * 设置文件路径
     * @param filePath 文件路径
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * 获取上传时间
     * @return 上传时间
     */
    public Date getUploadTime() {
        return uploadTime;
    }

    /**
     * 设置上传时间
     * @param uploadTime 上传时间
     */
    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    /**
     * 获取论文状态
     * @return 论文状态
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置论文状态
     * @param status 论文状态
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取论文标题
     * @return 论文标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置论文标题
     * @param title 论文标题
     */
    public void setTitle(String title) {
        this.title = title;
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
     * 获取评审理由
     * @return 评审理由
     */
    public String getReviewReason() {
        return reviewReason;
    }

    /**
     * 设置评审理由
     * @param reviewReason 评审理由
     */
    public void setReviewReason(String reviewReason) {
        this.reviewReason = reviewReason;
    }

    /**
     * 获取评审分数
     * @return 评审分数
     */
    public Integer getScore() {
        return score;
    }

    /**
     * 设置评审分数
     * @param score 评审分数
     */
    public void setScore(Integer score) {
        this.score = score;
    }

    /**
     * 获取评审等级
     * @return 评审等级
     */
    public String getLevel() {
        return level;
    }

    /**
     * 设置评审等级
     * @param level 评审等级
     */
    public void setLevel(String level) {
        this.level = level;
    }

    /**
     * 获取评审意见
     * @return 评审意见
     */
    public String getReviewComment() {
        return reviewComment;
    }

    /**
     * 设置评审意见
     * @param reviewComment 评审意见
     */
    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }
    
    /**
     * 获取文件大小
     * @return 文件大小（字节）
     */
    public Long getFileSize() {
        return fileSize;
    }
    
    /**
     * 设置文件大小
     * @param fileSize 文件大小（字节）
     */
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    /**
     * 获取文件类型
     * @return 文件类型
     */
    public String getFileType() {
        return fileType;
    }
    
    /**
     * 设置文件类型
     * @param fileType 文件类型
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}

