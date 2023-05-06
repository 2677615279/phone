package com.past.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 区县VO实体：给前端界面展示层返回的信息
 */
@Data
public class AreasVO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 9127795727765135227L;

    /**
     * 区县 主键id
     */
    private Long id;

    /**
     * 区县名称
     */
    private String name;

    /**
     * 所属城市
     */
    private CitiesVO city;

    /**
     * 状态 0正常 1删除 2禁用
     */
    private Integer status = 0;

}
