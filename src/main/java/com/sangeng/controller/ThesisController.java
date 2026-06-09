package com.sangeng.controller;

import com.sangeng.common.Result;
import com.sangeng.mapper.ProjectDao;
import com.sangeng.mapper.UserDao;
import com.sangeng.pojo.Project;
import com.sangeng.pojo.Thesis;
import com.sangeng.pojo.User;
import com.sangeng.service.ThesisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 论文控制器
 * 处理论文相关的HTTP请求，包括获取论文列表、提交评审、重新上传、下载论文等
 * @Controller注解：标记该类为Spring MVC控制器，由Spring容器管理
 * @RequestMapping("/thesis")：设置控制器的基础URL路径，所有请求路径都以/thesis开头
 * @CrossOrigin注解：处理跨域请求，允许来自http://localhost:5173的请求，支持凭证传递和所有HTTP方法
 */
@Controller
@RequestMapping("/thesis")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class ThesisController {
    
    /**
     * @Autowired注解：自动注入ThesisService实例
     * 用于调用论文服务的各种方法
     */
    @Autowired
    private ThesisService thesisService;
    
    /**
     * @Autowired注解：自动注入UserDao实例
     * 用于查询用户相关信息，如学生姓名
     */
    @Autowired
    private UserDao userDao;
    
    /**
     * @Autowired注解：自动注入ProjectDao实例
     * 用于查询项目相关信息，如项目名称
     */
    @Autowired
    private ProjectDao projectDao;

    /**
     * 获取论文列表，支持分页、标题搜索、学生姓名搜索和状态过滤
     * @param page 当前页码，默认值为1
     * @param size 每页大小，默认值为10
     * @param title 论文标题搜索关键字，非必填
     * @param studentName 学生姓名搜索关键字，非必填
     * @param status 论文状态过滤，非必填
     * @return Result对象，包含论文列表和总数
     * @GetMapping("/list")：映射HTTP GET请求到/thesis/list路径
     * @ResponseBody注解：将返回的Result对象转换为JSON格式响应
     * @RequestParam注解：获取请求参数，支持默认值和必填设置
     * 业务逻辑：
     * 1. 获取所有论文列表
     * 2. 构建包含详细信息的结果列表（添加学生姓名和项目名称）
     * 3. 根据条件进行过滤（标题、学生姓名、状态）
     * 4. 进行分页处理
     * 5. 构建结果集，包含分页后的论文列表和总数
     */
    @GetMapping("/list")
    @ResponseBody
    public Result getThesisList(@RequestParam(name = "page", defaultValue = "1") Integer page,
                               @RequestParam(name = "size", defaultValue = "10") Integer size,
                               @RequestParam(name = "title", required = false) String title,
                               @RequestParam(name = "studentName", required = false) String studentName,
                               @RequestParam(name = "status", required = false) String status) {
        try {
            // 获取所有论文列表
            List<Thesis> thesisList = thesisService.findAll();
            
            // 创建结果列表，使用Map存储每个论文的信息
            List<Map<String, Object>> resultList = new ArrayList<>();
            for (Thesis thesis : thesisList) {
                // 创建论文信息Map
                Map<String, Object> thesisMap = new HashMap<>();
                
                // 复制论文的基本属性
                thesisMap.put("id", thesis.getId());
                thesisMap.put("studentId", thesis.getStudentId());
                thesisMap.put("fileName", thesis.getFileName());
                thesisMap.put("filePath", thesis.getFilePath());
                thesisMap.put("uploadTime", thesis.getUploadTime());
                thesisMap.put("status", thesis.getStatus());
                thesisMap.put("title", thesis.getTitle());
                thesisMap.put("projectId", thesis.getProjectId());
                thesisMap.put("reviewReason", thesis.getReviewReason());
                
                // 添加调试信息
                System.out.println("论文ID: " + thesis.getId());
                System.out.println("论文标题: " + thesis.getTitle());
                System.out.println("项目ID: " + thesis.getProjectId());
                System.out.println("评审理由: " + thesis.getReviewReason());
                
                // 查询并添加学生姓名
                String studentNameValue = "";
                if (thesis.getStudentId() != null) {
                    User user = userDao.findById(thesis.getStudentId());
                    if (user != null) {
                        studentNameValue = user.getName();
                        thesisMap.put("studentName", studentNameValue);
                    } else {
                        thesisMap.put("studentName", studentNameValue);
                    }
                } else {
                    thesisMap.put("studentName", studentNameValue);
                }
                
                // 查询并添加项目名称
                String projectName = "";
                if (thesis.getProjectId() != null) {
                    Project project = projectDao.findById(thesis.getProjectId());
                    if (project != null) {
                        projectName = project.getTitle();
                        System.out.println("项目ID: " + thesis.getProjectId() + "，项目名称: " + projectName);
                    } else {
                        System.out.println("项目ID: " + thesis.getProjectId() + "，项目不存在");
                    }
                } else {
                    System.out.println("项目ID: null");
                }
                thesisMap.put("projectName", projectName);
                
                // 添加到临时列表
                resultList.add(thesisMap);
            }
            
            // 标题过滤
            if (title != null && !title.trim().isEmpty()) {
                resultList = resultList.stream()
                    .filter(map -> {
                        String thesisTitle = (String) map.get("title");
                        return thesisTitle != null && thesisTitle.contains(title);
                    })
                    .collect(java.util.stream.Collectors.toList());
            }
            
            // 学生姓名过滤
            if (studentName != null && !studentName.trim().isEmpty()) {
                resultList = resultList.stream()
                    .filter(map -> {
                        String name = (String) map.get("studentName");
                        return name != null && name.contains(studentName);
                    })
                    .collect(java.util.stream.Collectors.toList());
            }
            
            // 状态过滤
            if (status != null && !status.trim().isEmpty()) {
                resultList = resultList.stream()
                    .filter(map -> {
                        String thesisStatus = (String) map.get("status");
                        return thesisStatus != null && thesisStatus.equals(status);
                    })
                    .collect(java.util.stream.Collectors.toList());
            }
            
            // 分页处理
            int total = resultList.size(); // 总记录数
            int startIndex = (page - 1) * size; // 起始索引
            int endIndex = Math.min(startIndex + size, total); // 结束索引，确保不超过总数
            List<Map<String, Object>> pageResultList = new ArrayList<>();
            if (startIndex < total) {
                pageResultList = resultList.subList(startIndex, endIndex); // 分页后的列表
            }
            
            // 返回结果，包含数据和总数
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("data", pageResultList); // 分页后的论文列表
            resultMap.put("total", total); // 总数
            
            return Result.success(resultMap); // 返回成功结果
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈信息
            return Result.error("获取论文列表失败：" + e.getMessage()); // 返回错误结果
        }
    }
    
    /**
     * 获取单篇论文详情
     * @param id 论文ID，从URL路径中获取
     * @return Result对象，包含论文详情
     * @GetMapping("/detail/{id}")：映射HTTP GET请求到/thesis/detail/{id}路径，{id}为路径参数
     * @ResponseBody注解：将返回的Result对象转换为JSON格式响应
     * @PathVariable注解：获取URL路径中的参数
     * 业务逻辑：
     * 1. 根据ID查询论文
     * 2. 如果论文不存在，返回错误信息
     * 3. 构建包含详细信息的论文Map（添加学生姓名和项目名称）
     * 4. 返回论文详情
     */
    @GetMapping("/detail/{id}")
    @ResponseBody
    public Result getThesisDetail(@PathVariable Integer id) {
        try {
            // 获取论文详情
            Thesis thesis = thesisService.findById(id);
            if (thesis == null) {
                return Result.error("论文不存在"); // 论文不存在，返回错误信息
            }
            
            // 创建论文信息Map
            Map<String, Object> thesisMap = new HashMap<>();
            
            // 复制论文的基本属性
            thesisMap.put("id", thesis.getId());
            thesisMap.put("studentId", thesis.getStudentId());
            thesisMap.put("fileName", thesis.getFileName());
            thesisMap.put("filePath", thesis.getFilePath());
            thesisMap.put("uploadTime", thesis.getUploadTime());
            thesisMap.put("status", thesis.getStatus());
            thesisMap.put("reviewReason", thesis.getReviewReason());
            thesisMap.put("score", thesis.getScore());
            thesisMap.put("level", thesis.getLevel());
            thesisMap.put("reviewComment", thesis.getReviewComment());
            
            // 处理标题，确保不为null
            String title = thesis.getTitle() != null ? thesis.getTitle() : "";
            thesisMap.put("title", title);
            thesisMap.put("projectId", thesis.getProjectId());
            
            // 添加详细调试信息
            System.out.println("---------------------- 论文详情调试 ----------------------");
            System.out.println("论文ID: " + thesis.getId());
            System.out.println("fileSize字段值: " + thesis.getFileSize());
            System.out.println("fileType字段值: " + thesis.getFileType());
            System.out.println("获取论文详情 - 评审理由: " + thesis.getReviewReason());
            
            // 查询并添加学生姓名
            String studentName = "";
            if (thesis.getStudentId() != null) {
                User user = userDao.findById(thesis.getStudentId());
                if (user != null) {
                    studentName = user.getName();
                }
            }
            thesisMap.put("studentName", studentName);
            
            // 查询并添加项目名称
            String projectName = "";
            if (thesis.getProjectId() != null) {
                Project project = projectDao.findById(thesis.getProjectId());
                if (project != null) {
                    projectName = project.getTitle();
                }
            }
            thesisMap.put("projectName", projectName);
            
            // 设置文件大小，将字节转换为MB显示
            String fileSizeStr = "";
            if (thesis.getFileSize() != null) {
                double fileSizeMB = thesis.getFileSize() / (1024.0 * 1024.0);
                fileSizeStr = String.format("%.2fMB", fileSizeMB);
            }
            thesisMap.put("fileSize", fileSizeStr);
            
            // 设置文件类型
            thesisMap.put("fileType", thesis.getFileType() != null ? thesis.getFileType() : "");
            
            return Result.success(thesisMap); // 返回成功结果，包含论文详情
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈信息
            return Result.error("获取论文详情失败：" + e.getMessage()); // 返回错误结果
        }
    }
    
    /**
     * 提交论文评审结果
     * @param id 论文ID，从URL路径中获取
     * @param requestBody 请求体，包含评审状态、评审理由、分数、等级和评审意见
     * @return Result对象，包含评审结果
     * @PostMapping("/review/{id}")：映射HTTP POST请求到/thesis/review/{id}路径
     * @ResponseBody注解：将返回的Result对象转换为JSON格式响应
     * @RequestBody注解：将请求体转换为Map对象
     * 业务逻辑：
     * 1. 获取评审状态、评审理由、分数、等级和评审意见
     * 2. 验证状态值是否有效（只能是PASSED或REJECTED）
     * 3. 调用服务层方法更新论文状态和评审信息
     * 4. 返回评审结果
     */
    @PostMapping("/review/{id}")
    @ResponseBody
    public Result submitReview(@PathVariable Integer id, @RequestBody Map<String, Object> requestBody) {
        try {
            // 添加调试日志，打印完整请求体
            System.out.println("前端提交评审请求，论文ID：" + id);
            System.out.println("完整请求体：" + requestBody);
            
            // 获取状态值
            String status = (String) requestBody.get("status");
            String reviewReason = (String) requestBody.get("reviewReason");
            
            // 处理score参数，将Double转换为Integer
            Integer score = null;
            Object scoreObj = requestBody.get("score");
            if (scoreObj != null) {
                if (scoreObj instanceof Double) {
                    score = ((Double) scoreObj).intValue();
                } else if (scoreObj instanceof Integer) {
                    score = (Integer) scoreObj;
                }
            }
            
            String level = (String) requestBody.get("level");
            String reviewComment = (String) requestBody.get("reviewComment");
            
            // 添加调试日志，打印每个字段的值
            System.out.println("status：" + status);
            System.out.println("reviewReason：" + reviewReason);
            System.out.println("score：" + score);
            System.out.println("level：" + level);
            System.out.println("reviewComment：" + reviewComment);
            
            // 验证状态值
            if (status == null || (!"PASSED".equals(status) && !"REJECTED".equals(status))) {
                return Result.error("无效的状态值，只能是PASSED或REJECTED"); // 无效状态，返回错误信息
            }
            
            // 调用服务层方法更新论文状态和评审信息
            int result = thesisService.reviewThesis(id, status, reviewReason, score, level, reviewComment);
            System.out.println("调用reviewThesis结果：" + result);
            if (result > 0) {
                return Result.success("评审成功"); // 评审成功，返回成功信息
            } else {
                return Result.error("评审失败，论文不存在或状态已更新"); // 评审失败，返回失败信息
            }
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈信息
            return Result.error("评审失败：" + e.getMessage()); // 返回错误结果
        }
    }
    
    /**
     * 重新上传论文
     * @param id 论文ID，从URL路径中获取
     * @return Result对象，包含重新上传结果
     * @PostMapping("/reupload/{id}")：映射HTTP POST请求到/thesis/reupload/{id}路径
     * @ResponseBody注解：将返回的Result对象转换为JSON格式响应
     * 业务逻辑：
     * 1. 调用服务层方法将论文状态改为UPLOADED（已上传）
     * 2. 返回重新上传结果
     */
    @PostMapping("/reupload/{id}")
    @ResponseBody
    public Result reuploadThesis(@PathVariable Integer id) {
        try {
            // 调用服务层方法将论文状态改为已上传
            int result = thesisService.reviewThesis(id, "UPLOADED", null);
            if (result > 0) {
                return Result.success("重新上传成功"); // 重新上传成功，返回成功信息
            } else {
                return Result.error("重新上传失败，论文不存在或状态已更新"); // 重新上传失败，返回失败信息
            }
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈信息
            return Result.error("重新上传失败：" + e.getMessage()); // 返回错误结果
        }
    }
    
    /**
     * 获取当前学生的论文记录
     * @param session HttpSession对象，用于获取当前登录用户
     * @return Result对象，包含学生论文记录列表
     * @GetMapping("/student")：映射HTTP GET请求到/thesis/student路径
     * @ResponseBody注解：将返回的Result对象转换为JSON格式响应
     * 业务逻辑：
     * 1. 从Session中获取当前登录用户
     * 2. 如果用户未登录，返回错误信息
     * 3. 获取当前学生的所有论文记录
     * 4. 返回论文记录列表
     */
    @GetMapping("/student")
    @ResponseBody
    public Result getStudentThesis(HttpSession session) {
        try {
            // 从Session中获取当前用户
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return Result.error("未登录"); // 未登录，返回错误信息
            }
            
            // 获取当前学生的论文记录
            List<Thesis> thesisList = thesisService.findByStudent(user.getId());
            return Result.success(thesisList); // 返回成功结果，包含论文记录列表
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈信息
            return Result.error("获取论文记录失败：" + e.getMessage()); // 返回错误结果
        }
    }
    
    /**
     * 更新论文记录
     * @param id 论文ID，从URL路径中获取
     * @param file 上传的文件
     * @param title 论文标题
     * @param projectId 项目ID
     * @param session HttpSession对象，用于获取当前登录用户
     * @return Result对象，包含更新结果
     * @PostMapping("/update/{id}")：映射HTTP POST请求到/thesis/update/{id}路径
     * @ResponseBody注解：将返回的Result对象转换为JSON格式响应
     * @RequestParam注解：获取请求参数，支持文件上传
     * @PathVariable注解：获取URL路径中的参数
     * 业务逻辑：
     * 1. 从Session中获取当前登录用户
     * 2. 验证用户是否登录
     * 3. 验证用户是否有权限更新该论文（只能更新自己的论文）
     * 4. 确保上传目录存在
     * 5. 生成唯一文件名并保存文件
     * 6. 更新论文记录
     * 7. 返回更新结果
     */
    @PostMapping("/update/{id}")
    @ResponseBody
    public Result updateThesis(@PathVariable Integer id, 
                              @RequestParam("file") MultipartFile file, 
                              @RequestParam("title") String title, 
                              @RequestParam("projectId") Integer projectId, 
                              HttpSession session) {
        try {
            // 从Session中获取当前用户
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return Result.error("未登录"); // 未登录，返回错误信息
            }
            
            // 验证权限：只能更新自己的论文
            Thesis existingThesis = thesisService.findById(id);
            if (existingThesis == null) {
                return Result.error("论文不存在"); // 论文不存在，返回错误信息
            }
            if (!user.getId().equals(existingThesis.getStudentId())) {
                return Result.error("无权更新该论文"); // 无权限，返回错误信息
            }
            
            // 确保上传目录存在
            String uploadRoot = System.getProperty("user.dir") + "/thesis_uploads";
            File dir = new File(uploadRoot);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    return Result.error("创建上传目录失败"); // 创建目录失败，返回错误信息
                }
            }
            
            // 生成唯一文件名（UUID + 原文件扩展名）
            String orig = file.getOriginalFilename();
            String ext = "";
            int i = orig.lastIndexOf('.');
            if (i >= 0) ext = orig.substring(i);
            String fname = UUID.randomUUID().toString() + ext;
            File dest = new File(dir, fname);
            file.transferTo(dest);
            
            // 更新论文记录
            existingThesis.setTitle(title);
            existingThesis.setProjectId(projectId);
            existingThesis.setFileName(orig);
            existingThesis.setFilePath(dest.getAbsolutePath());
            existingThesis.setStatus("UPLOADED");
            existingThesis.setUploadTime(new Date());
            existingThesis.setFileSize(file.getSize()); // 设置文件大小（字节）
            String fileExt = ext.substring(1).toUpperCase(); // 获取文件扩展名，如PDF、DOCX等
            existingThesis.setFileType(fileExt); // 设置文件类型
            
            // 调用服务层方法更新论文
            int result = thesisService.updateThesis(existingThesis);
            if (result > 0) {
                return Result.success("论文更新成功"); // 更新成功，返回成功信息
            } else {
                return Result.error("论文更新失败"); // 更新失败，返回失败信息
            }
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈信息
            return Result.error("论文更新失败：" + e.getMessage()); // 返回错误结果
        }
    }
    
    /**
     * 下载论文
     * @param id 论文ID，从URL路径中获取
     * @param response HttpServletResponse对象，用于设置响应头和写入文件内容
     * @GetMapping("/download/{id}")：映射HTTP GET请求到/thesis/download/{id}路径
     * @PathVariable注解：获取URL路径中的参数
     * 业务逻辑：
     * 1. 根据ID查询论文
     * 2. 如果论文不存在，返回404错误
     * 3. 检查文件是否存在
     * 4. 如果文件不存在，返回404错误
     * 5. 设置响应头（内容类型、文件名、文件大小）
     * 6. 读取文件并写入响应
     */
    @GetMapping("/download/{id}")
    public void downloadThesis(@PathVariable Integer id, HttpServletResponse response) {
        try {
            // 获取论文详情
            Thesis thesis = thesisService.findById(id);
            if (thesis == null) {
                response.setStatus(404);
                response.getWriter().write("论文不存在");
                return;
            }
            
            // 获取文件路径
            String filePath = thesis.getFilePath();
            File file = new File(filePath);
            if (!file.exists()) {
                response.setStatus(404);
                response.getWriter().write("文件不存在");
                return;
            }
            
            // 设置响应头
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(thesis.getFileName(), "UTF-8"));
            response.setHeader("Content-Length", String.valueOf(file.length()));
            
            // 读取文件并写入响应
            try (InputStream in = new FileInputStream(file);
                 OutputStream out = response.getOutputStream()) {
                byte[] buffer = new byte[1024 * 8];
                int len;
                while ((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.setStatus(500);
                response.getWriter().write("下载失败：" + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
