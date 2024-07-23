<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<title>新增指标信息</title>
	<%@include file="/commons/edit.jsp" %>
	<link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
	<script src="${ctxPath}/scripts/rdmZhgl/monthUnProjectPlanEdit.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div id="ToolBar" class="topToolBar" style="display: block">
	<div>
		<a id="save" class="mini-button" img="${ctxPath}/scripts/mini/miniui/res/images/add.png" onclick="saveData()">保存</a>
		<a id="closeWindow" class="mini-button" img="${ctxPath}/scripts/mini/miniui/res/images/system_close.gif" onclick="CloseWindow()">关闭</a>
	</div>
</div>
<div class="mini-fit">
	<div class="form-container" style="margin:0 auto; width: 100%;height: auto">
		<form id="planForm" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<input id="asyncStatus" name="asyncStatus" class="mini-hidden"/>
			<table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
				<caption>
					非项目类工作计划
				</caption>
				<tbody>
				<tr class="firstRow displayTr">
					<td align="center"></td>
					<td align="left"></td>
					<td align="center"></td>
					<td align="left"></td>
				</tr>
				<tr>
					<td align="center"  style="white-space: nowrap;">
						工作任务名称<span style="color: #ff0000">*</span>：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="taskName" name="taskName" class="mini-textbox rxc" required="true" style="width:100%;height:34px" >
					</td>
					<td align="center"  style="white-space: nowrap;">
						计划编号：
					</td>
					<td align="center" colspan="3" rowspan="1">
						<input id="projectCode" name="projectCode" class="mini-textbox rxc" readonly style="width:100%;height:34px" >
					</td>
				</tr>
				<tr>
					<td align="center"  style="white-space: nowrap;">
						进度百分比<span style="color: #ff0000">*</span>：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="processRate" name="processRate" required class="mini-textbox rxc"  style="width:100%;height:34px" >
					</td>
					<td align="center"  style="white-space: nowrap;">
						进度状态<span style="color: #ff0000">*</span>：
					</td>
					<td align="center" colspan="3" rowspan="1">
						<input id="processStatus" name="processStatus" class="mini-combobox rxc" plugins="mini-combobox"
							   style="width:100%;height:34px"  label="进度状态："
							   length="50"
							   only_read="false" required="true" allowinput="false" mwidth="100"
							   wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
							   textField="text" valueField="key_" emptyText="请选择..."
							   url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=XPSZ-JDZT"
							   nullitemtext="请选择..." emptytext="请选择..."/>
					</td>
				</tr>
				<tr>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						任务来源<span style="color: #ff0000">*</span>：
					</td>
					<td align="center" colspan="3" rowspan="1">
						<input name="taskFrom"  emptyText="**年新品试制计划、预算项目、特殊订单、客户需求等" class="mini-textbox rxc" required="true"  style="width:100%;height:34px" >
					</td>
				</tr>
				<tr>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						计划年月<span style="color: #ff0000">*</span>：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input class="mini-monthpicker" allowInput="false"  required="true" style="width:100%;height:34px" id="yearMonth" name="yearMonth"/>
					</td>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						当前阶段：<span style="color: #ff0000">*</span>
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="stageId" name="stageId" class="mini-textbox rxc" required="true" style="width:100%;height:34px" >
					</td>
				</tr>
				<tr>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						任务开始日期：<span style="color: #ff0000">*</span>
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input name="startDate" class="mini-datepicker" allowInput="false"  required="true" style="width:100%;height:34px" >
					</td>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						任务结束日期：<span style="color: #ff0000">*</span>
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input name="endDate" class="mini-datepicker" allowInput="false"  required="true" style="width:100%;height:34px" >
					</td>
				</tr>
				<tr>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						任务负责人：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="responseMan" name="responseMan" class="mini-user" textname="responseManText"  single="false" allowInput="false" style="width:100%;"/>
					</td>
					<td align="center" style="white-space: nowrap;">
						计划级别<span style="color: #ff0000">*</span>：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="isCompanyLevel" name="isCompanyLevel" class="mini-combobox rxc" plugins="mini-combobox"
							   style="width:100%;height:34px"  label="计划级别："  onvaluechanged="onPlanLevel"
							   length="50"
							   only_read="false" required="true" allowinput="false" mwidth="100"
							   wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
							   textField="text" valueField="key_" emptyText="请选择..."
							   url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YDJH-JHJB"
							   nullitemtext="请选择..." emptytext="请选择..."/>
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						完成情况：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="finishStatus" name="finishStatus" class="mini-combobox rxc" plugins="mini-combobox"
							   style="width:100%;height:34px"  label="完成情况：" onvaluechanged="remarkTip"
							   length="50"
							   only_read="false" required="false" allowinput="false" mwidth="100"
							   wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
							   textField="text" valueField="key_" emptyText="请选择..."
							   url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=WCQK"
							   nullitemtext="请选择..." emptytext="请选择..."/>
					</td>
					<td align="center" style="white-space: nowrap;">
						是否提交延期申请：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="isDelayApply" name="isDelayApply" class="mini-combobox rxc" plugins="mini-combobox"
							   style="width:100%;height:34px"  label="完成情况："
							   length="50"
							   only_read="false" required="false" allowinput="false" mwidth="100"
							   wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
							   textField="text" valueField="key_" emptyText="请选择..."
							   url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YESORNO"
							   nullitemtext="请选择..." emptytext="请选择..."/>
					</td>
				</tr>
				<tr>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						备注：
					</td>
					<td align="center" colspan="3" rowspan="1">
						<input id="remark"  name="remark" class="mini-textbox rxc"  single="false" allowInput="true" style="width:100%;"/>
					</td>
				</tr>
				<tr>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						所属部门：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="deptId" name="deptId" class="mini-dep rxc" plugins="mini-dep"
							   style="width:98%;height:34px"
							   allowinput="false" textname="deptName" single="true" initlogindep="false"/>
					</td>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						归口部门：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="reportDeptId" name="reportDeptId" class="mini-dep rxc" plugins="mini-dep"
							   style="width:98%;height:34px"  emptyText="适用于跨部门项目，汇总至非负责人所在所"
							   allowinput="false" textname="reportDeptName" single="true" initlogindep="false"/>
					</td>
				</tr>
				<tr id="fileTr" style="display: none">
					<td style="text-align: center;height: 200px">证明材料：</td>
					<td colspan="3">
						<div style="margin-top: 1px;margin-bottom: 1px">
							<a id="addFile" class="mini-button"  onclick="addWorkFile()">添加材料</a>
							<a class="mini-button " id="downDocument"  plain="true" onclick="fileDown()">批量下载</a>
						</div>
						<div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
							 idField="id" url="${ctxPath}/rdmZhgl/core/monthWork/files/fileList.do?applyId=${applyObj.id}" autoload="true"
							 multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
							<div property="columns">
								<div type="indexcolumn" align="center" width="20">序号</div>
								<div field="fileName" align="left" headerAlign="center" width="150">文件名</div>
								<div field="fileSize" align="center" headerAlign="center" width="60">文件大小</div>
								<div field="fileDesc" align="center" headerAlign="center" width="100">文件描述</div>
								<div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer">操作</div>
							</div>
						</div>
					</td>
				</tr>
				</tbody>
			</table>
		</form>
		<div class="mini-toolbar">
			<ul class="toolBtnBox">
				<li style="float: left">
					<a id="addItem" class="mini-button"  onclick="addItem()">新增</a>
					<a id ="removeItem" class="mini-button btn-red" plain="true" onclick="removeItem()">删除</a>
				</li>
			</ul>
		</div>
		<div class="mini-fit" style="height: 100%;margin-top: 10px">
			<div id="itemGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
				 url="${ctxPath}/rdmZhgl/core/monthUnProjectPlan/items.do"
				 idField="id" allowAlternating="true" showPager="false" multiSelect="true" allowCellEdit="true"
				 allowCellSelect="true" allowCellWrap="true"
				 editNextOnEnterKey="true" editNextRowCell="true">
				<div property="columns">
					<div type="checkcolumn" width="10"></div>
					<div type="indexcolumn" headerAlign="center" align="center" width="20">序号</div>
					<div field="workContent" displayfield="workContent" width="80" headerAlign="center" align="center">本月工作内容<span
							style="color: #ff0000">*</span>
						<input property="editor" class="mini-textarea"   allowLimitValue="false"
							   required="false" only_read="false" allowinput="true"allowNull="true" value="null"  decimalPlaces="0"  style="width:100%;height:34px"/>
					</div>
					<div field="finishFlag" displayfield="finishFlag" width="80" headerAlign="center" align="center">本月工作完成标志<span
							style="color: #ff0000">*</span>
						<input property="editor" class="mini-textarea"  allowLimitValue="false"
							   required="false" only_read="false" allowinput="true"allowNull="true" value="null"  decimalPlaces="0"  style="width:100%;height:34px"/>
					</div>
					<div field="responseUserId" displayfield="userName" width="30" headerAlign="center" align="center">责任人<span
							style="color: #ff0000">*</span>
						<input property="editor" class="mini-user" style="width:auto;"  single="false"/>
					</div>
					<div field="responseDeptId" displayfield="responseDeptId" width="40" headerAlign="center" align="center">配合部门
						<input property="editor" class="mini-textbox"   allowLimitValue="false"
							   required="false" only_read="false" allowinput="true"allowNull="true" value="null"  decimalPlaces="0"  style="width:100%;height:34px"/>
					</div>
					<div field="sortIndex" displayfield="sortIndex" width="20" headerAlign="center" align="center">排序号<span
							style="color: #ff0000">*</span>
						<input property="editor" class="mini-spinner"   allowLimitValue="false"
							   required="false" only_read="false" allowinput="true"allowNull="true" value="null"  decimalPlaces="0"  minValue="0" style="width:100%;height:34px"/>
					</div>
				</div>
			</div>
		</div>
	</div>

</div>
<script type="text/javascript">
	mini.parse();
	var jsUseCtxPath = "${ctxPath}";
	var applyObj = ${applyObj};
	var action = '${action}';
	var planForm = new mini.Form("#planForm");
	var itemGrid = mini.get("itemGrid");
	var permission = ${permission};
	var isAdmin = ${isAdmin};
	var fileListGrid = mini.get("fileListGrid");
	var currentUserId = "${currentUser.userId}";
	var currentUserName = "${currentUserName}";
	var currentTime = "${currentTime}";
	var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
</script>
</body>
</html>
