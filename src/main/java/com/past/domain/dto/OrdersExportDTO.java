package com.past.domain.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.converters.date.DateStringConverter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 把订单数据导出到Excel所用DTO实体
 */
@Data
public class OrdersExportDTO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 5009208846482271479L;

    /**
     * 订单 主键id
     */
    @ExcelProperty(value = "ID")
    private String id;

    /**
     * 下单用户的主键id
     */
    @ExcelProperty(value = "下单用户编号")
    private Long userId;

    /**
     * 下单日
     */
    @ExcelProperty(value = "下单日期", converter = DateStringConverter.class)
    @DateTimeFormat(value = "yyyy-MM-dd") // 自定义转换格式效果
    private Date day;

    /**
     * 订单总价
     */
    @ExcelProperty(value = "订单总价")
    private Double price;

    /**
     * 收货人姓名
     */
    @ExcelProperty(value = "收货人姓名")
    private String username;

    /**
     * 手机号
     */
    @ExcelProperty(value = "手机号")
    private String phone;

    /**
     * 收货地址
     */
    @ExcelProperty(value = "收货地址")
    private String address;

    /**
     * 快递单号
     */
    @ExcelProperty(value = "快递单号")
    private String expressNo;

    /**
     * 状态 -1删除 1待付款 2待发货 3待收货 4待评价 5完成
     */
    @ExcelProperty(value = "状态")
    private String status;

}
