<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<title>标准专业领域维护</title>
	<%@include file="/commons/edit.jsp" %>
	<link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
	<script src="${ctxPath}/scripts/standardManager/fieldEdit.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div id="ToolBar" class="topToolBar" style="display: block">
	<div>
		<a id="save" class="mini-button" onclick="saveFieldData()"><spring:message code="page.fieldEdit.name" /></a>
		<a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()"><spring:message code="page.fieldEdit.name1" /></a>
	</div>
</div>
<div class="mini-fit">
	<div class="form-container" style="margin:0 auto; width: 100%;">
		<form id="fieldForm" method="post">
			<input id="fieldId" name="fieldId" class="mini-hidden"/>
			<table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
				<tr>
					<td align="center" style="white-space: nowrap;">
						<spring:message code="page.fieldEdit.name2" />：
					</td>
					<td align="left">
						<input name="fieldName" class="mini-textbox" style="width:100%;height:34px"/>
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						<spring:message code="page.fieldEdit.name3" />：
					</td>
					<td align="left">
						<input id="systemCategory" class="mini-combobox" name="systemCategoryId" textfield="systemCategoryName" valuefield="systemCategoryId" emptyText="<spring:message code="page.fieldEdit.name6" />..."
							   required="false" style="width: 100%" allowInput="false"/>
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						<spring:message code="page.fieldEdit.name4" />：
					</td>
					<td align="left">
						<input id="ywRespUserId"  name="ywRespUserId" textname="ywRespUserName" class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="<spring:message code="page.fieldEdit.name7" />"
							   mainfield="no"  single="true"  />
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						<spring:message code="page.fieldEdit.name5" />：
					</td>
					<td align="left">
						<input id="respUserId"  name="respUserId" textname="respUserName" class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="<spring:message code="page.fieldEdit.name7" />"
							   mainfield="no"  single="true"  />
					</td>
				</tr>
				</tbody>
			</table>
		</form>
	</div>

</div>
<script type="text/javascript">
	mini.parse();
	var jsUseCtxPath = "${ctxPath}";
	var fieldObj = ${fieldObj};
    var systemCategoryArr = ${systemCategoryArr};

    var fieldForm = new mini.Form("#fieldForm");
</script>
</body>
</html>