package com.past.service.impl;

import com.google.common.base.Preconditions;
import com.past.dao.*;
import com.past.domain.dto.CartsDTO;
import com.past.domain.entity.*;
import com.past.domain.vo.*;
import com.past.service.CartsService;
import com.past.util.BeanValidator;
import com.past.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 购物车模块 业务层接口的实现类
 */
@Service
@Transactional
@Slf4j
public class CartsServiceImpl implements CartsService {

    @Autowired
    private CartsDAO cartsDAO;

    @Autowired
    private GoodsDAO goodsDAO;

    @Autowired
    private UsersDAO usersDAO;

    @Autowired
    private GoodsTypesDAO goodsTypesDAO;

    @Autowired
    private MemoryDAO memoryDAO;


    /**
     * 根据主键id 查询购物车
     * @param id 购物车主键id
     * @return CartsVO
     */
    @Override
    public CartsVO selectByPrimaryKey(Long id) {

        Carts carts = cartsDAO.selectByPrimaryKey(id);

        if (carts == null || carts.getStatus() == 1) {
            throw new NullPointerException("查找的购物车不存在！");
        }

        CartsVO vo = new CartsVO();
        BeanUtils.copyProperties(carts, vo);

        Users user = usersDAO.selectByPrimaryKey(carts.getUserId());
        if (!ObjectUtils.isEmpty(user) && user.getStatus() != 1) {
            UsersVO usersVO = new UsersVO();
            BeanUtils.copyProperties(user, usersVO);
            vo.setUser(usersVO);
        }

        Goods goods = goodsDAO.selectByPrimaryKey(carts.getGoodsId());
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

            vo.setGoods(goodsVO);
        }

