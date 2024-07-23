<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html >
<html>
<head>
    <title>主数据属性治理工作提报</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>

</head
<body>


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
            <p style="text-align: center;font-size: 20px;font-weight: bold;margin-top: 20px">主数据属性治理工作提报</p>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="text-align: center;width: 20%">年月：</td>
                    <td style="min-width:170px">
                        <input id="yearMonth" name="yearMonth" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%">提报人：</td>
                    <td style="min-width:170px">
                        <input id="creatorName" name="creatorName" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">提报人部门：</td>
                    <td style="min-width:170px">
                        <input id="departName" name="departName" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
<%--                    <td style="text-align: center;width: 20%">工作报告名称：<span--%>
<%--                            style="color:red">*</span></td>--%>
<%--                    <td style="min-width:170px">--%>
<%--                        <input id="workReportName" name="workReportName" class="mini-textbox" style="width:98%;"/>--%>
<%--                    </td>--%>
                </tr>

                <tr>
                    <td style="text-align: center;width: 14%;height:10px">主数据属性治理列表：</td>
                    <td colspan="3" height="60px">
                        <div class="mini-toolbar" id="zsjbbListToolBar" style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="addFile('${applyId}')">添加附件</a>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid"
                             allowResize="false"
                             idField="id"
                             url="${ctxPath}/productDataManagement/core/zsjbb/fileList.do?applyId=${applyId}"
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
    var status = "${status}";
    var applyId = "${applyId}";
    var currentDay = ${currentDay};
    var instId = "${instId}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;


    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }


    var stageName = "";
    $(function () {
        var url = jsUseCtxPath + "/productDataManagement/core/zsjbb/getJson.do";
        $.post(
            url,
            {id: applyId},
            function (json) {
                businessForm.setData(json);
            });

        if (action == 'detail') {
            businessForm.setEnabled(false);
            $("#detailToolBar").show();
            $("#zsjbbListToolBar").hide();

            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        } else if (action == 'task') {
            taskActionProcess();
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
        // if (currentDay > 26) {
        //     mini.alert("已过当月26号截止填报日期！");
        //     return;
        // }
        var resultData = startCheck();
        if (!resultData.result) {
            mini.alert(resultData.message);
            return;
        }
        window.parent.startProcess(e);
    }

    //下一步审批
    function projectApprove() {
        //编制阶段的下一步需要校验表单必填字段
        if (stageName == 'start') {

            var formValid = validSzjbb();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }

        //检查通过
        window.parent.approve();
    }


    function validSzjbb() {

        var demandGridData = fileListGrid.getData();
        if (demandGridData.length == 0) {
            mini.alert("请上传治理报表！");
            return;
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
        if (stageName != 'start') {
            businessForm.setEnabled(false);
            $("#zsjbbListToolBar").hide();
        }


    }


    function addFile(applyId) {

        if (!applyId) {
            mini.alert("请先点击‘保存草稿’进行表单的保存！");
            return;
        }


        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/productDataManagement/core/zsjbb/openUploadWindow.do?applyId=" + applyId,
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

    function operationRenderer(e) {
        var record = e.record;
        //预览、下载和删除 只有智控所或管理员有权限
        if (!record.id) {
            return "";
        }
        var cellHtml = returnPreviewSpan(record.fileName, record.id, record.applyId, coverContent);
        var downloadUrl = '/productDataManagement/core/zsjbb/fileDownload.do';
        //只有智控所可以下载
        if (record) {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + "下载" + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.applyId + '\',\'' + downloadUrl + '\')">' + "下载" + '</span>';
        } else {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span  title=' + "下载" + ' style="color: silver" >' + "下载" + '</span>';
        }

        if (record && (currentUserId == "1" || record.CREATE_BY_ == currentUserId) && (action == "edit" || stageName == "tibao")) {
            var deleteUrl = "/productDataManagement/core/zsjbb/deleteFiles.do";
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
            var url = '/productDataManagement/core/zsjbb/preview.do?fileType=' + fileType;
            s = '<span  title=' + "预览" + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf6(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\',\'' + fileType + '\')">' + "预览" + '</span>';
        }
        return s;
    }

    function addZsjbbRow() {
        var row = {}
        zsjbbGrid.addRow(row);
    }


    function removeZsjbbRow() {
        var selecteds = zsjbbGrid.getSelecteds();
        zsjbbGrid.removeRows(selecteds);
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
        var yearMonth = mini.get('yearMonth').getText();
        if (!yearMonth) {
            resultData.message = "请选择“年月份”！";
            return resultData;
        }
        var url = jsUseCtxPath + '/productDataManagement/core/zsjbb/isExistYdgz.do?id=' + applyId + '&yearMonth=' + yearMonth + '&creatorId=' + currentUserId;
        var resultData = ajaxRequest(url, 'get', false);
        if (resultData && resultData.success) {
            mini.alert("当前用户已存在“" + yearMonth + "”月度的流程，不允许重复创建，请在原流程上进行操作！");
            return;
        }
        resultData.result = true;
        return resultData;
    }

    // function saveBusinessInProcess() {
    //     var formData = getData();
    //     $.ajax({
    //         url: jsUseCtxPath + '/productDataManagement/core/zsjbb/saveBusiness.do',
    //         type: 'post',
    //         async: false,
    //         data: mini.encode(formData),
    //         contentType: 'application/json',
    //         success: function (data) {
    //             if (data) {
    //                 var message = "";
    //                 if (data.success) {
    //                     message = "数据保存成功";
    //                 } else {
    //                     message = "数据保存失败" + data.message;
    //                 }
    //                 mini.alert(message, "提示信息", function () {
    //                     window.location.reload();
    //                 });
    //             }
    //         }
    //     });
    // }


</script>
</body>
</html>
