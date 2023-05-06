package com.past.dao;

import com.past.beans.PageRequest;
import com.past.domain.dto.GoodsTypesDTO;
import com.past.domain.entity.GoodsTypes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 操作商品分类实体的数据库持久层接口
 */
@Mapper
@Repository
public interface GoodsTypesDAO {


    /**
     * 根据主键id 删除一条商品分类数据
     * @param id 商品分类 主键id
     * @return 数据库中受影响的行数
     */
    int deleteByPrimaryKey(Long id);


    /**
     * 新增一条商品分类数据
     * @param record 待新增的商品分类对象
     * @return 数据库中受影响的行数
     */
    int insert(GoodsTypes record);


    /**
     * 新增一条商品分类数据，判断每个属性是否为Null，为不是Null的属性做新增
     * @param record 待新增的商品分类对象
     * @return 数据库中受影响的行数
     */
    int insertSelective(GoodsTypes record);


    /**
     * 根据主键id 查询一条商品分类数据
     * @param id 商品分类 主键id
     * @return 一个GoodsTypes对象
     */
    GoodsTypes selectByPrimaryKey(Long id);


    /**
     * 更新一条商品分类数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param record 更新后的商品分类对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKeySelective(GoodsTypes record);


    /**
     * 更新一条商品分类数据
     * @param record 更新后的商品分类对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKey(GoodsTypes record);


    /**
     * 批量更新多条商品分类数据
     * @param list 存储多个更新后的商品分类对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatch(List<GoodsTypes> list);


    /**
     * 批量更新多条商品分类数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param list 存储多个更新后的商品分类对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatchSelective(List<GoodsTypes> list);


    /**
     * 批量新增多条商品分类数据
     * @param list 存储多个待新增的商品分类对象 的集合
     * @return 数据库中受影响的行数
     */
    int batchInsert(@Param("list") List<GoodsTypes> list);


    /**
     * 根据id和商品分类名称查询的记录数
     * @param name 商品分类名称
     * @param id 商品分类主键id
     * @return 查询记录数
     */
    int countByName(@Param("name") String name, @Param("id") Long id);


    /**
     * 查询所有商品分类数据
     * @return 存储所有商品分类对象的集合
     */
    List<GoodsTypes> selectAll();


    /**
     * 根据商品分类名称 模糊查询
     * @param pageRequest 封装分页条件的实体
     * @return 符合条件的结果集
     */
    List<GoodsTypes> selectByNameContaining(@Param("pageRequest") PageRequest<GoodsTypesDTO> pageRequest);


    /**
     * 根据商品分类主键id 改变商品分类状态
     * @param status 状态 0正常 2禁用
     * @param id 商品分类 主键id
     * @param updateTime 最后一次更新时间
     * @param operator 最后一次操作者名称
     * @param operatorIp 最后一次操作者的ip
     * @return 更新后，数据库受影响的行数 > 0 ? true : false
     */
    int changeStatus(@Param("status") Integer status, @Param("id") Long id,
                     @Param("updateTime") Date updateTime, @Param("operator") String operator, @Param("operatorIp") String operatorIp);


}