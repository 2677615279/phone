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
</head>
<body>
<form class="layui-form">
    <div class="layui-form-item" style="margin:15px;height:30px;">
        <div style="height:40px;width:70px;float:left;line-height:40px;">订单编号:</div>
        <input type="text" class="layui-input" style="width:250px;float:left;" id="orderIdKeyword" name="orderIdKeyword" value="" lay-verify="" placeholder="请输入快递单号" autocomplete="off">
        <div style="height:40px;width:80px;float:left;line-height:40px;">&nbsp;&nbsp;起始日期:</div>
        <input type="text" id="startDate" name="startDate"  class="layui-input" style="float:left;width:100px;">
        <div style="height:40px;width:80px;float:left;line-height:40px;">&nbsp;&nbsp;结束日期:</div>
        <input type="text" id="endDate" name="endDate"  class="layui-input" style="float:left;width:100px;">
        <div style="height:40px;width:70px;float:left;line-height:40px;">&nbsp;&nbsp;订单状态:</div>
        <div class="layui-input-inline" style="width:150px;">
            <select name="orderStatus" id="orderStatus">
                <option value="">请选择订单状态</option>
                <option value="1">待付款</option>
                <option value="2">待发货</option>
                <option value="3">待收货</option>
                <option value="4">待评价</option>
                <option value="5">完成</option>
            </select>
        </div>
        <div class="layui-input-inline" style="width:80px;">
            <a class="layui-btn" id="search" data-type="reload" name="search">
                <i class="layui-icon"></i>搜索
            </a>
        </div>
        <div class="layui-input-inline" style="width:80px;">
            <button class="layui-btn layui-btn-normal" id="reset"  name="reset" type="reset">
                <i class="layui-icon">&#xe669;</i>重置
            </button>
        </div>
        <div style="float:right;width:110px;height:35px;">
            <div class="layui-input-inline" style="width:100px;">
                <a class="layui-btn layui-btn-normal" id="export" name="export">
                    <i class="layui-icon"></i>导出
                </a>
            </div>
        </div>
    </div>
</form>
<div>
    <table id="orders" lay-filter="orders"></table>
</div>
<div id="formData" style="display:none;width:750px;padding-top:10px;">
    <form id="orderForm" class="layui-form">
        <div class="layui-form-item">
            <label class="layui-form-label">编号</label>
            <div class="layui-input-block">
                <input type="text" name="id" id="orderId" style='background-color:#F8F8F8;' readonly="readonly" required lay-verify="required" value="" class="layui-input" />
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">总价</label>
            <div class="layui-input-block">
                <input type="text" name="price" id="orderPrice" required lay-verify="required" value="" class="layui-input" />
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">地址</label>
            <div class="layui-input-block">
                <input type="text" name="address" id="orderAddress" required lay-verify="required" value="" class="layui-input" />
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">收货人</label>
            <div class="layui-input-block">
                <input type="text" name="username" id="orderUserName" required lay-verify="required" value="" class="layui-input" />
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">手机号</label>
            <div class="layui-input-block">
                <input type="text" name="phone" id="orderPhone" required lay-verify="required" value="" class="layui-input" />
            </div>
        </div>
    </form>
</div>
<script type="text/html" id="bar">
    <a class="layui-btn layui-btn-xs" title="查看" lay-event="show">查看</a>
    <a class="layui-btn layui-btn-xs layui-btn-normal" title="编辑" lay-event="edit">编辑</a>
    <a class="layui-btn layui-btn-xs layui-btn-danger" title="刪除" lay-event="delete">刪除</a>
</script>
<script type="text/html" id="dateTpl">
    {{ layui.laytpl.fn(d.editdate) }}
