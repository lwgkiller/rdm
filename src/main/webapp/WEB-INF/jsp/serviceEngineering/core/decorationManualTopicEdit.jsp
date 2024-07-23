<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>装修手册Topic</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveBusiness" class="mini-button" onclick="saveBusiness()"><spring:message code="page.decorationManualTopicEdit.name" /></a>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()"><spring:message code="page.decorationManualTopicEdit.name1" /></a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;padding: 0">
        <form id="formBusiness" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                <caption style="font-weight: bold;font-size: 20px !important;"><spring:message code="page.decorationManualTopicEdit.name2" /></caption>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualTopicEdit.name3" />:</td>
                    <td style="min-width:170px">
                        <input id="businessNo" name="businessNo" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualTopicEdit.name4" />:</td>
                    <td style="min-width:170px">
                        <input id="chapter" name="chapter" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringDecorationManualChapter"
                               valueField="key" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualTopicEdit.name5" />:</td>
                    <td style="min-width:170px">
                        <input id="system" name="system" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringDecorationManualSystem"
                               valueField="key" textField="value"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualTopicEdit.name6" />:</td>
                    <td style="min-width:170px">
                        <input id="topicCode" name="topicCode" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualTopicEdit.name7" />:</td>
                    <td style="min-width:170px">
                        <input id="topicName" name="topicName" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualTopicEdit.name8" />:</td>
                    <td style="min-width:170px">
                        <input id="topicType" name="topicType" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringDecorationManualTopictype"
                               valueField="key" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualTopicEdit.name9" />:</td>
                    <td style="min-width:170px">
                        <input id="materialCode" name="materialCode" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualTopicEdit.name10" />:</td>
                    <td style="min-width:170px">
                        <input id="productSerie" name="productSerie" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringDecorationManualProductSerie"
                               valueField="key" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualTopicEdit.name25" />:</td>
                    <td style="min-width:170px">
                        <input id="salesArea" name="salesArea" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringDecorationManualSalesArea"
                               valueField="key" textField="value"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualTopicEdit.name11" />:</td>
                    <td style="min-width:170px">
                        <input id="salesModel" name="salesModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualTopicEdit.name12" />:</td>
                    <td style="min-width:170px">
                        <input id="designModel" name="designModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualTopicEdit.name13" />:</td>
                    <td style="min-width:170px">
                        <input id="manualVersion" name="manualVersion" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualTopicEdit.name14" />:</td>
                    <td>
                        <input id="manualStatus" name="manualStatus" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualTopicEdit.name15" />:</td>
                    <td style="min-width:170px">
                        <input id="isPS" name="isPS" class="mini-combobox" style="width:98%;"
                               valueField="key" textField="value"
                               data="[{'key' : '是','value' : '是'},{'key' : '否','value' : '否'}]"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.decorationManualTopicEdit.name16" />：</td>
                    <td colspan="3">
						<textarea id="remark" name="remark" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:98%;height:150px;line-height:25px;" label="" datatype="varchar" length="640"
                                  vtype="length:640" minlen="0" allowinput="true" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <%--///////////////////////////////////////////////////--%>
                <tr>
                    <td style="text-align: center;height: 400px"><spring:message code="page.decorationManualTopicEdit.name17" />：</td>
                    <td colspan="3">
                        <div style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="addBusinessFile()"><spring:message code="page.decorationManualTopicEdit.name18" /></a>
                            <span style="color: red"><spring:message code="page.decorationManualTopicEdit.name19" /></span>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
                             idField="id" autoload="true"
                             url="${ctxPath}/serviceEngineering/core/decorationManualTopic/getFileList.do?businessId=${businessId}"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20"><spring:message code="page.decorationManualTopicEdit.name20" /></div>
                                <div field="fileName" align="center" headerAlign="center" width="150"><spring:message code="page.decorationManualTopicEdit.name21" /></div>
                                <div field="fileSize" align="center" headerAlign="center" width="60"><spring:message code="page.decorationManualTopicEdit.name22" /></div>
                                <div field="fileDesc" align="center" headerAlign="center" width="100"><spring:message code="page.decorationManualTopicEdit.name23" /></div>
                                <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer"><spring:message code="page.decorationManualTopicEdit.name24" /></div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<%----%>
