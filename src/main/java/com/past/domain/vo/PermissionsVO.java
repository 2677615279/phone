package com.past.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 权限VO实体：给前端界面展示层返回的信息
 */
@Data
public class PermissionsVO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -1417701093868026911L;

    /**
     * 权限 主键id
     */
    private Long id;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限url
     */
    private String url;

    /**
     * 权限的父id 默认0
     */
    private Long parentId = 0L;

    /**
     * 状态 0正常 1删除 2禁用
     */
    private Integer status = 0;

}
