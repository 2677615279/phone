package com.past.service.impl;

import com.google.common.base.Preconditions;
import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.dao.GoodsDAO;
import com.past.dao.GoodsTypesDAO;
import com.past.dao.MemoryDAO;
import com.past.domain.dto.GoodsTypesDTO;
import com.past.domain.entity.GoodsTypes;
import com.past.domain.entity.Memory;
import com.past.domain.entity.Users;
import com.past.domain.vo.GoodsTypesVO;
import com.past.domain.vo.GoodsVO;
import com.past.domain.vo.MemoryVO;
import com.past.exception.ParamException;
import com.past.service.GoodsTypesService;
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
 * 商品分类模块 业务层接口的实现类
 */
@Service
@Transactional
@Slf4j
public class GoodsTypesServiceImpl implements GoodsTypesService {

    @Autowired
    private GoodsTypesDAO goodsTypesDAO;

    @Autowired
    private GoodsDAO goodsDAO;

    @Autowired
    private MemoryDAO memoryDAO;

    /**
     * 注意：以注解方式，这里只能使用@Resource(name = "myRedisTemplate")注入
     * 因为我在自定义RedisConfig类中定义了一个方法 将未指定泛型的RedisTemplate添加到bean中，bean的名称即myRedisTemplate
     * 而我在此处注入的时候，添加了泛型，所以不能单独使用@Autowired(按类型注入)或@Autowired、@Qualifier(value = "myRedisTemplate")组合使用
     */
    @Resource(name = "myRedisTemplate")
    private RedisTemplate<String, GoodsTypes> redisTemplate;

    
    /**
     * 根据主键id 查询商品分类数据
     * @param id 商品分类 主键id
     * @return 一个GoodsTypesVO对象
     */
    @Override
    public GoodsTypesVO selectByPrimaryKey(Long id) {

        GoodsTypes goodsTypes = goodsTypesDAO.selectByPrimaryKey(id);

        if (goodsTypes == null || goodsTypes.getStatus() == 1) {
            throw new NullPointerException("查找的商品分类不存在！");
        }

        GoodsTypesVO vo = new GoodsTypesVO();
        BeanUtils.copyProperties(goodsTypes, vo);

        return vo;
    }
    
    
    /**
     * 判断该商品分类名称是否存在，存在返回true 抛出异常；不存在返回false，继续执行业务
     * @param name 商品分类名称
     * @param id 商品分类 主键id
     * @return 存在返回true 抛出异常；不存在返回false，继续执行业务
     */
    @Override
    public boolean checkNameExist(String name, Long id) {
        
        return goodsTypesDAO.countByName(name, id) > 0; 
    }
    
    
    /**
     * 后台新增商品分类
     * @param goodsTypesDTO 待新增的商品分类DTO实体
     * @return 新增后，这条数据的主键id > 0 ? true : false
     */
    @Override
    public boolean insert(GoodsTypesDTO goodsTypesDTO) {

        // 校验对象参数 是否满足注解约束  不满足约束，抛出ParamException
        BeanValidator.check(goodsTypesDTO);

        goodsTypesDTO.setName(goodsTypesDTO.getName().trim());
        if (checkNameExist(goodsTypesDTO.getName(), goodsTypesDTO.getId())) {
            throw new ParamException("商品分类名称已存在，请更换商品分类名称！");
        }

        GoodsTypes goodsTypes = new GoodsTypes();
        BeanUtils.copyProperties(goodsTypesDTO, goodsTypes);
        goodsTypes.setCreateTime(new Date());
        goodsTypes.setUpdateTime(new Date());

        // 获取主体对象
        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        goodsTypes.setOperator(login.getUsername());
        goodsTypes.setOperatorIp(IpUtil.getLocalIP());

        return goodsTypesDAO.insertSelective(goodsTypes) > 0;
    }
    
    
    /**
     * 后台删除商品分类 物理删除
     * @param id 待删除数据的主键id
     * @return 删除成功(数据库受影响的行数>0)返回true
     */
    @Override
    public boolean deletePhysical(Long id) {

        GoodsTypes goodsTypes = goodsTypesDAO.selectByPrimaryKey(id);

        // 使用google.guava工具包校验待删除的商品分类是否为空 不存在商品分类，抛出异常。存在商品分类，继续执行代码
        Preconditions.checkNotNull(goodsTypes,"待删除的商品分类不存在！");

        return goodsTypesDAO.deleteByPrimaryKey(id) > 0;
    }
    
    
    /**
     * 后台删除商品分类 逻辑删除
     * @param id 待删除数据的主键id
     * @return 删除成功，即将此商品分类数据的状态改为1(删除)，返回true
     */
    @Override
    public boolean deleteLogic(Long id) {

        GoodsTypes goodsTypes = goodsTypesDAO.selectByPrimaryKey(id);

        // 使用google.guava工具包校验待删除的商品分类是否为空 不存在商品分类，抛出异常。存在商品分类，继续执行代码
        Preconditions.checkNotNull(goodsTypes,"待删除的商品分类不存在！");

        // 如果待删除商品分类的状态 不是删除状态，则执行删除
        if (goodsTypes.getStatus() != 1) {
            goodsTypes.setStatus(1);
            goodsTypes.setUpdateTime(new Date());

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();

            goodsTypes.setOperator(login.getUsername());
            goodsTypes.setOperatorIp(IpUtil.getLocalIP());

            return goodsTypesDAO.updateByPrimaryKeySelective(goodsTypes) > 0;
        }

        return false;
    }
    
    
    /**
     * 后台更新商品分类
     * @param goodsTypesDTO 更新后的商品分类DTO实体对象
     * @return 更新成功true 失败false
     */
    @Override
    public boolean update(GoodsTypesDTO goodsTypesDTO) {

        // 校验对象参数 是否满足注解约束  不满足约束，抛出ParamException
        BeanValidator.check(goodsTypesDTO);

        goodsTypesDTO.setName(goodsTypesDTO.getName().trim());
        if (checkNameExist(goodsTypesDTO.getName(), goodsTypesDTO.getId())) {
            throw new ParamException("商品分类名称已存在，请更换商品分类名称！");
        }

        GoodsTypes before = goodsTypesDAO.selectByPrimaryKey(goodsTypesDTO.getId());

        // 使用google.guava工具包校验待删除的用户是否为空 不存在商品分类，抛出异常。存在商品分类，继续执行代码
        Preconditions.checkNotNull(before,"待更新的商品分类不存在！");

        // 当查询所得的待更新商品分类不为空 并且状态不是删除状态，才可更新
        if (before.getStatus() != 1) {
            GoodsTypes after = new GoodsTypes();
            BeanUtils.copyProperties(goodsTypesDTO, after);
            after.setStatus(before.getStatus());
            after.setUpdateTime(new Date());

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();

            after.setOperator(login.getUsername());
            after.setOperatorIp(IpUtil.getLocalIP());

            return goodsTypesDAO.updateByPrimaryKeySelective(after) > 0;
        }

        return false;
    }
    
    
    /**
     * 后台查询全部商品分类数据(过滤掉状态为1 即删除) 的数据
     * @return 符合条件的结果集
     */
    @Override
    public List<GoodsTypesVO> selectAll() {

        ListOperations<String, GoodsTypes> listOperations = redisTemplate.opsForList();

        List<GoodsTypes> all = listOperations.range("goodstypes", 0, -1);

        if (CollectionUtils.isEmpty(all)) {

            all = goodsTypesDAO.selectAll();
            all.forEach(gt -> listOperations.rightPush("goodstypes", gt));
        }

        return Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(gt -> gt.getStatus() != 1)
                .map(gt -> {
                    GoodsTypesVO vo = new GoodsTypesVO();
                    BeanUtils.copyProperties(gt, vo);

                    List<GoodsVO> goodsVOList = goodsDAO.selectByType(gt.getId())
                            .stream()
                            .filter(g -> g.getStatus() != 1)
                            .map(g -> {
                                GoodsVO goodsVO = new GoodsVO();
                                BeanUtils.copyProperties(g, goodsVO);

                                Memory memory = memoryDAO.selectByPrimaryKey(g.getMemory());
                                if (!ObjectUtils.isEmpty(memory) && memory.getStatus() != 1) {
                                    MemoryVO memoryVO = new MemoryVO();
                                    BeanUtils.copyProperties(memory, memoryVO);
                                    goodsVO.setMemory(memoryVO);
                                }
                                // 注释掉  如果放开  会形成自循环 抛出异常
//                                goodsVO.setType(vo);
                                return goodsVO;
                            })
                            .collect(Collectors.toList());

                    if (!CollectionUtils.isEmpty(goodsVOList)) {
                        vo.setGoodsVOList(goodsVOList);
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
    public PageResult<GoodsTypesVO> selectPage(PageRequest<GoodsTypesDTO> pageRequest) {

        List<GoodsTypes> all = goodsTypesDAO.selectByNameContaining(pageRequest);

        List<GoodsTypesVO> voList = Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(gt -> gt.getStatus() != 1)
                .map(gt -> {
                    GoodsTypesVO vo = new GoodsTypesVO();
                    BeanUtils.copyProperties(gt, vo);
                    return vo;
                })
                .collect(Collectors.toList());

        return new PageResult<>(pageRequest, voList);
    }
    
    
    /**
     * 根据商品分类主键id 改变商品分类状态
     * @param status 状态 0正常 2禁用
     * @param id 商品分类 主键 id
     * @return 更新后，数据库受影响的行数 > 0 ? true : false
     */
    @Override
    public boolean changeStatus(Integer status, Long id) {

        GoodsTypes goodsTypes = goodsTypesDAO.selectByPrimaryKey(id);

        if (ObjectUtils.isEmpty(goodsTypes) || goodsTypes.getStatus() == 1) {
            return false;
        }

        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        Date updateTime = new Date();
        String operator = login.getUsername();
        String operatorIp = IpUtil.getLocalIP();

        return goodsTypesDAO.changeStatus(status, id, updateTime, operator, operatorIp) > 0;
    }
    
    
}