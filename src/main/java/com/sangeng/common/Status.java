package com.sangeng.common;

/**
 * 响应状态码枚举
 * 作用：定义系统中所有API接口的响应状态码
 * 说明：包含HTTP标准状态码和自定义业务状态码
 */
public enum Status {
    /**
     * 成功状态码
     * 作用：表示接口调用成功
     * 说明：HTTP 200 OK
     */
    SUCCESS(200, "操作成功"),
    
    /**
     * 服务器错误
     * 作用：表示服务器内部发生错误
     * 说明：HTTP 500 Internal Server Error
     */
    ERROR(500, "操作失败"),
    
    /**
     * 参数错误
     * 作用：表示客户端提交的参数不符合要求
     * 说明：HTTP 400 Bad Request
     */
    PARAM_ERROR(400, "参数错误"),
    
    /**
     * 资源未找到
     * 作用：表示请求的资源不存在
     * 说明：HTTP 404 Not Found
     */
    NOT_FOUND(404, "资源未找到"),
    
    /**
     * 未授权
     * 作用：表示请求需要用户认证
     * 说明：HTTP 401 Unauthorized
     */
    UNAUTHORIZED(401, "未授权"),
    
    /**
     * 禁止访问
     * 作用：表示服务器拒绝请求
     * 说明：HTTP 403 Forbidden
     */
    FORBIDDEN(403, "禁止访问"),
    
    /**
     * 业务错误
     * 作用：表示业务逻辑错误，如用户名已存在、余额不足等
     * 说明：自定义状态码600，用于具体业务场景的错误提示
     */
    BUSINESS_ERROR(600, "业务错误");

    /**
     * 状态码数值
     */
    private int code;
    
    /**
     * 状态码对应的提示信息
     */
    private String message;

    /**
     * 构造方法
     * @param code 状态码数值
     * @param message 状态码对应的提示信息
     */
    Status(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 获取状态码数值
     * @return 状态码数值
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取状态码对应的提示信息
     * @return 提示信息
     */
    public String getMessage() {
        return message;
    }
}