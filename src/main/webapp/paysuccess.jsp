<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>乐享手机商城</title>
    <!--头部资源-->
    <link href="/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
    <script src="/js/jquery.1.12.4.min.js" type="text/javascript"></script>
    <script src="/js/bootstrap.min.js" type="text/javascript"></script>
    <!--底部资源-->
    <link href="/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
    <script src="/js/bootstrap.min.js" type="text/javascript"></script>

    <link href="/css/style.css" rel="stylesheet">
    <link href="/css/layui.css" rel="stylesheet">
    <script src="/js/jquery.min.js" type="text/javascript"></script>
    <script src="/js/layui.js" type="text/javascript"></script>
</head>
<body>
<!--导航栏部分-->
<jsp:include page="/user/include/header.jsp" />
<div class="layui-container" id="main" style="height:90%;width:100%;">
    <img style="margin-left:27%;float:left;height:500px;width:400px;" src="/images/paysuccess.png" />
    <div style="float:left;height:500px;width:500px;padding-top:200px;">
        <h1>支付成功</h1>
            <button class="layui-btn layui-btn-normal" onclick="javascript:window.location.href='/'">继续购物</button>
            <button class="layui-btn layui-btn-warm" onclick="javascript:window.location.href='/user/center/#section2'">查看订单</button>
    </div>
</div>
<!--尾部-->
<jsp:include page="/user/include/foot.jsp" />
</body>
</html>
