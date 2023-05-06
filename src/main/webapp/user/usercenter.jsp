<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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

    <link href="/css/layui.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
    <script src="/js/jquery.min.js" type="text/javascript"></script>
    <script src="/js/ajaxfileupload.js" type="text/javascript"></script>
    <script src="/js/bootstrap.min.js" type="text/javascript"></script>
    <script src="/js/layui.js" type="text/javascript"></script>
    <style type="text/css">
        a {
            text-decoration: none;
            color: #333;
            out-line: none;
        }
        .layui-form-label{
            width:100px;
        }
    </style>
</head>
<body>
<!--导航栏部分-->
<jsp:include page="/user/include/header.jsp" />
<!-- 中间内容 -->
<div class="container-fluid">
    <div class="row">
        <!-- 控制栏 -->
        <div class="col-sm-3 col-md-2 sidebar sidebar-1">
            <ul class="nav nav-sidebar">
                <li class="list-group-item-diy"><a
                        href="/user/center/#section1">收藏中心<span class="sr-only">(current)</span></a></li>
                <li class="list-group-item-diy"><a
                        href="/user/center/#section2">我的订单</a></li>
                <li class="list-group-item-diy"><a
                        href="/user/center/#section3">地址管理</a></li>
                <li class="list-group-item-diy"><a
                        href="/user/center/#section4">猜你喜欢</a></li>
            </ul>
        </div>
        <!-- 控制内容 -->
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
            <div class="col-md-12">
                <hr />
                <h1>
                    <a name="section1">收藏中心</a>
                </h1>
                <hr />
                <div class="col-lg-12 col-md-12 col-sm-12" id="productArea"></div>
                <div id="pageBtn"></div>
                <br />
            </div>

            <div class="col-md-12">
                <hr />
                <h1>
                    <a name="section2">我的订单</a>
                </h1>
                <hr />
                <div class="layui-tab layui-tab-brief" lay-filter="docDemoTabBrief">
                    <ul class="layui-tab-title">
                        <li class="layui-this">待付款</li>
                        <li>待发货</li>
                        <li>待收货</li>
                        <li>待评价</li>
                        <li>完成</li>
                    </ul>
                    <div class="layui-tab-content" style="height: auto;">
                        <div class="layui-tab-item layui-show" id="state1">
                            <!-- <div class='layui-colla-item'> -->
                            <!-- <h2 class='layui-colla-title'>订单编号：3724a838bbc64359a33de2c198859431&nbsp;&nbsp;总价：17995元&nbsp;</h2>
                            <div class='layui-colla-content'>
                                <h4>收货人：汤姆&nbsp;&nbsp;收货地址：河南郑州二七幸福小区</h4>
                                <div class='layui-form'>
                                    <table class='layui-table'>
                                        <thead>
                                            <tr>
                                                <th>商品名称</th>
                                                <th>价格</th>
                                                <th>数量</th>
                                                <th>小计</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td><a href='goods/detail?goodsId=7'>OPPO R11s
                                                        Plus</a></td>
                                                <td>3599</td>
                                                <td>5</td>
                                                <td>17995</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                                <a href='javascript:void(0)'
                                    class='layui-btn layui-btn-normal layui-btn-fluid layui-btn-radius'>去付款</a>
                            </div> -->
                            <!-- </div> -->
                        </div>
                        <div class="layui-tab-item" id="state2"></div>
                        <div class="layui-tab-item" id="state3"></div>
                        <div class="layui-tab-item" id="state4"></div>
                        <div class="layui-tab-item" id="state5"></div>
                    </div>
                </div>
            </div>
            <div class="col-md-12">
                <hr />
                <h1>
                    <a name="section3">地址管理</a>
                </h1>
                <hr />
                <div class="col-sm-offset-2 col-md-offest-2" style="margin-left:0%;">
                    <div id="addrData">
                        <%-- <table class="layui-table">
                              <colgroup>
                                <col width="100">
                                <col width="150">
                                <col width="300">
                                <col width="340">
                                <col>
                              </colgroup>
                        <thead>
                            <tr>
                              <th>收货人</th>
                              <th>电话</th>
                              <th>所在地区</th>
                              <th>详细地址</th>
                              <th>操作</th>
                            </tr>
                          </thead>
                          <tbody>
                            <tr>
                              <td>贤心</td>
                              <td>2016-11-29</td>
                              <td>人生就像是一场修行</td>
                              <td>人生就像是一场修行</td>
                              <td>
                                   <button class="layui-btn layui-btn-xs layui-btn-warm">编辑</button>
                                 <button class="layui-btn layui-btn-xs layui-btn-danger">删除</button>
                              </td>
                            </tr>
                          </tbody>
                        </table> --%>
                    </div>
                    <button onclick="addAddress()" class="layui-btn layui-btn-normal layui-btn-lg layui-btn-fluid layui-btn-radius">添加地址</button>
                </div>
            </div>

            <div class="col-md-12">
                <hr />
                <h1>
                    <a name="section4">猜你喜欢</a>
                </h1>
                <hr />
                <table class="table table-hover center" id="recentViewArea">
                </table>
            </div>
        </div>
    </div>
