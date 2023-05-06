package com.past.service.impl;

import com.google.common.base.Preconditions;
import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.dao.*;
import com.past.domain.dto.OrdersDTO;
import com.past.domain.dto.OrdersDetailDTO;
import com.past.domain.entity.*;
import com.past.domain.vo.*;
import com.past.service.OrdersService;
import com.past.util.BeanValidator;
import com.past.util.IpUtil;
import com.past.util.OrderCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 订单模块 业务层接口的实现类
 */
@Service
@Transactional
@Slf4j
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private OrdersDAO ordersDAO;

    @Autowired
    private OrdersDetailDAO ordersDetailDAO;

    @Autowired
    private GoodsDAO goodsDAO;

    @Autowired
    private GoodsTypesDAO goodsTypesDAO;

    @Autowired
    private MemoryDAO memoryDAO;

    @Autowired
    private UsersDAO usersDAO;


    /**
     * 根据主键id 查询订单 含详情列表
     * @param id 订单主键id
     * @return OrdersVO
     */
    @Override
    public OrdersVO selectByPrimaryKey(String id) {

        Orders orders = ordersDAO.selectByPrimaryKey(id);

        if (orders == null || orders.getStatus() == -1) {
            throw new NullPointerException("查找的订单不存在！");
        }

        OrdersVO vo = new OrdersVO();
        BeanUtils.copyProperties(orders, vo);

        // 查出下单的用户
        Users user = usersDAO.selectByPrimaryKey(orders.getUserId());
        if (!ObjectUtils.isEmpty(user) && user.getStatus() != 1) {
            UsersVO usersVO = new UsersVO();
            BeanUtils.copyProperties(user, usersVO);
            vo.setUser(usersVO);
        }

        // 查出订单的详情列表
        List<OrdersDetail> detailList = ordersDetailDAO.selectByOrderId(orders.getId());
        List<OrdersDetailVO> detailVOList = Optional.ofNullable(detailList)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .map(od -> {
                    OrdersDetailVO detailVO = new OrdersDetailVO();
                    BeanUtils.copyProperties(od, detailVO);

                    Goods goods = goodsDAO.selectByPrimaryKey(od.getGoodsId());
                    if (!ObjectUtils.isEmpty(goods) && goods.getStatus() != 1) {
                        GoodsVO goodsVO = new GoodsVO();
                        BeanUtils.copyProperties(goods, goodsVO);

                        GoodsTypes types = goodsTypesDAO.selectByPrimaryKey(goods.getType());
                        if (!ObjectUtils.isEmpty(types) && types.getStatus() != 1) {
                            GoodsTypesVO typesVO = new GoodsTypesVO();
                            BeanUtils.copyProperties(types, typesVO);
                            goodsVO.setType(typesVO);
                        }

                        Memory memory = memoryDAO.selectByPrimaryKey(goods.getMemory());
                        if (!ObjectUtils.isEmpty(memory) && memory.getStatus() != 1) {
                            MemoryVO memoryVO = new MemoryVO();
                            BeanUtils.copyProperties(memory, memoryVO);
                            goodsVO.setMemory(memoryVO);
                        }

                        // 此处不用查商品的评价
                        detailVO.setGoods(goodsVO);
                    }

                    // 注释掉  如果放开  会形成自循环 抛出异常
//                    detailVO.setOrders(vo);
                    return detailVO;
                })
                .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(detailVOList)) {
            vo.setDetailList(detailVOList);
        }

        return vo;
    }


    /**
     * 前台用户 下单
     * @param ordersDTO 订单DTO实体
     * @return OrdersVO
     */
    @Override
    public OrdersVO take(OrdersDTO ordersDTO) {

        /*
         * 注意：因为ordersDTO从前端传来时，核心校验参数不全，需要在service层额外set属性，校验就要在service层处理，
         * 而且异于controller层的参数校验(只在类上和方法入参添加@Validated即可)
         * 此时需要：@Validated和@Valid必须组合使用在service层接口(注意：是接口而非其实现类)，
         * @Validated添加在service层接口和方法上，@Valid添加在方法的参数前，表示级联验证
         */
        // 校验对象参数 是否满足注解约束  不满足约束，抛出ParamException
        BeanValidator.check(ordersDTO);

        // 使用工具类 生成订单id
        ordersDTO.setId(OrderCodeUtil.getOrderCode());

        ordersDTO.setUsername(ordersDTO.getUsername().trim());
        ordersDTO.setPhone(ordersDTO.getPhone().trim());
        ordersDTO.setAddress(ordersDTO.getAddress().trim());

        Users user = usersDAO.selectByPrimaryKey(ordersDTO.getUserId());
        // 检查下单用户是否为null 为null抛出异常
        Preconditions.checkNotNull(user, "下单的用户不存在！");
        if (user.getStatus() != 0) {
            throw new NullPointerException("下单的用户不存在或被禁用！");
        }

        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersDTO, orders);
        orders.setCreateTime(new Date());
        orders.setUpdateTime(new Date());
        orders.setOperator(user.getUsername());
        orders.setOperatorIp(IpUtil.getLocalIP());

        int res = ordersDAO.insertSelective(orders);
        if (res > 0) {
            OrdersVO vo = new OrdersVO();
            BeanUtils.copyProperties(orders, vo);
            UsersVO usersVO = new UsersVO();
            BeanUtils.copyProperties(user, usersVO);
            vo.setUser(usersVO);

            return vo;
        }

        return null;
    }


    /**
     * 随着前台用户 下单   产生新的订单详情
     * @param ordersDetailDTO 订单详情DTO实体
     * @return OrdersDetailVO
     */
    @Override
    public OrdersDetailVO take(OrdersDetailDTO ordersDetailDTO) {

        /*
         * 注意：因为ordersDetailDTO从前端传来时，核心校验参数不全，需要在service层额外set属性，校验就要在service层处理，
         * 而且异于controller层的参数校验(只在类上和方法入参添加@Validated即可)
         * 此时需要：@Validated和@Valid必须组合使用在service层接口(注意：是接口而非其实现类)，
         * @Validated添加在service层接口和方法上，@Valid添加在方法的参数前，表示级联验证
         */
        // 校验对象参数 是否满足注解约束  不满足约束，抛出ParamException
        BeanValidator.check(ordersDetailDTO);

        Goods goods = goodsDAO.selectByPrimaryKey(ordersDetailDTO.getGoodsId());
        // 检查下单商品是否为null 为null抛出异常
        Preconditions.checkNotNull(goods, "下单的商品不存在！");
        if (goods.getStatus() == 1) {
            throw new RuntimeException("下单的商品不存在！");
        }
        GoodsVO goodsVO = new GoodsVO();

        GoodsTypes goodsTypes = goodsTypesDAO.selectByPrimaryKey(goods.getType());
        if (!ObjectUtils.isEmpty(goodsTypes) && goodsTypes.getStatus() != 1) {
            GoodsTypesVO typesVO = new GoodsTypesVO();
            BeanUtils.copyProperties(goodsTypes, typesVO);
            goodsVO.setType(typesVO);
        }

        Memory memory = memoryDAO.selectByPrimaryKey(goods.getMemory());
        if (!ObjectUtils.isEmpty(memory) && memory.getStatus() != 1) {
            MemoryVO memoryVO = new MemoryVO();
            BeanUtils.copyProperties(memory, memoryVO);
            goodsVO.setMemory(memoryVO);
        }

        // 此处不用查商品的评价

        OrdersDetail ordersDetail = new OrdersDetail();
        BeanUtils.copyProperties(ordersDetailDTO, ordersDetail);
        ordersDetail.setCreateTime(new Date());
        ordersDetail.setUpdateTime(new Date());

        // 获取主体对象
        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        ordersDetail.setOperator(login.getUsername());
        ordersDetail.setOperatorIp(IpUtil.getLocalIP());

        int res = ordersDetailDAO.insertSelective(ordersDetail);
        if (res > 0) {
            OrdersDetailVO vo = new OrdersDetailVO();
            BeanUtils.copyProperties(ordersDetail, vo);
            vo.setGoods(goodsVO);

            return vo;
        }

        return null;
    }


    /**
     * 根据下单的用户主键id 和 订单状态 查询符合条件的订单列表
     * @param userId 下单的用户主键id
     * @param status 订单状态
     * @return 符合条件的订单列表
     */
    @Override
    public List<OrdersVO> selectByUserIdWithStatus(Long userId, Integer status) {

        List<Orders> all = ordersDAO.selectByUserIdWithStatus(status, userId);

        return Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(o -> o.getStatus() != -1)
                .map(o -> {
                    OrdersVO vo = new OrdersVO();
                    BeanUtils.copyProperties(o, vo);

                    Users user = usersDAO.selectByPrimaryKey(o.getUserId());
                    if (!ObjectUtils.isEmpty(user) && user.getStatus() != 1) {
                        UsersVO usersVO = new UsersVO();
                        BeanUtils.copyProperties(user, usersVO);
                        vo.setUser(usersVO);
                    }

                    // 根据关联的订单主键id 查询详情列表 转换实体
                    List<OrdersDetailVO> detailVOList = ordersDetailDAO.selectByOrderId(o.getId())
                            .stream()
                            .map(od -> {
                                OrdersDetailVO detailVO = new OrdersDetailVO();
                                BeanUtils.copyProperties(od, detailVO);

                                Goods goods = goodsDAO.selectByPrimaryKey(od.getGoodsId());
                                if (!ObjectUtils.isEmpty(goods) && goods.getStatus() != 1) {
                                    GoodsVO goodsVO = new GoodsVO();
                                    BeanUtils.copyProperties(goods, goodsVO);

                                    GoodsTypes goodsTypes = goodsTypesDAO.selectByPrimaryKey(goods.getType());
                                    if (!ObjectUtils.isEmpty(goodsTypes) && goodsTypes.getStatus() != 1) {
                                        GoodsTypesVO goodsTypesVO = new GoodsTypesVO();
                                        BeanUtils.copyProperties(goodsTypes, goodsTypesVO);
                                        goodsVO.setType(goodsTypesVO);
                                    }

                                    Memory memory = memoryDAO.selectByPrimaryKey(goods.getMemory());
                                    if (!ObjectUtils.isEmpty(memory) && memory.getStatus() != 1) {
                                        MemoryVO memoryVO = new MemoryVO();
                                        BeanUtils.copyProperties(memory, memoryVO);
                                        goodsVO.setMemory(memoryVO);
                                    }

                                    detailVO.setGoods(goodsVO);
                                }

                                // 注释掉  如果放开  会形成自循环 抛出异常
//                                detailVO.setOrders(vo);
                                return detailVO;
                            })
                            .collect(Collectors.toList());

                    if (!CollectionUtils.isEmpty(detailVOList)) {
                        vo.setDetailList(detailVOList);
                    }

                    return vo;
                })
                .collect(Collectors.toList());
    }


    /**
     * 根据订单主键id 设置其已收货 状态为4(已收货 待评价)
     * @param id 订单主键id
     * @return 数据库受影响的行数 > 0 ? true : false
     */
    @Override
    public boolean receive(String id) {

        Orders orders = ordersDAO.selectByPrimaryKey(id);

        Preconditions.checkNotNull(orders, "已收货待评价的订单不存在！");

        // 收货的必要前提 必须保证订单是待收货状态 即status为3
        if (orders.getStatus() == 3) {
            // 设置已收货 待评价 的状态
            orders.setStatus(4);
            orders.setUpdateTime(new Date());

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();

            orders.setOperator(login.getUsername());
            orders.setOperatorIp(IpUtil.getLocalIP());

            return ordersDAO.updateByPrimaryKeySelective(orders) > 0;
        }

        return false;
    }


    /**
     * 根据订单主键id 发货 设置其待收货 状态为3(已发货 待收货)
     * @param id 订单主键id
     * @param expressNo 快递单号
     * @return 数据库受影响的行数 > 0 ? true : false
     */
    @Override
    public boolean deliver(String id, String expressNo) {

        Orders orders = ordersDAO.selectByPrimaryKey(id);

        Preconditions.checkNotNull(orders, "已发货待收货的订单不存在！");

        // 发货的必要前提 必须保证订单是待发货状态 即status为2
        if (orders.getStatus() == 2) {
            // 设置已发货 待收货 的状态
            orders.setStatus(3);
            // 设置快递单号
            orders.setExpressNo(expressNo);
            orders.setUpdateTime(new Date());

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();

            orders.setOperator(login.getUsername());
            orders.setOperatorIp(IpUtil.getLocalIP());

            return ordersDAO.updateByPrimaryKeySelective(orders) > 0;
        }

        return false;
    }


    /**
     * 根据订单主键id 设置其完成 状态为5(已评价)
     * @param id 订单主键id
     * @return 数据库受影响的行数 > 0 ? true : false
     */
    @Override
    public boolean evaluate(String id) {

        Orders orders = ordersDAO.selectByPrimaryKey(id);

        Preconditions.checkNotNull(orders, "已完成的订单不存在！");

        // 评价的必要前提 必须保证订单是待评价状态 即status为4
        if (orders.getStatus() == 4) {
            // 设置已完成 的状态
            orders.setStatus(5);
            orders.setUpdateTime(new Date());

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();

            orders.setOperator(login.getUsername());
            orders.setOperatorIp(IpUtil.getLocalIP());

            return ordersDAO.updateByPrimaryKeySelective(orders) > 0;
        }

        return false;
    }


    /**
     * 根据订单主键id 设置其待发货 状态为2(已付款 待发货)
     * @param id 订单主键id
     * @return 数据库受影响的行数 > 0 ? true : false
     */
    @Override
    public boolean pay(String id) {

        Orders orders = ordersDAO.selectByPrimaryKey(id);

        Preconditions.checkNotNull(orders, "已付款待发货的订单不存在！");

        // 付款的必要前提 必须保证订单是待付款状态 即status为1
        if (orders.getStatus() == 1) {
            // 设置已付款 待发货 的状态
            orders.setStatus(2);
            orders.setUpdateTime(new Date());

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();

            orders.setOperator(login.getUsername());
            orders.setOperatorIp(IpUtil.getLocalIP());

            return ordersDAO.updateByPrimaryKeySelective(orders) > 0;
        }

        return false;
    }


    /**
     * 根据订单主键id 设置其已删除 状态为-1(已删除)
     * @param id 订单主键id
     * @return 数据库受影响的行数 > 0 ? true : false
     */
    @Override
    public boolean deleteLogic(String id) {

        Orders orders = ordersDAO.selectByPrimaryKey(id);

        Preconditions.checkNotNull(orders, "待删除的订单不存在！");

        // 删除订单的必要前提 必须保证订单是已完成状态 即status为5
        if (orders.getStatus() == 5) {
            // 设置已删除 的状态
            orders.setStatus(-1);
            orders.setUpdateTime(new Date());

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();

            orders.setOperator(login.getUsername());
            orders.setOperatorIp(IpUtil.getLocalIP());

            return ordersDAO.updateByPrimaryKeySelective(orders) > 0;
        }

        return false;
    }


    /**
     * 更新订单
     * @param ordersDTO 更新后的订单DTO实体
     * @return 数据库受影响的行数 > 0 ? true : false
     */
    @Override
    public boolean update(OrdersDTO ordersDTO) {

        // 校验对象参数 是否满足注解约束  不满足约束，抛出ParamException
        BeanValidator.check(ordersDTO);

        ordersDTO.setUsername(ordersDTO.getUsername().trim());
        ordersDTO.setPhone(ordersDTO.getPhone().trim());
        ordersDTO.setAddress(ordersDTO.getAddress().trim());

        Orders before = ordersDAO.selectByPrimaryKey(ordersDTO.getId());
        Preconditions.checkNotNull(before, "待更新的订单不存在！");

        if (ordersDTO.getStatus() != -1) {
            Orders after = new Orders();
            BeanUtils.copyProperties(ordersDTO, after);
            after.setStatus(before.getStatus());
            after.setUpdateTime(new Date());

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();

            after.setOperator(login.getUsername());
            after.setOperatorIp(IpUtil.getLocalIP());

            return ordersDAO.updateByPrimaryKeySelective(after) > 0;
        }

        return false;
    }


    /**
     * 查询月订单成交信息列表
     * @return 订单信息列表
     */
    @Override
    public List<OrderWelcomeVO> selectTotalMoneyByMonth() {

        return ordersDAO.selectTotalMoneyByMonth();
    }


    /**
     * 查询订单总量
     * @return 订单总量
     */
    @Override
    public Integer selectTotal() {

        return ordersDAO.selectTotal();
    }


    /**
     * 查询待发货的订单总量
     * @return 待发货的订单总量
     */
    @Override
    public Integer selectTotalDeliver() {

        return ordersDAO.selectTotalDeliver();
    }


    /**
     * 后台根据分页条件实体，查询符合条件的结果集
     * @param pageRequest 分页条件实体对象
     * @return 分页结果实体
     */
    @Override
    public PageResult<OrdersVO> selectPage(PageRequest<OrderSearchVO> pageRequest) {

        List<Orders> all = ordersDAO.selectByOrderSearchVO(pageRequest);

        List<OrdersVO> voList = Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(o -> o.getStatus() != -1)
                .map(o -> {
                    OrdersVO vo = new OrdersVO();
                    BeanUtils.copyProperties(o, vo);

                    Users user = usersDAO.selectByPrimaryKey(o.getUserId());
                    if (!ObjectUtils.isEmpty(user) && user.getStatus() != 1) {
                        UsersVO usersVO = new UsersVO();
                        BeanUtils.copyProperties(user, usersVO);
                        vo.setUser(usersVO);
                    }

                    // 根据关联的订单主键id 查询详情列表 转换实体
                    List<OrdersDetailVO> detailVOList = ordersDetailDAO.selectByOrderId(o.getId())
                            .stream()
                            .map(od -> {
                                OrdersDetailVO detailVO = new OrdersDetailVO();
                                BeanUtils.copyProperties(od, detailVO);

                                Goods goods = goodsDAO.selectByPrimaryKey(od.getGoodsId());
                                if (!ObjectUtils.isEmpty(goods) && goods.getStatus() != 1) {
                                    GoodsVO goodsVO = new GoodsVO();
                                    BeanUtils.copyProperties(goods, goodsVO);

                                    GoodsTypes goodsTypes = goodsTypesDAO.selectByPrimaryKey(goods.getType());
                                    if (!ObjectUtils.isEmpty(goodsTypes) && goodsTypes.getStatus() != 1) {
                                        GoodsTypesVO goodsTypesVO = new GoodsTypesVO();
                                        BeanUtils.copyProperties(goodsTypes, goodsTypesVO);
                                        goodsVO.setType(goodsTypesVO);
                                    }

                                    Memory memory = memoryDAO.selectByPrimaryKey(goods.getMemory());
                                    if (!ObjectUtils.isEmpty(memory) && memory.getStatus() != 1) {
                                        MemoryVO memoryVO = new MemoryVO();
                                        BeanUtils.copyProperties(memory, memoryVO);
                                        goodsVO.setMemory(memoryVO);
                                    }

                                    detailVO.setGoods(goodsVO);
                                }

                                // 注释掉  如果放开  会形成自循环 抛出异常
//                                detailVO.setOrders(vo);
                                return detailVO;
                            })
                            .collect(Collectors.toList());

                    if (!CollectionUtils.isEmpty(detailVOList)) {
                        vo.setDetailList(detailVOList);
                    }

                    return vo;
                })
                .collect(Collectors.toList());

        return new PageResult<>(pageRequest, voList);
    }


}