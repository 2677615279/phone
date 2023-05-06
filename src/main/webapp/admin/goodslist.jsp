<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>layui</title>
    <link href="/css/layui.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
    <link href="/css/bootstrap.css" rel="stylesheet">
    <link href="/css/iconfont.css" rel="stylesheet">
    <script src="/js/jquery.1.12.4.min.js" type="text/javascript"></script>
    <script src="/js/bootstrap.js" type="text/javascript"></script>
    <script src="/js/layui.js" type="text/javascript"></script>
</head>
<body>

<div class="layui-form-item" style="margin:15px 15px;height:30px;">
    <div class="layui-input-inline">
        <input type="text" id="filter" name="filter" value="" lay-verify="" placeholder="请输入名称" autocomplete="off" class="layui-input">
    </div>
    <div class="layui-input-inline">
        <button class="layui-btn" id="search" data-type="reload" name="search">
            <i class="layui-icon"></i>搜索
        </button>
    </div>
    <div style="float:right;width:110px;height:35px;">
        <div class="layui-input-inline" style="width:100px;">
            <button class="layui-btn layui-btn-normal" id="delete" name="delete">
                <i class="layui-icon">&#x1006;</i>批量删除
            </button>
        </div>
    </div>
</div>
<div>
    <table id="goods" lay-filter="goods"></table>
</div>
<div id="editForm" style="display:none;width:800px;padding-top:10px;">
    <form id="formData" class="layui-form">
        <input type="hidden" name="id" id="goodsId" >
        <div class="layui-form-item">
            <label class="layui-form-label">名称</label>
            <div class="layui-input-block">
                <input type="text" name="name" id="goodsName" required lay-verify="required" placeholder="请输入商品名称" value="" class="layui-input" />
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">价格</label>
            <div class="layui-input-block">
                <input type="text" name="price" id="goodsPrice" required lay-verify="required" placeholder="请输入商品价格" value="" class="layui-input" />
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">库存</label>
            <div class="layui-input-block">
                <input type="text" name="num" id="goodsNum" required lay-verify="required" placeholder="请输入商品库存" value="" class="layui-input" />
            </div>
        </div>
        <div class="layui-form-item" lay-filter="test">
            <label class="layui-form-label">类别</label>
            <div class="layui-input-block">
                <select name="type" id="goodsType" required lay-verify="required" lay-filter="type">
                </select>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">内存</label>
            <div class="layui-input-block">
                <select name="memory" id="memory" required lay-verify="required" lay-filter="memory">
                </select>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">颜色</label>
            <div class="layui-input-block">
                <input type="text" name="color" id="goodsColor" required lay-verify="required"  value="" class="layui-input" />
            </div>
        </div>
        <div class="layui-form-item" id="img">
            <label class="layui-form-label">图片</label>
            <img style="width:260px;height:180px;" src="" id="goodsImg">
            <button type="button" class="layui-btn layui-btn-warm" id="uploadGoodsImg">
                <i class="layui-icon">&#xe67c;</i>选择图片
            </button>
        </div>
        <div class="layui-form-item" style="color: darkred; text-align: center">
            双击预览的图片，可删除所选，恢复原图
        </div>

        <div class="layui-form-item">
            <label class="layui-form-label">描述</label>
            <div class="layui-input-block">
                <textarea name="description" id="goodsDesc"  required lay-verify="required" class="layui-textarea"></textarea>
            </div>
        </div>
    </form>
</div>
<script type="text/html" id="bar">
    <a class="layui-btn layui-btn-xs layui-btn-normal" title="编辑" lay-event="edit" id="update">编辑</a>
    <a class="layui-btn layui-btn-xs" title="刪除" lay-event="delete">刪除</a>
</script>
<script type="text/html" id="dateTpl">
    {{ layui.laytpl.fn(d.editdate) }}
