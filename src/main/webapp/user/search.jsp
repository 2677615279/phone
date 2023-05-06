<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>乐享手机商城 搜索结果</title>
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
<div class="container">
    <form class="layui-form searchForm"  style="height: 70px;margin-top: 80px;">
        <div class="layui-input-inline"  style="vertical-align:top;">
            <div class="layui-form-item">
                <label class="layui-form-label" style="width: 90px;">商品名称</label>
                <div class="layui-input-inline">
                    <input type="text"   name="keyword" id="keywords"  placeholder="请输入查询的商品名称"
                           autocomplete="off" class="layui-input">
                </div>
            </div>
        </div>
        <div class="layui-input-inline"  style="vertical-align:top;">
            <div class="layui-form-item">
                <div class="layui-inline">
                    <label class="layui-form-label">金额</label>
                    <div class="layui-input-inline" style="width: 100px;">
                        <input type="text" name="left" id="startAmount" autocomplete="off" lay-verify="number" class="layui-input">
                    </div>
                    <div class="layui-form-mid">-</div>
                    <div class="layui-input-inline" style="width: 100px;">
                        <input type="text" name="right" id="endAmount" autocomplete="off" lay-verify="number"  class="layui-input">
                    </div>
                </div>
            </div>
        </div>
        <div class="layui-input-inline" style="vertical-align:top;">
            <button type="button" lay-submit lay-filter="searchForm" class="layui-btn">查询商品</button>
        </div>
    </form>
</div>
<div class="container">
    <div class="row">
        <div id="searchResultArea">
            <div class="span16" style="width:1120px;">
                <ul>
                    <c:forEach items="${searchList}" var="g" varStatus="t">
                        <c:if test="${t.count % 4 != 0}">
                            <a href="/goods/detail/${g.id}">
                                <li><img src="/images/${g.img}"/>
                                    <p class="goods-title">${g.name}</p>
                                    <p class="goods-desc">${g.description}</p>
                                    <p>
                                        <span class="newprice">${g.price}</span>&nbsp;
                                    </p>
                                </li>
                            </a>
                        </c:if>
                        <c:if test="${t.count % 4 == 0}">
                            <a href="/goods/detail/${g.id}">
                                <li class='brick4'><img src="/images/${g.img}"/>
                                    <p class="goods-title">${g.name}</p>
                                    <p class="goods-desc">${g.description}</p>
                                    <p>
                                        <span class="newprice">${g.price}元</span>&nbsp;
                                    </p>
                                </li>
                            </a>
                        </c:if>
                    </c:forEach>
                </ul>
            </div>
        </div>
    </div>

</div>

<!-- 尾部 -->
<jsp:include page="/user/include/foot.jsp" />

<script type="text/javascript">
    layui.use('form', function () {
        var form = layui.form;

        $("#logout").click(function () {
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
                    layer.close(index);
                },
                error: function () {
                    layer.msg('退出失败！', {icon: 5, shade: 0.4, time: 2000});
                    layer.close(index);
                    window.location.href = "/error.jsp";
                },
                dataType: "json"
            });
        });

        form.verify({
            //数组的两个值分别代表：[正则匹配、匹配不符时的提示文字]
            number: function(value, item){ //value：表单的值、item：表单的DOM对象
                if(value != null && value != ""){
                    if(!/^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$/.test(value)){
                        return '请输入数字';
                    }
                }
            }
        });

        form.on('submit(searchForm)', function (data) {
            if(data.field.left != null && data.field.left != '' && data.field.right != null && data.field.right !=''){
                if( parseInt(data.field.left) > parseInt(data.field.right)){
                    layer.msg(" 数字范围不正确！",{icon:5,anim:6,time:3000});
                    return false;
                }
            }

            $.ajax({
                type: "post",
                url: "/api/goods/search",
                data: data.field,
                dataType: "json",
                success: function (res) {
                    var arr = res.data;
                    if(arr.length == 0 || arr == null || arr == ''){
                        var str= "<img src='/images/not.jpg' style='width: 100%;height: 100%'>";
                        $("#searchResultArea").html(str);
                    }else{
                        var str = "<div class='span16' style='width:1120px;'><ul>";
                        for (var i = 0; i < arr.length; i++) {
                            if ((i + 1) % 4 != 0) {
                                str = str + "<a href='/goods/detail/" + arr[i].id + "'><li>" +
                                    "<img src='/images/" + arr[i].img + "' /><p class='goods-title'>" + arr[i].name + "</p>" +
                                    "<p class='goods-desc'>" + arr[i].description + "</p><p><span class='newprice'>" + arr[i].price + "元</span>&nbsp;" +
                                    "</p></li></a>";
                            } else {
                                str = str + "<a href='/goods/detail/" + arr[i].id + "'><li class='brick4'>" +
                                    "<img src='/images/" + arr[i].img + "' /><p class='goods-title'>" + arr[i].name + "</p>" +
                                    "<p class='goods-desc'>" + arr[i].description + "</p><p><span class='newprice'>" + arr[i].price + "元</span>&nbsp;" +
                                    "</p></li></a>";
                            }
                        }
                        var str = str + "</ul></div>";
                        $("#searchResultArea").html(str);
                    }
                }
            });
        })
    });



    /*function searchPre() {
        var word = $("#newSearchKeyWord").val();
        $.ajax({
            type: "post",
            url: "goods/searchPre",
            data: "keyword=" + word,
            dataType: "json",
            success: function (arr) {
                var str = "<div class='span16' style='width:1120px;'><ul>";
                for (var i = 0; i < arr.length; i++) {
                    if ((i + 1) % 4 != 0) {
                        str = str + "<a href='goods/detail?goodsId=" + arr[i].goodsId + "'><li>" +
                            "<img src='upload/" + arr[i].goodsImg + "' /><p class='goods-title'>" + arr[i].goodsName + "</p>" +
                            "<p class='goods-desc'>" + arr[i].goodsDesc + "</p><p><span class='newprice'>" + arr[i].goodsPrice + "元</span>&nbsp;" +
                            "</p></li></a>";
                    } else {
                        str = str + "<a href='goods/detail?goodsId=" + arr[i].goodsId + "'><li class='brick4'>" +
                            "<img src='upload/" + arr[i].goodsImg + "' /><p class='goods-title'>" + arr[i].goodsName + "</p>" +
                            "<p class='goods-desc'>" + arr[i].goodsDesc + "</p><p><span class='newprice'>" + arr[i].goodsPrice + "元</span>&nbsp;" +
                            "</p></li></a>";
                    }
                }
                var str = str + "</ul></div>";
                $("#searchResultArea").html(str);
            }
        });
    }*/
</script>
</body>
</html>
