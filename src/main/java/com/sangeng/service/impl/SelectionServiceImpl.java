package com.sangeng.service.impl;

import com.sangeng.mapper.ProjectDao;
import com.sangeng.mapper.SelectionDao;
import com.sangeng.pojo.Selection;
import com.sangeng.service.SelectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

/**
 * 选题服务实现类
 * 实现了SelectionService接口中定义的所有方法
 * @Service注解：将该类标记为Spring的服务组件，由Spring容器管理
 */
@Service
public class SelectionServiceImpl implements SelectionService {
    
    /**
     * @Autowired注解：自动注入SelectionDao实例
     * 用于操作选题相关的数据
     */
    @Autowired
    private SelectionDao selectionDao;
    
    /**
     * @Autowired注解：自动注入ProjectDao实例
     * 用于操作项目相关的数据，特别是更新项目的已选人数
     */
    @Autowired
    private ProjectDao projectDao;

    /**
     * 学生申请选题方法实现
     * @param studentId 学生ID
     * @param projectId 项目ID
     * @param reason 选题理由
     * @return boolean，申请成功返回true，失败返回false
     * @Transactional(rollbackFor = Exception.class)：标记该方法需要事务管理，任何异常都会回滚
     * 业务逻辑：
     * 1. 检查学生是否已有被批准的选题：如果已有，无法再次申请
     * 2. 检查学生是否有待审核的选题：如果有，无法再次申请
     * 3. 防重检查：检查学生是否已申请该项目：如果已申请，无法再次申请
     * 4. 检查项目是否已满：尝试增加项目的已选人数，如果返回0表示已满
     * 5. 插入申请记录：创建选题记录，设置状态为PENDING（待审核）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean apply(Integer studentId, Integer projectId, String reason){
        // 1. 检查学生是否已有被批准的选题
        if (selectionDao.findApprovedByStudent(studentId) != null) {
            return false; // 已有被批准的选题，无法再次申请
        }
        
        // 2. 检查学生是否有待审核的选题
        if (!selectionDao.findPendingByStudent(studentId).isEmpty()) {
            return false; // 有待审核的选题，无法再次申请
        }
        
        // 3. 检查项目是否已满：尝试增加项目的已选人数
        // increaseSelectedCount方法会检查已选人数是否小于最大学生数，是则增加并返回1，否则返回0
        int rows = projectDao.increaseSelectedCount(projectId);
        if (rows <= 0) {
            return false; // 项目已满
        }
        
        // 4. 检查学生是否已申请该项目（不管状态）
        Selection existingSelection = selectionDao.findByStudentProject(studentId, projectId);
        Date now = new Date();
        
        if (existingSelection != null) {
            // 如果已申请过，更新申请记录
            // 将状态改为PENDING，更新理由和时间
            int updateRows = selectionDao.updateSelection(studentId, projectId, reason, now);
            return updateRows > 0;
        } else {
            // 如果未申请过，插入新记录
            Selection s = new Selection();
            s.setStudentId(studentId); // 设置学生ID
            s.setProjectId(projectId); // 设置项目ID
            s.setStatus("PENDING"); // 设置状态为待审核
            s.setSelectTime(now); // 设置选题时间为当前时间
            s.setReason(reason); // 设置选题理由
            int insertRows = selectionDao.insert(s); // 插入到数据库
            return insertRows > 0;
        }
    }

    /**
     * 教师批准选题申请方法实现
     * @param selectionId 选题记录ID
     * @return boolean，批准成功返回true，失败返回false
     * @Transactional(rollbackFor = Exception.class)：标记该方法需要事务管理
     * 业务逻辑：
     * 1. 根据选题ID查询选题记录
     * 2. 更新选题状态为APPROVED（已批准）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approve(Integer selectionId) {
        // 根据选题ID查询选题记录
        Selection selection = selectionDao.findById(selectionId);
        if (selection == null) {
            return false; // 选题记录不存在
        }
        // 更新选题状态为已批准
        return selectionDao.updateStatus(selectionId, "APPROVED") > 0;
    }

    /**
     * 教师拒绝选题申请方法实现
     * @param selectionId 选题记录ID
     * @param reason 拒绝理由
     * @return boolean，拒绝成功返回true，失败返回false
     * @Transactional(rollbackFor = Exception.class)：标记该方法需要事务管理
     * 业务逻辑：
     * 1. 根据选题ID查询选题记录
     * 2. 更新项目的已选人数：将项目的已选人数减1（因为学生没有被批准参加该项目）
     * 3. 更新选题状态为REJECTED（已拒绝）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reject(Integer selectionId, String reason) {
        // 根据选题ID查询选题记录
        Selection selection = selectionDao.findById(selectionId);
        if (selection == null) {
            return false; // 选题记录不存在
        }
        
        // 更新项目的已选人数：将项目的已选人数减1
        // 因为学生的选题申请被拒绝，所以该学生不应占用项目的名额
        projectDao.updateSelectedCount(selection.getProjectId(), -1);
        
        // 更新选题状态为已拒绝
        return selectionDao.updateStatus(selectionId, "REJECTED") > 0;
    }

    /**
     * 学生取消选题方法实现
     * @param selectionId 选题记录ID
     * @param studentId 学生ID
     * @return boolean，取消成功返回true，失败返回false
     * @Transactional(rollbackFor = Exception.class)：标记该方法需要事务管理
     * 业务逻辑：
     * 1. 验证选题记录是否存在且属于该学生
     * 2. 更新项目的已选人数：将项目的已选人数减1
     * 3. 删除选题记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancel(Integer selectionId, Integer studentId) {
        // 根据选题ID查询选题记录
        Selection selection = selectionDao.findById(selectionId);
        // 验证选题记录是否存在且属于该学生
        if (selection == null || !selection.getStudentId().equals(studentId)) {
            return false;
        }
        // 更新项目的选中数量：将项目的已选人数减1
        projectDao.updateSelectedCount(selection.getProjectId(), -1);
        // 删除选题记录
        return selectionDao.deleteById(selectionId) > 0;
    }

    /**
     * 根据选题ID查询选题详情
     * @param selectionId 选题记录ID
     * @return Selection对象，包含选题的详细信息
     */
    @Override
    public Selection findById(Integer selectionId) {
        return selectionDao.findById(selectionId);
    }

