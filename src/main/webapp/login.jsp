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
    <script src="/js/jquery.cookie.js" type="text/javascript"></script>
    <script src="/js/layui.js" type="text/javascript"></script>
    <style type="text/css">
        .layadmin-user-login-footer p a{
            text-decoration:none
        }
    </style>
</head>
<body>
<!--导航栏部分-->
<jsp:include page="/user/include/header.jsp" />

<!-- 中间内容 -->
<div class="container-fluid">
    <form id="loginForm" method="post" style="height: 90%;">
        <div class="layadmin-user-login layadmin-user-display-show" style="padding-bottom:0px;padding-top:50px;height:200px;"
             id="LAY-user-login">
            <div class="layadmin-user-login-main" style="height:200px;">
                <div class="layadmin-user-login-box layadmin-user-login-header">
                    <h2>用户登录</h2>
                </div>
                <div
                        class="layadmin-user-login-box layadmin-user-login-body layui-form" style="height:100px;">
                    <div class="layui-form-item">
                        <label
                                class="layadmin-user-login-icon layui-icon layui-icon-username"
                                for="LAY-user-login-username"></label> <input type="text"
                                                                              name="principal" id="userName"
                                                                              lay-verify="required" placeholder="账号 必填项"
                                                                              class="layui-input layui-form-danger">
                    </div>
                    <div class="layui-form-item">
                        <label
                                class="layadmin-user-login-icon layui-icon layui-icon-password"
                                for="LAY-user-login-password"></label> <input type="password"
                                                                                                             name="credentials" id="userPass"
                                                                                                             lay-verify="required" placeholder="密码 必填项" class="layui-input">
                    </div>
                    <div class="layui-form-item">
                        <div class="layui-row">
                            <div class="layui-col-xs7">
                                <label class="layadmin-user-login-icon layui-icon layui-icon-vercode" for="LAY-user-login-vercode"></label>
                                <input type="text" name="code" id="LAY-user-login-vercode" lay-verify="required" placeholder="图形验证码 必填项" class="layui-input">
                            </div>
                            <div class="layui-col-xs5">
                                <div style="margin-left: 10px;">
                                    <canvas id="canvas" width="130" height="38"></canvas>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="layui-form-item" style="height:50px;">
                        <input type="checkbox" id="remember" name="remember" lay-skin="primary"
                               title="记住密码">
                        <div class="layui-unselect layui-form-checkbox" lay-skin="primary">
                            <span>记住密码</span><i class="layui-icon layui-icon-ok"></i>
                        </div>
                        <input type="checkbox" id="rememberMe" name="rememberMe" lay-skin="primary"
                               title="记住我">
                        <div class="layui-unselect layui-form-checkbox" lay-skin="primary">
                            <span>记住我</span><i class="layui-icon layui-icon-ok"></i>
                        </div>
                    </div>
                    <div class="layui-form-item">
                        <button class="layui-btn layui-btn-fluid" type="button" lay-submit=""
                                lay-filter="loginSubmit">登 录</button>
                    </div>
                </div>
            </div>
        </div>
    </form>
</div>

<!--尾部-->
<div class="layui-trans layadmin-user-login-footer">

    <p>© 2020 <a href="/" target="_self">LEMarket.com</a></p>
    <p>
        <span><a href="/" target="_self">前往首页</a></span>
        <span><a href="/adminlogin.jsp" target="_self">前往后台</a></span>
    </p>
</div>

