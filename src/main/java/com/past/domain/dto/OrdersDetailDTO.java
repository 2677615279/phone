package com.past.domain.dto;

import com.past.validation.InsertValidationGroup;
import com.past.validation.UpdateValidationGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 订单详情DTO实体：完成数据的中间交换,前端调用时传输
 * 操作数据库的常规增删改查时，所需的实体类
 */
@Data
public class OrdersDetailDTO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -6488432384208853065L;

    /**
     * 订单详情 主键id
     */
    @NotNull(message = "订单详情的主键id既不能为null，还必须有除空字符外的实际字符！", groups = {UpdateValidationGroup.class})
    private Long id;

    /**
     * 订单的主键id
     */
    @NotBlank(message = "订单的主键id既不能为null，还必须有除空字符外的实际字符！", groups = {InsertValidationGroup.class})
    private String orderId;

    /**
     * 下单商品的主键id
     */
    @NotNull(message = "下单商品的主键id既不能为null，还必须有除空字符外的实际字符！", groups = {InsertValidationGroup.class})
    private Long goodsId;

    /**
     * 商品单价
     */
    @NotNull(message = "订单总价既不能为null，还必须有除空字符外的实际字符！", groups = {InsertValidationGroup.class})
    private Double price;

    /**
     * 下单量
     */
    @NotNull(message = "下单量既不能为null，还必须有除空字符外的实际字符！", groups = {InsertValidationGroup.class})
    private Long num;

}
