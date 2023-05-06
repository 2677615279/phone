package com.past.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 订单id 工具类
 */
public class OrderCodeUtil {

    /**
     * 订单编号（核心部分）
     */
    private static long code;

    /**
     * 生成订单编号
     * @return 订单编号
     */
    public static synchronized String getOrderCode() {
        code++;
        String str = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        long m = Long.parseLong((str)) * 10000;
        m += code;
        return String.valueOf(m);
    }
    
    public static void main(String[] args) {
        System.out.println(OrderCodeUtil.getOrderCode());
    }

}