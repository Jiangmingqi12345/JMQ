package com.sangeng.common;

import java.io.Serializable;

/**
 * 统一响应结果类
 * 作用：封装所有API接口的返回格式，确保接口返回数据的一致性
 * 说明：实现Serializable接口，支持序列化，方便在网络传输中使用
 */
public class Result implements Serializable {
    // 序列化版本号，用于反序列化时版本匹配
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     * 作用：表示接口调用的结果状态，使用Status枚举中定义的状态码
     */
    private int code; 
    
    /**
     * 提示信息
     * 作用：向客户端返回的提示文本，如"操作成功"、"参数错误"等
     */
    private String message; 
    
    /**
     * 数据
     * 作用：向客户端返回的具体数据，可以是单个对象、列表、分页数据等
     */
    private Object data; 

    /**
     * 默认构造方法
     * 作用：创建一个空的Result对象
     */
    public Result() {
    }

    /**
     * 构造方法
     * 作用：创建一个包含状态码和提示信息的Result对象
     * @param code 状态码
     * @param message 提示信息
     */
    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 构造方法
     * 作用：创建一个包含状态码、提示信息和数据的Result对象
     * @param code 状态码
     * @param message 提示信息
     * @param data 数据
     */
    public Result(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 成功响应相关方法
    
    /**
     * 成功响应
     * 作用：返回一个默认的成功响应，状态码200，提示信息"操作成功"
     * @return Result对象
     */
    public static Result success() {
        return new Result(Status.SUCCESS.getCode(), Status.SUCCESS.getMessage());
    }

    /**
     * 成功响应
     * 作用：返回一个包含数据的成功响应
     * @param data 要返回的数据
     * @return Result对象
     */
    public static Result success(Object data) {
        return new Result(Status.SUCCESS.getCode(), Status.SUCCESS.getMessage(), data);
    }

    /**
     * 成功响应
     * 作用：返回一个包含自定义提示信息和数据的成功响应
     * @param message 自定义提示信息
     * @param data 要返回的数据
     * @return Result对象
     */
    public static Result success(String message, Object data) {
        return new Result(Status.SUCCESS.getCode(), message, data);
    }

    // 失败响应相关方法
    
    /**
     * 失败响应
     * 作用：返回一个默认的失败响应，状态码500，提示信息"操作失败"
     * @return Result对象
     */
    public static Result error() {
        return new Result(Status.ERROR.getCode(), Status.ERROR.getMessage());
    }

    /**
     * 失败响应
     * 作用：返回一个包含自定义提示信息的失败响应
     * @param message 自定义提示信息
     * @return Result对象
     */
    public static Result error(String message) {
        return new Result(Status.ERROR.getCode(), message);
    }

    /**
     * 失败响应
     * 作用：返回一个包含自定义状态码和提示信息的失败响应
     * @param code 自定义状态码
     * @param message 自定义提示信息
     * @return Result对象
     */
    public static Result error(int code, String message) {
        return new Result(code, message);
    }

    // getter和setter方法
    
    /**
     * 获取状态码
     * @return 状态码
     */
    public int getCode() {
        return code;
    }

    /**
     * 设置状态码
     * @param code 状态码
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * 获取提示信息
     * @return 提示信息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置提示信息
     * @param message 提示信息
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 获取数据
     * @return 数据
     */
    public Object getData() {
        return data;
    }

    /**
     * 设置数据
     * @param data 数据
     */
    public void setData(Object data) {
        this.data = data;
    }
}