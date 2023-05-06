package com.past.dao;

import com.past.domain.entity.Guess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 操作猜想推送实体的数据库持久层接口
 */
@Mapper
@Repository
public interface GuessDAO {


    /**
     * 根据主键id 删除一条猜想推送数据
     * @param id 猜想推送 主键id
     * @return 数据库中受影响的行数
     */
    int deleteByPrimaryKey(Long id);


    /**
     * 新增一条猜想推送数据
     * @param record 待新增的猜想推送对象
     * @return 数据库中受影响的行数
     */
    int insert(Guess record);


    /**
     * 新增一条猜想推送数据，判断每个属性是否为Null，为不是Null的属性做新增
     * @param record 待新增的猜想推送对象
     * @return 数据库中受影响的行数
     */
    int insertSelective(Guess record);


    /**
     * 根据主键id 查询一条猜想推送数据
     * @param id 猜想推送 主键id
     * @return 一个Guess对象
     */
    Guess selectByPrimaryKey(Long id);


    /**
     * 更新一条猜想推送数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param record 更新后的猜想推送对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKeySelective(Guess record);


    /**
     * 更新一条猜想推送数据
     * @param record 更新后的猜想推送对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKey(Guess record);


    /**
     * 批量更新多条猜想推送数据
     * @param list 存储多个更新后的猜想推送对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatch(List<Guess> list);


    /**
     * 批量更新多条猜想推送数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param list 存储多个更新后的猜想推送对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatchSelective(List<Guess> list);


    /**
     * 批量新增多条猜想推送数据
     * @param list 存储多个待新增的猜想推送对象 的集合
     * @return 数据库中受影响的行数
     */
    int batchInsert(@Param("list") List<Guess> list);


    /**
     * 查询所有猜想推送
     * @return 猜想推送列表
     */
    List<Guess> selectAll();


    /**
     * 根据关联的用户主键id 查询有关该用户的猜想推送列表
     * @param userId 关联的用户主键id
     * @return 该用户的猜想推送列表
     */
    List<Guess> selectByUserId(@Param("userId") Long userId);


    /**
     * 根据关联的用户主键id 查询有关该用户收藏的猜想推送列表
     * @param userId 关联的用户主键id
     * @return 该用户收藏的猜想推送列表
     */
    List<Guess> selectFavoriteByUserId(@Param("userId") Long userId);


    /**
     * 根据关联的用户主键id 查询有关该用户收藏的猜想推送列表 根据数量倒序排列
     * @param userId 关联的用户主键id
     * @return 该用户收藏的猜想推送列表
     */
    List<Guess> selectMostLikeByUserId(@Param("userId") Long userId);


    /**
     * 根据 关联的用户主键id 和 关联的商品主键id 查询该用户有关该商品的猜想推送
     * @param userId 关联的用户主键id
     * @param goodsId 关联的商品主键id
     * @return 该用户有关该商品的猜想推送
     */
    Guess selectByUserIdWithGoodsId(@Param("userId") Long userId, @Param("goodsId") Long goodsId);


    /**
     * 根据关联的用户主键id 查询该用户最近的猜想推送列表
     * @param userId 关联的用户主键id
     * @return 该用户最近的猜想推送列表
     */
    List<Guess> selectRecentViewGoodsByUserId(@Param("userId") Long userId);


    /**
     * 统计商品主键id 和 商品推送数目
     * @return List<Map<String,Object>>
     */
    List<Map<String,Object>> selectMostHotGoods();


}