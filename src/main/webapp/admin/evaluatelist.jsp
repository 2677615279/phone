<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>layui</title>
    <link href="/css/layui.css" rel="stylesheet">
    <link href="/css/bootstrap.css" rel="stylesheet">
    <link href="/css/iconfont.css" rel="stylesheet">
    <script src="/js/jquery.1.12.4.min.js" type="text/javascript"></script>
    <script src="/js/bootstrap.js" type="text/javascript"></script>
    <script src="/js/layui.js" type="text/javascript"></script>
    <style>
        label.layui-form-label{
            padding-top:0px;
        }
    </style>
</head>
<body>
<form class="layui-form">
    <div class="layui-form-item" style="margin:15px;height:30px;">
        <div style="height:40px;width:80px;float:left;line-height:40px;">评价关键词:</div>
        <input type="text" class="layui-input" style="width:250px;float:left;" id="keyword" name="keyword" value="" lay-verify="" placeholder="请输入关键词" autocomplete="off">
        <div class="layui-input-inline" style="width:150px;text-align:center;">
            <a class="layui-btn" id="search" data-type="reload" name="search">
                <i class="layui-icon"></i>搜索
            </a>
        </div>
        <div style="float:right;width:110px;height:35px;">
            <div class="layui-input-inline" style="width:100px;">
                <a class="layui-btn layui-btn-normal" id="delete" name="delete">
                    <i class="layui-icon">&#x1006;</i>批量删除
                </a>
            </div>
        </div>
    </div>
</form>
<div>
    <table id="eva" lay-filter="eva"></table>
</div>
<div id="formData" style="width:700px;display:none;padding-top:15px;">
    <form class="layui-form" id="evaForm">
        <div class="layui-form-item">
            <label class="layui-form-label">评价编号:</label>
            <div class="layui-input-block">
                <p name="evaId" id="evaId"></p>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">评价内容:</label>
            <div class="layui-input-block">
                <p name="evaContent" id="evaContent"></p>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">评价图片:</label>
            <div class="layui-input-block" id="evaImg">

            </div>
        </div>
    </form>
</div>
<script type="text/html" id="bar">
    <a class="layui-btn layui-btn-xs layui-btn-normal" title="查看" lay-event="show">查看</a>
    <a class="layui-btn layui-btn-xs" title="刪除" lay-event="delete">刪除</a>
</script>
<script type="text/html" id="dateTpl">
    {{ layui.laytpl.fn(d.editdate) }}
