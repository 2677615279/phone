package com.past.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体类 ： 属性和数据库字段一一对应
 */
@Data
public class Users implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -721311178701906159L;

    /**
    * 用户 主键id
    */
    private Long id;

    /**
    * 用户名
    */
    private String username;

    /**
     * MD5加密后的密码
     */
    private String password;

    /**
    * 密码加密所用 盐值
    */
    private String salt;

    /**
    * 手机号
    */
    private String phone;

    /**
    * 邮箱
    */
    private String email;

    /**
    * 生日
    */
    private Date birthday;

    /**
    * 状态 0正常 1删除 2禁用
    */
    private Integer status = 0;

    /**
    * 头像
    */
    private String img;

    /**
    * 注册时间
    */
    private Date createTime;

    /**
    * 最后一次的更新时间
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