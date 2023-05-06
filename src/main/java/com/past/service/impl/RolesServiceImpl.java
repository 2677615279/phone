package com.past.service.impl;

import com.google.common.base.Preconditions;
import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.dao.RolesDAO;
import com.past.domain.dto.RolesDTO;
import com.past.domain.entity.Roles;
import com.past.domain.entity.Users;
import com.past.domain.vo.RolesVO;
import com.past.exception.ParamException;
import com.past.service.RolesService;
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
 * 角色模块 业务层接口的实现类
 */
@Service
@Transactional
@Slf4j
public class RolesServiceImpl implements RolesService {

    @Autowired
    private RolesDAO rolesDAO;

    /**
     * 注意：以注解方式，这里只能使用@Resource(name = "myRedisTemplate")注入
     * 因为我在自定义RedisConfig类中定义了一个方法 将未指定泛型的RedisTemplate添加到bean中，bean的名称即myRedisTemplate
     * 而我在此处注入的时候，添加了泛型，所以不能单独使用@Autowired(按类型注入)或@Autowired、@Qualifier(value = "myRedisTemplate")组合使用
     */
    @Resource(name = "myRedisTemplate")
    private RedisTemplate<String, Roles> redisTemplate;


    /**
     * 根据主键id 查询角色数据
     * @param id 角色 主键id
     * @return 一个RolesVO对象
     */
    @Override
    public RolesVO selectByPrimaryKey(Long id) {

        Roles r = rolesDAO.selectByPrimaryKey(id);

        if (r == null || r.getStatus() == 1) {
            throw new NullPointerException("角色不存在！");
        }

        RolesVO vo = new RolesVO();
        BeanUtils.copyProperties(r, vo);

        return vo;
    }


    /**
     * 判断该角色名是否存在，存在返回true 抛出异常；不存在返回false，继续执行业务
     * @param name 角色名称
     * @param id 角色 主键id
     * @return 存在返回true 抛出异常；不存在返回false，继续执行业务
     */
    @Override
    public boolean checkNameExist(String name, Long id) {

        return rolesDAO.countByName(name, id) > 0;
    }


    /**
     * 根据存储角色主键id的集合，查询这些id代表的角色对象
     * @param ids 存储角色主键id的集合
     * @return 存储 这些id代表的角色对象 的集合
     */
    @Override
    public List<Roles> selectByIds(List<Long> ids) {

        return rolesDAO.selectByIds(ids);
    }


    /**
     * 新增角色
     * @param rolesDTO 待新增的角色DTO实体
     * @return 新增后，这条数据的主键id > 0 ? true : false
     */
    @Override
    public boolean insert(RolesDTO rolesDTO) {

        // 校验对象参数 是否满足注解约束  不满足约束，抛出ParamException
        BeanValidator.check(rolesDTO);

        rolesDTO.setName(rolesDTO.getName().trim());
        if (checkNameExist(rolesDTO.getName(), rolesDTO.getId())) {
            throw new ParamException("角色名已存在，请更换角色名！");
        }

        Roles roles = new Roles();
        BeanUtils.copyProperties(rolesDTO, roles);
        roles.setCreateTime(new Date());
        roles.setUpdateTime(new Date());

        // 获取主体对象
        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        roles.setOperator(login.getUsername());
        roles.setOperatorIp(IpUtil.getLocalIP());

        return rolesDAO.insertSelective(roles) > 0;
    }


    /**
     * 删除角色 物理删除
     * @param id 待删除数据的主键id
     * @return 删除成功(数据库受影响的行数>0)返回true
     */
    @Override
    public boolean deletePhysical(Long id) {

        Roles r = rolesDAO.selectByPrimaryKey(id);

        // 使用google.guava工具包校验待删除的角色是否为空 不存在角色，抛出异常。存在角色，继续执行代码
        Preconditions.checkNotNull(r,"待删除的角色不存在！");

        return rolesDAO.deleteByPrimaryKey(id) > 0;
    }


