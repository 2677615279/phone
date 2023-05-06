package com.past.config;

import com.past.common.HttpInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * Web 配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private HttpInterceptor httpInterceptor;

    /**
     * 添加拦截器
     * @param registry 拦截器注册
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 添加全局限流拦截器，拦截/**开头的请求，排除"/error", "/favicon.ico", "/css/**", "/js/**", "/fonts/**", "/images/**", "/uploads/**", "/fonts/**"
        registry.addInterceptor(httpInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(Arrays.asList("/error/**", "/favicon.ico", "/css/**", "/js/**", "/fonts/**", "/images/**",
                        "/uploads/**", "/swagger-resources/**", "/csrf", "/webjars/**", "/v2/api-docs", "/swagger-ui.html/**",
                        "/", "/index.jsp", "/exports/**", "/static/**"));
    }


    /**
     * 资源配置
     * @param registry 资源注册
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // 配置静态资源映射
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");

        // 配置swagger2映射
        registry.addResourceHandler("/swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        // 配置导出资源映射
        String absolutePath = "file:///D:/IdeaProjects/phone/exports/"; // 导出文件存储目录绝对路径
        String relativePath = "exports/"; // 相对路径
        // 访问/exports/下的所有请求，会映射到本地项目中的exports目录下的同名文件(绝对路径)
        registry.addResourceHandler("/exports/**")
                .addResourceLocations(absolutePath); // 使用绝对路径，相对路径会映射失败
    }


}
