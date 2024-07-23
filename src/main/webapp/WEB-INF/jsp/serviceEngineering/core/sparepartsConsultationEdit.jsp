<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>备件问题咨询</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()"><spring:message code="page.sparepartsConsultationEdit.name" /></a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()"><spring:message code="page.sparepartsConsultationEdit.name1" /></a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 80%;">
        <form id="formBusiness" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="applyUserId" name="applyUserId" class="mini-hidden"/>
            <input id="applyDepId" name="applyDepId" class="mini-hidden"/>
            <input id="businessStatus" name="businessStatus" class="mini-hidden"/>
            <input id="submitTimestamp" name="submitTimestamp" class="mini-hidden"/>
            <input id="businessStatusTimestamp" name="businessStatusTimestamp" class="mini-hidden"/>
            <input id="INST_ID_" name="INST_ID_" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold">
                    <spring:message code="page.sparepartsConsultationEdit.name2" />
                </caption>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.sparepartsConsultationEdit.name3" />：</td>
                    <td style="min-width:170px">
                        <input id="applyUser" name="applyUser" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.sparepartsConsultationEdit.name4" />：</td>
                    <td style="min-width:170px">
                        <input id="contactInformation" name="contactInformation" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.sparepartsConsultationEdit.name5" />：</td>
                    <td style="min-width:170px">
                        <input id="applyDep" name="applyDep" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.sparepartsConsultationEdit.name6" />：</td>
                    <td style="min-width:170px">
                        <input id="pin" name="pin" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.sparepartsConsultationEdit.name7" />：</td>
                    <td style="min-width:170px">
                        <input id="salesModel" name="salesModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.sparepartsConsultationEdit.name8" />：</td>
                    <td style="min-width:170px">
                        <input id="designModel" name="designModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.sparepartsConsultationEdit.name9" />：</td>
                    <td colspan="3">
                        <input id="problemSummary" name="problemSummary" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.sparepartsConsultationEdit.name10" />：</td>
                    <td colspan="3">
						<textarea id="problemDescription" name="problemDescription" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:98%;height:150px;line-height:25px;" label="" datatype="varchar" length="1000"
                                  vtype="length:1000" minlen="0" allowinput="true" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.sparepartsConsultationEdit.name11" />：</td>
                    <td style="min-width:170px">
                        <input id="problemType" name="problemType" class="mini-combobox" style="width:98%" enabled="false"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringSparepartsConsultationProblemType"
                               valueField="key" textField="value"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.sparepartsConsultationEdit.name12" />：</td>
                    <td style="min-width:170px">
                        <input id="isCoordinate" name="isCoordinate" class="mini-combobox" style="width:98%;" enabled="false"
                               textField="value" valueField="key" emptyText="<spring:message code="page.sparepartsConsultationEdit.name13" />..."
                               allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.sparepartsConsultationEdit.name13" />..."
                               data="[{'key' : '是','value' : '是'},
							   {'key' : '否','value' : '否'}]"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.sparepartsConsultationEdit.name14" />：</td>
                    <td style="min-width:170px">
                        <input id="coordinatorId" name="coordinatorId" textname="coordinator"
                               class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
                               label="<spring:message code="page.sparepartsConsultationEdit.name14" />" length="50" mainfield="no" single="true" enabled="false" onvaluechanged="valuechanged"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.sparepartsConsultationEdit.name15" />：</td>
                    <td style="min-width:170px">
                        <input id="coordinatorPhoneNo" name="coordinatorPhoneNo" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.sparepartsConsultationEdit.name16" />：</td>
                    <td style="min-width:170px">
                        <input id="isGss" name="isGss" class="mini-combobox" style="width:98%;" enabled="false"
                               textField="value" valueField="key" emptyText="<spring:message code="page.sparepartsConsultationEdit.name13" />..."
                               allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.sparepartsConsultationEdit.name13" />..."
                               data="[{'key' : '是','value' : '是'},
							   {'key' : '否','value' : '否'}]"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.sparepartsConsultationEdit.name17" />：</td>
                    <td style="min-width:170px">
                        <input id="gsserId" name="gsserId" textname="gsser"
                               class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
                               label="<spring:message code="page.sparepartsConsultationEdit.name17" />" length="50" mainfield="no" single="true" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.sparepartsConsultationEdit.name18" />：</td>
                    <td colspan="3">
						<textarea id="serviceEngineeringReply" name="serviceEngineeringReply" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:98%;height:150px;line-height:25px;" label="" datatype="varchar" length="1000"
                                  enabled="false"
                                  vtype="length:1000" minlen="0" allowinput="true" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.sparepartsConsultationEdit.name19" />：</td>
                    <td colspan="3">
						<textarea id="productInstituteReply" name="productInstituteReply" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:98%;height:150px;line-height:25px;" label="" datatype="varchar" length="1000"
                                  enabled="false"
                                  vtype="length:1000" minlen="0" allowinput="true" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.sparepartsConsultationEdit.name20" />：</td>
                    <td colspan="3">
						<textarea id="gsserReply" name="gsserReply" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:98%;height:150px;line-height:25px;" label="" datatype="varchar" length="1000"
                                  enabled="false"
                                  vtype="length:1000" minlen="0" allowinput="true" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;height: 300px"><spring:message code="page.sparepartsConsultationEdit.name21" />：</td>
                    <td colspan="3">
                        <div style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="addBusinessFile()"><spring:message code="page.sparepartsConsultationEdit.name22" /></a>
                            <span style="color: red"><spring:message code="page.sparepartsConsultationEdit.name23" /></span>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
                             idField="id"
                             url="${ctxPath}/serviceEngineering/core/sparepartsConsultation/getFileList.do?businessId=${businessId}"
                             autoload="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20"><spring:message code="page.sparepartsConsultationEdit.name24" /></div>
                                <div field="fileName" align="center" headerAlign="center" width="150"><spring:message code="page.sparepartsConsultationEdit.name25" /></div>
                                <div field="fileSize" align="center" headerAlign="center" width="60"><spring:message code="page.sparepartsConsultationEdit.name26" /></div>
                                <div field="fileDesc" align="center" headerAlign="center" width="100"><spring:message code="page.sparepartsConsultationEdit.name27" /></div>
                                <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer"><spring:message code="page.sparepartsConsultationEdit.name28" /></div>
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
    var isCPQueren = "";
    var isQueren2 = "";
    var isQueren3 = "";
    //..
    $(function () {
        var url = jsUseCtxPath + "/serviceEngineering/core/sparepartsConsultation/getDetail.do";
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
            url: jsUseCtxPath + '/serviceEngineering/core/sparepartsConsultation/saveBusiness.do',
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message = "";
                    if (data.success) {
                        message = sparepartsConsultationEdit_name;
                    } else {
                        message = sparepartsConsultationEdit_name1 + data.message;
                    }

                    mini.alert(message, sparepartsConsultationEdit_name2, function () {
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
        } else if (isQueren == 'yes') {
            var problemType = $.trim(mini.get("problemType").getValue())
            if (!problemType || problemType == '') {
                mini.alert(sparepartsConsultationEdit_name3);
                return;
            }
            var isCoordinate = $.trim(mini.get("isCoordinate").getValue())
            if (!isCoordinate || isCoordinate == '') {
                mini.alert(sparepartsConsultationEdit_name4);
                return;
            }
            var coordinatorId = mini.get("coordinatorId").getValue();
            if (isCoordinate == "是" && (!coordinatorId || coordinatorId == '')) {
                mini.alert(sparepartsConsultationEdit_name5);
                return;
            }
        } else if (isCPQueren == 'yes') {
            var productInstituteReply = $.trim(mini.get("productInstituteReply").getValue())
            if (!productInstituteReply || productInstituteReply == '') {
                mini.alert(sparepartsConsultationEdit_name6);
                return;
            }
        } else if (isQueren2 == 'yes') {
            var isGss = $.trim(mini.get("isGss").getValue())
            if (!isGss || isGss == '') {
                mini.alert(sparepartsConsultationEdit_name7);
                return;
            }
            var gsserId = mini.get("gsserId").getValue();
            if (isGss == "是" && (!gsserId || gsserId == '')) {
                mini.alert(sparepartsConsultationEdit_name8);
                return;
            }
        } else if (isQueren3 == 'yes') {
            var gsserReply = $.trim(mini.get("gsserReply").getValue())
            if (!gsserReply || gsserReply == '') {
                mini.alert(sparepartsConsultationEdit_name9);
                return;
            }
        }
        //检查通过
        window.parent.approve();
    }

    //..添加文件
    function addBusinessFile() {
        var businessId = mini.get("id").getValue();
        if (!businessId) {
            mini.alert(sparepartsConsultationEdit_name10);
            return;
        }
        mini.open({
            title: sparepartsConsultationEdit_name11,
            url: jsUseCtxPath + "/serviceEngineering/core/sparepartsConsultation/fileUploadWindow.do?businessId=" + businessId,
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
            title: sparepartsConsultationEdit_name12,
            width: 800,
            height: 600
        });
    }

    //..获取任务相关的环境变量，处理表单可见性
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
            if (nodeVars[i].KEY_ == 'isBianzhi') {
                isBianzhi = nodeVars[i].DEF_VAL_;
            } else if (nodeVars[i].KEY_ == 'isQueren') {
                isQueren = nodeVars[i].DEF_VAL_;
            } else if (nodeVars[i].KEY_ == 'isCPQueren') {
                isCPQueren = nodeVars[i].DEF_VAL_;
            } else if (nodeVars[i].KEY_ == 'isQueren2') {
                isQueren2 = nodeVars[i].DEF_VAL_;
            } else if (nodeVars[i].KEY_ == 'isQueren3') {
                isQueren3 = nodeVars[i].DEF_VAL_;
            }
            if (isBianzhi != 'yes') {
                formBusiness.setEnabled(false);
                mini.get("addFile").setEnabled(false);
                if (isQueren == 'yes') {
                    mini.get("problemType").setEnabled(true);
                    mini.get("isCoordinate").setEnabled(true);
                    mini.get("coordinatorId").setEnabled(true);
                    mini.get("coordinatorPhoneNo").setEnabled(true);
                    mini.get("serviceEngineeringReply").setEnabled(true);
                } else if (isCPQueren == 'yes') {
                    mini.get("productInstituteReply").setEnabled(true);
                } else if (isQueren2 == 'yes') {
                    mini.get("isGss").setEnabled(true);
                    mini.get("gsserId").setEnabled(true);
                } else if (isQueren3 == 'yes') {
                    mini.get("gsserReply").setEnabled(true);
                }
            }
        }
    }

    //..检验表单是否必填
    function validBusiness() {
//        var pin = $.trim(mini.get("pin").getValue());
//        if (!pin) {
//            return {"result": false, "message": "请填写整车编号"};
//        }
//        var salesModel = $.trim(mini.get("salesModel").getValue());
//        if (!salesModel) {
//            return {"result": false, "message": "请填写销售型号"};
//        }
        var problemSummary = $.trim(mini.get("problemSummary").getValue());
        if (!problemSummary) {
            return {"result": false, "message": sparepartsConsultationEdit_name13};
        }
        var problemDescription = $.trim(mini.get("problemDescription").getValue());
        if (!problemDescription) {
            return {"result": false, "message": sparepartsConsultationEdit_name14};
        }
        return {"result": true};
    }

    //..文件列表操作渲染
    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnPreviewSpan(record.fileName, record.id, record.mainId, coverContent);
        //和pdf预览用的同一个url
        var downLoadUrl = '/serviceEngineering/core/sparepartsConsultation/PdfPreviewAndAllDownload.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + sparepartsConsultationEdit_name15 + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainId + '\',\'' + downLoadUrl + '\')">' + sparepartsConsultationEdit_name15 + '</span>';
        //增加删除按钮
        if (action == 'edit' || (action == 'task' && isBianzhi == 'yes')) {
            var deleteUrl = "/serviceEngineering/core/sparepartsConsultation/delFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + sparepartsConsultationEdit_name16 + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainId + '\',\'' + deleteUrl + '\')">' + sparepartsConsultationEdit_name16 + '</span>';
        }
        return cellHtml;
    }

    //..文件列表预览渲染
    function returnPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title=' + sparepartsConsultationEdit_name17 + ' style="color: silver" >' + sparepartsConsultationEdit_name17 + '</span>';
        } else if (fileType == 'pdf') {
            var url = '/serviceEngineering/core/sparepartsConsultation/PdfPreviewAndAllDownload.do';
            s = '<span  title=' + sparepartsConsultationEdit_name17 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + sparepartsConsultationEdit_name17 + '</span>';
        } else if (fileType == 'office') {
            var url = '/serviceEngineering/core/sparepartsConsultation/OfficePreview.do';
            s = '<span  title=' + sparepartsConsultationEdit_name17 + ' style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + sparepartsConsultationEdit_name17 + '</span>';
        } else if (fileType == 'pic') {
            var url = '/serviceEngineering/core/sparepartsConsultation/ImagePreview.do';
            s = '<span  title=' + sparepartsConsultationEdit_name17 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + sparepartsConsultationEdit_name17 + '</span>';
        }
        return s;
    }

    //..
    function valuechanged() {
        mini.get("coordinatorPhoneNo").setValue("");
    }
</script>
</body>
</html>
