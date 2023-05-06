package com.past.dao;

import com.past.beans.PageRequest;
import com.past.domain.dto.EvaluatesDTO;
import com.past.domain.entity.Evaluates;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 操作评价实体的数据库持久层接口
 */
@Mapper
@Repository
public interface EvaluatesDAO {


    /**
     * 根据主键id 删除一条评价数据
     * @param id 评价 主键id
     * @return 数据库中受影响的行数
     */
    int deleteByPrimaryKey(Long id);


    /**
     * 新增一条评价数据
     * @param record 待新增的评价对象
     * @return 数据库中受影响的行数
     */
    int insert(Evaluates record);


    /**
     * 新增一条评价数据，判断每个属性是否为Null，为不是Null的属性做新增
     * @param record 待新增的评价对象
     * @return 数据库中受影响的行数
     */
    int insertSelective(Evaluates record);


    /**
     * 根据主键id 查询一条评价数据
     * @param id 评价 主键id
     * @return 一个Evaluates对象
     */
    Evaluates selectByPrimaryKey(Long id);


    /**
     * 更新一条评价数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param record 更新后的评价对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKeySelective(Evaluates record);


    /**
     * 更新一条评价数据
     * @param record 更新后的评价对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKey(Evaluates record);


    /**
     * 批量更新多条评价数据
     * @param list 存储多个更新后的评价对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatch(List<Evaluates> list);


    /**
     * 批量更新多条评价数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param list 存储多个更新后的评价对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatchSelective(List<Evaluates> list);


    /**
     * 批量新增多条评价数据
     * @param list 存储多个待新增的评价对象 的集合
     * @return 数据库中受影响的行数
     */
    int batchInsert(@Param("list") List<Evaluates> list);


    /**
     * 查询全部 评价，存入一个集合
     * @return 存储所有评价的集合
     */
    List<Evaluates> selectAll();


    /**
     * 根据关联的用户主键id，查询该用户的所有评价内容，并存入一个集合
     * @param userId 关联的用户主键id，即评价人的主键id
     * @return 存储该用户的所有评价内容的集合
     */
    List<Evaluates> selectByUserId(@Param("userId") Long userId);


    /**
     * 根据关联的商品主键id，查询该商品的所有评价内容，并存入一个集合
     * @param goodsId 关联的商品主键id
     * @return 存储该商品的所有评价内容的集合
     */
    List<Evaluates> selectByGoodsId(@Param("goodsId") Long goodsId);


    /**
     * 根据评价内容 模糊查询
     * @param pageRequest 封装分页条件的实体
     * @return 符合条件的结果集
     */
    List<Evaluates> selectByContentContaining(@Param("pageRequest") PageRequest<EvaluatesDTO> pageRequest);


}