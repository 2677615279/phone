package com.past.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 订单实体类 ： 属性和数据库字段一一对应
 */
@Data
public class Orders implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -428533436881708490L;

    /**
    * 订单 主键id
    */
    private String id;

    /**
    * 下单用户的主键id
    */
    private Long userId;

    /**
    * 下单日
    */
    private Date day;

    /**
    * 订单总价
    */
    private Double price;

    /**
    * 收货人姓名
    */
    private String username;

    /**
    * 手机号
    */
    private String phone;

    /**
    * 收货地址
    */
    private String address;

    /**
    * 快递单号
    */
    private String expressNo;

    /**
    * 状态 -1删除 1待付款 2待发货 3待收货 4待评价 5完成
    */
    private Integer status = 1;

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