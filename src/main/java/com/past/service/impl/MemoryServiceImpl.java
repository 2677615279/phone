package com.past.service.impl;

import com.google.common.base.Preconditions;
import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.dao.MemoryDAO;
import com.past.domain.dto.MemoryDTO;
import com.past.domain.entity.Memory;
import com.past.domain.entity.Users;
import com.past.domain.vo.MemoryVO;
import com.past.exception.ParamException;
import com.past.service.MemoryService;
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
 * 内存模块 业务层接口的实现类
 */
@Service
@Transactional
@Slf4j
public class MemoryServiceImpl implements MemoryService {

    @Autowired
    private MemoryDAO memoryDAO;

    /**
     * 注意：以注解方式，这里只能使用@Resource(name = "myRedisTemplate")注入
     * 因为我在自定义RedisConfig类中定义了一个方法 将未指定泛型的RedisTemplate添加到bean中，bean的名称即myRedisTemplate
     * 而我在此处注入的时候，添加了泛型，所以不能单独使用@Autowired(按类型注入)或@Autowired、@Qualifier(value = "myRedisTemplate")组合使用
     */
    @Resource(name = "myRedisTemplate")
    private RedisTemplate<String, Memory> redisTemplate;


    /**
     * 根据主键id 查询内存数据
     * @param id 内存 主键id
     * @return 一个MemoryVO对象
     */
    @Override
    public MemoryVO selectByPrimaryKey(Long id) {

        Memory memory = memoryDAO.selectByPrimaryKey(id);

        if (memory == null || memory.getStatus() == 1) {
            throw new NullPointerException("查找的内存不存在！");
        }

        MemoryVO vo = new MemoryVO();
        BeanUtils.copyProperties(memory, vo);

        return vo;
    }


    /**
     * 判断该内存名是否存在，存在返回true 抛出异常；不存在返回false，继续执行业务
     * @param name 内存名称
     * @param id 内存 主键id
     * @return 存在返回true 抛出异常；不存在返回false，继续执行业务
     */
    @Override
    public boolean checkNameExist(String name, Long id) {

        return memoryDAO.countByName(name, id) > 0;
    }


    /**
     * 后台新增内存
     * @param memoryDTO 待新增的内存DTO实体
     * @return 新增后，这条数据的主键id > 0 ? true : false
     */
    @Override
    public boolean insert(MemoryDTO memoryDTO) {

        // 校验对象参数 是否满足注解约束  不满足约束，抛出ParamException
        BeanValidator.check(memoryDTO);

        memoryDTO.setName(memoryDTO.getName().trim());
        if (checkNameExist(memoryDTO.getName(), memoryDTO.getId())) {
            throw new ParamException("内存名称已存在，请更换内存名称！");
        }

        Memory memory = new Memory();
        BeanUtils.copyProperties(memoryDTO, memory);
        memory.setCreateTime(new Date());
        memory.setUpdateTime(new Date());

        // 获取主体对象
        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        memory.setOperator(login.getUsername());
        memory.setOperatorIp(IpUtil.getLocalIP());

        return memoryDAO.insertSelective(memory) > 0;
    }


    /**
     * 后台删除内存 物理删除
     * @param id 待删除数据的主键id
     * @return 删除成功(数据库受影响的行数>0)返回true
     */
    @Override
    public boolean deletePhysical(Long id) {

        Memory memory = memoryDAO.selectByPrimaryKey(id);

        // 使用google.guava工具包校验待删除的内存是否为空 不存在内存，抛出异常。存在内存，继续执行代码
        Preconditions.checkNotNull(memory,"待删除的内存不存在！");

        return memoryDAO.deleteByPrimaryKey(id) > 0;
    }


