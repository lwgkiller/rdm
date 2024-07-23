<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>标准体系管理</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/standardManager/standardSystemMg.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/standardManager/standardUtil.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/common/UUID.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
	<div class="mini-toolbar" >
		<ul class="toolBtnBox">
			<li id="operateTree">
				<a class="mini-button" onclick="onAddBefore()"><spring:message code="page.standardSystemTabPage.name1" /></a>
				<a class="mini-button" onclick="onAddAfter()"><spring:message code="page.standardSystemTabPage.name2" /></a>
				<a class="mini-button" onclick="onAddSubNode()"><spring:message code="page.standardSystemTabPage.name3" /></a>
				<a class="mini-button" onclick="onEditNode('edit')"><spring:message code="page.standardSystemTabPage.name4" /></a>
				<a class="mini-button" onclick="onMoveNode()"><spring:message code="page.standardSystemTabPage.name5" /></a>
				<a class="mini-button btn-red" onclick="onRemoveNode()"><spring:message code="page.standardSystemTabPage.name6" /></a>
				<a class="mini-button" iconCls="icon-save" onclick="onSaveNode()"><spring:message code="page.standardSystemTabPage.name7" /></a>
				<p style="display: inline-block;color: red;font-size:14px;vertical-align: middle">（<image src="${ctxPath}/styles/images/warn.png" style="margin-right:5px;vertical-align: middle;height: 15px"/><spring:message code="page.standardSystemTabPage.name8" />）</p>
			</li>
			<li style="float: right">
				<span style="padding-left:5px;"><spring:message code="page.standardSystemTabPage.name9" />：</span>
				<input class="mini-textbox" width="120" id="filterNameId" onenter="filterSystemTree()" />
				<span style="padding-left:5px;"><spring:message code="page.standardSystemTabPage.name10" />：</span>
				<input class="mini-textbox" width="120" id="filterNumberId" onenter="filterSystemTree()" />
				<a class="mini-button" iconCls="icon-search" plain="true" onclick="filterSystemTree()"><spring:message code="page.standardSystemTabPage.name11" /></a>
				<a class="mini-button" onclick="expandAll()"><spring:message code="page.standardSystemTabPage.name12" /></a>
				<a class="mini-button" onclick="collapseAll()"><spring:message code="page.standardSystemTabPage.name13" /></a>
				<a class="mini-button" iconCls="icon-reload" onclick="refreshSystemTree()"><spring:message code="page.standardSystemTabPage.name14" /></a>
				<a class="mini-button" onclick="exportSystem()"><spring:message code="page.standardSystemTabPage.name15" /></a>
			</li>
		</ul>
	</div>
	<ul id="systemTree" class="mini-tree" url="${ctxPath}/standardManager/core/standardSystem/treeQuery.do?systemCategoryId=${tabName}"
		style="width:100%;height:90%;padding:5px;" onnodedblclick="leafNodeDbClick()"
		showTreeIcon="true" textField="systemName" expandOnLoad="0" idField="id" parentField="parentId" resultAsTree="false"
	></ul>

	<div id="moveWindow" title="<spring:message code="page.standardSystemTabPage.name16" />" class="mini-window" style="width:750px;height:450px;"
		 showModal="true" showFooter="true" allowResize="true">
		<ul id="moveTree" class="mini-tree" style="width:100%;height:100%;"
			showTreeIcon="true" textField="systemName" idField="id" expandOnLoad="0" parentField="parentId" resultAsTree="false">
		</ul>
		<div property="footer" style="padding:5px;height: 30px">
			<table style="width:100%;height: 100%">
				<tr>
					<td>
						<span style="font-size: 15px"><spring:message code="page.standardSystemTabPage.name17" />：</span>
						<select id="moveAction" style="height: 25px;width: 90px">
							<option value="before" ><spring:message code="page.standardSystemTabPage.name18" /></option>
							<option value="after"><spring:message code="page.standardSystemTabPage.name19" /></option>
							<option value="add" selected><spring:message code="page.standardSystemTabPage.name20" /></option>
						</select>
					</td>
					<td style="width:200px;text-align:right;">
						<input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.standardSystemTabPage.name21" />" onclick="okWindow()"/>
						<input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.standardSystemTabPage.name22" />" onclick="hideWindow()"/>
					</td>
				</tr>
			</table>
		</div>
	</div>

	<!--导出Excel相关HTML-->
	<form id="excelForm" action="${ctxPath}/standardManager/core/standardSystem/treeExport.do?systemCategoryId=${tabName}" method="post" target="excelIFrame">
		<input type="hidden" name="pageIndex" id="pageIndex"/>
		<input type="hidden" name="pageSize" id="pageSize"/>
		<input type="hidden" name="filter" id="filter"/>
	</form>
	<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var tabName="${tabName}";
    var currentUserRoles=${currentUserRoles};
    var isPointManager=whetherIsPointStandardManager(tabName,currentUserRoles);
    var systemTree=mini.get("systemTree");
    var moveWindow = mini.get("moveWindow");
    var moveTree = mini.get("moveTree");
	if(!isPointManager) {
	    $("#operateTree").hide();
    }

</script>
</body>
</html>