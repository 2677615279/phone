package com.past.service;

import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.domain.dto.ProvincesDTO;
import com.past.domain.vo.ProvincesVO;

import java.util.List;

/**
 * 省份模块 业务层接口
 */
public interface ProvincesService {


    /**
     * 根据主键id 查询省份
     * @param id 省份 主键id
     * @return 一个ProvincesVO对象
     */
    ProvincesVO selectByPrimaryKey(Long id);


    /**
     * 根据省名查询省份
     * @param name 省名
     * @return 一个ProvincesVO对象
     */
    ProvincesVO selectByName(String name);


    /**
     * 判断该省名是否存在，存在返回true 抛出异常；不存在返回false，继续执行业务
     * @param name 省名
     * @param id 省份 主键id
     * @return 存在返回true 抛出异常；不存在返回false，继续执行业务
     */
    boolean checkNameExist(String name, Long id);


    /**
     * 后台新增省份
     * @param provincesDTO 待新增的省份DTO实体
     * @return 新增后，这条数据的主键id > 0 ? true : false
     */
    boolean insert(ProvincesDTO provincesDTO);


    /**
     * 后台删除省份 物理删除
     * @param id 待删除数据的主键id
     * @return 删除成功(数据库受影响的行数>0)返回true
     */
    boolean deletePhysical(Long id);


    /**
     * 后台删除省份 逻辑删除
     * @param id 待删除数据的主键id
     * @return 删除成功，即将此省份数据的状态改为1(删除)，返回true
     */
    boolean deleteLogic(Long id);


    /**
     * 后台更新省份
     * @param provincesDTO 更新后的省份DTO实体对象
     * @return 更新成功true 失败false
     */
    boolean update(ProvincesDTO provincesDTO);


    /**
     * 后台查询全部省份数据(过滤掉状态为1 即删除) 的数据
     * @return 符合条件的结果集
     */
    List<ProvincesVO> selectAll();


    /**
     * 后台根据分页条件实体，查询符合条件的结果集
     * @param pageRequest 分页条件实体对象
     * @return 分页结果实体
     */
    PageResult<ProvincesVO> selectPage(PageRequest<ProvincesDTO> pageRequest);


    /**
     * 根据省份主键id 改变省份状态
     * @param status 状态 0正常 2禁用
     * @param id 省份 主键 id
     * @return 更新后，数据库受影响的行数 > 0 ? true : false
     */
    boolean changeStatus(Integer status, Long id);


}
