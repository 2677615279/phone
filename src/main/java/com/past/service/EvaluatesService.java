package com.past.service;

import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.domain.dto.EvaluatesDTO;
import com.past.domain.vo.EvaluatesVO;
import com.past.validation.InsertValidationGroup;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

/**
 * 评价模块(含图) 业务层接口
 */
@Validated
public interface EvaluatesService {


    /**
     * 根据主键id 查询评价
     * @param id 评价主键id
     * @return 一条评价数据，含其下的图片
     */
    EvaluatesVO selectByPrimaryKey(Long id);


    /**
     * 新增评价(含图)
     * @param evaluatesDTO 待新增的评价DTO实体
     * @param imgs 待新增的图片名称数组
     * @return 新增后，这条数据的主键id > 0 ? true : false
     */
    @Validated(value = {InsertValidationGroup.class})
    boolean insert(@Valid EvaluatesDTO evaluatesDTO, String[] imgs);


    /**
     * 删除评价 连带删除其下的所有图评 物理删除
     * @param id 待删除数据的主键id
     * @return 删除成功(数据库受影响的行数>0)返回true
     */
    boolean deletePhysical(Long id);


    /**
     * 删除评价 连带删除其下的所有图评 逻辑删除
     * @param id 待删除数据的主键id
     * @return 删除成功(数据库受影响的行数>0)返回true
     */
    boolean deleteLogic(Long id);


    /**
     * 根据商品的主键id，查询该商品的所有评价，存入一个集合
     * @param goodsId 评价的商品的主键id
     * @return 存储该商品所有评价的集合
     */
    List<EvaluatesVO> selectByGoodsId(Long goodsId);


    /**
     * 后台根据分页条件实体，查询符合条件的结果集
     * @param pageRequest 分页条件实体对象
     * @return 分页结果实体
     */
    PageResult<EvaluatesVO> selectPage(PageRequest<EvaluatesDTO> pageRequest);


}
