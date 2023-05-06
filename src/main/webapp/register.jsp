<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>乐享手机商城</title>
    <!--头部资源-->
    <link href="/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
    <script src="/js/jquery.1.12.4.min.js" type="text/javascript"></script>
    <script src="/js/bootstrap.min.js" type="text/javascript"></script>
    <!--底部资源-->
    <link href="/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
    <script src="/js/bootstrap.min.js" type="text/javascript"></script>

    <link href="/css/style.css" rel="stylesheet">
    <link href="/css/layui.css" rel="stylesheet">
    <link href="/css/admin.css" rel="stylesheet">
    <link href="/css/pro.css" rel="stylesheet">
    <link href="/css/login.css" rel="stylesheet">
    <script src="/js/jquery.min.js" type="text/javascript"></script>
    <script src="/js/layui.js" type="text/javascript"></script>
</head>
<body>
<!--导航栏部分-->
<jsp:include page="/user/include/header.jsp" />

<!-- 中间内容 -->
<div class="container-fluid">
    <form id="regForm" method="post">
        <div class="layadmin-user-login layadmin-user-display-show" style="padding-top:30px;padding-bottom:30px;"
             id="LAY-user-login">
            <div class="layadmin-user-login-main" style="margin-top: 50px">
                <div class="layadmin-user-login-box layadmin-user-login-header">
                    <h2>用户注册</h2>
                </div>
                <div class="layadmin-user-login-box layadmin-user-login-body layui-form">
                    <div class="layui-form-item">
                        <label
                                class="layadmin-user-login-icon layui-icon layui-icon-username"
                                for="LAY-user-login-username"></label> <input type="text"
                                                                              name="username" id="LAY-user-login-username"
                                                                              lay-verify="required"
                                                                              placeholder="用户名 必填项" class="layui-input">
                    </div>
                    <div class="layui-form-item">
                        <label
                                class="layadmin-user-login-icon layui-icon layui-icon-password"
                                for="LAY-user-login-password"></label> <input type="password"
                                                                              name="password" id="LAY-user-login-password"
                                                                              lay-verify="required"
                                                                              placeholder="密码 必填项" class="layui-input">
                    </div>
                    <div class="layui-form-item">
                        <label
                                class="layadmin-user-login-icon layui-icon layui-icon-password"
                                for="LAY-user-login-passwordCheck"></label> <input type="password"
                                                                            name="passwordCheck" id="LAY-user-login-passwordCheck"
                                                                            lay-verify="required"
                                                                            placeholder="确认密码 必填项" class="layui-input">
                    </div>
                    <div class="layui-form-item">
                        <label
                                class="layadmin-user-login-icon layui-icon layui-icon-cellphone"
                                for="LAY-user-login-phone"></label> <input type="text"
                                                                               name="phone" id="LAY-user-login-phone"
                                                                               lay-verify="phone" placeholder="手机 必填项" class="layui-input">
                    </div>
                    <div class="layui-form-item">
                        <label
                                class="layadmin-user-login-icon layui-icon layui-icon-cellphone"
                                for="LAY-user-login-email"></label> <input type="text"
                                                                           name="email" id="LAY-user-login-email"
                                                                           lay-verify="email" placeholder="邮箱 必填项" class="layui-input">
                    </div>
                    <div class="layui-form-item" style="color: darkred; text-align: center">以下为非必填项，可直接跳过，进行注册</div>
                    <div class="layui-form-item">
                        <label
                                class="layadmin-user-login-icon layui-icon layui-icon-date"
                                for="LAY-user-login-birthday"></label> <input type="date"
                                                                           name="birthday" id="LAY-user-login-birthday"
                                                                           placeholder="生日 非必填项" class="layui-input">

                    </div>

                    <div class="layui-form-item" id="img">
                            <button type="button" class="layui-btn" id="test1">
                                <i class="layui-icon">&#xe67c;</i>上传头像
                            </button>
                    </div>
                    <div class="layui-form-item" style="color: darkred; text-align: center">
                        双击预览的头像，可删除所选，恢复默认头像
                    </div>
                    <img alt="选择头像" src="/uploads/users_imgs/测试上传.jpg" id="previewImg" style="width: 50px;height: 50px;">

                    <div class="layui-form-item" style="height:50px;">
                        <input id="agreement" type="checkbox" name="agreement" lay-skin="primary"
                               title="同意用户协议" checked>
                        <div
                                class="layui-unselect layui-form-checkbox layui-form-checked"
                                lay-skin="primary">
                            <span>同意用户协议</span><i class="layui-icon layui-icon-ok"></i>
                        </div>
                    </div>
                    <div class="layui-form-item">
                        <button id="register" class="layui-btn layui-btn-fluid" lay-submit="" type="button"
                                lay-filter="reg-submit">注 册</button>
                    </div>
                    <div class="layui-trans layui-form-item layadmin-user-login-other">
                        <label>社交账号注册</label> <a href="javascript:"><i
                            class="layui-icon layui-icon-login-qq"></i></a> <a
                            href="javascript:"><i
                            class="layui-icon layui-icon-login-wechat"></i></a> <a
                            href="javascript:"><i
                            class="layui-icon layui-icon-login-weibo"></i></a> <a
                            lay-href="/api/users/login"
                            class="layadmin-user-jump-change layadmin-link layui-hide-xs">使用已有帐号登入</a>
                        <a lay-href="/api/users/login"
                           class="layadmin-user-jump-change layadmin-link layui-hide-sm layui-show-xs-inline-block">登入</a>
                    </div>
                </div>
            </div>
        </div>
    </form>
