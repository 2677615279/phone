package com.past.service;

import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.domain.dto.AreasDTO;
import com.past.domain.vo.AreasVO;

import java.util.List;

/**
 * 区县模块 业务层接口
 */
public interface AreasService {


    /**
     * 根据主键id 查询区县数据
     * @param id 区县 主键id
     * @return 一个AreasVO对象
     */
    AreasVO selectByPrimaryKey(Long id);


    /**
     * 根据区县名称 和 所属城市的主键id 查询区县
     * @param name 区县名称
     * @param cityId 所属城市的主键id
     * @return 一个AreasVO对象
     */
    AreasVO selectByNameWithCityId(String name, Long cityId);


    /**
     * 根据城市主键id 查询该城市下的所有区县
     * @param cityId 城市 主键id
     * @return 存储该城市下的所有区县的集合
     */
    List<AreasVO> selectByCityId(Long cityId);


    /**
     * 判断该区县名是否存在，存在返回true 抛出异常；不存在返回false，继续执行业务
     * @param name 区县名称
     * @param id 区县 主键id
     * @param cityId 所属城市 主键id
     * @return 存在返回true 抛出异常；不存在返回false，继续执行业务
     */
    boolean checkNameExist(String name, Long id, Long cityId);


    /**
     * 后台新增区县
     * @param areasDTO 待新增的区县DTO实体
     * @return 新增后，这条数据的主键id > 0 ? true : false
     */
    boolean insert(AreasDTO areasDTO);


    /**
     * 后台删除区县 物理删除
     * @param id 待删除数据的主键id
     * @return 删除成功(数据库受影响的行数>0)返回true
     */
    boolean deletePhysical(Long id);


    /**
     * 后台删除区县 逻辑删除
     * @param id 待删除数据的主键id
     * @return 删除成功，即将此区县数据的状态改为1(删除)，返回true
     */
    boolean deleteLogic(Long id);


    /**
     * 后台更新区县
     * @param areasDTO 更新后的区县DTO实体对象
     * @return 更新成功true 失败false
     */
    boolean update(AreasDTO areasDTO);


    /**
     * 后台查询全部区县数据(过滤掉状态为1 即删除) 的数据
     * @return 符合条件的结果集
     */
    List<AreasVO> selectAll();


    /**
     * 后台根据分页条件实体，查询符合条件的结果集
     * @param pageRequest 分页条件实体对象
     * @return 分页结果实体
     */
    PageResult<AreasVO> selectPage(PageRequest<AreasDTO> pageRequest);


    /**
     * 根据区县主键id 改变区县状态
     * @param status 状态 0正常 2禁用
     * @param id 区县 主键 id
     * @return 更新后，数据库受影响的行数 > 0 ? true : false
     */
    boolean changeStatus(Integer status, Long id);


    /**
     * 根据所属城市主键id 改变其下所有区县的状态
     * @param status 状态 0正常 2禁用
     * @param cityId 所属城市 主键 id
     */
    void changeStatusByCitiesStatus(Integer status, Long cityId);
    
    
}
