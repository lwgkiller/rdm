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

        .mini-grid-row {
            height: 40px;
        }
    </style>
</head>
<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveBtn" class="mini-button" style="display: none" onclick="saveMsg()">发送</a>
        <a id="linkBtn" class="mini-button" style="display: none" onclick="doBinding()">确认关联</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 80%;">
        <form id="formMsg" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="mainId" name="mainId" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 15%">接收人：</td>
                    <td>
                        <input id="recId" name="recId" textname="recName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 15%">消息内容：</td>
                    <td>
                       <textarea id="content" name="content" class="mini-textarea rxc"
                                 plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;"
                                 label="说明" datatype="varchar" allowinput="true"
                                 emptytext="请输入消息内容..." mwidth="80" wunit="%" mheight="20000" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="width: auto;height: 350px ">待关联标准表：</td>
                    <td colspan="7">
                        <div style="margin-top: 20px;margin-bottom: 2px">
                            <a id="editMsgZj" class="mini-button " style="margin-right: 5px;display: none" plain="true"
                               onclick="addMsgItem()">新增</a>
                            <a id="removeZj" style="margin-right: 5px;display: none" class="mini-button" onclick="removeMsg()">删除</a>
                        </div>
                        <div id="linkListGrid" class="mini-datagrid" style="width: 100%; height: 400px"
                             idField="id" autoload="true" oncellbeginedit="OnCellBeginEditTime"
                             url="${ctxPath}/componentTest/core/kanban/queryBindingStandardMsgItems.do?msgId=${id}" showPager="false"
                             allowCellEdit="true" allowCellSelect="true" multiSelect="true" allowAlternating="true">
                            <div property="columns">
                                <div type="checkcolumn" width="20"></div>
                                <div type="indexcolumn" align="center" width="30">序号</div>
                                <div field="defectStandardNumber" width="80" headerAlign="center" align="center">缺失标准编号
                                    <input property="editor" class="mini-textbox"/></div>
                                <div field="defectStandardName" width="80" headerAlign="center" align="center">缺失标准名称
                                    <input property="editor" class="mini-textbox"/></div>
                                <div field="standardId" displayField="standardNumber" width="80" headerAlign="center" align="center">关联标准
                                    <input style="width:98%;" property="editor" class="mini-buttonedit" showClose="true"
                                           oncloseclick="onSelectStandardCloseClick" allowInput="false"
                                           onbuttonclick="selectStandard()"/>
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<div id="selectStandardWindow" title="选择标准" class="mini-window" style="width:750px;height:450px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <input id="parentInputScene" style="display: none"/>
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span style="font-size: 14px;color: #777">标准编号: </span>
        <input class="mini-textbox" width="130" id="filterStandardNumberId" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">标准名称: </span>
        <input class="mini-textbox" width="130" id="filterStandardNameId" style="margin-right: 15px"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchStandard()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="standardListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onrowdblclick="onRowDblClick"
             idField="id" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20"
             allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/standardManager/core/standard/queryList.do"
        >
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div field="systemName" sortField="systemName" width="90" headerAlign="center" align="center"
                     allowSort="true">标准体系
                </div>
                <div field="categoryName" sortField="categoryName" width="60" headerAlign="center" align="center"
                     allowSort="true">标准类别
                </div>
                <div field="standardNumber" sortField="standardNumber" width="140" headerAlign="center" align="center"
                     align="center" allowSort="true">编号
                </div>
                <div field="standardName" sortField="standardName" width="180" headerAlign="center" align="center"
                     allowSort="true">名称
                </div>
                <div field="belongDepName" sortField="belongDepName" width="80" headerAlign="center" align="center"
                     allowSort="true">归口部门
                </div>
                <div field="standardStatus" sortField="standardStatus" width="60" headerAlign="center" align="center"
                     allowSort="true" renderer="statusRenderer">状态
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectStandardOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectStandardHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var action = "${action}";
    var id = "${id}";
    var mainId = "${mainId}";
    var formMsg = new mini.Form("#formMsg");
    var linkListGrid = mini.get("linkListGrid");
    var selectStandardWindow = mini.get("selectStandardWindow");
    var standardListGrid = mini.get("standardListGrid");

    //..
    $(function () {
        mini.get("mainId").setValue(mainId);
        if (id) {
            var url = jsUseCtxPath + "/componentTest/core/kanban/getBindingStandardMsg.do";
            $.post(
                url,
                {id: id},
                function (json) {
                    formMsg.setData(json);
                });
        }
        //明细入口
        if (action == 'detail') {
            formMsg.setEnabled(false);
            linkListGrid.setAllowCellEdit(false);
        } else if (action == 'add') {
            $("#saveBtn").show();
            $("#editMsgZj").show();
            $("#removeZj").show();
        } else if (action == 'edit') {
            formMsg.setEnabled(false);
            $("#linkBtn").show();
        }
    });

    //..
    function saveMsg() {
        var formValfirstId = valfirstIdSzh();
        if (!formValfirstId.result) {
            mini.alert(formValfirstId.message);
            return;
        }
        var formData = _GetFormJsonMini("formMsg");
        if (linkListGrid.getChanges().length > 0) {
            formData.subListChanges = linkListGrid.getChanges();
        }

        $.ajax({
            url: jsUseCtxPath + '/componentTest/core/kanban/saveBindingStandardMsg.do',
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (returnObj) {
                if (returnObj) {
                    var message = "";
                    if (returnObj.success) {
                        message = "发送成功";
                        mini.alert(message, "提示信息", function () {
                            window.location.href = jsUseCtxPath +
                                "/componentTest/core/kanban/editBindingStandardMsgPage.do?&action=edit&id=" + returnObj.data;
                        });
                    } else {
                        message = "发送失败，" + returnObj.message;
                        mini.alert(message, "提示信息");
                    }
                }
            }
        });
    }

    //..
    function doBinding() {
        var detail = linkListGrid.getData();
        var ids = [];
        for (var i = 0; i < detail.length; i++) {
            if (detail[i].standardId != undefined && detail[i].standardId != "") {
                ids.push(detail[i].standardId);
            }
        }
        var formData = _GetFormJsonMini("formMsg");
        if (linkListGrid.getChanges().length > 0) {
            formData.subListChanges = linkListGrid.getChanges();
        }

        $.ajax({
            url: jsUseCtxPath + '/componentTest/core/kanban/saveBindingStandardMsg.do',
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (returnObj) {
                if (returnObj) {
                    var message = "";
                    if (returnObj.success) {
                        message = "关联成功";
                        mini.alert(message, "提示信息", function (action) {
                            if (action == 'ok') {
                                CloseWindow();
                            }
                        });
                    } else {
                        message = "发送失败，" + returnObj.message;
                        mini.alert(message, "提示信息");
                    }
                }
            }
        });
    }

    //..
    function addMsgItem() {
        var row = {};
        linkListGrid.addRow(row);
    }

    //..
    function removeMsg() {
        var selecteds = linkListGrid.getSelecteds();
        var deleteArr = [];
        for (var i = 0; i < selecteds.length; i++) {
            var row = selecteds[i];
            deleteArr.push(row);
        }
        linkListGrid.removeRows(deleteArr);
    }

    //..
    function selectStandard(inputScene) {
        $("#parentInputScene").val(inputScene);
        selectStandardWindow.show();
        searchStandard();
    }

    //..
    function searchStandard() {
        var queryParam = [];
        //其他筛选条件
        var standardNumber = $.trim(mini.get("filterStandardNumberId").getValue());
        if (standardNumber) {
            queryParam.push({name: "standardNumber", value: standardNumber});
        }
        var standardName = $.trim(mini.get("filterStandardNameId").getValue());
        if (standardName) {
            queryParam.push({name: "standardName", value: standardName});
        }
        queryParam.push({name: "systemCategoryId", value: "JS"});
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = standardListGrid.getPageIndex();
        data.pageSize = standardListGrid.getPageSize();
        data.sortField = standardListGrid.getSortField();
        data.sortOrder = standardListGrid.getSortOrder();
        //查询
        standardListGrid.load(data);
    }

    //..
    function onRowDblClick() {
        selectStandardOK();
    }

    //..
    function selectStandardOK() {
        var selectRow = standardListGrid.getSelected();
        var row = linkListGrid.getSelected();
        if (selectRow) {
            linkListGrid.updateRow(row, {standardId: selectRow.id, standardNumber: selectRow.standardNumber});
        }
        selectStandardHide();
    }

    //..
    function selectStandardHide() {
        selectStandardWindow.hide();
        mini.get("filterStandardNumberId").setValue('');
        mini.get("filterStandardNameId").setValue('');
    }

    //..
    function onSelectStandardCloseClick(e) {
        var row = linkListGrid.getSelected();
        linkListGrid.updateRow(row, {standardId: "", standardNumber: ""});
        this.setText("");
        this.setValue("");
        var test = linkListGrid.getData();
    }

    //..按状态控制可编辑列
    function OnCellBeginEditTime(e) {
        var record = e.record, field = e.field;
        if (field == "standardId" && action == "add") {
            e.cancel = true;
        } else if (field == "standardId" && action == "edit") {
            e.cancel = false;
        } else if ((field == "defectStandardName" || field == "defectStandardNumber") && action == "edit") {
            e.cancel = true;
        }
    }

    //..验证
    function valfirstIdSzh() {
        var recId = $.trim(mini.get("recId").getValue())
        if (!recId) {
            return {"result": false, "message": "请选择接收人"};
        }
        var detail = linkListGrid.getData();
        if (detail.length < 1) {
            return {"result": false, "message": "请填写待关联标准表"};
        }
        if (detail.length > 0) {
            for (var i = 0; i < detail.length; i++) {
                if (detail[i].defectStandardNumber == undefined || detail[i].defectStandardNumber == "") {
                    return {"result": false, "message": "请填写标准编号"};
                } else if (detail[i].defectStandardName == undefined || detail[i].defectStandardName == "") {
                    return {"result": false, "message": "请填写标准名称"};
                }
            }
        }
        return {"result": true};
    }

    //..
    function statusRenderer(e) {
        var record = e.record;
        var status = record.standardStatus;

        var arr = [{'key': 'draft', 'value': '草稿', 'css': 'orange'},
            {'key': 'enable', 'value': '有效', 'css': 'green'},
            {'key': 'disable', 'value': '已废止', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }
</script>
</body>
</html>