</div>

<!--尾部-->
<jsp:include page="/user/include/foot.jsp" />

<script type="text/javascript">
    layui.use(['form','upload','layer'], function() {
        var form = layui.form;
        var layer = layui.layer;
        var upload = layui.upload;
        var files;
        //清空文件队列
        function clearFile(){
            for (var i in files) {
                delete files[i];
            }
        }

        // 处理文件上传
        // 1.直接没选图 使用默认 点击注册按钮 --> 不触发上传请求，直接走注册请求
        // 2.选了几次图 又删除了选择 使用默认 点击注册按钮 --> 不触发上传请求，直接走注册请求
        // 3.直接选一次 使用自选图 触发上传请求 点击注册按钮 --> 先触发上传请求，上传完毕后，再走注册请求
        // 4.选多次自选图 多次删除恢复默认 再选一次自选图 点击注册按钮 --> 触发上传请求，上传完毕后，再走注册请求
        var uploadInst = upload.render({
            elem: '#test1' //绑定元素
            ,url: '/api/files/upload' //上传接口
            ,multiple: false // 不允许多文件上传
            ,method: 'post'
            ,auto: false //选择文件后不自动上传
            ,bindAction: '#register' //绑定表单提交的按钮 触发上传请求
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
                //双击手工选择的图片   删除之前选择的图片  恢复原默认图片
                $("#previewImg").bind('dblclick', function () {
                    clearFile();
                    $("#previewImg").removeAttr("src");
                    $("#previewImg").attr('src', "/uploads/users_imgs/测试上传.jpg");
                });

            }
            ,before: function () {
                // 如果没有同意用户协议，阻止文件上传，layui官方文档 before:方法内return false即可，实测无效
                if (!$("#agreement").is(':checked')) {
                    layer.msg("您尚未同意用户协议，头像上传失败！请重试！",{icon:5,anim:6,time:2000});
                    // TODO 终止上传请求 F12报错layer.stopPropagation is not a function  但是对后续页面其他操作无影响
                    layer.stopPropagation(); // 阻止默认事件、事件冒泡
                    return false;
                }
                else if ($("#LAY-user-login-username").val().length < 3 || $("#LAY-user-login-username").val().length > 20) {
                    layer.msg("用户名不合规范，注册失败！请重试！",{icon:5,anim:6,time:2000});
                    layer.stopPropagation(); // 阻止默认事件、事件冒泡
                    return false;
                }
                else if ($("#LAY-user-login-password").val().length < 6 || $("#LAY-user-login-password").val().length > 20) {
                    layer.msg("密码不合规范，注册失败！请重试！",{icon:5,anim:6,time:2000});
                    layer.stopPropagation(); // 阻止默认事件、事件冒泡
                    return false;
                }
                else if ($("#LAY-user-login-passwordCheck").val().length < 6 || $("#LAY-user-login-passwordCheck").val().length > 20 || $("#LAY-user-login-passwordCheck").val() !== $("#LAY-user-login-password").val()) {
                    layer.msg("确认密码不合规范，注册失败！请重试！",{icon:5,anim:6,time:2000});
                    layer.stopPropagation(); // 阻止默认事件、事件冒泡
                    return false;
                }
                else if (!checkPhone()) {
                    layer.msg("手机不合规范，注册失败！请重试！",{icon:5,anim:6,time:2000});
                    layer.stopPropagation(); // 阻止默认事件、事件冒泡
                    return false;
                }
                else if (!checkEmail()) {
                    layer.msg("邮箱不合规范，注册失败！请重试！",{icon:5,anim:6,time:2000});
                    layer.stopPropagation(); // 阻止默认事件、事件冒泡
                    return false;
                }
                else {
                    return true;
                }
            }
            ,done: function(res){
                if (res.code == 200) {
                    layer.msg('头像上传成功，请稍等！', {
                        icon : 1,
                        time : 2000
                    });
                    clearFile();

                    // 如果不是默认头像，因为上传绑定了注册按钮，点击注册时，则会触发文件上传，此时不走form.on中的代码
                    // 上传完毕，将图片结果写入表单中一个新控件，重新渲染，走下面的代码，校验合格后，发送ajax请求执行注册
                    $("#img").append('<input type="hidden" value="'+res.data+'" name="img" >');
                    form.render(); //更新全部
                    // 这里需要使用new FormData()来获取表单中的控件
                    var formData = new FormData(document.getElementById("regForm"));
                    formData.delete("agreement"); // 删除是否同意用户协议控件的参数
                    form.render(); //更新全部

                    // 校验是否同意用户协议
                    if (!$("#agreement").is(':checked')) {
                        layer.msg("您尚未同意用户协议，注册失败！请重试！",{icon:5,anim:6,time:2000});
                        return false;
                    }
                    if (formData.get("username").length < 3 || formData.get("username").length > 20) {
                        layer.msg("用户名不合规范，注册失败！请重试！",{icon:5,anim:6,time:2000});
                        return false;
                    }
                    if (formData.get("password").length < 6 || formData.get("username").length > 20) {
                        layer.msg("密码不合规范，注册失败！请重试！",{icon:5,anim:6,time:2000});
                        return false;
                    }
                    if (formData.get("passwordCheck").length < 6 || formData.get("username").length > 20 || formData.get("passwordCheck") !== formData.get("password")) {
                        layer.msg("确认密码不合规范，注册失败！请重试！",{icon:5,anim:6,time:2000});
                        return false;
                    }
                    if (formData.get("phone").indexOf("1") !== 0 || formData.get("phone").length !== 11) {
                        layer.msg("手机不合规范，注册失败！请重试！",{icon:5,anim:6,time:2000});
                        return false;
                    }
                    if (formData.get("email").indexOf("@") === -1) {
                        layer.msg("邮箱不合规范，注册失败！请重试！",{icon:5,anim:6,time:2000});
                        return false;
                    }
                    $.ajax({
                        url: "/api/users/register",
                        type: "POST",
                        data: formData,
                        processData: false, // 禁止序列化data，默认为true
                        contentType: false, // 避免jquery对contentType做操作
                        success: function (data) {
                            if(data.message === "success"){
                                layer.msg("注册成功！即将转向登录页面！",{icon:1,anim:4,time:2000},function(){
                                    window.location.href = "/login.jsp";
                                });
                            }else{
                                layer.msg("注册失败！请重试！",{icon:5,anim:6,time:2000});
                            }
                        },
                        error: function () {
                            window.location.href = "/error.jsp"
                        },
                        dataType: "json"
                    });
                }
                else {
                    layer.msg('头像上传失败，注册失败！请重试！', {
                        icon : 5,
                        time : 2000
                    });
                    clearFile();
                }
            }
            ,error: function(){
                layer.msg('头像上传失败，注册失败！请重试！', {
                    icon : 5,
                    time : 2000
                });
            }
        });

        // 监听表单提交事件：当点击表单注册按钮，判断预览头像是不是默认，如果是默认头像则不触发文件上传，进入下面form.on中的代码
        // 校验合格后，发送ajax请求 执行注册
        form.on('submit(reg-submit)',function(){
            if ($("#previewImg").attr("src") === "/uploads/users_imgs/测试上传.jpg") {

                // 这里需要使用new FormData()来获取表单中的控件
                var formData = new FormData(document.getElementById("regForm"));
                formData.delete("agreement"); // 删除是否同意用户协议控件的参数
                form.render(); //更新全部

                // 校验是否同意用户协议
                if (!$("#agreement").is(':checked')) {
                    layer.msg("您尚未同意用户协议，注册失败！请重试！",{icon:5,anim:6,time:2000});
                    return false;
                }
                if (formData.get("username").length < 3 || formData.get("username").length > 20) {
                    layer.msg("用户名不合规范，注册失败！请重试！",{icon:5,anim:6,time:2000});
                    return false;
                }
                if (formData.get("password").length < 6 || formData.get("username").length > 20) {
                    layer.msg("密码不合规范，注册失败！请重试！",{icon:5,anim:6,time:2000});
                    return false;
                }
                if (formData.get("passwordCheck").length < 6 || formData.get("username").length > 20 || formData.get("passwordCheck") !== formData.get("password")) {
                    layer.msg("确认密码失败，注册失败！请重试！",{icon:5,anim:6,time:2000});
                    return false;
                }
                if (formData.get("phone").indexOf("1") !== 0 || formData.get("phone").length !== 11) {
                    layer.msg("手机不合规范，注册失败！请重试！",{icon:5,anim:6,time:2000});
                    return false;
                }
                if (formData.get("email").indexOf("@") === -1) {
                    layer.msg("邮箱不合规范，注册失败！请重试！",{icon:5,anim:6,time:2000});
                    return false;
                }
                $.ajax({
                    url: "/api/users/register",
                    type: "POST",
                    data: formData,
                    processData: false, // 禁止序列化data，默认为true
                    contentType: false, // 避免jquery对contentType做操作
                    success: function (data) {
                        if(data.message === "success"){
                            layer.msg("注册成功！即将转向登录页面！",{icon:1,anim:4,time:2000},function(){
                                window.location.href = "/login.jsp";
                            });
                        }else{
                            layer.msg("注册失败！请重试！",{icon:5,anim:6,time:2000});
                        }
                    },
                    error: function () {
                        window.location.href = "/error.jsp"
                    },
                    dataType: "json"
                });
            }
        });
    });

    //表单校验
    //校验用户名，单词字符，长度3-20位
    function checkUsername() {
        var username = $("#LAY-user-login-username").val();//获取该组件输入框的值
        // "/"是表达式开始和结束的标记
        //“^”表示行的开始；“$”表示行的结尾。 行定位符就是用来描述字串的边界。
        // \w 元字符用于查找单词字符。单词字符包括：a-z、A-Z、0-9，以及下划线, 包含 _ (下划线) 字符。
        // {3,30} 匹配\w最少3次 最多20次
        var reg_username = /^(a-z|A-Z|0-9)*[^$%^&*;:,<>?()\""\']{3,20}$/;
        var flag = reg_username.test(username);
        if (flag){//用户名合法
            $("#LAY-user-login-username").css("border","");
        } else {//用户名非法
            $("#LAY-user-login-username").css("border","1px solid red");
        }
        return flag;
    }

    //校验密码，单词字符，长度6-20位
    function checkPassword() {
        var password = $("#LAY-user-login-password").val();//获取该组件输入框的值
        // "/"是表达式开始和结束的标记
        //“^”表示行的开始；“$”表示行的结尾。 行定位符就是用来描述字串的边界。
        // \w 元字符用于查找单词字符。单词字符包括：a-z、A-Z、0-9，以及下划线, 包含 _ (下划线) 字符。
        // {6,20} 匹配\w最少6次 最多20次
        var reg_password = /^\w{6,20}$/;//定义正则
        var flag = reg_password.test(password);
        if (flag){//密码合法
            $("#LAY-user-login-password").css("border","");
        } else {//密码非法
            $("#LAY-user-login-password").css("border","1px solid red");
        }
        return flag;
    }

    //校验密码，单词字符，长度6-20位
    function checkPasswordNext() {
        var password = $("#LAY-user-login-password").val();//获取该组件输入框的值
        var passwordCheck = $("#LAY-user-login-passwordCheck").val();//获取该组件输入框的值
        // "/"是表达式开始和结束的标记
        //“^”表示行的开始；“$”表示行的结尾。 行定位符就是用来描述字串的边界。
        // \w 元字符用于查找单词字符。单词字符包括：a-z、A-Z、0-9，以及下划线, 包含 _ (下划线) 字符。
        // {6,20} 匹配\w最少6次 最多20次
        var reg_password = /^\w{6,20}$/;//定义正则
        var flag = reg_password.test(passwordCheck) && password === passwordCheck;
        if (flag){//密码合法
            $("#LAY-user-login-passwordCheck").css("border","");
        } else {//密码非法
            $("#LAY-user-login-passwordCheck").css("border","1px solid red");
        }
        return flag;
    }

    //校验邮箱
    function checkEmail(){
        var email = $("#LAY-user-login-email").val();
        // "/"是表达式开始和结束的标记
        //“^”表示行的开始；“$”表示行的结尾。 行定位符就是用来描述字串的边界。
        // \w 元字符用于查找单词字符。单词字符包括：a-z、A-Z、0-9，以及下划线, 包含 _ (下划线) 字符。
        // "+" 匹配的字符出现一次或多次    "."特殊字符 需要加\转义
        var reg_email = /^\w+@\w+\.\w+$/;//定义正则
        var flag = reg_email.test(email);
        if (flag){//邮箱合法
            $("#LAY-user-login-email").css("border","");
        } else {//邮箱非法
            $("#LAY-user-login-email").css("border","1px solid red");
        }
        return flag;
    }

    //校验手机
    function checkPhone(){
        var phone = $("#LAY-user-login-phone").val();
        // "/"是表达式开始和结束的标记
        //“^”表示行的开始；“$”表示行的结尾。 行定位符就是用来描述字串的边界。
        // \w 元字符用于查找单词字符。单词字符包括：a-z、A-Z、0-9，以及下划线, 包含 _ (下划线) 字符。
        // "+" 匹配的字符出现一次或多次    "."特殊字符 需要加\转义
        var reg_phone = /^1[0-9]{10}$/;//定义正则
        var flag = reg_phone.test(phone);
        if (flag){//手机合法
            $("#LAY-user-login-phone").css("border","");
        } else {//手机非法
            $("#LAY-user-login-phone").css("border","1px solid red");
        }
        return flag;
    }

    //当某一个组件失去焦点时，调用对应的校验方法
    $("#LAY-user-login-username").blur(checkUsername);
    $("#LAY-user-login-password").blur(checkPassword);
    $("#LAY-user-login-passwordCheck").blur(checkPasswordNext);
    $("#LAY-user-login-email").blur(checkEmail);
    $("#LAY-user-login-phone").blur(checkPhone);

</script>
</body>
</html>
