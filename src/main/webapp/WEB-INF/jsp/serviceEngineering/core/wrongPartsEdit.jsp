<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>错件整改编辑</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/serviceEngineering/wrongPartsEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/serviceEngineering/wrongPartsData.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="cjzgProcessInfo()"><spring:message
                code="page.wrongPartsEdit.name"/></a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()"><spring:message code="page.wrongPartsEdit.name1"/></a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 80%;">
        <form id="formCjzg" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="INST_ID_" name="INST_ID_" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <input id="actualTime" name="actualTime" class="mini-hidden"/>
            <input id="businessStatus" name="businessStatus" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold">
                    <spring:message code="page.wrongPartsEdit.name2"/>
                </caption>
                <tr>
                    <td style="width: 17%"><spring:message code="page.wrongPartsEdit.name3"/>：</td>
                    <td style="width: 33%;min-width:170px">
                        <input id="wrongPartName" name="wrongPartName" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 14%"><spring:message code="page.wrongPartsEdit.name4"/>：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="salesModel" name="salesModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%"><spring:message code="page.wrongPartsEdit.name5"/>：</td>
                    <td style="width: 36%;min-width:140px">
                        <input id="designModel" name="designModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 14%"><spring:message code="page.wrongPartsEdit.name6"/>：</td>
                    <td style="width: 36%;">
                        <input id="machineCode" name="machineCode" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%"><spring:message code="page.wrongPartsEdit.name7"/>：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="agent" name="agent" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 14%">XGSS整改人：</td>
                    <td style="width: 36%;min-width:140px">
                        <input id="XGSSRespUserId" name="XGSSRespUserId" textname="XGSSRespUserName" class="mini-user rxc"
                               plugins="mini-user" style="width:98%" allowinput="false" label="" length="50"
                               mainfield="no" single="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%"><spring:message code="page.wrongPartsEdit.name10"/>：</td>
                    <td style="width: 36%;min-width:140px">
                        <input id="responsibleDepartmentId" name="responsibleDepartmentId" class="mini-dep rxc" plugins="mini-dep"
                               style="width:98%;height:34px"
                               allowinput="false" textname="respDepName" length="200" maxlength="200" minlen="0" single="true" initlogindep="false"/>
                    </td>
                    <td style="width: 14%"><spring:message code="page.wrongPartsEdit.name8"/>：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="typeOfWrongPart" name="typeOfWrongPart" class="mini-combobox" style="width:98%"
                               textField="value" valueField="key" emptyText="<spring:message code="page.wrongPartsEdit.name9" />..."
                               required="false" allowInput="false" showNullItem="true"
                               nullItemText="<spring:message code="page.wrongPartsEdit.name9" />..."
                        />
                    </td>

                </tr>
                <tr>
                    <td style="width: 14%">责任人：</td>
                    <td style="width: 36%;min-width:140px">
                        <input id="principalUserId" name="principalUserId" textname="principalUserName" class="mini-user rxc"
                               plugins="mini-user" style="width:98%" allowinput="false" label="" length="50"
                               mainfield="no" single="true"/>
                    </td>
                    <td style="width: 14%">是否历史遗留问题：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="isHistory" name="isHistory" class="mini-combobox" style="width:98%"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true"
                               nullItemText="请选择..." data="[{'key' : '是','value' : '是'},{'key' : '否','value' : '否'}]"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%"><spring:message code="page.wrongPartsEdit.name12"/>：</td>
                    <td colspan="3">
						<textarea id="problemDescription" name="problemDescription" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:98%;height:150px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000"
                                  minlen="0" allowinput="true"
                                  emptytext="<spring:message code="page.wrongPartsEdit.name33" />" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%;height: 300px"><spring:message code="page.wrongPartsEdit.name13"/>：</td>
                    <td colspan="3">
                        <div style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="addCjzgFile()"><spring:message code="page.wrongPartsEdit.name14"/></a>
                            <span style="color: red"><spring:message code="page.wrongPartsEdit.name15"/></span>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
                             idField="id" url="${ctxPath}/serviceEngineering/core/wrongParts/cjzgFileList.do?cjzgId=${cjzgId}" autoload="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20"><spring:message code="page.wrongPartsEdit.name16"/></div>
                                <div field="fileName" width="140" headerAlign="center" align="center"><spring:message
                                        code="page.wrongPartsEdit.name17"/></div>
                                <div field="fileSize" width="80" headerAlign="center" align="center"><spring:message
                                        code="page.wrongPartsEdit.name18"/></div>
                                <div field="fileDesc" width="80" headerAlign="center" align="center"><spring:message
                                        code="page.wrongPartsEdit.name19"/></div>
                                <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer"><spring:message
                                        code="page.wrongPartsEdit.name20"/></div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%"><spring:message code="page.wrongPartsEdit.name21"/>：</td>
                    <td style="width: 36%;" colspan="3">
						<textarea id="causeAnalysis" name="causeAnalysis" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:98%;;height:150px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000"
                                  minlen="0" allowinput="true"
                                  emptytext="<spring:message code="page.wrongPartsEdit.name22" />" mwidth="80" wunit="%" mheight="150"
                                  hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%"><spring:message code="page.wrongPartsEdit.name23"/>：</td>
                    <td style="width: 36%;" colspan="3">
						<textarea id="involvedCar" name="involvedCar" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:98%;;height:150px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000"
                                  minlen="0" allowinput="true"
                                  emptytext="<spring:message code="page.wrongPartsEdit.name24" />" mwidth="80" wunit="%" mheight="150"
                                  hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%"><spring:message code="page.wrongPartsEdit.name25"/>：</td>
                    <td style="width: 36%;" colspan="3">
						<textarea id="rectificationPlan" name="rectificationPlan" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:98%;;height:150px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000"
                                  minlen="0" allowinput="true"
                                  emptytext="<spring:message code="page.wrongPartsEdit.name26" />" mwidth="80" wunit="%" mheight="150"
                                  hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">预计完成时间：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="promiseTime" name="promiseTime" class="mini-datepicker" format="yyyy-MM-dd"
                               showTime="false" showOkButton="false" showClearButton="false" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">业务规则整改措施：</td>
                    <td colspan="3">
						<textarea id="newRectificationMeasures" name="newRectificationMeasures" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:98%;height:150px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000"
                                  minlen="0" allowinput="true"
                                  emptytext="<spring:message code="page.wrongPartsEdit.name28" />" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%"><spring:message code="page.wrongPartsEdit.name29"/>：</td>
                    <td colspan="3">
						<textarea id="riskWarning" name="riskWarning" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:98%;height:150px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000"
                                  minlen="0" allowinput="true"
                                  emptytext="<spring:message code="page.wrongPartsEdit.name30" />" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%"><spring:message code="page.wrongPartsEdit.name31"/>：</td>
                    <td colspan="3">
						<textarea id="XGSSRectificationPlan" name="XGSSRectificationPlan" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:98%;height:150px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000"
                                  minlen="0" allowinput="true"
                                  emptytext="<spring:message code="page.wrongPartsEdit.name32" />" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var jsUseCtxPath = "${ctxPath}";
    var fileListGrid = mini.get("fileListGrid");
    var formCjzg = new mini.Form("#formCjzg");
    var cjzgId = "${cjzgId}";
    var nodeVarsStr = '${nodeVars}';
    var currentUserName = "${currentUserName}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentTime = "${currentTime}";
    var taskStatus = "${taskStatus}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
    var codeName = "";
    var fwgcsId = "${fwgcsId}";//后台传的服务工程所id，用来做不同部门必输项的差异处理
</script>
</body>
</html>