    /**
     * 后台删除内存 逻辑删除
     * @param id 待删除数据的主键id
     * @return 删除成功，即将此内存数据的状态改为1(删除)，返回true
     */
    @Override
    public boolean deleteLogic(Long id) {

        Memory memory = memoryDAO.selectByPrimaryKey(id);

        // 使用google.guava工具包校验待删除的内存是否为空 不存在内存，抛出异常。存在内存，继续执行代码
        Preconditions.checkNotNull(memory,"待删除的内存不存在！");

        // 如果待删除内存的状态 不是删除状态，则执行删除
        if (memory.getStatus() != 1) {
            memory.setStatus(1);
            memory.setUpdateTime(new Date());

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();

            memory.setOperator(login.getUsername());
            memory.setOperatorIp(IpUtil.getLocalIP());

            return memoryDAO.updateByPrimaryKeySelective(memory) > 0;
        }

        return false;
    }


    /**
     * 后台更新内存
     * @param memoryDTO 更新后的内存DTO实体对象
     * @return 更新成功true 失败false
     */
    @Override
    public boolean update(MemoryDTO memoryDTO) {

        // 校验对象参数 是否满足注解约束  不满足约束，抛出ParamException
        BeanValidator.check(memoryDTO);

        memoryDTO.setName(memoryDTO.getName().trim());
        if (checkNameExist(memoryDTO.getName(), memoryDTO.getId())) {
            throw new ParamException("内存名称已存在，请更换内存名称！");
        }

        Memory before = memoryDAO.selectByPrimaryKey(memoryDTO.getId());

        // 使用google.guava工具包校验待更新的内存是否为空 不存在内存，抛出异常。存在内存，继续执行代码
        Preconditions.checkNotNull(before,"待更新的内存不存在！");

        // 当查询所得的待更新内存不为空 并且状态不是删除状态，才可更新
        if (before.getStatus() != 1) {
            Memory after = new Memory();
            BeanUtils.copyProperties(memoryDTO, after);
            after.setStatus(before.getStatus());
            after.setUpdateTime(new Date());

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();

            after.setOperator(login.getUsername());
            after.setOperatorIp(IpUtil.getLocalIP());

            return memoryDAO.updateByPrimaryKeySelective(after) > 0;
        }

        return false;
    }


    /**
     * 后台查询全部内存数据(过滤掉状态为1 即删除) 的数据
     * @return 符合条件的结果集
     */
    @Override
    public List<MemoryVO> selectAll() {

        ListOperations<String, Memory> listOperations = redisTemplate.opsForList();

        List<Memory> all = listOperations.range("memorys", 0, -1);

        if (CollectionUtils.isEmpty(all)) {

            all = memoryDAO.selectAll();
            all.forEach(m -> listOperations.rightPush("memorys", m));
        }

        return Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(m -> m.getStatus() != 1)
                .map(m -> {
                    MemoryVO vo = new MemoryVO();
                    BeanUtils.copyProperties(m, vo);
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
    public PageResult<MemoryVO> selectPage(PageRequest<MemoryDTO> pageRequest) {

        List<Memory> all = memoryDAO.selectByNameContaining(pageRequest);

        List<MemoryVO> voList = Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(m -> m.getStatus() != 1)
                .map(m -> {
                    MemoryVO vo = new MemoryVO();
                    BeanUtils.copyProperties(m, vo);
                    return vo;
                })
                .collect(Collectors.toList());

        return new PageResult<>(pageRequest, voList);
    }


    /**
     * 根据内存主键id 改变内存状态
     * @param status 状态 0正常 2禁用
     * @param id 内存 主键 id
     * @return 更新后，数据库受影响的行数 > 0 ? true : false
     */
    @Override
    public boolean changeStatus(Integer status, Long id) {

        Memory memory = memoryDAO.selectByPrimaryKey(id);

        if (ObjectUtils.isEmpty(memory) || memory.getStatus() == 1) {
            return false;
        }

        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        Date updateTime = new Date();
        String operator = login.getUsername();
        String operatorIp = IpUtil.getLocalIP();

        return memoryDAO.changeStatus(status, id, updateTime, operator, operatorIp) > 0;
    }


}
