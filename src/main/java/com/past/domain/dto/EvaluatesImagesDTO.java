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
 * 评价图DTO实体：完成数据的中间交换,前端调用时传输
 * 操作数据库的常规增删改查时，所需的实体类
 */
@Data
public class EvaluatesImagesDTO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 3652293639147442626L;

    /**
     * 图评 主键id
     */
    @NotNull(message = "评价图主键id不能为null，必须有实际值！", groups = {UpdateValidationGroup.class})
    private Long id;

    /**
     * 评价图 名称
     */
    @NotBlank(message = "评价图名称既不能为null，还必须有除空字符外的实际字符！", groups = {InsertValidationGroup.class})
    @Length(min = 1, max = 255, message = "评价图名称的字符长度范围必须在[1,255]！", groups = {InsertValidationGroup.class})
    private String name;

    /**
     * 关联的评价主键id
     */
    @NotNull(message = "关联的评价主键id不能为null，必须有实际值！", groups = {InsertValidationGroup.class})
    private Long evaluateId;

    /**
     * 状态 0正常 1删除
     */
    @Min(value = 0L, message = "状态码最小值为0！")
    @Max(value = 1L, message = "状态码最大值为1！")
    private Integer status = 0;

}
