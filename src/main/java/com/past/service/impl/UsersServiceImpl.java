package com.past.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.dao.RolesDAO;
import com.past.dao.RolesUsersDAO;
import com.past.dao.UsersDAO;
import com.past.domain.dto.UsersDTO;
import com.past.domain.entity.Roles;
import com.past.domain.entity.RolesUsers;
import com.past.domain.entity.Users;
import com.past.domain.vo.UsersVO;
import com.past.exception.ParamException;
import com.past.service.UsersService;
import com.past.util.BeanValidator;
import com.past.util.IpUtil;
import com.past.util.SaltUtil;
import com.past.util.ShiroUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 用户模块 业务层接口的实现类
 */
@Service
@Transactional
@Slf4j
public class UsersServiceImpl implements UsersService {

    @Autowired
    private UsersDAO usersDAO;

    @Autowired
    private RolesDAO rolesDAO;

    @Autowired
    private RolesUsersDAO rolesUsersDAO;


    /**
     * 根据主键id 查询用户数据
     * @param id 用户 主键id
     * @return 一个UsersVO对象
     */
    @Override
    public UsersVO selectByPrimaryKey(Long id) {

        Users u = usersDAO.selectByPrimaryKey(id);

        if (u == null || u.getStatus() == 1) {
            throw new NullPointerException("用户不存在！");
        }

        UsersVO vo = new UsersVO();
        BeanUtils.copyProperties(u, vo);

        return vo;
    }

    /**
     * 根据账号 查询一条用户数据
     * @param principal 用户主身份信息 可以是username或phone或email
     * @return 一个Users对象
     */
    @Override
    public Users selectByPrincipal(String principal) {

        Users u = usersDAO.selectByPrincipal(principal);

        if (u == null || u.getStatus() == 1) {
            return null;
        }

        return u;
    }


    /**
     * 判断该用户的用户名是否存在，存在返回true 抛出异常；不存在返回false，继续执行业务
     * @param username 用户名
     * @param id 用户 主键id
     * @return 存在返回true 抛出异常；不存在返回false，继续执行业务
     */
    @Override
    public boolean checkUsernameExist(String username, Long id) {

        return usersDAO.countByUsername(username, id) > 0;
    }


    /**
     * 判断该用户的邮箱是否存在，存在返回true 抛出异常；不存在返回false，继续执行业务
     * @param email 邮箱
     * @param id 用户 主键id
     * @return 存在返回true 抛出异常；不存在返回false，继续执行业务
     */
    @Override
    public boolean checkEmailExist(String email, Long id) {

        return usersDAO.countByEmail(email, id) > 0;
    }


    /**
     * 判断该用户的手机号是否存在，存在返回true 抛出异常；不存在返回false，继续执行业务
     * @param phone 手机号
     * @param id 用户 主键id
     * @return 存在返回true 抛出异常；不存在返回false，继续执行业务
     */
    @Override
    public boolean checkPhoneExist(String phone, Long id) {

        return usersDAO.countByPhone(phone, id) > 0;
    }


    /**
     * 前台注册用户
     * @param usersDTO 待新增的用户DTO对象
     * @param passwordCheck 确认密码
     * @return 新增后，这条数据的主键id > 0 ? true : false
     */
    @Override
    public boolean register(UsersDTO usersDTO, String passwordCheck) {

        // 校验对象参数 是否满足注解约束  不满足约束，抛出ParamException
        BeanValidator.check(usersDTO);

        usersDTO.setUsername(usersDTO.getUsername().trim());
        usersDTO.setPassword(usersDTO.getPassword().trim());
        passwordCheck = passwordCheck.trim();
        if (checkUsernameExist(usersDTO.getUsername(), usersDTO.getId())) {
            throw new ParamException("用户名已存在且被他人占用，请更换用户名！");
        }
        if (checkPhoneExist(usersDTO.getPhone(), usersDTO.getId())) {
            throw new ParamException("手机号已存在且被他人占用，请更换手机号！");
        }
        if (checkEmailExist(usersDTO.getEmail(), usersDTO.getId())) {
            throw new ParamException("邮箱已存在且被他人占用，请更换邮箱！");
        }

        if (!usersDTO.getPassword().equals(passwordCheck)) {
            return false;
        }

        // 生成8位随机盐
        String salt = SaltUtil.getSalt(8);
        // 将随机盐设置到用户属性内，存入数据库
        usersDTO.setSalt(salt);
        // 对用户原始明文密码使用生成的8位随机盐，进行1024次散列的md5加密
        Md5Hash md5Hash = new Md5Hash(usersDTO.getPassword(), salt, 1024);
        // 将加密后的32位密文密码设置到用户属性内，存入数据库
        usersDTO.setPassword(md5Hash.toHex());

        Users users = new Users();
        // 拷贝属性
        BeanUtils.copyProperties(usersDTO, users);
        users.setCreateTime(new Date());
        users.setUpdateTime(new Date());

        // 没有上传头像，给个默认头像；上传了头像，则usersDTO中会有 拷贝后 users中也会有
        if (StringUtils.isEmpty(users.getImg())) {
            users.setImg("测试上传.jpg");
        }

        users.setOperator(users.getUsername());
        users.setOperatorIp(IpUtil.getLocalIP());

        return usersDAO.insertSelective(users) > 0;
    }


