package com.past.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 购物车VO实体：给前端界面展示层返回的信息
 */
@Data
public class CartsVO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 5284934965552575768L;

    /**
     * 购物车 主键id
     */
    private Long id;

    /**
     * 商品 关联商品的主键id
     */
    private GoodsVO goods;

    /**
     * 商品的数量
     */
    private Integer num;

    /**
     * 商品单价
     */
    private Double price;

    /**
     * 所属的用户 关联用户的主键id
     */
    private UsersVO user;

    /**
     * 状态 0正常 1删除
     */
    private Integer status = 0;

}
