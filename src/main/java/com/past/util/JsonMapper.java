package com.past.util;

import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.codehaus.jackson.type.TypeReference;

import java.text.SimpleDateFormat;

/**
 * Json转换工具--jackson convert
 */
@Slf4j  //日志处理
public class JsonMapper {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {

        //配置objectMapper
        objectMapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);//设置在反序列化时忽略在JSON字符串中存在，而在Java中不存在的属性
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);//如果是空对象的时候,不抛异常
        objectMapper.setFilters(new SimpleFilterProvider().setFailOnUnknownId(false));
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);//属性为null的转换
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    }


    /**
     * 对象转json字符串
     * @param src 对象实例
     * @param <T> 对象类型
     * @return json字符串
     */
    public static <T> String objToString(T src) {

        if (null == src) {
            return null;
        }
        try {
            return src instanceof String ? (String) src : objectMapper.writeValueAsString(src);
        } catch (Exception e) {
            //此处没有直接抛出异常， 当src不为空时，如果遇到return null，则知道遇到了异常
            log.warn("parse Object to String exception, error:{}", e); //使用占位符 做日志处理
            return null;
        }

    }


    /**
     * json字符串转对象
     * @param src json字符串
     * @param typeReference TypeReference实例
     * @param <T> 需要转换的目标类型
     * @return T 需要转换的目标类型
     */
    public static <T> T stringToObj(String src, TypeReference<T> typeReference) {

        if (null == src || null == typeReference) {
            return null;
        }
        try {
            return (T) (typeReference.getType().equals(String.class) ? src : objectMapper.readValue(src, typeReference));
        } catch (Exception e) {
            log.warn("parse String to Object exception, String:{}, TypeReference<T>:{}, error:{}", src, typeReference.getType(), e); //使用占位符 做日志处理
            return null;
        }

    }


}
