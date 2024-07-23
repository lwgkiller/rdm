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
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()"><spring:message
                code="page.maintenanceManualDemandEdit.name"/></a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()"><spring:message code="page.maintenanceManualDemandEdit.name1"/></a>
        <a id="test" class="mini-button" onclick="showManualfile()"><spring:message code="page.maintenanceManualDemandEdit.name2"/></a>
        <a id="processSave" class="mini-button" style="display: none" onclick="saveBusinessInProcess()">特殊保存处理</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 100%;">
        <form id="formBusiness" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="manualfileId" name="manualfileId" class="mini-hidden"/>
            <input id="applyUserId" name="applyUserId" class="mini-hidden"/>
            <input id="applyDepId" name="applyDepId" class="mini-hidden"/>
            <input id="businessStatus" name="businessStatus" class="mini-hidden"/>
            <input id="BconfirmingId" name="BconfirmingId" class="mini-hidden"/>
            <input id="Bconfirming" name="Bconfirming" class="mini-hidden"/>
            <input id="INST_ID_" name="INST_ID_" class="mini-hidden"/>
            <input id="demandListStatus" name="demandListStatus" class="mini-hidden"/>
            <input id="amHandle" name="amHandle" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold">
                    <spring:message code="page.maintenanceManualDemandEdit.name3"/>
                </caption>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualDemandEdit.name4"/>：</td>
                    <td style="min-width:170px">
                        <input id="busunessNo" name="busunessNo" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualDemandEdit.name5"/>：</td>
                    <td style="min-width:170px">
                        <input id="demandListNo" name="demandListNo" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualDemandEdit.name6"/>：</td>
                    <td style="min-width:170px">
                        <input id="salesModel" name="salesModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualDemandEdit.name7"/>：</td>
                    <td style="min-width:170px">
                        <input id="designModel" name="designModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualDemandEdit.name8"/>：</td>
                    <td style="min-width:170px">
                        <input id="materialCode" name="materialCode" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualDemandEdit.name9"/>：</td>
                    <td style="min-width:170px">
                        <input id="manualLanguage" name="manualLanguage" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringLanguage"
                               valueField="key" textField="value" multiSelect="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualDemandEdit.name10"/>：</td>
                    <td style="min-width:170px">
                        <input id="isCE" name="isCE" class="mini-combobox" style="width:98%;" enabled="true"
                               textField="value" valueField="key" emptyText="<spring:message code="page.maintenanceManualDemandEdit.name11" />..."
                               allowInput="false" showNullItem="true"
                               nullItemText="<spring:message code="page.maintenanceManualDemandEdit.name11" />..."
                               data="[{'key' : '是','value' : '是'},
							   {'key' : '否','value' : '否'}]"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualDemandEdit.name12"/>：</td>
                    <td style="min-width:170px">
                        <input id="salesArea" name="salesArea" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">是否需要自声明：</td>
                    <td style="min-width:170px">
                        <input id="isSelfDeclaration" name="isSelfDeclaration" class="mini-combobox" style="width:98%;" enabled="false"
                               textField="value" valueField="key" emptyText="<spring:message code="page.maintenanceManualMctEdit.name6" />..."
                               allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.maintenanceManualMctEdit.name6" />..."
                               data="[{'key' : '是','value' : '是'},{'key' : '否','value' : '否'}]"/>
                    </td>
                    <td style="text-align: center;width: 15%">EC自声明识别码：</td>
                    <td style="min-width:170px">
                        <input id="CEOnlyNum" name="CEOnlyNum" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">EC自声明语言：</td>
                    <td style="min-width:170px">
                        <input id="CELanguage" name="CELanguage" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualDemandEdit.name13"/>：</td>
                    <td style="min-width:170px">
                        <input id="quantity" name="quantity" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualDemandEdit.name14"/>：</td>
                    <td style="min-width:170px">
                        <input id="instructions" name="instructions" class="mini-combobox" style="width:98%;" enabled="false"
                               textField="value" valueField="key" emptyText="<spring:message code="page.maintenanceManualDemandEdit.name11" />..."
                               allowInput="false" showNullItem="true"
                               nullItemText="<spring:message code="page.maintenanceManualDemandEdit.name11" />..."
                               data="[{'key' : '打印','value' : '打印'},
							   {'key' : '产品主管确认','value' : '产品主管确认'},
							   {'key' : '翻译','value' : '翻译'},
							   {'key' : '原文件可用','value' : '原文件可用'},
							   {'key' : '新增','value' : '新增'},
							   {'key' : '变更','value' : '变更'}]"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.maintenanceManualDemandEdit.name15"/>：</td>
                    <td colspan="3">
						<textarea id="configurationDescription" name="configurationDescription" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:98%;height:150px;line-height:25px;" label="" datatype="varchar" length="1000"
                                  vtype="length:1000" minlen="0" allowinput="true" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.maintenanceManualDemandEdit.name16"/>：</td>
                    <td style="min-width:170px">
                        <input id="applyTime" name="applyTime" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                               valueType="string" showTime="false" showOkButton="false" showClearButton="true" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.maintenanceManualDemandEdit.name17"/>：</td>
                    <td style="min-width:170px">
                        <input id="publishTime" name="publishTime" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                               valueType="string" showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualDemandEdit.name18"/>：</td>
                    <td style="min-width:170px">
                        <input id="applyUser" name="applyUser" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualDemandEdit.name19"/>：</td>
                    <td style="min-width:170px">
                        <input id="applyDep" name="applyDep" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualDemandEdit.name20"/>(<span
                            style="color: red"><spring:message code="page.maintenanceManualDemandEdit.name21"/></span>)：
                    </td>
                    <td style="min-width:170px">
                        <input id="manualCode" name="manualCode" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualDemandEdit.name22"/>(<span
                            style="color: red"><spring:message code="page.maintenanceManualDemandEdit.name23"/></span>)：
                    </td>
                    <td style="min-width:170px">
                        <input id="manualVersion" name="manualVersion" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.maintenanceManualDemandEdit.name24"/>：</td>
                    <td colspan="3">
						<textarea id="remark" name="remark" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:98%;height:150px;line-height:25px;" label="" datatype="varchar" length="640"
                                  vtype="length:640" minlen="0" allowinput="true" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <%--///////////////////////////////////////////////////--%>
                <tr>
                    <td style="text-align: center;height: 300px"><spring:message code="page.maintenanceManualDemandEdit.name25"/>：</td>
                    <td colspan="3">
                        <div class="mini-toolbar" id="manualMatchButtons">
                            <a id="addManualMatch" class="mini-button" style="margin-bottom: 5px;margin-top: 5px"
                               onclick="addManualMatch"><spring:message code="page.maintenanceManualDemandEdit.name26"/></a>
                            <a id="delManualMatch" class="mini-button btn-red" style="margin-bottom: 5px;margin-top: 5px"
                               onclick="delManualMatch"><spring:message code="page.maintenanceManualDemandEdit.name27"/></a>
                            <span style="color: red"><spring:message code="page.maintenanceManualDemandEdit.name28"/></span>
                        </div>
                        <div id="manualMatchListGrid" class="mini-datagrid" allowResize="false" style="height:300px" autoload="true"
                             idField="id" allowCellEdit="false" allowCellSelect="true" allowSortColumn="false"
                             multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
                             url="${ctxPath}/serviceEngineering/core/maintenanceManualDemand/getManualMatchList.do?businessId=${businessId}">
                            <div property="columns">
                                <div type="checkcolumn" width="50"></div>
                                <div type="indexcolumn" headerAlign="center" width="50"><spring:message
                                        code="page.maintenanceManualDemandEdit.name29"/></div>
                                <div field="REF_ID_" width="80" headerAlign="center" align="center" renderer="renderREF" allowSort="true">
                                    类型标记
                                </div>
                                <div field="salesModel" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                                    <spring:message code="page.maintenanceManualDemandEdit.name6"/></div>
                                <div field="designModel" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                                    <spring:message code="page.maintenanceManualDemandEdit.name7"/></div>
                                <div field="materialCode" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                                    <spring:message code="page.maintenanceManualDemandEdit.name8"/>
                                </div>
                                <div field="manualDescription" width="150" headerAlign="center" align="center" renderer="render" allowSort="true">
                                    <spring:message code="page.maintenanceManualDemandEdit.name30"/>
                                </div>
                                <div field="cpzgName" width="70" headerAlign="center" align="center" renderer="render" allowSort="true">
                                    <spring:message code="page.maintenanceManualDemandEdit.name31"/></div>
                                <div field="manualLanguage" width="70" headerAlign="center" align="center" renderer="render" allowSort="true">
                                    <spring:message code="page.maintenanceManualDemandEdit.name32"/>
                                </div>
                                <div field="manualCode" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                                    <spring:message code="page.maintenanceManualDemandEdit.name33"/></div>
                                <div field="manualVersion" width="70" headerAlign="center" align="center" renderer="render" allowSort="true">
                                    <spring:message code="page.maintenanceManualDemandEdit.name22"/>
                                </div>
                                <div field="manualEdition" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">
                                    <spring:message code="page.maintenanceManualDemandEdit.name34"/>
                                </div>
                                <div field="isCE" width="70" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                                        code="page.maintenanceManualDemandEdit.name35"/></div>
                                <div field="keyUser" displayField="keyUser" width="70" headerAlign="center" align="center" renderer="render"
                                     allowSort="true"><spring:message code="page.maintenanceManualDemandEdit.name36"/>
                                </div>
                                <div field="publishTime" width="100" headerAlign="center" align="center" dateFormat="yyyy-MM-dd" renderer="render"
                                     allowSort="true"><spring:message code="page.maintenanceManualDemandEdit.name37"/>
                                </div>
                                <div field="manualStatus" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">
                                    <spring:message code="page.maintenanceManualDemandEdit.name38"/>
                                </div>
                                <div cellCls="actionIcons" width="60" headerAlign="center" align="center" renderer="manualFileRenderer"
                                     cellStyle="padding:0;"><spring:message code="page.maintenanceManualDemandEdit.name39"/>
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                <%--///////////////////////////////////////////////////--%>
                <tr>
                    <td style="text-align: center;height: 300px"><spring:message code="page.maintenanceManualDemandEdit.name40"/>：</td>
                    <td colspan="3">
                        <div style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="addBusinessFile()"><spring:message
                                    code="page.maintenanceManualDemandEdit.name41"/></a>
                            <span style="color: red"><spring:message code="page.maintenanceManualDemandEdit.name42"/></span>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
                             idField="id"
                             url="${ctxPath}/serviceEngineering/core/maintenanceManualDemand/getFileList.do?businessId=${businessId}"
                             autoload="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20"><spring:message
                                        code="page.maintenanceManualDemandEdit.name29"/></div>
                                <div field="fileName" align="center" headerAlign="center" width="150"><spring:message
                                        code="page.maintenanceManualDemandEdit.name43"/></div>
                                <div field="fileSize" align="center" headerAlign="center" width="60"><spring:message
                                        code="page.maintenanceManualDemandEdit.name44"/></div>
                                <div field="fileDesc" align="center" headerAlign="center" width="100"><spring:message
                                        code="page.maintenanceManualDemandEdit.name45"/></div>
                                <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer"><spring:message
                                        code="page.maintenanceManualDemandEdit.name39"/></div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<%--操保手册弹出窗口--%>
