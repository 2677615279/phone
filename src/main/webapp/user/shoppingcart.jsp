<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>商品智能交易平台</title>
    <link href="/css/style.css" rel="stylesheet">
    <link href="/css/bootstrap.min.css" rel="stylesheet">

    <link href="/css/layui.css" media="all" rel="stylesheet">
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
        #shoppingCarTable thead tr th{
            text-align:center;
        }
        #shoppingCarTable tbody tr td{
            text-align:center;
        }
    </style>
</head>
<body>
<!--导航栏部分-->
<jsp:include page="include/header.jsp" />

<!-- 中间内容 -->
<div class="container-fluid bigHead" id="maincart">
    <div class="row" style="background-color:white;">
        <div style="width:100%;height:120px;background-color:#BDDFFB;text-align:center;">
            <img style="width:1226px;height:120px;margin:0 auto;" src="/images/cartbanner3.jpg" />
        </div>
        <div class="col-sm-10  col-md-10 col-sm-offset-1 col-md-offset-1">
            <form id="cartForm" action="/carts/preOrder" method="post" class="layui-form">
                <div style="text-align:center;">
                    <table class="layui-table" lay-skin="nob" lay-size="lg" id="shoppingCarTable">
                    </table>
                </div>
                <hr />
                <div style='text-align:right;width:1000px;height:50px;font-size:16px;color:red;'>已选<span id="totalNum">0</span>件&nbsp;&nbsp;&nbsp;总价：<span id='totalPrice'>0</span>元</div>
                <div class="row" style="text-align:center;">
                    <button style="width:300px;" type="button" class="layui-btn layui-btn-normal layui-btn-lg" onclick="confirmPre();">去结算</button>
                </div>
            </form>
        </div>
    </div>
</div>
<!-- 尾部 -->
<jsp:include page="include/foot.jsp" />

