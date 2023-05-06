<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>乐享手机商城后台管理</title>
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
    <link href="/css/layui.css" media="all" rel="stylesheet">
    <script src="/js/jquery.min.js" type="text/javascript"></script>
    <script src="/js/bootstrap.min.js" type="text/javascript"></script>
    <script src="/js/layui.js" type="text/javascript"></script>
</head>
<body class="layui-layout-body">
<div class="layui-layout layui-layout-admin">

    <!-- 中间内容 -->
    <div class="container-fluid">
        <h1 class="title center" style="margin-top: 50px">修改密码</h1>
        <br />
        <div style="width:700px;height:auto;margin:0 auto;">
            <form class="layui-form" id="updatePwd" method="post">
                <input type="hidden" name="id" value="<shiro:principal property="id"/>">
                <div class="layui-form-item" >
                    <label class="layui-form-label">原密码</label>
                    <div class="layui-input-block">
                        <input type="password" name="password" id="userPassword" required
                               placeholder="请输入原密码" lay-verify="required"
                               class="layui-input" style="margin-top: 200px"/>
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">新密码</label>
                    <div class="layui-input-block">
                        <input type="password" name="newPassword" id="userPasswordNew" required
                               placeholder="请输入新密码" lay-verify="required"
                               class="layui-input" />
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">新密码</label>
                    <div class="layui-input-block">
                        <input type="password" name="newPasswordCheck" id="userPasswordNewCheck" required
                               placeholder="请再次确认新密码" lay-verify="required"
                               class="layui-input" />
                    </div>
                </div>

                <div class="layui-form-item" style="padding-left:105px;">
                    <button type="button" lay-submit lay-filter="updatePwd" id="updateSelf" class="layui-btn layui-btn-normal layui-btn-radius">修改</button>
                    <button type="reset" class="layui-btn layui-btn-danger layui-btn-radius">重置</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script type="text/javascript">
    layui.use([ 'form','layer','element'], function() {
        var form = layui.form;
        var layer=layui.layer;
        var element = layui.element;

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
                                window.location.href = "/adminlogin.jsp";
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

        // 监听表单提交事件   校验合格后，发送ajax请求 执行修改
        form.on('submit(updatePwd)',function(){

            form.render(); //更新全部
            if ($("#userPassword").val().length < 6 || $("#userPassword").val().length > 20) {
                layer.msg("原密码的长度不合规范，修改密码失败！请重试！",{icon:5,anim:6,time:2000,shade:0.4});
                return false;
            }
            if ($("#userPasswordNew").val().length < 6 || $("#userPasswordNew").val().length > 20) {
                layer.msg("新密码的长度不合规范，修改密码失败！请重试！",{icon:5,anim:6,time:2000,shade:0.4});
                return false;
            }
            if ($("#userPasswordNewCheck").val().length < 6 || $("#userPasswordNewCheck").val().length > 20) {
                layer.msg("确认新密码的长度不合规范，修改密码失败！请重试！",{icon:5,anim:6,time:2000,shade:0.4});
                return false;
            }
            if ($("#userPasswordNew").val() === $("#userPassword").val()) {
                layer.msg("新密码不能与原密码一致，修改密码失败！请重试！",{icon:5,anim:6,time:2000,shade:0.4});
                return false;
            }
            if ($("#userPasswordNew").val() !== $("#userPasswordNewCheck").val()) {
                layer.msg("两次新密码不一致，修改密码失败！请重试！",{icon:5,anim:6,time:2000,shade:0.4});
                return false;
            }

            $.ajax({
                url: "/api/users/updatePassword",
                type: "POST",
                data: $("#updatePwd").serialize(),
                success: function (data) {
                    if(data.message === "success"){
                        layer.msg("修改密码成功！请重新登录，谢谢合作！",{icon:1,anim:4,time:2000,shade:0.4},function(){
                            $.ajax({
                                type: "POST",
                                url: "/api/users/logout",
                                success: function (data) {
                                    if (data.message === 'success') {
                                        top.location.href = "/adminlogin.jsp";
                                    } else {
                                        layer.msg('退出失败', {icon: 5, shade: 0.4, time: 2000});
                                    }
                                },
                                error: function () {
                                    layer.msg('退出失败！', {icon: 5, shade: 0.4, time: 2000});
                                    top.location.href = "/error.jsp";
                                },
                                dataType: "json"
                            });
                        });
                    }else{
                        layer.msg("原密码错误，修改密码失败！请重试！",{icon:5,anim:6,time:2000,shade:0.4});
                    }
                },
                error: function () {
                    top.location.href = "/error.jsp"
                },
                dataType: "json"
            });
        });
    });
</script>
</body>
</html>
