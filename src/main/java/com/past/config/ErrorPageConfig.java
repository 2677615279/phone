package com.past.config;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

/**
 * 错误页面 配置类
 */
@Configuration
public class ErrorPageConfig {

    /**
     * 解决spring-boot2.x Whitelabel Error Page
     * @return WebServerFactoryCustomizer
     */
    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer(){
        return factory -> {
            ErrorPage error401Page = new ErrorPage(HttpStatus.UNAUTHORIZED, "/401.jsp");
            ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/404.jsp");
            ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error.jsp");
            factory.addErrorPages(error401Page, error404Page, error500Page);
        };
    }

}
