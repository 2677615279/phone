package com.past.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 图评VO实体：给前端界面展示层返回的信息
 */
@Data
public class EvaluatesImagesVO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1067164050813637690L;

    /**
     * 图评 主键id
     */
    private Long id;

    /**
     * 评价图 名称
     */
    private String name;

    /**
     * 状态 0正常 1删除
     */
    private Integer status = 0;

}
