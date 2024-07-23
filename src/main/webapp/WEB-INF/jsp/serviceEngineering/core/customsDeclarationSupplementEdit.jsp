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
                    报关信息补充
                </caption>
                <tr>
                    <td style="text-align: center;width: 15%">补充人：</td>
                    <td style="min-width:170px">
                        <input id="supplementUserId" name="supplementUserId" textname="supplementUser"
                               class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%">补充时间：</td>
                    <td style="min-width:170px">
                        <input id="supplementTime" name="supplementTime" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                               valueType="string" showTime="false" showOkButton="true" showClearButton="false" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <%--数据明细--%>
                <tr>
                    <td colspan="4" style="text-align: center;height: 50px">填写说明：
                        1、【品名】、【品牌类型】、【出口享惠情况】、【品牌】、【 GTIN 】、【 CAS 】、【其他】 内容不需要填写
                        2、 按照报关要素所列信息进行填写，如有疑问，联系服务工程所李辉处理。
                    </td>
                </tr>
                <%--itemListGrid--------------------------------------------------------------------%>
                <tr>
                    <td colspan="4" style="height: 500px">
                        <div id="itemsButtons" class="mini-toolbar">
                            <a id="exportItem" class="mini-button" style="margin-right: 5px" plain="true" onclick="exportItem()">导出</a>
                            <a id="openImportWindow" class="mini-button" style="margin-right: 5px" plain="true" onclick="openImportWindow">导入</a>
                        </div>
                        <div id="itemListGrid" class="mini-datagrid" style="height: 100%" allowResize="true" allowCellWrap="false"
                             showCellTip="true" autoload="true" idField="id" multiSelect="true" showPager="false"
                             showColumnsMenu="false" allowcelledit="true" allowcellselect="true" allowAlternating="true"
                             oncellvalidation="onCellValidation">
                            <div property="columns">
                                <div type="checkcolumn" width="40" trueValue="1" falseValue="0"></div>
                                <div field="machineModel" width="150" headerAlign="center" align="center" renderer="render">适用机型</div>
                                <div field="materialCode" width="100" headerAlign="center" align="center">物料号</div>
                                <div field="materialDescription" width="150" headerAlign="center" align="center" renderer="render">物料描述</div>
                                <div field="hsCode" width="120" headerAlign="center" align="center">HS编码</div>
                                <div field="elements" width="300" headerAlign="center" align="center" renderer="render">报关要素</div>
                                <div field="elementsFill" width="400" headerAlign="center" align="center" renderer="render">报关要素补充填写
                                    <input id="elementsFill" property="editor" class="mini-textarea" style="width:98%;"/>
                                </div>
                                <div field="additionalInfo" width="200" headerAlign="center" align="center" renderer="render">补充信息</div>
                                <div field="netWeight" width="80" headerAlign="center" align="center">净重
                                    <input id="netWeight" property="editor" class="mini-spinner" style="width:98%;" maxValue="99999"
                                           decimalPlaces="2"/>
                                </div>
                                <div field="remarks" width="200" headerAlign="center" align="center" renderer="render">备注</div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/serviceEngineering/core/customsDeclarationSupplement/exportItemList.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<%--导入窗口--%>
<div id="importWindow" title="导入窗口" class="mini-window" style="width:750px;height:280px;" showModal="true" showFooter="false" allowResize="true">
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
        var url = jsUseCtxPath + "/serviceEngineering/core/customsDeclarationSupplement/getDetail.do";
        $.post(
            url,
            {businessId: businessId},
            function (json) {
                formBusiness.setData(json);
                var recordItems = JSON.parse(json.recordItems);
                itemListGrid.setData(recordItems);
                if (action == 'detail') {
                    formBusiness.setEnabled(false);
                    itemListGrid.setAllowCellEdit(false);
                    $("#detailToolBar").show();
                    //非草稿放开流程信息查看按钮
                    if (status != 'DRAFTED') {
                        $("#processInfo").show();
                    }
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
        var data = itemListGrid.getData();
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
        formBusiness.setEnabled(false);
        if (codeName != "B") {
            itemListGrid.setAllowCellEdit(false);
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
        window.parent.startProcess(e);
    }
    //..流程中暂存信息（如编制阶段）
    function saveBusinessInProcess() {
        var formData = _GetFormJsonMini("formBusiness");
        if (formData.SUB_itemListGrid) {
            delete formData.SUB_itemListGrid;
        }
        var data = itemListGrid.getData();
        if (data.length > 0) {
            formData.recordItems = data;
        }
        formData.bos = [];
        formData.vars = [];
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/customsDeclarationSupplement/saveBusiness.do',
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
        if (codeName == "B") {
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
        if (e.field == 'elementsFill' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'netWeight' && !e.value) {
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
    function exportItem() {
        var params = [];
        var obj = {};
        obj.name = "businessId";
        obj.value = businessId;
        params.push(obj);
        $("#filter").val(mini.encode(params));
        var excelForm = $("#excelForm");
        excelForm.submit();
    }
    //..
    function openImportWindow() {
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
                '/serviceEngineering/core/customsDeclarationSupplement/importItem.do?businessId=' + businessId, false);
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
