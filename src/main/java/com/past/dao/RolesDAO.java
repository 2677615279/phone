package com.past.dao;

import com.past.beans.PageRequest;
import com.past.domain.dto.RolesDTO;
import com.past.domain.entity.Roles;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 操作角色实体的数据库持久层接口
 */
@Mapper
@Repository
public interface RolesDAO {


    /**
     * 根据主键id 删除一条角色数据
     * @param id 角色 主键id
     * @return 数据库中受影响的行数
     */
    int deleteByPrimaryKey(Long id);


    /**
     * 新增一条角色数据
     * @param record 待新增的角色对象
     * @return 数据库中受影响的行数
     */
    int insert(Roles record);


    /**
     * 新增一条角色数据，判断每个属性是否为Null，为不是Null的属性做新增
     * @param record 待新增的角色对象
     * @return 数据库中受影响的行数
     */
    int insertSelective(Roles record);


    /**
     * 根据主键id 查询一条角色数据
     * @param id 角色 主键id
     * @return 一个Roles对象
     */
    Roles selectByPrimaryKey(Long id);


    /**
     * 更新一条角色数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param record 更新后的角色对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKeySelective(Roles record);


    /**
     * 更新一条角色数据
     * @param record 更新后的角色对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKey(Roles record);


    /**
     * 批量更新多条角色数据
     * @param list 存储多个更新后的角色对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatch(List<Roles> list);


    /**
     * 批量更新多条角色数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param list 存储多个更新后的角色对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatchSelective(List<Roles> list);


    /**
     * 批量新增多条角色数据
     * @param list 存储多个待新增的角色对象 的集合
     * @return 数据库中受影响的行数
     */
    int batchInsert(@Param("list") List<Roles> list);


    /**
     * 根据集合中存储的所有主键id，查询这些主键id分配的所有角色数据，并将其存储到一个集合
     * @param ids 存储所有角色主键id的集合
     * @return 存储 这些主键id代表的所有角色数据 的集合
     */
    List<Roles> selectByIds(@Param("ids") List<Long> ids);


    /**
     * 根据id和角色名查询的记录数
     * @param name 角色名
     * @param id 角色主键id
     * @return 查询记录数
     */
    int countByName(@Param("name") String name, @Param("id") Long id);


    /**
     * 查询所有角色数据
     * @return 存储所有角色对象的集合
     */
    List<Roles> selectAll();


    /**
     * 根据角色名 模糊查询
     * @param pageRequest 封装分页条件的实体
     * @return 符合条件的结果集
     */
    List<Roles> selectByNameContaining(@Param("pageRequest") PageRequest<RolesDTO> pageRequest);


    /**
     * 根据角色主键id 改变角色状态
     * @param status 状态 0正常 2禁用
     * @param id 角色 主键id
     * @param updateTime 最后一次更新时间
     * @param operator 最后一次操作者名称
     * @param operatorIp 最后一次操作者的ip
     * @return 更新后，数据库受影响的行数 > 0 ? true : false
     */
    int changeStatus(@Param("status") Integer status, @Param("id") Long id,
                     @Param("updateTime") Date updateTime, @Param("operator") String operator, @Param("operatorIp") String operatorIp);
    
    
}