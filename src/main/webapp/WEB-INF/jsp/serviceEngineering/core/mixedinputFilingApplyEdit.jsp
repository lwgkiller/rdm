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
                    混投台账归档审批
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
                    <td style="text-align: center;width: 15%">备注：</td>
                    <td colspan="3">
						<textarea id="remarks" name="remarks" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:100px;line-height:25px;" datatype="varchar" allowinput="true"
                                  mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                </tr>
                <%--itemListGrid--------------------------------------------------------------------%>
                <tr>
                    <td style="text-align: center;height: 400px">混投明细：</td>
                    <td colspan="3">
                        <div class="mini-toolbar" id="itemButtons">
                            <a id="addItem" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="addItem" enabled="false">添加记录</a>
                            <a id="removeItem" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="removeItem" enabled="false">删除记录</a>
                            <a id="importItem" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="openImportWindow"
                               enabled="false">导入记录</a>
                        </div>
                        <div id="itemListGrid" class="mini-datagrid" style="height: 85%" allowResize="true" allowCellWrap="false"
                             showCellTip="true" autoload="true" idField="id" multiSelect="false" showPager="false"
                             showColumnsMenu="false" allowcelledit="true" allowcellselect="true" allowAlternating="true"
                             oncellvalidation="onCellValidation">
                            <div property="columns">
                                <div type="checkcolumn" width="40"></div>
                                <div field="orderNo_item" width="100" headerAlign="center" align="center" renderer="render">订单信息
                                    <input id="orderNo_item" property="editor" class="mini-textbox" style="width: 100%"/>
                                </div>
                                <div field="batchNo_item" width="200" headerAlign="center" align="center" renderer="render">批次号
                                    <input id="batchNo_item" property="editor" class="mini-textbox" style="width: 100%"/>
                                </div>
                                <div field="orderInputCount_item" width="70" headerAlign="center" align="center" renderer="render">订单数量
                                    <input id="orderInputCount_item" property="editor" class="mini-spinner" style="width:100%;" maxValue="9999"/>
                                </div>
                                <div field="materialCodeOfMachine_item" width="100" headerAlign="center" align="center" renderer="render">机型物料号
                                    <input id="materialCodeOfMachine_item" property="editor" class="mini-textbox" style="width: 100%"/>
                                </div>
                                <div field="materialDescriptionOfMachine_item" width="250" headerAlign="center" align="center" renderer="render">
                                    机型物料描述
                                    <input id="materialDescriptionOfMachine_item" property="editor" class="mini-textbox" style="width: 100%"/>
                                </div>
                                <div field="materialCode_item" width="100" headerAlign="center" align="center" renderer="render">物料号
                                    <input id="materialCode_item" property="editor" class="mini-textbox" style="width: 100%"/>
                                </div>
                                <div field="materialDescription_item" width="250" headerAlign="center" align="center" renderer="render">物料描述
                                    <input id="materialDescription_item" property="editor" class="mini-textbox" style="width: 100%"/>
                                </div>
                                <div field="materialCount_item" width="60" headerAlign="center" align="center" renderer="render">数量
                                    <input id="materialCount_item" property="editor" class="mini-spinner" style="width:100%;" maxValue="9999"/>
                                </div>
                                <div field="isMixedInput_item" width="70" headerAlign="center" align="center" renderer="render">是否混投
                                    <input id="isMixedInput_item" name="isMixedInput_item" class="mini-combobox"
                                           textField="value" valueField="key" property="editor"
                                           data="[{key:'是',value:'是'},{key:'否',value:'否'}]"/>
                                </div>
                                <div field="remarks1_item" width="250" headerAlign="center" align="center" renderer="render">分配车号或异常说明
                                    <input id="remarks1_item" property="editor" class="mini-textbox" style="width: 100%"/>
                                </div>
                                <div field="remarks2_item" width="250" headerAlign="center" align="center" renderer="render">混投依据或实际订单数量
                                    <input id="remarks2_item" property="editor" class="mini-textbox" style="width: 100%"/>
                                </div>
                                <div field="repUserName_item" width="60" headerAlign="center" align="center" renderer="render">责任人
                                    <input id="repUserName_item" property="editor" class="mini-textbox" style="width: 100%"/>
                                </div>
                                <div field="signYear_item" width="60" headerAlign="center" align="center" renderer="render">年份
                                    <input id="signYear_item" name="signYear_item" class="mini-combobox" style="width:98%"
                                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signYear" property="editor"
                                           valueField="key" textField="value" multiSelect="false"/>
                                </div>
                                <div field="signMonth_item" width="60" headerAlign="center" align="center" renderer="render">月份
                                    <input id="signMonth_item" name="signMonth_item" class="mini-combobox" style="width:98%"
                                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signMonth" property="editor"
                                           valueField="key" textField="value" multiSelect="false"/>
                                </div>
                                <div field="remarks_item" width="200" headerAlign="center" align="center" renderer="render">备注
                                    <input id="remarks_item" property="editor" class="mini-textbox" style="width: 100%"/>
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
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">混投台账导入模板.xlsx</a>
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
        var url = jsUseCtxPath + "/serviceEngineering/core/mixedinput/getFilingDetail.do";
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
                    mini.get("importItem").setEnabled(true);
                } else if (action == 'task') {
                    taskActionProcess();
                }
            });
    });
    //流程引擎调用此方法进行表单数据的获取
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
        formData.businessType = 'filing';
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
            mini.get("importItem").setEnabled(true);
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
            url: jsUseCtxPath + '/serviceEngineering/core/mixedinput/saveBusinessFiling.do',
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
        if (itemListGrid.data.length == 0) {
            return {"result": false, "message": '混投明细不能为空'};
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
    //..列表验证
    function onCellValidation(e) {
        if (e.field == 'orderNo_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'batchNo_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'orderInputCount_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'materialCodeOfMachine_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'materialDescriptionOfMachine_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'materialCode_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'materialDescription_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'materialCount_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'isMixedInput_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'remarks1_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'remarks2_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'repUserName_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'signYear_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'signMonth_item' && !e.value) {
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
        newRow.orderNo_item = "";
        newRow.batchNo_item = "";
        newRow.orderInputCount_item = 0
        newRow.materialCodeOfMachine_item = "";
        newRow.materialDescriptionOfMachine_item = "";
        newRow.materialCode_item = "";
        newRow.materialDescription_item = "";
        newRow.materialCount_item = 0
        newRow.isMixedInput_item = "";
        newRow.remarks1_item = "";
        newRow.remarks2_item = "";
        newRow.repUserName_item = "";
        newRow.signYear_item = "";
        newRow.signMonth_item = "";
        newRow.remarks_item = "";
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
    //..
    function openImportWindow() {
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
                            if (returnObj.message && !returnObj.success) {
                                mini.alert(returnObj.message);
                            } else if (returnObj.message && returnObj.success) {
                                var data = returnObj.data;
                                itemListGrid.setData(data);
                                mini.alert(returnObj.message);
                            }
                        }
                    }
                }
            };
            //开始上传
            xhr.open('POST', jsUseCtxPath + '/serviceEngineering/core/mixedinput/importFilingItemdata.do', false);
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
    }
    //..
    function downImportTemplate() {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/mixedinput/importMasterdataTemplateDownload.do");
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
