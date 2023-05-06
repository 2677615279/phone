package com.past.dao;

import com.past.domain.entity.RolesPermissions;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 操作角色权限关联实体的数据库持久层接口
 */
@Mapper
@Repository
public interface RolesPermissionsDAO {

    
    /**
     * 根据主键id 删除一条角色权限关联数据
     * @param id 角色权限关联 主键id
     * @return 数据库中受影响的行数
     */
    int deleteByPrimaryKey(Long id);

    
    /**
     * 新增一条角色权限关联数据
     * @param record 待新增的角色权限关联对象
     * @return 数据库中受影响的行数
     */
    int insert(RolesPermissions record);

    
    /**
     * 新增一条角色权限关联数据，判断每个属性是否为Null，为不是Null的属性做新增
     * @param record 待新增的角色权限关联对象
     * @return 数据库中受影响的行数
     */
    int insertSelective(RolesPermissions record);

    
    /**
     * 根据主键id 查询一条角色权限关联数据
     * @param id 角色权限关联 主键id
     * @return 一个RolesPermissions对象
     */
    RolesPermissions selectByPrimaryKey(Long id);

    
    /**
     * 更新一条角色权限关联数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param record 更新后的角色权限关联对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKeySelective(RolesPermissions record);

    
    /**
     * 更新一条角色权限关联数据
     * @param record 更新后的角色权限关联对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKey(RolesPermissions record);

    
    /**
     * 批量更新多条角色权限关联数据
     * @param list 存储多个更新后的角色权限关联对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatch(List<RolesPermissions> list);

    
    /**
     * 批量更新多条角色权限关联数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param list 存储多个更新后的角色权限关联对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatchSelective(List<RolesPermissions> list);

    
    /**
     * 批量新增多条角色权限关联数据
     * @param list 存储多个待新增的角色权限关联对象 的集合
     * @return 数据库中受影响的行数
     */
    int batchInsert(@Param("list") List<RolesPermissions> list);


    /**
     * 根据集合中存储的所有关联角色的主键id，查询所有为这些角色主键id关联的权限主键id，并将其存入一个集合
     * @param roleIds 存储所有关联角色的主键id
     * @return 存储 所有为这些角色主键id关联的权限主键id 的集合
     */
    List<Long> selectPermissionIdsByRoleIds(@Param("roleIds") List<Long> roleIds);


    /**
     * 根据关联角色的主键id，查询所有为该角色主键id关联的权限主键id，并将其存入一个集合
     * @param roleId 关联角色的主键id
     * @return 存储 所有为该角色主键id关联的权限主键id 的集合
     */
    List<Long> selectPermissionIdsByRoleId(@Param("roleId") Long roleId);


    /**
     * 根据集合中存储的所有关联权限的主键id，查询所有为这些权限主键id关联的角色主键id，并将其存入一个集合
     * @param permissionIds 存储所有关联权限的主键id
     * @return 存储 所有为这些权限主键id关联的角色主键id 的集合
     */
    List<Long> selectRoleIdsByPermissionIds(@Param("permissionIds") List<Long> permissionIds);


    /**
     * 根据关联权限的主键id，查询所有为该权限主键id关联的角色主键id，并将其存入一个集合
     * @param permissionId 关联权限的主键id
     * @return 存储 所有为该权限主键id关联的角色主键id 的集合
     */
    List<Long> selectRoleIdsByPermissionId(@Param("permissionId") Long permissionId);


    /**
     * 根据关联的 角色主键id 查询所有角色权限关联
     * @param roleId 关联的角色主键id
     * @return 存储 此角色的角色权限关联 的集合
     */
    List<RolesPermissions> selectByRoleId(@Param("roleId") Long roleId);


    /**
     * 根据存储关联的角色主键id的集合，查询所有符合角色的角色权限关联
     * @param roleIds 存储关联的角色主键id的集合
     * @return 存储 这些角色的角色权限关联 的集合
     */
    List<RolesPermissions> selectByRoleIds(@Param("roleIds") List<Long> roleIds);


    /**
     * 根据关联的 权限主键id 查询所有角色权限关联
     * @param permissionId 关联的权限主键id
     * @return 存储 此权限的角色权限关联 的集合
     */
    List<RolesPermissions> selectByPermissionId(@Param("permissionId") Long permissionId);


    /**
     * 查询所有角色权限关联
     * @return 存储所有角色权限关联的集合
     */
    List<RolesPermissions> selectAll();
    
    
}