<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html >
<html>
<head>
    <title>外发翻译申请表单</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>

    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <link href="${ctxPath}/styles/css/multiupload.css" rel="stylesheet" type="text/css"/>

</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="saveInfo" class="mini-button" style="display: none" onclick="saveInDetail()">保存</a>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow1" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>

<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;height:98%;padding: 0">
        <form id="currentForm" method="post" style="height: 95%;width: 98%">
            <input class="mini-hidden" id="id" name="id"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="baseInfoId" name="baseInfoId" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="text-align: center;width: 20%">翻译申请单号：</td>
                    <td style="min-width:170px">
                        <input id="transApplyId" name="transApplyId" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">手册类型:
                    <td style="min-width:170px">
                        <input id="manualType" name="manualType" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>


                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">外发申请单号：</td>
                    <td style="min-width:170px">
                        <input id="applyNumber" name="applyNumber" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%">申请人：</td>
                    <td style="min-width:170px">
                        <input id="CREATE_BY_" name="CREATE_BY_" class="mini-textbox" style="display: none"/>
                        <input id="creatorName" name="creatorName" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">销售型号：</td>
                    <td style="min-width:170px">
                        <input id="saleType" name="saleType" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%">设计型号：</td>
                    <td style="min-width:170px">
                        <input id="designType" name="designType" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>

                <tr>
                    </td>
                    <td style="text-align: center;width: 20%">物料编码：</td>
                    <td style="min-width:170px">
                        <input id="materialCode" name="materialCode" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%">手册编码：</td>
                    <td style="min-width:170px">
                        <input id="manualCode" name="manualCode" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>

                </tr>

                <tr>
                    <td style="text-align: center;width: 20%">源手册语言：</td>
                    <td style="min-width:170px">
                        <input id="sourceManualLan" name="sourceManualLan" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%">翻译语言：</td>
                    <td style="min-width:170px">
                        <input id="targetManualLan" name="targetManualLan" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>

                <tr>
                    <td style="text-align: center;width: 20%">总字符数：</td>
                    <td style="min-width:170px">
                        <input id="totalCodeNum" name="totalCodeNum" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%">自翻译率：</td>
                    <td style="min-width:170px">
                        <input id="selfTranslatePercent" name="selfTranslatePercent" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>

                <tr>
                    <td style="text-align: center;width: 20%">外发翻译率：</td>
                    <td style="min-width:170px">
                        <input id="outTranslatePercent" name="outTranslatePercent" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    <td style="text-align: center;width: 20%">是否CE版手册：</td>
                    <td style="min-width:170px">
                        <input id="mannulCE" name="mannulCE" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>

                </tr>

                <tr>
                    <td style="text-align: center;width: 20%">备注：</td>
                    <td style="min-width:170px" colspan="3">
                        <input id="exTransDesc" name="exTransDesc" class="mini-textbox" style="width:98%;"/>
                    </td>


                </tr>
            </table>

            <p style="font-size: 16px;font-weight: bold;margin-top: 20px">外发及归档文件清单</p>
            <div class="mini-toolbar" style="margin-bottom: 5px" id="demandListToolBar">
                <a class="mini-button" id="selfUploadFile" name="selfUploadFile" plain="true" onclick="addOutFile">外发文件上传</a>
                <a class="mini-button btn-red" id="delDemand" name="delDemand" plain="true" onclick="delDataRow">移除</a>
                <a class="mini-button" id="uploadReturnFile" name="uploadReturnFile" plain="true"
                   onclick="uploadReturnFile">回传文件上传</a>
            </div>

            <div id="demandGrid" class="mini-datagrid" allowResize="false" style="height:340px"
                 autoload="true"
                 idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                 multiSelect="false" showColumnsMenu="false" allowAlternating="true" showPager="false"
                 url="${ctxPath}/serviceEngineering/core/externalTranslation/demandList.do?applyId=${applyId}"
                 oncellbeginedit="OnCellBeginEdit"
            >
                <div property="columns">
                    <div type="checkcolumn" width="50"></div>
                    <div type="indexcolumn" headerAlign="center" width="50">序号</div>
                    <div name="action" cellCls="actionIcons" width="75" headerAlign="center" align="center"
                         renderer="onActionRenderer" cellStyle="padding:0;">操作
                    </div>
                    <div field="outFileName" width="120" headerAlign="center" align="center"
                         allowSort="true">外发文件名称<span style="color: #ff0000">*</span>
                        <input property="editor" class="mini-textbox"/>
                    </div>
                    <div field="outFileType" width="90" headerAlign="center" align="center"
                         allowSort="true">文件类型<span style="color: #ff0000">*</span>
                        <input property="editor" class="mini-textbox">
                    </div>
                    <div field="outFileTotal" width="90" headerAlign="center" align="center"
                         allowSort="true">文件数量<span style="color: #ff0000">*</span>
                        <input property="editor" class="mini-textbox">

                    </div>
                    <div field="outFileNum" width="120" headerAlign="center" align="center"
                         allowSort="true">外发字符数
                        <input property="editor" class="mini-textbox">
                    </div>
                    <div field="outRate" width="50" headerAlign="center" align="center"
                         allowSort="true">重复率
                        <input property="editor" class="mini-textbox">

                    </div>

                    <div field="outDesc" width="250" headerAlign="center" align="center"
                         allowSort="true">备注说明
                        <input property="editor" class="mini-textbox">
                    </div>

                    <div name="op" cellCls="actionIcons" width="110" headerAlign="center" align="center"
                         renderer="fileInfoRenderer" cellStyle="padding:0;">外发文件操作
                    </div>
                    <div field="returnFileName" name="returnFileName" width="120" headerAlign="center" align="center"
                         allowSort="true">回传文件名称
                        <input property="editor" class="mini-textbox"/>
                    </div>
                    <div field="returnFileTime" name="returnFileTime" width="120" headerAlign="center" align="center"
                         allowSort="true">回传文件上传时间
                        <input property="editor" class="mini-textbox"/>
                    </div>
                    <div id="reop" name="reop" cellCls="actionIcons" width="110" headerAlign="center" align="center"
                         renderer="returnFileInfoRenderer" cellStyle="padding:0;">回传文件操作
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var nodeVarsStr = '${nodeVars}';
    var jsUseCtxPath = "${ctxPath}";
    var currentForm = new mini.Form("#currentForm");
    var demandGrid = mini.get("demandGrid");

    var action = "${action}";
    var status = "${status}";
    var applyId = "${applyId}";
    var instId = "${instId}";
    var stageName = "";
    var baseInfoId = "${baseInfoId}"
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";

    function onActionRenderer(e) {
        var record = e.record;
    }

    function fileInfoRenderer(e) {
        var record = e.record;
        //预览、下载和删除
        var cellHtml = returnPreviewSpan(record.outFileName, record.outFileId, record.applyId, coverContent);
        var downloadUrl = '/serviceEngineering/core/externalTranslation/fileDownload.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.outFileName + '\',\'' + record.outFileId + '\',\'' + record.applyId + '\',\'' + downloadUrl + '\')">下载</span>';
        return cellHtml;
    }

    function returnFileInfoRenderer(e) {
        var record = e.record;
        //预览、下载和删除
        if (!record.returnFileId) {
            return "";
        }
        var cellHtml = returnPreviewSpan(record.returnFileName, record.returnFileid, record.applyId, coverContent);
        var downloadUrl = '/serviceEngineering/core/externalTranslation/fileDownload.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.returnFileName + '\',\'' + record.returnFileId + '\',\'' + record.applyId + '\',\'' + downloadUrl + '\')">下载</span>';
        if (record && action == "task") {
            var deleteUrl = "/serviceEngineering/core/externalTranslation/deleteFiles.do";
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.returnFileName + '\',\'' + record.returnFileId + '\',\'' + record.applyId + '\',\'' + deleteUrl + '\')">删除</span>';
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
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else {
            var url = '/serviceEngineering/core/externalTranslation/preview.do?fileType=' + fileType;
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf6(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\',\'' + fileType + '\')">预览</span>';
        }
        return s;
    }


    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }

    // 新增外发文件

    function addOutFile() {
        if (!applyId) {
            mini.alert("请先保存草稿！");
            return;
        }

        mini.open({
            title: "附件上传",
            url: jsUseCtxPath + "/serviceEngineering/core/externalTranslation/openUploadWindow.do?applyId=" + applyId,
            width: 800,
            height: 350,
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
                demandGrid.load();
            }
        });
    }
    function buchongInfo() {
        if (!applyId) {
            mini.alert("请先保存草稿！");
            return;
        }

        mini.open({
            title: "补充信息上传",
            url: jsUseCtxPath + "/serviceEngineering/core/externalTranslation/buchongUploadWindow.do?applyId=" + applyId,
            width: 800,
            height: 350,
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
                demandGrid.load();
            }
        });
    }
    function uploadReturnFile(e) {
        var selecteds = demandGrid.getSelecteds();
        if (selecteds.length > 1 || selecteds.length == 0) {
            mini.alert("请选择一条数据");
            return;
        }
        if (selecteds[0].returnFileId && selecteds[0].returnFileId != "") {
            mini.alert("回传文件已存在，请删除后上传");
            return;
        }
        mini.open({
            title: "附件上传",
            url: jsUseCtxPath + "/serviceEngineering/core/externalTranslation/openUploadWindow.do?applyId=" + applyId + "&detailId=" + selecteds[0].id + "&fType=" + "ret",
            width: 650,
            height: 350,
            showModal: false,
            allowResize: true,
            onload: function () {
                var iframe = this.getIFrameEl();
                var projectParams = {};
                var data = {projectParams: projectParams};  //传递上传参数
                iframe.contentWindow.SetData(data);
            },
            ondestroy: function (action) {
                demandGrid.load();
            }
        });
    }

    // 流程相关

    //保存草稿
    function saveDraft(e) {
        window.parent.saveDraft(e);
    }

    //发起流程
    function startProcess(e) {
        // 发起流程的时候必须填写外发文件

        var len = demandGrid.getData().length;
        if (len == 0) {
            mini.alert("请上传外发文件");
            return;
        }

        window.parent.startProcess(e);
    }

    //下一步审批
    function projectApprove() {
        //回传阶段下一步需要校验回传文件必须不为空
        if (stageName == 'start') {
            var len = demandGrid.getData().length;
            if (len == 0) {
                mini.alert("请上传外发文件");
                return;
            }
        }
        if (stageName == 'ret') {
            var demandGridData = demandGrid.getData();
            for (i = 0; i < demandGridData.length; i++) {
                if (demandGridData[i].returnFileId == undefined || demandGridData[i].returnFileId == "") {
                    mini.alert("请上传回传文件");
                    return;
                }
            }
        }


        //检查通过
        window.parent.approve();
    }

    function getData() {
        var formData = _GetFormJsonMini("currentForm");
        if (formData.SUB_fileListGrid) {
            delete formData.SUB_fileListGrid;
        }
        if (formData.SUB_demandGrid) {
            delete formData.SUB_demandGrid;
        }
        if (demandGrid.getChanges().length > 0) {
            formData.changeDemandGrid = demandGrid.getChanges();
        }


        return formData;
    }

    function delDataRow(e) {
        var selecteds = demandGrid.getSelecteds();
        demandGrid.removeRows(selecteds);
        saveDraft(e);
    }

    //删除文档
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
                            if (demandGrid) {
                                demandGrid.load();
                            }
                        }
                    });
                }
            }
        );
    }


    function taskActionProcess() {
        debugger;
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
        //编制阶段
        if (stageName == 'start') {
            mini.get("uploadReturnFile").hide();
            demandGrid.hideColumn("returnFileName");
            demandGrid.hideColumn("returnFileTime");
            demandGrid.hideColumn("reop");
            demandGrid.hideColumn("action");

        } else if (stageName == 'ret') {// 回传阶段
            mini.get("selfUploadFile").hide();
            mini.get("delDemand").hide();
            currentForm.setEnabled(false);

        } else {
            currentForm.setEnabled(false);
            mini.get("demandListToolBar").hide();
            demandGrid.hideColumn("returnFileName");
            demandGrid.hideColumn("returnFileTime");
            demandGrid.hideColumn("reop");

        }

    }


    // sript 流程相关
    $(function () {
        var url = jsUseCtxPath + "/serviceEngineering/core/externalTranslation/getJson.do?baseInfoId=" + baseInfoId;
        $.post(url,
            {id: applyId},
            function (json) {
                currentForm.setData(json);
            });
        demandGrid.setAllowCellEdit(false);
        if (action == 'detail') {
            currentForm.setEnabled(false);
            demandGrid.setAllowCellEdit(true);
            mini.get("demandListToolBar").hide();
            $("#detailToolBar").show();
            if (status != 'DRAFTED') {
                $("#processInfo").show();

            }
        } else if (action == 'task') {
            taskActionProcess();
        } else if (action == 'edit') {
            mini.get("uploadReturnFile").hide();
            demandGrid.hideColumn("returnFileName");
            demandGrid.hideColumn("returnFileTime");
            demandGrid.hideColumn("reop");
            demandGrid.hideColumn("action");
        }
    });

    function processInfo() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
        _OpenWindow({
            url: url,
            max: true,
            title: "流程图实例",
            width: 800,
            height: 600
        });
    }

    function OnCellBeginEdit(e) {
        if (action != 'task' && (e.field == "outFileNum" || e.field == "outRate")) {
            e.cancel = false;
        }
        else {
            e.cancel = true;
        }

    }


</script>
</body>
</html>
