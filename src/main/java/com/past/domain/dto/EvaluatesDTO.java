package com.past.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.past.validation.InsertValidationGroup;
import com.past.validation.UpdateValidationGroup;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 评价DTO实体：完成数据的中间交换,前端调用时传输
 * 操作数据库的常规增删改查时，所需的实体类
 */
@Data
public class EvaluatesDTO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -6538338689566130612L;

    /**
     * 评价 主键id
     */
    @NotNull(message = "评价主键id不能为null，必须有实际值！", groups = {UpdateValidationGroup.class})
    private Long id;

    /**
     * 评价人 关联用户的主键id
     */
    @NotNull(message = "评价人不能为null，必须有实际值！", groups = {InsertValidationGroup.class})
    private Long userId;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 评价日
     */
    @NotNull(message = "评价日不能为null，必须有实际值！", groups = {InsertValidationGroup.class})
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone="GMT+8")
    private Date day;

    /**
     * 评价的商品 关联的商品主键id
     */
    @NotNull(message = "评价的商品不能为null，必须有实际值！", groups = {InsertValidationGroup.class})
    private Long goodsId;

    /**
     * 评价等级 1~5  1星、2星、3星、4星、5星
     */
    @NotNull(message = "评价等级不能为null，必须有实际值！", groups = {InsertValidationGroup.class})
    private Integer level;

    /**
     * 状态 0正常 1删除
     */
    @Min(value = 0L, message = "状态码最小值为0！")
    @Max(value = 1L, message = "状态码最大值为1！")
    private Integer status = 0;

}
