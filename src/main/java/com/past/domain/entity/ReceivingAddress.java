package com.past.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 收货地址实体类 ： 属性和数据库字段一一对应
 */
@Data
public class ReceivingAddress implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -6971350539316152255L;

    /**
    * 收货地址 主键id
    */
    private Long id;

    /**
    * 省份名称
    */
    private String province;

    /**
    * 城市名称
    */
    private String city;

    /**
    * 区县名称
    */
    private String area;

    /**
    * 详细地址
    */
    private String detail;

    /**
    * 关联的用户主键id，该地址所属的用户
    */
    private Long userId;

    /**
    * 收货人手机
    */
    private String phone;

    /**
    * 收货人昵称
    */
    private String nickName;

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