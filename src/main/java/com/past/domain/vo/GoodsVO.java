package com.past.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 商品VO实体：给前端界面展示层返回的信息
 */
@Data
public class GoodsVO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -5370586583493679005L;

    /**
     * 商品 主键id
     */
    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品价格
     */
    private Double price;

    /**
     * 商品库存
     */
    private Long num;

    /**
     * 商品类别
     */
    private GoodsTypesVO type;

    /**
     * 商品内存
     */
    private MemoryVO memory;

    /**
     * 商品颜色
     */
    private String color;

    /**
     * 商品样图
     */
    private String img;

    /**
     * 状态 0正常 1删除 2热卖
     */
    private Integer status = 0;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品销量
     */
    private Long volume = 0L;

    /**
     * 商品的评价集合
     */
    private List<EvaluatesVO> evaluatesVOList;

}
