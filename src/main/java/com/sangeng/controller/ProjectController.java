package com.sangeng.controller;

import com.sangeng.common.Result;
import com.sangeng.pojo.Project;
import com.sangeng.pojo.User;
import com.sangeng.service.ProjectService;
import com.sangeng.service.SelectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目控制器
 * 处理项目相关的HTTP请求，包括获取项目列表、添加项目、更新项目、删除项目、申请选题等
 * @RestController注解：组合了@Controller和@ResponseBody，返回值自动转换为JSON格式
 * @RequestMapping("/projects")：设置控制器的基础URL路径，所有请求路径都以/projects开头
 */
@RestController
@RequestMapping("/projects")
public class ProjectController {
    
    /**
     * @Autowired注解：自动注入ProjectService实例
     * 用于调用项目服务的各种方法
     */
    @Autowired private ProjectService projectService;
    
    /**
     * @Autowired注解：自动注入SelectionService实例
     * 用于处理选题相关的操作，如学生申请选题
     */
    @Autowired private SelectionService selectionService;

    /**
     * 获取公开的项目列表，支持分页和名称搜索
     * @param page 当前页码，默认值为1
     * @param size 每页大小，默认值为10
     * @param name 项目名称搜索关键字，默认值为空字符串
     * @return Result对象，包含项目列表和总数
     * @GetMapping("/list")：映射HTTP GET请求到/projects/list路径
     * @RequestParam注解：获取请求参数，支持默认值设置
     * 业务逻辑：
     * 1. 调用ProjectService的listOpen方法获取开放状态的项目列表
     * 2. 模拟获取总数（实际项目中应从数据库查询）
     * 3. 构建结果集，包含项目列表和总数
     */
    @GetMapping("/list")
    public Result list(@RequestParam(name = "page", defaultValue="1") int page, 
                      @RequestParam(name = "size", defaultValue="10") int size, 
                      @RequestParam(name = "name", defaultValue="") String name){
        try {
            // 调用ProjectService的listOpen方法，获取开放状态的项目列表
            List<Project> projects = projectService.listOpen(page, size, name);
            // 模拟获取总数（实际项目中应从数据库查询）
            int total = projects.size();
            
            // 构建结果集
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("data", projects); // 项目列表
            resultMap.put("total", total); // 总数
            
            return Result.success(resultMap); // 返回成功结果
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈信息
            return Result.error("获取项目列表失败：" + e.getMessage()); // 返回错误结果
        }
    }

    /**
     * 获取所有项目列表，支持分页和名称搜索
     * @param page 当前页码，默认值为1
     * @param size 每页大小，默认值为10
     * @param name 项目名称搜索关键字，默认值为空字符串
     * @return Result对象，包含项目列表和总数
     * @GetMapping("/all")：映射HTTP GET请求到/projects/all路径
     * 业务逻辑：
     * 1. 调用ProjectService的findAll方法获取所有项目列表
     * 2. 模拟获取总数（实际项目中应从数据库查询）
     * 3. 构建结果集，包含项目列表和总数
     */
    @GetMapping("/all")
    public Result all(@RequestParam(name = "page", defaultValue="1") int page, 
                     @RequestParam(name = "size", defaultValue="10") int size, 
                     @RequestParam(name = "name", defaultValue="") String name){
        try {
            // 调用ProjectService的findAll方法，获取所有项目列表
            List<Project> projects = projectService.findAll(page, size, name);
            // 模拟获取总数（实际项目中应从数据库查询）
            int total = projects.size();
            
            // 构建结果集
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("data", projects); // 项目列表
            resultMap.put("total", total); // 总数
            
            return Result.success(resultMap); // 返回成功结果
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈信息
            return Result.error("获取所有项目列表失败：" + e.getMessage()); // 返回错误结果
        }
    }

    /**
     * 获取教师创建的项目列表
     * @param teacherId 教师ID，必填参数
     * @return Result对象，包含教师创建的项目列表
     * @GetMapping("/teacher")：映射HTTP GET请求到/projects/teacher路径
     * 业务逻辑：
     * 1. 调用ProjectService的findByTeacher方法获取教师创建的项目列表
     * 2. 返回项目列表
     */
    @GetMapping("/teacher")
    public Result findByTeacher(@RequestParam Integer teacherId){
        try {
            // 调用ProjectService的findByTeacher方法，获取教师创建的项目列表
            List<Project> projects = projectService.findByTeacher(teacherId);
            return Result.success(projects); // 返回成功结果
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈信息
            return Result.error("获取教师项目列表失败：" + e.getMessage()); // 返回错误结果
        }
    }

