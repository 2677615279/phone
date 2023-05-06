package com.past.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 评价图实体类 ： 属性和数据库字段一一对应
 */
@Data
public class EvaluatesImages implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -1558054720594718998L;

    /**
     * 图评 主键id
     */
    private Long id;

    /**
     * 评价图 名称
     */
    private String name;

    /**
     * 关联的评价主键id
     */
    private Long evaluateId;

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