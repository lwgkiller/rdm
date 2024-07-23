
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>编辑申请</title>
	<%@include file="/commons/edit.jsp"%>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
	<script src="${ctxPath}/scripts/rdmZhgl/gtzzApplyEdit.js?version=${static_res_version}" type="text/javascript"></script>
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
		<form id="planApplyForm" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<input id="taskId_" name="taskId_" class="mini-hidden"/>
			<input id="instId" name="instId" class="mini-hidden"/>
			<input id="applyYear" name="applyYear" class="mini-hidden"/>
			<table class="table-detail grey"  cellspacing="1" cellpadding="0">
				<caption >
					挂图作战审批单
				</caption>
				<tr>
					<td style="text-align: center">年月：</td>
					<td colspan="3">
						<input id="yearMonth" class="mini-monthpicker" onvaluechanged="searchList()" style="width: 100%" name="yearMonth"/>
					</td>
					<td style="text-align: center">备注：</td>
					<td colspan="3">
						<input name="reason"  class="mini-textbox rxc" style="width:100%;height:34px">
					</td>
				</tr>
			</table>
		</form>
		<div class="mini-fit" style="height:  100%;">
			<div style="text-align: center;margin-top: 10px"><span style="font-size: x-large;">重大项目列表</span></div>
			<div id="planListGrid" class="mini-datagrid" style="height: auto;min-height: 300px" allowResize="true" ondrawcell="onDrawCell" allowRowSelect="false"
				 url="${ctxPath}/rdmZhgl/core/gtzz/gtzzList.do" idField="id" showPager="false" allowCellWrap="true"
				  showColumnsMenu="false" sizeList="[10,20,50,100,200]"  allowAlternating="true"
				 pagerButtons="#pagerButtons">
				<div property="columns">
					<div type="indexcolumn"   align="center" headerAlign="center" width="40px">序号</div>
					<div name="action" cellCls="actionIcons" width="80" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">
						操作
					</div>
					<div field="projectName" name="projectName" width="200px" headerAlign="center" align="center" allowSort="false">项目名称</div>
					<div field="creator" name="creator" width="150px" headerAlign="center" align="center" allowSort="false">负责人</div>
					<div field="finishProcess" width="250px" headerAlign="center" align="center" allowSort="false" renderer="onFinishProcess">项目进度</div>
					<div field="deptName" name="deptName" width="150px" headerAlign="center" align="center" allowSort="false">负责部门</div>
					<div field="CREATE_TIME_" name="CREATE_TIME_" width="150px" headerAlign="center" align="center" allowSort="false">创建日期</div>
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
	var planListGrid = mini.get("planListGrid");
	let planApplyForm = new mini.Form("#planApplyForm");
	var finishProcessList = getDics("WCJD");
	//行功能按钮
	function onActionRenderer(e) {
		var record = e.record;
		var id = record.id;
		var s = '<span style="cursor: pointer;color: #0a7ac6"  title="总览" onclick="viewForm(\'' + id + '\')">总览</span>';
		return s;
	}
	function viewForm(id) {
		var url = jsUseCtxPath + "/rdmZhgl/core/gtzz/getViewPage.do?action=edit&&mainId=" + id;
		window.open(url);
	}
	function onDrawCell(e) {
		var record = e.record;
		var field = e.field;
		if (field=='finishProcess') {
			if(record.finishProcess=='1'){
				e.cellStyle = "background-color:green";
			}else if(record.finishProcess=='2'){
				e.cellStyle = "background-color:yellow";
			}else if(record.finishProcess=='3'){
				e.cellStyle = "background-color:red";
			}
		}
	}
	function onFinishProcess(e) {
		var record = e.record;
		var resultValue = record.finishProcess;
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
