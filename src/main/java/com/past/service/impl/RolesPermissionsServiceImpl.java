package com.past.service.impl;

import com.google.common.collect.Lists;
import com.past.dao.RolesPermissionsDAO;
import com.past.domain.entity.RolesPermissions;
import com.past.domain.entity.Users;
import com.past.service.RolesPermissionsService;
import com.past.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色权限关联模块 业务层接口
 */
@Service
@Transactional
@Slf4j
public class RolesPermissionsServiceImpl implements RolesPermissionsService {

    @Autowired
    private RolesPermissionsDAO rolesPermissionsDAO;


    /**
     * 根据存储关联角色的主键id的集合，查询所有属于这些角色的角色权限关联
     * @param roleIds 存储关联角色的主键id
     * @return 存储所有属于这些角色的角色权限关联的集合
     */
    @Override
    public List<RolesPermissions> selectByRoleIds(List<Long> roleIds) {

        return rolesPermissionsDAO.selectByRoleIds(roleIds);
    }


    /**
     * 根据关联角色的主键id，查询属于该角色的角色权限关联
     * @param roleId 关联角色的主键id
     * @return 存储所有属于该角色的角色权限关联的集合
     */
    @Override
    public List<RolesPermissions> selectByRoleId(Long roleId) {

        return rolesPermissionsDAO.selectByRoleId(roleId);
    }


    /**
     * 根据关联权限的主键id，查询属于该权限的角色权限关联
     * @param permissionId 关联权限的主键id
     * @return 存储所有属于该权限的角色权限关联的集合
     */
    @Override
    public List<RolesPermissions> selectByPermissionId(Long permissionId) {

        return rolesPermissionsDAO.selectByPermissionId(permissionId);
    }

    /**
     * 根据关联的角色主键id  删除角色权限关联数据
     * @param roleId 关联的角色主键id
     * @return 全部删除true 否则false
     */
    @Override
    public boolean deleteLogicByRoleId(Long roleId) {

        List<RolesPermissions> list = rolesPermissionsDAO.selectByRoleId(roleId)
                .stream().filter(rp -> rp.getStatus() == 0).collect(Collectors.toList());

        // 如果原始关联数据不存在 则代表没有需要删除的数据，返回true即可；否则遍历、依次逻辑删除
        if (!CollectionUtils.isEmpty(list)) {
            for (RolesPermissions rp : list) {
                rp.setStatus(1);
                rp.setUpdateTime(new Date());

                // 获取主体对象
                Subject subject = SecurityUtils.getSubject();
                Users login = (Users) subject.getPrincipal();

                rp.setOperator(login.getUsername());
                rp.setOperatorIp(IpUtil.getLocalIP());
            }

            return rolesPermissionsDAO.updateBatchSelective(list) == list.size();
        }

        return true;
    }


    /**
     * 根据关联的权限主键id  删除角色权限关联数据
     * @param permissionId 关联的权限主键id
     * @return 全部删除true 否则false
     */
    @Override
    public boolean deleteLogicByPermissionId(Long permissionId) {

        List<RolesPermissions> list = rolesPermissionsDAO.selectByPermissionId(permissionId)
                .stream().filter(rp -> rp.getStatus() == 0).collect(Collectors.toList());

        // 如果原始关联数据不存在 则代表没有需要删除的数据，返回true即可；否则遍历、依次逻辑删除
        if (!CollectionUtils.isEmpty(list)) {
            for (RolesPermissions rp : list) {
                rp.setStatus(1);
                rp.setUpdateTime(new Date());

                // 获取主体对象
                Subject subject = SecurityUtils.getSubject();
                Users login = (Users) subject.getPrincipal();

                rp.setOperator(login.getUsername());
                rp.setOperatorIp(IpUtil.getLocalIP());
            }

            return rolesPermissionsDAO.updateBatchSelective(list) == list.size();
        }

        return true;
    }


    /**
     * 为某个角色 添加角色权限关联数据
     * @param roleId 角色主键id
     * @param permissionIds 存储权限主键id的集合
     */
    @Override
    public boolean insert(Long roleId, List<Long> permissionIds) {

        List<RolesPermissions> list = Lists.newArrayList();

        for (Long permissionId : permissionIds) {
            RolesPermissions rp = new RolesPermissions();
            rp.setRoleId(roleId);
            rp.setPermissionId(permissionId);
            rp.setCreateTime(new Date());
            rp.setUpdateTime(new Date());

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();

            rp.setOperator(login.getUsername());
            rp.setOperatorIp(IpUtil.getLocalIP());

            list.add(rp);
        }

        return rolesPermissionsDAO.batchInsert(list) == list.size();
    }


    /**
     * 为某个权限 添加角色权限关联数据
     * @param roleIds 存储角色主键id的集合
     * @param permissionId 权限主键id
     */
    @Override
    public boolean insert(List<Long> roleIds, Long permissionId) {

        List<RolesPermissions> list = Lists.newArrayList();
        for (Long roleId : roleIds) {
            RolesPermissions rp = new RolesPermissions();
            rp.setPermissionId(permissionId);
            rp.setRoleId(roleId);
            rp.setCreateTime(new Date());
            rp.setUpdateTime(new Date());

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();

            rp.setOperator(login.getUsername());
            rp.setOperatorIp(IpUtil.getLocalIP());

            list.add(rp);
        }

        return rolesPermissionsDAO.batchInsert(list) == list.size();
    }


}
