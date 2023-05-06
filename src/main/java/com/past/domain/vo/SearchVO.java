package com.past.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 前台用户 搜索商品时 所用实体
 */
@Data
public class SearchVO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 5606729703991550391L;

    /**
     * 关键词
     */
    private String keyword;

    /**
     * 价格左区间
     */
    private Double left;

    /**
     * 价格右区间
     */
    private Double right;

}
