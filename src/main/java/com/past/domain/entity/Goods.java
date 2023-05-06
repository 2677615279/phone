package com.past.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品实体类 ： 属性和数据库字段一一对应
 */
@Data
public class Goods implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -7430687642054773161L;

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
    * 商品类别 关联的分类id
    */
    private Long type;

    /**
    * 商品内存 关联的内存id
    */
    private Long memory;

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
    * 创建时间
    */
    private Date createTime;

    /**
    * 最后一次的更新时间
    */
    private Date updateTime;

    /**
    * 最后一次的操作者名称
    */
    private String operator;

    /**
    * 最后一次的操作者ip
    */
    private String operatorIp;

}