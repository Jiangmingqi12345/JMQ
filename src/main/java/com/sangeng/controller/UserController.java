package com.sangeng.controller;

import com.sangeng.common.Result;
import com.sangeng.pojo.User;
import com.sangeng.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 * 处理用户相关的HTTP请求，包括获取用户列表、添加用户、更新用户、删除用户等
 * @RestController注解：组合了@Controller和@ResponseBody，返回值自动转换为JSON格式
 * @RequestMapping("/users")：设置控制器的基础URL路径，所有请求路径都以/users开头
 */
@RestController
@RequestMapping("/users")
public class UserController {

    /**
     * @Autowired注解：自动注入UserService实例
     * 用于调用用户服务的各种方法
     */
    @Autowired
    private UserService userService;

    /**
     * 获取用户列表，支持分页和搜索
     * @param page 当前页码，默认值为1
     * @param size 每页大小，默认值为10
     * @param username 用户名搜索关键字，非必填
     * @param role 角色搜索，非必填
     * @return Result对象，包含用户列表和总数
     * @GetMapping("/list")：映射HTTP GET请求到/users/list路径
     * @RequestParam注解：获取请求参数，支持默认值和必填设置
     * 业务逻辑：
     * 1. 根据条件获取用户列表
     *    - 如果角色参数不为空，则根据角色查询
     *    - 如果用户名校验不为空，则查询所有用户并过滤
     *    - 否则查询所有用户
     * 2. 进行模拟分页
     * 3. 构建结果集，包含分页后的用户列表和总数
     */
    @GetMapping("/list")
    public Result getUserList(@RequestParam(name = "page", defaultValue = "1") Integer page,
                              @RequestParam(name = "size", defaultValue = "10") Integer size,
                              @RequestParam(name = "username", required = false) String username,
                              @RequestParam(name = "role", required = false) String role) {
        try {
            List<User> userList;
            
            // 根据条件获取用户列表
            if (role != null && !role.isEmpty()) {
                // 根据角色查询用户
                userList = userService.findByRole(role);
            } else if (username != null && !username.isEmpty()) {
                // 这里需要在UserService中添加findByUsernameLike方法，暂时先用findAll
                userList = userService.findAll();
                // 过滤用户名包含关键字的用户
                userList = userList.stream()
                        .filter(user -> user.getUsername().contains(username))
                        .toList();
            } else {
                // 查询所有用户
                userList = userService.findAll();
            }
            
            // 模拟分页：计算起始索引和结束索引
            int total = userList.size(); // 总记录数
            int startIndex = (page - 1) * size; // 起始索引
            int endIndex = Math.min(startIndex + size, total); // 结束索引，确保不超过总数
            List<User> pageList = userList.subList(startIndex, endIndex); // 分页后的列表
            
            // 返回结果，包含数据和总数
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("data", pageList); // 分页后的用户列表
            resultMap.put("total", total); // 总记录数
            return Result.success(resultMap); // 返回成功结果
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈信息
            return Result.error("获取用户列表失败：" + e.getMessage()); // 返回错误结果
        }
    }

    /**
     * 根据ID获取单个用户信息
     * @param id 用户ID，从URL路径中获取
     * @return Result对象，包含用户信息
     * @GetMapping("/{id}")：映射HTTP GET请求到/users/{id}路径，{id}为路径参数
     * @PathVariable注解：获取URL路径中的参数
     * 业务逻辑：
     * 1. 根据ID查询用户
     * 2. 查询成功：返回用户信息
     * 3. 查询失败：返回用户不存在的错误信息
     */
    @GetMapping("/{id}")
    public Result getUserById(@PathVariable Integer id) {
        try {
            User user = userService.findById(id); // 根据ID查询用户
            if (user != null) {
                return Result.success(user); // 查询成功，返回用户信息
            } else {
                return Result.error("用户不存在"); // 查询失败，返回错误信息
            }
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈信息
            return Result.error("获取用户信息失败：" + e.getMessage()); // 返回错误结果
        }
    }

    /**
     * 添加用户
     * @param user 用户信息，从请求体中获取
     * @return Result对象，包含添加结果
     * @PostMapping：映射HTTP POST请求到/users路径
     * @RequestBody注解：将请求体转换为User对象
     * 业务逻辑：
     * 1. 调用UserService的register方法添加用户
     * 2. 添加成功：返回成功信息
     * 3. 添加失败：返回失败信息
     */
    @PostMapping
    public Result addUser(@RequestBody User user) {
        try {
            boolean success = userService.register(user); // 调用register方法添加用户
            if (success) {
                return Result.success("添加用户成功"); // 添加成功，返回成功信息
            } else {
                return Result.error("添加用户失败"); // 添加失败，返回失败信息
            }
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈信息
            return Result.error("添加用户失败：" + e.getMessage()); // 返回错误结果
        }
    }

    /**
     * 更新用户信息
     * @param id 用户ID，从URL路径中获取
     * @param user 用户信息，从请求体中获取
     * @return Result对象，包含更新结果
     * @PutMapping("/{id}")：映射HTTP PUT请求到/users/{id}路径
     * 业务逻辑：
     * 1. 设置用户ID（确保更新的是指定ID的用户）
     * 2. 调用UserService的updateUserInfo方法更新用户信息
     * 3. 更新成功：返回成功信息
     * 4. 更新失败：返回失败信息
     */
    @PutMapping("/{id}")
    public Result updateUser(@PathVariable Integer id, @RequestBody User user) {
        try {
            // 设置用户ID，确保更新的是指定ID的用户
            user.setId(id);
            boolean success = userService.updateUserInfo(user); // 调用updateUserInfo方法更新用户
            if (success) {
                return Result.success("更新用户成功"); // 更新成功，返回成功信息
            } else {
                return Result.error("更新用户失败"); // 更新失败，返回失败信息
            }
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈信息
            return Result.error("更新用户失败：" + e.getMessage()); // 返回错误结果
        }
    }

    /**
     * 删除用户
     * @param id 用户ID，从URL路径中获取
     * @return Result对象，包含删除结果
     * @DeleteMapping("/{id}")：映射HTTP DELETE请求到/users/{id}路径
     * 业务逻辑：
     * 1. 调用UserService的deleteUser方法删除用户
     * 2. 删除成功：返回成功信息
     * 3. 删除失败：返回失败信息
     */
    @DeleteMapping("/{id}")
    public Result deleteUser(@PathVariable Integer id) {
        try {
            boolean success = userService.deleteUser(id); // 调用deleteUser方法删除用户
            if (success) {
                return Result.success("删除用户成功"); // 删除成功，返回成功信息
            } else {
                return Result.error("删除用户失败"); // 删除失败，返回失败信息
            }
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常堆栈信息
            return Result.error("删除用户失败：" + e.getMessage()); // 返回错误结果
        }
    }
}