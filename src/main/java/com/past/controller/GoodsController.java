package com.past.controller;

import com.alibaba.fastjson.JSONObject;
import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.common.ResponseResult;
import com.past.domain.dto.GoodsDTO;
import com.past.domain.dto.GuessDTO;
import com.past.domain.entity.Users;
import com.past.domain.vo.EvaluatesVO;
import com.past.domain.vo.GoodsVO;
import com.past.domain.vo.GuessVO;
import com.past.domain.vo.SearchVO;
import com.past.service.GoodsService;
import com.past.service.GuessService;
import com.past.validation.InsertValidationGroup;
import com.past.validation.UpdateValidationGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品模块Controller
 */
@Controller
@Validated // 开启基础类型数据的校验
@Slf4j
@Api(tags = "GoodsController", description = "商品管理Controller", protocols = "http, https", hidden = false) // 作用在类上，描述该类的信息（用途、接收xx协议的请求、该内容是否在页面上隐藏等等）
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GuessService guessService;


    /**
     * 添加商品 只要有insert操作，会把type-with-goods-cache缓存下的所有数据清空
     * @param goodsDTO 待添加的商品DTO实体
     * @return ResponseResult
     */
    @PostMapping("/api/goods/insert")
    @ResponseBody
    @CacheEvict(cacheNames = "type-with-goods-cache", allEntries = true)
    @RequiresAuthentication
    @RequiresRoles(value = {"商品管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"}, logical = Logical.OR)
    @RequiresPermissions(value = {"goods:insert:*", "goods:update:*", "goods:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台添加商品", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台添加商品成功"),
            @ApiResponse(code = -1, message = "后台添加商品失败")
    }) // 定义响应的所有信息
    public ResponseResult insert(@Validated(value = InsertValidationGroup.class) GoodsDTO goodsDTO) {

        if (goodsService.insert(goodsDTO)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 删除商品 逻辑删除 只要有deleteLogic操作，会把type-with-goods-cache缓存下的所有数据清空
     * @param id 待删除的商品的主键id
     * @return ResponseResult
     */
    @PostMapping("/api/goods/deleteLogic")
    @ResponseBody
    @CacheEvict(cacheNames = "type-with-goods-cache", allEntries = true)
    @RequiresAuthentication
    @RequiresRoles(value = {"商品管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"}, logical = Logical.OR)
    @RequiresPermissions(value = {"goods:delete:*", "goods:update:*", "goods:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台删除商品", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台删除商品成功"),
            @ApiResponse(code = -1, message = "后台删除商品失败")
    }) // 定义响应的所有信息
    public ResponseResult deleteLogic(@NotNull(message = "删除操作必须提供待删除数据的主键id！") Long id) {

        if (goodsService.deleteLogic(id)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 更新商品 只要有update操作，会把type-with-goods-cache缓存下的所有数据清空
     * @param goodsDTO 更新后的商品DTO实体
     * @return ResponseResult
     */
    @PostMapping("/api/goods/update")
    @ResponseBody
    @CacheEvict(cacheNames = "type-with-goods-cache", allEntries = true)
    @RequiresAuthentication
    @RequiresRoles(value = {"商品管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"}, logical = Logical.OR)
    @RequiresPermissions(value = {"goods:update:*", "goods:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台修改商品", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台修改商品成功"),
            @ApiResponse(code = -1, message = "后台修改商品失败")
    }) // 定义响应的所有信息
    public ResponseResult update(@Validated(value = UpdateValidationGroup.class) GoodsDTO goodsDTO) {

        if (goodsService.update(goodsDTO)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 条件分页查询商品列表
     * 因为使用layui表格的异步渲染，为符合layui的要求，将pageNum、pageSize的请求参数设为page、limit
     * @param pageNum 页码
     * @param pageSize 每页显示数目
     * @param search 模糊查询的条件
     * @return JSONObject  因为layui表格异步渲染 默认需要返回固定格式 {code:xxx, msg:xxx, count:xxx, data:[]}
     */
    @PostMapping("/api/goods/list")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"商品管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"}, logical = Logical.OR)
    @RequiresPermissions(value = {"goods:select:*", "goods:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台条件查询商品分页列表", response = JSONObject.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    public JSONObject list(@RequestParam(value = "page", defaultValue = "1", required = false) Integer pageNum,
                           @RequestParam(value = "limit", defaultValue = "10", required = false) Integer pageSize,
                           @RequestParam(value = "search", required = false) String search) {

        JSONObject obj = new JSONObject();

        PageRequest<GoodsDTO> pageRequest = new PageRequest<>();
        pageRequest.setPageNum(pageNum);
        pageRequest.setPageSize(pageSize);
        if (search != null && !"".equals(search.trim())) {

            GoodsDTO goodsDTO = new GoodsDTO();
            search = search.trim();
            goodsDTO.setName(search);
            pageRequest.setQuery(goodsDTO);
        }

        PageResult<GoodsVO> pageResult = goodsService.selectPage(pageRequest);

        obj.put("code", 0);
        obj.put("msg", "");
        obj.put("count",pageResult.getTotal());
        obj.put("data", pageResult.getData());

        return obj;
    }


    /**
     * 根据类别 查询商品列表
     * @param typeId 关联类别的主键id
     * @return ResponseResult
     */
    @PostMapping("/api/goods/selectByType")
    @ResponseBody
    @ApiOperation(value = "根据商品类型查询商品列表", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "根据商品类型查询商品列表成功")
    }) // 定义响应的所有信息
    public ResponseResult selectByType(Long typeId) {

        List<GoodsVO> all = goodsService.selectByType(typeId);

        return ResponseResult.success(all);
    }


    /**
     * 查询热卖商品列表
     * @return ResponseResult
     */
    @PostMapping("/api/goods/selectByHot")
    @ResponseBody
    @RequiresUser
    @ApiOperation(value = "根据商品类型查询商品列表", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询热卖商品列表，截取前4个展示")
    }) // 定义响应的所有信息
    public ResponseResult selectByHot() {

        // 查询热卖商品列表 截取前4个
        List<GoodsVO> all = goodsService.selectByHot().stream().limit(4).collect(Collectors.toList());

        return ResponseResult.success(all);
    }


    /**
     * 点击 查找商品按钮时  跳转到查找页
     * @return modelAndView
     */
    @PostMapping("/goods/searchView")
    @ApiOperation(value = "前台点击查找商品按钮时，将请求转发到商品条件查找页",  httpMethod = "POST") // 作用在方法上，描述方法的用途
    public ModelAndView searchView() {

        // 查询所有商品
        List<GoodsVO> all = goodsService.selectAll();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("searchList", all);
        modelAndView.setViewName("/user/search");

        return modelAndView;
    }


    /**
     * 根据封装名称、价格区间的实体 查询符合条件的商品列表
     * @param searchVO 封装名称、价格区间的实体
     * @return ResponseResult
     */
    @PostMapping("/api/goods/search")
    @ResponseBody
    @ApiOperation(value = "前台条件查询商品列表", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "前台条件查询商品列表成功")
    }) // 定义响应的所有信息
    public ResponseResult search(SearchVO searchVO) {

        // 根据用户输入的 名称、价格区间 查询符合条件的商品列表
        List<GoodsVO> all = goodsService.selectByCondition(searchVO);

        return ResponseResult.success(all);
    }


    /**
     * 多选数据  批量删除商品 只要有update操作，会把type-with-goods-cache缓存下的所有数据清空
     * @param batchId 是所有被选中行的商品主键id，以","分隔开 组成的字符串。如"1,2,3,"
     * @return ResponseResult
     */
    @PostMapping("/api/goods/batchDelete")
    @ResponseBody
    @CacheEvict(cacheNames = "type-with-goods-cache", allEntries = true)
    @RequiresAuthentication
    @RequiresRoles(value = {"商品管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"}, logical = Logical.OR)
    @RequiresPermissions(value = {"goods:delete:*", "goods:update:*", "goods:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台批量删除商品", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台批量删除商品成功"),
            @ApiResponse(code = -1, message = "后台批量删除商品失败")
    }) // 定义响应的所有信息
    public ResponseResult batchDelete(@NotBlank String batchId) {

        // 以,分隔成存储多个主键id的 数组
        String[] array = batchId.split(",");
        boolean flag = true;
        for (String id : array) {
            boolean delete = goodsService.deleteLogic(Long.valueOf(id));
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
     * 根据销量 倒序查询
     * @return JSONObject
     */
    @PostMapping("/api/goods/selectByVolume")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"订单管理员", "评论管理员", "轮播图管理员", "区县管理员", "城市管理员", "省份管理员", "地址管理员",
            "内存管理员", "商品分类管理员", "商品管理员", "权限管理员", "角色管理员", "用户管理员",
            "后台管理员", "前台管理员", "系统管理员", "超级管理员"}, logical = Logical.OR)
    @ApiOperation(value = "按销量倒序查询商品列表，截取前5个展示", response = JSONObject.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    public JSONObject selectByVolume() {

        // 根据销量 倒序查询 截取前5个
        List<GoodsVO> all = goodsService.selectByVolume().stream().limit(5).collect(Collectors.toList());
        // 分别获取结果集中的 name 和 volume 属性  组成一个新集合
        List<String> name = all.stream().map(GoodsVO::getName).collect(Collectors.toList());
        List<Long> volume = all.stream().map(GoodsVO::getVolume).collect(Collectors.toList());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("volume", volume);

        return jsonObject;
    }


    /**
     * 根据商品主键id 获取信息、评论等，将其转发到产品详情页  另：根据商品点击事件 添加或更新猜想推送
     * @param id 商品主键id
     * @param model 传参模型
     * @return 产品详情页
     */
    @GetMapping("/goods/detail/{id}")
    @RequiresUser
    @ApiOperation(value = "前台登录用户点击商品样图后，将请求转发到商品详情页", httpMethod = "GET") // 作用在方法上，描述方法的用途
    public String detail(@PathVariable("id") @NotNull Long id, Model model) {

        // 根据商品主键id 查询商品(含重要信息和评论、图)
        GoodsVO goodsVO = goodsService.selectByPrimaryKey(id);
        // 获取该商品的评论列表 含图
        List<EvaluatesVO> evaVOList = goodsVO.getEvaluatesVOList();

        model.addAttribute("goods",goodsVO);
        model.addAttribute("evaList", evaVOList);

        // 获取主体对象
        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();
        // 获取该用户有关该商品的猜想推送
        GuessVO guessVO = guessService.selectByUserIdWithGoodsId(login.getId(), goodsVO.getId());
        if (!ObjectUtils.isEmpty(guessVO)) {
            // 构造guessDTO实体
            GuessDTO guessDTO = new GuessDTO();
            guessDTO.setId(guessVO.getId());
            guessDTO.setGoodsId(guessVO.getGoods().getId());
            guessDTO.setUserId(guessVO.getUser().getId());
            guessDTO.setFavorite(guessVO.getFavorite());
            guessDTO.setNum(guessVO.getNum() + 1);

            // 更新
            guessService.update(guessDTO);
        }
        else {
            // 构造guessDTO实体
            GuessDTO guessDTO = new GuessDTO();
            guessDTO.setGoodsId(goodsVO.getId());
            guessDTO.setUserId(login.getId());
            guessDTO.setFavorite(-1);
            guessDTO.setNum(1);

            // 新增
            guessService.insert(guessDTO);
        }

        return "/user/productdetail";
    }


}
