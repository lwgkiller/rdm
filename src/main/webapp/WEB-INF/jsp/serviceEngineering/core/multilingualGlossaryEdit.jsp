<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>多语言术语库扩充申请单</title>
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
            <input id="applyUserId" name="applyUserId" class="mini-hidden"/>
            <input id="chReviewerId" name="chReviewerId" class="mini-hidden"/>
            <input id="enReviewerId" name="enReviewerId" class="mini-hidden"/>
            <input id="multilingualReviewerId" name="multilingualReviewerId" class="mini-hidden"/>
            <input id="businessStatus" name="businessStatus" class="mini-hidden"/>
            <input id="INST_ID_" name="INST_ID_" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold">
                    多语言术语库扩充申请单
                </caption>
                <tr>
                    <td style="text-align: center;width: 15%">设计型号：</td>
                    <td style="min-width:170px">
                        <input id="designModel" name="designModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">发起人(自动生成)：</td>
                    <td style="min-width:170px">
                        <input id="applyUser" name="applyUser" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">发起日期(自动生成)：</td>
                    <td style="min-width:170px">
                        <input id="applyDate" name="applyDate" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">中文审核人(自动生成)：</td>
                    <td style="min-width:170px">
                        <input id="chReviewer" name="chReviewer" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">英文审核人(自动生成)：</td>
                    <td style="min-width:170px">
                        <input id="enReviewer" name="enReviewer" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">多语言翻译人(自动生成)：</td>
                    <td style="min-width:170px">
                        <input id="multilingualReviewer" name="multilingualReviewer" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <%--///////////////////////////////////////////////////--%>
                <tr>
                    <td style="text-align: center;height: 500px">术语明细：</td>
                    <td colspan="3">
                        <div class="mini-toolbar" id="itemButtons">
                            <a id="addItem" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="addItem" enabled="false">添加</a>
                            <a id="deleteItem" class="mini-button btn-red" style="margin-bottom: 5px;margin-top: 5px" onclick="deleteItem"
                               enabled="false">删除</a>
                            <a id="importItem" class="mini-button btn-red" style="margin-bottom: 5px;margin-top: 5px" onclick="openImportWindow"
                               enabled="false">导入</a>
                            <a id="exportItem" class="mini-button btn-red" style="margin-bottom: 5px;margin-top: 5px" onclick="exportItem"
                               enabled="true">普通导出</a>
                            <a id="exportItemGss" class="mini-button btn-red" style="margin-bottom: 5px;margin-top: 5px" onclick="exportItemGss"
                               enabled="true">GSS导出</a>
                        </div>
                        <div id="itemListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="true" allowCellWrap="false"
                             showCellTip="true"
                             idField="id" url="${ctxPath}/serviceEngineering/core/multilingualGlossary/getItemList.do?businessId=${businessId}"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowcelledit="true" allowcellselect="true"
                             allowAlternating="true" oncellvalidation="onCellValidation">
                            <div property="columns">
                                <div type="checkcolumn" width="25"></div>
                                <div type="indexcolumn" width="35" headerAlign="center" align="center">序号</div>
                                <div field="materialCode" name="materialCode" width="100" headerAlign="center" align="center" renderer="render">物料编码
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="chinese" name="chinese" width="180" headerAlign="center" align="center" renderer="render">中文
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="chineseHit" width="180" headerAlign="center" align="center" renderer="renderChinese">匹配到的中文</div>
                                <div field="en" name="en" width="180" headerAlign="center" align="center" renderer="render">英文
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="enHit" width="180" headerAlign="center" align="center" renderer="renderChinese">匹配到的英文</div>
                                <div field="multilingualKey" name="multilingualKey" width="80" headerAlign="center" align="center" renderer="render">
                                    多语言标识
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="multilingualText" name="multilingualText" width="180" headerAlign="center" align="center"
                                     renderer="render">多语言文本
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="remark" name="remark" width="180" headerAlign="center" align="center" renderer="render">备注
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<%----%>
<div id="importWindow" title="导入窗口" class="mini-window" style="width:750px;height:280px;"
     showModal="true" showFooter="false" allowResize="true">
    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
    </p>
    <div style="display: inline-block;float: right;">
        <a id="importBtn" class="mini-button" onclick="importItem()">导入</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="closeImportWindow()">关闭</a>
    </div>
    <div class="mini-fit" style="font-size: 14px;margin-top: 30px">
        <form id="formImport" method="post">
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 30%">上传模板：</td>
                    <td style="width: 70%;">
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">多语言术语库扩充申请单明细导入模板.xls</a>
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
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/serviceEngineering/core/multilingualGlossary/exportItem.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<!--GSS导出Excel相关HTML-->
<form id="excelFormGss" action="${ctxPath}/serviceEngineering/core/multilingualGlossary/exportItemGss.do" method="post" target="excelIFrameGss">
    <input type="hidden" name="filterGss" id="filterGss"/>
