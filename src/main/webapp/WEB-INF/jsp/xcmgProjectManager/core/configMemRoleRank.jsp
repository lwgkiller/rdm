
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>成员角色职级要求配置</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/xcmgProjectManager/projectConfig.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
	<ul class="toolBtnBox">
		<li>
			<a class="mini-button" iconCls="icon-reload" onclick="refreshMemRoleRank()" plain="true"><spring:message code="page.configMemRoleRank.name" /></a>
			<a class="mini-button" iconCls="icon-save" onclick="saveMemRoleRank()" plain="true"><spring:message code="page.configMemRoleRank.name1" /></a>
		</li>

	</ul>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="memRoleRankListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
		 url="${ctxPath}/xcmgProjectManager/core/config/memRoleRankList.do" idField="roleId"
		  allowAlternating="true"  showPager="false"
		allowCellEdit="true" allowCellSelect="true" >
		<div property="columns">
			<div field="roleName" width="120" headerAlign="center" align="center" ><spring:message code="page.configMemRoleRank.name2" />
				<input property="editor" class="mini-textbox" readonly/>
			</div>
			<div field="levelName" width="120" headerAlign="center" align="center" ><spring:message code="page.configMemRoleRank.name3" />
				<input property="editor" class="mini-textbox" readonly/>
			</div>
			<div field="minRankKey" displayField="minRankName" width="120" headerAlign="center" align="center" ><spring:message code="page.configMemRoleRank.name4" /><span style="color: #ff0000">*</span>
				<input property="editor" class="mini-combobox" style="width:90%;"
					   textField="zjName" valueField="zjKey" emptyText="<spring:message code="page.configMemRoleRank.name5" />..."
					   allowInput="false" showNullItem="false" nullItemText="<spring:message code="page.configMemRoleRank.name5" />..." />
			</div>
			<div field="creator" width="120" headerAlign="center" align="center" ><spring:message code="page.configMemRoleRank.name6" />
				<input property="editor" class="mini-textbox" readonly/>
			</div>
			<div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="120" headerAlign="center" ><spring:message code="page.configMemRoleRank.name7" />
				<input property="editor" class="mini-textbox" readonly/>
			</div>
			<div field="updator" width="120" headerAlign="center" align="center" ><spring:message code="page.configMemRoleRank.name8" />
				<input property="editor" class="mini-textbox" readonly/>
			</div>
			<div field="UPDATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="120" headerAlign="center" ><spring:message code="page.configMemRoleRank.name9" />
				<input property="editor" class="mini-textbox" readonly/>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var isManager=whetherIsProjectManager(${currentUserRoles});
	var zjList=${zjList};


    var memRoleRankListGrid = mini.get("memRoleRankListGrid");
    memRoleRankListGrid.load();

    memRoleRankListGrid.on("beforeload", function (e) {
        if (memRoleRankListGrid.getChanges().length > 0) {
            if (confirm(configMemRoleRank_name1)) {
                e.cancel = true;
            }
        }
    });

    memRoleRankListGrid.on("cellbeginedit", function (e) {
		if(!isManager) {
			e.editor.setEnabled(false);
		}
    });

    memRoleRankListGrid.on("cellbeginedit", function (e) {
        var record = e.record;
		if (e.field == "minRankKey") {
		   e.editor.setData(zjList);
		}
    });
</script>

</body>
</html>