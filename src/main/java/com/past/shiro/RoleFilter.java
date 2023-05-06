package com.past.shiro;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.Arrays;

/**
 * 自定义过滤器覆盖默认，且转或
 */
public class RoleFilter extends AuthorizationFilter {

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object mappedValue) throws Exception {
        String[] rolesArray = (String[]) mappedValue;

        if (rolesArray == null || rolesArray.length == 0) {
            //没有角色限制，有权限访问
            return true;
        }

        Subject subject = getSubject(servletRequest, servletResponse);

        // 只要有任意一个元素匹配返回true 当没有与其匹配的元素时返回false  stream流的anyMatch操作  短路 终端操作 操作后该流不能继续使用
        return Arrays.stream(rolesArray).anyMatch(subject::hasRole);
    }

}
