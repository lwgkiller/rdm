<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>操保手册制作/变更申请</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()"><spring:message
                code="page.maintenanceManualMctEdit.name"/></a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()"><spring:message code="page.maintenanceManualMctEdit.name1"/></a>
        <a id="test" class="mini-button" onclick="showManualfile()"><spring:message code="page.maintenanceManualMctEdit.name2"/></a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 100%;">
        <form id="formBusiness" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="applyUserId" name="applyUserId" class="mini-hidden"/>
            <input id="applyDepId" name="applyDepId" class="mini-hidden"/>
            <input id="businessStatus" name="businessStatus" class="mini-hidden"/>
            <input id="BconfirmingId" name="BconfirmingId" class="mini-hidden"/>
            <input id="Bconfirming" name="Bconfirming" class="mini-hidden"/>
            <input id="demandId" name="demandId" class="mini-hidden"/>
            <input id="demandInstid" name="demandInstid" class="mini-hidden"/>
            <input id="INST_ID_" name="INST_ID_" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold">
                    <spring:message code="page.maintenanceManualMctEdit.name3"/>
                </caption>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualMctEdit.name4"/>：</td>
                    <td style="min-width:170px">
                        <input id="busunessNo" name="busunessNo" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualMctEdit.name5"/>：</td>
                    <td style="min-width:170px">
                        <input id="instructions" name="instructions" class="mini-combobox" style="width:98%;" enabled="true"
                               textField="value" valueField="key" emptyText="<spring:message code="page.maintenanceManualMctEdit.name6" />..."
                               allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.maintenanceManualMctEdit.name6" />..."
                               data="[{'key' : '新增','value' : '新增'},
							   {'key' : '变更','value' : '变更'}]"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualMctEdit.name7"/>：</td>
                    <td style="min-width:170px">
                        <input id="salesModel" name="salesModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualMctEdit.name8"/>：</td>
                    <td style="min-width:170px">
                        <input id="designModel" name="designModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualMctEdit.name9"/>：</td>
                    <td style="min-width:170px">
                        <input id="materialCode" name="materialCode" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualMctEdit.name10"/>：</td>
                    <td style="min-width:170px">
                        <input id="salesArea" name="salesArea" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualMctEdit.name11"/>：</td>
                    <td style="min-width:170px">
                        <input id="manualLanguage" name="manualLanguage" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=serviceEngineeringLanguage"
                               valueField="key" textField="value" multiSelect="true"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualMctEdit.name12"/>：</td>
                    <td style="min-width:170px">
                        <input id="isCE" name="isCE" class="mini-combobox" style="width:98%;" enabled="true"
                               textField="value" valueField="key" emptyText="<spring:message code="page.maintenanceManualMctEdit.name6" />..."
                               allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.maintenanceManualMctEdit.name6" />..."
                               data="[{'key' : '是','value' : '是'},
							   {'key' : '否','value' : '否'}]" onValuechanged="isCEChanged"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">是否需要自声明：</td>
                    <td style="min-width:170px">
                        <input id="isSelfDeclaration" name="isSelfDeclaration" class="mini-combobox" style="width:98%;" enabled="true"
                               textField="value" valueField="key" emptyText="<spring:message code="page.maintenanceManualMctEdit.name6" />..."
                               allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.maintenanceManualMctEdit.name6" />..."
                               data="[{'key' : '是','value' : '是'},{'key' : '否','value' : '否'}]" onValuechanged="isSelfDeclarationChanged"/>
                    </td>
                    <td style="text-align: center;width: 15%">EC自声明识别码：</td>
                    <td style="min-width:170px">
                        <input id="CEOnlyNum" name="CEOnlyNum" textName="CEOnlyNum" style="width:98%;"
                               class="mini-buttonedit" showClose="true" allowInput="false" enabled="false"
                               oncloseclick="selectCECloseClick()" onbuttonclick="selectCEClick()"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.maintenanceManualMctEdit.name13"/>：</td>
                    <td style="min-width:170px">
                        <input id="applyTime" name="applyTime" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                               valueType="string" showTime="false" showOkButton="false" showClearButton="true" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.maintenanceManualMctEdit.name14"/>：</td>
                    <td style="min-width:170px">
                        <input id="publishTime" name="publishTime" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                               valueType="string" showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.maintenanceManualMctEdit.name15"/>：</td>
                    <td style="min-width:170px">
                        <input id="estimateTime" name="estimateTime" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                               valueType="string" showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualMctEdit.name53"/>：</td>
                    <td style="min-width:170px">
                        <input id="emissionStandard" name="emissionStandard" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=PFBZ"
                               valueField="key" textField="value" multiSelect="false" onValuechanged="emissionStandardChanged"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.maintenanceManualMctEdit.name54"/>：</td>
                    <td style="min-width:170px">
                        <input id="isPIOEPDocArchived" name="isPIOEPDocArchived" class="mini-combobox" style="width:98%;" enabled="false"
                               textField="value" valueField="key" emptyText="<spring:message code="page.maintenanceManualMctEdit.name6" />..."
                               allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.maintenanceManualMctEdit.name6" />..."
                               data="[{'key' : '是','value' : '是'},{'key' : '否','value' : '否'},
							   {'key' : '无需认证报告','value' : '无需认证报告'}]"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.maintenanceManualMctEdit.name55"/>：</td>
                    <td style="min-width:170px">
                        <input id="isCertifiReptArchived" name="isCertifiReptArchived" class="mini-combobox" style="width:98%;" enabled="false"
                               textField="value" valueField="key" emptyText="<spring:message code="page.maintenanceManualMctEdit.name6" />..."
                               allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.maintenanceManualMctEdit.name6" />..."
                               data="[{'key' : '是','value' : '是'},
							   {'key' : '否','value' : '否'}]"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualMctEdit.name16"/>：</td>
                    <td style="min-width:170px">
                        <input id="applyUser" name="applyUser" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualMctEdit.name17"/>：</td>
                    <td style="min-width:170px">
                        <input id="applyDep" name="applyDep" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualMctEdit.name18"/>：</td>
                    <td style="min-width:170px">
                        <input id="demandBusunessNo" name="demandBusunessNo" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%"><spring:message code="page.maintenanceManualMctEdit.name19"/>：</td>
                    <td style="min-width:170px">
                        <input id="demandListNo" name="demandListNo" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.maintenanceManualMctEdit.name20"/>：</td>
                    <td colspan="3">
						<textarea id="remark" name="remark" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:98%;height:150px;line-height:25px;" label="" datatype="varchar" length="640"
                                  vtype="length:640" minlen="0" allowinput="true" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.maintenanceManualMctEdit.name21"/>：</td>
                    <td colspan="3">
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downTemplate('新编')"><spring:message
                                code="page.maintenanceManualMctEdit.name22"/></a>
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downTemplate('变更')"><spring:message
                                code="page.maintenanceManualMctEdit.name23"/></a>
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downTemplate('翻译')"><spring:message
                                code="page.maintenanceManualMctEdit.name24"/></a>
                    </td>
                </tr>
                <%--///////////////////////////////////////////////////--%>
                <tr>
                    <td style="text-align: center;height: 300px"><spring:message code="page.maintenanceManualMctEdit.name25"/>：</td>
                    <td colspan="3">
                        <div class="mini-toolbar" id="manualMatchButtons">
                            <a id="addManualMatch" class="mini-button" style="margin-bottom: 5px;margin-top: 5px"
                               onclick="addManualMatch"><spring:message code="page.maintenanceManualMctEdit.name26"/></a>
                            <a id="delManualMatch" class="mini-button btn-red" style="margin-bottom: 5px;margin-top: 5px"
                               onclick="delManualMatch"><spring:message code="page.maintenanceManualMctEdit.name27"/></a>
                            <span style="color: red"><spring:message code="page.maintenanceManualMctEdit.name28"/></span>
                        </div>
                        <div id="manualMatchListGrid" class="mini-datagrid" allowResize="false" style="height:300px" autoload="true"
                             idField="id" allowCellEdit="false" allowCellSelect="true" allowSortColumn="false"
                             multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
                        <%--共用一个就行了--%>
                             url="${ctxPath}/serviceEngineering/core/maintenanceManualDemand/getManualMatchList.do?businessId=${businessId}">
                            <div property="columns">
                                <div type="checkcolumn" width="50"></div>
                                <div type="indexcolumn" headerAlign="center" width="50"><spring:message
                                        code="page.maintenanceManualMctEdit.name29"/></div>
                                <div field="REF_ID_" width="80" headerAlign="center" align="center" renderer="renderREF" allowSort="true">
                                    类型标记
                                </div>
                                <div field="salesModel" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                                    <spring:message code="page.maintenanceManualMctEdit.name7"/></div>
                                <div field="designModel" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                                    <spring:message code="page.maintenanceManualMctEdit.name8"/></div>
                                <div field="materialCode" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                                    <spring:message code="page.maintenanceManualMctEdit.name9"/>
                                </div>
                                <div field="manualDescription" width="150" headerAlign="center" align="center" renderer="render" allowSort="true">
                                    <spring:message code="page.maintenanceManualMctEdit.name30"/>
                                </div>
                                <div field="cpzgName" width="70" headerAlign="center" align="center" renderer="render" allowSort="true">
                                    <spring:message code="page.maintenanceManualMctEdit.name31"/></div>
                                <div field="manualLanguage" width="70" headerAlign="center" align="center" renderer="render" allowSort="true">
                                    <spring:message code="page.maintenanceManualMctEdit.name32"/>
                                </div>
                                <div field="manualCode" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                                    <spring:message code="page.maintenanceManualMctEdit.name33"/></div>
                                <div field="manualVersion" width="70" headerAlign="center" align="center" renderer="render" allowSort="true">
                                    <spring:message code="page.maintenanceManualMctEdit.name34"/>
                                </div>
                                <div field="manualEdition" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">
                                    <spring:message code="page.maintenanceManualMctEdit.name35"/>
                                </div>
                                <div field="isCE" width="70" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                                        code="page.maintenanceManualMctEdit.name36"/></div>
                                <div field="CEStatus" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">EC自声明状态</div>
                                <div field="keyUser" displayField="keyUser" width="70" headerAlign="center" align="center" renderer="render"
                                     allowSort="true"><spring:message code="page.maintenanceManualMctEdit.name37"/>
                                </div>
                                <div field="publishTime" width="100" headerAlign="center" align="center" dateFormat="yyyy-MM-dd" renderer="render"
                                     allowSort="true"><spring:message code="page.maintenanceManualMctEdit.name38"/>
                                </div>
                                <div field="manualStatus" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">
                                    <spring:message code="page.maintenanceManualMctEdit.name39"/>
                                </div>
                                <div cellCls="actionIcons" width="60" headerAlign="center" align="center" renderer="manualFileRenderer"
                                     cellStyle="padding:0;"><spring:message code="page.maintenanceManualMctEdit.name40"/>
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                <%--///////////////////////////////////////////////////--%>
                <tr>
                    <td style="text-align: center;height: 300px"><spring:message code="page.maintenanceManualMctEdit.name41"/>：</td>
                    <td colspan="3">
                        <div style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="addBusinessFile()"><spring:message
                                    code="page.maintenanceManualMctEdit.name42"/></a>
                            <span style="color: red"><spring:message code="page.maintenanceManualMctEdit.name43"/></span>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
                             idField="id"
                             url="${ctxPath}/serviceEngineering/core/maintenanceManualMct/getFileList.do?businessId=${businessId}"
                             autoload="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20"><spring:message code="page.maintenanceManualMctEdit.name29"/></div>
                                <div field="fileName" align="center" headerAlign="center" width="150"><spring:message
                                        code="page.maintenanceManualMctEdit.name44"/></div>
                                <div field="fileSize" align="center" headerAlign="center" width="60"><spring:message
                                        code="page.maintenanceManualMctEdit.name45"/></div>
                                <div field="fileDesc" align="center" headerAlign="center" width="100"><spring:message
                                        code="page.maintenanceManualMctEdit.name46"/></div>
                                <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer"><spring:message
                                        code="page.maintenanceManualMctEdit.name40"/></div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<%--操保手册弹出窗口--%>
