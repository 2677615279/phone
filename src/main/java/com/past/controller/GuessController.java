package com.past.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.common.ResponseResult;
import com.past.domain.entity.Users;
import com.past.domain.vo.GoodsVO;
import com.past.domain.vo.GuessVO;
import com.past.service.GoodsService;
import com.past.service.GuessService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 猜想推送模块Controller  按照商品点击量定制猜你喜欢
 */
@Controller
@RequestMapping("/api/guess")
@Validated // 开启基础类型数据的校验
@Slf4j
@Api(tags = "GuessController", description = "猜想推送管理Controller", protocols = "http, https", hidden = false) // 作用在类上，描述该类的信息（用途、接收xx协议的请求、该内容是否在页面上隐藏等等）
public class GuessController {

    @Autowired
    private GuessService guessService;

    @Autowired
    private GoodsService goodsService;


    /**
     * 根据页码 查询当前登录用户收藏的商品列表 展示在用户中心 收藏中心
     * @param pageNum 页码
     * @param pageSize 每页最多显示数目
     * @return JSONObject
     */
    @PostMapping("/selectFavoriteGoods")
    @ResponseBody
    @RequiresUser
    @ApiOperation(value = "条件查询猜想推送分页列表", response = JSONObject.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    public JSONObject selectFavoriteGoods(@RequestParam(value = "page", defaultValue = "1", required = false) Integer pageNum,
                                          @RequestParam(value = "limit", defaultValue = "8", required = false) Integer pageSize) {

        JSONObject obj = new JSONObject();
        
        // 构造分页查询实体
        PageRequest<Object> pageRequest = new PageRequest<>();
        pageRequest.setPageNum(pageNum);
        pageRequest.setPageSize(pageSize);
        pageRequest.setQuery(null);

        // 获取主体对象
        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        PageResult<GuessVO> pageResult = guessService.selectFavoriteByUserId(login.getId(), pageRequest);

        obj.put("code", 0);
        obj.put("msg", "");
        obj.put("count",pageResult.getTotal());
        obj.put("data", pageResult.getData());

        return obj;
    }


    /**
     * 将关联的商品主键id对应的商品 添加到当前登录用户的收藏中 即根据用户主键id和商品主键id查到该数据，修改其favorite字段为1，即收藏
     * 当收藏完毕后 会在当前页面加载热卖推荐的商品
     * @param goodsId 商品的主键id
     * @return ResponseResult
     */
    @PostMapping("/insertFavorite")
    @ResponseBody
    @RequiresUser
    @ApiOperation(value = "收藏商品", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "收藏商品成功"),
            @ApiResponse(code = -1, message = "收藏商品失败")
    }) // 定义响应的所有信息
    public ResponseResult insertFavorite(@NotNull Long goodsId) {

        // 获取主体对象
        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        // 判断是否完成了 根据用户主键id和商品主键id查到该数据，修改其favorite字段为1
        if (guessService.insertFavorite(login.getId(), goodsId)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 将关联的商品主键id对应的商品 从当前登录用户的收藏中移除 即根据用户主键id和商品主键id查到该数据，修改其favorite字段为-1，即取消收藏
     * @param goodsId 商品的主键id
     * @return ResponseResult
     */
    @PostMapping("/removeFavorite")
    @ResponseBody
    @RequiresUser
    @ApiOperation(value = "取消收藏商品", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "取消收藏商品成功"),
            @ApiResponse(code = -1, message = "取消收藏商品失败")
    }) // 定义响应的所有信息
    public ResponseResult removeFavorite(@NotNull Long goodsId) {

        // 获取主体对象
        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        // 判断是否完成了 根据用户主键id和商品主键id查到该数据，修改其favorite字段为1
        if (guessService.removeFavorite(login.getId(), goodsId)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 根据关联的商品的主键id 判断该用户是否已收藏了该商品
     * @param goodsId 商品的主键id
     * @return Boolean
     */
    @PostMapping("/isFavorite")
    @ResponseBody
    @RequiresUser
    @ApiOperation(value = "判断登录用户是否已收藏了该商品", response = Boolean.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    public Boolean isFavorite(@NotNull Long goodsId) {

        // 获取主体对象
        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        // 根据当前登录用户的主键id 和 商品的主键id  查询到猜想推送
        GuessVO guessVO = guessService.selectByUserIdWithGoodsId(login.getId(), goodsId);
        if (guessVO == null) {
            return Boolean.FALSE;
        }

        // 判断其是否已对该商品收藏
        if (guessVO.getFavorite() > 0) {

            return Boolean.TRUE;
        }
        else {
            return Boolean.FALSE;
        }
    }


    /**
     * 查询登录者最近浏览的未收藏的商品猜想推送列表  展示在用户中心 猜你喜欢
     * @return ResponseResult
     */
    @PostMapping("/selectRecentViewGoods")
    @ResponseBody
    @RequiresUser
    @ApiOperation(value = "查询登录用户最近浏览的未收藏的商品猜想推送列表，截取前8个展示", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询登录用户最近浏览的未收藏的商品猜想推送列表")
    }) // 定义响应的所有信息
    public ResponseResult selectRecentViewGoods() {

        // 获取主体对象
        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        // 获取登录用户 最近浏览的未收藏的商品猜想推送列表 按点击量倒序排列 截取前8个
        List<GuessVO> all = guessService.selectRecentViewGoodsByUserId(login.getId()).stream().limit(8).collect(Collectors.toList());

        return ResponseResult.success(all);
    }


    /**
     * 查看浏览量最高的前6个商品
     * @return JSONArray
     */
    @PostMapping("/selectMostHotGoods")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"订单管理员", "评论管理员", "轮播图管理员", "区县管理员", "城市管理员", "省份管理员", "地址管理员",
            "内存管理员", "商品分类管理员", "商品管理员", "权限管理员", "角色管理员", "用户管理员",
            "后台管理员", "前台管理员", "系统管理员", "超级管理员"}, logical = Logical.OR)
    @ApiOperation(value = "后台查询浏览量较高的商品列表，截取前6个展示", response = JSONArray.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    public JSONArray selectMostHotGoods() {

        JSONArray arr=new JSONArray();

        // 统计商品主键id 和 商品浏览次数 截取前6个
        List<Map<String, Object>> list = guessService.selectMostHotGoods().stream().limit(6).collect(Collectors.toList());

        for (Map<String, Object> map : list) {
            JSONObject obj=new JSONObject();
            Long goodsId = (Long) map.get("g");
            Number num = (Number) map.get("num");
            GoodsVO goodsVO = goodsService.selectByPrimaryKey(goodsId);
            obj.put("name", goodsVO.getName());
            obj.put("num", num.longValue());
            arr.add(obj);
        }

        return arr;
    }


}
