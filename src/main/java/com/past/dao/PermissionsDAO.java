package com.past.dao;

import com.past.beans.PageRequest;
import com.past.domain.dto.PermissionsDTO;
import com.past.domain.entity.Permissions;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 操作权限实体的数据库持久层接口
 */
@Mapper
@Repository
public interface PermissionsDAO {


    /**
     * 根据主键id 删除一条权限数据
     * @param id 权限 主键id
     * @return 数据库中受影响的行数
     */
    int deleteByPrimaryKey(Long id);


    /**
     * 新增一条权限数据
     * @param record 待新增的权限对象
     * @return 数据库中受影响的行数
     */
    int insert(Permissions record);


    /**
     * 新增一条权限数据，判断每个属性是否为Null，为不是Null的属性做新增
     * @param record 待新增的权限对象
     * @return 数据库中受影响的行数
     */
    int insertSelective(Permissions record);


    /**
     * 根据主键id 查询一条权限数据
     * @param id 权限 主键id
     * @return 一个Permissions对象
     */
    Permissions selectByPrimaryKey(Long id);


    /**
     * 更新一条权限数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param record 更新后的权限对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKeySelective(Permissions record);


    /**
     * 更新一条权限数据
     * @param record 更新后的权限对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKey(Permissions record);


    /**
     * 批量更新多条权限数据
     * @param list 存储多个更新后的权限对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatch(List<Permissions> list);


    /**
     * 批量更新多条权限数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param list 存储多个更新后的权限对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatchSelective(List<Permissions> list);


    /**
     * 批量新增多条权限数据
     * @param list 存储多个待新增的权限对象 的集合
     * @return 数据库中受影响的行数
     */
    int batchInsert(@Param("list") List<Permissions> list);


    /**
     * 根据集合中存储的所有主键id，查询这些主键id分配的所有权限数据，并将其存储到一个集合
     * @param ids 存储所有权限主键id的集合
     * @return 存储 这些主键id代表的所有权限数据 的集合
     */
    List<Permissions> selectByIds(@Param("ids") List<Long> ids);


    /**
     * 根据id和权限名查询的记录数
     * @param name 权限名
     * @param id 权限主键id
     * @return 查询记录数
     */
    int countByName(@Param("name") String name, @Param("id") Long id);


    /**
     * 根据id和权限url查询的记录数
     * @param url 权限url
     * @param id 权限主键id
     * @return 查询记录数
     */
    int countByUrl(@Param("url") String url, @Param("id") Long id);


    /**
     * 查询所有权限数据
     * @return 存储所有权限对象的集合
     */
    List<Permissions> selectAll();


    /**
     * 根据父id查询所有权限
     * @param parentId 父id
     * @return 存储该父id下的所有子权限的集合
     */
    List<Permissions> selectByParentId(@Param("parentId") Long parentId);


    /**
     * 根据父id集合查询所有权限
     * @param parentIds 存储父id的集合
     * @return 存储该父id下的所有子权限的集合
     */
    List<Permissions> selectByParentIds(@Param("parentIds") List<Long> parentIds);


    /**
     * 根据权限名 模糊查询
     * @param pageRequest 封装分页条件的实体
     * @return 符合条件的结果集
     */
    List<Permissions> selectByNameContaining(@Param("pageRequest") PageRequest<PermissionsDTO> pageRequest);


    /**
     * 根据权限主键id 改变权限状态
     * @param status 状态 0正常 2禁用
     * @param id 权限 主键id
     * @param updateTime 最后一次更新时间
     * @param operator 最后一次操作者名称
     * @param operatorIp 最后一次操作者的ip
     * @return 更新后，数据库受影响的行数 > 0 ? true : false
     */
    int changeStatus(@Param("status") Integer status, @Param("id") Long id,
                     @Param("updateTime") Date updateTime, @Param("operator") String operator, @Param("operatorIp") String operatorIp);


}