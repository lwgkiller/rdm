<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>海外技术资料传递流程信息</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()"><spring:message code="page.zlcdEdit.ProcessInfo" /></a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()"><spring:message code="page.zlcdEdit.CloseWindow" /></a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 98%">
        <form id="formZlcd" method="post">
            <input id="zlcdId" name="zlcdId" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold;font-size: 20px !important;">
                    <spring:message code="page.zlcdEdit.Technical" />
                </caption>
                <tr>
                    <td style="text-align: center;width: 17%"><spring:message code="page.zlcdEdit.Application" /></td>
                    <td style="width: 17%;min-width:170px">
                        <input id="dept" name="deptId" textname="deptName"
                               property="editor" class="mini-dep rxc" plugins="mini-dep"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                    <td style="text-align: center;width: 10%"><spring:message code="page.zlcdEdit.Applicant" />：</td>
                    <td style="width: 15%;min-width:170px">
                        <input id="apply" name="applyId" textname="applyName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"
                        />
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.zlcdEdit.Submission" />：
                    </td>
                    <td>
                        <input id="CREATE_TIME_" name="CREATE_TIME_" readonly="readonly" class="mini-datepicker"
                               format="yyyy-MM-dd"
                               style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width:15%;height: 500px"><spring:message code="page.zlcdEdit.Detailed" /></td>
                    <td colspan="5">
                        <div style="margin-top: 5px;margin-bottom: 2px">
                            <a id="addDetail" class="mini-button" onclick="addDetail()"><spring:message code="page.zlcdEdit.Add" /></a>
                            <a id="removeDetail" class="mini-button" onclick="removeDetail()"><spring:message code="page.zlcdEdit.Remove" /></a>
                            <a id="addFile" class="mini-button" onclick="fjupload()"><spring:message code="page.zlcdEdit.Upload" /></a>
                            <span style="color: red"><spring:message code="page.zlcdEdit.Note" /></span>
                        </div>
                        <div id="detailListGrid" class="mini-datagrid" style="width: 100%; height: 85%"
                             allowResize="false" allowCellWrap="true"
                             idField="id" url="${ctxPath}/rdm/core/Zlcd/getDetailList.do?belongId=${zlcdId}"
                             autoload="true"
                             allowCellEdit="true" allowCellSelect="true"
                             multiSelect="false" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="checkcolumn" width="10"></div>
                                <div field="content" width="60" headerAlign="center" align="center"><spring:message code="page.zlcdEdit.Content" />
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="purpose" width="60" headerAlign="center" align="center"><spring:message code="page.zlcdEdit.Purpose" />
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="fileType" width="30" headerAlign="center" align="center"><spring:message code="page.zlcdEdit.FileType" />
                                    <input property="editor" class="mini-combobox" style="width:98%;"
                                           textField="value" valueField="key" emptyText="<spring:message code="page.zlcdEdit.Please" />..."
                                           required="false" allowInput="false" showNullItem="true"
                                           nullItemText="<spring:message code="page.zlcdEdit.Please" />..."
                                           data="[
										   {'key' : 'Pdf','value' : 'Pdf'}
										   ,{'key' : '3D','value' : '3D'}
										   ,{'key' : '2D','value' : '2D'}
										   ,{'key' : 'Excel','value' : 'Excel'}]"
                                    /></div>
                                <div field="fileName" width="40" headerAlign="center" align="center"><spring:message code="page.zlcdEdit.FileName" />
                                </div>
                                <div field="action" width="30" headerAlign='center' align="center"
                                     renderer="operationRendererD"><spring:message code="page.zlcdEdit.Action" />
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%;height: 250px "><spring:message code="page.zlcdEdit.List" />：</td>
                    <td colspan="5">
                        <div style="margin-top: 2px;margin-bottom: 2px">
                            <a id="addSpFile" class="mini-button" onclick="spupload()"><spring:message code="page.zlcdEdit.Upload" /></a>
                        </div>
                        <div id="spFileListGrid" class="mini-datagrid" style="width: 100%; height: 85%"
                             allowResize="false"
                             idField="id" autoload="true"
                             url="${ctxPath}/rdm/core/Zlcd/getZlcdFileList.do?fileModel=sp&belongId=${zlcdId}"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                             <div property="columns">
                                <div field="fileName" width="140" headerAlign="center" align="center"><spring:message code="page.zlcdEdit.FileName" /></div>
                                <div field="fileSize" width="80" headerAlign="center" align="center"><spring:message code="page.zlcdEdit.FileSize" /></div>
                                <div field="action" width="100" headerAlign='center' align="center"
                                     renderer="operationRendererZ"><spring:message code="page.zlcdEdit.Action" />
                                </div>
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
    var nodeVarsStr = '${nodeVars}';
    var action = "${action}";
    var status = "${status}";
    var jsUseCtxPath = "${ctxPath}";
    var currentTime = "${currentTime}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var deptId = "${deptId}";
    var deptName = "${deptName}";
    var detailListGrid = mini.get("detailListGrid");
    var spFileListGrid = mini.get("spFileListGrid");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
    var formZlcd = new mini.Form("#formZlcd");
    var zlcdId = "${zlcdId}";
    var isFgld = ${isFgld};


    function operationRendererZ(e) {
        var record = e.record;
        var cellHtml = '';
        if (!record.fileId) {
            return cellHtml;
        }
        cellHtml = returnZlcdPreviewSpan(record.fileName, record.fileId, record.belongId, coverContent);
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + zlcdEdit_download + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadZlcdFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\')"><spring:message code="page.zlcdEdit.download" /></span>';
        //增加删除按钮
        if (action != "detail" && record.CREATE_BY_ == currentUserId) {
            var deleteUrl = "/rdm/core/Zlcd/deleteZlcdFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + zlcdEdit_delete + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteZlcdFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\',\'' + deleteUrl + '\')"><spring:message code="page.zlcdEdit.delete" /></span>';
        }
        return cellHtml;
    }
    function operationRendererD(e) {
        var record = e.record;
        var cellHtml = 'No operation authority';
        if (!record.fileId) {
            return cellHtml='';
        }
        if (isFgld||record.creator == currentUserId || record.CREATE_BY_ == currentUserId){
            cellHtml = returnZlcdPreviewSpan(record.fileName, record.fileId, record.detailId, coverContent);
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + zlcdEdit_download + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downLoadZlcdFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.detailId + '\')"><spring:message code="page.zlcdEdit.download" /></span>';
        }
        //增加删除按钮
        if (action != "detail" && record.CREATE_BY_ == currentUserId) {
            var deleteUrl = "/rdm/core/Zlcd/deleteZlcdFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + zlcdEdit_delete + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteZlcdFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.detailId + '\',\'' + deleteUrl + '\')"><spring:message code="page.zlcdEdit.delete" /></span>';
        }
        return cellHtml;
    }

    var first = "";
    var second = "";
    var third = "";
    var forth = "";
    $(function () {
        if (zlcdId) {
            var url = jsUseCtxPath + "/rdm/core/Zlcd/getZlcdDetail.do";
            $.post(
                url,
                {zlcdId: zlcdId},
                function (json) {
                    formZlcd.setData(json);
                });
        } else {
            mini.get("apply").setValue(currentUserId);
            mini.get("apply").setText(currentUserName);
            mini.get("dept").setValue(deptId);
            mini.get("dept").setText(deptName);
        }
        mini.get("addFile").setEnabled(false);
        mini.get("addSpFile").setEnabled(false);
        //变更入口
        if (action == 'task') {
            taskActionProcess();
        } else if (action == "detail") {
            formZlcd.setEnabled(false);
            detailListGrid.setAllowCellEdit(false);
            mini.get("addFile").setEnabled(false);
            mini.get("addDetail").setEnabled(false);
            mini.get("removeDetail").setEnabled(false);
            $("#detailToolBar").show();
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        }
    });


    function getData() {
        var formData = _GetFormJsonMini("formZlcd");
        formData.detail = detailListGrid.getChanges();
        //此处用于向后台产生流程实例时替换标题中的参数时使用
        // formData.bos=[];
        // formData.vars=[{key:'companyName',val:formData.companyName}];
        return formData;
    }

    function saveZlcd(e) {

        window.parent.saveDraft(e);
    }


    function startZlcdProcess(e) {
        var formValid = validFirst();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.startProcess(e);
    }

    function zlcdApprove() {
        //编制阶段的下一步需要校验表单必填字段
        if (first == 'yes') {
            var formValid = validFirst();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if (second == 'yes') {
            var formValid = validSecond();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if (third == 'yes') {
            var formValid = validThird();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if (forth == 'yes') {
            var formValid = validForth();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        //检查通过
        window.parent.approve();
    }

    function validFirst() {
        var dept = $.trim(mini.get("dept").getValue());
        if (!dept) {
            return {"result": false, "message": zlcdEdit_zlcd1};
        }
        var apply = $.trim(mini.get("apply").getValue());
        if (!apply) {
            return {"result": false, "message": zlcdEdit_zlcd2};
        }
        var CREATE_TIME_ = $.trim(mini.get("CREATE_TIME_").getValue());
        if (!CREATE_TIME_) {
            return {"result": false, "message": zlcdEdit_zlcd3};
        }
        var detail = detailListGrid.getData();
        if (detail.length < 1) {
            return {"result": false, "message": zlcdEdit_zlcd4};
        } else {
            for (var i = 0; i < detail.length; i++) {
                if (detail[i].content == undefined || detail[i].content == "") {
                    return {"result": false, "message": zlcdEdit_zlcd5};
                }
                if (detail[i].purpose == undefined || detail[i].purpose == "") {
                    return {"result": false, "message": zlcdEdit_zlcd6};
                }
                if (detail[i].fileType == undefined || detail[i].fileType == "") {
                    return {"result": false, "message": zlcdEdit_zlcd7};
                }
            }
        }
        return {"result": true};
    }

    function validSecond() {
        var detail = detailListGrid.getData();
        for (var i = 0; i < detail.length; i++) {
            if ((detail[i].fileType == "Pdf" || detail[i].fileType == "3D" || detail[i].fileType == "2D")
                && (detail[i].fileName == undefined || detail[i].fileName == "")) {
                return {"result": false, "message": zlcdEdit_zlcd8};
            }
        }
        return {"result": true};
    }


    function validThird() {
        var detail = spFileListGrid.getData();
        if (detail.length < 1) {
            return {"result": false, "message": zlcdEdit_zlcd9};
        }
        return {"result": true};
    }


    function validForth() {
        var detail = detailListGrid.getData();
        for (var i = 0; i < detail.length; i++) {
            if (detail[i].fileType == "Excel"
                && (detail[i].fileName == undefined || detail[i].fileName == "")) {
                return {"result": false, "message": zlcdEdit_zlcd10};
            }
        }
        return {"result": true};
    }

    function processInfo() {
        var instId = $("#instId").val();
        var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
        _OpenWindow({
            url: url,
            max: true,
            title: zlcdEdit_zlcd11,
            width: 800,
            height: 600
        });
    }


    function taskActionProcess() {
        //获取上一环节的结果和处理人
        bpmPreTaskTipInForm();

        //获取环境变量
        if (!nodeVarsStr) {
            nodeVarsStr = "[]";
        }
        var nodeVars = $.parseJSON(nodeVarsStr);
        for (var i = 0; i < nodeVars.length; i++) {
            if (nodeVars[i].KEY_ == 'first') {
                first = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'second') {
                second = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'third') {
                third = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'forth') {
                forth = nodeVars[i].DEF_VAL_;
            }
        }
        formZlcd.setEnabled(false);
        detailListGrid.setAllowCellEdit(false);
        mini.get("addFile").setEnabled(false);
        mini.get("addDetail").setEnabled(false);
        mini.get("removeDetail").setEnabled(false);
        mini.get("addSpFile").setEnabled(false);
        if (first == 'yes') {
            detailListGrid.setAllowCellEdit(true);
            mini.get("CREATE_TIME_").setEnabled(true);
            mini.get("apply").setEnabled(true);
            mini.get("dept").setEnabled(true);
            mini.get("addDetail").setEnabled(true);
            mini.get("removeDetail").setEnabled(true);
        }
        if (second == 'yes') {
            mini.get("addFile").setEnabled(true);
        }
        if (third == 'yes') {
            mini.get("addSpFile").setEnabled(true);
        }
        if (forth == 'yes') {
            mini.get("addFile").setEnabled(true);
        }

    }
    function spupload() {
        var zlcdId = mini.get("zlcdId").getValue();
        if (!zlcdId) {
            mini.alert(zlcdEdit_zlcd12);
            return;
        }
        mini.open({
            title: zlcdEdit_zlcd13,
            url: jsUseCtxPath + "/rdm/core/Zlcd/openUploadWindow.do?fileModel=sp&belongd=" + zlcdId,
            width: 850,
            height: 550,
            showModal: true,
            allowResize: true,
            ondestroy: function () {
                if (spFileListGrid) {
                    spFileListGrid.load();
                }
            }
        });
    }

    function fjupload() {
        var selected = detailListGrid.getSelected();
        if (!selected) {
            mini.alert(zlcdEdit_zlcd14);
            return;
        }
        if (selected.fileName && selected.fileName != "") {
            mini.alert(zlcdEdit_zlcd15);
            return;
        }
        if (second=="yes"&&selected.fileType && selected.fileType == "Excel") {
            mini.alert(zlcdEdit_zlcd16);
            return;
        }
        mini.open({
            title: zlcdEdit_zlcd13,
            url: jsUseCtxPath + "/rdm/core/Zlcd/openUploadWindow.do?fileModel=xx&belongd=" + selected.detailId,
            width: 850,
            height: 550,
            showModal: true,
            allowResize: true,
            ondestroy: function () {
                if (detailListGrid) {
                    detailListGrid.load();
                }
            }
        });
    }

    function returnZlcdPreviewSpan(fileName, fileId, formId, coverContent) {
        var s = '';
        if (fileName == "") {
            return s;
        }
        var fileType = getFileType(fileName);
        if (fileType == 'other') {
            s = '<span  title=' + zlcdEdit_zlcd17 + ' style="color: silver" ><spring:message code="page.zlcdEdit.Preview" /></span>';
        } else if (fileType == 'pdf') {
            var url = '/rdm/core/Zlcd/zlcdPdfPreview';
            s = '<span  title=' + zlcdEdit_zlcd17 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')"><spring:message code="page.zlcdEdit.Preview" /></span>';
        } else if (fileType == 'office') {
            var url = '/rdm/core/Zlcd/zlcdOfficePreview';
            s = '<span  title=' + zlcdEdit_zlcd17 + ' style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')"><spring:message code="page.zlcdEdit.Preview" /></span>';
        } else if (fileType == 'pic') {
            var url = '/rdm/core/Zlcd/zlcdImagePreview';
            s = '<span  title=' + zlcdEdit_zlcd17 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')"><spring:message code="page.zlcdEdit.Preview" /></span>';
        }
        return s;
    }

    function downLoadZlcdFile(fileName, fileId, formId) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + '/rdm/core/Zlcd/zlcdPdfPreview.do?action=download');
        var inputFileName = $("<input>");
        inputFileName.attr("type", "hidden");
        inputFileName.attr("name", "fileName");
        inputFileName.attr("value", fileName);
        var inputstandardId = $("<input>");
        inputstandardId.attr("type", "hidden");
        inputstandardId.attr("name", "formId");
        inputstandardId.attr("value", formId);
        var inputFileId = $("<input>");
        inputFileId.attr("type", "hidden");
        inputFileId.attr("name", "fileId");
        inputFileId.attr("value", fileId);
        $("body").append(form);
        form.append(inputFileName);
        form.append(inputstandardId);
        form.append(inputFileId);
        form.submit();
        form.remove();
    }


    function deleteZlcdFile(fileName, fileId, formId, urlValue) {
        mini.confirm(zlcdEdit_zlcd18, zlcdEdit_zlcd19,
            function (action) {
                if (action == "ok") {
                    var url = jsUseCtxPath + urlValue;
                    var data = {
                        fileName: fileName,
                        id: fileId,
                        formId: formId
                    };
                    $.ajax({
                        url: url,
                        method: 'post',
                        contentType: 'application/json',
                        data: mini.encode(data),
                        success: function (json) {
                            if (detailListGrid) {
                                detailListGrid.load();
                            }
                            if (spFileListGrid) {
                                spFileListGrid.load();
                            }
                        }
                    });
                }
            }
        );
    }


    function addDetail() {
        var formId = mini.get("zlcdId").getValue();
        if (!formId) {
            mini.alert(zlcdEdit_zlcd20);
            return;
        } else {
            var row = {};
            detailListGrid.addRow(row);
        }
    }

    function removeDetail() {
        var selecteds = detailListGrid.getSelecteds();
        if (selecteds.length <= 0) {
            mini.alert(zlcdEdit_zlcd21);
            return;
        }
        var deleteArr = [];
        for (var i = 0; i < selecteds.length; i++) {
            var row = selecteds[i];
            deleteArr.push(row);
        }
        detailListGrid.removeRows(deleteArr);
    }


</script>
</body>
</html>
