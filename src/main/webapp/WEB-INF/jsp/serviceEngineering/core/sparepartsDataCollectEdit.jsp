<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>零部件技术资料收集</title>
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
    <div class="form-container" style="margin: 0 auto; width: 80%;">
        <form id="formBusiness" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="applyUserId" name="applyUserId" class="mini-hidden"/>
            <input id="applyDepId" name="applyDepId" class="mini-hidden"/>
            <input id="businessStatus" name="businessStatus" class="mini-hidden"/>
            <input id="INST_ID_" name="INST_ID_" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold">
                    零部件技术资料收集
                </caption>
                <tr>
                    <td style="text-align: center;width: 15%">单据编号(自动生成)：</td>
                    <td style="min-width:170px">
                        <input id="busunessNo" name="busunessNo" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">收集品类：</td>
                    <td style="min-width:170px">
                        <input id="collectType" name="collectType" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=professionalCategory"
                               valueField="key" textField="value" multiSelect="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">销售型号：</td>
                    <td style="min-width:170px">
                        <input id="salesModel" name="salesModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">设计型号：</td>
                    <td style="min-width:170px">
                        <input id="designModel" name="designModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">父项物料编码：</td>
                    <td style="min-width:170px">
                        <input id="materialCodeP" name="materialCodeP" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">父项物料描述：</td>
                    <td style="min-width:170px">
                        <input id="materialDescriptionP" name="materialDescriptionP" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">物料编码：</td>
                    <td style="min-width:170px">
                        <input id="materialCode" name="materialCode" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">物料描述：</td>
                    <td style="min-width:170px">
                        <input id="materialDescription" name="materialDescription" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">申请时间(自动生成)：</td>
                    <td style="min-width:170px">
                        <input id="applyTime" name="applyTime" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                               valueType="string" showTime="false" showOkButton="true" showClearButton="false" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%">需求时间：</td>
                    <td style="min-width:170px">
                        <input id="publishTime" name="publishTime" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                               valueType="string" showTime="false" showOkButton="false" showClearButton="false" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">业务处理人员：</td>
                    <td style="min-width:170px">
                        <input id="collectorId" name="collectorId" textname="collector"
                               class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
                               label="业务处理人员"
                               length="50"
                               mainfield="no" single="true"/>
                    </td>
                    <td style="text-align: center;width: 15%">资料审核人员：</td>
                    <td style="min-width:170px">
                        <input id="collectorId2" name="collectorId2" textname="collector2"
                               class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
                               label="资料审核人员"
                               length="50"
                               mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">申请人：</td>
                    <td style="min-width:170px">
                        <input id="applyUser" name="applyUser" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">申请部门：</td>
                    <td style="min-width:170px">
                        <input id="applyDep" name="applyDep" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">备注：</td>
                    <td colspan="3">
						<textarea id="remark" name="remark" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:98%;height:150px;line-height:25px;" label="" datatype="varchar" length="640"
                                  vtype="length:640" minlen="0" allowinput="true" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <%-------------------------------------------------------------%>
                <tr>
                    <td style="text-align: center;height: 250px">资料收集上传：</td>
                    <td colspan="3">
                        <div style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="addBusinessFile()">添加文件</a>
                            <span style="color: red">注：添加文件前，请先进行草稿的保存</span>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
                             idField="id" autoload="true"
                             url="${ctxPath}/serviceEngineering/core/sparepartsDataCollect/getFileList.do?businessId=${businessId}"
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

