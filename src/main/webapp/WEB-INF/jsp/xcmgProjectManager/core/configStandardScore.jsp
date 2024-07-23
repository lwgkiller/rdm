
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>项目标准分值配置</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/xcmgProjectManager/projectConfig.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
	<ul class="toolBtnBox">
		<li style="float: left">
			<span class="text" style="width:auto"><spring:message code="page.configStandardScore.name" />: </span>
			<input id="projectCategory" name="categoryId" class="mini-combobox" style="width:180px;"
				   textField="categoryName" valueField="categoryId"
				   required="false" allowInput="false" showNullItem="false" onvaluechanged="queryStandardScoreByCategory()"/>
		</li>
		<span class="separator"></span>
		<li>
			<a class="mini-button" iconCls="icon-reload" onclick="refreshStandardScore()" plain="true"><spring:message code="page.configStandardScore.name1" /></a>
			<a class="mini-button" iconCls="icon-save" onclick="saveStandardScore()" plain="true"><spring:message code="page.configStandardScore.name2" /></a>
		</li>

	</ul>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="standardScoreListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
		  idField="id" allowAlternating="true"  showPager="false" allowCellEdit="true" allowCellSelect="true" >
		<div property="columns">
			<div field="levelName" width="120" headerAlign="center" align="center" ><spring:message code="page.configStandardScore.name3" />
				<input property="editor" class="mini-textbox" readonly/>
			</div>
			<div field="score" width="120" headerAlign="center" align="center" ><spring:message code="page.configStandardScore.name4" /><span style="color: #ff0000">*</span>
				<input property="editor" class="mini-textbox"/>
			</div>
			<div field="creator" width="120" headerAlign="center" align="center" ><spring:message code="page.configStandardScore.name5" />
				<input property="editor" class="mini-textbox" readonly/>
			</div>
			<div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="120" headerAlign="center" ><spring:message code="page.configStandardScore.name6" />
				<input property="editor" class="mini-textbox" readonly/>
			</div>
			<div field="updator" width="120" headerAlign="center" align="center" ><spring:message code="page.configStandardScore.name7" />
				<input property="editor" class="mini-textbox" readonly/>
			</div>
			<div field="UPDATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="120" headerAlign="center" ><spring:message code="page.configStandardScore.name8" />
				<input property="editor" class="mini-textbox" readonly/>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var url="${ctxPath}/xcmgProjectManager/core/config/standardScoreList.do?categoryId=";
    var isManager=whetherIsProjectManager(${currentUserRoles});
    //设置项目类别下拉及初始值
    var smallCategory=${smallCategoryInfos};
    mini.get("projectCategory").load(smallCategory);
    var categoryValue="";
    for(var i=0;i<smallCategory.length;i++) {
        if(smallCategory[i].categoryName.indexOf('产品研发')!=-1) {
            categoryValue=smallCategory[i].categoryId;
            break;
		}
	}
    mini.get("projectCategory").setValue(categoryValue);

    var standardScoreListGrid = mini.get("standardScoreListGrid");
    standardScoreListGrid.setUrl(url+mini.get("projectCategory").getValue());
    standardScoreListGrid.load();

    standardScoreListGrid.on("beforeload", function (e) {
        if (standardScoreListGrid.getChanges().length > 0) {
            if (confirm(configStandardScore_name)) {
                e.cancel = true;
            }
        }
    });
    standardScoreListGrid.on("cellcommitedit", function (e) {
        if (e.field == "score") {
            var r = /^\+?[1-9][0-9]*$/;　　//正整数
            if(!r.test(e.value)){
                mini.alert(configStandardScore_name1);
                e.value='';
            }
        }
    });

    standardScoreListGrid.on("cellbeginedit", function (e) {
        var record = e.record;
        if(e.field == "score") {
            e.editor.setEnabled(false);
			if(isManager) {
                e.editor.setEnabled(true);
			}
        }
    });
</script>

</body>
</html>