</script>
<script type="text/javascript">
    var table;
    var layer;
    var form;
    var upload;
    var element;
    var pageNum;
    var img;

    layui.use(['layer', 'table','form','upload','element'], function ()
    {
        table = layui.table;
        layer = layui.layer;
        form =layui.form;
        upload=layui.upload;
        element = layui.element;

        var files;
        //清空文件队列
        function clearFile(){
            for (var i in files) {
                delete files[i];
            }
        }

        layui.laytpl.fn = function (value)
        {
            //json日期格式转换为正常格式
            var date = new Date(parseInt(value.replace("/Date(", "").replace(")/", ""), 10));
            var month = date.getMonth() + 1 < 10 ? "0" + (date.getMonth() + 1) : date.getMonth() + 1;
            var day = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
            return date.getFullYear() + "-" + month + "-" + day;
        };

        $.ajax({
                type: "POST",
                url: "/api/goodstypes/selectAll",
                success: function(data){
                    var arr = data.data;
                    var str="";
                    for(var i=0;i<arr.length;i++){
                        str=str+"<option value='"+arr[i].id+"'>"+arr[i].name+"</option>";
                    }
                    $("#goodsType").html(str);
                    form.render();
                }
            });
        $.ajax({
                type: "POST",
                url: "/api/memory/selectAll",
                success: function(data){
                    var arr = data.data;
                    var str="";
                    for(var i=0;i<arr.length;i++){
                        str=str+"<option value='"+arr[i].id+"'>"+arr[i].name+"</option>";
                    }
                    $("#memory").html(str);
                    form.render();
                }
            });

        var keyword = $("#filter").val();
        // 异步请求 渲染表格
        var tableIns = table.render({
            elem: '#goods'
            , id: 'goods'
            , method: 'post'
            , loading: false
            , url: '/api/goods/list'
            , cols: [[
                { checkbox: true, LAY_CHECKED: false } //其它参数在此省略
                , { field: 'id', title: '编号', width: 80, align: 'center' }
                , { field: 'name', title: '商品名称', width: 140, align: 'center' }
                , { field: 'price', title: '价格', width: 80,sort:true, align: 'center' }
                , { field: 'num', title: '库存', width: 80,sort:true, align: 'center' }
                , { field: 'color', title: '颜色', width: 80, align: 'center' }
                ,{field:'type', title: '分类', width: 100 ,align: 'center',templet: function(d){
                        return d.type.name
                    }
                }
                ,{field:'memory', title: '内存', width: 80 ,align: 'center',templet: function(d){
                        return d.memory.name
                    }
                }
                ,{field:'img', title: '商品图片', width: 120 ,align: 'center',templet: function(d){
                        return '<img src="/images/'+d.img+'" />'
                    }
                }
                , { field: 'description', title: '商品描述', width: 160, align: 'center' }
                , {title: '操作', fixed: 'right', width: 160, align: 'center', toolbar: '#bar' }
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

        // 搜索
        $("#search").click(function () {
            var strFilter = $("#filter").val();
            tableIns.reload({
                where: {
                    search: strFilter
                },page: {
                    curr: 1 //重新从第 1 页开始
                }
            });
        });


        // 批量删除
        $("#delete").click(function ()
        {
            var checkStatus = table.checkStatus('goods');
            var count = checkStatus.data.length;//选中的行数
            if (count > 0)
            {
                parent.layer.confirm('确定要删除所选商品？', { icon: 3 }, function (index)
                {
                    var data = checkStatus.data; //获取选中行的数据
                    var batchId = '';
                    for (var i = 0; i < data.length; i++)
                    {
                        batchId += data[i].id + ",";
                    }
                    $.ajax({
                        url: '/api/goods/batchDelete',
                        type: "post",
                        data: { 'batchId': batchId },
                        success: function (result){
                            if (result.message==="success"){
                                parent.layer.msg('删除成功', { icon: 1, shade: 0.4, time: 1000 });
                                // 更新成功 重新渲染
                                var strFilter = $("#filter").val();
                                tableIns.reload({
                                    where: {
                                        search: strFilter
                                    },page: {
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
        //#endregion


        //工具条事件监听 更新商品 删除商品
        table.on('tool(goods)', function (obj)
        { //注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
            var data = obj.data; //获得当前行数据
            var layEvent = obj.event; //获得 lay-event 对应的值
            var tr = obj.tr; //获得当前行 tr 的DOM对象
            var goodsId = data.id;
            if (layEvent === 'edit')
            { //编辑
                layer.open({
                    type: 1,
                    title: '编辑商品信息',
                    shade: 0.4,  //阴影度
                    fix: false,
                    shadeClose: true,
                    maxmin: false,
                    area: ['900px;', '800px;'],    //窗体大小（宽,高）
                    content: $('#editForm'),
                    success: function (layero, index) {


                        var uploadInst = upload.render({
                            elem: '#uploadGoodsImg' //绑定元素
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
                                        $('#goodsImg').attr('src', result); //图片链接（base64）
                                        obj.pushFile(); // 再把当前文件重新加入队列
                                    });
                                    layer.close(index);
                                });
                                //双击手工选择的图片   删除之前选择的图片
                                $("#goodsImg").bind('dblclick', function () {
                                    clearFile();
                                    $("#goodsImg").removeAttr("src");
                                    $('#goodsImg').attr('src', '/images/' + img);
                                });
                            }
                            ,before: function () {
                                // 阻止文件上传，layui官方文档 before:方法内return false即可，实测无效
                                if ($("#goodsName").val().length < 2 || $("#goodsName").val().length > 20) {
                                    layer.msg("商品名称不合规范，添加失败！请重试！",{icon:5,anim:6,time:2000});
                                    // TODO 终止上传请求 F12报错layer.stopPropagation is not a function  但是对后续页面其他操作无影响
                                    layer.stopPropagation(); // 阻止默认事件、事件冒泡
                                    return false;
                                }
                                else if ($("#goodsPrice").val().length < 1) {
                                    layer.msg("商品价格不合规范，添加失败！请重试！",{icon:5,anim:6,time:2000});
                                    layer.stopPropagation(); // 阻止默认事件、事件冒泡
                                    return false;
                                }
                                else if ($("#goodsNum").val().length < 1) {
                                    layer.msg("商品库存不合规范，添加失败！请重试！",{icon:5,anim:6,time:2000});
                                    layer.stopPropagation(); // 阻止默认事件、事件冒泡
                                    return false;
                                }
                                else if ($("#goodsType").val() === "") {
                                    layer.msg("商品类别不合规范，添加失败！请重试！",{icon:5,anim:6,time:2000});
                                    layer.stopPropagation(); // 阻止默认事件、事件冒泡
                                    return false;
                                }
                                else if ($("#memory").val() === "") {
                                    layer.msg("商品内存不合规范，添加失败！请重试！",{icon:5,anim:6,time:2000});
                                    layer.stopPropagation(); // 阻止默认事件、事件冒泡
                                    return false;
                                }
                                else if ($("#goodsColor").val().length < 2 || $("#goodsColor").val().length > 20) {
                                    layer.msg("商品颜色不合规范，添加失败！请重试！",{icon:5,anim:6,time:2000});
                                    layer.stopPropagation(); // 阻止默认事件、事件冒泡
                                    return false;
                                }
                                else if ($("#goodsDesc").val().length < 1 || $("#goodsDesc").val().length > 255) {
                                    layer.msg("商品描述不合规范，添加失败！请重试！",{icon:5,anim:6,time:2000});
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
                                    var formData = new FormData(document.getElementById("formData"));
                                    form.render(); //更新全部

                                    $.ajax({
                                        url: "/api/goods/update",
                                        type: "POST",
                                        data: formData,
                                        processData: false, // 禁止序列化data，默认为true
                                        contentType: false, // 避免jquery对contentType做操作
                                        success: function (data) {
                                            if(data.message === "success"){
                                                layer.msg("更新成功！",{icon:1,anim:4,time:2000},function(){
                                                    layer.close(index);
                                                    // 更新成功 重新渲染
                                                    var strFilter = $("#filter").val();
                                                    tableIns.reload({
                                                        where: {
                                                            search: strFilter
                                                        },page: {
                                                            curr: pageNum
                                                        }
                                                    });
                                                });
                                            }else{
                                                layer.msg("修改失败，请重试！",{icon:5,anim:6,time:2000});
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
                        $("#goodsId").val(data.id);
                        $("#goodsName").val(data.name);
                        $("#goodsPrice").val(data.price);
                        $("#goodsNum").val(data.num);
                        $("#goodsColor").val(data.color);
                        $("#goodsType").val(data.type.id);
                        $("#memory").val(data.memory.id);
                        $("#goodsImg").attr("src","/images/" + data.img);
                        $("#goodsDesc").val(data.description);
                        img = data.img;
                        form.render();
                        body.find('#hidValue').val(index); //将本层的窗口索引传给子页面层的hidValue中
                    },
                    btn:['修改','取消'],
                    yes: function(index, layero){
                        if ($("#goodsImg").attr("src") === '/images/' + data.img) {
                            // 阻止文件上传，layui官方文档 before:方法内return false即可，实测无效
                            if ($("#goodsName").val().length < 2 || $("#goodsName").val().length > 20) {
                                layer.msg("商品名称不合规范，添加失败！请重试！",{icon:5,anim:6,time:2000});
                                layer.stopPropagation(); // 阻止默认事件、事件冒泡
                                return false;
                            }
                            if ($("#goodsPrice").val().length < 1) {
                                layer.msg("商品价格不合规范，添加失败！请重试！",{icon:5,anim:6,time:2000});
                                layer.stopPropagation(); // 阻止默认事件、事件冒泡
                                return false;
                            }
                            if ($("#goodsNum").val().length < 1) {
                                layer.msg("商品库存不合规范，添加失败！请重试！",{icon:5,anim:6,time:2000});
                                layer.stopPropagation(); // 阻止默认事件、事件冒泡
                                return false;
                            }
                            if ($("#goodsType").val() === "") {
                                layer.msg("商品类别不合规范，添加失败！请重试！",{icon:5,anim:6,time:2000});
                                layer.stopPropagation(); // 阻止默认事件、事件冒泡
                                return false;
                            }
                            if ($("#memory").val() === "") {
                                layer.msg("商品内存不合规范，添加失败！请重试！",{icon:5,anim:6,time:2000});
                                layer.stopPropagation(); // 阻止默认事件、事件冒泡
                                return false;
                            }
                            if ($("#goodsColor").val().length < 2 || $("#goodsColor").val().length > 20) {
                                layer.msg("商品颜色不合规范，添加失败！请重试！",{icon:5,anim:6,time:2000});
                                layer.stopPropagation(); // 阻止默认事件、事件冒泡
                                return false;
                            }
                            if ($("#goodsDesc").val().length < 1 || $("#goodsDesc").val().length > 255) {
                                layer.msg("商品描述不合规范，添加失败！请重试！",{icon:5,anim:6,time:2000});
                                layer.stopPropagation(); // 阻止默认事件、事件冒泡
                                return false;
                            }

                            $.post('/api/goods/update',$('#formData').serialize(),function(data){
                                if (data.message === 'success') {
                                    parent.layer.msg('修改成功', { icon: 1, shade: 0.4, time: 1000 });
                                    // 修改成功 重新渲染
                                    var strFilter = $("#filter").val();
                                    tableIns.reload({
                                        where: {
                                            search: strFilter
                                        },page: {
                                            curr: pageNum
                                        }
                                    });
                                    $("#handle_status").val('');
                                    $("#formData")[0].reset();
                                    layui.form.render();
                                }
                                else
                                {
                                    parent.layer.msg('修改失败', { icon: 5, shade: 0.4, time: 1000 });
                                }
                                layer.close(index);
                            });
                        }
                    }
                });
            }else if(layEvent === 'delete'){
                layer.confirm('是否删除该商品？', {
                    btn: ['确认', '取消'] //可以无限个按钮
                    ,btn1: function(index, layero){
                        $.ajax({
                            type: "POST",
                            url: "/api/goods/deleteLogic",
                            data: {"id": data.id},
                            success: function(data){
                                if(data.message==='success'){
                                    parent.layer.msg('删除成功', { icon: 1, shade: 0.4, time: 1000 });
                                    $(tr).remove();
                                    // 删除成功 重新渲染
                                    var strFilter = $("#filter").val();
                                    tableIns.reload({
                                        where: {
                                            search: strFilter
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
            }
        });
    });
</script>
</body>
</html>
