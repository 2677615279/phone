package com.past.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.past.validation.InsertValidationGroup;
import com.past.validation.UpdateValidationGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 订单DTO实体：完成数据的中间交换,前端调用时传输
 * 操作数据库的常规增删改查时，所需的实体类
 */
@Data
public class OrdersDTO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 2502957376950269726L;

    /**
     * 订单 主键id
     */
    @NotBlank(message = "订单的主键id既不能为null，还必须有除空字符外的实际字符！", groups = {UpdateValidationGroup.class})
    private String id;

    /**
     * 下单用户的主键id
     */
    @NotNull(message = "下单用户的主键id不能为null，必须有实际值！", groups = {InsertValidationGroup.class})
    private Long userId;

    /**
     * 下单日
     */
    @NotNull(message = "下单日不能为null，且必须满足格式要求！", groups = {InsertValidationGroup.class})
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone="GMT+8")
    private Date day;

    /**
     * 订单总价
     */
    @NotNull(message = "订单价格不能为null，必须有实际值！", groups = {InsertValidationGroup.class})
    private Double price;

    /**
     * 收货人姓名
     */
    @NotBlank(message = "收货人姓名既不能为null，还必须有除空字符外的实际字符！", groups = {InsertValidationGroup.class})
    @Length(min = 1, max = 20, message = "收货人姓名字符长度范围必须在[1,20]！", groups = {InsertValidationGroup.class})
    private String username;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号既不能为null，还必须有除空字符外的实际字符！", groups = {InsertValidationGroup.class})
    @Length(min = 8, max = 11, message = "手机号字符长度范围必须在[8,11]！", groups = {InsertValidationGroup.class})
    private String phone;

    /**
     * 收货地址
     */
    @NotBlank(message = "收货地址既不能为null，还必须有除空字符外的实际字符！", groups = {InsertValidationGroup.class})
    @Length(min = 2, max = 100, message = "收货地址字符长度范围必须在[2,100]！", groups = {InsertValidationGroup.class})
    private String address;

    /**
     * 快递单号
     */
    private String expressNo;

    /**
     * 状态 -1删除 1待付款 2待发货 3待收货 4待评价 5完成
     */
    @Min(value = -1L, message = "状态码最小值为-1！")
    @Max(value = 5L, message = "状态码最大值为5！")
    private Integer status = 1;

}
