<%--
  Created by IntelliJ IDEA.
  User: matianyu
  Date: 2021/2/23
  Time: 14:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>新增</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <style>
        html, body {
            overflow: hidden;
            height: 100%;
            width: 100%;
            padding: 0;
            margin: 0;
        }

    </style>
</head>
<body>
<div id="changeToolBar" class="topToolBar">
    <div>
        <a id="saveChange" class="mini-button" onclick="saveNew()">保存</a>
        <a class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 80%;">
        <form id="formTool" method="post">
            <input id="toolid" name="toolid" class="mini-hidden"/>
            <input id="type" name="type" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 20%;text-align: center;font-size:14pt">名称：<span></span></td>
                    <td>
                        <input id="name" name="name" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 20%;text-align: center;font-size:14pt">大小：</td>
                    <td>
                        <input id="size" name="size" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                    <td style="border-right-style:none"></td>
                <tr>
                    <td style="width: 20%;text-align: center;font-size:14pt">用途说明：</td>
                    <td colspan="3">
                        <input id="usedirection" name="usedirection" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <td style="border-right-style:none"></td>
                <tr>
                    <td style="width: 20%;text-align: center;font-size:14pt">适用人员（部门）：</td>
                    <td>
                        <input id="applicable" name="applicable" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 20%;text-align: center;font-size:14pt">归口人（部门）：</td>
                    <td>
                        <input id="reperson" name="reperson" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <td style="border-right-style:none"></td>
                <tr>
                    <td style="width: 20%;text-align: center;font-size:14pt">下载地址：</td>
                    <td colspan="3">
                        <input id="download" name="download" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var action="${action}";
    var jsUseCtxPath = "${ctxPath}";
    var toolid = "${toolid}";
    var formTool = new mini.Form("#formTool");
    var nodeVarsStr='${nodeVars}';
    var currentUserName="${currentUserName}";
    var currentTime="${currentTime}";
    $(function () {
        if(toolid) {
            var url = jsUseCtxPath + "/researchTool/tool/getDetail.do";
            $.post(
                url,
                {toolid: toolid},
                function (json) {
                    formTool.setData(json);
                });
        }
    });
    function saveNew() {
        var formData =new mini.Form("formTool");
        var data = formData.getData();
        var json = mini.encode(data);
        $.ajax({
            url: jsUseCtxPath + '/researchTool/tool/saveTool.do',
            type:'POST',
            data: json,
            async: false,
            contentType: 'application/json',
            success: function (data) {
                if(data) {
                    mini.alert(data.message,"提示消息",function (action) {
                        if(action=='ok') {
                            CloseWindow();
                        }
                    });
                }
            }
        });
    }

</script>
</body>
</html>