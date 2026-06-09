# 毕业设计管理系统 - 后端说明文档

## 1. 项目概述

本项目是一个基于SSM（Spring + Spring MVC + MyBatis）框架开发的毕业设计管理系统，主要用于管理学生的毕业设计选题和论文提交评审流程。系统支持学生、教师和管理员三种角色，实现了用户管理、项目管理、选题管理和论文管理等核心功能。

## 2. 技术栈

- **后端框架**：SSM（Spring + Spring MVC + MyBatis）
- **数据库**：MySQL
- **构建工具**：Maven
- **开发工具**：IntelliJ IDEA
- **前端框架**：Vue 3 + Element Plus

## 3. 项目结构

### 3.1 目录结构

```
MyDemo5/                          # 项目根目录
├── src/                          # 源代码目录
│   ├── main/                     # 主代码目录
│   │   ├── java/                  # Java源代码
│   │   │   └── com/              # 包名
│   │   │       └── sangeng/       # 项目包名
│   │   │           ├── controller/    # 控制器层
│   │   │           ├── common/        # 通用工具类
│   │   │           ├── mapper/        # Mapper接口
│   │   │           ├── pojo/          # 实体类
│   │   │           └── service/       # 服务层
│   │   │               └── impl/      # 服务实现类
│   │   └── resources/             # 资源文件
│   │       ├── com/               # Mapper XML文件
│   │       │   └── sangeng/       # 与Java包结构对应
│   │       │       └── mapper/    # Mapper XML文件
│   │       ├── applicationContext.xml  # Spring配置文件
│   │       ├── db.properties        # 数据库配置文件
│   │       ├── mybatis-config.xml   # MyBatis配置文件
│   │       └── spring-mvc.xml       # Spring MVC配置文件
│   └── test/                     # 测试代码目录
├── thesis_uploads/                # 论文上传目录
├── pom.xml                        # Maven配置文件
└── README.md                       # 项目说明文档
```

### 3.2 核心组件说明

#### 3.2.1 配置文件

- **applicationContext.xml**：Spring核心配置文件，定义了数据源、事务管理器、MyBatis整合等配置
- **mybatis-config.xml**：MyBatis配置文件，定义了日志实现、驼峰命名映射等配置
- **db.properties**：数据库连接配置，包含数据库URL、用户名、密码等

#### 3.2.2 通用工具类

- **Result.java**：统一响应结果封装，包含状态码、消息和数据
- **Status.java**：响应状态码枚举，定义了成功、错误、参数错误等状态

#### 3.2.3 实体类

- **User.java**：用户实体类，包含用户基本信息
- **Project.java**：项目实体类，包含项目基本信息
- **Selection.java**：选题实体类，记录学生的选题申请
- **Thesis.java**：论文实体类，记录学生的论文信息

#### 3.2.4 Mapper层

Mapper层负责与数据库交互，包含接口和XML文件：

- **UserDao.java/UserDao.xml**：用户数据访问
- **ProjectDao.java/ProjectDao.xml**：项目数据访问
- **SelectionDao.java/SelectionDao.xml**：选题数据访问
- **ThesisDao.java/ThesisDao.xml**：论文数据访问

#### 3.2.5 Service层

Service层负责处理业务逻辑，包含接口和实现类：

- **UserService/UserServiceImpl**：用户业务逻辑
- **ProjectService/ProjectServiceImpl**：项目业务逻辑
- **SelectionService/SelectionServiceImpl**：选题业务逻辑
- **ThesisService/ThesisServiceImpl**：论文业务逻辑

#### 3.2.6 Controller层

Controller层负责处理HTTP请求，返回响应结果：

- **LoginController**：处理用户登录相关请求
- **UserController**：处理用户管理相关请求
- **ProjectController**：处理项目管理相关请求
- **SelectionController**：处理选题管理相关请求
- **ThesisController**：处理论文管理相关请求
- **FileUploadController**：处理文件上传相关请求

## 4. 业务流程

### 4.1 用户登录流程

1. 用户在前端输入用户名和密码
2. 前端发送POST请求到`/login/check`
3. LoginController接收请求，调用UserService的login方法
4. UserService调用UserDao的findByUsername方法查询用户
5. 验证密码是否匹配
6. 登录成功：将用户信息存入Session，返回成功响应
7. 登录失败：返回错误信息

### 4.2 项目管理流程

#### 4.2.1 教师发布项目

