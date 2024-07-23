<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>用户组人员列表</title>
	<%@include file="/commons/list.jsp"%>
    <script src="${ctxPath}/scripts/standardManager/fieldUserList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
	<div class="searchBox">
		<form id="searchForm" class="search-form" style="margin-bottom: 25px">
			<input id="fieldId" name="fieldId" class="mini-hidden"/>
			<ul>
				<li style="margin-left: 10px"><span class="text" style="width:auto"><spring:message code="page.fieldUserList.name" />: </span>
					<input class="mini-textbox" style="width: 150px" name="userName"/>
                </li>
				<li style="margin-left: 10px">
                    <a class="mini-button"plain="true" onclick="searchFrm()"><spring:message code="page.fieldUserList.name1" /></a>
				</li>
                <div style="display: inline-block" class="separator"></div>
				<li>
					<a class="mini-button" style="margin-left: 10px"iconCls="icon-add" onclick="addUser()"><spring:message code="page.fieldUserList.name2" /></a>
					<a class="mini-button btn-red" style="margin-left: 10px" plain="true" onclick="removeUser()"><spring:message code="page.fieldUserList.name3" /></a>
					<a class="mini-button" style="margin-left: 10px" iconCls="icon-save" onclick="saveUser()"><spring:message code="page.fieldUserList.name4" /></a>
					<p style="display: inline-block;color: #888;font-size:15px;vertical-align: middle">（<image src="${ctxPath}/styles/images/notice.png" style="margin-right:5px;vertical-align: middle;height: 15px"/><spring:message code="page.fieldUserList.name5" />）</p>
				</li>
			</ul>
		</form>
	</div>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="fieldUserListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
		 url="${ctxPath}/standardManager/core/standardField/fieldUserList.do" idField="id" showPager="true"
		 multiSelect="true" showColumnsMenu="false" allowCellEdit="true" allowCellSelect="true"
		 sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
		<div property="columns">
			<div type="checkcolumn" width="15%"></div>
			<div field="userId"  displayField="userName" headerAlign="center" width="45%"><spring:message code="page.fieldUserList.name" />
				<input property="editor" class="mini-user rxc" plugins="mini-user"  style="width:90%;height:34px;" allowinput="false" length="50" maxlength="50"  mainfield="no"  single="true" />
			</div>
			<div field="fieldId"  headerAlign="left" visible="false">fieldId</div>
			<div field="userDepName" width="40%" headerAlign="center" align="center" allowSort="false"><spring:message code="page.fieldUserList.name6" />
				<input property="editor" class="mini-textbox" readonly/></div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var fieldUserListGrid=mini.get("fieldUserListGrid");
    var fieldId = "${fieldId}";

    fieldUserListGrid.on("cellcommitedit", function (e) {
        var grid = e.sender,
            record = e.record;
        if (e.field == "userId") {
            var userId=e.value;
            //判断是否已经有这个成员
            var gridData=grid.data;
            for(var i=0;i<gridData.length;i++) {
                if(gridData[i].userId==userId) {
                    mini.alert(fieldUserList_bj);
                    grid.removeRow (record);
                    return;
                }
            }
            var userInfo=getUserInfoById(userId);
            grid.updateRow(record, {userDepName:userInfo.mainDepName});
        }
    });

</script>
<redxun:gridScript gridId="fieldUserListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>