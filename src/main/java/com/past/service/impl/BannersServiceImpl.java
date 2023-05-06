package com.past.service.impl;

import com.google.common.base.Preconditions;
import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.dao.BannersDAO;
import com.past.domain.dto.BannersDTO;
import com.past.domain.entity.Banners;
import com.past.domain.entity.Users;
import com.past.domain.vo.BannersVO;
import com.past.exception.ParamException;
import com.past.service.BannersService;
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
 * banner图模块 业务层接口的实现类
 */
@Service
@Transactional
@Slf4j
public class BannersServiceImpl implements BannersService {

    @Autowired
    private BannersDAO bannersDAO;

    /**
     * 注意：以注解方式，这里只能使用@Resource(name = "myRedisTemplate")注入
     * 因为我在自定义RedisConfig类中定义了一个方法 将未指定泛型的RedisTemplate添加到bean中，bean的名称即myRedisTemplate
     * 而我在此处注入的时候，添加了泛型，所以不能单独使用@Autowired(按类型注入)或@Autowired、@Qualifier(value = "myRedisTemplate")组合使用
     */
    @Resource(name = "myRedisTemplate")
    private RedisTemplate<String, Banners> redisTemplate;


    /**
     * 根据主键id 查询banner图数据
     * @param id banner图 主键id
     * @return 一个BannersVO对象
     */
    @Override
    public BannersVO selectByPrimaryKey(Long id) {

        Banners banners = bannersDAO.selectByPrimaryKey(id);

        if (banners == null || banners.getStatus() == 1) {
            throw new NullPointerException("查找的banner图不存在！");
        }

        BannersVO vo = new BannersVO();
        BeanUtils.copyProperties(banners, vo);

        return vo;
    }


    /**
     * 判断该banner图名是否存在，存在返回true 抛出异常；不存在返回false，继续执行业务
     * @param name banner图名称
     * @param id banner图 主键id
     * @return 存在返回true 抛出异常；不存在返回false，继续执行业务
     */
    @Override
    public boolean checkNameExist(String name, Long id) {

        return bannersDAO.countByName(name, id) > 0;
    }


    /**
     * 判断该banner图所指的商品详情url是否存在，存在返回true 抛出异常；不存在返回false，继续执行业务
     * @param url banner图所指的商品详情url
     * @param id banner图 主键id
     * @return 存在返回true 抛出异常；不存在返回false，继续执行业务
     */
    @Override
    public boolean checkUrlExist(String url, Long id) {

        return bannersDAO.countByUrl(url, id) > 0;
    }


    /**
     * 后台新增banner图
     * @param bannersDTO 待新增的banner图DTO实体
     * @return 新增后，这条数据的主键id > 0 ? true : false
     */
    @Override
    public boolean insert(BannersDTO bannersDTO) {

        // 校验对象参数 是否满足注解约束  不满足约束，抛出ParamException
        BeanValidator.check(bannersDTO);

        bannersDTO.setName(bannersDTO.getName().trim());
        bannersDTO.setUrl(bannersDTO.getUrl().trim());
        if (checkNameExist(bannersDTO.getName(), bannersDTO.getId())) {
            throw new ParamException("bannner图名称已存在，请更换banner图名称！");
        }
        if (checkUrlExist(bannersDTO.getUrl(), bannersDTO.getId())) {
            throw new ParamException("bannner图所指商品详情url已存在，请更换banner图url！");
        }

        Banners banners = new Banners();
        BeanUtils.copyProperties(bannersDTO, banners);
        banners.setCreateTime(new Date());
        banners.setUpdateTime(new Date());

        // 获取主体对象
        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        banners.setOperator(login.getUsername());
        banners.setOperatorIp(IpUtil.getLocalIP());

        return bannersDAO.insertSelective(banners) > 0;
    }


    /**
     * 后台删除banner图 物理删除
     * @param id 待删除数据的主键id
     * @return 删除成功(数据库受影响的行数>0)返回true
     */
    @Override
    public boolean deletePhysical(Long id) {

        Banners banners = bannersDAO.selectByPrimaryKey(id);

        // 使用google.guava工具包校验待删除的banner图是否为空 不存在banner图，抛出异常。存在banner图，继续执行代码
        Preconditions.checkNotNull(banners,"待删除的banner图不存在！");

        return bannersDAO.deleteByPrimaryKey(id) > 0;
    }


