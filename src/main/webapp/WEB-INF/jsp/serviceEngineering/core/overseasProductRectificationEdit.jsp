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
            <input id="isSynToGss" name="isSynToGss" class="mini-hidden"/>
            <input id="INST_ID_" name="INST_ID_" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold">
                    海外产品市场整改
                </caption>
                <tr>
                    <td style="text-align: center;width: 15%">整改类型:</td>
                    <td style="min-width:170px">
                        <input id="rectificationType" name="rectificationType" class="mini-combobox" style="width:98%"
                               data="[{'key':'外购件','value':'外购件'},{'key':'装配件','value':'装配件'}]"
                               valueField="key" textField="value" multiSelect="false"/>
                    </td>
                    <td style="text-align: center;width: 20%">是否需要设计人员补充：</td>
                    <td style="min-width:170px">
                        <input id="isNeedDesigner" name="isNeedDesigner" class="mini-combobox" style="width:98%"
                               data="[{'key':'是','value':'是'},{'key':'否','value':'否'}]"
                               valueField="key" textField="value" multiSelect="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">设计人员：</td>
                    <td>
                        <input id="designer" name="designerId" textname="designer"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                    <td style="text-align: center;width: 15%">发起人：</td>
                    `
                    <td style="min-width:170px">
                        <input id="applyUser" name="applyUserId" textname="applyUser"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">发起时间：</td>
                    <td style="min-width:170px">
                        <input id="applyTime" name="applyTime" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                               valueType="string" showTime="false" showOkButton="true" showClearButton="false" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <%--itemListGrid--------------------------------------------------------------------%>
                <tr>
                    <td colspan="4" style="text-align: center;height: 50px">整改物料明细：</td>
                </tr>
                <tr>
                    <td colspan="4" style="height: 500px">
                        <div id="itemButtons" class="mini-toolbar">
                            <a id="addItem" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="addItem" enabled="false">添加记录</a>
                            <a id="removeItem" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="removeItem" enabled="false">删除记录</a>
                            <a id="openImportWindow" class="mini-button" style="margin-right: 5px" plain="true" onclick="openImportWindow"
                               enabled="false">导入</a>
                        </div>
                        <div id="itemListGrid" class="mini-datagrid" style="height: 95%" allowResize="true" allowCellWrap="false"
                             showCellTip="true" autoload="true" idField="id" multiSelect="true" showPager="false"
                             showColumnsMenu="false" allowcelledit="true" allowcellselect="true" allowAlternating="true"
                             oncellvalidation="onCellValidation">
                            <div property="columns">
                                <div type="checkcolumn" width="40" trueValue="1" falseValue="0"></div>
                                <div type="indexcolumn" width="40" headerAlign="center" align="center">序号</div>
                                <div field="materialCodeNew" width="120" headerAlign="center" align="center">新物料编码
                                    <input id="materialCodeNew" property="editor" class="mini-textbox" style="width: 100%"/>
                                </div>
                                <div field="materialNameNew" width="200" headerAlign="center" align="center" renderer="render">新物料名称
                                    <input id="materialNameNew" property="editor" class="mini-textbox" style="width: 100%"/>
                                </div>
                                <div field="materialCountNew" width="80" headerAlign="center" align="center">数量
                                    <input id="materialCountNew" property="editor" class="mini-spinner" style="width:100%;" maxValue="9999"/>
                                </div>
                                <div field="materialCodeOld" width="120" headerAlign="center" align="center">旧物料编码
                                    <input id="materialCodeOld" property="editor" class="mini-textbox" style="width: 100%"/>
                                </div>
                                <div field="materialNameOld" width="200" headerAlign="center" align="center" renderer="render">旧物料名称
                                    <input id="materialNameOld" property="editor" class="mini-textbox" style="width: 100%"/>
                                </div>
                                <div field="materialCountOld" width="80" headerAlign="center" align="center">数量
                                    <input id="materialCountOld" property="editor" class="mini-spinner" style="width:100%;" maxValue="9999"/>
                                </div>
                                <div field="remarks" width="300" headerAlign="center" align="center" renderer="render">备注信息
                                    <input id="remarks" property="editor" class="mini-textarea" style="width:98%;"/>
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                <%--itemListGrid2--------------------------------------------------------------------%>
                <tr>
                    <td style="text-align: center;height: 400px">整机编号列表：</td>
                    <td colspan="3">
                        <div id="itemButtons2" class="mini-toolbar">
                            <a id="addItem2" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="addItem2"
                               enabled="false">添加记录</a>
                            <a id="removeItem2" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="removeItem2" enabled="false">删除记录</a>
                            <a id="openImportWindow2" class="mini-button" style="margin-right: 5px" plain="true" onclick="openImportWindow"
                               enabled="false">导入</a>
                        </div>
                        <div id="itemListGrid2" class="mini-datagrid" style="height: 85%" allowResize="true" allowCellWrap="false"
                             showCellTip="true" autoload="true" idField="id" multiSelect="true" showPager="false"
                             showColumnsMenu="false" allowcelledit="true" allowcellselect="true" allowAlternating="true"
                             oncellvalidation="onCellValidation2">
                            <div property="columns">
                                <div type="checkcolumn" width="40" trueValue="1" falseValue="0"></div>
                                <div type="indexcolumn" width="40" headerAlign="center" align="center">序号</div>
                                <div field="pin" width="150" headerAlign="center" align="center" renderer="render">受影响车辆
                                    <input id="pin" property="editor" class="mini-textbox" style="width:98%;"/>
                                </div>
                                <div field="isSynToGss" width="150" headerAlign="center" align="center">是否同步到GSS</div>
                            </div>
                        </div>
                    </td>
                </tr>
                <%--fileListGrid--%>
                <tr>
                    <td style="text-align: center;height: 400px">附件列表：</td>
                    <td colspan="3">
                        <div style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="addBusinessFile()" enabled="false">添加文件</a>
                            <span style="color: red">注：添加文件前，请先进行草稿的保存</span>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
                             idField="id" autoload="true"
                             url="${ctxPath}/serviceEngineering/core/overseasProductRectification/getFileList.do?businessId=${businessId}"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20">序号</div>
                                <div field="fileName" align="center" headerAlign="center" width="150">文件名</div>
                                <div field="fileSize" align="center" headerAlign="center" width="60">文件大小</div>
                                <div field="fileDesc" align="center" headerAlign="center" width="100">备注说明</div>
                                <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer">操作</div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<%--导入窗口--%>
<div id="importWindow" title="导入窗口" class="mini-window" style="width:750px;height:280px;" showModal="true" showFooter="false" allowResize="true">
    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
    </p>
    <div style="display: inline-block;float: right;">
        <a id="importBtn" class="mini-button" onclick="importItem()">导入整改物料明细</a>
        <a id="importBtn" class="mini-button" onclick="importItem2()">导入整机编号列表</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="closeImportWindow()">关闭</a>
    </div>
    <div class="mini-fit" style="font-size: 14px;margin-top: 30px">
        <form id="formImport" method="post">
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 30%">上传模板：</td>
                    <td style="width: 70%;">
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">物料明细模板下载.xlsx</a>
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate2()">整机编号模板下载.xlsx</a>
                    </td>
                </tr>
                <tr>
                    <td style="width: 30%">操作：</td>
                    <td>
                        <input class="mini-textbox" style="width:60%;float: left" id="fileName" name="fileName" readonly/>
                        <input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()" accept="application/xls"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px" onclick="uploadFile()">选择文件</a>
                        <a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px" onclick="clearUploadFile()">清除</a>
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
    var itemListGrid2 = mini.get("itemListGrid2");
    var fileListGrid = mini.get("fileListGrid");
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
        var url = jsUseCtxPath + "/serviceEngineering/core/overseasProductRectification/getDetail.do";
        $.post(
            url,
            {businessId: businessId},
            function (json) {
                formBusiness.setData(json);
                var recordItems = JSON.parse(json.recordItems);
                itemListGrid.setData(recordItems);
                var recordItems2 = JSON.parse(json.recordItems2);
                itemListGrid2.setData(recordItems2);
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
                    mini.get("addItem2").setEnabled(true);
                    mini.get("removeItem2").setEnabled(true);
                    mini.get("openImportWindow2").setEnabled(true);
                    mini.get("addFile").setEnabled(true);
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
        if (itemListGrid.getData().length > 0) {
            formData.recordItems = itemListGrid.getData();
        }
        if (formData.SUB_itemListGrid2) {
            delete formData.SUB_itemListGrid2;
        }
        if (itemListGrid2.getData().length > 0) {
            formData.recordItems2 = itemListGrid2.getData();
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
            mini.get("openImportWindow").setEnabled(true);
            mini.get("addItem2").setEnabled(true);
            mini.get("removeItem2").setEnabled(true);
            mini.get("openImportWindow2").setEnabled(true);
            mini.get("addFile").setEnabled(true);
        } else if (codeName == "B") {
            mini.get("addItem").setEnabled(true);
            mini.get("removeItem").setEnabled(true);
            mini.get("openImportWindow").setEnabled(true);
        } else {
            formBusiness.setEnabled(false);
            itemListGrid.setAllowCellEdit(false);
            itemListGrid2.setAllowCellEdit(false);
        }
    }
    //..流程信息浏览
    function processInfo() {
        var instId = $("#instId").val();
        var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
        _OpenWindow({
            url: url,
            max: true,
            title: "流程图实例",
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
            url: jsUseCtxPath + '/serviceEngineering/core/overseasProductRectification/saveBusiness.do',
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
        var rectificationType = $.trim(mini.get("rectificationType").getValue());
        if (!rectificationType) {
            return {"result": false, "message": '整改类型不能为空'};
        }
        var isNeedDesigner = $.trim(mini.get("isNeedDesigner").getValue());
        if (!isNeedDesigner) {
            return {"result": false, "message": '是否需要设计人员补充不能为空'};
        }
        if (isNeedDesigner) {
            var designer = $.trim(mini.get("designer").getValue());
            if (isNeedDesigner == '是' && !designer) {
                return {"result": false, "message": '需要设计人员补充时设计人员不能为空'};
            }

        }
        if (itemListGrid.data.length == 0) {
            return {"result": false, "message": '整改物料明细不能为空'};
        }
        if (itemListGrid2.data.length == 0) {
            return {"result": false, "message": '整机编号列表不能为空'};
        }
        //..
        if(!itemListGrid.data[0].materialCodeNew || !itemListGrid.data[0].materialNameNew ||
            !itemListGrid.data[0].materialCodeOld || !itemListGrid.data[0].materialNameOld){
            return {"result": false, "message": '首行的新旧物料号物料描述都不能为空'};
        }
        //..
        for (var i = 0, l = itemListGrid.data.length; i < l; i++) {
            if((itemListGrid.data[i].materialCodeNew && !itemListGrid.data[i].materialCountNew)){
                return {"result": false, "message": '有新物料的情况下其数量不能为空'};
            }
            if((itemListGrid.data[i].materialCodeNew && !itemListGrid.data[i].materialNameNew)||
                (itemListGrid.data[i].materialCodeOld && !itemListGrid.data[i].materialNameOld)){
                return {"result": false, "message": '新旧物料号均不能出现只有物料号没有描述的情况'};
            }
            if (!itemListGrid.data[i].materialCodeNew && !itemListGrid.data[i].materialCodeOld) {
                return {"result": false, "message": '新旧物料号不能同时为空'};
            }
            if (itemListGrid.data[i].materialCodeNew && itemListGrid.data[i].materialCodeOld &&
                (itemListGrid.data[i].materialCodeNew == itemListGrid.data[i].materialCodeOld)) {
                return {"result": false, "message": '新旧物料号不能出现相同的情况'};
            }
        }
        //..
        var materialCodeNews = new Set();
        var materialCodeOlds = new Set();
        var materialCodeNewsCount = 0;
        var materialCodeOldsCount = 0;
        for (var i = 0, l = itemListGrid.data.length; i < l; i++) {
            if (itemListGrid.data[i].materialCodeNew) {
                materialCodeNewsCount++;
                materialCodeNews.add(itemListGrid.data[i].materialCodeNew);
            }
            if (itemListGrid.data[i].materialCodeOld) {
                materialCodeOldsCount++;
                materialCodeOlds.add(itemListGrid.data[i].materialCodeOld);
            }
        }
        if (materialCodeNews.size != materialCodeNewsCount || materialCodeOlds.size != materialCodeOldsCount) {
            return {"result": false, "message": '新旧物料号在列表中均不能出现重复'};
        }
        //
        itemListGrid2.validate();
        if (!itemListGrid2.isValid()) {
            var error = itemListGrid2.getCellErrors()[0];
            itemListGrid2.beginEditCell(error.record, error.column);
            return {"result": false, "message": error.column.header + error.errorText};
        }
        return {"result": true};
    }
    //..列表验证
    function onCellValidation(e) {
    }
    function onCellValidation2(e) {
        if (e.field == 'pin' && !e.value) {
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
        itemListGrid.addRow(newRow);
    }
    //..
    function removeItem() {
        var rows = itemListGrid.getSelecteds();
        if (rows.length == 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        delRowGrid("itemListGrid");
    }
    //..
    function addItem2() {
        var newRow = {};
        itemListGrid2.addRow(newRow);
    }
    //..
    function removeItem2() {
        var rows = itemListGrid2.getSelecteds();
        if (rows.length == 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        delRowGrid("itemListGrid2");
    }
    //..添加文件
    function addBusinessFile() {
        var businessId = mini.get("id").getValue();
        if (!businessId) {
            mini.alert("请先点击‘保存草稿’进行表单的保存！");
            return;
        }
        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/serviceEngineering/core/overseasProductRectification/fileUploadWindow.do?businessId=" + businessId,
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
    //..文件列表操作渲染
    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnPreviewSpan(record.fileName, record.id, record.mainId, coverContent);
        //和pdf预览用的同一个url
        var downLoadUrl = '/serviceEngineering/core/overseasProductRectification/PdfPreviewAndAllDownload.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainId + '\',\'' + downLoadUrl + '\')">下载</span>';
        //增加删除按钮
        if (action == 'edit' || (action == 'task' && codeName == 'A')) {
            var deleteUrl = "/serviceEngineering/core/overseasProductRectification/delFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }
    //..文件列表预览渲染
    function returnPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            var url = '/serviceEngineering/core/overseasProductRectification/PdfPreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/serviceEngineering/core/overseasProductRectification/OfficePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/serviceEngineering/core/overseasProductRectification/ImagePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        return s;
    }
    //..以下导入..
    function openImportWindow() {
        var businessId = mini.get("id").getValue();
        if (!businessId) {
            mini.alert("请先点击‘保存草稿’进行表单的保存！");
            return;
        }
        importWindow.show();
    }
    //..
    function importItem() {
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
            xhr.open('POST', jsUseCtxPath +
                '/serviceEngineering/core/overseasProductRectification/importItem.do?businessId=' + businessId, false);
            xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            var fd = new FormData();
            fd.append('importFile', file);
            xhr.send(fd);
        }
    }
    //..
    function importItem2() {
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
            xhr.open('POST', jsUseCtxPath +
                '/serviceEngineering/core/overseasProductRectification/importItem2.do?businessId=' + businessId, false);
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
        window.location.reload();
    }
    //..
    function clearUploadFile() {
        $("#inputFile").val('');
        mini.get("fileName").setValue('');
    }
    //..
    function downImportTemplate() {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/overseasProductRectification/importItemTDownload.do");
        $("body").append(form);
        form.submit();
        form.remove();
    }
    //..
    function downImportTemplate2() {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/overseasProductRectification/importItemTDownload2.do");
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
            } else {
                clearUploadFile();
                mini.alert('请上传excel文件！');
            }
        }
    }
    //..
    function uploadFile() {
        $("#inputFile").click();
    }
</script>
</body>
</html>
