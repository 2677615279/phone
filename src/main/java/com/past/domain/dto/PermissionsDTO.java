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
 * 权限DTO实体：完成数据的中间交换,前端调用时传输
 * 操作数据库的常规增删改查时，所需的实体类
 */
@Data
public class PermissionsDTO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 3592547634320463287L;

    /**
     * 权限 主键id
     */
    @NotNull(message = "权限主键id不能为null，必须有实际值！", groups = {UpdateValidationGroup.class})
    private Long id;

    /**
     * 权限名称
     */
    @NotBlank(message = "权限名称既不能为null，还必须有除空字符外的实际字符！", groups = {InsertValidationGroup.class})
    @Length(min = 2, max = 30, message = "角色名称字符长度范围必须在[2,30]！", groups = {InsertValidationGroup.class})
    private String name;

    /**
     * 权限url
     */
    private String url;

    /**
     * 权限的父id 默认0
     */
    @Min(value = 0L, message = "权限的父id最小值为0！")
    private Long parentId = 0L;

    /**
     * 状态 0正常 1删除 2禁用
     */
    @Min(value = 0L, message = "状态码最小值为0！")
    @Max(value = 2L, message = "状态码最大值为2！")
    private Integer status = 0;

}
