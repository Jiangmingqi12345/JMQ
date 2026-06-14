package com.sangeng.pojo;

import java.util.Date;

/**
 * 项目实体类
 * 作用：表示毕业设计项目信息
 * 说明：对应数据库中的project表，包含项目的基本信息和状态
 */
public class Project {
    /**
     * 项目ID
     * 作用：唯一标识一个项目
     * 说明：数据库主键
     */
    private Integer id;
    
    /**
     * 项目标题
     * 作用：项目的名称，显示给学生查看
     * 说明：必填字段，长度一般限制在100字符以内
     */
    private String title;
    
    /**
     * 项目描述
     * 作用：详细描述项目的内容、要求、技术栈等
     * 说明：可为空，用于学生了解项目详情
     */
    private String description;
    
    /**
     * 指导教师ID
     * 作用：关联到负责该项目的教师
     * 说明：外键，关联到user表的id字段，对应role为TEACHER的用户
     */
    private Integer teacherId;
    
    /**
     * 项目状态
     * 作用：标识项目的当前状态
     * 说明：0-未发布，1-已发布，2-已结束等
     */
    private Integer status;
    
    /**
     * 最大可选学生数
     * 作用：限制该项目最多可被多少学生选择
     * 说明：由教师在发布项目时设置
     */
    private Integer maxStudents;
    
    /**
     * 已选学生数
     * 作用：记录当前已有多少学生选择了该项目
     * 说明：系统自动维护，无需手动设置
     */
    private Integer selectedCount;
    
    /**
     * 创建时间
     * 作用：记录项目的创建时间
     * 说明：系统自动生成，无需手动设置
     */
    private Date createTime;
    
    /**
     * 获取项目ID
     * @return 项目ID
     */
    public Integer getId() {
        return id;
    }
    
    /**
     * 设置项目ID
     * @param id 项目ID
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    /**
     * 获取项目标题
     * @return 项目标题
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * 设置项目标题
     * @param title 项目标题
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     * 获取项目描述
     * @return 项目描述
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 设置项目描述
     * @param description 项目描述
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * 获取指导教师ID
     * @return 指导教师ID
     */
    public Integer getTeacherId() {
        return teacherId;
    }
    
    /**
     * 设置指导教师ID
     * @param teacherId 指导教师ID
     */
    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }
    
    /**
     * 获取项目状态
     * @return 项目状态
     */
    public Integer getStatus() {
        return status;
    }
    
    /**
     * 设置项目状态
     * @param status 项目状态
     */
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    /**
     * 获取最大可选学生数
     * @return 最大可选学生数
     */
    public Integer getMaxStudents() {
        return maxStudents;
    }
    
    /**
     * 设置最大可选学生数
     * @param maxStudents 最大可选学生数
     */
    public void setMaxStudents(Integer maxStudents) {
        this.maxStudents = maxStudents;
    }
    
    /**
     * 获取已选学生数
     * @return 已选学生数
     */
    public Integer getSelectedCount() {
        return selectedCount;
    }
    
    /**
     * 设置已选学生数
     * @param selectedCount 已选学生数
     */
    public void setSelectedCount(Integer selectedCount) {
        this.selectedCount = selectedCount;
    }
    
    /**
     * 获取创建时间
     * @return 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }
    
    /**
     * 设置创建时间
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}

