package com.past.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 猜想推送实体类 ： 属性和数据库字段一一对应
 * 按照商品点击量定制猜你喜欢
 */
@Data
public class Guess implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 5080498185313533246L;

    /**
    * 猜想 主键id
    */
    private Long id;

    /**
    * 推送的商品 关联的商品主键id
    */
    private Long goodsId;

    /**
    * 该商品点击量 即当前登录用户浏览该商品的次数
    */
    private Integer num;

    /**
    * 是否喜欢并收藏  1收藏 -1未收藏
    */
    private Integer favorite = -1;

    /**
    * 推送的用户 关联的用户主键id
    */
    private Long userId;

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