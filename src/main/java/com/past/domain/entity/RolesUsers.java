package com.past.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 角色用户关联实体类 ： 属性和数据库字段一一对应
 */
@Data
public class RolesUsers implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -5639089684643638360L;

    /**
    * 角色用户关联 主键id
    */
    private Long id;

    /**
    * 关联的角色id
    */
    private Long roleId;

    /**
    * 关联的用户id
    */
    private Long userId;

    /**
    * 当前角色用户关联的状态 0正常 1删除
    */
    private Integer status = 0;

    /**
    * 创建时间
    */
    private Date createTime;

    /**
    * 最近一次的更新时间
    */
    private Date updateTime;

    /**
    * 最后一次操作的操作者名称
    */
    private String operator;

    /**
    * 最后一次操作的操作者ip
    */
    private String operatorIp;

}