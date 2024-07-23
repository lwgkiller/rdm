<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib  prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib  prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html >
<head>
	<title>月度工作</title>
	<%@include file="/commons/list.jsp"%>
</head>
<body>
<div class="mini-toolbar">
	<div class="searchBox">
		<ul class="toolBtnBox">
			<a id="planApply" class="mini-button " style="margin-left: 10px;" plain="true" onclick="doPlanApply()"><spring:message code="page.monthWorkTab.name" /></a>
			<a id="finishApply" class="mini-button " style="margin-left: 10px;" plain="true" onclick="doFinishApply()"><spring:message code="page.monthWorkTab.name1" /></a>
		</ul>
		<form id="searchForm" class="search-form" style="">
			<ul>
				<li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.monthWorkTab.name2" />: </span>
					<input id="deptId" name="deptId" class="mini-dep rxc" plugins="mini-dep"
						   style="width:200px;height:34px" onvaluechanged="searchList()"
						   allowinput="false" textname="deptName" length="200" maxlength="200" minlen="0" single="false"
						   initlogindep="false"/>
				</li>
				<li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.monthWorkTab.name3" />: </span>
					<input name="responseMan" class="mini-textbox rxc" allowInput="true" style="width:100px;"/>
				</li>
				<li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.monthWorkTab.name4" />: </span>
					<input name="planResponseMan" class="mini-textbox rxc" allowInput="true" style="width:100px;"/>
				</li>
				<li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.monthWorkTab.name5" />: </span>
					<input id="finishStatus" name="finishStatus" class="mini-combobox rxc" plugins="mini-combobox"
						   style="width:100px;height:34px" label="<spring:message code="page.monthWorkTab.name5" />：" onvaluechanged="searchList()"
						   length="50" onvaluechanged="remarkTip"
						   only_read="false" required="false" allowinput="false" mwidth="100"
						   wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
						   textField="text" valueField="key_" emptyText="<spring:message code="page.monthWorkTab.name6" />..."
						   url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=WCQK"
						   nullitemtext="<spring:message code="page.monthWorkTab.name6" />..." emptytext="<spring:message code="page.monthWorkTab.name6" />..."/>
				</li>
				<li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.monthWorkTab.name7" />: </span>
					<input id="isCompanyLevel" name="isCompanyLevel" class="mini-combobox rxc" plugins="mini-combobox"
						   style="width:100px;height:34px" label="<spring:message code="page.monthWorkTab.name7" />："
						   length="50" onvaluechanged="searchList()"
						   only_read="false" required="false" allowinput="false" mwidth="100"
						   wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="true"
						   textField="text" valueField="key_" emptyText="<spring:message code="page.monthWorkTab.name6" />..."
						   url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YDJH-JHJB"
						   nullitemtext="<spring:message code="page.monthWorkTab.name6" />..." emptytext="<spring:message code="page.monthWorkTab.name6" />..."/>
				</li>
				<li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.monthWorkTab.name8" />: </span><input
						id="yearMonthStart" allowinput="false"  class="mini-monthpicker"
						style="width: 100px" name="yearMonthStart"/></li>
				<li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.monthWorkTab.name9" />: </span><input
						id="yearMonthEnd" allowinput="false"  class="mini-monthpicker"
						style="width: 100px" name="yearMonthEnd"/></li>
			</ul>
		</form>
	</div>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="systemTab" class="mini-tabs" activeIndex="0" style="width:100%;height:100%;">
		<div id="baseInfo" title="<spring:message code="page.monthWorkTab.name10" />" name="baseInfo" url="${ctxPath}/rdmZhgl/core/monthWork/getListPage.do"></div>
		<div id="sgyk" title="<spring:message code="page.monthWorkTab.name11" />"  url="${ctxPath}/rdmZhgl/core/monthUnProjectPlan/getListPage.do"></div>
		<div id="technology" title="<spring:message code="page.monthWorkTab.name12" />"  url="${ctxPath}/rdmZhgl/core/monthUnPlanTask/getListPage.do"></div>
	</div>
</div>
<script>
	mini.parse();
	var ctxPath="${ctxPath}";
	var permission = ${permission};
	if (!permission) {
		mini.get("planApply").setEnabled(false);
		mini.get("finishApply").setEnabled(false);
	}
	$(function () {
		var year = new Date().getFullYear();
		var month = new Date().getMonth();
		month = month +1;
		var nowDate = year+"-"+month;
		mini.get('yearMonthStart').setValue(nowDate);
		mini.get('yearMonthEnd').setValue(nowDate);
	})
	function searchList() {

	}
	//新增计划审批流程
	function doPlanApply() {
		var url = ctxPath + "/bpm/core/bpmInst/YDGZJHSP/start.do?applyType=0";
		var winObj = window.open(url);
		var loop = setInterval(function () {
			if (winObj.closed) {
				clearInterval(loop);
			}
		}, 1000);
	}
	//新增计划审批流程
	function doFinishApply() {
		var url = ctxPath + "/bpm/core/bpmInst/YDGZJHSP/start.do?applyType=1";
		var winObj = window.open(url);
		var loop = setInterval(function () {
			if (winObj.closed) {
				clearInterval(loop);
			}
		}, 1000);
	}
</script>

</body>
</html>
