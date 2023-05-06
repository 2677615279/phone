package com.past.beans;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 通用分页查询对象
 */
@Data
public class PageRequest<T> implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -4222463573940228842L;

    /**
     * 当前页
     */
    @NotNull(message = "当前页码不能为null！")
    @Min(value = 1L, message = "当前页码最小值为1，且必须是正整数！")
    private Integer pageNum = 1;

    /**
     * 每页显示数目
     */
    @NotNull(message = "每页显示数目不能为null！")
    @Max(value = 100L, message = "每页显示数目最大值为100，且必须是正整数！")
    @Min(value = 1L, message = "每页显示数目最小值为1，且必须是正整数！")
    private Integer pageSize = 10;

    /**
     * 动态查询条件
     */
    @Valid
    private T query;

}