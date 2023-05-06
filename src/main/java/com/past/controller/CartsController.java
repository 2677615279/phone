package com.past.controller;

import com.google.common.collect.Lists;
import com.past.common.ResponseResult;
import com.past.domain.dto.CartsDTO;
import com.past.domain.entity.Users;
import com.past.domain.vo.CartsVO;
import com.past.domain.vo.GoodsVO;
import com.past.domain.vo.ReceivingAddressVO;
import com.past.service.CartsService;
import com.past.service.GoodsService;
import com.past.service.ReceivingAddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 购物车模块Controller
 */
@Controller
@Validated // 开启基础类型数据的校验
@Slf4j
@Api(tags = "CartsController", description = "购物车管理Controller", protocols = "http, https", hidden = false) // 作用在类上，描述该类的信息（用途、接收xx协议的请求、该内容是否在页面上隐藏等等）
public class CartsController {

    @Autowired
    private CartsService cartsService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private ReceivingAddressService addressService;


    /**
     * 添加商品到购物车 从用户中心页面收藏中心的商品中添加购物车，默认数量为1 从商品详情中加入购物车，可以自选数量
     * @param goodsId 商品主键id
     * @param num 该商品数量
     * @return ResponseResult
     * @return ResponseResult
     */
    @PostMapping("/api/carts/insert")
    @ResponseBody
    @RequiresUser
    @ApiOperation(value = "添加商品到购物车列表", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "添加商品到购物车列表成功"),
            @ApiResponse(code = -1, message = "添加商品到购物车列表失败")
    }) // 定义响应的所有信息
    public ResponseResult insert(@NotNull Long goodsId, @NotNull Integer num) {

        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        CartsVO cartsVO = cartsService.selectByUserIdWithGoodsId(login.getId(), goodsId);
        if(cartsVO != null){
            // 构建CartsDTO实体 执行更新操作
            CartsDTO cartsDTO = new CartsDTO();
            cartsDTO.setId(cartsVO.getId());
            cartsDTO.setGoodsId(cartsVO.getGoods().getId());
            cartsDTO.setNum(cartsVO.getNum() + num);
            cartsDTO.setPrice(cartsVO.getPrice());
            cartsDTO.setUserId(cartsVO.getUser().getId());

            if (cartsService.update(cartsDTO)) {
                return ResponseResult.success();
            }
            else {
                return ResponseResult.error("failure");
            }
        }
        else {
            GoodsVO goodsVO = goodsService.selectByPrimaryKey(goodsId);
            // 构建CartsDTO实体 执行新增操作
            CartsDTO cartsDTO = new CartsDTO();
            cartsDTO.setGoodsId(goodsVO.getId());
            cartsDTO.setNum(num);
            cartsDTO.setPrice(goodsVO.getPrice());
            cartsDTO.setUserId(login.getId());

            if (cartsService.insert(cartsDTO)) {
                return ResponseResult.success();
            } else {
                return ResponseResult.error("failure");
            }
        }
    }


    /**
     * 查询当前用户的购物车列表
     * @return ResponseResult
     */
    @PostMapping("/api/carts/selectByUser")
    @ResponseBody
    @RequiresUser
    @ApiOperation(value = "查询登录用户的购物车列表", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "添加商品到购物车列表成功")
    }) // 定义响应的所有信息
    public ResponseResult selectByUser() {

        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        List<CartsVO> all = cartsService.selectByUserId(login.getId());

        return ResponseResult.success(all);
    }


    /**
     * 根据主键id 删除购物车 逻辑删除
     * @param id 购物车主键id
     * @return ResponseResult
     */
    @PostMapping("/api/carts/deleteLogic")
    @ResponseBody
    @RequiresUser
    @ApiOperation(value = "将某商品从购物车列表中删除", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "将某商品从购物车列表中删除成功"),
            @ApiResponse(code = -1, message = "将某商品从购物车列表中删除失败")
    }) // 定义响应的所有信息
    public ResponseResult deleteLogic(@NotNull(message = "删除操作必须提供待删除数据的主键id！") Long id) {

        if (cartsService.deleteLogic(id)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 根据主键id 递减该购物车中商品的数量
     * @param id 购物车主键id
     * @return ResponseResult
     */
    @PostMapping("/api/carts/reduceCartNum")
    @ResponseBody
    @RequiresUser
    @ApiOperation(value = "递减购物车列表中某商品的数量", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "递减购物车列表中某商品的数量成功"),
            @ApiResponse(code = -1, message = "递减购物车列表中某商品的数量失败")
    }) // 定义响应的所有信息
    public ResponseResult reduceCartNum(@NotNull Long id) {

        CartsVO cartsVO = cartsService.selectByPrimaryKey(id);

        // 构建CartsDTO实体 执行更新操作
        CartsDTO cartsDTO = new CartsDTO();
        cartsDTO.setId(cartsVO.getId());
        cartsDTO.setGoodsId(cartsVO.getGoods().getId());
        cartsDTO.setNum(cartsVO.getNum() - 1);
        cartsDTO.setPrice(cartsVO.getPrice());
        cartsDTO.setUserId(cartsVO.getUser().getId());

        if (cartsService.update(cartsDTO)) {
            return ResponseResult.success();
        }
        else {
            return ResponseResult.error("failure");
        }
    }


    /**
     * 根据主键id 递增该购物车中商品的数量
     * @param id 购物车主键id
     * @return ResponseResult
     */
    @PostMapping("/api/carts/addCartNum")
    @ResponseBody
    @RequiresUser
    @ApiOperation(value = "递增购物车列表中某商品的数量", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "递增购物车列表中某商品的数量成功"),
            @ApiResponse(code = -1, message = "递增购物车列表中某商品的数量失败")
    }) // 定义响应的所有信息
    public ResponseResult addCartNum(@NotNull Long id) {

        CartsVO cartsVO = cartsService.selectByPrimaryKey(id);

        // 构建CartsDTO实体 执行更新操作
        CartsDTO cartsDTO = new CartsDTO();
        cartsDTO.setId(cartsVO.getId());
        cartsDTO.setGoodsId(cartsVO.getGoods().getId());
        cartsDTO.setNum(cartsVO.getNum() + 1);
        cartsDTO.setPrice(cartsVO.getPrice());
        cartsDTO.setUserId(cartsVO.getUser().getId());

        if (cartsService.update(cartsDTO)) {
            return ResponseResult.success();
        }
        else {
            return ResponseResult.error("failure");
        }
    }


    /**
     * 预购商品 跳转确认订单页面
     * @param goodsList 购物车主键id列表
     * @param model 传参模型
     * @return /user/confirmorder.jsp
     */
    @PostMapping("/carts/preOrder")
    @RequiresUser
    @ApiOperation(value = "预购商品，将请求转发到确认订单页面", httpMethod = "POST") // 作用在方法上，描述方法的用途
    public String preOrder(@NotNull Long[] goodsList, Model model){

        List<CartsVO> all = Lists.newArrayList();
        for (Long i : goodsList) {
            CartsVO cartsVO = cartsService.selectByPrimaryKey(i);
            all.add(cartsVO);
        }
        model.addAttribute("cartList", all);

        List<ReceivingAddressVO> addressVOList = addressService.selectByUserId();
        model.addAttribute("addrList", addressVOList);

        return "/user/confirmorder";
    }


}
