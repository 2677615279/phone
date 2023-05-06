package com.past.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 订单VO实体：给前端界面展示层返回的信息
 */
@Data
public class OrdersVO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -6311208493172235169L;

    /**
     * 订单 主键id
     */
    private String id;

    /**
     * 下单用户
     */
    private UsersVO user;

    /**
     * 下单日
     */
    private Date day;

    /**
     * 订单总价
     */
    private Double price;

    /**
     * 收货人姓名
     */
    private String username;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 收货地址
     */
    private String address;

    /**
     * 快递单号
     */
    private String expressNo;

    /**
     * 状态 -1删除 1待付款 2待发货 3待收货 4待评价 5完成
     */
    private Integer status = 1;

    /**
     * 该订单的详情列表
     */
    private List<OrdersDetailVO> detailList;

}