        return vo;
    }


    /**
     * 根据 关联的用户主键id 和 关联的商品主键id 查询购物车
     * @param userId 关联的用户主键id
     * @param goodsId 关联的商品主键id
     * @return CartsVO
     */
    @Override
    public CartsVO selectByUserIdWithGoodsId(Long userId, Long goodsId) {

        Carts carts = cartsDAO.selectByUserIdWithGoodsId(userId, goodsId);

        if (carts == null || carts.getStatus() == 1) {
            return null;
        }

        CartsVO vo = new CartsVO();
        BeanUtils.copyProperties(carts, vo);

        Users user = usersDAO.selectByPrimaryKey(carts.getUserId());
        if (!ObjectUtils.isEmpty(user) && user.getStatus() != 1) {
            UsersVO usersVO = new UsersVO();
            BeanUtils.copyProperties(user, usersVO);
            vo.setUser(usersVO);
        }

        Goods goods = goodsDAO.selectByPrimaryKey(carts.getGoodsId());
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

            vo.setGoods(goodsVO);
        }

        return vo;
    }


    /**
     * 根据 关联的用户主键id 查询该用户的购物车列表
     * @param userId 关联的用户主键id
     * @return 该用户的购物车列表
     */
    @Override
    public List<CartsVO> selectByUserId(Long userId) {

        List<Carts> cartsList = cartsDAO.selectByUserId(userId);

        return Optional.ofNullable(cartsList)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(c -> c.getStatus() == 0)
                .map(c -> {
                    CartsVO vo = new CartsVO();
                    BeanUtils.copyProperties(c, vo);

                    Users user = usersDAO.selectByPrimaryKey(c.getUserId());
                    if (!ObjectUtils.isEmpty(user) && user.getStatus() != 1) {
                        UsersVO usersVO = new UsersVO();
                        BeanUtils.copyProperties(user, usersVO);
                        vo.setUser(usersVO);
                    }

                    Goods goods = goodsDAO.selectByPrimaryKey(c.getGoodsId());
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

                        vo.setGoods(goodsVO);
                    }

                    return vo;
                })
                .collect(Collectors.toList());
    }


    /**
     * 新增商品 到购物车
     * @param cartsDTO 待新增的购物车DTO实体
     * @return 新增后，这条数据的主键id > 0 ? true : false
     */
    @Override
    public boolean insert(CartsDTO cartsDTO) {

        /*
         * 注意：因为cartsDTO从前端传来时，核心校验参数不全，需要在service层额外set属性，校验就要在service层处理，
         * 而且异于controller层的参数校验(只在类上和方法入参添加@Validated即可)
         * 此时需要：@Validated和@Valid必须组合使用在service层接口(注意：是接口而非其实现类)，
         * @Validated添加在service层接口和方法上，@Valid添加在方法的参数前，表示级联验证
         */
        // 校验对象参数 是否满足注解约束  不满足约束，抛出ParamException
        BeanValidator.check(cartsDTO);

        Carts carts = new Carts();
        BeanUtils.copyProperties(cartsDTO, carts);
        carts.setCreateTime(new Date());
        carts.setUpdateTime(new Date());

        Users user = usersDAO.selectByPrimaryKey(cartsDTO.getUserId());
        Preconditions.checkNotNull(user, "添加购物车的用户不存在！");
        carts.setOperator(user.getUsername());
        carts.setOperatorIp(IpUtil.getLocalIP());

        return cartsDAO.insertSelective(carts) > 0;
    }


    /**
     * 删除购物车 物理删除
     * @param id 购物车 主键id
     * @return 数据库受影响的行数 > 0 ? true : false
     */
    @Override
    public boolean deletePhysical(Long id) {

        Carts carts = cartsDAO.selectByPrimaryKey(id);
        // 不存在购物车 抛出异常
        Preconditions.checkNotNull(carts, "待删除的购物车不存在！");

        return cartsDAO.deleteByPrimaryKey(id) > 0;
    }


    /**
     * 删除购物车 逻辑删除
     * @param id 购物车 主键id
     * @return 数据库受影响的行数 > 0 ? true : false
     */
    @Override
    public boolean deleteLogic(Long id) {

        Carts carts = cartsDAO.selectByPrimaryKey(id);
        // 不存在购物车 抛出异常
        Preconditions.checkNotNull(carts, "待删除的购物车不存在！");

        // 只有当其存在 且状态不是删除  才可逻辑删除
        if (carts.getStatus() != 1) {
            carts.setStatus(1);
            carts.setUpdateTime(new Date());

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();

            carts.setOperator(login.getUsername());
            carts.setOperatorIp(IpUtil.getLocalIP());

            return cartsDAO.updateByPrimaryKeySelective(carts) > 0;
        }

        return false;
    }


    /**
     * 更新购物车
     * @param cartsDTO 更新后的购物车DTO实体
     * @return 数据库受影响的行数 > 0 ? true : false
     */
    @Override
    public boolean update(CartsDTO cartsDTO) {

        /*
         * 注意：因为cartsDTO从前端传来时，核心校验参数不全，需要在service层额外set属性，校验就要在service层处理，
         * 而且异于controller层的参数校验(只在类上和方法入参添加@Validated即可)
         * 此时需要：@Validated和@Valid必须组合使用在service层接口(注意：是接口而非其实现类)，
         * @Validated添加在service层接口和方法上，@Valid添加在方法的参数前，表示级联验证
         */
        // 校验对象参数 是否满足注解约束  不满足约束，抛出ParamException
        BeanValidator.check(cartsDTO);

        Carts before = cartsDAO.selectByPrimaryKey(cartsDTO.getId());
        Preconditions.checkNotNull(before, "待更新的购物车不存在！");

        // 只有当其存在 且状态不是删除  才可更新
        if (before.getStatus() != 1) {
            Carts after = new Carts();
            BeanUtils.copyProperties(cartsDTO, after);
            after.setStatus(before.getStatus());
            after.setUpdateTime(new Date());

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();

            after.setOperator(login.getUsername());
            after.setOperatorIp(IpUtil.getLocalIP());

            return cartsDAO.updateByPrimaryKeySelective(after) > 0;
        }

        return false;
    }


}
