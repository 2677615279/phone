package com.past.service;

import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.domain.dto.GoodsTypesDTO;
import com.past.domain.vo.GoodsTypesVO;

import java.util.List;

/**
 * 商品分类模块 业务层接口
 */
public interface GoodsTypesService {


    /**
     * 根据主键id 查询商品分类数据
     * @param id 商品分类 主键id
     * @return 一个GoodsTypes对象
     */
    GoodsTypesVO selectByPrimaryKey(Long id);


    /**
     * 判断该商品分类名称是否存在，存在返回true 抛出异常；不存在返回false，继续执行业务
     * @param name 商品分类名称
     * @param id 商品分类 主键id
     * @return 存在返回true 抛出异常；不存在返回false，继续执行业务
     */
    boolean checkNameExist(String name, Long id);


    /**
     * 后台新增商品分类
     * @param goodsTypesDTO 待新增的商品分类DTO实体
     * @return 新增后，这条数据的主键id > 0 ? true : false
     */
    boolean insert(GoodsTypesDTO goodsTypesDTO);


    /**
     * 后台删除商品分类 物理删除
     * @param id 待删除数据的主键id
     * @return 删除成功(数据库受影响的行数>0)返回true
     */
    boolean deletePhysical(Long id);


    /**
     * 后台删除商品分类 逻辑删除
     * @param id 待删除数据的主键id
     * @return 删除成功，即将此商品分类数据的状态改为1(删除)，返回true
     */
    boolean deleteLogic(Long id);


    /**
     * 后台更新商品分类
     * @param goodsTypesDTO 更新后的商品分类DTO实体对象
     * @return 更新成功true 失败false
     */
    boolean update(GoodsTypesDTO goodsTypesDTO);


    /**
     * 后台查询全部商品分类数据(过滤掉状态为1 即删除) 的数据
     * @return 符合条件的结果集
     */
    List<GoodsTypesVO> selectAll();


    /**
     * 后台根据分页条件实体，查询符合条件的结果集
     * @param pageRequest 分页条件实体对象
     * @return 分页结果实体
     */
    PageResult<GoodsTypesVO> selectPage(PageRequest<GoodsTypesDTO> pageRequest);


    /**
     * 根据商品分类主键id 改变商品分类状态
     * @param status 状态 0正常 2禁用
     * @param id 商品分类 主键 id
     * @return 更新后，数据库受影响的行数 > 0 ? true : false
     */
    boolean changeStatus(Integer status, Long id);
    
    
}
