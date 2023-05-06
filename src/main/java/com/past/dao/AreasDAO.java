package com.past.dao;

import com.past.beans.PageRequest;
import com.past.domain.dto.AreasDTO;
import com.past.domain.entity.Areas;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 操作区县实体的数据库持久层接口
 */
@Mapper
@Repository
public interface AreasDAO {


    /**
     * 根据主键id 删除一条区县数据
     * @param id 区县 主键id
     * @return 数据库中受影响的行数
     */
    int deleteByPrimaryKey(Long id);


    /**
     * 新增一条区县数据
     * @param record 待新增的区县对象
     * @return 数据库中受影响的行数
     */
    int insert(Areas record);


    /**
     * 新增一条区县数据，判断每个属性是否为Null，为不是Null的属性做新增
     * @param record 待新增的区县对象
     * @return 数据库中受影响的行数
     */
    int insertSelective(Areas record);


    /**
     * 根据主键id 查询一条区县数据
     * @param id 区县 主键id
     * @return 一个Areas对象
     */
    Areas selectByPrimaryKey(Long id);


    /**
     * 更新一条区县数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param record 更新后的区县对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKeySelective(Areas record);


    /**
     * 更新一条区县数据
     * @param record 更新后的区县对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKey(Areas record);


    /**
     * 批量更新多条区县数据
     * @param list 存储多个更新后的区县对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatch(List<Areas> list);


    /**
     * 批量更新多条区县数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param list 存储多个更新后的区县对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatchSelective(List<Areas> list);


    /**
     * 批量新增多条区县数据
     * @param list 存储多个待新增的区县对象 的集合
     * @return 数据库中受影响的行数
     */
    int batchInsert(@Param("list") List<Areas> list);


    /**
     * 根据id和区县名称查询的记录数
     * @param name 区县名称
     * @param id 区县主键id
     * @param cityId 所属城市主键id
     * @return 查询记录数
     */
    int countByNameWithCityId(@Param("name") String name, @Param("id") Long id, @Param("cityId") Long cityId);


    /**
     * 查询所有区县数据
     * @return 存储所有区县的集合
     */
    List<Areas> selectAll();


    /**
     * 根据城市主键id 查询该城市下的所有区县
     * @param cityId 城市 主键id
     * @return 存储该城市下的所有区县的集合
     */
    List<Areas> selectByCityId(@Param("cityId") Long cityId);


    /**
     * 根据区县名称 和 所属城市的主键id 查询区县
     * @param name 区县名称
     * @param cityId 所属城市的主键id
     * @return 一个Areas对象
     */
    Areas selectByNameWithCityId(@Param("name") String name, @Param("cityId") Long cityId);


    /**
     * 根据区县中的区县名称 模糊查询
     * @param pageRequest 封装分页条件的实体
     * @return 符合条件的结果集
     */
    List<Areas> selectByNameContaining(@Param("pageRequest") PageRequest<AreasDTO> pageRequest);


    /**
     * 根据区县主键id 改变区县状态
     * @param status 状态 0正常 2禁用
     * @param id 区县 主键 id
     * @param updateTime banner图 最后一次更新时间
     * @param operator banner图 最后一次操作者名称
     * @param operatorIp banner图 最后一次操作者的ip
     * @return 更新后，数据库受影响的行数 > 0 ? true : false
     */
    int changeStatus(@Param("status") Integer status, @Param("id") Long id,
                     @Param("updateTime") Date updateTime, @Param("operator") String operator, @Param("operatorIp") String operatorIp);


    /**
     * 根据区县主键id 改变区县状态
     * @param status 状态 0正常 2禁用
     * @param cityId 所属城市 主键 id
     * @param updateTime 最后一次更新时间
     * @param operator 最后一次操作者名称
     * @param operatorIp 最后一次操作者的ip
     * @return 更新后，数据库受影响的行数 > 0 ? true : false
     */
    int changeStatusByCitiesStatus(@Param("status") Integer status, @Param("cityId") Long cityId,
                                   @Param("updateTime") Date updateTime, @Param("operator") String operator, @Param("operatorIp") String operatorIp);


    /**
     * 根据区县的主键id 查询其所属 的城市主键id
     * @param id 区县 主键 id
     * @return 所属 的城市主键id
     */
    long selectCityIdById(@Param("id") Long id);


}