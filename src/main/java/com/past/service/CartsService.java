package com.past.service;

import com.past.domain.dto.CartsDTO;
import com.past.domain.vo.CartsVO;
import com.past.validation.InsertValidationGroup;
import com.past.validation.UpdateValidationGroup;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

/**
 * 购物车模块 业务层接口
 */
@Validated
public interface CartsService {


    /**
     * 根据主键id 查询购物车
     * @param id 购物车主键id
     * @return CartsVO
     */
    CartsVO selectByPrimaryKey(Long id);


    /**
     * 根据 关联的用户主键id 和 关联的商品主键id 查询购物车
     * @param userId 关联的用户主键id
     * @param goodsId 关联的商品主键id
     * @return CartsVO
     */
    CartsVO selectByUserIdWithGoodsId(Long userId, Long goodsId);


    /**
     * 根据 关联的用户主键id 查询该用户的购物车列表
     * @param userId 关联的用户主键id
     * @return 该用户的购物车列表
     */
    List<CartsVO> selectByUserId(Long userId);


    /**
     * 新增商品 到购物车
     * @param cartsDTO 待新增的购物车DTO实体
     * @return 新增后，这条数据的主键id > 0 ? true : false
     */
    @Validated(value = {InsertValidationGroup.class})
    boolean insert(@Valid CartsDTO cartsDTO);


    /**
     * 删除购物车 物理删除
     * @param id 购物车 主键id
     * @return 数据库受影响的行数 > 0 ? true : false
     */
    boolean deletePhysical(Long id);


    /**
     * 删除购物车 逻辑删除
     * @param id 购物车 主键id
     * @return 数据库受影响的行数 > 0 ? true : false
     */
    boolean deleteLogic(Long id);


    /**
     * 更新购物车
     * @param cartsDTO 更新后的购物车DTO实体
     * @return 数据库受影响的行数 > 0 ? true : false
     */
    @Validated(value = {UpdateValidationGroup.class})
    boolean update(@Valid CartsDTO cartsDTO);


}