</form>
<iframe id="excelIFrameGss" name="excelIFrameGss" style="display: none;"></iframe>

<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var status = "${status}";
    var jsUseCtxPath = "${ctxPath}";
    var itemListGrid = mini.get("itemListGrid");
    var formBusiness = new mini.Form("#formBusiness");
    var businessId = "${businessId}";
    var nodeVarsStr = '${nodeVars}';
    var currentTime = new Date().format("yyyy-MM-dd hh:mm:ss");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";

    var businessStatus = "";
    var isAediting = "";
    var isBchineseReviewing = "";
    var isCenglishTranslation = "";
    var isDmultilingualTranslation = "";
    var importWindow = mini.get("importWindow");

    //..
    $(function () {
        var url = jsUseCtxPath + "/serviceEngineering/core/multilingualGlossary/getDetail.do";
        $.post(
            url,
            {businessId: businessId},
            function (json) {
                formBusiness.setData(json);
                itemListGrid.load();
                businessStatus = mini.get("businessStatus").getValue();
                if (action == 'detail') {
                    formBusiness.setEnabled(false);
                    $("#detailToolBar").show();
                    //非草稿放开流程信息查看按钮
                    if (status != 'DRAFTED') {
                        $("#processInfo").show();
                    }
                    mini.get("itemListGrid").getColumn("materialCode").readOnly = true;
                    mini.get("itemListGrid").getColumn("chinese").readOnly = true;
                    mini.get("itemListGrid").getColumn("en").readOnly = true;
                    mini.get("itemListGrid").getColumn("multilingualKey").readOnly = true;
                    mini.get("itemListGrid").getColumn("multilingualText").readOnly = true;
                    mini.get("itemListGrid").getColumn("remark").readOnly = true;
                } else if (action == 'edit') {
                    mini.get("addItem").setEnabled(true);
                    mini.get("deleteItem").setEnabled(true);
                    mini.get("importItem").setEnabled(true);
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
        var data = itemListGrid.getChanges();
        if (data.length > 0) {
            formData.itemChangeData = data;
        }
        return formData;
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
        if (formData.SUB_fileListGrid) {
            delete formData.SUB_fileListGrid;
        }
        var data = itemListGrid.getChanges();
        if (data.length > 0) {
            formData.itemChangeData = data;
        }
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/multilingualGlossary/saveBusiness.do',
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message = "";
                    if (data.success) {
                        message = "数据保存成功";
                    } else {
                        message = "数据保存失败" + data.message;
                    }

                    mini.alert(message, "提示信息", function () {
                        window.location.reload();
                    });
                }
            }
        });
    }
    //..流程中的审批或者下一步
    function businessApprove(e) {
        var formValid = validBusiness();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.approve();
    }
    //..流程信息浏览
    function processInfo() {
        var instId = $("#INST_ID_").val();
        var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
        _OpenWindow({
            url: url,
            max: true,
            title: "流程图实例",
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
            if (nodeVars[i].KEY_ == 'isAediting') {
                isAediting = nodeVars[i].DEF_VAL_;
            } else if (nodeVars[i].KEY_ == 'isBchineseReviewing') {
                isBchineseReviewing = nodeVars[i].DEF_VAL_;
            } else if (nodeVars[i].KEY_ == 'isCenglishTranslation') {
                isCenglishTranslation = nodeVars[i].DEF_VAL_;
            } else if (nodeVars[i].KEY_ == 'isDmultilingualTranslation') {
                isDmultilingualTranslation = nodeVars[i].DEF_VAL_;
            }
        }
        if (isAediting != 'yes') {
            formBusiness.setEnabled(false);
            if (isBchineseReviewing == 'yes') {
                mini.get("itemListGrid").getColumn("materialCode").readOnly = true;
                mini.get("itemListGrid").getColumn("chinese").readOnly = false;
                mini.get("itemListGrid").getColumn("en").readOnly = true;
                mini.get("itemListGrid").getColumn("multilingualKey").readOnly = true;
                mini.get("itemListGrid").getColumn("multilingualText").readOnly = true;
                mini.get("itemListGrid").getColumn("remark").readOnly = false;
            } else if (isCenglishTranslation == "yes") {
                mini.get("itemListGrid").getColumn("materialCode").readOnly = true;
                mini.get("itemListGrid").getColumn("chinese").readOnly = true;
                mini.get("itemListGrid").getColumn("en").readOnly = false;
                mini.get("itemListGrid").getColumn("multilingualKey").readOnly = true;
                mini.get("itemListGrid").getColumn("multilingualText").readOnly = true;
                mini.get("itemListGrid").getColumn("remark").readOnly = false;
            } else if (isDmultilingualTranslation == "yes") {
                mini.get("itemListGrid").getColumn("materialCode").readOnly = true;
                mini.get("itemListGrid").getColumn("chinese").readOnly = true;
                mini.get("itemListGrid").getColumn("en").readOnly = true;
                mini.get("itemListGrid").getColumn("multilingualKey").readOnly = true;
                mini.get("itemListGrid").getColumn("multilingualText").readOnly = false;
                mini.get("itemListGrid").getColumn("remark").readOnly = false;
            }
        } else {
            mini.get("addItem").setEnabled(true);
            mini.get("deleteItem").setEnabled(true);
            mini.get("importItem").setEnabled(true);
        }
    }
    //..检验表单是否必填
    function validBusiness() {
        var designModel = $.trim(mini.get("designModel").getValue());
        if (!designModel) {
            return {"result": false, "message": "请填写设计型号"};
        }
        if (itemListGrid.totalCount == 0) {
            return {"result": false, "message": '请填写术语库扩充明细'};
        }
        var rows = itemListGrid.getData();
        var st = new Set();
        for (i = 0; i < rows.length; i++) {
            st.add(rows[i].multilingualKey);
        }
        if (st.size > 1) {
            return {"result": false, "message": '多语言标识不唯一'};
        }
        //明细表单验证
        itemListGrid.validate();
        if (!itemListGrid.isValid()) {
            var error = itemListGrid.getCellErrors()[0];
            itemListGrid.beginEditCell(error.record, error.column);
            mini.alert(error.column.header + error.errorText);
            return;
        }
        //语言匹配完成再提交，要不然会出现流程驱动的细节更改被最终流程再次驱动的更改给覆盖
        if (itemListGrid.getChanges().length > 0) {
            return {"result": false, "message": '请先点击保存草稿或暂存信息进行语言库的预匹配'};
        }
        return {"result": true};
    }
    //..
    function onCellValidation(e) {
        if (e.field == 'materialCode' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'multilingualKey' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'chinese' && (!e.value || e.value == '') && businessStatus == 'B-chineseReviewing') {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'en' && (!e.value || e.value == '') && businessStatus == 'C-englishTranslation') {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'multilingualText' && (!e.value || e.value == '') && businessStatus == 'D-multilingualTranslation') {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
    }
    //..删除明细
    function deleteItem() {
        var row = itemListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
            return;
        }
        delRowGrid("itemListGrid");
    }
    //..添加
    function addItem() {
        if (businessId == null || businessId == "") {
            mini.alert('请先保存草稿生成主信息再添加！');
            return;
        }
        var newRow = {}
        itemListGrid.addRow(newRow, 0);
    }
    //..普通导出
    function exportItem() {
        var rows = itemListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        var params = [];
        for (var i = 0; i < rows.length; i++) {
            var obj = {};
            obj.name = "id";
            obj.value = rows[i].id
            params.push(obj);
        }
        $("#filter").val(mini.encode(params));
        var excelForm = $("#excelForm");
        excelForm.submit();
    }
    //..GSS导出
    function exportItemGss() {
        var rows = itemListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        var params = [];
        for (var i = 0; i < rows.length; i++) {
            var obj = {};
            obj.name = "materialCode";
            obj.value = rows[i].materialCode
            params.push(obj);
        }
        $("#filterGss").val(mini.encode(params));
        var excelFormGss = $("#excelFormGss");
        excelFormGss.submit();
    }
    //..所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
    //..根据匹配场景渲染中文
    function renderChinese(e) {
        if (e.value != null && e.value != "") {
            if (e.record.chineseMaterialHit != null && e.record.chineseMaterialHit != "" && e.record.chineseMaterialHit == 'true') {
                var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;color: #0b7edc" >' + e.value + '</span>';
            } else if (e.record.chineseTextHit != null && e.record.chineseTextHit != "" && e.record.chineseTextHit == 'true') {
                var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;color: #ef1b01" >' + e.value + '</span>';
            } else {
                var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            }
            return html;
        }
    }
    //..根据匹配场景渲染英文
    function renderEn(e) {
        if (e.value != null && e.value != "") {
            if (e.record.enMaterialHit != null && e.record.enMaterialHit != "" && e.record.enMaterialHit == 'true') {
                var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;color: #0b7edc" >' + e.value + '</span>';
            } else if (e.record.enTextHit != null && e.record.enTextHit != "" && e.record.enTextHit == 'true') {
                var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;color: #ef1b01" >' + e.value + '</span>';
            } else {
                var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            }
            return html;
        }
    }
    //..
    function openImportWindow() {
        if (businessId == null || businessId == "") {
            mini.alert('请先保存草稿生成主信息再导入！');
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
            xhr.open('POST', jsUseCtxPath + '/serviceEngineering/core/multilingualGlossary/importExcel.do?mainId=' + businessId, false);
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
        itemListGrid.load();
    }
    //..
    function downImportTemplate() {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/multilingualGlossary/importTemplateDownload.do");
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
