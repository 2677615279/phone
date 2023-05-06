package com.past.service;

import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.domain.dto.GuessDTO;
import com.past.domain.vo.GuessVO;
import com.past.validation.InsertValidationGroup;
import com.past.validation.UpdateValidationGroup;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 猜想推送模块 业务层接口
 */
@Validated
public interface GuessService {


    /**
     * 根据关联的用户主键id和关联的商品主键id 查询该用户有关该商品的猜想推送
     * @param userId 关联的用户主键id
     * @param goodsId 关联的商品主键id
     * @return 该用户有关该商品的猜想推送
     */
    GuessVO selectByUserIdWithGoodsId(Long userId, Long goodsId);


    /**
     * 根据关联的用户主键id 查询该用户的猜想推送列表
     * @param userId 关联的用户主键id
     * @return 该用户的猜想推送列表
     */
    List<GuessVO> selectByUserId(Long userId);


    /**
     * 根据关联的用户主键id 查询该用户最近浏览的未收藏的商品猜想推送列表
     * @param userId 关联的用户主键id
     * @return 该用户最近的猜想推送列表
     */
    List<GuessVO> selectRecentViewGoodsByUserId(Long userId);


    /**
     * 统计商品主键id 和 商品点击次数
     * @return List<Map<String,Object>>
     */
    List<Map<String,Object>> selectMostHotGoods();


    /**
     * 新增猜想推送
     * @param guessDTO 待新增的猜想推送DTO实体
     * @return 该数据新增后的主键id
     */
    @Validated(value = {InsertValidationGroup.class})
    Integer insert(@Valid GuessDTO guessDTO);


    /**
     * 更新猜想推送
     * @param guessDTO 更新后的猜想推送DTO实体
     * @return 该数据新增后的主键id
     */
    @Validated(value = {UpdateValidationGroup.class})
    Integer update(@Valid GuessDTO guessDTO);


    /**
     * 根据关联的用户主键id和关联的商品主键id 查询该用户有关该商品的猜想推送 将其favorite字段改为1，即收藏推送中的商品
     * @param userId 联的用户主键id
     * @param goodsId 关联的商品主键id
     * @return 数据库受影响的行数 > 0 ? true : false
     */
    boolean insertFavorite(Long userId, Long goodsId);


    /**
     * 根据关联的用户主键id和关联的商品主键id 查询该用户有关该商品的猜想推送 将其favorite字段改为-1，即不收藏推送中的商品
     * @param userId 联的用户主键id
     * @param goodsId 关联的商品主键id
     * @return 数据库受影响的行数 > 0 ? true : false
     */
    boolean removeFavorite(Long userId, Long goodsId);


    /**
     * 根据关联的用户主键id 查询出该用户收藏的推送列表 再分页
     * @param userId 关联的用户主键id
     * @param pageRequest 封装分页请求参数的实体
     * @return 符合条件的结果集
     */
    PageResult<GuessVO> selectFavoriteByUserId(Long userId, PageRequest<Object> pageRequest);


}
