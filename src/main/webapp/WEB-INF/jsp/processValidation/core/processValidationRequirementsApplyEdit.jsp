<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 100%;">
        <form id="formBusiness" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="businessStatus" name="businessStatus" class="mini-hidden"/>
            <input id="businessType" name="businessType" class="mini-hidden"/>
            <input id="INST_ID_" name="INST_ID_" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold">
                    工艺验证需求审批
                </caption>
                <tr>
                    <td style="text-align: center;width: 15%">申请人：</td>
                    <td style="min-width:170px">
                        <input id="applyUserId" name="applyUserId" textname="applyUser"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%">申请时间：</td>
                    <td style="min-width:170px">
                        <input id="applyTime" name="applyTime" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                               valueType="string" showTime="false" showOkButton="true" showClearButton="false" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">验证内容：<span style="color:red">*</span></td>
                    <td colspan="3">
                        <input id="validationContent" name="validationContent" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">需求详细描述：<span style="color:red">*</span></td>
                    <td colspan="3">
						<textarea id="requirementsDescription" name="requirementsDescription" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:100px;line-height:25px;" datatype="varchar" allowinput="true"
                                  mwidth="80" wunit="%" mheight="300" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">任务来源：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="souceOfPVRTask" name="souceOfPVRTask"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=souceOfPVRTask"
                               valueField="key" textField="value"/>
                    </td>
                    <td style="text-align: center;width: 15%">任务执行人：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="taskExecutorId" name="taskExecutorId" textname="taskExecutor" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="任务执行人" length="50"
                               mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">处理结果：<span style="color:red">*</span></td>
                    <td colspan="3">
						<textarea id="processingResults" name="processingResults" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:100px;line-height:25px;" datatype="varchar" allowinput="true"
                                  mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                </tr>
                <%--itemListGrid--------------------------------------------------------------------%>
                <tr>
                    <td style="text-align: center;height: 400px">需求明细：</td>
                    <td colspan="3">
                        <div class="mini-toolbar" id="itemButtons">
                            <a id="addItem" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="addItem" enabled="false">添加记录</a>
                            <a id="removeItem" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="removeItem" enabled="false">删除记录</a>
                        </div>
                        <div id="itemListGrid" class="mini-datagrid" style="height: 85%" allowResize="true" allowCellWrap="false"
                             showCellTip="true" autoload="true" idField="id" multiSelect="false" showPager="false"
                             showColumnsMenu="false" allowcelledit="true" allowcellselect="true" allowAlternating="true"
                             oncellvalidation="onCellValidation">
                            <div property="columns">
                                <div type="checkcolumn" width="40"></div>
                                <div field="drawingNoName_item" width="200" headerAlign="center" align="center" renderer="render">部件图号及名称
                                    <input id="drawingNoName_item" property="editor" class="mini-textbox" style="width: 100%"/>
                                </div>
                                <div field="indicatorName_item" width="200" headerAlign="center" align="center" renderer="render">指标名称
                                    <input id="indicatorName_item" property="editor" class="mini-textbox" style="width: 100%"/>
                                </div>
                                <div field="targetValue_item" width="150" headerAlign="center" align="center" renderer="render">目标值
                                    <input id="targetValue_item" property="editor" class="mini-textbox" style="width: 100%"/>
                                </div>
                                <div field="evaluationCriterion_item" width="150" headerAlign="center" align="center" renderer="render">评价标准
                                    <input id="evaluationCriterion_item" property="editor" class="mini-textbox" style="width: 100%"/>
                                </div>
                                <div field="measuringMethod_item" width="150" headerAlign="center" align="center" renderer="render">测量方法
                                    <input id="measuringMethod_item" property="editor" class="mini-textbox" style="width: 100%"/>
                                </div>
                                <div field="souceOfTask_item" width="150" headerAlign="center" align="center" renderer="render">任务来源
                                    <input id="souceOfTask_item" property="editor" class="mini-textbox" style="width: 100%"/>
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<%--javascript------------------------------------------------------%>
<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var status = "${status}";
    var formBusiness = new mini.Form("#formBusiness");
    var jsUseCtxPath = "${ctxPath}";
    var itemListGrid = mini.get("itemListGrid");
    var businessId = "${businessId}";
    var nodeVarsStr = '${nodeVars}';
    var currentTime = "${currentTime}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
    var codeName = "";
    //..
    $(function () {
        //document.getElementById("itemListGrid").style.width = (screen.width - 250) + 'px';
        var url = jsUseCtxPath + "/processValidation/core/requirements/getApplyDetail.do";
        $.post(
            url,
            {businessId: businessId},
            function (json) {
                formBusiness.setData(json);
                var recordItems = JSON.parse(json.recordItems);
                itemListGrid.setData(recordItems);
                if (action == 'detail') {
                    formBusiness.setEnabled(false);
                    $("#detailToolBar").show();
                    //非草稿放开流程信息查看按钮
                    if (status != 'DRAFTED') {
                        $("#processInfo").show();
                    }
                } else if (action == 'edit') {
                    mini.get("addItem").setEnabled(true);
                    mini.get("removeItem").setEnabled(true);
                    mini.get("taskExecutorId").setEnabled(false);
                    mini.get("processingResults").setEnabled(false);
                } else if (action == 'task') {
                    taskActionProcess();
                }
            });
    });
    //..流程引擎调用此方法进行表单数据的获取
    function getData() {
        var formData = _GetFormJsonMini("formBusiness");
        if (formData.SUB_itemListGrid) {
            delete formData.SUB_itemListGrid;
        }
        data = itemListGrid.getData();
        if (data.length > 0) {
            formData.recordItems = data;
        }
        formData.bos = [];
        formData.vars = [];
        return formData;
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
        if (codeName == "A") {
            mini.get("addItem").setEnabled(true);
            mini.get("removeItem").setEnabled(true);
            mini.get("taskExecutorId").setEnabled(false);
            mini.get("processingResults").setEnabled(false);
        } else {
            formBusiness.setEnabled(false);
            itemListGrid.setAllowCellEdit(false);
            if (codeName == "C") {
                mini.get("taskExecutorId").setEnabled(true);
            } else if (codeName == "D") {
                mini.get("processingResults").setEnabled(true);
            }
        }
    }
    //..流程信息浏览
    function processInfo() {
        var instId = $("#instId").val();
        var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
        _OpenWindow({
            url: url,
            max: true,
            title: decorationManualTopicPSEdit_name,
            width: 800,
            height: 600
        });
    }
    //..保存草稿
    function saveBusiness(e) {
        window.parent.saveDraft(e);
    }
    //..启动流程
    function startBusinessProcess(e) {
        var formValid = validBusinessA();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.startProcess(e);
    }
    //..流程中暂存信息（如编制阶段）
    function saveBusinessInProcess() {
        var formData = _GetFormJsonMini("formBusiness");
        if (formData.SUB_itemListGrid) {
            delete formData.SUB_itemListGrid;
        }
        formData.bos = [];
        formData.vars = [];
        $.ajax({
            url: jsUseCtxPath + '/processValidation/core/requirements/saveApply.do',
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message = "";
                    if (data.success) {
                        message = '数据保存成功';
                    } else {
                        message = '数据保存失败' + data.message;
                    }
                    mini.alert(message, '提示信息', function () {
                        window.location.reload();
                    });
                }
            }
        });
    }
    //..流程中的审批或者下一步
    function businessApprove(e) {
        var businessStatus = mini.get("businessStatus");
        //编制阶段的下一步需要校验表单必填字段
        if (codeName == "A") {
            var formValid = validBusinessA();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        } else if (codeName == "C") {
            var formValid = validBusinessC();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        } else if (codeName == "D") {
            var formValid = validBusinessD();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        window.parent.approve();
    }
    //..
    function validBusinessA() {
        var validationContent = $.trim(mini.get("validationContent").getValue());
        if (!validationContent) {
            return {"result": false, "message": "验证内容不能为空"};
        }
        var requirementsDescription = $.trim(mini.get("requirementsDescription").getValue());
        if (!requirementsDescription) {
            return {"result": false, "message": "需求详细描述不能为空"};
        }
        var souceOfPVRTask = $.trim(mini.get("souceOfPVRTask").getValue());
        if (!souceOfPVRTask) {
            return {"result": false, "message": "任务来源不能为空"};
        }
        if (itemListGrid.data.length == 0) {
            return {"result": false, "message": '需求明细不能为空'};
        }
        //明细表单验证
        itemListGrid.validate();
        if (!itemListGrid.isValid()) {
            var error = itemListGrid.getCellErrors()[0];
            itemListGrid.beginEditCell(error.record, error.column);
            return {"result": false, "message": error.column.header + error.errorText};
        }
        return {"result": true};
    }
    //..
    function validBusinessC() {
        var taskExecutorId = $.trim(mini.get("taskExecutorId").getValue());
        if (!taskExecutorId) {
            return {"result": false, "message": "任务执行人不能为空"};
        }
        return {"result": true};
    }
    //..
    function validBusinessD() {
        var processingResults = $.trim(mini.get("processingResults").getValue());
        if (!processingResults) {
            return {"result": false, "message": "处理结果不能为空"};
        }
        return {"result": true};
    }
    //..列表验证
    function onCellValidation(e) {
        if (e.field == 'drawingNoName_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'indicatorName_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'targetValue_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'evaluationCriterion_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'measuringMethod_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'souceOfTask_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
    }
    //..
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
    //..
    function addItem() {
        var newRow = {};
        newRow.drawingNoName_item = "";
        newRow.indicatorName_item = "";
        newRow.targetValue_item = "";
        newRow.evaluationCriterion_item = "";
        newRow.measuringMethod_item = "";
        newRow.souceOfTask_item = "";
        itemListGrid.addRow(newRow);
    }
    //..
    function removeItem() {
        var row = itemListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
            return;
        }
        delRowGrid("itemListGrid");
    }
</script>
</body>
</html>
