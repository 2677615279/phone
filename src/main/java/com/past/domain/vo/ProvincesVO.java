package com.past.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 省份VO实体：给前端界面展示层返回的信息
 */
@Data
public class ProvincesVO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -1679219330839883390L;

    /**
     * 省份 主键id
     */
    private Long id;

    /**
     * 省名
     */
    private String name;

    /**
     * 状态 0正常 1删除 2禁用
     */
    private Integer status = 0;

}
