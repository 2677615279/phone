package com.past.dao;

import com.past.domain.entity.EvaluatesImages;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 操作评价图实体的数据库持久层接口
 */
@Mapper
@Repository
public interface EvaluatesImagesDAO {


    /**
     * 根据主键id 删除一条评价图数据
     * @param id 评价图 主键id
     * @return 数据库中受影响的行数
     */
    int deleteByPrimaryKey(Long id);


    /**
     * 新增一条评价图数据
     * @param record 待新增的评价图对象
     * @return 数据库中受影响的行数
     */
    int insert(EvaluatesImages record);


    /**
     * 新增一条评价图数据，判断每个属性是否为Null，为不是Null的属性做新增
     * @param record 待新增的评价图对象
     * @return 数据库中受影响的行数
     */
    int insertSelective(EvaluatesImages record);


    /**
     * 根据主键id 查询一条评价图数据
     * @param id 评价图 主键id
     * @return 一个EvaluatesImages对象
     */
    EvaluatesImages selectByPrimaryKey(Long id);


    /**
     * 更新一条评价图数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param record 更新后的评价图对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKeySelective(EvaluatesImages record);


    /**
     * 更新一条评价图数据
     * @param record 更新后的评价图对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKey(EvaluatesImages record);


    /**
     * 批量更新多条评价图数据
     * @param list 存储多个更新后的评价图对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatch(List<EvaluatesImages> list);


    /**
     * 批量更新多条评价图数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param list 存储多个更新后的评价图对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatchSelective(List<EvaluatesImages> list);


    /**
     * 批量新增多条评价图数据
     * @param list 存储多个待新增的评价图对象 的集合
     * @return 数据库中受影响的行数
     */
    int batchInsert(@Param("list") List<EvaluatesImages> list);


    /**
     * 根据评价主键id 查询其下的所有评价图，存入一个集合
     * @param evaluateId 关联的评价主键id
     * @return 存储其下的所有评价图的集合
     */
    List<EvaluatesImages> selectByEvaluateId(@Param("evaluateId") Long evaluateId);


    /**
     * 根据评价主键id 删除其下的所有评价图
     * @param evaluateId 关联的评价主键id
     * @return 数据库受影响的行数
     */
    int deleteByEvaluateId(@Param("evaluateId") Long evaluateId);


}