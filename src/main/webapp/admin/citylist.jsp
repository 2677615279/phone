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
        <div style="height:40px;width:70px;float:left;line-height:40px;">城市名称:</div>
        <input type="text" class="layui-input" style="display:inline-block;width:250px;float:left;" id="keyword"
               name="keyword" value="" lay-verify="" placeholder="请输入城市名称" autocomplete="off">
        <div class="layui-input-inline" style="width:150px;text-align:center;">
            <a class="layui-btn" id="search" data-type="reload" name="search">
                <i class="layui-icon"></i>搜索
            </a>
        </div>
        <div style="float:left;width:380px;height:40px;">
            <div class="layui-input-inline" style="width:100px;margin-right:20px;">
                <a class="layui-btn layui-btn-normal" id="add" name="add">
                    <i class="layui-icon">&#xe654;</i>添加城市
                </a>
            </div>
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
    <table id="type" lay-filter="type"></table>
</div>
<div id="formData" style="width:350px;display:none;padding-top:15px;">
    <form class="layui-form" id="cityForm">
        <input type="hidden" name="id" id="showCityId"/>

        <div class="layui-form-item">
            <label class="layui-form-label">省份</label>
            <div class="layui-input-block">
                <select name="provinceId" id="chooseUpdate" lay-verify="required">
                </select>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">城市</label>
            <div class="layui-input-block">
                <input type="text" name="name" id="showCityName" required lay-verify="required" value=""
                       class="layui-input"/>
            </div>
        </div>
    </form>
</div>

<div id="addData" style="width:350px;display:none;padding-top:15px;">
    <form class="layui-form" id="addCityForm">
        <div class="layui-form-item">
            <label class="layui-form-label">省份</label>
            <div class="layui-input-block">
                <select name="provinceId" id="chooseInsert" lay-verify="required">
                </select>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">城市</label>
            <div class="layui-input-block">
                <input type="text" id="addTypeName" name="name" required lay-verify="required" value=""
                       class="layui-input"/>
            </div>
        </div>
    </form>
</div>

<script type="text/html" id="bar">
    <a class="layui-btn layui-btn-xs layui-btn-normal" title="编辑" lay-event="edit">编辑</a>
    <a class="layui-btn layui-btn-xs" title="刪除" lay-event="delete">刪除</a>
</script>
<script type="text/html" id="dateTpl">
    {{ layui.laytpl.fn(d.editdate) }}
