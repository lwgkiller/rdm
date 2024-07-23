<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>操保手册发运/需求申请</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()"><spring:message code="page.maintenanceManualFeedbackEdit.name" /></a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()"><spring:message code="page.maintenanceManualFeedbackEdit.name1" /></a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 80%;">
        <form id="formBusiness" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="applyUserId" name="applyUserId" class="mini-hidden"/>
            <input id="applyDepId" name="applyDepId" class="mini-hidden"/>
            <input id="businessStatus" name="businessStatus" class="mini-hidden"/>
            <input id="BconfirmingId" name="BconfirmingId" class="mini-hidden"/>
            <input id="Bconfirming" name="Bconfirming" class="mini-hidden"/>
            <input id="INST_ID_" name="INST_ID_" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold">
                    <spring:message code="page.maintenanceManualFeedbackEdit.name2" />
                </caption>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualFeedbackEdit.name3" />：</td>
                    <td style="min-width:170px">
                        <input id="busunessNo" name="busunessNo" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualFeedbackEdit.name4" />：</td>
                    <td style="min-width:170px">
                        <input id="salesModel" name="salesModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualFeedbackEdit.name5" />：</td>
                    <td style="min-width:170px">
                        <input id="designModel" name="designModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualFeedbackEdit.name6" />：</td>
                    <td style="min-width:170px">
                        <input id="materialCode" name="materialCode" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualFeedbackEdit.name7" />：</td>
                    <td style="min-width:170px">
                        <input id="salesArea" name="salesArea" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualFeedbackEdit.name8" />：</td>
                    <td style="min-width:170px">
                        <input id="manualLanguage" name="manualLanguage" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringLanguage"
                               valueField="key" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualFeedbackEdit.name9" />：</td>
                    <td style="min-width:170px">
                        <input id="isCE" name="isCE" class="mini-combobox" style="width:98%;" enabled="true"
                               textField="value" valueField="key" emptyText="<spring:message code="page.maintenanceManualFeedbackEdit.name17" />..."
                               allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.maintenanceManualFeedbackEdit.name17" />..."
                               data="[{'key' : '是','value' : '是'},
							   {'key' : '否','value' : '否'}]"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualFeedbackEdit.name10" />：</td>
                    <td style="min-width:170px">
                        <input id="manualCode" name="manualCode" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.maintenanceManualFeedbackEdit.name11" />：</td>
                    <td colspan="3">
						<textarea id="abnormalDescription" name="abnormalDescription" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:98%;height:150px;line-height:25px;" label="" datatype="varchar" length="1000"
                                  vtype="length:1000" minlen="0" allowinput="true" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.maintenanceManualFeedbackEdit.name12" />：</td>
                    <td style="min-width:170px">
                        <input id="applyTime" name="applyTime" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                               valueType="string" showTime="false" showOkButton="true" showClearButton="false" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.maintenanceManualFeedbackEdit.name13" />：</td>
                    <td style="min-width:170px">
                        <input id="publishTime" name="publishTime" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                               valueType="string" showTime="false" showOkButton="true" showClearButton="false" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualFeedbackEdit.name14" />：</td>
                    <td style="min-width:170px">
                        <input id="applyUser" name="applyUser" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualFeedbackEdit.name15" />：</td>
                    <td style="min-width:170px">
                        <input id="applyDep" name="applyDep" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualFeedbackEdit.name16" />：</td>
                    <td style="min-width:170px">
                        <input id="isHasProblem" name="isHasProblem" class="mini-combobox" style="width:98%;" enabled="false"
                               textField="value" valueField="key" emptyText="<spring:message code="page.maintenanceManualFeedbackEdit.name17" />..."
                               allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.maintenanceManualFeedbackEdit.name17" />..."
                               data="[{'key' : '是','value' : '是'},
							   {'key' : '否','value' : '否'}]"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualFeedbackEdit.name18" />：</td>
                    <td style="min-width:170px">
                        <input id="productSupervisorId" name="productSupervisorId" textname="productSupervisor"
                               class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
                               label="<spring:message code="page.maintenanceManualFeedbackEdit.name18" />" length="50" mainfield="no" single="true" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.maintenanceManualFeedbackEdit.name19" />：</td>
                    <td colspan="3">
						<textarea id="problemDescription" name="problemDescription" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:98%;height:150px;line-height:25px;" label="" datatype="varchar" length="1000" enabled="false"
                                  vtype="length:1000" minlen="0" allowinput="true" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;height: 300px"><spring:message code="page.maintenanceManualFeedbackEdit.name20" />：</td>
                    <td colspan="3">
                        <div style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="addBusinessFile()"><spring:message code="page.maintenanceManualFeedbackEdit.name27" /></a>
                            <span style="color: red"><spring:message code="page.maintenanceManualFeedbackEdit.name21" /></span>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
                             idField="id"
                             url="${ctxPath}/serviceEngineering/core/maintenanceManualFeedback/getFileList.do?businessId=${businessId}"
                             autoload="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20"><spring:message code="page.maintenanceManualFeedbackEdit.name22" /></div>
                                <div field="fileName" align="center" headerAlign="center" width="150"><spring:message code="page.maintenanceManualFeedbackEdit.name23" /></div>
                                <div field="fileSize" align="center" headerAlign="center" width="60"><spring:message code="page.maintenanceManualFeedbackEdit.name24" /></div>
                                <div field="fileDesc" align="center" headerAlign="center" width="100"><spring:message code="page.maintenanceManualFeedbackEdit.name25" /></div>
                                <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer"><spring:message code="page.maintenanceManualFeedbackEdit.name26" /></div>
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
    var isBianzhi = "";
    var isQueren = "";
    var isQueren2 = "";
    var isCPZGQueren = "";
    var isMCT = "";
    $(function () {
        var url = jsUseCtxPath + "/serviceEngineering/core/maintenanceManualFeedback/getDetail.do";
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
        mini.get("applyUserId").setValue(currentUserId);
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
        var formValid = validBusiness();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        var formData = _GetFormJsonMini("formBusiness");
        if (formData.SUB_fileListGrid) {
            delete formData.SUB_fileListGrid;
        }
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/maintenanceManualFeedback/saveBusiness.do',
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message = "";
                    if (data.success) {
                        message = maintenanceManualFeedbackEdit_name;
                    } else {
                        message = maintenanceManualFeedbackEdit_name1 + data.message;
                    }

                    mini.alert(message, maintenanceManualFeedbackEdit_name2, function () {
                        window.location.reload();
                    });
                }
            }
        });
    }

    //..流程中的审批或者下一步
    function businessApprove() {
        //编制阶段的下一步需要校验表单必填字段
        if (isBianzhi == 'yes') {
            var formValid = validBusiness();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        } else if (isQueren == 'yes' || isCPZGQueren == "yes") {
            var isHasProblem = $.trim(mini.get("isHasProblem").getValue())
            if (!isHasProblem || isHasProblem == '') {
                mini.alert(maintenanceManualFeedbackEdit_name3);
                return;
            }
            if (isQueren == 'yes') {
                var productSupervisorId = mini.get("productSupervisorId").getValue();
                if (isHasProblem == "是" && (!productSupervisorId || productSupervisorId == '')) {
                    mini.alert(maintenanceManualFeedbackEdit_name4);
                    return;
                }
            }
        }
        //检查通过
        window.parent.approve();
    }

    //..添加文件
    function addBusinessFile() {
        var businessId = mini.get("id").getValue();
        if (!businessId) {
            mini.alert(maintenanceManualFeedbackEdit_name5);
            return;
        }
        mini.open({
            title: maintenanceManualFeedbackEdit_name6,
            url: jsUseCtxPath + "/serviceEngineering/core/maintenanceManualFeedback/fileUploadWindow.do?businessId=" + businessId,
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
            title: maintenanceManualFeedbackEdit_name7,
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
            if (nodeVars[i].KEY_ == 'isBianzhi') {
                isBianzhi = nodeVars[i].DEF_VAL_;
            } else if (nodeVars[i].KEY_ == 'isQueren') {
                isQueren = nodeVars[i].DEF_VAL_;
            } else if (nodeVars[i].KEY_ == 'isCPZGQueren') {
                isCPZGQueren = nodeVars[i].DEF_VAL_;
            } else if (nodeVars[i].KEY_ == 'isQueren2') {
                isQueren2 = nodeVars[i].DEF_VAL_;
            } else if (nodeVars[i].KEY_ == 'isMCT') {
                isMCT = nodeVars[i].DEF_VAL_;
            }
        }
        if (isBianzhi != 'yes') {
            formBusiness.setEnabled(false);
            mini.get("addFile").setEnabled(false);
            if (isQueren == 'yes') {
                mini.get("isHasProblem").setEnabled(true);
                mini.get("productSupervisorId").setEnabled(true);
                mini.get("problemDescription").setEnabled(true);
            } else if (isCPZGQueren == 'yes') {
                mini.get("isHasProblem").setEnabled(true);
                mini.get("problemDescription").setEnabled(true);
            }
        }
    }



    //..检验表单是否必填
    function validBusiness() {
        var salesModel = $.trim(mini.get("salesModel").getValue());
        if (!salesModel) {
            return {"result": false, "message": maintenanceManualFeedbackEdit_name8};
        }
        var designModel = $.trim(mini.get("designModel").getValue());
        if (!designModel) {
            return {"result": false, "message": maintenanceManualFeedbackEdit_name9};
        }
        var materialCode = $.trim(mini.get("materialCode").getValue());
        if (!materialCode) {
            return {"result": false, "message": maintenanceManualFeedbackEdit_name10};
        }
        var applyTime = $.trim(mini.get("applyTime").getValue());
        if (!applyTime) {
            return {"result": false, "message": maintenanceManualFeedbackEdit_name11};
        }
        var publishTime = $.trim(mini.get("publishTime").getValue());
        if (!publishTime) {
            return {"result": false, "message": maintenanceManualFeedbackEdit_name12};
        }
        var publishTime = $.trim(mini.get("publishTime").getValue());
        if (!publishTime) {
            return {"result": false, "message": maintenanceManualFeedbackEdit_name12};
        }
        return {"result": true};
    }

    //..文件列表操作渲染
    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnPreviewSpan(record.fileName, record.id, record.mainId, coverContent);
        //增加删除按钮
        if (action == 'edit' || (action == 'task' && isBianzhi == 'yes')) {
            var deleteUrl = "/serviceEngineering/core/maintenanceManualFeedback/delFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + maintenanceManualFeedbackEdit_name13 + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainId + '\',\'' + deleteUrl + '\')">' + maintenanceManualFeedbackEdit_name13 + '</span>';
        }
        return cellHtml;
    }

    //..文件列表预览渲染
    function returnPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title=' + maintenanceManualFeedbackEdit_name14 + ' style="color: silver" >' + maintenanceManualFeedbackEdit_name14 + '</span>';
        } else if (fileType == 'pdf') {
            var url = '/serviceEngineering/core/maintenanceManualFeedback/PdfPreview.do';
            s = '<span  title=' + maintenanceManualFeedbackEdit_name14 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + maintenanceManualFeedbackEdit_name14 + '</span>';
        } else if (fileType == 'office') {
            var url = '/serviceEngineering/core/maintenanceManualFeedback/OfficePreview.do';
            s = '<span  title=' + maintenanceManualFeedbackEdit_name14 + ' style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + maintenanceManualFeedbackEdit_name14 + '</span>';
        } else if (fileType == 'pic') {
            var url = '/serviceEngineering/core/maintenanceManualFeedback/ImagePreview.do';
            s = '<span  title=' + maintenanceManualFeedbackEdit_name14 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + maintenanceManualFeedbackEdit_name14 + '</span>';
        }
        return s;
    }
</script>
</body>
</html>
