package com.past.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 收货地址VO实体：给前端界面展示层返回的信息
 */
@Data
public class ReceivingAddressVO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 4250973016866574265L;

    /**
     * 收货地址 主键id
     */
    private Long id;

    /**
     * 省份名称
     */
    private String province;

    /**
     * 城市名称
     */
    private String city;

    /**
     * 区县名称
     */
    private String area;

    /**
     * 详细地址
     */
    private String detail;

    /**
     * 收货人手机，可能和收货人注册时所用手机不同，故单列该属性
     */
    private String phone;

    /**
     * 收货人昵称
     */
    private String nickName;

    /**
     * 状态 0正常 1删除
     */
    private Integer status = 0;

}
