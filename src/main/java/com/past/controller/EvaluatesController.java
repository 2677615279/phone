package com.past.controller;

import com.alibaba.fastjson.JSONObject;
import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.common.ResponseResult;
import com.past.domain.dto.EvaluatesDTO;
import com.past.domain.entity.Users;
import com.past.domain.vo.EvaluatesVO;
import com.past.domain.vo.OrdersDetailVO;
import com.past.domain.vo.OrdersVO;
import com.past.service.EvaluatesService;
import com.past.service.OrdersService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 评价模块Controller
 */
@Controller
@RequestMapping("/api/evaluates")
@Validated // 开启基础类型数据的校验
@Slf4j
@Api(tags = "EvaluatesController", description = "评论管理Controller", protocols = "http, https", hidden = false) // 作用在类上，描述该类的信息（用途、接收xx协议的请求、该内容是否在页面上隐藏等等）
public class EvaluatesController {

    @Autowired
    private EvaluatesService evaluatesService;

    @Autowired
    private OrdersService ordersService;


    /**
     * 添加评价
     * @param evaluatesDTO 待新增的评价DTO实体，内容不全，待处理完毕，再去业务层经@Validated校验
     * @param imgUrls 待补充的评价图url，以,分隔
     * @param orderId 待评价的订单主键id 一个订单号对应一个评价
     * @return ResponseResult
     */
    @PostMapping("/insert")
    @ResponseBody
    @RequiresUser
    @ApiOperation(value = "添加评价(可含评价图)", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "添加评价(可含评价图)成功"),
            @ApiResponse(code = -1, message = "添加评价(可含评价图)失败")
    }) // 定义响应的所有信息
    public ResponseResult insert(EvaluatesDTO evaluatesDTO, String imgUrls, @NotNull String orderId) {

        // 分割评价图  存入一个数组
        String[] imgs = imgUrls.split(",");
        // 获取主体对象
        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        // 获取订单 和 详情列表(当前订单号 买了几种商品 就有几个详情)
        OrdersVO ordersVO = ordersService.selectByPrimaryKey(orderId);
        List<OrdersDetailVO> detailVOList = ordersVO.getDetailList();

        boolean flag = true;
        for (OrdersDetailVO ordersDetailVO : detailVOList) {
            // 完善evaluatesDTO实体
            evaluatesDTO.setDay(new Date());
            evaluatesDTO.setGoodsId(ordersDetailVO.getGoods().getId());
            evaluatesDTO.setUserId(login.getId());

            boolean insert = evaluatesService.insert(evaluatesDTO, imgs);
            // 只要有一个新增失败  flag赋值false
            if (!insert) {
                flag = false;
            }
        }

        if(flag){
            // 修改订单状态为5 已评价 即完成
            if (ordersService.evaluate(orderId)) {

                return ResponseResult.success();
            }
            else {
                return ResponseResult.error("failure");
            }
        }
        else{
            return ResponseResult.error("failure");
        }
    }


    /**
     * 删除评价 逻辑删除
     * @param id 待删除的评价的主键id
     * @return ResponseResult
     */
    @PostMapping("/deleteLogic")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"评论管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"}, logical = Logical.OR)
    @RequiresPermissions(value = {"evaluates:delete:*", "evaluates:update:*", "evaluates:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "删除评价及其评价图", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "删除评价及其评价图成功"),
            @ApiResponse(code = -1, message = "删除评价及其评价图失败")
    }) // 定义响应的所有信息
    public ResponseResult deleteLogic(@NotNull(message = "删除操作必须提供待删除数据的主键id！") Long id) {

        if (evaluatesService.deleteLogic(id)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 根据主键id 查询评价
     * @param id 评价的主键id
     * @return ResponseResult
     */
    @PostMapping("/selectById")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"评论管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"}, logical = Logical.OR)
    @RequiresPermissions(value = {"evaluates:select:*", "evaluates:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "查询评价及其评价图", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询评价及其评价图成功")
    }) // 定义响应的所有信息
    public ResponseResult selectById(@NotNull(message = "精确查询操作必须提供待查询数据的主键id！") Long id){

        EvaluatesVO evaluatesVO = evaluatesService.selectByPrimaryKey(id);

        return ResponseResult.success(evaluatesVO);
    }


    /**
     * 条件分页查询评价列表
     * 因为使用layui表格的异步渲染，为符合layui的要求，将pageNum、pageSize的请求参数设为page、limit
     * @param pageNum 页码
     * @param pageSize 每页显示数目
     * @param search 模糊查询的条件
     * @return JSONObject  因为layui表格异步渲染 默认需要返回固定格式 {code:xxx, msg:xxx, count:xxx, data:[]}
     */
    @PostMapping("/list")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"评论管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"}, logical = Logical.OR)
    @RequiresPermissions(value = {"evaluates:select:*", "evaluates:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台条件查询评价分页列表", response = JSONObject.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    public JSONObject list(@RequestParam(value = "page", defaultValue = "1", required = false) Integer pageNum,
                           @RequestParam(value = "limit", defaultValue = "10", required = false) Integer pageSize,
                           @RequestParam(value = "search", required = false) String search) {

        JSONObject obj = new JSONObject();

        PageRequest<EvaluatesDTO> pageRequest = new PageRequest<>();
        pageRequest.setPageNum(pageNum);
        pageRequest.setPageSize(pageSize);
        if (search != null && !"".equals(search.trim())) {

            EvaluatesDTO evaluatesDTO = new EvaluatesDTO();
            search = search.trim();
            evaluatesDTO.setContent(search);
            pageRequest.setQuery(evaluatesDTO);
        }

        PageResult<EvaluatesVO> pageResult = evaluatesService.selectPage(pageRequest);

        obj.put("code", 0);
        obj.put("msg", "");
        obj.put("count",pageResult.getTotal());
        obj.put("data", pageResult.getData());

        return obj;
    }


    /**
     * 多选数据  批量删除评价
     * @param batchId 是所有被选中行的评价主键id，以","分隔开 组成的字符串。如"1,2,3,"
     * @return ResponseResult
     */
    @PostMapping("/batchDelete")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"评论管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"}, logical = Logical.OR)
    @RequiresPermissions(value = {"evaluates:delete:*", "evaluates:update:*", "evaluates:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台批量删除评价及其评价图", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台批量删除评价及其评价图成功"),
            @ApiResponse(code = -1, message = "后台批量删除评价及其评价图失败")
    }) // 定义响应的所有信息
    public ResponseResult batchDelete(@NotBlank String batchId) {

        // 以,分隔成存储多个主键id的 数组
        String[] array = batchId.split(",");
        boolean flag = true;
        for (String id : array) {
            boolean delete = evaluatesService.deleteLogic(Long.valueOf(id));
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
