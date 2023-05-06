package com.past.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 后台管理员 搜索订单时 所用实体
 */
@Data
public class OrderSearchVO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -3567791417806682582L;

    /**
     * 订单id 关键词
     */
    private String orderIdKeyword;

    /**
     * 起始日
     */
    private String startDate;

    /**
     * 结束日
     */
    private String endDate;

    /**
     * 订单状态 1待付款 2待发货 3待收货 4待评价 5完成
     */
    private Integer orderStatus;

}
