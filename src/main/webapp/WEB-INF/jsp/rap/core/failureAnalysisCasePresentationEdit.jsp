<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html >
<html>
<head>
    <title>动力所故障分析案例提报</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>

</head>
<body>

<div id="spEditToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="saveTopic" class="mini-button" onclick="saveBusinessInProcess()">管理员保存</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
    </div>
</div>

<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow1" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>

<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;height:98%;padding: 0">
        <form id="businessForm" method="post" style="height: 95%;width: 98%">
            <input class="mini-hidden" id="id" name="id"/>
            <input class="mini-hidden" id="CREATE_BY_" name="CREATE_BY_"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <p style="text-align: center;font-size: 20px;font-weight: bold;margin-top: 20px">动力所故障分析案例提报</p>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="text-align: center;width: 20%">申请人：</td>
                    <td style="min-width:170px">
                        <input id="createrName" name="createrName" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%">申请年月：</td>
                    <td style="min-width:170px">
                        <input id="yearMonth" name="yearMonth" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">故障案例名称：<span
                            style="color:red">*</span></td>
                    <td colspan="3" style="min-width:170px">
                        <input id="caseName" name="caseName" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>

                <tr>
                    <td style="text-align: center;width: 14%;height:10px">故障案例附件列表：</td>
                    <td colspan="3" height="60px">
                        <div class="mini-toolbar" id="caseFileUpload" style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addCaseFile" class="mini-button" onclick="addCaseFile('${applyId}')">上传附件</a>
                            <a id="downloadFile" class="mini-button" onclick="downloadTemp()">下载模板</a>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid"
                             allowResize="false"
                             idField="id"
                             url="${ctxPath}/environment/core/failureAnalysisCase/fileList.do?applyId=${applyId}"
                             autoload="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false"
                             allowAlternating="true"
                             style="height:150px;"
                        >
                        <div property="columns">
                            <div type="indexcolumn" align="center" width="20">序号</div>
                            <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
                            <div field="fileDesc" width="80" headerAlign="center" align="center">附件描述
                            </div>
                            <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                            <div field="action" width="100" headerAlign='center' align="center"
                                 renderer="operationRenderer">操作
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
    var jsUseCtxPath = "${ctxPath}";
    var nodeVarsStr = '${nodeVars}';
    var businessForm = new mini.Form("#businessForm");
    var fileListGrid = mini.get("fileListGrid");
    var action = "${action}";
    var applyId = "${applyId}";
    var instId = "${instId}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var isSuozhang = "${isSuozhang}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
    var stageName = "";

    $(function () {
        var url = jsUseCtxPath + "/environment/core/failureAnalysisCase/getJson.do";
        $.post(
            url,
            {id: applyId},
            function (json) {
                businessForm.setData(json);
            });
        if (action == 'detail') {
            businessForm.setEnabled(false);
            $("#detailToolBar").show();
            $("#caseFileUpload").hide();
            $("#processInfo").show();
        } else if (action == 'task') {
            taskActionProcess();
        }else if (action == "spEdit") {
            $("#spEditToolBar").show();
        }
    });

    function getData() {
        var formData = _GetFormJsonMini("businessForm");
        if (formData.SUB_fileListGrid) {
            delete formData.SUB_fileListGrid;
        }
        if (formData.SUB_demandGrid) {
            delete formData.SUB_demandGrid;
        }
        return formData;
    }

    //保存草稿
    function saveDraft(e) {
        var resultData = startCheck();
        if (!resultData.result) {
            mini.alert(resultData.message);
            return;
        }
        window.parent.saveDraft(e);
    }

    //发起流程
    function startProcess(e) {
        var resultData = startCheck();
        if (!resultData.result) {
            mini.alert(resultData.message);
            return;
        }
        var formValid = validCaseFile();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.startProcess(e);
    }

    //下一步审批
    function projectApprove() {
        //编制阶段的下一步需要校验表单必填字段
        if (stageName == 'sqrbz') {
            var formValid = validCaseFile();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        //检查通过
        window.parent.approve();
    }
    
    function validCaseFile() {
        var caseName = $.trim(mini.get("caseName").getValue());
        if (!caseName) {
            return {"result": false, "message": "请填写故障案例名称！"};
        }
        var demandGridData = fileListGrid.getData();
        if (demandGridData.length == 0) {
            return {"result": false, "message": "请上传故障案例附件！"};
        }
        return {"result": true};
    }

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

    function taskActionProcess() {
        //获取上一环节的结果和处理人
        bpmPreTaskTipInForm();
        //获取环境变量
        if (!nodeVarsStr) {
            nodeVarsStr = "[]";
        }
        var nodeVars = $.parseJSON(nodeVarsStr);
        for (var i = 0; i < nodeVars.length; i++) {
            if (nodeVars[i].KEY_ == 'stageName') {
                stageName = nodeVars[i].DEF_VAL_;
            }
        }
        if (stageName != 'sqrbz') {
            businessForm.setEnabled(false);
            $("#caseFileUpload").hide();
        }
    }

    function addCaseFile(applyId) {
        if (!applyId) {
            mini.alert("请先保存表单后再上传文件！");
            return;
        }
        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/environment/core/failureAnalysisCase/openUploadWindow.do?applyId=" + applyId,
            width: 750,
            height: 450,
            showModal: false,
            allowResize: true,
            onload: function () {
                var iframe = this.getIFrameEl();
                var projectParams = {};
                projectParams.applyId = applyId;
                var data = {projectParams: projectParams};  //传递上传参数
                iframe.contentWindow.SetData(data);
            },
            ondestroy: function (action) {
                fileListGrid.load();
            }
        });
    }

    function downloadTemp() {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/environment/core/failureAnalysisCase/caseTemplateDownload.do");
        $("body").append(form);
        form.submit();
        form.remove();
    }

    function operationRenderer(e) {
        var record = e.record;
        if (!record.id) {
            return "";
        }
        var cellHtml = returnPreviewSpan(record.fileName, record.id, record.applyId, coverContent);
        var downloadUrl = '/environment/core/failureAnalysisCase/fileDownload.do';
        if (record) {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + "下载" + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.applyId + '\',\'' + downloadUrl + '\')">' + "下载" + '</span>';
        } else {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span  title=' + "下载" + ' style="color: silver" >' + "下载" + '</span>';
        }
        if (record && (currentUserId == "1" || isSuozhang == "true" || record.CREATE_BY_ == currentUserId) && (action == "edit" || stageName == "sqrbz" || action == "spEdit")) {
            var deleteUrl = "/environment/core/failureAnalysisCase/deleteFiles.do";
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + "删除" + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.applyId + '\',\'' + deleteUrl + '\')">' + "删除" + '</span>';
        } else {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span  title=' + "删除" + ' style="color: silver" > ' + "删除" + ' </span>';
        }
        return cellHtml;
    }

    function returnPreviewSpan(fileName, fileId, formId, coverContent) {
        var s = '';
        if (fileName == "") {
            return s;
        }
        var fileType = getFileType(fileName);
        if (fileType == 'other') {
            s = '&nbsp;&nbsp;&nbsp;<span  title=' + "预览" + ' style="color: silver" >' + "预览" + '</span>';
        } else {
            var url = '/environment/core/failureAnalysisCase/preview.do?fileType=' + fileType;
            s = '<span  title=' + "预览" + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf6(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\',\'' + fileType + '\')">' + "预览" + '</span>';
        }
        return s;
    }

    function deleteFile(fileName, fileId, formId, urlValue) {
        mini.confirm("确定删除？", "提示",
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
                            fileListGrid.load();
                        }
                    });
                }
            }
        );
    }

    function startCheck() {
        var resultData = {"result": false, "message": ""};
        var caseName = mini.get('caseName').getText();
        if (!caseName) {
            resultData.message = "请填写“故障案例名称”！";
            return resultData;
        }
        //故障案例名称是否重复
        var url = jsUseCtxPath + '/environment/core/failureAnalysisCase/isExist.do?caseName=' + caseName+"&applyId="+applyId;
        var resultData = ajaxRequest(url, 'get', false);
        if (resultData && resultData.success) {
            mini.alert("名称为：“" + caseName + "”的故障案例已存在！");
            return;
        }
        resultData.result = true;
        return resultData;
    }

    function saveBusinessInProcess() {
        var resultData = startCheck();
        if (!resultData.result) {
            mini.alert(resultData.message);
            return;
        }
        var formData = getData();
        $.ajax({
            url: jsUseCtxPath + '/environment/core/failureAnalysisCase/saveBusiness.do',
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

</script>
</body>
</html>
