package com.past.service.impl;

import com.google.common.base.Preconditions;
import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.dao.AreasDAO;
import com.past.dao.CitiesDAO;
import com.past.dao.ProvincesDAO;
import com.past.domain.dto.AreasDTO;
import com.past.domain.entity.Areas;
import com.past.domain.entity.Cities;
import com.past.domain.entity.Provinces;
import com.past.domain.entity.Users;
import com.past.domain.vo.AreasVO;
import com.past.domain.vo.CitiesVO;
import com.past.domain.vo.ProvincesVO;
import com.past.exception.ParamException;
import com.past.service.AreasService;
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
 * 区县模块 业务层接口的实现类
 */
@Service
@Transactional
@Slf4j
public class AreasServiceImpl implements AreasService {

    @Autowired
    private AreasDAO areasDAO;

    @Autowired
    private CitiesDAO citiesDAO;

    @Autowired
    private ProvincesDAO provincesDAO;

    /**
     * 注意：以注解方式，这里只能使用@Resource(name = "myRedisTemplate")注入
     * 因为我在自定义RedisConfig类中定义了一个方法 将未指定泛型的RedisTemplate添加到bean中，bean的名称即myRedisTemplate
     * 而我在此处注入的时候，添加了泛型，所以不能单独使用@Autowired(按类型注入)或@Autowired、@Qualifier(value = "myRedisTemplate")组合使用
     */
    @Resource(name = "myRedisTemplate")
    private RedisTemplate<String, Areas> redisTemplate;


    /**
     * 根据主键id 查询区县数据
     * @param id 区县 主键id
     * @return 一个AreasVO对象
     */
    @Override
    public AreasVO selectByPrimaryKey(Long id) {

        Areas areas = areasDAO.selectByPrimaryKey(id);

        if (areas == null || areas.getStatus() == 1) {
            throw new NullPointerException("查找的区县不存在！");
        }

        AreasVO vo = new AreasVO();
        BeanUtils.copyProperties(areas, vo);
        Cities city = citiesDAO.selectByPrimaryKey(areas.getCityId());
        if (!ObjectUtils.isEmpty(city) && city.getStatus() != 1) {
            CitiesVO citiesVO = new CitiesVO();
            BeanUtils.copyProperties(city, citiesVO);

            Provinces province = provincesDAO.selectByPrimaryKey(city.getProvinceId());
            if (!ObjectUtils.isEmpty(province) && province.getStatus() != 1) {
                ProvincesVO provincesVO = new ProvincesVO();
                BeanUtils.copyProperties(province, provincesVO);
                citiesVO.setProvince(provincesVO);
            }

            vo.setCity(citiesVO);
        }

        return vo;
    }


    /**
     * 根据区县名称 和 所属城市的主键id 查询区县
     * @param name 区县名称
     * @param cityId 所属城市的主键id
     * @return 一个AreasVO对象
     */
    @Override
    public AreasVO selectByNameWithCityId(String name, Long cityId) {

        Areas areas = areasDAO.selectByNameWithCityId(name, cityId);

        if (areas == null || areas.getStatus() == 1) {
            throw new NullPointerException("查找的区县不存在！");
        }

        AreasVO vo = new AreasVO();
        BeanUtils.copyProperties(areas, vo);
        Cities city = citiesDAO.selectByPrimaryKey(areas.getCityId());
        if (!ObjectUtils.isEmpty(city) && city.getStatus() != 1) {
            CitiesVO citiesVO = new CitiesVO();
            BeanUtils.copyProperties(city, citiesVO);

            Provinces province = provincesDAO.selectByPrimaryKey(city.getProvinceId());
            if (!ObjectUtils.isEmpty(province) && province.getStatus() != 1) {
                ProvincesVO provincesVO = new ProvincesVO();
                BeanUtils.copyProperties(province, provincesVO);
                citiesVO.setProvince(provincesVO);
            }

            vo.setCity(citiesVO);
        }

        return vo;
    }


    /**
     * 根据城市主键id 查询该城市下的所有区县
     * @param cityId 城市 主键id
     * @return 存储该城市下的所有区县的集合
     */
    @Override
    public List<AreasVO> selectByCityId(Long cityId) {

        List<Areas> all = areasDAO.selectByCityId(cityId);

        return Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(a -> a.getStatus() != 1)
                .map(a -> {
                    AreasVO vo = new AreasVO();
                    BeanUtils.copyProperties(a, vo);
                    Cities city = citiesDAO.selectByPrimaryKey(a.getCityId());
                    if (!ObjectUtils.isEmpty(city) && city.getStatus() != 1) {
                        CitiesVO citiesVO = new CitiesVO();
                        BeanUtils.copyProperties(city, citiesVO);

                        Provinces province = provincesDAO.selectByPrimaryKey(city.getProvinceId());
                        if (!ObjectUtils.isEmpty(province) && province.getStatus() != 1) {
                            ProvincesVO provincesVO = new ProvincesVO();
                            BeanUtils.copyProperties(province, provincesVO);
                            citiesVO.setProvince(provincesVO);
                        }

                        vo.setCity(citiesVO);
                    }
                    return vo;
                })
                .collect(Collectors.toList());
    }