<script type="text/javascript">
    var layer;
    var table;
    var form;
    $(function(){
        layui.use(['layer','table','form'], function () {
            layer = layui.layer;
            table=layui.table;
            form=layui.form;
            form.on('checkbox("th")',function(data){
                // 得到checkbox原始DOM对象
                console.log(data.elem);
            });

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

            // 查询当前用户的购物车列表
            $.ajax({
                type : 'POST',
                url : '/api/carts/selectByUser',
                dataType : 'json',
                success : function(res) {
                    var arr = res.data;
                    if(arr!=null && arr!="" && arr.length != 0){
                        var str='<thead><tr>'+
                            '<th><input onclick="selectAll()" lay-ignore style="margin-left:10px;" type="checkbox" id="allSelect"></th>'+
                            '<th>商品图</th>'+
                            '<th>商品名称</th>'+
                            '<th>商品单价</th>'+
                            '<th>商品数量</th>'+
                            '<th>总价</th>'+
                            '<th>是否刪除</th>'+
                            '</tr></thead><tbody>';
                        for(var i=0;i<arr.length;i++){
                            str=str+'<tr id="goodsData'+arr[i].id+'">'+
                                '<td>'+
                                '<input type="checkbox" lay-ignore id="box'+arr[i].id+'" name="goodsList" value="'+arr[i].id+'">'+
                                '</td>'+
                                '<td><img style="height:40px;" src="/images/'+arr[i].goods.img+'" />'+
                                '<td><a style="text-decoration:none" href="/goods/detail/'+arr[i].goods.id+'">'+arr[i].goods.name+'</a></td>'+
                                '<td><span id="singlePrice'+arr[i].id+'">'+arr[i].goods.price+'元</span></td>'+
                                '<td><button type="button" onclick="reduceNum('+arr[i].id+')" class="layui-btn" id="reduce'+arr[i].id+'">-</button><span style="width:50px;height:25px;display:inline-block;" id="num'+arr[i].id+'">'+arr[i].num+'</span><button type="button" onclick="addNum('+arr[i].id+');" class="layui-btn" id="add'+arr[i].id+'">+</button></td>'+
                                '<td><span id="totalPrice'+arr[i].id+'">'+arr[i].num*arr[i].goods.price+'</span>元</td>'+
                                '<td><button class="layui-btn layui-btn-danger" type="button" onclick="deleteCart('+arr[i].id+')">删除</button></td>'+
                                '</tr>';
                        }
                        str=str+"</tbody>";
                        $("#shoppingCarTable").html(str);
                        form.render();
                        $("input[name='goodsList']").bind("click",function(){
                            changeTotal();
                            var flag=checkAll();
                            $("#allSelect").prop("checked",flag);
                        });
                    }else{
                        var empty="<div style='width:100%;height:500px;'><div style='text-align:center;padding-top:100px;'>"+
                            "<img src='/images/emptycart.png'><p><h3>亲，您的购物车里还没有物品哦，赶紧去<a href='/'>逛逛</a>吧!</h3></p></div></div>";
                        $("#maincart").html(empty);
                    }
                },
                error : function(result) {
                    layer.msg('查询错误');
                }
            });
        });
    });

    // 选择全部
    function selectAll(){
        var flag=$("#allSelect").prop("checked");
        var arr=$("input[name='goodsList']");
        for(var i=0;i<arr.length;i++){
            $(arr[i]).prop("checked",flag);
        }
        changeTotal();
    }

    // 确认是否已选择全部
    function checkAll(){
        var arr=$("input[name='goodsList']");
        for(var i=0;i<arr.length;i++){
            if(!$(arr[i]).prop("checked")){
                return false;
            }
        }
        return true;
    }

    // 改变商品总数和订单总价
    function changeTotal(){
        var arr=$("input[name='goodsList']");
        var totalPrice=0;
        var num=0;
        for(var i=0;i<arr.length;i++){
            if($(arr[i]).prop("checked")){
                num++;
                var cartId=$(arr[i]).val();
                var singleTotal=parseInt($("#totalPrice"+cartId).html());
                totalPrice+=singleTotal;
            }
        }
        $("#totalPrice").html(totalPrice);
        $("#totalNum").html(num);
    }

    // 购物车中商品数量递减
    function reduceNum(id){
        var num=parseInt($("#num"+id).html());
        num--;
        if(num<=0){
            $(this).attr("disabled",true);
        }else{
            $.ajax({
                type: "POST",
                url: "/api/carts/reduceCartNum",
                data: {"id": id},
                success:function(data){
                    if(data.message=="success"){
                        $("#num"+id).html(num);
                        refreshPrice(id);
                    }
                }
            });
        }
    }

    // 购物车中商品数量递增
    function addNum(id){
        var num=parseInt($("#num"+id).html());
        num++;
        $("reduceNum"+id).attr("disabled",false);
        $.ajax({
            type: "POST",
            url: "/api/carts/addCartNum",
            data: {"id": id},
            success:function(data){
                if(data.message=="success"){
                    $("#num"+id).html(num);
                    refreshPrice(id);
                }
            }
        });
    }

    // 刷新该id代表购物车的总价
    function refreshPrice(id){
        var num=parseInt($("#num"+id).html());
        var singlePrice=parseInt($("#singlePrice"+id).html());
        var total=num*singlePrice;
        $("#totalPrice"+id).html(total);
        changeTotal();
    }

    // 删除购物车
    function deleteCart(id){
        layer.confirm("真的要删除吗？",function(){
            $.ajax({
                type:"post",
                url:"/api/carts/deleteLogic",
                data: {"id": id},
                success:function(data){
                    if(data.message=="success"){
                        layer.msg('删除成功', { icon: 1, shade: 0.4, time: 1000 });
                        $("#goodsData"+id).remove();
                    }else{
                        layer.msg('删除失败', { icon: 5, shade: 0.4, time: 1000 });
                    }
                    // $("#goodsData"+id).remove();
                }
            });
        })
    }

    // 确认 如果购物车列表有商品，则提交；无商品，则提示 请选择购买的商品
    function confirmPre(){
        var num=parseInt($("#totalNum").html());
        if(num<1){
            layer.msg('请选择购买的商品！', { icon: 5, anim: 6 ,shade: 0.4, time: 1000 });
        }else{
            $("#cartForm").submit();
        }
    }
</script>
</body>
</html>
