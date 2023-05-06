package com.past.service;

import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.domain.dto.UsersDTO;
import com.past.domain.entity.Users;
import com.past.domain.vo.UsersVO;

import java.util.List;

/**
 * 用户模块 业务层接口
 */
public interface UsersService {


    /**
     * 根据主键id 查询用户数据
     * @param id 用户 主键id
     * @return 一个UsersVO对象
     */
    UsersVO selectByPrimaryKey(Long id);


    /**
     * 根据账号 查询一条用户数据
     * @param principal 用户主身份信息 可以是username或phone或email
     * @return 一个Users对象
     */
    Users selectByPrincipal(String principal);


    /**
     * 判断该用户的用户名是否存在，存在返回true 抛出异常；不存在返回false，继续执行业务
     * @param username 用户名
     * @param id 用户 主键id
     * @return 存在返回true 抛出异常；不存在返回false，继续执行业务
     */
    boolean checkUsernameExist(String username, Long id);


    /**
     * 判断该用户的邮箱是否存在，存在返回true 抛出异常；不存在返回false，继续执行业务
     * @param email 邮箱
     * @param id 用户 主键id
     * @return 存在返回true 抛出异常；不存在返回false，继续执行业务
     */
    boolean checkEmailExist(String email, Long id);


    /**
     * 判断该用户的手机号是否存在，存在返回true 抛出异常；不存在返回false，继续执行业务
     * @param phone 手机号
     * @param id 用户 主键id
     * @return 存在返回true 抛出异常；不存在返回false，继续执行业务
     */
    boolean checkPhoneExist(String phone, Long id);


    /**
     * 前台注册用户
     * @param usersDTO 待新增的用户DTO对象
     * @param passwordCheck 确认密码
     * @return 新增后，这条数据的主键id > 0 ? true : false
     */
    boolean register(UsersDTO usersDTO, String passwordCheck);


    /**
     * 根据账号 查询所属用户 确认该用户的角色  管理员登录前的必要工作
     * @param principal 用户主身份信息 可以是username或phone或email
     * @return 拥有任意一个管理员角色返回true 否则false
     */
    boolean confirmRole(String principal);


    /**
     * 后台删除用户 物理删除
     * @param id 待删除数据的主键id
     * @return 删除成功(数据库受影响的行数>0)返回true
     */
    boolean deletePhysical(Long id);


    /**
     * 后台删除用户 逻辑删除
     * @param id 待删除数据的主键id
     * @return 删除成功，即将此用户数据的状态改为1(删除)，返回true
     */
    boolean deleteLogic(Long id);


    /**
     * 后台更新用户
     * @param usersDTO 更新后的用户DTO实体对象
     * @return 更新成功true 失败false
     */
    boolean update(UsersDTO usersDTO);


    /**
     * 登录的用户 更新自己的信息
     * @param usersDTO 更新后的用户DTO实体
     * @return 更新成功true 失败false
     */
    boolean updateBySelf(UsersDTO usersDTO);


    /**
     * 登录的用户 更新自己的密码
     * @param usersDTO 更新后的用户DTO实体
     * @param newPassword 新密码
     * @param newPasswordCheck 确认新密码
     * @return 更新成功true 失败false
     */
    boolean updatePassword(UsersDTO usersDTO, String newPassword, String newPasswordCheck);


    /**
     * 后台查询全部用户数据(过滤掉状态为1 即删除) 的数据
     * @return 最后的查询结果集符合条件 返回true
     */
    List<UsersVO> selectAll();


    /**
     * 后台根据分页条件实体，查询符合条件的结果集
     * @param pageRequest 分页条件实体对象
     * @return 分页结果实体
     */
    PageResult<UsersVO> selectPage(PageRequest<UsersDTO> pageRequest);


    /**
     * 判断字符串是否是邮箱
     * @param search 字符串
     * @return 是true 否false
     */
    boolean checkEmail(String search);


    /**
     * 判断字符串是否是手机号
     * @param search 字符串
     * @return 是true 否false
     */
    boolean checkPhone(String search);


    /**
     * 根据用户主键id 改变用户状态
     * @param status 状态 0正常 2禁用
     * @param id 用户 主键 id
     * @return 更新后，数据库受影响的行数 > 0 ? true : false
     */
    boolean changeStatus(Integer status, Long id);


}
