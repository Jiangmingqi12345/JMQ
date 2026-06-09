package com.sangeng.controller;

import com.sangeng.common.Result;
import com.sangeng.pojo.Selection;
import com.sangeng.pojo.User;
import com.sangeng.service.SelectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 选题控制器
 * 处理选题相关的HTTP请求，包括获取选题列表、审核选题、拒绝选题、学生取消选题等
 * @RestController注解：组合了@Controller和@ResponseBody，返回值自动转换为JSON格式
 * @RequestMapping("/selections")：设置控制器的基础URL路径，所有请求路径都以/selections开头
 */
@RestController
@RequestMapping("/selections")
public class SelectionController {

    /**
     * @Autowired注解：自动注入SelectionService实例
     * 用于调用选题服务的各种方法
     */
    @Autowired
    private SelectionService selectionService;

    /**
     * 获取选题列表，支持分页、状态过滤和项目名称搜索
     * @param page 当前页码，默认值为1
     * @param size 每页大小，默认值为10
     * @param name 项目名称搜索关键字，非必填
     * @param status 选题状态过滤，非必填
     * @return Result对象，包含选题列表和总数
     * @GetMapping("/list")：映射HTTP GET请求到/selections/list路径
     * @RequestParam注解：获取请求参数，支持默认值和必填设置
     * 业务逻辑：
     * 1. 根据条件获取选题列表
     *    - 如果状态参数不为空，则根据状态查询
     *    - 否则查询所有选题
     * 2. 如果项目名称关键字不为空，则进行过滤
     * 3. 进行模拟分页
     * 4. 构建结果集，包含分页后的选题列表和总数
     */
    @GetMapping("/list")
    public Result getSelectionList(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                  @RequestParam(name = "size", defaultValue = "10") Integer size,
                                  @RequestParam(name = "name", required = false) String name,
                                  @RequestParam(name = "status", required = false) String status) {
        try {
            List<Selection> selectionList;
            
            // 根据条件获取选题列表
            if (status != null && !status.isEmpty()) {
                // 根据状态查询选题列表
                selectionList = selectionService.findAllByStatus(status);
            } else {
                // 查询所有选题
                selectionList = selectionService.findAll();
            }
            
            // 项目名称过滤
            if (name != null && !name.trim().isEmpty()) {
                selectionList = selectionList.stream()
                    .filter(selection -> {
                        String projectName = selection.getProjectName();
                        return projectName != null && projectName.contains(name);
                    })
                    .collect(java.util.stream.Collectors.toList());
            }
            
            // 模拟分页
            int total = selectionList.size(); // 总记录数
            int startIndex = (page - 1) * size; // 起始索引
            int endIndex = Math.min(startIndex + size, total); // 结束索引，确保不超过总数
            List<Selection> pageList = new java.util.ArrayList<>();
            if (startIndex < total) {
                pageList = selectionList.subList(startIndex, endIndex); // 分页后的列表
            }
            
            // 返回结果，包含数据和总数
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("data", pageList); // 分页后的选题列表
            resultMap.put("total", total); // 总数
            
            return Result.success(resultMap); // 返回成功结果
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈信息
            return Result.error("获取选题列表失败：" + e.getMessage()); // 返回错误结果
        }
    }

    /**
     * 根据ID获取单个选题详情
     * @param id 选题ID，从URL路径中获取
     * @return Result对象，包含选题详情
     * @GetMapping("/{id}")：映射HTTP GET请求到/selections/{id}路径，{id}为路径参数
     * @PathVariable注解：获取URL路径中的参数
     * 业务逻辑：
     * 1. 根据ID查询选题
     * 2. 查询成功：返回选题详情
     * 3. 查询失败：返回选题不存在的错误信息
     */
    @GetMapping("/{id}")
    public Result getSelectionById(@PathVariable Integer id) {
        try {
            Selection selection = selectionService.findById(id); // 根据ID查询选题
            if (selection != null) {
                return Result.success(selection); // 查询成功，返回选题详情
            } else {
                return Result.error("选题不存在"); // 查询失败，返回错误信息
            }
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈信息
            return Result.error("获取选题详情失败：" + e.getMessage()); // 返回错误结果
        }
    }

    /**
     * 审核选题通过
     * @param id 选题ID，从URL路径中获取
     * @return Result对象，包含审核结果
     * @PutMapping("/{id}/approve")：映射HTTP PUT请求到/selections/{id}/approve路径
     * 业务逻辑：
     * 1. 调用SelectionService的approve方法审核选题通过
     * 2. 返回审核结果
     */
    @PutMapping("/{id}/approve")
    public Result approveSelection(@PathVariable Integer id) {
        try {
            // 调用SelectionService的approve方法，审核选题通过
            boolean success = selectionService.approve(id);
            if (success) {
                return Result.success("审核通过成功"); // 审核成功，返回成功信息
            } else {
                return Result.error("审核通过失败"); // 审核失败，返回失败信息
            }
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈信息
            return Result.error("审核通过失败：" + e.getMessage()); // 返回错误结果
        }
    }

