<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2019/8/7
  Time: 16:40
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.4.1.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.easyui.min.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/themes/black/easyui.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/themes/icon.css" type="text/css">

<script type="text/javascript">
    var MaskUtil = (function () {
        var $mask, $maskMsg;
        var defMsg = '正在处理，请稍等。。。';
        function init() {
            if (!$mask) {
                $mask = $("<div class=\"datagrid-mask mymask\"></div>").appendTo("body");
            }
            if (!$maskMsg) {
                $maskMsg = $("<div class=\"datagrid-mask-msg mymask\">" + defMsg + "</div>")
                    .appendTo("body").css({'font-size': '12px'});
            }
            $mask.css({width: "100%", height: $(document).height()});

            var scrollTop = $(document.body).scrollTop();

            $maskMsg.css({
                left: ($(document.body).outerWidth(true) - 190) / 2
                , top: (($(window).height() - 45) / 2) + scrollTop
                , height: "40px"
            });
        }
        return {
            mask: function (msg) {
                init();
                $mask.show();
                $maskMsg.html(msg || defMsg).show();
            }
            , unmask: function () {
                $mask.hide();
                $maskMsg.hide();
            }
        }
    }());
</script>