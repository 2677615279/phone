package com.past.service;

import com.past.domain.entity.RolesPermissions;

import java.util.List;

/**
 * 角色权限关联模块 业务层接口
 */
public interface RolesPermissionsService {


    /**
     * 根据存储关联角色的主键id的集合，查询所有属于这些角色的角色权限关联
     * @param roleIds 存储关联角色的主键id的集合
     * @return 存储所有属于这些角色的角色权限关联的集合
     */
    List<RolesPermissions> selectByRoleIds(List<Long> roleIds);


    /**
     * 根据关联角色的主键id，查询属于该角色的角色权限关联
     * @param roleId 关联角色的主键id
     * @return 存储所有属于该角色的角色权限关联的集合
     */
    List<RolesPermissions> selectByRoleId(Long roleId);


    /**
     * 根据关联权限的主键id，查询属于该权限的角色权限关联
     * @param permissionId 关联权限的主键id
     * @return 存储所有属于该权限的角色权限关联的集合
     */
    List<RolesPermissions> selectByPermissionId(Long permissionId);


    /**
     * 根据关联的角色主键id  删除角色权限关联数据
     * @param roleId 关联的角色主键id
     * @return 全部删除true 否则false
     */
    boolean deleteLogicByRoleId(Long roleId);


    /**
     * 根据关联的权限主键id  删除角色权限关联数据
     * @param permissionId 关联的权限主键id
     * @return 全部删除true 否则false
     */
    boolean deleteLogicByPermissionId(Long permissionId);


    /**
     * 为某个角色 添加角色权限关联数据
     * @param roleId 角色主键id
     * @param permissionIds 存储权限主键id的集合
     */
    boolean insert(Long roleId, List<Long> permissionIds);


    /**
     * 为某个权限 添加角色权限关联数据
     * @param roleIds 存储角色主键id的集合
     * @param permissionId 权限主键id
     */
    boolean insert(List<Long> roleIds, Long permissionId);


}
