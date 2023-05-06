package com.past.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 购物车实体类 ： 属性和数据库字段一一对应
 */
@Data
public class Carts implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -796647996956078257L;

    /**
    * 购物车 主键id
    */
    private Long id;

    /**
    * 商品 关联商品的主键id
    */
    private Long goodsId;

    /**
    * 商品的数量
    */
    private Integer num;

    /**
    * 商品单价
    */
    private Double price;

    /**
    * 所属的用户 关联用户的主键id
    */
    private Long userId;

    /**
    * 状态 0正常 1删除
    */
    private Integer status = 0;

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