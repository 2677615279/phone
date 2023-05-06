package com.past.dao;

import com.past.beans.PageRequest;
import com.past.domain.dto.BannersDTO;
import com.past.domain.entity.Banners;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 操作Banner图实体的数据库持久层接口
 */
@Mapper
@Repository
public interface BannersDAO {


    /**
     * 根据主键id 删除一条Banner图数据
     * @param id Banner图 主键id
     * @return 数据库中受影响的行数
     */
    int deleteByPrimaryKey(Long id);


    /**
     * 新增一条Banner图数据
     * @param record 待新增的Banner图对象
     * @return 数据库中受影响的行数
     */
    int insert(Banners record);


    /**
     * 新增一条Banner图数据，判断每个属性是否为Null，为不是Null的属性做新增
     * @param record 待新增的Banner图对象
     * @return 数据库中受影响的行数
     */
    int insertSelective(Banners record);


    /**
     * 根据主键id 查询一条Banner图数据
     * @param id Banner图 主键id
     * @return 一个Banners对象
     */
    Banners selectByPrimaryKey(Long id);


    /**
     * 更新一条Banner图数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param record 更新后的Banner图对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKeySelective(Banners record);


    /**
     * 更新一条Banner图数据
     * @param record 更新后的Banner图对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKey(Banners record);


    /**
     * 批量更新多条Banner图数据
     * @param list 存储多个更新后的Banner图对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatch(List<Banners> list);


    /**
     * 批量更新多条Banner图数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param list 存储多个更新后的Banner图对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatchSelective(List<Banners> list);


    /**
     * 批量新增多条Banner图数据
     * @param list 存储多个待新增的Banner图对象 的集合
     * @return 数据库中受影响的行数
     */
    int batchInsert(@Param("list") List<Banners> list);


    /**
     * 根据id和banner图名称查询的记录数
     * @param name banner图名称
     * @param id banner图主键id
     * @return 查询记录数
     */
    int countByName(@Param("name") String name, @Param("id") Long id);


    /**
     * 根据id和banner图名称查询的记录数
     * @param url banner图所指商品详情的url
     * @param id banner图主键id
     * @return 查询记录数
     */
    int countByUrl(@Param("url") String url, @Param("id") Long id);


    /**
     * 查询所有banner图数据
     * @return 存储所有banner图的集合
     */
    List<Banners> selectAll();


    /**
     * 根据banner图名称 模糊查询
     * @param pageRequest 封装分页条件的实体
     * @return 符合条件的结果集
     */
    List<Banners> selectByNameContaining(@Param("pageRequest") PageRequest<BannersDTO> pageRequest);


    /**
     * 根据banner图主键id 改变banner图状态
     * @param status 状态 0正常 2禁用
     * @param id banner图 主键id
     * @param updateTime 最后一次更新时间
     * @param operator 最后一次操作者名称
     * @param operatorIp 最后一次操作者的ip
     * @return 更新后，数据库受影响的行数 > 0 ? true : false
     */
    int changeStatus(@Param("status") Integer status, @Param("id") Long id,
                     @Param("updateTime") Date updateTime, @Param("operator") String operator, @Param("operatorIp") String operatorIp);


}