    /**
     * 判断该区县名是否存在，存在返回true 抛出异常；不存在返回false，继续执行业务
     * @param name 区县名称
     * @param id 区县 主键id
     * @param cityId 所属城市 主键id
     * @return 存在返回true 抛出异常；不存在返回false，继续执行业务
     */
    @Override
    public boolean checkNameExist(String name, Long id, Long cityId) {

        return areasDAO.countByNameWithCityId(name, id, cityId) > 0;
    }


    /**
     * 后台新增区县
     * @param areasDTO 待新增的区县DTO实体
     * @return 新增后，这条数据的主键id > 0 ? true : false
     */
    @Override
    public boolean insert(AreasDTO areasDTO) {

        // 校验对象参数 是否满足注解约束  不满足约束，抛出ParamException
        BeanValidator.check(areasDTO);

        areasDTO.setName(areasDTO.getName().trim());
        if (checkNameExist(areasDTO.getName(), areasDTO.getId(), areasDTO.getCityId())) {
            throw new ParamException("区县名称已存在，请更换区县名称！");
        }

        Areas areas = new Areas();
        BeanUtils.copyProperties(areasDTO, areas);
        areas.setCreateTime(new Date());
        areas.setUpdateTime(new Date());

        // 获取主体对象
        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        areas.setOperator(login.getUsername());
        areas.setOperatorIp(IpUtil.getLocalIP());

        return areasDAO.insertSelective(areas) > 0;
    }


    /**
     * 后台删除区县 物理删除
     * @param id 待删除数据的主键id
     * @return 删除成功(数据库受影响的行数>0)返回true
     */
    @Override
    public boolean deletePhysical(Long id) {

        Areas areas = areasDAO.selectByPrimaryKey(id);

        // 使用google.guava工具包校验待删除的区县是否为空 不存在区县，抛出异常。存在区县，继续执行代码
        Preconditions.checkNotNull(areas,"待删除的区县不存在！");

        return areasDAO.deleteByPrimaryKey(id) > 0;
    }


    /**
     * 后台删除区县 逻辑删除
     * @param id 待删除数据的主键id
     * @return 删除成功，即将此区县数据的状态改为1(删除)，返回true
     */
    @Override
    public boolean deleteLogic(Long id) {

        Areas areas = areasDAO.selectByPrimaryKey(id);

        // 使用google.guava工具包校验待删除的区县是否为空 不存在区县，抛出异常。存在区县，继续执行代码
        Preconditions.checkNotNull(areas,"待删除的区县不存在！");

        // 如果待删除区县的状态 不是删除状态，则执行删除
        if (areas.getStatus() != 1) {
            areas.setStatus(1);
            areas.setUpdateTime(new Date());

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();

            areas.setOperator(login.getUsername());
            areas.setOperatorIp(IpUtil.getLocalIP());

            return areasDAO.updateByPrimaryKeySelective(areas) > 0;
        }

        return false;
    }


    /**
     * 后台更新区县
     * @param areasDTO 更新后的区县DTO实体对象
     * @return 更新成功true 失败false
     */
    @Override
    public boolean update(AreasDTO areasDTO) {

        // 校验对象参数 是否满足注解约束  不满足约束，抛出ParamException
        BeanValidator.check(areasDTO);

        areasDTO.setName(areasDTO.getName().trim());
        if (checkNameExist(areasDTO.getName(), areasDTO.getId(), areasDTO.getCityId())) {
            throw new ParamException("区县名称已存在，请更换区县名称！");
        }

        Areas before = areasDAO.selectByPrimaryKey(areasDTO.getId());

        // 使用google.guava工具包校验待更新的区县是否为空 不存在区县，抛出异常。存在区县，继续执行代码
        Preconditions.checkNotNull(before,"待更新的区县不存在！");

        // 当查询所得的待更新区县不为空 并且状态不是删除状态，才可更新
        if (before.getStatus() != 1) {
            Areas after = new Areas();
            BeanUtils.copyProperties(areasDTO, after);
            after.setStatus(before.getStatus());
            after.setUpdateTime(new Date());

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();

            after.setOperator(login.getUsername());
            after.setOperatorIp(IpUtil.getLocalIP());

            return areasDAO.updateByPrimaryKeySelective(after) > 0;
        }

        return false;
    }


