package com.past.controller;

import com.alibaba.fastjson.JSONObject;
import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.common.ResponseResult;
import com.past.domain.dto.GoodsTypesDTO;
import com.past.domain.vo.GoodsTypesVO;
import com.past.service.GoodsTypesService;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品分类模块Controller
 */
@Controller
@RequestMapping("/api/goodstypes")
@Validated // 开启基础类型数据的校验
@Slf4j
@Api(tags = "GoodsTypesController", description = "商品分类管理Controller", protocols = "http, https", hidden = false) // 作用在类上，描述该类的信息（用途、接收xx协议的请求、该内容是否在页面上隐藏等等）
public class GoodsTypesController {
    
    @Autowired
    private GoodsTypesService goodsTypesService;


    /**
     * 添加商品分类 只要有insert操作，会把type-with-goods-cache缓存下的所有数据清空
     * @param goodsTypesDTO 待添加的商品分类DTO实体
     * @return ResponseResult
     */
    @PostMapping("/insert")
    @ResponseBody
    @CacheEvict(cacheNames = "type-with-goods-cache", allEntries = true)
    @RequiresAuthentication
    @RequiresRoles(value = {"商品分类管理员", "商品管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"}, logical = Logical.OR)
    @RequiresPermissions(value = {"goodstypes:insert:*", "goodstypes:update:*", "goodstypes:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台添加商品分类", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台添加商品分类成功"),
            @ApiResponse(code = -1, message = "后台添加商品分类失败")
    }) // 定义响应的所有信息
    public ResponseResult insert(@Validated(value = InsertValidationGroup.class) GoodsTypesDTO goodsTypesDTO) {

        if (goodsTypesService.insert(goodsTypesDTO)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 删除商品分类 逻辑删除 只要有deleteLogic操作，会把type-with-goods-cache缓存下的所有数据清空
     * @param id 待删除的商品分类的主键id
     * @return ResponseResult
     */
    @PostMapping("/deleteLogic")
    @ResponseBody
    @CacheEvict(cacheNames = "type-with-goods-cache", allEntries = true)
    @RequiresAuthentication
    @RequiresRoles(value = {"商品分类管理员", "商品管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"}, logical = Logical.OR)
    @RequiresPermissions(value = {"goodstypes:delete:*", "goodstypes:update:*", "goodstypes:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台删除商品分类", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台删除商品分类成功"),
            @ApiResponse(code = -1, message = "后台删除商品分类失败")
    }) // 定义响应的所有信息
    public ResponseResult deleteLogic(@NotNull(message = "删除操作必须提供待删除数据的主键id！") Long id) {

        if (goodsTypesService.deleteLogic(id)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 更新商品分类 只要有update操作，会把type-with-goods-cache缓存下的所有数据清空
     * @param goodsTypesDTO 更新后的商品分类DTO实体
     * @return ResponseResult
     */
    @PostMapping("/update")
    @ResponseBody
    @CacheEvict(cacheNames = "type-with-goods-cache", allEntries = true)
    @RequiresAuthentication
    @RequiresRoles(value = {"商品分类管理员", "商品管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"}, logical = Logical.OR)
    @RequiresPermissions(value = {"goodstypes:update:*", "goodstypes:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台修改商品分类", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台修改商品分类成功"),
            @ApiResponse(code = -1, message = "后台修改商品分类失败")
    }) // 定义响应的所有信息
    public ResponseResult update(@Validated(value = UpdateValidationGroup.class) GoodsTypesDTO goodsTypesDTO) {

        if (goodsTypesService.update(goodsTypesDTO)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 条件分页查询商品分类列表
     * 因为使用layui表格的异步渲染，为符合layui的要求，将pageNum、pageSize的请求参数设为page、limit
     * @param pageNum 页码
     * @param pageSize 每页显示数目
     * @param search 模糊查询的条件
     * @return JSONObject  因为layui表格异步渲染 默认需要返回固定格式 {code:xxx, msg:xxx, count:xxx, data:[]}
     */
    @PostMapping("/list")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"商品分类管理员", "商品管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"}, logical = Logical.OR)
    @RequiresPermissions(value = {"goodstypes:select:*", "goodstypes:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台条件查询商品分类分页列表", response = JSONObject.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    public JSONObject list(@RequestParam(value = "page", defaultValue = "1", required = false) Integer pageNum,
                           @RequestParam(value = "limit", defaultValue = "10", required = false) Integer pageSize,
                           @RequestParam(value = "search", required = false) String search) {

        JSONObject obj = new JSONObject();

        PageRequest<GoodsTypesDTO> pageRequest = new PageRequest<>();
        pageRequest.setPageNum(pageNum);
        pageRequest.setPageSize(pageSize);
        if (search != null && !"".equals(search.trim())) {

            GoodsTypesDTO goodsTypesDTO = new GoodsTypesDTO();
            search = search.trim();
            goodsTypesDTO.setName(search);
            pageRequest.setQuery(goodsTypesDTO);
        }

        PageResult<GoodsTypesVO> pageResult = goodsTypesService.selectPage(pageRequest);

        obj.put("code", 0);
        obj.put("msg", "");
        obj.put("count",pageResult.getTotal());
        obj.put("data", pageResult.getData());

        return obj;
    }


    /**
     * 更新商品分类状态 只要有changeStatus操作，会把type-with-goods-cache缓存下的所有数据清空
     * @param id 被更新商品分类的 主键id
     * @param status 更新后的状态
     * @return ResponseResult
     */
    @PostMapping("/changeStatus")
    @ResponseBody
    @CacheEvict(cacheNames = "type-with-goods-cache", allEntries = true)
    @RequiresAuthentication
    @RequiresRoles(value = {"商品分类管理员", "商品管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"}, logical = Logical.OR)
    @RequiresPermissions(value = {"goodstypes:update:*", "goodstypes:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台修改商品分类状态", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台修改商品分类状态成功"),
            @ApiResponse(code = -1, message = "后台修改商品分类状态失败")
    }) // 定义响应的所有信息
    public ResponseResult changeStatus(@NotNull(message = "更新状态必须提供被更新者的主键id") Long id,
                                       @NotNull(message = "更新状态必须提供新的状态") Integer status){

        if (goodsTypesService.changeStatus(status, id)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 多选数据  批量删除商品分类 只要有batchDelete操作，会把type-with-goods-cache缓存下的所有数据清空
     * @param batchId 是所有被选中行的商品分类主键id，以","分隔开 组成的字符串。如"1,2,3,"
     * @return ResponseResult
     */
    @PostMapping("/batchDelete")
    @ResponseBody
    @CacheEvict(cacheNames = "type-with-goods-cache", allEntries = true)
    @RequiresAuthentication
    @RequiresRoles(value = {"商品分类管理员", "商品管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"}, logical = Logical.OR)
    @RequiresPermissions(value = {"goodstypes:delete:*", "goodstypes:update:*", "goodstypes:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台批量删除商品分类", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台批量删除商品分类成功"),
            @ApiResponse(code = -1, message = "后台批量删除商品分类失败")
    }) // 定义响应的所有信息
    public ResponseResult batchDelete(@NotBlank String batchId) {

        // 以,分隔成存储多个主键id的 数组
        String[] array = batchId.split(",");
        boolean flag = true;
        for (String id : array) {
            boolean delete = goodsTypesService.deleteLogic(Long.valueOf(id));
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


    /**
     * 查询商品分类列表
     * @return ResponseResult
     */
    @PostMapping("/selectAll")
    @ResponseBody
    @Cacheable(cacheNames = "type-with-goods-cache")
    @ApiOperation(value = "查询商品分类列表", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询商品分类列表成功")
    }) // 定义响应的所有信息
    public ResponseResult selectAll() {

        // 查询所有正常的分类
        List<GoodsTypesVO> all = goodsTypesService.selectAll().stream().filter(gt -> gt.getStatus() == 0).collect(Collectors.toList());

        return ResponseResult.success(all);
    }
    
    
}
