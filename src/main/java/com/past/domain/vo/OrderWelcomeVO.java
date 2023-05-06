package com.past.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 后台管理员 查看订单月成交量数据图 所用实体
 */
@Data
public class OrderWelcomeVO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 4128080385017166024L;

    /**
     * 月成交总金额
     */
    private Long totalMoney;

    /**
     * 订单的月份
     */
    private String orderMonth;

    /**
     * 订单数量
     */
    private Long sheets;
}