<script type="text/javascript">
    layui.use(['form','layer'], function() {
        var form = layui.form;
        var layer = layui.layer;
        var show_num = [];
        draw(show_num);

        $("#canvas").on('click',function(){
            draw(show_num);
        });

        form.on('submit(loginSubmit)',function(){
            var formData = new FormData(document.getElementById("loginForm"));
            var code = $('#LAY-user-login-vercode').val().toLowerCase();
            var num = show_num.join("");
            if(code !== num){
                layer.msg("验证码输入错误，请重新输入！",{icon:5,anim:6,time:3000});
                draw(show_num);
                $('#LAY-user-login-vercode').val("");
                return false;
            }
            if ($("#remember").is(':checked')) {
                formData.delete("remember");
            }
            if ($("#rememberMe").is(':checked')) {
                formData.set("rememberMe", true);
            }
            else {
                formData.append("rememberMe", false);
            }
            form.render(); //更新全部

            $.ajax({
                url: "/api/users/login",
                type: "POST",
                data: formData,
                processData: false, // 禁止序列化data，默认为true
                contentType: false, // 避免jquery对contentType做操作
                success: function(data){
                    if(data.message === "success"){
                        layer.msg("登录成功！",{icon:1,anim:2,time:2000},function(){
                            window.location.href = "/";
                        });
                    }
                    else{
                        layer.msg("账号或密码错误或账号被禁用，登录失败！",{icon:5,anim:6,time:3000});
                    }

                    if($("#remember").is(':checked')){
                        setCookie('principal',$("#userName").val(),7);
                        setCookie('credentials',$("#userPass").val(),7);
                    }
                    else{
                        delCookie('principal');
                    }
                },
                error: function () {
                    window.location.href = "/error.jsp"
                },
                dataType:"json"
            });
        });
    });


    /**
     * 设置cookie
     */

    function setCookie(name,value,day){
        var date = new Date();
        date.setDate(date.getDate() + day);
        document.cookie = name + '=' + value + ';expires='+ date;
    }
    /**
     * 获取cookie
     */
    function getCookie(name){
        var reg = RegExp(name+'=([^;]+)');
        var arr = document.cookie.match(reg);
        if(arr){
            return arr[1];
        }else{
            return '';
        }
    }

    /**
     * 删除cookie
     */

    function delCookie(name){
        setCookie(name,null,-1);
    }

    $(function () {
        //页面初始化时，如果帐号密码cookie存在则填充
        if(getCookie('principal') && getCookie('credentials')){
            $("#userName").val(getCookie('principal'));
            $("#userPass").val(getCookie('credentials'));
            $("#remember").attr('checked',true);
        }
    });



    function draw(show_num) {
        var canvas_width = $('#canvas').width();
        var canvas_height = $('#canvas').height();
        var canvas = document.getElementById("canvas");//获取到canvas的对象，演员
        var context = canvas.getContext("2d");//获取到canvas画图的环境，演员表演的舞台
        canvas.width = canvas_width;
        canvas.height = canvas_height;
        var sCode = "A,B,C,E,F,G,H,J,K,L,M,N,P,Q,R,S,T,W,X,Y,Z,1,2,3,4,5,6,7,8,9,0";
        var aCode = sCode.split(",");
        var aLength = aCode.length;//获取到数组的长度

        for (var i = 0; i <= 3; i++) {
            var j = Math.floor(Math.random() * aLength);//获取到随机的索引值
            var deg = Math.random() * 30 * Math.PI / 180;//产生0~30之间的随机弧度
            var txt = aCode[j];//得到随机的一个内容
            show_num[i] = txt.toLowerCase();
            var x = 10 + i * 20;//文字在canvas上的x坐标
            var y = 20 + Math.random() * 8;//文字在canvas上的y坐标
            context.font = "bold 23px 微软雅黑";

            context.translate(x, y);
            context.rotate(deg);

            context.fillStyle = randomColor();
            context.fillText(txt, 0, 0);

            context.rotate(-deg);
            context.translate(-x, -y);
        }
        for (var i = 0; i <= 5; i++) { //验证码上显示线条
            context.strokeStyle = randomColor();
            context.beginPath();
            context.moveTo(Math.random() * canvas_width, Math.random() * canvas_height);
            context.lineTo(Math.random() * canvas_width, Math.random() * canvas_height);
            context.stroke();
        }
        for (var i = 0; i <= 30; i++) { //验证码上显示小点
            context.strokeStyle = randomColor();
            context.beginPath();
            var x = Math.random() * canvas_width;
            var y = Math.random() * canvas_height;
            context.moveTo(x, y);
            context.lineTo(x + 1, y + 1);
            context.stroke();
        }
    }

    function randomColor() {//得到随机的颜色值
        var r = Math.floor(Math.random() * 256);
        var g = Math.floor(Math.random() * 256);
        var b = Math.floor(Math.random() * 256);
        return "rgb(" + r + "," + g + "," + b + ")";
    }
</script>
</body>
</html>
