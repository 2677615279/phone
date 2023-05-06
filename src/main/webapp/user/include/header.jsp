<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<nav class="navbar navbar-default navbar-fixed-top">
    <div class="container-fluid">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed"
                    data-toggle="collapse" data-target="#bs-example-navbar-collapse-1"
                    aria-expanded="false">
                <span class="sr-only">Toggle navigation</span> <span
                    class="icon-bar"></span> <span class="icon-bar"></span> <span
                    class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="/">乐享手机商城</a>
        </div>

        <div class="collapse navbar-collapse"
             id="bs-example-navbar-collapse-1">

            <ul class="nav navbar-nav navbar-right" style="padding-right:30px;">
                <!-- 未认证 -->
                <shiro:guest>
                    <li><a href="/register.jsp">注册</a></li>
                    <li><a href="/login.jsp">登录</a></li>
                </shiro:guest>

                <!-- 已认证或记住我 -->
                <shiro:user>
                    <li><a href="/user/center">用户中心</a></li>
                    <li class="dropdown"><a class="dropdown-toggle"
                                            data-toggle="dropdown" role="button" aria-haspopup="true"
                                            aria-expanded="false">
                        <div style="margin-top:-5px;float:left;width:30px; height:30px; border-radius:50%; overflow:hidden;">
                            <img src="/uploads/users_imgs/<shiro:principal property="img"/>" style="width:30px;height:30px;" class="layui-nav-img">
                        </div>&nbsp;&nbsp;<shiro:principal property="username"/> <span
                            class="caret"></span>
                    </a>
                        <ul class="dropdown-menu">
                            <li><a href="/user/cart">购物车</a></li>
                            <li><a href="/user/updateself.jsp">个人资料修改</a></li>
                            <li><a href="/user/updatepassword.jsp">修改密码</a></li>
                            <li><a id="logout">退出</a></li>
                        </ul>
                    </li>
                </shiro:user>

            </ul>

            <div class="navbar-form navbar-right">
                <form action="/goods/searchView" method="post">
                    <button class="btn btn-default" type="submit">查找商品</button>
                </form>
            </div>
        </div>
    </div>
</nav>
