package com.past.service.impl;

import com.google.common.base.Preconditions;
import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.dao.*;
import com.past.domain.dto.EvaluatesDTO;
import com.past.domain.dto.EvaluatesImagesDTO;
import com.past.domain.entity.*;
import com.past.domain.vo.*;
import com.past.service.EvaluatesService;
import com.past.util.BeanValidator;
import com.past.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 评价模块(含图) 业务层接口的实现类
 */
@Service
@Transactional
@Slf4j
public class EvaluatesServiceImpl implements EvaluatesService {

    @Autowired
    private EvaluatesDAO evaluatesDAO;

    @Autowired
    private EvaluatesImagesDAO evaluatesImagesDAO;

    @Autowired
    private UsersDAO usersDAO;

    @Autowired
    private GoodsDAO goodsDAO;

    @Autowired
    private GoodsTypesDAO goodsTypesDAO;

    @Autowired
    private MemoryDAO memoryDAO;


    /**
     * 根据主键id 查询评价
     * @param id 评价主键id
     * @return 一条评价数据，含其下的图片
     */
    @Override
    public EvaluatesVO selectByPrimaryKey(Long id) {

        Evaluates evaluates = evaluatesDAO.selectByPrimaryKey(id);

        if (evaluates == null || evaluates.getStatus() == 1) {
            throw new NullPointerException("查找的评价不存在！");
        }

        EvaluatesVO vo = new EvaluatesVO();
        BeanUtils.copyProperties(evaluates, vo);

        Users user = usersDAO.selectByPrimaryKey(evaluates.getUserId());
        if (!ObjectUtils.isEmpty(user) && user.getStatus() != 1) {
            UsersVO userVO = new UsersVO();
            BeanUtils.copyProperties(user, userVO);
            vo.setUser(userVO);
        }

        Goods goods = goodsDAO.selectByPrimaryKey(evaluates.getGoodsId());
        if (!ObjectUtils.isEmpty(goods) && goods.getStatus() != 1) {
            GoodsVO goodsVO = new GoodsVO();
            BeanUtils.copyProperties(goods, goodsVO);

            GoodsTypes goodsTypes = goodsTypesDAO.selectByPrimaryKey(goods.getType());
            if (!ObjectUtils.isEmpty(goodsTypes) && goodsTypes.getStatus() != 1) {
                GoodsTypesVO goodsTypesVO = new GoodsTypesVO();
                BeanUtils.copyProperties(goodsTypes, goodsTypesVO);
                goodsVO.setType(goodsTypesVO);
            }

            Memory memory = memoryDAO.selectByPrimaryKey(goods.getMemory());
            if (!ObjectUtils.isEmpty(memory) && memory.getStatus() != 1) {
                MemoryVO memoryVO = new MemoryVO();
                BeanUtils.copyProperties(memory, memoryVO);
                goodsVO.setMemory(memoryVO);
            }

            vo.setGoods(goodsVO);
        }

        List<EvaluatesImages> imgList = evaluatesImagesDAO.selectByEvaluateId(id);
        List<EvaluatesImagesVO> imgVOList = Optional.ofNullable(imgList)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(img -> img.getStatus() == 0)
                .map(img -> {
                    EvaluatesImagesVO imgVO = new EvaluatesImagesVO();
                    BeanUtils.copyProperties(img, imgVO);
                    return imgVO;
                })
                .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(imgVOList)) {
            vo.setImgList(imgVOList);
        }

