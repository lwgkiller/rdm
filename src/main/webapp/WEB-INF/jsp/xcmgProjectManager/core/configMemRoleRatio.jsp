
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>成员角色系数配置</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/xcmgProjectManager/projectConfig.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
	<ul class="toolBtnBox">
		<li>
			<a class="mini-button" iconCls="icon-reload" onclick="refreshMemRoleRatio()" plain="true"><spring:message code="page.configMemRoleRatio.name1" /></a>
			<a class="mini-button" iconCls="icon-save" onclick="saveMemRoleRatio()" plain="true"><spring:message code="page.configMemRoleRatio.name2" /></a>
		</li>

	</ul>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="memRoleRatioListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
		 url="${ctxPath}/xcmgProjectManager/core/config/memRoleRatioList.do" idField="roleId"
		  allowAlternating="true"  showPager="false"
		allowCellEdit="true" allowCellSelect="true" >
		<div property="columns">
			<div field="roleName" width="120" headerAlign="center" align="center" ><spring:message code="page.configMemRoleRatio.name3" />
				<input property="editor" class="mini-textbox" readonly/>
			</div>
			<div field="minRatio" width="120" headerAlign="center" align="center" ><spring:message code="page.configMemRoleRatio.name4" /><span style="color: #ff0000">*</span>
				<input property="editor" class="mini-textbox"/>
			</div>
			<div field="maxRatio" width="120" headerAlign="center" align="center" ><spring:message code="page.configMemRoleRatio.name5" /><span style="color: #ff0000">*</span>
				<input property="editor" class="mini-textbox"/>
			</div>
			<div field="creator" width="120" headerAlign="center" align="center" ><spring:message code="page.configMemRoleRatio.name6" />
				<input property="editor" class="mini-textbox" readonly/>
			</div>
			<div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="120" headerAlign="center" ><spring:message code="page.configMemRoleRatio.name7" />
				<input property="editor" class="mini-textbox" readonly/>
			</div>
			<div field="updator" width="120" headerAlign="center" align="center" ><spring:message code="page.configMemRoleRatio.name8" />
				<input property="editor" class="mini-textbox" readonly/>
			</div>
			<div field="UPDATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="120" headerAlign="center" ><spring:message code="page.configMemRoleRatio.name9" />
				<input property="editor" class="mini-textbox" readonly/>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var isManager=whetherIsProjectManager(${currentUserRoles});

    var memRoleRatioListGrid = mini.get("memRoleRatioListGrid");
    memRoleRatioListGrid.load();

    memRoleRatioListGrid.on("beforeload", function (e) {
        if (memRoleRatioListGrid.getChanges().length > 0) {
            if (confirm(configMemRoleRatio_name)) {
                e.cancel = true;
            }
        }
    });
    memRoleRatioListGrid.on("cellcommitedit", function (e) {
        if (e.field == "minRatio"||e.field == "maxRatio") {
            var r = /^\d+(\.\d+)?$/;　　//非负浮点数
            if(!r.test(e.value)){
                mini.alert(configMemRoleRatio_name1);
                e.value='';
            }
        }
    });

    memRoleRatioListGrid.on("cellbeginedit", function (e) {
		if(!isManager) {
			e.editor.setEnabled(false);
			return;
		}
        var record = e.record;
		if(e.field == "minRatio"||e.field == "maxRatio") {
		    if(record.roleName=='项目指导人') {
                e.editor.setEnabled(false);
			} else {
                e.editor.setEnabled(true);
			}
		}
    });
</script>

</body>
</html>