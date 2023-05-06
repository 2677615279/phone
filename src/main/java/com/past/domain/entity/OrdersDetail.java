package com.past.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 订单详情实体类 ： 属性和数据库字段一一对应
 */
@Data
public class OrdersDetail implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -478258882561401974L;

    /**
    * 订单详情 主键id
    */
    private Long id;

    /**
    * 订单的主键id
    */
    private String orderId;

    /**
    * 下单商品的主键id
    */
    private Long goodsId;

    /**
    * 商品单价
    */
    private Double price;

    /**
    * 下单量
    */
    private Long num;

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