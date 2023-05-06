package com.past.service.impl;

import com.google.common.base.Preconditions;
import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.dao.ProvincesDAO;
import com.past.domain.dto.ProvincesDTO;
import com.past.domain.entity.Provinces;
import com.past.domain.entity.Users;
import com.past.domain.vo.ProvincesVO;
import com.past.exception.ParamException;
import com.past.service.ProvincesService;
import com.past.util.BeanValidator;
import com.past.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 省份模块 业务层接口的实现类
 */
@Service
@Transactional
@Slf4j
public class ProvincesServiceImpl implements ProvincesService {

    @Autowired
    private ProvincesDAO provincesDAO;

    /**
     * 注意：以注解方式，这里只能使用@Resource(name = "myRedisTemplate")注入
     * 因为我在自定义RedisConfig类中定义了一个方法 将未指定泛型的RedisTemplate添加到bean中，bean的名称即myRedisTemplate
     * 而我在此处注入的时候，添加了泛型，所以不能单独使用@Autowired(按类型注入)或@Autowired、@Qualifier(value = "myRedisTemplate")组合使用
     */
    @Resource(name = "myRedisTemplate")
    private RedisTemplate<String, Provinces> redisTemplate;


    /**
     * 根据主键id 查询省份
     * @param id 省份 主键id
     * @return 一个ProvincesVO对象
     */
    @Override
    public ProvincesVO selectByPrimaryKey(Long id) {

        Provinces province = provincesDAO.selectByPrimaryKey(id);

        if (province == null || province.getStatus() == 1) {
            throw new NullPointerException("省份不存在！");
        }

        ProvincesVO vo = new ProvincesVO();
        BeanUtils.copyProperties(province, vo);

        return vo;
    }


    /**
     * 根据省名查询省份
     * @param name 省名
     * @return 一个ProvincesVO对象
     */
    @Override
    public ProvincesVO selectByName(String name) {

        Provinces province = provincesDAO.selectByName(name);

        if (province == null || province.getStatus() == 1) {
            throw new NullPointerException("省份不存在！");
        }

        ProvincesVO vo = new ProvincesVO();
        BeanUtils.copyProperties(province, vo);

        return vo;
    }


    /**
     * 判断该省名是否存在，存在返回true 抛出异常；不存在返回false，继续执行业务
     * @param name 省名
     * @param id 省份 主键id
     * @return 存在返回true 抛出异常；不存在返回false，继续执行业务
     */
    @Override
    public boolean checkNameExist(String name, Long id) {

        return provincesDAO.countByName(name, id) > 0;
    }


    /**
     * 后台新增省份
     * @param provincesDTO 待新增的省份DTO实体
     * @return 新增后，这条数据的主键id > 0 ? true : false
     */
    @Override
    public boolean insert(ProvincesDTO provincesDTO) {

        // 校验对象参数 是否满足注解约束  不满足约束，抛出ParamException
        BeanValidator.check(provincesDTO);

        provincesDTO.setName(provincesDTO.getName().trim());
        if (checkNameExist(provincesDTO.getName(), provincesDTO.getId())) {
            throw new ParamException("省名已存在，请更换省名！");
        }

        Provinces provinces = new Provinces();
        BeanUtils.copyProperties(provincesDTO, provinces);
        provinces.setCreateTime(new Date());
        provinces.setUpdateTime(new Date());

        // 获取主体对象
        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        provinces.setOperator(login.getUsername());
        provinces.setOperatorIp(IpUtil.getLocalIP());

        return provincesDAO.insertSelective(provinces) > 0;
    }


    /**
     * 后台删除省份 物理删除
     * @param id 待删除数据的主键id
     * @return 删除成功(数据库受影响的行数>0)返回true
     */
    @Override
    public boolean deletePhysical(Long id) {

        Provinces provinces = provincesDAO.selectByPrimaryKey(id);

        // 使用google.guava工具包校验待删除的省份是否为空 不存在省份，抛出异常。存在省份，继续执行代码
        Preconditions.checkNotNull(provinces,"待删除的省份不存在！");

        return provincesDAO.deleteByPrimaryKey(id) > 0;
    }


