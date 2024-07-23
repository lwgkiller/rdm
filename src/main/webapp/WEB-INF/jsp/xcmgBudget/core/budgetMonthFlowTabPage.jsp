<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib  prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib  prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html >
<head>
	<title>挖掘机械研究院预算申报审批管理</title>
	<%@include file="/commons/edit.jsp"%>
	<script src="${ctxPath}/scripts/xcmgbudget/budgetMonthFlowTab.js?version=${static_res_version}" type="text/javascript"></script>
	<style>
		.table-detail > tbody > tr > td{
			border: 1px solid #ccc;
		}
	</style>
</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
	<div>
		<a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
		<a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
	</div>
</div>
<div class="mini-fit" style="height: 100%;">
	<div class="form-container" style="margin: 0;padding-top: 0">
		<form id="formProject" method="post">
			<input id="budgetId" name="budgetId" class="mini-hidden"/>
			<input id="instId" name="instId" class="mini-hidden"/>
			<table class="table-detail" cellspacing="1" cellpadding="0">
				<tr>
					<td style="width: 10%;text-align: center">预算申报月份：</td>
					<td style="width: 10%;">
						<input id="flowYearMonth" name="flowYearMonth" class="mini-monthpicker" onfocus="this.blur()" style="width:98%;"/>
					</td>
					<td style="width: 10%;text-align: center">申报部门：</td>
					<td style="width: 10%">
						<input id="flowDeptId" name="flowDeptId" class="mini-dep rxc" plugins="mini-dep" readonly
							   style="width:98%;height:34px" allowinput="false" textname="flowDeptName" length="200" maxlength="200" minlen="0" single="true" initlogindep="false"/>
					</td>
					<td style="width: 10%;text-align: center">申报人：</td>
					<td style="width: 10%">
						<input id="userId" name="userId" textname="userName" class="mini-user rxc" allowinput="false" length="200" maxlength="200" readonly
							   style="width:98%;height:34px">
					</td>
					<td style="width: 10%;text-align: center">项目类型：</td>
					<td style="width: 10%">
						<input id="budgetType" name="budgetType" class="mini-combobox" style="width:98%;"
							   textField="name" valueField="id" emptyText="请选择..." data="[{'name':'项目类预算','id':'xml'},{'name':'非项目类预算','id':'fxml'}]"
							   required="false" allowInput="false" showNullItem="false" nullItemText="请选择..." onvaluechanged="changeProjectTr()"/>
					</td>
				</tr>
				<tr id="projectTr" style="display:none">
					<input id="projectId" name="projectId" class="mini-hidden"/>
<%--					<td style="width: 10%;text-align: center">关联项目编号：</td>--%>
<%--					<td style="width: 10%">--%>
<%--						<input id="number" name="number" class="mini-textbox" style="width:98%;height:34px" enabled="false">--%>
<%--					</td>--%>
					<td style="width: 10%;text-align: center">关联项目名称：</td>
					<td style="width:auto">
<%--						<input id="projectName" name="projectName" class="mini-textbox" textname="projectName" style="width:84%;height:34px" enabled="false">--%>
						<input id="projectName" name="projectName" textname="projectName" class="mini-buttonedit" style="width:98%;height:34px"
							   allowInput="false" onbuttonclick="addRelatedProject()" showClose="true" oncloseclick="relatedCloseClick()"/>
					</td>
					<td style="width: 10%;text-align: center">项目负责人：</td>
					<td style="width: 10%">
						<input id="respId" name="respId" class="mini-textbox" style="width:98%;height:34px" enabled="false">
					</td>
					<td style="width: 10%;text-align: center">自筹财务订单号：</td>
					<td style="width: 10%">
						<input id="cwddh" name="cwddh" class="mini-textbox" style="width:98%;height:34px" enabled="false">
					</td>
					<td style="width: 10%;text-align: center">国拨财务订单号：</td>
					<td style="width: 10%">
						<input id="gbcwddh" name="gbcwddh" class="mini-textbox" style="width:98%;height:34px" enabled="false">
					</td>
				</tr>
			</table>
		</form>
		<div id="systemTab" class="mini-tabs" activeIndex="0" style="width:100%;height:94%;margin-top: 5px"></div>
	</div>
