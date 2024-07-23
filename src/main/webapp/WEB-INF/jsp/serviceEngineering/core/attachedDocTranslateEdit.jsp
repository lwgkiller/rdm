<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>技术资料翻译申请单</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 100%;">
        <form id="formBusiness" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="sourceBusinessId" name="sourceBusinessId" class="mini-hidden"/>
            <input id="sourceInstId" name="sourceInstId" class="mini-hidden"/>
            <input id="sourceBpmSolKey" name="sourceBpmSolKey" class="mini-hidden"/>
            <input id="sourceRefId" name="sourceRefId" class="mini-hidden"/>
            <input id="applyId" name="applyId" class="mini-hidden"/>
            <input id="applyDepId" name="applyDepId" class="mini-hidden"/>
            <input id="businessStatus" name="businessStatus" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold">
                    技术资料翻译申请单
                </caption>
                <tr>
                    <td style="text-align: center;width: 15%">翻译申请单号(自动生成)：<span style="color:red">*</span>
                    </td>
                    <td style="min-width:170px">
                        <input id="transApplyId" name="transApplyId" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">文件类型:<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="manualType" name="manualType" class="mini-combobox" style="width:98%;" enabled="true"
                               textField="value" valueField="key" emptyText="请选择..."
                               allowInput="false" showNullItem="true" nullItemText="请选择..."
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=JSZLFYSQWJLX"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">销售型号：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="saleType" name="saleType" textname="saleType" style="width:98%;height:34px;"
                               class="mini-buttonedit" showClose="true" allowInput="true"
                               oncloseclick="inputNameCloseClick()" onbuttonclick="inputNameClick()"/>
                    </td>
                    <td style="text-align: center;width: 15%">设计型号：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="designType" name="designType" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">物料编码：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="materialCode" name="materialCode" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">手册编码：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="manualCode" name="manualCode" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">是/否CE版手册：</td>
                    <td style="min-width:170px">
                        <input id="mannulCE" name="mannulCE" class="mini-combobox" style="width:98%;" enabled="true"
                               textField="value" valueField="key" emptyText="-"
                               allowInput="false" showNullItem="true" nullItemText="-"
                               data="[{'key' : '是','value' : '是'},
							   {'key' : '否','value' : '否'}]"/>
                    </td>
                    <td style="text-align: center;width: 20%">手册版本：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="manualVersion" name="manualVersion" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">发运需求申请单编号：</td>
                    <td style="min-width:170px">
                        <input id="transportApplyId" name="transportApplyId" class="mini-textbox" style="width:98%;">
                    </td>
                    <td style="text-align: center;width: 20%">发运需求申请单对应需求单号：</td>
                    <td style="min-width:170px">
                        <input id="transportApplyNo" name="transportApplyNo" class="mini-textbox" style="width:98%;">
                    </td>
                </tr>
                <tr>

                    <td style="text-align: center;width: 20%">申请时间：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="applyTime" name="applyTime" class="mini-datepicker" format="yyyy-MM-dd"
                               allowInput="false"
                               valueType="string" showTime="false" showOkButton="false" showClearButton="false"
                               style="width:98%;"/>
                    </td>
                    </td>
                    <td style="text-align: center;width: 20%">需求时间：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="needTime" name="needTime" class="mini-datepicker" format="yyyy-MM-dd"
                               allowInput="false"
                               valueType="string" showTime="false" showOkButton="false" showClearButton="false"
                               style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">源手册语言：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="sourceManualLan" name="sourceManualLan" class="mini-combobox" style="width:98%;"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringLanguage"
                               valueField="key" textField="value"/>
                    </td>
                    <td style="text-align: center;width: 20%">翻译语言：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="targetManualLan" name="targetManualLan" class="mini-combobox" style="width:98%;"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringLanguage"
                               valueField="key" textField="value" multiSelect="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">翻译人员<span style="color:red">*</span>：</td>
                    <td style="min-width:170px">
                        <input id="translatorId" name="translatorId" textname="translator" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="翻译人员"
                               showclose="true"
                               length="200" maxlength="200" mainfield="no" single="true"/>
                    </td>

                    <td style="text-align: center;width: 20%">总字符数：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="totalCodeNum" name="totalCodeNum" class="mini-textbox" style="width:98%;"
                               enabled="false">
                    </td>
                </tr>
                <tr>

                    <td style="text-align: center;width: 20%">自翻译率：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="selfTranslatePercent" name="selfTranslatePercent" class="mini-textbox"
                               style="width:98%;" enabled="false">
                    </td>
                    <td style="text-align: center;width: 20%">外发翻译率：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="outTranslatePercent" name="outTranslatePercent" class="mini-textbox"
                               style="width:98%;" enabled="false">
                    </td>
                </tr>
                <tr>

                    <td style="text-align: center;width: 20%">申请人(自动生成)：</td>
                    <td style="min-width:170px">
                        <input id="creatorName" name="creatorName" class="mini-textbox" style="width:98%;"
                               enabled="false">
                    </td>

                    <td style="text-align: center;width: 20%">申请部门（自动生成）：</td>
                    <td style="min-width:170px">
                        <input id="applyDep" name="applyDep" class="mini-textbox" style="width:98%;" enabled="false">
                    </td>
                </tr>

                <tr>
                    <td style="text-align: center;width: 20%;height: 150px">备注：（不超过1000字）</td>
                    <td colspan="3">
						<textarea id="remark" name="remark" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:99%;line-height:25px;"
                                  label="说明" datatype="varchar" allowinput="true"
                                  emptytext="请输入备注说明..." mwidth="80" wunit="%" mheight="20000" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;height: 300px">外发及归档文件清单：</td>
                    <td colspan="3">
                        <div style="margin-top: 10px;margin-bottom: 2px">
                            <a class="mini-button" id="selfUploadFile" name="selfUploadFile" plain="true" onclick="addOutFile">外发文件上传</a>
                            <a class="mini-button btn-red" id="delDemand" name="delDemand" plain="true" onclick="delDataRow">移除</a>
                            <a class="mini-button" id="uploadReturnFile" name="uploadReturnFile" plain="true" onclick="uploadReturnFile">回传文件上传</a>
                            <a class="mini-button" id="saveFileContract" name="saveFileContract" plain="true" onclick="saveDemand">保存信息</a>
                        </div>
                        <div id="demandGrid" class="mini-datagrid" allowResize="false" style="height:340px"
                             autoload="true"
                             idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                             multiSelect="false" showColumnsMenu="false" allowAlternating="true" showPager="false"
                             url="${ctxPath}/serviceEngineering/core/externalTranslation/demandList.do?applyId=${businessId}"
                             oncellbeginedit="OnCellBeginEdit">
                            <div property="columns">
                                <div type="checkcolumn" width="50"></div>
                                <div type="indexcolumn" headerAlign="center" width="50">序号</div>
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
                    </td>
                </tr>
                <%--附件--%>
                <tr>
                    <td style="text-align: center;height: 300px">附件列表：</td>
                    <td colspan="3">
                        <div style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="addBusinessFile()">添加文件</a>
                            <span style="color: red">注：添加文件前，请先进行草稿的保存</span>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%"
                             allowResize="false"
                             idField="id"
                             url="${ctxPath}/serviceEngineering/core/attachedDocTranslate/getFileList.do?businessId=${businessId}"
                             autoload="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20">序号</div>
                                <div field="fileName" align="center" headerAlign="center" width="150">文件名</div>
                                <div field="fileSize" align="center" headerAlign="center" width="60">文件大小</div>
                                <div field="fileDesc" align="center" headerAlign="center" width="100">备注说明</div>
                                <div field="userName" width="100" headerAlign="center" align="center">创建者</div>
                                <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center">创建时间</div>
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

