package com.past.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.common.ResponseResult;
import com.past.domain.dto.RolesDTO;
import com.past.domain.entity.RolesPermissions;
import com.past.domain.vo.PermissionsVO;
import com.past.domain.vo.RolesVO;
import com.past.service.PermissionsService;
import com.past.service.RolesPermissionsService;
import com.past.service.RolesService;
import com.past.util.ShiroUtil;
import com.past.validation.InsertValidationGroup;
import com.past.validation.UpdateValidationGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 角色模块Controller
 */
@Controller
@RequestMapping("/api/roles")
@Validated // 开启基础类型数据的校验
@Slf4j
@Api(tags = "RolesController", description = "角色管理Controller", protocols = "http, https", hidden = false) // 作用在类上，描述该类的信息（用途、接收xx协议的请求、该内容是否在页面上隐藏等等）
public class RolesController {

    @Autowired
    private RolesService rolesService;

    @Autowired
    private RolesPermissionsService rolesPermissionsService;

    @Autowired
    private PermissionsService permissionsService;


    /**
     * 后台添加角色
     * @param rolesDTO 待新增的角色DTO实体
     * @return ResponseResult
     */
    @PostMapping("/insert")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"角色管理员", "用户管理员", "权限管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"roles:insert:*", "roles:update:*", "roles:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台新增角色", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台添加角色成功"),
            @ApiResponse(code = -1, message = "后台添加角色失败")
    }) // 定义响应的所有信息
    public ResponseResult insert(@Validated(value = {InsertValidationGroup.class}) RolesDTO rolesDTO) {

        if (rolesService.insert(rolesDTO)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 后台删除角色 逻辑删除
     * @param id 待删除数据的主键id
     * @return ResponseResult
     */
    @PostMapping("/deleteLogic")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"角色管理员", "用户管理员", "权限管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"roles:delete:*", "roles:update:*", "roles:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台删除角色", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台删除角色成功"),
            @ApiResponse(code = -1, message = "后台删除角色失败")
    }) // 定义响应的所有信息
    public ResponseResult deleteLogic(@NotNull(message = "删除操作必须提供待删除数据的主键id！") Long id) {

        if (rolesService.deleteLogic(id)) {

            ShiroUtil.clearCache();
            return ResponseResult.success();
        }

        ShiroUtil.clearCache();
        return ResponseResult.error("failure");
    }


    /**
     * 后台修改角色
     * @param rolesDTO 修改后的角色DTO实体
     * @return ResponseResult
     */
    @PostMapping("/update")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"角色管理员", "用户管理员", "权限管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"roles:update:*", "roles:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台修改角色", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台修改角色成功"),
            @ApiResponse(code = -1, message = "后台修改角色失败")
    }) // 定义响应的所有信息
    public ResponseResult update(@Validated(value = UpdateValidationGroup.class) RolesDTO rolesDTO) {

        if (rolesService.update(rolesDTO)) {

            ShiroUtil.clearCache();
            return ResponseResult.success();
        }

        ShiroUtil.clearCache();
        return ResponseResult.error("failure");
    }


    /**
     * 条件分页查询角色列表
     * 因为使用layui表格的异步渲染，为符合layui的要求，将pageNum、pageSize的请求参数设为page、limit
     * @param pageNum 页码
     * @param pageSize 每页显示数目
     * @param search 模糊查询的条件
     * @return JSONObject  因为layui表格异步渲染 默认需要返回固定格式 {code:xxx, msg:xxx, count:xxx, data:[]}
     */
    @PostMapping("/list")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"角色管理员", "用户管理员", "权限管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"roles:select:*", "roles:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台条件查询角色分页列表", response = JSONObject.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    public JSONObject list(@RequestParam(value = "page", defaultValue = "1", required = false) Integer pageNum,
                           @RequestParam(value = "limit", defaultValue = "10", required = false) Integer pageSize,
                           @RequestParam(value = "search", required = false) String search) {

        JSONObject obj = new JSONObject();

        PageRequest<RolesDTO> pageRequest = new PageRequest<>();
        pageRequest.setPageNum(pageNum);
        pageRequest.setPageSize(pageSize);
        if (search != null && !"".equals(search.trim())) {

            RolesDTO rolesDTO = new RolesDTO();
            search = search.trim();
            rolesDTO.setName(search);
            pageRequest.setQuery(rolesDTO);
        }

        PageResult<RolesVO> pageResult = rolesService.selectPage(pageRequest);

        obj.put("code", 0);
        obj.put("msg", "");
        obj.put("count",pageResult.getTotal());
        obj.put("data", pageResult.getData());

        return obj;
    }


    /**
     * 更新角色状态
     * @param id 被更新角色的 主键id
     * @param status 更新后的状态
     * @return ResponseResult
     */
    @PostMapping("/changeStatus")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"角色管理员", "用户管理员", "权限管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"roles:update:*", "roles:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台修改角色状态", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台修改角色状态成功"),
            @ApiResponse(code = -1, message = "后台修改角色状态失败")
    }) // 定义响应的所有信息
    public ResponseResult changeStatus(@NotNull(message = "更新状态必须提供被更新者的主键id") Long id,
                                       @NotNull(message = "更新状态必须提供新的状态") Integer status){

        if (rolesService.changeStatus(status, id)) {

            ShiroUtil.clearCache();
            return ResponseResult.success();
        }

        ShiroUtil.clearCache();
        return ResponseResult.error("failure");
    }


    /**
     * 多选数据  批量删除角色
     * @param batchId 是所有被选中行的角色主键id，以","分隔开 组成的字符串。如"1,2,3,"
     * @return ResponseResult
     */
    @PostMapping("/batchDelete")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"角色管理员", "用户管理员", "权限管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"roles:delete:*", "roles:update:*", "roles:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台批量删除角色", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台批量删除角色成功"),
            @ApiResponse(code = -1, message = "后台批量删除角色失败")
    }) // 定义响应的所有信息
    public ResponseResult batchDelete(@NotBlank String batchId) {

        // 以,分隔成存储多个主键id的 数组
        String[] array = batchId.split(",");
        boolean flag = true;
        for (String id : array) {
            boolean delete = rolesService.deleteLogic(Long.valueOf(id));
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
     * 根据角色主键id 获取 已分配的权限 和 未分配的权限
     * @param id 用户主键id
     * @return Map
     */
    @PostMapping("/selectAssign")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"角色管理员", "用户管理员", "权限管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"permissions:*:*", "roles:update:*", "roles:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台查询角色已分配的权限和未分配的权限", response = Map.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    public Map<String, Object> selectAssign(@NotNull Long id) {

        Map<String, Object> map = Maps.newHashMap();
        // 根据角色主键id 查询中间表 涉及该角色的正常关联数据的权限id，并存入一个集合
        List<Long> permissionIds = rolesPermissionsService.selectByRoleId(id)
                .stream()
                .filter(rp -> rp.getStatus() == 0)
                .map(RolesPermissions::getPermissionId)
                .collect(Collectors.toList());
        List<PermissionsVO> assign = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(permissionIds)) {
            // 根据存储权限主键id的集合，查询并过滤出 正常的权限，并存入一个集合。即存储着该角色分配的所有正常权限
            assign = permissionsService.selectByIds(permissionIds)
                    .stream()
                    .filter(p -> p.getStatus() == 0)
                    .map(p -> {
                        PermissionsVO vo = new PermissionsVO();
                        BeanUtils.copyProperties(p, vo);
                        return vo;
                    })
                    .collect(Collectors.toList());
        }
        // 查询权限表全部数据 过滤出 正常的权限，并存入一个集合。即存储着权限表中所有正常权限
        List<PermissionsVO> all = permissionsService.selectAll()
                .stream()
                .filter(vo -> vo.getStatus() == 0)
                .collect(Collectors.toList());

        // 使存储权限表中全部正常权限的集合，删除 其中该角色分配的所有正常权限的集合。
        // 整体删除部分，剩下的是存储着该角色尚未分配的正常权限的集合
        all.removeAll(assign);

        // 将该用户 已分配的所有正常角色 和 尚未分配所有正常角色 存入一个map，响应回客户端
        map.put("assign", assign);
        map.put("unassign", all);

        return map;
    }


    /**
     * 根据角色主键id 为角色分配权限：删除旧权限 添加新权限
     * @param id 角色主键id
     * @param permissionIds 更新分配后的 新权限的主键id字符串，以逗号分隔  如"1,2,3,"
     * @return ResponseResult
     */
    @PostMapping("/assign")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"角色管理员", "用户管理员", "权限管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"permissions:*:*", "roles:update:*", "roles:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台为角色分配权限", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台为角色分配权限成功"),
            @ApiResponse(code = -1, message = "后台为角色分配权限失败")
    }) // 定义响应的所有信息
    public ResponseResult assign(@NotNull Long id, @NotNull String permissionIds) {

        String[] array = permissionIds.split(",");
        // 存储权限主键id的集合
        List<Long> ids = Lists.newArrayList();
        // 如果提交前  前端页面右侧已分配权限栏有值，则遍历再依次添加到声明的ids集合中
        if (!StringUtils.isEmpty(permissionIds) && permissionIds.contains(",")) {
            for (String s : array) {
                ids.add(Long.valueOf(s));
            }
        }

        // 传过来的permissionIds还是之前本已分配的，代表分配的权限并无做改变，此时不用其他操作直接响应返回即可

        // 根据角色主键id 查询中间表 涉及该角色的正常关联数据的权限id，并存入一个集合
        List<Long> permissionIdss = rolesPermissionsService.selectByRoleId(id)
                .stream()
                .filter(rp -> rp.getStatus() == 0)
                .map(RolesPermissions::getPermissionId)
                .collect(Collectors.toList());
        // 存储着该角色原先已分配正常权限的集合
        List<PermissionsVO> assign = Lists.newArrayList();
        // 存储着该角色原先已分配正常权限主键id的集合
        List<Long> assignIds = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(permissionIdss)) {
            // 根据存储角色主键id的集合，查询并过滤出 正常的角色，并存入一个集合。即存储着该用户原先分配的所有正常角色
            assign = permissionsService.selectByIds(permissionIdss)
                    .stream()
                    .filter(p -> p.getStatus() == 0)
                    .map(p -> {
                        PermissionsVO vo = new PermissionsVO();
                        BeanUtils.copyProperties(p, vo);
                        return vo;
                    })
                    .collect(Collectors.toList());
        }
        if (!CollectionUtils.isEmpty(assign)) {
            // 将存储着该用户原先已分配正常角色的集合 映射成 存储着该用户原先已分配正常角色主键id的集合
            assignIds = assign.stream().map(PermissionsVO::getId).collect(Collectors.toList());
        }

        // 使存储着该角色原先已分配正常权限主键id的集合 删除 页面表单提交的权限id字符串所转换成的集合
        // 删除后 如果原先已分配正常权限主键id的集合为空，则代表提交前后 分配的权限并无做改变，此时无需操作 直接响应返回即可
        if (assignIds.size() >= ids.size()) {
            assignIds.removeAll(ids);
            if (assignIds.isEmpty()) {
                ShiroUtil.clearCache();
                return ResponseResult.success();
            }
        }

        // 根据角色主键id 删除其所有的角色权限关联数据
        if (!rolesPermissionsService.deleteLogicByRoleId(id)) {

            ShiroUtil.clearCache();
            return ResponseResult.error("failure");
        }

        // 如果ids为空，代表前端页面清空了已有权限，则只删除已分配的所有权限即可；否则还要执行批量新增操作
        if (CollectionUtils.isEmpty(ids)) {

            ShiroUtil.clearCache();
            return ResponseResult.success();
        }
        else {
            // 为该角色 批量添加权限
            if (!rolesPermissionsService.insert(id, ids)) {

                ShiroUtil.clearCache();
                return ResponseResult.error("failure");
            }
            else {
                ShiroUtil.clearCache();
                return ResponseResult.success();
            }
        }
    }


}