    /**
     * 后台删除省份 逻辑删除
     * @param id 待删除数据的主键id
     * @return 删除成功，即将此省份数据的状态改为1(删除)，返回true
     */
    @Override
    public boolean deleteLogic(Long id) {

        Provinces provinces = provincesDAO.selectByPrimaryKey(id);

        // 使用google.guava工具包校验待删除的省份是否为空 不存在省份，抛出异常。存在省份，继续执行代码
        Preconditions.checkNotNull(provinces,"待删除的省份不存在！");

        // 如果待删除省份的状态 不是删除状态，则执行删除
        if (provinces.getStatus() != 1) {
            provinces.setStatus(1);
            provinces.setUpdateTime(new Date());

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();

            provinces.setOperator(login.getUsername());
            provinces.setOperatorIp(IpUtil.getLocalIP());

            return provincesDAO.updateByPrimaryKeySelective(provinces) > 0;
        }

        return false;
    }


    /**
     * 后台更新省份
     * @param provincesDTO 更新后的省份DTO实体对象
     * @return 更新成功true 失败false
     */
    @Override
    public boolean update(ProvincesDTO provincesDTO) {

        // 校验对象参数 是否满足注解约束  不满足约束，抛出ParamException
        BeanValidator.check(provincesDTO);

        provincesDTO.setName(provincesDTO.getName().trim());
        if (checkNameExist(provincesDTO.getName(), provincesDTO.getId())) {
            throw new ParamException("省名已存在，请更换省名！");
        }

        Provinces before = provincesDAO.selectByPrimaryKey(provincesDTO.getId());

        // 使用google.guava工具包校验待更新的省份是否为空 不存在省份，抛出异常。存在省份，继续执行代码
        Preconditions.checkNotNull(before,"待更新的省份不存在！");

        // 当查询所得的待更新省份不为空 并且状态不是删除状态，才可更新
        if (before.getStatus() != 1) {
            Provinces after = new Provinces();
            BeanUtils.copyProperties(provincesDTO, after);
            after.setStatus(before.getStatus());
            after.setUpdateTime(new Date());

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();

            after.setOperator(login.getUsername());
            after.setOperatorIp(IpUtil.getLocalIP());

            return provincesDAO.updateByPrimaryKeySelective(after) > 0;
        }

        return false;
    }


    /**
     * 后台查询全部省份数据(过滤掉状态为1 即删除) 的数据
     * @return 符合条件的结果集
     */
    @Override
    public List<ProvincesVO> selectAll() {

        ListOperations<String, Provinces> listOperations = redisTemplate.opsForList();

        List<Provinces> all = listOperations.range("provinces", 0, -1);

        if (CollectionUtils.isEmpty(all)) {

            all = provincesDAO.selectAll();
            all.forEach(p -> listOperations.rightPush("provinces", p));
        }

        return Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(p -> p.getStatus() != 1)
                .map(p -> {
                    ProvincesVO vo = new ProvincesVO();
                    BeanUtils.copyProperties(p, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }


    /**
     * 后台根据分页条件实体，查询符合条件的结果集
     * @param pageRequest 分页条件实体对象
     * @return 分页结果实体
     */
    @Override
    public PageResult<ProvincesVO> selectPage(PageRequest<ProvincesDTO> pageRequest) {

        List<Provinces> all = provincesDAO.selectByNameContaining(pageRequest);

        List<ProvincesVO> voList = Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(p -> p.getStatus() != 1)
                .map(p -> {
                    ProvincesVO vo = new ProvincesVO();
                    BeanUtils.copyProperties(p, vo);
                    return vo;
                })
                .collect(Collectors.toList());

        return new PageResult<>(pageRequest, voList);
    }


    /**
     * 根据省份主键id 改变省份状态
     * @param status 状态 0正常 2禁用
     * @param id 省份 主键 id
     * @return 更新后，数据库受影响的行数 > 0 ? true : false
     */
    @Override
    public boolean changeStatus(Integer status, Long id) {

        Provinces provinces = provincesDAO.selectByPrimaryKey(id);

        if (ObjectUtils.isEmpty(provinces) || provinces.getStatus() == 1) {
            return false;
        }

        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        Date updateTime = new Date();
        String operator = login.getUsername();
        String operatorIp = IpUtil.getLocalIP();

        return provincesDAO.changeStatus(status, id, updateTime, operator, operatorIp) > 0;
    }


}
