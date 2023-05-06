package com.past.dao;

import com.past.beans.PageRequest;
import com.past.domain.dto.UsersDTO;
import com.past.domain.entity.Users;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 操作用户实体的数据库持久层接口
 */
@Mapper
@Repository
public interface UsersDAO {


    /**
     * 根据主键id 删除一条用户数据
     * @param id 用户 主键id
     * @return 数据库中受影响的行数
     */
    int deleteByPrimaryKey(Long id);


    /**
     * 新增一条用户数据
     * @param record 待新增的用户对象
     * @return 数据库中受影响的行数
     */
    int insert(Users record);


    /**
     * 新增一条用户数据，判断每个属性是否为Null，为不是Null的属性做新增
     * @param record 待新增的用户对象
     * @return 数据库中受影响的行数
     */
    int insertSelective(Users record);


    /**
     * 根据主键id 查询一条用户数据
     * @param id 用户 主键id
     * @return 一个Users对象
     */
    Users selectByPrimaryKey(Long id);


    /**
     * 更新一条用户数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param record 更新后的用户对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKeySelective(Users record);


    /**
     * 更新一条用户数据
     * @param record 更新后的用户对象
     * @return 数据库中受影响的行数
     */
    int updateByPrimaryKey(Users record);


    /**
     * 批量更新多条用户数据
     * @param list 存储多个更新后的用户对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatch(List<Users> list);


    /**
     * 批量更新多条用户数据，判断每个属性是否为Null，为不是Null的属性做更新
     * @param list 存储多个更新后的用户对象 的集合
     * @return 数据库中受影响的行数
     */
    int updateBatchSelective(List<Users> list);


    /**
     * 批量新增多条用户数据
     * @param list 存储多个待新增的用户对象 的集合
     * @return 数据库中受影响的行数
     */
    int batchInsert(@Param("list") List<Users> list);


    /**
     * 根据账号 查询一条用户数据
     * @param principal 用户主身份信息 可以是username或phone或email
     * @return 一个Users对象
     */
    Users selectByPrincipal(@Param("principal") String principal);


    /**
     * 根据集合中存储的所有主键id，查询这些主键id分配的所有用户数据，并将其存储到一个集合
     * @param ids 存储所有用户主键id的集合
     * @return 存储 这些主键id代表的所有用户数据 的集合
     */
    List<Users> selectByIds(@Param("ids") List<Long> ids);


    /**
     * 根据id和用户名查询的记录数
     * @param username 用户名
     * @param id 用户主键id
     * @return 查询记录数
     */
    int countByUsername(@Param("username") String username, @Param("id") Long id);


    /**
     * 根据id和邮箱查询的记录数
     * @param email 邮箱
     * @param id 用户主键id
     * @return 查询记录数
     */
    int countByEmail(@Param("email") String email, @Param("id") Long id);


    /**
     * 根据id和手机号查询的记录数
     * @param phone 手机号
     * @param id 用户主键id
     * @return 查询记录数
     */
    int countByPhone(@Param("phone") String phone, @Param("id") Long id);


    /**
     * 查询所有用户数据
     * @return 存储所有用户对象的集合
     */
    List<Users> selectAll();


    /**
     * 根据主身份信息 模糊查询
     * @param pageRequest 封装分页条件的实体
     * @return 符合条件的结果集
     */
    List<Users> selectByPrincipalContaining(@Param("pageRequest") PageRequest<UsersDTO> pageRequest);


    /**
     * 根据用户主键id 改变用户状态
     * @param status 状态 0正常 2禁用
     * @param id 用户 主键id
     * @param updateTime 最后一次更新时间
     * @param operator 最后一次操作者名称
     * @param operatorIp 最后一次操作者的ip
     * @return 更新后，数据库受影响的行数 > 0 ? true : false
     */
    int changeStatus(@Param("status") Integer status, @Param("id") Long id,
                     @Param("updateTime") Date updateTime, @Param("operator") String operator, @Param("operatorIp") String operatorIp);


    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 一个Users对象
     */
    Users selectByUsername(@Param("username") String username);


}