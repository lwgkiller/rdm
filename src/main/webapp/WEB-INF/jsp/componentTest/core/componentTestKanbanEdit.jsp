<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>零部件试验项目信息编辑</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/standardManager/standardUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/TreeSelectWindow.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveBusiness" class="mini-button" onclick="saveBusiness()">保存</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
        <a id="bindingStandard" class="mini-button" onclick="bindingStandard()" style="display: none">关联标准</a>
        <a id="bindingStandardMsg" class="mini-button" onclick="bindingStandardMsg()" style="display: none">发送关联标准消息</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;padding: 0">
        <form id="businessForm" method="post">
            <input class="mini-hidden" id="id" name="id"/>
            <input class="mini-hidden" id="businessStatus" name="businessStatus"/>
            <input class="mini-hidden" id="applyTime" name="applyTime"/>
            <input class="mini-hidden" id="testMajorLeaderId" name="testMajorLeaderId"/>
            <input class="mini-hidden" id="testMajorLeader" name="testMajorLeader"/>
            <input class="mini-hidden" id="testItemsJson" name="testItemsJson"/>
            <input class="mini-hidden" id="samplePlanTime" name="samplePlanTime"/>
            <input class="mini-hidden" id="projectId" name="projectId"/>
            <input class="mini-hidden" id="technicalRequirement" name="technicalRequirement"/>
            <input class="mini-hidden" id="sampleCount" name="sampleCount"/>
            <input class="mini-hidden" id="sampleParameters" name="sampleParameters"/>
            <input class="mini-hidden" id="testConclusion" name="testConclusion"/>
            <input class="mini-hidden" id="componentTestAbnormalTag" name="componentTestAbnormalTag"/>
            <input class="mini-hidden" id="INST_ID_" name="INST_ID_"/>
            <input class="mini-hidden" id="improvementSuggestions" name="improvementSuggestions"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                <caption style="font-weight: bold;font-size: 20px !important;">零部件试验项目信息编辑</caption>
                <tr>
                    <td style="text-align: center;width: 15%">试验编号(保存自动生成)：</td>
                    <td style="min-width:170px">
                        <input id="testNo" name="testNo" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">零部件类别：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="componentCategory" name="componentCategory"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=componentCategory"
                               valueField="key" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">零部件名称：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="componentName" name="componentName" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">零部件型号：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="componentModel" name="componentModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">物料编码：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="materialCode" name="materialCode" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">样品类型：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="sampleType" name="sampleType"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=sampleType"
                               valueField="key" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">配套主机型号：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="machineModel" name="machineModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">配套主机名称：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="machineName" name="machineName" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">配套主机单位：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="machineCompany" name="machineCompany" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">供应商名称：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="supplierName" name="supplierName" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">承担单位：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="laboratory" name="laboratory" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">试验类型：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="testType" name="testType"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=testType"
                               valueField="key" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">申请人：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="applyUserId" name="applyUserId" textname="applyUser" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="申请人" length="50"
                               mainfield="no" single="true"/>
                    </td>
                    <td style="text-align: center;width: 15%">申请部门(保存自动生成)：</td>
                    <td style="min-width:170px">
                        <input id="applyDepId" name="applyDepId" class="mini-dep rxc" plugins="mini-dep"
                               data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                               style="width:98%;height:34px" allowinput="false" label="申请部门" textname="applyDep" length="500"
                               maxlength="500" minlen="0" single="true" required="false" initlogindep="false" showclose="false"
                               mwidth="160" wunit="px" mheight="34" hunit="px" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">试验负责人：</td>
                    <td style="min-width:170px">
                        <input id="testLeaderId" name="testLeaderId" textname="testLeader" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="试验负责人" length="50"
                               mainfield="no" single="true"/>
                    </td>
                    <td style="text-align: center;width: 15%">试验计划类型：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="testCategory" name="testCategory"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=testCategory"
                               valueField="key" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">试验运行状态：</td>
                    <td style="min-width:170px">
                        <input id="testStatus" name="testStatus"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=testStatus"
                               valueField="key" textField="value"/>
                    </td>
                    <td style="text-align: center;width: 15%">试验进度：</td>
                    <td style="min-width:170px">
                        <input id="testProgress" name="testProgress" class="mini-spinner"
                               style="width:98%;" minValue="0" maxValue="1" decimalPlaces="4" format="p" increment="0.05"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">计划试验时间：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="plannedTestMonth" name="plannedTestMonth" class="mini-monthpicker" style="width:98%" allowinput="false"
                               enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">实际试验时间：</td>
                    <td style="min-width:170px">
                        <input id="actualTestMonth" name="actualTestMonth" class="mini-monthpicker" style="width:98%;" allowinput="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">试验完成时间：</td>
                    <td style="min-width:170px">
                        <input id="completeTestMonth" name="completeTestMonth" class="mini-monthpicker" style="width:98%;" allowinput="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">试验结果：</td>
                    <td style="min-width:170px">
                        <input id="testResult" name="testResult"
                               class="mini-combobox" style="width:98%" showNullItem="true"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=testResult"
                               valueField="key" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">不合格项说明：</td>
                    <td style="min-width:170px">
                        <input id="nonconformingDescription" name="nonconformingDescription" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%">不合格零部件当前状态：</td>
                    <td style="min-width:170px">
                        <input id="unqualifiedStatus" name="unqualifiedStatus"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=unqualifiedStatus"
                               valueField="key" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">检测合同状态：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="testContractStatus" name="testContractStatus"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=testContractStatus"
                               valueField="key" textField="value"/>
                    </td>
                    <td style="text-align: center;width: 15%">检测费用：</td>
                    <td style="min-width:170px">
                        <input id="testCost" name="testCost" class="mini-spinner"
                               style="width:98%;" minValue="0" maxValue="999999" decimalPlaces="2"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">试验次数：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="testRounds" name="testRounds"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=testRounds"
                               valueField="key" textField="value"/>
                    </td>
                    <td style="text-align: center;width: 15%">样件来源：</td>
                    <td style="min-width:170px">
                        <input id="sampleSource" name="sampleSource"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=sampleSource"
                               valueField="key" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">样件处理方式：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="sampleProcessingMethod" name="sampleProcessingMethod"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=sampleProcessingMethod"
                               valueField="key" textField="value"/>
                    </td>
                    <td style="text-align: center;width: 15%">样件当前状态：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="sampleStatus" name="sampleStatus"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=sampleStatus"
                               valueField="key" textField="value"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">领料人：</td>
                    <td style="min-width:170px">
                        <input id="receiverId" name="receiverId" textname="receiver" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="领料人" length="50"
                               mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">备注：</td>
                    <td colspan="3">
						<textarea id="remark" name="remark" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;"
                                  datatype="varchar" allowinput="true"
                                  mwidth="80" wunit="%" mheight="200" hunit="px">
                        </textarea>
                    </td>
                </tr>
                <%---------------------------------%>
                <tr>
                    <td style="text-align: center;height: 300px">检测合同：</td>
                    <td colspan="3">
                        <div class="mini-toolbar" id="fileButtonsContract" style="display: none">
                            <a id="uploadFileContract" class="mini-button" style="margin-bottom: 5px;margin-top: 5px"
                               onclick="uploadFileContract">添加合同</a>
                            <a id="saveFileContract" class="mini-button" style="margin-bottom: 5px;margin-top: 5px"
                               onclick="saveFileContract">保存编号</a>
                        </div>
                        <div id="fileListGridContract" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false" idField="id"
                             url="${ctxPath}/componentTest/core/kanban/getTestContractFileList.do?mainKanbanId=${businessId}"
                             multiSelect="false" showPager="true" showColumnsMenu="false" sizeList="[10,20,50,100]" pageSize="10"
                             allowAlternating="true" pagerButtons="#pagerButtons" allowCellEdit="true" allowCellSelect="true">
                            <div property="columns">
                                <div type="indexcolumn" headerAlign="center" align="center" width="20">序号</div>
                                <div field="fileDesc" align="center" headerAlign="center" width="100">合同编号
                                    <input property="editor" class="mini-textbox" style="width:100%;"/>
                                </div>
                                <div field="fileName" align="center" headerAlign="center" width="150">文件名</div>
                                <div field="fileSize" align="center" headerAlign="center" width="60">文件大小</div>
                                <div field="action" width="80" headerAlign='center' align="center" renderer="operationRendererContract">操作</div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;height: 300px">检测报告：</td>
                    <td colspan="3">
                        <div class="mini-toolbar" id="fileButtonsReport" style="display: none">
                            <a id="uploadFileReport" class="mini-button" style="margin-bottom: 5px;margin-top: 5px"
                               onclick="uploadFileReport">添加报告</a>
                            <a id="saveFileReport" class="mini-button" style="margin-bottom: 5px;margin-top: 5px"
                               onclick="saveFileReport">保存编号</a>
                        </div>
                        <div id="fileListGridReport" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false" idField="id"
                             url="${ctxPath}/componentTest/core/kanban/getTestReportFileList.do?mainKanbanId=${businessId}"
                             multiSelect="false" showPager="true" showColumnsMenu="false" sizeList="[10,20,50,100]" pageSize="10"
                             allowAlternating="true" pagerButtons="#pagerButtons" allowCellEdit="true" allowCellSelect="true">
                            <div property="columns">
                                <div type="indexcolumn" headerAlign="center" align="center" width="20">序号</div>
                                <div field="fileDesc" align="center" headerAlign="center" width="100">报告编号
                                    <input property="editor" class="mini-textbox" style="width:100%;"/>
                                </div>
                                <div field="fileName" align="center" headerAlign="center" width="150">文件名</div>
                                <div field="fileSize" align="center" headerAlign="center" width="60">文件大小</div>
                                <div field="action" width="80" headerAlign='center' align="center" renderer="operationRendererReport">操作</div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;height: 300px">关联标准：</td>
                    <td colspan="3">
                        <div id="standardListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
                             onlyCheckSelection="true"
                             url="${ctxPath}/standardManager/core/standard/queryList.do?componentTestId=${businessId}" idField="id"
                             multiSelect="false" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50"
                             allowAlternating="true" pagerButtons="#pagerButtons" showPager="true">
                            <div property="columns">
                                <div type="checkcolumn" width="20"></div>
                                <div name="action" cellCls="actionIcons" width="30" headerAlign="center" align="center"
                                     renderer="onStandardActionRenderer"
                                     cellStyle="padding:0;">操作
                                </div>
                                <div field="categoryName" headerAlign='center' align='center' width="40">标准类别</div>
                                <div field="standardNumber" width="50" headerAlign="center" align="center" allowSort="true">标准编号</div>
                                <div field="standardName" width="100" headerAlign="center" align="center" allowSort="true">标准名称</div>
                                <div field="standardStatus" sortField="standardStatus" width="30" headerAlign="center"
                                     align="center" hideable="true"
                                     allowSort="true" renderer="statusRenderer">状态
                                </div>
                                <div cellCls="actionIcons" width="50" headerAlign="center" align="center" renderer="fileRenderer"
                                     cellStyle="padding:0;" hideable="true">标准全文
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
    var businessId = "${businessId}";
    var action = "${action}";
    var businessForm = new mini.Form("#businessForm");
    var fileListGridContract = mini.get("fileListGridContract");
    var fileListGridReport = mini.get("fileListGridReport");
    var standardListGrid = mini.get("standardListGrid");
    var currentUserId = "${currentUserId}"
    var currentUserName = "${currentUserName}";
    var isGlNetwork = "${isGlNetwork}";
    var isComponentTestAdmin = "${isComponentTestAdmin}";
    //var isComponentTestAdmin = "true"//暂时放开
    var currentTime = new Date().format("yyyy-MM-dd hh:mm:ss");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    //..
    $(function () {
        if (businessId) {
            var url = jsUseCtxPath + "/componentTest/core/kanban/queryDataById.do?businessId=" + businessId;
            $.ajax({
                url: url,
                method: 'get',
                success: function (json) {
                    businessForm.setData(json);
                    //不同场景的处理，因为依赖值，所以提到异步这里
                    if (action == 'detail') {
                        businessForm.setEnabled(false);
                        mini.get("saveBusiness").setEnabled(false);
                        //todo:由于新的权限控制更严谨，明细下的编辑权限取消
//                        if (mini.get("testStatus").getValue() != '已完成' &&
//                            (isComponentTestAdmin == "true" || currentUserId == mini.get("testLeaderId").getValue())) {
//                            mini.get("saveBusiness").setEnabled(true);
//                            mini.get("testResult").setEnabled(true);
//                        }
                    } else if (action == 'edit') {
                        mini.get("fileButtonsContract").show();
                        mini.get("fileButtonsReport").show();
                        mini.get("bindingStandard").show();
                        mini.get("bindingStandardMsg").show();
                        if (isComponentTestAdmin == "true") {
                            mini.get("plannedTestMonth").setEnabled(true);
                        }
                    }
                }
            });
            fileListGridContract.load();
            fileListGridReport.load();
            standardListGrid.load();
        } else if (action == 'add') {
            mini.get("fileButtonsContract").show();
            mini.get("fileButtonsReport").show();
            mini.get("bindingStandard").show();
            mini.get("bindingStandardMsg").show();
            if (isComponentTestAdmin == "true") {
                mini.get("plannedTestMonth").setEnabled(true);
            }
        }
    });
    //..
    function saveBusiness() {
        var postData = {};
        postData.id = mini.get("id").getValue();
        postData.testNo = mini.get("testNo").getValue();
        postData.componentCategory = mini.get("componentCategory").getValue();
        postData.componentName = mini.get("componentName").getValue();
        postData.componentModel = mini.get("componentModel").getValue();
        postData.materialCode = mini.get("materialCode").getValue();
        postData.sampleType = mini.get("sampleType").getValue();
        postData.machineModel = mini.get("machineModel").getValue();
        postData.machineName = mini.get("machineName").getValue();
        postData.machineCompany = mini.get("machineCompany").getValue();
        postData.supplierName = mini.get("supplierName").getValue();
        postData.laboratory = mini.get("laboratory").getValue();
        postData.testType = mini.get("testType").getValue();
        postData.applyUserId = mini.get("applyUserId").getValue();
        postData.applyUser = mini.get("applyUserId").getText();
        postData.applyDepId = mini.get("applyDepId").getValue();
        postData.applyDep = mini.get("applyDepId").getText();
        postData.testCategory = mini.get("testCategory").getValue();
        postData.testStatus = mini.get("testStatus").getValue();
        postData.testProgress = mini.get("testProgress").getValue();
        postData.plannedTestMonth = mini.get("plannedTestMonth").getText();
        postData.actualTestMonth = mini.get("actualTestMonth").getText();
        postData.completeTestMonth = mini.get("completeTestMonth").getText();
        postData.testResult = mini.get("testResult").getValue();
        postData.nonconformingDescription = mini.get("nonconformingDescription").getValue();
        postData.testContractStatus = mini.get("testContractStatus").getValue();
        postData.testCost = mini.get("testCost").getValue();
        postData.testRounds = mini.get("testRounds").getValue();
        postData.sampleSource = mini.get("sampleSource").getValue();
        postData.sampleProcessingMethod = mini.get("sampleProcessingMethod").getValue();
        postData.sampleStatus = mini.get("sampleStatus").getValue();
        postData.receiverId = mini.get("receiverId").getValue();
        postData.receiver = mini.get("receiverId").getText();
        postData.remark = mini.get("remark").getValue();
        postData.testLeaderId = mini.get("testLeaderId").getValue();
        postData.testLeader = mini.get("testLeaderId").getText();
        postData.unqualifiedStatus = mini.get("unqualifiedStatus").getValue();
        postData.componentTestAbnormalTag = mini.get("componentTestAbnormalTag").getValue();
        //检查必填项
        var checkResult = saveValidCheck(postData);
        if (!checkResult.success) {
            mini.alert(checkResult.reason);
            return;
        }
        $.ajax({
            url: jsUseCtxPath + "/componentTest/core/kanban/saveBusiness.do",
            type: 'POST',
            contentType: 'application/json',
            data: mini.encode(postData),
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            var url = jsUseCtxPath + "/componentTest/core/kanban/editPage.do?businessId=" +
                                returnData.data + "&action=" + action;
                            window.location.href = url;
                        }
                    });
                }
            }
        });
    }
    //..
    function saveValidCheck(postData) {
        var checkResult = {};
        if (!postData.componentCategory) {
            checkResult.success = false;
            checkResult.reason = '请选择零部件类别！';
            return checkResult;
        }
        if (!postData.componentName) {
            checkResult.success = false;
            checkResult.reason = '请填写零部件名称！';
            return checkResult;
        }
        if (!postData.componentModel) {
            checkResult.success = false;
            checkResult.reason = '请填写零部件型号！';
            return checkResult;
        }
        if (!postData.materialCode) {
            checkResult.success = false;
            checkResult.reason = '请填写物料编码！';
            return checkResult;
        }
        if (!postData.sampleType) {
            checkResult.success = false;
            checkResult.reason = '请选择样品类型！';
            return checkResult;
        }
        if (!postData.machineName) {
            checkResult.success = false;
            checkResult.reason = '请填写配套主机名称！';
            return checkResult;
        }
        if (!postData.machineModel) {
            checkResult.success = false;
            checkResult.reason = '请填写配套主机型号！';
            return checkResult;
        }
        if (!postData.machineCompany) {
            checkResult.success = false;
            checkResult.reason = '请填写配套主机单位！';
            return checkResult;
        }
        if (!postData.supplierName) {
            checkResult.success = false;
            checkResult.reason = '请填写供应商名称！';
            return checkResult;
        }
        if (!postData.laboratory) {
            checkResult.success = false;
            checkResult.reason = '请填写承担单位！';
            return checkResult;
        }
        if (!postData.testType) {
            checkResult.success = false;
            checkResult.reason = '请选择试验类型！';
            return checkResult;
        }
        if (!postData.testCategory) {
            checkResult.success = false;
            checkResult.reason = '请选择试验计划类型！';
            return checkResult;
        }
        if (!postData.testContractStatus) {
            checkResult.success = false;
            checkResult.reason = '请选择检测合同状态！';
            return checkResult;
        }
        if (!postData.testRounds) {
            checkResult.success = false;
            checkResult.reason = '请选择试验轮次！';
            return checkResult;
        }
        if (!postData.applyUserId) {
            checkResult.success = false;
            checkResult.reason = '请选择申请人！';
            return checkResult;
        }
        if (!postData.plannedTestMonth) {
            checkResult.success = false;
            checkResult.reason = '请选择计划试验时间！';
            return checkResult;
        }
        if (postData.testResult == "不合格") {
            if (!postData.unqualifiedStatus) {
                checkResult.success = false;
                checkResult.reason = '试验结果为不合格时必须有不合格零部件当前状态！';
                return checkResult;
            }
        }
        if (postData.sampleSource == "厂内领料") {
            if (!postData.receiverId) {
                checkResult.success = false;
                checkResult.reason = '领料类型厂内领料时必须选择领料人！';
                return checkResult;
            }
        }
        if (!postData.sampleProcessingMethod) {
            checkResult.success = false;
            checkResult.reason = '请选样件处理方式！';
            return checkResult;
        }
        if (!postData.sampleStatus) {
            checkResult.success = false;
            checkResult.reason = '请选择样件当前状态！';
            return checkResult;
        }
        checkResult.success = true;
        return checkResult;
    }
    //..
    function uploadFileContract() {
        var mainKanbanId = mini.get("id").getValue();
        if (!mainKanbanId) {
            mini.alert("请先点击‘保存’进行记录创建！")
            return;
        }
        mini.open({
            title: "合同上传",
            url: jsUseCtxPath + "/componentTest/core/kanban/openContractUploadWindow.do?mainKanbanId=" + mainKanbanId,
            width: 800,
            height: 350,
            showModal: false,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                if (fileListGridContract) {
                    fileListGridContract.load();
                }
            }
        });
    }
    //..保存合同编号2022-06-30
    function saveFileContract() {
        var postData = fileListGridContract.getChanges();
        var postDataJson = mini.encode(postData);
        $.ajax({
            url: jsUseCtxPath + "/componentTest/core/kanban/contractFileNameSave.do",
            type: 'POST',
            contentType: 'application/json',
            data: postDataJson,
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            fileListGridContract.reload();
                        }
                    });
                }
            }
        });
    }
    //..
    function operationRendererContract(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnPreviewSpanContract(record.fileName, record.id, record.mainKanbanId, coverContent);
        //和pdf预览用的同一个url
        var downLoadUrl = '/componentTest/core/kanban/pdfPreviewAndAllDownloadContract.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainKanbanId + '\',\'' + downLoadUrl + '\')">下载</span>';
        //增加删除按钮
        if (action != 'detail') {
            var deleteUrl = "/componentTest/core/kanban/delContractFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFileContract(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainKanbanId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }
    //..
    function returnPreviewSpanContract(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            var url = '/componentTest/core/kanban/pdfPreviewAndAllDownloadContract.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/componentTest/core/kanban/officePreviewContract.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/componentTest/core/kanban/imagePreviewContract.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        return s;
    }
    //删除文档,因为有多个filegrid，因此要重构rdmzhgl中的此函数
    function deleteFileContract(fileName, fileId, formId, urlValue) {
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
                            if (fileListGridContract) {
                                fileListGridContract.load();
                            }
                        }
                    });
                }
            }
        );
    }
    //..
    function uploadFileReport() {
        var mainKanbanId = mini.get("id").getValue();
        if (!mainKanbanId) {
            mini.alert("请先点击‘保存’进行记录创建！")
            return;
        }
        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/componentTest/core/kanban/openReportUploadWindow.do?mainKanbanId=" + mainKanbanId,
            width: 800,
            height: 350,
            showModal: false,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                if (fileListGridReport) {
                    fileListGridReport.load();
                }
            }
        });
    }
    //..保存报告编号2022-06-30
    function saveFileReport() {
        var postData = fileListGridReport.getChanges();
        var postDataJson = mini.encode(postData);
        $.ajax({
            url: jsUseCtxPath + "/componentTest/core/kanban/reportFileNameSave.do",
            type: 'POST',
            contentType: 'application/json',
            data: postDataJson,
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            fileListGridReport.reload();
                        }
                    });
                }
            }
        });
    }
    //..
    function operationRendererReport(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnPreviewSpanReport(record.fileName, record.id, record.mainKanbanId, coverContent);
        //和pdf预览用的同一个url
        var downLoadUrl = '/componentTest/core/kanban/pdfPreviewAndAllDownloadReport.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainKanbanId + '\',\'' + downLoadUrl + '\')">下载</span>';
        //增加删除按钮
        if (action != 'detail') {
            var deleteUrl = "/componentTest/core/kanban/delReportFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFileReport(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainKanbanId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }
    //..
    function returnPreviewSpanReport(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            var url = '/componentTest/core/kanban/pdfPreviewAndAllDownloadReport.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/componentTest/core/kanban/officePreviewReport.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/componentTest/core/kanban/imagePreviewReport.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        return s;
    }
    //删除文档,因为有多个filegrid，因此要重构rdmzhgl中的此函数
    function deleteFileReport(fileName, fileId, formId, urlValue) {
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
                            if (fileListGridReport) {
                                fileListGridReport.load();
                            }
                        }
                    });
                }
            }
        );
    }
    ////////以下标准相关
    //..
    function bindingStandard() {
        var mainId = mini.get("id").getValue();
        if (!mainId) {
            mini.alert("请先点击‘保存’进行记录创建！")
            return;
        }
        var url = jsUseCtxPath + "/standardManager/core/standard/tabPage.do?tabName=JS&action=add&type=componentTest&businessId=" + mainId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                standardListGrid.load();
            }
        }, 1000);
    }
    //..
    function onStandardActionRenderer(e) {
        var record = e.record;
        var s = "";
        if (action == "add" || action == "edit") {
            s = '<span style="color:#ef1b01;" title="移除" onclick="removeStandard(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">移除</span>';
        } else {
            s = '<span  title="移除" style="color: silver">移除</span>';
        }
        return s;
    }
    //..
    function removeStandard(record) {
        var mainId = mini.get("id").getValue();
        var rows = [];
        rows.push(record);
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    rowIds.push(r.id);
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/componentTest/core/kanban/deleteStandard.do",
                    method: 'POST',
                    showMsg: false,
                    data: {ids: rowIds.join(','), mainId: mainId},
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            standardListGrid.load();
                        }
                    }
                });
            }
        });
    }
    //..
    function statusRenderer(e) {
        var record = e.record;
        var status = record.standardStatus;

        var arr = [{'key': 'draft', 'value': '草稿', 'css': 'orange'},
            {'key': 'enable', 'value': '有效', 'css': 'green'},
            {'key': 'disable', 'value': '已废止', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }
    //..
    function bindingStandardMsg() {
        var mainId = mini.get("id").getValue();
        if (!mainId) {
            mini.alert("请先点击‘保存’进行记录创建！")
            return;
        }
        mini.open({
            title: "新增",
            url: jsUseCtxPath + "/componentTest/core/kanban/bindingStandardMsgListPage.do?action=add&mainId=" + mainId,
            width: 1050,
            height: 800,
            allowResize: true,
            ondestroy: function () {
                standardListGrid.load();
            }
        });
    }
    //..以下标准预览相关
    function fileRenderer(e) {
        var record = e.record;
        var standardId = record.id;
        var existFile = record.existFile;
        var standardName = record.standardName;
        var standardNumber = record.standardNumber;
        var status = record.standardStatus;
        var categoryName = record.categoryName;
        var systemCategoryId = record.systemCategoryId;
        if (existFile) {
            var s = '<span title="预览"  onclick="previewStandard(\'' + standardId + '\',\'' + standardName + '\',\'' + standardNumber + '\',\'' + categoryName + '\',\'' + status + '\',\'' + coverContent + '\',\'' + systemCategoryId + '\')">预览</span>';
            if (isGlNetwork == "true") {
                s = '<span title="预览"  style="color: silver">预览</span>';
            }
            return s;
        } else {
            var s = '<span title="预览"  style="color: silver">预览</span>';
            return s;
        }
    }
    //..
    function previewStandard(standardId, standardName, standardNumber, categoryName, status, coverConent, systemCategoryId) {
        if (status == 'disable') {
            mini.confirm("该标准已废止，确定预览吗？", "提示", function (action) {
                if (action == "ok") {
                    previewStandardDo(standardId, standardName, standardNumber, categoryName, status, coverConent, systemCategoryId);
                }
            });
        } else {
            previewStandardDo(standardId, standardName, standardNumber, categoryName, status, coverConent, systemCategoryId);
        }

    }
    //..
    function previewStandardDo(standardId, standardName, standardNumber, categoryName, status, coverConent, systemCategoryId) {
        var resultCodeId = judgePreviewNeedApply(standardId, categoryName);
        if (resultCodeId.result == '0' || resultCodeId.result == '1') {
            if (resultCodeId.result == '1') {
                changeApplyUseStatus(resultCodeId.applyId, 'yes');
            }
            //记录预览情况
            recordStandardOperate('preview', standardId);
            var previewUrl = jsUseCtxPath + "/standardManager/core/standard/preview.do?standardId=" + standardId;
            if (systemCategoryId == 'JS') {
                window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverConent) + "&plate=bz&path=" + jsUseCtxPath + "&recordId=" + standardId + "&file=" + encodeURIComponent(previewUrl));
            } else {
                window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverConent) + "&path=" + jsUseCtxPath + "&recordId=" + standardId + "&file=" + encodeURIComponent(previewUrl));
            }

        } else if (resultCodeId.result == '2') {
            mini.alert('请在“标准预览申请”页面跟进申请单“' + resultCodeId.applyId + '”的审批');
        } else if (resultCodeId.result == '3') {
            //跳转到新增预览申请界面
            mini.confirm("当前操作需要提交预览申请，确定继续？", "权限不足", function (action) {
                if (action == "ok") {
                    addApply('preview', standardId, standardName);
                }
            });
        }
    }
    //..
    function judgePreviewNeedApply(standardId, categoryName) {
        if (categoryName != '企业标准') {
            return {result: '0'};
        }
        var applyCategoryId = 'preview';
        //标准管理领导不需要申请
        var isLeader = whetherIsLeader(currentUserRoles);
        if (isLeader) {
            return {result: '0'};
        }
        //技术标准管理人员不需要申请
        var JSSystemStandardManager = whetherIsPointStandardManager('JS', currentUserRoles);
        if (JSSystemStandardManager) {
            return {result: '0'};
        }
        //非管理职级的人员预览也不需要
        var isGLMan = whetherIsGLMan(currentUserZJ);
        if (!isGLMan) {
            return {result: '0'};
        }
        //其他场景都需要判断是否已经有申请单
        var resultCodeId = {result: '3'};
        $.ajax({
            url: jsUseCtxPath + "/standardManager/core/standardApply/checkOperateApply.do",
            type: 'POST',
            data: mini.encode({standardId: standardId, applyCategoryId: applyCategoryId, applyUserId: currentUserId}),
            contentType: 'application/json',
            async: false,
            success: function (data) {
                if (data) {
                    resultCodeId.result = data.result;
                    resultCodeId.applyId = data.applyId;
                }
            }
        });
        return resultCodeId;
    }
    //..
    function changeApplyUseStatus(applyId, useStatus) {
        $.ajax({
            url: jsUseCtxPath + "/standardManager/core/standardApply/changeUseStatus.do?applyId=" + applyId + "&useStatus=" + useStatus,
            method: 'GET',
            success: function () {

            }
        });
    }
    //..
    function recordStandardOperate(action, standardId) {
        $.ajax({
            url: jsUseCtxPath + "/standardManager/core/standard/record.do?standardId=" + standardId + "&action=" + action,
            method: 'GET',
            success: function (data) {

            }
        });
    }
    //..
    function addApply(applyCategoryId, standardId, standardName) {
        var title = "新增预览申请";
        if (applyCategoryId == 'download') {
            title = "新增下载申请";
        }
        var width = getWindowSize().width;
        var height = getWindowSize().height;
        _OpenWindow({
            url: jsUseCtxPath + "/bpm/core/bpmInst/BZGLSQ/start.do?standardApplyCategoryId=" + applyCategoryId + "&standardId=" + standardId,
            title: title,
            width: width,
            height: height,
            showMaxButton: true,
            showModal: true,
            allowResize: true,
            ondestroy: function () {
                if (standardListGrid) {
                    standardListGrid.reload();
                }
            }
        });
    }
</script>
</body>
</html>