1. 教师登录系统
2. 进入项目管理页面，点击"发布项目"
3. 填写项目信息，点击"提交"
4. 前端发送POST请求到`/projects`
5. ProjectController接收请求，调用ProjectService的create方法
6. ProjectService调用ProjectDao的insert方法插入项目信息
7. 返回操作结果

#### 4.2.2 学生选题

1. 学生登录系统
2. 进入项目列表页面，浏览开放的项目
3. 选择感兴趣的项目，点击"申请选题"
4. 填写选题理由，点击"提交"
5. 前端发送POST请求到`/projects/{id}/apply`
6. ProjectController接收请求，调用SelectionService的apply方法
7. SelectionService进行防重检查和项目容量检查
8. 检查通过：创建选题记录，更新项目已选人数
9. 返回申请结果

### 4.3 选题审核流程

1. 教师登录系统
2. 进入选题管理页面，查看待审核的选题申请
3. 选择一个申请，点击"审核"
4. 选择"通过"或"拒绝"，填写审核理由
5. 前端发送PUT请求到`/selections/{id}/approve`或`/selections/{id}/reject`
6. SelectionController接收请求，调用SelectionService的approve或reject方法
7. SelectionService更新选题状态
8. 返回审核结果

### 4.4 论文提交流程

1. 学生登录系统
2. 进入论文管理页面，点击"上传论文"
3. 填写论文标题，选择项目，上传论文文件
4. 前端发送POST请求到`/thesis/upload`
5. FileUploadController接收请求，验证用户权限
6. 生成唯一文件名，保存文件到服务器
7. 创建论文记录，设置状态为"已上传"
8. 返回上传结果

### 4.5 论文评审流程

1. 教师登录系统
2. 进入论文管理页面，查看待评审的论文
3. 选择一篇论文，点击"评审"
4. 下载论文文件，进行评审
5. 填写评审结果和评审理由
6. 前端发送POST请求到`/thesis/review/{id}`
7. ThesisController接收请求，调用ThesisService的reviewThesis方法
8. ThesisService更新论文状态和评审理由
9. 返回评审结果

## 5. 核心功能说明

### 5.1 用户管理

- **登录**：验证用户名和密码，创建会话
- **注册**：创建新用户
- **更新信息**：修改用户基本信息
- **修改密码**：更新用户密码
- **用户列表**：查询所有用户，支持分页和搜索

### 5.2 项目管理

- **发布项目**：教师发布新项目
- **更新项目**：修改项目信息
- **删除项目**：删除指定项目
- **项目列表**：查询项目，支持分页、搜索和状态过滤
- **开放/关闭项目**：更新项目状态，控制选题开放

### 5.3 选题管理

- **申请选题**：学生申请选择项目
- **审核选题**：教师审核学生的选题申请
- **取消选题**：学生取消已申请的选题
- **选题列表**：查询选题记录，支持分页和状态过滤

### 5.4 论文管理

- **上传论文**：学生上传论文文件
- **更新论文**：修改已上传的论文
- **下载论文**：下载论文文件
- **论文评审**：教师评审学生的论文
- **重新上传**：学生重新上传被拒绝的论文
- **论文列表**：查询论文记录，支持分页、搜索和状态过滤

## 6. 数据库设计

### 6.1 用户表（user_account）

| 字段名 | 数据类型 | 描述 | 约束 |
| --- | --- | --- | --- |
| id | int | 用户ID | 主键，自增 |
| username | varchar(50) | 用户名 | 唯一，非空 |
| password | varchar(50) | 密码 | 非空 |
| name | varchar(50) | 姓名 | 非空 |
| role | varchar(20) | 角色 | 非空（STUDENT/TEACHER/ADMIN） |
| email | varchar(100) | 邮箱 |  |
| major | varchar(50) | 专业 |  |
| create_time | datetime | 创建时间 | 非空 |
| update_time | datetime | 更新时间 |  |

### 6.2 项目表（project）

| 字段名 | 数据类型 | 描述 | 约束 |
| --- | --- | --- | --- |
| id | int | 项目ID | 主键，自增 |
| title | varchar(200) | 项目标题 | 非空 |
| description | text | 项目描述 | 非空 |
| teacher_id | int | 教师ID | 外键，关联user_account表 |
| max_students | int | 最大学生数 | 非空 |
| selected_count | int | 已选学生数 | 非空，默认0 |
| status | int | 状态 | 非空（0-关闭，1-开放） |
| create_time | datetime | 创建时间 | 非空 |
| update_time | datetime | 更新时间 |  |

### 6.3 选题表（selection）

