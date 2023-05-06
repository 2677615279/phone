package com.past.service.impl;

import com.google.common.collect.Lists;
import com.past.dao.RolesUsersDAO;
import com.past.domain.entity.RolesUsers;
import com.past.domain.entity.Users;
import com.past.service.RolesUsersService;
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
 * 角色用户关联模块 业务层接口的实现类
 */
@Service
@Transactional
@Slf4j
public class RolesUsersServiceImpl implements RolesUsersService {

    @Autowired
    private RolesUsersDAO rolesUsersDAO;


    /**
     * 根据主键id 查询一条角色用户关联数据
     * @param id 角色用户 主键id
     * @return 一个RolesUsers对象
     */
    @Override
    public RolesUsers selectByPrimaryKey(Long id) {

        RolesUsers ru = rolesUsersDAO.selectByPrimaryKey(id);

        if (ru == null || ru.getStatus() == 1) {
            throw new NullPointerException("角色用户关联不存在！");
        }

        return ru;
    }


    /**
     * 根据关联的用户主键id，查询属于该用户的所有角色用户关联，并存入一个集合
     * @param userId 关联的用户主键id
     * @return 存储于该用户的所有角色用户关联的集合
     */
    @Override
    public List<RolesUsers> selectByUserId(Long userId) {

        return rolesUsersDAO.selectByUserId(userId);
    }


    /**
     * 根据关联的角色主键id，查询属于该用户的所有角色用户关联，并存入一个集合
     * @param roleId 关联的角色主键id
     * @return 存储于该角色的所有角色用户关联的集合
     */
    @Override
    public List<RolesUsers> selectByRoleId(Long roleId) {

        return rolesUsersDAO.selectByRoleId(roleId);
    }


    /**
     * 根据关联的用户主键id  删除用户角色关联数据
     * @param userId 关联的用户主键id
     * @return 全部删除true 否则false
     */
    @Override
    public boolean deleteLogicByUserId(Long userId) {

        List<RolesUsers> list = rolesUsersDAO.selectByUserId(userId)
                .stream().filter(ru -> ru.getStatus() == 0).collect(Collectors.toList());

        // 如果原始关联数据不存在 则代表没有需要删除的数据，返回true即可；否则遍历、依次逻辑删除
        if (!CollectionUtils.isEmpty(list)) {
            for (RolesUsers ru : list) {
                ru.setStatus(1);
                ru.setUpdateTime(new Date());

                // 获取主体对象
                Subject subject = SecurityUtils.getSubject();
                Users login = (Users) subject.getPrincipal();
                ru.setOperator(login.getUsername());
                ru.setOperatorIp(IpUtil.getLocalIP());
            }

            return rolesUsersDAO.updateBatchSelective(list) == list.size();
        }

        return true;
    }


    /**
     * 添加角色用户关联数据
     * @param userId 用户主键id
     * @param roleIds 存储角色主键id的集合
     */
    @Override
    public boolean insert(Long userId, List<Long> roleIds) {

        List<RolesUsers> list = Lists.newArrayList();

        for (Long roleId : roleIds) {
            RolesUsers ru = new RolesUsers();
            ru.setUserId(userId);
            ru.setRoleId(roleId);
            ru.setCreateTime(new Date());
            ru.setUpdateTime(new Date());

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();
            ru.setOperator(login.getUsername());
            ru.setOperatorIp(IpUtil.getLocalIP());

            list.add(ru);
        }

        return rolesUsersDAO.batchInsert(list) == list.size();
    }


}