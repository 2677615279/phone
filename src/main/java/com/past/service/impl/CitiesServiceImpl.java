package com.past.service.impl;

import com.google.common.base.Preconditions;
import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.dao.AreasDAO;
import com.past.dao.CitiesDAO;
import com.past.dao.ProvincesDAO;
import com.past.domain.dto.CitiesDTO;
import com.past.domain.entity.Areas;
import com.past.domain.entity.Cities;
import com.past.domain.entity.Provinces;
import com.past.domain.entity.Users;
import com.past.domain.vo.CitiesVO;
import com.past.domain.vo.ProvincesVO;
import com.past.exception.ParamException;
import com.past.service.CitiesService;
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
 * 城市模块 业务层接口的实现类
 */
@Service
@Transactional
@Slf4j
public class CitiesServiceImpl implements CitiesService {

    @Autowired
    private CitiesDAO citiesDAO;

    @Autowired
    private ProvincesDAO provincesDAO;

    @Autowired
    private AreasDAO areasDAO;

    /**
     * 注意：以注解方式，这里只能使用@Resource(name = "myRedisTemplate")注入
     * 因为我在自定义RedisConfig类中定义了一个方法 将未指定泛型的RedisTemplate添加到bean中，bean的名称即myRedisTemplate
     * 而我在此处注入的时候，添加了泛型，所以不能单独使用@Autowired(按类型注入)或@Autowired、@Qualifier(value = "myRedisTemplate")组合使用
     */
    @Resource(name = "myRedisTemplate")
    private RedisTemplate<String, Cities> redisTemplate;


    /**
     * 根据主键id 查询城市数据
     * @param id 城市 主键id
     * @return 一个CitiesVO对象
     */
    @Override
    public CitiesVO selectByPrimaryKey(Long id) {

        Cities city = citiesDAO.selectByPrimaryKey(id);

        if (city == null || city.getStatus() == 1) {
            throw new NullPointerException("查找的城市不存在！");
        }

        CitiesVO vo = new CitiesVO();
        BeanUtils.copyProperties(city, vo);

        Provinces province = provincesDAO.selectByPrimaryKey(city.getProvinceId());
        if (!ObjectUtils.isEmpty(province) && province.getStatus() != 1) {
            ProvincesVO provincesVO = new ProvincesVO();
            BeanUtils.copyProperties(province, provincesVO);
            vo.setProvince(provincesVO);
        }

        return vo;
    }


    /**
     * 根据城市名称 和 所属省份的主键id 查询城市
     * @param name 城市名称
     * @param provinceId 所属省份的主键id
     * @return 一个Cities对象
     */
    @Override
    public CitiesVO selectByNameWithProvinceId(String name, Long provinceId) {

        Cities city = citiesDAO.selectByNameWithProvinceId(name, provinceId);

        if (city == null || city.getStatus() == 1) {
            throw new NullPointerException("查找的城市不存在！");
        }

        CitiesVO vo = new CitiesVO();
        BeanUtils.copyProperties(city, vo);

        Provinces province = provincesDAO.selectByPrimaryKey(city.getProvinceId());
        if (!ObjectUtils.isEmpty(province) && province.getStatus() != 1) {
            ProvincesVO provincesVO = new ProvincesVO();
            BeanUtils.copyProperties(province, provincesVO);
            vo.setProvince(provincesVO);
        }

        return vo;
    }


    /**
     * 根据省份主键id 查询该省份下的所有城市
     * @param provinceId 省份 主键id
     * @return 存储该省份下的所有城市的集合
     */
    @Override
    public List<CitiesVO> selectByProvinceId(Long provinceId) {

        List<Cities> all = citiesDAO.selectByProvinceId(provinceId);

        return Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(c -> c.getStatus() != 1)
                .map(c -> {
                    CitiesVO vo = new CitiesVO();
                    BeanUtils.copyProperties(c, vo);
                    Provinces province = provincesDAO.selectByPrimaryKey(c.getProvinceId());
                    if (!ObjectUtils.isEmpty(province) && province.getStatus() != 1) {
                        ProvincesVO provincesVO = new ProvincesVO();
                        BeanUtils.copyProperties(province, provincesVO);
                        vo.setProvince(provincesVO);
                    }
                    return vo;
                })
                .collect(Collectors.toList());
    }


    /**
     * 根据城市主键id 查询所属省份的主键id
     * @param id 城市 主键 id
     * @return 省份的主键id
     */
    @Override
    public Long selectProvinceIdById(Long id) {

        return citiesDAO.selectProvinceIdById(id);
    }