    /**
     * 根据学生ID查询该学生的所有选题记录
     * @param studentId 学生ID
     * @return List<Selection>，包含该学生的所有选题记录
     */
    @Override
    public List<Selection> findByStudent(Integer studentId) {
        return selectionDao.findByStudent(studentId);
    }

    /**
     * 根据教师ID查询该教师所有项目的选题记录
     * @param teacherId 教师ID
     * @return List<Selection>，包含该教师所有项目的选题记录
     */
    @Override
    public List<Selection> findByTeacher(Integer teacherId) {
        return selectionDao.findByTeacher(teacherId);
    }

    /**
     * 根据项目ID查询该项目的所有选题记录
     * @param projectId 项目ID
     * @return List<Selection>，包含该项目的所有选题记录
     */
    @Override
    public List<Selection> findByProject(Integer projectId) {
        return selectionDao.findByProject(projectId);
    }

    /**
     * 根据状态查询所有选题记录
     * @param status 选题状态（PENDING-待审核，APPROVED-已批准，REJECTED-已拒绝）
     * @return List<Selection>，包含指定状态的选题记录
     */
    @Override
    public List<Selection> findAllByStatus(String status) {
        return selectionDao.findAllByStatus(status);
    }

    /**
     * 查询所有选题记录
     * @return List<Selection>，包含所有选题记录
     */
    @Override
    public List<Selection> findAll() {
        return selectionDao.findAll();
    }
}

