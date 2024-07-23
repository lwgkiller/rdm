<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>编辑申请</title>
	<%@include file="/commons/edit.jsp"%>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
	<script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/xcmgProjectManager/projectFileApplyEdit.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/xcmgProjectManager/projectProcess.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
	<div>
		<a id="processInfo" class="mini-button"  onclick="processInfo()"><spring:message code="page.projectFileApplyEdit.name" /></a>
		<a id="closeWindow" class="mini-button" onclick="CloseWindow()"><spring:message code="page.projectFileApplyEdit.name1" /></a>
	</div>
</div>
<div class="mini-fit">
	<div class="form-container" style="margin:0 auto; width: 80%;">
		<form id="projectFileApply" method="post">
			<input id="projectId" name="projectId" class="mini-hidden"/>
			<input id="stageId" name="stageId" class="mini-hidden"/>
			<input id="instId" name="instId" class="mini-hidden"/>
			<input id="fileIds" name="fileIds" class="mini-hidden"/>
			<input id="mainDepId" name="mainDepId" class="mini-hidden"/>
			<table class="table-detail grey"  cellspacing="1" cellpadding="0">
				<caption>
					<spring:message code="page.projectFileApplyEdit.name2" />
				</caption>
				<tr>
					<td style="width: 15%;text-align: center"><spring:message code="page.projectFileApplyEdit.name3" />：</td>
					<td >
						<input style="width:98%;" class="mini-textbox" readonly id="projectName" name="projectName" />
					</td>
					<td style="width: 15%;text-align: center"><spring:message code="page.projectFileApplyEdit.name4" />：</td>
					<td >
						<input style="width:98%;" class="mini-textbox" readonly id="id" name="id" />
					</td>
				</tr>
				<tr>
					<td style="width: 15%;text-align: center"><spring:message code="page.projectFileApplyEdit.name5" />：</td>
					<td >
						<input style="width:98%;" class="mini-textbox" readonly id="mainResUserName" name="mainResUserName" />
					</td>
					<td style="width: 15%;text-align: center"><spring:message code="page.projectFileApplyEdit.name6" /> ：</td>
					<td >
						<input style="width:98%;" class="mini-textbox" readonly id="projectType" name="projectType" />
					</td>
				</tr>
				<tr>
					<td style="width: 15%;text-align: center"><spring:message code="page.projectFileApplyEdit.name7" />：</td>
					<td >
						<input style="width:98%;" class="mini-textbox" readonly id="number" name="number" />
					</td>
					<td style="width: 15%;text-align: center"><spring:message code="page.projectFileApplyEdit.name8" /> ：</td>
					<td >
						<input style="width:98%;" class="mini-textbox" readonly id="projectLevel" name="projectLevel" />
					</td>
				</tr>
				<tr>
					<td style="width: 15%;text-align: center"><spring:message code="page.projectFileApplyEdit.name9" />：</td>
					<td >
						<input style="width:98%;" class="mini-textbox" readonly id="mainDeptName" name="mainDeptName" />
					</td>
					<td style="width: 15%;text-align: center"><spring:message code="page.projectFileApplyEdit.name10" />：</td>
					<td>
						<input style="width:98%;" class="mini-textbox" readonly id="stageName" name="stageName" />
					</td>
				</tr>
				<tr>
					<td style="text-align: center;height: 500px" ><spring:message code="page.projectFileApplyEdit.name11" />：</td>
					<td colspan="3">
							<div style="margin-top: 25px;margin-bottom: 2px">
								<a id="addFile" class="mini-button" style="display: none"  onclick="addFile()"><spring:message code="page.projectFileApplyEdit.name12" /></a>
								<a id="delFile" class="mini-button" style="display: none;margin-left: 10px"  onclick="delFile()"><spring:message code="page.projectFileApplyEdit.name13" /></a>
								<span id="tipInfo" style="display:none; color: red"><spring:message code="page.projectFileApplyEdit.name14" /></span>
							</div>
						<div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 100%" allowResize="false"
							  idField="id" url="${ctxPath}/xcmgProjectManager/core/xcmgProjectFile/getApprovalFileList.do"
							 multiSelect="true" showPager="false" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
							<div property="columns">
								<div type="checkcolumn" width="10"></div>
								<div type="indexcolumn" align="center"  width="20"><spring:message code="page.projectFileApplyEdit.name15" /></div>
								<div field="fileName"  width="140" headerAlign="center" align="center" allowSort="false" ><spring:message code="page.projectFileApplyEdit.name16" /></div>
								<div field="deliveryName"  sortField="categoryName"  width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.projectFileApplyEdit.name17" /></div>
								<div field="fileSize"  sortField="levelName"  width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.projectFileApplyEdit.name18" /></div>
								<div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer"><spring:message code="page.projectFileApplyEdit.name19" /></div>
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
    let nodeVarsStr='${nodeVars}';
	var action="${action}";
	let jsUseCtxPath="${ctxPath}";
	let ApplyObj=${applyObj};
	let status="${status}";
	let projectFileApply = new mini.Form("#projectFileApply");
	var fileListGrid = mini.get("fileListGrid");
    var currentUserName="${currentUserName}";
    var currentTime="${currentTime}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>" + xzxgwjjxyxgs;

    function statusRenderer(e) {
		let record = e.record;
		let status = record.standardStatus;

		let arr = [{'key': 'draft', 'value': '草稿', 'css': 'orange'},
            {'key': 'enable', 'value': '有效', 'css': 'green'},
            {'key': 'disable', 'value': '已废止', 'css': 'red'}
        ];
        return $.formatItemValue(arr, status);
    }
	function operationRenderer(e) {
		let record = e.record;
		var cellHtml = '';
		cellHtml=returnPreviewSpan(record.fileName,record.relativeFilePath,'project',coverContent);
		cellHtml+='&nbsp;&nbsp;&nbsp;<span title=' + projectFileApplyEdit_name + ' style="color:#409EFF;cursor: pointer;" ' +
				'onclick="downProjectLoadFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">' + projectFileApplyEdit_name + '</span>';
		//增加删除按钮
		if(action=='edit'||editForm=='1' || action == 'change') {
			cellHtml+='&nbsp;&nbsp;&nbsp;<span title=' + projectFileApplyEdit_name1 + ' style="color:#409EFF;cursor: pointer;" ' +
					'onclick="unboundProjectApprovalFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">' + projectFileApplyEdit_name1 + '</span>';
		}

		return cellHtml;
	}

</script>
</body>
</html>
