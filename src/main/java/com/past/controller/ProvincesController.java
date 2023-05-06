package com.past.controller;

import com.alibaba.fastjson.JSONObject;
import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.common.ResponseResult;
import com.past.domain.dto.ProvincesDTO;
import com.past.domain.vo.ProvincesVO;
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

/**
 * 省份模块Controller
 */
@Controller
@RequestMapping("/api/provinces")
@Validated // 开启基础类型数据的校验
@Slf4j
@Api(tags = "ProvincesController", description = "省份管理Controller", protocols = "http, https", hidden = false) // 作用在类上，描述该类的信息（用途、接收xx协议的请求、该内容是否在页面上隐藏等等）
public class ProvincesController {
    
    @Autowired
    private ProvincesService provincesService;

    @Autowired
    private CitiesService citiesService;


    /**
     * 添加省份
     * @param provincesDTO 待添加的省份DTO实体
     * @return ResponseResult
     */
    @PostMapping("/insert")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"省份管理员", "地址管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"}, logical = Logical.OR)
    @RequiresPermissions(value = {"provinces:insert:*", "provinces:update:*", "provinces:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台添加省份", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台添加省份成功"),
            @ApiResponse(code = -1, message = "后台添加省份失败")
    }) // 定义响应的所有信息
    public ResponseResult insert(@Validated(value = InsertValidationGroup.class) ProvincesDTO provincesDTO) {

        if (provincesService.insert(provincesDTO)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 删除省份 逻辑删除
     * @param id 待删除的省份的主键id
     * @return ResponseResult
     */
    @PostMapping("/deleteLogic")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"省份管理员", "地址管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"}, logical = Logical.OR)
    @RequiresPermissions(value = {"provinces:delete:*", "provinces:update:*", "provinces:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台删除省份", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台删除省份成功"),
            @ApiResponse(code = -1, message = "后台删除省份失败")
    }) // 定义响应的所有信息
    public ResponseResult deleteLogic(@NotNull(message = "删除操作必须提供待删除数据的主键id！") Long id) {

        if (provincesService.deleteLogic(id)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 更新省份
     * @param provincesDTO 更新后的省份DTO实体
     * @return ResponseResult
     */
    @PostMapping("/update")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"省份管理员", "地址管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"}, logical = Logical.OR)
    @RequiresPermissions(value = {"provinces:update:*", "provinces:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台修改省份", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台修改省份成功"),
            @ApiResponse(code = -1, message = "后台修改省份失败")
    }) // 定义响应的所有信息
    public ResponseResult update(@Validated(value = UpdateValidationGroup.class) ProvincesDTO provincesDTO) {

        if (provincesService.update(provincesDTO)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 条件分页查询省份列表
     * 因为使用layui表格的异步渲染，为符合layui的要求，将pageNum、pageSize的请求参数设为page、limit
     * @param pageNum 页码
     * @param pageSize 每页显示数目
     * @param search 模糊查询的条件
     * @return JSONObject  因为layui表格异步渲染 默认需要返回固定格式 {code:xxx, msg:xxx, count:xxx, data:[]}
     */
    @PostMapping("/list")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"省份管理员", "地址管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"}, logical = Logical.OR)
    @RequiresPermissions(value = {"provinces:select:*", "provinces:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台条件查询省份分页列表", response = JSONObject.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    public JSONObject list(@RequestParam(value = "page", defaultValue = "1", required = false) Integer pageNum,
                           @RequestParam(value = "limit", defaultValue = "10", required = false) Integer pageSize,
                           @RequestParam(value = "search", required = false) String search) {

        JSONObject obj = new JSONObject();

        PageRequest<ProvincesDTO> pageRequest = new PageRequest<>();
        pageRequest.setPageNum(pageNum);
        pageRequest.setPageSize(pageSize);
        if (search != null && !"".equals(search.trim())) {

            ProvincesDTO provincesDTO = new ProvincesDTO();
            search = search.trim();
            provincesDTO.setName(search);
            pageRequest.setQuery(provincesDTO);
        }

        PageResult<ProvincesVO> pageResult = provincesService.selectPage(pageRequest);

        obj.put("code", 0);
        obj.put("msg", "");
        obj.put("count",pageResult.getTotal());
        obj.put("data", pageResult.getData());

        return obj;
    }


    /**
     * 更新省份状态
     * @param id 被更新省份的 主键id
     * @param status 更新后的状态
     * @return ResponseResult
     */
    @PostMapping("/changeStatus")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"省份管理员", "地址管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"}, logical = Logical.OR)
    @RequiresPermissions(value = {"provinces:update:*", "provinces:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台修改省份状态", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台修改省份状态成功"),
            @ApiResponse(code = -1, message = "后台修改省份状态失败")
    }) // 定义响应的所有信息
    public ResponseResult changeStatus(@NotNull(message = "更新状态必须提供被更新者的主键id") Long id,
                                       @NotNull(message = "更新状态必须提供新的状态") Integer status){

        // 如果当前id代表的省份 禁用成功
        if (provincesService.changeStatus(status, id)) {

            // 其下所有城市、区县也禁用
            citiesService.changeStatusByProvincesStatus(status, id);
            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 多选数据  批量删除省份
     * @param batchId 是所有被选中行的省份主键id，以","分隔开 组成的字符串。如"1,2,3,"
     * @return ResponseResult
     */
    @PostMapping("/batchDelete")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"省份管理员", "地址管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"}, logical = Logical.OR)
    @RequiresPermissions(value = {"provinces:delete:*", "provinces:update:*", "provinces:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台批量删除省份", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台批量删除省份成功"),
            @ApiResponse(code = -1, message = "后台批量删除省份失败")
    }) // 定义响应的所有信息
    public ResponseResult batchDelete(@NotBlank String batchId) {

        // 以,分隔成存储多个主键id的 数组
        String[] array = batchId.split(",");
        boolean flag = true;
        for (String id : array) {
            boolean delete = provincesService.deleteLogic(Long.valueOf(id));
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