</script>
<script type="text/javascript">
    var table;
    var layer;
    var form;
    var pageNum;
    var search;

    layui.use(['layer', 'table', 'form'], function () {
        table = layui.table;
        layer = layui.layer;
        form = layui.form;

        var keyword = $("#keyword").val();
        // 异步请求 渲染表格
        var tableIns = table.render({
            elem: '#type'
            , id: 'type'
            , url: '/api/cities/list'
            , method: 'post'
            , loading: false
            , width: 1147
            , cols: [[
                {checkbox: true, LAY_CHECKED: false} //其它参数在此省略
                , {field: 'id', title: '城市编号', width: 120, align: 'center'}
                , {field: 'name', title: '城市名称', width: 350, align: 'center'}
                , {field: 'province', title: '所属省份', width: 350, templet: function (data) {return data.province.name} , align: 'center'}
                , {
                    field: 'status', title: '城市状态', width: 112, align: 'center', templet: function (data) {
                        if (data.status === 0) {
                            return "<input type='checkbox' lay-filter='switchTest' name='" + data.id + "' lay-skin='switch' lay-text='禁用|正常'>";
                        } else {
                            return "<input type='checkbox' lay-filter='switchTest' name='" + data.id + "' lay-skin='switch' lay-text='禁用|正常' checked>";
                        }
                    }
                }
                , {title: '操作', fixed: 'right', width: 160, align: 'center', toolbar: '#bar'}
            ]]
            , page: true
            , limits: [5, 10, 15]
            , limit: 10 //默认采用10
            , where: {"search": keyword}
            , done: function (res, curr, count) {
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

        // 点击添加城市 按钮  触发事件
        $("#add").click(function () {
            layer.open({
                type: 1,
                title: '添加城市',
                shade: 0.4,  //阴影度
                fix: false,
                shadeClose: true,
                maxmin: false,
                area: ['400px;', '550px;'],    //窗体大小（宽,高）
                content: $('#addData'),
                success: function (layero, index) {
                    $("#chooseInsert").html("");
                    $.post("/api/cities/beforeInsert", function (data) {
                        var list = data.data;
                        var str = '';
                        for (var i = 0; i < list.length; i++) {
                            str += '<option value="'+list[i].id+'">'+list[i].name+'</option>';
                        }
                        $("#chooseInsert").html(str);
                        form.render();
                    },"json");
                    var body = layer.getChildFrame('body', index); //得到子页面层的BODY
                    layero.addClass('layui-form');//添加form标识
                    layero.find('.layui-layer-btn0').attr('lay-filter', 'fromContent').attr('lay-submit', '');//将按钮弄成能提交的
                    form.render();
                    body.find('#hidValue').val(index); //将本层的窗口索引传给子页面层的hidValue中
                },
                btn: ['添加', '取消'],
                yes: function (index, layero) {
                    form.on('submit(fromContent)', function (data) {
                        $.post('/api/cities/insert', $('#addCityForm').serialize(), function (res) {
                            if (res.message === "success") {
                                parent.layer.msg('添加城市成功！', {icon: 1, shade: 0.4, time: 1000});
                                // 添加成功后 再次异步请求 渲染表格
                                var word = $("#keyword").val();
                                tableIns.reload({
                                    where: {
                                        search: word
                                    }, page: {
                                        curr: pageNum
                                    }
                                });
                                $("#addCityForm")[0].reset();
                                layui.form.render();
                                layer.close(index);
                            } else {
                                parent.layer.msg("添加城市失败，请重试！", {icon: 5, shade: 0.4, time: 1000});
                            }
                        });
                    })
                }
            });
        });

        // 搜索成功后 再次渲染表格
        $("#search").click(function () {
            var word = $("#keyword").val();
            tableIns.reload({
                where: {
                    search: word
                }, page: {
                    curr: 1 //重新从第 1 页开始
                }
            });
        });

        // 批量删除
        $("#delete").click(function () {
            var checkStatus = table.checkStatus('type');
            var count = checkStatus.data.length;//选中的行数
            if (count > 0) {
                parent.layer.confirm('确定要删除所选城市？', {icon: 3}, function (index) {
                    var data = checkStatus.data; //获取选中行的数据
                    var batchId = '';
                    for (var i = 0; i < data.length; i++) {
                        batchId += data[i].id + ",";
                    }
                    $.ajax({
                        url: '/api/cities/batchDelete',
                        type: "post",
                        data: {'batchId': batchId},
                        success: function (data) {
                            if (data.message === "success") {
                                parent.layer.msg('删除成功！', {icon: 1, shade: 0.4, time: 1000});
                                // 批量删除成功后 再次异步请求 渲染表格
                                var word = $("#keyword").val();
                                tableIns.reload({
                                    where: {
                                        search: word
                                    }, page: {
                                        curr: pageNum
                                    }
                                });
                            } else {
                                parent.layer.msg("删除失败，请重试！", {icon: 5, shade: [0.4], time: 1000});
                            }
                            parent.layer.close(index);
                        }
                    })
                });
            } else
                parent.layer.msg("请至少选择一条数据！", {icon: 5, shade: 0.4, time: 1000});
        });

        // 禁用和解禁
        form.on('switch(switchTest)', function (data) {
            var id = data.elem.name;
            var flag = data.elem.checked;
            if (flag) {
                $.ajax({
                    type: "post",
                    url: "/api/cities/changeStatus",
                    data: {"id": id, "status": 2},
                    success: function (data) {
                        if (data.message === "success") {
                            layer.msg('禁用城市成功！', {icon: 1, time: 2000});
                        } else {
                            layer.msg('禁用城市失败，请重试！', {icon: 5, time: 2000});
                        }
                    }
                });
            } else {
                $.ajax({
                    type: "post",
                    url: "/api/cities/changeStatus",
                    data: {"id": id, "status": 0},
                    success: function (data) {
                        if (data.message === "success") {
                            layer.msg("解禁城市成功！", {icon: 1, time: 2000});
                        } else {
                            layer.msg("解禁城市失败，请重试！", {icon: 5, time: 2000});
                        }
                    }
                });
            }
        });

        // 工具条事件监听  更新城市、删除城市
        table.on('tool(type)', function (obj) { //注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
            var data = obj.data; //获得当前行数据
            var layEvent = obj.event; //获得 lay-event 对应的值
            var tr = obj.tr; //获得当前行 tr 的DOM对象
            if (layEvent === 'edit') { //编辑
                layer.open({
                    type: 1,
                    title: '编辑城市信息',
                    shade: 0.4,  //阴影度
                    fix: false,
                    shadeClose: true,
                    maxmin: false,
                    area: ['400px;', '550px;'],    //窗体大小（宽,高）
                    content: $('#formData'),
                    success: function (layero, index) {
                        $("#chooseUpdate").html("");
                        $.post("/api/cities/beforeUpdate", {"id": data.id}, function (data) {
                            var list = data.data;
                            var provinceId = data.message;
                            var str = '<option value="'+provinceId+'">保持原省份</option>';
                            for (var i = 0; i < list.length; i++) {
                                str += '<option value="'+list[i].id+'">'+list[i].name+'</option>';
                            }
                            $("#chooseUpdate").html(str);
                            form.render();
                        },"json");
                        var body = layer.getChildFrame('body', index); //得到子页面层的BODY
                        layero.addClass('layui-form');//添加form标识
                        layero.find('.layui-layer-btn0').attr('lay-filter', 'editFormContent').attr('lay-submit', '');//将按钮弄成能提交的
                        form.render();
                        $("#showCityId").val(data.id);
                        $("#showCityName").val(data.name);
                        body.find('#hidValue').val(index); //将本层的窗口索引传给子页面层的hidValue中
                    },
                    btn: ['修改', '取消'],
                    yes: function (index, layero) {
                        form.on('submit(editFormContent)', function (data) {
                            $.post('/api/cities/update', $('#cityForm').serialize(), function (res) {
                                if (res.message === "success") {
                                    parent.layer.msg('编辑城市成功！', {icon: 1, shade: 0.4, time: 1000});
                                    // 编辑成功后 再次异步请求 渲染表格
                                    var word = $("#keyword").val();
                                    tableIns.reload({
                                        where: {
                                            search: word
                                        }, page: {
                                            curr: pageNum
                                        }
                                    });
                                    $("#cityForm")[0].reset();
                                    layui.form.render();
                                    layer.close(index);
                                } else {
                                    parent.layer.msg('编辑城市失败，请重试！', {icon: 5, shade: 0.4, time: 1000});
                                }
                            });
                        })
                    }
                });
            }
            else if (layEvent === 'delete') {
                layer.confirm('是否删除该城市？', {
                    btn: ['确认', '取消'] //可以无限个按钮
                    , btn1: function (index, layero) {
                        $.ajax({
                            type: "POST",
                            url: "/api/cities/deleteLogic",
                            data: {"id": data.id},
                            success: function (res) {
                                if (res.message === "success") {
                                    parent.layer.msg('删除成功！', {icon: 1, shade: 0.4, time: 1000});
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
                                } else {
                                    parent.layer.msg('删除失败，请重试！', {icon: 5, shade: 0.4, time: 1000});
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