package com.past.service.impl;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.past.beans.PageRequest;
import com.past.beans.PageResult;
import com.past.dao.OrdersDAO;
import com.past.dao.UsersDAO;
import com.past.domain.dto.OrdersExportDTO;
import com.past.domain.dto.UsersExportDTO;
import com.past.domain.entity.Orders;
import com.past.domain.entity.Users;
import com.past.service.ExcelExportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * excel导出模块 业务层接口的实现类
 */
@Service
@Slf4j
public class ExcelExportServiceImpl implements ExcelExportService {

    @Autowired
    private UsersDAO usersDAO;

    @Autowired
    private OrdersDAO ordersDAO;


    /**
     * 执行数据库查询和excel导出，将用户数据写入到outputStream中
     * @param outputStream 输出流
     */
    private void exportUsers(OutputStream outputStream) {

        // 1.创建EasyExcel导出对象 ExcelWriter
        ExcelWriter excelWriter = EasyExcelFactory.write(outputStream, UsersExportDTO.class).build();

        // 2.查询数据库 获取待导出的数据并转换为导出DTO实体
        List<Users> all = usersDAO.selectAll();
        List<UsersExportDTO> source = Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .map(u -> {
                    UsersExportDTO usersExportDTO = new UsersExportDTO();
                    BeanUtils.copyProperties(u, usersExportDTO);
                    if (u.getStatus() == 0) {
                        usersExportDTO.setStatus("正常");
                    }
                    else if (u.getStatus() == 1) {
                        usersExportDTO.setStatus("删除");
                    }
                    else {
                        usersExportDTO.setStatus("禁用");
                    }

                    return usersExportDTO;
                })
                .collect(Collectors.toList());

        // 3.分批加载数据
        PageRequest<Object> pageRequest = new PageRequest<>();
        // excel每页显示50条数据
        pageRequest.setPageSize(50);
        int pageNum = 0;
        PageResult<UsersExportDTO> pageResult;

        do {
            // 页码先累加，再赋值
            pageRequest.setPageNum(++pageNum);
            log.info("开始导出第[ {} ]页数据！", pageNum);
            // 根据封装后的查询实体  获取当前页的查询结果，
            pageResult = new PageResult<>(pageRequest, source);

            // 4.导出分批加载的数据 将数据写入到 不同的sheet页中
            WriteSheet writeSheet = EasyExcelFactory.writerSheet(pageNum, "第" + pageNum + "页").build();
            excelWriter.write(pageResult.getData(), writeSheet);
            log.info("结束导出第[ {} ]页数据！", pageNum);

        } while (!pageResult.isLastPage()); // 总页数>当前页，即不是最后一页，说明还有数据，需要再次循环 分批加载数据到sheet页

        // 5.收尾  调用finish方法，关闭excel文件流
        excelWriter.finish();
        log.info("完成导出...");
    }


    /**
     * 同步 导出用户数据到excel
     */
    @Override
    public void exportUsers() {

        // 1.实现数据导出到excel中
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); // 初始化输出流