    /**
     * 根据账号 查询所属用户 确认该用户的角色  管理员登录前的必要工作
     * @param principal 用户主身份信息 可以是username或phone或email
     * @return 拥有任意一个管理员角色返回true 否则false
     */
    @Override
    public boolean confirmRole(String principal) {

        Users u = usersDAO.selectByPrincipal(principal);

        // 如果用户不存在抛出空指针异常，否则继续执行
        Preconditions.checkNotNull(u, "查找的用户不存在！");

        // 存储角色用户关联的集合
        List<RolesUsers> rolesUsersList = rolesUsersDAO.selectByUserId(u.getId());
        // 存储从角色用户关联中映射的角色id的集合
        List<Long> roleIds = Lists.newArrayList();
        // 存储用户所分配的角色的集合、未过滤
        List<Roles> rolesList = Lists.newArrayList();
        // 存储用户所分配的角色的集合、已过滤
        List<Roles> rolesListFilter = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(rolesUsersList)) {
            roleIds = rolesUsersList.stream()
                    .filter(ru -> ru.getStatus() == 0)
                    .map(RolesUsers::getRoleId)
                    .collect(Collectors.toList());
        }
        if (!CollectionUtils.isEmpty(roleIds)) {
            rolesList = rolesDAO.selectByIds(roleIds);
        }
        if (!CollectionUtils.isEmpty(rolesList)) {
            rolesListFilter = rolesList.stream()
                    .filter(r -> r.getStatus() == 0)
                    .collect(Collectors.toList());
        }

