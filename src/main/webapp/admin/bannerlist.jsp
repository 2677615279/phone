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
        <div style="height:40px;width:70px;float:left;line-height:40px;">banner图:</div>
        <input type="text" class="layui-input" style="display:inline-block;width:250px;float:left;" id="keyword"
               name="keyword" value="" lay-verify="" placeholder="请输入banner图名称" autocomplete="off">
        <div class="layui-input-inline" style="width:150px;text-align:center;">
            <a class="layui-btn" id="search" data-type="reload" name="search">
                <i class="layui-icon"></i>搜索
            </a>
        </div>
        <div style="float:left;width:380px;height:40px;">
            <div class="layui-input-inline" style="width:100px;margin-right:20px;">
                <a class="layui-btn layui-btn-normal" id="add" name="add">
                    <i class="layui-icon">&#xe654;</i>添加banner图
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
    <form class="layui-form" id="bannerForm">
        <input type="hidden" name="id" id="showBannerId"/>
        <div class="layui-form-item">
            <label class="layui-form-label">名称</label>
            <div class="layui-input-block">
                <input type="text" name="name" id="showBannerName" required lay-verify="required" value=""
                       class="layui-input"/>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">url</label>
            <div class="layui-input-block">
                <input type="text" name="url" id="showBannerUrl" required lay-verify="required" value=""
                       class="layui-input"/>
            </div>
        </div>
        <div class="layui-form-item" id="img">
            <label class="layui-form-label"></label>
            <img style="width:400px;height:100px;" src="" id="bannerImg">
            <button type="button" class="layui-btn layui-btn-warm" id="uploadBannerImg" style="margin-left:100px;margin-top: 20px">
                <i class="layui-icon">&#xe67c;</i>选择图片
            </button>
        </div>
        <div class="layui-form-item" style="color: darkred; text-align: center">
            双击预览的图片，可删除所选，恢复原图
        </div>
    </form>
</div>