    /**
     * 后台删除banner图 逻辑删除
     * @param id 待删除数据的主键id
     * @return 删除成功，即将此banner图数据的状态改为1(删除)，返回true
     */
    @Override
    public boolean deleteLogic(Long id) {

        Banners banners = bannersDAO.selectByPrimaryKey(id);

        // 使用google.guava工具包校验待删除的banner图是否为空 不存在banner图，抛出异常。存在banner图，继续执行代码
        Preconditions.checkNotNull(banners,"待删除的banner图不存在！");

        // 如果待删除banner图的状态 不是删除状态，则执行删除
        if (banners.getStatus() != 1) {
            banners.setStatus(1);
            banners.setUpdateTime(new Date());

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();

            banners.setOperator(login.getUsername());
            banners.setOperatorIp(IpUtil.getLocalIP());

            return bannersDAO.updateByPrimaryKeySelective(banners) > 0;
        }

        return false;
    }


    /**
     * 后台更新banner图
     * @param bannersDTO 更新后的banner图DTO实体对象
     * @return 更新成功true 失败false
     */
    @Override
    public boolean update(BannersDTO bannersDTO) {

        // 校验对象参数 是否满足注解约束  不满足约束，抛出ParamException
        BeanValidator.check(bannersDTO);

        bannersDTO.setName(bannersDTO.getName().trim());
        bannersDTO.setUrl(bannersDTO.getUrl().trim());
        if (checkNameExist(bannersDTO.getName(), bannersDTO.getId())) {
            throw new ParamException("bannner图名称已存在，请更换banner图名称！");
        }
        if (checkUrlExist(bannersDTO.getUrl(), bannersDTO.getId())) {
            throw new ParamException("bannner图所指商品详情url已存在，请更换banner图url！");
        }

        Banners before = bannersDAO.selectByPrimaryKey(bannersDTO.getId());

        // 使用google.guava工具包校验待更新的banner图是否为空 不存在banner图，抛出异常。存在banner图，继续执行代码
        Preconditions.checkNotNull(before,"待更新的banner图不存在！");

        // 当查询所得的待更新banner图不为空 并且状态不是删除状态，才可更新
        if (before.getStatus() != 1) {
            Banners after = new Banners();
            BeanUtils.copyProperties(bannersDTO, after);
            after.setStatus(before.getStatus());
            after.setUpdateTime(new Date());

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();

            after.setOperator(login.getUsername());
            after.setOperatorIp(IpUtil.getLocalIP());

            return bannersDAO.updateByPrimaryKeySelective(after) > 0;
        }

        return false;
    }


    /**
     * 后台查询全部banner图数据(过滤掉状态为1 即删除) 的数据
     * @return 符合条件的结果集
     */
    @Override
    public List<BannersVO> selectAll() {

        ListOperations<String, Banners> listOperations = redisTemplate.opsForList();

        List<Banners> all = listOperations.range("banners", 0, -1);

        if (CollectionUtils.isEmpty(all)) {

            all = bannersDAO.selectAll();
            all.forEach(b -> listOperations.rightPush("banners", b));
        }

        return Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(b -> b.getStatus() != 1)
                .map(b -> {
                    BannersVO vo = new BannersVO();
                    BeanUtils.copyProperties(b, vo);
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
    public PageResult<BannersVO> selectPage(PageRequest<BannersDTO> pageRequest) {

        List<Banners> all = bannersDAO.selectByNameContaining(pageRequest);

        List<BannersVO> voList = Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(b -> b.getStatus() != 1)
                .map(b -> {
                    BannersVO vo = new BannersVO();
                    BeanUtils.copyProperties(b, vo);
                    return vo;
                })
                .collect(Collectors.toList());

        return new PageResult<>(pageRequest, voList);
    }


    /**
     * 根据banner图主键id 改变banner图状态
     * @param status 状态 0正常 2禁用
     * @param id banner图 主键 id
     * @return 更新后，数据库受影响的行数 > 0 ? true : false
     */
    @Override
    public boolean changeStatus(Integer status, Long id) {

        Banners banners = bannersDAO.selectByPrimaryKey(id);

        if (ObjectUtils.isEmpty(banners) || banners.getStatus() == 1) {
            return false;
        }

        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        Date updateTime = new Date();
        String operator = login.getUsername();
        String operatorIp = IpUtil.getLocalIP();

        return bannersDAO.changeStatus(status, id, updateTime, operator, operatorIp) > 0;
    }


}