</script>
<script type="text/javascript">
    var table;
    var layer;
    var form;
    var laydate;
    var pageNum;
    layui.use(['layer', 'table','form','laydate'], function ()
    {
        table = layui.table;
        layer = layui.layer;
        form =layui.form;
        laydate=layui.laydate;
        layui.laytpl.fn = function (value)
        {
            //json日期格式转换为正常格式
            var date = new Date(parseInt(value.replace("/Date(", "").replace(")/", ""), 10));
            var month = date.getMonth() + 1 < 10 ? "0" + (date.getMonth() + 1) : date.getMonth() + 1;
            var day = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
            return date.getFullYear() + "-" + month + "-" + day;
        };

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

        var keyword = $("#keyword").val();
        // 异步请求 渲染表格
        var tableIns = table.render({
            elem: '#eva'
            , id: 'eva'
            , url: '/api/evaluates/list'
            , method: 'post'
            , loading: false
            ,width:1140
            , cols: [[
                { checkbox: true, LAY_CHECKED: false } //其它参数在此省略
                ,{ field: 'id', title: '评价编号', width: 100, align: 'center' }
                , { field: 'user', title: '评价人', width: 120, align: 'center',templet:function(d){
                        return d.user.username;
                    }
                }
                , { field: 'content', title: '评价内容', width: 303, align: 'center' }
                ,{field:'day', title: '评价时间', width: 150, align: 'center',templet: function(d){
                        return new Date(d.day).format("yyyy-MM-dd");
                    }
                }
                ,{ field:'goods', title: '评价商品', width: 150, align: 'center',templet: function(d){
                        return d.goods.name;
                    }
                },{ field: 'level', title: '评价星级', width: 100, align: 'center' }
                ,{title: '操作', fixed: 'right', width: 160, align: 'center', toolbar: '#bar'}
            ]]
            , page: true
            , limits: [5, 10, 15]
            , limit: 10 //默认采用10
            , where: {"search": keyword}
            , done: function (res, curr, count)
            {
                //如果是异步请求数据方式，res即为你接口返回的信息。
                //如果是直接赋值的方式，res即为：{data: [], count: 99} data为当前页数据、count为数据总长度
                //console.log(res);
                //得到当前页码
                //console.log(curr);
                layer.closeAll('loading');
                $("#curPageIndex").val(curr);
                pageNum = curr;
                //得到数据总量
                //console.log(count);
            }
        });

        // 批量删除
        $("#delete").click(function ()
        {
            var checkStatus = table.checkStatus('eva');
            var count = checkStatus.data.length;//选中的行数
            if (count > 0)
            {
                parent.layer.confirm('确定要删除所选评论？', { icon: 3 }, function (index)
                {
                    var data = checkStatus.data; //获取选中行的数据
                    var batchId = '';
                    for (var i = 0; i < data.length; i++)
                    {
                        batchId += data[i].id + ",";
                    }
                    $.ajax({
                        url: '/api/evaluates/batchDelete',
                        type: "post",
                        data: {'batchId': batchId},
                        success: function (result){
                            if (result.message=="success"){
                                parent.layer.msg('删除成功', { icon: 1, shade: 0.4, time: 1000 });
                                // 删除成功后 再次异步请求 渲染表格
                                var word = $("#keyword").val();
                                tableIns.reload({
                                    where: {
                                        search: word
                                    }, page: {
                                        curr: pageNum
                                    }
                                });
                            }else{
                                parent.layer.msg("删除失败", { icon: 5, shade: [0.4], time: 1000 });
                            }
                            parent.layer.close(index);
                        }
                    })
                });
            }
            else
                parent.layer.msg("请至少选择一条数据", { icon: 5, shade: 0.4, time: 1000 });
        });

        // 搜索
        $("#search").click(function ()
        {
            var word=$("#keyword").val();
            tableIns.reload({
                where: {
                    search: word
                },page: {
                    curr: 1 //重新从第 1 页开始
                }
            });
        });

        // 工具条事件监听 查看评价 删除评价
        table.on('tool(eva)', function (obj)
        { //注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
            var data = obj.data; //获得当前行数据
            var layEvent = obj.event; //获得 lay-event 对应的值
            var tr = obj.tr; //获得当前行 tr 的DOM对象
            if (layEvent === 'show')
            { //编辑
                layer.open({
                    type: 1,
                    title: '查看评价详情',
                    shade: 0.4,  //阴影度
                    fix: false,
                    shadeClose: true,
                    maxmin: false,
                    area: ['800px;', '400px;'],    //窗体大小（宽,高）
                    content: $('#formData'),
                    success: function (layero, index)
                    {
                        $.ajax({
                            type:"post",
                            url:"/api/evaluates/selectById",
                            data:{"id": data.id},
                            dataType:"json",
                            success:function(res){
                                var data = res.data;
                                $("#evaId").html(data.id);
                                $("#evaContent").html(data.content);
                                var arr = data.imgList;
                                if(arr == null || arr == '' || arr.length == 0){
                                    $("#evaImg").html("暂无评价图片");
                                }else{
                                    var str="";
                                    for(var i=0;i<arr.length;i++){
                                        str=str+"<img style='width:100px;height:100px;' src='/uploads/evaluates_imgs/"+arr[i].name+"'  />";
                                    }
                                    $("#evaImg").html(str);
                                }
                            }
                        });
                        var body = layer.getChildFrame('body', index); //得到子页面层的BODY
                        form.render();
                        body.find('#hidValue').val(index); //将本层的窗口索引传给子页面层的hidValue中
                    },
                    btn:['关闭'],
                    yes: function(index, layero){
                        layer.close(index);
                        $("#evaImg").html("");
                    }
                });
            }else if(layEvent === 'delete'){
                layer.confirm('是否删除该评价？', {
                    btn: ['确认', '取消'] //可以无限个按钮
                    ,btn1: function(index, layero){
                        $.ajax({
                            type: "POST",
                            url: "/api/evaluates/deleteLogic",
                            data: {"id": data.id},
                            success: function(data){
                                if(data.message=='success'){
                                    parent.layer.msg('删除成功', { icon: 1, shade: 0.4, time: 1000 });
                                    $(tr).remove();
                                    // 删除成功后 再次渲染表格
                                    var word = $("#keyword").val();
                                    tableIns.reload({
                                        where: {
                                            search: word
                                        }, page: {
                                            curr: pageNum
                                        }
                                    });
                                }else{
                                    parent.layer.msg('删除失败', { icon: 5, shade: 0.4, time: 1000 });
                                }
                            }
                        });
                        layer.close(index);
                    }
                });
            }
        });
    });
</script>
</body>
</html>
