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
<div id="editForm" style="width: 700px; padding-top: 60px;margin: 0 auto">
    <form id="formData" class="layui-form">
        <div class="layui-form-item">
            <label class="layui-form-label">名称</label>
            <div class="layui-input-block">
                <input type="text" name="name" id="goodsName" required
                       lay-verify="required" placeholder="请输入商品名称" value=""
                       class="layui-input" />
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">价格</label>
            <div class="layui-input-block">
                <input type="text" name="price" id="goodsPrice" required
                       lay-verify="required" placeholder="请输入商品价格" value=""
                       class="layui-input" />
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">库存</label>
            <div class="layui-input-block">
                <input type="text" name="num" id="goodsNum" required
                       lay-verify="required" placeholder="请输入商品库存" value=""
                       class="layui-input" />
            </div>
        </div>
        <div class="layui-form-item" lay-filter="test">
            <label class="layui-form-label">类别</label>
            <div class="layui-input-block">
                <select name="type" id="goodsType" required
                        lay-verify="required" lay-filter="type">
                </select>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">内存</label>
            <div class="layui-input-block">
                <select name="memory" id="memory" required
                        lay-verify="required" lay-filter="memory">
                </select>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">颜色</label>
            <div class="layui-input-block">
                <input type="text" name="color" id="goodsColor" required
                       lay-verify="required" value="" class="layui-input" />
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">描述</label>
            <div class="layui-input-block">
					<textarea name="description" id="goodsDesc" required
                              lay-verify="required" class="layui-textarea"></textarea>
            </div>
        </div>

        <div class="layui-form-item" id="addimg">
            <label class="layui-form-label">图片</label>
            <div class="layui-input-block">
                <button type="button" class="layui-btn layui-btn-normal" id="test1">
                    <i class="layui-icon">&#xe67c;</i>选择图片
                </button>
            </div>
        </div>
        <div class="layui-form-item" style="color: darkred; text-align: center">
            双击预览的图片，可删除所选
        </div>
        <img alt="选择图片"  id="previewImg" style="margin:0 auto;width: 130px;height: 180px;display: none">

        <div class="layui-form-item" style="margin-left:110px;">
            <button type="button" lay-submit lay-filter="*" class="layui-btn layui-btn-lg" style="margin-right:50px;" id="insert">添加</button>
            <button type="reset" class="layui-btn layui-btn-warm layui-btn-lg" style="margin-left: 260px">重置</button>
        </div>
    </form>
</div>

<script>
    $(function(){
        layui.use(['layer', 'table','form','upload'], function () {
            var table = layui.table;
            var layer = layui.layer;
            var form =layui.form;
            var upload=layui.upload;

            var files;
            //清空文件队列
            function clearFile(){
                for (var i in files) {
                    delete files[i];
                }
            }

            var uploadInst = upload.render({
                elem: '#test1' //绑定元素
                ,url: '/api/files/uploadBanner' //上传接口
                ,multiple: false // 不允许多文件上传
                ,method: 'post'
                ,auto: false //选择文件后不自动上传
                ,bindAction: '#insert' //绑定表单提交的按钮 触发上传请求
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

                        // 如果没有图片，因为上传绑定了添加按钮，点击添加时，则会触发文件上传，此时不走form.on中的代码
                        // 上传完毕，将图片结果写入表单中一个新控件，重新渲染，走下面的代码，校验合格后，发送ajax请求执行添加
                        $("#addimg").append('<input type="hidden" value="'+res.data+'" name="img" id="toinsert">');
                        form.render(); //更新全部
                        // 这里需要使用new FormData()来获取表单中的控件
                        var formData = new FormData(document.getElementById("formData"));
                        form.render(); //更新全部

                        $.ajax({
                            url: "/api/goods/insert",
                            type: "POST",
                            data: formData,
                            processData: false, // 禁止序列化data，默认为true
                            contentType: false, // 避免jquery对contentType做操作
                            success: function (data) {
                                if(data.message === "success"){
                                    layer.msg("添加成功！",{icon:1,anim:4,time:2000},function(){
                                        $("#formData")[0].reset();
                                        clearFile();
                                        $("#previewImg").removeAttr("src");
                                        $("#previewImg").css("display","none");
                                        $("#toinsert").remove();
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

            form.on('submit(*)',function(){
                if ($("#previewImg").attr("src") === undefined) {
                    layer.msg("你未完成必选项，添加商品失败，请重试！", {icon: 5, shade: 0.4, time: 1000});
                    form.init;
                }
            });

            $.ajax({
                type: "POST",
                url: "/api/goodstypes/selectAll",
                success: function(data){
                    var arr = data.data;
                    var str="<option value=''>请选择分类</option>";
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
                    var str="<option value=''>请选择内存大小</option>";
                    for(var i=0;i<arr.length;i++){
                        str=str+"<option value='"+arr[i].id+"'>"+arr[i].name+"</option>";
                    }
                    $("#memory").html(str);
                    form.render();
                }
            });
        });
    });
</script>
</body>
</html>
