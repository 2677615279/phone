package com.past.dao;

import com.past.beans.PageRequest;
import com.past.domain.dto.ProvincesDTO;
import com.past.domain.entity.Provinces;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 操作省份实体的数据库持久层接口
 */
@Mapper
@Repository
public interface ProvincesDAO {


    /**
     * 根据主键id 删除一条省份数据
     * @param id 省份 主键id
     * @return 数据库中受影响的行数
     */
    int deleteByPrimaryKey(Long id);


    /**
     * 新增一条省份数据
     * @param record 待新增的省份对象
     * @return 数据库中受影响的行数
     */
    int insert(Provinces record);


    /**
     * 新增一条省份数据，判断每个属性是否为Null，为不是Null的属性做新增
     * @param record 待新增的省份对象
     * @return 数据库中受影响的行数
     */
    int insertSelective(Provinces record);


    /**
     * 根据主键id 查询一条省份数据
     * @param id 省份 主键id
     * @return 一个Provinces对象
     */
    Provinces selectByPrimaryKey(Long id);


    /**
     * 更新一条省份数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param record 更新后的省份对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKeySelective(Provinces record);


    /**
     * 更新一条省份数据
     * @param record 更新后的省份对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKey(Provinces record);


    /**
     * 批量更新多条省份数据
     * @param list 存储多个更新后的省份对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatch(List<Provinces> list);


    /**
     * 批量更新多条省份数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param list 存储多个更新后的省份对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatchSelective(List<Provinces> list);


    /**
     * 批量新增多条省份数据
     * @param list 存储多个待新增的省份对象 的集合
     * @return 数据库中受影响的行数
     */
    int batchInsert(@Param("list") List<Provinces> list);


    /**
     * 根据id和省名查询的记录数
     * @param name 省名
     * @param id 省份主键id
     * @return 查询记录数
     */
    int countByName(@Param("name") String name, @Param("id") Long id);


    /**
     * 查询所有省份数据
     * @return 存储所有省份的集合
     */
    List<Provinces> selectAll();


    /**
     * 根据省名查询省份
     * @param name 省名
     * @return 一个Provinces对象
     */
    Provinces selectByName(@Param("name") String name);


    /**
     * 根据省份中的省名 模糊查询
     * @param pageRequest 封装分页条件的实体
     * @return 符合条件的结果集
     */
    List<Provinces> selectByNameContaining(@Param("pageRequest") PageRequest<ProvincesDTO> pageRequest);


    /**
     * 根据省份主键id 改变省份状态
     * @param status 状态 0正常 2禁用
     * @param id 省份 主键 id
     * @param updateTime 最后一次更新时间
     * @param operator 最后一次操作者名称
     * @param operatorIp 最后一次操作者的ip
     * @return 更新后，数据库受影响的行数 > 0 ? true : false
     */
    int changeStatus(@Param("status") Integer status, @Param("id") Long id,
                     @Param("updateTime") Date updateTime, @Param("operator") String operator, @Param("operatorIp") String operatorIp);


}