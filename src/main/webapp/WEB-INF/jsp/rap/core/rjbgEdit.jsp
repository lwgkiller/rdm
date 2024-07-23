<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>新增</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <style>
        html, body {
            overflow: hidden;
            height: 100%;
            width: 100%;
            padding: 0;
            margin: 0;
        }

        .table-detail > tbody > tr > td {
            border: 1px solid #eee;
            background-color: #fff;
            white-space: normal;
            word-break: break-all;
            color: rgb(85, 85, 85);
            font-weight: normal;
            padding: 4px;
            height: 40px;
            min-height: 40px;
            box-sizing: border-box;
            font-size: 15px;
        }
    </style>
</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 80%;">
        <form id="formRjbg" method="post">
            <input id="rjbgId" name="rjbgId" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold;font-size: 20px !important;">
                    发动机软件开发申请
                </caption>
                <tr>
                    <td style="  width: 20%;text-align: center">流程编号(提交后自动生成)：</td>
                    <td style="width: 20%">
                        <input id="noticeNo" name="noticeNo" class="mini-textbox" readonly style="  width:98%;"/>
                    </td>
                </tr>
                <td style="width: 20%;text-align: center">申请人：</td>
                <td>
                    <input id="apply" name="applyId" textname="applyName"
                           property="editor" class="mini-user rxc" plugins="mini-user"
                           style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                </td>
                <td style="width: 20%;text-align: center">申请部门：</td>
                <td>
                    <input id="appDept" name="appDeptId" class="mini-dep rxc" plugins="mini-dep"
                           style="width:98%;height:34px"
                           allowinput="false" textname="appDeptName" single="true" initlogindep="false"/>
                </td>
                </tr>
                <tr>
                    <td style="width: 20%;text-align: center">发动机品牌：</td>
                    <td style="width: 20%">
                        <input id="fdjGrand" name="fdjGrand" class="mini-textbox" style="  width:98%;"/>
                    </td>
                    <td style="  width: 20%;text-align: center">发动机型号：</td>
                    <td style="width: 20%">
                        <input id="fdjModel" name="fdjModel" class="mini-textbox" style="  width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="  width: 20%;text-align: center">设计型号：</td>
                    <td style="width: 20%">
                        <input id="model" style="width:98%;" class="mini-buttonedit" showClose="true"
                               oncloseclick="onRelModelCloseClick()"
                               name="modelId" textname="modelName" allowInput="false"
                               onbuttonclick="selectModelClick()"/>
                    </td>
                    <td style="  width: 20%;text-align: center">电气工程师：</td>
                    <td style="width: 20%">
                        <input id="dqId" name="dqId" textname="dqName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="  width: 20%;text-align: center">动力工程师：</td>
                    <td style="width: 20%">
                        <input id="dlId" name="dlId" textname="dlName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                    <td style="  width: 20%;text-align: center">控制工程师：</td>
                    <td style="width: 20%">
                        <input id="kzId" name="kzId" textname="kzName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="width:15%;height: 400px;text-align: center">申请原因及目的：</td>
                    <td colspan="3">
                        <div style="margin-bottom: 2px">
                            <a id="addReason" class="mini-button" onclick="addReason()">添加原因</a>
                            <a id="removeReason" class="mini-button" onclick="removeReason()">删除原因</a>
                            <span style="color: red">注：添加前请先进行表单的保存、添加或删除后请点击保存以生效</span>
                        </div>
                        <div id="reasonListGrid" class="mini-datagrid" style="width: 100%; height: 85%"
                             allowResize="false" allowCellWrap="true"
                             idField="id" url="${ctxPath}/environment/core/Rjbg/queryReason.do?belongId=${rjbgId}"
                             autoload="true" allowCellEdit="true" allowCellSelect="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="checkcolumn" width="10"></div>
                                <div type="indexcolumn" align="center" headerAlign="center" width="20">序号</div>
                                <div field="reason" name="reason" width="60" headerAlign="center" align="center">申请原因
                                    <input property="editor" class="mini-textbox"/></div>
                                <div field="purpose" width="60" headerAlign="center" align="center">申请目的
                                    <input property="editor" class="mini-textbox"/></div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="width: 20%;height: 300px;text-align: center">发动机软件附件表：</td>
                    <td colspan="3">
                        <div style="margin-top: 2px">
                            <a style="margin-top: 2px" id="addSfFile" class="mini-button" onclick="fileuploadSoft()">添加附件</a>
                        </div>
                        <div id="sfFileListGrid" class="mini-datagrid" style="width: 100%; height: 80%"
                             allowResize="false"
                             idField="id" autoload="true"
                             url="${ctxPath}/environment/core/Rjbg/getRjbgFileList.do?fileType=soft&rjbgId=${rjbgId}"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="15">序号</div>
                                <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
                                <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                                <div field="action" width="100" headerAlign='center' align="center"
                                     renderer="operationRendererF">操作
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="width: 20%;height: 300px;text-align: center">软件验证方案附件表：</td>
                    <td colspan="3">
                        <div>
                            <a id="addSfcFile" class="mini-button" onclick="fileuploadSoftCheck()">添加附件</a>
                        </div>
                        <div id="sfcFileListGrid" class="mini-datagrid" style="width: 100%; height: 80%"
                             allowResize="false"
                             idField="id" autoload="true"
                             url="${ctxPath}/environment/core/Rjbg/getRjbgFileList.do?fileType=SoftCheck&rjbgId=${rjbgId}"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" headerAlign="center" align="center" width="20">序号</div>
                                <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
                                <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                                <div field="action" width="100" headerAlign='center' align="center"
                                     renderer="operationRendererF">操作
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<div id="selectModelWindow" title="选择设计型号" class="mini-window" style="width:900px;height:550px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="true">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span class="text" style="width:auto">设计型号: </span><input
            class="mini-textbox" style="width: 120px" id="designModel" name="designModel"/>
        <span class="text" style="width:auto">销售型号: </span><input
            class="mini-textbox" style="width: 120px" id="saleModel" name="saleModel"/>
        <span class="text" style="width:auto">产品所: </span><input
            class="mini-textbox" style="width: 120px" id="departName" name="departName"/>
        <span class="text" style="width:auto">产品主管: </span><input
            class="mini-textbox" style="width: 120px" id="productManagerName" name="productManagerName"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchModel()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="selectModelListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id" showColumnsMenu="false" onload="onGridLoad"
             allowAlternating="true" showPager="true" multiSelect="true"
             url="${ctxPath}/world/core/productSpectrum/dataListQuery.do?">
            <div property="columns">
                <div type="checkcolumn" width="20"></div>
                <div field="designModel" width="150" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    设计型号
                </div>
                <div field="departName" width="120" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    产品所
                </div>
                <div field="productManagerName" width="80" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    产品主管
                </div>
                <div field="saleModel" width="120" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    销售型号
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectModelOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectModelHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var status = "${status}";
    var action = "${action}";
    var jsUseCtxPath = "${ctxPath}";
    var rjbgId = "${rjbgId}";
    var formRjbg = new mini.Form("#formRjbg");
    var sfFileListGrid = mini.get("sfFileListGrid");
    var sfcFileListGrid = mini.get("sfcFileListGrid");
    var reasonListGrid = mini.get("reasonListGrid");
    var selectModelWindow = mini.get("selectModelWindow");
    var selectModelListGrid = mini.get("selectModelListGrid");
    var currentUserName = "${currentUserName}";
    var nodeVarsStr = '${nodeVars}';
    var currentUserId = "${currentUserId}";
    var currentTime = "${currentTime}";
    var deptName = "${deptName}";
    var currentUserMainDepId = "${currentUserMainDepId}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var selectRowIds = [];
    var selectRowNames = [];

    function operationRendererF(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnRjbgPreviewSpan(record.fileName, record.fileId, record.belongId, coverContent);
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadRjbgFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\')">下载</span>';
        //增加删除按钮
        if (record.CREATE_BY_ == currentUserId && action != 'detail') {
            var deleteUrl = "/environment/core/Rjbg/deleteRjbgFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteSmFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }


    var stageName = "";
    $(function () {
        if (rjbgId) {
            var url = jsUseCtxPath + "/environment/core/Rjbg/getRjbgDetail.do";
            $.post(
                url,
                {rjbgId: rjbgId},
                function (json) {
                    formRjbg.setData(json);
                });
        } else {
            mini.get("apply").setValue(currentUserId);
            mini.get("apply").setText(currentUserName);
            mini.get("appDept").setValue(currentUserMainDepId);
            mini.get("appDept").setText(deptName);
        }
        //变更入口
        if (action == 'task') {
            taskActionProcess();
        } else if (action == "detail") {
            formRjbg.setEnabled(false);
            reasonListGrid.setAllowCellEdit(false);
            mini.get("addReason").setEnabled(false);
            mini.get("removeReason").setEnabled(false);
            mini.get("addSfFile").setEnabled(false);
            mini.get("addSfcFile").setEnabled(false);
            $("#detailToolBar").show();
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        }else if (action == "edit") {
            mini.get("addSfFile").setEnabled(false);
            mini.get("addSfcFile").setEnabled(false);
        }
    });

    function getData() {
        var formData = _GetFormJsonMini("formRjbg");
        formData.reason = reasonListGrid.getChanges();
        return formData;
    }

    function saveRjbg(e) {
        // var formValid = validFirst();
        // if (!formValid.result) {
        //     mini.alert(formValid.message);
        //     return;
        // }
        window.parent.saveDraft(e);
    }

    function saveChange(rjbgId) {
        var formData = _GetFormJsonMini("formRjbg");
        $.ajax({
            url: jsUseCtxPath + '/environment/core/Rjbg/saveRjbg.do?rjbgId=' + rjbgId,
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message = "";
                    if (data.success) {
                        message = "数据变更成功";
                    } else {
                        message = "数据变更失败" + data.message;
                    }

                    mini.showMessageBox({
                        title: "提示信息",
                        iconCls: "mini-messagebox-info",
                        buttons: ["ok"],
                        message: message,
                        callback: function (action) {
                            if (action == "ok") {
                                CloseWindow("ok");
                            }
                        }
                    });
                }
            }
        });
    }


    function checkModel() {
        var fdjModel =mini.get("fdjModel").getValue();
        $.ajax({
            url: jsUseCtxPath + '/environment/core/Rjbg/checkModel.do?fdjModel='+fdjModel,
            type:'POST',
            async: false,
            contentType: 'application/json',
            success: function (data) {
                if(!data.success) {
                    mini.alert(data.message,"提示消息",function (action) {
                        if(action=='ok') {
                            CloseWindow();
                        }
                    });
                }
            }
        });
    }

    function startRjbgProcess(e) {
        var formValid = validFirst();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        var fdjModel =mini.get("fdjModel").getValue();
        $.ajax({
            url: jsUseCtxPath + '/environment/core/Rjbg/checkModel.do?fdjModel='+fdjModel,
            type:'POST',
            async: false,
            contentType: 'application/json',
            success: function (data) {
                if(data.success) {
                    window.parent.startProcess(e);
                }else {
                    mini.alert(data.message,"提示消息",function (action) {
                        if(action=='ok') {
                            window.parent.startProcess(e);
                        }
                    });
                }
            }
        });
    }

    function validFirst() {
        var apply = $.trim(mini.get("apply").getValue());
        if (!apply) {
            return {"result": false, "message": "请填写申请人"};
        }
        var appDept = $.trim(mini.get("appDept").getValue());
        if (!appDept) {
            return {"result": false, "message": "请填写申请部门"};
        }
        var fdjGrand = $.trim(mini.get("fdjGrand").getValue());
        if (!fdjGrand) {
            return {"result": false, "message": "请填写发动机品牌"};
        }
        var fdjModel = $.trim(mini.get("fdjModel").getValue());
        if (!fdjModel) {
            return {"result": false, "message": "请填写发动机型号"};
        }
        var model = $.trim(mini.get("model").getValue());
        if (!model) {
            return {"result": false, "message": "请选择设计型号"};
        }
        var dlId = $.trim(mini.get("dlId").getValue());
        if (!dlId) {
            return {"result": false, "message": "请选择动力工程师"};
        }
        var dqId = $.trim(mini.get("dqId").getValue());
        if (!dqId) {
            return {"result": false, "message": "请选择电气工程师"};
        }
        var kzId = $.trim(mini.get("kzId").getValue());
        if (!kzId) {
            return {"result": false, "message": "请选择控制工程师"};
        }
        var reason = reasonListGrid.getData();
        if (reason.length < 1) {
            return {"result": false, "message": "请添加申请原因及目的"};
        } else {
            for (var i = 0; i < reason.length; i++) {
                if (reason[i].reason == undefined || reason[i].reason == "") {
                    return {"result": false, "message": "请填写申请原因"};
                }
                if (reason[i].purpose == undefined || reason[i].purpose == "") {
                    return {"result": false, "message": "请填写申请目的"};
                }
            }
        }
        return {"result": true};
    }

    function validSecond() {
        var sfFileListGrid = $.trim(mini.get("sfFileListGrid").getData());
        if (!sfFileListGrid) {
            return {"result": false, "message": "请上传发动机软件"};
        }
        return {"result": true};
    }

    function validThird() {
        var sfcFileListGrid = $.trim(mini.get("sfcFileListGrid").getData());
        if (!sfcFileListGrid) {
            return {"result": false, "message": "请上传软件验证方案"};
        }
        return {"result": true};
    }

    function rjbgApprove() {
        //编制阶段的下一步需要校验表单必填字段
        if (stageName == 'first') {
            var formValid = validFirst();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if (stageName == 'second') {
            var formValid = validSecond();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if (stageName == 'third') {
            var formValid = validThird();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        //检查通过
        window.parent.approve();
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


    function fileuploadSoftCheck() {
        var rjbgId = mini.get("rjbgId").getValue();
        if (!rjbgId) {
            mini.alert('请先点击‘保存草稿’进行表单的保存！');
            return;
        }
        mini.open({
            title: "添加附件",
            url: jsUseCtxPath + "/environment/core/Rjbg/openUploadWindow.do?fileType=softCheck&rjbgId=" + rjbgId,
            width: 850,
            height: 550,
            showModal: true,
            allowResize: true,
            ondestroy: function () {
                if (sfcFileListGrid) {
                    sfcFileListGrid.load();
                }
            }
        });
    }


    function fileuploadSoft() {
        var rjbgId = mini.get("rjbgId").getValue();
        if (!rjbgId) {
            mini.alert('请先点击‘保存草稿’进行表单的保存！');
            return;
        }
        mini.open({
            title: "添加附件",
            url: jsUseCtxPath + "/environment/core/Rjbg/openUploadWindow.do?fileType=soft&rjbgId=" + rjbgId,
            width: 850,
            height: 550,
            showModal: true,
            allowResize: true,
            ondestroy: function () {
                if (sfFileListGrid) {
                    sfFileListGrid.load();
                }
            }
        });
    }

    function taskActionProcess() {
        //获取上一环节的结果和处理人
        bpmPreTaskTipInForm();

        //获取环境变量
        if (!nodeVarsStr) {
            nodeVarsStr = "[]";
        }
        formRjbg.setEnabled(false);
        reasonListGrid.setAllowCellEdit(false);
        mini.get("addSfcFile").setEnabled(false);
        mini.get("addSfFile").setEnabled(false);
        mini.get("addReason").setEnabled(false);
        mini.get("removeReason").setEnabled(false);
        var nodeVars = $.parseJSON(nodeVarsStr);
        for (var i = 0; i < nodeVars.length; i++) {
            if (nodeVars[i].KEY_ == 'stageName') {
                stageName = nodeVars[i].DEF_VAL_;
            }
        }
        if (stageName == 'first') {
            reasonListGrid.setAllowCellEdit(true);
            formRjbg.setEnabled(true);
            mini.get("addReason").setEnabled(true);
            mini.get("removeReason").setEnabled(true);
        }

        if (stageName == 'second') {
            mini.get("addSfFile").setEnabled(true);
        }
        if (stageName == 'third') {
            mini.get("addSfcFile").setEnabled(true);
        }
    }


    function returnRjbgPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            var url = '/environment/core/Rjbg/rjbgPdfPreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/environment/core/Rjbg/rjbgOfficePreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/environment/core/Rjbg/rjbgImagePreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        return s;
    }

    function downLoadRjbgFile(fileName, fileId, formId) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + '/environment/core/Rjbg/rjbgPdfPreview.do?action=download');
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


    function deleteSmFile(fileName, fileId, formId, urlValue) {
        mini.confirm("确定删除？", "确定？",
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
                            if (sfFileListGrid) {
                                sfFileListGrid.load();
                            }
                            if (sfcFileListGrid) {
                                sfcFileListGrid.load();
                            }
                        }
                    });
                }
            }
        );
    }

    function addReason() {
        var formId = mini.get("rjbgId").getValue();
        if (!formId) {
            mini.alert("请先点击‘保存草稿’进行表单创建!");
            return;
        } else {
            var row = {};
            reasonListGrid.addRow(row);
        }
    }

    function removeReason() {
        var selecteds = reasonListGrid.getSelecteds();
        if (selecteds.length <= 0) {
            mini.alert("请选择一条记录");
            return;
        }
        var deleteArr = [];
        for (var i = 0; i < selecteds.length; i++) {
            var row = selecteds[i];
            deleteArr.push(row);
        }
        reasonListGrid.removeRows(deleteArr);
    }


    function selectModelClick() {
        var modelId = mini.get("model").getValue();
        var modelName = mini.get("model").getText();
        if(modelId){
            selectRowIds = modelId.split(',');
            selectRowNames = modelName.split(',');
        }
        selectModelWindow.show();
        searchModel();
    }

    function searchModel() {
        var queryParam = [];
        //其他筛选条件
        var designModel = $.trim(mini.get("designModel").getValue());
        if (designModel) {
            queryParam.push({name: "designModel", value: designModel});
        }
        var departName = $.trim(mini.get("departName").getValue());
        if (departName) {
            queryParam.push({name: "departName", value: departName});
        }
        var productManagerName = $.trim(mini.get("productManagerName").getValue());
        if (productManagerName) {
            queryParam.push({name: "productManagerName", value: productManagerName});
        }
        var saleModel = $.trim(mini.get("saleModel").getValue());
        if (saleModel) {
            queryParam.push({name: "saleModel", value: saleModel});
        }
        var data = {};
        data.filter = mini.encode(queryParam);
        //查询
        selectModelListGrid.load(data);
    }

    function selectModelOK() {
        if (selectRowIds) {
            mini.get("model").setValue(selectRowIds.toString());
            mini.get("model").setText(selectRowNames.toString());
            selectModelHide();
        }
    }

    function selectModelHide() {
        selectModelWindow.hide();
        mini.get("saleModel").setValue('');
        mini.get("productManagerName").setValue('');
        mini.get("departName").setValue('');
        mini.get("designModel").setValue('');
        selectRowIds.splice(0, selectRowIds.length);
        selectRowNames.splice(0, selectRowNames.length);
        selectModelListGrid.clearSelect();
    }

    function onRelModelCloseClick() {
        mini.get("model").setValue('');
        mini.get("model").setText('');
        selectRowIds.splice(0, selectRowIds.length);
        selectRowNames.splice(0, selectRowNames.length);
        selectModelListGrid.clearSelect();
    }

    function onGridLoad(e) {
        if (selectRowIds.length>0) {
            for (var i = 0; i < selectModelListGrid.data.length; i++) {
                if (selectRowIds.indexOf(selectModelListGrid.data[i].id) != -1) {
                    selectModelListGrid.setSelected(selectModelListGrid.getRow(i));
                }
            }
        }
    }


    selectModelListGrid.on("select", function (e) {
        var rec = e.record;
        var designModel = rec.designModel;
        var id = rec.id;
        if (selectRowIds.indexOf(id) == -1) {
            selectRowIds.push(id);
            selectRowNames.push(designModel);
        }
    });

    selectModelListGrid.on("deselect", function (e) {
        var rec = e.record;
        var designModel = rec.designModel;
        var id = rec.id;
        delItem(designModel, selectRowNames);
        delItem(id, selectRowIds);
    });

    function delItem(item, list) {
        // 表示先获取这个元素的下标，然后从这个下标开始计算，删除长度为1的元素
        list.splice(list.indexOf(item), 1)
    }
</script>
</body>
</html>