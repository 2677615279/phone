package com.past.controller;

import com.alibaba.fastjson.JSONObject;
import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.common.ResponseResult;
import com.past.domain.dto.CitiesDTO;
import com.past.domain.vo.CitiesVO;
import com.past.domain.vo.ProvincesVO;
import com.past.service.AreasService;
import com.past.service.CitiesService;
import com.past.service.ProvincesService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 城市模块Controller
 */
@Controller
@RequestMapping("/api/cities")
@Validated // 开启基础类型数据的校验
@Slf4j
@Api(tags = "CitiesController", description = "城市管理Controller", protocols = "http, https", hidden = false) // 作用在类上，描述该类的信息（用途、接收xx协议的请求、该内容是否在页面上隐藏等等）
public class CitiesController {

    @Autowired
    private CitiesService citiesService;

    @Autowired
    private ProvincesService provincesService;

    @Autowired
    private AreasService areasService;


    /**
     * 新增之前的查询 以备选择所属省份id
     * @return ResponseResult
     */
    @PostMapping("/beforeInsert")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"城市管理员", "省份管理员", "地址管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"provinces:select:*", "provinces:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台添加城市前必要的查询，获取可选所属省份列表", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台添加城市前必要的查询，获取可选所属省份列表成功")
    }) // 定义响应的所有信息
    public ResponseResult beforeInsert() {

        List<ProvincesVO> voList = provincesService.selectAll();
        // 对查询所有的省份做过滤，只保留正常状态的省份。使新增场景选择上一级时 不出现非正常状态的可选数据
        List<ProvincesVO> voListFilter = Optional.ofNullable(voList)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(vo -> vo.getStatus() == 0)
                .collect(Collectors.toList());

        return ResponseResult.success(voListFilter);
    }


    /**
     * 添加城市
     * @param citiesDTO 待添加的城市DTO实体
     * @return ResponseResult
     */
    @PostMapping("/insert")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"城市管理员", "省份管理员", "地址管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"cities:insert:*", "cities:update:*", "cities:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台添加城市", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台添加城市成功"),
            @ApiResponse(code = -1, message = "后台添加城市失败")
    }) // 定义响应的所有信息
    public ResponseResult insert(@Validated(value = InsertValidationGroup.class) CitiesDTO citiesDTO) {

        if (citiesService.insert(citiesDTO)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 删除城市 逻辑删除
     * @param id 待删除的城市的主键id
     * @return ResponseResult
     */
    @PostMapping("/deleteLogic")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"城市管理员", "省份管理员", "地址管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"cities:delete:*", "cities:update:*", "cities:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台删除城市", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台删除城市成功"),
            @ApiResponse(code = -1, message = "后台删除城市失败")
    }) // 定义响应的所有信息
    public ResponseResult deleteLogic(@NotNull(message = "删除操作必须提供待删除数据的主键id！") Long id) {

        if (citiesService.deleteLogic(id)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 更新之前的查询 以备选择所属省份id
     * @param id 待更新数据的主键id
     * @return ResponseResult
     */
    @PostMapping("/beforeUpdate")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"城市管理员", "省份管理员", "地址管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"cities:select:*", "provinces:select:*", "cities:*:*", "provinces:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台修改城市前必要的查询，获取可选所属省份列表", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台修改城市前必要的查询，获取可选所属省份列表成功")
    }) // 定义响应的所有信息
    public ResponseResult beforeUpdate(@NotNull(message = "更新操作必须提供待更新数据的主键id！") Long id) {

        CitiesVO city = citiesService.selectByPrimaryKey(id);

        List<ProvincesVO> voList = provincesService.selectAll();
        // 对查询所有的省份做过滤，过滤并保留正常状态的省份。使更新场景选择上一级时，以备选择所属省份id
        List<ProvincesVO> voListFilter = Optional.ofNullable(voList)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(vo -> vo.getStatus() == 0)
                .collect(Collectors.toList());

        return ResponseResult.success(String.valueOf(city.getProvince().getId()), voListFilter);
    }


    /**
     * 更新城市
     * @param citiesDTO 更新后的城市DTO实体
     * @return ResponseResult
     */
    @PostMapping("/update")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"城市管理员", "省份管理员", "地址管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"cities:update:*", "cities:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台修改城市", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台修改城市成功"),
            @ApiResponse(code = -1, message = "后台修改城市失败")
    }) // 定义响应的所有信息
    public ResponseResult update(@Validated(value = UpdateValidationGroup.class) CitiesDTO citiesDTO) {

        if (citiesService.update(citiesDTO)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 条件分页查询城市列表
     * 因为使用layui表格的异步渲染，为符合layui的要求，将pageNum、pageSize的请求参数设为page、limit
     * @param pageNum 页码
     * @param pageSize 每页显示数目
     * @param search 模糊查询的条件
     * @return JSONObject  因为layui表格异步渲染 默认需要返回固定格式 {code:xxx, msg:xxx, count:xxx, data:[]}
     */
    @PostMapping("/list")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"城市管理员", "省份管理员", "地址管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"cities:select:*", "cities:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台条件查询城市分页列表", response = JSONObject.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    public JSONObject list(@RequestParam(value = "page", defaultValue = "1", required = false) Integer pageNum,
                           @RequestParam(value = "limit", defaultValue = "10", required = false) Integer pageSize,
                           @RequestParam(value = "search", required = false) String search) {

        JSONObject obj = new JSONObject();

        PageRequest<CitiesDTO> pageRequest = new PageRequest<>();
        pageRequest.setPageNum(pageNum);
        pageRequest.setPageSize(pageSize);
        if (search != null && !"".equals(search.trim())) {

            CitiesDTO citiesDTO = new CitiesDTO();
            search = search.trim();
            citiesDTO.setName(search);
            pageRequest.setQuery(citiesDTO);
        }

        PageResult<CitiesVO> pageResult = citiesService.selectPage(pageRequest);

        obj.put("code", 0);
        obj.put("msg", "");
        obj.put("count",pageResult.getTotal());
        obj.put("data", pageResult.getData());

        return obj;
    }


    /**
     * 更新城市状态
     * @param id 被更新城市的 主键id
     * @param status 更新后的状态
     * @return ResponseResult
     */
    @PostMapping("/changeStatus")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"城市管理员", "省份管理员", "地址管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"cities:update:*", "cities:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台修改城市状态", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台修改城市状态成功"),
            @ApiResponse(code = -1, message = "后台修改城市状态失败")
    }) // 定义响应的所有信息
    public ResponseResult changeStatus(@NotNull(message = "更新状态必须提供被更新者的主键id") Long id,
                                       @NotNull(message = "更新状态必须提供新的状态") Integer status){

        // 如果当前id代表的城市 禁用成功
        if (citiesService.changeStatus(status, id)) {

            // 其下所有区县也执行禁用
            areasService.changeStatusByCitiesStatus(status, id);
            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 多选数据  批量删除城市
     * @param batchId 是所有被选中行的城市主键id，以","分隔开 组成的字符串。如"1,2,3,"
     * @return ResponseResult
     */
    @PostMapping("/batchDelete")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"城市管理员", "省份管理员", "地址管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"cities:delete:*", "cities:update:*", "cities:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台批量删除城市", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台批量删除城市成功"),
            @ApiResponse(code = -1, message = "后台批量删除城市失败")
    }) // 定义响应的所有信息
    public ResponseResult batchDelete(@NotBlank String batchId) {

        // 以,分隔成存储多个主键id的 数组
        String[] array = batchId.split(",");
        boolean flag = true;
        for (String id : array) {
            boolean delete = citiesService.deleteLogic(Long.valueOf(id));
            // 只要有一个删除失败  flag赋值false
            if (!delete) {
                flag = false;
            }
        }

        if (flag) {
            return ResponseResult.success();
        }
        else {
            return ResponseResult.error("failure");
        }
    }
    

}