<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var jsUseCtxPath = "${ctxPath}";
    var obj =${obj};
    var currentTime = "${currentTime}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
    var formBusiness = new mini.Form("#formBusiness");
    var businessId = "${businessId}";
    var fileListGrid = mini.get("fileListGrid");
    //..
    $(function () {
        formBusiness.setData(obj);
        if (action == 'detail') {
            formBusiness.setEnabled(false);
            mini.get("saveBusiness").setEnabled(false);
            mini.get("addFile").setEnabled(false);
        } else if (action == "copy") {
            mini.get("id").setValue("");
            mini.get("manualVersion").setValue("");
            mini.get("manualStatus").setValue("编辑中");
            fileListGrid.reload();
        } else if (action == "add") {
            mini.get("manualVersion").setValue("A");
            mini.get("manualStatus").setValue("编辑中");
        }
    });
    //..
    function saveBusiness() {
        var formValid = validBusiness();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        var formData = _GetFormJsonMini("formBusiness");
        $.ajax({
            url: jsUseCtxPath + "/serviceEngineering/core/decorationManualTopic/saveBusiness.do",
            type: 'POST',
            contentType: 'application/json',
            data: mini.encode(formData),
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, decorationManualTopicEdit_name, function () {
                        if (returnData.success) {
                            var url = jsUseCtxPath + "/serviceEngineering/core/decorationManualTopic/editPage.do?" +
                                "id=" + returnData.data + "&action=edit";
                            window.location.href = url;
                        }
                    });
                }
            }
        });
    }
    //..
    function validBusiness() {
        var chapter = $.trim(mini.get("chapter").getValue());
        if (!chapter) {
            return {"result": false, "message": decorationManualTopicEdit_name1};
        }
        var system = $.trim(mini.get("system").getValue());
        if (!system) {
            return {"result": false, "message": decorationManualTopicEdit_name2};
        }
        var topicType = $.trim(mini.get("topicType").getValue());
        if (!topicType) {
            return {"result": false, "message": decorationManualTopicEdit_name3};
        }
        var productSerie = $.trim(mini.get("productSerie").getValue());
        if (!productSerie) {
            return {"result": false, "message": decorationManualTopicEdit_name4};
        }
        var manualVersion = $.trim(mini.get("manualVersion").getValue());
        if (!manualVersion) {
            return {"result": false, "message": decorationManualTopicEdit_name5};
        }
        var isPS = $.trim(mini.get("isPS").getValue());
        if (!isPS) {
            return {"result": false, "message": decorationManualTopicEdit_name6};
        }
        return {"result": true};
    }
    //..
    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnPreviewSpan(record.fileName, record.id, record.mainId, coverContent);
        //和pdf预览用的同一个url
        var downLoadUrl = '/serviceEngineering/core/decorationManualTopic/PdfPreviewAndAllDownload.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + decorationManualTopicEdit_name7 + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainId + '\',\'' + downLoadUrl + '\')">' + decorationManualTopicEdit_name7 + '</span>';
        //增加删除按钮
        if (action == 'edit') {
            var deleteUrl = "/serviceEngineering/core/decorationManualTopic/delFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + decorationManualTopicEdit_name8 + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainId + '\',\'' + deleteUrl + '\')">' + decorationManualTopicEdit_name8 + '</span>';
        }
        return cellHtml;
    }
    //..
    function returnPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title=' + decorationManualTopicEdit_name9 + ' style="color: silver" >' + decorationManualTopicEdit_name9 + '</span>';
        } else if (fileType == 'pdf') {
            var url = '/serviceEngineering/core/decorationManualTopic/PdfPreview.do';
            s = '<span  title=' + decorationManualTopicEdit_name9 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + decorationManualTopicEdit_name9 + '</span>';
        } else if (fileType == 'office') {
            var url = '/serviceEngineering/core/decorationManualTopic/OfficePreview.do';
            s = '<span  title=' + decorationManualTopicEdit_name9 + ' style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + decorationManualTopicEdit_name9 + '</span>';
        } else if (fileType == 'pic') {
            var url = '/serviceEngineering/core/decorationManualTopic/ImagePreview.do';
            s = '<span  title=' + decorationManualTopicEdit_name9 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + decorationManualTopicEdit_name9 + '</span>';
        }
        return s;
    }
    //..
    function addBusinessFile() {
        var businessId = mini.get("id").getValue();
        if (!businessId) {
            mini.alert(decorationManualTopicEdit_name10);
            return;
        }
        mini.open({
            title: decorationManualTopicEdit_name11,
            url: jsUseCtxPath + "/serviceEngineering/core/decorationManualTopic/fileUploadWindow.do?businessId=" + businessId,
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
</script>
</body>
</html>
