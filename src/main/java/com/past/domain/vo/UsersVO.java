package com.past.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户VO实体：给前端界面展示层返回的信息
 */
@Data
public class UsersVO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 685133218330648895L;

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
     * 用户头像
     */
    private String img;

}
