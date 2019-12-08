<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2019/8/6
  Time: 14:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>加班工时计算</title>
    <%@ include file="common.jsp" %>
</head>

<body class="easyui-layout" style="background:url('${pageContext.request.contextPath }/img/ssmbackground.jpg') no-repeat;height:100%;width:100%;overflow: hidden;">
<div id="w" class="easyui-window" title="计算完成后请在本软件所在文件夹查看Excel文件" collapsible="true"
     minimizable="false" maximizable="false" icon="icon-save" draggable="true" resizable="false"
     style="width: 450px; height: 330px; padding: 30px; background: #fafafa;"
     data-options="closable:false,draggable:false">
    <form id="main-form" method="post" enctype="multipart/form-data" action='${pageContext.request.contextPath}/count' novalidate>

        <!-- menulogin -->
        <div style="margin: 30px 0px;">
            <label>
                <input class="easyui-filebox"
                       id="file"
                       name="file"
                       iconWidth="28"
                       prompt="上传需计算文件"
                       data-options="label:'文件:',required:true,accept:'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel'"
                       missingMessage="不能为空"
                       style="width: 300px; height: 34px; padding: 10px;">
            </label>
        </div>

    </form>
    <div style="text-align: center; padding: 5px 0">
        <a href="javascript:void(0)" id="upload-btn"
           class="easyui-linkbutton"  style="width: 100px" data-options="iconCls:'icon-edit'">计算</a>
        <a href="javascript:void(0)" class="easyui-linkbutton"
           id="download-btn" style="width: 100px" data-options="iconCls:'icon-save'">下载模板</a>
    </div>


</div>
<script>

    $(function () {
        <%--if ("${msg }" !== "") {--%>
        <%--    //$.messager.alert('提示',"${msg }");--%>
        <%--    var showmsg = "${msg }";--%>
        <%--    $.messager.show({--%>
        <%--        title: '提示',--%>
        <%--        msg: showmsg--%>
        <%--    });--%>
        <%--}--%>

        /** 给登录按钮绑定点击事件  */
        $("#upload-btn").on("click", function () {

            if ($("#main-form").form('validate')) {
                // $("#main-form").submit();
                MaskUtil.mask();
                var data = new FormData($("#main-form")[0]);
                $.ajax({
                    url:'${pageContext.request.contextPath}/count',
                    type:'post',
                    cache:false,//cache设置为false，上传文件不需要缓存
                    processData:false,//processData设置为false。因为data值是FormData对象，不需要对数据做处理
                    contentType:false,//contentType设置为false。因为是由<form>表单构造的FormData对象，且已经声明了属性enctype="multipart/form-data"，所以这里设置为false
                    data:data,
                    error:function(){
                        MaskUtil.unmask();
                        $.messager.alert('提示','请求失败!')
                    },
                    success:function(res){
                        MaskUtil.unmask();
                        console.log(res);
                        var showmsg = res.msg;
                        if(res.code===0){
                            $.messager.show({
                                title: '提示',
                                msg: showmsg
                            });
                            $('#main-form').form('clear');
                        }else{
                            $.messager.show({
                                title: '提示',
                                msg: showmsg
                            });
                        }
                    }
                });
            }

        });
        //相应输入框的回车键
        /* $('#main-form').find('input').on('keyup',function(event){
            if(event.keyCode=='13'){
                $('#main-form').submit();
            }
        }) */

        /** 按了回车键 */
        $(document).keydown(function (event) {
            if (event.keyCode === 13) {
                $("#upload-btn").trigger("click");
            }
        });

        $('#download-btn').on("click", function () {
            var url = '${pageContext.request.contextPath}/download';
            // console.log(url);
            // var $form = $("<form id='download' class='hidden' method='post'></form>");
            // $form.attr("url",url);
            // $('body').append($form);
            // $form.submit();
            window.location.href=url;
        });

    });



    function clearForm() {
        $('#main-form').form('clear');
    };

    $('#captcha').click(function () {
        $('#captcha').attr('src',$('#captcha').attr('src')+"?time="+new Date().getTime());
    });
</script>
</body>
</html>