<div id="addData" style="width:350px;display:none;padding-top:15px;">
    <form class="layui-form" id="addBannerForm">
        <div class="layui-form-item">
            <label class="layui-form-label">名称</label>
            <div class="layui-input-block">
                <input type="text" id="addTypeName" name="name" required lay-verify="required" value=""
                       class="layui-input"/>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">url</label>
            <div class="layui-input-block">
                <input type="text" id="addTypeUrl" name="url" required lay-verify="required" value=""
                       class="layui-input"/>
            </div>
        </div>
        <div class="layui-form-item" id="addimg">
            <button type="button" class="layui-btn" id="test1" style="margin-left:100px;margin-top: 20px">
                <i class="layui-icon">&#xe67c;</i>上传图片
            </button>
        </div>
        <div class="layui-form-item" style="color: darkred; text-align: center">
            双击预览的图片，可删除所选
        </div>
        <img alt="选择图片"  id="previewImg" style="width: 400px;height: 100px;display: none">
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
    var upload;
    var element;
    var img;

    layui.use(['layer', 'table', 'form','upload','element'], function () {
        table = layui.table;
        layer = layui.layer;
        form = layui.form;
        upload = layui.upload;
        element = layui.element;
        var files;
        //清空文件队列
        function clearFile(){
            for (var i in files) {
                delete files[i];
            }
        }

        var keyword = $("#keyword").val();
        // 异步请求 渲染表格
        var tableIns = table.render({
            elem: '#type'
            , id: 'type'
            , url: '/api/banners/list'
            , method: 'post'
            , loading: false
            , width: 1486
            , cols: [[
                {checkbox: true, LAY_CHECKED: false} //其它参数在此省略
                , {field: 'id', title: 'banner图编号', width: 120, align: 'center'}
                , {field: 'name', title: 'banner图名称', width: 350, align: 'center'}
                , {field: 'url', title: 'banner图url', width: 350, align: 'center'}
                , {field: 'img', title: 'banner图', width: 250, align: 'center',templet: function(d){
                        return "<img src='/images/" + d.img + "' style='width:220px;height:50px;' />";
                    }}
                , {
                    field: 'status', title: 'banner图状态', width: 200, align: 'center', templet: function (data) {
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

        // 点击添加banner图 按钮  触发事件
        $("#add").click(function () {
            layer.open({
                type: 1,
                title: '添加banner图',
                shade: 0.4,  //阴影度
                fix: false,
                shadeClose: true,
                maxmin: false,
                area: ['400px;', '500px;'],    //窗体大小（宽,高）
                content: $('#addData'),
                success: function (layero, index) {
                    var uploadInst = upload.render({
                        elem: '#test1' //绑定元素
                        ,url: '/api/files/uploadBanner' //上传接口
                        ,multiple: false // 不允许多文件上传
                        ,method: 'post'
                        ,auto: false //选择文件后不自动上传
                        ,bindAction: '.layui-layer-btn0' //绑定表单提交的按钮 触发上传请求
                        ,field: 'multipartFile'
                        ,accept: 'images'//校验上传文件类型
                        ,headers: {access_token: localStorage.access_token}
                        ,acceptMime: "image/*"
                        ,choose: function(obj){
                            files = obj.pushFile(); //将每次选择的文件追加到文件队列
                            clearFile(); //删除所有选择过的文件
                            layer.confirm('确定导入文件吗？', {icon: 5, title:'提示'}, function(index){
                                //预读本地文件示例，不支持ie8
                                obj.preview(function(index, file, result){
                                    $('#previewImg').attr('src', result); //图片链接（base64）
                                    $("#previewImg").css("display","block");
                                    obj.pushFile(); // 再把当前文件重新加入队列
                                });
                                layer.close(index);
                            });
                            //双击手工选择的图片   删除之前选择的图片
                            $("#previewImg").bind('dblclick', function () {
                                clearFile();
                                $("#previewImg").removeAttr("src");
                                $("#previewImg").css("display","none");
                            });

                        }
                        ,before: function () {
                            // 阻止文件上传，layui官方文档 before:方法内return false即可，实测无效
                            if ($("#addTypeName").val().length < 1 || $("#addTypeName").val().length > 255) {
                                layer.msg("banner图名称不合规范，上传失败！请重试！",{icon:5,anim:6,time:2000});
                                // TODO 终止上传请求 F12报错layer.stopPropagation is not a function  但是对后续页面其他操作无影响
                                layer.stopPropagation(); // 阻止默认事件、事件冒泡
                                return false;
                            }
                            else if ($("#addTypeUrl").val().length < 1 || $("#addTypeUrl").val().length > 255) {
                                layer.msg("banner图url不合规范，注册失败！请重试！",{icon:5,anim:6,time:2000});
                                layer.stopPropagation(); // 阻止默认事件、事件冒泡
                                return false;
                            }
                            else {
                                return true;
                            }
                        }
                        ,done: function(res){
                            if (res.code == 200) {
                                layer.msg('图片上传成功，请稍等！', {
                                    icon : 1,
                                    time : 2000
                                });
                                clearFile();

                                // 如果没有图片，因为上传绑定了添加按钮，点击添加时，则会触发文件上传，此时不走form.on中的代码
                                // 上传完毕，将图片结果写入表单中一个新控件，重新渲染，走下面的代码，校验合格后，发送ajax请求执行添加
                                $("#addimg").append('<input type="hidden" value="'+res.data+'" name="img" >');
                                form.render(); //更新全部
                                // 这里需要使用new FormData()来获取表单中的控件
                                var formData = new FormData(document.getElementById("addBannerForm"));
                                form.render(); //更新全部

                                if (formData.get("name").length < 1 || formData.get("name").length > 255) {
                                    layer.msg("banner图名称不合规范，添加失败！请重试！",{icon:5,anim:6,time:2000});
                                    return false;
                                }
                                if (formData.get("url").length < 1 || formData.get("url").length > 255) {
                                    layer.msg("banner图url不合规范，添加失败！请重试！",{icon:5,anim:6,time:2000});
                                    return false;
                                }

                                $.ajax({
                                    url: "/api/banners/insert",
                                    type: "POST",
                                    data: formData,
                                    processData: false, // 禁止序列化data，默认为true
                                    contentType: false, // 避免jquery对contentType做操作
                                    success: function (data) {
                                        if(data.message === "success"){
                                            layer.msg("添加成功！",{icon:1,anim:4,time:2000},function(){
                                                layer.close(index);
                                                var word = $("#keyword").val();
                                                tableIns.reload({
                                                    where: {
                                                        search: word
                                                    }, page: {
                                                        curr: pageNum
                                                    }
                                                });
                                            });
                                        }else{
                                            layer.msg("添加失败！请重试！",{icon:5,anim:6,time:2000});
                                        }
                                    },
                                    error: function () {
                                        top.location.href = "/error.jsp"
                                    },
                                    dataType: "json"
                                });

                            }
                            else {
                                layer.msg('图片上传失败，添加失败！请重试！', {
                                    icon : 5,
                                    time : 2000
                                });
                                clearFile();
                            }
                        }
                        ,error: function(){
                            layer.msg('图片上传失败，添加失败！请重试！', {
                                icon : 5,
                                time : 2000
                            });
                        }
                    });

                    var body = layer.getChildFrame('body', index); //得到子页面层的BODY
                    layero.addClass('layui-form');//添加form标识
                    layero.find('.layui-layer-btn0').attr('lay-filter', 'fromContent').attr('lay-submit', '');//将按钮弄成能提交的
                    form.render();
                    body.find('#hidValue').val(index); //将本层的窗口索引传给子页面层的hidValue中
                },
                btn: ['添加', '取消'],
                yes: function (index, layero) {
                    form.on('submit(fromContent)', function (data) {
                        if ($("#previewImg").attr("src") === undefined) {
                            layer.msg("你未完成必选项，添加banner图失败，请重试！", {icon: 5, shade: 0.4, time: 1000});
                            form.init;
                        }
                    });
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
                parent.layer.confirm('确定要删除所选banner图？', {icon: 3}, function (index) {
                    var data = checkStatus.data; //获取选中行的数据
                    var batchId = '';
                    for (var i = 0; i < data.length; i++) {
                        batchId += data[i].id + ",";
                    }
                    $.ajax({
                        url: '/api/banners/batchDelete',
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
                    url: "/api/banners/changeStatus",
                    data: {"id": id, "status": 2},
                    success: function (data) {
                        if (data.message === "success") {
                            layer.msg('禁用banner图成功！', {icon: 1, time: 2000});
                        } else {
                            layer.msg('禁用banner图失败，请重试！', {icon: 5, time: 2000});
                        }
                    }
                });
            } else {
                $.ajax({
                    type: "post",
                    url: "/api/banners/changeStatus",
                    data: {"id": id, "status": 0},
                    success: function (data) {
                        if (data.message === "success") {
                            layer.msg("解禁banner图成功！", {icon: 1, time: 2000});
                        } else {
                            layer.msg("解禁banner图失败，请重试！", {icon: 5, time: 2000});
                        }
                    }
                });
            }
        });

        // 工具条事件监听  更新banner图、删除banner图
        table.on('tool(type)', function (obj) { //注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
            var data = obj.data; //获得当前行数据
            var layEvent = obj.event; //获得 lay-event 对应的值
            var tr = obj.tr; //获得当前行 tr 的DOM对象
            if (layEvent === 'edit') { //编辑
                layer.open({
                    type: 1,
                    title: '编辑banner图信息',
                    shade: 0.4,  //阴影度
                    fix: false,
                    shadeClose: true,
                    maxmin: false,
                    area: ['400px;', '500px;'],    //窗体大小（宽,高）
                    content: $('#formData'),
                    success: function (layero, index) {
                        var uploadInst = upload.render({
                            elem: '#uploadBannerImg' //绑定元素
                            ,url: '/api/files/uploadBanner' //上传接口
                            ,multiple: false // 不允许多文件上传
                            ,method: 'post'
                            ,auto: false //选择文件后不自动上传
                            ,bindAction: '.layui-layer-btn0' //绑定表单提交的按钮 触发上传请求
                            ,field: 'multipartFile'
                            ,accept: 'images'//校验上传文件类型
                            ,headers: {access_token: localStorage.access_token}
                            ,acceptMime: "image/*"
                            ,choose: function(obj){
                                files = obj.pushFile(); //将每次选择的文件追加到文件队列
                                clearFile(); //删除所有选择过的文件
                                layer.confirm('确定导入文件吗？', {icon: 5, title:'提示'}, function(index){
                                    //预读本地文件示例，不支持ie8
                                    obj.preview(function(index, file, result){
                                        $('#bannerImg').attr('src', result); //图片链接（base64）
                                        obj.pushFile(); // 再把当前文件重新加入队列
                                    });
                                    layer.close(index);
                                });
                                //双击手工选择的图片   删除之前选择的图片
                                $("#bannerImg").bind('dblclick', function () {
                                    clearFile();
                                    $("#bannerImg").removeAttr("src");
                                    $('#bannerImg').attr('src', '/images/' + img);
                                });
                            }
                            ,before: function () {
                                // 阻止文件上传，layui官方文档 before:方法内return false即可，实测无效
                                if ($("#showBannerName").val().length < 1 || $("#showBannerName").val().length > 255) {
                                    layer.msg("banner图名称不合规范，上传失败！请重试！",{icon:5,anim:6,time:2000});
                                    layer.stopPropagation(); // 阻止默认事件、事件冒泡
                                    return false;
                                }
                                else if ($("#showBannerUrl").val().length < 1 || $("#showBannerUrl").val().length > 255) {
                                    layer.msg("banner图url不合规范，注册失败！请重试！",{icon:5,anim:6,time:2000});
                                    layer.stopPropagation(); // 阻止默认事件、事件冒泡
                                    return false;
                                }
                                else {
                                    return true;
                                }
                            }
                            ,done: function(res){
                                if (res.code == 200) {
                                    layer.msg('图片上传成功，请稍等！', {
                                        icon : 1,
                                        time : 2000
                                    });
                                    clearFile();
                                    $("#img").append("<input type='hidden' name='img' value='" + res.data + "' />");
                                    form.render(); //更新全部
                                    // 这里需要使用new FormData()来获取表单中的控件
                                    var formData = new FormData(document.getElementById("bannerForm"));
                                    form.render(); //更新全部

                                    if (formData.get("name").length < 1 || formData.get("name").length > 255) {
                                        layer.msg("banner图名称不合规范，修改失败！请重试！",{icon:5,anim:6,time:2000});
                                        return false;
                                    }
                                    if (formData.get("url").length < 1 || formData.get("url").length > 255) {
                                        layer.msg("banner图url不合规范，修改失败！请重试！",{icon:5,anim:6,time:2000});
                                        return false;
                                    }

                                    $.ajax({
                                        url: "/api/banners/update",
                                        type: "POST",
                                        data: formData,
                                        processData: false, // 禁止序列化data，默认为true
                                        contentType: false, // 避免jquery对contentType做操作
                                        success: function (data) {
                                            if(data.message === "success"){
                                                layer.msg("更新成功！",{icon:1,anim:4,time:2000},function(){
                                                    layer.close(index);
                                                    var word = $("#keyword").val();
                                                    tableIns.reload({
                                                        where: {
                                                            search: word
                                                        }, page: {
                                                            curr: pageNum
                                                        }
                                                    });
                                                });
                                            }else{
                                                layer.msg("修改失败！请重试！",{icon:5,anim:6,time:2000});
                                            }
                                        },
                                        error: function () {
                                            top.location.href = "/error.jsp"
                                        },
                                        dataType: "json"
                                    });

                                }
                                else {
                                    layer.msg('图片上传失败，添加失败！请重试！', {
                                        icon : 5,
                                        time : 2000
                                    });
                                    clearFile();
                                }
                            }
                            ,error: function(){
                                layer.msg('图片上传失败，添加失败！请重试！', {
                                    icon : 5,
                                    time : 2000
                                });
                            }
                        });

                        var body = layer.getChildFrame('body', index); //得到子页面层的BODY
                        layero.addClass('layui-form');//添加form标识
                        layero.find('.layui-layer-btn0').attr('lay-filter', 'editFormContent').attr('lay-submit', '');//将按钮弄成能提交的
                        $("#showBannerId").val(data.id);
                        $("#showBannerName").val(data.name);
                        $("#showBannerUrl").val(data.url);
                        $("#bannerImg").attr("src", "/images/" + data.img);
                        img = data.img;
                        form.render();
                        body.find('#hidValue').val(index); //将本层的窗口索引传给子页面层的hidValue中
                    },
                    btn: ['修改', '取消'],
                    yes: function (index, layero) {
                        if ($("#bannerImg").attr("src") === '/images/' + data.img) {
                            form.render(); //更新全部
                            // 这里需要使用new FormData()来获取表单中的控件
                            var formData = new FormData(document.getElementById("bannerForm"));
                            form.render(); //更新全部
                            if (formData.get("name").length < 1 || formData.get("name").length > 255) {
                                layer.msg("banner图名称不合规范，修改失败！请重试！",{icon:5,anim:6,time:2000});
                                return false;
                            }
                            if (formData.get("url").length < 1 || formData.get("url").length > 255) {
                                layer.msg("banner图url不合规范，修改失败！请重试！",{icon:5,anim:6,time:2000});
                                return false;
                            }
                            $.ajax({
                                url: "/api/banners/update",
                                type: "POST",
                                data: formData,
                                processData: false, // 禁止序列化data，默认为true
                                contentType: false, // 避免jquery对contentType做操作
                                success: function (data) {
                                    if(data.message === "success"){
                                        parent.layer.msg('修改banner图成功！', {icon: 1, shade: 0.4, time: 1000}, function () {
                                            // 修改成功后 再次渲染表格
                                            var word = $("#keyword").val();
                                            tableIns.reload({
                                                where: {
                                                    search: word
                                                }, page: {
                                                    curr: pageNum
                                                }
                                            });
                                            $("#handle_status").val('');
                                            $("#bannerForm")[0].reset();
                                            layui.form.render();
                                            layer.close(index);
                                        });
                                    }else{
                                        parent.layer.msg('修改banner图失败，请重试！', {icon: 5, shade: 0.4, time: 1000});
                                        layer.close(index);
                                    }
                                },
                                error: function () {
                                    parent.layer.msg('修改banner图失败，请重试！', {icon: 5, shade: 0.4, time: 1000});
                                    layer.close(index);
                                },
                                dataType: "json"
                            });

                        }

                    }
                });
            }
            else if (layEvent === 'delete') {
                layer.confirm('是否删除该banner图？', {
                    btn: ['确认', '取消'] //可以无限个按钮
                    , btn1: function (index, layero) {
                        $.ajax({
                            type: "POST",
                            url: "/api/banners/deleteLogic",
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