<div id="selectManualFileWindow" title="<spring:message code="page.maintenanceManualMctEdit.name47" />" class="mini-window"
     style="width:1450px;height:700px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px" borderStyle="border-left:0;border-top:0;border-right:0;">
        <li style="margin-right: 15px">
            <span class="text" style="width:auto"><spring:message code="page.maintenanceManualMctEdit.name7"/>：</span>
            <input class="mini-textbox" id="salesModelSub" name="salesModelSub" style="width: 120px"/>
            <span class="text" style="width:auto"><spring:message code="page.maintenanceManualMctEdit.name8"/>：</span>
            <input class="mini-textbox" id="designModelSub" name="designModelSub" style="width: 120px"/>
            <span class="text" style="width:auto"><spring:message code="page.maintenanceManualMctEdit.name48"/>：</span>
            <input class="mini-textbox" id="materialCodeSub" name="materialCodeSub" style="width: 120px"/>
            <span class="text" style="width:auto"><spring:message code="page.maintenanceManualMctEdit.name30"/>：</span>
            <input class="mini-textbox" id="manualDescriptionSub" name="manualDescriptionSub" style="width: 120px"/>
            <span class="text" style="width:auto"><spring:message code="page.maintenanceManualMctEdit.name32"/>：</span>
            <input class="mini-textbox" id="manualLanguageSub" name="manualLanguageSub" style="width: 120px"/>
            <span class="text" style="width:auto"><spring:message code="page.maintenanceManualMctEdit.name33"/>：</span>
            <input class="mini-textbox" id="manualCodeSub" name="manualCodeSub" style="width: 120px"/>
            <span class="text" style="width:auto"><spring:message code="page.maintenanceManualMctEdit.name49"/>：</span>
            <input class="mini-textbox" id="isCESub" name="isCESub" style="width: 120px"/>
        </li>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchManualFile()"><spring:message
                code="page.maintenanceManualMctEdit.name52"/></a>
    </div>
    <div class="mini-fit">
        <div id="manualFileListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" multiSelect="true"
             allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/serviceEngineering/core/maintenanceManualfile/dataListQuery.do">
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div field="salesModel" width="120" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.maintenanceManualMctEdit.name7"/></div>
                <div field="designModel" width="120" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.maintenanceManualMctEdit.name8"/></div>
                <div field="materialCode" width="120" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.maintenanceManualMctEdit.name9"/></div>
                <div field="manualDescription" width="400" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.maintenanceManualMctEdit.name30"/></div>
                <div field="cpzgName" width="80" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.maintenanceManualMctEdit.name31"/></div>
                <div field="manualLanguage" width="80" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.maintenanceManualMctEdit.name32"/></div>
                <div field="manualCode" width="140" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.maintenanceManualMctEdit.name33"/></div>
                <div field="manualVersion" width="80" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.maintenanceManualMctEdit.name34"/></div>
                <div field="manualEdition" width="80" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.maintenanceManualMctEdit.name35"/></div>
                <div field="isCE" width="80" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.maintenanceManualMctEdit.name36"/></div>
                <div field="CEStatus" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">EC自声明状态</div>
                <div field="manualStatus" width="80" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.maintenanceManualMctEdit.name39"/></div>
                <div field="publishTime" width="110" headerAlign="center" align="center" dateFormat="yyyy-MM-dd" renderer="render" allowSort="true">
                    <spring:message code="page.maintenanceManualMctEdit.name38"/>
                </div>
                <div field="remark" width="300" headerAlign="center" align="center" renderer="render" allowSort="true"><spring:message
                        code="page.maintenanceManualfileList.name33"/></div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.maintenanceManualMctEdit.name50" />"
                           onclick="selectFileOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.maintenanceManualMctEdit.name51" />"
                           onclick="selectFileHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>
