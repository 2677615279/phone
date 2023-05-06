package com.past.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 商品分类VO实体：给前端界面展示层返回的信息
 */
@Data
public class GoodsTypesVO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 5284778441596137773L;

    /**
     * 商品分类 主键id
     */
    private Long id;

    /**
     * 商品分类名称
     */
    private String name;

    /**
     * 状态 0正常 1删除 2禁用
     */
    private Integer status = 0;

    /**
     * 该类下的商品列表
     */
    private List<GoodsVO> goodsVOList;

}
