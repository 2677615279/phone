package com.past.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.common.ResponseResult;
import com.past.domain.dto.PermissionsDTO;
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
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 权限模块Controller
 */
@Controller
@RequestMapping("/api/permissions")
@Validated // 开启基础类型数据的校验
@Slf4j
@Api(tags = "PermissionsController", description = "权限管理Controller", protocols = "http, https", hidden = false) // 作用在类上，描述该类的信息（用途、接收xx协议的请求、该内容是否在页面上隐藏等等）
public class PermissionsController {

    @Autowired
    private PermissionsService permissionsService;

    @Autowired
    private RolesService rolesService;

    @Autowired
    private RolesPermissionsService rolesPermissionsService;


    /**
     * 新增之前的查询 以备选择父id
     * @return ResponseResult
     */
    @PostMapping("/beforeInsert")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"权限管理员", "角色管理员", "用户管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"permissions:select:*", "permissions:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台添加权限前必要的查询，获取可选父权限列表", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台添加权限前必要的查询，获取可选父权限列表成功")
    }) // 定义响应的所有信息
    public ResponseResult beforeInsert() {

        List<PermissionsVO> voList = permissionsService.selectAll();
        // 对查询所有的权限做过滤，只保留正常状态的权限。使新增场景选择上一级时 不出现非正常状态的可选数据
        List<PermissionsVO> voListFilter = Optional.ofNullable(voList)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(vo -> vo.getStatus() == 0)
                .collect(Collectors.toList());

        return ResponseResult.success(voListFilter);
    }


    /**
     * 后台添加限
     * @param permissionsDTO 待新增的权限DTO对象
     * @return ResponseResult
     */
    @PostMapping("/insert")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"权限管理员", "角色管理员", "用户管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"permissions:insert:*", "permissions:update:*", "permissions:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台添加权限", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台添加权限成功"),
            @ApiResponse(code = -1, message = "后台添加权限失败")
    }) // 定义响应的所有信息
    public ResponseResult insert(@Validated(value = {InsertValidationGroup.class}) PermissionsDTO permissionsDTO) {

        if (permissionsService.insert(permissionsDTO)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 删除权限 逻辑删除
     * @param id 待删除数据的主键id
     * @return ResponseResult
     */
    @PostMapping("/deleteLogic")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"权限管理员", "角色管理员", "用户管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"permissions:delete:*", "permissions:update:*", "permissions:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台删除权限", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台删除权限成功"),
            @ApiResponse(code = -1, message = "后台删除权限失败")
    }) // 定义响应的所有信息
    public ResponseResult deleteLogic(@NotNull(message = "删除操作必须提供待删除数据的主键id！") Long id) {

        if (permissionsService.deleteLogic(id)) {

            ShiroUtil.clearCache();
            return ResponseResult.success();
        }

        ShiroUtil.clearCache();
        return ResponseResult.error("failure");
    }


    /**
     * 更新之前的查询 以备选择父id
     * @param id 待更新数据的主键id
     * @return ResponseResult
     */
    @PostMapping("/beforeUpdate")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"权限管理员", "角色管理员", "用户管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"permissions:select:*", "permissions:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台修改权限前必要的查询，获取可选父权限列表", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台修改权限前必要的查询，获取可选父权限列表成功")
    }) // 定义响应的所有信息
    public ResponseResult beforeUpdate(@NotNull(message = "更新操作必须提供待更新数据的主键id！") Long id) {

        PermissionsVO p = permissionsService.selectByPrimaryKey(id);
        List<PermissionsVO> voList = permissionsService.selectAll();
        // 对查询所有的权限做过滤，过滤掉当前修改的权限并保留正常状态的其他权限。使更新场景选择上一级时 不出现当前被修改的数据
        List<PermissionsVO> voListFilter = Optional.ofNullable(voList)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(vo -> vo.getStatus() == 0)
                .filter(vo -> !vo.getId().equals(id))
                .collect(Collectors.toList());

        return ResponseResult.success(String.valueOf(p.getParentId()), voListFilter);
    }


    /**
     * 后台修改权限
     * @param permissionsDTO 修改后的权限DTO实体
     * @return ResponseResult
     */
    @PostMapping("/update")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"权限管理员", "角色管理员", "用户管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"permissions:update:*", "permissions:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台修改权限", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台修改权限成功"),
            @ApiResponse(code = -1, message = "后台修改权限失败")
    }) // 定义响应的所有信息
    public ResponseResult update(@Validated(value = UpdateValidationGroup.class) PermissionsDTO permissionsDTO) {

        if (permissionsService.update(permissionsDTO)) {

            ShiroUtil.clearCache();
            return ResponseResult.success();
        }

        ShiroUtil.clearCache();
        return ResponseResult.error("failure");
    }


    /**
     * 条件分页查询权限列表
     * 因为使用layui表格的异步渲染，为符合layui的要求，将pageNum、pageSize的请求参数设为page、limit
     * @param pageNum 页码
     * @param pageSize 每页显示数目
     * @param search 模糊查询的条件
     * @return JSONObject  因为layui表格异步渲染 默认需要返回固定格式 {code:xxx, msg:xxx, count:xxx, data:[]}
     */
    @PostMapping("/list")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"权限管理员", "角色管理员", "用户管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"permissions:select:*", "permissions:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台条件查询权限分页列表", response = JSONObject.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    public JSONObject list(@RequestParam(value = "page", defaultValue = "1", required = false) Integer pageNum,
                           @RequestParam(value = "limit", defaultValue = "10", required = false) Integer pageSize,
                           @RequestParam(value = "search", required = false) String search) {

        JSONObject obj = new JSONObject();

        PageRequest<PermissionsDTO> pageRequest = new PageRequest<>();
        pageRequest.setPageNum(pageNum);
        pageRequest.setPageSize(pageSize);
        if (search != null && !"".equals(search.trim())) {

            PermissionsDTO permissionsDTO = new PermissionsDTO();
            search = search.trim();
            permissionsDTO.setName(search);
            pageRequest.setQuery(permissionsDTO);
        }

        PageResult<PermissionsVO> pageResult = permissionsService.selectPage(pageRequest);

        obj.put("code", 0);
        obj.put("msg", "");
        obj.put("count",pageResult.getTotal());
        obj.put("data", pageResult.getData());

        return obj;
    }


    /**
     * 更新权限状态
     * @param id 被更新权限的 主键id
     * @param status 更新后的状态
     * @return ResponseResult
     */
    @PostMapping("/changeStatus")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"权限管理员", "角色管理员", "用户管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"permissions:update:*", "permissions:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台修改权限状态", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台修改权限状态成功"),
            @ApiResponse(code = -1, message = "后台修改权限状态失败")
    }) // 定义响应的所有信息
    public ResponseResult changeStatus(@NotNull(message = "更新状态必须提供被更新者的主键id") Long id,
                                       @NotNull(message = "更新状态必须提供新的状态") Integer status){

        if (permissionsService.changeStatus(status, id)) {

            ShiroUtil.clearCache();
            return ResponseResult.success();
        }

        ShiroUtil.clearCache();
        return ResponseResult.error("failure");
    }


    /**
     * 多选数据  批量删除权限
     * @param batchId 是所有被选中行的权限主键id，以","分隔开 组成的字符串。如"1,2,3,"
     * @return ResponseResult
     */
    @PostMapping("/batchDelete")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"权限管理员", "角色管理员", "用户管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"permissions:delete:*", "permissions:update:*", "permissions:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台批量删除权限", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台批量删除权限成功"),
            @ApiResponse(code = -1, message = "后台批量删除权限失败")
    }) // 定义响应的所有信息
    public ResponseResult batchDelete(@NotBlank String batchId) {

        // 以,分隔成存储多个主键id的 数组
        String[] array = batchId.split(",");
        boolean flag = true;
        for (String id : array) {
            boolean delete = permissionsService.deleteLogic(Long.valueOf(id));
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
     * 根据权限主键id 获取 已分配的角色 和 未分配的角色
     * @param id 权限主键id
     * @return Map
     */
    @PostMapping("/selectAssign")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"权限管理员", "角色管理员", "用户管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"roles:*:*", "permissions:update:*", "permissions:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台查询权限已分配的角色和未分配的角色", response = Map.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    public Map<String, Object> selectAssign(@NotNull Long id) {

        Map<String, Object> map = Maps.newHashMap();
        // 根据权限主键id 查询中间表 涉及该权限的正常关联数据的角色id，并存入一个集合
        List<Long> roleIds = rolesPermissionsService.selectByPermissionId(id)
                .stream()
                .filter(rp -> rp.getStatus() == 0)
                .map(RolesPermissions::getRoleId)
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

        // 使存储角色表中全部正常角色的集合，删除 其中该权限分配的所有正常角色的集合。
        // 整体删除部分，剩下的是存储着该权限尚未分配的正常角色的集合
        all.removeAll(assign);

        // 将该权限 已分配的所有正常角色 和 尚未分配所有正常角色 存入一个map，响应回客户端
        map.put("assign", assign);
        map.put("unassign", all);

        return map;
    }


    /**
     * 根据权限主键id 为权限分配角色：删除旧角色 添加新角色
     * @param id 权限主键id
     * @param roleIds 更新分配后的 新角色的主键id字符串，以逗号分隔  如"1,2,3,"
     * @return ResponseResult
     */
    @PostMapping("/assign")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"权限管理员", "角色管理员", "用户管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"roles:*:*", "permissions:update:*", "permissions:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台为权限分配角色", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台为权限分配角色成功"),
            @ApiResponse(code = -1, message = "后台为权限分配角色失败")
    }) // 定义响应的所有信息
    public ResponseResult assign(@NotNull Long id, @NotNull String roleIds) {

        String[] array = roleIds.split(",");
        // 存储角色主键id的集合
        List<Long> ids = Lists.newArrayList();
        // 如果提交前  前端页面右侧已分配角色栏有值，则遍历再依次添加到声明的ids集合中
        if (!StringUtils.isEmpty(roleIds) && roleIds.contains(",")) {
            for (String s : array) {
                ids.add(Long.valueOf(s));
            }
        }

        // 传过来的roleIds还是之前本已分配的，代表分配的角色并无做改变，此时不用其他操作直接响应返回即可

        // 根据权限主键id 查询中间表 涉及该权限的正常关联数据的角色id，并存入一个集合
        List<Long> roleIdss = rolesPermissionsService.selectByPermissionId(id)
                .stream()
                .filter(ru -> ru.getStatus() == 0)
                .map(RolesPermissions::getRoleId)
                .collect(Collectors.toList());
        // 存储着该权限原先已分配正常角色的集合
        List<RolesVO> assign = Lists.newArrayList();
        // 存储着该权限原先已分配正常角色主键id的集合
        List<Long> assignIds = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(roleIdss)) {
            // 根据存储角色主键id的集合，查询并过滤出 正常的角色，并存入一个集合。即存储着该权限原先分配的所有正常角色
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
            // 将存储着该权限原先已分配正常角色的集合 映射成 存储着该权限原先已分配正常角色主键id的集合
            assignIds = assign.stream().map(RolesVO::getId).collect(Collectors.toList());
        }

        // 使存储着该权限原先已分配正常角色主键id的集合 删除 页面表单提交的角色id字符串所转换成的集合
        // 删除后 如果原先已分配正常角色主键id的集合为空，则代表提交前后 分配的角色并无做改变，此时无需操作 直接响应返回即可
        if (assignIds.size() >= ids.size()) {
            assignIds.removeAll(ids);
            if (assignIds.isEmpty()) {
                ShiroUtil.clearCache();
                return ResponseResult.success();
            }
        }

        // 根据权限主键id 删除其所有的角色权限关联数据
        if (!rolesPermissionsService.deleteLogicByPermissionId(id)) {

            ShiroUtil.clearCache();
            return ResponseResult.error("failure");
        }

        // 如果ids为空，代表前端页面清空了已有角色，则只删除已分配的所有角色即可；否则还要执行批量新增操作
        if (CollectionUtils.isEmpty(ids)) {

            ShiroUtil.clearCache();
            return ResponseResult.success();
        }
        else {
            // 为该权限 批量添加角色
            if (!rolesPermissionsService.insert(ids, id)) {

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