</div>
<div id="projectWindow" title="关联项目信息选择窗口" class="mini-window" style="width:1200px;height:700px;"
	 showModal="true" showFooter="false" allowResize="true">
	<div class="mini-toolbar" >
		<div class="searchBox">
			<form id="searchGrid" class="search-form" style="margin-bottom: 25px">
				<ul>
					<li style="margin-right: 15px">
						<span class="text" style="width:auto">项目名称: </span>
						<input class="mini-textbox" id="relatedProjectName" name="relatedProjectName" />
					</li>
					<li style="margin-right: 15px">
						<span class="text" style="width:auto">项目编号: </span>
						<input class="mini-textbox" id="projectNumber" name="projectNumber">
					</li>
					<li style="margin-right: 15px">
						<a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchProcessData()">查询</a>
						<a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="cleanProcessData()">清空查询</a>
					</li>
					<li style="display: inline-block;float: right;">
							<a id="importBtn" class="mini-button" onclick="choseRelatedProject()">确认</a>
							<a id="closeProjectWindow" class="mini-button btn-red" onclick="closeProjectWindow()">关闭</a>
					</li>
				</ul>

			</form>
			<span style="color: red">（仅审批中有财务订单号的项目可作为关联项目）</span>
		</div>
	</div>
	<div class="mini-fit" style="height: 100%;">
		<div id="projectListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true" idField="projectId" allowCellWrap="true"
			 url="${ctxPath}/xcmgBudget/core/budgetMonth/getProjectList.do"
			 multiSelect="false" showColumnsMenu="true" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
			<div property="columns">
				<div type="checkcolumn" width="20"></div>
				<div field="projectId" visible="false"></div>
				<div type="indexcolumn" headerAlign="center" align="center" width="30">序号</div>
				<div field="projectName"  sortField="projectName"  width="170" headerAlign="center" align="center" allowSort="true">项目名称</div>
				<div field="number"  sortField="number" width="110" headerAlign="center" align="center" allowSort="true">项目编号</div>
				<div field="cwddh" sortField="cwddh" width="80"  allowSort="true" headerAlign="center" align="center" allowSort="true">自筹财务订单号</div>
				<div field="gbcwddh" sortField="gbcwddh" width="80"  allowSort="true" headerAlign="center" align="center" allowSort="true">国拨财务订单号</div>
				<div field="respMan" sortField="respMan" width="50" headerAlign="center" align="center" allowSort="false">项目负责人</div>
				<div field="status" sortField="status" width="50" allowSort="true" headerAlign="center" align="center"  renderer="onStatusRenderer">项目状态</div>
			</div>
		</div>
	</div>
</div>
<script>
    mini.parse();
	var ctxPath="${ctxPath}";
	var bigTypeArray=${bigTypeArray};
    var applyObj=${applyObject};
    var nodeVars =${nodeVars};
    var action="${action}";
    var status="${status}";
	var currentUserNo = "${currentUserNo}";
	var currentUserId = "${currentUserId}";
    var formProject = new mini.Form("formProject");
    var systemTab=mini.get("systemTab");
	var isysManager=${isysManager};
	var projectWindow = mini.get("projectWindow");
	var projectListGrid=mini.get("projectListGrid");

    function activeChanged() {
        var flowYearMonth = mini.get("flowYearMonth").getText();
        if(!flowYearMonth) {
            return;
        }
        var activeTab = systemTab.getActiveTab();
        var newUrl='';
        var name=activeTab.name;
        var type=name.split("_")[0];
        var flowDeptId = mini.get("flowDeptId").getValue();
        var flowDeptName =  mini.get("flowDeptId").getText();
		var budgetType =  mini.get("budgetType").getValue();
		var budgetId =  mini.get("budgetId").getValue();
        var bigTypeId=name.split("_")[1];
        var bigTypeName=activeTab.title;
        var projectId = mini.get("projectId").getValue();
        //是否能编辑预算数据
		var editBudget=false;

		if(action=='edit' || action=='task'&&nodeVars.stageName && nodeVars.stageName == 'bianzhi' || action=='update' && isysManager){
			editBudget=true;
		}
		newUrl=ctxPath+"/xcmgBudget/core/budgetMonthFlow/cgTabPage.do?yearMonth="+flowYearMonth+"&deptId="+flowDeptId+"&deptName="+encodeURIComponent(flowDeptName)+"&bigTypeId="+bigTypeId+"&bigTypeName="+encodeURIComponent(bigTypeName)+"&editBudget="+editBudget+"&budgetType="+budgetType+"&budgetId="+budgetId+"&action="+action+"&projectId="+projectId;
		systemTab.updateTab(activeTab,{url:newUrl,loadedUrl:newUrl});
        systemTab.reloadTab(activeTab);
    }

	function onStatusRenderer(e) {
		var record = e.record;
		var status = record.status;
		var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
			{'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
			{'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},
			{'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
			{'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},
			{'key': 'PENDING', 'value': '挂起', 'css': 'gray'}
		];

		return $.formatItemValue(arr, status);
	}

</script>

</body>
</html>