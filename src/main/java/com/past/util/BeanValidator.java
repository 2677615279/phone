package com.past.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.past.exception.ParamException;
import org.apache.commons.collections.MapUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

/**
 * 校验工具--Validator
 */
public class BeanValidator {

    //全局校验工厂
    private static ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();


    /**
     * 校验单个实体对象，返回值Map<String,String>   key是String类型，表示校验有问题的字段，value也是String类型，表示该字段的错误信息
     * @param t
     * @param groups
     * @param <T>
     * @return
     */
    public static <T>Map<String,String> validate(T t, Class<?>... groups){

        Validator validator = validatorFactory.getValidator();//根据校验工厂 获取校验对象validator
        //根据校验对象validator获取校验结果
        Set validateResult = validator.validate(t,groups);

        //校验没错误,返回空map
        if (validateResult.isEmpty()){
            return Collections.emptyMap();
        } else { //校验有错误
            //初始化LinkedHashMap
            LinkedHashMap<String,String> errors = Maps.newLinkedHashMap();
            //获取迭代器
            Iterator iterator = validateResult.iterator();
            while (iterator.hasNext()){
                ConstraintViolation violation = (ConstraintViolation) iterator.next();
                //将校验错误字段 和 错误信息 填入map中
                errors.put(violation.getPropertyPath().toString(),violation.getMessage());
            }
            return errors;
        }

    }


    /**
     * 对集合和集合中的对象数据校验
     * @param collection
     * @return
     */
    public static Map<String, String> validateList(Collection<?> collection) {

        //判断参数是否为空
        Preconditions.checkNotNull(collection);
        //不为空
        Iterator iterator = collection.iterator();
        Map<String, String> errors;

        do {
            if (!iterator.hasNext()) { //如果集合中没有数据
                return Collections.emptyMap();//返回空map
            }
            //集合不为空，获取当前迭代的值
            Object object = iterator.next();
            errors = validate(object, (Class<?>[]) null); //调用上面封装的校验单个对象的方法
        } while (errors.isEmpty()); //校验后的错误结果集为空，再次校验，否则return

        return errors;

    }


    /**
     * 校验任何对象，对上面2个方法进行重新封装到一起
     * @param first
     * @param objects
     * @return
     */
    public static Map<String, String> validateObject(Object first, Object... objects) {

        if (objects != null && objects.length > 0) {
            return validateList(Lists.asList(first, objects));
        } else {
            return validate(first);
        }

    }


    /**
     * 校验自定义的参数异常
     * @param param
     * @throws ParamException
     */
    public static void check(Object param) throws ParamException {

        Map<String, String> map = BeanValidator.validateObject(param);
        if (MapUtils.isNotEmpty(map)) { //校验后有错误信息
            throw new ParamException(map.toString());
        }

    }


}
