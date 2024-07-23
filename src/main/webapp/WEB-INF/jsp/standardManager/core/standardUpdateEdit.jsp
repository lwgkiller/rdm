<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>新增</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <style>
        html, body {
            overflow: hidden;
            height: 100%;
            width: 100%;
            padding: 0;
            margin: 0;
        }
        .table-detail > tbody > tr > td {
            border: 1px solid #eee;
            text-align: center;
            background-color: #fff;
            white-space: normal;
            word-break: break-all;
            color: rgb(85, 85, 85);
            font-weight: normal;
            padding: 4px;
            height: 40px;
            min-height: 40px;
            box-sizing: border-box;
            font-size: 15px;
        }
        .mini-grid-row
        {
            height:40px;
        }
    </style>
</head>
<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveBtn" class="mini-button" style="display: none" onclick="saveGzjl()">保存</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 80%;">
        <form id="formGzjl" method="post">
            <input id="updateId" name="updateId" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 15%">更正人：</td>
                    <td >
                        <input id="userId" name="userId" textname="userName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: auto;height: 250px ">更正记录：</td>
                    <td colspan="7">
                        <div style="margin-top: 20px;margin-bottom: 2px">
                            <a id="removeZj" style="margin-right: 5px;display: none" class="mini-button" onclick="removeGzjl()">删除</a>
                        </div>
                        <div id="linkListGrid" class="mini-datagrid" style="width: 100%; height: 250px"
                             idField="id" autoload="true"
                             url="${ctxPath}/gzjl/getGzjlDetailList.do?belongId=${updateId}" showPager="false"
                             allowCellEdit="true" allowCellSelect="true" multiSelect="true"   allowAlternating="true">
                            <div property="columns">
                                <div type="checkcolumn" width="20"></div>
                                <div type="indexcolumn" align="center" width="30">序号</div>
                                <div field="updateNote" width="80" headerAlign="center" align="center">更改字段</div>
                                <div field="oldNote" width="80" headerAlign="center" dateFormat="yyyy-MM-dd HH:mm:ss" align="center">原记录</div>
                                <div field="newNote" width="80" headerAlign="center" dateFormat="yyyy-MM-dd HH:mm:ss" align="center">现记录</div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="width: 15%">更正原因：</td>
                    <td>
                       <textarea id="why" name="why" class="mini-textarea rxc"
                                 plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;"
                                 label="说明" datatype="varchar" allowinput="true"
                                 emptytext="请输入更正原因..." mwidth="80" wunit="%" mheight="20000" hunit="px"></textarea>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var status = "${status}";
    var action = "${action}";
    var jsUseCtxPath = "${ctxPath}";
    var updateId = "${updateId}";
    var formGzjl = new mini.Form("#formGzjl");
    var linkListGrid = mini.get("linkListGrid");
    var selectStandardWindow = mini.get("selectStandardWindow");
    var standardListGrid=mini.get("standardListGrid");
    var currentUserMainDepName = "${currentUserMainDepName}";
    var currentUserId = "${currentUserId}";

    $(function () {
        if (updateId) {
            var url = jsUseCtxPath + "/gzjl/getGzjlBaseInfo.do";
            $.post(
                url,
                {updateId: updateId},
                function (json) {
                    formGzjl.setData(json);
                });
        }
        //明细入口
        if (action == 'detail') {
            formGzjl.setEnabled(false);
            linkListGrid.setAllowCellEdit(false);
        }else if(action == 'add'||action == 'edit'){
            linkListGrid.setAllowCellEdit(false);
            formGzjl.setEnabled(false);
            mini.get("why").setEnabled(true);
            $("#saveBtn").show();
        }
    });

    


    //检验表单是否必填
    function valfirstIdSzh() {
        var why = $.trim(mini.get("why").getValue())
        if (!why) {
            return {"result": false, "message": "请填写更正原因"};
        }

        return {"result": true};
    }


    //保存
    function saveGzjl() {
        var formValfirstId = valfirstIdSzh();
        if (!formValfirstId.result) {
            mini.alert(formValfirstId.message);
            return;
        }
        var formData = _GetFormJsonMini("formGzjl");
        if(linkListGrid.getChanges().length > 0){
            formData.ryzj=linkListGrid.getChanges();
        }

        $.ajax({
            url: jsUseCtxPath + '/gzjl/saveGzjlDetail.do',
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (returnObj) {
                if (returnObj) {
                    var message = "";
                    if (returnObj.success) {
                        message = "保存成功";
                        mini.alert(message, "提示信息", function () {
                            window.location.href = jsUseCtxPath + "/gzjl/editPage.do?action=edit&updateId=" + returnObj.data;
                        });
                    } else {
                        message = "保存成功，" + returnObj.message;
                        mini.alert(message, "提示信息");
                    }
                }
            }
        });
    }
    


    function removeGzjl() {
        var selecteds = linkListGrid.getSelecteds();
        var deleteArr = [];
        for (var i = 0; i < selecteds.length; i++) {
            var row = selecteds[i];
            deleteArr.push(row);
        }
        linkListGrid.removeRows(deleteArr);
    }

    
    
</script>
</body>
</html>