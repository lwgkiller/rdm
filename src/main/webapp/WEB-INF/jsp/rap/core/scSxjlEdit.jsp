
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
        <a id="save" name="save" class="mini-button" onclick="saveSxjlSc()">保存</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 80%;">
        <form id="formRjbgSc" method="post">
            <input id="belongId" name="belongId" class="mini-hidden"/>
            <input id="scsxId" name="scsxId" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 20%;text-align: left">执行车号：</td>
                    <td style="width: 33%;min-width:170px;font-size:14pt">
                        <input id="scwjNo" name="scwjNo"  class="mini-textbox"  style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="width: 20%;text-align: left">发动机型号：</td>
                    <td>
                        <input id="scfdjModel" name="scfdjModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 20%;text-align: left">发动机编号：</td>
                    <td>
                        <input id="scfdjNo" name="scfdjNo" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 20%;text-align: left">发动机控制器型号：</td>
                    <td>
                        <input id="scfdjkzModel" name="scfdjkzModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 20%;text-align: left">发动机控制器编号：</td>
                    <td>
                        <input id="scfdjkzNo" name="scfdjkzNo" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 20%;text-align: left">刷写前软件版本号：</td>
                    <td>
                        <input id="scsxqNo" name="scsxqNo" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 20%;text-align: left">刷写后软件版本号：</td>
                    <td>
                        <input id="scsxhNo" name="scsxhNo" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 20%;text-align: left">刷写人姓名：</td>
                    <td>
                        <input id="scsxName" name="scsxName" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 20%;text-align: left">刷写人部门：</td>
                    <td>
                        <input id="scsxDep" name="scsxDep" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 20%;text-align: left">刷写时间：</td>
                    <td>
                        <input id="scsxTime" name="scsxTime" class="mini-datepicker"  format="yyyy-MM-dd" style="width:98%;"/>
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
    var scsxId = "${scsxId}";
    var formRjbgSc = new mini.Form("#formRjbgSc");
    var currentUserName="${currentUserName}";
    var currentUserId = "${currentUserId}";

    function validSx() {
        var scwjNo = $.trim(mini.get("scwjNo").getValue());
        if (!scwjNo) {
            return {"result": false, "message": "请完善刷写记录"};
        }
        var scfdjModel = $.trim(mini.get("scfdjModel").getValue());
        if (!scfdjModel) {
            return {"result": false, "message": "请完善刷写记录"};
        }
        var scfdjNo = $.trim(mini.get("scfdjNo").getValue());
        if (!scfdjNo) {
            return {"result": false, "message": "请完善刷写记录"};
        }
        var scfdjkzModel = $.trim(mini.get("scfdjkzModel").getValue());
        if (!scfdjkzModel) {
            return {"result": false, "message": "请完善刷写记录"};
        }
        var scfdjkzNo = $.trim(mini.get("scfdjkzNo").getValue());
        if (!scfdjkzNo) {
            return {"result": false, "message": "请完善刷写记录"};
        }
        var scsxqNo = $.trim(mini.get("scsxqNo").getValue());
        if (!scsxqNo) {
            return {"result": false, "message": "请完善刷写记录"};
        }
        var scsxhNo = $.trim(mini.get("scsxhNo").getValue());
        if (!scsxhNo) {
            return {"result": false, "message": "请完善刷写记录"};
        }
        var scsxName = $.trim(mini.get("scsxName").getValue());
        if (!scsxName) {
            return {"result": false, "message": "请完善刷写记录"};
        }
        var scsxDep = $.trim(mini.get("scsxDep").getValue());
        if (!scsxDep) {
            return {"result": false, "message": "请完善刷写记录"};
        }
        var scsxTime = $.trim(mini.get("scsxTime").getValue());
        if (!scsxTime) {
            return {"result": false, "message": "请完善刷写记录"};
        }
        return {"result": true};
    }


    function saveSxjlSc() {
        var formValid = validSx();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        var formData = new mini.Form("formRjbgSc");
        var data = formData.getData();
        var json = mini.encode(data);
        $.ajax({
            url: jsUseCtxPath + '/environment/core/Rjbg/saveScsx.do?belongId='+belongId,
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
        if (scsxId) {
            var url = jsUseCtxPath + "/environment/core/Rjbg/getRjbgScDetail.do";
            $.post(
                url,
                {scsxId: scsxId},
                function (json) {
                    formRjbgSc.setData(json);
                });
        }
        if (action == "detail") {
            formRjbgSc.setEnabled(false);
        }
    });
    
</script>
</body>
</html>