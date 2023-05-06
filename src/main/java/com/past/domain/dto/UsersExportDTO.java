package com.past.domain.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.converters.date.DateStringConverter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 把用户数据导出到Excel所用DTO实体
 */
@Data
public class UsersExportDTO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -3816208468088223831L;

    /**
     * 用户 主键id
     */
    @ExcelProperty(value = "ID")
    private Long id;

    /**
     * 用户名
     */
    @ExcelProperty(value = "用户名")
    private String username;

    /**
     * 手机号
     */
    @ExcelProperty(value = "手机号")
    private String phone;

    /**
     * 邮箱
     */
    @ExcelProperty(value = "邮箱")
    private String email;

    /**
     * 生日
     */
    @ExcelProperty(value = "生日", converter = DateStringConverter.class)
    @DateTimeFormat(value = "yyyy-MM-dd") // 自定义转换格式效果
    private Date birthday;

    /**
     * 状态
     */
    @ExcelProperty(value = "状态")
    private String status;

}
