package com.past.service;

import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.domain.dto.MemoryDTO;
import com.past.domain.vo.MemoryVO;

import java.util.List;

/**
 * 内存模块 业务层接口
 */
public interface MemoryService {
    

    /**
     * 根据主键id 查询内存数据
     * @param id 内存 主键id
     * @return 一个MemoryVO对象
     */
    MemoryVO selectByPrimaryKey(Long id);


    /**
     * 判断该内存名是否存在，存在返回true 抛出异常；不存在返回false，继续执行业务
     * @param name 内存名称
     * @param id 内存 主键id
     * @return 存在返回true 抛出异常；不存在返回false，继续执行业务
     */
    boolean checkNameExist(String name, Long id);


    /**
     * 后台新增内存
     * @param memoryDTO 待新增的内存DTO实体
     * @return 新增后，这条数据的主键id > 0 ? true : false
     */
    boolean insert(MemoryDTO memoryDTO);


    /**
     * 后台删除内存 物理删除
     * @param id 待删除数据的主键id
     * @return 删除成功(数据库受影响的行数>0)返回true
     */
    boolean deletePhysical(Long id);


    /**
     * 后台删除内存 逻辑删除
     * @param id 待删除数据的主键id
     * @return 删除成功，即将此内存数据的状态改为1(删除)，返回true
     */
    boolean deleteLogic(Long id);


    /**
     * 后台更新内存
     * @param memoryDTO 更新后的内存DTO实体对象
     * @return 更新成功true 失败false
     */
    boolean update(MemoryDTO memoryDTO);


    /**
     * 后台查询全部内存数据(过滤掉状态为1 即删除) 的数据
     * @return 符合条件的结果集
     */
    List<MemoryVO> selectAll();


    /**
     * 后台根据分页条件实体，查询符合条件的结果集
     * @param pageRequest 分页条件实体对象
     * @return 分页结果实体
     */
    PageResult<MemoryVO> selectPage(PageRequest<MemoryDTO> pageRequest);


    /**
     * 根据内存主键id 改变内存状态
     * @param status 状态 0正常 2禁用
     * @param id 内存 主键 id
     * @return 更新后，数据库受影响的行数 > 0 ? true : false
     */
    boolean changeStatus(Integer status, Long id);


}
