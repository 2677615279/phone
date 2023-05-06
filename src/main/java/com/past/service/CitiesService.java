package com.past.service;

import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.domain.dto.CitiesDTO;
import com.past.domain.vo.CitiesVO;

import java.util.List;

/**
 * 城市模块 业务层接口
 */
public interface CitiesService {


    /**
     * 根据主键id 查询城市数据
     * @param id 城市 主键id
     * @return 一个CitiesVO对象
     */
    CitiesVO selectByPrimaryKey(Long id);


    /**
     * 根据城市名称 和 所属省份的主键id 查询城市
     * @param name 城市名称
     * @param provinceId 所属省份的主键id
     * @return 一个CitiesVO对象
     */
    CitiesVO selectByNameWithProvinceId(String name, Long provinceId);


    /**
     * 根据省份主键id 查询该省份下的所有城市
     * @param provinceId 省份 主键id
     * @return 存储该省份下的所有城市的集合
     */
    List<CitiesVO> selectByProvinceId(Long provinceId);


    /**
     * 根据城市主键id 查询所属省份的主键id
     * @param id 城市 主键 id
     * @return 省份的主键id
     */
    Long selectProvinceIdById(Long id);


    /**
     * 判断该城市名是否存在，存在返回true 抛出异常；不存在返回false，继续执行业务
     * @param name 城市名称
     * @param id 城市 主键id
     * @param provinceId 所属省份 主键id
     * @return 存在返回true 抛出异常；不存在返回false，继续执行业务
     */
    boolean checkNameExist(String name, Long id, Long provinceId);


    /**
     * 后台新增城市
     * @param citiesDTO 待新增的城市DTO实体
     * @return 新增后，这条数据的主键id > 0 ? true : false
     */
    boolean insert(CitiesDTO citiesDTO);


    /**
     * 后台删除城市 物理删除
     * @param id 待删除数据的主键id
     * @return 删除成功(数据库受影响的行数>0)返回true
     */
    boolean deletePhysical(Long id);


    /**
     * 后台删除城市 逻辑删除
     * @param id 待删除数据的主键id
     * @return 删除成功，即将此城市数据的状态改为1(删除)，返回true
     */
    boolean deleteLogic(Long id);


    /**
     * 后台更新城市
     * @param citiesDTO 更新后的城市DTO实体对象
     * @return 更新成功true 失败false
     */
    boolean update(CitiesDTO citiesDTO);


    /**
     * 后台查询全部城市数据(过滤掉状态为1 即删除) 的数据
     * @return 符合条件的结果集
     */
    List<CitiesVO> selectAll();


    /**
     * 后台根据分页条件实体，查询符合条件的结果集
     * @param pageRequest 分页条件实体对象
     * @return 分页结果实体
     */
    PageResult<CitiesVO> selectPage(PageRequest<CitiesDTO> pageRequest);


    /**
     * 根据城市主键id 改变城市状态
     * @param status 状态 0正常 2禁用
     * @param id 城市 主键 id
     * @return 更新后，数据库受影响的行数 > 0 ? true : false
     */
    boolean changeStatus(Integer status, Long id);


    /**
     * 根据所属省份主键id 改变其下所有城市、区县的状态
     * @param status 状态 0正常 2禁用
     * @param provinceId 所属省份 主键 id
     */
    void changeStatusByProvincesStatus(Integer status, Long provinceId);


}
