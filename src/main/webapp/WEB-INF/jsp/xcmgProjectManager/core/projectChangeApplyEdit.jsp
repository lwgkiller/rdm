
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>编辑申请</title>
	<%@include file="/commons/edit.jsp"%>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
	<script src="${ctxPath}/scripts/xcmgProjectManager/projectChangeApplyEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body >
<div id="detailToolBar" class="topToolBar" style="display: none">
	<div>
		<a id="processInfo" class="mini-button"  onclick="processInfo()"><spring:message code="page.projectChangeApplyEdit.name" /></a>
		<a id="closeWindow" class="mini-button" onclick="CloseWindow()"><spring:message code="page.projectChangeApplyEdit.name1" /></a>
	</div>
</div>
<div class="mini-fit" >
	<div class="form-container" style="margin:0 auto; width: 80%;">
		<form id="projectChangeApply" method="post" >
			<input id="id" name="id" class="mini-hidden"/>
			<input id="projectId" name="projectId" class="mini-hidden"/>
			<input id="taskId_" name="taskId_" class="mini-hidden"/>
			<input id="instId" name="instId" class="mini-hidden"/>
			<input id="mainDepId" name="mainDepId" class="mini-hidden"/>
			<table class="table-detail grey" cellspacing="1" cellpadding="0" >
				<caption>
					<spring:message code="page.projectChangeApplyEdit.name2" />
				</caption>
				<tr>
					<td style="width: 15%;text-align: center"><spring:message code="page.projectChangeApplyEdit.name3" />：</td>
					<td>
						<input style="width:98%;" class="mini-textbox" readonly id="projectName" name="projectName" />
					</td>
					<td style="width: 15%;text-align: center">是否需要分管领导审批：</td>
					<td>
						<input id="isLeader" name="isLeader"
							   class="mini-combobox" style="width:98%;"
							   textField="value" valueField="key" emptyText="请选择..."
							   required="false" allowInput="false" showNullItem="true"
							   nullItemText="请选择..."
							   data="[{'key' : '是','value' : '是'},{'key' : '否','value' : '否'}]"
						/>
					</td>
				</tr>
				<tr>
					<td style="width: 15%;text-align: center"><spring:message code="page.projectChangeApplyEdit.name4" />：</td>
					<td >
						<input style="width:98%;" class="mini-textbox" readonly id="mainResUserName" name="mainResUserName" />
					</td>
					<td style="width: 15%;text-align: center"><spring:message code="page.projectChangeApplyEdit.name5" /> ：</td>
					<td >
						<input style="width:98%;" class="mini-textbox" readonly id="projectType" name="projectType" />
					</td>
				</tr>
				<tr>
					<td style="width: 15%;text-align: center"><spring:message code="page.projectChangeApplyEdit.name6" />：</td>
					<td >
						<input style="width:98%;" class="mini-textbox" readonly id="number" name="number" />
					</td>
					<td style="width: 15%;text-align: center"><spring:message code="page.projectChangeApplyEdit.name7" /> ：</td>
					<td >
						<input style="width:98%;" class="mini-textbox" readonly id="projectLevel" name="projectLevel" />
					</td>
				</tr>
				<tr>
					<td style="width: 15%;text-align: center"><spring:message code="page.projectChangeApplyEdit.name8" />：</td>
					<td >
						<input style="width:98%;" class="mini-textbox" readonly id="mainDeptName" name="mainDeptName" />
					</td>
					<td style="width: 15%;text-align: center"><spring:message code="page.projectChangeApplyEdit.name9" />：</td>
					<td>
						<input style="width:98%;" class="mini-textbox" readonly id="currentStage" name="currentStage" />
					</td>
				</tr>
				<tr>
					<td style="width: 15%;text-align: center">是否重大变更：</td>
					<td>
						<input id="isBigChange" name="isBigChange"
							   class="mini-combobox" style="width:98%;"
							   textField="value" valueField="key" emptyText="请选择..."
							   required="false" allowInput="false" showNullItem="true"
							   nullItemText="请选择..."
							   data="[{'key' : '是','value' : '是'},{'key' : '否','value' : '否'}]"
						/>
					</td>
				</tr>
				<tr>
					<td style="text-align: left"><spring:message code="page.projectChangeApplyEdit.name10" />：</td>
					<td colspan="3">
						<textarea id="currentCondition"  name="currentCondition" class="mini-textarea rxc" plugins="mini-textarea" style="width:98%;height:150px;line-height:25px;"
								  label="<spring:message code="page.projectChangeApplyEdit.name11" />" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
								   emptytext="<spring:message code="page.projectChangeApplyEdit.name12" />..." mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
					</td>
				</tr>
				<tr>
					<td style="text-align: left"><spring:message code="page.projectChangeApplyEdit.name13" />：</td>
					<td colspan="3">
						<textarea  id="reason" name="reason" class="mini-textarea rxc" plugins="mini-textarea" style="width:98%;height:150px;line-height:25px;"
								  label="<spring:message code="page.projectChangeApplyEdit.name14" />" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
								  emptytext="<spring:message code="page.projectChangeApplyEdit.name15" />..." mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
					</td>
				</tr>
				<tr>
					<td style="text-align: left"><spring:message code="page.projectChangeApplyEdit.name16" />：</td>
					<td colspan="3">
						<textarea  id="changeContent" name="changeContent" class="mini-textarea rxc" plugins="mini-textarea" style="width:98%;height:150px;line-height:25px;"
								  label="<spring:message code="page.projectChangeApplyEdit.name17" />" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
								  emptytext="<spring:message code="page.projectChangeApplyEdit.name18" />..." mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
					</td>
				</tr>
				<tr>
					<td style="text-align: left"><spring:message code="page.projectChangeApplyEdit.name19" />：</td>
					<td colspan="3">
						<textarea  id="changeDesignDept" name="changeDesignDept" class="mini-textarea rxc" plugins="mini-textarea" style="width:98%;height:150px;line-height:25px;"
								  label="<spring:message code="page.projectChangeApplyEdit.name20" />" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
								  emptytext="<spring:message code="page.projectChangeApplyEdit.name21" />..." mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
					</td>
				</tr>
				<tr>
					<td style="text-align: left"><spring:message code="page.projectChangeApplyEdit.name22" /> ：</td>
					<td colspan="3">
						<textarea id="adjustMeasure"  name="adjustMeasure" class="mini-textarea rxc" plugins="mini-textarea" style="width:98%;height:150px;line-height:25px;"
								   label="<spring:message code="page.projectChangeApplyEdit.name23" />" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
								   emptytext="<spring:message code="page.projectChangeApplyEdit.name24" />..." mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
					</td>
				</tr>
				<tr>
					<td><spring:message code="page.projectChangeApplyEdit.name25" />：</td>
					<td colspan="3">
						<input name="fj" class="upload-panel rxc" plugins="upload-panel" style="width:auto;" allowupload="true" label="11"
							   length="2048" sizelimit="50" isone="false" filetype="" mwidth="0" wunit="%" mheight="0" hunit="%"/>

					</td>
				</tr>
			</table>
			<span>
				<spring:message code="page.projectChangeApplyEdit.name26" />
				<spring:message code="page.projectChangeApplyEdit.name27" />
			</span>
		</form>
	</div>
</div>


<script type="text/javascript">
    mini.parse();
    let nodeVarsStr='${nodeVars}';
	let action="${action}";
	let jsUseCtxPath="${ctxPath}";
	let changeApplyObj=${changeApplyObj};
	let status="${status}";
	let projectChangeApply = new mini.Form("#projectChangeApply");

    projectChangeApply.setData(changeApplyObj);

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
