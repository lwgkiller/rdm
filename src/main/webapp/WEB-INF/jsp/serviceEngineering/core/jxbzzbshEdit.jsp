<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html >
<html>
<head>
    <title  >检修标准值表表单</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/serviceEngineering/jxbzzbshEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="jxbzzbshProcessInfo()"><spring:message code="page.jxbzzbshEdit.name" /></a>
        <a id="saveOldData" class="mini-button" style="display: none" onclick="saveOldData()"><spring:message code="page.jxbzzbshEdit.name1" /></a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()"><spring:message code="page.jxbzzbshEdit.name2" /></a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;height:98%;padding: 0">
        <form id="jxbzzbshForm" method="post" style="height: 95%;width: 100%">
            <input class="mini-hidden" id="id" name="id"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="oldId" name="oldId" class="mini-hidden"/>
            <input id="autoProcess" name="autoProcess" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <input id="step" name="step" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption id="nochange" style="font-weight: bold"><spring:message code="page.jxbzzbshEdit.name3" /></caption>
                <caption id="change" style="display: none;font-weight: bold"><spring:message code="page.jxbzzbshEdit.name4" /></caption>
                <tr>
                    <td style="text-align: center;width: 14%">
                        <image src="${ctxPath}/styles/images/warn.png"
                               style="cursor:pointer;vertical-align: middle;width: 18px;height: 18px;"
                               title="<spring:message code="page.jxbzzbshEdit.name5" />"
                        />
                        <spring:message code="page.jxbzzbshEdit.name6" />：
                    </td>
                    <td style="width: 36%;min-width:170px">
                        <input id="shipmentnotmadeId" style="width:98%;" class="mini-buttonedit" onbuttonclick="onButtonEdit" name="shipmentnotmadeId"
                               textName="materialCode" allowInput="false"/>
                    </td>
                    <td style="text-align: center;width: 14%"><spring:message code="page.jxbzzbshEdit.name7" />：</td>
                    <td style="width: 33%;min-width:170px">
                        <input id="productDepartmentId" name="productDepartmentId" class="mini-dep rxc" plugins="mini-dep"
                               style="width:98%;height:34px"
                               allowinput="false" textname="productDepartment" length="200" maxlength="200" minlen="0" single="true"
                               initlogindep="false"/>
                    </td>
                </tr>
                <tr>

                    <td style="text-align: center;width: 14%"><spring:message code="page.jxbzzbshEdit.name8" />：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="productType" name="productType" class="mini-combobox" style="width:98%"
                               textField="value" valueField="key" emptyText="<spring:message code="page.jxbzzbshEdit.name9" />..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.jxbzzbshEdit.name9" />..."
                               data="[{key:'lunWa',value:'轮挖'},{key:'lvWa',value:'履挖'},{key:'teWa',value:'特挖'},{key:'dianWa',value:'电挖'}]"
                        />
                    </td>
                    <td style="text-align: center;width: 14%"><spring:message code="page.jxbzzbshEdit.name10" />：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="salesModel" name="salesModel" class="mini-textbox" style="width:98%;"/>
                    </td>

                </tr>
                <tr>
                    <td style="text-align: center;width: 14%"><spring:message code="page.jxbzzbshEdit.name11" />：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="versionType" name="versionType" class="mini-combobox" style="width:98%" onvaluechanged="versionTypeChange()"
                               textField="value" valueField="key" emptyText="<spring:message code="page.jxbzzbshEdit.name9" />..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.jxbzzbshEdit.name9" />..."
                               data="[{key:'cgb',value:'常规版'},{key:'csb',value:'测试版'},{key:'wzb',value:'完整版'}]"
                        />
                    </td>
                    <td style="text-align: center;width: 14%"><spring:message code="page.jxbzzbshEdit.name12" />：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="applicationNumber" name="applicationNumber" readonly class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 14%"><spring:message code="page.jxbzzbshEdit.name13" />：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="versionNum" name="versionNum" readonly class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 14%"><spring:message code="page.jxbzzbshEdit.name14" />：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="note" name="note" readonly class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 14%">适用第四位PIN码：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="pinFour" name="pinFour"  class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 14%">手册编码：</td>
                    <td style="width: 36%;min-width:170px">
                        <input id="gssNum" name="gssNum"  class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr id="testChangeTr" style="display: none">
                    <td style="text-align: center;width: 14%">测试版是否变更：</td>
                    <td >
                        <input id="testChange" name="testChange" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key" emptyText="请选择..." onvaluechanged="istestChange()"
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[ {'key' : '是','value' : '是'},{'key' : '否','value' : '否'}]"
                        />
                    </td>
                <tr>
                <tr id="changeTr" style="display: none">
                    <td style="text-align: center;width: 14%"><spring:message code="page.jxbzzbshEdit.name15" />：</td>
                    <td colspan="3">
                        <input id="changeReason" name="changeReason"  class="mini-textbox" style="width:98%;"/>
                    </td>
                <tr>
                    <td style="text-align: center;width: 14%;height: 300px"><spring:message code="page.jxbzzbshEdit.name16" />：</td>
                    <td colspan="3">
                        <div style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="addJxbzzbshFile()"><spring:message code="page.jxbzzbshEdit.name17" /></a>
                            <span style="color: red"><spring:message code="page.jxbzzbshEdit.name18" /></span>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
                             idField="id" url="${ctxPath}/serviceEngineering/core/jxbzzbsh/queryJxbzzbshFileList.do?jxbzzbshId=${jxbzzbshId}"
                             autoload="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20"><spring:message code="page.jxbzzbshEdit.name19" /></div>
                                <div field="fileName" width="140" headerAlign="center" align="center"><spring:message code="page.jxbzzbshEdit.name20" /></div>
                                <div field="versionNum" width="80" headerAlign="center" align="center">版本号</div>
                                <div field="fileVersionType" width="80" headerAlign="center" align="center">版本类型</div>
                                <div field="fileSize" width="80" headerAlign="center" align="center"><spring:message code="page.jxbzzbshEdit.name21" /></div>
                                <div field="fileLanguage" width="80" headerAlign="center" align="center">语言</div>
                                <div field="creator" width="100" headerAlign="center" align="center"><spring:message code="page.jxbzzbshEdit.name22" /></div>
                                <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center"><spring:message code="page.jxbzzbshEdit.name23" /></div>
                                <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer"><spring:message code="page.jxbzzbshEdit.name24" /></div>
                            </div>
                        </div>
                    </td>
                </tr>
                <%--<tr>--%>
                <%--<td style="text-align: center;width: 14%">发放部门：</td>--%>
                <%--<td  style="width: 33%;min-width:170px">--%>
                <%--<input id="distributionDepartmentId" name="distributionDepartmentId" class="mini-dep rxc" plugins="mini-dep"--%>
                <%--style="width:98%;height:34px"--%>
                <%--allowinput="false" textname="distributionDepartment" length="200" maxlength="200" minlen="0" single="false" initlogindep="false"/>--%>
                <%--</td>--%>
                <%--</tr>--%>
            </table>
        </form>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var jxbzzbshForm = new mini.Form("#jxbzzbshForm");
    var jxbzzbshId = "${jxbzzbshId}";
    var action = "${action}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var mainGroupId = "${mainGroupId}";
    var fileListGrid = mini.get("fileListGrid");
    var nodeVarsStr = '${nodeVars}';
    var wdzy = "${wdzy}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
    var importWindow = mini.get("importWindow");
    var matObj =${matObj};
    var changeType ="${change}";
    var oldId ="${oldId}";
    var isFWGCS = ${isFWGCS};
    //所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }

    function versionTypeChange() {
        var  versionType = mini.get("versionType").getValue();
        if ( versionType == 'wzb') {
            $("#testChangeTr").show();
        } else {
            $("#testChangeTr").hide();
        }
    }
    function istestChange() {
        var  testChange = mini.get("testChange").getValue();
        if ( testChange == '是') {
            $("#changeTr").show();
        } else {
            $("#changeTr").hide();
        }
    }
</script>
</body>
</html>
