
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>项目级别划分配置</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/xcmgProjectManager/projectConfig.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
	<ul class="toolBtnBox">
		<li>
			<a class="mini-button" iconCls="icon-reload" onclick="refreshLevelDivide()" plain="true"><spring:message code="page.configLevelDivide.name" /></a>
			<a class="mini-button" iconCls="icon-save" onclick="saveLevelDivide()" plain="true"><spring:message code="page.configLevelDivide.name1" /></a>
		</li>

	</ul>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="levelDivideListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
		 url="${ctxPath}/xcmgProjectManager/core/config/levelDivideList.do" idField="levelId"
		  allowAlternating="true"  showPager="false"
		allowCellEdit="true" allowCellSelect="true" >
		<div property="columns">
			<div field="levelName" width="120" headerAlign="center" align="center" ><spring:message code="page.configLevelDivide.name2" />
				<input property="editor" class="mini-textbox" readonly/>
			</div>
			<div field="minScore" width="120" headerAlign="center" align="center" ><spring:message code="page.configLevelDivide.name3" /><span style="color: #ff0000">*</span>
				<input property="editor" class="mini-textbox"/>
			</div>
			<div field="maxScore" width="120" headerAlign="center" align="center" ><spring:message code="page.configLevelDivide.name4" /><span style="color: #ff0000">*</span>
				<input property="editor" class="mini-textbox"/>
			</div>
			<div field="creator" width="120" headerAlign="center" align="center" ><spring:message code="page.configLevelDivide.name5" />
				<input property="editor" class="mini-textbox" readonly/>
			</div>
			<div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="120" headerAlign="center" ><spring:message code="page.configLevelDivide.name6" />
				<input property="editor" class="mini-textbox" readonly/>
			</div>
			<div field="updator" width="120" headerAlign="center" align="center" ><spring:message code="page.configLevelDivide.name7" />
				<input property="editor" class="mini-textbox" readonly/>
			</div>
			<div field="UPDATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="120" headerAlign="center" ><spring:message code="page.configLevelDivide.name8" />
				<input property="editor" class="mini-textbox" readonly/>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var isManager=whetherIsProjectManager(${currentUserRoles});

    var levelDivideListGrid = mini.get("levelDivideListGrid");
    levelDivideListGrid.load();

    levelDivideListGrid.on("beforeload", function (e) {
        if (levelDivideListGrid.getChanges().length > 0) {
            if (confirm(configLevelDivide_name)) {
                e.cancel = true;
            }
        }
    });
    levelDivideListGrid.on("cellcommitedit", function (e) {
        if (e.field == "minScore"||e.field == "maxScore") {
            var r = /^\+?[1-9][0-9]*$/;　　//正整数
            if(!r.test(e.value)){
                mini.alert(configLevelDivide_name1);
                e.value='';
            }
        }
    });

    levelDivideListGrid.on("cellbeginedit", function (e) {
        var record = e.record;
        if(e.field == "minScore"||e.field == "maxScore") {
            e.editor.setEnabled(false);
			if(isManager) {
                e.editor.setEnabled(true);
			}
        }
    });
</script>

</body>
</html>