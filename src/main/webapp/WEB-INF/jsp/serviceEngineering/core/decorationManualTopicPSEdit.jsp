<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()"><spring:message code="page.decorationManualTopicPSEdit.name" /></a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()"><spring:message code="page.decorationManualTopicPSEdit.name1" /></a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 100%;">
        <form id="formBusiness" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="businessStatus" name="businessStatus" class="mini-hidden"/>
            <input id="applicantId" name="applicantId" class="mini-hidden"/>
            <input id="INST_ID_" name="INST_ID_" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold">
                    <spring:message code="page.decorationManualTopicPSEdit.name2" />
                </caption>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualTopicPSEdit.name3" />:</td>
                    <td style="min-width:170px">
                        <input id="businessNo" name="businessNo" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.decorationManualTopicPSEdit.name4" />：</td>
                    <td style="min-width:170px">
                        <input id="createTime" name="createTime" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualTopicPSEdit.name5" />：</td>
                    <td style="min-width:170px">
                        <input id="applicant" name="applicant" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualTopicPSEdit.name6" />：</td>
                    <td>
                        <input id="reviewer" name="reviewerId" textname="reviewer"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualTopicPSEdit.name7" />：</td>
                    <td>
                        <input id="assessors" name="assessorIds" textname="assessors"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="false"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualTopicPSEdit.name8" />：</td>
                    <td>
                        <input id="approver" name="approverId" textname="approver"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.decorationManualTopicPSEdit.name9" />：</td>
                    <td colspan="3">
						<textarea id="remark" name="remark" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:100px;line-height:25px;" datatype="varchar" allowinput="true"
                                  mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                </tr>
                <%--topicGrid-----------------------------------------------------------------------%>
                <tr>
                    <td style="text-align: center;height: 500px"><spring:message code="page.decorationManualTopicPSEdit.name10" />：</td>
                    <td colspan="3">
                        <div class="mini-toolbar" id="topicToolBar">
                            <a id="selectTopic" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="selectTopic"><spring:message code="page.decorationManualTopicPSEdit.name11" /></a><a
                                id="deleteTopic" class="mini-button btn-red" style="margin-bottom: 5px;margin-top: 5px"
                                onclick="deleteTopic"><spring:message code="page.decorationManualTopicPSEdit.name12" /></a>
                        </div>
                        <div id="topicGrid" class="mini-datagrid" style="height: 85%" allowResize="true" allowCellWrap="true"
                             url="${ctxPath}/serviceEngineering/core/decorationManualTopicPS/getTopicToPS.do?businessId=${businessId}"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowcelledit="true" allowcellselect="true"
                             idField="id" allowAlternating="true" autoload="true">
                            <div property="columns">
                                <div type="checkcolumn" width="40"></div>
                                <div name="action" cellCls="actionIcons" width="150" headerAlign="center" align="center" renderer="topicRenderer"
                                     cellStyle="padding:0;"><spring:message code="page.decorationManualTopicPSEdit.name13" />
                                </div>
                                <div field="businessNo" width="140" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSEdit.name3" /></div>
                                <div field="chapter" width="140" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSEdit.name14" /></div>
                                <div field="system" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSEdit.name15" /></div>
                                <div field="topicCode" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSEdit.name16" /></div>
                                <div field="topicName" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSEdit.name17" /></div>
                                <div field="topicType" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSEdit.name18" /></div>
                                <div field="materialCode" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSEdit.name19" /></div>
                                <div field="productSerie" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSEdit.name20" /></div>
                                <div field="salesArea" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSEdit.name21" /></div>
                                <div field="salesModel" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSEdit.name22" /></div>
                                <div field="designModel" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSEdit.name23" /></div>
                                <div field="remark" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSEdit.name9" /></div>
                                <div field="manualVersion" width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSEdit.name24" /></div>
                                <div field="manualStatus" width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSEdit.name25" /></div>
                                <div field="isPS" width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSEdit.name26" /></div>
                                <div field="creatorName" width="80" headerAlign="center" align="center" allowSort="false"><spring:message code="page.decorationManualTopicPSEdit.name5" /></div>
                                <div field="createTime" width="150" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center"
                                     allowSort="true"><spring:message code="page.decorationManualTopicPSEdit.name4" />
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                <%--itemListGrid--------------------------------------------------------------------%>
                <tr>
                    <td style="text-align: center;height: 400px"><spring:message code="page.decorationManualTopicPSEdit.name27" />：</td>
                    <td colspan="3">
                        <div id="itemListGrid" class="mini-datagrid" style="height: 85%" allowResize="true" allowCellWrap="false"
                             showCellTip="true" autoload="true" idField="id" multiSelect="false" showPager="false"
                             showColumnsMenu="false" allowcelledit="false" allowcellselect="true" allowAlternating="true"
                             oncellbeginedit="cellBeginEdit">
                            <div property="columns">
                                <div type="indexcolumn" width="35" headerAlign="center" align="center"><spring:message code="page.decorationManualTopicPSEdit.name28" /></div>
                                <div field="assessorId_item" width="100" headerAlign="center" align="center" visible="false"><spring:message code="page.decorationManualTopicPSEdit.name29" /></div>
                                <div field="assessor_item" width="100" headerAlign="center" align="center" renderer="render"><spring:message code="page.decorationManualTopicPSEdit.name30" /></div>
                                <div field="assessorDepId_item" width="100" headerAlign="center" align="center" visible="false"><spring:message code="page.decorationManualTopicPSEdit.name31" /></div>
                                <div field="assessorDep_item" width="100" headerAlign="center" align="center" renderer="render"><spring:message code="page.decorationManualTopicPSEdit.name32" /></div>
                                <div field="assessTime_item" width="100" headerAlign="center" align="center" renderer="render"><spring:message code="page.decorationManualTopicPSEdit.name33" /></div>
                                <div field="isPS_item" width="100" headerAlign="center" align="center" renderer="render"><spring:message code="page.decorationManualTopicPSEdit.name34" />
                                    <input id="isPS_item" property="editor" class="mini-combobox" style="width: 100%" valueField="key"
                                           textField="value" data="[{'key' : '是','value' : '是'},{'key' : '否','value' : '否'}]"/>
                                </div>
                                <div field="psComment_item" width="200" headerAlign="center" align="center" renderer="render"><spring:message code="page.decorationManualTopicPSEdit.name35" />
                                    <input id="psComment_item" property="editor" class="mini-textbox" style="width: 100%"/>
                                </div>
                                <div field="psFeedback_item" width="200" headerAlign="center" align="center" renderer="render"><spring:message code="page.decorationManualTopicPSEdit.name36" />
                                    <input id="psFeedback_item" property="editor" class="mini-textbox" style="width: 100%"/>
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<%--selectTopicWindow-----------------------------------------------%>
<div id="selectTopicWindow" title="<spring:message code="page.decorationManualTopicPSEdit.name11" />" class="mini-window" style="width:1080px;height:600px;" showModal="true" showFooter="true"
     allowResize="true" showCloseButton="false">
    <div class="mini-toolbar">
        <div class="searchBox">
            <form id="searchForm" class="search-form" style="margin-bottom: 25px">
                <ul>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.decorationManualTopicPSEdit.name3" />：</span>
                        <input class="mini-textbox" id="businessNo_topic" name="businessNo_topic"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.decorationManualTopicPSEdit.name14" />：</span>
                        <input class="mini-textbox" id="chapter_topic" name="chapter_topic"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.decorationManualTopicPSEdit.name15" />：</span>
                        <input class="mini-textbox" id="system_topic" name="system_topic"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.decorationManualTopicPSEdit.name18" />：</span>
                        <input class="mini-textbox" id="topicType_topic" name="topicType_topic"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.decorationManualTopicPSEdit.name20" />：</span>
                        <input class="mini-textbox" id="productSerie_topic" name="productSerie_topic"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.decorationManualTopicPSEdit.name21" />：</span>
                        <input class="mini-textbox" id="salesArea_topic" name="salesArea_topic"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.decorationManualTopicPSEdit.name24" />：</span>
                        <input class="mini-textbox" id="manualVersion_topic" name="manualVersion_topic"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.decorationManualTopicPSEdit.name25" />：</span>
                        <input class="mini-textbox" id="manualStatus_topic" name="manualStatus_topic"/>
                    </li>
                    <li style="margin-left: 10px">
                        <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchTopic"><spring:message code="page.decorationManualTopicPSEdit.name37" /></a>
                        <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearSearchTopic"><spring:message code="page.decorationManualTopicPSEdit.name38" /></a>
                    </li>
                </ul>
            </form>
        </div>
    </div>
    <div class="mini-fit" style="height: 100%;">
        <div id="topicListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id" multiSelect="true" sizeList="[20,50,100,200]" pageSize="20"
             allowAlternating="true" pagerButtons="#pagerButtons" showColumnsMenu="true"
             url="${ctxPath}/serviceEngineering/core/decorationManualTopic/dataListQuery.do">
            <div property="columns">
                <div type="checkcolumn" width="40"></div>
                <div field="businessNo" width="140" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSEdit.name3" /></div>
                <div field="chapter" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSEdit.name14" /></div>
                <div field="system" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSEdit.name15" /></div>
                <div field="topicCode" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSEdit.name16" /></div>
                <div field="topicName" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSEdit.name17" /></div>
                <div field="topicType" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSEdit.name18" /></div>
                <div field="materialCode" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSEdit.name19" /></div>
                <div field="productSerie" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSEdit.name20" /></div>
                <div field="salesArea" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSEdit.name21" /></div>
                <div field="salesModel" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSEdit.name22" /></div>
                <div field="designModel" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSEdit.name23" /></div>
                <div field="remark" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSEdit.name9" /></div>
                <div field="manualVersion" width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSEdit.name24" /></div>
                <div field="manualStatus" width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSEdit.name25" /></div>
                <div field="isPS" width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSEdit.name26" /></div>
                <div field="creatorName" width="80" headerAlign="center" align="center" allowSort="false"><spring:message code="page.decorationManualTopicPSEdit.name5" /></div>
                <div field="createTime" width="150" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center"
                     allowSort="true"><spring:message code="page.decorationManualTopicPSEdit.name4" />
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.decorationManualTopicPSEdit.name39" />" onclick="selectTopicOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.decorationManualTopicPSEdit.name40" />" onclick="selectTopicHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>
<%--javascript------------------------------------------------------%>
<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var status = "${status}";
    var formBusiness = new mini.Form("#formBusiness");
    var jsUseCtxPath = "${ctxPath}";
    var topicGrid = mini.get("topicGrid");
    var itemListGrid = mini.get("itemListGrid");
    var selectTopicWindow = mini.get("selectTopicWindow");
    var topicListGrid = mini.get("topicListGrid");
    var businessId = "${businessId}";
    var nodeVarsStr = '${nodeVars}';
    var currentTime = "${currentTime}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
    //..
    $(function () {
        document.getElementById("itemListGrid").style.width = (screen.width - 250) + 'px';
        document.getElementById("topicGrid").style.width = (screen.width - 250) + 'px';
        var url = jsUseCtxPath + "/serviceEngineering/core/decorationManualTopicPS/getDetail.do";
        $.post(
            url,
            {businessId: businessId},
            function (json) {
                formBusiness.setData(json);
                var topicPSItems = JSON.parse(json.topicPSItems);
                itemListGrid.setData(topicPSItems);
                if (action == 'detail') {
                    formBusiness.setEnabled(false);
                    $("#topicToolBar").hide();
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
        if (formData.SUB_topicGrid) {
            delete formData.SUB_topicGrid;
        }
        if (formData.SUB_itemListGrid) {
            delete formData.SUB_itemListGrid;
        }
        var data = topicGrid.getData();
        if (data.length > 0) {
            formData.topicChangeData = data;
        }
        data = itemListGrid.getData();
        if (data.length > 0) {
            formData.itemChangeData = data;
        }
        formData.bos = [];
        formData.vars = [];
        return formData;
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
        //业务状态写法可以替代环境变量
        var businessStatus = mini.get("businessStatus");
        if (businessStatus.value != "A") {//不是编辑中
            formBusiness.setEnabled(false);
            $("#topicToolBar").hide();
            if (businessStatus.value == 'C') {//评审中
                itemListGrid.setAllowCellEdit(true);
                itemListGrid.columns[8].readOnly = true;
            } else if (businessStatus.value == 'D') {//意见反馈中
                itemListGrid.setAllowCellEdit(true);
                itemListGrid.columns[6].readOnly = true;
                itemListGrid.columns[7].readOnly = true;
            }
        }
    }
    //..流程信息浏览
    function processInfo() {
        var instId = $("#instId").val();
        var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
        _OpenWindow({
            url: url,
            max: true,
            title: decorationManualTopicPSEdit_name,
            width: 800,
            height: 600
        });
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
        if (formData.SUB_topicGrid) {
            delete formData.SUB_topicGrid;
        }
        if (formData.SUB_itemListGrid) {
            delete formData.SUB_itemListGrid;
        }
        formData.bos = [];
        formData.vars = [];
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/decorationManualTopicPS/saveBusiness.do',
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message = "";
                    if (data.success) {
                        message = decorationManualTopicPSEdit_name1;
                    } else {
                        message = decorationManualTopicPSEdit_name2 + data.message;
                    }
                    mini.alert(message, decorationManualTopicPSEdit_name3, function () {
                        window.location.reload();
                    });
                }
            }
        });
    }
    //..流程中的审批或者下一步
    function businessApprove(e) {
        var businessStatus = mini.get("businessStatus");
        //编制阶段的下一步需要校验表单必填字段
        if (businessStatus.value == "A") {
            var formValid = validBusiness();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        //评审中的下一步需要校验表单必填字段
        if (businessStatus.value == "C") {
            var formValid = validBusinessC();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        //意见反馈中的下一步需要校验表单必填字段
        if (businessStatus.value == "D") {
            var formValid = validBusinessD();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        window.parent.approve();
    }
    //..
    function validBusiness() {
        var reviewer = $.trim(mini.get("reviewer").getValue());
        if (!reviewer) {
            return {"result": false, "message": decorationManualTopicPSEdit_name4};
        }
        var assessors = $.trim(mini.get("assessors").getValue());
        if (!assessors) {
            return {"result": false, "message": decorationManualTopicPSEdit_name5};
        }
        var approver = $.trim(mini.get("approver").getValue());
        if (!approver) {
            return {"result": false, "message": decorationManualTopicPSEdit_name6};
        }
        if (topicGrid.data.length == 0) {
            return {"result": false, "message": decorationManualTopicPSEdit_name7};
        }
        return {"result": true};
    }
    //..
    function validBusinessC() {
        var rows = itemListGrid.getData();
        for (i = 0; i < rows.length; i++) {
            if (rows[i].assessorId_item == currentUserId && (rows[i].isPS_item == "" || rows[i].isPS_item == null)) {
                return {"result": false, "message": decorationManualTopicPSEdit_name8};
            }
            if (rows[i].assessorId_item == currentUserId && rows[i].isPS_item == "是" &&
                (rows[i].psComment_item == "" || rows[i].psComment_item == null)) {
                return {"result": false, "message": decorationManualTopicPSEdit_name9};
            }
        }
        return {"result": true};
    }
    //..
    function validBusinessD() {
        var rows = itemListGrid.getData();
        for (i = 0; i < rows.length; i++) {
            if (rows[i].isPS_item == "是" && (rows[i].psFeedback_item == "" || rows[i].psFeedback_item == null)) {
                return {"result": false, "message": decorationManualTopicPSEdit_name10};
            }
        }
        return {"result": true};
    }
    //..
    function detailTopic(businessId) {
        var url = jsUseCtxPath + "/serviceEngineering/core/decorationManualTopic/editPage.do?id=" + businessId + '&action=detail';
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload();
                }
            }
        }, 1000);
    }
    //..
    function selectTopic() {
        selectTopicWindow.show();
        searchTopic();
    }
    //..
    function deleteTopic() {
        var selecteds = topicGrid.getSelecteds();
        topicGrid.removeRows(selecteds);
    }
    //..
    function searchTopic() {
        var queryParam = [];
        var businessNo_topic = $.trim(mini.get("businessNo_topic").getValue());
        if (businessNo_topic) {
            queryParam.push({name: "businessNo", value: businessNo_topic});
        }
        var chapter_topic = $.trim(mini.get("chapter_topic").getValue());
        if (chapter_topic) {
            queryParam.push({name: "chapter", value: chapter_topic});
        }
        var system_topic = $.trim(mini.get("system_topic").getValue());
        if (system_topic) {
            queryParam.push({name: "system", value: system_topic});
        }
        var topicType_topic = $.trim(mini.get("topicType_topic").getValue());
        if (topicType_topic) {
            queryParam.push({name: "topicType", value: topicType_topic});
        }
        var productSerie_topic = $.trim(mini.get("productSerie_topic").getValue());
        if (productSerie_topic) {
            queryParam.push({name: "productSerie", value: productSerie_topic});
        }
        var salesArea_topic = $.trim(mini.get("salesArea_topic").getValue());
        if (salesArea_topic) {
            queryParam.push({name: "salesArea", value: salesArea_topic});
        }
        var manualVersion_topic = $.trim(mini.get("manualVersion_topic").getValue());
        if (manualVersion_topic) {
            queryParam.push({name: "manualVersion", value: manualVersion_topic});
        }
        var manualStatus_topic = $.trim(mini.get("manualStatus_topic").getValue());
        if (manualStatus_topic) {
            queryParam.push({name: "manualStatus", value: manualStatus_topic});
        }
        queryParam.push({name: "isPS", value: "是"});
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = topicListGrid.getPageIndex();
        data.pageSize = topicListGrid.getPageSize();
        data.sortField = topicListGrid.getSortField();
        data.sortOrder = topicListGrid.getSortOrder();
        topicListGrid.load(data);
        topicListGrid.deselectAll();
    }
    //..
    function clearSearchTopic() {
        mini.get("businessNo_topic").setValue("");
        mini.get("chapter_topic").setValue("");
        mini.get("system_topic").setValue("");
        mini.get("topicType_topic").setValue("");
        mini.get("productSerie_topic").setValue("");
        mini.get("salesArea_topic").setValue("");
        mini.get("manualVersion_topic").setValue("");
        mini.get("manualStatus_topic").setValue("");
        searchTopic();
    }
    //..
    function selectTopicOK() {
        const set = new Set();
        for (var j = 0; j < topicGrid.data.length; j++) {
            set.add(topicGrid.data[j].businessNo + ":" + topicGrid.data[j].manualVersion);
        }
        var selecteds = topicListGrid.getSelecteds();
        for (var i = 0; i < selecteds.length; i++) {
            if (!set.has(selecteds[i].businessNo + ":" + selecteds[i].manualVersion)) {
                topicGrid.addRow(selecteds[i]);
            }
        }
        selectTopicHide();
    }
    //..
    function selectTopicHide() {
        selectTopicWindow.hide();
    }
    //..
    function topicRenderer(e) {
        var record = e.record;
        var businessId = record.id;
        var s = '<span  title=' + decorationManualTopicPSEdit_name11 + ' style="color:#409EFF;cursor: pointer;" onclick="detailTopic(\'' + businessId + '\')">' + decorationManualTopicPSEdit_name11 + '</span>';
        return s;
    }
    //..
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
    //..
    function cellBeginEdit(sender) {
        var businessStatus = mini.get("businessStatus");
        if ((businessStatus.value == 'C' && sender.record.assessorId_item == currentUserId) ||
            businessStatus.value == 'D') {//评审中且是自己的或者意见反馈中
            sender.editor.setEnabled(true);
        } else {
            sender.editor.setEnabled(false);
        }
    }
</script>
</body>
</html>
