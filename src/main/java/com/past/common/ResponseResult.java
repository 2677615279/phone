package com.past.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 自定义 统一响应返回实体json数据
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@ApiModel(value = "统一返回结果实体", description = "封装统一返回结果信息实体") // 描述实体信息
public class ResponseResult implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -4317481120359789347L;

    /**
     * 请求响应的状态码
     */
    @ApiModelProperty(name = "code", value = "状态码") // 描述实体中的属性
    private int code;

    /**
     * 请求响应的信息
     */
    @ApiModelProperty(name = "message", value = "描述信息")
    private String message;

    /**
     * 请求响应的数据
     */
    @ApiModelProperty(name = "data", value = "响应结果")
    private Object data;

    /**
     * 请求成功的响应内容
     * @param message 请求响应的信息
     * @param data 请求响应的数据
     * @return ResponseResult
     */
    public static ResponseResult success(String message, Object data) {

        return ResponseResult.builder().code(200).message(message).data(data).build();
    }

    /**
     * 请求成功的响应内容
     * @param data 请求响应的数据
     * @return ResponseResult
     */
    public static ResponseResult success(Object data) {

       return ResponseResult.builder().code(200).message("success").data(data).build();
    }

    /**
     * 请求成功的响应内容
     * @return ResponseResult
     */
    public static ResponseResult success() {

        return ResponseResult.builder().code(200).message("success").build();
    }

    /**
     * 请求失败的响应内容，需要自定义失败的状态码
     * @param message 失败的异常信息
     * @param code 自定义失败的状态码
     * @return ResponseResult
     */
    public static ResponseResult error(String message, int code) {

       return ResponseResult.builder().code(code).message(message).build();
    }

    /**
     * 请求失败的响应内容，默认失败的状态码为-1
     * @param message 失败的异常信息
     * @return ResponseResult
     */
    public static ResponseResult error(String message) {

       return ResponseResult.builder().code(-1).message(message).build();
    }

}
