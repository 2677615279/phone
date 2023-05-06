package com.past.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Banner图VO实体：给前端界面展示层返回的信息
 */
@Data
public class BannersVO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 7652496378162953661L;

    /**
     * banner图 主键id
     */
    private Long id;

    /**
     * banner图名称
     */
    private String name;

    /**
     * banner图点击后跳转的商品详情url
     */
    private String url;

    /**
     * banner图片名称
     */
    private String img;

    /**
     * 状态 0正常 1删除 2禁用
     */
    private Integer status = 0;

}
