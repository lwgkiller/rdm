<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>机型测试下发</title>
	<%@include file="/commons/edit.jsp"%>
	<link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
	<script src="${ctxPath}/scripts/serviceEngineering/jxcshcEdit.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
	<div>
		<a id="processInfo" class="mini-button" style="display: none" onclick="jxcshcProcessInfo()"><spring:message code="page.jxcshcEdit.name1" /></a>
		<a id="closeWindow" class="mini-button" onclick="CloseWindow()"><spring:message code="page.jxcshcEdit.name2" /></a>
	</div>
</div>
<div class="mini-fit">
	<div class="form-container" style="margin: 0 auto; width: 80%;">
		<form id="formJxcshc" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<input id="instId" name="instId" class="mini-hidden"/>
			<input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
			<input id="step" name="step" class="mini-hidden"/>
			<table class="table-detail grey"  cellspacing="1" cellpadding="0">
				<caption style="font-weight: bold">
					<spring:message code="page.jxcshcEdit.name3" />
				</caption>
				<tr>
					<td style="text-align: center;width: 14%"><spring:message code="page.jxcshcEdit.name4" />：</td>
					<td style="width: 36%;min-width:170px">
						<input id="versionType" name="versionType" class="mini-combobox" style="width:50%"
							   textField="value" valueField="key" emptyText="<spring:message code="page.jxcshcEdit.name5" />..."
							   required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.jxcshcEdit.name5" />..."
							   data="[{key:'cgb',value:'常规版'},{key:'wzb',value:'完整版'}]"
						/>
					</td>
				</tr>
				<tr>
					<td style="text-align: center;width: 14%;height: 300px"><spring:message code="page.jxcshcEdit.name6" />：</td>
					<td colspan="3">
						<div style="margin-top: 10px;margin-bottom: 2px">
							<a id="addFile" class="mini-button"  onclick="addJxcshcFile()"><spring:message code="page.jxcshcEdit.name7" /></a>
							<span style="color: red"><spring:message code="page.jxcshcEdit.name8" /></span>
						</div>
						<div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
							 idField="id" url="${ctxPath}/serviceEngineering/core/jxcshc/queryFileList.do?jxcshcId=${jxcshcId}" autoload="true"
							 multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true" >
							<div property="columns">
								<div type="indexcolumn" align="center"  width="20"><spring:message code="page.jxcshcEdit.name9" /></div>
								<div field="fileName"  width="140" headerAlign="center" align="center" ><spring:message code="page.jxcshcEdit.name10" /></div>
								<div field="fileSize"  width="80" headerAlign="center" align="center" ><spring:message code="page.jxcshcEdit.name11" /></div>
								<%--<div field="fileDesc"  width="80" headerAlign="center" align="center" >备注说明</div>--%>
								<div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer"><spring:message code="page.jxcshcEdit.name12" /></div>
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
    var action="${action}";
    var taskStatus="${taskStatus}";
    var jsUseCtxPath="${ctxPath}";
	var fileListGrid=mini.get("fileListGrid");
    var formJxcshc = new mini.Form("#formJxcshc");
    var jxcshcId="${jxcshcId}";
    var nodeVarsStr='${nodeVars}';
    var currentUserName="${currentUserName}";
    var currentTime="${currentTime}";
    var isTest = "${isTest}"
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>" + xzxgwjjxyxgs;
</script>
</body>
</html>
