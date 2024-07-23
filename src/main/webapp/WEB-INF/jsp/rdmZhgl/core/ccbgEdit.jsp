<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>出差报告编辑</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/ccbgEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="ccbgProcessInfo()"><spring:message code="page.ccbgEdit.name" /></a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()"><spring:message code="page.ccbgEdit.name1" /></a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 80%;">
        <form id="formCcbg" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="reviewerUserId" name="reviewerUserId" class="mini-hidden"/>
            <input id="approverUserId" name="approverUserId" class="mini-hidden"/>
            <input id="businessStatus" name="businessStatus" class="mini-hidden"/>
            <input id="INST_ID_" name="INST_ID_" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold">
                    <spring:message code="page.ccbgEdit.name2" />
                </caption>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.ccbgEdit.name3" />：</td>
                    <td style="min-width:170px">
                        <input id="editorUserId" name="editorUserId" textname="editorUserName"
                               class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
                               label="<spring:message code="page.ccbgEdit.name3" />" length="50" mainfield="no" single="true" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.ccbgEdit.name4" />：</td>
                    <td style="min-width:170px">
                        <input id="editorUserDeptId" name="editorUserDeptId" class="mini-dep rxc" plugins="mini-dep"
                               data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                               style="width:98%;height:34px" allowinput="false" label="<spring:message code="page.ccbgEdit.name4" />" textname="editorUserDeptName" length="500"
                               maxlength="500" minlen="0" single="true" required="false" initlogindep="false" showclose="false"
                               mwidth="160" wunit="px" mheight="34" hunit="px" enabled="false"/>
                    </td>
                <tr>


                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.ccbgEdit.name5" />：</td>
                    <td style="min-width:170px">
                        <input id="level1" name="level1" class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=CCBGYJFL"
                               valueField="key" textField="value" showNullItem="true" onvaluechanged="onLevel1Changed"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.ccbgEdit.name6" />：</td>
                    <td style="min-width:170px">
                        <input id="level2" name="level2" class="mini-combobox" style="width:98%"
                               valueField="key" textField="value"/>
                    </td>
                <tr>


                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.ccbgEdit.name7" />：</td>
                    <td colspan="3">
                        <input id="remark" name="remark" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.ccbgEdit.name8" />：</td>
                    <td colspan="3">
                        <input id="memberUserIds" name="memberUserIds" textname="memberUserNames" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" label="<spring:message code="page.ccbgEdit.name8" />" length="1000" maxlength="1000" mainfield="no"
                               single="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.ccbgEdit.name9" />：</td>
                    <td style="min-width:170px">
                        <input id="beginTime" name="beginTime" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false" valueType="string"
                               showTime="false" showOkButton="false" showClearButton="true" style="width:98%;" onvaluechanged="getFormatTime"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.ccbgEdit.name10" />：</td>
                    <td style="min-width:170px">
                        <input id="endTime" name="endTime" class="mini-datepicker" format="yyyy-MM-dd" allowInput="false" valueType="string"
                               showTime="false" showOkButton="false" showClearButton="true" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.ccbgEdit.name11" />：</td>
                    <td style="min-width:170px">
                        <input id="year" name="year" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.ccbgEdit.name12" />：</td>
                    <td style="min-width:170px">
                        <input id="month" name="month" class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                <tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.ccbgEdit.name13" />：</td>
                    <td colspan="3">
						<textarea id="trip" name="trip" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:98%;height:150px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000"
                                  minlen="0" allowinput="true" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.ccbgEdit.name14" />：</td>
                    <td colspan="3">
						<textarea id="primaryCoverage" name="primaryCoverage" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:98%;height:150px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000"
                                  minlen="0" allowinput="true" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.ccbgEdit.name15" />：</td>
                    <td colspan="3">
						<textarea id="summaryAndProposal" name="summaryAndProposal" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:98%;height:150px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000"
                                  minlen="0" allowinput="true" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;height: 300px"><spring:message code="page.ccbgEdit.name16" />：</td>
                    <td colspan="3">
                        <div style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="addCcbgFile()"><spring:message code="page.ccbgEdit.name17" /></a>
                            <span style="color: red"><spring:message code="page.ccbgEdit.name18" /></span>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
                             idField="id" url="${ctxPath}/zhgl/core/ccbg/getCcbgFileList.do?ccbgId=${ccbgId}" autoload="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20"><spring:message code="page.ccbgEdit.name19" /></div>
                                <div field="fileName" align="center" headerAlign="center" width="150"><spring:message code="page.ccbgEdit.name20" /></div>
                                <div field="fileSize" align="center" headerAlign="center" width="60"><spring:message code="page.ccbgEdit.name21" /></div>
                                <div field="fileDesc" align="center" headerAlign="center" width="100"><spring:message code="page.ccbgEdit.name22" /></div>
                                <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer"><spring:message code="page.ccbgEdit.name23" /></div>
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
    var action = "${action}";
    var status = "${status}";
    var jsUseCtxPath = "${ctxPath}";
    var fileListGrid = mini.get("fileListGrid");
    var formCcbg = new mini.Form("#formCcbg");
    var ccbgId = "${ccbgId}";
    var nodeVarsStr = '${nodeVars}';
    var currentTime = "${currentTime}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentUserMainGroupId = "${currentUserMainGroupId}";
    var currentUserMainGroupName = "${currentUserMainGroupName}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;

    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnCcbgPreviewSpan(record.fileName, record.id, record.reportId, coverContent);
        //增加删除按钮
        if (action == 'edit' || (action == 'task' && isBianzhi == 'yes')) {
            var deleteUrl = "/zhgl/core/ccbg/delCcbgFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + ccbgEdit_name + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.reportId + '\',\'' + deleteUrl + '\')">' + ccbgEdit_name + '</span>';
        }
        return cellHtml;
    }
    //自动填写年份月份
    function getFormatTime() {
        var currentDate = mini.get("beginTime").text;
        var year = mini.get("year");
        var month = mini.get("month");
        year.setValue(currentDate.substring(0, 4));
        month.setValue(currentDate.substring(5, 7));
    }

    function onLevel1Changed() {
        var level1 = mini.get("level1").getValue();
        var url = jsUseCtxPath + "/sys/core/sysDic/getByDicKey.do?dicKey=CCBGEJFL";
        var level2s = [];
        $.ajax({
            url: url,
            method: 'get',
            success: function (array) {
                level2s.clear();
                debugger;
                for (var i = 0, l = array.length; i < l; i++) {
                    if (array[i].key.split('-')[0] == level1) {
                        level2s.push({
                            key: array[i].key.split('-')[1],
                            value: array[i].key.split('-')[1]
                        })
                    }
                }
                mini.get("level2").setData(level2s);
            }
        });


        //positionCombo.select(0);
    }
</script>
</body>
</html>