</script>
<script type="text/javascript">
    var table;
    var layer;
    var form;
    var upload;
    var laydate;
    var element;
    var pageNum;
    var tableIns;
    layui.use(['layer', 'table','form','upload','laydate','element'], function ()
    {
        table = layui.table;
        layer = layui.layer;
        form =layui.form;
        upload=layui.upload;
        laydate=layui.laydate;
        element=layui.element;
        layui.laytpl.fn = function (value) {
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

        // 渲染开始日期
        var start=laydate.render({
            elem: '#startDate', //指定元素
            type: 'date',
            max: 'date',
            btns:['clear','confirm'],
            done:function(value,date){
                endMax=end.config.max;
                end.config.min=date;
                end.config.min.month=date.month-1;
            }
        });

        // 渲染结束日期
        var end=laydate.render({
            elem: '#endDate', //指定元素
            type:'date',
            max:'date',
            done:function(value,date){
                if($.trim(value)==''){
                    var curDate=new Date();
                    date={'date':curDate.getDate(),'month':curDate.getMonth()+1,'year':curDate.getFullYear()};
                }
                start.config.max=date;
                start.config.max.month=date.month-1;
            }
        });


        var keyword=$("#orderIdKeyword").val();
        var startDate=$("#startDate").val();
        var endDate=$("#endDate").val();
        var status=$("#orderStatus").val();

        // 异步请求 渲染表格
        tableIns = table.render({
            elem: '#orders'
            , id: 'orders'
            , url: '/api/orders/list'
            , method: 'post'
            , loading: false
            ,width:1140
            , cols: [[
                { field: 'id', title: '订单编号', width: 300, align: 'center' }
                ,{ field: 'user', title: '下单用户', width: 120, align: 'center',templet:function(d){
                        return d.user.username
                    }
                }
                , { field: 'day', title: '下单日期', width: 120, align: 'center' ,templet: function (d) {
                        return new Date(d.day).format('yyyy-MM-dd');
                    }
                }
                ,{ field: 'price', title: '订单总价', width: 120, align: 'center' }
                ,{field:'status', title: '订单状态', width: 102, align: 'center',templet: function(d){
                        if(d.status==1){
                            return "待付款";
                        }else if(d.status==2){
                            return "待发货";
                        }else if(d.status==3){
                            return "待收货";
                        }else if(d.status==4){
                            return "待评价";
                        }else{
                            return "完成";
                        }
                    }
                }
                , {field:'expressNo', title: '快递单号', width: 180, align: 'center',templet: function(d){
                        if(d.status==1){
                            return "等待付款";
                        }else if(d.status==2){
                            return "<button onclick='deliverOrder(\""+d.id+"\")' class='layui-btn layui-btn-sm layui-btn-fluid layui-btn-warm'>发货</button>";
                        }else if(d.status==3){
                            return d.expressNo;
                        }else if(d.status==4){
                            return d.expressNo;
                        }else{
                            return d.expressNo;
                        }
                    }
                }
                , {title: '操作', fixed: 'right', width: 190, align: 'center', toolbar: '#bar'}
            ]]
            , page: true
            , limits: [5, 10, 15]
            , limit: 10 //默认采用10
            , where: {"orderIdKeyword": keyword, "startDate":startDate, "endDate":endDate, "orderStatus":status}
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

        // 搜索
        $("#search").click(function ()
        {
            var keyword=$("#orderIdKeyword").val();
            var start=$("#startDate").val();
            var end=$("#endDate").val();
            var status=$("#orderStatus").val();
            tableIns.reload({
                where: {
                    orderIdKeyword: keyword,
                    startDate:start,
                    endDate:end,
                    orderStatus:status
                },page: {
                    curr: 1 //重新从第 1 页开始
                }
            });
        });


        // 导出
        $("#export").click(function () {
            layer.confirm('是否导出订单数据？', {
                btn: ['确认', '取消'] //可以无限个按钮
                , btn1: function (index, layero) {
                    $.ajax({
                        type: "POST",
                        url: "/api/orders/export",
                        success: function (data) {
                            if (data.message === 'success') {
                                parent.layer.msg('导出成功！', {icon: 1, shade: 0.4, time: 1000});
                            } else {
                                parent.layer.msg('导出失败，请重试！', {icon: 5, shade: 0.4, time: 1000});
                            }
                        }
                    });
                    layer.close(index);
                }
            });
        });


        //工具条事件监听 编辑订单 删除订单 查看订单详情
        table.on('tool(orders)', function (obj)
        { //注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
            var data = obj.data; //获得当前行数据
            var layEvent = obj.event; //获得 lay-event 对应的值
            var tr = obj.tr; //获得当前行 tr 的DOM对象
            if (layEvent === 'edit')
            { //编辑
                layer.open({
                    type: 1,
                    title: '编辑订单信息',
                    shade: 0.4,  //阴影度
                    fix: false,
                    shadeClose: true,
                    maxmin: false,
                    area: ['800px;', '400px;'],    //窗体大小（宽,高）
                    content: $('#formData'),
                    success: function (layero, index) {
                        var body = layer.getChildFrame('body', index); //得到子页面层的BODY
                        $("#orderId").val(data.id);
                        $("#orderPrice").val(data.price);
                        $("#orderUserName").val(data.username);
                        $("#orderPhone").val(data.phone);
                        $("#orderAddress").val(data.address);
                        form.render();
                        body.find('#hidValue').val(index); //将本层的窗口索引传给子页面层的hidValue中
                    },
                    btn:['修改','取消'],
                    yes: function(index, layero){
                        $.post('/api/orders/update',$('#orderForm').serialize(),function(data){
                            if (data.message == 'success') {
                                parent.layer.msg('修改成功', { icon: 1, shade: 0.4, time: 1000 });
                                // 修改成功 重新加载表格
                                var keyword=$("#orderIdKeyword").val();
                                var startDate=$("#startDate").val();
                                var endDate=$("#endDate").val();
                                var status=$("#orderStatus").val();
                                tableIns.reload({
                                    where: {
                                        orderIdKeyword: keyword,
                                        startDate:startDate,
                                        endDate:endDate,
                                        orderStatus:status
                                    },page: {
                                        curr: pageNum
                                    }
                                });
                                $("#handle_status").val('');
                            }
                            else
                            {
                                parent.layer.msg('修改失败', { icon: 5, shade: 0.4, time: 1000 });
                            }
                            layer.close(index);
                        });
                    }
                });
            }else if(layEvent === 'delete'){
                layer.confirm('是否删除该订单？', {
                    btn: ['确认', '取消'] //可以无限个按钮
                    ,btn1: function(index, layero){
                        $.ajax({
                            type: "POST",
                            url: "/api/orders/deleteLogic",
                            data: {"id": data.id},
                            success: function(data){
                                if(data.message=='success'){
                                    parent.layer.msg('删除成功', { icon: 1, shade: 0.4, time: 1000 });
                                    $(tr).remove();
                                    // 删除成功 重新加载表格
                                    var keyword=$("#orderIdKeyword").val();
                                    var startDate=$("#startDate").val();
                                    var endDate=$("#endDate").val();
                                    var status=$("#orderStatus").val();
                                    tableIns.reload({
                                        where: {
                                            orderIdKeyword: keyword,
                                            startDate:startDate,
                                            endDate:endDate,
                                            orderStatus:status
                                        },page: {
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
            }else if(layEvent ==='show'){
                var str="";
                $.ajax({
                    type : "post",
                    url : "/api/orders/selectById",
                    async: false,
                    data:{"id": data.id},
                    dataType : "json",
                    success : function(res) {
                        var data = res.data;
                        str = str+ "<div class='layui-collapse' lay-accordion=''>";
                        var date=new Date(data.day).toLocaleString();
                        str = str
                            + "<div class='layui-colla-item'>"
                            + "<h2 class='layui-colla-title'>订单编号："
                            + data.id
                            + "&nbsp;&nbsp;总价："
                            + data.price
                            + "元&nbsp;订单日期："+date+"</h2>"
                            + "<div class='layui-colla-content layui-show'><h4>收货人："
                            + data.username
                            + "&nbsp;&nbsp;收货地址："
                            + data.address
                            + "&nbsp;电话："+data.phone+"</h4>"
                            + "<div class='layui-form'><table class='layui-table'><thead><tr><th>商品名称</th><th>价格</th><th>数量</th><th>小计</th></tr>"
                            + "</thead><tbody>";
                        var arr = data.detailList;
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
                        str = str + "</div>";
                    }
                });
                layer.open({
                    type: 1,
                    title: '查看订单详情',
                    shade: 0.4,  //阴影度
                    fix: false,
                    shadeClose: true,
                    maxmin: false,
                    area: ['800px;', '400px;'],    //窗体大小（宽,高）
                    content: str,
                    success: function (layero, index) {
                        var body = layer.getChildFrame('body', index); //得到子页面层的BODY
                        element.render();
                        body.find('#hidValue').val(index); //将本层的窗口索引传给子页面层的hidValue中
                    },
                    btn:['关闭'],
                    yes: function(index, layero){
                        layer.close(index);
                    }
                });
            }
        });
    });

    // 根据订单主键id  发货
    function deliverOrder(id){
        layer.open({
            type: 1,
            title: '发货',
            shade: 0.4,  //阴影度
            fix: false,
            shadeClose: true,
            maxmin: false,
            area: ['600px;', '300px;'],    //窗体大小（宽,高）
            content: "<div style='width:500px;padding-top:15px;'><div class='layui-form-item'><label class='layui-form-label'>订单编号</label>"+
                "<div class='layui-input-block'><input type='text' name='id' id='orderId' style='background-color:#F8F8F8;' "+
                " readonly='readonly' required lay-verify='required' value='"+id+"' class='layui-input' /></div></div>"+
                "<div class='layui-form-item'><label class='layui-form-label'>快递单号</label><div class='layui-input-block'><input type='text' "+
                " id='expressNo' required lay-verify='required' class='layui-input' /></div></div></div>",
            success: function (layero, index) {
                var body = layer.getChildFrame('body', index); //得到子页面层的BODY
                body.find('#hidValue').val(index); //将本层的窗口索引传给子页面层的hidValue中
            },
            btn:['发货','取消'],
            yes: function(index, layero){
                var no=$("#expressNo").val();
                $.post('/api/orders/deliver',{"id":id,"expressNo":no},function(data){
                    if (data.message == 'success') {
                        parent.layer.msg('发货成功', { icon: 1, shade: 0.4, time: 1000 });
                        // 发货成功 重新加载表格
                        var keyword=$("#orderIdKeyword").val();
                        var startDate=$("#startDate").val();
                        var endDate=$("#endDate").val();
                        var status=$("#orderStatus").val();
                        tableIns.reload({
                            where: {
                                orderIdKeyword: keyword,
                                startDate:startDate,
                                endDate:endDate,
                                orderStatus:status
                            },page: {
                                curr: pageNum
                            }
                        });
                        $("#handle_status").val('');
                    }
                    else {
                        parent.layer.msg('发货失败', { icon: 5, shade: 0.4, time: 1000 });
                    }
                    layer.close(index);
                });
            }
        });
    }
</script>
</body>
</html>
