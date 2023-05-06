package com.past.service.impl;

import com.google.common.base.Preconditions;
import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.dao.PermissionsDAO;
import com.past.domain.dto.PermissionsDTO;
import com.past.domain.entity.Permissions;
import com.past.domain.entity.Users;
import com.past.domain.vo.PermissionsVO;
import com.past.exception.ParamException;
import com.past.service.PermissionsService;
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
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 权限模块 业务层接口的实现类
 */
@Service
@Transactional
@Slf4j
public class PermissionsServiceImpl implements PermissionsService {

    @Autowired
    private PermissionsDAO permissionsDAO;

    /**
     * 注意：以注解方式，这里只能使用@Resource(name = "myRedisTemplate")注入
     * 因为我在自定义RedisConfig类中定义了一个方法 将未指定泛型的RedisTemplate添加到bean中，bean的名称即myRedisTemplate
     * 而我在此处注入的时候，添加了泛型，所以不能单独使用@Autowired(按类型注入)或@Autowired、@Qualifier(value = "myRedisTemplate")组合使用
     */
    @Resource(name = "myRedisTemplate")
    private RedisTemplate<String, Permissions> redisTemplate;


    /**
     * 根据主键id 查询权限数据
     * @param id 权限 主键id
     * @return 一个PermissionsVO对象
     */
    @Override
    public PermissionsVO selectByPrimaryKey(Long id) {

        Permissions p = permissionsDAO.selectByPrimaryKey(id);

        if (p == null || p.getStatus() == 1) {
            throw new NullPointerException("查找的权限不存在！");
        }

        PermissionsVO vo = new PermissionsVO();
        BeanUtils.copyProperties(p, vo);

        return vo;
    }


    /**
     * 判断该权限名是否存在，存在返回true 抛出异常；不存在返回false，继续执行业务
     * @param name 权限名称
     * @param id 权限 主键id
     * @return 存在返回true 抛出异常；不存在返回false，继续执行业务
     */
    @Override
    public boolean checkNameExist(String name, Long id) {

        return permissionsDAO.countByName(name, id) > 0;
    }


    /**
     * 判断该权限url是否存在，存在返回true 抛出异常；不存在返回false，继续执行业务
     * @param url 权限url
     * @param id 权限 主键id
     * @return 存在返回true 抛出异常；不存在返回false，继续执行业务
     */
    @Override
    public boolean checkUrlExist(String url, Long id) {

        return permissionsDAO.countByUrl(url, id) > 0;
    }


    /**
     * 根据存储权限主键id的集合，查询这些id代表的权限对象
     * @param ids 存储权限主键id的集合
     * @return 存储 这些id代表的权限对象 的集合
     */
    @Override
    public List<Permissions> selectByIds(List<Long> ids) {

        return permissionsDAO.selectByIds(ids);
    }


    /**
     * 后台新增权限
     * @param permissionsDTO 待新增的权限DTO实体
     * @return 新增后，这条数据的主键id > 0 ? true : false
     */
    @Override
    public boolean insert(PermissionsDTO permissionsDTO) {

        // 校验对象参数 是否满足注解约束  不满足约束，抛出ParamException
        BeanValidator.check(permissionsDTO);

        permissionsDTO.setName(permissionsDTO.getName().trim());
        permissionsDTO.setUrl(permissionsDTO.getUrl().trim());
        if (checkNameExist(permissionsDTO.getName(), permissionsDTO.getId())) {
            throw new ParamException("权限名已存在，请更换权限名！");
        }
        // 前台权限url非必填，不填时接收过来都为""  此时将其置为null。不影响后续有实际值时的判断
        if (StringUtils.isEmpty(permissionsDTO.getUrl())) {
            permissionsDTO.setUrl(null);
        }
        if (checkUrlExist(permissionsDTO.getUrl(), permissionsDTO.getId())) {
            throw new ParamException("权限url已存在，请更换权限url！");
        }

        Permissions permissions = new Permissions();
        BeanUtils.copyProperties(permissionsDTO, permissions);
        permissions.setCreateTime(new Date());
        permissions.setUpdateTime(new Date());

        // 获取主体对象
        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        permissions.setOperator(login.getUsername());
        permissions.setOperatorIp(IpUtil.getLocalIP());

        return permissionsDAO.insertSelective(permissions) > 0;
    }


    /**
     * 后台删除权限 物理删除
     * @param id 待删除数据的主键id
     * @return 删除成功(数据库受影响的行数>0)返回true
     */
    @Override
    public boolean deletePhysical(Long id) {

        Permissions p = permissionsDAO.selectByPrimaryKey(id);

        // 使用google.guava工具包校验待删除的权限是否为空 不存在权限，抛出异常。存在权限，继续执行代码
        Preconditions.checkNotNull(p,"待删除的权限不存在！");

        return permissionsDAO.deleteByPrimaryKey(id) > 0;
    }


