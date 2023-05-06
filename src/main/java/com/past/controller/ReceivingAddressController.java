package com.past.controller;

import com.past.common.ResponseResult;
import com.past.domain.dto.ReceivingAddressDTO;
import com.past.domain.vo.AreasVO;
import com.past.domain.vo.CitiesVO;
import com.past.domain.vo.ProvincesVO;
import com.past.domain.vo.ReceivingAddressVO;
import com.past.service.AreasService;
import com.past.service.CitiesService;
import com.past.service.ProvincesService;
import com.past.service.ReceivingAddressService;
import com.past.validation.InsertValidationGroup;
import com.past.validation.UpdateValidationGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 收货地址模块Controller
 */
@Controller
@RequestMapping("/api/receivingaddress")
@Validated // 开启基础类型数据的校验
@Slf4j
@Api(tags = "ReceivingAddressController", description = "收货地址管理Controller", protocols = "http, https", hidden = false) // 作用在类上，描述该类的信息（用途、接收xx协议的请求、该内容是否在页面上隐藏等等）
public class ReceivingAddressController {

    @Autowired
    private ReceivingAddressService addressService;

    @Autowired
    private ProvincesService provincesService;

    @Autowired
    private CitiesService citiesService;

    @Autowired
    private AreasService areasService;


    /**
     * 添加收货地址
     * @param receivingAddressDTO 待添加的收货地址
     * @return ResponseResult
     */
    @PostMapping("/insert")
    @ResponseBody
    @RequiresUser
    @ApiOperation(value = "添加收货地址", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "添加收货地址成功"),
            @ApiResponse(code = -1, message = "添加收货地址失败")
    }) // 定义响应的所有信息
    public ResponseResult insert(@Validated(value = InsertValidationGroup.class) ReceivingAddressDTO receivingAddressDTO) {

        if (addressService.insert(receivingAddressDTO)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 删除收货地址 逻辑删除
     * @param id 待删除的收货地址的主键id
     * @return ResponseResult
     */
    @PostMapping("/deleteLogic")
    @ResponseBody
    @RequiresUser
    @ApiOperation(value = "删除收货地址", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "删除收货地址成功"),
            @ApiResponse(code = -1, message = "删除收货地址失败")
    }) // 定义响应的所有信息
    public ResponseResult deleteLogic(@NotNull(message = "删除操作必须提供待删除数据的主键id！") Long id) {

        if (addressService.deleteLogic(id)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 更新收货地址
     * @param addressDTO 更新后的收货地址
     * @return ResponseResult
     */
    @PostMapping("/updateSelf")
    @ResponseBody
    @RequiresUser
    @ApiOperation(value = "修改收货地址", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "修改收货地址成功"),
            @ApiResponse(code = -1, message = "修改收货地址失败")
    }) // 定义响应的所有信息
    public ResponseResult updateSelf(@Validated(value = UpdateValidationGroup.class) ReceivingAddressDTO addressDTO) {

        if (addressService.updateSelf(addressDTO)) {

            return ResponseResult.success();
        }

        return ResponseResult.error("failure");
    }


    /**
     * 查询收货地址
     * @param id 原收货地址的主键id
     * @return ResponseResult
     */
    @PostMapping("/selectById")
    @ResponseBody
    @RequiresUser
    @ApiOperation(value = "查询收货地址", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询收货地址成功")
    }) // 定义响应的所有信息
    public ResponseResult selectById(@NotNull(message = "精确查询操作必须提供待查询数据的主键id！") Long id) {

        // 查出收货地址的省市区都是name，要根据name查出省市区id 重新赋值
        ReceivingAddressVO vo = addressService.selectByPrimaryKey(id);

        ProvincesVO province = provincesService.selectByName(vo.getProvince());
        CitiesVO city = citiesService.selectByNameWithProvinceId(vo.getCity(), province.getId());
        AreasVO area = areasService.selectByNameWithCityId(vo.getArea(), city.getId());

        vo.setProvince(String.valueOf(province.getId()));
        vo.setCity(String.valueOf(city.getId()));
        vo.setArea(String.valueOf(area.getId()));

        return ResponseResult.success(vo);
    }


    /**
     * 根据当前登录的用户 查询其下的所有收货地址
     * @return ResponseResult
     */
    @PostMapping("/selectBySelf")
    @ResponseBody
    @RequiresUser
    @ApiOperation(value = "查询当前登录用户的收货地址列表", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询当前登录用户的收货地址列表成功")
    }) // 定义响应的所有信息
    public ResponseResult selectBySelf() {

        // 根据登录者主键id 查询他的所有收货地址
        List<ReceivingAddressVO> voList = addressService.selectByUserId();

        return ResponseResult.success(voList);
    }


    /**
     * 添加地址前的必要工作：获取所有可选省份
     * @return ResponseResult
     */
    @PostMapping("/selectAllProvince")
    @ResponseBody
    @RequiresUser
    @ApiOperation(value = "添加地址前的必要工作，查询可选省份列表", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "添加地址前的必要工作，查询可选省份列表成功")
    }) // 定义响应的所有信息
    public ResponseResult selectAllProvince() {

        List<ProvincesVO> all = provincesService.selectAll();

        return ResponseResult.success(all);
    }


    /**
     * 添加地址前的必要工作：根据选择的省份 获取其下的城市
     * @param provinceId 省份的主键id
     * @return ResponseResult
     */
    @PostMapping("/selectCityByProvinceId")
    @ResponseBody
    @RequiresUser
    @ApiOperation(value = "添加地址前的必要工作，查询某省份下的可选城市列表", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "添加地址前的必要工作，查询某省份下的可选城市列表成功")
    }) // 定义响应的所有信息
    public ResponseResult selectCityByProvinceId(@NotNull Long provinceId) {

        List<CitiesVO> all = citiesService.selectByProvinceId(provinceId);

        return ResponseResult.success(all);
    }


    /**
     * 添加地址前的必要工作：根据选择的城市 获取其下的区县
     * @param cityId 城市的主键id
     * @return ResponseResult
     */
    @PostMapping("/selectAreaByCityId")
    @ResponseBody
    @RequiresUser
    @ApiOperation(value = "添加地址前的必要工作，查询某城市下的可选区县列表", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "添加地址前的必要工作，查询某城市下的可选区县列表成功")
    }) // 定义响应的所有信息
    public ResponseResult selectAreaByCityId(@NotNull Long cityId) {

        List<AreasVO> all = areasService.selectByCityId(cityId);

        return ResponseResult.success(all);
    }


}