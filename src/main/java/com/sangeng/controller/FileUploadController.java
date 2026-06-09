package com.sangeng.controller;
import com.sangeng.pojo.Thesis;
import com.sangeng.service.ThesisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传控制器
 * 处理论文文件的上传请求
 * @Controller注解：标记该类为Spring MVC控制器，由Spring容器管理
 * @RequestMapping("/thesis")：设置控制器的基础URL路径，所有请求路径都以/thesis开头
 * @CrossOrigin注解：处理跨域请求，允许来自http://localhost:5173的请求，支持凭证传递和所有HTTP方法
 */
@Controller
@RequestMapping("/thesis")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class FileUploadController {
    
    /**
     * @Autowired注解：自动注入ThesisService实例
     * 用于调用论文服务的uploadThesis方法保存论文信息
     */
    @Autowired private ThesisService thesisService;
    
    /**
     * 上传文件的根目录
     * 使用项目根目录下的thesis_uploads文件夹作为上传目录
     */
    private final String uploadRoot = System.getProperty("user.dir") + "/thesis_uploads"; // 使用项目根目录下的thesis_uploads文件夹

    /**
     * 处理论文文件上传请求
     * @param file 上传的文件
     * @param title 论文标题
     * @param projectId 项目ID
     * @param session HttpSession对象，用于获取当前登录用户
     * @return Result对象，包含上传结果
     * @PostMapping("/upload")：映射HTTP POST请求到/thesis/upload路径
     * @ResponseBody注解：将返回的Result对象转换为JSON格式响应
     * @RequestParam注解：获取请求参数，支持文件上传
     * 业务逻辑：
     * 1. 从Session中获取当前登录用户
     * 2. 验证用户是否为学生角色
     * 3. 验证文件是否为空
     * 4. 确保上传目录存在，如果不存在则创建
     * 5. 生成唯一文件名
     * 6. 保存文件到上传目录
     * 7. 创建Thesis对象，设置相关属性
     * 8. 调用服务层方法保存论文信息
     * 9. 返回上传结果
     */
    @PostMapping("/upload")
    @ResponseBody
    public com.sangeng.common.Result upload(@RequestParam("file") MultipartFile file, @RequestParam("title") String title, @RequestParam("projectId") Integer projectId, HttpSession session) {
        try {
            // 从Session中获取当前登录用户
            com.sangeng.pojo.User user = (com.sangeng.pojo.User) session.getAttribute("user");
            // 验证用户是否为学生角色
            if(user==null || !"STUDENT".equals(user.getRole())) {
                return com.sangeng.common.Result.error("未授权操作"); // 未授权操作，返回错误信息
            }
            // 验证文件是否为空
            if(file.isEmpty()) {
                return com.sangeng.common.Result.error("文件不能为空"); // 文件为空，返回错误信息
            }
            
            // 确保上传路径存在
            File dir = new File(uploadRoot);
            if(!dir.exists()) {
                boolean created = dir.mkdirs();
                if(!created) {
                    return com.sangeng.common.Result.error("创建上传目录失败"); // 创建目录失败，返回错误信息
                }
            }
            
            // 生成唯一文件名
            String orig = file.getOriginalFilename(); // 获取原文件名
            String ext = ""; // 文件扩展名
            int i = orig.lastIndexOf('.'); // 查找最后一个点的位置
            if(i>=0) ext = orig.substring(i); // 获取文件扩展名
            String fname = UUID.randomUUID().toString()+ext; // 使用UUID生成唯一文件名
            File dest = new File(dir,fname); // 目标文件
            file.transferTo(dest); // 保存文件
            
            // 创建Thesis对象，设置相关属性
            Thesis t = new Thesis();
            t.setStudentId(user.getId()); // 设置学生ID
            t.setFileName(orig); // 设置原文件名
            t.setFilePath(dest.getAbsolutePath()); // 设置文件路径
            t.setStatus("UPLOADED"); // 设置状态为已上传
            t.setTitle(title); // 设置论文标题
            t.setProjectId(projectId); // 设置项目ID
            t.setFileSize(file.getSize()); // 设置文件大小（字节）
            t.setFileType(ext.substring(1).toUpperCase()); // 设置文件类型，如PDF、DOCX等
            thesisService.uploadThesis(t); // 调用服务层方法保存论文信息
            return com.sangeng.common.Result.success("上传成功"); // 上传成功，返回成功信息
        } catch (IOException e) {
            e.printStackTrace(); // 打印异常堆栈信息
            return com.sangeng.common.Result.error("文件上传失败：" + e.getMessage()); // 文件上传失败，返回错误信息
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈信息
            return com.sangeng.common.Result.error("上传失败：" + e.getMessage()); // 上传失败，返回错误信息
        }
    }
}