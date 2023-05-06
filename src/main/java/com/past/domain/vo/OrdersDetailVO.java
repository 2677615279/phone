package com.past.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单详情VO实体：给前端界面展示层返回的信息
 */
@Data
public class OrdersDetailVO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -3948720130547065929L;

    /**
     * 订单详情 主键id
     */
    private Long id;

    /**
     * 订单
     */
    private OrdersVO orders;

    /**
     * 下单商品
     */
    private GoodsVO goods;

    /**
     * 商品单价
     */
    private Double price;

    /**
     * 下单量
     */
    private Long num;

}
