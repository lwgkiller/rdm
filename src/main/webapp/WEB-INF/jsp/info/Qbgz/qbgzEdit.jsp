<%--
  Created by IntelliJ IDEA.
  User: matianyu
  Date: 2021/2/23
  Time: 14:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>情报报告</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/qbgz/qbgzEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
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
<div id="changeToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="saveChange" class="mini-button" onclick="saveChange()"><spring:message code="page.qbgzEdit.name" /></a>
        <a class="mini-button" onclick="CloseWindow()"><spring:message code="page.qbgzEdit.name1" /></a>
    </div>
</div>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()"><spring:message code="page.qbgzEdit.name2" /></a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()"><spring:message code="page.qbgzEdit.name1" /></a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 70%;">
        <form id="formQbgz" method="post">
            <input id="qbgzId" name="qbgzId" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 10%"><spring:message code="page.qbgzEdit.name3" /></td>
                    <td>
                        <input id="qbNum" name="qbNum" class="mini-textbox" readonly style="width:98%"/>
                    </td>
                    <td>
                        <span style="color: red">(<spring:message code="page.qbgzEdit.name4" />)</span>
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%"><spring:message code="page.qbgzEdit.name5" />：</td>
                    <td colspan="3">
                        <input id="companyName" name="companyName" class="mini-combobox" style="width:98%;"
                               textField="text"
                               valueField="id" emptyText="<spring:message code="page.qbgzEdit.name6" />..."
                               data="[{id:'三一',text:'三一'},{id:'卡特',text:'卡特'},{id:'小松',text:'小松'},
                         {id:'柳工',text:'柳工'},{id:'临工',text:'临工'},{id:'其他',text:'其他'}]"
                               required="true" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.qbgzEdit.name6" />..."/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%;text-align: left"><spring:message code="page.qbgzEdit.name7" />：</td>
                    <td colspan="3">
                        <input id="projectName" name="projectName" class="mini-combobox" style="width:98%;"
                               textField="text"
                               valueField="id" emptyText="<spring:message code="page.qbgzEdit.name6" />..."
                               data="[{id:'成本价格',text:'成本价格'},{id:'产品技术',text:'产品技术'},{id:'研发体系',text:'研发体系'},
                         {id:'产权创新',text:'产权创新'}]"
                               required="true" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.qbgzEdit.name6" />..."
                               onvaluechanged="valuechanged()"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: left"><spring:message code="page.qbgzEdit.name8" /></td>
                    <td colspan="3">
                        <input id="qbgzType" name="qbgzType" class="mini-combobox" style="width:98%" textField="text"
                               valueField="id" emptyText="<spring:message code="page.qbgzEdit.name6" />..."
                               required="true" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.qbgzEdit.name6" />..."/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: left"><spring:message code="page.qbgzEdit.name9" /></td>
                    <td colspan="3">
                        <input id="qbLevel" name="qbLevel" class="mini-combobox" style="width:98%;"
                               textField="text"
                               valueField="id" emptyText="<spring:message code="page.qbgzEdit.name6" />..."
                               data="[{id:'1',text:'1'},{id:'2',text:'2'},{id:'3',text:'3'},
                         {id:'4',text:'4'},{id:'5',text:'5'}]"
                               required="true" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.qbgzEdit.name6" />..."/>
                    </td>
                </tr>
                <tr>
                    <td rowspan="4"><spring:message code="page.qbgzEdit.name10" />：</td>
                    <td style="width: 10%"><spring:message code="page.qbgzEdit.name11" />：</td>
                    <td>
                        <input id="qbName" name="qbName" class="mini-textbox" style="width:98%"/>
                    </td>
                </tr>
                <tr>
                <td style="width: 10%"><spring:message code="page.qbgzEdit.name12" />：</td>
                <td colspan="3">
                    <input id="qbContent" name="qbContent" class="mini-textarea" style="width:98%;height: 200px"/>
                </td>
                </tr>
                <td style="width: 7%"><spring:message code="page.qbgzEdit.name13" />：</td>
                <td colspan="3">
                    <input id="qbComment" name="qbComment" class="mini-textarea" style="width:98%;height: 150px"/>
                </td>
                </tr>
                <tr>
                    <td style="width: 14%;height: 200px"><spring:message code="page.qbgzEdit.name14" />：</td>
                    <td colspan="2">
                        <div style="margin-top: 25px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="fileupload()"><spring:message code="page.qbgzEdit.name15" /></a>
                            <span style="color: red"><spring:message code="page.qbgzEdit.name16" /></span>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 100%"
                             allowResize="false"
                             idField="id" autoload="true"
                             url="${ctxPath}/Info/Qbgz/getQbgzFileList.do?qbgzId=${qbgzId}"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20"><spring:message code="page.qbgzEdit.name17" /></div>
                                <div field="fileName" width="140" headerAlign="center" align="center"><spring:message code="page.qbgzEdit.name18" /></div>
                                <div field="fileSize" width="80" headerAlign="center" align="center"><spring:message code="page.qbgzEdit.name19" /></div>
                                <div field="note" width="80" headerAlign="center" align="center"><spring:message code="page.qbgzEdit.name20" /></div>
                                <div field="action" width="100" headerAlign='center' align="center"
                                     renderer="operationRenderer"><spring:message code="page.qbgzEdit.name21" />
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
    var action = "${action}";
    var jsUseCtxPath = "${ctxPath}";
    var qbgzId = "${qbgzId}";
    var isLDR = ${isLDR};
    var isQbzy =${isQbzy};
    var formQbgz = new mini.Form("#formQbgz");
    var fileListGrid = mini.get("fileListGrid");
    var nodeVarsStr = '${nodeVars}';
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";


    function valuechanged() {
        var projectName = mini.get("projectName").getValue();
        if ("成本价格" == projectName) {
            mini.get("qbgzType").setData("[{id:'物料成本',text:'物料成本'},{id:'挖机售价',text:'挖机售价'},{id:'供应商情况',text:'供应商情况'}]");
        } else if ("产品技术" == projectName) {
            mini.get("qbgzType").setData("[{id:'新品信息',text:'新品信息'},{id:'新品计划',text:'新品计划'},{id:'创新规划',text:'创新规划'}]");
        } else if ("研发体系" == projectName) {
            mini.get("qbgzType").setData("[{id:'研发体系文件',text:'研发体系文件'},{id:'机构及人员信息',text:'机构及人员信息'}" +
                ",{id:'海外研发情况',text:'海外研发情况'},{id:'奖项申报',text:'奖项申报'}]");
        } else if ("产权创新" == projectName) {
            mini.get("qbgzType").setData("[{id:'专利',text:'专利'},{id:'论文',text:'论文'},{id:'标准',text:'标准'}]");
        }
    }

    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        if (isLDR || record.CREATE_BY_ == currentUserId || currentUserId == '1' || isQbzy) {
            cellHtml = returnQbgzPreviewSpan(record.fileName, record.fileId, record.belongId, coverContent);
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + qbgzEdit_name + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downLoadstandardChangeFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\')">' + qbgzEdit_name + '</span>';
        }

        //增加删除按钮
        if (action == 'edit' || (action == 'task' && isBianzhi == 'yes') || action == 'change') {
            var deleteUrl = "/Info/Qbgz/deleteQbgzFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + qbgzEdit_name1 + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.belongId + '\',\'' + deleteUrl + '\')">' + qbgzEdit_name1 + '</span>';
        }
        return cellHtml;
    }

</script>
</body>
</html>