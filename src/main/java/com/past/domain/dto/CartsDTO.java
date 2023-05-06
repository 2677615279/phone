package com.past.domain.dto;

import com.past.validation.InsertValidationGroup;
import com.past.validation.UpdateValidationGroup;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 购物车DTO实体：完成数据的中间交换,前端调用时传输
 * 操作数据库的常规增删改查时，所需的实体类
 */
@Data
public class CartsDTO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -7673153099195581687L;

    /**
     * 购物车 主键id
     */
    @NotNull(message = "购物车主键id不能为null，必须有实际值！", groups = {UpdateValidationGroup.class})
    private Long id;

    /**
     * 商品 关联商品的主键id
     */
    @NotNull(message = "关联商品的主键id不能为null，必须有实际值！", groups = {InsertValidationGroup.class})
    private Long goodsId;

    /**
     * 商品的数量
     */
    @NotNull(message = "商品的数量不能为null，必须有实际值！", groups = {InsertValidationGroup.class})
    private Integer num;

    /**
     * 商品单价
     */
    @NotNull(message = "商品的价格不能为null，必须有实际值！", groups = {InsertValidationGroup.class})
    private Double price;

    /**
     * 所属的用户 关联用户的主键id
     */
    @NotNull(message = "关联用户的主键id不能为null，必须有实际值！", groups = {InsertValidationGroup.class})
    private Long userId;

    /**
     * 状态 0正常 1删除
     */
    @Min(value = 0L, message = "状态码最小值为0！")
    @Max(value = 1L, message = "状态码最大值为1！")
    private Integer status = 0;

}