    /**
     * 判断该城市名是否存在，存在返回true 抛出异常；不存在返回false，继续执行业务
     * @param name 城市名称
     * @param id 城市 主键id
     * @param provinceId 所属省份 主键id
     * @return 存在返回true 抛出异常；不存在返回false，继续执行业务
     */
    @Override
    public boolean checkNameExist(String name, Long id, Long provinceId) {

        return citiesDAO.countByNameWithProvinceId(name, id, provinceId) > 0;
    }


    /**
     * 后台新增城市
     * @param citiesDTO 待新增的城市DTO实体
     * @return 新增后，这条数据的主键id > 0 ? true : false
     */
    @Override
    public boolean insert(CitiesDTO citiesDTO) {

        // 校验对象参数 是否满足注解约束  不满足约束，抛出ParamException
        BeanValidator.check(citiesDTO);

        citiesDTO.setName(citiesDTO.getName().trim());
        if (checkNameExist(citiesDTO.getName(), citiesDTO.getId(), citiesDTO.getProvinceId())) {
            throw new ParamException("市名已存在，请更换市名！");
        }

        Cities cities = new Cities();
        BeanUtils.copyProperties(citiesDTO, cities);
        cities.setCreateTime(new Date());
        cities.setUpdateTime(new Date());

        // 获取主体对象
        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        cities.setOperator(login.getUsername());
        cities.setOperatorIp(IpUtil.getLocalIP());

        return citiesDAO.insertSelective(cities) > 0;
    }


    /**
     * 后台删除城市 物理删除
     * @param id 待删除数据的主键id
     * @return 删除成功(数据库受影响的行数>0)返回true
     */
    @Override
    public boolean deletePhysical(Long id) {

        Cities cities = citiesDAO.selectByPrimaryKey(id);

        // 使用google.guava工具包校验待删除的城市是否为空 不存在城市，抛出异常。存在城市，继续执行代码
        Preconditions.checkNotNull(cities,"待删除的城市不存在！");

        return citiesDAO.deleteByPrimaryKey(id) > 0;
    }


    /**
     * 后台删除城市 逻辑删除
     * @param id 待删除数据的主键id
     * @return 删除成功，即将此城市数据的状态改为1(删除)，返回true
     */
    @Override
    public boolean deleteLogic(Long id) {

        Cities cities = citiesDAO.selectByPrimaryKey(id);

        // 使用google.guava工具包校验待删除的城市是否为空 不存在城市，抛出异常。存在城市，继续执行代码
        Preconditions.checkNotNull(cities,"待删除的城市不存在！");

        // 如果待删除城市的状态 不是删除状态，则执行删除
        if (cities.getStatus() != 1) {
            cities.setStatus(1);
            cities.setUpdateTime(new Date());

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();

            cities.setOperator(login.getUsername());
            cities.setOperatorIp(IpUtil.getLocalIP());

            return citiesDAO.updateByPrimaryKeySelective(cities) > 0;
        }

        return false;
    }


    /**
     * 后台更新城市
     * @param citiesDTO 更新后的城市DTO实体对象
     * @return 更新成功true 失败false
     */
    @Override
    public boolean update(CitiesDTO citiesDTO) {

        // 校验对象参数 是否满足注解约束  不满足约束，抛出ParamException
        BeanValidator.check(citiesDTO);

        citiesDTO.setName(citiesDTO.getName().trim());
        if (checkNameExist(citiesDTO.getName(), citiesDTO.getId(), citiesDTO.getProvinceId())) {
            throw new ParamException("市名已存在，请更换市名！");
        }

        Cities before = citiesDAO.selectByPrimaryKey(citiesDTO.getId());

        // 使用google.guava工具包校验待更新的城市是否为空 不存在城市，抛出异常。存在城市，继续执行代码
        Preconditions.checkNotNull(before,"待更新的城市不存在！");

        // 当查询所得的待更新城市不为空 并且状态不是删除状态，才可更新
        if (before.getStatus() != 1) {
            Cities after = new Cities();
            BeanUtils.copyProperties(citiesDTO, after);
            after.setStatus(before.getStatus());
            after.setUpdateTime(new Date());

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();

            after.setOperator(login.getUsername());
            after.setOperatorIp(IpUtil.getLocalIP());

            return citiesDAO.updateByPrimaryKeySelective(after) > 0;
        }

        return false;
    }


