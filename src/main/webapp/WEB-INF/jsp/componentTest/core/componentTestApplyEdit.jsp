<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>零部件试验申请信息编辑</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/standardManager/standardUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/TreeSelectWindow.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <style type="text/css">
        fieldset {
            border: solid 1px #aaaaaab3;
            min-width: 920px;
        }

        .hideFieldset {
            border-left: 0;
            border-right: 0;
            border-bottom: 0;
        }

        .hideFieldset .fieldset-body {
            display: none;
        }
    </style>
</head>
<body>
<%--工具栏--%>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
        <a id="saveDetail" class="mini-button" style="display: none" onclick="saveBusinessInProcess()">保存</a>
    </div>
</div>
<%--表单视图--%>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;padding: 0">
        <form id="businessForm" method="post">
            <input class="mini-hidden" id="unqualifiedStatus" name="unqualifiedStatus"/>
            <input class="mini-hidden" id="testReport" name="testReport"/>
            <input class="mini-hidden" id="testContract" name="testContract"/>
            <input class="mini-hidden" id="id" name="id"/>
            <input class="mini-hidden" id="componentTestAbnormalTag" name="componentTestAbnormalTag"/>
            <input class="mini-hidden" id="INST_ID_" name="INST_ID_"/>
            <input class="mini-hidden" id="instId" name="instId"/>
            <input class="mini-hidden" id="businessStatus" name="businessStatus"/>
            <%--基本信息--%>
            <fieldset id="fieldset1">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" checked onclick="toggleFieldSet(this, 'fieldset1')" hideFocus/>
                        测试基本信息
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail" cellspacing="1" cellpadding="0">
                        <tr>
                            <td style="width: 10%">试验编号：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="testNo" name="testNo" class="mini-textbox" style="width:98%;" enabled="false"/>
                            </td>
                            <td style="width: 10%">申请人：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="applyUserId" name="applyUserId" textname="applyUser" class="mini-user rxc"
                                       plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="申请人" length="50"
                                       mainfield="no" single="true" enabled="false"/>
                            </td>
                            <td style="width: 10%">申请部门：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="applyDepId" name="applyDepId" class="mini-dep rxc" plugins="mini-dep"
                                       data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                                       style="width:98%;height:34px" allowinput="false" label="申请部门" textname="applyDep" length="500"
                                       maxlength="500" minlen="0" single="true" required="false" initlogindep="false" showclose="false"
                                       mwidth="160" wunit="px" mheight="34" hunit="px" enabled="false"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 10%">试验类型：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="testType" name="testType"
                                       class="mini-combobox" style="width:98%"
                                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=testType"
                                       valueField="key" textField="value"/>
                            </td>
                            <td style="width: 10%">试验计划类型：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="testCategory" name="testCategory"
                                       class="mini-combobox" style="width:98%"
                                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=testCategory"
                                       valueField="key" textField="value"/>
                            </td>
                            <td style="width: 10%">试验申请时间：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="applyTime" name="applyTime" class="mini-textbox" style="width:98%" enabled="false"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 10%">期望试验开始时间：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="hopeTestMonth" name="hopeTestMonth" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                                       valueType="string" showTime="false" showOkButton="false" showClearButton="true"
                                       style="width:98%;height:34px;" onvaluechanged="onHopeTestMonthChange"/>
                            </td>
                            <td style="width: 10%">计划送样时间：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="samplePlanTime" name="samplePlanTime" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                                       valueType="string" showTime="false" showOkButton="false" showClearButton="true"
                                       style="width:98%;height:34px;" ondrawdate="onDrawSamplePlanTime"/>
                            </td>
                            <td style="width: 10%">关联的科技项目：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <%--<input id="projectId" name="projectId" class="mini-combobox" style="width:98%;"--%>
                                <%--textField="projectName" valueField="projectId" emptyText="请选择..."--%>
                                <%--required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."--%>
                                <%--onvaluechanged="projectChange()"/>--%>
                                <input id="projectId" name="projectId" textName="projectName" style="width:98%;height:34px;"
                                       class="mini-buttonedit" showClose="true" allowInput="false"
                                       oncloseclick="selectProjectCloseClick()" onbuttonclick="selectProjectClick()"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 10%">科研项目编号：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="projectNo" name="projectNo" class="mini-textbox" style="width:98%;" enabled="false"/>
                            </td>
                            <td style="width: 10%">专业类别：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="componentCategory" name="componentCategory"
                                       class="mini-combobox" style="width:98%"
                                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=componentCategory"
                                       valueField="key" textField="value"/>
                            </td>
                            <td style="width: 10%">试验专业负责人：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="testMajorLeaderId" name="testMajorLeaderId" textname="testMajorLeader" class="mini-user rxc"
                                       plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="试验专业负责人" length="50"
                                       mainfield="no" single="true" enabled="false"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 10%">是否降本需求：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="isJiangBen" name="isJiangBen"
                                       class="mini-combobox" style="width:98%"
                                       data="[{'key':'是','value':'是'},{'key':'否','value':'否'}]"
                                       valueField="key" textField="value"/>
                            </td>
                            <td style="width: 10%">关联的产品所：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="relDepId" name="relDepId" class="mini-dep rxc" plugins="mini-dep"
                                       data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                                       style="width:98%;height:34px" allowinput="false" label="关联的产品所" textname="relDep" length="500"
                                       maxlength="500" minlen="0" single="true" required="false" initlogindep="false" showclose="false"
                                       mwidth="160" wunit="px" mheight="34" hunit="px"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 10%">测试目的：<span style="color:red">*</span></td>
                            <td colspan="5">
                                <textarea id="testPurpose" name="testPurpose" class="mini-textarea rxc"
                                          plugins="mini-textarea" style="width:99.1%;height:100px;line-height:25px;"
                                          datatype="varchar" allowinput="true"
                                          mwidth="80" wunit="%" mheight="100" hunit="px">
                                </textarea>
                            </td>
                        </tr>
                    </table>
                </div>
            </fieldset>
            <%--样品信息--%>
            <fieldset id="fieldset2">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" checked onclick="toggleFieldSet(this, 'fieldset2')" hideFocus/>
                        样品信息
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail" cellspacing="1" cellpadding="0">
                        <tr>
                            <td style="width: 10%">样品名称：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="componentName" name="componentName" class="mini-textbox" style="width:98%;"/>
                            </td>
                            <td style="width: 10%">样品类型：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="sampleType" name="sampleType"
                                       class="mini-combobox" style="width:98%"
                                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=sampleType"
                                       valueField="key" textField="value"/>
                            </td>
                            <td style="width: 10%">型号规格：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="componentModel" name="componentModel" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 10%">样品数量：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="sampleCount" name="sampleCount" class="mini-spinner"
                                       style="width:98%;" minValue="1" maxValue="100" decimalPlaces="4" increment="1"/>
                            </td>
                            <td style="width: 10%">物料编码：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="materialCode" name="materialCode" class="mini-textbox" style="width:98%;"/>
                            </td>
                            <td style="width: 10%">样品来源：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="sampleSource" name="sampleSource"
                                       class="mini-combobox" style="width:98%"
                                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=sampleSource"
                                       valueField="key" textField="value" onvaluechanged="onSampleSourceChange()"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 10%">领料人：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="receiverId" name="receiverId" textname="receiver" class="mini-user rxc"
                                       plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="领料人" length="50"
                                       mainfield="no" single="true"/>
                            </td>
                            <td style="width: 10%">供应商名称：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="supplierName" name="supplierName" class="mini-textbox" style="width:98%;"/>
                            </td>
                            <td style="width: 10%">样品处理方式：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="sampleProcessingMethod" name="sampleProcessingMethod"
                                       class="mini-combobox" style="width:98%"
                                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=sampleProcessingMethod"
                                       valueField="key" textField="value"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 10%">配套主机名称：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="machineName" name="machineName" class="mini-textbox" style="width:98%;"/>
                            </td>
                            <td style="width: 10%">配套主机型号：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="machineModel" name="machineModel" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 10%">样品参数：</td>
                            <td colspan="5">
                                <textarea id="sampleParameters" name="sampleParameters" class="mini-textarea rxc"
                                          plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;"
                                          datatype="varchar" allowinput="true"
                                          mwidth="80" wunit="%" mheight="200" hunit="px">
                                </textarea>
                            </td>
                        </tr>
                    </table>
                </div>
            </fieldset>
            <%--测试项目--%>
            <fieldset id="fieldset3">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" checked onclick="toggleFieldSet(this, 'fieldset3')" hideFocus/>
                        测试项目信息
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail" cellspacing="1" cellpadding="0">
                        <%--testItemsJson:测试项目字段序列化--%>
                        <tr>
                            <td style="width: 10%;height: 400px;text-align: center;">测试项目：</td>
                            <td colspan="5">
                                <div id="testItemsButtons" class="mini-toolbar">
                                    <a id="importTestItems" class="mini-button" style="margin-bottom: 5px;margin-top: 5px"
                                       onclick="importTestItem">测试能力导入</a>
                                    <a id="deleteTestItems" class="mini-button" style="margin-bottom: 5px;margin-top: 5px"
                                       onclick="deleteTestItem">删除</a>
                                    <span style="color: red;font-size: large">点击完“完成”标记后，如果不立即提交请进行暂存信息，否则将无法保存完成标记</span>
                                </div>
                                <div id="testItemsGrid" class="mini-datagrid" style="height: 85%" allowResize="true" allowCellWrap="false"
                                     showCellTip="true" autoload="true" idField="id" multiSelect="true" showPager="false"
                                     showColumnsMenu="false" allowcelledit="true" allowcellselect="true" allowAlternating="true"
                                     oncellvalidation="testItemsGridCellValidation" oncellbeginedit="OnCellBeginEdit">
                                    <div property="columns">
                                        <div type="checkcolumn" width="40" trueValue="1" falseValue="0"></div>
                                        <div type="indexcolumn" width="40" headerAlign="center" align="center">序号</div>
                                        <div field="testItem_item" width="200" headerAlign="center" align="center" renderer="render">样品名称
                                        </div>
                                        <div field="itemOfTestItem_item" width="200" headerAlign="center" align="center" renderer="render">测试项次
                                        </div>
                                        <div field="resources_item" width="200" headerAlign="center" align="center" renderer="render">资源
                                        </div>
                                        <div field="status_item" width="200" headerAlign="center" align="center" renderer="render">状态
                                            <input property="editor" class="mini-textbox" style="width:98%;"/>
                                        </div>
                                        <div field="type_item" width="200" headerAlign="center" align="center" renderer="render">类型
                                        </div>
                                        <div field="operation_item" name="operation_item" width="200" headerAlign="center" align="center"
                                             renderer="bRender">操作
                                        </div>
                                    </div>
                                </div>
                            </td>
                        </tr>
                        <%--testItemsNonStandardJson:非标测试项目字段序列化--%>
                        <tr>
                            <td style="width: 10%;height: 300px;text-align: center;">非标测试项目：</td>
                            <td colspan="5">
                                <div id="testItemsNonStandardButtons" class="mini-toolbar">
                                    <a id="addTestItemsNonStandard" class="mini-button" style="margin-bottom: 5px;margin-top: 5px"
                                       onclick="addTestItemNonStandard">添加</a>
                                    <a id="deleteTestItemsNonStandard" class="mini-button" style="margin-bottom: 5px;margin-top: 5px"
                                       onclick="deleteTestItemNonStandard">删除</a>
                                    <span style="color: red;font-size: large">点击完“完成”标记后，如果不立即提交请进行暂存信息，否则将无法保存完成标记</span>
                                </div>
                                <div id="testItemsNonStandardGrid" class="mini-datagrid" style="height: 85%" allowResize="true" allowCellWrap="false"
                                     showCellTip="true" autoload="true" idField="id" multiSelect="true" showPager="false"
                                     showColumnsMenu="false" allowcelledit="true" allowcellselect="true" allowAlternating="true"
                                     oncellvalidation="testItemsGridCellValidation" oncellbeginedit="OnCellBeginEdit">
                                    <div property="columns">
                                        <div type="checkcolumn" width="40" trueValue="1" falseValue="0"></div>
                                        <div type="indexcolumn" width="40" headerAlign="center" align="center">序号</div>
                                        <div field="testItem_item" width="200" headerAlign="center" align="center" renderer="render">样品名称
                                        </div>
                                        <div field="itemOfTestItem_item" width="200" headerAlign="center" align="center" renderer="render">测试项次
                                            <input id="itemOfTestItem_item" property="editor" class="mini-textbox" style="width:98%;"/>
                                        </div>
                                        <div field="status_item" width="200" headerAlign="center" align="center" renderer="render">状态
                                            <input property="editor" class="mini-textbox" style="width:98%;"/>
                                        </div>
                                        <div field="type_item" width="200" headerAlign="center" align="center" renderer="render">类型
                                        </div>
                                        <div field="operation_item" name="operation_item" width="200" headerAlign="center" align="center"
                                             renderer="bNonStandardRender">操作
                                        </div>
                                    </div>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 10%;height: 300px;text-align: center;">测试依据标准：</td>
                            <td colspan="5">
                                <div id="standardButtons" class="mini-toolbar">
                                    <a id="bindingStandard" class="mini-button" style="margin-bottom: 5px;margin-top: 5px"
                                       onclick="bindingStandard()">关联标准</a>
                                    <a id="bindingStandardMsg" class="mini-button" style="margin-bottom: 5px;margin-top: 5px"
                                       onclick="bindingStandardMsg()">发送关联标准消息</a>
                                </div>
                                <div id="standardListGrid" class="mini-datagrid" style="height:85%;" allowResize="false"
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
                        <tr>
                            <td style="width: 10%;text-align: center;">技术要求：</td>
                            <td colspan="5">
                                <textarea id="technicalRequirement" name="technicalRequirement" class="mini-textarea rxc"
                                          plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;"
                                          datatype="varchar" allowinput="true"
                                          mwidth="80" wunit="%" mheight="200" hunit="px">
                                </textarea>
                            </td>
                        </tr>
                    </table>
                </div>
            </fieldset>
            <%--过程结果--%>
            <fieldset id="fieldset4">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" checked onclick="toggleFieldSet(this, 'fieldset4')" hideFocus/>
                        测试进度及结果信息
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail" cellspacing="1" cellpadding="0">
                        <tr>
                            <td style="width: 10%">承担单位：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="laboratory" name="laboratory"
                                       class="mini-combobox" style="width:98%"
                                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=undertakeLaboratory"
                                       valueField="key" textField="value" enabled="false"/>
                            </td>
                            <td style="width: 10%">测试负责人：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="testLeaderId" name="testLeaderId" textname="testLeader" class="mini-user rxc"
                                       plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="测试负责人" length="50"
                                       mainfield="no" single="true"/>
                            </td>
                            <td style="width: 10%">测试运行状态：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="testStatus" name="testStatus"
                                       class="mini-combobox" style="width:98%"
                                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=testStatus"
                                       valueField="key" textField="value" enabled="false"/>
                            </td>
                        </tr>
                        <tr>

                            <td style="width: 10%">测试进度：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="testProgress" name="testProgress" class="mini-spinner" enabled="false"
                                       style="width:98%;" minValue="0" maxValue="1" decimalPlaces="4" format="p" increment="0.05"/>
                            </td>
                            <td style="width: 10%">测试费用：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="testCost" name="testCost" class="mini-spinner"
                                       style="width:98%;" minValue="0" maxValue="999999" decimalPlaces="2"/>
                            </td>
                            <td style="width: 10%">测试次数：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="testRounds" name="testRounds"
                                       class="mini-combobox" style="width:98%"
                                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=testRounds"
                                       valueField="key" textField="value"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 10%">计划测试开始时间：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="plannedTestMonth" name="plannedTestMonth" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                                       valueType="string" showTime="false" showOkButton="false" showClearButton="true"
                                       style="width:98%;height:34px;"/>
                            </td>
                            <td style="width: 10%">测试开始时间：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="actualTestMonth" name="actualTestMonth" class="mini-textbox" style="width:98%" enabled="false"/>
                            </td>
                            <td style="width: 10%">测试完成时间：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="completeTestMonth" name="completeTestMonth" class="mini-textbox" style="width:98%" enabled="false"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 10%">样品当前状态：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="sampleStatus" name="sampleStatus"
                                       class="mini-combobox" style="width:98%"
                                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=sampleStatus"
                                       valueField="key" textField="value"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 10%">测试方案状态：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="testContractStatus" name="testContractStatus"
                                       class="mini-combobox" style="width:98%"
                                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=testContractStatus"
                                       valueField="key" textField="value"/>
                            </td>
                            <td style="width: 10%">测试报告状态：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="testReportStatus" name="testReportStatus"
                                       class="mini-combobox" style="width:98%"
                                       data="[{key:'已归档',value:'已归档'},{key:'未归档',value:'未归档'},{key:'不用归档',value:'不用归档'}]"
                                       valueField="key" textField="value"/>
                            </td>
                            <td style="width: 10%">测试结果：<span style="color:red">*</span></td>
                            <td style="width: 23%">
                                <input id="testResult" name="testResult"
                                       class="mini-combobox" style="width:98%" showNullItem="true"
                                       url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=testResult"
                                       valueField="key" textField="value"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 10%">测试结论：</td>
                            <td colspan="5">
                                <textarea id="testConclusion" name="testConclusion" class="mini-textarea rxc"
                                          plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;"
                                          datatype="varchar" allowinput="true"
                                          mwidth="80" wunit="%" mheight="200" hunit="px">
                                </textarea>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 10%">不合格项说明：</td>
                            <td colspan="5">
                                <textarea id="nonconformingDescription" name="nonconformingDescription" class="mini-textarea rxc"
                                          plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;"
                                          datatype="varchar" allowinput="true"
                                          mwidth="80" wunit="%" mheight="200" hunit="px">
                                </textarea>
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 10%">改进建议：</td>
                            <td colspan="5">
                                <textarea id="improvementSuggestions" name="improvementSuggestions" class="mini-textarea rxc"
                                          plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;"
                                          datatype="varchar" allowinput="true"
                                          mwidth="80" wunit="%" mheight="200" hunit="px">
                                </textarea>
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;height: 300px">合同/方案：</td>
                            <td colspan="5">
                                <div class="mini-toolbar" id="fileButtonsContract">
                                    <a id="uploadFileContract" class="mini-button" style="margin-bottom: 5px;margin-top: 5px"
                                       onclick="uploadFileContract">添加合同</a>
                                    <a id="saveFileContract" class="mini-button" style="margin-bottom: 5px;margin-top: 5px"
                                       onclick="saveFileContract">保存编号</a>
                                </div>
                                <div id="fileListGridContract" class="mini-datagrid" style="height: 85%" allowResize="false" idField="id"
                                     url="${ctxPath}/componentTest/core/kanban/getTestContractFileList.do?mainKanbanId=${businessId}"
                                     multiSelect="false" showPager="true" showColumnsMenu="false" sizeList="[10,20,50,100]" pageSize="10"
                                     allowAlternating="true" pagerButtons="#pagerButtons" allowCellEdit="true" allowCellSelect="true" autoLoad="true">
                                    <div property="columns">
                                        <div type="indexcolumn" headerAlign="center" align="center" width="20">序号</div>
                                        <div field="fileDesc" align="center" headerAlign="center" width="100">合同/方案编号
                                            <input property="editor" class="mini-textbox" style="width:100%;"/>
                                        </div>
                                        <div field="fileName" align="center" headerAlign="center" width="150">文件名</div>
                                        <div field="fileSize" align="center" headerAlign="center" width="60">文件大小</div>
                                        <div field="action" width="80" headerAlign='center' align="center" renderer="operationRendererContract">操作
                                        </div>
                                    </div>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;height: 300px">测试报告：</td>
                            <td colspan="5">
                                <div class="mini-toolbar" id="fileButtonsReport">
                                    <a id="uploadFileReport" class="mini-button" style="margin-bottom: 5px;margin-top: 5px"
                                       onclick="uploadFileReport">添加报告</a>
                                    <a id="saveFileReport" class="mini-button" style="margin-bottom: 5px;margin-top: 5px"
                                       onclick="saveFileReport">保存编号</a>
                                </div>
                                <div id="fileListGridReport" class="mini-datagrid" style="height: 85%" allowResize="false" idField="id"
                                     url="${ctxPath}/componentTest/core/kanban/getTestReportFileList.do?mainKanbanId=${businessId}"
                                     multiSelect="false" showPager="true" showColumnsMenu="false" sizeList="[10,20,50,100]" pageSize="10"
                                     allowAlternating="true" pagerButtons="#pagerButtons" allowCellEdit="true" allowCellSelect="true" autoLoad="true">
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
                    </table>
                </div>
            </fieldset>
            <%--市场表现追踪--%>
            <fieldset id="fieldset5">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" checked onclick="toggleFieldSet(this, 'fieldset5')" hideFocus/>
                        市场表现追踪
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail" cellspacing="1" cellpadding="0">
                        <tr>
                            <td style="width: 10%">描述：</td>
                            <td colspan="5">
                                <textarea id="marketPerformanceTracking" name="marketPerformanceTracking" class="mini-textarea rxc"
                                          plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;"
                                          datatype="varchar" allowinput="true"
                                          mwidth="80" wunit="%" mheight="200" hunit="px">
                                </textarea>
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;height: 300px">相关附件：</td>
                            <td colspan="5">
                                <div class="mini-toolbar" id="fileButtonsMPT">
                                    <a id="uploadFileMPT" class="mini-button" style="margin-bottom: 5px;margin-top: 5px"
                                       onclick="uploadFileMPT">添加附件</a>
                                </div>
                                <div id="fileListGridMPT" class="mini-datagrid" style="height: 85%" allowResize="false" idField="id"
                                     url="${ctxPath}/componentTest/core/kanban/getTestMPTFileList.do?mainKanbanId=${businessId}"
                                     multiSelect="false" showPager="true" showColumnsMenu="false" sizeList="[10,20,50,100]" pageSize="10"
                                     allowAlternating="true" pagerButtons="#pagerButtons" allowCellEdit="true" allowCellSelect="true" autoLoad="true">
                                    <div property="columns">
                                        <div type="indexcolumn" headerAlign="center" align="center" width="20">序号</div>
                                        <div field="fileDesc" align="center" headerAlign="center" width="100">描述
                                            <input property="editor" class="mini-textbox" style="width:100%;"/>
                                        </div>
                                        <div field="fileName" align="center" headerAlign="center" width="150">文件名</div>
                                        <div field="fileSize" align="center" headerAlign="center" width="60">文件大小</div>
                                        <div field="action" width="80" headerAlign='center' align="center" renderer="operationRendererMPT">操作
                                        </div>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </table>
                </div>
            </fieldset>
        </form>
    </div>
