package com.past.controller;

import com.past.common.ResponseResult;
import com.past.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * 文件模块Controller
 */
@Controller
@RequestMapping("/api/files")
@Validated // 开启基础注解校验
@Slf4j
@Api(tags = "FileController", description = "文件管理Controller", protocols = "http, https", hidden = false) // 作用在类上，描述该类的信息（用途、接收xx协议的请求、该内容是否在页面上隐藏等等）
public class FileController {

    @Autowired
    private FileService fileService;


    /**
     * 上传用户头像
     * 1.直接没选图 使用默认 不触发上传请求
     * 2.选了几次图  又删除了选择  使用默认 不触发上传请求
     * 3.直接选一次图 不用默认图 触发上传请求
     * 4.选了多次图  又使用之前选择的图 不用默认图 触发上传请求
     * @param multipartFile 多部分文件对象
     * @return ResponseResult 通用返回结果类型
     */
    @PostMapping("/upload")
    @ResponseBody
    @ApiOperation(value = "上传头像", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "上传头像成功"),
            @ApiResponse(code = 500, message = "上传头像失败")
    }) // 定义响应的所有信息
    public ResponseResult upload(@NotNull MultipartFile multipartFile) {

        try {
            // 从multipartFile中获取输入流和原始文件名称 调用业务层的方法
            String name = fileService.upload(multipartFile.getInputStream(), multipartFile.getOriginalFilename());
            return ResponseResult.success(name);
        } catch (Exception e) {
            log.error("文件上传失败：", e);
        }

        return ResponseResult.error("failure", 500);
    }


    /**
     * 上传banner图或商品样图
     * @param multipartFile 多部分文件对象
     * @return ResponseResult
     */
    @PostMapping("/uploadBanner")
    @ResponseBody
    @ApiOperation(value = "上传Banner图或商品样图", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "上传Banner图或商品样图成功"),
            @ApiResponse(code = 500, message = "上传Banner图或商品样图失败")
    }) // 定义响应的所有信息
    public ResponseResult uploadBannerOrGoodsImg(@NotNull MultipartFile multipartFile) {

        try {
            // 从multipartFile中获取输入流和原始文件名称 调用业务层的方法
            String name = fileService.uploadBannerOrGoodsImg(multipartFile.getInputStream(), multipartFile.getOriginalFilename());
            return ResponseResult.success(name);
        } catch (Exception e) {
            log.error("文件上传失败：", e);
        }

        return ResponseResult.error("failure", 500);
    }


    /**
     * 上传评价图
     * @param multipartFile 多部分文件对象
     * @return ResponseResult
     */
    @PostMapping("/uploadEvaImg")
    @ResponseBody
    @ApiOperation(value = "上传评价图", response = ResponseResult.class, httpMethod = "POST") // 作用在方法上，描述方法的用途
    @ApiResponses({
            @ApiResponse(code = 200, message = "上传评价图成功"),
            @ApiResponse(code = 500, message = "上传评价图失败")
    }) // 定义响应的所有信息
    public ResponseResult uploadEvaImg(@NotNull MultipartFile multipartFile) {

        try {
            // 从multipartFile中获取输入流和原始文件名称 调用业务层的方法
            String name = fileService.uploadEvaImg(multipartFile.getInputStream(), multipartFile.getOriginalFilename());
            return ResponseResult.success(name);
        } catch (Exception e) {
            log.error("文件上传失败：", e);
        }

        return ResponseResult.error("failure", 500);
    }


}
