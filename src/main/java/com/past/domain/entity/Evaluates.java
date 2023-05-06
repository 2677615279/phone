package com.past.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 评价实体类 ： 属性和数据库字段一一对应
 */
@Data
public class Evaluates implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -8817779419239241695L;

    /**
    * 评价 主键id
    */
    private Long id;

    /**
    * 评价人 关联用户的主键id
    */
    private Long userId;

    /**
    * 评价内容
    */
    private String content;

    /**
    * 评价日
    */
    private Date day;

    /**
    * 评价的商品 关联的商品主键id
    */
    private Long goodsId;

    /**
    * 评价等级 1~5  1星、2星、3星、4星、5星
    */
    private Integer level;

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