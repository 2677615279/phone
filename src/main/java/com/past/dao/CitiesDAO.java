package com.past.dao;

import com.past.beans.PageRequest;
import com.past.domain.dto.CitiesDTO;
import com.past.domain.entity.Cities;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 操作城市实体的数据库持久层接口
 */
@Mapper
@Repository
public interface CitiesDAO {


    /**
     * 根据主键id 删除一条城市数据
     * @param id 城市 主键id
     * @return 数据库中受影响的行数
     */
    int deleteByPrimaryKey(Long id);


    /**
     * 新增一条城市数据
     * @param record 待新增的城市对象
     * @return 数据库中受影响的行数
     */
    int insert(Cities record);


    /**
     * 新增一条城市数据，判断每个属性是否为Null，为不是Null的属性做新增
     * @param record 待新增的城市对象
     * @return 数据库中受影响的行数
     */
    int insertSelective(Cities record);


    /**
     * 根据主键id 查询一条城市数据
     * @param id 城市 主键id
     * @return 一个Cities对象
     */
    Cities selectByPrimaryKey(Long id);


    /**
     * 更新一条城市数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param record 更新后的城市对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKeySelective(Cities record);


    /**
     * 更新一条城市数据
     * @param record 更新后的城市对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKey(Cities record);


    /**
     * 批量更新多条城市数据
     * @param list 存储多个更新后的城市对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatch(List<Cities> list);


    /**
     * 批量更新多条城市数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param list 存储多个更新后的城市对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatchSelective(List<Cities> list);


    /**
     * 批量新增多条城市数据
     * @param list 存储多个待新增的城市对象 的集合
     * @return 数据库中受影响的行数
     */
    int batchInsert(@Param("list") List<Cities> list);


    /**
     * 根据id和市名查询的记录数
     * @param name 市名
     * @param id 城市主键id
     * @param provinceId 所属省份主键id
     * @return 查询记录数
     */
    int countByNameWithProvinceId(@Param("name") String name, @Param("id") Long id, @Param("provinceId") Long provinceId);


    /**
     * 查询所有城市数据
     * @return 存储所有城市的集合
     */
    List<Cities> selectAll();


    /**
     * 根据省份主键id 查询该省份下的所有城市
     * @param provinceId 城市 主键id
     * @return 存储该省份下的所有城市的集合
     */
    List<Cities> selectByProvinceId(@Param("provinceId") Long provinceId);


    /**
     * 根据城市名称 和 所属省份的主键id 查询城市
     * @param name 城市名称
     * @param provinceId 所属省份的主键id
     * @return 一个Cities对象
     */
    Cities selectByNameWithProvinceId(@Param("name") String name, @Param("provinceId") Long provinceId);


    /**
     * 根据城市中的市名 模糊查询
     * @param pageRequest 封装分页条件的实体
     * @return 符合条件的结果集
     */
    List<Cities> selectByNameContaining(@Param("pageRequest") PageRequest<CitiesDTO> pageRequest);


    /**
     * 根据城市主键id 改变城市状态
     * @param status 状态 0正常 2禁用
     * @param id 城市 主键 id
     * @param updateTime banner图 最后一次更新时间
     * @param operator banner图 最后一次操作者名称
     * @param operatorIp banner图 最后一次操作者的ip
     * @return 更新后，数据库受影响的行数 > 0 ? true : false
     */
    int changeStatus(@Param("status") Integer status, @Param("id") Long id,
                     @Param("updateTime") Date updateTime, @Param("operator") String operator, @Param("operatorIp") String operatorIp);


    /**
     * 根据所属省份主键id 改变城市状态
     * @param status 状态 0正常 2禁用
     * @param provinceId 所属省份 主键 id
     * @param updateTime 最后一次更新时间
     * @param operator 最后一次操作者名称
     * @param operatorIp 最后一次操作者的ip
     * @return 更新后，数据库受影响的行数 > 0 ? true : false
     */
    int changeStatusByProvincesStatus(@Param("status")Integer status, @Param("provinceId") Long provinceId,
                                      @Param("updateTime") Date updateTime, @Param("operator") String operator, @Param("operatorIp") String operatorIp);


    /**
     * 根据城市主键id 查询所属省份的主键id
     * @param id 城市 主键 id
     * @return 省份的主键id
     */
    long selectProvinceIdById(@Param("id") Long id);


}