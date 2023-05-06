package com.past.service.impl;

import com.google.common.base.Preconditions;
import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.dao.*;
import com.past.domain.dto.GuessDTO;
import com.past.domain.entity.*;
import com.past.domain.vo.*;
import com.past.service.GuessService;
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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 猜想推送模块 业务层接口的实现类
 */
@Service
@Transactional
@Slf4j
public class GuessServiceImpl implements GuessService {

    @Autowired
    private GuessDAO guessDAO;

    @Autowired
    private UsersDAO usersDAO;

    @Autowired
    private GoodsDAO goodsDAO;

    @Autowired
    private GoodsTypesDAO goodsTypesDAO;

    @Autowired
    private MemoryDAO memoryDAO;


    /**
     * 根据关联的用户主键id和关联的商品主键id 查询该用户有关该商品的猜想推送
     * @param userId 关联的用户主键id
     * @param goodsId 关联的商品主键id
     * @return 该用户有关该商品的猜想推送
     */
    @Override
    public GuessVO selectByUserIdWithGoodsId(Long userId, Long goodsId) {

        Guess guess = guessDAO.selectByUserIdWithGoodsId(userId, goodsId);

        if (guess == null) {
            return null;
        }

        GuessVO vo = new GuessVO();
        BeanUtils.copyProperties(guess, vo);

        Users user = usersDAO.selectByPrimaryKey(guess.getUserId());
        if (!ObjectUtils.isEmpty(user) && user.getStatus() != 1) {
            UsersVO usersVO = new UsersVO();
            BeanUtils.copyProperties(user, usersVO);
            vo.setUser(usersVO);
        }

        Goods goods = goodsDAO.selectByPrimaryKey(guess.getGoodsId());
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
     * 根据关联的用户主键id 查询该用户的猜想推送列表
     * @param userId 关联的用户主键id
     * @return 该用户的猜想推送列表
     */
    @Override
    public List<GuessVO> selectByUserId(Long userId) {

        List<Guess> all = guessDAO.selectMostLikeByUserId(userId);

        return Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .map(g -> {
                    GuessVO vo = new GuessVO();
                    BeanUtils.copyProperties(g, vo);

                    Users user = usersDAO.selectByPrimaryKey(g.getUserId());
                    if (!ObjectUtils.isEmpty(user) && user.getStatus() != 1) {
                        UsersVO usersVO = new UsersVO();
                        BeanUtils.copyProperties(user, usersVO);
                        vo.setUser(usersVO);
                    }

                    Goods goods = goodsDAO.selectByPrimaryKey(g.getGoodsId());
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
     * 根据关联的用户主键id 查询该用户最近浏览的未收藏的商品猜想推送列表
     * @param userId 关联的用户主键id
     * @return 该用户最近的猜想推送列表
     */
    @Override
    public List<GuessVO> selectRecentViewGoodsByUserId(Long userId) {

        List<Guess> all = guessDAO.selectRecentViewGoodsByUserId(userId);

        return Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .map(g -> {
                    GuessVO vo = new GuessVO();
                    BeanUtils.copyProperties(g, vo);

                    Users user = usersDAO.selectByPrimaryKey(g.getUserId());
                    if (!ObjectUtils.isEmpty(user) && user.getStatus() != 1) {
                        UsersVO usersVO = new UsersVO();
                        BeanUtils.copyProperties(user, usersVO);
                        vo.setUser(usersVO);
                    }

                    Goods goods = goodsDAO.selectByPrimaryKey(g.getGoodsId());
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
     * 统计商品主键id 和 商品点击次数
     * @return List<Map<String,Object>>
     */
    @Override
    public List<Map<String, Object>> selectMostHotGoods() {

        return guessDAO.selectMostHotGoods();
    }


    /**
     * 新增猜想推送
     * @param guessDTO 待新增的猜想推送DTO实体
     * @return 该数据新增后的主键id
     */
    @Override
    public Integer insert(GuessDTO guessDTO) {

        /*
         * 注意：因为guessDTO从前端传来时，核心校验参数不全，需要在service层额外set属性，校验就要在service层处理，
         * 而且异于controller层的参数校验(只在类上和方法入参添加@Validated即可)
         * 此时需要：@Validated和@Valid必须组合使用在service层接口(注意：是接口而非其实现类)，
         * @Validated添加在service层接口和方法上，@Valid添加在方法的参数前，表示级联验证
         */
        // 校验对象参数 是否满足注解约束  不满足约束，抛出ParamException
        BeanValidator.check(guessDTO);

        Guess guess = new Guess();
        BeanUtils.copyProperties(guessDTO, guess);

        guess.setCreateTime(new Date());
        guess.setUpdateTime(new Date());

        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        guess.setOperator(login.getUsername());
        guess.setOperatorIp(IpUtil.getLocalIP());

        return guessDAO.insertSelective(guess);
    }


    /**
     * 更新猜想推送
     * @param guessDTO 更新后的猜想推送DTO实体
     * @return 该数据新增后的主键id
     */
    @Override
    public Integer update(GuessDTO guessDTO) {

        /*
         * 注意：因为guessDTO从前端传来时，核心校验参数不全，需要在service层额外set属性，校验就要在service层处理，
         * 而且异于controller层的参数校验(只在类上和方法入参添加@Validated即可)
         * 此时需要：@Validated和@Valid必须组合使用在service层接口(注意：是接口而非其实现类)，
         * @Validated添加在service层接口和方法上，@Valid添加在方法的参数前，表示级联验证
         */
        // 校验对象参数 是否满足注解约束  不满足约束，抛出ParamException
        BeanValidator.check(guessDTO);

        Guess before = guessDAO.selectByPrimaryKey(guessDTO.getId());
        Preconditions.checkNotNull(before, "待更新的猜想推送不存在！");

        Guess after = new Guess();
        BeanUtils.copyProperties(guessDTO, after);
        after.setUpdateTime(new Date());
        Users user = usersDAO.selectByPrimaryKey(guessDTO.getUserId());
        if (!ObjectUtils.isEmpty(user) && user.getStatus() != 1) {
            after.setOperator(user.getUsername());
            after.setOperatorIp(IpUtil.getLocalIP());
        }

        return guessDAO.updateByPrimaryKeySelective(after);
    }


    /**
     * 根据关联的用户主键id和关联的商品主键id 查询该用户有关该商品的猜想推送 将其favorite字段改为1，即收藏推送中的商品
     * @param userId 联的用户主键id
     * @param goodsId 关联的商品主键id
     * @return 数据库受影响的行数 > 0 ? true : false
     */
    @Override
    public boolean insertFavorite(Long userId, Long goodsId) {

        Guess guess = guessDAO.selectByUserIdWithGoodsId(userId, goodsId);
        Preconditions.checkNotNull(guess, "待收藏的猜想推送不存在！");

        // 当其favorite字段不是1时 即未收藏状态 才能执行收藏操作
        if (guess.getFavorite() != 1) {
            guess.setFavorite(1);
            guess.setUpdateTime(new Date());
            Users user = usersDAO.selectByPrimaryKey(userId);
            if (!ObjectUtils.isEmpty(user) && user.getStatus() != 1) {
                guess.setOperator(user.getUsername());
                guess.setOperatorIp(IpUtil.getLocalIP());
            }

            return guessDAO.updateByPrimaryKeySelective(guess) > 0;
        }

        return false;
    }


    /**
     * 根据关联的用户主键id和关联的商品主键id 查询该用户有关该商品的猜想推送 将其favorite字段改为-1，即取消收藏推送中的商品
     * @param userId 联的用户主键id
     * @param goodsId 关联的商品主键id
     * @return 数据库受影响的行数 > 0 ? true : false
     */
    @Override
    public boolean removeFavorite(Long userId, Long goodsId) {

        Guess guess = guessDAO.selectByUserIdWithGoodsId(userId, goodsId);
        Preconditions.checkNotNull(guess, "待取消收藏的猜想推送不存在！");

        // 当其favorite字段不是-1时 即收藏状态 才能执行取消收藏操作
        if (guess.getFavorite() != -1) {
            guess.setFavorite(-1);
            guess.setUpdateTime(new Date());
            Users user = usersDAO.selectByPrimaryKey(userId);
            if (!ObjectUtils.isEmpty(user) && user.getStatus() != 1) {
                guess.setOperator(user.getUsername());
                guess.setOperatorIp(IpUtil.getLocalIP());
            }

            return guessDAO.updateByPrimaryKeySelective(guess) > 0;
        }

        return false;
    }


    /**
     * 根据关联的用户主键id 查询出该用户收藏的推送列表 再分页
     * @param userId 关联的用户主键id
     * @param pageRequest 封装分页请求参数的实体
     * @return 符合条件的结果集
     */
    @Override
    public PageResult<GuessVO> selectFavoriteByUserId(Long userId, PageRequest<Object> pageRequest) {

        List<Guess> all = guessDAO.selectFavoriteByUserId(userId);

        List<GuessVO> voList = Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .map(g -> {
                    GuessVO vo = new GuessVO();
                    BeanUtils.copyProperties(g, vo);

                    Users user = usersDAO.selectByPrimaryKey(g.getUserId());
                    if (!ObjectUtils.isEmpty(user) && user.getStatus() != 1) {
                        UsersVO usersVO = new UsersVO();
                        BeanUtils.copyProperties(user, usersVO);
                        vo.setUser(usersVO);
                    }

                    Goods goods = goodsDAO.selectByPrimaryKey(g.getGoodsId());
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

        return new PageResult<>(pageRequest, voList);
    }


}
