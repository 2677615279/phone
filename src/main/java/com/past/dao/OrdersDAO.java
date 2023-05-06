package com.past.dao;

import com.past.beans.PageRequest;
import com.past.domain.entity.Orders;
import com.past.domain.vo.OrderSearchVO;
import com.past.domain.vo.OrderWelcomeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 操作订单实体的数据库持久层接口
 */
@Mapper
@Repository
public interface OrdersDAO {


    /**
     * 根据主键id 删除一条订单数据
     * @param id 订单 主键id
     * @return 数据库中受影响的行数
     */
    int deleteByPrimaryKey(String id);


    /**
     * 新增一条订单数据
     * @param record 待新增的订单对象
     * @return 数据库中受影响的行数
     */
    int insert(Orders record);


    /**
     * 新增一条订单数据，判断每个属性是否为Null，为不是Null的属性做新增
     * @param record 待新增的订单对象
     * @return 数据库中受影响的行数
     */
    int insertSelective(Orders record);


    /**
     * 根据主键id 查询一条订单数据
     * @param id 订单 主键id
     * @return 一个Orders对象
     */
    Orders selectByPrimaryKey(String id);


    /**
     * 更新一条订单数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param record 更新后的订单对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKeySelective(Orders record);


    /**
     * 更新一条订单数据
     * @param record 更新后的订单对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKey(Orders record);


    /**
     * 批量更新多条订单数据
     * @param list 存储多个更新后的订单对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatch(List<Orders> list);


    /**
     * 批量更新多条订单数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param list 存储多个更新后的订单对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatchSelective(List<Orders> list);


    /**
     * 批量新增多条订单数据
     * @param list 存储多个待新增的订单对象 的集合
     * @return 数据库中受影响的行数
     */
    int batchInsert(@Param("list") List<Orders> list);


    /**
     * 根据查询条件 搜索符合条件的订单列表
     * @param pageRequest 封装查询条件的实体
     * @return 符合条件的订单列表
     */
    List<Orders> selectByOrderSearchVO(@Param("pageRequest") PageRequest<OrderSearchVO> pageRequest);


    /**
     * 根据用户主键id 查询其下的订单列表
     * @param userId 关联用户的主键id
     * @return 该用户的订单列表
     */
    List<Orders> selectByUserId(@Param("userId") Long userId);


    /**
     * 根据用户主键id 和 订单状态 查询符合条件的订单列表
     * @param status 状态 1待付款 2待发货 3待收货 4待评价 5完成
     * @param userId 关联用户的主键id
     * @return 该用户符合条件的订单列表
     */
    List<Orders> selectByUserIdWithStatus(@Param("status") Integer status, @Param("userId") Long userId);


    /**
     * 查询月订单成交信息列表
     * @return 订单信息列表
     */
    List<OrderWelcomeVO> selectTotalMoneyByMonth();


    /**
     * 查询订单总量
     * @return 订单总量
     */
    int selectTotal();


    /**
     * 查询待发货的订单总量
     * @return 待发货的订单总量
     */
    int selectTotalDeliver();


    /**
     * 查询所有订单数据
     * @return 存储所有订单对象的集合
     */
    List<Orders> selectAll();


}