<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>零件图册缺词补充申请单</title>
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
    <div class="form-container" style="margin: 0 auto; width: 100%;">
        <form id="formBusiness" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="applyUserId" name="applyUserId" class="mini-hidden"/>
            <input id="chReviewerId" name="chReviewerId" class="mini-hidden"/>
            <input id="enReviewerId" name="enReviewerId" class="mini-hidden"/>
            <input id="multilingualReviewerId" name="multilingualReviewerId" class="mini-hidden"/>
            <input id="businessStatus" name="businessStatus" class="mini-hidden"/>
            <input id="INST_ID_" name="INST_ID_" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold">
                    零件图册缺词补充申请单
                </caption>
                <tr>
                    <td style="text-align: center;width: 15%">整机编码：</td>
                    <td style="min-width:170px">
                        <input id="zhengJiCode" name="zhengJiCode" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">多语言标识：</td>
                    <td style="min-width:170px">
                        <input id="multilingualSign" name="multilingualSign" class="mini-combobox" style="width:98%;"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=multilingualKey"
                               valueField="value" textField="key" multiSelect="false"/>
                    </td>
                </tr>


                <tr>
                    <td style="text-align: center;width: 15%">发起人(自动生成)：</td>
                    <td style="min-width:170px">
                        <input id="applyUser" name="applyUser" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>

                    <td style="text-align: center;width: 15%">发起日期(自动生成)：</td>
                    <td style="min-width:170px">
                        <input id="applyDate" name="applyDate" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">中文审核人(自动生成)：</td>
                    <td style="min-width:170px">
                        <input id="chReviewer" name="chReviewer" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">英文审核人(自动生成)：</td>
                    <td style="min-width:170px">
                        <input id="enReviewer" name="enReviewer" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">多语言翻译人(自动生成)：</td>
                    <td style="min-width:170px">
                        <input id="multilingualReviewer" name="multilingualReviewer" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">是否外发：</td>
                    <td style="min-width:170px">
                        <input id="externalSelect" name="externalSelect" class="mini-combobox" style="width:98%;" enabled="false"
                               textField="key" valueField="value" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[ {'key' : '是','value' : 'true'},{'key' : '否','value' : 'false'}]"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">总字符数：</td>
                    <td style="min-width:170px">
                        <input id="totalCodeNum" name="totalCodeNum" class="mini-textbox" style="width:98%;"
                               enabled="false">
                    </td>
                    <td style="text-align: center;width: 20%">自翻译率：</td>
                    <td style="min-width:170px">
                        <input id="selfTranslatePercent" name="selfTranslatePercent" class="mini-textbox"
                               style="width:98%;" enabled="false">
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">外发翻译率：</td>
                    <td style="min-width:170px">
                        <input id="outTranslatePercent" name="outTranslatePercent" class="mini-textbox"
                               style="width:98%;" enabled="false">
                    </td>

                    <td style="text-align: center;width: 15%">手册类型(自动生成)：</td>
                    <td style="min-width:170px">
                        <input id="manualType" name="manualType" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                </tr>
                <tr>

                    <td style="text-align: center;width: 15%">是否跳过英语：</td>
                    <td style="min-width:170px">
                        <input id="jumpEnglish" name="jumpEnglish" class="mini-combobox" style="width:98%;" enabled="false"
                               textField="key" valueField="value" emptyText="否"
                               required="false" allowInput="false" showNullItem="false" nullItemText="否"
                               data="[ {'key' : '是','value' : 'true'},{'key' : '否','value' : 'false'}]"/>
                    </td>
                </tr>

                <%--///////////////////////////////////////////////////--%>
                <tr>
                    <td style="text-align: center;height: 500px">术语明细：</td>
                    <td colspan="3">
                        <div class="mini-toolbar" id="itemButtons">
                            <a id="addItem" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="addItem" enabled="false">添加</a>
                            <a id="deleteItem" class="mini-button btn-red" style="margin-bottom: 5px;margin-top: 5px" onclick="deleteItem"
                               enabled="false">删除</a>
                            <a id="importItem" class="mini-button " style="margin-bottom: 5px;margin-top: 5px" onclick="openImportWindow"
                               enabled="false">导入</a>
                            <a id="exportItem" class="mini-button " style="margin-bottom: 5px;margin-top: 5px" onclick="exportItem"
                               enabled="true">普通导出</a>
                            <a id="exportItemGss" class="mini-button " style="margin-bottom: 5px;margin-top: 5px" onclick="exportItemGss"
                               enabled="true">GSS导出</a>
                            <a id="importItemTrans" class="mini-button " style="margin-bottom: 5px;margin-top: 5px" onclick="openImportTransWindow"
                               enabled="false">译文导入</a>
                        </div>
                        <div id="itemListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="true" allowCellWrap="false"
                             showCellTip="true"
                             idField="id" url="${ctxPath}/serviceEngineering/core/multiLanguageBuild/getItemList.do?businessId=${businessId}"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowcelledit="true" allowcellselect="true"
                             allowAlternating="true"
                             oncellendedit="OnCellEndEdit"
                             oncellvalidation="onCellValidation">
                            <div property="columns">
                                <div type="checkcolumn" width="25"></div>
                                <div type="indexcolumn" width="35" headerAlign="center" align="center">序号</div>
                                <div field="materialCode" name="materialCode" width="100" headerAlign="center" align="center" renderer="render">物料编码
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="originChinese" name="originChinese" width="180" headerAlign="center" align="center" renderer="render">原始中文
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="chinese" name="chinese" width="180" headerAlign="center" align="center" renderer="renderChinese">建议中文
                                <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="en" name="en" width="180" headerAlign="center" align="center" renderer="render">英文
                                    <input property="editor" class="mini-textbox"/>
                                </div>
