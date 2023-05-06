package com.past.common;

import com.google.common.base.Preconditions;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 获取Spring上下文--ApplicationContext
 */
@Component
public class ApplicationContextHelper implements ApplicationContextAware {

    private static  ApplicationContext applicationContext;

    /**
     * 重写set IOC容器的方法
     * @param context spring上下文对象
     * @throws BeansException Beans异常
     */
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {

        applicationContext = Preconditions.checkNotNull(context);
    }


    /**
     * 根据传入的类型 从applicationContext中取出spring容器的bean
     * @param cla 类型
     * @param <T> 泛型
     * @return T
     */
    public static <T> T getBean(Class<T> cla) {
        return applicationContext.getBean(cla);
    }


    /**
     * 根据bean名称获取工厂中指定的bean对象
     * @param name bean名称
     * @return Object
     */
    public static Object getBean(String name){
        return applicationContext.getBean(name);
    }


    /**
     * 根据传入的bean名称和类型 从applicationContext中取出spring容器的bean
     * @param name bean名称
     * @param cla 类型
     * @param <T> 泛型
     * @return T
     */
    public static <T> T getBean(String name, Class<T> cla) {
        return applicationContext.getBean(name, cla);
    }


}
