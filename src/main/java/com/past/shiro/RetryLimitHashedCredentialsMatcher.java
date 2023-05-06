package com.past.shiro;

import com.past.dao.UsersDAO;
import com.past.domain.entity.Users;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义 限制登录次数的 凭证匹配器 即同一账号 密码连续输错6次起 自动禁用该账户
 * 只适用于前台登录，账号、密码、状态的校验、记录错误登录次数等 均由此凭证匹配器处理
 * 因为后台登录还需额外验证角色，所以在用户service的adminLogin方法中由我们手动处理
 * 同时保证对明文密码的MD5加密 所以需要继承 HashedCredentialsMatcher
 */
@Slf4j
public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {

    @Autowired
    private UsersDAO usersDAO;

    /**
     * 缓存中存储的key和value  值是原子整数
     */
    private Cache<String, AtomicInteger> passwordRetryCache;

    /**
     * 有参构造 passwordRetryCache是ehcache-shiro.xml中配置缓存名称
     * @param cacheManager 缓存管理器
     */
    public RetryLimitHashedCredentialsMatcher(CacheManager cacheManager) {
        passwordRetryCache = cacheManager.getCache("passwordRetryCache");
    }


    /**
     * 凭证匹配规则
     * 同一账号的凭证连续不匹配超过5次，即连续输错6次密码开始 禁用账号 抛出锁定用户异常 并修改数据库status等字段
     * @param token 认证令牌 存储用户账号和密码的token
     * @param info 认证信息
     * @return 凭证匹配true 不匹配false
     */
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {

        // 从token中获取 主身份信息，可以是username或phone或email
        String principal = (String)token.getPrincipal();
        // 获取用户登录次数
        AtomicInteger retryCount = passwordRetryCache.get(principal);

        // 如果用户没有登录过,登录次数加1 并放入缓存
        if (retryCount == null) {
            retryCount = new AtomicInteger(0);
            passwordRetryCache.put(principal, retryCount);
        }

        //如果用户登录失败次数大于5次 抛出锁定用户异常  并修改数据库字段
        if (retryCount.incrementAndGet() > 5) {
            // 根据主身份信息 即username或phone或email  查询用户是否存在
            Users subject = usersDAO.selectByPrincipal(principal);
            // 如果用户存在且状态正常 则将其状态改为禁用 即status从0改为2
            if (subject != null && subject.getStatus() == 0){
                subject.setStatus(2);
                subject.setUpdateTime(new Date());
                subject.setOperator("System");
                subject.setOperatorIp("System IP");

                // 更新用户信息
                usersDAO.updateByPrimaryKeySelective(subject);
                log.info("锁定用户：{}", subject.getUsername());

                // 第一次锁定用户完毕后，应清除缓存中该用户的数据；当5分钟内管理员恢复该用户，不影响5分钟内其后续登录
                // 否则即使5分钟内管理员恢复该用户，5分钟内该用户随即继续以正确的账号密码登录 同样被认为请求次数多于5次导致被锁定
                passwordRetryCache.remove(principal);
            }

            //抛出用户锁定异常
            throw new LockedAccountException("由于密码多次错误,当前账户已被锁定，请联系管理员处理，感谢您的配合！");
        }

        // 判断用户账号和密码是否正确
        boolean matches = super.doCredentialsMatch(token, info);
        //如果正确,从缓存中将用户登录计数 清除
        if (matches) {
            passwordRetryCache.remove(principal);
        }

        return matches;
    }


}
