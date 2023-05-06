package com.past.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 内存实体类 ： 属性和数据库字段一一对应
 */
@Data
public class Memory implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -6240848322383208027L;

    /**
    * 内存 主键id
    */
    private Long id;

    /**
    * 内存名称，即多少GB
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
    * 最后一次的操作者名称
    */
    private String operator;

    /**
    * 最后一次的操作者ip
    */
    private String operatorIp;

}