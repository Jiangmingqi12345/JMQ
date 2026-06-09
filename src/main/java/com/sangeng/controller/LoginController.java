package com.sangeng.controller;

import com.sangeng.common.Result;
import com.sangeng.pojo.User;
import com.sangeng.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;

/**
 * 登录控制器
 * 处理用户登录相关的请求
 * @Controller注解：标记该类为Spring MVC控制器，由Spring容器管理
 * @RequestMapping("/login")：设置控制器的基础URL路径，所有请求路径都以/login开头
 * @CrossOrigin注解：处理跨域请求，允许来自http://localhost:5173的请求，支持凭证传递和所有HTTP方法
 */
@Controller
@RequestMapping("/login")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class LoginController {
    
    /**
     * @Autowired注解：自动注入UserService实例
     * 用于调用用户服务的登录方法
     */
    @Autowired
    private UserService userService;

    /**
     * 登录检查方法
     * @param user 包含用户名和密码的User对象，由@RequestBody注解从请求体中获取
     * @param session HttpSession对象，用于存储登录用户信息
     * @return Result对象，包含登录结果
     * @RequestMapping("/check")：映射URL路径为/login/check
     * @ResponseBody注解：将返回的Result对象转换为JSON格式响应
     * 业务逻辑：
     * 1. 调用UserService的login方法验证用户名和密码
     * 2. 登录成功：将用户信息存入session，返回成功结果
     * 3. 登录失败：返回错误信息
     * 4. 异常处理：捕获所有异常，返回错误信息
     */
    @RequestMapping("/check")
    @ResponseBody
    public Result loginCheck(@RequestBody User user, HttpSession session){
        // 调试日志：打印登录请求信息
        System.out.println("登录请求：用户名=" + user.getUsername() + ", 密码=" + user.getPassword());
        try {
            // 调用UserService的login方法验证用户名和密码
            User loggedInUser = userService.login(user.getUsername(), user.getPassword());
            // 调试日志：打印登录结果
            System.out.println("登录结果：" + loggedInUser);
            
            // 登录成功：将用户信息存入session，返回成功结果
            if(loggedInUser!=null){
                session.setAttribute("user", loggedInUser);
                return Result.success(loggedInUser);
            }
            // 登录失败：返回错误信息
            return Result.error("用户名或密码错误");
        } catch (Exception e) {
            // 异常处理：打印异常信息，返回错误结果
            System.out.println("登录异常：" + e.getMessage());
            e.printStackTrace();
            return Result.error("登录失败：" + e.getMessage());
        }
    }
    
    /**
     * 登录页访问入口
     * @return Result对象，包含访问结果
     * @RequestMapping(value="/entry")：映射URL路径为/login/entry
     * @ResponseBody注解：将返回的Result对象转换为JSON格式响应
     * 业务逻辑：返回登录页访问成功的消息
     */
    @RequestMapping(value="/entry")
    @ResponseBody
    public Result entry(){
        return Result.success("登录页访问成功");
    }
    
    /**
     * 检查登录状态
     * @param user 从session中获取的User对象，由@SessionAttribute("user")注解自动注入
     * @return Result对象，包含登录状态检查结果
     * @RequestMapping(value="/check-login")：映射URL路径为/login/check-login
     * @ResponseBody注解：将返回的Result对象转换为JSON格式响应
     * 业务逻辑：
     * 1. 如果session中存在user对象，返回登录状态有效
     * 2. 否则返回登录状态无效
     */
    @RequestMapping(value="/check-login")
    @ResponseBody
    public Result checkLogin(@SessionAttribute("user") User user){
        if(user != null){
            return Result.success("登录状态有效");
        }
        return Result.error("登录状态无效");
    }
    
    /**
     * 处理登录状态检查的OPTIONS请求
     * @return Result对象，包含OPTIONS请求处理结果
     * @RequestMapping(value="/check-login", method=RequestMethod.OPTIONS)：映射OPTIONS请求到/login/check-login
     * @ResponseBody注解：将返回的Result对象转换为JSON格式响应
     * 业务逻辑：处理跨域预检请求，返回成功消息
     */
    @RequestMapping(value="/check-login", method=RequestMethod.OPTIONS)
    @ResponseBody
    public Result checkLoginOptions(){
        return Result.success("登录状态检查前置请求成功");
    }

}