    /**
     * 后台删除权限 逻辑删除
     * @param id 待删除数据的主键id
     * @return 删除成功，即将此权限数据的状态改为1(删除)，返回true
     */
    @Override
    public boolean deleteLogic(Long id) {

        Permissions p = permissionsDAO.selectByPrimaryKey(id);

        // 使用google.guava工具包校验待删除的权限是否为空 不存在权限，抛出异常。存在权限，继续执行代码
        Preconditions.checkNotNull(p,"待删除的权限不存在！");

        // 如果待删除权限的状态 不是删除状态，则执行删除
        if (p.getStatus() != 1) {
            p.setStatus(1);
            p.setUpdateTime(new Date());

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();

            p.setOperator(login.getUsername());
            p.setOperatorIp(IpUtil.getLocalIP());

            return permissionsDAO.updateByPrimaryKeySelective(p) > 0;
        }

        return false;
    }


    /**
     * 后台更新权限
     * @param permissionsDTO 更新后的权限DTO实体对象
     * @return 更新成功true 失败false
     */
    @Override
    public boolean update(PermissionsDTO permissionsDTO) {

        BeanValidator.check(permissionsDTO);

        permissionsDTO.setName(permissionsDTO.getName().trim());
        permissionsDTO.setUrl(permissionsDTO.getUrl().trim());
        if (checkNameExist(permissionsDTO.getName(), permissionsDTO.getId())) {
            throw new ParamException("权限名已存在，请更换权限名！");
        }
        // 前台权限url非必填，不填时都为""  此时将其置为null。不影响后续有实际值时的判断
        if (StringUtils.isEmpty(permissionsDTO.getUrl())) {
            permissionsDTO.setUrl(null);
        }
        if (checkUrlExist(permissionsDTO.getUrl(), permissionsDTO.getId())) {
            throw new ParamException("权限url已存在，请更换权限url！");
        }

        Permissions before = permissionsDAO.selectByPrimaryKey(permissionsDTO.getId());

        Preconditions.checkNotNull(before, "待更新的权限不存在！");

        // 当查询所得的待更新权限不为空 并且状态不是删除状态，才可更新
        if (before.getStatus() != 1) {
            Permissions after = new Permissions();
            BeanUtils.copyProperties(permissionsDTO, after);
            after.setStatus(before.getStatus());
            after.setUpdateTime(new Date());

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();

            after.setOperator(login.getUsername());
            after.setOperatorIp(IpUtil.getLocalIP());

            return permissionsDAO.updateByPrimaryKeySelective(after) > 0;
        }

        return false;
    }


    /**
     * 后台查询全部权限数据(过滤掉状态为1 即删除) 的数据
     * @return 符合条件的结果集
     */
    @Override
    public List<PermissionsVO> selectAll() {

        ListOperations<String, Permissions> listOperations = redisTemplate.opsForList();

        List<Permissions> all = listOperations.range("permissions", 0, -1);

        if (CollectionUtils.isEmpty(all)) {

            all = permissionsDAO.selectAll();
            all.forEach(p -> listOperations.rightPush("permissions", p));
        }

        return Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(p -> p.getStatus() != 1)
                .map(p -> {
                    PermissionsVO vo = new PermissionsVO();
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
    public PageResult<PermissionsVO> selectPage(PageRequest<PermissionsDTO> pageRequest) {

        List<Permissions> all = permissionsDAO.selectByNameContaining(pageRequest);

        List<PermissionsVO> voList = Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(p -> p.getStatus() != 1)
                .map(p -> {
                    PermissionsVO vo = new PermissionsVO();
                    BeanUtils.copyProperties(p, vo);
                    return vo;
                })
                .collect(Collectors.toList());

        return new PageResult<>(pageRequest, voList);
    }


    /**
     * 根据权限主键id 改变权限状态
     * @param status 状态 0正常 2禁用
     * @param id 权限 主键 id
     * @return 更新后，数据库受影响的行数 > 0 ? true : false
     */
    @Override
    public boolean changeStatus(Integer status, Long id) {

        Permissions permissions = permissionsDAO.selectByPrimaryKey(id);

        if (ObjectUtils.isEmpty(permissions) || permissions.getStatus() == 1) {
            return false;
        }

        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        Date updateTime = new Date();
        String operator = login.getUsername();
        String operatorIp = IpUtil.getLocalIP();

        return permissionsDAO.changeStatus(status, id, updateTime, operator, operatorIp) > 0;
    }


}
