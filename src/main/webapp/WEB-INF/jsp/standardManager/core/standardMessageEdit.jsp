<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>发布宣贯</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
    <script src="${ctxPath}/scripts/standardManager/standardUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/standardManager/standardMessageEdit.js?version=${static_res_version}"
            type="text/javascript"></script>
    <style>
        html, body {
            overflow: hidden;
            height: 100%;
            width: 100%;
            padding: 0;
            margin: 0;
        }

    </style>
</head>
<body>
<div class="mini-panel" style="width: 100%; height: 100%" showfooter="false" bodystyle="padding:0"
     showheader="false">
    <div style="margin-top:5px;margin-right:10px;float: right">
        <a id="sendBtn" class="mini-button" iconCls="icon-ok" onclick="sendMsg()"><spring:message code="page.standardMessageEdit.name" /></a>
        <a class="mini-button" iconCls="icon-cancel" onclick="CloseWindow()"><spring:message code="page.standardMessageEdit.name1" /></a>
    </div>
    <div style="margin: 45px 10px 10px 10px;border:1px solid #d1d1d1;height: 84%">
        <table class="table-detail" cellspacing="1" cellpadding="0">
            <tr>
                <td style="text-align: left"><spring:message code="page.standardMessageEdit.name2" />：<span style="color: red">（<spring:message code="page.standardMessageEdit.name3" />）</span></td>
                <td id="recTd" colspan="3">
                    <input id="depSelectId" name="receiveDeptIds" class="mini-dep rxc" plugins="mini-dep"
                           style="width:98%;height:34px"
                           allowinput="false" textname="receiveDeptNames" single="false" initlogindep="false"/>
                </td>
            </tr>
            <tr>
                <td style="width: 10%;text-align: left"><spring:message code="page.standardMessageEdit.name4" />：<span style="color: #ff0000">*</span></td>
                <td style="width: 40%;min-width:170px">
                    <input id="standardSelectId" style="width:98%;" class="mini-buttonedit" name="standardId"
                           textname="standardName" allowInput="false" onbuttonclick="selectStandard()"/>
                </td>
            </tr>
            <tr>
                <input type="text" id="messageId" style="display: none" />
                <td style="width: 20%;text-align: left"><spring:message code="page.standardMessageEdit.name5" />：<span style="color: #ff0000">*</span></td>
                <td>
                    <input id="standardMsgTitle" class="mini-textbox" style="width:98%;"/>
                </td>
            </tr>
            <tr>
                <td style="text-align: left"><spring:message code="page.standardMessageEdit.name6" /></td>
                <td>
                    <textarea id="standardMsgContent" class="mini-textarea rxc" plugins="mini-textarea"
                              style="width:98%;height:250px;line-height:25px;" label="<spring:message code="page.standardMessageEdit.name7" />" datatype="varchar"
                              length="500" vtype="length:500" minlen="0" allowinput="true" emptytext="<spring:message code="page.standardMessageEdit.name8" />..."
                              mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                </td>
            </tr>
        </table>
    </div>

    <div id="selectStandardWindow" title="<spring:message code="page.standardMessageEdit.name9" />" class="mini-window" style="width:650px;height:450px;"
         showModal="true" showFooter="true" allowResize="true">
        <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
             borderStyle="border-left:0;border-top:0;border-right:0;">
            <span style="font-size: 14px;color: #777"><spring:message code="page.standardMessageEdit.name10" />: </span>
            <input id="standardSystemCategory" class="mini-combobox" style="width:150px;margin-right: 15px"
                   textField="systemCategoryName" valueField="systemCategoryId"
                   required="false" allowInput="false" />
            <span style="font-size: 14px;color: #777"><spring:message code="page.standardMessageEdit.name11" />: </span>
            <input class="mini-textbox" width="130" id="filterNumberId" style="margin-right: 15px"/>
            <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchStandard()"><spring:message code="page.standardMessageEdit.name12" /></a>
        </div>
        <div class="mini-fit">
            <div id="standardListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
                 idField="id" multiSelect="false" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20"
                 allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/standardManager/core/standard/queryList.do"
                 onrowdblclick="onRowDblClick()"
            >
                <div property="columns">
                    <div type="checkcolumn" width="30"></div>
                    <div field="systemName" sortField="systemName" width="120" headerAlign="center" align="center"
                         allowSort="true"><spring:message code="page.standardMessageEdit.name13" />
                    </div>
                    <div field="categoryName" sortField="categoryName" width="60" headerAlign="center" align="center"
                         allowSort="true"><spring:message code="page.standardMessageEdit.name14" />
                    </div>
                    <div field="standardNumber" sortField="standardNumber" width="120" headerAlign="center" align="center"
                         align="center" allowSort="true"><spring:message code="page.standardMessageEdit.name15" />
                    </div>
                    <div field="standardName" sortField="standardName" width="150" headerAlign="center" align="center"
                         allowSort="true"><spring:message code="page.standardMessageEdit.name16" />
                    </div>
                    <div field="belongDepName" sortField="belongDepName" width="80" headerAlign="center" align="center"
                         allowSort="true"><spring:message code="page.standardMessageEdit.name17" />
                    </div>
                    <div field="standardStatus" sortField="standardStatus" width="60" headerAlign="center" align="center"
                         allowSort="true" renderer="statusRenderer"><spring:message code="page.standardMessageEdit.name18" />
                    </div>
                </div>
            </div>
        </div>
        <div property="footer" style="padding:5px;height: 35px">
            <table style="width:100%;height: 100%">
                <tr>
                    <td style="width:120px;text-align:center;">
                        <input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.standardMessageEdit.name19" />" onclick="okWindow()"/>
                        <input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.standardMessageEdit.name20" />" onclick="hideWindow()"/>
                    </td>
                </tr>
            </table>
        </div>
    </div>

</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var messageObj =${messageObj};
    var standardObj=${standardObj};
    var canEditStandard=${canEditStandard};
    var scene="${scene}";
    setData();

    function setData() {
        if (messageObj) {
            $("#messageId").val(messageObj.id);
            mini.get("standardMsgTitle").setValue(messageObj.standardMsgTitle);
            mini.get("standardMsgContent").setValue(messageObj.standardMsgContent);
            mini.get("depSelectId").setValue(messageObj.receiveDeptIds);
            mini.get("depSelectId").setText(messageObj.receiveDeptNames);
        }
        if(standardObj && standardObj.id) {
            mini.get("standardSelectId").setValue(standardObj.id);
            mini.get("standardSelectId").setText(standardObj.standardName);
        }
        if(!canEditStandard) {
            mini.get("standardSelectId").setEnabled(false);
        }
    }

    var standardListGrid=mini.get("standardListGrid");
    var selectStandardWindow=mini.get("selectStandardWindow");

    function statusRenderer(e) {
        var record = e.record;
        var status = record.standardStatus;

        var arr = [{'key': 'draft', 'value': '草稿', 'css': 'orange'},
            {'key': 'enable', 'value': '有效', 'css': 'green'},
            {'key': 'disable', 'value': '已废止', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }
</script>
</body>
</html>