</div>


<!-- 尾部 -->
<jsp:include page="/user/include/foot.jsp" />
<div class="layui-form" id="addrForm" style="display:none;padding-top:10px;">
    <form id="addrFormData">
        <input type="hidden" name="id" id="addrId" />
        <div class="layui-form-item">
            <label class="layui-form-label">收货人名称</label>
            <div class="layui-input-inline">
                <input type="text" name="nickName" id="addrNickName" required lay-verify="required" placeholder="请输入收货人名称" value="" class="layui-input" />
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">收货人电话</label>
            <div class="layui-input-inline">
                <input type="text" name="phone" id="addrPhone" required lay-verify="required" placeholder="请输入收货电话" value="" class="layui-input" />
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">请选择地区</label>
            <div class="layui-input-inline">
                <select name="province" id="proData" lay-filter="province" required lay-verify="required">
                </select>
            </div>
            <div class="layui-input-inline">
                <select name="city" id="cityData" lay-filter="city" required lay-verify="required">
                    <option value="">请选择市</option>
                </select>
            </div>
            <div class="layui-input-inline">
                <select name="area" id="areaData" required lay-verify="required">
                    <option value="">请选择县/区</option>
                </select>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">详细地址</label>
            <div class="layui-input-inline">
                <input style="width:590px;" type="text" name="detail" id="addrDetail" placeholder="请输入详细地址" required lay-verify="required" value="" class="layui-input" />
            </div>
        </div>
        <button style="display:none;" type="reset" id="resetBtn">重置</button>
    </form>
</div>
<div id="evaData" style="width:600px;padding-top:15px;display:none;">
    <form id="evaForm">
        <input type="hidden" id="evaOrderId" name="orderId" />
        <div class="layui-form-item">
            <label class="layui-form-label" style="padding-top:15px;">评分</label>
            <div class="layui-input-inline">
                <input type="hidden" name="level" required lay-verify="required" id="evaStar" />
                <div id="evaLevel"></div>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">评论图</label>
            <div  class="layui-input-block">
                <button type="button" class="layui-btn" id="test2">多图片上传</button>
                <button type="button" class="layui-btn" id="test6">开始上传</button>
                <button type="button" class="layui-btn layui-danger" onclick="cleanImgsPreview()" id="cleanImgs">清空预览</button>
                预览图：
                <div class="layui-upload-list" id="demo2"></div>
            </div>
        </div>
        <input type="text" id="imgUrls" name="imgUrls" style="display: none;" class="layui-input">
        <div class="layui-form-item">
            <label class="layui-form-label">评价内容</label>
            <div class="layui-input-inline">
                <textarea rows="8" cols="50"  required lay-verify="required" name="content"></textarea>
            </div>
        </div>
        <button type="reset" style="display:none;" id="evaReset"></button>
    </form>
