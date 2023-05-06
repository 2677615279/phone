package com.past.service;

import com.past.domain.dto.ReceivingAddressDTO;
import com.past.domain.vo.ReceivingAddressVO;

import java.util.List;

/**
 * 收货地址模块 业务层接口
 */
public interface ReceivingAddressService {


    /**
     * 根据主键id 查询收货地址
     * @param id 收货地址 主键id
     * @return 一个ReceivingAddressVO对象
     */
    ReceivingAddressVO selectByPrimaryKey(Long id);


    /**
     * 根据所属的收货用户的主键id 查询该用户下的所有收货地址，并存入一个集合
     * @return 存储该用户下的所有收货地址的集合
     */
    List<ReceivingAddressVO> selectByUserId();


    /**
     * 新增收货地址
     * @param receivingAddressDTO 待新增的收货地址DTO实体
     * @return 新增后，这条数据的主键id > 0 ? true : false
     */
    boolean insert(ReceivingAddressDTO receivingAddressDTO);


    /**
     * 删除收货地址 物理删除
     * @param id 待删除数据的主键id
     * @return 删除成功(数据库受影响的行数>0)返回true
     */
    boolean deletePhysical(Long id);


    /**
     * 删除收货地址 逻辑删除
     * @param id 待删除数据的主键id
     * @return 删除成功，即将此收货地址数据的状态改为1(删除)，返回true
     */
    boolean deleteLogic(Long id);


    /**
     * 前台用户更新自己的收货地址
     * @param receivingAddressDTO 更新后的收货地址DTO实体对象
     * @return 更新成功true 失败false
     */
    boolean updateSelf(ReceivingAddressDTO receivingAddressDTO);


}
