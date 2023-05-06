package com.past.dao;

import com.past.beans.PageRequest;
import com.past.domain.dto.MemoryDTO;
import com.past.domain.entity.Memory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 操作内存实体的数据库持久层接口
 */
@Mapper
@Repository
public interface MemoryDAO {


    /**
     * 根据主键id 删除一条内存数据
     * @param id 内存 主键id
     * @return 数据库中受影响的行数
     */
    int deleteByPrimaryKey(Long id);


    /**
     * 新增一条内存数据
     * @param record 待新增的内存对象
     * @return 数据库中受影响的行数
     */
    int insert(Memory record);


    /**
     * 新增一条内存数据，判断每个属性是否为Null，为不是Null的属性做新增
     * @param record 待新增的内存对象
     * @return 数据库中受影响的行数
     */
    int insertSelective(Memory record);


    /**
     * 根据主键id 查询一条内存数据
     * @param id 内存 主键id
     * @return 一个Memory对象
     */
    Memory selectByPrimaryKey(Long id);


    /**
     * 更新一条内存数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param record 更新后的内存对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKeySelective(Memory record);


    /**
     * 更新一条内存数据
     * @param record 更新后的内存对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKey(Memory record);


    /**
     * 批量更新多条内存数据
     * @param list 存储多个更新后的内存对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatch(List<Memory> list);


    /**
     * 批量更新多条内存数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param list 存储多个更新后的内存对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatchSelective(List<Memory> list);


    /**
     * 批量新增多条内存数据
     * @param list 存储多个待新增的内存对象 的集合
     * @return 数据库中受影响的行数
     */
    int batchInsert(@Param("list") List<Memory> list);


    /**
     * 根据id和内存名称查询的记录数
     * @param name 内存名称
     * @param id 内存主键id
     * @return 查询记录数
     */
    int countByName(@Param("name") String name, @Param("id") Long id);


    /**
     * 查询所有内存数据
     * @return 存储所有内存的集合
     */
    List<Memory> selectAll();


    /**
     * 根据内存名 模糊查询
     * @param pageRequest 封装分页条件的实体
     * @return 符合条件的结果集
     */
    List<Memory> selectByNameContaining(@Param("pageRequest") PageRequest<MemoryDTO> pageRequest);


    /**
     * 根据内存主键id 改变内存状态
     * @param status 状态 0正常 2禁用
     * @param id 内存 主键id
     * @param updateTime 最后一次更新时间
     * @param operator 最后一次操作者名称
     * @param operatorIp 最后一次操作者的ip
     * @return 更新后，数据库受影响的行数 > 0 ? true : false
     */
    int changeStatus(@Param("status") Integer status, @Param("id") Long id,
                     @Param("updateTime") Date updateTime, @Param("operator") String operator, @Param("operatorIp") String operatorIp);


}