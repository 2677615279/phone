package com.past.dao;

import com.past.beans.PageRequest;
import com.past.domain.dto.GoodsDTO;
import com.past.domain.entity.Goods;
import com.past.domain.vo.SearchVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 操作商品实体的数据库持久层接口
 */
@Mapper
@Repository
public interface GoodsDAO {


    /**
     * 根据主键id 删除一条商品数据
     * @param id 商品 主键id
     * @return 数据库中受影响的行数
     */
    int deleteByPrimaryKey(Long id);


    /**
     * 新增一条商品数据
     * @param record 待新增的商品对象
     * @return 数据库中受影响的行数
     */
    int insert(Goods record);


    /**
     * 新增一条商品数据，判断每个属性是否为Null，为不是Null的属性做新增
     * @param record 待新增的商品对象
     * @return 数据库中受影响的行数
     */
    int insertSelective(Goods record);


    /**
     * 根据主键id 查询一条商品数据
     * @param id 商品 主键id
     * @return 一个Goods对象
     */
    Goods selectByPrimaryKey(Long id);

    
    /**
     * 更新一条商品数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param record 更新后的商品对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKeySelective(Goods record);

    
    /**
     * 更新一条商品数据
     * @param record 更新后的商品对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKey(Goods record);
    
    
    /**
     * 批量更新多条商品数据
     * @param list 存储多个更新后的商品对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatch(List<Goods> list);
    
    
    /**
     * 批量更新多条商品数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param list 存储多个更新后的商品对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatchSelective(List<Goods> list);
    
    
    /**
     * 批量新增多条商品数据
     * @param list 存储多个待新增的商品对象 的集合
     * @return 数据库中受影响的行数
     */
    int batchInsert(@Param("list") List<Goods> list);
    

    /**
     * 根据id和商品名称查询的记录数
     * @param name 商品名称
     * @param id 商品主键id
     * @return 查询记录数
     */
    int countByName(@Param("name") String name, @Param("id") Long id);
    
    
    /**
     * 查询所有商品数据
     * @param flag true：查询全部，根据volume倒序排列   false：查询全部
     * @return 存储所有商品的集合
     */
    List<Goods> selectAll(@Param("flag") boolean flag);


    /**
     * 根据搜索条件 搜索商品
     * @param searchVO 封装搜索条件的实体
     * @return 符合条件的结果集
     */
    List<Goods> selectByCondition(@Param("searchVO") SearchVO searchVO);
    
    
    /**
     * 根据商品名称 模糊查询
     * @param pageRequest 封装分页条件的实体
     * @return 符合条件的结果集
     */
    List<Goods> selectByNameContaining(@Param("pageRequest") PageRequest<GoodsDTO> pageRequest);


    /**
     * 根据类别查询 商品
     * @param type 所属类型的主键id
     * @return 存储该类别的商品的集合
     */
    List<Goods> selectByType(@Param("type") Long type);


    /**
     * 根据商品主键id 改变商品状态
     * @param status 状态 0正常 2禁用
     * @param id 商品 主键id
     * @param updateTime 最后一次更新时间
     * @param operator 最后一次操作者名称
     * @param operatorIp 最后一次操作者的ip
     * @return 更新后，数据库受影响的行数 > 0 ? true : false
     */
    int changeStatus(@Param("status") Integer status, @Param("id") Long id,
                     @Param("updateTime") Date updateTime, @Param("operator") String operator, @Param("operatorIp") String operatorIp);
    
    
}