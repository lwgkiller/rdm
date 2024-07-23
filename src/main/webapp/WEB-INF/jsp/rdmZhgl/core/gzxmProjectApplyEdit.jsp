
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>编辑申请</title>
	<%@include file="/commons/edit.jsp"%>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
	<script src="${ctxPath}/scripts/rdmZhgl/gzxmProjectApplyEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
	<div>
		<a id="processInfo" class="mini-button"  onclick="processInfo()">流程信息</a>
		<a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
	</div>
</div>
<div class="mini-fit">
	<div class="form-container" style="margin:0 auto; width: 100%;">
		<form id="productApplyForm" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<input id="taskId_" name="taskId_" class="mini-hidden"/>
			<input id="instId" name="instId" class="mini-hidden"/>
			<input id="projectIds" name="projectIds" class="mini-hidden"/>
			<table class="table-detail grey"  cellspacing="1" cellpadding="0">
				<caption >
					国重项目审批单
				</caption>
				<tr>
					<td style="text-align: center;width: 20%">编制人：</td>
					<td style="min-width:170px">
						<input id="editorUserId" name="editorUserId" textname="editorUserName"
							   class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
							   label="编制人" length="50" mainfield="no" single="true" enabled="false"/>
					</td>
					<td style="text-align: center;width: 20%">部门：</td>
					<td style="min-width:170px">
						<input id="editorUserDeptId" name="editorUserDeptId" class="mini-dep rxc" plugins="mini-dep"
							   data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
							   style="width:98%;height:34px" allowinput="false" label="部门" textname="editorUserDeptName" length="500"
							   maxlength="500" minlen="0" single="true" required="false" initlogindep="false" showclose="false"
							   mwidth="160" wunit="px" mheight="34" hunit="px" enabled="false"/>
					</td>
				<tr>
				<tr>
					<td style="text-align: center">备注：</td>
					<td colspan="3">
						<textarea id="reason" name="reason" class="mini-textarea rxc" plugins="mini-textarea"
								  style="width:99%;height:100px;line-height:25px;"
								  label="备注" datatype="varchar" length="500" vtype="length:500" minlen="0"
								  allowinput="true"
								  mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
					</td>
				</tr>
			</table>
		</form>
		<div class="mini-fit" style="height:  100%;">
			<div id="projectGrid" class="mini-datagrid" allowResize="true" style="height: 100%"
				 url="${ctxPath}/rdmZhgl/core/gzxm/projectApply/projectInfo.do" idField="id" showPager="false" allowCellWrap="true"
				 allowCellEdit="true" allowCellSelect="true"
				 multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" allowAlternating="true"
				 oncellendedit="cellendedit" allowHeaderWrap="true"  onlyCheckSelection="true"
				 pagerButtons="#pagerButtons">
				<div property="columns">
					<div type="indexcolumn"   align="center" headerAlign="center" width="30px">序号</div>
					<div field="projectName" name="projectName" width="150px" headerAlign="center" align="center" renderer="onProjectName">项目名称</div>
					<div field="projectResponsor" name="projectResponsor" width="150px" headerAlign="center" align="center">项目负责人</div>
					<div field="projectResponseDept" name="projectResponseDept" width="150px" headerAlign="center" align="center" >项目承担单位</div>
					<div field="subjectName" name="subjectName" width="150px" headerAlign="center" align="center" renderer="onSubjectName">课题名称</div>
					<div field="responsor" name="responsor" width="150px" headerAlign="center" align="center">课题负责人</div>
					<div field="responseDept" name="responseDept" width="150px" headerAlign="center" align="center">课题承担单位</div>
					<div field="startDate" name="startDate" width="150px" headerAlign="center" align="center">课题开始时间</div>
					<div field="endDate" name="endDate" width="150px" headerAlign="center" align="center">课题结束时间</div>
					<div field="currentStage" name="currentStage" width="100px" headerAlign="center" align="center"  renderer="onFinishProcess">进度状态</div>
				</div>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    let nodeVarsStr='${nodeVars}';
	var action="${action}";
	let jsUseCtxPath="${ctxPath}";
	let ApplyObj=${applyObj};
	let status="${status}";
	var projectIds = "${projectIds}";
	var projectGrid = mini.get("projectGrid");
	let productApplyForm = new mini.Form("#productApplyForm");
	var finishProcessList = getDics("WCJD");
	projectGrid.on("load", function () {
		projectGrid.mergeColumns(["projectName","projectResponsor","projectResponseDept"]);
	});
	function onActionRenderer(e) {
		var record = e.record;
		var id = record.id;
		var s = '<span  title="详情" style="cursor: pointer;color:green" onclick="viewForm(\'' + id + '\')">详情</span>';
		return s;
	}
	//修改
	function viewForm(id) {
		var url = jsUseCtxPath + "/rdmZhgl/core/gzxm/projectApply/detail.do?id=" + id;
		window.open(url);
	}
	function onProcessStatus(e) {
		var processStatus = e.record.processStatus;
		var _html = '';
		if(processStatus=='1'){
			_html = '<span style="color: green">正常</span>'
		}else if(processStatus=='2'){
			_html = '<span style="color: #d9d90a">轻微落后</span>'
		}else if(processStatus=='3'){
			_html = '<span style="color: red">严重滞后</span>'
		}
		return _html;
	}
	function onProjectName(e) {
		var record = e.record;
		var id = record.id;
		var s = '';
		s += '<span style="cursor: pointer;color: #0a7ac6"  onclick="overSeeProject(\'' + id + '\')">'+record.projectName+'</span>';
		return s;
	}
	function onSubjectName(e) {
		var record = e.record;
		var id = record.subjectId;
		var s = '';
		if(record.subjectName){
			s += '<span style="cursor: pointer;color: #0a7ac6"  onclick="overSeeSubject(\'' + id + '\')">'+record.subjectName+'</span>';
		}
		return s;
	}
	function overSeeProject(mainId) {
		var url = jsUseCtxPath + "/rdmZhgl/core/gzxm/project/getViewPage.do?action=edit&&mainId=" + mainId;
		window.open(url);
	}
	function overSeeSubject(mainId) {
		var url = jsUseCtxPath + "/rdmZhgl/core/gzxm/subject/getViewPage.do?action=edit&&mainId=" + mainId;
		window.open(url);
	}

	function onFinishProcess(e) {
		var record = e.record;
		var resultValue = record.currentStage;
		var resultText = '';
		for (var i = 0; i < finishProcessList.length; i++) {
			if (finishProcessList[i].key_ == resultValue) {
				resultText = finishProcessList[i].text;
				break
			}
		}
		return resultText;
	}
</script>
</body>
</html>
