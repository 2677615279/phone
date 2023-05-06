package com.past.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.common.ResponseResult;
import com.past.domain.dto.UsersDTO;
import com.past.domain.entity.RolesUsers;
import com.past.domain.entity.Users;
import com.past.domain.vo.RolesVO;
import com.past.domain.vo.UsersVO;
import com.past.service.ExcelExportService;
import com.past.service.RolesService;
import com.past.service.RolesUsersService;
import com.past.service.UsersService;
import com.past.util.ShiroUtil;
import com.past.validation.InsertValidationGroup;
import com.past.validation.UpdateValidationGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.annotation.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户模块Controller
 */
@Controller
@Validated // 开启基础类型数据的校验
@Slf4j
@Api(tags = "UsersController", description = "用户管理Controller", protocols = "http, https", hidden = false) // 作用在类上，描述该类的信息（用途、接收xx协议的请求、该内容是否在页面上隐藏等等）
public class UsersController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private RolesUsersService rolesUsersService;

    @Autowired
    private RolesService rolesService;

    @Autowired
    private ExcelExportService excelExportService;


    /**
     * 用户注册(前台)
     * 当用户最终选择默认头像，点击注册按钮时，不会执行文件上传请求，校验合格后直接执行注册请求
     * 此时usersDTO中的img属性为null，在service层为其赋值默认头像，然后保存到数据库
     * 当用户最终选择自选头像，点击注册按钮时，会先执行文件上传请求，然后将响应结果中的数据写入表单一个新控件，校验合格后再执行注册请求
     * 此时usersDTO中的img属性不是null或""，而是上传成功的实际头像名称，在service层无需处理img属性，保存到数据库
     * @param usersDTO 待注册的用户DTO对象
     * @param passwordCheck 确认密码
     * @return ResponseResult
     */
    @PostMapping("/api/users/register")
    @ResponseBody
    @ApiOperation(value = "用户注册", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "用户注册成功"),
            @ApiResponse(code = -1, message = "用户注册失败")
    }) // 定义响应的所有信息
    public ResponseResult register(@Validated(value = {InsertValidationGroup.class}) UsersDTO usersDTO,
                                   @NotBlank(message = "注册时提供的确认密码既不能为null，还必须有除空字符外的实际字符！") String passwordCheck) {

        if (usersService.register(usersDTO, passwordCheck)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 用户登录 通过自定义凭证匹配器 自动校验状态、密码，同一账号超过5次登录失败，自动锁定账户
     * @param principal 用户主身份信息 可以是username或phone或email
     * @param credentials 用户凭证 即密码
     * @param rememberMe 是否选择 记住我 -> 注意，不是记住密码(记住密码还要执行登录操作)，而记住我 可以不执行登录认证 直接访问
     * @return ResponseResult
     */
    @PostMapping("/api/users/login")
    @ResponseBody
    @ApiOperation(value = "用户登录", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "用户登录成功"),
            @ApiResponse(code = -1, message = "用户登录失败")
    }) // 定义响应的所有信息
    public ResponseResult login(@NotBlank(message = "用户输入的主身份信息既不能为null，还必须有除空字符外的实际字符！") String principal,
                                @NotBlank(message = "用户输入的凭证既不能为null，还必须有除空字符外的实际字符！") String credentials,
                                boolean rememberMe) {

        try {
            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            AuthenticationToken token = new UsernamePasswordToken(principal, credentials, rememberMe);
            // 主体执行登录操作  会先去自定义CustomerRealm中执行认证流程 再去自定义RetryLimitHashedCredentialsMatcher中确认密码
            subject.login(token);
            // 设置shiro内置的session为当前窗口永不过期
            subject.getSession().setTimeout(-1000L);

            Users u = usersService.selectByPrincipal(principal);
            String username = u.getUsername();
            log.info("用户登录成功...");
            log.info("当前登录的用户：{}", username);

            return ResponseResult.success();
        } catch (UnknownAccountException | IncorrectCredentialsException e) {
            log.error("账号或密码错误！");
        } catch (LockedAccountException e) {
            log.error("账号已被锁定，请联系管理员！");
        }

        return ResponseResult.error("failure");
    }


    /**
     * 管理员登录 通过自定义凭证匹配器 自动校验状态、密码，同一账号超过5次登录失败，自动锁定账户
     * @param principal 用户主身份信息 可以是username或phone或email
     * @param credentials 用户凭证 即密码
     * @return ResponseResult
     */
    @PostMapping("/api/users/adminLogin")
    @ResponseBody
    @ApiOperation(value = "管理员登录", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "管理员登录成功"),
            @ApiResponse(code = -1, message = "管理员登录失败")
    }) // 定义响应的所有信息
    public ResponseResult adminLogin(@NotBlank(message = "用户输入的主身份信息既不能为null，还必须有除空字符外的实际字符！") String principal,
                                     @NotBlank(message = "用户输入的凭证既不能为null，还必须有除空字符外的实际字符！") String credentials) {

        try {
            if (usersService.confirmRole(principal)) {
                // 获取主体对象
                Subject subject = SecurityUtils.getSubject();
                AuthenticationToken token = new UsernamePasswordToken(principal, credentials);
                // 主体执行登录操作  会先去自定义CustomerRealm中执行认证流程 再去自定义RetryLimitHashedCredentialsMatcher中确认密码
                subject.login(token);
                // 设置shiro内置的session为当前窗口永不过期
                subject.getSession().setTimeout(-1000L);

                Users u = usersService.selectByPrincipal(principal);
                String username = u.getUsername();
                log.info("管理员登录成功...");
                log.info("当前登录的管理员：{}", username);

                return ResponseResult.success();
            }
            else {
                log.error("登录账户不具有管理员角色的身份...");
                return ResponseResult.error("failure");
            }
        } catch (UnknownAccountException | IncorrectCredentialsException e) {
            log.error("账号或密码错误！");
        } catch (LockedAccountException e) {
            log.error("账号已被锁定，请联系管理员！");
        }

        return ResponseResult.error("failure");
    }


    /**
     * 用户退出
     * @return ResponseResult
     */
    @PostMapping("/api/users/logout")
    @ResponseBody
    @RequiresUser
    @ApiOperation(value = "退出登录", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "退出登录成功"),
            @ApiResponse(code = -1, message = "退出登录失败")
    }) // 定义响应的所有信息
    public ResponseResult logout(){

        try {
            // 获取主体对象
            Subject subject = SecurityUtils.getSubject();
            Users login = (Users) subject.getPrincipal();
            String username = login.getUsername();

            // 执行退出操作
            subject.logout();
            // 清除缓存
            ShiroUtil.clearCache();

            log.info("{} 退出成功...", username);

            return ResponseResult.success();
        } catch (Exception e) {
            log.info("退出失败...");
        }

        return ResponseResult.error("failure");
    }


    /**
     * 删除用户 逻辑删除
     * @param id 待删除数据的主键id
     * @return 删除成功后重定向分页查询请求
     */
    @PostMapping("/api/users/deleteLogic")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"用户管理员", "角色管理员", "权限管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"users:delete:*", "users:update:*", "users:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台删除用户", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台删除用户成功"),
            @ApiResponse(code = -1, message = "后台删除用户失败")
    }) // 定义响应的所有信息
    public ResponseResult deleteLogic(@NotNull(message = "删除操作必须提供待删除数据的主键id！") Long id) {

        if (usersService.deleteLogic(id)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 后台修改用户
     * @param usersDTO 修改后的用户DTO实体
     * @return 修改成功后重定向分页查询请求
     */
    @PostMapping("/api/users/update")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"用户管理员", "角色管理员", "权限管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"users:update:*", "users:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台修改用户", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台修改用户成功"),
            @ApiResponse(code = -1, message = "后台修改用户失败")
    }) // 定义响应的所有信息
    public ResponseResult update(@Validated(value = UpdateValidationGroup.class) UsersDTO usersDTO) {

        if (usersService.update(usersDTO)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 登录的用户 修改自己的信息
     * @param usersDTO 修改后的用户DTO实体
     * @return ResponseResult
     */
    @PostMapping("/api/users/updateSelf")
    @ResponseBody
    @RequiresUser
    @ApiOperation(value = "登录者修改自己的信息", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "登录者修改自己的信息成功"),
            @ApiResponse(code = -1, message = "登录者修改自己的信息失败")
    }) // 定义响应的所有信息
    public ResponseResult updateSelf(@Validated(value = UpdateValidationGroup.class) UsersDTO usersDTO) {

        if (usersService.updateBySelf(usersDTO)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 登录的用户 修改自己的密码
     * @param usersDTO 修改后的用户DTO实体
     * @param newPassword 新密码
     * @param newPasswordCheck 确认新密码
     * @return ResponseResult
     */
    @PostMapping("/api/users/updatePassword")
    @ResponseBody
    @RequiresUser
    @ApiOperation(value = "登录者修改自己的密码", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "登录者修改自己的密码成功"),
            @ApiResponse(code = -1, message = "登录者修改自己的密码失败")
    }) // 定义响应的所有信息
    public ResponseResult updatePassword(@Validated(value = UpdateValidationGroup.class) UsersDTO usersDTO,
                                         @NotBlank String newPassword,
                                         @NotBlank String newPasswordCheck) {

        if (usersService.updatePassword(usersDTO, newPassword, newPasswordCheck)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 条件分页查询用户列表
     * 因为使用layui表格的异步渲染，为符合layui的要求，将pageNum、pageSize的请求参数设为page、limit
     * @param pageNum 页码
     * @param pageSize 每页显示数目
     * @param search 模糊查询的条件
     * @return JSONObject  因为layui表格异步渲染 默认需要返回固定格式 {code:xxx, msg:xxx, count:xxx, data:[]}
     */
    @PostMapping("/api/users/list")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"用户管理员", "角色管理员", "权限管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"users:select:*", "users:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台条件查询用户分页列表", response = JSONObject.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    public JSONObject list(@RequestParam(value = "page", defaultValue = "1", required = false) Integer pageNum,
                           @RequestParam(value = "limit", defaultValue = "10", required = false) Integer pageSize,
                           @RequestParam(value = "search", required = false) String search) {

        JSONObject obj = new JSONObject();

        PageRequest<UsersDTO> pageRequest = new PageRequest<>();
        pageRequest.setPageNum(pageNum);
        pageRequest.setPageSize(pageSize);
        if (search != null && !"".equals(search.trim())) {

            UsersDTO usersDTO = new UsersDTO();
            search = search.trim();
            // 如果查询条件是邮箱：有@关键字符
            if (usersService.checkEmail(search)) {
                usersDTO.setEmail(search);
            }
            // 如果查询条件是手机号：以1打头且不含@关键字符，且全部是[0-9]区间内的数字
            else if (usersService.checkPhone(search)) {
                usersDTO.setPhone(search);
            }
            else {
                usersDTO.setUsername(search);
            }
            pageRequest.setQuery(usersDTO);
        }

        PageResult<UsersVO> pageResult = usersService.selectPage(pageRequest);

        obj.put("code", 0);
        obj.put("msg", "");
        obj.put("count",pageResult.getTotal());
        obj.put("data", pageResult.getData());

        return obj;
    }


    /**
     * 更新用户状态
     * @param id 被更新用户的 主键id
     * @param status 更新后的状态
     * @return ResponseResult
     */
    @PostMapping("/api/users/changeStatus")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"用户管理员", "角色管理员", "权限管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"users:update:*", "users:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台修改用户状态", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台修改用户状态成功"),
            @ApiResponse(code = -1, message = "后台修改用户状态失败")
    }) // 定义响应的所有信息
    public ResponseResult changeStatus(@NotNull(message = "更新状态必须提供被更新者的主键id") Long id,
                                       @NotNull(message = "更新状态必须提供新的状态") Integer status){

        if (usersService.changeStatus(status, id)) {

            ShiroUtil.clearCache();
            return ResponseResult.success();
        }

        ShiroUtil.clearCache();
        return ResponseResult.error("failure");
    }


    /**
     * 多选数据  批量删除用户
     * @param batchId 是所有被选中行的用户主键id，以","分隔开 组成的字符串。如"1,2,3,"
     * @return ResponseResult
     */
    @PostMapping("/api/users/batchDelete")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"用户管理员", "角色管理员", "权限管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"users:delete:*", "users:update:*", "users:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台批量删除用户", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台批量删除用户成功"),
            @ApiResponse(code = -1, message = "后台批量删除用户失败")
    }) // 定义响应的所有信息
    public ResponseResult batchDelete(@NotBlank String batchId) {

        // 以,分隔成存储多个主键id的 数组
        String[] array = batchId.split(",");
        boolean flag = true;
        for (String id : array) {
            boolean delete = usersService.deleteLogic(Long.valueOf(id));
            // 只要有一个删除失败  flag赋值false
            if (!delete) {
                flag = false;
            }
        }

        if (flag) {
            ShiroUtil.clearCache();
            return ResponseResult.success();
        }
        else {
            ShiroUtil.clearCache();
            return ResponseResult.error("failure");
        }
    }


    /**
     * 根据用户主键id 获取 已分配的角色 和 未分配的角色
     * @param id 用户主键id
     * @return Map
     */
    @PostMapping("/api/users/selectAssign")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"用户管理员", "角色管理员", "权限管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"roles:*:*", "users:update:*", "users:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台查询用户已分配的角色和未分配的角色", response = Map.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    public Map<String, Object> selectAssign(@NotNull Long id) {

        Map<String, Object> map = Maps.newHashMap();
        // 根据用户主键id 查询中间表 涉及该用户的正常关联数据的角色id，并存入一个集合
        List<Long> roleIds = rolesUsersService.selectByUserId(id)
                .stream()
                .filter(ru -> ru.getStatus() == 0)
                .map(RolesUsers::getRoleId)
                .collect(Collectors.toList());
        List<RolesVO> assign = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(roleIds)) {
            // 根据存储角色主键id的集合，查询并过滤出 正常的角色，并存入一个集合。即存储着该用户分配的所有正常角色
            assign = rolesService.selectByIds(roleIds)
                    .stream()
                    .filter(r -> r.getStatus() == 0)
                    .map(r -> {
                        RolesVO vo = new RolesVO();
                        BeanUtils.copyProperties(r, vo);
                        return vo;
                    })
                    .collect(Collectors.toList());
        }
        // 查询角色表全部数据 过滤出 正常的角色，并存入一个集合。即存储着角色表中所有正常角色
        List<RolesVO> all = rolesService.selectAll()
                .stream()
                .filter(vo -> vo.getStatus() == 0)
                .collect(Collectors.toList());

        // 使存储角色表中全部正常角色的集合，删除 其中该用户分配的所有正常角色的集合。
        // 整体删除部分，剩下的是存储着该用户尚未分配的正常角色的集合
        all.removeAll(assign);

        // 将该用户 已分配的所有正常角色 和 尚未分配所有正常角色 存入一个map，响应回客户端
        map.put("assign", assign);
        map.put("unassign", all);

        return map;
    }


    /**
     * 根据用户主键id 为用户分配角色：删除旧角色 添加新角色
     * @param id 用户主键id
     * @param roleIds 更新分配后的 新角色的主键id字符串，以逗号分隔  如"1,2,3,"
     * @return ResponseResult
     */
    @PostMapping("/api/users/assign")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"用户管理员", "角色管理员", "权限管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"roles:*:*", "users:update:*", "users:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台为用户分配角色", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台为用户分配角色成功"),
            @ApiResponse(code = -1, message = "后台为用户分配角色失败")
    }) // 定义响应的所有信息
    public ResponseResult assign(@NotNull Long id, @NotNull String roleIds) {

        String[] array = roleIds.split(",");
        // 存储前端页面右侧已分配角色主键id的集合
        List<Long> ids = Lists.newArrayList();
        // 如果提交前  前端页面右侧已分配角色栏有值，则遍历再依次添加到声明的ids集合中
        if (!StringUtils.isEmpty(roleIds) && roleIds.contains(",")) {
            for (String s : array) {
                ids.add(Long.valueOf(s));
            }
        }

        // 传过来的roleIds还是之前本已分配的，代表分配的角色并无做改变，此时不用其他操作直接响应返回即可

        // 根据用户主键id 查询中间表 涉及该用户的正常关联数据的角色id，并存入一个集合
        List<Long> roleIdss = rolesUsersService.selectByUserId(id)
                .stream()
                .filter(ru -> ru.getStatus() == 0)
                .map(RolesUsers::getRoleId)
                .collect(Collectors.toList());
        // 存储着该用户原先已分配正常角色的集合
        List<RolesVO> assign = Lists.newArrayList();
        // 存储着该用户原先已分配正常角色主键id的集合
        List<Long> assignIds = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(roleIdss)) {
            // 根据存储角色主键id的集合，查询并过滤出 正常的角色，并存入一个集合。即存储着该用户原先分配的所有正常角色
            assign = rolesService.selectByIds(roleIdss)
                    .stream()
                    .filter(r -> r.getStatus() == 0)
                    .map(r -> {
                        RolesVO vo = new RolesVO();
                        BeanUtils.copyProperties(r, vo);
                        return vo;
                    })
                    .collect(Collectors.toList());
        }
        if (!CollectionUtils.isEmpty(assign)) {
            // 将存储着该用户原先已分配正常角色的集合 映射成 存储着该用户原先已分配正常角色主键id的集合
            assignIds = assign.stream().map(RolesVO::getId).collect(Collectors.toList());
        }

        // 使存储着该用户原先已分配正常角色主键id的集合 删除 页面表单提交的角色id字符串所转换成的集合
        // 删除后 如果原先已分配正常角色主键id的集合为空，则代表提交前后 分配的角色并无做改变，此时无需操作 直接响应返回即可
        if (assignIds.size() >= ids.size()) {
            assignIds.removeAll(ids);
            if (assignIds.isEmpty()) {
                ShiroUtil.clearCache();
                return ResponseResult.success();
            }
        }

        // 根据用户主键id 删除其所有的角色用户关联数据
        if (!rolesUsersService.deleteLogicByUserId(id)) {

            ShiroUtil.clearCache();
            return ResponseResult.error("failure");
        }

        // 如果ids为空，代表前端页面清空了已有角色，则只删除已分配的所有角色即可；否则还要执行批量新增操作
        if (CollectionUtils.isEmpty(ids)) {

            ShiroUtil.clearCache();
            return ResponseResult.success();
        }
        else {
            // 为该用户 批量添加角色
            if (!rolesUsersService.insert(id, ids)) {

                ShiroUtil.clearCache();
                return ResponseResult.error("failure");
            }
            else {
                ShiroUtil.clearCache();
                return ResponseResult.success();
            }
        }
    }


    /**
     * 后台导出用户数据
     * @return ResponseResult
     */
    @PostMapping("/api/users/export")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"用户管理员", "角色管理员", "权限管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"roles:*:*", "permissions:*:*", "users:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台导出用户数据", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台导出用户数据成功")
    }) // 定义响应的所有信息
    public ResponseResult export() {

        excelExportService.asyncExportUsers();

        return ResponseResult.success();
    }


    /**
     * 将请求转发到/user/usercenter.jsp
     * @return /user/usercenter
     */
    @GetMapping("/user/center")
    @RequiresUser
    @ApiOperation(value = "前台登录用户将请求转发到用户中心页面", httpMethod = "GET") // 作用在方法上，描述方法的用途
    public String usercenter() {

        return "/user/usercenter";
    }


    /**
     * 将请求转发到/user/shoppingcart.jsp
     * @return /user/shoppingcart
     */
    @GetMapping("/user/cart")
    @RequiresUser
    @ApiOperation(value = "前台登录用户将请求转发到购物车页面", httpMethod = "GET") // 作用在方法上，描述方法的用途
    public String usercart() {

        return "/user/shoppingcart";
    }


}