</div>
<%--选择项目窗口--%>
<div id="selectProjectWindow" title="选择项目" class="mini-window" style="width:1300px;height:550px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px" borderStyle="border-left:0;border-top:0;border-right:0;">
        <span style="font-size: 14px;color: #777">项目名称: </span>
        <input class="mini-textbox" width="130" id="filterProjectName" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">项目编号: </span>
        <input class="mini-textbox" width="130" id="filterNumber" style="margin-right: 15px"/>
        <%--<span style="font-size: 14px;color: #777">项目负责人: </span>--%>
        <%--<input class="mini-textbox" width="130" id="filterRespMan" style="margin-right: 15px"/>--%>
        <a class="mini-button" plain="true" onclick="searchProject()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="projectListGrid" class="mini-datagrid" style="width: 100%; height: 100%;"
             allowResize="false" idField="id" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20"
             allowAlternating="true" pagerButtons="#pagerButtons"
             url="${ctxPath}/xcmgProjectManager/core/xcmgProject/allProjectListQuery.do">
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div field="projectName" sortField="projectName" width="140" headerAlign="center" align="center"
                     align="center" allowSort="true">项目名称
                </div>
                <div field="number" sortField="number" width="160" headerAlign="center" align="center"
                     allowSort="true">项目编号
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectProjectOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectProjectHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>
<%--JS--%>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessId = "${businessId}";
    var action = "${action}";
    var currentUserId = "${currentUserId}"
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var currentTime = new Date().format("yyyy-MM-dd hh:mm:ss");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var nodeName = "";
    var status = "${status}";
    var nodeVarsStr = '${nodeVars}';
    var isGlNetwork = "${isGlNetwork}";
    var isComponentTestAdmin = "${isComponentTestAdmin}";
    var projectIdToNo = new Map();
    var businessForm = new mini.Form("#businessForm");
    var fileListGridContract = mini.get("fileListGridContract");
    var fileListGridReport = mini.get("fileListGridReport");
    var fileListGridMPT = mini.get("fileListGridMPT");
    var standardListGrid = mini.get("standardListGrid");
    var testItemsGrid = mini.get("testItemsGrid");
    var testItemsNonStandardGrid = mini.get("testItemsNonStandardGrid");
    var fileButtonsContract = mini.get("fileButtonsContract");
    var fileButtonsReport = mini.get("fileButtonsReport");
    var fileButtonsMPT = mini.get("fileButtonsMPT");
    var standardButtons = mini.get("standardButtons");
    var testItemsButtons = mini.get("testItemsButtons");
    var testItemsNonStandardButtons = mini.get("testItemsNonStandardButtons");
    var selectProjectWindow = mini.get("selectProjectWindow");
    var projectListGrid = mini.get("projectListGrid");
    //..锁住一切
    function lockAll() {
        businessForm.setEnabled(false);
        fileButtonsContract.hide();
        fileButtonsReport.hide();
        fileButtonsMPT.hide();
        standardButtons.hide();
        testItemsButtons.hide();
        testItemsGrid.setAllowCellEdit(false);
        testItemsGrid.hideColumn("operation_item");
        testItemsNonStandardButtons.hide();
        testItemsNonStandardGrid.setAllowCellEdit(false);
        testItemsNonStandardGrid.hideColumn("operation_item");
    }
    //..解锁基本信息
    function unlockFieldset1() {
        mini.get("testType").setEnabled(true);
        mini.get("testCategory").setEnabled(true);
        mini.get("hopeTestMonth").setEnabled(true);
        mini.get("samplePlanTime").setEnabled(true);
        mini.get("projectId").setEnabled(true);
        mini.get("componentCategory").setEnabled(true);
        mini.get("isJiangBen").setEnabled(true);
        mini.get("testPurpose").setEnabled(true);
        mini.get("relDepId").setEnabled(true);
    }
    //..解锁样品信息
    function unlockFieldset2() {
        mini.get("componentName").setEnabled(true);
        mini.get("sampleType").setEnabled(true);
        mini.get("componentModel").setEnabled(true);
        mini.get("sampleCount").setEnabled(true);
        mini.get("materialCode").setEnabled(true);
        mini.get("sampleSource").setEnabled(true);
        if (mini.get("sampleSource").getValue() == "供应商送样") {
            mini.get("receiverId").setEnabled(false);
        } else {
            mini.get("receiverId").setEnabled(true);
        }
//        mini.get("machineCompany").setEnabled(true);
        mini.get("supplierName").setEnabled(true);
        mini.get("machineName").setEnabled(true);
        mini.get("machineModel").setEnabled(true);
        mini.get("sampleProcessingMethod").setEnabled(true);
//        mini.get("remark").setEnabled(true);
        mini.get("sampleParameters").setEnabled(true);


    }
    //..解锁测试项目
    function unlockFieldset3() {
        mini.get("technicalRequirement").setEnabled(true);
        standardButtons.show();
        testItemsButtons.show();
        testItemsGrid.setAllowCellEdit(true);
        testItemsNonStandardButtons.show();
        testItemsNonStandardGrid.setAllowCellEdit(true);
    }
    //..解锁过程结果
    function unlockFieldset4() {
        mini.get("laboratory").setEnabled(true);
        mini.get("testLeaderId").setEnabled(true);
        mini.get("testCost").setEnabled(true);
        mini.get("testRounds").setEnabled(true);
        mini.get("sampleStatus").setEnabled(true);
        mini.get("testContractStatus").setEnabled(true);
        mini.get("testReportStatus").setEnabled(true);
        mini.get("testResult").setEnabled(true);
        mini.get("testConclusion").setEnabled(true);
        mini.get("nonconformingDescription").setEnabled(true);
        mini.get("improvementSuggestions").setEnabled(true);
        fileButtonsContract.show();
        fileButtonsReport.show();
    }
    //..解锁市场表现追踪
    function unlockFieldset5() {
        mini.get("marketPerformanceTracking").setEnabled(true);
        fileButtonsMPT.show();
    }
    //..表单弹出事件控制
    function toggleFieldSet(ck, id) {
        var dom = document.getElementById(id);
        if (ck.checked) {
            dom.className = "";
        } else {
            dom.className = "hideFieldset";
        }
    }
    //..
    function OnCellBeginEdit(e) {
        var field = e.field;
        if (field == 'status_item' && action != 'detail') {
            e.cancel = true;
        }
    }
    //..
    $(function () {
        lockAll();
        var url = jsUseCtxPath + "/componentTest/core/apply/queryDataById.do";
        $.post(
            url,
            {businessId: businessId},
            function (json) {
                //getProjectToCombobox(json.applyUserId);//初始化申请人相关的项目列表到组件 不用这个了！
                businessForm.setData(json);
                var testItems = JSON.parse(json.testItemsJson);
                testItemsGrid.setData(testItems);
                var testItemsNonStandard = JSON.parse(json.testItemsNonStandardJson);
                testItemsNonStandardGrid.setData(testItemsNonStandard);
                if (action == 'detail') {
                    $("#detailToolBar").show();
                    var businessStatus = mini.get("businessStatus").getValue();
                    //非草稿放开流程信息查看按钮
                    if (status != 'DRAFTED') {
                        $("#processInfo").show();
                    }
                    //零部件试验管理员放开计划试验时间的编辑
                    if (isComponentTestAdmin == "true" || currentUserNo == "admin") {
                        $("#saveDetail").show();
                        mini.get("plannedTestMonth").setEnabled(true);
                        mini.get("testConclusion").setEnabled(true);
                        mini.get("nonconformingDescription").setEnabled(true);
                        unlockFieldset3()//解锁测试项目
                    }
                    //零部件试验管理员和试验负责人放开市场表现追踪的编辑
                    if (mini.get("testLeaderId").getValue() == currentUserId ||
                        isComponentTestAdmin == "true" || currentUserNo == "admin") {
                        $("#saveDetail").show();
                        unlockFieldset5()//解锁市场表现追踪
                    }

                } else if (action == 'edit') {
                    unlockFieldset1();//解锁基本信息
                    unlockFieldset2();//解锁样品信息
                    unlockFieldset3();//解锁测试项目
                } else if (action == 'task') {
                    taskActionProcess();
                }
            });
        if (businessId) {
            standardListGrid.load();
        }
    });
    //..流程引擎调用此方法进行表单数据的获取
    function getData() {
        var formData = _GetFormJsonMini("businessForm");
        formData.bos = [];
        formData.vars = [];
        var testItems = testItemsGrid.getData();
        if (testItems.length > 0) {
            formData.testItems = testItems;
        }
        var testItemsNonStandard = testItemsNonStandardGrid.getData();
        if (testItemsNonStandard.length > 0) {
            formData.testItemsNonStandard = testItemsNonStandard;
        }
        return formData;
    }
    //初始化申请人相关的项目列表到组件
    function getProjectToCombobox(applyUserId) {
        //申请人科技项目
        $.ajax({
            url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/queryParticipateProject.do?queryProjectUserId=' + applyUserId,
            type: 'get',
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    mini.get("projectId").load(data);
                    for (var i = 0, l = data.length; i < l; i++) {
                        projectIdToNo.set(data[i].projectId, data[i].number);
                    }
                    projectChange();
                }
            }
        });
    }
    //选择科技项目,显示项目编号
    function projectChange() {
        var projectId = mini.get("projectId").getValue();
        var projectNo = projectIdToNo.get(projectId);
        mini.get("projectNo").setValue(projectNo);
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
            if (nodeVars[i].KEY_ == 'nodeName') {
                nodeName = nodeVars[i].DEF_VAL_;
            }
        }
        if (nodeName == "A1") {
            //试验申请节点：填写基本信息、样品信息、测试项目模块信息，
            //除了31“领料人”、32“备注”外都为必填项，提交后数据不可修改。
            unlockFieldset1();//解锁基本信息
            unlockFieldset2();//解锁样品信息
            unlockFieldset3();//解锁测试项目
        } else if (nodeName == "C") {
            //试验分配节点：操作过程与结果模块中的11承担单位、15试验负责人，计划测试开始时间
            //可修改（完善）样品信息、测试项目模块信息。
            mini.get("laboratory").setEnabled(true);
            mini.get("testLeaderId").setEnabled(true);
            mini.get("plannedTestMonth").setEnabled(true);
            unlockFieldset2();//解锁样品信息
            unlockFieldset3();//解锁测试项目
        } else if (nodeName == "D1") {
            //合同方案上传节点：计划测试开始时间,操作合同方案
            mini.get("plannedTestMonth").setEnabled(true);
            fileButtonsContract.show();
        } else if (nodeName == "D2") {
            //合同方案校对节点：计划测试开始时间
            mini.get("plannedTestMonth").setEnabled(true);
        } else if (nodeName == "D5") {
            //合同方案批准节点：计划测试开始时间
            mini.get("plannedTestMonth").setEnabled(true);
        } else if (nodeName == "E") {
            //试验执行维护节点：测试项目只能点击、过程及结果模块可进行编辑。
            testItemsGrid.showColumn("operation_item");
            testItemsNonStandardGrid.showColumn("operation_item");
            unlockFieldset4();//解锁过程结果
        } else if (nodeName == "F1") {
            //报告上传节点：操作报告
            fileButtonsReport.show();
        }
    }
    //..保存草稿
    function saveBusiness(e) {
        window.parent.saveDraft(e);
    }
    //..启动流程
    function startBusinessProcess(e) {
        var formValid = validBusinessA();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.startProcess(e);
    }
    //..流程中暂存信息（如编制阶段）
    function saveBusinessInProcess() {
        var formData = getData();
        $.ajax({
            url: jsUseCtxPath + '/componentTest/core/apply/saveBusiness.do',
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (returnData) {
                if (returnData) {
                    var message = "";
                    if (returnData.success) {
                        message = "数据保存成功";
                    } else {
                        message = "数据保存失败" + returnData.message;
                    }
                    mini.alert(message, "提示信息", function () {
                        window.location.reload();
                    });
                }
            },
            error: function (returnData) {
                mini.alert(returnData.message);
            }
        });
    }
    //..流程中的审批或者下一步
    function businessApprove(e) {
        if (nodeName == 'A1') {
            var formValid = validBusinessA();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if (nodeName == 'C') {
            var formValid = validBusinessC();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if (nodeName == 'D1') {
            var formValid = validBusinessD();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if (nodeName == 'E') {
            var formValid = validBusinessE();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if (nodeName == 'F1') {
            var formValid = validBusinessF();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        window.parent.approve();
    }
    //..
    function validBusinessA() {
        var id = mini.get("id").getValue();
        if (!id) {
            return {"result": false, "message": "请先进行草稿的保存"};
        }
        var testType = $.trim(mini.get("testType").getValue());
        if (!testType) {
            return {"result": false, "message": "试验类型不能为空"};
        }
        var testCategory = $.trim(mini.get("testCategory").getValue());
        if (!testCategory) {
            return {"result": false, "message": "试验计划类型不能为空"};
        }
        var hopeTestMonth = $.trim(mini.get("hopeTestMonth").getValue());
        if (!hopeTestMonth) {
            return {"result": false, "message": "期望试验开始时间不能为空"};
        }
        var samplePlanTime = $.trim(mini.get("samplePlanTime").getValue());
        if (!samplePlanTime) {
            return {"result": false, "message": "计划送样时间不能为空"};
        }
        var projectId = $.trim(mini.get("projectId").getValue());
        if (!projectId && testType != "质量抽检测试") {
            return {"result": false, "message": "科技项目不能为空"};
        }
        var componentCategory = $.trim(mini.get("componentCategory").getValue());
        if (!componentCategory) {
            return {"result": false, "message": "专业类别不能为空"};
        }
        var testMajorLeaderId = $.trim(mini.get("testMajorLeaderId").getValue());
        if (!testMajorLeaderId) {
            return {"result": false, "message": "试验专业负责人不能为空，请选择专业类别后暂存信息生成"};
        }
        var isJiangBen = $.trim(mini.get("isJiangBen").getValue());
        if (!isJiangBen) {
            return {"result": false, "message": "是否降本需求不能为空"};
        }
        var relDepId = $.trim(mini.get("relDepId").getValue());
        if (!relDepId) {
            return {"result": false, "message": "关联的产品所不能为空"};
        }
        var testPurpose = $.trim(mini.get("testPurpose").getValue());
        if (!testPurpose) {
            return {"result": false, "message": "测试目的不能为空"};
        }
        //
        var componentName = $.trim(mini.get("componentName").getValue());
        if (!componentName) {
            return {"result": false, "message": "样品名称不能为空"};
        }
        var sampleType = $.trim(mini.get("sampleType").getValue());
        if (!sampleType) {
            return {"result": false, "message": "样品类型不能为空"};
        }
        var componentModel = $.trim(mini.get("componentModel").getValue());
        if (!componentModel) {
            return {"result": false, "message": "型号规格不能为空"};
        }
        var sampleCount = $.trim(mini.get("sampleCount").getValue());
        if (!sampleCount) {
            return {"result": false, "message": "样品数量不能为空"};
        }
        var materialCode = $.trim(mini.get("materialCode").getValue());
        if (!materialCode) {
            return {"result": false, "message": "物料编码不能为空"};
        }
        var sampleSource = $.trim(mini.get("sampleSource").getValue());
        if (!sampleSource) {
            return {"result": false, "message": "样品来源不能为空"};
        }
        var receiverId = $.trim(mini.get("receiverId").getValue());
        if (sampleSource == "厂内领料") {
            if (!receiverId) {
                return {"result": false, "message": "样品来源为 厂内领料 时，领料人不能为空"};
            }
        }
        var supplierName = $.trim(mini.get("supplierName").getValue());
        if (!supplierName) {
            return {"result": false, "message": "供应商名称不能为空"};
        }
        var machineName = $.trim(mini.get("machineName").getValue());
        if (!machineName) {
            return {"result": false, "message": "配套主机名称不能为空"};
        }
        var machineModel = $.trim(mini.get("machineModel").getValue());
        if (!machineModel) {
            return {"result": false, "message": "配套主机型号不能为空"};
        }
        var sampleProcessingMethod = $.trim(mini.get("sampleProcessingMethod").getValue());
        if (!sampleProcessingMethod) {
            return {"result": false, "message": "样品处理方式不能为空"};
        }
        var sampleParameters = $.trim(mini.get("sampleParameters").getValue());
        if (!sampleParameters) {
            return {"result": false, "message": "样品参数不能为空"};
        }
        //
        var technicalRequirement = $.trim(mini.get("technicalRequirement").getValue());
        if (!technicalRequirement) {
            return {"result": false, "message": "技术要求不能为空"};
        }
        if (testItemsGrid.getData().length == 0 && testItemsNonStandardGrid.getData().length == 0) {
            return {"result": false, "message": "测试项目和非标测试项目不能同时为空"};
        }
        if (standardListGrid.getData().length == 0) {
            return {"result": false, "message": "测试依据标准不能为空"};
        }
        testItemsGrid.validate();
        if (!testItemsGrid.isValid()) {
            var error = testItemsGrid.getCellErrors()[0];
            testItemsGrid.beginEditCell(error.record, error.column);
            return {"result": false, "message": error.column.header + error.errorText};
        }
        testItemsNonStandardGrid.validate();
        if (!testItemsNonStandardGrid.isValid()) {
            var error = testItemsNonStandardGrid.getCellErrors()[0];
            testItemsNonStandardGrid.beginEditCell(error.record, error.column);
            return {"result": false, "message": error.column.header + error.errorText};
        }
        return {"result": true};
    }
    //..
    function validBusinessC() {
        var componentName = $.trim(mini.get("componentName").getValue());
        if (!componentName) {
            return {"result": false, "message": "样品名称不能为空"};
        }
        var sampleType = $.trim(mini.get("sampleType").getValue());
        if (!sampleType) {
            return {"result": false, "message": "样品类型不能为空"};
        }
        var componentModel = $.trim(mini.get("componentModel").getValue());
        if (!componentModel) {
            return {"result": false, "message": "型号规格不能为空"};
        }
        var sampleCount = $.trim(mini.get("sampleCount").getValue());
        if (!sampleCount) {
            return {"result": false, "message": "样品数量不能为空"};
        }
        var materialCode = $.trim(mini.get("materialCode").getValue());
        if (!materialCode) {
            return {"result": false, "message": "物料编码不能为空"};
        }
        var sampleSource = $.trim(mini.get("sampleSource").getValue());
        if (!sampleSource) {
            return {"result": false, "message": "样品来源不能为空"};
        }
//        var machineCompany = $.trim(mini.get("machineCompany").getValue());
//        if (!machineCompany) {
//            return {"result": false, "message": "配套主机单位不能为空"};
//        }
        var supplierName = $.trim(mini.get("supplierName").getValue());
        if (!supplierName) {
            return {"result": false, "message": "供应商名称不能为空"};
        }
        var machineName = $.trim(mini.get("machineName").getValue());
        if (!machineName) {
            return {"result": false, "message": "配套主机名称不能为空"};
        }
        var machineModel = $.trim(mini.get("machineModel").getValue());
        if (!machineModel) {
            return {"result": false, "message": "配套主机型号不能为空"};
        }
        var sampleProcessingMethod = $.trim(mini.get("sampleProcessingMethod").getValue());
        if (!sampleProcessingMethod) {
            return {"result": false, "message": "样品处理方式不能为空"};
        }
        var sampleParameters = $.trim(mini.get("sampleParameters").getValue());
        if (!sampleParameters) {
            return {"result": false, "message": "样品参数不能为空"};
        }
        //
        var technicalRequirement = $.trim(mini.get("technicalRequirement").getValue());
        if (!technicalRequirement) {
            return {"result": false, "message": "技术要求不能为空"};
        }
        if (testItemsGrid.getData().length == 0 && testItemsNonStandardGrid.getData().length == 0) {
            return {"result": false, "message": "测试项目和非标测试项目不能同时为空"};
        }
        testItemsGrid.validate();
        if (!testItemsGrid.isValid()) {
            var error = testItemsGrid.getCellErrors()[0];
            testItemsGrid.beginEditCell(error.record, error.column);
            return {"result": false, "message": error.column.header + error.errorText};
        }
        testItemsNonStandardGrid.validate();
        if (!testItemsNonStandardGrid.isValid()) {
            var error = testItemsNonStandardGrid.getCellErrors()[0];
            testItemsNonStandardGrid.beginEditCell(error.record, error.column);
            return {"result": false, "message": error.column.header + error.errorText};
        }
        //
        var laboratory = $.trim(mini.get("laboratory").getValue());
        if (!laboratory) {
            return {"result": false, "message": "承担单位不能为空"};
        }
        var testLeaderId = $.trim(mini.get("testLeaderId").getValue());
        if (!testLeaderId) {
            return {"result": false, "message": "试验负责人不能为空"};
        }
        var plannedTestMonth = $.trim(mini.get("plannedTestMonth").getValue());
        if (!plannedTestMonth) {
            return {"result": false, "message": "计划测试开始时间不能为空"};
        }
        return {"result": true};
    }
    //..
    function validBusinessD() {
        var contracts = fileListGridContract.getData()
        if (contracts.length == 0) {
            return {"result": false, "message": "合同/方案不能为空"};
        }
        for (var i = 0, l = contracts.length; i < l; i++) {
            if (!contracts[i].fileDesc) {
                return {"result": false, "message": "合同/方案编号不能为空"};
            }
            if (contracts[i]._state == "modified") {
                return {"result": false, "message": "请保存合同编号"};
            }
        }
        return {"result": true};
    }
    //..
    function validBusinessE() {
        var laboratory = $.trim(mini.get("laboratory").getValue());
        if (!laboratory) {
            return {"result": false, "message": "承担单位不能为空"};
        }
        var testLeaderId = $.trim(mini.get("testLeaderId").getValue());
        if (!testLeaderId) {
            return {"result": false, "message": "试验负责人不能为空"};
        }
        var testCost = $.trim(mini.get("testCost").getValue());
        debugger;
        if (!testCost) {
            return {"result": false, "message": "试验费用不能为空"};
        }
        var testRounds = $.trim(mini.get("testRounds").getValue());
        if (!testRounds) {
            return {"result": false, "message": "试验次数不能为空"};
        }
        var sampleStatus = $.trim(mini.get("sampleStatus").getValue());
        if (!sampleStatus) {
            return {"result": false, "message": "样品当前状态不能为空"};
        }
        var testContractStatus = $.trim(mini.get("testContractStatus").getValue());
        if (!testContractStatus) {
            return {"result": false, "message": "测试方案状态不能为空"};
        }
        var testReportStatus = $.trim(mini.get("testReportStatus").getValue());
        if (!testReportStatus) {
            return {"result": false, "message": "测试报告状态不能为空"};
        }
        var testResult = $.trim(mini.get("testResult").getValue());
        if (!testResult) {
            return {"result": false, "message": "试验结果不能为空"};
        }
        var testConclusion = $.trim(mini.get("testConclusion").getValue());
        if (!testConclusion) {
            return {"result": false, "message": "测试结论不能为空"};
        }
        var nonconformingDescription = $.trim(mini.get("nonconformingDescription").getValue());
        if (!nonconformingDescription) {
            return {"result": false, "message": "不合格项说明不能为空"};
        }
        var improvementSuggestions = $.trim(mini.get("improvementSuggestions").getValue());
        if (!improvementSuggestions) {
            return {"result": false, "message": "改进建议不能为空"};
        }
        var rows = testItemsGrid.getData();
        for (var i = 0, l = rows.length; i < l; i++) {
            if (rows[i].status_item != "已完成") {
                return {"result": false, "message": "测试项目不允许出现未完成的项次"};
            }
        }
        rows = testItemsNonStandardGrid.getData();
        for (var i = 0, l = rows.length; i < l; i++) {
            if (rows[i].status_item != "已完成") {
                return {"result": false, "message": "非标测试项目不允许出现未完成的项次"};
            }
        }
        return {"result": true};
    }
    //..
    function validBusinessF() {
        var report = fileListGridReport.getData()
        if (report.length == 0) {
            return {"result": false, "message": "测试报告不能为空"};
        }
        for (var i = 0, l = report.length; i < l; i++) {
            if (!report[i].fileDesc) {
                return {"result": false, "message": "报告编号不能为空"};
            }
            if (report[i]._state == "modified") {
                return {"result": false, "message": "请保存报告编号"};
            }
        }
        return {"result": true};
    }
    //..
    function testItemsGridCellValidation(e) {
        if (e.field == 'testItem_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'itemOfTestItem_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
        if (e.field == 'resources_item' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
    }
    //////..合同..//////
    function uploadFileContract() {
        var businessId = mini.get("id").getValue();
        if (!businessId) {
            mini.alert("请先点击‘保存’进行记录创建！")
            return;
        }
        mini.open({
            title: "合同上传",
            url: jsUseCtxPath + "/componentTest/core/kanban/openContractUploadWindow.do?mainKanbanId=" + businessId,
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
    //..保存合同编号
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
        if (action == "edit" || (action == 'task' && (nodeName == "D1" || nodeName == "E"))) {
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
    //..删除文档,因为有多个filegrid，因此要重构rdmzhgl中的此函数
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
    //////..报告..//////
    function uploadFileReport() {
        var businessId = mini.get("id").getValue();
        if (!businessId) {
            mini.alert("请先点击‘保存’进行记录创建！")
            return;
        }
        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/componentTest/core/kanban/openReportUploadWindow.do?mainKanbanId=" + businessId,
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
    //..保存报告编号
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
        if (action == "edit" || (action == 'task' && (nodeName == "E" || nodeName == "F1"))) {
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
    //..删除文档,因为有多个filegrid，因此要重构rdmzhgl中的此函数
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
    //////..跟踪附件..//////
    function uploadFileMPT() {
        var businessId = mini.get("id").getValue();
        if (!businessId) {
            mini.alert("请先点击‘保存’进行记录创建！")
            return;
        }
        mini.open({
            title: "附件上传",
            url: jsUseCtxPath + "/componentTest/core/kanban/openMPTUploadWindow.do?mainKanbanId=" + businessId,
            width: 800,
            height: 350,
            showModal: false,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                if (fileListGridMPT) {
                    fileListGridMPT.load();
                }
            }
        });
    }
    //..
    function operationRendererMPT(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnPreviewSpanMPT(record.fileName, record.id, record.mainKanbanId, coverContent);
        //和pdf预览用的同一个url
        var downLoadUrl = '/componentTest/core/kanban/pdfPreviewAndAllDownloadMPT.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainKanbanId + '\',\'' + downLoadUrl + '\')">下载</span>';
        //增加删除按钮
        if (action == "detail" && (mini.get("testLeaderId").getValue() == currentUserId ||
            isComponentTestAdmin == "true" || currentUserNo == "admin")) {
            var deleteUrl = "/componentTest/core/kanban/delMPTFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFileMPT(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainKanbanId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }
    //..
    function returnPreviewSpanMPT(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            var url = '/componentTest/core/kanban/pdfPreviewAndAllDownloadMPT.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'office') {
            var url = '/componentTest/core/kanban/officePreviewMPT.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        } else if (fileType == 'pic') {
            var url = '/componentTest/core/kanban/imagePreviewMPT.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
        }
        return s;
    }
    //..删除文档,因为有多个filegrid，因此要重构rdmzhgl中的此函数
    function deleteFileMPT(fileName, fileId, formId, urlValue) {
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
                            if (fileListGridMPT) {
                                fileListGridMPT.load();
                            }
                        }
                    });
                }
            }
        );
    }
    //..
    function onDrawSamplePlanTime(e) {
        var date = e.date;
        var hopeTestMonth = mini.get("hopeTestMonth").getValue();
        if (hopeTestMonth) {
            var samplePlanTime = new Date(hopeTestMonth).sub(3, 'day');
            if (date.getTime() > samplePlanTime.getTime()) {
                e.allowSelect = false;
            }
        } else {
            e.allowSelect = false;
        }
    }
    //..
    function onHopeTestMonthChange() {
        mini.get("samplePlanTime").setValue("");
    }
    //..
    function onSampleSourceChange() {
        if (mini.get("sampleSource").getValue() == "供应商送样") {
            mini.get("receiverId").setEnabled(false);
        } else {
            mini.get("receiverId").setEnabled(true);
        }
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
        if (action == "edit" || (action == 'task' && (nodeName == "A1" || nodeName == "C"))) {
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
    //..以下测试项目相关
    function importTestItem() {
        var capabilityToPathMap = new Map();
        var componentCategory = mini.get("componentCategory").getValue();
        if (!componentCategory) {
            mini.alert("请先选择专业类别，再进行操作");
            return;
        }
        var componentName = mini.get("componentName").getValue();
        if (!componentName) {
            mini.alert("请先输入样品名称，再进行操作");
            return;
        }
        $.ajax({
            //递归取chart要求的树结构，老方法
            url: jsUseCtxPath + '/componentTest/core/capability/getComponentTestCapabilityChartData.do',
            type: 'get',
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    //只要第一层
                    for (var i = 0, l = data[0].children.length; i < l; i++) {
                        capabilityToPathMap.set(data[0].children[i].name, "0.1." + data[0].children[i].value)
                    }
                    var path = capabilityToPathMap.get(componentCategory);
                    var win = new TreeSelectWindow();
                    win.set({
                        //新方法，根据路径获取包含某路径的数组件要求的树全貌
                        url: jsUseCtxPath + "/componentTest/core/capability/getComponentTestCapabilityTreeByPath.do?path=" + path,
                        title: "选择树形",
                        width: 800,
                        height: 600
                    });
                    win.show();
                    //设置回调
                    win.setData(null, function (action) {
                        if (action == "ok") {
                            //获取数据
                            var data = win.getData();
                            if (data) {
                                $.ajax({
                                    url: jsUseCtxPath + '/componentTest/core/capability/componentTestCapabilityItemListQuery2.do?' +
                                    'capabilityId=' + data.id,
                                    type: 'get',
                                    contentType: 'application/json',
                                    success: function (data) {
                                        if (data.length > 0) {
                                            for (var i = 0, l = data.length; i < l; i++) {
                                                var newRow = {};
                                                newRow.testItem_item = mini.get("componentName").getValue();//样品名称
                                                newRow.itemOfTestItem_item = data[i].capabilityItem;//测试项次
                                                newRow.keyPoint_item = data[i].keyPoint;//关注要点
                                                newRow.resources_item = data[i].resources;//资源
                                                newRow.status_item = "未开展";//完成状态
                                                newRow.type_item = "内置";//类型
                                                testItemsGrid.addRow(newRow);
                                            }
                                        }
                                    }
                                });
                            }
                        } else if (action == "nodeclick") {
                            //获取数据
                            debugger;
                            var data = win.getData();
                            if (data.isLeaf == "true") {
//                                console.log(data);
//                                mini.alert(data.id);
                            }
                        }
                    });
                }
            }
        });
    }
    //..
    function addTestItem() {
        var newRow = {};
        newRow.status_item = "未开展";//完成状态
        newRow.type_item = "自定义";//类型
        testItemsGrid.addRow(newRow);
    }
    //..
    function deleteTestItem() {
        var selecteds = testItemsGrid.getSelecteds();
        testItemsGrid.removeRows(selecteds, true);
    }
    //..
    function addTestItemNonStandard() {
        var componentName = mini.get("componentName").getValue();
        if (!componentName) {
            mini.alert("请先输入样品名称，再进行操作");
            return;
        }
        var newRow = {};
        newRow.testItem_item = mini.get("componentName").getValue();//样品名称
        newRow.status_item = "未开展";//完成状态
        newRow.type_item = "自定义";//类型
        testItemsNonStandardGrid.addRow(newRow);
    }
    //..
    function deleteTestItemNonStandard() {
        var selecteds = testItemsNonStandardGrid.getSelecteds();
        testItemsNonStandardGrid.removeRows(selecteds, true);
    }
    //..
    function bRender(e) {
        return '<a class="mini-button btn-red" style="width: 100%;height: 100%" plain="true" onclick="bRenderClick()">完成</a>'
    }
    //..
    function bRenderClick() {
        var row = testItemsGrid.getSelected();
        if (row == null) {
            mini.alert("请至选中一条记录");
            return;
        }
        if (row.status_item == "进行中") {
            row.status_item = "已完成";
        } else if (row.status_item == "已完成") {
            row.status_item = "进行中";
        }
        testItemsGrid.updateRow(row);
    }
    //..
    function bNonStandardRender(e) {
        return '<a class="mini-button btn-red" style="width: 100%;height: 100%" plain="true" onclick="bNonStandardRenderClick()">完成</a>'
    }
    //..
    function bNonStandardRenderClick() {
        var row = testItemsNonStandardGrid.getSelected();
        if (row == null) {
            mini.alert("请至选中一条记录");
            return;
        }
        if (row.status_item == "进行中") {
            row.status_item = "已完成";
        } else if (row.status_item == "已完成") {
            row.status_item = "进行中";
        }
        testItemsNonStandardGrid.updateRow(row);
    }
    //..以下科技项目相关
    function searchProject() {
        var queryParam = [];
        var projectName = $.trim(mini.get("filterProjectName").getValue());
        if (projectName) {
            queryParam.push({name: "projectName", value: projectName});
        }
        var number = $.trim(mini.get("filterNumber").getValue());
        if (number) {
            queryParam.push({name: "number", value: number});
        }
//        var respMan = $.trim(mini.get("filterRespMan").getValue());
//        if (respMan) {
//            queryParam.push({name: "respMan", value: respMan});
//        }
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = projectListGrid.getPageIndex();
        data.pageSize = projectListGrid.getPageSize();
        data.sortField = projectListGrid.getSortField();
        data.sortOrder = projectListGrid.getSortOrder();
        //查询
        projectListGrid.load(data);
    }
    //..
    function selectProjectClick() {
        selectProjectWindow.show();
        searchProject();
    }
    //..
    function selectProjectCloseClick() {
        mini.get("projectId").setValue("");
        mini.get("projectId").setText("");
        mini.get("projectNo").setValue("");
    }
    //..
    function selectProjectOK() {
        var selectRow = projectListGrid.getSelected();
        if (selectRow) {
            mini.get("projectId").setValue(selectRow.projectId);
            mini.get("projectId").setText(selectRow.projectName);
            mini.get("projectNo").setValue(selectRow.number);
        } else {
            mini.alert("请选择一条数据！");
            return;
        }
        selectProjectHide();
    }
    //..
    function selectProjectHide() {
        selectProjectWindow.hide();
        mini.get("filterProjectName").setValue("");
        mini.get("filterNumber").setValue("");
        //mini.get("filterRespMan").setValue("");
    }

</script>
</body>
</html>