<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var status = "${status}";
    var jsUseCtxPath = "${ctxPath}";
    var fileListGrid = mini.get("fileListGrid");
    var formBusiness = new mini.Form("#formBusiness");
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
        var url = jsUseCtxPath + "/serviceEngineering/core/sparepartsDataCollect/getDetail.do";
        $.post(
            url,
            {businessId: businessId},
            function (json) {
                formBusiness.setData(json);
                if (action == 'detail') {
                    formBusiness.setEnabled(false);
                    mini.get("addFile").setEnabled(false);
                    $("#detailToolBar").show();
                    //非草稿放开流程信息查看按钮
                    if (status != 'DRAFTED') {
                        $("#processInfo").show();
                    }
                } else if (action == 'edit') {

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
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/sparepartsDataCollect/saveBusiness.do',
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
    function businessApprove() {
        var formValid = validBusiness();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.approve();
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
            url: jsUseCtxPath + "/serviceEngineering/core/sparepartsDataCollect/fileUploadWindow.do?" +
            "businessId=" + businessId,
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
    //..流程信息浏览
    function processInfo() {
        var instId = $("#INST_ID_").val();
        var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
        _OpenWindow({
            url: url,
            max: true,
            title: decorationManualCollectEdit_name5,
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
            if (nodeVars[i].KEY_ == 'codeName') {
                codeName = nodeVars[i].DEF_VAL_;
            }
        }
        if (codeName == 'A') {//如果是编辑节点

        } else {//如果不是编辑节点，首先吧表单和个文件的上传权限都封死
            formBusiness.setEnabled(false);
            mini.get("addFile").setEnabled(false);
            if (codeName == 'B') {//如果是资料上传节点，放开上传权限
                mini.get("addFile").setEnabled(true);
            }
        }
    }
    //..检验表单是否必填
    function validBusiness() {
        var collectType = $.trim(mini.get("collectType").getValue());
        if (!collectType) {
            return {"result": false, "message": "收集类型不能为空！"};
        }
        var salesModel = $.trim(mini.get("salesModel").getValue());
        if (!salesModel) {
            return {"result": false, "message": "销售型号不能为空！"};
        }
        var designModel = $.trim(mini.get("designModel").getValue());
        if (!designModel) {
            return {"result": false, "message": "设计型号不能为空！"};
        }
        var materialCode = $.trim(mini.get("materialCode").getValue());
        if (!materialCode) {
            return {"result": false, "message": "物料号不能为空！"};
        }
        var materialDescription = $.trim(mini.get("materialDescription").getValue());
        if (!materialDescription) {
            return {"result": false, "message": "物料描述不能为空！"};
        }
        var materialCodeP = $.trim(mini.get("materialCodeP").getValue());
        if (!materialCodeP) {
            return {"result": false, "message": "父项物料号不能为空！"};
        }
        var materialDescriptionP = $.trim(mini.get("materialDescriptionP").getValue());
        if (!materialDescriptionP) {
            return {"result": false, "message": "父项物料描述不能为空！"};
        }
        var publishTime = $.trim(mini.get("publishTime").getValue());
        if (!publishTime) {
            return {"result": false, "message": "需求时间不能为空！"};
        }
        var collectorId = $.trim(mini.get("collectorId").getValue());
        if (!collectorId) {
            return {"result": false, "message": "业务处理人员不能为空！"};
        }
        var collectorId2 = $.trim(mini.get("collectorId2").getValue());
        if (!collectorId2) {
            return {"result": false, "message": "资料审核人员不能为空！"};
        }
        if (codeName == 'B' && fileListGrid.totalCount == 0) {
            return {"result": false, "message": "请上传资料!"};
        }
        return {"result": true};
    }
    //..文件列表操作渲染
    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnPreviewSpan(record.fileName, record.id, record.mainId, coverContent);
        //和pdf预览用的同一个url
        var downLoadUrl = '/serviceEngineering/core/sparepartsDataCollect/PdfPreviewAndAllDownload.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainId + '\',\'' + downLoadUrl + '\')">下载</span>';
        //增加删除按钮
        if (action == 'edit' || (action == 'task' && codeName == 'B')) {
            var deleteUrl = "/serviceEngineering/core/sparepartsDataCollect/delFile.do"
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
            var url = '/serviceEngineering/core/sparepartsDataCollect/PdfPreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/serviceEngineering/core/sparepartsDataCollect/OfficePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/serviceEngineering/core/sparepartsDataCollect/ImagePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        return s;
    }
</script>
</body>
</html>
