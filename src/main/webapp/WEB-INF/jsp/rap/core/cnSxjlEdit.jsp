
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>新增</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
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
<div id="detailToolBar" class="topToolBar" >
    <div>
        <a id="save" name="save" class="mini-button"  onclick="saveSxjl()">保存</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 80%;">
        <form id="formRjbgCn" method="post">
            <input id="belongId" name="belongId" class="mini-hidden"/>
            <input id="cnsxId" name="cnsxId" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 20%;text-align: left">执行车号：</td>
                    <td style="width: 33%;min-width:170px;font-size:14pt">
                        <input id="cnwjNo" name="cnwjNo"  class="mini-textbox"  style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="width: 20%;text-align: left">发动机型号：</td>
                    <td>
                        <input id="cnfdjModel" name="cnfdjModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 20%;text-align: left">发动机编号：</td>
                    <td>
                        <input id="cnfdjNo" name="cnfdjNo" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 20%;text-align: left">发动机控制器型号：</td>
                    <td>
                        <input id="cnfdjkzModel" name="cnfdjkzModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 20%;text-align: left">发动机控制器编号：</td>
                    <td>
                        <input id="cnfdjkzNo" name="cnfdjkzNo" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 20%;text-align: left">刷写前软件版本号：</td>
                    <td>
                        <input id="cnsxqNo" name="cnsxqNo" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 20%;text-align: left">刷写后软件版本号：</td>
                    <td>
                        <input id="cnsxhNo" name="cnsxhNo" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 20%;text-align: left">刷写人姓名：</td>
                    <td>
                        <input id="cnsxName" name="cnsxName" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 20%;text-align: left">刷写人部门：</td>
                    <td>
                        <input id="cnsxDep" name="cnsxDep" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 20%;text-align: left">刷写时间：</td>
                    <td>
                        <input id="cnsxTime" name="cnsxTime" class="mini-datepicker"  format="yyyy-MM-dd" style="width:98%;"/>
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
    var belongId = "${belongId}";
    var cnsxId = "${cnsxId}";
    var formRjbgCn = new mini.Form("#formRjbgCn");
    var currentUserName="${currentUserName}";
    var currentUserId = "${currentUserId}";

    function validSx() {
        var cnwjNo = $.trim(mini.get("cnwjNo").getValue());
        if (!cnwjNo) {
            return {"result": false, "message": "请完善刷写记录"};
        }
        var cnfdjModel = $.trim(mini.get("cnfdjModel").getValue());
        if (!cnfdjModel) {
            return {"result": false, "message": "请完善刷写记录"};
        }
        var cnfdjNo = $.trim(mini.get("cnfdjNo").getValue());
        if (!cnfdjNo) {
            return {"result": false, "message": "请完善刷写记录"};
        }
        var cnfdjkzModel = $.trim(mini.get("cnfdjkzModel").getValue());
        if (!cnfdjkzModel) {
            return {"result": false, "message": "请完善刷写记录"};
        }
        var cnfdjkzNo = $.trim(mini.get("cnfdjkzNo").getValue());
        if (!cnfdjkzNo) {
            return {"result": false, "message": "请完善刷写记录"};
        }
        var cnsxqNo = $.trim(mini.get("cnsxqNo").getValue());
        if (!cnsxqNo) {
            return {"result": false, "message": "请完善刷写记录"};
        }
        var cnsxhNo = $.trim(mini.get("cnsxhNo").getValue());
        if (!cnsxhNo) {
            return {"result": false, "message": "请完善刷写记录"};
        }
        var cnsxName = $.trim(mini.get("cnsxName").getValue());
        if (!cnsxName) {
            return {"result": false, "message": "请完善刷写记录"};
        }
        var cnsxDep = $.trim(mini.get("cnsxDep").getValue());
        if (!cnsxDep) {
            return {"result": false, "message": "请完善刷写记录"};
        }
        var cnsxTime = $.trim(mini.get("cnsxTime").getValue());
        if (!cnsxTime) {
            return {"result": false, "message": "请完善刷写记录"};
        }
        return {"result": true};
    }

    function saveSxjl() {
        var formValid = validSx();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        var formData = new mini.Form("formRjbgCn");
        var data = formData.getData();
        var json = mini.encode(data);
        $.ajax({
            url: jsUseCtxPath + '/environment/core/Rjbg/saveCnsx.do?belongId='+belongId,
            type: 'POST',
            data: json,
            async: false,
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    mini.alert(data.message, "提示消息", function (action) {
                        if (action == 'ok') {
                            CloseWindow();
                        }
                    });
                }
            }
        });
    }
    $(function () {
        if (cnsxId) {
            var url = jsUseCtxPath + "/environment/core/Rjbg/getRjbgCnDetail.do";
            $.post(
                url,
                {cnsxId: cnsxId},
                function (json) {
                    formRjbgCn.setData(json);
                });
        }
        //变更入口
        if (action == "detail") {
            formRjbgCn.setEnabled(false);
        }
    });
</script>
</body>
</html>