<div id="selectMaintenanceManualMctWindow" title="选择操保手册" class="mini-window" style="width:1300px;height:550px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span style="font-size: 14px;color: #777">单据编号: </span>
        <input class="mini-textbox" width="130" id="filterBusunessNo" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">销售型号: </span>
        <input class="mini-textbox" width="130" id="filterSalesModel" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">设计型号: </span>
        <input class="mini-textbox" width="130" id="filterDesignModel" style="margin-right: 15px"/>
        <a class="mini-button" plain="true" onclick="searchInputList()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="maintenanceManualMctListGrid" class="mini-datagrid" style="width: 100%; height: 100%;"
             allowResize="false" onrowdblclick="onRowDblClick"
             idField="id" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20"
             allowAlternating="true" pagerButtons="#pagerButtons"
             url="${ctxPath}/serviceEngineering/core/maintenanceManualMct/dataListQuery.do"
        >
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div field="busunessNo" sortField="busunessNo" width="140" headerAlign="center" align="center"
                     align="center" allowSort="true">编号
                </div>
                <div field="salesModel" sortField="salesModel" width="160" headerAlign="center" align="center"
                     allowSort="true">销售型号
                </div>
                <div field="designModel" sortField="designModel" width="160" headerAlign="center" align="center"
                     allowSort="true">设计型号
                </div>
                <div field="materialCode" sortField="materialCode" width="90" headerAlign="center" align="center"
                     allowSort="true">物料编码
                </div>

                <div field="isCE" sortField="isCE" width="90" headerAlign="center" align="center"
                     allowSort="true">是否CE版本
                </div>
                <div field="businessStatus" width="150" headerAlign="center" align="center"
                     allowSort="true" renderer="onMaintenanceManualMctStatusRenderer">
                    审批状态
                </div>
                <div field="demandBusunessNo" sortField="demandBusunessNo" width="170" headerAlign="center"
                     align="center"
                     allowSort="true">发运需求申请单编号
                </div>
                <div field="demandListNo" sortField="demandListNo" width="200" headerAlign="center" align="center"
                     allowSort="true">发运需求申请单对应需求单号
                </div>
                <div field="applyUser" sortField="applyUser" align="center"
                     width="60" headerAlign="center" allowSort="true">起草人
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectInputOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectInputHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var status = "${status}";
    var businessStatus = mini.get("businessStatus");
    var jsUseCtxPath = "${ctxPath}";
    var fileListGrid = mini.get("fileListGrid");
    var demandGrid = mini.get("demandGrid");
    //var externalTranslationGrid = mini.get("externalTranslationGrid");
    var formBusiness = new mini.Form("#formBusiness");
    var businessId = "${businessId}";
    var nodeVarsStr = '${nodeVars}';
    var currentTime = "${currentTime}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";

    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var stageName = "";

    var maintenanceManualMctListGrid = mini.get("maintenanceManualMctListGrid");
    var selectMaintenanceManualMctWindow = mini.get("selectMaintenanceManualMctWindow");
    //..
    $(function () {
        mini.get("saveFileContract").hide();
        var url = jsUseCtxPath + "/serviceEngineering/core/attachedDocTranslate/getDetail.do";
        $.post(
            url,
            {businessId: businessId},
            function (json) {
                formBusiness.setData(json);
                if (action == 'detail') {
                    if (currentUserId == mini.get("translatorId").getValue() || currentUserId == '1') {
                        mini.get("saveFileContract").show();
                    }
                    formBusiness.setEnabled(false);
                    mini.get("addFile").setEnabled(false);
                    mini.get("uploadReturnFile").hide();
                    mini.get("selfUploadFile").hide();
                    mini.get("delDemand").hide();
                    demandGrid.hideColumn("returnFileName");
                    demandGrid.hideColumn("returnFileTime");
                    demandGrid.hideColumn("reop");
                    $("#detailToolBar").show();
                    //非草稿放开流程信息查看按钮
                    if (status != 'DRAFTED') {
                        $("#processInfo").show();
                    }
                } else if (action == 'task') {
                    taskActionProcess();
                } else if (action == 'edit') {
                    mini.get("uploadReturnFile").hide();
                    mini.get("selfUploadFile").hide();
                    mini.get("delDemand").hide();
                    demandGrid.hideColumn("returnFileName");
                    demandGrid.hideColumn("returnFileTime");
                    demandGrid.hideColumn("reop");
                }
            });
    });

    //..流程引擎调用此方法进行表单数据的获取
    function getData() {
        var formData = _GetFormJsonMini("formBusiness");
        if (formData.SUB_fileListGrid) {
            delete formData.SUB_fileListGrid;
        }

        return formData;
    }

    //..保存草稿
    function saveBusiness(e) {
        mini.get("applyId").setValue(currentUserId);
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

    //..流程中暂存信息（如编制阶段,加上翻译组审核阶段）
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
            url: jsUseCtxPath + '/serviceEngineering/core/attachedDocTranslate/saveBusiness.do',
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
        //编制阶段的下一步需要校验表单必填字段
        if (stageName == 'start') {
            var formValid = validBusiness();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if (stageName == 'applyProcess') {
            var formValid2 = validBusiness2();
            if (!formValid2.result) {
                mini.alert(formValid2.message);
                return;
            }
        }
        if (stageName == 'outgo') {
            var listFile = fileListGrid.getData();
            if (listFile.length == 0) {
                mini.alert("必须上传附件归档后再点下一步！");
                return;
            } else if (mini.get("sourceBpmSolKey").getValue() == 'PresaleDocumentApply' && listFile.length != 1) {
                mini.alert("售前文件自动发起的流程只能保留1个译文！以便于回传");
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
            mini.alert('请先点击‘保存草稿’进行表单的保存！');
            return;
        }
        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/serviceEngineering/core/attachedDocTranslate/fileUploadWindow.do?businessId=" + businessId,
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
                stageName = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'stageName') {
                stageName = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'isOutgo') {
                stageName = nodeVars[i].DEF_VAL_;
            }

        }
        if (stageName != 'start') {
            formBusiness.setEnabled(false);
            mini.get("addFile").setEnabled(false);
            if (stageName == 'applyProcess') {
                mini.get("totalCodeNum").setEnabled(true);
                mini.get("selfTranslatePercent").setEnabled(true);
                mini.get("outTranslatePercent").setEnabled(true);
                mini.get("addFile").setEnabled(true);
                //外发
                mini.get("uploadReturnFile").hide();
                demandGrid.hideColumn("returnFileName");
                demandGrid.hideColumn("returnFileTime");
                demandGrid.hideColumn("reop");
            } else if (stageName == 'outgo') {
                mini.get("addFile").setEnabled(true);
                //外发
                mini.get("selfUploadFile").hide();
                mini.get("delDemand").hide();
            } else if (stageName == 'final') {//最终审核
                mini.get("uploadReturnFile").hide();
                //外发
                mini.get("selfUploadFile").hide();
                mini.get("delDemand").hide();
            } else {
                mini.get("uploadReturnFile").hide();
                mini.get("selfUploadFile").hide();
                mini.get("delDemand").hide();
                demandGrid.hideColumn("returnFileName");
                demandGrid.hideColumn("returnFileTime");
                demandGrid.hideColumn("reop");
            }
        } else {
            mini.get("uploadReturnFile").hide();
            mini.get("selfUploadFile").hide();
            mini.get("delDemand").hide();
            demandGrid.hideColumn("returnFileName");
            demandGrid.hideColumn("returnFileTime");
            demandGrid.hideColumn("reop");
        }
    }

    //..检验表单是否必填(编制状态)
    function validBusiness() {
        var manualType = $.trim(mini.get("manualType").getValue());
        if (!manualType) {
            return {"result": false, "message": "请填写手册类型"};
        }
        var saleType = $.trim(mini.get("saleType").getValue());
        if (!saleType) {
            return {"result": false, "message": "请填写销售型号"};
        }
        var designType = $.trim(mini.get("designType").getValue());
        if (!designType) {
            return {"result": false, "message": "请填写设计型号"};
        }
        var materialCode = $.trim(mini.get("materialCode").getValue());
        if (!materialCode) {
            return {"result": false, "message": "请填写物料编码"};
        }
        var manualCode = $.trim(mini.get("manualCode").getValue());
        if (!manualCode) {
            return {"result": false, "message": "请填写手册编码"};
        }
        var manualVersion = $.trim(mini.get("manualVersion").getValue());
        if (!manualVersion) {
            return {"result": false, "message": "请填写手册版本"};
        }

        var applyTime = $.trim(mini.get("applyTime").getValue());
        if (!applyTime) {
            return {"result": false, "message": "请确定申请时间"};
        }
        var needTime = $.trim(mini.get("needTime").getValue());
        if (!needTime) {
            return {"result": false, "message": "请确定需求时间"};
        }
        var sourceManualLan = $.trim(mini.get("sourceManualLan").getValue());
        if (!sourceManualLan) {
            return {"result": false, "message": "请确定源手册语言"};
        }
        var targetManualLan = $.trim(mini.get("targetManualLan").getValue());
        if (!targetManualLan) {
            return {"result": false, "message": "请确定翻译语言"};
        }
        var translatorId = $.trim(mini.get("translatorId").getValue());
        if (!translatorId) {
            return {"result": false, "message": "请确定翻译人员"};
        }
        return {"result": true};
    }

    //..检验表单是否必填(审核状态)
    function validBusiness2() {

        var totalCodeNum = $.trim(mini.get("totalCodeNum").getValue());
        if (!totalCodeNum) {
            return {"result": false, "message": "请确定总字符数"};
        }
        var selfTranslatePercent = $.trim(mini.get("selfTranslatePercent").getValue());
        if (!selfTranslatePercent) {
            return {"result": false, "message": "请确定自翻译率"};
        }
        var outTranslatePercent = $.trim(mini.get("outTranslatePercent").getValue());
        if (!outTranslatePercent) {
            return {"result": false, "message": "请确定外发翻译率"};
        }
        return {"result": true};
    }

    //..文件列表操作渲染
    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        var userName = e.record.userName;
        cellHtml = returnPreviewSpan(record.fileName, record.id, record.mainId, coverContent);

        var downLoadUrl = '/serviceEngineering/core/attachedDocTranslate/PdfPreviewAndAllDownload.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainId + '\',\'' + downLoadUrl + '\')">下载</span>';

        //增加删除按钮
        if (action == 'edit' || (action == 'task' && userName == currentUserName && stageName == 'applyProcess')) {
            var deleteUrl = "/serviceEngineering/core/attachedDocTranslate/delFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        if (action == 'task' && (userName == currentUserName || mini.get("sourceBpmSolKey").getValue() == 'PresaleDocumentApply') && stageName == 'outgo') {
            var deleteUrl = "/serviceEngineering/core/attachedDocTranslate/delFile.do"
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
            var url = '/serviceEngineering/core/attachedDocTranslate/PdfPreviewAndAllDownload.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/serviceEngineering/core/attachedDocTranslate/OfficePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/serviceEngineering/core/attachedDocTranslate/ImagePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        return s;
    }

    //..新弹窗
    function inputNameCloseClick() {
        // mini.get("manualType").setValue("");
        // mini.get("manualType").setText("");
        mini.get("saleType").setValue("");
        mini.get("saleType").setText("");
    }

    //..新弹窗
    function inputNameClick() {
        var inputType = mini.get("manualType").getValue();
        if (!inputType) {
            mini.alert("请先选择手册类型！");
            return;
        }
        if (inputType == '操保手册') {
            selectMaintenanceManualMctWindow.show();
        }

        searchInputList();
    }

    //查询
    function searchInputList() {
        var inputType = mini.get("manualType").getValue();
        var queryParam = [];
        if (inputType == '操保手册') {
            var busunessNo = $.trim(mini.get("filterBusunessNo").getValue());
            if (busunessNo) {
                queryParam.push({name: "busunessNo", value: busunessNo});
            }
            var salesModel = $.trim(mini.get("filterSalesModel").getValue());
            if (salesModel) {
                queryParam.push({name: "salesModel", value: salesModel});
            }
            var designModel = $.trim(mini.get("filterDesignModel").getValue());
            if (designModel) {
                queryParam.push({name: "designModel", value: designModel});
            }

        }
        var inputList = '';
        if (inputType == '操保手册') {
            inputList = maintenanceManualMctListGrid;
        }
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = inputList.getPageIndex();
        data.pageSize = inputList.getPageSize();
        data.sortField = inputList.getSortField();
        data.sortOrder = inputList.getSortOrder();
        //查询
        inputList.load(data);
    }

    function selectInputOK() {
        var inputType = mini.get("manualType").getValue();
        var inputList = '';
        if (inputType == '操保手册') {
            inputList = maintenanceManualMctListGrid;
        }
        var selectRow = inputList.getSelected();
        if (selectRow) {
            var saleType = '';
            var designType = '';
            var materialCode = '';
            var mannulCE = '';
            var demandBusunessNo = '';
            var demandListNo = '';
            if (inputType == '操保手册') {
                saleType = selectRow.salesModel;
                designType = selectRow.designModel;
                materialCode = selectRow.materialCode;
                mannulCE = selectRow.isCE;
                demandBusunessNo = selectRow.demandBusunessNo;
                demandListNo = selectRow.demandListNo;

            }
            mini.get("saleType").setValue(saleType);
            mini.get("saleType").setText(saleType);
            mini.get("designType").setValue(designType);
            mini.get("designType").setText(designType);
            mini.get("materialCode").setValue(materialCode);
            mini.get("materialCode").setText(materialCode);
            mini.get("mannulCE").setValue(mannulCE);
            mini.get("mannulCE").setText(mannulCE);

            mini.get("transportApplyId").setValue(demandBusunessNo);
            mini.get("transportApplyId").setText(demandBusunessNo);
            mini.get("transportApplyNo").setValue(demandListNo);
            mini.get("transportApplyNo").setText(demandListNo);


        } else {
            mini.alert("请选择一条数据！");
            return;
        }
        selectInputHide();
    }

    function selectInputHide() {
        var inputType = mini.get("manualType").getValue();
        if (inputType == '操保手册') {
            selectMaintenanceManualMctWindow.hide();
            mini.get("filterBusunessNo").setValue('');
            mini.get("filterSalesModel").setValue('');
            mini.get("filterDesignModel").setValue('');
        }
    }

    function onRowDblClick() {
        selectInputOK();
    }

    //判断是否外发翻译
    function addTask() {
        id = mini.get("id").getValue();
        var url = jsUseCtxPath + "/bpm/core/bpmInst/WFFY/start.do?baseInfoId=" + id;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (formBusiness) {
                    formBusiness.reload()
                }
            }
        }, 1000);

    }

    // 启动外发翻译子流程窗体
    function addSubTaskWindow() {
        var id = mini.get("id").getValue();
        mini.open({
            title: "外发文件申请",
            url: jsUseCtxPath + "/bpm/core/bpmInst/WFFY/start.do?baseInfoId=" + id,
            width: 1080,
            height: 800,
            showModal: true,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                externalTranslationGrid.reload();
            }
        });

    }
    // 反馈单号重定向
    function jumpRenderer(e) {
        var record = e.record;
        var applyId = record.id;
        var instId = record.instId;
        var status = record.status;
        var title = record.applyNumber;
        var s = '';
        // s += '<span  title="明细" onclick="clickDetail(\'' + applyId + '\',\'' + status + '\',\'' + instId + '\')">明细</span>';
        s += '<span  title="明细" style="color:#44cef6;text-decoration:underline" onclick="clickDetail(\'' + applyId + '\',\'' + status + '\',\'' + instId + '\')">' + record.applyNumber + '</span>';
        return s;
    }

    function clickDetail(applyId, status, instId) {
        var url = jsUseCtxPath + "/serviceEngineering/core/externalTranslation/applyEditPage.do?action=detail&applyId=" + applyId + "&status=" + status + "&instId=" + instId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (externalTranslationList) {
                    externalTranslationList.reload()
                }
            }
        }, 1000);
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '审批中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '批准', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }
    //..状态渲染
    function onMaintenanceManualMctStatusRenderer(e) {
        var record = e.record;
        var businessStatus = record.businessStatus;

        var arr = [{'key': 'A-editing', 'value': '编制中'},
            {'key': 'B-proofreading', 'value': '服务工程校对中'},
            {'key': 'C-reviewing', 'value': '产品所长审核中'},
            {'key': 'D-reviewingService', 'value': '服务工程所长审核'},
            {'key': 'E-approving', 'value': '分管领导批准'},
            {'key': 'F-executing', 'value': '执行中'},
            {'key': 'G-close', 'value': '归档'}
        ];
        return $.formatItemValue(arr, businessStatus);
    }
    //..外发功能迁移
    function addOutFile() {
        if (!businessId) {
            mini.alert("请先保存草稿！");
            return;
        }
        mini.open({
            title: "附件上传",
            url: jsUseCtxPath + "/serviceEngineering/core/externalTranslation/openUploadWindow.do?applyId=" + businessId,
            width: 800,
            height: 350,
            showModal: false,
            allowResize: true,
            onload: function () {
                var iframe = this.getIFrameEl();
                var projectParams = {};
                projectParams.applyId = businessId;
                var data = {projectParams: projectParams};  //传递上传参数
                iframe.contentWindow.SetData(data);
            },
            ondestroy: function (action) {
                demandGrid.load();
            }
        });
    }
    function delDataRow() {
        var selected = demandGrid.getSelected();
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/serviceEngineering/core/externalTranslation/deleteOriFiles.do",
                    method: 'POST',
                    showMsg: false,
                    data: {demandId: selected.id},
                    success: function (data) {
                        demandGrid.load();
                    }
                });
            }
        })
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
            url: jsUseCtxPath + "/serviceEngineering/core/externalTranslation/openUploadWindow.do?applyId=" + businessId +
            "&detailId=" + selecteds[0].id + "&fType=" + "ret",
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
        if (record && (action == "task" && stageName == 'outgo')) {
            var deleteUrl = "/serviceEngineering/core/externalTranslation/deleteFiles.do";
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile2(\'' + record.returnFileName + '\',\'' + record.returnFileId + '\',\'' + record.applyId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }
    //..删除文档
    function deleteFile2(fileName, fileId, formId, urlValue) {
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
    function OnCellBeginEdit(e) {
        if (action != 'task' && (e.field == "outFileNum" || e.field == "outRate")) {
            e.cancel = false;
        } else if (action == 'detail' && (e.field == "outFileTotal" || e.field == "outFileNum" ||
            e.field == "outRate" || e.field == "outDesc")) {
            e.cancel = false;
        } else {
            e.cancel = true;
        }

    }
    //..保存文件信息
    function saveDemand() {
        var postData = demandGrid.getChanges();
        var postDataJson = mini.encode(postData);
        $.ajax({
            url: jsUseCtxPath + "/serviceEngineering/core/externalTranslation/saveDemand.do",
            type: 'POST',
            contentType: 'application/json',
            data: postDataJson,
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            demandGrid.reload();
                        }
                    });
                }
            }
        });
    }
</script>
<%--<redxun:gridScript gridId="externalTranslationGrid" entityName="" winHeight="" winWidth="" entityTitle=""--%>
<%--baseUrl=""/>--%>
</body>
</html>
