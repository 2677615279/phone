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
    <h1 class="title center" style="margin-top: 50px">修改个人信息</h1>
    <br />
    <div style="width:700px;height:auto;margin:0 auto;">
        <form class="layui-form" id="userInfo" method="post">
            <input type="hidden" name="id" value="<shiro:principal property="id"/>">
            <div class="layui-form-item">
                <label class="layui-form-label">用户名</label>
                <div class="layui-input-block">
                    <input type="text" name="username" id="userName" required
                           lay-verify="required" placeholder="请输入用户名" value="<shiro:principal property="username"/>"
                           class="layui-input" style="margin-top: 130px" />
                </div>
            </div>

            <div class="layui-form-item">
                <label class="layui-form-label">电话</label>
                <div class="layui-input-block">
                    <input type="text" name="phone" id="userPhone" required
                           lay-verify="phone" placeholder="请输入电话号码" value="<shiro:principal property="phone"/>"
                           class="layui-input" />
                </div>
            </div>

            <div class="layui-form-item">
                <label class="layui-form-label">邮箱</label>
                <div class="layui-input-block">
                    <input type="text" name="email" id="userEmail" required
                           lay-verify="email" placeholder="请输入邮箱" value="<shiro:principal property="email"/>"
                           class="layui-input" />
                </div>
            </div>

            <div class="layui-form-item" style="color: darkred; text-align: center">以下为非必填项，可直接跳过，进行修改</div>
            <div class="layui-form-item">
                <label class="layui-form-label">生日</label>
                <div class="layui-input-block">
                    <input type="date" name="birthday" id="userBirthday"
                           placeholder="请输入生日"
                           class="layui-input" />
                </div>
            </div>

            <div class="layui-form-item" id="img">
                <label class="layui-form-label">头像</label>
                <div class="layui-input-block">
                    <button type="button" class="layui-btn" id="test1">
                        <i class="layui-icon">&#xe67c;</i>上传头像
                    </button>
                </div>
            </div>
            <div class="layui-form-item" style="color: darkred; text-align: center">
                双击预览的头像，可删除所选，恢复原头像
            </div>
            <div class="layui-form-item" style="color: darkred; text-align: center">
                <img alt="选择头像" src="/uploads/users_imgs/<shiro:principal property="img"/>" id="previewImg" style="width: 50px;height: 50px;">
            </div>

            <div class="layui-form-item" style="padding-left:105px;">
                <button type="button" lay-submit lay-filter="updateInfo" id="updateSelf" class="layui-btn layui-btn-normal layui-btn-radius">修改</button>
                <button type="reset" class="layui-btn layui-btn-danger layui-btn-radius">重置</button>
            </div>
        </form>
    </div>
</div>