<%--                                <div field="enHit" width="180" headerAlign="center" align="center" renderer="renderChinese">匹配到的英文</div>--%>
<%--                                <div field="multilingualKey" name="multilingualKey" width="80" headerAlign="center" align="center" renderer="render">--%>
<%--                                    多语言标识--%>
<%--                                    <input property="editor" class="mini-textbox"/>--%>
<%--                                </div>--%>
                                <div field="multilingualText" name="multilingualText" width="180" headerAlign="center" align="center"
                                     renderer="render">多语言文本
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                                <div field="remark" name="remark" width="180" headerAlign="center" align="center" renderer="render">备注
                                    <input property="editor" class="mini-textbox"/>
                                </div>
                            </div>
                        </div>
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
            </table>
        </form>
    </div>
</div>
<%----%>
<div id="importWindow" title="导入窗口" class="mini-window" style="width:750px;height:280px;"
     showModal="true" showFooter="false" allowResize="true">
    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
    </p>
    <div style="display: inline-block;float: right;">
        <a id="importBtn" class="mini-button" onclick="importItem()">导入</a>
        <a id="closeImportWindow" class="mini-button btn-red" onclick="closeImportWindow()">关闭</a>
    </div>
    <div class="mini-fit" style="font-size: 14px;margin-top: 30px">
        <form id="formImport" method="post">
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 30%">上传模板：</td>
                    <td style="width: 70%;">
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">零件图册缺词补充导入模板.xls</a>
                    </td>
                </tr>
                <tr>
                    <td style="width: 30%">操作：</td>
                    <td>
                        <input class="mini-textbox" style="width:60%;float: left" id="fileName" name="fileName"
                               readonly/>
                        <input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()"
                               accept="application/xls"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="uploadFile">选择文件</a>
                        <a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="clearUploadFile">清除</a>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<div id="importTransWindow" title="译文导入窗口" class="mini-window" style="width:750px;height:280px;"
     showModal="true" showFooter="false" allowResize="true">
    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
    </p>
    <div style="display: inline-block;float: right;">
        <a id="importTransBtn" class="mini-button" onclick="importTransItem()">导入</a>
        <a id="closeTransWindow" class="mini-button btn-red" onclick="closeImportTransWindow()">关闭</a>
    </div>
    <div class="mini-fit" style="font-size: 14px;margin-top: 30px">
        <form id="formTransImport" method="post">
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 30%">上传模板：</td>
                    <td style="width: 70%;">
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTransTemplate()">零件图册译文明细导入模板.xls</a>
                    </td>
                </tr>
                <tr>
                    <td style="width: 30%">操作：</td>
                    <td>
                        <input class="mini-textbox" style="width:60%;float: left" id="fileNameTrans" name="fileNameTrans"
                               readonly/>
                        <input id="inputFileTrans" style="display:none;" type="file" onchange="getSelectTransFile()"
                               accept="application/xls"/>
                        <a id="uploadFileBtnTrans" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="uploadFileTrans">选择文件</a>
                        <a id="clearFileBtnTrans" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="clearUploadFile">清除</a>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/serviceEngineering/core/multiLanguageBuild/exportItem.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<!--GSS导出Excel相关HTML-->
