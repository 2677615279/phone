package com.past.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 猜想推送VO实体：给前端界面展示层返回的信息
 */
@Data
public class GuessVO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 3494355118050901602L;

    /**
     * 猜想 主键id
     */
    private Long id;

    /**
     * 推送的商品 关联的商品主键id
     */
    private GoodsVO goods;

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
    private UsersVO user;

}
