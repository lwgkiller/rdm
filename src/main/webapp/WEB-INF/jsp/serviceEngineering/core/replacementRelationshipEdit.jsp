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
            <input id="INST_ID_" name="INST_ID_" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold">
                    物料替换关系审批
                </caption>
                <tr>
                    <td style="text-align: center;width: 15%">替换类型:</td>
                    <td style="min-width:170px">
                        <input id="replaceType" name="replaceType" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringReplacementRelationshipReplaceType"
                               valueField="key" textField="value" multiSelect="false" onValuechanged="replaceTypeChanged"/>
                    </td>
                    <td style="text-align: center;width: 20%">信息源替换类型：</td>
                    <td style="min-width:170px">
                        <input id="replaceTypeOri" name="replaceTypeOri" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringReplacementRelationshipReplaceTypeOri"
                               valueField="key" textField="value" multiSelect="false" onValuechanged="replaceTypeOriChanged"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">适用机型：</td>
                    <td colspan="3">
                        <input id="applicableModels" name="applicableModels" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">制品处理意见：</td>
                    <td colspan="3">
                        <input id="WIPHandlingComments" name="WIPHandlingComments" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">信息来源：</td>
                    <td style="min-width:170px">
                        <input id="informationSources" name="informationSources" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">技术审核人：</td>
                    <td>
                        <input id="approver" name="approverId" textname="approver"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">申请人：</td>
                    <td style="min-width:170px">
                        <input id="applyUser" name="applyUserId" textname="applyUser"
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
                    <td style="text-align: center;width: 15%">备注：</td>
                    <td colspan="3">
						<textarea id="remark" name="remark" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:100px;line-height:25px;" datatype="varchar" allowinput="true"
                                  mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                </tr>
                <%--itemListGrid--------------------------------------------------------------------%>
                <tr>
                    <td style="text-align: center;height: 400px">替换关系明细：</td>
                    <td colspan="3">
                        <div class="mini-toolbar" id="itemButtons">
                            <a id="addItem" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="addItem" enabled="false">添加记录</a>
                            <a id="removeItem" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="removeItem" enabled="false">删除记录</a>
                            <a id="openImportWindow" class="mini-button" style="margin-right: 5px" plain="true" onclick="openImportWindow()"
                               enabled="false">导入</a>
                        </div>
                        <div id="itemListGrid" class="mini-datagrid" style="height: 85%" allowResize="true" allowCellWrap="false"
                             showCellTip="true" autoload="true" idField="id" multiSelect="false" showPager="false"
                             showColumnsMenu="false" allowcelledit="true" allowcellselect="true" allowAlternating="true"
                             oncellvalidation="onCellValidation">
                            <div property="columns">
                                <div field="busunessNoItem_item" width="50" headerAlign="center" align="center">行项目号</div>
                                <div field="materialCodeOri_item" width="120" headerAlign="center" align="center" renderer="render">原车件物料号
                                    <input id="materialCodeOri_item" property="editor" class="mini-textbox" style="width: 100%"/>
                                </div>
                                <div field="materialNameOri_item" width="120" headerAlign="center" align="center" renderer="render">原车件名称
                                    <input id="materialNameOri_item" property="editor" class="mini-textbox" style="width: 100%"/>
                                </div>
                                <div field="materialCountOri_item" width="120" headerAlign="center" align="center" renderer="render">原车件数量
                                    <input id="materialNameOri_item" property="editor" class="mini-spinner" style="width:100%;" maxValue="9999"/>
                                </div>
                                <div field="materialCodeRep_item" width="120" headerAlign="center" align="center" renderer="render">替换件物料
                                    <input id="materialCodeRep_item" property="editor" class="mini-textbox" style="width: 100%"/>
                                </div>
                                <div field="materialNameRep_item" width="120" headerAlign="center" align="center" renderer="render">替换件名称
                                    <input id="materialNameRep_item" property="editor" class="mini-textbox" style="width: 100%"/>
                                </div>
                                <div field="materialCountRep_item" width="120" headerAlign="center" align="center" renderer="render">替换件数量
                                    <input id="materialCountRep_item" property="editor" class="mini-spinner" style="width:100%;" maxValue="9999"/>
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<%-------------------------------%>
<div id="importWindow" title="导入窗口" class="mini-window" style="width:750px;height:280px;"
     showModal="true" showFooter="false" allowResize="true">
    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
    </p>
    <div style="display: inline-block;float: right;">
        <a id="importBtn" class="mini-button" onclick="importBusiness()">导入</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="closeImportWindow()">关闭</a>
    </div>
    <div class="mini-fit" style="font-size: 14px;margin-top: 30px">
        <form id="formImport" method="post">
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 30%">上传模板：</td>
                    <td style="width: 70%;">
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">物料替换关系导入模板.xlsx</a>
                    </td>
                </tr>
                <tr>
                    <td style="width: 30%">操作：</td>
                    <td>
                        <input class="mini-textbox" style="width:60%;float: left" id="fileName" name="fileName"
                               readonly/>
                        <input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()"
                               accept="application/xls"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="uploadFile">选择文件</a>
                        <a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="clearUploadFile">清除</a>
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
    var importWindow = mini.get("importWindow");
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
        document.getElementById("itemListGrid").style.width = (screen.width - 250) + 'px';
        var url = jsUseCtxPath + "/serviceEngineering/core/replacementRelationship/getDetail.do";
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
                    mini.get("openImportWindow").setEnabled(true);
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
        if (codeName != "A") {//不是编辑中
            formBusiness.setEnabled(false);
            itemListGrid.setAllowCellEdit(false);
        } else {
            mini.get("addItem").setEnabled(true);
            mini.get("removeItem").setEnabled(true);
            mini.get("openImportWindow").setEnabled(true);
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
        if (formData.SUB_itemListGrid) {
            delete formData.SUB_itemListGrid;
        }
        formData.bos = [];
        formData.vars = [];
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/replacementRelationship/saveBusiness.do',
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
            var formValid = validBusiness();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        window.parent.approve();
    }
    //..
    function validBusiness() {
        var replaceType = $.trim(mini.get("replaceType").getValue());
        if (!replaceType) {
            return {"result": false, "message": '替换类型不能为空'};
        }
        var replaceType = $.trim(mini.get("replaceType").getValue());
        if (!replaceType) {
            return {"result": false, "message": '信息源替换类型不能为空'};
        }
        var applicableModels = $.trim(mini.get("applicableModels").getValue());
        if (!applicableModels) {
            return {"result": false, "message": '适用机型不能为空'};
        }
        var WIPHandlingComments = $.trim(mini.get("WIPHandlingComments").getValue());
        if (!WIPHandlingComments) {
            return {"result": false, "message": '制品处理意见不能为空'};
        }
        var informationSources = $.trim(mini.get("informationSources").getValue());
        if (!informationSources) {
            return {"result": false, "message": '信息来源不能为空'};
        }
        var approverId = $.trim(mini.get("approver").getValue());
        if (!approverId) {
            return {"result": false, "message": '技术审核人不能为空'};
        }
        if (itemListGrid.data.length == 0) {
            return {"result": false, "message": '替换关系明细不能为空'};
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
//        if (e.field == 'materialCodeOri_item') {
//            if (e.value && e.value.length != 9) {
//                e.isValid = false;
//                e.errorText = '必须为9位';
//            }
//        }
//        if (e.field == 'materialCodeRep_item') {
//            if (e.value && e.value.length != 9) {
//                e.isValid = false;
//                e.errorText = '必须为9位';
//            }
//        }
//        if (e.field == 'materialNameRep_item' && (!e.value || e.value == '')) {
//            e.isValid = false;
//            e.errorText = '不能为空';
//        }
//        if (e.field == 'materialCountRep_item' && (!e.value || e.value == '')) {
//            e.isValid = false;
//            e.errorText = '不能为空';
//        }
    }
    //..
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
    //..replaceType
    function replaceTypeChanged(e) {
        if (e.value == "单向") {
            mini.get("replaceTypeOri").setValue('N/Y');
        } else if (e.value == "双向") {
            mini.get("replaceTypeOri").setValue('Y/Y');
        } else if (e.value == "专用件I" || e.value == "专用件II" || e.value == "组合") {
            mini.get("replaceTypeOri").setValue('/');
        }
    }
    //..replaceTypeOri
    function replaceTypeOriChanged(e) {
        if (e.value == "N/Y") {
            mini.get("replaceType").setValue('单向');
        } else if (e.value == "Y/Y") {
            mini.get("replaceType").setValue('双向');
        }
    }
    //..
    function addItem() {
        var newRow = {};
        newRow.busunessNoItem_item = itemListGrid.data.length;
        newRow.materialCountOri_item = 1;
        newRow.materialCountRep_item = 1;
        itemListGrid.addRow(newRow);
    }
    //..
    function removeItem() {
        var row = itemListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
            return;
        }
        if (row.busunessNoItem_item != itemListGrid.data.length - 1) {
            mini.alert("由于行项目号顺序是重要信息，只能从末尾进行删除！");
            return;
        }
        delRowGrid("itemListGrid");
    }
    //..
    function openImportWindow() {
        if (!businessId) {
            mini.alert("请先保存草稿");
            return;
        }
        importWindow.show();
    }
    //..
    function importBusiness() {
        var file = null;
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            file = fileList[0];
        }
        if (!file) {
            mini.alert('请选择文件！');
            return;
        }
        //XMLHttpRequest方式上传表单
        var xhr = false;
        try {
            //尝试创建 XMLHttpRequest 对象，除 IE 外的浏览器都支持这个方法。
            xhr = new XMLHttpRequest();
        } catch (e) {
            //使用较新版本的 IE 创建 IE 兼容的对象（Msxml2.XMLHTTP）。
            xhr = ActiveXobject("Msxml12.XMLHTTP");
        }

        if (xhr.upload) {
            xhr.onreadystatechange = function (e) {
                if (xhr.readyState == 4) {
                    if (xhr.status == 200) {
                        if (xhr.responseText) {
                            var returnObj = JSON.parse(xhr.responseText);
                            var message = '';
                            if (returnObj.message) {
                                message = returnObj.message;
                            }
                            mini.alert(message);
                        }
                    }
                }
            };
            //开始上传
            xhr.open('POST', jsUseCtxPath + '/serviceEngineering/core/replacementRelationship/importItemdata.do?businessId=${businessId}', false);
            xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            var fd = new FormData();
            fd.append('importFile', file);
            xhr.send(fd);
        }
    }
    //..
    function closeImportWindow() {
        importWindow.hide();
        clearUploadFile();
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + mini.get("instId").getValue();
        window.parent.location.href = url;
    }
    //..
    function downImportTemplate() {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/replacementRelationship/importItemdataTemplateDownload.do");
        $("body").append(form);
        form.submit();
        form.remove();
    }
    //..
    function getSelectFile() {
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            var fileNameSuffix = fileList[0].name.split('.').pop();
            if (fileNameSuffix == 'xls' || fileNameSuffix == 'xlsx') {
                mini.get("fileName").setValue(fileList[0].name);
            }
            else {
                clearUploadFile();
                mini.alert('请上传excel文件！');
            }
        }
    }
    //..
    function uploadFile() {
        $("#inputFile").click();
    }
    //..
    function clearUploadFile() {
        $("#inputFile").val('');
        mini.get("fileName").setValue('');
    }
</script>
</body>
</html>
