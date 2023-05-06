package com.past.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品分类实体类 ： 属性和数据库字段一一对应
 */
@Data
public class GoodsTypes implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 2751193905379736591L;

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
    * 创建时间
    */
    private Date createTime;

    /**
    * 最后一次的更新时间
    */
    private Date updateTime;

    /**
    * 最后一次操作的操作者名称
    */
    private String operator;

    /**
    * 最后一次操作的操作者ip
    */
    private String operatorIp;

}