        return vo;
    }


    /**
     * 新增评价(含图)
     * @param evaluatesDTO 待新增的评价DTO实体
     * @param imgs 待新增的图片名称数组
     * @return 新增后，这条数据的主键id > 0 ? true : false
     */
    @Override
    public boolean insert(EvaluatesDTO evaluatesDTO, String[] imgs) {

        /*
         * 注意：因为evaluatesDTO从前端传来时，核心校验参数不全，需要在service层额外set属性，校验就要在service层处理，
         * 而且异于controller层的参数校验(只在类上和方法入参添加@Validated即可)
         * 此时需要：@Validated和@Valid必须组合使用在service层接口(注意：是接口而非其实现类)，
         * @Validated添加在service层接口和方法上，@Valid添加在方法的参数前，表示级联验证
         */
        // 校验对象参数 是否满足注解约束  不满足约束，抛出ParamException
        BeanValidator.check(evaluatesDTO);

        if (!StringUtils.isEmpty(evaluatesDTO.getContent())) {
            evaluatesDTO.setContent(evaluatesDTO.getContent().trim());
        }

        Date date = new Date();
        Evaluates evaluates = new Evaluates();
        BeanUtils.copyProperties(evaluatesDTO, evaluates);
        evaluates.setCreateTime(date);
        evaluates.setUpdateTime(date);

        // 获取主体对象
        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        evaluates.setOperator(login.getUsername());
        evaluates.setOperatorIp(IpUtil.getLocalIP());

        // 如果没有图片 直接新增评价即可
        if (imgs == null || imgs.length == 0 || Arrays.asList(imgs).contains("")) {

            return evaluatesDAO.insertSelective(evaluates) > 0;
        }

        // 新增评价、获取刚才新增数据的主键id，再新增图片
        int res = evaluatesDAO.insertSelective(evaluates);
        if (res > 0) {
            for (String img : imgs) {
                EvaluatesImagesDTO evaluatesImagesDTO = new EvaluatesImagesDTO();
                evaluatesImagesDTO.setName(img);
                evaluatesImagesDTO.setEvaluateId(evaluates.getId());

                // 校验对象参数 是否满足注解约束  不满足约束，抛出ParamException
                BeanValidator.check(evaluatesImagesDTO);
                EvaluatesImages evaluatesImages = new EvaluatesImages();
                BeanUtils.copyProperties(evaluatesImagesDTO, evaluatesImages);

                evaluatesImages.setCreateTime(date);
                evaluatesImages.setUpdateTime(date);
                evaluatesImages.setOperator(evaluates.getOperator());
                evaluatesImages.setOperatorIp(evaluates.getOperatorIp());

                evaluatesImagesDAO.insertSelective(evaluatesImages);
            }
        }

        return res > 0;
    }


    /**
     * 删除评价 连带删除其下的所有图片 物理删除
     * @param id 待删除数据的主键id
     * @return 删除成功(数据库受影响的行数>0)返回true
     */
    @Override
    public boolean deletePhysical(Long id) {

        Evaluates evaluates = evaluatesDAO.selectByPrimaryKey(id);

        // 使用google.guava工具包校验待删除的评价是否为空 不存在评价，抛出异常。存在评价，继续执行代码
        Preconditions.checkNotNull(evaluates,"待删除的评价不存在！");

        List<EvaluatesImages> imgList = evaluatesImagesDAO.selectByEvaluateId(id);
        if (!CollectionUtils.isEmpty(imgList)) {
            evaluatesImagesDAO.deleteByEvaluateId(id);
        }

        return evaluatesDAO.deleteByPrimaryKey(id) > 0;
    }


    /**
     * 删除评价 连带删除其下的所有图片 逻辑删除
     * @param id 待删除数据的主键id
     * @return 删除成功(数据库受影响的行数>0)返回true
     */
    @Override
    public boolean deleteLogic(Long id) {

        Evaluates evaluates = evaluatesDAO.selectByPrimaryKey(id);

        // 使用google.guava工具包校验待删除的评价是否为空 不存在评价，抛出异常。存在评价，继续执行代码
        Preconditions.checkNotNull(evaluates,"待删除的评价不存在！");

        // 只有当其存在 且状态不是删除 才可逻辑删除
        if (evaluates.getStatus() != 1) {
            Date updateTime = new Date();
            evaluates.setStatus(1);
            evaluates.setUpdateTime(updateTime);

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();

            evaluates.setOperator(login.getUsername());
            evaluates.setOperatorIp(IpUtil.getLocalIP());

            // 查询该评价下的所有图片，有则依次逻辑删除，无则删除评价即可
            List<EvaluatesImages> imgList = evaluatesImagesDAO.selectByEvaluateId(id)
                    .stream().filter(e -> e.getStatus() != 1).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(imgList)) {
                for (EvaluatesImages img : imgList) {
                    img.setStatus(1);
                    img.setUpdateTime(updateTime);
                    img.setOperator(evaluates.getOperator());
                    img.setOperatorIp(evaluates.getOperatorIp());
                }
                evaluatesImagesDAO.updateBatchSelective(imgList);
            }

            return evaluatesDAO.updateByPrimaryKeySelective(evaluates) > 0;
        }

       return false;
    }


    /**
     * 根据商品的主键id，查询该商品的所有评价，存入一个集合
     * @param goodsId 评价的商品的主键id
     * @return 存储该商品所有评价的集合
     */
    @Override
    public List<EvaluatesVO> selectByGoodsId(Long goodsId) {

        List<Evaluates> all = evaluatesDAO.selectByGoodsId(goodsId);

        return Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(e -> e.getStatus() == 0)
                .map(e -> {
                    EvaluatesVO vo = new EvaluatesVO();
                    BeanUtils.copyProperties(e, vo);

                    Users user = usersDAO.selectByPrimaryKey(e.getUserId());
                    if (!ObjectUtils.isEmpty(user) && user.getStatus() != 1) {
                        UsersVO userVO = new UsersVO();
                        BeanUtils.copyProperties(user, userVO);
                        vo.setUser(userVO);
                    }

                    Goods goods = goodsDAO.selectByPrimaryKey(e.getGoodsId());
                    if (!ObjectUtils.isEmpty(goods) && goods.getStatus() != 1) {
                        GoodsVO goodsVO = new GoodsVO();
                        BeanUtils.copyProperties(goods, goodsVO);

                        GoodsTypes goodsTypes = goodsTypesDAO.selectByPrimaryKey(goods.getType());
                        if (!ObjectUtils.isEmpty(goodsTypes) && goodsTypes.getStatus() != 1) {
                            GoodsTypesVO goodsTypesVO = new GoodsTypesVO();
                            BeanUtils.copyProperties(goodsTypes, goodsTypesVO);
                            goodsVO.setType(goodsTypesVO);
                        }

                        Memory memory = memoryDAO.selectByPrimaryKey(goods.getMemory());
                        if (!ObjectUtils.isEmpty(memory) && memory.getStatus() != 1) {
                            MemoryVO memoryVO = new MemoryVO();
                            BeanUtils.copyProperties(memory, memoryVO);
                            goodsVO.setMemory(memoryVO);
                        }

                        vo.setGoods(goodsVO);
                    }

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
                        vo.setImgList(imgList);
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
    public PageResult<EvaluatesVO> selectPage(PageRequest<EvaluatesDTO> pageRequest) {

        List<Evaluates> all = evaluatesDAO.selectByContentContaining(pageRequest);

        List<EvaluatesVO> voList = Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(e -> e.getStatus() == 0)
                .map(e -> {
                    EvaluatesVO vo = new EvaluatesVO();
                    BeanUtils.copyProperties(e, vo);

                    Users user = usersDAO.selectByPrimaryKey(e.getUserId());
                    if (!ObjectUtils.isEmpty(user) && user.getStatus() != 1) {
                        UsersVO userVO = new UsersVO();
                        BeanUtils.copyProperties(user, userVO);
                        vo.setUser(userVO);
                    }

                    Goods goods = goodsDAO.selectByPrimaryKey(e.getGoodsId());
                    if (!ObjectUtils.isEmpty(goods) && goods.getStatus() != 1) {
                        GoodsVO goodsVO = new GoodsVO();
                        BeanUtils.copyProperties(goods, goodsVO);

                        GoodsTypes goodsTypes = goodsTypesDAO.selectByPrimaryKey(goods.getType());
                        if (!ObjectUtils.isEmpty(goodsTypes) && goodsTypes.getStatus() != 1) {
                            GoodsTypesVO goodsTypesVO = new GoodsTypesVO();
                            BeanUtils.copyProperties(goodsTypes, goodsTypesVO);
                            goodsVO.setType(goodsTypesVO);
                        }

                        Memory memory = memoryDAO.selectByPrimaryKey(goods.getMemory());
                        if (!ObjectUtils.isEmpty(memory) && memory.getStatus() != 1) {
                            MemoryVO memoryVO = new MemoryVO();
                            BeanUtils.copyProperties(memory, memoryVO);
                            goodsVO.setMemory(memoryVO);
                        }

                        vo.setGoods(goodsVO);
                    }

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
                        vo.setImgList(imgList);
                    }

                    return vo;
                })
                .collect(Collectors.toList());

        return new PageResult<>(pageRequest, voList);
    }


}