    /**
     * 后台查询全部区县数据(过滤掉状态为1 即删除) 的数据
     * @return 符合条件的结果集
     */
    @Override
    public List<AreasVO> selectAll() {

        ListOperations<String, Areas> listOperations = redisTemplate.opsForList();

        List<Areas> all = listOperations.range("areas", 0, -1);

        if (CollectionUtils.isEmpty(all)) {

            all = areasDAO.selectAll();
            all.forEach(a -> listOperations.rightPush("areas", a));
        }

        return Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(a -> a.getStatus() != 1)
                .map(a -> {
                    AreasVO vo = new AreasVO();
                    BeanUtils.copyProperties(a, vo);
                    Cities city = citiesDAO.selectByPrimaryKey(a.getCityId());
                    if (!ObjectUtils.isEmpty(city) && city.getStatus() != 1) {
                        CitiesVO citiesVO = new CitiesVO();
                        BeanUtils.copyProperties(city, citiesVO);

                        Provinces province = provincesDAO.selectByPrimaryKey(city.getProvinceId());
                        if (!ObjectUtils.isEmpty(province) && province.getStatus() != 1) {
                            ProvincesVO provincesVO = new ProvincesVO();
                            BeanUtils.copyProperties(province, provincesVO);
                            citiesVO.setProvince(provincesVO);
                        }

                        vo.setCity(citiesVO);
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
    public PageResult<AreasVO> selectPage(PageRequest<AreasDTO> pageRequest) {

        List<Areas> all = areasDAO.selectByNameContaining(pageRequest);

        List<AreasVO> voList = Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(a -> a.getStatus() != 1)
                .map(a -> {
                    AreasVO vo = new AreasVO();
                    BeanUtils.copyProperties(a, vo);
                    Cities city = citiesDAO.selectByPrimaryKey(a.getCityId());
                    if (!ObjectUtils.isEmpty(city) && city.getStatus() != 1) {
                        CitiesVO citiesVO = new CitiesVO();
                        BeanUtils.copyProperties(city, citiesVO);

                        Provinces province = provincesDAO.selectByPrimaryKey(city.getProvinceId());
                        if (!ObjectUtils.isEmpty(province) && province.getStatus() != 1) {
                            ProvincesVO provincesVO = new ProvincesVO();
                            BeanUtils.copyProperties(province, provincesVO);
                            citiesVO.setProvince(provincesVO);
                        }

                        vo.setCity(citiesVO);
                    }
                    return vo;
                })
                .collect(Collectors.toList());

        return new PageResult<>(pageRequest, voList);
    }


    /**
     * 根据区县主键id 改变区县状态
     * @param status 状态 0正常 2禁用
     * @param id 区县 主键 id
     * @return 更新后，数据库受影响的行数 > 0 ? true : false
     */
    @Override
    public boolean changeStatus(Integer status, Long id) {

        Areas area = areasDAO.selectByPrimaryKey(id);
        Long cityId = areasDAO.selectCityIdById(id);
        Cities city = citiesDAO.selectByPrimaryKey(cityId);

        // 如果待更新的区县为空 或 其所属城市为空 或 其所属城市的状态非正常 返回false
        if (ObjectUtils.isEmpty(area) || ObjectUtils.isEmpty(cityId) || ObjectUtils.isEmpty(city)
                || city.getStatus() != 0 || area.getStatus() == 1) {
            return false;
        }

        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        Date updateTime = new Date();
        String operator = login.getUsername();
        String operatorIp = IpUtil.getLocalIP();

        return areasDAO.changeStatus(status, id, updateTime, operator, operatorIp) > 0;
    }


    /**
     * 根据所属城市主键id 改变其下所有区县的状态
     * @param status 状态 0正常 2禁用
     * @param cityId 所属城市 主键 id
     */
    @Override
    public void changeStatusByCitiesStatus(Integer status, Long cityId) {

        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        Date updateTime = new Date();
        String operator = login.getUsername();
        String operatorIp = IpUtil.getLocalIP();

        // 根据所属城市的主键id 查询该城市下的所有区县
        List<Areas> areasList = areasDAO.selectByCityId(cityId)
                .stream().filter(a -> a.getStatus() != 1).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(areasList)) {

            // 该城市下所有区县 状态跟随该城市的状态 禁用和解禁
            areasDAO.changeStatusByCitiesStatus(status, cityId, updateTime, operator, operatorIp);
        }
    }

    
}