    /**
     * 删除角色 逻辑删除
     * @param id 待删除数据的主键id
     * @return 删除成功，即将此角色数据的状态改为1(删除)，返回true
     */
    @Override
    public boolean deleteLogic(Long id) {

        Roles r = rolesDAO.selectByPrimaryKey(id);

        // 使用google.guava工具包校验待删除的角色是否为空 不存在角色，抛出异常。存在角色，继续执行代码
        Preconditions.checkNotNull(r,"待删除的角色不存在！");

        // 如果待删除角色的状态 不是删除状态，则执行删除
        if (r.getStatus() != 1) {
            r.setStatus(1);
            r.setUpdateTime(new Date());

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();

            r.setOperator(login.getUsername());
            r.setOperatorIp(IpUtil.getLocalIP());

            return rolesDAO.updateByPrimaryKeySelective(r) > 0;
        }

        return false;
    }


    /**
     * 后台更新角色
     * @param rolesDTO 更新后的角色DTO实体对象
     * @return 更新成功true 失败false
     */
    @Override
    public boolean update(RolesDTO rolesDTO) {

        // 校验对象参数 是否满足注解约束  不满足约束，抛出ParamException
        BeanValidator.check(rolesDTO);

        rolesDTO.setName(rolesDTO.getName().trim());
        if (checkNameExist(rolesDTO.getName(), rolesDTO.getId())) {
            throw new ParamException("角色名已存在，请更换角色名！");
        }

        Roles before = rolesDAO.selectByPrimaryKey(rolesDTO.getId());

        // 使用google.guava工具包校验待删除的用户是否为空 不存在角色，抛出异常。存在角色，继续执行代码
        Preconditions.checkNotNull(before,"待更新的角色不存在！");

        // 当查询所得的待更新角色不为空 并且状态不是删除状态，才可更新
        if (before.getStatus() != 1) {
            Roles after = new Roles();
            BeanUtils.copyProperties(rolesDTO, after);
            after.setStatus(before.getStatus());
            after.setUpdateTime(new Date());

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();

            after.setOperator(login.getUsername());
            after.setOperatorIp(IpUtil.getLocalIP());

            return rolesDAO.updateByPrimaryKeySelective(after) > 0;
        }

        return false;
    }


    /**
     * 后台查询全部角色数据(过滤掉状态为1 即删除) 的数据
     * @return 符合条件的结果集
     */
    @Override
    public List<RolesVO> selectAll() {

        ListOperations<String, Roles> listOperations = redisTemplate.opsForList();

        List<Roles> all = listOperations.range("roles", 0, -1);

        if (CollectionUtils.isEmpty(all)) {

            all = rolesDAO.selectAll();
            all.forEach(r -> listOperations.rightPush("roles", r));
        }

        return Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(r -> r.getStatus() != 1)
                .map(r -> {
                    RolesVO vo = new RolesVO();
                    BeanUtils.copyProperties(r, vo);
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
    public PageResult<RolesVO> selectPage(PageRequest<RolesDTO> pageRequest) {

        List<Roles> all = rolesDAO.selectByNameContaining(pageRequest);

        List<RolesVO> voList = Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(r -> r.getStatus() != 1)
                .map(r -> {
                    RolesVO vo = new RolesVO();
                    BeanUtils.copyProperties(r, vo);
                    return vo;
                })
                .collect(Collectors.toList());

        return new PageResult<>(pageRequest, voList);
    }


    /**
     * 根据角色主键id 改变角色状态
     * @param status 状态 0正常 2禁用
     * @param id 角色 主键 id
     * @return 更新后，数据库受影响的行数 > 0 ? true : false
     */
    @Override
    public boolean changeStatus(Integer status, Long id) {

        Roles roles = rolesDAO.selectByPrimaryKey(id);

        if (ObjectUtils.isEmpty(roles) || roles.getStatus() == 1) {
            return false;
        }

        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        Date updateTime = new Date();
        String operator = login.getUsername();
        String operatorIp = IpUtil.getLocalIP();

        return rolesDAO.changeStatus(status, id, updateTime, operator, operatorIp) > 0;
    }


}
