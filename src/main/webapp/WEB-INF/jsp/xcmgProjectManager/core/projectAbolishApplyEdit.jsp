
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>编辑申请</title>
	<%@include file="/commons/edit.jsp"%>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
	<script src="${ctxPath}/scripts/xcmgProjectManager/projectAbolishApplyEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
	<div>
		<a id="processInfo" class="mini-button"  onclick="processInfo()"><spring:message code="page.projectAbolishApplyEdit.name" /></a>
		<a id="closeWindow" class="mini-button" onclick="CloseWindow()"><spring:message code="page.projectAbolishApplyEdit.name1" /></a>
	</div>
</div>
<div class="mini-fit">
	<div class="form-container" style="margin:0 auto; width: 80%;">
		<form id="projectAbolishApply" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<input id="projectId" name="projectId" class="mini-hidden"/>
			<input id="taskId_" name="taskId_" class="mini-hidden"/>
			<input id="instId" name="instId" class="mini-hidden"/>
			<input id="mainDepId" name="mainDepId" class="mini-hidden"/>
			<table class="table-detail grey"  cellspacing="1" cellpadding="0">
				<caption>
					<spring:message code="page.projectAbolishApplyEdit.name2" />
				</caption>
				<tr>
					<td style="width: 15%;text-align: center"><spring:message code="page.projectAbolishApplyEdit.name3" />：</td>
					<td colspan="3">
						<input style="width:98%;" class="mini-textbox" readonly id="projectName" name="projectName" />
					</td>
				</tr>
				<tr>
					<td style="width: 15%;text-align: center"><spring:message code="page.projectAbolishApplyEdit.name4" />：</td>
					<td >
						<input style="width:98%;" class="mini-textbox" readonly id="mainResUserName" name="mainResUserName" />
					</td>
					<td style="width: 15%;text-align: center"><spring:message code="page.projectAbolishApplyEdit.name5" /> ：</td>
					<td >
						<input style="width:98%;" class="mini-textbox" readonly id="projectType" name="projectType" />
					</td>
				</tr>
				<tr>
					<td style="width: 15%;text-align: center"><spring:message code="page.projectAbolishApplyEdit.name6" />：</td>
					<td >
						<input style="width:98%;" class="mini-textbox" readonly id="number" name="number" />
					</td>
					<td style="width: 15%;text-align: center"><spring:message code="page.projectAbolishApplyEdit.name7" /> ：</td>
					<td >
						<input style="width:98%;" class="mini-textbox" readonly id="projectLevel" name="projectLevel" />
					</td>
				</tr>
				<tr>
					<td style="width: 15%;text-align: center"><spring:message code="page.projectAbolishApplyEdit.name8" />：</td>
					<td >
						<input style="width:98%;" class="mini-textbox" readonly id="mainDeptName" name="mainDeptName" />
					</td>
					<td style="width: 15%;text-align: center"><spring:message code="page.projectAbolishApplyEdit.name9" />：</td>
					<td>
						<input style="width:98%;" class="mini-textbox" readonly id="currentStage" name="currentStage" />
					</td>
				</tr>
				<tr>
					<td style="text-align: left"><spring:message code="page.projectAbolishApplyEdit.name10" />：</td>
					<td colspan="3">
						<textarea id="reason" name="reason" class="mini-textarea rxc" plugins="mini-textarea" style="width:98%;height:200px;line-height:25px;"
								  label="<spring:message code="page.projectAbolishApplyEdit.name11" />" datatype="varchar" length="500" vtype="length:500" minlen="0" allowinput="true"
								  emptytext="<spring:message code="page.projectAbolishApplyEdit.name12" />..." mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
					</td>
				</tr>
				<tr>
					<td style="text-align: center"><spring:message code="page.projectAbolishApplyEdit.name13" />：</td>
					<td colspan="3">
						<input name="fj" class="upload-panel rxc" plugins="upload-panel" style="width:auto;" allowupload="true" label="11"
							   length="2048" sizelimit="50" isone="false" filetype="" mwidth="0" wunit="%" mheight="0" hunit="%"/>

					</td>
				</tr>
			</table>
		</form>
	</div>
</div>


<script type="text/javascript">
    mini.parse();
    let nodeVarsStr='${nodeVars}';
	var action="${action}";
	let jsUseCtxPath="${ctxPath}";
	let ApplyObj=${applyObj};
	let status="${status}";

	let projectAbolishApply = new mini.Form("#projectAbolishApply");

	projectAbolishApply.setData(ApplyObj);

    function statusRenderer(e) {
		let record = e.record;
		let status = record.standardStatus;

		let arr = [{'key': 'draft', 'value': '草稿', 'css': 'orange'},
            {'key': 'enable', 'value': '有效', 'css': 'green'},
            {'key': 'disable', 'value': '已废止', 'css': 'red'}
        ];
        return $.formatItemValue(arr, status);
    }

</script>
</body>
</html>