</div>
<script type="text/javascript">
    var layer;
    var element;
    var form;
    var rate;
    var upload;
    var laypage;
    var success=0;
    var fail=0;
    var imgurls="";
    var totalCount=0;
    var currentPage=1;

    layui.use([ 'layer', 'element','form','rate','upload','laypage'], function() {
        layer = layui.layer;
        element = layui.element;
        rate=layui.rate;
        upload=layui.upload;
        laypage=layui.laypage;
        form = layui.form;

        element.render();
        laypage.render({
            elem: 'pageBtn'
            ,count: totalCount //数据总数，从服务端得到
            ,limit:8
            ,jump: function(obj, first){
                //obj包含了当前分页的所有参数，比如：
                currentPage=obj.curr;
                if(!first){
                    showFavorite(obj.curr);
                }
            }
        });

        // 上传评价图
        var uploadInst = upload.render({
            elem: '#test2' //绑定元素
            ,url: '/api/files/uploadEvaImg' //绑定上传接口
            ,method: 'post'
            ,multiple: true //允许多文件上传
            ,number:5 //设置同时可上传的文件数量
            ,auto:false  //选择文件后不自动上传
            ,bindAction:'#test6' // 触发按钮
            ,field: 'multipartFile'
            ,accept: 'images'//校验上传文件类型
            ,headers: {access_token: localStorage.access_token}
            ,acceptMime: "image/*"
            ,before: function(obj){
                //预读本地文件示例，不支持ie8
                obj.preview(function(index, file, result){
                    $('#demo2').append('<img style="width:70px;height:70px;" src="'+ result +'" alt="'+ file.name +'" class="layui-upload-img">')
                });
            },done: function(res, index, upload) {
                //每个图片上传结束的回调，成功的话，就把新图片的名字保存起来，作为数据提交
                console.log(res.code);
                if(res.code != 200){
                    fail++;
                }else{
                    success++;
                    imgurls=imgurls+""+res.data+",";
                }
                $('#imgUrls').val(imgurls);
            },
            allDone:function(obj){
                layer.msg("总共要上传图片总数为："+(fail+success)+"\n"
                    +"其中上传成功图片数为："+success+"\n"
                    +"其中上传失败图片数为："+fail
                )
            }
        });

        // 重新渲染评价星级
        var ins1 = rate.render({
            elem: '#evaLevel',  //绑定元素
            theme: '#FF5722',
            choose:function(value){
                $("#evaStar").val(value);
            }
        });

        // 绑定表单省份元素的选择事件
        form.on('select(province)',function(data){
            $.ajax({
                type:"post",
                url:"/api/receivingaddress/selectCityByProvinceId",
                data:{"provinceId": data.value},
                dataType:"json",
                success:function(res){
                    var data = res.data;
                    var str="<option value=''>请选择市</option>";
                    for(var i=0;i<data.length;i++){
                        str=str+"<option value='"+data[i].id+"'>"+data[i].name+"</option>";
                    }
                    $("#cityData").html(str);
                    form.render();
                }
            });
        });

        // 绑定表单城市元素的选择事件
        form.on('select(city)',function(data){
            $.ajax({
                type:"post",
                url:"/api/receivingaddress/selectAreaByCityId",
                data:{"cityId": data.value},
                dataType:"json",
                success:function(res){
                    var data = res.data;
                    var str="<option value=''>请选择县/区</option>";
                    for(var i=0;i<data.length;i++){
                        str=str+"<option value='"+data[i].id+"'>"+data[i].name+"</option>";
                    }
                    $("#areaData").html(str);
                    form.render();
                }
            });
        });
    });

    $(function() {
        showFavorite(1);
        showReadyPayOrder();
        findReadyToDeliverOrder();
        findReadyToReceiveOrder();
        findReadyToEvaluateOrder();
        findFinishOrder();
        showUserAddress();
        showRecentView();
        showProvince();
    });

    // 格式化日期为yyyy-MM-dd
    Date.prototype.format = function(format) {
        var o = {
            "M+" : this.getMonth()+1, //month
            "d+" : this.getDate(),    //day
            "h+" : this.getHours(),   //hour
            "m+" : this.getMinutes(), //minute
            "s+" : this.getSeconds(), //second
            "q+" : Math.floor((this.getMonth()+3)/3),  //quarter
            "S" : this.getMilliseconds() //millisecond
        };
        if(/(y+)/.test(format)) format=format.replace(RegExp.$1,
            (this.getFullYear()+"").substr(4 - RegExp.$1.length));
        for(var k in o)if(new RegExp("("+ k +")").test(format))
            format = format.replace(RegExp.$1,
                RegExp.$1.length==1 ? o[k] :
                    ("00"+ o[k]).substr((""+ o[k]).length));
        return format;
    };

    // 清空预览的评价图
    function cleanImgsPreview(){
        success=0;
        fail=0;
        $('#demo2').html("");
        $('#imgUrls').val("");
    }

    // 展示当前登录用户的收货地址列表
    function showUserAddress(){
        $.ajax({
            type:"post",
            url:"/api/receivingaddress/selectBySelf",
            dataType:"json",
            success:function(res){
                var data = res.data;
                if(data!=null && data!="" && data.length > 0){
                    str="<table class='layui-table'><colgroup><col width='100'><col width='150'>"+
                        "<col width='300'><col width='340'><col></colgroup><thead><tr><th>收货人</th>"+
                        "<th>电话</th><th>所在地区</th><th>详细地址</th><th>操作</th></tr></thead><tbody>";
                    for(var i=0;i<data.length;i++){
                        str=str+"<tr><td>"+data[i].nickName+"</td><td>"+data[i].phone+"</td>"+
                            "<td>"+data[i].province+data[i].city+data[i].area+"</td>"+
                            "<td>"+data[i].detail+"</td><td>"+
                            "<button onclick='modifyAddress("+data[i].id+")' class='layui-btn layui-btn-xs layui-btn-warm'>编辑</button>"+
                            "<button onclick='deleteAddress("+data[i].id+")' class='layui-btn layui-btn-xs layui-btn-danger'>删除</button>"+
                            "</td></tr>";
                    }
                    str=str+"</tbody></table>";
                }else{
                    str="<h3>暂无收货地址！</h3>";
                }
                $("#addrData").html(str);
            }
        });
    }

    // 发表评价
    function publishEva(id){
        layer.open({
            type: 1,
            title: '发表评价',
            shade: 0.4,  //阴影度
            fix: false,
            shadeClose: true,
            maxmin: false,
            area: ['700px;', '500px;'],    //窗体大小（宽,高）
            content: $('#evaData'),
            btn:['发布','取消'],
            success: function (layero, index) {
                layero.addClass('layui-form');//添加form标识
                layero.find('.layui-layer-btn0').attr('lay-filter', 'fromContent').attr('lay-submit', '');//将按钮弄成能提交的
                $("#evaOrderId").val(id);
                form.render();
            },
            yes: function(index, layero){
                form.on('submit(fromContent)',function(data){
                    $.post("/api/evaluates/insert",$("#evaForm").serialize(),function(data){
                        if (data.message == 'success'){
                            parent.layer.msg('发表评价成功！', { icon: 1, shade: 0.4, time: 1000 });
                        }else{
                            parent.layer.msg('发表评价失败！,请重试！', { icon: 5, shade: 0.4, time: 1000 });
                        }
                        layer.close(index);
                        findReadyToEvaluateOrder();
                        findFinishOrder();
                        $("#evaReset").click();
                        cleanImgsPreview();
                    });

                })
            }
        });
    }

    // 根据地址主键id 先查询出该地址 再修改收货地址
    function modifyAddress(id){
        $.ajax({
            type:"post",
            url:"/api/receivingaddress/selectById",
            data:{"id": id},
            dataType:"json",
            success:function(res){
                var data = res.data;
                layer.open({
                    type: 1,
                    title: '修改地址',
                    shade: 0.4,  //阴影度
                    fix: false,
                    shadeClose: true,
                    maxmin: false,
                    area: ['800px;', '500px;'],    //窗体大小（宽,高）
                    content: $('#addrForm'),
                    btn:['修改','取消'],
                    success: function (layero, index) {
                        $("#addrId").val(data.id);
                        $("#addrNickName").val(data.nickName);
                        $("#addrPhone").val(data.phone);
                        $("#proData").val(data.province);
                        reloadCity(data.province,data.city);
                        reloadArea(data.city,data.area);
                        $("#addrDetail").val(data.detail);
                        layero.addClass('layui-form');//添加form标识
                        layero.find('.layui-layer-btn0').attr('lay-filter', 'fromContent').attr('lay-submit', '');//将按钮弄成能提交的
                        form.render();
                    },
                    yes: function(index, layero){
                        form.on('submit(fromContent)',function(data){
                            $.post("/api/receivingaddress/updateSelf",$("#addrFormData").serialize(),function(data){
                                if (data.message == 'success'){
                                    parent.layer.msg('修改地址成功！', { icon: 1, shade: 0.4, time: 1000 });
                                    showUserAddress();
                                }else{
                                    parent.layer.msg('修改地址失败！,请重试！', { icon: 5, shade: 0.4, time: 1000 });
                                }
                                layer.close(index);
                                $("#resetBtn").click();
                            });

                        })
                    }
                });
            }
        });
    }

    // 根据选择省份id 重新加载其下的城市
    function reloadCity(id,cityId){
        $.ajax({
            type:"post",
            url:"/api/receivingaddress/selectCityByProvinceId",
            data:{"provinceId": id},
            dataType:"json",
            success:function(res){
                var data = res.data;
                var str="<option value=''>请选择市</option>";
                for(var i=0;i<data.length;i++){
                    str=str+"<option value='"+data[i].id+"'>"+data[i].name+"</option>";
                }
                $("#cityData").html(str);
                $("#cityData").val(cityId);
                form.render();
            }
        });
    }

    // 根据选择城市id 重新加载其下的区县
    function reloadArea(id,areaId){
        $.ajax({
            type:"post",
            url:"/api/receivingaddress/selectAreaByCityId",
            data:{"cityId": id},
            dataType:"json",
            success:function(res){
                var data = res.data;
                var str="<option value=''>请选择县/区</option>";
                for(var i=0;i<data.length;i++){
                    str=str+"<option value='"+data[i].id+"'>"+data[i].name+"</option>";
                }
                $("#areaData").html(str);
                $("#areaData").val(areaId);
                form.render();
            }
        });
    }

    // 根据收货地址的主键id 删除收货地址
    function deleteAddress(id){
        layer.confirm("确定要删除该地址吗？",function(){
            $.ajax({
                type:"post",
                url:"/api/receivingaddress/deleteLogic",
                data:{"id": id},
                success:function(data){
                    if(data.message=="success"){
                        layer.msg('删除地址成功！', {
                            icon : 1,
                            time : 2000
                        });
                    }else{
                        layer.msg('删除地址失败！请重试！', {
                            icon : 5,
                            time : 2000
                        });
                    }
                    showUserAddress();
                }
            });
        });
    }

    // 添加收货地址
    function addAddress(){
        layer.open({
            type: 1,
            title: '添加地址',
            shade: 0.4,  //阴影度
            fix: false,
            shadeClose: true,
            maxmin: false,
            area: ['800px;', '500px;'],    //窗体大小（宽,高）
            content: $('#addrForm'),
            btn:['添加','取消'],
            success: function (layero, index) {
                layero.addClass('layui-form');//添加form标识
                layero.find('.layui-layer-btn0').attr('lay-filter', 'fromContent').attr('lay-submit', '');//将按钮弄成能提交的
                $("#resetBtn").click();
                form.render();
            },
            yes: function(index, layero){
                form.on('submit(fromContent)',function(data){
                    $.post("/api/receivingaddress/insert",$("#addrFormData").serialize(),function(data){
                        if (data.message == 'success'){
                            parent.layer.msg('添加地址成功！', { icon: 1, shade: 0.4, time: 1000 });
                            showUserAddress();
                        }else{
                            parent.layer.msg('添加地址失败！,请重试！', { icon: 5, shade: 0.4, time: 1000 });
                        }
                        layer.close(index);
                        $("#resetBtn").click();
                    });

                })

            }
        });
    }

    // 添加该id对应的商品到收藏列表
    function addToFavorite(id){
        $.ajax({
            type:"post",
            url:"/api/guess/insertFavorite",
            data:{"goodsId": id},
            success : function(data){
                if (data.message == "success") {
                    layer.msg('收藏成功！', {
                        icon : 1,
                        time : 2000
                    });
                } else {
                    layer.msg('收藏失败！', {
                        icon : 5,
                        time : 2000
                    });
                }
                // 收藏操作后 展示登录者最近浏览的未收藏的商品猜想推送列表 按点击量倒序排列 截取前8个
                showRecentView();
                showFavorite(currentPage);
            }
        });
    }

    // 从收藏列表 移除该id对应的商品
    function removeFavorite(id) {
        layer.confirm("确定取消收藏吗？", function() {
            $.ajax({
                type : "post",
                url : "/api/guess/removeFavorite",
                data : {"goodsId": id},
                success : function(data) {
                    if (data.message == "success") {
                        layer.msg('取消收藏成功！', {
                            icon : 1,
                            time : 2000
                        });
                        showFavorite();
                    } else {
                        layer.msg('取消收藏失败！', {
                            icon : 5,
                            time : 2000
                        });
                    }
                    // 收藏操作后 展示登录者最近浏览的未收藏的商品猜想推送列表 按点击量倒序排列 截取前8个
                    showRecentView();
                    showFavorite(currentPage);
                }
            });
        })
    }

    // 根据页码 查询收藏的商品列表
    function showFavorite(curr) {
        $.ajax({
            type : "post",
            url : "/api/guess/selectFavoriteGoods",
            data: {"page": curr},
            dataType : "json",
            success : function(res) {
                var arr=res.data;
                totalCount=res.count;
                if(totalCount == 0){
                    $("#productArea").html('<h2>暂无收藏</h2>');
                }else{
                    var str = "<div style='padding-left:16px;padding-top:0px;' class='span16'><ul>";
                    for (var i = 0; i < arr.length; i++) {
                        if ((i + 1) % 4 != 0) {
                            str = str
                                + "<li><a href='/goods/detail/"
                                + arr[i].goods.id
                                + "'>"
                                + "<img style='margin-bottom:2px;margin-top:10px;' src='/images/"+arr[i].goods.img+"' /><p class='goods-title'>"
                                + arr[i].goods.name
                                + "</p>"
                                + "<p class='goods-desc'>"
                                + arr[i].goods.description
                                + "</p></a><p><span class='newprice'>"
                                + arr[i].goods.price
                                + "元</span>&nbsp;"
                                + "</p><button onclick='addToCart("
                                + arr[i].goods.id
                                + ")'  class='layui-btn layui-btn-sm'>加入购物车</button>"
                                + "<button onclick='removeFavorite("
                                + arr[i].goods.id
                                + ")' class='layui-btn layui-btn-sm'>取消收藏</button></li>";
                        } else {
                            str = str
                                + "<li class='brick4'><a href='/goods/detail/"
                                + arr[i].goods.id
                                + "'>"
                                + "<img style='margin-bottom:2px;margin-top:10px;' src='/images/"+arr[i].goods.img+"' /><p class='goods-title'>"
                                + arr[i].goods.name
                                + "</p>"
                                + "<p class='goods-desc'>"
                                + arr[i].goods.description
                                + "</p></a><p><span class='newprice'>"
                                + arr[i].goods.price
                                + "元</span>&nbsp;"
                                + "</p><button onclick='addToCart("
                                + arr[i].goods.id
                                + ")' class='layui-btn layui-btn-sm'>加入购物车</button>"
                                + "<button onclick='removeFavorite("
                                + arr[i].goods.id
                                + ")' class='layui-btn layui-btn-sm'>取消收藏</button></li>";
                        }
                    }
                    var str = str + "</ul></div>";
                    $("#productArea").html(str);
                }
            }
        });
    }

    // 展示登录者最近浏览的未收藏的商品猜想推送列表 按点击量倒序排列 截取前8个
    function showRecentView() {
        $.ajax({
            type : "post",
            url : "/api/guess/selectRecentViewGoods",
            dataType : "json",
            success : function(res) {
                var arr = res.data;
                if(arr.length  == 0 || arr == null || arr == '' || typeof(arr) == 'undefined'){
                    $("#recentViewArea").html('<h2>这里是空的</h2>');
                }else{
                    var str = "<div style='padding-left:0px;margin-left:-10px;padding-top:0px;' class='span16'><ul>";
                    for (var i = 0; i < arr.length; i++) {
                        if ((i + 1) % 4 != 0) {
                            str = str
                                + "<li><a href='/goods/detail/"
                                + arr[i].goods.id
                                + "'>"
                                + "<img style='margin-bottom:2px;margin-top:10px;' src='/images/"+arr[i].goods.img+"' /><p class='goods-title'>"
                                + arr[i].goods.name
                                + "</p>"
                                + "<p class='goods-desc'>"
                                + arr[i].goods.description
                                + "</p></a><p><span class='newprice'>"
                                + arr[i].goods.price
                                + "元</span>&nbsp;"
                                + "</p>"
                                + "<button onclick='addToFavorite("
                                + arr[i].goods.id
                                + ")' class='layui-btn layui-btn-sm layui-btn-normal' onclick='addToFavorite();'>收藏商品</button></li>";
                        } else {
                            str = str
                                + "<li class='brick4'><a href='/goods/detail/"
                                + arr[i].goods.id
                                + "'>"
                                + "<img style='margin-bottom:2px;margin-top:10px;' src='/images/"+arr[i].goods.img+"' /><p class='goods-title'>"
                                + arr[i].goods.name
                                + "</p>"
                                + "<p class='goods-desc'>"
                                + arr[i].goods.description
                                + "</p></a><p><span class='newprice'>"
                                + arr[i].goods.price
                                + "元</span>&nbsp;"
                                + "</p>"
                                + "<button onclick='addToFavorite("
                                + arr[i].goods.id
                                + ")' class='layui-btn layui-btn-sm layui-btn-normal' onclick='addToFavorite()'>收藏商品</button></li>";
                        }
                    }
                    var str = str + "</ul></div>";
                    $("#recentViewArea").html(str);
                }
            }
        });
    }

    // 添加该id代表的商品到购物车
    function addToCart(id) {
        var num = 1;
        $.ajax({
            type : "post",
            url : "/api/carts/insert",
            data : {"goodsId": id, "num": num},
            success : function(data) {
                if (data.message == "success") {
                    layer.msg('添加成功！', {
                        icon : 1,
                        time : 2000
                    });
                    showHotGoods();
                } else {
                    layer.msg('添加失败！', {
                        icon : 5,
                        time : 2000
                    });
                }
            }
        });
    }

    // 展示待付款的订单列表
    function showReadyPayOrder() {
        $.ajax({
            type : "post",
            url : "/api/orders/selectReadyToPay",
            dataType : "json",
            success : function(res) {
                var data = res.data;
                str = "";
                if (data == null ||data=="" || data.length == 0) {
                    str = str + "<h2>暂无相关订单信息</h2>"
                } else {
                    str = str+ "<div class='layui-collapse' lay-accordion=''>";
                    for (var i = 0; i < data.length; i++) {
                        var date=new Date(data[i].day).format('yyyy-MM-dd');
                        str = str
                            + "<div class='layui-colla-item'>"
                            + "<h2 class='layui-colla-title'>订单编号："
                            + data[i].id
                            + "&nbsp;&nbsp;总价："
                            + data[i].price
                            + "元&nbsp;订单日期："+date+"</h2>"
                            + "<div class='layui-colla-content'><h4>收货人："
                            + data[i].username
                            + "&nbsp;&nbsp;收货地址："
                            + data[i].address
                            + "&nbsp;电话："+data[i].phone+"</h4>"
                            + "<div class='layui-form'><table class='layui-table'><thead><tr><th>商品名称</th><th>价格</th><th>数量</th><th>小计</th></tr>"
                            + "</thead><tbody>";
                        var arr = data[i].detailList;
                        for (var t = 0; t < arr.length; t++) {
                            str = str
                                + "<tr><td><a href='/goods/detail/"
                                + arr[t].goods.id
                                + "'>"
                                + arr[t].goods.name
                                + "</a></td>" + "<td>"
                                + arr[t].goods.price
                                + "</td><td>"
                                + arr[t].num
                                + "台</td><td>"
                                + arr[t].goods.price
                                * arr[t].num + "元</td>"
                                + "</tr>";
                        }
                        str = str
                            + "</tbody></table></div><button onclick='payfor(\""+data[i].id+"\")' class='layui-btn layui-btn-normal layui-btn-fluid layui-btn-radius'>去付款</button>"
                            + "</div></div>";
                    }
                    str = str + "</div>";
                }

                $("#state1").html(str);
            }
        });
    }

    // 对该id代表的订单 付款
    function payfor(id){
        $.ajax({
            type: "POST",
            url: "/order/pay",
            data: {"id": id},
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
    }

    // 展示已发货待收货的订单列表
    function findReadyToDeliverOrder() {
        $.ajax({
            type : "post",
            url : "/api/orders/selectReadyToDeliver",
            dataType : "json",
            success : function(res) {
                var data = res.data;
                str = "";
                if (data == null ||data=="" || data.length == 0) {
                    str = str + "<h2>暂无相关订单信息</h2>"
                } else {
                    str = str+ "<div class='layui-collapse' lay-accordion=''>";
                    for (var i = 0; i < data.length; i++) {
                        var date=new Date(data[i].day).format('yyyy-MM-dd');
                        str = str
                            + "<div class='layui-colla-item'>"
                            + "<h2 class='layui-colla-title'>订单编号："
                            + data[i].id
                            + "&nbsp;&nbsp;总价："
                            + data[i].price
                            + "元&nbsp;订单日期："+date+"</h2>"
                            + "<div class='layui-colla-content'><h4>收货人："
                            + data[i].username
                            + "&nbsp;&nbsp;收货地址："
                            + data[i].address
                            + "&nbsp;电话："+data[i].phone+"</h4>"
                            + "<div class='layui-form'><table class='layui-table'><thead><tr><th>商品名称</th><th>价格</th><th>数量</th><th>小计</th></tr>"
                            + "</thead><tbody>";
                        var arr = data[i].detailList;
                        for (var t = 0; t < arr.length; t++) {
                            str = str
                                + "<tr><td><a href='/goods/detail/"
                                + arr[t].goods.id
                                + "'>"
                                + arr[t].goods.name
                                + "</a></td>" + "<td>"
                                + arr[t].goods.price
                                + "</td><td>"
                                + arr[t].num
                                + "台</td><td>"
                                + arr[t].goods.price
                                * arr[t].num + "元</td>"
                                + "</tr>";
                        }
                        str = str
                            + "</tbody></table></div>"
                            + "</div></div>";
                    }
                    str = str + "</div>";
                }
                $("#state2").html(str);
            }
        });
    }

    // 显示已收货的订单列表
    function findReadyToReceiveOrder() {
        $.ajax({
            type : "post",
            url : "/api/orders/selectReadyToReceive",
            dataType : "json",
            success : function(res) {
                var data = res.data;
                str = "";
                if (data == null ||data=="" || data.length == 0) {
                    str = str + "<h2>暂无相关订单信息</h2>"
                } else {
                    str = str+ "<div class='layui-collapse' lay-accordion=''>";
                    for (var i = 0; i < data.length; i++) {
                        var date=new Date(data[i].day).format('yyyy-MM-dd');
                        str = str
                            + "<div class='layui-colla-item'>"
                            + "<h2 class='layui-colla-title'>订单编号："
                            + data[i].id
                            + "&nbsp;&nbsp;总价："
                            + data[i].price
                            + "元&nbsp;订单日期："+date+"</h2>"
                            + "<div class='layui-colla-content'><h4>收货人："
                            + data[i].username
                            + "&nbsp;&nbsp;收货地址："
                            + data[i].address
                            + "&nbsp;电话："+data[i].phone+"&nbsp;快递单号"+data[i].expressNo+"</h4>"
                            + "<div class='layui-form'><table class='layui-table'><thead><tr><th>商品名称</th><th>价格</th><th>数量</th><th>小计</th></tr>"
                            + "</thead><tbody>";
                        var arr = data[i].detailList;
                        for (var t = 0; t < arr.length; t++) {
                            str = str
                                + "<tr><td><a href='/goods/detail/"
                                + arr[t].goods.id
                                + "'>"
                                + arr[t].goods.name
                                + "</a></td>" + "<td>"
                                + arr[t].goods.price
                                + "</td><td>"
                                + arr[t].num
                                + "台</td><td>"
                                + arr[t].goods.price
                                * arr[t].num + "元</td>"
                                + "</tr>";
                        }
                        str = str
                            + "</tbody></table></div><button onclick='confirmReceive(\""+data[i].id+"\")' class='layui-btn layui-btn-normal layui-btn-fluid layui-btn-radius'>确认收货</button>"
                            + "</div></div>";
                    }
                    str = str + "</div>";
                }
                $("#state3").html(str);
            }
        });
    }

    // 确认订单收货
    function confirmReceive(id){
        layer.confirm("确认收货吗？",function(){
            $.ajax({
                type:"post",
                url:"/api/orders/receive",
                data:{"id": id},
                success:function(data){
                    if(data.message=="success"){
                        layer.msg('确认收货成功！', {
                            icon : 1,
                            time : 2000
                        });
                    }else{
                        layer.msg('确认收货失败！请重试！', {
                            icon : 5,
                            time : 2000
                        });
                    }
                    findReadyToReceiveOrder();
                    findReadyToEvaluateOrder();
                }
            });
        });
    }

    // 展示待评价的订单列表
    function findReadyToEvaluateOrder() {
        $.ajax({
            type : "post",
            url : "/api/orders/selectReadyToEvaluate",
            dataType : "json",
            success : function(res) {
                var data = res.data;
                str = "";
                if (data == null ||data=="" || data.length == 0) {
                    str = str + "<h2>暂无相关订单信息</h2>"
                } else {
                    str = str+ "<div class='layui-collapse' lay-accordion=''>";
                    for (var i = 0; i < data.length; i++) {
                        var date=new Date(data[i].day).format('yyyy-MM-dd');
                        str = str
                            + "<div class='layui-colla-item'>"
                            + "<h2 class='layui-colla-title'>订单编号："
                            + data[i].id
                            + "&nbsp;&nbsp;总价："
                            + data[i].price
                            + "元&nbsp;订单日期："+date+"</h2>"
                            + "<div class='layui-colla-content'><h4>收货人："
                            + data[i].username
                            + "&nbsp;&nbsp;收货地址："
                            + data[i].address
                            + "&nbsp;电话："+data[i].phone+"&nbsp;快递单号"+data[i].expressNo+"</h4>"
                            + "<div class='layui-form'><table class='layui-table'><thead><tr><th>商品名称</th><th>价格</th><th>数量</th><th>小计</th></tr>"
                            + "</thead><tbody>";
                        var arr = data[i].detailList;
                        for (var t = 0; t < arr.length; t++) {
                            str = str
                                + "<tr><td><a href='/goods/detail/"
                                + arr[t].goods.id
                                + "'>"
                                + arr[t].goods.name
                                + "</a></td>" + "<td>"
                                + arr[t].goods.price
                                + "</td><td>"
                                + arr[t].num
                                + "台</td><td>"
                                + arr[t].goods.price
                                * arr[t].num + "元</td>"
                                + "</tr>";
                        }
                        str = str
                            + "</tbody></table></div><button onclick='publishEva(\""+data[i].id+"\")'  class='layui-btn layui-btn-normal layui-btn-fluid layui-btn-radius'>发表评价</button>"
                            + "</div></div>";
                    }
                    str = str + "</div>";
                }
                $("#state4").html(str);
            }
        });
    }

    // 显示评价完成 已完成的订单列表
    function findFinishOrder() {
        $.ajax({
            type : "post",
            url : "/api/orders/selectFinished",
            dataType : "json",
            success : function(res) {
                var data = res.data;
                var str = "";
                if (data == null ||data=="" || data.length == 0) {
                    str = str + "<h2>暂无相关订单信息</h2>"
                } else {
                    str = str+ "<div class='layui-collapse' lay-accordion=''>";
                    for (var i = 0; i < data.length; i++) {
                        var date=new Date(data[i].day).format('yyyy-MM-dd');
                        str = str
                            + "<div class='layui-colla-item'>"
                            + "<h2 class='layui-colla-title'>订单编号："
                            + data[i].id
                            + "&nbsp;&nbsp;总价："
                            + data[i].price
                            + "元&nbsp;订单日期："+date+"</h2>"
                            + "<div class='layui-colla-content'><h4>收货人："
                            + data[i].username
                            + "&nbsp;&nbsp;收货地址："
                            + data[i].address
                            + "&nbsp;电话："+data[i].phone+"&nbsp;快递单号"+data[i].expressNo+"</h4>"
                            + "<div class='layui-form'><table class='layui-table'><thead><tr><th>商品名称</th><th>价格</th><th>数量</th><th>小计</th></tr>"
                            + "</thead><tbody>";
                        var arr = data[i].detailList;
                        for (var t = 0; t < arr.length; t++) {
                            str = str
                                + "<tr><td><a href='/goods/detail/"
                                + arr[t].goods.id
                                + "'>"
                                + arr[t].goods.name
                                + "</a></td>" + "<td>"
                                + arr[t].goods.price
                                + "</td><td>"
                                + arr[t].num
                                + "台</td><td>"
                                + arr[t].goods.price
                                * arr[t].num + "元</td>"
                                + "</tr>";
                        }
                        str = str
                            + "</tbody></table></div><button onclick='deleteOrder(\""+data[i].id+"\")' class='layui-btn layui-btn-normal layui-btn-fluid layui-btn-radius'>删除订单</button>"
                            + "</div></div>";
                    }
                    str = str + "</div>";
                }
                $("#state5").html(str);
            }
        });
    }

    // 根据订单主键id 删除订单
    function deleteOrder(id){
        layer.confirm("确认删除该订单吗？",function(){
            $.ajax({
                type:"post",
                url:"/api/orders/deleteLogic",
                data:{"id": id},
                success:function(data){
                    if(data.message=="success"){
                        layer.msg("删除订单成功！",{icon:1 , time:2000 });
                    }else{
                        layer.msg("删除订单失败！请重试！",{icon: 5, time: 2000});
                    }
                    findFinishOrder();
                }
            });
        });
    }

    // 展示所有可选的省份
    function showProvince(){
        $.ajax({
            type:"post",
            url:"/api/receivingaddress/selectAllProvince",
            dataType:"json",
            success:function(res){
                var data = res.data;
                var str="<option value=''>请选择省</option>";
                for(var i=0;i<data.length;i++){
                    str=str+"<option value='"+data[i].id+"'>"+data[i].name+"</option>"
                }
                $("#proData").html(str);
            }
        });
    }

    // 退出登录
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
            },
            error: function () {
                layer.msg('退出失败！', {icon: 5, shade: 0.4, time: 2000});
                window.location.href = "/error.jsp";
            },
            dataType: "json"
        });
    });
</script>
</body>
</html>
