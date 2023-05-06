<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>商品智能交易平台</title>
    <!--头部资源-->
    <link href="/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
    <script src="/js/jquery.1.12.4.min.js" type="text/javascript"></script>
    <script src="/js/bootstrap.min.js" type="text/javascript"></script>
    <!--底部资源-->
    <link href="/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
    <script src="/js/bootstrap.min.js" type="text/javascript"></script>

    <link href="/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
    <link href="/css/layui.css" rel="stylesheet">
    <script src="/js/jquery.min.js" type="text/javascript"></script>
    <script src="/js/bootstrap.min.js" type="text/javascript"></script>
    <script src="/js/layui.js" type="text/javascript"></script>
</head>
<body>
<!--导航栏部分-->
<jsp:include page="/user/include/header.jsp" />

<!-- 中间内容 -->
<div class="container-fluid bigHead">
    <div class="row" style="background-color:white;">
        <div style="width:100%;height:120px;text-align:center;background-color:#3353A8;">
            <img src="/images/cartbanner5.jpg" style="width:1226px;height:120px;" />
        </div>
        <div class="col-sm-10  col-md-10 col-sm-offset-1 col-md-offset-1">
            <table class="layui-table" lay-skin="nob" id="shoppingCarTable">
                <thead>
                <tr>
                    <th>订单编号</th>
                    <th>总价</th>
                    <th>收件人</th>
                    <th>电话</th>
                    <th>收货地址</th>
                    <th>状态</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <th>${order.id}</th>
                    <th>${order.price}</th>
                    <th>${order.username}</th>
                    <th>${order.phone}</th>
                    <th>${order.address}</th>
                    <th>待付款</th>
                </tr>
                </tbody>
            </table>
            <hr />
            <div class="row" style="text-align:center;">
                <button type="button"
                        class="layui-btn layui-btn-lg layui-btn-normal"
                        onclick="javascript:window.location.href='/'">继续购买</button>
                <button type="button"
                        class="layui-btn layui-btn-lg layui-btn-warm"
                        id="pay">立即支付</button>
            </div>
            <h2>猜你喜欢</h2>
            <div>
                <div class="span16" style="width:1080px;">
                    <ul>
                        <c:forEach items="${guessList}"  var="g">
                            <a href="/goods/detail/${g.goods.id}">
                                <li>
                                    <img src="/images/${g.goods.img}" />
                                    <p class="goods-title">${g.goods.name}</p>
                                    <p class="goods-desc">${g.goods.description}</p>
                                    <p><span class="newprice">${g.goods.price}</span>&nbsp;
                                    </p>
                                </li>
                            </a>
                        </c:forEach>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- 尾部 -->
<jsp:include page="/user/include/foot.jsp" />

<script type="text/javascript">
    var layer;
    layui.use(['layer','form'], function () {
        layer = layui.layer;
        form =layui.form;

        $("#pay").click(function () {
            $.ajax({
                type: "POST",
                url: "/order/pay",
                data: {"id": "${order.id}"},
                success: function (res) {
                    if (res.message == 'success') {
                        window.location.href = "/order/paysuccess";
                    }
                    else {
                        window.alert("付款前请先登录，谢谢合作！");
                        $.ajax({
                            type: "POST",
                            url: "/api/users/logout",
                            success: function (data) {
                                if (data.message === 'success') {
                                    layer.msg('退出成功！', {icon: 1, shade: 0.4, time: 2000});
                                    window.location.href = "/";
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
                },
                dataType: "json"
            });
        });
    });
</script>
</body>
</html>
