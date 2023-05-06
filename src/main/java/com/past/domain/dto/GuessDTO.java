package com.past.domain.dto;

import com.past.validation.InsertValidationGroup;
import com.past.validation.UpdateValidationGroup;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 猜想推送DTO实体：完成数据的中间交换,前端调用时传输
 * 操作数据库的常规增删改查时，所需的实体类
 */
@Data
public class GuessDTO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -8111061111208410722L;

    /**
     * 猜想 主键id
     */
    @NotNull(message = "猜想推送主键id不能为null，必须有实际值！", groups = {UpdateValidationGroup.class})
    private Long id;

    /**
     * 推送的商品 关联的商品主键id
     */
    @NotNull(message = "关联商品的主键id不能为null，必须有实际值！", groups = {InsertValidationGroup.class})
    private Long goodsId;

    /**
     * 该商品点击量 即当前登录用户浏览该商品的次数
     */
    @NotNull(message = "该商品点击量不能为null，必须有实际值！", groups = {InsertValidationGroup.class})
    private Integer num;

    /**
     * 是否喜欢并收藏  1收藏 -1未收藏
     */
    @Min(value = -1L, message = "状态码最小值为-1！")
    @Max(value = 1L, message = "状态码最大值为1！")
    private Integer favorite = -1;

    /**
     * 推送的用户 关联的用户主键id
     */
    @NotNull(message = "关联用户主键id不能为null，必须有实际值！", groups = {InsertValidationGroup.class})
    private Long userId;

}
