package com.past.service;

/**
 * excel导出模块 业务层接口
 */
public interface ExcelExportService {


    /**
     * 同步 导出用户数据到excel
     */
    void exportUsers();


    /**
     * 使用线程池 异步 导出用户数据到excel
     */
    void asyncExportUsers();


    /**
     * 同步 导出订单数据到excel
     */
    void exportOrders();


    /**
     * 使用线程池 异步 导出订单数据到excel
     */
    void asyncExportOrders();


}
