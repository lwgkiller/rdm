
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>用户组人员列表</title>
	<%@include file="/commons/list.jsp"%>
</head>
<body>
<div class="mini-toolbar">
	<div class="searchBox">
		<form id="searchForm" class="search-form" style="margin-bottom: 25px">
			<input id="groupId" name="groupId" class="mini-hidden"/>
			<ul>
				<li style="margin-left: 15px"><span class="text" style="width:auto"><spring:message code="page.subManagerUserList.name" />: </span><input
						class="mini-textbox" style="width: 150px" name="userName"/></li>
				<li style="margin-left: 15px">
					<a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.subManagerUserList.name1" /></a>
				</li>
			</ul>
		</form>
	</div>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="groupUserListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
		 url="${ctxPath}/standardManager/core/subManagerUser/groupUserList.do" idField="id" showPager="true"
		 multiSelect="true" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
		<div property="columns">
			<div field="userName" width="80" headerAlign="center" align="center" ><spring:message code="page.subManagerUserList.name" /></div>
			<div field="deptName" width="50" headerAlign="center" align="center" allowSort="true"><spring:message code="page.subManagerUserList.name2" /></div>
			<div field="createName" width="50" headerAlign="center" align="center" ><spring:message code="page.subManagerUserList.name3" /></div>
			<div field="CREATE_TIME_" width="60" headerAlign="center" align="center" ><spring:message code="page.subManagerUserList.name4" /></div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var groupUserListGrid=mini.get("groupUserListGrid");
    var groupId = "${groupId}";
	$(function () {
		mini.get('groupId').setValue(groupId);
		searchFrm();
	});
</script>
<redxun:gridScript gridId="groupUserListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>