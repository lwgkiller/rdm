<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<title>新增指标信息</title>
	<%@include file="/commons/edit.jsp" %>
	<link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
	<script src="${ctxPath}/scripts/rdmZhgl/monthUnPlanTaskEdit.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div id="ToolBar" class="topToolBar" style="display: block">
	<div>
		<a id="save" class="mini-button" img="${ctxPath}/scripts/mini/miniui/res/images/add.png" onclick="saveData()"><spring:message code="page.monthUnPlanTaskEdit.name" /></a>
		<a id="closeWindow" class="mini-button" img="${ctxPath}/scripts/mini/miniui/res/images/system_close.gif" onclick="CloseWindow()"><spring:message code="page.monthUnPlanTaskEdit.name1" /></a>
	</div>
</div>
<div class="mini-fit">
	<div class="form-container" style="margin:0 auto; width: 100%;height: auto">
		<form id="planForm" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<input id="asyncStatus" name="asyncStatus" class="mini-hidden"/>
			<table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
				<caption>
					<spring:message code="page.monthUnPlanTaskEdit.name2" />
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
						<spring:message code="page.monthUnPlanTaskEdit.name3" /><span style="color: #ff0000">*</span>：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="taskName" name="taskName" class="mini-textbox rxc" required="true" style="width:100%;height:34px" >
					</td>
					<td align="center"  style="white-space: nowrap;">
						<spring:message code="page.monthUnPlanTaskEdit.name4" />：
					</td>
					<td align="center" colspan="3" rowspan="1">
						<input id="projectCode" name="projectCode" class="mini-textbox rxc" readonly style="width:100%;height:34px" >
					</td>
				</tr>
				<tr>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						<spring:message code="page.monthUnPlanTaskEdit.name5" /><span style="color: #ff0000">*</span>：
					</td>
					<td align="center" colspan="3" rowspan="1">
						<input name="taskFrom"  emptyText="" class="mini-textbox rxc" required="true"  style="width:100%;height:34px" >
					</td>
				</tr>
				<tr>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						<spring:message code="page.monthUnPlanTaskEdit.name6" /><span style="color: #ff0000">*</span>：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input class="mini-monthpicker" allowInput="false"  required="true" style="width:100%;height:34px" id="yearMonth" name="yearMonth"/>
					</td>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						<spring:message code="page.monthUnPlanTaskEdit.name7" />：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="deptId" name="deptId" class="mini-dep rxc" plugins="mini-dep"
							   style="width:98%;height:34px"
							   allowinput="false" textname="deptName" single="true" initlogindep="false"/>
					</td>
				</tr>
				<tr>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						<spring:message code="page.monthUnPlanTaskEdit.name8" />：<span style="color: #ff0000">*</span>
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input name="startDate" class="mini-datepicker" allowInput="false"  required="true" style="width:100%;height:34px" >
					</td>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						<spring:message code="page.monthUnPlanTaskEdit.name9" />：<span style="color: #ff0000">*</span>
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input name="endDate" class="mini-datepicker" allowInput="false"  required="true" style="width:100%;height:34px" >
					</td>
				</tr>
				<tr>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						<spring:message code="page.monthUnPlanTaskEdit.name10" />：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="responseMan" name="responseMan" class="mini-user" textname="responseManText"  single="false" allowInput="false" style="width:100%;"/>
					</td>
					<td align="center" style="white-space: nowrap;">
						<spring:message code="page.monthUnPlanTaskEdit.name11" /><span style="color: #ff0000">*</span>：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="isCompanyLevel" name="isCompanyLevel" class="mini-combobox rxc" plugins="mini-combobox"
							   style="width:100%;height:34px"  label="<spring:message code="page.monthUnPlanTaskEdit.name11" />："  onvaluechanged="onPlanLevel"
							   length="50"
							   only_read="false" required="true" allowinput="false" mwidth="100"
							   wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
							   textField="text" valueField="key_" emptyText="<spring:message code="page.monthUnPlanTaskEdit.name12" />..."
							   url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YDJH-JHJB"
							   nullitemtext="<spring:message code="page.monthUnPlanTaskEdit.name12" />..." emptytext="<spring:message code="page.monthUnPlanTaskEdit.name12" />..."/>
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						<spring:message code="page.monthUnPlanTaskEdit.name13" />：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="finishStatus" name="finishStatus" class="mini-combobox rxc" plugins="mini-combobox"
							   style="width:100%;height:34px"  label="<spring:message code="page.monthUnPlanTaskEdit.name13" />：" onvaluechanged="remarkTip"
							   length="50"
							   only_read="false" required="false" allowinput="false" mwidth="100"
							   wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
							   textField="text" valueField="key_" emptyText="<spring:message code="page.monthUnPlanTaskEdit.name12" />..."
							   url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=WCQK"
							   nullitemtext="<spring:message code="page.monthUnPlanTaskEdit.name12" />..." emptytext="<spring:message code="page.monthUnPlanTaskEdit.name12" />..."/>
					</td>
					<td align="center" style="white-space: nowrap;">
						<spring:message code="page.monthUnPlanTaskEdit.name14" />：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="isDelayApply" name="isDelayApply" class="mini-combobox rxc" plugins="mini-combobox"
							   style="width:100%;height:34px"  label="<spring:message code="page.monthUnPlanTaskEdit.name13" />："
							   length="50"
							   only_read="false" required="false" allowinput="false" mwidth="100"
							   wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
							   textField="text" valueField="key_" emptyText="<spring:message code="page.monthUnPlanTaskEdit.name12" />..."
							   url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YESORNO"
							   nullitemtext="<spring:message code="page.monthUnPlanTaskEdit.name12" />..." emptytext="<spring:message code="page.monthUnPlanTaskEdit.name12" />..."/>
					</td>
				</tr>
				<tr>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						<spring:message code="page.monthUnPlanTaskEdit.name15" />：
					</td>
					<td align="center" colspan="3" rowspan="1">
						<input id="remark"  name="remark" class="mini-textbox rxc"  single="false" allowInput="true" style="width:100%;"/>
					</td>
				</tr>
				<tr id="fileTr" style="display: none">
					<td style="text-align: center;height: 200px"><spring:message code="page.monthUnPlanTaskEdit.name16" />：</td>
					<td colspan="3">
						<div style="margin-top: 1px;margin-bottom: 1px">
							<a id="addFile" class="mini-button"  onclick="addWorkFile()"><spring:message code="page.monthUnPlanTaskEdit.name17" /></a>
							<a class="mini-button " id="downDocument"  plain="true" onclick="fileDown()"><spring:message code="page.monthUnPlanTaskEdit.name18" /></a>
						</div>
						<div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
							 idField="id" url="${ctxPath}/rdmZhgl/core/monthWork/files/fileList.do?applyId=${applyObj.id}" autoload="true"
							 multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
							<div property="columns">
								<div type="indexcolumn" align="center" width="20"><spring:message code="page.monthUnPlanTaskEdit.name19" /></div>
								<div field="fileName" align="left" headerAlign="center" width="150"><spring:message code="page.monthUnPlanTaskEdit.name20" /></div>
								<div field="fileSize" align="center" headerAlign="center" width="60"><spring:message code="page.monthUnPlanTaskEdit.name21" /></div>
								<div field="fileDesc" align="center" headerAlign="center" width="100"><spring:message code="page.monthUnPlanTaskEdit.name22" /></div>
								<div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer"><spring:message code="page.monthUnPlanTaskEdit.name23" /></div>
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
					<a id="addItem" class="mini-button"  onclick="addItem()"><spring:message code="page.monthUnPlanTaskEdit.name24" /></a>
					<a id ="removeItem" class="mini-button btn-red" plain="true" onclick="removeItem()"><spring:message code="page.monthUnPlanTaskEdit.name25" /></a>
				</li>
			</ul>
		</div>
		<div class="mini-fit" style="height: 100%;margin-top: 10px">
			<div id="itemGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
				 url="${ctxPath}/rdmZhgl/core/monthUnPlanTask/items.do"
				 idField="id" allowAlternating="true" showPager="false" multiSelect="true" allowCellEdit="true"
				 allowCellSelect="true" allowCellWrap="true"
				 editNextOnEnterKey="true" editNextRowCell="true">
				<div property="columns">
					<div type="checkcolumn" width="10"></div>
					<div type="indexcolumn" headerAlign="center" align="center" width="20"><spring:message code="page.monthUnPlanTaskEdit.name19" /></div>
					<div field="workContent" displayfield="workContent" width="80" headerAlign="center" align="center"><spring:message code="page.monthUnPlanTaskEdit.name26" /><span
							style="color: #ff0000">*</span>
						<input property="editor" class="mini-textarea"   allowLimitValue="false"
							   required="false" only_read="false" allowinput="true"allowNull="true" value="null"  decimalPlaces="0"  style="width:100%;height:34px"/>
					</div>
					<div field="finishFlag" displayfield="finishFlag" width="80" headerAlign="center" align="center"><spring:message code="page.monthUnPlanTaskEdit.name27" /><span
							style="color: #ff0000">*</span>
						<input property="editor" class="mini-textarea"   allowLimitValue="false"
							   required="false" only_read="false" allowinput="true"allowNull="true" value="null"  decimalPlaces="0"  style="width:100%;height:34px"/>
					</div>
					<div field="responseUserId" displayfield="userName" width="30" headerAlign="center" align="center"><spring:message code="page.monthUnPlanTaskEdit.name28" /><span
							style="color: #ff0000">*</span>
						<input property="editor" class="mini-user" style="width:auto;"  single="false"/>
					</div>
					<div field="responseDeptId" displayfield="responseDeptId" width="40" headerAlign="center" align="center"><spring:message code="page.monthUnPlanTaskEdit.name29" />
						<input property="editor" class="mini-textbox"   allowLimitValue="false"
							   required="false" only_read="false" allowinput="true"allowNull="true" value="null"  decimalPlaces="0"  style="width:100%;height:34px"/>
					</div>
					<div field="sortIndex" displayfield="sortIndex" width="20" headerAlign="center" align="center"><spring:message code="page.monthUnPlanTaskEdit.name30" /><span
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
	var isLeader = ${isLeader};
	var fileListGrid = mini.get("fileListGrid");
	var currentUserName = "${currentUserName}";
	var currentTime = "${currentTime}";
	var currentUserId = "${currentUser.userId}";
	var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
</script>
</body>
</html>
