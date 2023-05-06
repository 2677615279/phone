package com.past.dao;

import com.past.domain.entity.RolesUsers;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 操作角色用户关联实体的数据库持久层接口
 */
@Mapper
@Repository
public interface RolesUsersDAO {


    /**
     * 根据主键id 删除一条角色用户关联数据
     * @param id 角色用户关联 主键id
     * @return 数据库中受影响的行数
     */
    int deleteByPrimaryKey(Long id);


    /**
     * 新增一条角色用户关联数据
     * @param record 待新增的角色用户关联对象
     * @return 数据库中受影响的行数
     */
    int insert(RolesUsers record);


    /**
     * 新增一条角色用户关联数据，判断每个属性是否为Null，为不是Null的属性做新增
     * @param record 待新增的角色用户关联对象
     * @return 数据库中受影响的行数
     */
    int insertSelective(RolesUsers record);


    /**
     * 根据主键id 查询一条角色用户关联数据
     * @param id 角色用户关联 主键id
     * @return 一个RolesUsers对象
     */
    RolesUsers selectByPrimaryKey(Long id);


    /**
     * 更新一条角色用户关联数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param record 更新后的角色用户关联对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKeySelective(RolesUsers record);


    /**
     * 更新一条角色用户关联数据
     * @param record 更新后的角色用户关联对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKey(RolesUsers record);


    /**
     * 批量更新多条角色用户关联数据
     * @param list 存储多个更新后的角色用户关联对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatch(List<RolesUsers> list);


    /**
     * 批量更新多条角色用户关联数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param list 存储多个更新后的角色用户关联对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatchSelective(List<RolesUsers> list);


    /**
     * 批量新增多条角色用户关联数据
     * @param list 存储多个待新增的角色用户关联对象 的集合
     * @return 数据库中受影响的行数
     */
    int batchInsert(@Param("list") List<RolesUsers> list);


    /**
     * 根据关联的用户主键id，查询为此用户分配的所有角色的角色主键id
     * @param userId 关联的用户主键id
     * @return 存储 为此用户分配的所有角色的 角色主键id 的集合
     */
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);


    /**
     * 根据关联的角色主键id，查询为此角色分配的所有用户的用户主键id
     * @param roleId 关联的角色主键id
     * @return 存储 为此角色分配的所有用户的 用户主键id 的集合
     */
    List<Long> selectUserIdsByRoleId(@Param("roleId") Long roleId);


    /**
     * 根据关联的用户主键id，查询为此用户的角色用户关联
     * @param userId 关联的用户主键id
     * @return 存储 此用户的角色用户关联 的集合
     */
    List<RolesUsers> selectByUserId(@Param("userId") Long userId);


    /**
     * 根据关联的角色主键id，查询为此角色的角色用户关联
     * @param roleId 关联的角色主键id
     * @return 存储 此角色的角色用户关联 的集合
     */
    List<RolesUsers> selectByRoleId(@Param("roleId") Long roleId);


    /**
     * 查询所有 角色用户关联
     * @return 存储 所有角色用户关联 的集合
     */
    List<RolesUsers> selectAll();


    /**
     * 根据关联的用户主键id和角色主键id 查询该关联数据
     * @param userId 关联的用户主键id
     * @param roleId 关联的角色主键id
     * @return 一条关联数据
     */
    RolesUsers selectByUserIdWithRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);


}