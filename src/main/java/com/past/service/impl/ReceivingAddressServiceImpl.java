package com.past.service.impl;

import com.google.common.base.Preconditions;
import com.past.dao.AreasDAO;
import com.past.dao.CitiesDAO;
import com.past.dao.ProvincesDAO;
import com.past.dao.ReceivingAddressDAO;
import com.past.domain.dto.ReceivingAddressDTO;
import com.past.domain.entity.*;
import com.past.domain.vo.ReceivingAddressVO;
import com.past.service.ReceivingAddressService;
import com.past.util.BeanValidator;
import com.past.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 收货地址模块 业务层接口的实现类
 */
@Service
@Transactional
@Slf4j
public class ReceivingAddressServiceImpl implements ReceivingAddressService {

    @Autowired
    private ReceivingAddressDAO receivingAddressDAO;

    @Autowired
    private ProvincesDAO provincesDAO;

    @Autowired
    private CitiesDAO citiesDAO;

    @Autowired
    private AreasDAO areasDAO;


    /**
     * 根据主键id 查询收货地址
     * @param id 收货地址 主键id
     * @return 一个ReceivingAddressVO对象
     */
    @Override
    public ReceivingAddressVO selectByPrimaryKey(Long id) {

        ReceivingAddress address = receivingAddressDAO.selectByPrimaryKey(id);

        if (address == null || address.getStatus() == 1) {
            throw new NullPointerException("收货地址不存在！");
        }

        ReceivingAddressVO vo = new ReceivingAddressVO();
        BeanUtils.copyProperties(address, vo);

        return vo;
    }


