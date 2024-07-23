<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>装修手册资料收集</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()"><spring:message code="page.decorationManualCollectEdit.name" /></a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()"><spring:message code="page.decorationManualCollectEdit.name1" /></a>
        <a id="showManualfile" class="mini-button" onclick="showManualfile()"><spring:message code="page.decorationManualCollectEdit.name2" /></a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 80%;">
        <form id="formBusiness" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="applyUserId" name="applyUserId" class="mini-hidden"/>
            <input id="applyDepId" name="applyDepId" class="mini-hidden"/>
            <input id="businessStatus" name="businessStatus" class="mini-hidden"/>
            <input id="demandId" name="demandId" class="mini-hidden"/>
            <input id="demandItemId" name="demandItemId" class="mini-hidden"/>
            <input id="demandInstId" name="demandInstId" class="mini-hidden"/>
            <input id="INST_ID_" name="INST_ID_" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold">
                    <spring:message code="page.decorationManualCollectEdit.name3" />
                </caption>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualCollectEdit.name4" />：</td>
                    <td style="min-width:170px">
                        <input id="busunessNo" name="busunessNo" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualCollectEdit.name5" />：</td>
                    <td style="min-width:170px">
                        <input id="demandBusunessNo" name="demandBusunessNo" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualCollectEdit.name6" />：</td>
                    <td style="min-width:170px">
                        <input id="instructions" name="instructions" class="mini-combobox" style="width:98%;" enabled="true"
                               textField="value" valueField="key" emptyText="<spring:message code="page.decorationManualCollectEdit.name7" />..."
                               allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.decorationManualCollectEdit.name7" />..."
                               data="[{'key' : '新增','value' : '新增'},{'key' : '变更','value' : '变更'},
							   {'key' : '翻译','value' : '翻译'}]"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualCollectEdit.name8" />：</td>
                    <td style="min-width:170px">
                        <input id="collectType" name="collectType" class="mini-combobox" style="width:98%;" enabled="true"
                               textField="value" valueField="key" emptyText="<spring:message code="page.decorationManualCollectEdit.name7" />..."
                               allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.decorationManualCollectEdit.name7" />..."
                               data="[{'key':'技术规格资料','value':'技术规格资料'},{'key':'力矩及工具标准值资料','value':'力矩及工具标准值资料'},
                           {'key':'故障代码','value':'故障代码'}]"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualCollectEdit.name9" />：</td>
                    <td style="min-width:170px">
                        <input id="salesModel" name="salesModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualCollectEdit.name10" />：</td>
                    <td style="min-width:170px">
                        <input id="designModel" name="designModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualCollectEdit.name11" />：</td>
                    <td style="min-width:170px">
                        <input id="materialCode" name="materialCode" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualCollectEdit.name12" />：</td>
                    <td style="min-width:170px">
                        <input id="manualLanguage" name="manualLanguage" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringLanguage"
                               valueField="key" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualCollectEdit.name13" />：</td>
                    <td style="min-width:170px">
                        <input id="salesArea" name="salesArea" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualCollectEdit.name14" />：</td>
                    <td style="min-width:170px">
                        <input id="manualCode" name="manualCode" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.decorationManualCollectEdit.name15" />：</td>
                    <td style="min-width:170px">
                        <input id="applyTime" name="applyTime" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                               valueType="string" showTime="false" showOkButton="true" showClearButton="false" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.decorationManualCollectEdit.name16" />：</td>
                    <td style="min-width:170px">
                        <input id="publishTime" name="publishTime" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                               valueType="string" showTime="false" showOkButton="true" showClearButton="false" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualCollectEdit.name17" />：</td>
                    <td style="min-width:170px">
                        <input id="collectorId" name="collectorId" textname="collector"
                               class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="<spring:message code="page.decorationManualCollectEdit.name17" />"
                               length="50"
                               mainfield="no" single="true"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualCollectEdit.name18" />：</td>
                    <td style="min-width:170px">
                        <input id="collectorId2" name="collectorId2" textname="collector2"
                               class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="<spring:message code="page.decorationManualCollectEdit.name18" />"
                               length="50"
                               mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualCollectEdit.name19" />：</td>
                    <td style="min-width:170px">
                        <input id="applyUser" name="applyUser" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualCollectEdit.name20" />：</td>
                    <td style="min-width:170px">
                        <input id="applyDep" name="applyDep" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.decorationManualCollectEdit.name21" />：</td>
                    <td colspan="3">
						<textarea id="remark" name="remark" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:98%;height:150px;line-height:25px;" label="" datatype="varchar" length="640"
                                  vtype="length:640" minlen="0" allowinput="true" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.decorationManualCollectEdit.name22" />：</td>
                    <td colspan="3">
                        <a id="downTemplate1" href="#" style="color:blue;text-decoration:underline;display:none"
                           onclick="downTemplate('技术规格资料')"><spring:message code="page.decorationManualCollectEdit.name23" /></a>
                        <a id="downTemplate2" href="#" style="color:blue;text-decoration:underline;display:none"
                           onclick="downTemplate('力矩及工具标准值资料')"><spring:message code="page.decorationManualCollectEdit.name24" /></a>
                        <a id="downTemplate3" href="#" style="color:blue;text-decoration:underline;display:none"
                           onclick="downTemplate('故障代码')"><spring:message code="page.decorationManualCollectEdit.name25" /></a>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;height: 250px"><spring:message code="page.decorationManualCollectEdit.name26" />：</td>
                    <td colspan="3">
                        <div style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="addBusinessFile()"><spring:message code="page.decorationManualCollectEdit.name27" /></a>
                            <span style="color: red"><spring:message code="page.decorationManualCollectEdit.name28" /></span>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
                             idField="id" autoload="true"
                             url="${ctxPath}/serviceEngineering/core/decorationManualCollect/getFileList.do?businessId=${businessId}&fileType=give"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20"><spring:message code="page.decorationManualCollectEdit.name29" /></div>
                                <div field="fileName" align="center" headerAlign="center" width="150"><spring:message code="page.decorationManualCollectEdit.name30" /></div>
                                <div field="fileSize" align="center" headerAlign="center" width="60"><spring:message code="page.decorationManualCollectEdit.name31" /></div>
                                <div field="fileDesc" align="center" headerAlign="center" width="100"><spring:message code="page.decorationManualCollectEdit.name32" /></div>
                                <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer"><spring:message code="page.decorationManualCollectEdit.name33" /></div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;height: 250px"><spring:message code="page.decorationManualCollectEdit.name34" />：</td>
                    <td colspan="3">
                        <div style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addFile2" class="mini-button" onclick="addBusinessFile2()"><spring:message code="page.decorationManualCollectEdit.name27" /></a>
                            <span style="color: red"><spring:message code="page.decorationManualCollectEdit.name28" /></span>
                        </div>
                        <div id="fileListGrid2" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
                             idField="id" autoload="true"
                             url="${ctxPath}/serviceEngineering/core/decorationManualCollect/getFileList.do?businessId=${businessId}&fileType=get"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20"><spring:message code="page.decorationManualCollectEdit.name29" /></div>
                                <div field="fileName" align="center" headerAlign="center" width="150"><spring:message code="page.decorationManualCollectEdit.name30" /></div>
                                <div field="fileSize" align="center" headerAlign="center" width="60"><spring:message code="page.decorationManualCollectEdit.name31" /></div>
                                <div field="fileDesc" align="center" headerAlign="center" width="100"><spring:message code="page.decorationManualCollectEdit.name32" /></div>
                                <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer2"><spring:message code="page.decorationManualCollectEdit.name33" /></div>
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
    var fileListGrid2 = mini.get("fileListGrid2");
    var formBusiness = new mini.Form("#formBusiness");
    var businessId = "${businessId}";
    var demandId = "${demandId}";
    var nodeVarsStr = '${nodeVars}';
    var currentTime = "${currentTime}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
    var codeName = "";
    //..
    $(function () {
//        //通过需求申请单自动打开，自动填写相应字段
//        if (demandId && demandId != '') {
//            var url = jsUseCtxPath + "/serviceEngineering/core/maintenanceManualDemand/getDetail.do";
//            $.post(
//                url,
//                {businessId: demandId},
//                function (json) {
//                    mini.get("BconfirmingId").setValue(json.BconfirmingId);
//                    mini.get("Bconfirming").setValue(json.Bconfirming);
//                    mini.get("demandId").setValue(json.id);//其实就是{businessId: demandId}的demandId
//                    mini.get("demandInstid").setValue(json.INST_ID_);
//                    mini.get("instructions").setValue(json.instructions);
//                    mini.get("instructions").setEnabled(false);
//                    mini.get("salesModel").setValue(json.salesModel);
//                    mini.get("salesModel").setEnabled(false);
//                    mini.get("designModel").setValue(json.designModel);
//                    mini.get("designModel").setEnabled(false);
//                    mini.get("materialCode").setValue(json.materialCode);
//                    mini.get("materialCode").setEnabled(false);
//                    mini.get("salesArea").setValue(json.salesArea);
//                    mini.get("salesArea").setEnabled(false);
//                    mini.get("manualLanguage").setValue(json.manualLanguage);
//                    mini.get("manualLanguage").setEnabled(false);
//                    mini.get("isCE").setValue(json.isCE);
//                    mini.get("isCE").setEnabled(false);
//                    mini.get("applyTime").setValue(json.applyTime);
//                    mini.get("applyTime").setEnabled(false);
//                    mini.get("publishTime").setValue(json.publishTime);
//                    mini.get("publishTime").setEnabled(false);
//                    mini.get("manualCode").setValue(json.manualCode);
////                    mini.get("manualCode").setEnabled(false);
//                    mini.get("demandBusunessNo").setValue(json.busunessNo);
//                    mini.get("demandListNo").setValue(json.demandListNo);
//                    if (action == 'detail') {
//                        formBusiness.setEnabled(false);
//                        mini.get("addFile").setEnabled(false);
//                        $("#detailToolBar").show();
//                        //非草稿放开流程信息查看按钮
//                        if (status != 'DRAFTED') {
//                            $("#processInfo").show();
//                        }
//                    } else if (action == 'task') {
//                        taskActionProcess();
//                    }
//                });
//        } else {//正常打开
        var url = jsUseCtxPath + "/serviceEngineering/core/decorationManualCollect/getDetail.do";
        $.post(
            url,
            {businessId: businessId},
            function (json) {
                formBusiness.setData(json);
                if (action == 'detail') {
                    formBusiness.setEnabled(false);
                    mini.get("addFile").setEnabled(false);
                    mini.get("addFile2").setEnabled(false);
                    $("#detailToolBar").show();
                    //非草稿放开流程信息查看按钮
                    if (status != 'DRAFTED') {
                        $("#processInfo").show();
                    }
                } else if (action == 'edit') {
                    $("#downTemplate1").show();
                    $("#downTemplate2").show();
                    $("#downTemplate3").show();
                } else if (action == 'task') {
                    taskActionProcess();
                }
//                    //正常打开的是通过需求申请单自动生成的单据
//                    if (json.demandId && json.demandId != '') {
//                        mini.get("instructions").setEnabled(false);
//                        mini.get("salesModel").setEnabled(false);
//                        mini.get("designModel").setEnabled(false);
//                        mini.get("materialCode").setEnabled(false);
//                        mini.get("salesArea").setEnabled(false);
//                        mini.get("manualLanguage").setEnabled(false);
//                        mini.get("isCE").setEnabled(false);
//                        mini.get("applyTime").setEnabled(false);
//                        mini.get("publishTime").setEnabled(false);
//                    }
            });
//        }
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
            url: jsUseCtxPath + '/serviceEngineering/core/decorationManualCollect/saveBusiness.do',
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message = "";
                    if (data.success) {
                        message = decorationManualCollectEdit_name;
                    } else {
                        message = decorationManualCollectEdit_name1 + data.message;
                    }

                    mini.alert(message, decorationManualCollectEdit_name2, function () {
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
            mini.alert(decorationManualCollectEdit_name3);
            return;
        }
        mini.open({
            title: decorationManualCollectEdit_name4,
            url: jsUseCtxPath + "/serviceEngineering/core/decorationManualCollect/fileUploadWindow.do?" +
            "businessId=" + businessId + "&fileType=give",
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
    function addBusinessFile2() {
        var businessId = mini.get("id").getValue();
        if (!businessId) {
            mini.alert(decorationManualCollectEdit_name3);
            return;
        }
        mini.open({
            title: decorationManualCollectEdit_name4,
            url: jsUseCtxPath + "/serviceEngineering/core/decorationManualCollect/fileUploadWindow.do?" +
            "businessId=" + businessId + "&fileType=get",
            width: 750,
            height: 450,
            showModal: false,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                if (fileListGrid2) {
                    fileListGrid2.load();
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
        if (codeName == 'A') {//如果是编辑节点，则和action=='edit'一样，放开通用模板下载
            $("#downTemplate1").show();
            $("#downTemplate2").show();
            $("#downTemplate3").show();
        } else {//如果不是编辑节点，首先吧表单和两个文件的上传权限都封死
            formBusiness.setEnabled(false);
            mini.get("addFile").setEnabled(false);
            mini.get("addFile2").setEnabled(false);
            if (codeName == 'C' || codeName == 'E') {//如果是资料上传节点，则放开资料上传权限
                mini.get("addFile2").setEnabled(true);
            }
            if (codeName == 'D') {//如果是资料确认节点，放开参考上传权限
                mini.get("addFile").setEnabled(true);
            }
        }
    }
    //..检验表单是否必填
    function validBusiness() {
        var instructions = $.trim(mini.get("instructions").getValue());
        if (!instructions) {
            return {"result": false, "message": decorationManualCollectEdit_name6};
        }
        var collectType = $.trim(mini.get("collectType").getValue());
        if (instructions != '翻译' && !collectType) {
            return {"result": false, "message": decorationManualCollectEdit_name7};
        }
        var salesModel = $.trim(mini.get("salesModel").getValue());
        if (!salesModel) {
            return {"result": false, "message": decorationManualCollectEdit_name8};
        }
        var designModel = $.trim(mini.get("designModel").getValue());
        if (!designModel) {
            return {"result": false, "message": decorationManualCollectEdit_name9};
        }
        var materialCode = $.trim(mini.get("materialCode").getValue());
        if (!materialCode) {
            return {"result": false, "message": decorationManualCollectEdit_name10};
        }
        var salesArea = $.trim(mini.get("salesArea").getValue());
        if (!salesArea) {
            return {"result": false, "message": decorationManualCollectEdit_name11};
        }
        var manualLanguage = $.trim(mini.get("manualLanguage").getValue());
        if (!manualLanguage) {
            return {"result": false, "message": decorationManualCollectEdit_name12};
        }
        var publishTime = $.trim(mini.get("publishTime").getValue());
        if (!publishTime) {
            return {"result": false, "message": decorationManualCollectEdit_name13};
        }
        var manualCode = $.trim(mini.get("manualCode").getValue());
        if (!manualCode && instructions != "新增") {
            return {"result": false, "message": decorationManualCollectEdit_name14};
        }
        var collectorId = $.trim(mini.get("collectorId").getValue());
        if (!collectorId) {
            return {"result": false, "message": decorationManualCollectEdit_name15};
        }
        var collectorId2 = $.trim(mini.get("collectorId2").getValue());
        if (collectType == '力矩及工具标准值资料' && !collectorId2) {
            return {"result": false, "message": decorationManualCollectEdit_name16};
        }
        if (fileListGrid.totalCount == 0 && instructions != "翻译") {
            return {"result": false, "message": decorationManualCollectEdit_name17};
        }
        return {"result": true};
    }
    //..文件列表操作渲染
    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnPreviewSpan(record.fileName, record.id, record.mainId, coverContent);
        //和pdf预览用的同一个url
        var downLoadUrl = '/serviceEngineering/core/decorationManualCollect/PdfPreviewAndAllDownload.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + decorationManualCollectEdit_name18 + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainId + '\',\'' + downLoadUrl + '\')">' + decorationManualCollectEdit_name18 + '</span>';
        //增加删除按钮
        if (action == 'edit' || (action == 'task' && (codeName == 'A' || codeName == 'D'))) {
            var deleteUrl = "/serviceEngineering/core/decorationManualCollect/delFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + decorationManualCollectEdit_name19 + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainId + '\',\'' + deleteUrl + '\')">' + decorationManualCollectEdit_name19 + '</span>';
        }
        return cellHtml;
    }
    function operationRenderer2(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnPreviewSpan(record.fileName, record.id, record.mainId, coverContent);
        //和pdf预览用的同一个url
        var downLoadUrl = '/serviceEngineering/core/decorationManualCollect/PdfPreviewAndAllDownload.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + decorationManualCollectEdit_name18 + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainId + '\',\'' + downLoadUrl + '\')">' + decorationManualCollectEdit_name18 + '</span>';
        //增加删除按钮
        if (action == 'task' && (codeName == 'C' || codeName == 'E')) {
            var deleteUrl = "/serviceEngineering/core/decorationManualCollect/delFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + decorationManualCollectEdit_name19 + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile2(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainId + '\',\'' + deleteUrl + '\')">' + decorationManualCollectEdit_name19 + '</span>';
        }
        return cellHtml;
    }
    //公用deleteFile和fileListGrid耦合，我不改了，加一个吧
    function deleteFile2(fileName, fileId, formId, urlValue) {
        mini.confirm(decorationManualCollectEdit_name20, decorationManualCollectEdit_name21,
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
                            if (fileListGrid2) {
                                fileListGrid2.load();
                            }
                        }
                    });
                }
            }
        );
    }
    //..文件列表预览渲染
    function returnPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title=' + decorationManualCollectEdit_name22 + ' style="color: silver" >' + decorationManualCollectEdit_name22 + '</span>';
        } else if (fileType == 'pdf') {
            var url = '/serviceEngineering/core/decorationManualCollect/PdfPreview.do';
            s = '<span  title=' + decorationManualCollectEdit_name22 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + decorationManualCollectEdit_name22 + '</span>';
        } else if (fileType == 'office') {
            var url = '/serviceEngineering/core/decorationManualCollect/OfficePreview.do';
            s = '<span  title=' + decorationManualCollectEdit_name22 + ' style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + decorationManualCollectEdit_name22 + '</span>';
        } else if (fileType == 'pic') {
            var url = '/serviceEngineering/core/decorationManualCollect/ImagePreview.do';
            s = '<span  title=' + decorationManualCollectEdit_name22 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + decorationManualCollectEdit_name22 + '</span>';
        }
        return s;
    }
    //..便捷查看相关归档文件
    function showManualfile() {
        var salesModel = mini.get("salesModel").getValue();
        var designModel = mini.get("designModel").getValue();
        var materialCode = mini.get("materialCode").getValue();
        var manualLanguage = mini.get("manualLanguage").getValue();
        var manualType = "装修手册";
        var realUrl = jsUseCtxPath + "/serviceEngineering/core/decorationManualFile/dataListPage.do?salesModel=" + salesModel +
            "&designModel=" + designModel + "&materialCode=" + materialCode + "&manualLanguage=" + manualLanguage + "&manualType=" + manualType;
        var config = {
            url: realUrl,
            max: true
        };
        _OpenWindow(config);
    }
    //..
    function downTemplate(type) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/decorationManualCollect/templateDownload.do?type=" + type);
        $("body").append(form);
        form.submit();
        form.remove();
    }
</script>
</body>
</html>
