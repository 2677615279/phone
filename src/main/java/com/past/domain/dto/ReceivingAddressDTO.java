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
 * 收货地址DTO实体：完成数据的中间交换,前端调用时传输
 * 操作数据库的常规增删改查时，所需的实体类
 */
@Data
public class ReceivingAddressDTO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 7039113258912001859L;

    /**
     * 收货地址 主键id
     */
    @NotNull(message = "收货地址主键id不能为null，必须有实际值！", groups = {UpdateValidationGroup.class})
    private Long id;

    /**
     * 省份名称
     */
    @NotBlank(message = "省份名称既不能为null，还必须有除空字符外的实际字符！", groups = {InsertValidationGroup.class})
    private String province;

    /**
     * 城市名称
     */
    @NotBlank(message = "城市名称既不能为null，还必须有除空字符外的实际字符！", groups = {InsertValidationGroup.class})
    private String city;

    /**
     * 区县名称
     */
    @NotBlank(message = "区县名称既不能为null，还必须有除空字符外的实际字符！", groups = {InsertValidationGroup.class})
    private String area;

    /**
     * 详细地址
     */
    @NotBlank(message = "详细地址既不能为null，还必须有除空字符外的实际字符！", groups = {InsertValidationGroup.class})
    @Length(min = 2, max = 60, message = "详细地址字符长度范围必须在[2,60]！", groups = {InsertValidationGroup.class})
    private String detail;

    /**
     * 关联的用户主键id，该地址所属的用户
     */
    private Long userId;

    /**
     * 收货人手机，可能和收货人注册时所用手机不同，故单列该属性
     */
    @NotBlank(message = "手机号既不能为null，还必须有除空字符外的实际字符！", groups = {InsertValidationGroup.class})
    @Length(min = 8, max = 11, message = "手机号字符长度范围必须在[8,11]！", groups = {InsertValidationGroup.class})
    private String phone;

    /**
     * 收货人昵称
     */
    @NotBlank(message = "收货人既不能为null，还必须有除空字符外的实际字符！", groups = {InsertValidationGroup.class})
    private String nickName;

    /**
     * 状态 0正常 1删除
     */
    @Min(value = 0L, message = "状态码最小值为0！")
    @Max(value = 1L, message = "状态码最大值为1！")
    private Integer status = 0;

}
