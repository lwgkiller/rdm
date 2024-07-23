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
<div id="changeJpzlBar" class="topJpzlBar">
    <div>
        <a id="saveChange" class="mini-button" onclick="saveNew()">保存</a>
        <a class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 80%;">
        <form id="formJpzl" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 20%;text-align: center">目录名称：</td>
                    <td>
                        <input id="dirName" name="dirName" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 20%;text-align: center">备注说明：</td>
                    <td>
                        <input id="dirDesc" name="dirDesc" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 20%;text-align: center">归口人(部门)：</td>
                    <td colspan="3">
                        <input id="respUser" name="respUser" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 20%;text-align: center">下载地址：</td>
                    <td colspan="3">
                        <input id="dirLink" name="dirLink" class="mini-textbox" style="width:98%;"/>
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
    var id = "${id}";
    var formJpzl = new mini.Form("#formJpzl");
    var currentUserName="${currentUserName}";
    var currentTime="${currentTime}";
    $(function () {
        if(id) {
            var url = jsUseCtxPath + "/serviceEngineering/core/jpzl/getDetail.do";
            $.post(
                url,
                {id: id},
                function (json) {
                    formJpzl.setData(json);
                });
        }
    });
    function saveNew() {
        var formData =new mini.Form("formJpzl");
        var data = formData.getData();
        var json = mini.encode(data);
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/jpzl/saveJpzl.do',
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