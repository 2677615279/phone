package com.past.service;

import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.domain.dto.BannersDTO;
import com.past.domain.vo.BannersVO;

import java.util.List;

/**
 * banner图模块 业务层接口
 */
public interface BannersService {


    /**
     * 根据主键id 查询banner图数据
     * @param id banner图 主键id
     * @return 一个BannersVO对象
     */
    BannersVO selectByPrimaryKey(Long id);


    /**
     * 判断该banner图名是否存在，存在返回true 抛出异常；不存在返回false，继续执行业务
     * @param name banner图名称
     * @param id banner图 主键id
     * @return 存在返回true 抛出异常；不存在返回false，继续执行业务
     */
    boolean checkNameExist(String name, Long id);


    /**
     * 判断该banner图所指的商品详情url是否存在，存在返回true 抛出异常；不存在返回false，继续执行业务
     * @param url banner图所指的商品详情url
     * @param id banner图 主键id
     * @return 存在返回true 抛出异常；不存在返回false，继续执行业务
     */
    boolean checkUrlExist(String url, Long id);


    /**
     * 后台新增banner图
     * @param bannersDTO 待新增的banner图DTO实体
     * @return 新增后，这条数据的主键id > 0 ? true : false
     */
    boolean insert(BannersDTO bannersDTO);


    /**
     * 后台删除banner图 物理删除
     * @param id 待删除数据的主键id
     * @return 删除成功(数据库受影响的行数>0)返回true
     */
    boolean deletePhysical(Long id);


    /**
     * 后台删除banner图 逻辑删除
     * @param id 待删除数据的主键id
     * @return 删除成功，即将此banner图数据的状态改为1(删除)，返回true
     */
    boolean deleteLogic(Long id);


    /**
     * 后台更新banner图
     * @param bannersDTO 更新后的banner图DTO实体对象
     * @return 更新成功true 失败false
     */
    boolean update(BannersDTO bannersDTO);


    /**
     * 后台查询全部banner图数据(过滤掉状态为1 即删除) 的数据
     * @return 符合条件的结果集
     */
    List<BannersVO> selectAll();


    /**
     * 后台根据分页条件实体，查询符合条件的结果集
     * @param pageRequest 分页条件实体对象
     * @return 分页结果实体
     */
    PageResult<BannersVO> selectPage(PageRequest<BannersDTO> pageRequest);


    /**
     * 根据banner图主键id 改变banner图状态
     * @param status 状态 0正常 2禁用
     * @param id banner图 主键 id
     * @return 更新后，数据库受影响的行数 > 0 ? true : false
     */
    boolean changeStatus(Integer status, Long id);


}
