package com.past.config;

import com.past.shiro.CustomerRealm;
import com.past.shiro.RetryLimitHashedCredentialsMatcher;
import com.past.shiro.RoleFilter;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shiro 配置类
 */
@Configuration
public class ShiroConfig {


    /**
     * 开启Shiro的注解(如@RequiresRoles,@RequiresPermissions等等),
     * 需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证
     * 配置以下两个bean(DefaultAdvisorAutoProxyCreator和AuthorizationAttributeSourceAdvisor)即可实现此功能
     * @return defaultAdvisorAutoProxyCreator
     */
    @Bean("defaultAdvisorAutoProxyCreator")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator(){

        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);

        return defaultAdvisorAutoProxyCreator;
    }


    /**
     * 开启aop注解支持
     * @param defaultWebSecurityManager 默认Web安全管理器
     * @return authorizationAttributeSourceAdvisor
     */
    @Bean("authorizationAttributeSourceAdvisor")
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(@Qualifier("defaultWebSecurityManager") DefaultWebSecurityManager defaultWebSecurityManager) {

        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(defaultWebSecurityManager);

        return authorizationAttributeSourceAdvisor;
    }


    /**
     * 配置 shiroFilterFactoryBean 的过滤规则和受限资源
     * @param defaultWebSecurityManager 默认Web安全管理器
     * @param roleFilter 自定义 角色过滤器  由shiro默认的与关系判断  改为  或关系判断
     * @return shiroFilterFactoryBean
     */
    @Bean("shiroFilterFactoryBean")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(@Qualifier("defaultWebSecurityManager") DefaultWebSecurityManager defaultWebSecurityManager,
                                                         @Qualifier("roleFilter")RoleFilter roleFilter) {

        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

        // 给filter设置安全管理器
        shiroFilterFactoryBean.setSecurityManager(defaultWebSecurityManager);

        // 存储过滤器的map
        Map<String, Filter> filtersMap = new LinkedHashMap<>();
        // 限制请求时必需有某一角色 自定义角色过滤器，将判断角色拦截过滤规则由默认的与关系转为或关系
        filtersMap.put("roles", roleFilter);
        shiroFilterFactoryBean.setFilters(filtersMap);

         // 必须初始化一个有序Map  不然资源过滤会出问题
        Map<String, String> map = new LinkedHashMap<>();
        // 配置系统公共资源 anon 表示设置为公共资源  要设置到所有受限资源的上边
        map.put("/static/**", "anon");
        map.put("/webjars/**", "anon");
        map.put("/swagger-resources/**", "anon");
        map.put("/csrf", "anon");
        map.put("/favicon.ico", "anon");
        map.put("/401.jsp", "anon");
        map.put("/404.jsp", "anon");
        map.put("/error.jsp", "anon");
        map.put("/css/**", "anon");
        map.put("/fonts/**", "anon");
        map.put("/images/**", "anon");
        map.put("/js/**", "anon");
        map.put("/register.jsp", "anon");
        map.put("/login.jsp", "anon");
        map.put("/adminlogin.jsp", "anon");
        map.put("/index.jsp", "anon");
        map.put("/", "anon");
        map.put("/api/files/upload", "anon");
        map.put("/api/users/login","anon");
        map.put("/api/users/adminLogin","anon");
        map.put("/api/users/register", "anon");
        map.put("/api/goodstypes/selectAll", "anon");
        map.put("/api/goods/selectByType", "anon");
        map.put("/goods/searchView", "anon");
        map.put("/api/goods/search", "anon");
        map.put("/uploads/**", "anon");

        // 配置系统受限资源 user 表示配置记住我或认证通过可以访问的地址
        map.put("/api/users/logout", "user");
        map.put("/user/**", "user");
        map.put("/api/users/updateSelf", "user");
        map.put("/api/users/updatePassword", "user");
        map.put("/api/receivingaddress/**", "user");
        map.put("/api/files/uploadEvaImg", "user");
        map.put("/api/evaluates/insert", "user");
        map.put("/goods/detail/**", "user");
        map.put("/api/goods/selectByHot", "user");
        map.put("/api/guess/selectFavoriteGoods", "user");
        map.put("/api/guess/insertFavorite", "user");
        map.put("/api/guess/removeFavorite", "user");
        map.put("/api/guess/isFavorite", "user");
        map.put("/api/guess/selectRecentViewGoods", "user");
        map.put("/api/carts/**", "user");
        map.put("/carts/**", "user");
        map.put("/api/orders/takeOrder", "user");
        map.put("/api/orders/selectReadyToPay", "user");
        map.put("/api/orders/selectReadyToDeliver", "user");
        map.put("/api/orders/selectReadyToReceive", "user");
        map.put("/api/orders/selectReadyToEvaluate", "user");
        map.put("/api/orders/selectFinished", "user");
        map.put("/api/orders/receive", "user");
        map.put("/order/**", "user");
        map.put("/api/orders/notify_url", "user");
        map.put("/api/orders/return_url", "user");
        map.put("/api/orders/deleteLogic", "user");

        // 配置角色受限资源  有其中一个角色 即可访问
        map.put("/swagger-ui.html/**", "roles[超级管理员,系统管理员,后台管理员,前台管理员]");
        map.put("/v2/api-docs", "roles[超级管理员,系统管理员,后台管理员,前台管理员]");
        map.put("/exports/**", "roles[超级管理员,系统管理员,后台管理员,前台管理员,用户管理员,角色管理员,权限管理员,订单管理员]");

        // 配置系统受限资源 authc 请求这个资源需要认证和授权(需要form表单登录认证)，登录不了会跳转到setLoginUrl的路径
        // 排除LoginUrl，对其他全部的资源请求全部需要认证和授权
        map.put("/**","authc");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);

        // 默认认证界面路径
        shiroFilterFactoryBean.setLoginUrl("/login.jsp");

        return shiroFilterFactoryBean;
    }


    /**
     * 将defaultWebSecurityManager(...)的方法的返回值  set并注入到SecurityUtils中
     * 让某个实例的某个方法的返回值注入为Bean的实例
     * Spring静态注入
     * @return factoryBean
     */
    @Bean("methodInvokingFactoryBean")
    public MethodInvokingFactoryBean methodInvokingFactoryBean(@Qualifier("customerRealm") CustomerRealm customerRealm,
                                                               @Qualifier("ehCacheManager") EhCacheManager ehCacheManager,
                                                               @Qualifier("rememberMeManager") CookieRememberMeManager rememberMeManager){

        MethodInvokingFactoryBean factoryBean = new MethodInvokingFactoryBean();
        factoryBean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
        factoryBean.setArguments(defaultWebSecurityManager(customerRealm, ehCacheManager, rememberMeManager));

        return factoryBean;
    }


    /**
     * 配置 默认Web安全管理器
     * @param customerRealm 自定义realm认证授权规则
     * @return defaultWebSecurityManager
     */
    @Bean("defaultWebSecurityManager")
    public DefaultWebSecurityManager defaultWebSecurityManager(@Qualifier("customerRealm") CustomerRealm customerRealm,
                                                               @Qualifier("ehCacheManager") EhCacheManager ehCacheManager,
                                                               @Qualifier("rememberMeManager") CookieRememberMeManager rememberMeManager) {

        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();

        // 给安全管理器设置自定义realm认证授权规则
        defaultWebSecurityManager.setRealm(customerRealm);
        // 配置 ehcache缓存管理器
        defaultWebSecurityManager.setCacheManager(ehCacheManager);
        // 配置 记住我管理器
        defaultWebSecurityManager.setRememberMeManager(rememberMeManager);
        // 将安全管理器绑定到线程作业内容
        ThreadContext.bind(defaultWebSecurityManager);

        return defaultWebSecurityManager;
    }


    /**
     * 配置 自定义Realm
     * @return customerRealm
     */
    @Bean("customerRealm")
    public CustomerRealm customerRealm(@Qualifier("hashedCredentialsMatcher") HashedCredentialsMatcher hashedCredentialsMatcher) {

        CustomerRealm customerRealm = new CustomerRealm();

        // 设置 凭证匹配器
        customerRealm.setCredentialsMatcher(hashedCredentialsMatcher);
        // 开启全局缓存
        customerRealm.setCachingEnabled(true);
        // 开启认证缓存
        customerRealm.setAuthenticationCachingEnabled(true);
        customerRealm.setAuthenticationCacheName("authenticationCache");
        // 开启授权缓存
        customerRealm.setAuthorizationCachingEnabled(true);
        customerRealm.setAuthorizationCacheName("authorizationCache");

        return customerRealm;
    }


    /**
     * 记住我 过滤器
     * FormAuthenticationFilter 过滤器 过滤记住我
     * @return formAuthenticationFilter
     */
    @Bean("formAuthenticationFilter")
    public FormAuthenticationFilter formAuthenticationFilter(){

        FormAuthenticationFilter formAuthenticationFilter = new FormAuthenticationFilter();
        //对应前端的checkbox的name = rememberMe
        formAuthenticationFilter.setRememberMeParam("rememberMe");

        return formAuthenticationFilter;
    }


    /**
     * 记住我 管理器
     * cookie管理对象;记住我功能,rememberMe管理器
     * @return cookieRememberMeManager
     */
    @Bean("rememberMeManager")
    public CookieRememberMeManager rememberMeManager(@Qualifier("rememberMeCookie") SimpleCookie rememberMeCookie){

        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie);
        //rememberMe cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度(128 256 512 位)
        cookieRememberMeManager.setCipherKey(Base64.decode("4AvVhmFLUs0KTA3Kprsdag=="));

        return cookieRememberMeManager;
    }


    /**
     * 记住我
     * cookie对象;会话Cookie模板 ,默认为: JSESSIONID 问题: 与SERVLET容器名冲突,重新定义为sid或rememberMe，自定义
     * @return simpleCookie
     */
    @Bean("rememberMeCookie")
    public SimpleCookie rememberMeCookie(){

        //这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        //setcookie的httponly属性如果设为true的话，会增加对xss防护的安全系数。它有以下特点：

        //setcookie()的第七个参数
        //设为true后，只能通过http访问，javascript无法访问
        //防止xss读取cookie
        simpleCookie.setHttpOnly(true);
        simpleCookie.setPath("/");
        //<!-- 记住我cookie生效时间30天 ,单位秒;-->
        simpleCookie.setMaxAge(2592000);

        return simpleCookie;
    }


    /**
     * shiro缓存管理器
     * 需要添加到securityManager中
     * @return cacheManager
     */
    @Bean("ehCacheManager")
    @DependsOn("lifecycleBeanPostProcessor")
    public EhCacheManager ehCacheManager(){

        EhCacheManager cacheManager = new EhCacheManager();
        cacheManager.setCacheManagerConfigFile("classpath:ehcache-shiro.xml");

        return cacheManager;
    }


    /**
     * 凭证匹配器 使用自定义RetryLimitHashedCredentialsMatcher  限制登录次数的 凭证匹配器，即同一账号 密码连续输错5次  自动禁用该账户
     * @return hashedCredentialsMatcher
     */
    @Bean("hashedCredentialsMatcher")
    public HashedCredentialsMatcher hashedCredentialsMatcher(@Qualifier("ehCacheManager") EhCacheManager ehCacheManager) {

        // 使用有参构造 传入ehCacheManager缓存管理器 ehcache-shiro.xml中添加了RetryLimitHashedCredentialsMatcher需要的passwordRetryCache
        RetryLimitHashedCredentialsMatcher hashedCredentialsMatcher = new RetryLimitHashedCredentialsMatcher(ehCacheManager);

        // 散列算法:这里使用MD5算法;
        hashedCredentialsMatcher.setHashAlgorithmName("MD5");
        // 散列的次数1024，比如散列两次，相当于md5(md5(""));
        hashedCredentialsMatcher.setHashIterations(1024);

        return hashedCredentialsMatcher;
    }


    /**
     * 配置Shiro生命周期处理器
     * @return LifecycleBeanPostProcessor
     */
    @Bean("lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {

        return new LifecycleBeanPostProcessor();
    }


    /**
     * 角色过滤器 且转或
     * @return RoleFilter
     */
    @Bean("roleFilter")
    public RoleFilter roleFilter() {

        return new RoleFilter();
    }


}