package com.past.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 评价VO实体：给前端界面展示层返回的信息
 */
@Data
public class EvaluatesVO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1860590362913593936L;

    /**
     * 评价 主键id
     */
    private Long id;

    /**
     * 评价人
     */
    private UsersVO user;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 评价日
     */
    private Date day;

    /**
     * 评价的商品
     */
    private GoodsVO goods;

    /**
     * 评价等级 1~5  1星、2星、3星、4星、5星
     */
    private Integer level;

    /**
     * 状态 0正常 1删除
     */
    private Integer status = 0;

    /**
     * 存储评价图的集合
     */
    private List<EvaluatesImagesVO> imgList;

}