<div id="selectManualFileWindow" title="<spring:message code="page.maintenanceManualDemandEdit.name46" />" class="mini-window"
     style="width:1450px;height:700px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px" borderStyle="border-left:0;border-top:0;border-right:0;">
        <li style="margin-right: 15px">
            <span class="text" style="width:auto"><spring:message code="page.maintenanceManualDemandEdit.name6"/>：</span>
            <input class="mini-textbox" id="salesModelSub" name="salesModelSub" style="width: 120px"/>
            <span class="text" style="width:auto"><spring:message code="page.maintenanceManualDemandEdit.name7"/>：</span>
            <input class="mini-textbox" id="designModelSub" name="designModelSub" style="width: 120px"/>
            <span class="text" style="width:auto"><spring:message code="page.maintenanceManualDemandEdit.name47"/>：</span>
            <input class="mini-textbox" id="materialCodeSub" name="materialCodeSub" style="width: 120px"/>
            <span class="text" style="width:auto"><spring:message code="page.maintenanceManualDemandEdit.name30"/>：</span>
            <input class="mini-textbox" id="manualDescriptionSub" name="manualDescriptionSub" style="width: 120px"/>
            <span class="text" style="width:auto"><spring:message code="page.maintenanceManualDemandEdit.name32"/>：</span>
            <input class="mini-textbox" id="manualLanguageSub" name="manualLanguageSub" style="width: 120px"/>
            <span class="text" style="width:auto"><spring:message code="page.maintenanceManualDemandEdit.name33"/>：</span>
            <input class="mini-textbox" id="manualCodeSub" name="manualCodeSub" style="width: 120px"/>
            <span class="text" style="width:auto"><spring:message code="page.maintenanceManualDemandEdit.name48"/>：</span>
            <input class="mini-textbox" id="isCESub" name="isCESub" style="width: 120px"/>
        </li>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchManualFile()"><spring:message
                code="page.maintenanceManualDemandEdit.name49"/></a>
    </div>
    <div class="mini-fit">
        <div id="manualFileListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" multiSelect="true"
             allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/serviceEngineering/core/maintenanceManualfile/dataListQuery.do">
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div field="salesModel" width="120" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.maintenanceManualDemandEdit.name6"/></div>
                <div field="designModel" width="120" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.maintenanceManualDemandEdit.name7"/></div>
                <div field="materialCode" width="120" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.maintenanceManualDemandEdit.name8"/></div>
                <div field="manualDescription" width="400" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.maintenanceManualDemandEdit.name30"/></div>
                <div field="cpzgName" width="80" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.maintenanceManualDemandEdit.name31"/></div>
                <div field="manualLanguage" width="80" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.maintenanceManualDemandEdit.name32"/></div>
                <div field="manualCode" width="140" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.maintenanceManualDemandEdit.name33"/></div>
                <div field="manualVersion" width="80" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.maintenanceManualDemandEdit.name22"/></div>
                <div field="manualEdition" width="80" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.maintenanceManualDemandEdit.name34"/></div>
                <div field="isCE" width="80" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.maintenanceManualDemandEdit.name35"/></div>
                <div field="manualStatus" width="80" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.maintenanceManualDemandEdit.name38"/></div>
                <div field="publishTime" width="110" headerAlign="center" align="center" dateFormat="yyyy-MM-dd" renderer="render" allowSort="true">
                    <spring:message code="page.maintenanceManualDemandEdit.name37"/></div>
                <div field="remark" width="300" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.maintenanceManualfileList.name33"/></div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.maintenanceManualDemandEdit.name50" />"
                           onclick="selectFileOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.maintenanceManualDemandEdit.name51" />"
                           onclick="selectFileHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var status = "${status}";
    var jsUseCtxPath = "${ctxPath}";
    var fileListGrid = mini.get("fileListGrid");
    var formBusiness = new mini.Form("#formBusiness");
    var manualMatchListGrid = mini.get("manualMatchListGrid");
    var selectManualFileWindow = mini.get("selectManualFileWindow");
    var manualFileListGrid = mini.get("manualFileListGrid");
    var businessId = "${businessId}";
    var nodeVarsStr = '${nodeVars}';
    var currentTime = "${currentTime}";
    var applyTime = "${applyTime}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var maintenanceManualAdmin = "${maintenanceManualAdmin}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
    var isBianzhi = "";
    var isQueren = "";
    var isTranslation = "";
    var isCPZGQueren = "";
    var isDayin = "";

    //..
    $(function () {
        var url = jsUseCtxPath + "/serviceEngineering/core/maintenanceManualDemand/getDetail.do";
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
                    //特殊保存处理
                    if (currentUserNo == 'admin' || currentUserNo == maintenanceManualAdmin) {
                        $("#processSave").show();
                        mini.get("configurationDescription").setEnabled(true);
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
        if (formData.SUB_manualMatchListGrid) {
            delete formData.SUB_manualMatchListGrid;
        }
        //@lwgkiller:想要正确进行标题解析，不能删
        formData.bos = [];
        if (manualMatchListGrid.getChanges().length > 0) {
            formData.changeManualMatchListGrid = manualMatchListGrid.getChanges();
        }
        return formData;
    }

    //..保存草稿
    function saveBusiness(e) {
        //mini.get("applyUserId").setValue(currentUserId);
        window.parent.saveDraft(e);
    }

    //..启动流程
    function startBusinessProcess(e) {

        // @mh 启动时候，申请时间要给他加上
        mini.get("applyTime").setValue(applyTime);
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
        if (formData.SUB_manualMatchListGrid) {
            delete formData.SUB_manualMatchListGrid;
        }
        if (manualMatchListGrid.getChanges().length > 0) {
            formData.changeManualMatchListGrid = manualMatchListGrid.getChanges();
        }
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/maintenanceManualDemand/saveBusiness.do',
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message = "";
                    if (data.success) {
                        message = maintenanceManualDemandEdit_name;
                    } else {
                        message = maintenanceManualDemandEdit_name1 + data.message;
                    }

                    mini.alert(message, maintenanceManualDemandEdit_name2, function () {
                        window.location.reload();
                    });
                }
            }
        });
    }

    //..流程中的审批或者下一步
    function businessApprove() {
        var manualLanguages = mini.get("manualLanguage").getValue().split(',');
        var manualLanguagesMatch = new Array();
        for (var i = 0; i < manualMatchListGrid.getData().length; i++) {
            //只有MCT最终回传才会带回参考标记，理论上这个判断没用，但是保留着
            //原文件标记，这个标记也得过滤掉
            if (manualMatchListGrid.getData()[i].REF_ID_ != '1' && manualMatchListGrid.getData()[i].REF_ID_ != '2') {
                manualLanguagesMatch.push(manualMatchListGrid.getData()[i].manualLanguage);
            }
        }
        //编制阶段的下一步需要校验表单必填字段
        if (isBianzhi == 'yes') {
            var formValid = validBusiness();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
            //检查通过
            window.parent.approve();
        } else if (isQueren == 'yes') {
            if (manualMatchListGrid.getChanges().length > 0) {
                mini.alert("请先点击暂存信息，进行图册匹配明细信息的保存！");
                return;
            }
            var instructions = $.trim(mini.get("instructions").getValue())
            if (!instructions) {
                mini.alert("请填写下一步指令！");
                return;
            } else if (instructions == '新增' || instructions == '变更' || instructions == '原文件可用') {
                mini.alert("本阶段下一步指令仅能为 打印|产品主管确认|翻译");
                return;
            } else if (instructions == '打印') {
                if (manualLanguages.sort().toString() != manualLanguagesMatch.sort().toString()) {
                    mini.alert("图册匹配明细与主记录的语言不完全匹配！");
                    return;
                }
                var manualMatchs = manualMatchListGrid.getData();
                for (var i = 0; i < manualMatchs.length; i++) {
                    if (manualMatchs[i].REF_ID_ != '1' &&//只有MCT最终回传才会带回参考标记，理论上这个判断没用，但是保留着
                        manualMatchs[i].REF_ID_ != '2' &&//原文件标记，这个标记也得过滤掉
                        manualMatchs[i].manualStatus != '可打印' &&
                        manualMatchs[i].manualStatus != '历史版本') {
                        mini.alert("图册匹配明细状态必须是“可打印”或“历史版本”！");
                        return;
                    }
                    if (manualMatchs[i].REF_ID_ != '1' &&//只有MCT最终回传才会带回参考标记，理论上这个判断没用，但是保留着
                        manualMatchs[i].REF_ID_ != '2' &&//原文件标记，这个标记也得过滤掉
                        ($.trim(manualMatchs[i].salesModel) != $.trim(mini.get("salesModel").getValue()) ||
                        $.trim(manualMatchs[i].designModel) != $.trim(mini.get("designModel").getValue()) ||
                        $.trim(manualMatchs[i].materialCode) != $.trim(mini.get("materialCode").getValue()))) {
                        mini.alert("图册匹配明细的销售型号设计型号和物料号必须和单据一致！");
                        return;
                    }
                }
            }
            window.parent.approve();
        } else if (isTranslation == 'yes') {
            if (manualMatchListGrid.getChanges().length > 0) {
                mini.alert("请先点击暂存信息，进行图册匹配明细信息的保存！");
                return;
            }
            if (manualLanguages.sort().toString() != manualLanguagesMatch.sort().toString()) {
                mini.alert("图册匹配明细与主记录的语言不完全匹配！");
                return;
            }
            var manualMatchs = manualMatchListGrid.getData();
            for (var i = 0; i < manualMatchs.length; i++) {
                if (manualMatchs[i].REF_ID_ != '1' &&//只有MCT最终回传才会带回参考标记，理论上这个判断没用，但是保留着
                    manualMatchs[i].REF_ID_ != '2' &&//原文件标记，这个标记也得过滤掉
                    manualMatchs[i].manualStatus != '可打印' &&
                    manualMatchs[i].manualStatus != '历史版本') {
                    mini.alert("图册匹配明细状态必须是“可打印”或“历史版本”！");
                    return;
                }
                if (manualMatchs[i].REF_ID_ != '1' &&//只有MCT最终回传才会带回参考标记，理论上这个判断没用，但是保留着
                    manualMatchs[i].REF_ID_ != '2' &&//原文件标记，这个标记也得过滤掉
                    ($.trim(manualMatchs[i].salesModel) != $.trim(mini.get("salesModel").getValue()) ||
                    $.trim(manualMatchs[i].designModel) != $.trim(mini.get("designModel").getValue()) ||
                    $.trim(manualMatchs[i].materialCode) != $.trim(mini.get("materialCode").getValue()))) {
                    mini.alert("图册匹配明细的销售型号设计型号和物料号必须和单据一致！");
                    return;
                }
            }
            window.parent.approve();
        } else if (isCPZGQueren == 'yes') {
            if (manualMatchListGrid.getChanges().length > 0) {
                mini.alert("请先点击暂存信息，进行图册匹配明细信息的保存！");
                return;
            }
            var instructions = $.trim(mini.get("instructions").getValue())
            if (!instructions) {
                mini.alert("请填写下一步指令！");
                return;
            } else if (instructions == '打印' || instructions == '产品主管确认' || instructions == '翻译') {
                mini.alert("本阶段下一步指令仅能为 新增|变更|原文件可用");
                return;
            } else if (instructions == '原文件可用') {
                var manualMatchs = manualMatchListGrid.getData();
                if (manualMatchs.length != 1) {
                    mini.alert("图册匹配明细里的原文件必须有唯一的一条记录！");
                    return;
                }
                for (var i = 0; i < manualMatchs.length; i++) {
                    if (manualMatchs[i].REF_ID_ != '1' &&//只有MCT最终回传才会带回参考标记，理论上这个判断没用，但是保留着
                        manualMatchs[i].REF_ID_ != '2' &&//原文件标记，这个标记也得过滤掉
                        manualMatchs[i].manualStatus != '可打印' &&
                        manualMatchs[i].manualStatus != '历史版本') {
                        mini.alert("图册匹配明细状态必须是“可打印”或“历史版本”！");
                        return;
                    }
                    if (manualMatchs[i].REF_ID_ != '1' &&//只有MCT最终回传才会带回参考标记，理论上这个判断没用，但是保留着
                        ($.trim(manualMatchs[i].salesModel) != $.trim(mini.get("salesModel").getValue()) ||
                        $.trim(manualMatchs[i].designModel) != $.trim(mini.get("designModel").getValue()) ||
                        $.trim(manualMatchs[i].materialCode) != $.trim(mini.get("materialCode").getValue()))) {
                        mini.alert("图册匹配明细的销售型号设计型号和物料号必须和单据一致！");
                        return;
                    }
                }
                window.parent.approve();
            } else if (instructions == '新增' || instructions == '变更') {
                var manualMatchs = manualMatchListGrid.getData();
                if (manualMatchs.length != 1) {
                    mini.alert("图册匹配明细必须有唯一的一条记录，供新增和变更参考！");
                    return;
                }
                //todo:mark！！！如果不存在demandId和businessId一样的MCT申请，点击办理->增，改前端并不办理，而是开启增，改对应的MCT申请，申请者为当前用户。
                //此后这一步永远不需要人工办理了，当相应的MCT申请进入了“处理中”的状态时，回调驱动这一步办理通过，回调时此表单肯定存在instructions指令，
                //根据相应指令，网关会自动转至相应的XX中状态，办理者就是MCT的申请者，又回来了
                $.ajax({
                    url: jsUseCtxPath + '/serviceEngineering/core/maintenanceManualMct/dataListQuery.do?demandId=' + businessId,
                    type: 'POST',
                    async: false,
                    contentType: 'application/json',
                    success: function (returnData) {
                        if (returnData && returnData.total > 0) {
                            mini.alert(maintenanceManualDemandEdit_name11);
                        } else {
                            var formData = _GetFormJsonMini("formBusiness");
                            if (formData.SUB_fileListGrid) {
                                delete formData.SUB_fileListGrid;
                            }
                            $.ajax({
                                url: jsUseCtxPath + '/serviceEngineering/core/maintenanceManualDemand/saveBusiness.do',
                                type: 'post',
                                async: false,
                                data: mini.encode(formData),
                                contentType: 'application/json',
                                success: function (data) {
                                    if (data) {
                                        if (data.success) {
                                            var url = jsUseCtxPath + "/bpm/core/bpmInst/maintenanceManualMct/start.do?demandId_=" + businessId;
                                            window.open(url);
                                        } else {
                                            message = maintenanceManualDemandEdit_name1 + data.message;
                                        }
                                    }
                                }
                            });
                        }
                    }
                })
            }
        } else {
            window.parent.approve();
        }
    }

    //..添加文件
    function addBusinessFile() {
        var businessId = mini.get("id").getValue();
        if (!businessId) {
            mini.alert(maintenanceManualDemandEdit_name12);
            return;
        }
        mini.open({
            title: maintenanceManualDemandEdit_name13,
            url: jsUseCtxPath + "/serviceEngineering/core/maintenanceManualDemand/fileUploadWindow.do?businessId=" + businessId,
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
            title: maintenanceManualDemandEdit_name14,
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
            } else if (nodeVars[i].KEY_ == 'isTranslation') {
                isTranslation = nodeVars[i].DEF_VAL_;
            } else if (nodeVars[i].KEY_ == 'isCPZGQueren') {
                isCPZGQueren = nodeVars[i].DEF_VAL_;
            } else if (nodeVars[i].KEY_ == 'isDayin') {
                isDayin = nodeVars[i].DEF_VAL_;
            }
        }
        if (isBianzhi != 'yes') {
            formBusiness.setEnabled(false);
            mini.get("remark").setEnabled(true);
            mini.get("addFile").setEnabled(false);
        }
        else {
            mini.get("addManualMatch").setEnabled(true);
            mini.get("delManualMatch").setEnabled(true);
        }
        if (isQueren == 'yes') {
            mini.get("instructions").setEnabled(true);
        } else if (isCPZGQueren == 'yes') {
            mini.get("instructions").setEnabled(true);
        } else if (isDayin == 'yes') {
            mini.get("addManualMatch").setEnabled(false);
            mini.get("delManualMatch").setEnabled(false);
        }
    }

    //..检验表单是否必填
    function validBusiness() {
        var id = $.trim(mini.get("id").getValue());
        if (!id) {
            return {"result": false, "message": "请先保存草稿"};
        }
        var demandListNo = $.trim(mini.get("demandListNo").getValue());
        if (!demandListNo) {
            return {"result": false, "message": maintenanceManualDemandEdit_name15};
        }
        var salesModel = $.trim(mini.get("salesModel").getValue());
        if (!salesModel) {
            return {"result": false, "message": maintenanceManualDemandEdit_name16};
        }
        var designModel = $.trim(mini.get("designModel").getValue());
        if (!designModel) {
            return {"result": false, "message": maintenanceManualDemandEdit_name17};
        }
        var materialCode = $.trim(mini.get("materialCode").getValue());
        if (!materialCode) {
            return {"result": false, "message": maintenanceManualDemandEdit_name18};
        }
        var manualLanguage = $.trim(mini.get("manualLanguage").getValue());
        if (!manualLanguage) {
            return {"result": false, "message": maintenanceManualDemandEdit_name19};
        }
        var isCE = $.trim(mini.get("isCE").getValue());
        if (!isCE) {
            return {"result": false, "message": maintenanceManualDemandEdit_name20};
        }
        var salesArea = $.trim(mini.get("salesArea").getValue());
        if (!salesArea) {
            return {"result": false, "message": maintenanceManualDemandEdit_name21};
        }
        var quantity = $.trim(mini.get("quantity").getValue());
        if (!quantity) {
            return {"result": false, "message": maintenanceManualDemandEdit_name22};
        }
        var configurationDescription = $.trim(mini.get("configurationDescription").getValue());
        if (!configurationDescription) {
            return {"result": false, "message": maintenanceManualDemandEdit_name23};
        }
        var applyTime = $.trim(mini.get("applyTime").getValue());
        if (!applyTime) {
            return {"result": false, "message": maintenanceManualDemandEdit_name24};
        }
        var publishTime = $.trim(mini.get("publishTime").getValue());
        if (!publishTime) {
            return {"result": false, "message": maintenanceManualDemandEdit_name25};
        }
        if (manualMatchListGrid.getChanges().length > 0) {
            return {"result": false, "message": maintenanceManualDemandEdit_name3};
        }
        //..需求单号、销售型号、设计型号、物料编码、语言、是否CE版手册、销售区域、数量、发车时间完全一致时，提示已发起
        var xiaohanNoNoNo = false;
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/maintenanceManualDemand/dataListQuery.do?businessId=' + businessId,
            type: 'POST',
            async: false,
            contentType: 'application/json',
            success: function (returnData) {
                if (returnData && returnData.total > 1) {
                    xiaohanNoNoNo = true;
                }
            }
        })
        if (xiaohanNoNoNo == true) {
            return {"result": false, "message": maintenanceManualDemandEdit_name28};
        }
        return {"result": true};
    }

    //..文件列表操作渲染
    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnPreviewSpan(record.fileName, record.id, record.mainId, coverContent);
        //和pdf预览用的同一个url
        var downLoadUrl = '/serviceEngineering/core/maintenanceManualDemand/PdfPreviewAndAllDownload.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + maintenanceManualDemandEdit_name29 + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainId + '\',\'' + downLoadUrl + '\')">' + maintenanceManualDemandEdit_name29 + '</span>';
        //增加删除按钮
        if (action == 'edit' || (action == 'task' && isBianzhi == 'yes')) {
            var deleteUrl = "/serviceEngineering/core/maintenanceManualDemand/delFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + maintenanceManualDemandEdit_name30 + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainId + '\',\'' + deleteUrl + '\')">' + maintenanceManualDemandEdit_name30 + '</span>';
        }
        return cellHtml;
    }

    //..文件列表预览渲染
    function returnPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title=' + maintenanceManualDemandEdit_name31 + ' style="color: silver" >' + maintenanceManualDemandEdit_name31 + '</span>';
        } else if (fileType == 'pdf') {
            var url = '/serviceEngineering/core/maintenanceManualDemand/PdfPreview.do';
            s = '<span  title=' + maintenanceManualDemandEdit_name31 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + maintenanceManualDemandEdit_name31 + '</span>';
        } else if (fileType == 'office') {
            var url = '/serviceEngineering/core/maintenanceManualDemand/OfficePreview.do';
            s = '<span  title=' + maintenanceManualDemandEdit_name31 + ' style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + maintenanceManualDemandEdit_name31 + '</span>';
        } else if (fileType == 'pic') {
            var url = '/serviceEngineering/core/maintenanceManualDemand/ImagePreview.do';
            s = '<span  title=' + maintenanceManualDemandEdit_name31 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + maintenanceManualDemandEdit_name31 + '</span>';
        }
        return s;
    }

    //..便捷查看相关归档文件
    function showManualfile() {
        var salesModel = mini.get("salesModel").getValue();
        var designModel = mini.get("designModel").getValue();
        var materialCode = mini.get("materialCode").getValue();
        var manualLanguage = mini.get("manualLanguage").getValue();
        var isCE = mini.get("isCE").getValue();
        var realUrl = jsUseCtxPath + "/serviceEngineering/core/maintenanceManualfile/dataListPage.do?salesModel=" + salesModel +
            "&designModel=" + designModel + "&materialCode=" + materialCode + "&manualLanguage=" + manualLanguage + "&isCE=" + isCE;
        var config = {
            url: realUrl,
            max: true
        };
        _OpenWindow(config);
    }

    //..
    function manualFileRenderer(e) {
        var record = e.record;
        var cellHtml = '<span  title=' + maintenanceManualDemandEdit_name31 + ' style="color:#409EFF;cursor: pointer;" onclick="previewManualFile(\'' + record.manualFileId + '\',\'' + coverContent + '\')">' + maintenanceManualDemandEdit_name31 + '</span>';
        return cellHtml;
    }

    //..
    function previewManualFile(id, coverContent) {
        var previewUrl = jsUseCtxPath + "/serviceEngineering/core/maintenanceManualfile/Preview.do?id=" + id;
        window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverContent) + "&file=" + encodeURIComponent(previewUrl));
    }

    //..
    function addManualMatch() {
        //打开选择窗口，选择后插入本表中
        selectManualFileWindow.show();
        searchManualFile();
    }

    //..
    function delManualMatch() {
        var selecteds = manualMatchListGrid.getSelecteds();
        manualMatchListGrid.removeRows(selecteds);
    }

    //..
    function searchManualFile() {
        var paramArray = [];
        paramArray.push({name: "salesModel", value: mini.get('salesModelSub').getValue()});
        paramArray.push({name: "designModel", value: mini.get('designModelSub').getValue()});
        paramArray.push({name: "materialCode", value: mini.get('materialCodeSub').getValue()});
        paramArray.push({name: "manualDescription", value: mini.get('manualDescriptionSub').getValue()});
        paramArray.push({name: "manualLanguage", value: mini.get('manualLanguageSub').getValue()});
        paramArray.push({name: "manualCode", value: mini.get('manualCodeSub').getValue()});
        paramArray.push({name: "isCE", value: mini.get('isCESub').getValue()});
        var data = {};
        data.filter = mini.encode(paramArray);
        manualFileListGrid.load(data);
    }

    //..
    function selectFileOK() {
        var rows = manualFileListGrid.getSelecteds();
        if (rows.length > 0) {
            for (var index = 0; index < rows.length; index++) {
                var row = rows[index];
                row.manualFileId = row.id;
                row.id = '';
                manualMatchListGrid.addRow(row);
            }
        }
        selectFileHide();
    }

    //..
    function selectFileHide() {
        mini.get('salesModelSub').setValue('');
        mini.get('designModelSub').setValue('');
        mini.get('materialCodeSub').setValue('');
        mini.get('manualDescriptionSub').setValue('');
        mini.get('manualLanguageSub').setValue('');
        mini.get('manualCodeSub').setValue('');
        mini.get('isCESub').setValue('');
        selectManualFileWindow.hide();
    }

    //..
    function renderREF(e) {
        if (e.value != null && e.value != "" && e.record.REF_ID_ == '1') {
            return "参考手册";
        } else if (e.value != null && e.value != "" && e.record.REF_ID_ == '2') {
            return "原文件可用";
        }
        else {
            return "";
        }
    }
</script>
</body>
</html>