    /**
     * 后台查询全部城市数据(过滤掉状态为1 即删除) 的数据
     * @return 符合条件的结果集
     */
    @Override
    public List<CitiesVO> selectAll() {

        ListOperations<String, Cities> listOperations = redisTemplate.opsForList();

        List<Cities> all = listOperations.range("cities", 0, -1);

        if (CollectionUtils.isEmpty(all)) {

            all = citiesDAO.selectAll();
            all.forEach(c -> listOperations.rightPush("cities", c));
        }

        return Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(c -> c.getStatus() != 1)
                .map(c -> {
                    CitiesVO vo = new CitiesVO();
                    BeanUtils.copyProperties(c, vo);
                    Provinces province = provincesDAO.selectByPrimaryKey(c.getProvinceId());
                    if (!ObjectUtils.isEmpty(province) && province.getStatus() != 1) {
                        ProvincesVO provincesVO = new ProvincesVO();
                        BeanUtils.copyProperties(province, provincesVO);
                        vo.setProvince(provincesVO);
                    }
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
    public PageResult<CitiesVO> selectPage(PageRequest<CitiesDTO> pageRequest) {

        List<Cities> all = citiesDAO.selectByNameContaining(pageRequest);

        List<CitiesVO> voList = Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(c -> c.getStatus() != 1)
                .map(c -> {
                    CitiesVO vo = new CitiesVO();
                    BeanUtils.copyProperties(c, vo);
                    Provinces province = provincesDAO.selectByPrimaryKey(c.getProvinceId());
                    if (!ObjectUtils.isEmpty(province) && province.getStatus() != 1) {
                        ProvincesVO provincesVO = new ProvincesVO();
                        BeanUtils.copyProperties(province, provincesVO);
                        vo.setProvince(provincesVO);
                    }
                    return vo;
                })
                .collect(Collectors.toList());

        return new PageResult<>(pageRequest, voList);
    }


    /**
     * 根据城市主键id 改变城市状态
     * @param status 状态 0正常 2禁用
     * @param id 城市 主键 id
     * @return 更新后，数据库受影响的行数 > 0 ? true : false
     */
    @Override
    public boolean changeStatus(Integer status, Long id) {

        Cities city = citiesDAO.selectByPrimaryKey(id);
        Long provinceId = citiesDAO.selectProvinceIdById(id);
        Provinces province = provincesDAO.selectByPrimaryKey(provinceId);

        // 如果待更新的城市为空 或 其所属省份为空 或 其所属省份的状态非正常 返回false
        if (ObjectUtils.isEmpty(city) || ObjectUtils.isEmpty(provinceId) || ObjectUtils.isEmpty(province)
                || province.getStatus() != 0 || city.getStatus() == 1) {
            return false;
        }

        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        Date updateTime = new Date();
        String operator = login.getUsername();
        String operatorIp = IpUtil.getLocalIP();

        return citiesDAO.changeStatus(status, id, updateTime, operator, operatorIp) > 0;
    }


    /**
     * 根据所属省份主键id 改变其下所有城市、区县的状态
     * @param status 状态 0正常 2禁用
     * @param provinceId 所属省份 主键 id
     */
    @Override
    public void changeStatusByProvincesStatus(Integer status, Long provinceId) {

        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        Date updateTime = new Date();
        String operator = login.getUsername();
        String operatorIp = IpUtil.getLocalIP();

        // 根据所属省份的主键id 查询该省份下的所有城市
        List<Cities> citiesList = citiesDAO.selectByProvinceId(provinceId)
                .stream().filter(c -> c.getStatus() != 1).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(citiesList)) {

            // 该省其下所有城市 状态跟随该省的状态 禁用和解禁
            citiesDAO.changeStatusByProvincesStatus(status, provinceId, updateTime, operator, operatorIp);

            // 遍历该省下的所有城市
            for (Cities c : citiesList) {
                // 获取每个城市的主键id
                Long cityId = c.getId();
                // 根据城市主键id 获取其下所有区县
                List<Areas> areasList = areasDAO.selectByCityId(cityId)
                        .stream().filter(a -> a.getStatus() != 1).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(areasList)) {

                    // 该城市下所有区县 状态跟随该城市的状态 禁用和解禁，也即是跟随城市所属省份的状态 禁用和解禁
                    areasDAO.changeStatusByCitiesStatus(status, cityId, updateTime, operator, operatorIp);
                }
            }
        }
    }


}