</div>
<script type="text/javascript">
    layui.use([ 'form','upload','layer','element','table'], function() {
        var form = layui.form;
        var layer=layui.layer;
        var element = layui.element;
        var table = layui.table;
        var upload = layui.upload;
        var files;

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

        //清空文件队列
        function clearFile(){
            for (var i in files) {
                delete files[i];
            }
        }

        // 上传新头像 才会触发请求
        var uploadInst = upload.render({
            elem: '#test1' //绑定元素
            ,url: '/api/files/upload' //上传接口
            ,multiple: false // 不允许多文件上传
            ,method: 'post'
            ,auto: false //选择文件后不自动上传
            ,bindAction: '#updateSelf' //绑定表单提交的按钮 触发上传请求
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
                        obj.pushFile(); // 再把当前文件重新加入队列
                    });
                    layer.close(index);
                });
                //双击手工选择的图片   删除之前选择的图片  恢复原图片
                $("#previewImg").bind('dblclick', function () {
                    clearFile();
                    $("#previewImg").removeAttr("src");
                    $("#previewImg").attr('src', "/uploads/users_imgs/<shiro:principal property="img"/>");
                });

            }
            ,before: function () {
                // 如果没有校验通过，阻止文件上传，layui官方文档 before:方法内return false即可，实测无效

                if ($("#userName").val().length < 3 || $("#userName").val().length > 20) {
                    layer.msg("用户名不合规范，修改信息失败！请重试！",{icon:5,anim:6,time:2000,shade:0.4});
                    layer.stopPropagation(); // 阻止默认事件、事件冒泡
                    return false;
                }
                else if (!checkPhone()) {
                    layer.msg("手机不合规范，修改信息失败！请重试！",{icon:5,anim:6,time:2000,shade:0.4});
                    layer.stopPropagation(); // 阻止默认事件、事件冒泡
                    return false;
                }
                else if (!checkEmail()) {
                    layer.msg("邮箱不合规范，修改信息失败！请重试！",{icon:5,anim:6,time:2000,shade:0.4});
                    layer.stopPropagation(); // 阻止默认事件、事件冒泡
                    return false;
                }
                else {
                    return true;
                }
            }
            ,done: function(res){
                if (res.code == 200) {
                    layer.msg('新头像上传成功，请稍等！', {
                        icon : 1,
                        time : 2000
                    });
                    clearFile();

                    // 如果不是原头像，因为上传绑定了修改按钮，点击修改时，则会触发文件上传，此时不走form.on中的代码
                    // 上传完毕，将图片结果写入表单中一个新控件，重新渲染，走下面的代码，校验合格后，发送ajax请求执行注册
                    $("#img").append('<input type="hidden" value="'+res.data+'" name="img" >');
                    form.render(); //更新全部
                    // 这里需要使用new FormData()来获取表单中的控件
                    var formData = new FormData(document.getElementById("userInfo"));
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
                        url: "/api/users/updateSelf",
                        type: "POST",
                        data: formData,
                        processData: false, // 禁止序列化data，默认为true
                        contentType: false, // 避免jquery对contentType做操作
                        success: function (data) {
                            if(data.message === "success"){
                                layer.msg("修改信息成功！",{icon:1,anim:4,time:2000,shade:0.4});
                            }else{
                                layer.msg("修改信息失败！请重试！",{icon:5,anim:6,time:2000,shade:0.4});
                            }
                        },
                        error: function () {
                            top.location.href = "/error.jsp"
                        },
                        dataType: "json"
                    });
                }
                else {
                    layer.msg('修改信息失败！请重试！', {icon:5,anim:6,time:2000,shade:0.4});
                    clearFile();
                }
            }
            ,error: function(){
                layer.msg('头像上传失败，修改信息失败！请重试！', {icon:5,anim:6,time:2000,shade:0.4});
            }
        });

        // 监听表单提交事件：当点击表单修改按钮，判断预览头像是不是原头像，如果是原头像则不触发文件上传，进入下面form.on中的代码
        // 校验合格后，发送ajax请求 执行修改
        form.on('submit(updateInfo)',function(){
            if ($("#previewImg").attr("src") === "/uploads/users_imgs/<shiro:principal property="img"/>") {

                // 这里需要使用new FormData()来获取表单中的控件
                var formData = new FormData(document.getElementById("userInfo"));
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
                    url: "/api/users/updateSelf",
                    type: "POST",
                    data: formData,
                    processData: false, // 禁止序列化data，默认为true
                    contentType: false, // 避免jquery对contentType做操作
                    success: function (data) {
                        if(data.message === "success"){
                            layer.msg("修改信息成功！",{icon:1,anim:4,time:2000,shade:0.4});
                        }else{
                            layer.msg("修改信息失败！请重试！",{icon:5,anim:6,time:2000,shade:0.4});
                        }
                    },
                    error: function () {
                        top.location.href = "/error.jsp"
                    },
                    dataType: "json"
                });
            }
        });
    });


    //表单校验
    //校验用户名，单词字符，长度3-20位
    function checkUsername() {
        var username = $("#userName").val();//获取该组件输入框的值
        // "/"是表达式开始和结束的标记
        //“^”表示行的开始；“$”表示行的结尾。 行定位符就是用来描述字串的边界。
        // \w 元字符用于查找单词字符。单词字符包括：a-z、A-Z、0-9，以及下划线, 包含 _ (下划线) 字符。
        // {3,30} 匹配\w最少3次 最多20次
        var reg_username = /^(a-z|A-Z|0-9)*[^$%^&*;:,<>?()\""\']{3,20}$/;
        var flag = reg_username.test(username);
        if (flag){//用户名合法
            $("#userName").css("border","");
        } else {//用户名非法
            $("#userName").css("border","1px solid red");
        }
        return flag;
    }

    //校验邮箱
    function checkEmail(){
        var email = $("#userEmail").val();
        // "/"是表达式开始和结束的标记
        //“^”表示行的开始；“$”表示行的结尾。 行定位符就是用来描述字串的边界。
        // \w 元字符用于查找单词字符。单词字符包括：a-z、A-Z、0-9，以及下划线, 包含 _ (下划线) 字符。
        // "+" 匹配的字符出现一次或多次    "."特殊字符 需要加\转义
        var reg_email = /^\w+@\w+\.\w+$/;//定义正则
        var flag = reg_email.test(email);
        if (flag){//邮箱合法
            $("#userEmail").css("border","");
        } else {//邮箱非法
            $("#userEmail").css("border","1px solid red");
        }
        return flag;
    }

    //校验手机
    function checkPhone(){
        var phone = $("#userPhone").val();
        // "/"是表达式开始和结束的标记
        //“^”表示行的开始；“$”表示行的结尾。 行定位符就是用来描述字串的边界。
        // \w 元字符用于查找单词字符。单词字符包括：a-z、A-Z、0-9，以及下划线, 包含 _ (下划线) 字符。
        // "+" 匹配的字符出现一次或多次    "."特殊字符 需要加\转义
        var reg_phone = /^1[0-9]{10}$/;//定义正则
        var flag = reg_phone.test(phone);
        if (flag){//手机合法
            $("#userPhone").css("border","");
        } else {//手机非法
            $("#userPhone").css("border","1px solid red");
        }
        return flag;
    }

    //当某一个组件失去焦点时，调用对应的校验方法
    $("#userName").blur(checkUsername);
    $("#userEmail").blur(checkEmail);
    $("#userPhone").blur(checkPhone);
</script>
</body>
</html>
