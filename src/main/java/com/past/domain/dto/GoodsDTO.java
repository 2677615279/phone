package com.past.domain.dto;

import com.past.validation.InsertValidationGroup;
import com.past.validation.UpdateValidationGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 商品DTO实体：完成数据的中间交换,前端调用时传输
 * 操作数据库的常规增删改查时，所需的实体类
 */
@Data
public class GoodsDTO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2545022940595561266L;

    /**
     * 商品 主键id
     */
    @NotNull(message = "商品主键id不能为null，必须有实际值！", groups = {UpdateValidationGroup.class})
    private Long id;

    /**
     * 商品名称
     */
    @NotBlank(message = "商品的名称既不能为null，还必须有除空字符外的实际字符！", groups = {InsertValidationGroup.class})
    @Length(min = 2, max = 20, message = "商品的名称字符长度范围必须在[2,20]！", groups = {InsertValidationGroup.class})
    private String name;

    /**
     * 商品价格
     */
    @NotNull(message = "商品价格不能为null，必须有实际值！", groups = {InsertValidationGroup.class})
    private Double price;

    /**
     * 商品库存
     */
    @NotNull(message = "商品库存不能为null，必须有实际值！", groups = {InsertValidationGroup.class})
    private Long num;

    /**
     * 商品类别 关联的分类id
     */
    @NotNull(message = "关联的商品类别不能为null，必须有实际值！", groups = {InsertValidationGroup.class})
    private Long type;

    /**
     * 商品内存 关联的内存id
     */
    @NotNull(message = "关联的内存不能为null，必须有实际值！", groups = {InsertValidationGroup.class})
    private Long memory;

    /**
     * 商品颜色
     */
    @NotBlank(message = "商品的颜色既不能为null，还必须有除空字符外的实际字符！", groups = {InsertValidationGroup.class})
    @Length(min = 2, max = 20, message = "商品的颜色字符长度范围必须在[2,20]！", groups = {InsertValidationGroup.class})
    private String color;

    /**
     * 商品样图
     */
    @NotBlank(message = "商品样图既不能为null，还必须有除空字符外的实际字符！", groups = {InsertValidationGroup.class})
    @Length(min = 1, max = 255, message = "商品样图的字符长度范围必须在[1,255]！", groups = {InsertValidationGroup.class})
    private String img;

    /**
     * 状态 0正常 1删除 2热卖
     */
    @Min(value = 0L, message = "状态码最小值为0！")
    @Max(value = 2L, message = "状态码最大值为2！")
    private Integer status = 0;

    /**
     * 商品描述
     */
    @NotBlank(message = "商品描述既不能为null，还必须有除空字符外的实际字符！", groups = {InsertValidationGroup.class})
    @Length(min = 1, max = 255, message = "商品描述的字符长度范围必须在[1,255]！", groups = {InsertValidationGroup.class})
    private String description;

    /**
     * 商品销量
     */
    private Long volume = 0L;

}
