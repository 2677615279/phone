package com.past.controller;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.google.common.collect.Lists;
import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.common.ResponseResult;
import com.past.domain.dto.GoodsDTO;
import com.past.domain.dto.OrdersDTO;
import com.past.domain.dto.OrdersDetailDTO;
import com.past.domain.entity.Users;
import com.past.domain.vo.*;
import com.past.service.*;
import com.past.util.AlipayConfig;
import com.past.validation.UpdateValidationGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单模块Controller
 */
@Controller
@Validated // 开启基础类型数据的校验
@Slf4j
@Api(tags = "OrdersController", description = "订单管理Controller", protocols = "http, https", hidden = false) // 作用在类上，描述该类的信息（用途、接收xx协议的请求、该内容是否在页面上隐藏等等）
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private ReceivingAddressService addressService;

    @Autowired
    private CartsService cartsService;

    @Autowired
    private GuessService guessService;

    @Autowired
    private ExcelExportService excelExportService;


    /**
     * 下单 确认后会在当前页面加载猜你喜欢的商品推送列表
     * @param goodsList 购物车主键id的数组 一个元素对应购物车中一种商品，选择了几项就有几个订单详情
     * @param addressId 收货地址的主键id
     * @param model 传参模型
     * @return /user/takeorder.jsp
     */
    @PostMapping("/order/take")
    @RequiresUser
    @ApiOperation(value = "前台登录用户在购物车列表中选择商品确认下单后将请求转发到订单信息页面", httpMethod = "POST") // 作用在方法上，描述方法的用途
    public String takeOrder(@NotNull Long[] goodsList, @NotNull Long addressId, Model model) {

        List<CartsVO> cartList = Lists.newArrayList();
        List<OrdersDetailDTO> detailDTOList = Lists.newArrayList();
        double totalPrice = 0D;

        // 遍历购物车中选择下单的商品列表，依次查出该购物车对象收集到一个集合，以便后面下单成功后从购物车删除该下单列表
        for (Long i : goodsList) {
            CartsVO cartsVO = cartsService.selectByPrimaryKey(i);
            totalPrice += cartsVO.getNum() * cartsVO.getGoods().getPrice();
            cartList.add(cartsVO);

            // 构建 OrdersDetailDTO实体并收集 下单了几种商品(即购物车选择了几项)就有几个详情
            OrdersDetailDTO detailDTO = new OrdersDetailDTO();
            detailDTO.setGoodsId(cartsVO.getGoods().getId());
            detailDTO.setPrice(cartsVO.getGoods().getPrice() * cartsVO.getNum());
            detailDTO.setNum(Long.valueOf(cartsVO.getNum()));
            detailDTOList.add(detailDTO);
        }

        Date orderDate=new Date();
        ReceivingAddressVO addressVO = addressService.selectByPrimaryKey(addressId);
        String address = addressVO.getProvince() + addressVO.getCity() + addressVO.getArea() + addressVO.getDetail();

        // 获取主体信息
        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        // 构建OrdersDTO实体
        OrdersDTO ordersDTO = new OrdersDTO();
        ordersDTO.setUserId(login.getId());
        ordersDTO.setDay(orderDate);
        ordersDTO.setPrice(totalPrice);
        ordersDTO.setUsername(addressVO.getNickName());
        ordersDTO.setPhone(addressVO.getPhone());
        ordersDTO.setAddress(address);

        // 添加订单 完毕后将该订单的主键id赋值给其下的详情列表，再依次添加详情
        OrdersVO takeOrder = ordersService.take(ordersDTO);
        for (OrdersDetailDTO ordersDetailDTO : detailDTOList) {
            ordersDetailDTO.setOrderId(takeOrder.getId());
            ordersService.take(ordersDetailDTO);
        }

        // 遍历从购物车中选择下单的项  查出下单的商品信息 更新库存和销量
        for (CartsVO c : cartList) {
            GoodsVO goodsVO = goodsService.selectByPrimaryKey(c.getGoods().getId());
            // 构建GoodsDTO实体 更新库存和销量
            GoodsDTO goodsDTO = new GoodsDTO();
            BeanUtils.copyProperties(goodsVO, goodsDTO);
            goodsDTO.setNum(goodsVO.getNum() - c.getNum());
            goodsDTO.setVolume(goodsVO.getVolume() + c.getNum());

            // 更新商品库存和销量
            goodsService.update(goodsDTO);
            // 从购物车删除这一项 逻辑删除
            cartsService.deleteLogic(c.getId());
        }

        model.addAttribute("order",takeOrder);

        // 根据该用户浏览商品的点击量 获取该用户的猜想推送 根据点击量倒序排列 截取前四个
        List<GuessVO> guessVOList = guessService.selectByUserId(login.getId()).stream().limit(4).collect(Collectors.toList());
        model.addAttribute("guessList", guessVOList);

        return "/user/takeorder";
    }


    /**
     * 查询当前登录者 待付款的订单列表
     * @return  ResponseResult
     */
    @PostMapping("/api/orders/selectReadyToPay")
    @ResponseBody
    @RequiresUser
    @ApiOperation(value = "查询当前登录者待付款的订单列表", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询当前登录者待付款的订单列表")
    }) // 定义响应的所有信息
    public ResponseResult selectReadyToPay() {

        // 获取主体信息
        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        List<OrdersVO> all = ordersService.selectByUserIdWithStatus(login.getId(), 1);
        return ResponseResult.success(all);
    }


    /**
     * 查询当前登录者 待发货的订单列表
     * @return  ResponseResult
     */
    @PostMapping("/api/orders/selectReadyToDeliver")
    @ResponseBody
    @RequiresUser
    @ApiOperation(value = "查询当前登录者待发货的订单列表", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询当前登录者待发货的订单列表")
    }) // 定义响应的所有信息
    public ResponseResult selectReadyToDeliver(){

        // 获取主体信息
        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        List<OrdersVO> all = ordersService.selectByUserIdWithStatus(login.getId(), 2);
        return ResponseResult.success(all);
    }


    /**
     * 查询当前登录者 待收货的订单列表
     * @return  ResponseResult
     */
    @PostMapping("/api/orders/selectReadyToReceive")
    @ResponseBody
    @RequiresUser
    @ApiOperation(value = "查询当前登录者待收货的订单列表", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询当前登录者待收货的订单列表")
    }) // 定义响应的所有信息
    public ResponseResult selectReadyToReceive(){

        // 获取主体信息
        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        List<OrdersVO> all = ordersService.selectByUserIdWithStatus(login.getId(), 3);
        return ResponseResult.success(all);
    }


    /**
     * 查询当前登录者 待评价的订单列表
     * @return  ResponseResult
     */
    @PostMapping("/api/orders/selectReadyToEvaluate")
    @ResponseBody
    @RequiresUser
    @ApiOperation(value = "查询当前登录者待评价的订单列表", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询当前登录者待评价的订单列表")
    }) // 定义响应的所有信息
    public ResponseResult selectReadyToEvaluate(){

        // 获取主体信息
        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        List<OrdersVO> all = ordersService.selectByUserIdWithStatus(login.getId(), 4);
        return ResponseResult.success(all);
    }


    /**
     * 查询当前登录者 已完成的订单列表
     * @return  ResponseResult
     */
    @PostMapping("/api/orders/selectFinished")
    @ResponseBody
    @RequiresUser
    @ApiOperation(value = "查询当前登录者已完成的订单列表", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询当前登录者已完成的订单列表")
    }) // 定义响应的所有信息
    public ResponseResult selectFinished(){

        // 获取主体信息
        Subject subject = SecurityUtils.getSubject();
        Users login = (Users) subject.getPrincipal();

        List<OrdersVO> all = ordersService.selectByUserIdWithStatus(login.getId(), 5);
        return ResponseResult.success(all);
    }


    /**
     * 根据订单主键id 收货
     * @param id 订单主键id
     * @return ResponseResult
     */
    @PostMapping("/api/orders/receive")
    @ResponseBody
    @RequiresUser
    @ApiOperation(value = "前台登录用户确认收货", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "前台登录用户确认收货成功"),
            @ApiResponse(code = -1, message = "前台登录用户确认收货失败")
    }) // 定义响应的所有信息
    public ResponseResult receive(@NotBlank String id){

        if (ordersService.receive(id)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 根据订单主键id 删除订单 逻辑删除
     * @param id 订单主键id
     * @return ResponseResult
     */
    @PostMapping("/api/orders/deleteLogic")
    @ResponseBody
    @RequiresUser
    @ApiOperation(value = "删除订单", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "删除订单成功"),
            @ApiResponse(code = -1, message = "删除订单失败")
    }) // 定义响应的所有信息
    public ResponseResult deleteLogic(@NotBlank String id){

        if (ordersService.deleteLogic(id)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 条件分页查询订单列表
     * 因为使用layui表格的异步渲染，为符合layui的要求，将pageNum、pageSize的请求参数设为page、limit
     * @param pageNum 页码
     * @param pageSize 每页显示数目
     * @param orderIdKeyword 订单主键id
     * @param startDate 起始日
     * @param endDate 终止日
     * @param orderStatus 订单状态
     * @return JSONObject  因为layui表格异步渲染 默认需要返回固定格式 {code:xxx, msg:xxx, count:xxx, data:[]}
     */
    @PostMapping("/api/orders/list")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"订单管理员", "前台管理员", "后台管理员", "系统管理员", "超级管理员"}, logical = Logical.OR)
    @RequiresPermissions(value = {"orders:select:*", "orders:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台条件查询订单分页列表", response = JSONObject.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    public JSONObject list(@RequestParam(value = "page", defaultValue = "1", required = false) Integer pageNum,
                           @RequestParam(value = "limit", defaultValue = "10", required = false) Integer pageSize,
                           @RequestParam(value = "orderIdKeyword", required = false) String orderIdKeyword,
                           @RequestParam(value = "startDate", required = false) String startDate,
                           @RequestParam(value = "endDate", required = false) String endDate,
                           @RequestParam(value = "orderStatus", required = false) Integer orderStatus){

        JSONObject obj = new JSONObject();

        OrderSearchVO orderSearchVO = new OrderSearchVO();
        PageRequest<OrderSearchVO> pageRequest = new PageRequest<>();
        pageRequest.setPageNum(pageNum);
        pageRequest.setPageSize(pageSize);

        if (orderIdKeyword != null && !"".equals(orderIdKeyword.trim())) {
            orderSearchVO.setOrderIdKeyword(orderIdKeyword);
        }
        if (startDate != null && !"".equals(startDate.trim())) {
            orderSearchVO.setStartDate(startDate);
        }
        if (endDate != null && !"".equals(endDate.trim())) {
            orderSearchVO.setEndDate(endDate);
        }
        if (orderStatus != null) {
            orderSearchVO.setOrderStatus(orderStatus);
        }

        pageRequest.setQuery(orderSearchVO);
        PageResult<OrdersVO> pageResult = ordersService.selectPage(pageRequest);

        obj.put("code", 0);
        obj.put("msg", "");
        obj.put("count",pageResult.getTotal());
        obj.put("data", pageResult.getData());

        return obj;
    }


    /**
     * 更新订单
     * @param ordersDTO 更新后的订单DTO实体
     * @return ResponseResult
     */
    @PostMapping("/api/orders/update")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"订单管理员", "前台管理员", "后台管理员", "系统管理员", "超级管理员"}, logical = Logical.OR)
    @RequiresPermissions(value = {"orders:update:*", "orders:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台修改订单", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台修改订单成功"),
            @ApiResponse(code = -1, message = "后台修改订单失败")
    }) // 定义响应的所有信息
    public ResponseResult update(@Validated(value = {UpdateValidationGroup.class}) OrdersDTO ordersDTO) {

        if (ordersService.update(ordersDTO)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 发货  根据订单主键id 和 快递单号 设置订单已发货 待收货的状态 状态为3
     * @param id 订单主键id
     * @param expressNo 快递单号
     * @return ResponseResult
     */
    @PostMapping("/api/orders/deliver")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"订单管理员", "前台管理员", "后台管理员", "系统管理员", "超级管理员"}, logical = Logical.OR)
    @RequiresPermissions(value = {"orders:update:*", "orders:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台设置订单发货", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台设置订单发货成功"),
            @ApiResponse(code = -1, message = "后台设置订单发货失败")
    }) // 定义响应的所有信息
    public ResponseResult deliver(@NotBlank String id, @NotBlank String expressNo) {

        if (ordersService.deliver(id, expressNo)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 根据订单主键id 查询订单
     * @param id 订单主键id
     * @return ResponseResult
     */
    @PostMapping("/api/orders/selectById")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"订单管理员", "前台管理员", "后台管理员", "系统管理员", "超级管理员"}, logical = Logical.OR)
    @RequiresPermissions(value = {"orders:select:*", "orders:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台查询订单", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台查询订单成功")
    }) // 定义响应的所有信息
    public ResponseResult selectById(@NotBlank String id){

        OrdersVO ordersVO = ordersService.selectByPrimaryKey(id);

        return ResponseResult.success(ordersVO);
    }


    /**
     * 根据月分组 查询成交额 截取前5个月的记录
     * @return JSONObject
     */
    @PostMapping("/api/orders/selectTotalMoneyByMonth")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"订单管理员", "评论管理员", "轮播图管理员", "区县管理员", "城市管理员", "省份管理员", "地址管理员",
            "内存管理员", "商品分类管理员", "商品管理员", "权限管理员", "角色管理员", "用户管理员",
            "后台管理员", "前台管理员", "系统管理员", "超级管理员"}, logical = Logical.OR)
    @ApiOperation(value = "后台查询订单成交额，按月分组，截取前5个月的记录展示", response = JSONObject.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    public JSONObject selectTotalMoneyByMonth(){

        JSONObject obj=new JSONObject();
        
        // 根据月分组 查询成交额 截取前5个月的记录
        List<OrderWelcomeVO> all = ordersService.selectTotalMoneyByMonth().stream().limit(5).collect(Collectors.toList());

        // 使用stream流依次操作  将all中OrderWelcomeVO对象分别映射成其三个属性 再收集
        List<Long> total = all.stream().map(OrderWelcomeVO::getTotalMoney).collect(Collectors.toList());
        List<String> month = all.stream().map(OrderWelcomeVO::getOrderMonth).collect(Collectors.toList());
        List<Long> sheets = all.stream().map(OrderWelcomeVO::getSheets).collect(Collectors.toList());

        obj.put("month", month);
        obj.put("total", total);
        obj.put("sheets", sheets);

        return obj;
    }


    /**
     * 统计总订单量和待发货的订单量
     * @return JSONObject
     */
    @PostMapping("/api/orders/selectToDo")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"订单管理员", "评论管理员", "轮播图管理员", "区县管理员", "城市管理员", "省份管理员", "地址管理员",
            "内存管理员", "商品分类管理员", "商品管理员", "权限管理员", "角色管理员", "用户管理员",
            "后台管理员", "前台管理员", "系统管理员", "超级管理员"}, logical = Logical.OR)
    @ApiOperation(value = "后台查询总订单量和待发货的订单量", response = JSONObject.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    public JSONObject selectToDo(){

        JSONObject obj=new JSONObject();

        // 统计总订单量
        Integer totalOrder = ordersService.selectTotal();
        // 统计待发货的订单量
        Integer totalDeliver = ordersService.selectTotalDeliver();

        obj.put("total", totalOrder);
        obj.put("deliver", totalDeliver);

        return obj;
    }


    /**
     * 后台导出订单数据
     * @return ResponseResult
     */
    @PostMapping("/api/orders/export")
    @ResponseBody
    @RequiresAuthentication
    @RequiresRoles(value = {"订单管理员", "后台管理员", "前台管理员", "系统管理员", "超级管理员"},
            logical = Logical.OR)
    @RequiresPermissions(value = {"orders:*:*", "*:*:*"}, logical = Logical.OR)
    @ApiOperation(value = "后台导出订单数据", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台导出订单数据成功")
    }) // 定义响应的所有信息
    public ResponseResult export() {

        excelExportService.asyncExportOrders();

        return ResponseResult.success();
    }


    /**
     * 根据订单主键id 对其付款
     * @param id 订单主键id
     * @return ResponseResult
     */
    @PostMapping("/order/pay")
    @ResponseBody
    @RequiresUser
    @ApiOperation(value = "为订单付款(未集成支付宝，姑且暂用)", httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "后台设置订单付款成功"),
            @ApiResponse(code = -1, message = "后台设置订单付款失败")
    }) // 定义响应的所有信息
    public ResponseResult pay(@NotBlank String id){

        Subject subject = SecurityUtils.getSubject();

        // 用户付款前 走了登录流程 则可以付款
        if (subject.isAuthenticated()) {
            if (ordersService.pay(id)) {
                log.info("订单 {} 同步通知支付成功！", id);
                return ResponseResult.success();
            }
            return ResponseResult.error("failure");
        }

        // 没有登录 即使通过记住我访问也不能付款
        return ResponseResult.error("failure");
    }


    /**
     * 付款
     * @param request hhtp请求
     * @param response http响应
     */
    @PostMapping("/api/orders/notify_url")
    @RequiresUser
    @ApiOperation(value = "前台登录用户使用ZFB付款时请求的通知url，需要集成支付宝使用", httpMethod = "POST") // 作用在方法上，描述方法的用途
    public void notifyUrl(HttpServletRequest request, HttpServletResponse response){
        Map<String,String> params = new HashMap<>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            params.put(name, valueStr);
        }

        boolean signVerified=false;
        try {
            signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type);
        } catch (AlipayApiException e1) {
            e1.printStackTrace();
        } //调用SDK验证签名

        //——请在这里编写您的程序（以下代码仅作参考）——
		/* 实际验证过程建议商户务必添加以下校验：
		1、需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
		2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
		3、校验通知中的seller_id（或者seller_email) 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email）
		4、验证app_id是否为该商户本身。
		*/
        try (
                PrintWriter out = response.getWriter()
        ) {
            //验证成功
            if(signVerified) {
                //商户订单号
                String outTradeNo;
                //交易状态
                String tradeStatus;
                outTradeNo = new String(request.getParameter("out_trade_no").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

                //支付宝交易号
                String tradeNo = new String(request.getParameter("trade_no").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

                tradeStatus = new String(request.getParameter("trade_status").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

                if("TRADE_FINISHED".equals(tradeStatus)|| "TRADE_SUCCESS".equals(tradeStatus)){
                    //判断该笔订单是否在商户网站中已经做过处理
                    //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                    //如果有做过处理，不执行商户的业务程序
                    if(ordersService.pay(outTradeNo)){
                        log.info("同步通知支付成功！");
                    }
                    //注意：
                    //付款完成后，支付宝系统发送该交易状态通知
                }
                out.println("success");

            }
            else {//验证失败
                out.println("fail");

                //调试用，写文本函数记录程序运行情况是否正常
                //String sWord = AlipaySignature.getSignCheckContentV1(params);
                //AlipayConfig.logResult(sWord);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 根据交易编号 付款  并修改订单状态
     * @param outTradeNo 交易编号 即订单编号
     * @return /paysuccess.jsp
     */
    @PostMapping("/api/orders/return_url")
    @RequiresUser
    @ApiOperation(value = "前台登录用户使用ZFB付款成功后，将请求转发到付款成功页，需要集成支付宝使用", httpMethod = "POST") // 作用在方法上，描述方法的用途
    public String returnUrl(String outTradeNo){

        if(ordersService.pay(outTradeNo)){
            log.info("同步通知支付成功！");
        }

        return "/paysuccess";
    }


    /**
     * 付款后将请求转发到/paysuccess.jsp
     * @return /paysuccess
     */
    @GetMapping("/order/paysuccess")
    @RequiresUser
    @ApiOperation(value = "付款后将请求转发到成功支付页面", httpMethod = "GET") // 作用在方法上，描述方法的用途
    public String paySuccess() {
        return "/paysuccess";
    }


}
