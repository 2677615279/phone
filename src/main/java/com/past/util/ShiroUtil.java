package com.past.util;

import com.past.domain.entity.Users;
import com.past.shiro.CustomerRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;

/**
 * Shiro工具类
 */
public class ShiroUtil {


    /**
     * 处理登录者，更新个人信息后，动态更新shiro中subject信息
     * @param subject 修改前后 主体
     * @param before 修改前 shiro中subject信息
     * @param after 修改后 shiro中subject信息
     */
    public static void refreshPrincipal(Subject subject, Users before, Users after) {

        clearCache();

        if (after.getPassword() == null) {
            after.setPassword(before.getPassword());
        }
        if (after.getSalt() == null) {
            after.setSalt(before.getSalt());
        }
        if (after.getBirthday() == null) {
            after.setBirthday(before.getBirthday());
        }
        if (after.getImg() == null) {
            after.setImg(before.getImg());
        }

        PrincipalCollection principalCollection = subject.getPrincipals();
        // realName认证信息的key，对应的value就是认证的user对象
        String realmName = principalCollection.getRealmNames().iterator().next();

        PrincipalCollection newPrincipalCollection = new SimplePrincipalCollection(after, realmName);
        subject.runAs(newPrincipalCollection);
    }


    /**
     * 清除缓存
     */
    public static void clearCache() {

        //添加成功之后 清除缓存
        DefaultWebSecurityManager defaultWebSecurityManager = (DefaultWebSecurityManager) SecurityUtils.getSecurityManager();
        CustomerRealm customerRealm = (CustomerRealm) defaultWebSecurityManager.getRealms().iterator().next();

        //清除 相关的缓存
        customerRealm.clearAllCache();
    }


}
