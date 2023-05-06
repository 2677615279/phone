package com.past.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Banner图实体类 ： 属性和数据库字段一一对应
 */
@Data
public class Banners implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 3328979652805663551L;

    /**
    * banner图 主键id
    */
    private Long id;

    /**
    * banner图名称
    */
    private String name;

    /**
    * banner图点击后跳转的商品详情url
    */
    private String url;

    /**
    * banner图片名称
    */
    private String img;

    /**
    * 状态 0正常 1删除 2禁用
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