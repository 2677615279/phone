package com.past.service;

import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.domain.dto.RolesDTO;
import com.past.domain.entity.Roles;
import com.past.domain.vo.RolesVO;

import java.util.List;

/**
 * 角色模块 业务层接口
 */
public interface RolesService {


    /**
     * 根据主键id 查询角色数据
     * @param id 角色 主键id
     * @return 一个RolesVO对象
     */
    RolesVO selectByPrimaryKey(Long id);


    /**
     * 判断该角色名是否存在，存在返回true 抛出异常；不存在返回false，继续执行业务
     * @param name 角色名称
     * @param id 角色 主键id
     * @return 存在返回true 抛出异常；不存在返回false，继续执行业务
     */
    boolean checkNameExist(String name, Long id);


    /**
     * 后台根据存储角色主键id的集合，查询这些id代表的角色对象
     * @param ids 存储角色主键id的集合
     * @return 存储 这些id代表的角色对象 的集合
     */
    List<Roles> selectByIds(List<Long> ids);


    /**
     * 后台新增角色
     * @param rolesDTO 待新增的角色DTO实体
     * @return 新增后，这条数据的主键id > 0 ? true : false
     */
    boolean insert(RolesDTO rolesDTO);


    /**
     * 后台删除角色 物理删除
     * @param id 待删除数据的主键id
     * @return 删除成功(数据库受影响的行数>0)返回true
     */
    boolean deletePhysical(Long id);


    /**
     * 后台删除角色 逻辑删除
     * @param id 待删除数据的主键id
     * @return 删除成功，即将此角色数据的状态改为1(删除)，返回true
     */
    boolean deleteLogic(Long id);


    /**
     * 后台更新角色
     * @param rolesDTO 更新后的角色DTO实体对象
     * @return 更新成功true 失败false
     */
    boolean update(RolesDTO rolesDTO);


    /**
     * 后台查询全部角色数据(过滤掉状态为1 即删除) 的数据
     * @return 符合条件的结果集
     */
    List<RolesVO> selectAll();


    /**
     * 后台根据分页条件实体，查询符合条件的结果集
     * @param pageRequest 分页条件实体对象
     * @return 分页结果实体
     */
    PageResult<RolesVO> selectPage(PageRequest<RolesDTO> pageRequest);


    /**
     * 根据角色主键id 改变角色状态
     * @param status 状态 0正常 2禁用
     * @param id 角色 主键 id
     * @return 更新后，数据库受影响的行数 > 0 ? true : false
     */
    boolean changeStatus(Integer status, Long id);


}