<%--EC自声明弹出窗口--%>
<div id="selectCEWindow" title="选择EC自声明" class="mini-window"
     style="width:1450px;height:700px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px" borderStyle="border-left:0;border-top:0;border-right:0;">
        <span style="font-size: 14px;color: #777">销售型号: </span>
        <input class="mini-textbox" width="130" id="saleModel" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">配置信息识别码: </span>
        <input class="mini-textbox" width="130" id="onlyNum" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">噪声指令证书编号: </span>
        <input class="mini-textbox" width="130" id="zsNum" style="margin-right: 15px"/>
        <a class="mini-button" plain="true" onclick="searchCE()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="CEListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" multiSelect="true"
             allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/wwrz/core/CE/queryInfoList.do">
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div field="onlyNum" width="30" headerAlign="center" align="center" allowSort="true">配置信息识别码</div>
                <div field="saleModel" width="30" headerAlign="center" align="center" allowSort="true">销售型号</div>
                <div field="designModel" width="30" dateFormat="yyyy-MM-dd" headerAlign='center' align="center">设计型号</div>
                <div field="materialNum" width="30" headerAlign="center" align="center" allowSort="true">物料号</div>
                <div field="emission" width="30" headerAlign="center" align="center" allowSort="true">排放标准</div>
                <div field="zsNum" width="30" headerAlign="center" align="center" allowSort="true">噪声指令证书编号</div>
                <div field="zsStartDate" width="30" align="center" headerAlign="center" allowSort="true" dateFormat="yyyy-MM-dd">证书签发日期</div>
                <div field="zsEndDate" width="30" align="center" headerAlign="center" allowSort="true" dateFormat="yyyy-MM-dd">证书有效期</div>
                <div field="noteStatus" headerAlign='center' align='center' width="30" renderer="onValidStatus">台账状态</div>
                <div field="userName" headerAlign='center' align='center' width="30">创建人</div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectCEOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectCEHide()"/>
                    <input type="button" style="height: 25px;width: 70px;color: red" value="暂无" onclick="zanwuCE()"/>
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
    var selectCEWindow = mini.get("selectCEWindow");
    var CEListGrid = mini.get("CEListGrid");
    var businessId = "${businessId}";
    var demandId = "${demandId}";
    var nodeVarsStr = '${nodeVars}';
    var currentTime = "${currentTime}";
    var applyTime = "${applyTime}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
    var isBianzhi = "";
    var isZhixing = "";
    var isTranslation = "";
    //..
    $(function () {
        //通过需求申请单自动打开，自动填写相应字段
        if (demandId && demandId != '') {
            var url = jsUseCtxPath + "/serviceEngineering/core/maintenanceManualDemand/getDetail.do";
            $.post(
                url,
                {businessId: demandId},
                function (json) {
                    mini.get("BconfirmingId").setValue(json.BconfirmingId);
                    mini.get("Bconfirming").setValue(json.Bconfirming);
                    mini.get("demandId").setValue(json.id);//其实就是{businessId: demandId}的demandId
                    mini.get("demandInstid").setValue(json.INST_ID_);
                    mini.get("instructions").setValue(json.instructions);
                    mini.get("instructions").setEnabled(false);
                    mini.get("salesModel").setValue(json.salesModel);
                    mini.get("salesModel").setEnabled(false);
                    mini.get("designModel").setValue(json.designModel);
                    mini.get("designModel").setEnabled(false);
                    mini.get("materialCode").setValue(json.materialCode);
                    mini.get("materialCode").setEnabled(false);
                    mini.get("salesArea").setValue(json.salesArea);
                    mini.get("salesArea").setEnabled(false);
                    mini.get("manualLanguage").setValue(json.manualLanguage);
                    mini.get("manualLanguage").setEnabled(false);
                    mini.get("isCE").setValue(json.isCE);
                    mini.get("isCE").setEnabled(false);
                    mini.get("isSelfDeclaration").setValue(json.isSelfDeclaration);
                    mini.get("CEOnlyNum").setValue(json.CEOnlyNum);
                    mini.get("CEOnlyNum").setText(json.CEOnlyNum);
                    mini.get("applyTime").setValue(json.applyTime);
                    mini.get("applyTime").setEnabled(false);
                    mini.get("publishTime").setValue(json.publishTime);
                    mini.get("publishTime").setEnabled(false);
                    mini.get("demandBusunessNo").setValue(json.busunessNo);
                    mini.get("demandListNo").setValue(json.demandListNo);
                    if (json.isCE == "是") {
                        mini.get("isCertifiReptArchived").setEnabled(true);
                    }
                    if (json.isSelfDeclaration == "是") {
                        mini.get("CEOnlyNum").setEnabled(true);
                    }
                    //获取明细
                    $.post(
                        jsUseCtxPath + "/serviceEngineering/core/maintenanceManualDemand/getManualMatchList.do",
                        {businessId: demandId},
                        function (jsons) {
                            for (var index = 0; index < jsons.length; index++) {
                                var row = jsons[index];
                                row.id = '';
                                row.businessId = '';
                                manualMatchListGrid.addRow(row);
                            }
                        });
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
        } else {//正常打开
            var url = jsUseCtxPath + "/serviceEngineering/core/maintenanceManualMct/getDetail.do";
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
                    } else if (action == 'edit') {//正常没有，草稿状态也得限制才需要
                        if (mini.get("isCE").getValue() == "是") {
                            mini.get("isCertifiReptArchived").setEnabled(true);
                        }
                        if (mini.get("emissionStandard").getValue() == "国四") {
                            mini.get("isPIOEPDocArchived").setEnabled(true);
                        }
                        if (mini.get("isSelfDeclaration").getValue() == "是") {
                            mini.get("CEOnlyNum").setEnabled(true);
                        }
                    }
                    //正常打开的是通过需求申请单自动生成的单据
                    if (json.demandId && json.demandId != '') {
                        mini.get("instructions").setEnabled(false);
                        mini.get("salesModel").setEnabled(false);
                        mini.get("designModel").setEnabled(false);
                        mini.get("materialCode").setEnabled(false);
                        mini.get("salesArea").setEnabled(false);
                        mini.get("manualLanguage").setEnabled(false);
                        mini.get("isCE").setEnabled(false);
                        mini.get("applyTime").setEnabled(false);
                        mini.get("publishTime").setEnabled(false);
                    }
                });
        }
    });
    //..
    function isCEChanged(e) {
        var isCE = e.value;
        if (isCE == "是") {
            mini.get("isCertifiReptArchived").setEnabled(true);
        } else {
            mini.get("isCertifiReptArchived").setEnabled(false);
        }
    }
    //..
    function emissionStandardChanged(e) {
        var emissionStandard = e.value;
        if (emissionStandard == "国四") {
            mini.get("isPIOEPDocArchived").setEnabled(true);
        } else {
            mini.get("isPIOEPDocArchived").setEnabled(false);
        }
    }
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
//        mini.get("applyUserId").setValue(currentUserId);
        window.parent.saveDraft(e);
    }
    //..启动流程
    function startBusinessProcess(e) {
        //@mh 流程启动前线赋值申请时间
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
            url: jsUseCtxPath + '/serviceEngineering/core/maintenanceManualMct/saveBusiness.do',
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message = "";
                    if (data.success) {
                        message = maintenanceManualMctEdit_name;
                    } else {
                        message = maintenanceManualMctEdit_name1 + data.message;
                    }

                    mini.alert(message, maintenanceManualMctEdit_name2, function () {
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
            //不是参考手册的才加入判断
            if (manualMatchListGrid.getData()[i].REF_ID_ != '1') {
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
        } else if (isZhixing == 'yes') {
            if (manualMatchListGrid.getChanges().length > 0) {
                mini.alert("请先点击暂存信息，进行图册匹配明细信息的保存！");
                return;
            }
            if (manualLanguagesMatch.length == 0) {
                mini.alert("图册匹配明细必须至少有一条精确匹配的记录！如果不需要翻译，则需要完全匹配需求语言数量！");
                return;
            }
            var manualMatchs = manualMatchListGrid.getData();
            for (var i = 0; i < manualMatchs.length; i++) {
                if (manualMatchs[i].REF_ID_ != '1' && manualMatchs[i].manualStatus != '可打印') {//不是参考手册的才加入判断
                    mini.alert("图册匹配明细状态必须是“可打印”!");
                    return;
                }
                if (manualMatchs[i].REF_ID_ != '1' &&//不是参考手册的才加入判断
                    ($.trim(manualMatchs[i].salesModel) != $.trim(mini.get("salesModel").getValue()) ||
                    $.trim(manualMatchs[i].designModel) != $.trim(mini.get("designModel").getValue()) ||
                    $.trim(manualMatchs[i].materialCode) != $.trim(mini.get("materialCode").getValue()))) {
                    mini.alert("图册匹配明细的销售型号设计型号和物料号必须和单据一致！");
                    return;
                }
                //EC自声明验证
                if (manualMatchs[i].REF_ID_ != '1' &&//不是参考手册的才加入判断
                    $.trim(mini.get("isSelfDeclaration").getValue()) == "是") {
                    if ($.trim(mini.get("CEOnlyNum").getValue()) == "暂无" && $.trim(manualMatchs[i].CEStatus) != "待更新") {
                        mini.alert("需要EC自声明且暂无的，图册匹配明细的EC自声明状态必须为 待更新！");
                        return;
                    } else if ($.trim(mini.get("CEOnlyNum").getValue()) != "暂无" && $.trim(manualMatchs[i].CEStatus) != "已具备") {
                        mini.alert("需要EC自声明且已经关联自声明编码的，图册匹配明细的EC自声明状态必须为 已具备！");
                        return;
                    }
                }
            }
            //检查通过
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
                if (manualMatchs[i].REF_ID_ != '1' && manualMatchs[i].manualStatus != '可打印') {//不是参考手册的才加入判断
                    mini.alert("图册匹配明细状态必须是“可打印”!");
                    return;
                }
                if (manualMatchs[i].REF_ID_ != '1' &&//不是参考手册的才加入判断
                    ($.trim(manualMatchs[i].salesModel) != $.trim(mini.get("salesModel").getValue()) ||
                    $.trim(manualMatchs[i].designModel) != $.trim(mini.get("designModel").getValue()) ||
                    $.trim(manualMatchs[i].materialCode) != $.trim(mini.get("materialCode").getValue()))) {
                    mini.alert("图册匹配明细的销售型号设计型号和物料号必须和单据一致！");
                    return;
                }
                //EC自声明验证
                if (manualMatchs[i].REF_ID_ != '1' &&//不是参考手册的才加入判断
                    $.trim(mini.get("isSelfDeclaration").getValue()) == "是") {
                    if ($.trim(mini.get("CEOnlyNum").getValue()) == "暂无" && $.trim(manualMatchs[i].CEStatus) != "待更新") {
                        mini.alert("需要EC自声明且暂无的，图册匹配明细的EC自声明状态必须为 待更新！");
                        return;
                    } else if ($.trim(mini.get("CEOnlyNum").getValue()) != "暂无" && $.trim(manualMatchs[i].CEStatus) != "已具备") {
                        mini.alert("需要EC自声明且已经关联自声明编码的，图册匹配明细的EC自声明状态必须为 已具备！");
                        return;
                    }
                }
            }
            //检查通过
            window.parent.approve();
        } else {
            window.parent.approve();
        }
    }
    //..添加文件
    function addBusinessFile() {
        var businessId = mini.get("id").getValue();
        if (!businessId) {
            mini.alert(maintenanceManualMctEdit_name8);
            return;
        }
        mini.open({
            title: maintenanceManualMctEdit_name9,
            url: jsUseCtxPath + "/serviceEngineering/core/maintenanceManualMct/fileUploadWindow.do?businessId=" + businessId,
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
            title: maintenanceManualMctEdit_name10,
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
            } else if (nodeVars[i].KEY_ == 'isTranslation') {
                isTranslation = nodeVars[i].DEF_VAL_;
            } else if (nodeVars[i].KEY_ == 'isZhixing') {
                isZhixing = nodeVars[i].DEF_VAL_;
            }
        }
        if (isBianzhi == 'yes') {
            //
            if (mini.get("isCE").getValue() == "是") {
                mini.get("isCertifiReptArchived").setEnabled(true);
            }
            if (mini.get("emissionStandard").getValue() == "国四") {
                mini.get("isPIOEPDocArchived").setEnabled(true);
            }
        } else {
            formBusiness.setEnabled(false);
            mini.get("estimateTime").setEnabled(true);
            mini.get("remark").setEnabled(true);
            mini.get("addFile").setEnabled(false);
        }
    }
    //..检验表单是否必填
    function validBusiness() {
        var instructions = $.trim(mini.get("instructions").getValue());
        if (!instructions) {
            return {"result": false, "message": maintenanceManualMctEdit_name11};
        }
        var salesModel = $.trim(mini.get("salesModel").getValue());
        if (!salesModel) {
            return {"result": false, "message": maintenanceManualMctEdit_name12};
        }
        var designModel = $.trim(mini.get("designModel").getValue());
        if (!designModel) {
            return {"result": false, "message": maintenanceManualMctEdit_name13};
        }
        var materialCode = $.trim(mini.get("materialCode").getValue());
        if (!materialCode) {
            return {"result": false, "message": maintenanceManualMctEdit_name14};
        }
        var salesArea = $.trim(mini.get("salesArea").getValue());
        if (!salesArea) {
            return {"result": false, "message": maintenanceManualMctEdit_name15};
        }
        var manualLanguage = $.trim(mini.get("manualLanguage").getValue());
        if (!manualLanguage) {
            return {"result": false, "message": maintenanceManualMctEdit_name16};
        }
        var isCE = $.trim(mini.get("isCE").getValue());
        if (!isCE) {
            return {"result": false, "message": maintenanceManualMctEdit_name17};
        }
        var applyTime = $.trim(mini.get("applyTime").getValue());
        if (!applyTime) {
            return {"result": false, "message": maintenanceManualMctEdit_name18};
        }
        var publishTime = $.trim(mini.get("publishTime").getValue());
        if (!publishTime) {
            return {"result": false, "message": maintenanceManualMctEdit_name19};
        }
        var emissionStandard = $.trim(mini.get("emissionStandard").getValue());
        if (!emissionStandard) {
            return {"result": false, "message": "请选择排放标准"};
        }
        var isPIOEPDocArchived = $.trim(mini.get("isPIOEPDocArchived").getValue());
        if (!isPIOEPDocArchived && emissionStandard == "国四") {
            return {"result": false, "message": "国四标准的必须选择环保公开信息是否已归档"};
        }
        var isCertifiReptArchived = $.trim(mini.get("isCertifiReptArchived").getValue());
        if (!isCertifiReptArchived && isCE == "是") {
            return {"result": false, "message": "是CE认证的必须选择认证报告证书是否已归档"};
        }
        var isSelfDeclaration = $.trim(mini.get("isSelfDeclaration").getValue());
        if (!isSelfDeclaration) {
            return {"result": false, "message": "是否需要自声明不能为空"};
        }
        var CEOnlyNum = $.trim(mini.get("CEOnlyNum").getValue());
        if (isSelfDeclaration == "是" && !CEOnlyNum) {
            return {"result": false, "message": "需要自声明时，必须选择EC自声明识别码，如暂时没有请在选择窗口点击 暂无 按钮"};
        }
        if (fileListGrid.totalCount == 0) {
            return {"result": false, "message": maintenanceManualMctEdit_name20};
        }
        if (manualMatchListGrid.getChanges().length > 0) {
            return {"result": false, "message": maintenanceManualMctEdit_name3};
        }
        if (manualMatchListGrid.totalCount != 1) {
            return {"result": false, "message": maintenanceManualMctEdit_name21};
        }
        return {"result": true};
    }
    //..文件列表操作渲染
    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnPreviewSpan(record.fileName, record.id, record.mainId, coverContent);
        //和pdf预览用的同一个url
        var downLoadUrl = '/serviceEngineering/core/maintenanceManualMct/PdfPreviewAndAllDownload.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + maintenanceManualMctEdit_name22 + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainId + '\',\'' + downLoadUrl + '\')">' + maintenanceManualMctEdit_name22 + '</span>';
        //增加删除按钮
        if (action == 'edit' || (action == 'task' && isBianzhi == 'yes')) {
            var deleteUrl = "/serviceEngineering/core/maintenanceManualMct/delFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + maintenanceManualMctEdit_name23 + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.mainId + '\',\'' + deleteUrl + '\')">' + maintenanceManualMctEdit_name23 + '</span>';
        }
        return cellHtml;
    }
    //..文件列表预览渲染
    function returnPreviewSpan(fileName, fileId, formId, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title=' + maintenanceManualMctEdit_name24 + ' style="color: silver" >' + maintenanceManualMctEdit_name24 + '</span>';
        } else if (fileType == 'pdf') {
            var url = '/serviceEngineering/core/maintenanceManualMct/PdfPreview.do';
            s = '<span  title=' + maintenanceManualMctEdit_name24 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + maintenanceManualMctEdit_name24 + '</span>';
        } else if (fileType == 'office') {
            var url = '/serviceEngineering/core/maintenanceManualMct/OfficePreview.do';
            s = '<span  title=' + maintenanceManualMctEdit_name24 + ' style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + maintenanceManualMctEdit_name24 + '</span>';
        } else if (fileType == 'pic') {
            var url = '/serviceEngineering/core/maintenanceManualMct/ImagePreview.do';
            s = '<span  title=' + maintenanceManualMctEdit_name24 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + maintenanceManualMctEdit_name24 + '</span>';
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
    function downTemplate(type) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/serviceEngineering/core/maintenanceManualMct/templateDownload.do?type=" + type);
        $("body").append(form);
        form.submit();
        form.remove();
    }
    //..
    function manualFileRenderer(e) {
        var record = e.record;
        var cellHtml = '<span  title=' + maintenanceManualMctEdit_name24 + ' style="color:#409EFF;cursor: pointer;" onclick="previewManualFile(\'' + record.manualFileId + '\',\'' + coverContent + '\')">' + maintenanceManualMctEdit_name24 + '</span>';
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
        } else {
            return "";
        }
    }
    //..以下EC自声明相关
    function searchCE() {
        var queryParam = [];
        var saleModel = $.trim(mini.get("saleModel").getValue());
        if (saleModel) {
            queryParam.push({name: "saleModel", value: projectName});
        }
        var onlyNum = $.trim(mini.get("onlyNum").getValue());
        if (onlyNum) {
            queryParam.push({name: "onlyNum", value: number});
        }
        var zsNum = $.trim(mini.get("zsNum").getValue());
        if (zsNum) {
            queryParam.push({name: "zsNum", value: respMan});
        }
        queryParam.push({name: "noteStatus", value: "0"});
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = CEListGrid.getPageIndex();
        data.pageSize = CEListGrid.getPageSize();
        data.sortField = CEListGrid.getSortField();
        data.sortOrder = CEListGrid.getSortOrder();
        //查询
        CEListGrid.load(data);
    }
    //..
    function selectCEClick() {
        selectCEWindow.show();
        searchCE();
    }
    //..
    function selectCECloseClick() {
        mini.get("saleModel").setValue("");
        mini.get("onlyNum").setValue("");
        mini.get("zsNum").setValue("");
    }
    //..
    function selectCEOK() {
        var selectRow = CEListGrid.getSelected();
        if (selectRow) {
            mini.get("CEOnlyNum").setValue(selectRow.onlyNum);
            mini.get("CEOnlyNum").setText(selectRow.onlyNum);
        } else {
            mini.alert("请选择一条数据！");
            return;
        }
        selectCEHide();
    }
    //..
    function zanwuCE() {
        mini.get("CEOnlyNum").setValue("暂无");
        mini.get("CEOnlyNum").setText("暂无");
        selectCEHide();
    }
    //..
    function selectCEHide() {
        selectCEWindow.hide();
        mini.get("saleModel").setValue("");
        mini.get("onlyNum").setValue("");
        mini.get("zsNum").setValue("");
    }
    //..
    function onValidStatus(e) {
        var record = e.record;
        var valid = record.noteStatus;
        var _html = '';
        var color = '';
        var text = '';
        if (valid == '0') {
            color = '#2edfa3';
            text = "有效";
        } else if (valid == '1') {
            color = 'red';
            text = "作废";
        }
        _html = '<span style="color: ' + color + '">' + text + '</span>'
        return _html;
    }
    //..
    function isSelfDeclarationChanged(e) {
        var isSelfDeclaration = e.value;
        if (isSelfDeclaration == "是") {
            mini.get("CEOnlyNum").setEnabled(true);
        } else {
            mini.get("CEOnlyNum").setEnabled(false);
        }
    }
</script>
</body>
</html>
