package com.past.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 内存VO实体：给前端界面展示层返回的信息
 */
@Data
public class MemoryVO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -7132961358764767490L;

    /**
     * 内存 主键id
     */
    private Long id;

    /**
     * 内存名称，即多少GB
     */
    private String name;

    /**
     * 状态 0正常 1删除 2禁用
     */
    private Integer status = 0;

}