<form id="excelFormGss" action="${ctxPath}/serviceEngineering/core/multiLanguageBuild/exportItemGss.do" method="post" target="excelIFrameGss">
    <input type="hidden" name="filterGss" id="filterGss"/>
</form>
<iframe id="excelIFrameGss" name="excelIFrameGss" style="display: none;"></iframe>

<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var status = "${status}";
    var jsUseCtxPath = "${ctxPath}";
    var itemListGrid = mini.get("itemListGrid");
    var formBusiness = new mini.Form("#formBusiness");
    var businessId = "${businessId}";
    var nodeVarsStr = '${nodeVars}';
    var currentTime = new Date().format("yyyy-MM-dd hh:mm:ss");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";

    var businessStatus = "";
    var isAediting = "";
    var isAchineseReviewing ="";
    var isBchineseReviewing = "";
    var isCenglishTranslation = "";
    var isDmultilingualTranslation = "";
    var isGDYW = "";
    var importWindow = mini.get("importWindow");
    var importTransWindow = mini.get("importTransWindow");
    var demandGrid = mini.get("demandGrid");
    var multilingualSign =$.trim(mini.get("multilingualSign").getValue());
    var multilingualText =$.trim(mini.get("multilingualSign").getText());
    //..
    $(function () {
        mini.get("saveFileContract").hide();
        mini.get("manualType").setValue(manualType);
        var url = jsUseCtxPath + "/serviceEngineering/core/multiLanguageBuild/getDetail.do";
        $.post(
            url,
            {businessId: businessId},
            function (json) {
                formBusiness.setData(json);
                itemListGrid.load();
                businessStatus = mini.get("businessStatus").getValue();
                if (action == 'detail') {
                    formBusiness.setEnabled(false);
                    $("#detailToolBar").show();
                    //非草稿放开流程信息查看按钮
                    if (status != 'DRAFTED') {
                        $("#processInfo").show();
                    }
                    mini.get("itemListGrid").getColumn("materialCode").readOnly = true;
                    mini.get("itemListGrid").getColumn("originChinese").readOnly = true;
                    mini.get("itemListGrid").getColumn("chinese").readOnly = true;
                    mini.get("itemListGrid").getColumn("en").readOnly = true;
                    mini.get("itemListGrid").getColumn("multilingualText").readOnly = true;
                    mini.get("itemListGrid").getColumn("remark").readOnly = true;
                    mini.get("uploadReturnFile").hide();
                    mini.get("selfUploadFile").hide();
                    mini.get("delDemand").hide();
                } else if (action == 'edit') {
                    mini.get("addItem").setEnabled(true);
                    mini.get("deleteItem").setEnabled(true);
                    mini.get("importItem").setEnabled(true);
                    //外发
                    mini.get("uploadReturnFile").hide();
                    mini.get("selfUploadFile").hide();
                    mini.get("delDemand").hide();
                    demandGrid.hideColumn("returnFileName");
                    demandGrid.hideColumn("returnFileTime");
                    demandGrid.hideColumn("reop");
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
        var data = itemListGrid.getChanges();
        if (data.length > 0) {
            formData.itemChangeData = data;
        }
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
        var data = itemListGrid.getChanges();
        if (data.length > 0) {
            formData.itemChangeData = data;
        }

        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/multiLanguageBuild/saveBusiness.do',
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
    function businessApprove(e) {
        var formValid = validBusiness();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.approve();
    }
    //..流程信息浏览
    function processInfo() {
        var instId = $("#INST_ID_").val();
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
            if (nodeVars[i].KEY_ == 'isAediting') {
                isAediting = nodeVars[i].DEF_VAL_;
            } else if(nodeVars[i].KEY_ == 'AchineseReviewing') {
                isAchineseReviewing = nodeVars[i].DEF_VAL_;
            }else if (nodeVars[i].KEY_ == 'isBchineseReviewing') {
                isBchineseReviewing = nodeVars[i].DEF_VAL_;
            } else if (nodeVars[i].KEY_ == 'isCenglishTranslation') {
                isCenglishTranslation = nodeVars[i].DEF_VAL_;
            } else if (nodeVars[i].KEY_ == 'isDmultilingualTranslation') {
                isDmultilingualTranslation = nodeVars[i].DEF_VAL_;
            } else if (nodeVars[i].KEY_ == 'isGDYW') {
                isGDYW = nodeVars[i].DEF_VAL_;
            }
        }
        if (isAediting != 'yes') {
            formBusiness.setEnabled(false);
            if (isBchineseReviewing == 'yes') {
                mini.get("itemListGrid").getColumn("materialCode").readOnly = false;
                mini.get("itemListGrid").getColumn("originChinese").readOnly = true;
                mini.get("itemListGrid").getColumn("chinese").readOnly = false;
                mini.get("itemListGrid").getColumn("en").readOnly = true;
                mini.get("itemListGrid").getColumn("multilingualText").readOnly = true;
                mini.get("itemListGrid").getColumn("remark").readOnly = false;
                //外发
                mini.get("uploadReturnFile").hide();
                mini.get("selfUploadFile").hide();
                mini.get("delDemand").hide();
                demandGrid.hideColumn("returnFileName");
                demandGrid.hideColumn("returnFileTime");
                demandGrid.hideColumn("reop");
                //@mh 增加一个跳过英文审核节点
                mini.get("jumpEnglish").setEnabled(true);
            } else if (isAchineseReviewing == 'yes') {
                mini.get("itemListGrid").getColumn("materialCode").readOnly = false;
                mini.get("itemListGrid").getColumn("originChinese").readOnly = true;
                mini.get("itemListGrid").getColumn("chinese").readOnly = false;
                mini.get("itemListGrid").getColumn("en").readOnly = true;
                mini.get("itemListGrid").getColumn("multilingualText").readOnly = true;
                mini.get("itemListGrid").getColumn("remark").readOnly = false;
                //外发
                mini.get("uploadReturnFile").hide();
                mini.get("selfUploadFile").hide();
                mini.get("delDemand").hide();
                demandGrid.hideColumn("returnFileName");
                demandGrid.hideColumn("returnFileTime");
                demandGrid.hideColumn("reop");
            } else if (isCenglishTranslation == "yes") {

                mini.get("importItemTrans").setEnabled(true);
                mini.get("itemListGrid").getColumn("materialCode").readOnly = true;
                mini.get("itemListGrid").getColumn("originChinese").readOnly = true;
                mini.get("itemListGrid").getColumn("chinese").readOnly = true;
                mini.get("itemListGrid").getColumn("en").readOnly = false;
                mini.get("itemListGrid").getColumn("multilingualText").readOnly = true;
                mini.get("itemListGrid").getColumn("remark").readOnly = false;
                //外发
                mini.get("totalCodeNum").setEnabled(true);
                mini.get("selfTranslatePercent").setEnabled(true);
                mini.get("outTranslatePercent").setEnabled(true);
                mini.get("uploadReturnFile").hide();
                mini.get("selfUploadFile").hide();
                mini.get("delDemand").hide();
                demandGrid.hideColumn("returnFileName");
                demandGrid.hideColumn("returnFileTime");
                demandGrid.hideColumn("reop");
            } else if (isDmultilingualTranslation == "yes") {
                mini.get("importItemTrans").setEnabled(true);
                mini.get("itemListGrid").getColumn("materialCode").readOnly = true;
                mini.get("itemListGrid").getColumn("originChinese").readOnly = true;
                mini.get("itemListGrid").getColumn("chinese").readOnly = true;
                mini.get("itemListGrid").getColumn("en").readOnly = true;
                mini.get("itemListGrid").getColumn("multilingualText").readOnly = false;
                mini.get("itemListGrid").getColumn("remark").readOnly = false;
                //外发
                mini.get("externalSelect").setEnabled(true);
                mini.get("totalCodeNum").setEnabled(true);
                mini.get("selfTranslatePercent").setEnabled(true);
                mini.get("outTranslatePercent").setEnabled(true);
                mini.get("selfUploadFile").show();
                mini.get("delDemand").show();
                mini.get("uploadReturnFile").hide();
                demandGrid.hideColumn("returnFileName");
                demandGrid.hideColumn("returnFileTime");
                demandGrid.hideColumn("reop");

            }else if(isGDYW =="yes"){
                //外发
                mini.get("uploadReturnFile").show();
                mini.get("selfUploadFile").hide();
                mini.get("delDemand").show();
            }else {
                //外发
                mini.get("uploadReturnFile").hide();
                mini.get("selfUploadFile").hide();
                mini.get("delDemand").hide();
            }

        } else {
            mini.get("addItem").setEnabled(true);
            mini.get("deleteItem").setEnabled(true);
            mini.get("importItem").setEnabled(true);
        }
    }
    //..检验表单是否必填
    function validBusiness() {

        var multilingualSign = $.trim(mini.get("multilingualSign").getValue());
        if (!multilingualSign) {
            return {"result": false, "message": "请填写多语言标识"};
        }

        var zhengJiCode = $.trim(mini.get("zhengJiCode").getValue());
        if (!zhengJiCode) {
            return {"result": false, "message": "请填写整机编码"};
        }

        if (itemListGrid.totalCount == 0) {
            return {"result": false, "message": '请填写术语库扩充明细或先保存一下草稿'};
        }
        if(isDmultilingualTranslation =="yes"){
            var externalSelect = $.trim(mini.get("externalSelect").getValue());
            if(!externalSelect){
                return {"result": false, "message": "请填写是否外发"};
            }

            if(externalSelect =="true"){
                var totalCodeNum = $.trim(mini.get("totalCodeNum").getValue());
                if(!totalCodeNum){
                    return {"result": false, "message": "请填写总字符数"};
                }
                var selfTranslatePercent = $.trim(mini.get("selfTranslatePercent").getValue());
                if(!selfTranslatePercent){
                    return {"result": false, "message": "请填写自翻译率"};
                }
                var outTranslatePercent = $.trim(mini.get("outTranslatePercent").getValue());
                if(!outTranslatePercent){
                    return {"result": false, "message": "请填写外发翻译率"};
                }
                if (demandGrid.totalCount == 0) {
                    return {"result": false, "message": '请上传附件'};
                }
            }

        }


        if(isGDYW =="yes")
        {

                var demandGridData = demandGrid.getData();
                for (i = 0; i < demandGridData.length; i++) {
                    if (demandGridData[i].returnFileId == undefined || demandGridData[i].returnFileId == "") {
                        mini.alert("请上传回传文件");
                        return;
                    }
                }
        }

        if(isBchineseReviewing =="yes"){
            var jumpEnglish = $.trim(mini.get("jumpEnglish").getValue());
            if(!jumpEnglish){
                return {"result": false, "message": "请选择是否跳过英文翻译！"};
            }
        }

        //var rows = itemListGrid.getData();
      //  var st = new Set();
        //改模板后multilingualKey取消
        // for (i = 0; i < rows.length; i++) {
        //     st.add(rows[i].multilingualKey);
        //
        // }
        // if (st.size > 1) {
        //     return {"result": false, "message": '多语言标识不唯一'};
        // }
        //明细表单验证
        itemListGrid.validate();
        if (!itemListGrid.isValid()) {
            var error = itemListGrid.getCellErrors()[0];
            itemListGrid.beginEditCell(error.record, error.column);
            mini.alert(error.column.header + error.errorText);
            return;
        }
        //语言匹配完成再提交，要不然会出现流程驱动的细节更改被最终流程再次驱动的更改给覆盖
        if ((itemListGrid.getChanges().length > 0) &&(isAchineseReviewing !='yes') ) {
            return {"result": false, "message": '明细有改动请先点击保存草稿或暂存信息再提交'};
        }
        return {"result": true};
    }
    //..
    function onCellValidation(e) {
        if (e.field == 'materialCode' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'originChinese' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }

        if (e.field == 'chinese' && (!e.value || e.value == '') && businessStatus == 'B-chineseReviewing') {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'en' && (!e.value || e.value == '') && businessStatus == 'C-englishTranslation') {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'multilingualText' && (!e.value || e.value == '') && businessStatus == 'D-multilingualTranslation') {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
    }
    //..删除明细
    function deleteItem() {
        var row = itemListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
            return;
        }
        delRowGrid("itemListGrid");
    }
    //..添加
    function addItem() {
        if (businessId == null || businessId == "") {
            mini.alert('请先保存草稿生成主信息再添加！');
            return;
        }
        var newRow = {};
        itemListGrid.addRow(newRow, 0);
    }
    //..普通导出
    function exportItem() {
        var rows = itemListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        var params = [];
        for (var i = 0; i < rows.length; i++) {
            var obj = {};
            obj.name = "id";
            obj.value = rows[i].id
            params.push(obj);
        }
        $("#filter").val(mini.encode(params));
        var excelForm = $("#excelForm");
        excelForm.submit();
    }
    //..GSS导出
    function exportItemGss() {
        var rows = itemListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        var params = [];
        for (var i = 0; i < rows.length; i++) {
            var obj = {};
            obj.name = "materialCode";
            obj.value = rows[i].materialCode
            params.push(obj);
        }
        $("#filterGss").val(mini.encode(params));
        var excelFormGss = $("#excelFormGss");
        excelFormGss.submit();
    }
    //..所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
    //..根据匹配场景渲染中文
    function renderChinese(e) {
        if (e.value != null && e.value != "") {
            if (e.record.chineseMaterialHit != null && e.record.chineseMaterialHit != "" && e.record.chineseMaterialHit == 'true') {
                var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;color: #0b7edc" >' + e.value + '</span>';
            } else if (e.record.chineseTextHit != null && e.record.chineseTextHit != "" && e.record.chineseTextHit == 'true') {
                var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;color: #ef1b01" >' + e.value + '</span>';
            } else {
                var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            }
            return html;
        }
    }

    function openImportWindow() {
        if (businessId == null || businessId == "") {
            mini.alert('请先保存草稿生成主信息再导入！');
            return;
        }
        importWindow.show();
    }
    function openImportTransWindow() {

        if (businessId == null || businessId == "") {
            mini.alert('请先保存草稿生成主信息再导入！');
            return;
        }
        mini.alert('请先普通导出全部数据后再译文导入,避免数据丢失！',importTransWindow.show());

    }
    //..
    function importItem() {
        var file = null;
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            file = fileList[0];
        }
        if (!file) {
            mini.alert('请选择文件！');
            return;
        }
        //XMLHttpRequest方式上传表单
        var xhr = false;
        try {
            //尝试创建 XMLHttpRequest 对象，除 IE 外的浏览器都支持这个方法。
            xhr = new XMLHttpRequest();
        } catch (e) {
            //使用较新版本的 IE 创建 IE 兼容的对象（Msxml2.XMLHTTP）。
            xhr = ActiveXobject("Msxml12.XMLHTTP");
        }

        if (xhr.upload) {
            xhr.onreadystatechange = function (e) {
                if (xhr.readyState == 4) {
                    if (xhr.status == 200) {
                        if (xhr.responseText) {
                            var returnObj = JSON.parse(xhr.responseText);
                            var message = '';
                            if (returnObj.message) {
                                message = returnObj.message;
                            }
                            mini.alert(message);
                        }
                    }
                }
            };
            //开始上传
            xhr.open('POST', jsUseCtxPath + '/serviceEngineering/core/multiLanguageBuild/importExcel.do?mainId=' + businessId, false);
            xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            var fd = new FormData();
            fd.append('importFile', file);
            xhr.send(fd);
        }
    }

    function importTransItem() {
        var file = null;
        var fileList = $("#inputFileTrans")[0].files;
        if (fileList && fileList.length > 0) {
            file = fileList[0];
        }
        if (!file) {
            mini.alert('请选择文件！');
            return;
        }
        //XMLHttpRequest方式上传表单
        var xhr = false;
        try {
            //尝试创建 XMLHttpRequest 对象，除 IE 外的浏览器都支持这个方法。
            xhr = new XMLHttpRequest();
        } catch (e) {
            //使用较新版本的 IE 创建 IE 兼容的对象（Msxml2.XMLHTTP）。
            xhr = ActiveXobject("Msxml12.XMLHTTP");
        }

        if (xhr.upload) {
            xhr.onreadystatechange = function (e) {
                if (xhr.readyState == 4) {
                    if (xhr.status == 200) {
                        if (xhr.responseText) {
                            var returnObj = JSON.parse(xhr.responseText);
                            var message = '';
                            if (returnObj.message) {
                                message = returnObj.message;
                            }
                            mini.alert(message);
                        }
                    }
                }
            };
            //开始上传
            xhr.open('POST', jsUseCtxPath + '/serviceEngineering/core/multiLanguageBuild/importTransExcel.do?mainId=' + businessId, false);
            xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            var fd = new FormData();
            fd.append('importFile', file);
            xhr.send(fd);
        }
    }
    //..
    function closeImportWindow() {
        importWindow.hide();
        clearUploadFile();
        itemListGrid.load();
    }
    function closeImportTransWindow() {
        importTransWindow.hide();
        clearUploadFile();
        itemListGrid.load();
    }
    //..
    function downImportTemplate() {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/multiLanguageBuild/importTemplateDownload.do");
        $("body").append(form);
        form.submit();
        form.remove();
    }
    //..
    function downImportTransTemplate() {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/multiLanguageBuild/importTransTemplateDownload.do");
        $("body").append(form);
        form.submit();
        form.remove();
    }
    //..
    function getSelectFile() {
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            var fileNameSuffix = fileList[0].name.split('.').pop();
            if (fileNameSuffix == 'xls' || fileNameSuffix == 'xlsx') {
                mini.get("fileName").setValue(fileList[0].name);
            }
            else {
                clearUploadFile();
                mini.alert('请上传excel文件！');
            }
        }
    }

    function getSelectTransFile() {
        var fileList = $("#inputFileTrans")[0].files;
        if (fileList && fileList.length > 0) {
            var fileNameSuffix = fileList[0].name.split('.').pop();
            if (fileNameSuffix == 'xls' || fileNameSuffix == 'xlsx') {
                mini.get("fileNameTrans").setValue(fileList[0].name);
            }
            else {
                clearUploadFile();
                mini.alert('请上传excel文件！');
            }
        }
    }
    //..
    function uploadFile() {
        $("#inputFile").click();
    }
    function uploadFileTrans() {
        $("#inputFileTrans").click();
    }
    //..
    function clearUploadFile() {
        $("#inputFile").val('');
        mini.get("fileName").setValue('');
        $("#inputFileTrans").val('');
        mini.get("fileNameTrans").setValue('');
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
        if (record && (action == "task" && isGDYW == "yes")) {
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

    //中文建议词汇填写完后,需要自动填充英文和所选语言
    function OnCellEndEdit(e) {
        var field = e.field;
        var record = e.record;

        if (field == "chinese") {
            // todo 获取多语言标识，并映射
            var multilingualSign = mini.get("multilingualSign").getValue();
            // 根据语言.建议中文去词条库中查 如果有，自动给英文和多语言文本赋值
            var chinese = record.chinese;
            // 只有编制阶段且有意见可以编辑意见反馈字段
            $.ajax({
                url: jsUseCtxPath + '/yfgj/core/mulitilingualTranslation/getRecommend.do?chineseName='+chinese+'&multilingualSign='+multilingualSign,
                type: 'post',
                async: false,
                data: {chineseName:chinese,multilingualSign:multilingualSign},
                contentType: 'application/json',
                success: function (data) {
                    if (data) {
                        if (data.success) {
                            itemListGrid.updateRow(record,
                                {
                                    en: data.englishName,
                                    multilingualText: data.multiName
                                });
                        } else {
                            itemListGrid.updateRow(record,
                                {
                                    en: "",
                                    multilingualText:""
                                });
                        }
                    }
                }
            });
        }


    }

</script>
</body>
</html>