        exportUsers(outputStream); // 执行数据库查询和excel导出，将数据写入到outputStream中

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray()); //以输出流中转换的字节数组，初始化输入流

        // 2.实现文件上传
        String dirStr = "exports";
        // 拼接文件的存储路径
        String storagePath = dirStr + "/" + "用户导出.xlsx";
        File dir = new File(dirStr);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File excel = new File(storagePath);
        if (excel.isFile() && excel.exists()) {
            excel.delete();
        }
        // 使用 TWR 方式，因为其不能关闭外部资源，所以重新定义内部资源并赋值
        try (
                InputStream inner = inputStream;
                OutputStream out = new FileOutputStream(new File(storagePath));
        ) {
            byte[] bytes = new byte[10240]; // 存储字节数据的临时缓冲区
            int content; // 读取文件的内容长度
            while ((content = inner.read(bytes, 0, bytes.length)) != -1) {
                out.write(bytes, 0, content);
            }

            out.flush();
        } catch (Exception e) {
            log.error("文件上传失败：", e);
            throw new RuntimeException(e);
        }
    }


    /**
     * 使用线程池 异步 导出用户数据到excel
     */
    @Async(value = "executor") // 使用线程池异步
    @Override
    public void asyncExportUsers() {

        exportUsers();
    }


    /**
     * 执行数据库查询和excel导出，将订单数据写入到outputStream中
     * @param outputStream 输出流
     */
    private void exportOrders(OutputStream outputStream) {

        // 1.创建EasyExcel导出对象 ExcelWriter
        ExcelWriter excelWriter = EasyExcelFactory.write(outputStream, OrdersExportDTO.class).build();

        // 2.查询数据库 获取待导出的数据并转换为导出DTO实体
        List<Orders> all = ordersDAO.selectAll();
        List<OrdersExportDTO> source = Optional.ofNullable(all)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .map(o -> {
                    OrdersExportDTO ordersExportDTO = new OrdersExportDTO();
                    BeanUtils.copyProperties(o, ordersExportDTO);
                    if (o.getStatus() == 1) {
                        ordersExportDTO.setStatus("待付款");
                    }
                    else if (o.getStatus() == 2) {
                        ordersExportDTO.setStatus("待发货");
                    }
                    else if (o.getStatus() == 3) {
                        ordersExportDTO.setStatus("待收货");
                    }
                    else if (o.getStatus() == 4) {
                        ordersExportDTO.setStatus("待评价");
                    }
                    else if (o.getStatus() == 5) {
                        ordersExportDTO.setStatus("完成");
                    }
                    else {
                        ordersExportDTO.setStatus("删除");
                    }

                    return ordersExportDTO;
                })
                .collect(Collectors.toList());

        // 3.分批加载数据
        PageRequest<Object> pageRequest = new PageRequest<>();
        // excel每页显示50条数据
        pageRequest.setPageSize(50);
        int pageNum = 0;
        PageResult<OrdersExportDTO> pageResult;

        do {
            // 页码先累加，再赋值
            pageRequest.setPageNum(++pageNum);
            log.info("开始导出第[ {} ]页数据！", pageNum);
            // 根据封装后的查询实体  获取当前页的查询结果，
            pageResult = new PageResult<>(pageRequest, source);

            // 4.导出分批加载的数据 将数据写入到 不同的sheet页中
            WriteSheet writeSheet = EasyExcelFactory.writerSheet(pageNum, "第" + pageNum + "页").build();
            excelWriter.write(pageResult.getData(), writeSheet);
            log.info("结束导出第[ {} ]页数据！", pageNum);

        } while (!pageResult.isLastPage()); // 总页数>当前页，即不是最后一页，说明还有数据，需要再次循环 分批加载数据到sheet页

        // 5.收尾  调用finish方法，关闭excel文件流
        excelWriter.finish();
        log.info("完成导出...");
    }


    /**
     * 同步 导出订单数据到excel
     */
    @Override
    public void exportOrders() {

        // 1.实现数据导出到excel中
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); // 初始化输出流

        exportOrders(outputStream); // 执行数据库查询和excel导出，将数据写入到outputStream中

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray()); //以输出流中转换的字节数组，初始化输入流

        // 2.实现文件上传
        String dirStr = "exports";
        // 拼接文件的存储路径
        String storagePath = dirStr + "/" + "订单导出.xlsx";
        File dir = new File(dirStr);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File excel = new File(storagePath);
        if (excel.isFile() && excel.exists()) {
            excel.delete();
        }
        // 使用 TWR 方式，因为其不能关闭外部资源，所以重新定义内部资源并赋值
        try (
                InputStream inner = inputStream;
                OutputStream out = new FileOutputStream(new File(storagePath));
        ) {
            byte[] bytes = new byte[10240]; // 存储字节数据的临时缓冲区
            int content; // 读取文件的内容长度
            while ((content = inner.read(bytes, 0, bytes.length)) != -1) {
                out.write(bytes, 0, content);
            }

            out.flush();
        } catch (Exception e) {
            log.error("文件上传失败：", e);
            throw new RuntimeException(e);
        }
    }


    /**
     * 使用线程池 异步 导出订单数据到excel
     */
    @Override
    @Async(value = "executor") // 使用线程池异步
    public void asyncExportOrders() {

        exportOrders();
    }


}
