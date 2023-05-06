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
        .layui-table-cell{
            height:36px;
            line-height: 36px;
        }
    </style>
</head>
<body>
<form class="layui-form">
    <div class="layui-form-item" style="margin:15px;height:30px;">
        <div style="height:40px;width:70px;float:left;line-height:40px;">关键词：</div>
        <input type="text" class="layui-input" style="width:250px;float:left;" id="keyword" name="keyword" value=""
               lay-verify="" placeholder="请输入关键词" autocomplete="off">
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
    <table id="users" lay-filter="users"></table>
</div>
<div id="formData" style="width:700px;display:none;padding-top:15px;">
    <form class="layui-form" id="userForm">
        <input type="hidden" name="id" id="userId"/>
        <div class="layui-form-item">
            <label class="layui-form-label">用户名</label>
            <div class="layui-input-block">
                <input type="text" name="username" id="userName" required lay-verify="required" value=""
                       class="layui-input"/>
            </div>
        </div>

        <!--开始修改-->
        <div class="layui-form-item">
            <label class="layui-form-label">手机</label>
            <div class="layui-input-block">
                <input type="text" name="phone" id="userPhone" required lay-verify="required" value=""
                       class="layui-input"/>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">邮箱</label>
            <div class="layui-input-block">
                <input type="text" name="email" id="userEmail" required lay-verify="required" value=""
                       class="layui-input"/>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">生日</label>
            <div class="layui-input-block">
                <input type="date" name="birthday" id="userBirthday" value=""
                       class="layui-input"/>
            </div>
        </div>
        <!--结束修改-->

        <div class="layui-form-item" id="img">
            <label class="layui-form-label">头像</label>
            <img style="width:80px;height:80px;" src="" id="userImg">
            <button type="button" class="layui-btn layui-btn-warm" id="uploadUserImg">
                <i class="layui-icon">&#xe67c;</i>选择图片
            </button>
        </div>
        <div class="layui-form-item" style="color: darkred; text-align: center">
            双击预览的头像，可删除所选，恢复原头像
        </div>
    </form>
</div>

<div id="roleData" style="width:400px;display:none;padding-top:15px;">
    <form class="form-inline">
        <div class="form-group" style="margin-left: 27px">
            <label for="leftRoleList">未分配角色列表</label><br>
            <select class="form-control" id="leftRoleList" multiple style="overflow: auto;width: 150px;height: 350px">

            </select>
        </div>
        <div class="form-group">
            <ul id="roleBtns">
                <li id="leftToRightBtn"><i class="glyphicon glyphicon-chevron-right" style="font-size: 50px"></i></li>
                <li id="rightToLeftBtn"><i class="glyphicon glyphicon-chevron-left" style="font-size: 50px"></i></li>
            </ul>
        </div>
        <div class="form-group">
            <label for="rightRoleList">已分配角色列表</label><br>
            <select class="form-control" id="rightRoleList" multiple style="overflow: auto;width: 150px;height: 350px">

            </select>
        </div>
    </form>
