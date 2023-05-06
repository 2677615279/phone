package com.past.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 城市VO实体：给前端界面展示层返回的信息
 */
@Data
public class CitiesVO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -9195882783881865255L;

    /**
     * 城市 主键id
     */
    private Long id;

    /**
     * 市名
     */
    private String name;

    /**
     * 所属省份
     */
    private ProvincesVO province;

    /**
     * 状态 0正常 1删除 2禁用
     */
    private Integer status = 0;

}