| 字段名 | 数据类型 | 描述 | 约束 |
| --- | --- | --- | --- |
| id | int | 选题ID | 主键，自增 |
| student_id | int | 学生ID | 外键，关联user_account表 |
| project_id | int | 项目ID | 外键，关联project表 |
| status | varchar(20) | 状态 | 非空（PENDING-待审核，APPROVED-通过，REJECTED-拒绝） |
| select_time | datetime | 选题时间 | 非空 |
| reason | varchar(500) | 选题理由 |  |

### 6.4 论文表（thesis）

| 字段名 | 数据类型 | 描述 | 约束 |
| --- | --- | --- | --- |
| id | int | 论文ID | 主键，自增 |
| student_id | int | 学生ID | 外键，关联user_account表 |
| file_name | varchar(200) | 文件名 | 非空 |
| file_path | varchar(500) | 文件路径 | 非空 |
| status | varchar(20) | 状态 | 非空（UPLOADED-已上传，REVIEWING-审核中，PASSED-通过，REJECTED-拒绝） |
| upload_time | datetime | 上传时间 | 非空 |
| title | varchar(200) | 论文标题 | 非空 |
| project_id | int | 项目ID | 外键，关联project表 |
| review_reason | varchar(500) | 评审理由 |  |

## 7. 系统部署

### 7.1 环境要求

- JDK 1.8+
- Tomcat 8.5+
- MySQL 5.7+
- Maven 3.6+

### 7.2 部署步骤

1. **创建数据库**：执行SQL脚本创建数据库和表
2. **配置数据库连接**：修改`db.properties`文件中的数据库连接信息
3. **构建项目**：执行`mvn clean package`命令构建项目
4. **部署WAR包**：将生成的WAR包复制到Tomcat的`webapps`目录
5. **启动Tomcat**：启动Tomcat服务器
6. **访问系统**：在浏览器中输入`http://localhost:8080/MyDemo5`

## 8. 接口说明

### 8.1 认证接口

- **登录**：POST `/login/check`
- **检查登录状态**：GET `/login/check-login`

### 8.2 用户接口

- **获取用户列表**：GET `/users/list`
- **获取用户详情**：GET `/users/{id}`
- **添加用户**：POST `/users`
- **更新用户**：PUT `/users/{id}`
- **删除用户**：DELETE `/users/{id}`

### 8.3 项目接口

- **获取开放项目列表**：GET `/projects/list`
- **获取所有项目**：GET `/projects/all`
- **获取教师项目**：GET `/projects/teacher?teacherId={id}`
- **获取项目详情**：GET `/projects/{id}`
- **添加项目**：POST `/projects`
- **更新项目**：PUT `/projects/{id}`
- **删除项目**：DELETE `/projects/{id}`
- **申请选题**：POST `/projects/{id}/apply`
- **更新项目状态**：PUT `/projects/{id}/status?status={status}`

### 8.4 选题接口

- **获取选题列表**：GET `/selections/list`
- **获取选题详情**：GET `/selections/{id}`
- **审核通过**：PUT `/selections/{id}/approve`
- **拒绝选题**：PUT `/selections/{id}/reject?reason={reason}`
- **获取学生选题**：GET `/selections/student`
- **取消选题**：DELETE `/selections/{id}/cancel`

### 8.5 论文接口

- **获取论文列表**：GET `/thesis/list`
- **获取论文详情**：GET `/thesis/detail/{id}`
- **上传论文**：POST `/thesis/upload`
- **更新论文**：POST `/thesis/update/{id}`
- **下载论文**：GET `/thesis/download/{id}`
- **提交评审**：POST `/thesis/review/{id}`
- **重新上传**：POST `/thesis/reupload/{id}`
- **获取学生论文**：GET `/thesis/student`

## 9. 注意事项

1. **文件上传**：论文文件上传到项目根目录下的`thesis_uploads`文件夹，请确保该文件夹存在且有写入权限
2. **跨域配置**：系统已配置跨域支持，允许来自`http://localhost:5173`的请求
3. **事务管理**：关键业务操作（如选题申请、论文评审）已添加事务管理，确保数据一致性
4. **日志配置**：系统使用Log4j进行日志记录，日志文件位于`logs`文件夹
5. **安全性**：系统实现了基于角色的访问控制，不同角色具有不同的操作权限

## 10. 总结

本系统实现了毕业设计管理的全流程，包括用户管理、项目管理、选题管理和论文管理等核心功能。系统采用SSM框架开发，具有良好的扩展性和可维护性。通过本系统，可以有效提高毕业设计管理的效率，规范毕业设计的流程，为学生、教师和管理员提供便捷的服务。