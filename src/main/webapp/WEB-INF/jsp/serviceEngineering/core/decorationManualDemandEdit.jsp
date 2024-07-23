<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>装修手册需求申请</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()"><spring:message
                code="page.decorationManualDemandEdit.name"/></a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()"><spring:message code="page.decorationManualDemandEdit.name1"/></a>
        <a id="showManualfile" class="mini-button" onclick="showManualfile()"><spring:message code="page.decorationManualDemandEdit.name2"/></a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 80%;">
        <form id="formBusiness" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="applyUserId" name="applyUserId" class="mini-hidden"/>
            <input id="applyDepId" name="applyDepId" class="mini-hidden"/>
            <input id="businessStatus" name="businessStatus" class="mini-hidden"/>
            <input id="INST_ID_" name="INST_ID_" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold">
                    <spring:message code="page.decorationManualDemandEdit.name3"/>
                </caption>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualDemandEdit.name4"/>：</td>
                    <td style="min-width:170px">
                        <input id="busunessNo" name="busunessNo" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualDemandEdit.name5"/>:</td>
                    <td>
                        <input id="cpzgId" name="cpzgId" showclose="true" textname="cpzgName" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
                               length="50" maxlength="50" mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualDemandEdit.name6"/>：</td>
                    <td style="min-width:170px">
                        <input id="salesModel" name="salesModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualDemandEdit.name7"/>：</td>
                    <td style="min-width:170px">
                        <input id="designModel" name="designModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualDemandEdit.name8"/>：</td>
                    <td style="min-width:170px">
                        <input id="materialCode" name="materialCode" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualDemandEdit.name9"/>：</td>
                    <td>
                        <input id="salesArea" name="salesArea" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringDecorationManualSalesArea"
                               valueField="key" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualDemandEdit.name10"/>：</td>
                    <td style="min-width:170px">
                        <input id="salesCountry" name="salesCountry" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.decorationManualDemandEdit.name11"/>：</td>
                    <td colspan="3">
						<textarea id="configurationDescription" name="configurationDescription" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:98%;height:150px;line-height:25px;" label="" datatype="varchar" length="1000"
                                  vtype="length:1000" minlen="0" allowinput="true" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.decorationManualDemandEdit.name12"/>：</td>
                    <td style="min-width:170px">
                        <input id="applyTime" name="applyTime" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                               valueType="string" showTime="false" showOkButton="true" showClearButton="false" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.decorationManualDemandEdit.name13"/>：</td>
                    <td style="min-width:170px">
                        <input id="publishTime" name="publishTime" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                               valueType="string" showTime="false" showOkButton="false" showClearButton="false" style="width:98%;"
                               ondrawdate="onDrawDate"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualDemandEdit.name14"/>：</td>
                    <td style="min-width:170px">
                        <input id="applyUser" name="applyUser" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualDemandEdit.name15"/>：</td>
                    <td style="min-width:170px">
                        <input id="applyDep" name="applyDep" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.decorationManualDemandEdit.name16"/>：</td>
                    <td colspan="3">
						<textarea id="remark" name="remark" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:98%;height:150px;line-height:25px;" label="" datatype="varchar" length="640"
                                  vtype="length:640" minlen="0" allowinput="true" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <%--///////////////////////////////////////////////////--%>
                <tr>
                    <td style="text-align: center;height: 500px"><spring:message code="page.decorationManualDemandEdit.name17"/>：</td>
                    <td colspan="3">
                        <div class="mini-toolbar" id="itemButtons">
                            <a id="addItem" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="addItem"
                               enabled="false"><spring:message code="page.decorationManualDemandEdit.name18"/></a>
                            <a id="deleteItem" class="mini-button btn-red" style="margin-bottom: 5px;margin-top: 5px" onclick="deleteItem"
                               enabled="false"><spring:message code="page.decorationManualDemandEdit.name19"/></a>
                            <input id="selectInstructions" class="mini-combobox" textField="value" valueField="key"
                                   emptyText="<spring:message code="page.decorationManualDemandEdit.name20" />" allowInput="false" showNullItem="true"
                                   nullItemText="<spring:message code="page.decorationManualDemandEdit.name20" />"
                                   data="[{'key' : '新增','value' : '新增'},{'key' : '变更','value' : '变更'},
                                           {'key' : '翻译','value' : '翻译'}]"/>
                            <input id="selectCollectType" class="mini-combobox" textField="value" valueField="key"
                                   emptyText="<spring:message code="page.decorationManualDemandEdit.name21" />" allowInput="false" showNullItem="true"
                                   nullItemText="<spring:message code="page.decorationManualDemandEdit.name21" />"
                                   data="[{'key':'技术规格资料','value':'技术规格资料'},{'key':'力矩及工具标准值资料','value':'力矩及工具标准值资料'},
                                           {'key':'故障代码','value':'故障代码'}]"/>
                            <a id="submitItem" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="submitItem"
                               enabled="false"><spring:message code="page.decorationManualDemandEdit.name22"/></a>
                            <a id="syncItem" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="syncItem"
                               enabled="false"><spring:message code="page.decorationManualDemandEdit.name23"/></a>
                        </div>
                        <div id="itemListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="true" allowCellWrap="false"
                             showCellTip="true" autoload="true"
                             idField="id" url="${ctxPath}/serviceEngineering/core/decorationManualDemand/getItemList.do?businessId=${businessId}"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowcelledit="true" allowcellselect="true"
                             allowAlternating="true" oncellvalidation="onCellValidation" oncellbeginedit="cellbeginedit">
                            <div property="columns">
                                <div type="checkcolumn" width="20"></div>
                                <div field="manualLanguage" width="80" headerAlign="center" align="center" renderer="render"><spring:message
                                        code="page.decorationManualDemandEdit.name24"/>
                                    <input property="editor" class="mini-combobox" style="width:98%"
                                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringLanguage"
                                           valueField="key" textField="key"/>
                                </div>
                                <div field="manualType" width="100" headerAlign="center" align="center" renderer="render">手册类型
                                    <input property="editor" class="mini-combobox" style="width:98%"
                                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringDecorationManualType"
                                           valueField="key" textField="key"/>
                                </div>
                                <div field="repUserId" displayField="repUser" width="100" align="center" headerAlign="center">负责人
                                    <input property="editor" class="mini-user rxc" plugins="mini-user" allowinput="false" single="true"
                                           mainfield="no"/>
                                </div>
                                <div field="accomplishTime" displayField="accomplishTime" width="100" align="center" headerAlign="center">完成时间
                                    <input property="editor" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                                           valueType="string" showTime="false" showOkButton="false" showClearButton="false" style="width:98%;"/>
                                </div>
                                <div field="manualCode" width="100" headerAlign="center" align="center" renderer="render"><spring:message
                                        code="page.decorationManualDemandEdit.name25"/></div>
                                <div field="businessStatus" width="100" headerAlign="center" align="center" renderer="render"><spring:message
                                        code="page.decorationManualDemandEdit.name26"/></div>
                            </div>
                        </div>
                    </td>
                </tr>
                <%--///////////////////////////////////////////////////--%>
                <tr>
                    <td style="text-align: center;height: 300px"><spring:message code="page.decorationManualDemandEdit.name27"/>：</td>
                    <td colspan="3">
                        <div style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="addBusinessFile()"><spring:message
                                    code="page.decorationManualDemandEdit.name28"/></a>
                            <span style="color: red"><spring:message code="page.decorationManualDemandEdit.name29"/></span>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
                             idField="id" autoload="true"
                             url="${ctxPath}/serviceEngineering/core/decorationManualDemand/getFileList.do?businessId=${businessId}"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20"><spring:message
                                        code="page.decorationManualDemandEdit.name30"/></div>
                                <div field="fileName" align="center" headerAlign="center" width="150"><spring:message
                                        code="page.decorationManualDemandEdit.name31"/></div>
                                <div field="fileSize" align="center" headerAlign="center" width="60"><spring:message
                                        code="page.decorationManualDemandEdit.name32"/></div>
                                <div field="fileDesc" align="center" headerAlign="center" width="100"><spring:message
                                        code="page.decorationManualDemandEdit.name33"/></div>
                                <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer"><spring:message
                                        code="page.decorationManualDemandEdit.name34"/></div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var status = "${status}";
    var jsUseCtxPath = "${ctxPath}";
    var itemListGrid = mini.get("itemListGrid");
    var fileListGrid = mini.get("fileListGrid");
    var formBusiness = new mini.Form("#formBusiness");
    var businessId = "${businessId}";
    var nodeVarsStr = '${nodeVars}';
    var currentTime = "${currentTime}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
    var codeName = "";
    var isDecorationManualAdmin = "${isDecorationManualAdmin}";
    //..
    function cellbeginedit(e) {
        var record = e.record, field = e.field, value = e.value;
        if (codeName != 'F' && codeName != 'A2'){
            if(field=='repUserId' || field=='accomplishTime'){
                e.cancel = true;
            }
        }
    }
    //..
    $(function () {
        var url = jsUseCtxPath + "/serviceEngineering/core/decorationManualDemand/getDetail.do";
        $.post(
            url,
            {businessId: businessId},
            function (json) {
                formBusiness.setData(json);
                if (action == 'detail') {
                    formBusiness.setEnabled(false);
                    itemListGrid.setAllowCellEdit(false);
                    mini.get("addFile").setEnabled(false);
                    $("#detailToolBar").show();
                    //非草稿放开流程信息查看按钮
                    if (status != 'DRAFTED') {
                        $("#processInfo").show();
                    }
                    if (isDecorationManualAdmin == "true") {
                        mini.get("submitItem").setEnabled(true);
                        mini.get("syncItem").setEnabled(true);
                        mini.get("selectInstructions").setEnabled(true);
                        mini.get("selectCollectType").setEnabled(true);
                    }
                } else if (action == 'edit') {
                    mini.get("addItem").setEnabled(true);
                    mini.get("deleteItem").setEnabled(true);
                } else if (action == 'task') {
                    taskActionProcess();
                }
            });
    });
    //..流程引擎调用此方法进行表单数据的获取
    function getData() {
        var formData = _GetFormJsonMini("formBusiness");
        if (formData.SUB_fileListGrid) {
            delete formData.SUB_fileListGrid;
        }
        formData.bos = [];
        var data = itemListGrid.getData();
        if (data.length > 0) {
            formData.itemChangeData = data;
        }
        return formData;
    }
    //..添加
    function addItem() {
        var newRow = {}
        itemListGrid.addRow(newRow, 0);
    }
    //..删除明细
    function deleteItem() {
        var row = itemListGrid.getSelected();
        if (!row) {
            mini.alert(decorationManualDemandEdit_name);
            return;
        }
        delRowGrid("itemListGrid");
    }
    //..启动收集流程
    function submitItem() {
        if (itemListGrid.getChanges().length > 0) {
            return {"result": false, "message": decorationManualDemandEdit_name1};
        }
        var rows = itemListGrid.getSelecteds();
        if (rows.length != 1) {
            mini.alert(decorationManualDemandEdit_name2);
            return;
        }
        if (!mini.get("selectInstructions").getValue()) {
            mini.alert(decorationManualDemandEdit_name3);
            return;
        }
        if (mini.get("selectInstructions").getValue() != '翻译' && !mini.get("selectCollectType").getValue()) {
            mini.alert(decorationManualDemandEdit_name4);
            return;
        }
        mini.confirm(decorationManualDemandEdit_name5, decorationManualDemandEdit_name6, function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/serviceEngineering/core/decorationManualDemand/createCollect.do",
                    method: 'POST',
                    data: {
                        demandId: businessId,
                        demandItemId: rows[0].id,
                        instructions: mini.get("selectInstructions").getValue(),
                        collectType: mini.get("selectCollectType").getValue()
                    },
                    success: function (returnData) {
                        if (returnData) {
                            if (returnData.success) {
                                itemListGrid.reload();
                            }
                        }
                    }
                });
            }
        });
    }
    //..同步归档状态
    function syncItem() {
        var rows = itemListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert(decorationManualDemandEdit_name7);
            return;
        }
        mini.confirm(decorationManualDemandEdit_name8, decorationManualDemandEdit_name6, function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    rowIds.push(r.id);
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/serviceEngineering/core/decorationManualDemand/syncManual.do",
                    method: 'POST',
                    showMsg: false,
                    postJson: true,
                    data: {
                        rows: rows,
                        salesModel: mini.get("salesModel").getValue(),
                        designModel: mini.get("designModel").getValue(),
                        materialCode: mini.get("materialCode").getValue(),
                    },
                    success: function (returnData) {
                        if (returnData) {
                            if (returnData.success) {
                                itemListGrid.reload();
                            }
                        }
                    }
                });
            }
        });
    }
    //..保存草稿
    function saveBusiness(e) {
        mini.get("applyUserId").setValue(currentUserId);
        window.parent.saveDraft(e);
    }
    //..启动流程
    function startBusinessProcess(e) {
        var formValid = validBusiness();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.startProcess(e);
    }
    //..流程中暂存信息（如编制阶段）
    function saveBusinessInProcess() {
        var formData = _GetFormJsonMini("formBusiness");
        if (formData.SUB_fileListGrid) {
            delete formData.SUB_fileListGrid;
        }
        formData.bos = [];
        var data = itemListGrid.getChanges();
        if (data.length > 0) {
            formData.itemChangeData = data;
        }
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/decorationManualDemand/saveBusiness.do',
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message = "";
                    if (data.success) {
                        message = decorationManualDemandEdit_name9;
                    } else {
                        message = decorationManualDemandEdit_name10 + data.message;
                    }
                    mini.alert(message, decorationManualDemandEdit_name11, function () {
                        window.location.reload();
                    });
                }
            }
        });
    }
    //流程中的审批或者下一步
    function businessApprove() {
        //编制阶段的下一步需要校验表单必填字段
        if (codeName == 'A') {
            var formValid = validBusiness();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
            window.parent.approve();
        } else if (codeName == 'A2') {
            var rows = itemListGrid.getData();
            for (var i = 0, l = rows.length; i < l; i++) {
                if (!rows[i].repUserId) {
                    mini.alert("明细中的责任人不能为空!");
                    return;
                }
                if (!rows[i].accomplishTime) {
                    mini.alert("明细中的完成时间不能为空!");
                    return;
                }
            }
            window.parent.approve();
        } else if (codeName == 'F') {
            var rows = itemListGrid.getData();
            for (var i = 0, l = rows.length; i < l; i++) {
                if (rows[i].businessStatus != '已归档') {
                    mini.alert(decorationManualDemandEdit_name12);
                    return;
                }
            }
            window.parent.approve();
        } else {
            window.parent.approve();
        }
    }
    //..添加文件
    function addBusinessFile() {
        var businessId = mini.get("id").getValue();
        if (!businessId) {
            mini.alert(decorationManualDemandEdit_name13);
            return;
        }
        mini.open({
            title: decorationManualDemandEdit_name14,
            url: jsUseCtxPath + "/serviceEngineering/core/decorationManualDemand/fileUploadWindow.do?businessId=" + businessId,
            width: 750,
            height: 450,
            showModal: false,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                if (fileListGrid) {
                    fileListGrid.load();
                }
            }
        });
    }
    //..流程信息浏览
    function processInfo() {
        var instId = $("#INST_ID_").val();
        var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
        _OpenWindow({
            url: url,
            max: true,
            title: decorationManualDemandEdit_name15,
            width: 800,
            height: 600
        });
    }
    //..获取任务相关的环境变量，处理表单可见性
    function taskActionProcess() {
        //获取上一环节的结果和处理人
        bpmPreTaskTipInForm();
        //获取环境变量
        if (!nodeVarsStr) {
            nodeVarsStr = "[]";
        }
        var nodeVars = $.parseJSON(nodeVarsStr);
        for (var i = 0; i < nodeVars.length; i++) {
            if (nodeVars[i].KEY_ == 'codeName') {
                codeName = nodeVars[i].DEF_VAL_;
            }
        }
        if (codeName == 'A') {//如果是编辑节点，则和action=='edit'一样
            mini.get("addItem").setEnabled(true);
            mini.get("deleteItem").setEnabled(true);
        } else {//如果不是编辑节点，首先吧表单和文件的上传权限都封死
            formBusiness.setEnabled(false);
            itemListGrid.setAllowCellEdit(false);
            mini.get("addFile").setEnabled(false);
            if (codeName == 'F' || codeName == 'A2') {//需求确认节点，则放开启动需求和同步状态权限
                mini.get("submitItem").setEnabled(true);
                mini.get("syncItem").setEnabled(true);
                mini.get("selectInstructions").setEnabled(true);
                mini.get("selectCollectType").setEnabled(true);
            }
            if (codeName == 'A2') {
                itemListGrid.setAllowCellEdit(true);
            }
        }
    }
    //..检验表单是否必填
    function validBusiness() {
        var cpzgId = $.trim(mini.get("cpzgId").getValue());
        if (!cpzgId) {
            return {"result": false, "message": decorationManualDemandEdit_name16};
        }
        var salesModel = $.trim(mini.get("salesModel").getValue());
        if (!salesModel) {
            return {"result": false, "message": decorationManualDemandEdit_name17};
        }
        var designModel = $.trim(mini.get("designModel").getValue());
        if (!designModel) {
            return {"result": false, "message": decorationManualDemandEdit_name18};
        }
        var materialCode = $.trim(mini.get("materialCode").getValue());
        if (!materialCode) {
            return {"result": false, "message": decorationManualDemandEdit_name19};
        }
        var salesArea = $.trim(mini.get("salesArea").getValue());
        if (!salesArea) {
            return {"result": false, "message": decorationManualDemandEdit_name20};
        }
        var configurationDescription = $.trim(mini.get("configurationDescription").getValue());
        if (!configurationDescription) {
            return {"result": false, "message": decorationManualDemandEdit_name21};
        }
        var publishTime = $.trim(mini.get("publishTime").getValue());
        if (!publishTime) {
            return {"result": false, "message": decorationManualDemandEdit_name22};
        }
        if (itemListGrid.getData().length == 0) {
            return {"result": false, "message": decorationManualDemandEdit_name23};
        }
        if (itemListGrid.getChanges().length > 0) {
            return {"result": false, "message": decorationManualDemandEdit_name24};
        }
        //明细表单验证
        itemListGrid.validate();
        if (!itemListGrid.isValid()) {
            var error = itemListGrid.getCellErrors()[0];
            itemListGrid.beginEditCell(error.record, error.column);
            mini.alert(error.column.header + error.errorText);
            return;
        }
        return {"result": true};
    }
    //..列表验证
    function onCellValidation(e) {
        if (e.field == 'manualLanguage' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = decorationManualDemandEdit_name25;
        }
        if (e.field == 'manualType' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = decorationManualDemandEdit_name25;
        }
    }
    //..文件列表操作渲染
    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnPreviewSpan(record.fileName, record.id, record.mainId, coverContent);
        //和pdf预览用的同一个url
        var downLoadUrl = '/serviceEngineering/core/decorationManualDemand/PdfPreviewAndAllDownload.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + decorationManualDemandEdit_name26 + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainId + '\',\'' + downLoadUrl + '\')">' + decorationManualDemandEdit_name26 + '</span>';
        //增加删除按钮
        if (action == 'edit' || (action == 'task' && codeName == 'A')) {
            var deleteUrl = "/serviceEngineering/core/decorationManualDemand/delFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + decorationManualDemandEdit_name27 + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainId + '\',\'' + deleteUrl + '\')">' + decorationManualDemandEdit_name27 + '</span>';
        }
        return cellHtml;
    }
    //..文件列表预览渲染
    function returnPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title=' + decorationManualDemandEdit_name28 + ' style="color: silver" >' + decorationManualDemandEdit_name28 + '</span>';
        } else if (fileType == 'pdf') {
            var url = '/serviceEngineering/core/decorationManualDemand/PdfPreview.do';
            s = '<span  title=' + decorationManualDemandEdit_name28 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + decorationManualDemandEdit_name28 + '</span>';
        } else if (fileType == 'office') {
            var url = '/serviceEngineering/core/decorationManualDemand/OfficePreview.do';
            s = '<span  title=' + decorationManualDemandEdit_name28 + ' style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + decorationManualDemandEdit_name28 + '</span>';
        } else if (fileType == 'pic') {
            var url = '/serviceEngineering/core/decorationManualDemand/ImagePreview.do';
            s = '<span  title=' + decorationManualDemandEdit_name28 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + decorationManualDemandEdit_name28 + '</span>';
        }
        return s;
    }
    //..便捷查看相关归档文件
    function showManualfile() {
        var row = itemListGrid.getSelected();
        if (!row) {
            mini.alert(decorationManualDemandEdit_name29);
            return;
        }
        var salesModel = mini.get("salesModel").getValue();
        var designModel = mini.get("designModel").getValue();
        var materialCode = mini.get("materialCode").getValue();
        var manualLanguage = row.manualLanguage;
        var manualType = "装修手册";
        var realUrl = jsUseCtxPath + "/serviceEngineering/core/decorationManualFile/dataListPage.do?salesModel=" + salesModel +
            "&designModel=" + designModel + "&materialCode=" + materialCode + "&manualLanguage=" + manualLanguage + "&manualType=" + manualType;
        var config = {
            url: realUrl,
            max: true
        };
        _OpenWindow(config);
    }
    //..设置日期选择限制
    function onDrawDate(e) {
        var date = e.date;
        var dateNowForword = new Date().add(7, 'day');
        if (date.getTime() < dateNowForword.getTime()) {
            e.allowSelect = false;
        }
    }
    //..
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
</script>
</body>
</html>
