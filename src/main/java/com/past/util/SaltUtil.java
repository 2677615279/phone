package com.past.util;

import java.util.Random;

/**
 * 盐工具类
 */
public class SaltUtil {

    /**
     * 生成salt的静态方法
     * @param n 最后生成随机盐的位数
     * @return String
     */
    public static String getSalt(int n){

        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            int index = new Random().nextInt(chars.length);//从[0,chars.length)范围内 随机出一个整数作为索引
            char c = chars[index]; //取出该索引对应的字符
            sb.append(c); //使用StringBuilder把每次循环取出的字符进行拼接
        }
        return sb.toString();
    }

}
