package com.example.modbus.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * HTTP 接口统一响应对象。
 *
 * @param <T> data 字段的实际数据类型
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    /** 业务状态码，200 表示成功。 */
    private int code;
    /** 返回消息，成功时通常是 success。 */
    private String message;
    /** 接口返回数据；没有数据时不输出。 */
    private T data;

    public ApiResponse() {
    }

    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 构造成功响应并携带数据。
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    /**
     * 构造成功响应，只返回提示消息。
     */
    public static ApiResponse<Void> success(String message) {
        return new ApiResponse<>(200, message, null);
    }

    /**
     * 构造失败响应。
     */
    public static ApiResponse<Void> fail(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
