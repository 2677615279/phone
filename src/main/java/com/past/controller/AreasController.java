package com.past.controller;

import com.alibaba.fastjson.JSONObject;
import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.common.ResponseResult;
import com.past.domain.dto.AreasDTO;
import com.past.domain.vo.AreasVO;
import com.past.domain.vo.CitiesVO;
import com.past.service.AreasService;
import com.past.service.CitiesService;
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
 * 区县模块Controller
 */
@Controller
@RequestMapping("/api/areas")
@Validated // 开启基础类型数据的校验
@Slf4j
@Api(tags = "AreasController", description = "区县管理Controller", protocols = "http, https", hidden = false) // 作用在类上，描述该类的信息（用途、接收xx协议的请求、该内容是否在页面上隐藏等等）
public class AreasController {
    
    @Autowired
    private AreasService areasService;
    
    @Autowired
    private CitiesService citiesService;


    /**
     * 新增之前的查询 以备选择所属城市id
     * @return ResponseResult
     */
    @PostMapping("/beforeInsert")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"区县管理员", "城市管理员", "省份管理员", "地址管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"cities:select:*", "provinces:select:*", "cities:*:*", "procinces:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台添加区县前必要的查询，获取可选所属城市列表", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台添加区县前必要的查询，获取可选所属城市列表成功")
    }) // 定义响应的所有信息
    public ResponseResult beforeInsert(@NotNull Long provinceId) {

        List<CitiesVO> voList = citiesService.selectByProvinceId(provinceId);

        // 对查询所有的城市做过滤，只保留正常状态的城市。使新增场景选择上一级时 不出现非正常状态的可选数据
        List<CitiesVO> citiesVOList = Optional.ofNullable(voList)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .filter(vo -> vo.getStatus() == 0)
                .collect(Collectors.toList());

        return ResponseResult.success(citiesVOList);
    }


    /**
     * 添加区县
     * @param areasDTO 待添加的区县DTO实体
     * @return ResponseResult
     */
    @PostMapping("/insert")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"区县管理员", "城市管理员", "省份管理员", "地址管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"areas:insert:*", "areas:update:*", "areas:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台添加区县", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台添加区县成功"),
            @ApiResponse(code = -1, message = "后台添加区县失败")
    }) // 定义响应的所有信息
    public ResponseResult insert(@Validated(value = InsertValidationGroup.class) AreasDTO areasDTO) {

        if (areasService.insert(areasDTO)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 删除区县 逻辑删除
     * @param id 待删除的区县的主键id
     * @return ResponseResult
     */
    @PostMapping("/deleteLogic")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"区县管理员", "城市管理员", "省份管理员", "地址管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"areas:delete:*", "areas:update:*", "areas:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台删除区县", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台删除区县成功"),
            @ApiResponse(code = -1, message = "后台删除区县失败")
    }) // 定义响应的所有信息
    public ResponseResult deleteLogic(@NotNull(message = "删除操作必须提供待删除数据的主键id！") Long id) {

        if (areasService.deleteLogic(id)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 更新区县
     * @param areasDTO 更新后的区县DTO实体
     * @return ResponseResult
     */
    @PostMapping("/update")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"区县管理员", "城市管理员", "省份管理员", "地址管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"areas:update:*", "areas:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台修改区县", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台修改区县成功"),
            @ApiResponse(code = -1, message = "后台修改区县失败")
    }) // 定义响应的所有信息
    public ResponseResult update(@Validated(value = UpdateValidationGroup.class) AreasDTO areasDTO) {

        if (areasService.update(areasDTO)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 条件分页查询区县列表
     * 因为使用layui表格的异步渲染，为符合layui的要求，将pageNum、pageSize的请求参数设为page、limit
     * @param pageNum 页码
     * @param pageSize 每页显示数目
     * @param search 模糊查询的条件
     * @return JSONObject  因为layui表格异步渲染 默认需要返回固定格式 {code:xxx, msg:xxx, count:xxx, data:[]}
     */
    @PostMapping("/list")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"区县管理员", "城市管理员", "省份管理员", "地址管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"areas:select:*", "areas:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台条件查询区县分页列表", response = JSONObject.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    public JSONObject list(@RequestParam(value = "page", defaultValue = "1", required = false) Integer pageNum,
                           @RequestParam(value = "limit", defaultValue = "10", required = false) Integer pageSize,
                           @RequestParam(value = "search", required = false) String search) {

        JSONObject obj = new JSONObject();

        PageRequest<AreasDTO> pageRequest = new PageRequest<>();
        pageRequest.setPageNum(pageNum);
        pageRequest.setPageSize(pageSize);
        if (search != null && !"".equals(search.trim())) {

            AreasDTO areasDTO = new AreasDTO();
            search = search.trim();
            areasDTO.setName(search);
            pageRequest.setQuery(areasDTO);
        }

        PageResult<AreasVO> pageResult = areasService.selectPage(pageRequest);

        obj.put("code", 0);
        obj.put("msg", "");
        obj.put("count",pageResult.getTotal());
        obj.put("data", pageResult.getData());

        return obj;
    }


    /**
     * 更新区县状态 前提保证其上级状态为正常 即0
     * @param id 被更新区县的 主键id
     * @param status 更新后的状态
     * @return ResponseResult
     */
    @PostMapping("/changeStatus")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"区县管理员", "城市管理员", "省份管理员", "地址管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"areas:update:*", "areas:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台修改区县状态", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台修改区县状态成功"),
            @ApiResponse(code = -1, message = "后台修改区县状态失败")
    }) // 定义响应的所有信息
    public ResponseResult changeStatus(@NotNull(message = "更新状态必须提供被更新者的主键id") Long id,
                                       @NotNull(message = "更新状态必须提供新的状态") Integer status){

        // 更新区县状态，前提保证其上级状态为正常 即0
        if (areasService.changeStatus(status, id)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 多选数据  批量删除区县
     * @param batchId 是所有被选中行的区县主键id，以","分隔开 组成的字符串。如"1,2,3,"
     * @return ResponseResult
     */
    @PostMapping("/batchDelete")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"区县管理员", "城市管理员", "省份管理员", "地址管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"areas:delete:*", "areas:update:*", "areas:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台批量删除区县", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台批量删除区县成功"),
            @ApiResponse(code = -1, message = "后台批量删除区县失败")
    }) // 定义响应的所有信息
    public ResponseResult batchDelete(@NotBlank String batchId) {

        // 以,分隔成存储多个主键id的 数组
        String[] array = batchId.split(",");
        boolean flag = true;
        for (String id : array) {
            boolean delete = areasService.deleteLogic(Long.valueOf(id));
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