        // 只要有任意一个元素匹配返回true 当没有与其匹配的元素时返回false  stream流的anyMatch操作  短路 终端操作 操作后该流不能继续使用
        return rolesListFilter.stream().anyMatch(r -> r.getName().contains("管理员"));
    }


    /**
     * 后台删除用户 物理删除
     * @param id 待删除数据的主键id
     * @return 删除成功(数据库受影响的行数>0)返回true
     */
    @Override
    public boolean deletePhysical(Long id) {

        Users u = usersDAO.selectByPrimaryKey(id);

        // 使用google.guava工具包校验待删除的用户是否为空 不存在用户，抛出异常。存在用户，继续执行代码
        Preconditions.checkNotNull(u,"待删除的用户不存在！");

        return usersDAO.deleteByPrimaryKey(id) > 0;
    }


    /**
     * 后台删除用户 逻辑删除
     * @param id 待删除数据的主键id
     * @return 删除成功，即将此用户数据的状态改为1(删除)，返回true
     */
    @Override
    public boolean deleteLogic(Long id) {

        Users u = usersDAO.selectByPrimaryKey(id);

        // 使用google.guava工具包校验待删除的用户是否为空 不存在用户，抛出异常。存在用户，继续执行代码
        Preconditions.checkNotNull(u,"待删除的用户不存在！");

        // 如果待删除用户的状态 不是删除状态，则执行删除
        if (u.getStatus() != 1) {
            u.setStatus(1);
            u.setUpdateTime(new Date());

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();

            u.setOperator(login.getUsername());
            u.setOperatorIp(IpUtil.getLocalIP());

            // 不能删除正在登录的自己
            if (u.getId().equals(login.getId())) {
                log.error("已登录的管理员不能删除属于当前自己的账户！");
                return false;
            }

            return usersDAO.updateByPrimaryKeySelective(u) > 0;
        }

        return false;
    }


    /**
     * 后台更新用户
     * @param usersDTO 更新后的用户DTO实体对象
     * @return 更新成功true 失败false
     */
    @Override
    public boolean update(UsersDTO usersDTO) {

        // 校验对象参数 是否满足注解约束  不满足约束，抛出ParamException
        BeanValidator.check(usersDTO);

        usersDTO.setUsername(usersDTO.getUsername().trim());
        if (checkUsernameExist(usersDTO.getUsername(), usersDTO.getId())) {
            throw new ParamException("用户名已存在且被他人占用，请更换用户名！");
        }
        if (checkPhoneExist(usersDTO.getPhone(), usersDTO.getId())) {
            throw new ParamException("手机号已存在且被他人占用，请更换手机号！");
        }
        if (checkEmailExist(usersDTO.getEmail(), usersDTO.getId())) {
            throw new ParamException("邮箱已存在且被他人占用，请更换邮箱！");
        }

        Users before = usersDAO.selectByPrimaryKey(usersDTO.getId());

        // 使用google.guava工具包校验待更新的用户是否为空，不存在用户，抛出异常。存在用户，继续执行代码
        Preconditions.checkNotNull(before,"待更新的用户不存在");

        // 当查询所得的待更新用户不为空 并且状态不是删除状态，才可更新
        if (before.getStatus() != 1) {
            Users after = new Users();
            BeanUtils.copyProperties(usersDTO, after);
            after.setStatus(before.getStatus());
            after.setUpdateTime(new Date());

            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();
            after.setOperator(login.getUsername());
            after.setOperatorIp(IpUtil.getLocalIP());

            // 如果在后台表格更新是当前登录者自己的信息，则刷新subject存储的principal信息
            if (after.getId().equals(login.getId())) {
                ShiroUtil.refreshPrincipal(subject, before, after);
            }

            return usersDAO.updateByPrimaryKeySelective(after) > 0;
        }

        return false;
    }


    /**
     * 登录的用户 更新自己的信息
     * @param usersDTO 更新后的用户DTO实体
     * @return 更新成功true 失败false
     */
    @Override
    public boolean updateBySelf(UsersDTO usersDTO) {

        // 校验对象参数 是否满足注解约束  不满足约束，抛出ParamException
        BeanValidator.check(usersDTO);

        usersDTO.setUsername(usersDTO.getUsername().trim());
        if (checkUsernameExist(usersDTO.getUsername(), usersDTO.getId())) {
            throw new ParamException("用户名已存在且被他人占用，请更换用户名！");
        }
        if (checkPhoneExist(usersDTO.getPhone(), usersDTO.getId())) {
            throw new ParamException("手机号已存在且被他人占用，请更换手机号！");
        }
        if (checkEmailExist(usersDTO.getEmail(), usersDTO.getId())) {
            throw new ParamException("邮箱已存在且被他人占用，请更换邮箱！");
        }

        Users before = usersDAO.selectByPrimaryKey(usersDTO.getId());

        // 使用google.guava工具包校验待更新的用户是否为空，不存在用户，抛出异常。存在用户，继续执行代码
        Preconditions.checkNotNull(before,"待更新的用户不存在");

        Users after = new Users();
        BeanUtils.copyProperties(usersDTO, after);

        // 获取主体对象
        Subject subject = SecurityUtils.getSubject();

        // 判断状态是否正常
        if (before.getStatus() == 0) {
            // 没有上传新头像，还是原头像；上传了新头像，则usersDTO中会有 拷贝后 after中也会有
            after.setUpdateTime(new Date());
            after.setOperator(before.getUsername());
            after.setOperatorIp(IpUtil.getLocalIP());

            // 刷新subject存储的principal信息
            ShiroUtil.refreshPrincipal(subject, before, after);

            return usersDAO.updateByPrimaryKeySelective(after) > 0;
        }

        return false;
    }


    /**
     * 登录的用户 更新自己的密码
     * @param usersDTO 更新后的用户DTO实体
     * @param newPassword 新密码
     * @param newPasswordCheck 确认新密码
     * @return 更新成功true 失败false
     */
    @Override
    public boolean updatePassword(UsersDTO usersDTO, String newPassword, String newPasswordCheck) {

        // 校验对象参数 是否满足注解约束  不满足约束，抛出ParamException
        BeanValidator.check(usersDTO);

        Users before = usersDAO.selectByPrimaryKey(usersDTO.getId());

        // 使用google.guava工具包校验待更新的用户是否为空，不存在用户，抛出异常。存在用户，继续执行代码
        Preconditions.checkNotNull(before,"待更新的用户不存在");

        // 判断状态是否正常
        if (before.getStatus() == 0) {
            if (StringUtils.isEmpty(usersDTO.getPassword().trim())) {
                return false;
            }
            if (StringUtils.isEmpty(newPassword.trim())) {
                return false;
            }
            if (StringUtils.isEmpty(newPasswordCheck.trim())) {
                return false;
            }

            // 前台所输原密码 和 新密码相同  返回false
            if (usersDTO.getPassword().trim().equals(newPassword.trim())) {
                return false;
            }
            // 前台所输新密码 和 确认新密码不相同  返回false
            if (!newPassword.trim().equals(newPasswordCheck.trim())) {
                return false;
            }

            // 原密码加密后 和 数据库保存的原始加密密码不相同  返回false
            String salt = before.getSalt();
            Md5Hash md5Hash = new Md5Hash(usersDTO.getPassword().trim(), salt, 1024);
            if (!md5Hash.toHex().equals(before.getPassword())) {
                return false;
            }

            // 新密码加密后 和 数据库保存的原始加密密码相同  返回false
            Md5Hash md5HashNew = new Md5Hash(newPassword.trim(), salt, 1024);
            if (md5HashNew.toHex().equals(before.getPassword())) {
                return false;
            }

            usersDTO.setPassword(md5HashNew.toHex());
            Users after = new Users();
            BeanUtils.copyProperties(usersDTO, after);

            after.setUpdateTime(new Date());
            after.setOperator(before.getUsername());
            after.setOperatorIp(IpUtil.getLocalIP());

            return usersDAO.updateByPrimaryKeySelective(after) > 0;
        }

        return false;
    }


    /**
     * 后台查询全部用户数据(过滤掉状态为1 即删除) 的数据
     * @return 符合条件的结果集
     */
    @Override
    public List<UsersVO> selectAll() {

        List<Users> all = usersDAO.selectAll();

        // 过滤掉删除状态的数据 再转换实体
        return Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(u -> u.getStatus() != 1)
                .map(u -> {
                    UsersVO vo = new UsersVO();
                    BeanUtils.copyProperties(u, vo);
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
    public PageResult<UsersVO> selectPage(PageRequest<UsersDTO> pageRequest) {

        List<Users> all = usersDAO.selectByPrincipalContaining(pageRequest);

        List<UsersVO> voList = Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(u -> u.getStatus() != 1)
                .map(u -> {
                    UsersVO vo = new UsersVO();
                    BeanUtils.copyProperties(u, vo);
                    return vo;
                })
                .collect(Collectors.toList());

        // 通过分页结果实体类的有参构造，传入分页请求实例和待分页的源集合
        return new PageResult<>(pageRequest, voList);
    }


    /**
     * 判断字符串是否是邮箱
     * @param search 字符串
     * @return 是true 否false
     */
    @Override
    public boolean checkEmail(String search) {

        return search.contains("@");
    }


    /**
     * 判断字符串是否是手机号
     * @param search 字符串
     * @return 是true 否false
     */
    @Override
    public boolean checkPhone(String search) {

        if (search.startsWith("1") && !search.contains("@")) {
            // [0,9]区间的数字 出现0次或多次
            return search.matches("^[0-9]*$");
        }

        return false;
    }


    /**
     * 根据用户主键id 改变用户状态
     * @param status 状态 0正常 2禁用
     * @param id 用户 主键 id
     * @return 更新后，数据库受影响的行数 > 0 ? true : false
     */
    @Override
    public boolean changeStatus(Integer status, Long id) {

        Users users = usersDAO.selectByPrimaryKey(id);

        if (ObjectUtils.isEmpty(users) || users.getStatus() == 1) {
            return false;
        }

        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        Date updateTime = new Date();
        String operator = login.getUsername();
        String operatorIp = IpUtil.getLocalIP();

        // 不能禁用解禁正在登录的自己
        if (id.equals(login.getId())) {
            log.error("已登录的管理员不能禁用或解禁属于当前自己的账户！");
            return false;
        }

        return usersDAO.changeStatus(status, id, updateTime, operator, operatorIp) > 0;
    }


}
