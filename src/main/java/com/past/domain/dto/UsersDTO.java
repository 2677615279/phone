package com.past.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.past.validation.InsertValidationGroup;
import com.past.validation.UpdateValidationGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户DTO实体：完成数据的中间交换,前端调用时传输
 * 操作数据库的常规增删改查时，所需的实体类
 */
@Data
public class UsersDTO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -3541341644236258678L;

    /**
     * 用户 主键id
     */
    @NotNull(message = "用户主键id不能为null，必须有实际值！", groups = {UpdateValidationGroup.class})
    private Long id;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名既不能为null，还必须有除空字符外的实际字符！", groups = {InsertValidationGroup.class})
    @Length(min = 3, max = 20, message = "用户名字符长度范围必须在[3,20]！", groups = {InsertValidationGroup.class})
    private String username;

    /**
     * MD5加密后的密码
     */
    @NotBlank(message = "用户名既不能为null，还必须有除空字符外的实际字符！", groups = {InsertValidationGroup.class})
    @Length(min = 6, max = 64, message = "密码字符长度范围必须在[6,64]！", groups = {InsertValidationGroup.class})
    private String password;

    /**
     * 密码加密所用 盐值
     */
    private String salt;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号既不能为null，还必须有除空字符外的实际字符！", groups = {InsertValidationGroup.class})
    @Length(min = 8, max = 11, message = "手机号字符长度范围必须在[8,11]！", groups = {InsertValidationGroup.class})
    private String phone;

    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱既不能为null，还必须有除空字符外的实际字符！", groups = {InsertValidationGroup.class})
    @Email(message = "邮箱必须为有效格式！", groups = {InsertValidationGroup.class})
    @Length(min = 8, max = 40, message = "邮箱字符长度范围必须在[8,40]！", groups = {InsertValidationGroup.class})
    private String email;

    /**
     * 生日
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone="GMT+8")
    private Date birthday;

    /**
     * 状态 0正常 1删除 2禁用
     */
    @Min(value = 0L, message = "状态码最小值为0！")
    @Max(value = 2L, message = "状态码最大值为2！")
    private Integer status = 0;

    /**
     * 头像
     */
    private String img;

}
