package com.past.service.impl;

import com.past.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * 文件模块 业务层接口的实现类
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {

    // 存储空间  通过fileName和存储空间  定位到文件存储的位置，通过输入流生成文件 存储指定位置
    private static final String BUCKET = "src/main/resources/static/uploads/users_imgs";
    private static final String BANNERORGOODSIMG = "src/main/resources/static/images";
    private static final String EVAIMG = "src/main/resources/static/uploads/evaluates_imgs";


    /**
     * 文件上传
     * @param inputStream 文件输入流
     * @param fileName 文件名称
     * @return 文件名
     */
    @Override
    public String upload(InputStream inputStream, String fileName) {


        // 拼接文件的存储路径
        String storagePath = BUCKET + "/" + fileName;
        File file = new File(storagePath);
        // 获取图片文件名后缀 .jpg
        String fileSuf = fileName.substring(fileName.indexOf("."));

        int num = 1;
        while (file.isFile() && file.exists()) {
            String filePre;
            // 获取图片文件名前缀
            if (!fileName.contains("_")) {
                filePre = fileName.substring(0,fileName.length() - 4);
            }
            else {
                int index = fileName.indexOf("_");
                filePre = fileName.substring(0,index);
            }
            fileName = filePre + "_" + num + fileSuf;
            storagePath = BUCKET + "/" + fileName;
            file = new File(storagePath);
            num++;
        }

        File dir = new File(BUCKET);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 使用 TWR 方式，因为其不能关闭外部资源，所以重新定义内部资源并赋值
        try (
                InputStream inner = inputStream;
                OutputStream outputStream = new FileOutputStream(file);
        ) {
            byte[] bytes = new byte[10240]; // 存储字节数据的临时缓冲区
            int content; // 读取文件的内容长度
            while ((content = inner.read(bytes, 0, bytes.length)) != -1) {
                outputStream.write(bytes, 0, content);
            }

            outputStream.flush();
            return fileName;
        } catch (Exception e) {
            log.error("文件上传失败：", e);
            throw new RuntimeException("运行时异常！", e);
        }
    }


    @Override
    public String uploadBannerOrGoodsImg(InputStream inputStream, String fileName) {

        // 拼接文件的存储路径
        String storagePath = BANNERORGOODSIMG + "/" + fileName;
        File file = new File(storagePath);
        // 获取图片文件名后缀 .jpg
        String fileSuf = fileName.substring(fileName.indexOf("."));

        int num = 1;
        while (file.isFile() && file.exists()) {
            String filePre;
            // 获取图片文件名前缀
            if (!fileName.contains("_")) {
                filePre = fileName.substring(0,fileName.length() - 4);
            }
            else {
                int index = fileName.indexOf("_");
                filePre = fileName.substring(0,index);
            }
            fileName = filePre + "_" + num + fileSuf;
            storagePath = BANNERORGOODSIMG + "/" + fileName;
            file = new File(storagePath);
            num++;
        }

        File dir = new File(BANNERORGOODSIMG);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 使用 TWR 方式，因为其不能关闭外部资源，所以重新定义内部资源并赋值
        try (
                InputStream inner = inputStream;
                OutputStream outputStream = new FileOutputStream(file);
        ) {
            byte[] bytes = new byte[10240]; // 存储字节数据的临时缓冲区
            int content; // 读取文件的内容长度
            while ((content = inner.read(bytes, 0, bytes.length)) != -1) {
                outputStream.write(bytes, 0, content);
            }

            outputStream.flush();
            return fileName;
        } catch (Exception e) {
            log.error("文件上传失败：", e);
            throw new RuntimeException("运行时异常！", e);
        }
    }


    /**
     * 文件上传 评价图
     * @param inputStream 文件输入流
     * @param fileName 文件名称
     * @return 文件名
     */
    @Override
    public String uploadEvaImg(InputStream inputStream, String fileName) {

        // 拼接文件的存储路径
        String storagePath = EVAIMG + "/" + fileName;
        File file = new File(storagePath);
        // 获取图片文件名后缀 .jpg
        String fileSuf = fileName.substring(fileName.indexOf("."));

        int num = 1;
        while (file.isFile() && file.exists()) {
            String filePre;
            // 获取图片文件名前缀
            if (!fileName.contains("_")) {
                filePre = fileName.substring(0,fileName.length() - 4);
            }
            else {
                int index = fileName.indexOf("_");
                filePre = fileName.substring(0,index);
            }
            fileName = filePre + "_" + num + fileSuf;
            storagePath = EVAIMG + "/" + fileName;
            file = new File(storagePath);
            num++;
        }

        File dir = new File(EVAIMG);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 使用 TWR 方式，因为其不能关闭外部资源，所以重新定义内部资源并赋值
        try (
                InputStream inner = inputStream;
                OutputStream outputStream = new FileOutputStream(file);
        ) {
            byte[] bytes = new byte[10240]; // 存储字节数据的临时缓冲区
            int content; // 读取文件的内容长度
            while ((content = inner.read(bytes, 0, bytes.length)) != -1) {
                outputStream.write(bytes, 0, content);
            }

            outputStream.flush();
            return fileName;
        } catch (Exception e) {
            log.error("文件上传失败：", e);
            throw new RuntimeException("运行时异常！", e);
        }
    }


    /**
     * 文件上传
     * @param file 文件对象
     */
    @Override
    public String upload(File file) {

        try {
            return upload(new FileInputStream(file), file.getName());
        } catch (Exception e) {
            log.error("文件上传失败：", e);
            throw new RuntimeException("运行时异常！", e);
        }
    }


    @Override
    public String uploadBannerOrGoodsImg(File file) {

        try {
            return uploadBannerOrGoodsImg(new FileInputStream(file), file.getName());
        } catch (Exception e) {
            log.error("文件上传失败：", e);
            throw new RuntimeException("运行时异常！", e);
        }
    }


    /**
     * 文件上传 评价图
     * @param file 文件对象
     */
    @Override
    public String uploadEvaImg(File file) {

        try {
            return uploadEvaImg(new FileInputStream(file), file.getName());
        } catch (Exception e) {
            log.error("文件上传失败：", e);
            throw new RuntimeException("运行时异常！", e);
        }
    }


}
