<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>乐享手机商城后台管理</title>
    <link href="/css/layui.css" rel="stylesheet" >
    <link href="/css/iconfont.css" rel="stylesheet">
    <script src="/js/jquery.1.12.4.min.js" type="text/javascript"></script>
    <script src="/js/layui.js" type="text/javascript"></script>
    <link href="/css/bootstrap.min.css" rel="stylesheet">
    <script src="/js/bootstrap.min.js" type="text/javascript"></script>
</head>
<body class="layui-layout-body">
<div class="layui-layout layui-layout-admin">

    <!--头部-->
    <div class="layui-header">
        <div class="layui-logo"><a href="welcome.jsp" target="myframe">乐享手机商城管理</a></div>
        <ul class="layui-nav layui-layout-right">
            <li class="layui-nav-item">
                        <img src="/uploads/users_imgs/<shiro:principal property="img"/>" style="width:30px;height:30px;" class="layui-nav-img">&nbsp;<shiro:principal property="username"/>
            </li>
            <li class="layui-nav-item"><a id="logout"><span>退出</span></a></li>
        </ul>
    </div>

    <!--左侧-->
    <div class="layui-side layui-bg-black">
        <div class="layui-side-scroll">
            <!-- 左侧导航区域（可配合layui已有的垂直导航） -->
            <ul class="layui-nav layui-nav-tree" lay-filter="test">
                <shiro:hasAnyRoles name="商品管理员,内存管理员,商品分类管理员,前台管理员,后台管理员,系统管理员,超级管理员">
                    <li class="layui-nav-item">
                        <a class=""	href="javascript:;">商品管理</a>
                        <dl class="layui-nav-child">
                            <shiro:hasAnyRoles name="商品管理员,前台管理员,后台管理员,系统管理员,超级管理员">
                                <dd>
                                    <a href="/admin/goodsadd.jsp" target="myframe">添加商品</a>
                                </dd>
                                <dd>
                                    <a href="/admin/goodslist.jsp" target="myframe">查看商品</a>
                                </dd>
                            </shiro:hasAnyRoles>
                            <shiro:hasAnyRoles name="商品分类管理员,前台管理员,后台管理员,系统管理员,超级管理员">
                                <dd>
                                    <a href="/admin/goodstypeslist.jsp" target="myframe">查看分类</a>
                                </dd>
                            </shiro:hasAnyRoles>
                            <shiro:hasAnyRoles name="内存管理员,前台管理员,后台管理员,系统管理员,超级管理员">
                                <dd>
                                    <a href="/admin/memorylist.jsp" target="myframe">查看内存</a>
                                </dd>
                            </shiro:hasAnyRoles>
                        </dl>
                    </li>
                </shiro:hasAnyRoles>

                <shiro:hasAnyRoles name="订单管理员,前台管理员,后台管理员,系统管理员,超级管理员">
                    <li class="layui-nav-item">
                        <a class=""	href="javascript:;">订单管理</a>
                        <dl class="layui-nav-child">
                            <dd>
                                <a href="/admin/orderlist.jsp" target="myframe">查看订单</a>
                            </dd>
                        </dl>
                    </li>
                </shiro:hasAnyRoles>

                <shiro:hasAnyRoles name="用户管理员,角色管理员,权限管理员,前台管理员,后台管理员,系统管理员,超级管理员">
                    <li class="layui-nav-item">
                        <a class=""	href="javascript:;">用户管理</a>
                        <dl class="layui-nav-child">
                            <dd>
                                <a href="/admin/userlist.jsp" target="myframe">查看用户</a>
                            </dd>
                        </dl>
                    </li>
                </shiro:hasAnyRoles>

                <shiro:hasAnyRoles name="用户管理员,角色管理员,权限管理员,前台管理员,后台管理员,系统管理员,超级管理员">
                    <li class="layui-nav-item">
                        <a class=""	href="javascript:;">角色管理</a>
                            <dl class="layui-nav-child">
                                <dd>
                                    <a href="/admin/rolelist.jsp" target="myframe">查看角色</a>
                                </dd>
                            </dl>
                    </li>
                </shiro:hasAnyRoles>

                <shiro:hasAnyRoles name="用户管理员,角色管理员,权限管理员,前台管理员,后台管理员,系统管理员,超级管理员">
                    <li class="layui-nav-item">
                        <a class=""	href="javascript:;">权限管理</a>
                            <dl class="layui-nav-child">
                                <dd>
                                    <a href="/admin/permissionlist.jsp" target="myframe">查看权限</a>
                                </dd>
                            </dl>
                    </li>
                </shiro:hasAnyRoles>

                <shiro:hasAnyRoles name="区县管理员,城市管理员,省份管理员,地址管理员,前台管理员,后台管理员,系统管理员,超级管理员">
                    <li class="layui-nav-item">
                        <a class=""	href="javascript:;">地址管理</a>
                        <dl class="layui-nav-child">
                            <shiro:hasAnyRoles name="省份管理员,地址管理员,前台管理员,后台管理员,系统管理员,超级管理员">
                                <dd>
                                    <a href="/admin/provincelist.jsp" target="myframe">查看省份</a>
                                </dd>
                            </shiro:hasAnyRoles>
                            <shiro:hasAnyRoles name="城市管理员,省份管理员,地址管理员,前台管理员,后台管理员,系统管理员,超级管理员">
                                <dd>
                                    <a href="/admin/citylist.jsp" target="myframe">查看城市</a>
                                </dd>
                            </shiro:hasAnyRoles>
                            <dd>
                                <a href="/admin/arealist.jsp" target="myframe">查看区县</a>
                            </dd>
                        </dl>
                    </li>
                </shiro:hasAnyRoles>

                <shiro:hasAnyRoles name="轮播图管理员,前台管理员,后台管理员,系统管理员,超级管理员">
                    <li class="layui-nav-item">
                        <a class=""	href="javascript:;">轮播图管理</a>
                        <dl class="layui-nav-child">
                            <dd>
                                <a href="/admin/bannerlist.jsp" target="myframe">查看轮播图</a>
                            </dd>
                        </dl>
                    </li>
                </shiro:hasAnyRoles>

                <shiro:hasAnyRoles name="评论管理员,前台管理员,后台管理员,系统管理员,超级管理员">
                    <li class="layui-nav-item">
                        <a class=""	href="javascript:;">评论管理</a>
                        <dl class="layui-nav-child">
                            <dd>
                                <a href="/admin/evaluatelist.jsp" target="myframe">查看评论</a>
                            </dd>
                        </dl>
                    </li>
                </shiro:hasAnyRoles>


                <li class="layui-nav-item">
                    <a class=""	href="javascript:;">个人账户管理</a>
                    <dl class="layui-nav-child">
                        <dd>
                            <a href="/admin/updateself.jsp" target="myframe">修改个人信息</a>
                        </dd>
                        <dd>
                            <a href="/admin/updatepassword.jsp" target="myframe">修改密码</a>
                        </dd>
                    </dl>
                </li>

                <shiro:hasAnyRoles name="超级管理员,系统管理员,后台管理员,前台管理员">
                    <li class="layui-nav-item">
                        <a class=""	href="javascript:;">API文档管理</a>
                        <dl class="layui-nav-child">
                            <dd>
                                <a href="/swagger-ui.html" target="myframe">查看文档</a>
                            </dd>
                        </dl>
                    </li>
                </shiro:hasAnyRoles>
            </ul>
        </div>
    </div>

    <div class="layui-body">
        <!-- 内容主体区域 -->
        <iframe src="/admin/welcome.jsp" name="myframe" style="width:99%;height:99%;border: 0;" ></iframe>
    </div>

    <div class="layui-footer">
        <!-- 底部固定区域 -->
        © LEMarket.com - 底部固定区域
    </div>
</div>

<script>
    //JavaScript代码区域
    layui.use(['element','layer', 'table'], function() {
        var element = layui.element;
        var table = layui.table;
        var layer = layui.layer;

        $("#logout").click(function () {
            layer.confirm('是否退出登录？', {
                btn: ['确认', '取消'] //可以无限个按钮)
                , btn1: function (index, layero) {
                    $.ajax({
                        type: "POST",
                        url: "/api/users/logout",
                        success: function (data) {
                            if (data.message === 'success') {
                                layer.msg('退出成功！', {icon: 1, shade: 0.4, time: 2000});
                                window.location.href = "/adminlogin.jsp";
                            } else {
                                layer.msg('退出失败！', {icon: 5, shade: 0.4, time: 2000});
                            }
                        },
                        error: function () {
                            layer.msg('退出失败！', {icon: 5, shade: 0.4, time: 2000});
                            window.location.href = "/error.jsp";
                        },
                        dataType: "json"
                    });
                }
            });
        });
    });
</script>
</body>
</html>
