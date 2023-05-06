package com.past.dao;

import com.past.domain.entity.ReceivingAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 操作收货地址实体的数据库持久层接口
 */
@Mapper
@Repository
public interface ReceivingAddressDAO {

    
    /**
     * 根据主键id 删除一条收货地址数据
     * @param id 收货地址 主键id
     * @return 数据库中受影响的行数
     */
    int deleteByPrimaryKey(Long id);

    
    /**
     * 新增一条收货地址数据
     * @param record 待新增的收货地址对象
     * @return 数据库中受影响的行数
     */
    int insert(ReceivingAddress record);
    

    /**
     * 新增一条收货地址数据，判断每个属性是否为Null，为不是Null的属性做新增
     * @param record 待新增的收货地址对象
     * @return 数据库中受影响的行数
     */
    int insertSelective(ReceivingAddress record);

    
    /**
     * 根据主键id 查询一条收货地址数据
     * @param id 收货地址 主键id
     * @return 一个ReceivingAddress对象
     */
    ReceivingAddress selectByPrimaryKey(Long id);
    

    /**
     * 更新一条收货地址数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param record 更新后的收货地址对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKeySelective(ReceivingAddress record);
    

    /**
     * 更新一条收货地址数据
     * @param record 更新后的收货地址对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKey(ReceivingAddress record);
    
    
    /**
     * 批量更新多条收货地址数据
     * @param list 存储多个更新后的收货地址对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatch(List<ReceivingAddress> list);
    
    
    /**
     * 批量更新多条收货地址数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param list 存储多个更新后的收货地址对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatchSelective(List<ReceivingAddress> list);
    
    
    /**
     * 批量新增多条收货地址数据
     * @param list 存储多个待新增的收货地址对象 的集合
     * @return 数据库中受影响的行数
     */
    int batchInsert(@Param("list") List<ReceivingAddress> list);


    /**
     * 根据所属的收货用户的主键id 查询该用户下的所有收货地址，并存入一个集合
     * @param userId 所属的收货用户的主键id
     * @return 存储该用户下的所有收货地址的集合
     */
    List<ReceivingAddress> selectByUserId(@Param("userId") Long userId);


}