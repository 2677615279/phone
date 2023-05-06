package com.past.dao;

import com.past.domain.entity.OrdersDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 操作订单详情实体的数据库持久层接口
 */
@Mapper
@Repository
public interface OrdersDetailDAO {


    /**
     * 根据主键id 删除一条订单详情数据
     * @param id 订单详情 主键id
     * @return 数据库中受影响的行数
     */
    int deleteByPrimaryKey(Long id);


    /**
     * 新增一条订单详情数据
     * @param record 待新增的订单详情对象
     * @return 数据库中受影响的行数
     */
    int insert(OrdersDetail record);


    /**
     * 新增一条订单详情数据，判断每个属性是否为Null，为不是Null的属性做新增
     * @param record 待新增的订单详情对象
     * @return 数据库中受影响的行数
     */
    int insertSelective(OrdersDetail record);


    /**
     * 根据主键id 查询一条订单详情数据
     * @param id 订单详情 主键id
     * @return 一个OrdersDetail对象
     */
    OrdersDetail selectByPrimaryKey(Long id);


    /**
     * 更新一条订单详情数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param record 更新后的订单详情对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKeySelective(OrdersDetail record);


    /**
     * 更新一条订单详情数据
     * @param record 更新后的订单详情对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKey(OrdersDetail record);


    /**
     * 批量更新多条订单详情数据
     * @param list 存储多个更新后的订单详情对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatch(List<OrdersDetail> list);


    /**
     * 批量更新多条订单详情数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param list 存储多个更新后的订单详情对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatchSelective(List<OrdersDetail> list);


    /**
     * 批量新增多条订单详情数据
     * @param list 存储多个待新增的订单详情对象 的集合
     * @return 数据库中受影响的行数
     */
    int batchInsert(@Param("list") List<OrdersDetail> list);


    /**
     * 根据订单号 查询详情列表
     * @param orderId 订单id
     * @return 该订单的详情列表
     */
    List<OrdersDetail> selectByOrderId(@Param("orderId") String orderId);


}