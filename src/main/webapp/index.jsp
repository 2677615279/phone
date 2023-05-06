<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
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

    <link href="/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
    <link href="/css/layui.css" rel="stylesheet">
    <script src="/js/jquery.min.js" type="text/javascript"></script>
    <script src="/js/bootstrap.min.js" type="text/javascript"></script>
    <script src="/js/layui.js" type="text/javascript"></script>
</head>
<body style="height: 100%">
<!--导航栏部分-->
<jsp:include page="/user/include/header.jsp" />
<!-- 中间内容 -->
<div class="container-fluid">
    <div class="row">
        <!-- 控制栏 -->
        <div class="col-sm-3 col-md-2 sidebar sidebar-1">
            <ul class="nav nav-sidebar">
                <c:forEach items="${applicationScope.goodsTypeList}" var="t" varStatus="c">
                    <c:if test="${fn:length(t.goodsVOList) != 0 && t.goodsVOList != null}">
                        <c:if test="${c.count == 1}">
                            <li class="list-group-item-diy"><a href="/#productArea${t.id}">${t.name}<span
                                    class="sr-only">(current)</span></a></li>
                        </c:if>
                        <c:if test="${c.count != 1}">
                            <li class="list-group-item-diy"><a href="/#productArea${t.id}">${t.name}</a></li>
                        </c:if>
                    </c:if>
                </c:forEach>
            </ul>
        </div>
        <!-- 控制内容 -->
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
            <div id="myCarousel" class="layui-carousel">
                <div carousel-item>
                    <c:forEach items="${applicationScope.bannerList}" var="b">
                        <div><a href="${b.url}"><img style="width:1144px;height:300px;" src="/images/${b.img}"></a></div>
                    </c:forEach>
                </div>
            </div>

            <!-- <div name="productArea1" class="row pd-10" id="productArea1">
                <div class="span16">
                    <h2 class="title">苹果手机</h2>
                    <ul>
                                    <a href="#">
                                        <li>
                                            <img src="upload/红米note4X.jpg" />
                                            <p class="goods-title">小米6X 4GB+32GB</p>
                                            <p class="goods-desc">全索尼相机，骁龙660 AIE处理器</p>
                                            <p><span class="newprice">1499元</span>&nbsp;
                                            </p>
                                        </li>
                                    </a>
                </div>
            </div> -->
            <c:forEach items="${applicationScope.goodsTypeList}" var="t">
                <c:if test="${fn:length(t.goodsVOList) != 0 && t.goodsVOList != null}">
                    <div name="productArea${t.id}" class="row" id="productArea${t.id}"></div>
                </c:if>
            </c:forEach>
        </div>
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2">
            <jsp:include page="/user/include/foot.jsp" />
        </div>
    </div>
</div>

<script type="text/javascript">
    var layer;
    layui.use(['carousel','layer'], function(){
        layer=layui.layer;
        var carousel = layui.carousel;

        //建造实例
        carousel.render({
            elem: '#myCarousel'
            ,width: '1144px' //设置容器宽度
            ,anim: 'fade' //切换动画方式
            ,autoplay: true     //自动切换
            ,interval: 3000
        });
    });

    $(function(){
        getAllType();
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
    });

    // 获取所有 正在售卖商品的所属分类
    function getAllType(){
        $.ajax({
            type: "POST",
            url: "/api/goodstypes/selectAll",
            success: function(data){
                var arr = data.data;
                for(var i = 0; i < arr.length; i++){
                    showGoodsByType(arr[i].id);
                }
            }
        });
    }

    // 根据分类id 获取该类别下的商品列表
    function showGoodsByType(id){
        $.ajax({
            type: "POST",
            url: "/api/goods/selectByType",
            data: {"typeId": id},
            success: function(data){
                var arr = data.data;
                var str="<div class='span16'><ul>";
                for(var i=0;i<arr.length;i++){
                    if(i==0){
                        str=str+"<h2 class='title'>"+arr[i].type.name+"</h2>";
                    }
                    if((i+1)%4!=0){
                        str=str+"<a href='/goods/detail/"+arr[i].id+"'><li>"+
                            "<img src='/images/"+arr[i].img+"' /><p class='goods-title'>"+arr[i].name+"</p>"+
                            "<p class='goods-desc'>"+arr[i].description+"</p><p><span class='newprice'>"+arr[i].price+"元</span>&nbsp;"+
                            "</p></li></a>";
                    }else{
                        str=str+"<a href='/goods/detail/"+arr[i].id+"'><li class='brick4'>"+
                            "<img src='/images/"+arr[i].img+"' /><p class='goods-title'>"+arr[i].name+"</p>"+
                            "<p class='goods-desc'>"+arr[i].description+"</p><p><span class='newprice'>"+arr[i].price+"元</span>&nbsp;"+
                            "</p></li></a>";
                    }
                }
                var str=str+"</ul></div>";
                $("#productArea"+id).html(str);
            }
        });
    }
</script>
</body>
</html>
