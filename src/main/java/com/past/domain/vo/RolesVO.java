package com.past.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 角色VO实体：给前端界面展示层返回的信息
 */
@Data
public class RolesVO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 7944189224069206701L;

    /**
     * 角色 主键id
     */
    private Long id;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 状态 0正常 1删除 2禁用
     */
    private Integer status = 0;

}
