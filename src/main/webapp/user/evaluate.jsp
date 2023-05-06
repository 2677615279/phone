<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>评价商品</title>
    <link href="/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
    <link href="/css/layui.css" rel="stylesheet">
    <script src="/js/jquery.min.js" type="text/javascript"></script>
    <script src="/js/bootstrap.min.js" type="text/javascript"></script>
    <script src="/js/layui.js" type="text/javascript"></script>
</head>
<body>

<script type="text/javascript">
    layui.use(['table','rate','form'], function(){
        var rate = layui.rate;
        var form=layui.form;
        var table=layui.table;
        //渲染
        var ins1 = rate.render({
            elem: '#evaLevel',  //绑定元素
            theme: '#FF5722',
            choose:function(value){
                $("#evaStar").val(value);
            }
        });
    });
</script>
</body>
</html>
