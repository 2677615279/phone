package com.past.service;

import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.domain.dto.PermissionsDTO;
import com.past.domain.entity.Permissions;
import com.past.domain.vo.PermissionsVO;

import java.util.List;

/**
 * 权限模块 业务层接口
 */
public interface PermissionsService {


    /**
     * 根据主键id 查询权限数据
     * @param id 权限 主键id
     * @return 一个PermissionsVO对象
     */
    PermissionsVO selectByPrimaryKey(Long id);


    /**
     * 判断该权限名是否存在，存在返回true 抛出异常；不存在返回false，继续执行业务
     * @param name 权限名称
     * @param id 权限 主键id
     * @return 存在返回true 抛出异常；不存在返回false，继续执行业务
     */
    boolean checkNameExist(String name, Long id);


    /**
     * 判断该权限url是否存在，存在返回true 抛出异常；不存在返回false，继续执行业务
     * @param url 权限url
     * @param id 权限 主键id
     * @return 存在返回true 抛出异常；不存在返回false，继续执行业务
     */
    boolean checkUrlExist(String url, Long id);


    /**
     * 根据存储权限主键id的集合，查询这些id代表的权限对象
     * @param ids 存储权限主键id的集合
     * @return 存储 这些id代表的权限对象 的集合
     */
    List<Permissions> selectByIds(List<Long> ids);


    /**
     * 后台新增权限
     * @param permissionsDTO 待新增的权限DTO实体
     * @return 新增后，这条数据的主键id > 0 ? true : false
     */
    boolean insert(PermissionsDTO permissionsDTO);


    /**
     * 后台删除权限 物理删除
     * @param id 待删除数据的主键id
     * @return 删除成功(数据库受影响的行数>0)返回true
     */
    boolean deletePhysical(Long id);


    /**
     * 后台删除权限 逻辑删除
     * @param id 待删除数据的主键id
     * @return 删除成功，即将此权限数据的状态改为1(删除)，返回true
     */
    boolean deleteLogic(Long id);


    /**
     * 后台更新权限
     * @param permissionsDTO 更新后的权限DTO实体对象
     * @return 更新成功true 失败false
     */
    boolean update(PermissionsDTO permissionsDTO);


    /**
     * 后台查询全部角色数据(过滤掉状态为1 即删除) 的数据
     * @return 符合条件的结果集
     */
    List<PermissionsVO> selectAll();


    /**
     * 后台根据分页条件实体，查询符合条件的结果集
     * @param pageRequest 分页条件实体对象
     * @return 分页结果实体
     */
    PageResult<PermissionsVO> selectPage(PageRequest<PermissionsDTO> pageRequest);


    /**
     * 根据权限主键id 改变权限状态
     * @param status 状态 0正常 2禁用
     * @param id 权限 主键 id
     * @return 更新后，数据库受影响的行数 > 0 ? true : false
     */
    boolean changeStatus(Integer status, Long id);


}