    /**
     * 根据ID获取单个项目详情
     * @param id 项目ID，从URL路径中获取
     * @return Result对象，包含项目详情
     * @GetMapping("/{id}")：映射HTTP GET请求到/projects/{id}路径，{id}为路径参数
     * @PathVariable注解：获取URL路径中的参数
     * 业务逻辑：
     * 1. 调用ProjectService的getById方法根据ID查询项目
     * 2. 查询成功：返回项目详情
     * 3. 查询失败：返回项目不存在的错误信息
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id){
        try {
            // 调用ProjectService的getById方法，根据ID查询项目
            Project project = projectService.getById(id);
            if (project != null) {
                return Result.success(project); // 查询成功，返回项目详情
            } else {
                return Result.error("项目不存在"); // 查询失败，返回错误信息
            }
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈信息
            return Result.error("获取项目详情失败：" + e.getMessage()); // 返回错误结果
        }
    }

    /**
     * 添加项目
     * @param project 项目信息，从请求体中获取
     * @return Result对象，包含添加结果
     * @PostMapping：映射HTTP POST请求到/projects路径
     * @RequestBody注解：将请求体转换为Project对象
     * 业务逻辑：
     * 1. 调用ProjectService的create方法添加项目
     * 2. 添加成功：返回成功信息
     * 3. 添加失败：返回失败信息
     */
    @PostMapping
    public Result create(@RequestBody Project project){
        try {
            // 调用ProjectService的create方法，添加项目
            boolean success = projectService.create(project);
            if (success) {
                return Result.success("添加项目成功"); // 添加成功，返回成功信息
            } else {
                return Result.error("添加项目失败"); // 添加失败，返回失败信息
            }
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈信息
            return Result.error("添加项目失败：" + e.getMessage()); // 返回错误结果
        }
    }

    /**
     * 更新项目信息
     * @param id 项目ID，从URL路径中获取
     * @param project 项目信息，从请求体中获取
     * @return Result对象，包含更新结果
     * @PutMapping("/{id}")：映射HTTP PUT请求到/projects/{id}路径
     * 业务逻辑：
     * 1. 设置项目ID（确保更新的是指定ID的项目）
     * 2. 调用ProjectService的update方法更新项目信息
     * 3. 更新成功：返回成功信息
     * 4. 更新失败：返回失败信息
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable Integer id, @RequestBody Project project){
        try {
            // 设置项目ID，确保更新的是指定ID的项目
            project.setId(id);
            // 调用ProjectService的update方法，更新项目信息
            boolean success = projectService.update(project);
            if (success) {
                return Result.success("更新项目成功"); // 更新成功，返回成功信息
            } else {
                return Result.error("更新项目失败"); // 更新失败，返回失败信息
            }
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈信息
            return Result.error("更新项目失败：" + e.getMessage()); // 返回错误结果
        }
    }

    /**
     * 删除项目
     * @param id 项目ID，从URL路径中获取
     * @return Result对象，包含删除结果
     * @DeleteMapping("/{id}")：映射HTTP DELETE请求到/projects/{id}路径
     * 业务逻辑：
     * 1. 调用ProjectService的delete方法删除项目
     * 2. 删除成功：返回成功信息
     * 3. 删除失败：返回失败信息
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){
        try {
            // 调用ProjectService的delete方法，删除项目
            boolean success = projectService.delete(id);
            if (success) {
                return Result.success("删除项目成功"); // 删除成功，返回成功信息
            } else {
                return Result.error("删除项目失败"); // 删除失败，返回失败信息
            }
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈信息
            return Result.error("删除项目失败：" + e.getMessage()); // 返回错误结果
        }
    }

    /**
     * 学生申请选题
     * @param id 项目ID，从URL路径中获取
     * @param user 当前登录用户，从session中获取
     * @param requestBody 请求体，包含申请理由
     * @return Result对象，包含申请结果
     * @PostMapping("/{id}/apply")：映射HTTP POST请求到/projects/{id}/apply路径
     * @SessionAttribute("user")：从session中获取当前登录用户
     * @RequestBody：获取请求体中的申请理由
     * 业务逻辑：
     * 1. 验证用户是否为学生角色
     * 2. 获取申请理由
     * 3. 调用SelectionService的apply方法申请选题
     * 4. 返回申请结果
     */
    @PostMapping("/{id}/apply")
    public Result apply(@PathVariable Integer id, @SessionAttribute("user") User user, @RequestBody Map<String, String> requestBody){
        try {
            // 验证用户是否为学生角色
            if(user==null || !"STUDENT".equals(user.getRole())) {
                return Result.error("没有权限申请选题"); // 没有权限，返回错误信息
            }
            // 获取申请理由，默认为空字符串
            String reason = requestBody.getOrDefault("reason", "");
            // 调用SelectionService的apply方法，申请选题
            boolean ok = selectionService.apply(user.getId(), id, reason);
            if (ok) {
                return Result.success("申请成功"); // 申请成功，返回成功信息
            } else {
                return Result.error("申请失败"); // 申请失败，返回失败信息
            }
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈信息
            return Result.error("申请选题失败：" + e.getMessage()); // 返回错误结果
        }
    }

    /**
     * 更新项目状态
     * @param id 项目ID，从URL路径中获取
     * @param status 新状态，从请求参数中获取
     * @return Result对象，包含更新结果
     * @PutMapping("/{id}/status")：映射HTTP PUT请求到/projects/{id}/status路径
     * 业务逻辑：
     * 1. 调用ProjectService的updateStatus方法更新项目状态
     * 2. 更新成功：返回成功信息
     * 3. 更新失败：返回失败信息
     */
    @PutMapping("/{id}/status")
    public Result updateStatus(@PathVariable Integer id, @RequestParam Integer status){
        try {
            // 调用ProjectService的updateStatus方法，更新项目状态
            boolean success = projectService.updateStatus(id, status);
            if (success) {
                return Result.success("更新项目状态成功"); // 更新成功，返回成功信息
            } else {
                return Result.error("更新项目状态失败"); // 更新失败，返回失败信息
            }
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈信息
            return Result.error("更新项目状态失败：" + e.getMessage()); // 返回错误结果
        }
    }
}