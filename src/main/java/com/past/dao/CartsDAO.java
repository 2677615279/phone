package com.past.dao;

import com.past.domain.entity.Carts;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 操作购物车实体的数据库持久层接口
 */
@Mapper
@Repository
public interface CartsDAO {


    /**
     * 根据主键id 删除一条购物车数据
     * @param id 购物车 主键id
     * @return 数据库中受影响的行数
     */
    int deleteByPrimaryKey(Long id);


    /**
     * 新增一条购物车数据
     * @param record 待新增的购物车对象
     * @return 数据库中受影响的行数
     */
    int insert(Carts record);


    /**
     * 新增一条购物车数据，判断每个属性是否为Null，为不是Null的属性做新增
     * @param record 待新增的购物车对象
     * @return 数据库中受影响的行数
     */
    int insertSelective(Carts record);


    /**
     * 根据主键id 查询一条购物车数据
     * @param id 购物车 主键id
     * @return 一个Carts对象
     */
    Carts selectByPrimaryKey(Long id);


    /**
     * 更新一条购物车数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param record 更新后的购物车对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKeySelective(Carts record);


    /**
     * 更新一条购物车数据
     * @param record 更新后的购物车对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKey(Carts record);


    /**
     * 批量更新多条购物车数据
     * @param list 存储多个更新后的购物车对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatch(List<Carts> list);


    /**
     * 批量更新多条购物车数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param list 存储多个更新后的购物车对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatchSelective(List<Carts> list);


    /**
     * 批量新增多条购物车数据
     * @param list 存储多个待新增的购物车对象 的集合
     * @return 数据库中受影响的行数
     */
    int batchInsert(@Param("list") List<Carts> list);


    /**
     * 根据关联的用户主键id 查询该用户的购物车列表
     * @param userId 关联的用户主键id
     * @return 该用户的购物车列表
     */
    List<Carts> selectByUserId(@Param("userId") Long userId);


    /**
     * 根据关联的商品主键id 和 关联的用户主键id 查询该用户有关商品的购物车
     * @param userId 关联的用户主键id
     * @param goodsId 关联的商品主键id
     * @return 该用户有关商品的购物车
     */
    Carts selectByUserIdWithGoodsId(@Param("userId") Long userId, @Param("goodsId") Long goodsId);


}