package com.past.service.impl;

import com.google.common.base.Preconditions;
import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.dao.*;
import com.past.domain.dto.GoodsDTO;
import com.past.domain.entity.*;
import com.past.domain.vo.*;
import com.past.exception.ParamException;
import com.past.service.GoodsService;
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
 * 商品模块 业务层接口的实现类
 */
@Service
@Transactional
@Slf4j
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsDAO goodsDAO;

    @Autowired
    private GoodsTypesDAO goodsTypesDAO;

    @Autowired
    private MemoryDAO memoryDAO;

    @Autowired
    private EvaluatesDAO evaluatesDAO;

    @Autowired
    private EvaluatesImagesDAO evaluatesImagesDAO;

    @Autowired
    private UsersDAO usersDAO;

    /**
     * 注意：以注解方式，这里只能使用@Resource(name = "myRedisTemplate")注入
     * 因为我在自定义RedisConfig类中定义了一个方法 将未指定泛型的RedisTemplate添加到bean中，bean的名称即myRedisTemplate
     * 而我在此处注入的时候，添加了泛型，所以不能单独使用@Autowired(按类型注入)或@Autowired、@Qualifier(value = "myRedisTemplate")组合使用
     */
    @Resource(name = "myRedisTemplate")
    private RedisTemplate<String, Goods> redisTemplate;


    /**
     * 根据主键id 查询商品
     * @param id 商品 主键id
     * @return GoodsVO
     */
    @Override
    public GoodsVO selectByPrimaryKey(Long id) {

        Goods goods = goodsDAO.selectByPrimaryKey(id);

        if (goods == null || goods.getStatus() == 1) {
            throw new NullPointerException("查找的商品不存在！");
        }

        GoodsVO vo = new GoodsVO();
        BeanUtils.copyProperties(goods, vo);

        GoodsTypes type = goodsTypesDAO.selectByPrimaryKey(goods.getType());
        if (!ObjectUtils.isEmpty(type) && type.getStatus() != 1) {
            GoodsTypesVO typesVO = new GoodsTypesVO();
            BeanUtils.copyProperties(type, typesVO);
            vo.setType(typesVO);
        }

        Memory memory = memoryDAO.selectByPrimaryKey(goods.getMemory());
        if (!ObjectUtils.isEmpty(memory) && memory.getStatus() != 1) {
            MemoryVO memoryVO = new MemoryVO();
            BeanUtils.copyProperties(memory, memoryVO);
            vo.setMemory(memoryVO);
        }

        List<Evaluates> all = evaluatesDAO.selectByGoodsId(goods.getId());
        List<EvaluatesVO> voList = Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(e -> e.getStatus() == 0)
                .map(e -> {
                    EvaluatesVO evaluatesVO = new EvaluatesVO();
                    BeanUtils.copyProperties(e, evaluatesVO);

                    Users user = usersDAO.selectByPrimaryKey(e.getUserId());
                    if (!ObjectUtils.isEmpty(user) && user.getStatus() != 1) {
                        UsersVO userVO = new UsersVO();
                        BeanUtils.copyProperties(user, userVO);
                        evaluatesVO.setUser(userVO);
                    }

                    evaluatesVO.setGoods(vo);

                    // 查询其下所有图片
                    List<EvaluatesImagesVO> imgList = evaluatesImagesDAO.selectByEvaluateId(e.getId())
                            .stream()
                            .filter(img -> img.getStatus() == 0)
                            .map(img -> {
                                EvaluatesImagesVO imgVO = new EvaluatesImagesVO();
                                BeanUtils.copyProperties(img, imgVO);
                                return imgVO;
                            })
                            .collect(Collectors.toList());

                    if (!CollectionUtils.isEmpty(imgList)) {
                        evaluatesVO.setImgList(imgList);
                    }

                    return evaluatesVO;
                })
                .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(voList)) {
            vo.setEvaluatesVOList(voList);
        }

        return vo;
    }


    /**
     * 判断 该商品的名称是否已存在
     * @param name 商品名称
     * @param id 商品 主键id
     * @return 存在返回true 抛出异常；不存在返回false，继续执行业务
     */
    @Override
    public boolean checkNameExist(String name, Long id) {

        return goodsDAO.countByName(name, id) > 0;
    }


    /**
     * 根据关联的分类主键id 查询该类的商品 存入一个集合
     * @param typeId 关联的分类主键id
     * @return 存储该类商品的集合
     */
    @Override
    public List<GoodsVO> selectByType(Long typeId) {

        List<Goods> all = goodsDAO.selectByType(typeId);

        return Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(g -> g.getStatus() != 1)
                .map(g -> {
                    GoodsVO vo = new GoodsVO();
                    BeanUtils.copyProperties(g, vo);

                    GoodsTypes types = goodsTypesDAO.selectByPrimaryKey(g.getType());
                    if (!ObjectUtils.isEmpty(types) && types.getStatus() != 1) {
                        GoodsTypesVO goodsTypesVO = new GoodsTypesVO();
                        BeanUtils.copyProperties(types, goodsTypesVO);
                        vo.setType(goodsTypesVO);
                    }

                    Memory memory = memoryDAO.selectByPrimaryKey(g.getMemory());
                    if (!ObjectUtils.isEmpty(memory) && memory.getStatus() != 1) {
                        MemoryVO memoryVO = new MemoryVO();
                        BeanUtils.copyProperties(memory, memoryVO);
                        vo.setMemory(memoryVO);
                    }

                    // 此处不用查评价 只是根据类别查商品 评价是根据每个商品的主键id所获取

                    return vo;
                })
                .collect(Collectors.toList());
    }


    /**
     * 查询热卖的商品 即status为2 存入一个集合
     * @return 存储热卖商品的集合
     */
    @Override
    public List<GoodsVO> selectByHot() {

        List<Goods> all = goodsDAO.selectAll(false);

        return Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(g -> g.getStatus() == 2)
                .map(g -> {
                    GoodsVO vo = new GoodsVO();
                    BeanUtils.copyProperties(g, vo);

                    GoodsTypes types = goodsTypesDAO.selectByPrimaryKey(g.getType());
                    if (!ObjectUtils.isEmpty(types) && types.getStatus() != 1) {
                        GoodsTypesVO goodsTypesVO = new GoodsTypesVO();
                        BeanUtils.copyProperties(types, goodsTypesVO);
                        vo.setType(goodsTypesVO);
                    }

                    Memory memory = memoryDAO.selectByPrimaryKey(g.getMemory());
                    if (!ObjectUtils.isEmpty(memory) && memory.getStatus() != 1) {
                        MemoryVO memoryVO = new MemoryVO();
                        BeanUtils.copyProperties(memory, memoryVO);
                        vo.setMemory(memoryVO);
                    }

                    // 此处不用查评价 只是筛选热卖商品 评价是根据每个商品的主键id所获取

                    return vo;
                })
                .collect(Collectors.toList());
    }


    /**
     * 根据销量查询  降序排列
     * @return 存储符合条件结果的集合
     */
    @Override
    public List<GoodsVO> selectByVolume() {

        List<Goods> all = goodsDAO.selectAll(true);

        return Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(g -> g.getStatus() != 1)
                .map(g -> {
                    GoodsVO vo = new GoodsVO();
                    BeanUtils.copyProperties(g, vo);

                    GoodsTypes types = goodsTypesDAO.selectByPrimaryKey(g.getType());
                    if (!ObjectUtils.isEmpty(types) && types.getStatus() != 1) {
                        GoodsTypesVO goodsTypesVO = new GoodsTypesVO();
                        BeanUtils.copyProperties(types, goodsTypesVO);
                        vo.setType(goodsTypesVO);
                    }

                    Memory memory = memoryDAO.selectByPrimaryKey(g.getMemory());
                    if (!ObjectUtils.isEmpty(memory) && memory.getStatus() != 1) {
                        MemoryVO memoryVO = new MemoryVO();
                        BeanUtils.copyProperties(memory, memoryVO);
                        vo.setMemory(memoryVO);
                    }

                    // 此处不用查评价 只是筛选热卖商品 评价是根据每个商品的主键id所获取

                    return vo;
                })
                .collect(Collectors.toList());
    }


    /**
     * 根据条件 查询符合的结果集
     * @param searchVO 封装查询条件的实体
     * @return 符合条件的结果集
     */
    @Override
    public List<GoodsVO> selectByCondition(SearchVO searchVO) {

        List<Goods> all = goodsDAO.selectByCondition(searchVO);

        return Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(g -> g.getStatus() != 1)
                .map(g -> {
                    GoodsVO vo = new GoodsVO();
                    BeanUtils.copyProperties(g, vo);

                    GoodsTypes types = goodsTypesDAO.selectByPrimaryKey(g.getType());
                    if (!ObjectUtils.isEmpty(types) && types.getStatus() != 1) {
                        GoodsTypesVO goodsTypesVO = new GoodsTypesVO();
                        BeanUtils.copyProperties(types, goodsTypesVO);
                        vo.setType(goodsTypesVO);
                    }

                    Memory memory = memoryDAO.selectByPrimaryKey(g.getMemory());
                    if (!ObjectUtils.isEmpty(memory) && memory.getStatus() != 1) {
                        MemoryVO memoryVO = new MemoryVO();
                        BeanUtils.copyProperties(memory, memoryVO);
                        vo.setMemory(memoryVO);
                    }

                    // 此处不用查评价 只是筛选热卖商品 评价是根据每个商品的主键id所获取

                    return vo;
                })
                .collect(Collectors.toList());
    }


    /**
     * 新增 商品
     * @param goodsDTO 待新增的商品DTO实体
     * @return 新增后，这条数据的主键id > 0 ? true : false
     */
    @Override
    public boolean insert(GoodsDTO goodsDTO) {

        // 校验对象参数 是否满足注解约束  不满足约束，抛出ParamException
        BeanValidator.check(goodsDTO);

        goodsDTO.setName(goodsDTO.getName().trim());
        goodsDTO.setColor(goodsDTO.getColor().trim());
        goodsDTO.setDescription(goodsDTO.getDescription().trim());

        if (checkNameExist(goodsDTO.getName(), goodsDTO.getId())) {
            throw new ParamException("商品名称已存在，请更换商品名称！");
        }

        Goods goods = new Goods();
        BeanUtils.copyProperties(goodsDTO, goods);
        goods.setCreateTime(new Date());
        goods.setUpdateTime(new Date());

        // 获取主体对象
        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        goods.setOperator(login.getUsername());
        goods.setOperatorIp(IpUtil.getLocalIP());


        return goodsDAO.insertSelective(goods) > 0;
    }


    /**
     * 根据主键id 删除商品 物理删除
     * @param id 待删除商品的主键id
     * @return 数据库受影响的行数 > 0 ? true : false
     */
    @Override
    public boolean deletePhysical(Long id) {

        Goods goods = goodsDAO.selectByPrimaryKey(id);

        // 使用google.guava工具包校验待删除的商品是否为空 不存在商品，抛出异常。存在商品，继续执行代码
        Preconditions.checkNotNull(goods,"待删除的商品不存在！");

        return goodsDAO.deleteByPrimaryKey(id) > 0;
    }


    /**
     * 根据主键id 删除商品 逻辑删除
     * @param id 待删除商品的主键id
     * @return 数据库受影响的行数 > 0 ? true : false
     */
    @Override
    public boolean deleteLogic(Long id) {

        Goods goods = goodsDAO.selectByPrimaryKey(id);

        // 使用google.guava工具包校验待删除的商品是否为空 不存在商品，抛出异常。存在商品，继续执行代码
        Preconditions.checkNotNull(goods,"待删除的商品不存在！");

        // 如果待删除商品的状态 不是删除状态，则执行删除
        if (goods.getStatus() != 1) {
            goods.setStatus(1);
            goods.setUpdateTime(new Date());

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();

            goods.setOperator(login.getUsername());
            goods.setOperatorIp(IpUtil.getLocalIP());

            return goodsDAO.updateByPrimaryKeySelective(goods) > 0;
        }

        return false;
    }


    /**
     * 更新商品
     * @param goodsDTO 更新后的商品DTO实体
     * @return 数据库受影响的行数 > 0 ? true : false
     */
    @Override
    public boolean update(GoodsDTO goodsDTO) {

        // 校验对象参数 是否满足注解约束  不满足约束，抛出ParamException
        BeanValidator.check(goodsDTO);

        goodsDTO.setName(goodsDTO.getName().trim());
        goodsDTO.setColor(goodsDTO.getColor().trim());
        goodsDTO.setDescription(goodsDTO.getDescription().trim());

        if (checkNameExist(goodsDTO.getName(), goodsDTO.getId())) {
            throw new ParamException("商品名称已存在，请更换商品名称！");
        }

        Goods before = goodsDAO.selectByPrimaryKey(goodsDTO.getId());

        // 使用google.guava工具包校验待更新的商品是否为空 不存在商品，抛出异常。存在商品，继续执行代码
        Preconditions.checkNotNull(before,"待更新的商品不存在！");

        // 当查询所得的待更新商品不为空 并且状态不是删除状态，才可更新
        if (before.getStatus() != 1) {
            Goods after = new Goods();
            BeanUtils.copyProperties(goodsDTO, after);
            after.setStatus(before.getStatus());
            after.setUpdateTime(new Date());

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();

            after.setOperator(login.getUsername());
            after.setOperatorIp(IpUtil.getLocalIP());

            return goodsDAO.updateByPrimaryKeySelective(after) > 0;
        }

        return false;
    }


    /**
     * 查询全部商品 过滤掉状态为1即已删除的  存入一个集合
     * @return 存储符合条件的结果集
     */
    @Override
    public List<GoodsVO> selectAll() {

        ListOperations<String, Goods> listOperations = redisTemplate.opsForList();

        List<Goods> all = listOperations.range("goods", 0, -1);

        if (CollectionUtils.isEmpty(all)) {

            all = goodsDAO.selectAll(false);
            all.forEach(g -> listOperations.rightPush("goods", g));
        }

        return Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(g -> g.getStatus() != 1)
                .map(g -> {
                    GoodsVO vo = new GoodsVO();
                    BeanUtils.copyProperties(g, vo);

                    GoodsTypes types = goodsTypesDAO.selectByPrimaryKey(g.getType());
                    if (!ObjectUtils.isEmpty(types) && types.getStatus() != 1) {
                        GoodsTypesVO goodsTypesVO = new GoodsTypesVO();
                        BeanUtils.copyProperties(types, goodsTypesVO);
                        vo.setType(goodsTypesVO);
                    }

                    Memory memory = memoryDAO.selectByPrimaryKey(g.getMemory());
                    if (!ObjectUtils.isEmpty(memory) && memory.getStatus() != 1) {
                        MemoryVO memoryVO = new MemoryVO();
                        BeanUtils.copyProperties(memory, memoryVO);
                        vo.setMemory(memoryVO);
                    }

                    // 此处不用查评价 只是筛选正常+热卖的商品 评价是根据每个商品的主键id所获取

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
    public PageResult<GoodsVO> selectPage(PageRequest<GoodsDTO> pageRequest) {

        List<Goods> all = goodsDAO.selectByNameContaining(pageRequest);

        List<GoodsVO> voList = Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(g -> g.getStatus() != 1)
                .map(g -> {
                    GoodsVO vo = new GoodsVO();
                    BeanUtils.copyProperties(g, vo);

                    GoodsTypes types = goodsTypesDAO.selectByPrimaryKey(g.getType());
                    if (!ObjectUtils.isEmpty(types) && types.getStatus() != 1) {
                        GoodsTypesVO goodsTypesVO = new GoodsTypesVO();
                        BeanUtils.copyProperties(types, goodsTypesVO);
                        vo.setType(goodsTypesVO);
                    }

                    Memory memory = memoryDAO.selectByPrimaryKey(g.getMemory());
                    if (!ObjectUtils.isEmpty(memory) && memory.getStatus() != 1) {
                        MemoryVO memoryVO = new MemoryVO();
                        BeanUtils.copyProperties(memory, memoryVO);
                        vo.setMemory(memoryVO);
                    }

                    // 此处不用查评价 只是筛选正常+热卖的商品 评价是根据每个商品的主键id所获取

                    return vo;
                })
                .collect(Collectors.toList());

        return new PageResult<>(pageRequest, voList);
    }


}