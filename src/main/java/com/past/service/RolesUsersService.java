package com.past.service;

import com.past.domain.entity.RolesUsers;

import java.util.List;

/**
 * 角色用户关联模块 业务层接口
 */
public interface RolesUsersService {


    /**
     * 根据主键id 查询一条角色用户关联数据
     * @param id 角色用户 主键id
     * @return 一个RolesUsers对象
     */
    RolesUsers selectByPrimaryKey(Long id);


    /**
     * 根据关联的用户主键id，查询属于该用户的所有角色用户关联，并存入一个集合
     * @param userId 关联的用户主键id
     * @return 存储于该用户的所有角色用户关联的集合
     */
    List<RolesUsers> selectByUserId(Long userId);


    /**
     * 根据关联的角色主键id，查询属于该用户的所有角色用户关联，并存入一个集合
     * @param roleId 关联的角色主键id
     * @return 存储于该角色的所有角色用户关联的集合
     */
    List<RolesUsers> selectByRoleId(Long roleId);


    /**
     * 根据关联的用户主键id  删除用户角色关联数据
     * @param userId 关联的用户主键id
     * @return 全部删除true 否则false
     */
    boolean deleteLogicByUserId(Long userId);


    /**
     * 添加角色用户关联数据
     * @param userId 用户主键id
     * @param roleIds 存储角色主键id的集合
     */
    boolean insert(Long userId, List<Long> roleIds);


}
