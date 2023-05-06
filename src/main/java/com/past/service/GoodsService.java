package com.past.service;

import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.domain.dto.GoodsDTO;
import com.past.domain.vo.GoodsVO;
import com.past.domain.vo.SearchVO;

import java.util.List;

/**
 * 商品模块 业务层接口
 */
public interface GoodsService {


    /**
     * 根据主键id 查询商品
     * @param id 商品 主键id
     * @return GoodsVO
     */
    GoodsVO selectByPrimaryKey(Long id);


    /**
     * 判断 该商品的名称是否已存在
     * @param name 商品名称
     * @param id 商品 主键id
     * @return 存在返回true 抛出异常；不存在返回false，继续执行业务
     */
    boolean checkNameExist(String name, Long id);


    /**
     * 根据关联的分类主键id 查询该类的商品 存入一个集合
     * @param typeId 关联的分类主键id
     * @return 存储该类商品的集合
     */
    List<GoodsVO> selectByType(Long typeId);


    /**
     * 查询热卖的商品 即status为3 存入一个集合
     * @return 存储热卖商品的集合
     */
    List<GoodsVO> selectByHot();


    /**
     * 根据销量查询  降序排列
     * @return 存储符合条件结果的集合
     */
    List<GoodsVO> selectByVolume();


    /**
     * 根据条件 查询符合的结果集
     * @param vo 封装查询条件的实体
     * @return 符合条件的结果集
     */
    List<GoodsVO> selectByCondition(SearchVO vo);


    /**
     * 新增 商品
     * @param goodsDTO 待新增的商品DTO实体
     * @return 新增后，这条数据的主键id > 0 ? true : false
     */
    boolean insert(GoodsDTO goodsDTO);


    /**
     * 根据主键id 删除商品 物理删除
     * @param id 待删除商品的主键id
     * @return 数据库受影响的行数 > 0 ? true : false
     */
    boolean deletePhysical(Long id);


    /**
     * 根据主键id 删除商品 逻辑删除
     * @param id 待删除商品的主键id
     * @return 数据库受影响的行数 > 0 ? true : false
     */
    boolean deleteLogic(Long id);


    /**
     * 更新商品
     * @param goodsDTO 更新后的商品DTO实体
     * @return 数据库受影响的行数 > 0 ? true : false
     */
    boolean update(GoodsDTO goodsDTO);


    /**
     * 查询全部商品 过滤掉状态为1即已删除的  存入一个集合
     * @return 存储符合条件的结果集
     */
    List<GoodsVO> selectAll();


    /**
     * 后台根据分页条件实体，查询符合条件的结果集
     * @param pageRequest 分页条件实体对象
     * @return 分页结果实体
     */
    PageResult<GoodsVO> selectPage(PageRequest<GoodsDTO> pageRequest);


}
