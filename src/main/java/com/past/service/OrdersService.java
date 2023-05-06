package com.past.service;

import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.domain.dto.OrdersDTO;
import com.past.domain.dto.OrdersDetailDTO;
import com.past.domain.vo.OrderSearchVO;
import com.past.domain.vo.OrderWelcomeVO;
import com.past.domain.vo.OrdersDetailVO;
import com.past.domain.vo.OrdersVO;
import com.past.validation.InsertValidationGroup;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

/**
 * 订单模块 业务层接口
 */
@Validated
public interface OrdersService {


    /**
     * 根据主键id 查询订单 含详情列表
     * @param id 订单主键id
     * @return OrdersVO
     */
    OrdersVO selectByPrimaryKey(String id);


    /**
     * 前台用户 下单 ，成功后订单状态默认为1 待付款
     * @param ordersDTO 订单DTO实体
     * @return OrdersVO
     */
    @Validated(value = {InsertValidationGroup.class})
    OrdersVO take(@Valid OrdersDTO ordersDTO);


    /**
     * 随着前台用户 下单   产生订单详情
     * @param ordersDetailDTO 订单详情DTO实体
     * @return OrdersDetailVO
     */
    @Validated(value = {InsertValidationGroup.class})
    OrdersDetailVO take(@Valid OrdersDetailDTO ordersDetailDTO);


    /**
     * 根据下单的用户主键id 和 订单状态 查询符合条件的订单列表
     * @param userId 下单的用户主键id
     * @param status 订单状态
     * @return 符合条件的订单列表
     */
    List<OrdersVO> selectByUserIdWithStatus(Long userId, Integer status);


    /**
     * 根据订单主键id 设置其已收货 状态为4(已收货 待评价)
     * @param id 订单主键id
     * @return 数据库受影响的行数 > 0 ? true : false
     */
    boolean receive(String id);


    /**
     * 根据订单主键id 发货 设置其待收货 状态为3(已发货 待收货)
     * @param id 订单主键id
     * @param expressNo 快递单号
     * @return 数据库受影响的行数 > 0 ? true : false
     */
    boolean deliver(String id, String expressNo);


    /**
     * 根据订单主键id 设置其完成 状态为5(已评价)
     * @param id 订单主键id
     * @return 数据库受影响的行数 > 0 ? true : false
     */
    boolean evaluate(String id);


    /**
     * 根据订单主键id 设置其待发货 状态为2(已付款 待发货)
     * @param id 订单主键id
     * @return 数据库受影响的行数 > 0 ? true : false
     */
    boolean pay(String id);


    /**
     * 根据订单主键id 设置其已删除 状态为-1(已删除)
     * @param id 订单主键id
     * @return 数据库受影响的行数 > 0 ? true : false
     */
    boolean deleteLogic(String id);


    /**
     * 更新订单
     * @param ordersDTO 更新后的订单DTO实体
     * @return 数据库受影响的行数 > 0 ? true : false
     */
    boolean update(OrdersDTO ordersDTO);


    /**
     * 查询月订单成交信息列表
     * @return 订单信息列表
     */
    List<OrderWelcomeVO> selectTotalMoneyByMonth();


    /**
     * 查询订单总量
     * @return 订单总量
     */
    Integer selectTotal();


    /**
     * 查询待发货的订单总量
     * @return 待发货的订单总量
     */
    Integer selectTotalDeliver();


    /**
     * 后台根据分页条件实体，查询符合条件的结果集
     * @param pageRequest 分页条件实体对象
     * @return 分页结果实体
     */
    PageResult<OrdersVO> selectPage(PageRequest<OrderSearchVO> pageRequest);


}
