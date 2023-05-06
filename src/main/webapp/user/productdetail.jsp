<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>商品详情</title>
    <link href="/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
    <link href="/css/layui.css" rel="stylesheet">
    <script src="/js/jquery.min.js" type="text/javascript"></script>
    <script src="/js/bootstrap.min.js" type="text/javascript"></script>
    <script src="/js/layui.js" type="text/javascript"></script>

    <!--头部资源-->
    <link href="/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
    <script src="/js/jquery.1.12.4.min.js" type="text/javascript"></script>
    <script src="/js/bootstrap.min.js" type="text/javascript"></script>
    <!--底部资源-->
    <link href="/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
    <script src="/js/bootstrap.min.js" type="text/javascript"></script>
    <style>
        .dl-horizontal span {
            font-size: 16px;
            margin: 15px 15px;
        }

        .dl-horizontal .badge {
            background-color: #009688;
        }

        #btnFont button {
            font-size: 16px;
        }

        #img {
            float: center;
            padding-top: 35px;
        }
    </style>
</head>
<body style="background-color:white;">
<!--导航栏部分-->
<jsp:include page="/user/include/header.jsp"/>

<!--中间部分-->
<div class="container">
    <div class="row clearfix">
        <div class="col-md-12" style="padding-top:25px;padding-bottom:25px;">
            <input type="hidden" id="goodsId"
                   value="${goods.id}">
            <h1 class="text-center">${goods.name}</h1>
        </div>
    </div>
    <div class="row clearfix" style="margin-bottom:50px;">
        <div class="col-md-5 column center">
            <img id="img" style="width: 280px;height: 200px;" src="/images/${goods.img}"
                 class="img-rounded"/>
        </div>
        <div class="col-md-5 column">
            <dl class="dl-horizontal">
                <dt><span class="badge">${goods.price}元</span></dt>
                <dd><span class="badge">${goods.memory.name}</span></dd>
                <dt><span class="badge">${goods.color}</span></dt>
                <dd><span class="badge">${goods.description}</span></dd>
                <dt><span class="badge">购买数量</span></dt>
                <dd>
                    <div style="height: 38px;padding-left:10px;padding-top:5px;">
                        <button id="redbtn" class="layui-btn layui-btn-radius" onclick="reduceCounts()"
                                style="float: left;">-
                        </button>
                        <div class="layui-input-inline"
                             style="width: 50px; float: left;">
                            <input type="text" id="num" name="num"
                                   autocomplete="off" value="1" class="layui-input">
                        </div>
                        <button id="addbtn" onclick="addCounts()" class="layui-btn layui-btn-radius"
                                style="float: left;">+
                        </button>
                    </div>
                </dd>
                <dt style="padding-top:20px;">
                    <button
                            class="layui-btn layui-btn-lg layui-btn-radius"
                            onclick="addToCart()">
                        <i class="layui-icon">&#xe608;</i>加入购物车
                    </button>
                </dt>
                <dd style="padding-top:20px;padding-left:10px;width:320px;">
                    <button class="layui-btn layui-btn-lg layui-btn-radius" id="fav"
                            onclick="addToFavorite()">
                        <i class="layui-icon">&#xe600;</i>收藏
                    </button>
                    <button class="layui-btn layui-btn-lg layui-btn-radius" id="unfav"
                            onclick="removeFavorite()">
                        <i class="layui-icon">&#xe658;</i>取消收藏
                    </button>
                    <button
                            class="layui-btn layui-btn-lg layui-btn-radius"
                            onclick="javascript:window.location.href='/user/cart';">
                        <i class="layui-icon">&#xe698;</i>去购物车
                    </button>
                </dd>
            </dl>
        </div>
    </div>
    <div class="row clearfix">
        <div class="col-md-12 column" id="hotGoods">
        </div>
        <div>
            <div class="row clearfix">
                <h2 style="padding-left:30px;">商品评价</h2>
                <div class="col-md-12 column">
                    <div class="media-body" style="padding-left:20px;">
                        <c:if test="${evaList != null && evaList.size() > 0}">
                            <c:forEach items="${evaList}" var="e">
                                <hr class="layui-bg-blue">
                                <a href="javascript:;" class="media-left" style="float: left;">
                                    <img src="/uploads/users_imgs/${e.user.img}" class="layui-nav-img"
                                         style="height:55px; width:55px;">
                                </a>
                                <div class="pad-btm">
                                    <p class="fontColor"><span>${e.user.username}</span>
                                    <div style="height:30px;width:150px;float:right;margin-top:-30px;"
                                         id="evaStar${e.id}" class="evaStar"></div>
                                    </p>
                                    <p class="fontColor">
                                        <span class="layui-badge layui-bg-blue"><fmt:formatDate value="${e.day}"
                                                                                                pattern="yyyy年-MM月-dd日"/></span>
                                    </p>
                                </div>
                                <p class="message-text">${e.content}</p>
                                <div>
                                    <c:if test="${e.imgList != null && e.imgList.size() > 0}">
                                        <c:forEach items="${e.imgList}" var="g">
                                            <img src="/uploads/evaluates_imgs/${g.name}" height="80px" width="100px"/>
                                        </c:forEach>
                                    </c:if>
                                </div>
                            </c:forEach>
                        </c:if>
                        <c:if test="${empty evaList}">
                            <h3>暂无评价</h3>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- 尾部 -->
