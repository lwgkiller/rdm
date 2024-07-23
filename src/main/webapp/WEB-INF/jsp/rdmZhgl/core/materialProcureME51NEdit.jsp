<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>物料采购申请</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div id="toolBar" class="topToolBar">
    <div>
        <span style="color: red;font-size: large">编辑完成后，请先点击保存，再进行相应操作</span>
        <a id="saveBtn" class="mini-button" onclick="saveApply()">保存</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
        <a id="createToSap" class="mini-button btn-red" style="margin-right: 5px" onclick="createToSap()">创建至SAP</a>
        <a id="sendToSap" class="mini-button btn-red" style="margin-right: 5px" onclick="sendToSap()">更新至SAP</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 80%;">
        <form id="applyForm" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="operation" name="operation" class="mini-hidden"/>
            <input id="result" name="result" class="mini-hidden"/>
            <input id="message" name="message" class="mini-hidden"/>
            <fieldset id="fdBaseInfo">
                <legend>
                    <label style="font-size:17px">
                        基本信息
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail grey" cellspacing="1" cellpadding="0">
                        <tr>
                            <td style="text-align: center;width: 20%">申请人：</td>
                            <td style="min-width:170px">
                                <input id="applyUserName" name="applyUserName" readonly class="mini-textbox" style="width:98%;"/>
                            </td>
                            <td style="text-align: center;width: 20%">申请部门：</td>
                            <td style="min-width:170px">
                                <input id="applyDeptName" name="applyDeptName" readonly class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;width: 20%">物料采购申请单号：</td>
                            <td style="min-width:170px">
                                <input id="applyNo" name="applyNo" readonly class="mini-textbox" style="width:98%;"/>
                            </td>
                            <td style="text-align: center;width: 20%">业务状态：</td>
                            <td style="min-width:170px">
                                <input id="businessStatus" name="businessStatus" readonly class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;width: 20%">SAP是否调用成功：</td>
                            <td style="min-width:170px">
                                <input id="result" name="result" readonly class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td align="center" style="white-space: nowrap;">
                                SAP调用返回消息：
                            </td>
                            <td colspan="3">
						        <textarea id="message" name="message" class="mini-textarea rxc" plugins="mini-textarea"
                                          style="width:99%;height:100px;line-height:25px;" required
                                          label="SAP调用返回消息" datatype="varchar" length="500" vtype="length:500" minlen="0"
                                          allowinput="true" readonly
                                          mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                            </td>
                        </tr>
                        <tr>
                            <td align="center" style="white-space: nowrap;">
                                领用事由：
                            </td>
                            <td colspan="3">
						        <textarea id="reasonForUse" name="reasonForUse" class="mini-textarea rxc" plugins="mini-textarea"
                                          style="width:99%;height:100px;line-height:25px;" required
                                          label="领用事由" datatype="varchar" length="500" vtype="length:500" minlen="0"
                                          allowinput="true"
                                          mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                            </td>
                        </tr>
                    </table>
                </div>
            </fieldset>
            <fieldset id="fdTestProblem">
                <legend>
                    <label style="font-size:17px">
                        物料信息
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <div class="mini-toolbar" id="problemButtons">
                        <a class="mini-button" id="addDetailButton" plain="true" onclick="addDetail()">添加</a>
                        <a class="mini-button btn-red" id="delDetailButton" plain="true" onclick="delDetail()">删除</a>
                    </div>
                    <div id="grid_detail" class="mini-datagrid" allowResize="false" style="margin-top: 5px" allowRowSelect="true"
                         enableHotTrack="false"
                         idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false" cellEditAction="celldblclick"
                         onlyCheckSelection="true"
                         multiSelect="true" showColumnsMenu="false" showPager="false" allowCellWrap="true"
                         showVGridLines="true" autoload="true" url="${ctxPath}/rdmZhgl/core/materialProcureME51N/getItemList.do?mainId=${id}">
                        <div property="columns">
                            <div type="checkcolumn" width="25"></div>
                            <div type="indexcolumn" headerAlign="center" align="center" width="30">序号</div>
                            <div field="itemNo" headerAlign="center" align="center" width="40">行项目号</div>
                            <div field="operation" headerAlign="center" align="center" width="40">指令</div>
                            <div field="isDelete" headerAlign="center" align="center" width="40" renderer="isDeleteRenderer">删除标记</div>
                            <div field="materialCode" headerAlign="center" align="center" width="100">物料号
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="purcureCount" headerAlign="center" align="center" width="80">申请数量
                                <input property="editor" class="mini-spinner" allowLimitValue="false" allowNull="false"/>
                            </div>
                            <div field="factoryNumber" headerAlign="center" align="center" width="100">工厂代码</div>
                            <div field="purcureOrg" headerAlign="center" align="center" width="100">采购组织</div>
                        </div>
                    </div>
                </div>
            </fieldset>
        </form>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var grid_detail = mini.get("grid_detail");
    var applyForm = new mini.Form("#applyForm");
    var id = "${id}";
    //..
    $(function () {
        var url = jsUseCtxPath + "/rdmZhgl/core/materialProcureME51N/getDetail.do";
        $.post(
            url,
            {id: id},
            function (json) {
                applyForm.setData(json);
                if (mini.get("businessStatus").getValue() == "新增待创建") {
                    mini.get("createToSap").setVisible(true);
                } else {
                    mini.get("createToSap").setVisible(false);
                }
                if (mini.get("businessStatus").getValue() == "修改待推送") {
                    mini.get("sendToSap").setVisible(true);
                } else {
                    mini.get("sendToSap").setVisible(false);
                }
                //明细入口
                if (action == 'detail') {
                    mini.get("saveBtn").setEnabled(false);
                    mini.get("createToSap").setVisible(false);
                    mini.get("sendToSap").setVisible(false);
                    mini.get("addDetailButton").setEnabled(false);
                    mini.get("delDetailButton").setEnabled(false);
                } else if (action == 'edit') {
                    mini.get("saveBtn").setEnabled(true);
                    mini.get("addDetailButton").setEnabled(true);
                    mini.get("delDetailButton").setEnabled(true);
                } else if (action == 'add') {
                    mini.get("saveBtn").setEnabled(true);
                    mini.get("addDetailButton").setEnabled(true);
                    mini.get("delDetailButton").setEnabled(true);
                }
            });
        grid_detail.on("cellbeginedit", function (e) {
            if (e.record.isDelete == "true") {
                e.editor.setEnabled(false);
            } else if (e.record.itemNo && e.field == "materialCode") {
                e.editor.setEnabled(false);
            } else {
                e.editor.setEnabled(true);
            }
        });
    });
    //..
    function addDetail() {
        var row = {};
        row.factoryNumber = "2080";
        row.purcureOrg = "2080";
        row.isDelete = "false";
        row.operation = "";
        row.itemNo = "";
        grid_detail.addRow(row);
    }
    //..
    function delDetail() {
        var selecteds = grid_detail.getSelecteds();
        var deleteArr = [];
        if (!mini.get("businessStatus").getValue() || mini.get("businessStatus").getValue() == "新增待创建") {//此状态下直接删除
            for (var i = 0; i < selecteds.length; i++) {
                var row = selecteds[i];
                deleteArr.push(row);
            }
            grid_detail.removeRows(deleteArr);
        } else {//其余状态打删除标记，但是指令为ADD的是新增的明细未传输的，也可以直接删除
            for (var i = 0; i < selecteds.length; i++) {
                var row = selecteds[i];
                if (row.id && row.operation != "ADD") {
                    grid_detail.updateRow(row, {"isDelete": "true"});
                } else {
                    grid_detail.removeRow(row);
                }
            }
        }
    }
    //..
    function saveValidCheck() {
        var reasonForUse = $.trim(mini.get("reasonForUse").getValue())
        if (!reasonForUse) {
            return {"result": false, "message": "请填写领用事由"};
        }
        var detail = grid_detail.getData();
        if (detail.length < 1) {
            return {"result": false, "message": "请添加物料信息"};
        } else {
            for (var i = 0; i < detail.length; i++) {
                if (detail[i].materialCode == undefined || detail[i].materialCode == "") {
                    return {"result": false, "message": "请填写物料号"};
                }
                if (detail[i].purcureCount == undefined || detail[i].purcureCount == 0) {
                    return {"result": false, "message": "请填写申请数量"};
                }
            }
        }
        var alreadySet = new Set();
        for (var i = 0; i < detail.length; i++) {
            alreadySet.add(detail[i].itemNo + "-" + detail[i].materialCode);
        }
        if (alreadySet.size != detail.length) {
            return {"result": false, "message": "物料和行项目号的组合不允许出现重复"};
        }

        return {"result": true};
    }
    //..
    function saveApply() {
        var checkResult = saveValidCheck();
        if (!checkResult.result) {
            mini.alert(checkResult.message);
            return;
        }
        var formData = _GetFormJsonMini("applyForm");
        formData.itemsChangeData = grid_detail.getChanges();
        $.ajax({
            url: jsUseCtxPath + '/rdmZhgl/core/materialProcureME51N/saveApply.do?',
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (returnObj) {
                if (returnObj) {
                    var message = "";
                    if (returnObj.success) {
                        message = "数据保存成功";
                        mini.alert(message, "提示信息", function () {
                            window.location.href = jsUseCtxPath + "/rdmZhgl/core/materialProcureME51N/editPage.do?action=edit&id=" + returnObj.data;
                        });
                    } else {
                        message = "数据保存失败，" + returnObj.message;
                        mini.alert(message, "提示信息");
                    }
                }
            }
        });
    }
    //..创建至SAP
    function createToSap() {
        var row = applyForm.getData();
        var itemsChangeData = grid_detail.getChanges();
        if (itemsChangeData.length > 0) {
            mini.alert("明细信息有修改，请点击保存后再创建！");
            return;
        }
        mini.confirm("确定创建选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/rdmZhgl/core/materialProcureME51N/createToSap.do",
                    method: 'POST',
                    data: row,
                    postJson: true,
                    showMsg: false,
                    success: function (returnData) {
                        if (returnData.success) {
                            mini.alert(returnData.message, "提示信息", function () {
                                window.location.href = jsUseCtxPath + "/rdmZhgl/core/materialProcureME51N/editPage.do?action=edit&id=" + id;
                            });
                        } else {
                            mini.alert("同步失败:" + returnData.message, "提示信息");
                        }
                    },
                    fail: function (returnData) {
                        mini.alert("同步失败:" + returnData.message, "提示信息");
                    }
                });
            }
        });
    }
    //..更新至SAP
    function sendToSap() {
        var row = applyForm.getData();
        var itemsChangeData = grid_detail.getChanges();
        if (itemsChangeData.length > 0) {
            mini.alert("明细信息有修改，请点击保存后再更新！");
            return;
        }
        mini.confirm("确定推送选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/rdmZhgl/core/materialProcureME51N/sendToSap.do",
                    method: 'POST',
                    data: row,
                    postJson: true,
                    showMsg: false,
                    success: function (returnData) {
                        if (returnData.success) {
                            mini.alert(returnData.message, "提示信息", function () {
                                window.location.href = jsUseCtxPath + "/rdmZhgl/core/materialProcureME51N/editPage.do?action=edit&id=" + id;
                            });
                        } else {
                            mini.alert("同步失败:" + returnData.message, "提示信息");
                        }
                    },
                    fail: function (returnData) {
                        mini.alert("同步失败:" + returnData.message, "提示信息");
                    }
                });
            }
        });
    }
    //..
    function resultRenderer(e) {
        var record = e.record;
        var status = record.result;
        var arr = [
            {'key': 'S', 'value': '成功', 'css': 'green'},
            {'key': 'E', 'value': '失败', 'css': 'red'}
        ];
        return $.formatItemValue(arr, status);
    }
    //..
    function isDeleteRenderer(e) {
        var record = e.record;
        var status = record.isDelete;
        var arr = [
            {'key': 'true', 'value': '是', 'css': 'red'},
            {'key': 'false', 'value': '否', 'css': 'green'}
        ];
        return $.formatItemValue(arr, status);
    }
</script>
</body>
</html>
