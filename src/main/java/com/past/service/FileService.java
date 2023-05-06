package com.past.service;

import java.io.File;
import java.io.InputStream;

/**
 * 文件模块 业务层接口
 */
public interface FileService {


    /**
     * 文件上传
     * @param inputStream 文件输入流
     * @param fileName 文件名称
     * @return 文件名
     */
    String upload(InputStream inputStream, String fileName);


    /**
     * 文件上传 banner
     * @param inputStream 文件输入流
     * @param fileName 文件名称
     * @return 文件名
     */
    String uploadBannerOrGoodsImg(InputStream inputStream, String fileName);


    /**
     * 文件上传 评价图
     * @param inputStream 文件输入流
     * @param fileName 文件名称
     * @return 文件名
     */
    String uploadEvaImg(InputStream inputStream, String fileName);


    /**
     * 文件上传
     * @param file 文件对象
     */
    String upload(File file);


    /**
     * 文件上传 banner
     * @param file 文件对象
     */
    String uploadBannerOrGoodsImg(File file);


    /**
     * 文件上传 评价图
     * @param file 文件对象
     */
    String uploadEvaImg(File file);


}
