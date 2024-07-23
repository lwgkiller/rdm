
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>编辑申请</title>
	<%@include file="/commons/edit.jsp"%>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
	<script src="${ctxPath}/scripts/rdmZhgl/monthWorkDelayApplyEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
	<div>
		<a id="processInfo" class="mini-button"  onclick="processInfo()"><spring:message code="page.monthWorkDelayApplyEdit.name" /></a>
		<a id="closeWindow" class="mini-button" onclick="CloseWindow()"><spring:message code="page.monthWorkDelayApplyEdit.name1" /></a>
	</div>
</div>
<div class="mini-fit">
	<div class="form-container" style="margin:0 auto; width: 100%;">
		<form id="planApplyForm" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<input id="taskId_" name="taskId_" class="mini-hidden"/>
			<input id="instId" name="instId" class="mini-hidden"/>
			<input id="deptId" name="deptId" class="mini-hidden"/>
			<input id="planIds" name="planIds" class="mini-hidden"/>
			<input id="unPlanIds" name="unPlanIds" class="mini-hidden"/>
			<input id="unPlanTaskIds" name="unPlanTaskIds" class="mini-hidden"/>
			<table class="table-detail grey"  cellspacing="1" cellpadding="0">
				<caption >
					<spring:message code="page.monthWorkDelayApplyEdit.name2" />
				</caption>
				<tr>
					<td style="width: 15%;text-align: center"><spring:message code="page.monthWorkDelayApplyEdit.name3" />：</td>
					<td >
						<input style="width:98%;" class="mini-textbox" readonly  name="deptName" />
					</td>
					<td style="width: 15%;text-align: center"><spring:message code="page.monthWorkDelayApplyEdit.name4" /> ：</td>
					<td >
						<input id="yearMonth" class="mini-monthpicker" required="true" emptyText="<spring:message code="page.monthWorkDelayApplyEdit.name5" />"
							   style="width:50%;height:34px" name="yearMonth"/>
					</td>
				</tr>
				<tr>
					<td style="text-align: center"><spring:message code="page.monthWorkDelayApplyEdit.name6" />：</td>
					<td colspan="3">
						<textarea id="reason" name="reason" class="mini-textarea rxc" plugins="mini-textarea" style="width:99%;height:100px;line-height:25px;"
								  label="<spring:message code="page.monthWorkDelayApplyEdit.name6" />" datatype="varchar" length="500" vtype="length:500" minlen="0" allowinput="true" required
								  emptytext="<spring:message code="page.monthWorkDelayApplyEdit.name7" />..." mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
					</td>
				</tr>
			</table>
		</form>
		<div class="mini-fit" style="height:  100%;">
			<div id="projectPlanDiv">
				<div style="text-align: center;margin-top: 10px"><span style="font-size: x-large;"><spring:message code="page.monthWorkDelayApplyEdit.name8" /></span></div>
				<div id="planListGrid" class="mini-datagrid" style="height: auto;min-height: 300px" allowResize="true"
					 url="${ctxPath}/rdmZhgl/core/monthWork/plans.do" idField="id" showPager="false" allowCellWrap="true"
					 multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]"  allowAlternating="true"
					 pagerButtons="#pagerButtons">
					<div property="columns">
						<div type="checkcolumn"  field="mainId" name="mainId" width="30px"></div>
						<div field="rowNum"   name="rowNum"   align="center" headerAlign="center" width="40px"><spring:message code="page.monthWorkDelayApplyEdit.name9" /></div>
						<div field="projectName" name="projectName" width="200px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkDelayApplyEdit.name10" /></div>
						<div field="number" name="number" width="150px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkDelayApplyEdit.name11" /></div>
						<div field="startEndDate" name="startEndDate" width="200px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkDelayApplyEdit.name12" /></div>
						<div field="stageName" name="stageName" width="100px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkDelayApplyEdit.name13" /></div>
						<div field="responseMan" name="responseMan" width="100px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkDelayApplyEdit.name14" /></div>
						<div field="workContent" width="250px" headerAlign="center" align="left" allowSort="false"><spring:message code="page.monthWorkDelayApplyEdit.name15" /></div>
						<div field="finishFlag" width="250px" headerAlign="center" align="left" allowSort="false"><spring:message code="page.monthWorkDelayApplyEdit.name16" /></div>
						<div field="planResponseMan" width="100px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkDelayApplyEdit.name17" /></div>
						<div field="responseDeptId" width="150px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkDelayApplyEdit.name18" /></div>
						<div field="isCompanyLevel" name="isCompanyLevel" width="100px" headerAlign="center" align="center" renderer="onCompany" allowSort="false"><spring:message code="page.monthWorkDelayApplyEdit.name19" /></div>
						<div field="finishStatus" name="finishStatus" width="100px" headerAlign="center" align="center" allowSort="false"  renderer="onFinish"><spring:message code="page.monthWorkDelayApplyEdit.name20" /></div>
						<div field="remark" name="remark" width="200px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkDelayApplyEdit.name21" /></div>
						<div field="isDelayApply" name="isDelayApply" width="150px" headerAlign="center" align="center" allowSort="false" renderer="onDelayApplyRate"><spring:message code="page.monthWorkDelayApplyEdit.name22" /></div>
					</div>
				</div>
			</div>
			<div id="unProjectPlanDiv">
				<div style="text-align: center;margin-top: 10px"><span style="font-size: x-large;"><spring:message code="page.monthWorkDelayApplyEdit.name23" /></span></div>
				<div id="unPlanListGrid" class="mini-datagrid" style="height: auto;min-height: 300px" allowResize="true"
					 url="${ctxPath}/rdmZhgl/core/monthUnProjectPlan/plans.do" idField="id" showPager="false" allowCellWrap="true"
					 multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]"  allowAlternating="true"
					 pagerButtons="#pagerButtons">
					<div property="columns">
						<div type="checkcolumn"  field="mainId" name="mainId" width="30px"></div>
						<div field="rowNum"   name="rowNum"   align="center" headerAlign="center" width="40px"><spring:message code="page.monthWorkDelayApplyEdit.name9" /></div>
						<div field="taskName" name="taskName" width="200px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkDelayApplyEdit.name24" /></div>
						<div field="taskFrom" name="taskFrom" width="150px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkDelayApplyEdit.name25" /></div>
						<div field="startEndDate" name="startEndDate" width="200px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkDelayApplyEdit.name26" /></div>
						<div field="stageId" name="stageId" width="100px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkDelayApplyEdit.name27" /></div>
						<div field="responseMan" name="responseMan" width="100px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkDelayApplyEdit.name28" /></div>
						<div field="workContent" width="250px" headerAlign="center" align="left" allowSort="false"><spring:message code="page.monthWorkDelayApplyEdit.name15" /></div>
						<div field="finishFlag" width="250px" headerAlign="center" align="left" allowSort="false"><spring:message code="page.monthWorkDelayApplyEdit.name16" /></div>
						<div field="planResponseMan" width="100px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkDelayApplyEdit.name17" /></div>
						<div field="responseDeptId" width="150px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkDelayApplyEdit.name18" /></div>
						<div field="isCompanyLevel" name="isCompanyLevel" width="100px" headerAlign="center" align="center" renderer="onCompany" allowSort="false"><spring:message code="page.monthWorkDelayApplyEdit.name19" /></div>
						<div field="finishStatus" name="finishStatus" width="100px" headerAlign="center" align="center" allowSort="false"  renderer="onFinish"><spring:message code="page.monthWorkDelayApplyEdit.name20" /></div>
						<div field="remark" name="remark" width="200px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkDelayApplyEdit.name21" /></div>
						<div field="isDelayApply" name="isDelayApply" width="150px" headerAlign="center" align="center" allowSort="false" renderer="onDelayApplyRate"><spring:message code="page.monthWorkDelayApplyEdit.name22" /></div>
					</div>
				</div>
			</div>
			<div id="unPlanTaskDiv">
				<div style="text-align: center;margin-top: 10px"><span style="font-size: x-large;"><spring:message code="page.monthWorkDelayApplyEdit.name29" /></span></div>
				<div id="unPlanTaskListGrid" class="mini-datagrid" style="height: auto;min-height: 300px" allowResize="true"
					 url="${ctxPath}/rdmZhgl/core/monthUnPlanTask/plans.do" idField="id" showPager="false" allowCellWrap="true"
					 multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]"  allowAlternating="true"
					 pagerButtons="#pagerButtons">
					<div property="columns">
						<div type="checkcolumn"  field="mainId" name="mainId" width="30px"></div>
						<div field="rowNum"   name="rowNum"   align="center" headerAlign="center" width="40px"><spring:message code="page.monthWorkDelayApplyEdit.name9" /></div>
						<div field="taskName" name="taskName" width="200px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkDelayApplyEdit.name24" /></div>
						<div field="taskFrom" name="taskFrom" width="150px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkDelayApplyEdit.name25" /></div>
						<div field="startEndDate" name="startEndDate" width="200px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkDelayApplyEdit.name26" /></div>
						<div field="responseMan" name="responseMan" width="100px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkDelayApplyEdit.name28" /></div>
						<div field="workContent" width="250px" headerAlign="center" align="left" allowSort="false"><spring:message code="page.monthWorkDelayApplyEdit.name15" /></div>
						<div field="finishFlag" width="250px" headerAlign="center" align="left" allowSort="false"><spring:message code="page.monthWorkDelayApplyEdit.name16" /></div>
						<div field="planResponseMan" width="100px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkDelayApplyEdit.name17" /></div>
						<div field="responseDeptId" width="150px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkDelayApplyEdit.name18" /></div>
						<div field="isCompanyLevel" name="isCompanyLevel" width="100px" headerAlign="center" align="center" renderer="onCompany" allowSort="false"><spring:message code="page.monthWorkDelayApplyEdit.name19" /></div>
						<div field="finishStatus" name="finishStatus" width="100px" headerAlign="center" align="center" allowSort="false"  renderer="onFinish"><spring:message code="page.monthWorkDelayApplyEdit.name20" /></div>
						<div field="remark" name="remark" width="200px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkDelayApplyEdit.name21" /></div>
						<div field="isDelayApply" name="isDelayApply" width="150px" headerAlign="center" align="center" allowSort="false" renderer="onDelayApplyRate"><spring:message code="page.monthWorkDelayApplyEdit.name22" /></div>
					</div>
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
	var unPlanListGrid = mini.get("unPlanListGrid");
	var unPlanTaskListGrid = mini.get("unPlanTaskListGrid");
	let planApplyForm = new mini.Form("#planApplyForm");
	var yesOrNo = getDics("YESORNO");
	var jhjbList = getDics("YDJH-JHJB");
	var finishStatusList = getDics("WCQK");
	planListGrid.frozenColumns(0, 3);
	unPlanListGrid.frozenColumns(0, 3);
	unPlanTaskListGrid.frozenColumns(0, 3);
	planListGrid.on("load", function () {
		planListGrid.mergeColumns(["mainId","rowNum","projectName", "number", "startEndDate", "stageName", "responseMan", "isCompanyLevel","finishStatus", "isDelayApply","remark"]);
	});
	unPlanListGrid.on("load", function () {
		unPlanListGrid.mergeColumns(["mainId","rowNum","taskName", "taskFrom", "startEndDate", "stageId", "responseMan", "isCompanyLevel","finishStatus","isDelayApply","remark"]);
	});
	unPlanTaskListGrid.on("load", function () {
		unPlanTaskListGrid.mergeColumns(["mainId","rowNum","taskName", "taskFrom", "startEndDate",  "responseMan", "isCompanyLevel","finishStatus","isDelayApply","remark"]);
	});
    function statusRenderer(e) {
		let record = e.record;
		let status = record.standardStatus;

		let arr = [{'key': 'draft', 'value': '草稿', 'css': 'orange'},
            {'key': 'enable', 'value': '有效', 'css': 'green'},
            {'key': 'disable', 'value': '已废止', 'css': 'red'}
        ];
        return $.formatItemValue(arr, status);
    }
	function onCompany(e) {
		var record = e.record;
		var resultValue = record.isCompanyLevel;
		var resultText = '';
		for (var i = 0; i < jhjbList.length; i++) {
			if (jhjbList[i].key_ == resultValue) {
				resultText = jhjbList[i].text;
				break
			}
		}
		return resultText;
	}
	function onFinish(e) {
		var record = e.record;
		var resultValue = record.finishStatus;
		var resultText = '';
		for (var i = 0; i < finishStatusList.length; i++) {
			if (finishStatusList[i].key_ == resultValue) {
				resultText = finishStatusList[i].text;
				break
			}
		}
		var _html = '';
		if(resultValue=='1'&&record.isDelayApply != '1'){
			_html = '<span style="color: red">'+resultText+'</span>'
		}else if(resultValue=='0'){
			_html = '<span style="color: green">'+resultText+'</span>'
		}else if(resultValue=='1'&&record.isDelayApply == '1'){
			_html = '<span style="color: green">'+resultText+'</span>'
		}
		return _html;
	}
	function onDelayApplyRate(e) {
		var record = e.record;
		var delayApply = record.isDelayApply;
		var resultText = '';
		for (var i = 0; i < yesOrNo.length; i++) {
			if (yesOrNo[i].key_ == delayApply) {
				resultText = yesOrNo[i].text;
				break
			}
		}
		return resultText;
	}

	function getDics(dicKey) {
		let resultDate = [];
		$.ajax({
			async: false,
			url: __rootPath + '/sys/core/commonInfo/getDicItem.do?dicType=' + dicKey,
			type: 'GET',
			contentType: 'application/json',
			success: function (data) {
				if (data.code == 200) {
					resultDate = data.data;
				}
			}
		});
		return resultDate;
	}
</script>
</body>
</html>