    /**
     * 根据所属的收货用户的主键id 查询该用户下的所有收货地址，并存入一个集合
     * @return 存储该用户下的所有收货地址的集合
     */
    @Override
    public List<ReceivingAddressVO> selectByUserId() {

        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        List<ReceivingAddress> all = receivingAddressDAO.selectByUserId(login.getId());

        return Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(r -> r.getStatus() != 1)
                .map(r -> {
                    ReceivingAddressVO vo = new ReceivingAddressVO();
                    BeanUtils.copyProperties(r, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }


    /**
     * 新增收货地址
     * @param receivingAddressDTO 待新增的收货地址DTO实体
     * @return 新增后，这条数据的主键id > 0 ? true : false
     */
    @Override
    public boolean insert(ReceivingAddressDTO receivingAddressDTO) {

        // 校验对象参数 是否满足注解约束  不满足约束，抛出ParamException
        BeanValidator.check(receivingAddressDTO);

        // 因为前端表单控件传过来的值 是id  所以要根据id查出来 再取name属性 重新赋值
        Provinces province = provincesDAO.selectByPrimaryKey(Long.valueOf(receivingAddressDTO.getProvince()));
        Cities city = citiesDAO.selectByPrimaryKey(Long.valueOf(receivingAddressDTO.getCity()));
        Areas area = areasDAO.selectByPrimaryKey(Long.valueOf(receivingAddressDTO.getArea()));

        if (province.getStatus() == 1 || city.getStatus() == 1 || area.getStatus() == 1) {
            return false;
        }

        receivingAddressDTO.setProvince(province.getName());
        receivingAddressDTO.setCity(city.getName());
        receivingAddressDTO.setArea(area.getName());

        ReceivingAddress address = new ReceivingAddress();
        BeanUtils.copyProperties(receivingAddressDTO, address);
        address.setCreateTime(new Date());
        address.setUpdateTime(new Date());

        // 获取主体对象
        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        address.setOperator(login.getUsername());
        address.setOperatorIp(IpUtil.getLocalIP());
        address.setUserId(login.getId());

        // 查询该用户下的所有收货地址，如果出现重复添加(省市区详细地址收货人昵称手机都一致)  返回false
        List<ReceivingAddress> addressList = receivingAddressDAO.selectByUserId(address.getUserId());
        Set<ReceivingAddress> addressSet = new HashSet<>(addressList);
        for (ReceivingAddress ra : addressSet) {
            if (ra.getProvince().equals(address.getProvince()) && ra.getCity().equals(address.getCity())
                    && ra.getArea().equals(address.getArea()) && ra.getDetail().equals(address.getDetail())
                    && ra.getNickName().equals(address.getNickName()) && ra.getPhone().equals(address.getPhone())) {
                log.error("该用户重复添加收货地址，添加失败，请检查核心参数！");
                return false;
            }
        }

        return receivingAddressDAO.insertSelective(address) > 0;
    }


    /**
     * 删除收货地址 物理删除
     * @param id 待删除数据的主键id
     * @return 删除成功(数据库受影响的行数>0)返回true
     */
    @Override
    public boolean deletePhysical(Long id) {

        ReceivingAddress address = receivingAddressDAO.selectByPrimaryKey(id);

        // 使用google.guava工具包校验待删除的收货地址是否为空 不存在收货地址，抛出异常。存在收货地址，继续执行代码
        Preconditions.checkNotNull(address,"待删除的收货地址不存在！");

        return receivingAddressDAO.deleteByPrimaryKey(id) > 0;
    }


    /**
     * 删除收货地址 逻辑删除
     * @param id 待删除数据的主键id
     * @return 删除成功，即将此收货地址数据的状态改为1(删除)，返回true
     */
    @Override
    public boolean deleteLogic(Long id) {

        ReceivingAddress address = receivingAddressDAO.selectByPrimaryKey(id);

        // 使用google.guava工具包校验待删除的收货地址是否为空 不存在收货地址，抛出异常。存在收货地址，继续执行代码
        Preconditions.checkNotNull(address,"待删除的收货地址不存在！");

        // 如果待删除收货地址的状态 不是删除状态，则执行删除
        if (address.getStatus() != 1) {
            address.setStatus(1);
            address.setUpdateTime(new Date());

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();

            address.setOperator(login.getUsername());
            address.setOperatorIp(IpUtil.getLocalIP());

            return receivingAddressDAO.updateByPrimaryKeySelective(address) > 0;
        }

        return false;
    }


    /**
     * 前台用户更新自己的收货地址
     * @param receivingAddressDTO 更新后的收货地址DTO实体对象
     * @return 更新成功true 失败false
     */
    @Override
    public boolean updateSelf(ReceivingAddressDTO receivingAddressDTO) {

        // 校验对象参数 是否满足注解约束  不满足约束，抛出ParamException
        BeanValidator.check(receivingAddressDTO);

        // 因为前端表单控件传过来的值 是id  所以要根据id查出来 再取name属性 重新赋值
        Provinces province = provincesDAO.selectByPrimaryKey(Long.valueOf(receivingAddressDTO.getProvince()));
        Cities city = citiesDAO.selectByPrimaryKey(Long.valueOf(receivingAddressDTO.getCity()));
        Areas area = areasDAO.selectByPrimaryKey(Long.valueOf(receivingAddressDTO.getArea()));

        receivingAddressDTO.setProvince(province.getName());
        receivingAddressDTO.setCity(city.getName());
        receivingAddressDTO.setArea(area.getName());

        ReceivingAddress before = receivingAddressDAO.selectByPrimaryKey(receivingAddressDTO.getId());

        // 使用google.guava工具包校验待更新的收货地址是否为空 不存在收货地址，抛出异常。存在收货地址，继续执行代码
        Preconditions.checkNotNull(before,"待更新的收货地址不存在！");

        // 当查询所得的待更新收货地址不为空 并且状态不是删除状态，才可更新
        if (before.getStatus() != 1) {
            ReceivingAddress after = new ReceivingAddress();
            BeanUtils.copyProperties(receivingAddressDTO, after);
            after.setUpdateTime(new Date());

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();

            after.setOperator(login.getUsername());
            after.setOperatorIp(IpUtil.getLocalIP());

            // 查询该用户下的所有收货地址，过滤掉当前正在修改的此数据
            List<ReceivingAddress> addressList = receivingAddressDAO.selectByUserId(before.getUserId());
            Set<ReceivingAddress> addressSet = new HashSet<>(addressList)
                    .stream().filter(ra -> !ra.getId().equals(after.getId())).collect(Collectors.toSet());

            for (ReceivingAddress ra : addressSet) {
                if (ra.getProvince().equals(after.getProvince()) && ra.getCity().equals(after.getCity())
                        && ra.getArea().equals(after.getArea()) && ra.getDetail().equals(after.getDetail())
                        && ra.getNickName().equals(after.getNickName()) && ra.getPhone().equals(after.getPhone())) {
                    log.error("该用户已有修改后的收货地址，修改失败，请检查核心参数！");
                    return false;
                }
            }

            return receivingAddressDAO.updateByPrimaryKeySelective(after) > 0;
        }

        return false;
    }


}