    /**
     * 拒绝选题
     * @param id 选题ID，从URL路径中获取
     * @param reason 拒绝理由，从请求参数中获取
     * @return Result对象，包含拒绝结果
     * @PutMapping("/{id}/reject")：映射HTTP PUT请求到/selections/{id}/reject路径
     * @RequestParam(name = "reason")：获取拒绝理由，必填参数
     * 业务逻辑：
     * 1. 调用SelectionService的reject方法拒绝选题
     * 2. 返回拒绝结果
     */
    @PutMapping("/{id}/reject")
    public Result rejectSelection(@PathVariable Integer id, @RequestParam(name = "reason") String reason) {
        try {
            // 调用SelectionService的reject方法，拒绝选题
            boolean success = selectionService.reject(id, reason);
            if (success) {
                return Result.success("拒绝选题成功"); // 拒绝成功，返回成功信息
            } else {
                return Result.error("拒绝选题失败"); // 拒绝失败，返回失败信息
            }
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈信息
            return Result.error("拒绝选题失败：" + e.getMessage()); // 返回错误结果
        }
    }
    
    /**
     * 获取当前学生的选题列表
     * @param user 当前登录用户，从session中获取
     * @param page 当前页码，默认值为1
     * @param size 每页大小，默认值为10
     * @return Result对象，包含学生选题列表和总数
     * @GetMapping("/student")：映射HTTP GET请求到/selections/student路径
     * @SessionAttribute("user")：从session中获取当前登录用户
     * 业务逻辑：
     * 1. 验证用户是否为学生角色
     * 2. 获取当前学生的所有选题记录
     * 3. 进行模拟分页
     * 4. 构建结果集，包含分页后的选题列表和总数
     */
    @GetMapping("/student")
    public Result getStudentSelectionList(@SessionAttribute("user") User user,
                                          @RequestParam(name = "page", defaultValue = "1") Integer page,
                                          @RequestParam(name = "size", defaultValue = "10") Integer size) {
        try {
            // 验证用户是否为学生角色
            if (user == null || !"STUDENT".equals(user.getRole())) {
                return Result.error("没有权限访问此资源"); // 没有权限，返回错误信息
            }
            
            // 获取当前学生的所有选题记录
            List<Selection> selectionList = selectionService.findByStudent(user.getId());
            
            // 模拟分页
            int total = selectionList.size(); // 总记录数
            int startIndex = (page - 1) * size; // 起始索引
            int endIndex = Math.min(startIndex + size, total); // 结束索引，确保不超过总数
            List<Selection> pageList = selectionList.subList(startIndex, endIndex); // 分页后的列表
            
            // 返回结果，包含数据和总数
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("data", pageList); // 分页后的选题列表
            resultMap.put("total", total); // 总数
            
            return Result.success(resultMap); // 返回成功结果
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈信息
            return Result.error("获取学生选题列表失败：" + e.getMessage()); // 返回错误结果
        }
    }
    
    /**
     * 学生取消选题申请
     * @param id 选题ID，从URL路径中获取
     * @param user 当前登录用户，从session中获取
     * @return Result对象，包含取消结果
     * @DeleteMapping("/{id}/cancel")：映射HTTP DELETE请求到/selections/{id}/cancel路径
     * @SessionAttribute("user")：从session中获取当前登录用户
     * 业务逻辑：
     * 1. 验证用户是否为学生角色
     * 2. 调用SelectionService的cancel方法取消选题
     * 3. 返回取消结果
     */
    @DeleteMapping("/{id}/cancel")
    public Result cancelSelection(@PathVariable Integer id, @SessionAttribute("user") User user) {
        try {
            // 验证用户是否为学生角色
            if (user == null || !"STUDENT".equals(user.getRole())) {
                return Result.error("没有权限取消选题"); // 没有权限，返回错误信息
            }
            
            // 调用SelectionService的cancel方法，取消选题
            boolean success = selectionService.cancel(id, user.getId());
            if (success) {
                return Result.success("取消选题成功"); // 取消成功，返回成功信息
            } else {
                return Result.error("取消选题失败"); // 取消失败，返回失败信息
            }
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈信息
            return Result.error("取消选题失败：" + e.getMessage()); // 返回错误结果
        }
    }
}