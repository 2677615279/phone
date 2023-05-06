<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>乐享手机商城</title>
    <link href="/css/style.css" rel="stylesheet">
    <link href="/css/layui.css" rel="stylesheet">
    <link href="/css/admin.css" rel="stylesheet">
    <script src="/js/jquery.min.js" type="text/javascript"></script>
    <script src="/js/layui.js" type="text/javascript"></script>
</head>
<body>
<div class="layui-body" style="position:absolute;left:0;" id="LAY_app_body">
    <div class="layadmin-tabsbody-item layui-show">
        <div class="layui-fluid" style="padding-top:30px;">
            <div class="layadmin-tips">
                <i class="layui-icon" face=""></i>
                <div class="layui-text">
                    <h1>
                        <span class="layui-anim layui-anim-loop layui-anim-">4</span>
                        <span class="layui-anim layui-anim-loop layui-anim-rotate">0</span>
                        <span class="layui-anim layui-anim-loop layui-anim-">1</span>
                    </h1>
                </div>
                <div class="layui-text">
                    <h1>
                        <span class="layui-anim layui-anim-loop layui-anim-">您</span>
                        <span class="layui-anim layui-anim-loop layui-anim-">不</span>
                        <span class="layui-anim layui-anim-loop layui-anim-">具</span>
                        <span class="layui-anim layui-anim-loop layui-anim-">有</span>
                        <span class="layui-anim layui-anim-loop layui-anim-">访</span>
                        <span class="layui-anim layui-anim-loop layui-anim-">问</span>
                        <span class="layui-anim layui-anim-loop layui-anim-">权</span>
                        <span class="layui-anim layui-anim-loop layui-anim-">限</span>
                    </h1>
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    layui.use('form', function() {
        var form = layui.form;
    });
</script>
</body>
</html>