<jsp:include page="/user/include/foot.jsp"/>

<script type="text/javascript">
    var flag = true;
    var layer;
    var rate;
    layui.use(['layer', 'rate'], function () {
        layer = layui.layer;
        // 评分组件
        rate = layui.rate;

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
            });
        });

        // 监控输入框变化
        $("#num").bind('input propertychange', function () {
            var num = parseInt($("#num").val());
            if (num <= 0) {
                layer.msg('输入值不合法！', {
                    icon: 5,
                    time: 2000
                });
                $("#num").val(1);
            }
        });

        // 评分组件 重新渲染全部
        rate.render({
            elem: '.evaStar',  //绑定元素
            theme: '#FF5722', //颜色
            readonly: true, //只读
        });

        // 声明一个数组 遍历评论 依次向数组尾部添加其主键id和评分星级
        var arr = [];
        <c:forEach items="${evaList}" var="e">
        arr.push('${e.id}${e.level}');
        </c:forEach>
        for (var i = 0; i < arr.length; i++) {
            // 获取主键id 左闭右开
            var id = arr[i].substring(0, arr[i].length - 1);
            // 获取评分星级 截取一个长度的字符
            var v = arr[i].substr(arr[i].length - 1, 1);
            // 渲染评分组件 星级
            rate.render({
                elem: '#evaStar' + id,  //绑定元素
                theme: '#FF5722',
                readonly: true,
                value: v
            });
        }
    });

    $(function () {
        isFavorite();
    });

    // 购买数量递减
    function reduceCounts() {
        var num = parseInt($("#num").val());
        if (num - 1 <= 0) {
            $("#redbtn").prop("disabled", true);
        } else {
            num--;
            $("#num").val(num);
        }
    }

    // 购买数量递增
    function addCounts() {
        var num = parseInt($("#num").val());
        $("#num").val(num + 1);
        $("#redbtn").prop("disabled", false);
    }

    // 加入购物车
    function addToCart() {
        var num = $("#num").val();
        var goodsId = $("#goodsId").val();
        $.ajax({
            type: "post",
            url: "/api/carts/insert",
            data: {"goodsId": goodsId, "num": num},
            success: function (data) {
                if (data.message == "success") {
                    layer.msg('添加成功！', {
                        icon: 1,
                        time: 2000
                    });
                    showHotGoods();
                } else {
                    layer.msg('添加失败！', {
                        icon: 5,
                        time: 2000
                    });
                }
            }
        });
    }

    // 加入收藏
    function addToFavorite() {
        var goodsId = $("#goodsId").val();
        $.ajax({
            type: "post",
            url: "/api/guess/insertFavorite",
            data: {"goodsId": goodsId},
            success: function (data) {
                if (data.message == "success") {
                    layer.msg('收藏成功！', {
                        icon: 1,
                        time: 2000
                    });
                    // 收藏完毕 加载热卖商品
                    showHotGoods();
                } else {
                    layer.msg('收藏失败！', {
                        icon: 5,
                        time: 2000
                    });
                }
                // 判断是否收藏过该商品 避免重复收藏操作
                isFavorite();
            }
        });
    }

    // 取消收藏
    function removeFavorite() {
        var goodsId = $("#goodsId").val();
        $.ajax({
            type: "post",
            url: "/api/guess/removeFavorite",
            data: {"goodsId": goodsId},
            success: function (data) {
                if (data.message == "success") {
                    layer.msg('取消收藏成功！', {
                        icon: 1,
                        time: 2000
                    });
                    // 取消收藏完毕 加载热卖商品
                    showHotGoods();
                } else {
                    layer.msg('取消收藏失败！', {
                        icon: 5,
                        time: 2000
                    });
                }
                // 判断是否收藏过该商品 避免重复收藏操作
                isFavorite();
            }
        });
    }

    // 判断该商品是否已收藏
    function isFavorite() {
        var goodsId = $("#goodsId").val();
        $.ajax({
            type: "post",
            url: "/api/guess/isFavorite",
            data: {"goodsId": goodsId},
            success: function (data) {
                if (data == "true") {
                    $("#fav").hide();
                    $("#unfav").show();
                } else {
                    $("#fav").show();
                    $("#unfav").hide();
                }
            }
        });
    }

    // 展示热卖商品
    function showHotGoods() {
        $.ajax({
            type: "post",
            url: "/api/goods/selectByHot",
            dataType: "json",
            success: function (res) {
                var arr = res.data;
                var str = "<h2>热卖推荐</h2><div><div class='span16' style='width:1120px;'><ul>";
                for (var i = 0; i < arr.length; i++) {
                    str = str
                        + "<a href='/goods/detail/"
                        + arr[i].id
                        + "'>"
                        + "<li><img src='/images/" + arr[i].img + "' /><p class='goods-title'>"
                        + arr[i].name + "</p>"
                        + "<p class='goods-desc'>"
                        + arr[i].description
                        + "</p><p><span class='newprice'>"
                        + arr[i].price + "</span>&nbsp;"
                        + "</p></li></a>";
                }
                str = str + "</ul></div></div>";
                if (flag) {
                    flag = false;
                    $("#hotGoods").html(str);
                }
            }
        });
    }
</script>
</body>
</html>