</div>
<script type="text/html" id="bar">
    <a class="layui-btn layui-btn-xs layui-btn-normal" title="分配角色" lay-event="role">分配角色</a>
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
    var upload;
    var laydate;
    var pageNum;
    var search;
    var img;
    var files;

    $("#leftToRightBtn").click(function () {
        var items = $("#leftRoleList :selected");
        if (items.length === 0) {
            layer.msg("请选择要分配的角色", {time:1000, icon:6});
        }
        else {
            $("#rightRoleList").append(items.clone());
            items.remove();
        }
    });
    $("#rightToLeftBtn").click(function () {
        var items = $("#rightRoleList :selected");
        if (items.length === 0) {
            layer.msg("请选择要取消分配的角色", {time:1000, icon:6});
        }
        else {
            $("#leftRoleList").append(items.clone());
            items.remove();
        }
    });

    layui.use(['layer', 'table', 'form', 'upload', 'laydate'], function () {
        table = layui.table;
        layer = layui.layer;
        form = layui.form;
        upload = layui.upload;
        laydate = layui.laydate;
        layui.laytpl.fn = function (value) {
            //json日期格式转换为正常格式
            var date = new Date(parseInt(value.replace("/Date(", "").replace(")/", ""), 10));
            var month = date.getMonth() + 1 < 10 ? "0" + (date.getMonth() + 1) : date.getMonth() + 1;
            var day = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
            return date.getFullYear() + "-" + month + "-" + day;
        };

        //清空文件队列
        function clearFile(){
            for (var i in files) {
                delete files[i];
            }
        }


        var keyword = $("#keyword").val();
        // 异步请求 渲染表格
        var tableIns = table.render({
            elem: '#users'
            , id: 'users'
            , method: 'post'
            , url: '/api/users/list'
            , loading: false
            , width: 1220
            , cols: [[
                {checkbox: true, LAY_CHECKED: false} //其它参数在此省略
                , {field: 'id', title: '用户编号', width: 100, align: 'center'}
                , {field: 'username', title: '用户名', width: 120, align: 'cfenter'}
                , {field: 'phone', title: '用户电话', width: 150, align: 'center'}
                , {field: 'email', title: '用户邮箱', width: 251, align: 'center'}
                , {
                    field: 'img', title: '用户头像', width: 190, align: 'center', templet: function (data) {
                        var str = "<img style='width:36px;height:36px;' src='/uploads/users_imgs/" + data.img + "' />";
                        return str;
                    }
                }
                , {
                    field: 'status', title: '用户状态', width: 112, align: 'center', templet: function (data) {
                        if (data.status === 0) {
                            return "<input type='checkbox' lay-filter='switchTest' name='" + data.id + "' lay-skin='switch' lay-text='禁用|正常'>";
                        } else {
                            return "<input type='checkbox' lay-filter='switchTest' name='" + data.id + "' lay-skin='switch' lay-text='禁用|正常' checked>";
                        }
                    }
                }, {title: '操作', fixed: 'right', width: 240, align: 'center', toolbar: '#bar'}
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

        // 批量删除
        $("#delete").click(function () {
            var checkStatus = table.checkStatus('users');
            var count = checkStatus.data.length;//选中的行数
            if (count > 0) {
                parent.layer.confirm('确定要删除所选用户？', {icon: 3}, function (index) {
                    var data = checkStatus.data; //获取选中行的数据
                    var batchId = '';
                    for (var i = 0; i < data.length; i++) {
                        batchId += data[i].id + ",";
                    }
                    $.ajax({
                        url: '/api/users/batchDelete',
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
                parent.layer.msg("请至少选择一条数据", {icon: 5, shade: 0.4, time: 1000});
        });

        // 导出
        $("#export").click(function () {
            layer.confirm('是否导出用户数据？', {
                btn: ['确认', '取消'] //可以无限个按钮
                , btn1: function (index, layero) {
                    $.ajax({
                        type: "POST",
                        url: "/api/users/export",
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

        // 禁用和解禁
        form.on('switch(switchTest)', function (data) {
            var id = data.elem.name;
            var flag = data.elem.checked;
            if (flag) {
                $.ajax({
                    type: "post",
                    url: "/api/users/changeStatus",
                    data: {"id": id, "status": 2},
                    success: function (data) {
                        if (data.message === "success") {
                            layer.msg('禁用用户成功！', {icon: 1, time: 2000});
                        } else {
                            layer.msg('禁用用户失败，请重试！', {icon: 5, time: 2000});
                        }
                    }
                });
            } else {
                $.ajax({
                    type: "post",
                    url: "/api/users/changeStatus",
                    data: {"id": id, "status": 0},
                    success: function (data) {
                        if (data.message === "success") {
                            layer.msg("解禁用户成功！", {icon: 1, time: 2000});
                        } else {
                            layer.msg("解禁用户失败，请重试！", {icon: 5, time: 2000});
                        }
                    }
                });
            }
        });

        //工具条事件监听  更新用户、删除用户、分配角色
        table.on('tool(users)', function (obj) { //注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
            var data = obj.data; //获得当前行数据
            var layEvent = obj.event; //获得 lay-event 对应的值
            var tr = obj.tr; //获得当前行 tr 的DOM对象
            if (layEvent === 'edit') { //编辑
                layer.open({
                    type: 1,
                    title: '编辑用户信息',
                    shade: 0.4,  //阴影度
                    fix: false,
                    shadeClose: true,
                    maxmin: false,
                    area: ['800px;', '490px;'],    //窗体大小（宽,高）
                    content: $('#formData'),
                    success: function (layero, index) {
                        var uploadInst = upload.render({
                            elem: '#uploadUserImg' //绑定元素
                            , url: '/api/files/upload' //上传接口
                            , method: "post"
                            , multiple: false // 不允许多文件上传
                            , auto: false //选择文件后不自动上传
                            , bindAction: ".layui-layer-btn0" //绑定表单提交的按钮 触发上传请求
                            , field: 'multipartFile'
                            , accept: 'images'//校验上传文件类型
                            , headers: {access_token: localStorage.access_token}
                            , acceptMime: "image/*"
                            ,choose: function(obj){
                                files = obj.pushFile(); //将每次选择的文件追加到文件队列
                                clearFile(); //删除所有选择过的文件
                                layer.confirm('确定导入文件吗？', {icon: 5, title:'提示'}, function(index){
                                    //预读本地文件示例，不支持ie8
                                    obj.preview(function(index, file, result){
                                        $('#userImg').attr('src', result); //图片链接（base64）
                                        obj.pushFile(); // 再把当前文件重新加入队列
                                    });
                                    clearFile();
                                    layer.close(index);
                                });
                                //双击手工选择的图片   删除之前选择的图片  恢复原图片
                                $("#userImg").bind('dblclick', function () {
                                    clearFile();
                                    $("#userImg").removeAttr("src");
                                    $("#userImg").attr('src', "/uploads/users_imgs/" + img);
                                });
                            }
                            ,before: function () {
                                // 如果没有校验通过，阻止文件上传，layui官方文档 before:方法内return false即可，实测无效
                                if ($("#userName").val().length < 3 || $("#userName").val().length > 20) {
                                    layer.msg("用户名不合规范，修改信息失败！请重试！",{icon:5,anim:6,time:2000,shade:0.4});
                                    layer.stopPropagation(); // 阻止默认事件、事件冒泡
                                    return false;
                                }
                                else if ($("#userPhone").val().indexOf("1") !== 0 || $("#userPhone").val().length !== 11) {
                                    layer.msg("手机不合规范，修改信息失败！请重试！",{icon:5,anim:6,time:2000,shade:0.4});
                                    layer.stopPropagation(); // 阻止默认事件、事件冒泡
                                    return false;
                                }
                                else if ($("#userEmail").val().indexOf("@") < 0) {
                                    layer.msg("邮箱不合规范，修改信息失败！请重试！",{icon:5,anim:6,time:2000,shade:0.4});
                                    layer.stopPropagation(); // 阻止默认事件、事件冒泡
                                    return false;
                                }
                                else {
                                    return true;
                                }
                            }
                            , done: function (res) {
                                if (res.code == 200) {
                                    layer.msg("新头像上传成功，请稍等！", {icon: 1, time: 2000});
                                    $("#img").append("<input type='hidden' name='img' value='" + res.data + "' />");
                                    clearFile();
                                    form.render(); //更新全部
                                    // 这里需要使用new FormData()来获取表单中的控件
                                    var formData = new FormData(document.getElementById("userForm"));
                                    form.render(); //更新全部
                                    if (formData.get("username").length < 3 || formData.get("username").length > 20) {
                                        layer.msg("用户名不合规范，修改信息失败！请重试！",{icon:5,anim:6,time:2000,shade:0.4});
                                        return false;
                                    }
                                    if (formData.get("phone").indexOf("1") !== 0 || formData.get("phone").length !== 11) {
                                        layer.msg("手机不合规范，修改信息失败！请重试！",{icon:5,anim:6,time:2000,shade:0.4});
                                        return false;
                                    }
                                    if (formData.get("email").indexOf("@") === -1) {
                                        layer.msg("邮箱不合规范，修改信息失败！请重试！",{icon:5,anim:6,time:2000,shade:0.4});
                                        return false;
                                    }

                                    $.ajax({
                                        url: "/api/users/update",
                                        type: "POST",
                                        data: formData,
                                        processData: false, // 禁止序列化data，默认为true
                                        contentType: false, // 避免jquery对contentType做操作
                                        success: function (data) {
                                            if(data.message === "success"){
                                                layer.msg("修改成功！",{icon:1,anim:4,time:2000,shade:0.4},function(){
                                                    layer.close(index);
                                                    // 修改成功后 再次渲染表格
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
                                                layer.msg("修改信息失败！请重试！",{icon:5,anim:6,time:2000,shade:0.4});
                                            }
                                        },
                                        error: function () {
                                            layer.msg("修改信息失败！请重试！",{icon:5,anim:6,time:2000,shade:0.4});
                                        },
                                        dataType: "json"
                                    });
                                }
                                else {
                                    layer.msg("新头像上传失败！请重试！", {icon: 5, time: 2000});
                                    clearFile();
                                }
                            }
                            , error: function () {
                                layer.msg("新头像上传失败！请重试！", {icon: 5, time: 2000});
                            }
                        });
                        var body = layer.getChildFrame('body', index); //得到子页面层的BODY
                        $("#userId").val(data.id);
                        $("#userName").val(data.username);
                        $("#userPhone").val(data.phone);
                        $("#userEmail").val(data.email);
                        $("#userImg").attr("src", "/uploads/users_imgs/" + data.img);
                        img = data.img;
                        form.render();
                        body.find('#hidValue').val(index); //将本层的窗口索引传给子页面层的hidValue中
                        clearFile();
                    },
                    btn: ['修改', '取消'],
                    yes: function (index, layero) {
                        if ($("#userImg").attr("src") === '/uploads/users_imgs/' + data.img) {
                            form.render(); //更新全部
                            // 这里需要使用new FormData()来获取表单中的控件
                            var formData = new FormData(document.getElementById("userForm"));
                            form.render(); //更新全部
                            if (formData.get("username").length < 3 || formData.get("username").length > 20) {
                                layer.msg("用户名不合规范，修改信息失败！请重试！",{icon:5,anim:6,time:2000,shade:0.4});
                                return false;
                            }
                            if (formData.get("phone").indexOf("1") !== 0 || formData.get("phone").length !== 11) {
                                layer.msg("手机不合规范，修改信息失败！请重试！",{icon:5,anim:6,time:2000,shade:0.4});
                                return false;
                            }
                            if (formData.get("email").indexOf("@") === -1) {
                                layer.msg("邮箱不合规范，修改信息失败！请重试！",{icon:5,anim:6,time:2000,shade:0.4});
                                return false;
                            }

                            $.ajax({
                                url: "/api/users/update",
                                type: "POST",
                                data: formData,
                                processData: false, // 禁止序列化data，默认为true
                                contentType: false, // 避免jquery对contentType做操作
                                success: function (data) {
                                    if(data.message === "success"){
                                        parent.layer.msg('修改用户信息成功！', {icon: 1, shade: 0.4, time: 1000}, function () {
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
                                            layer.close(index);
                                        });
                                    }else{
                                        parent.layer.msg('修改用户信息失败，请重试！', {icon: 5, shade: 0.4, time: 1000});
                                        layer.close(index);
                                    }
                                },
                                error: function () {
                                    parent.layer.msg('修改用户信息失败，请重试！', {icon: 5, shade: 0.4, time: 1000});
                                    layer.close(index);
                                },
                                dataType: "json"
                            });
                            clearFile();
                            // layer.close(index);
                        }
                    },
                    btn2: function (index, layero) {
                        layer.close(index);
                    }
                });
            }
            else if (layEvent === 'delete') {
                layer.confirm('是否删除该用户？', {
                    btn: ['确认', '取消'] //可以无限个按钮
                    , btn1: function (index, layero) {
                        $.ajax({
                            type: "POST",
                            url: "/api/users/deleteLogic",
                            data: {"id": data.id},
                            success: function (data) {
                                if (data.message === 'success') {
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
            else if (layEvent === 'role') {
                layer.open({
                    type: 1,
                    title: '分配角色',
                    shade: 0.4,  //阴影度
                    fix: false,
                    shadeClose: true,
                    maxmin: false,
                    area: ['400px;', '490px;'],    //窗体大小（宽,高）
                    content: $('#roleData'),
                    success: function (layero, index) {
                        $("#rightRoleList").html("");
                        $("#leftRoleList").html("");
                        var id = data.id;
                        $.post("/api/users/selectAssign",{"id": id},function (data) {
                            var assign = data["assign"];
                            var unassign = data["unassign"];
                            var assignRole ="";
                            var unassignRole ="";
                            for (var i = 0; i< assign.length; i++) {
                                assignRole += '<option value="'+assign[i].id+'">'+assign[i].name+'</option>';
                            }
                            for (var i = 0; i< unassign.length; i++) {
                                unassignRole += '<option value="'+unassign[i].id+'">'+unassign[i].name+'</option>';
                            }
                            $("#rightRoleList").html(assignRole);
                            $("#leftRoleList").html(unassignRole);
                        },"json");
                        var body = layer.getChildFrame('body', index); //得到子页面层的BODY
                        body.find('#hidValue').val(index); //将本层的窗口索引传给子页面层的hidValue中
                    },
                    btn: ['保存', '取消'],
                    yes: function (index, layero) {
                        var id = data.id;
                        var roleIds = "";
                        var roleArray = $("#rightRoleList option");
                        for (var i = 0; i < roleArray.length; i++) {
                            roleIds += $(roleArray[i]).val() + ",";
                        }
                        $.post("/api/users/assign", {"id": id, "roleIds": roleIds}, function (data) {
                            if (data.message === "success") {
                                parent.layer.msg('分配角色成功！', {icon: 1, shade: 0.4, time: 1000});
                                // 再次渲染表格
                                var word = $("#keyword").val();
                                tableIns.reload({
                                    where: {
                                        search: word
                                    }, page: {
                                        curr: pageNum
                                    }
                                });
                                layer.close(index);
                            }
                            else {
                                parent.layer.msg('分配角色失败，请重试！', {icon: 5, shade: 0.4, time: 1000});
                                layer.close(index);
                            }
                        },"json");
                    },
                    btn2: function (index, layero) {
                        layer.close(index);
                    }
                });
            }
        });
    });
</script>
</body>
</html>
