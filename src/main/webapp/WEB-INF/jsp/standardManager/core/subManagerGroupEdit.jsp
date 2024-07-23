<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<title>标准子管理员用户组维护</title>
	<%@include file="/commons/edit.jsp" %>
	<link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
	<script src="${ctxPath}/scripts/standardManager/subManagerGroupEdit.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div id="ToolBar" class="topToolBar" style="display: block">
	<div>
		<a id="save" class="mini-button" onclick="saveData()"><spring:message code="page.subManagerGroupEdit.name" /></a>
		<a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()"><spring:message code="page.subManagerGroupEdit.name1" /></a>
	</div>
</div>
<div class="mini-fit">
	<div class="form-container" style="margin:0 auto; width: 100%;">
		<form id="groupForm" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
				<tr>
					<td align="center" style="white-space: nowrap;">
						<spring:message code="page.subManagerGroupEdit.name2" />：
					</td>
					<td align="left">
						<input name="groupName" class="mini-textbox" style="width:100%;height:34px"/>
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						<spring:message code="page.subManagerGroupEdit.name3" />：
					</td>
					<td align="left">
						<input id="systemCategory" class="mini-combobox" name="systemCategoryId" textfield="systemCategoryName" valuefield="systemCategoryId" emptyText="请选择..."
							   required="false" style="width: 100%" allowInput="false" onvaluechanged="systemCategoryChanged()"/>
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						<spring:message code="page.subManagerGroupEdit.name4" />：
					</td>
					<td align="left">
						<input id="systemTreeSelectId" style="width:100%;" class="mini-buttonedit" name="systemId" textname="systemName" allowInput="false" onbuttonclick="selectSystem()"/>
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						<spring:message code="page.subManagerGroupEdit.name5" />：
					</td>
					<td align="left">
						<input id="recUserSelectId"  name="recUser" textname="recUserNames" class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="用户"
							   mainfield="no"  single="false"  />
					</td>
				</tr>
				</tbody>
			</table>
		</form>
	</div>

	<div id="selectSystemWindow" title="<spring:message code="page.subManagerGroupEdit.name6" />" class="mini-window" style="width:750px;height:450px;"
		 showModal="true" showFooter="true" allowResize="true">
		<div class="mini-toolbar" style="text-align:center;line-height:30px;"
			 borderStyle="border-left:0;border-top:0;border-right:0;">
			<span style="font-size: 14px;color: #777"><spring:message code="page.subManagerGroupEdit.name7" />: </span>
			<input class="mini-textbox" width="130" id="filterNameId" onenter="filterSystemTree()"/>
			<a class="mini-button" iconCls="icon-search" plain="true" onclick="filterSystemTree()"></a>
		</div>
		<div class="mini-fit">
			<ul id="selectSystemTree" class="mini-tree"
				style="width:100%;height:98%;padding:5px;"
				showTreeIcon="true" textField="systemName" expandOnLoad="0" idField="id" parentField="parentId"
				resultAsTree="false"
			></ul>
		</div>
		<div property="footer" style="padding:5px;height: 40px">
			<table style="width:100%;height: 100%">
				<tr>
					<td style="width:120px;text-align:center;">
						<input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.subManagerGroupEdit.name8" />" onclick="selectSystemOK()"/>
						<input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.subManagerGroupEdit.name9" />" onclick="selectSystemHide()"/>
					</td>
				</tr>
			</table>
		</div>
	</div>
</div>
<script type="text/javascript">
	mini.parse();
	var jsUseCtxPath = "${ctxPath}";
	var applyObj = ${applyObj};
	var action = '${action}';
    var systemCategoryIdSet = ${systemCategoryIdSet};
    var systemArray=${systemArray};

    var selectSystemTree=mini.get("selectSystemTree");
    var groupForm = new mini.Form("#groupForm");
    var selectSystemWindow=mini.get("selectSystemWindow");
</script>
</body>
</html>