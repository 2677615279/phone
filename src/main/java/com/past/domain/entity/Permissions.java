package com.past.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 权限实体类 ： 属性和数据库字段一一对应
 */
@Data
public class Permissions implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 6175144888564511959L;

    /**
    * 权限 主键id
    */
    private Long id;

    /**
    * 权限名称
    */
    private String name;

    /**
    * 权限url
    */
    private String url;

    /**
    * 权限的父id 默认0
    */
    private Long parentId = 0L;

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