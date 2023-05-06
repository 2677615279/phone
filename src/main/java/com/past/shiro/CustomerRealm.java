package com.past.shiro;

import com.past.domain.entity.*;
import com.past.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 自定义Realm 重写认证和授权规则
 */
@Slf4j
public class CustomerRealm extends AuthorizingRealm {

    @Autowired
    @Lazy // 延迟加载
    private UsersService usersService;

    @Autowired
    @Lazy
    private RolesService rolesService;

    @Autowired
    @Lazy
    private PermissionsService permissionsService;

    @Autowired
    @Lazy
    private RolesUsersService rolesUsersService;

    @Autowired
    @Lazy
    private RolesPermissionsService rolesPermissionsService;


    /**
     * 授权规则
     * @param principalCollection 主身份收集信息
     * @return SimpleAuthorizationInfo
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {


        // 从身份集中获取 主身份信息，即Users对象，需要强转
        // 因为在下面的认证逻辑中，SimpleAuthenticationInfo传入的第一个参数是一个Users对象，所以主身份就是一个Users对象
        Users subject = (Users) principalCollection.getPrimaryPrincipal();

        // 声明一些集合
        Set<RolesUsers> rolesUsersSet; // 存储当前登录用户的所有角色用户关联数据的集合(已确定每条数据的状态为0，即正常)
        Set<Long> rolesIds; // 存储当前登录用户的所有角色id的集合(不能确定每个角色id代表的数据状态为0，即正常)
        Set<Roles> rolesSet = null; // 存储当前登录用户的所有角色的集合(已确定每条数据的状态为0，即正常)
        Set<Long> rolesIdsFilter; // 存储当前登录用户的所有角色id的集合(经过滤，已确定每个角色id代表的数据状态为0，即正常)
        Set<RolesPermissions> rolesPermissionsSet = null; // 存储当前登录用户的所有角色权限关联数据的集合(已确定每条数据的状态为0，即正常)
        Set<Long> permissionsIds; // 存储当前登录用户的所有权限id的集合(不能确定每个权限id代表的数据状态为0，即正常)
        Set<Permissions> permissionsSet = null; // 存储当前登录用户的所有权限的集合(已确定每条数据的状态为0，即正常)

        // 如果登录用户主体不为空，且状态正常为0，执行授权操作
        // 授权操作：获取该登录用户的所有角色(正常状态)和所有权限(正常状态)
        // 涉及5张表的查询和过滤操作，过滤掉属于该用户的非正常的关联数据、角色、权限；保留正常的
        if (!ObjectUtils.isEmpty(subject) && subject.getStatus() == 0) {
            // 初始化一个授权信息实例  simpleAuthorizationInfo
            SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

            // 根据用户主体的主键id，查询所有属于该用户的角色用户关联，以状态0(正常)为过滤条件，最后收集为一个集合
            rolesUsersSet = rolesUsersService.selectByUserId(subject.getId())
                    .stream().filter(ru -> ru.getStatus() == 0).collect(Collectors.toSet());

            if (!CollectionUtils.isEmpty(rolesUsersSet)) {
                // 将过滤好的角色用户关联集合中的元素映射成角色主键id，最后收集为一个集合
                rolesIds = rolesUsersSet.stream().map(RolesUsers::getRoleId).collect(Collectors.toSet());

                // 根据存储角色主键id的集合，查询这些id代表的角色对象，以状态0(正常)为过滤条件，最后收集为一个集合
                rolesSet = rolesService.selectByIds(new ArrayList<>(rolesIds))
                        .stream().filter(r -> r.getStatus() == 0).collect(Collectors.toSet());
            }

            if (!CollectionUtils.isEmpty(rolesSet)) {
                // 将过滤好的角色集合中的元素映射成角色主键id，最后收集为一个集合
                rolesIdsFilter = rolesSet.stream().map(Roles::getId).collect(Collectors.toSet());

                // 根据存储过滤后的角色主键id的集合，查询所有属于这些角色的角色权限关联，以状态0(正常)为过滤条件，最后收集为一个集合
                rolesPermissionsSet = rolesPermissionsService.selectByRoleIds(new ArrayList<>(rolesIdsFilter))
                        .stream().filter(rp -> rp.getStatus() == 0).collect(Collectors.toSet());
            }

            if (!CollectionUtils.isEmpty(rolesPermissionsSet)) {
                // 将过滤好的角色权限关联集合中的元素映射成权限主键id，最后收集为一个集合
                permissionsIds = rolesPermissionsSet.stream().map(RolesPermissions::getPermissionId).collect(Collectors.toSet());

                // 根据存储权限主键id的集合，查询这些id代表的权限对象，以状态0(正常)为过滤条件，最后收集为一个集合
                permissionsSet = permissionsService.selectByIds(new ArrayList<>(permissionsIds))
                        .stream().filter(p -> p.getStatus() == 0).collect(Collectors.toSet());
            }

            // 将角色集合中的每个roles对象 映射为 其name属性，并收集为一个新集合
            Set<String> roleNameSet = Optional.ofNullable(rolesSet)
                    .map(Set::stream)
                    .orElseGet(Stream::empty)
                    .map(Roles::getName)
                    .collect(Collectors.toSet());

            // 将权限集合中的每个permissions对象 映射为 其name属性，并收集为一个新集合
            Set<String> permissionNameSet = Optional.ofNullable(permissionsSet)
                    .map(Set::stream)
                    .orElseGet(Stream::empty)
                    .map(Permissions::getName)
                    .collect(Collectors.toSet());

            // 向授权信息实例  simpleAuthorizationInfo中  添加角色集合数据和权限集合数据
            simpleAuthorizationInfo.addRoles(roleNameSet);
            simpleAuthorizationInfo.addStringPermissions(permissionNameSet);

            log.info("当前登录的用户所拥有的角色 : {}", roleNameSet);
            log.info("当前登录的用户所拥有的权限 : {}", permissionNameSet);

            return simpleAuthorizationInfo;
        }

        return null;
    }


    /**
     * 认证规则
     * @param authenticationToken 存储用户账号和密码的token 认证令牌
     * @return SimpleAuthenticationInfo
     * @throws AuthenticationException 认证异常
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        // 从token中获取 主身份信息，可以是username或phone或email
        String principal = (String) authenticationToken.getPrincipal();

        // 根据主身份信息 查询用户主体
        Users subject = usersService.selectByPrincipal(principal);

        if (ObjectUtils.isEmpty(subject) || subject.getStatus() == 1) {
            throw new UnknownAccountException("账号或密码错误！");
        }
        if (subject.getStatus() == 2) {
            throw new LockedAccountException("账号已被锁定,请联系管理员！");
        }

        // 使用全参构造方法，返回一个认证信息实例 SimpleAuthenticationInfo
        return new SimpleAuthenticationInfo(subject, subject.getPassword(), new MyByteSource(subject.getSalt()), this.getName());
    }


    /**
     * 重写方法,清除当前用户的的 授权缓存
     * @param principals 用户主身份集
     */
    @Override
    public void clearCachedAuthorizationInfo(PrincipalCollection principals) {
        super.clearCachedAuthorizationInfo(principals);
    }


    /**
     * 重写方法，清除当前用户的 认证缓存
     * @param principals 用户主身份集
     */
    @Override
    public void clearCachedAuthenticationInfo(PrincipalCollection principals) {

        super.clearCachedAuthenticationInfo(principals);
    }


    /**
     * 重写方法，清除当前用户的所有缓存
     * @param principals 用户主身份集
     */
    @Override
    public void clearCache(PrincipalCollection principals) {

        super.clearCache(principals);
    }


    /**
     * 自定义方法：清除所有 授权缓存
     */
    public void clearAllCachedAuthorizationInfo() {

        getAuthorizationCache().clear();
    }


    /**
     * 自定义方法：清除所有 认证缓存
     */
    public void clearAllCachedAuthenticationInfo() {

        getAuthenticationCache().clear();
    }


    /**
     * 自定义方法：清除所有的  认证缓存  和 授权缓存
     */
    public void clearAllCache() {

        clearAllCachedAuthenticationInfo();
        clearAllCachedAuthorizationInfo();
    }


}
