
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>公共文档管理</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/standardManager/standardTemplateList.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/standardManager/standardUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
	<ul class="toolBtnBox">
		<li style="float: left" id="operateTool">
			<a id="uploadBtn" class="mini-button" iconCls="icon-upload" onclick="openUploadWindow()" plain="true"><spring:message code="page.standardConfigTemplateList.name" /></a>
			<span class="separator"></span>
		</li>
		<li >
			<span class="text" style="width:auto"><spring:message code="page.standardConfigTemplateList.name1" />: </span><input class="mini-textbox" id="docName" />
			<a class="mini-button" iconCls="icon-search" onclick="searchDoc()" plain="true"><spring:message code="page.standardConfigTemplateList.name2" /></a>
		</li>
	</ul>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="templateListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
		  idField="id" allowAlternating="true" showPager="false" multiSelect="false">
		<div property="columns">
			<div cellCls="actionIcons" width="60" headerAlign="center" align="center" renderer="actionOperate" cellStyle="padding:0;"><spring:message code="page.standardConfigTemplateList.name3" /></div>
			<div field="templateName" headerAlign='center' align='center' width="100" ><spring:message code="page.standardConfigTemplateList.name4" /></div>
			<div field="size" headerAlign='center' align='center' width="60"><spring:message code="page.standardConfigTemplateList.name5" /></div>
			<div field="description" headerAlign='center' align='center' width="100"><spring:message code="page.standardConfigTemplateList.name6" /></div>
			<div field="downloadNum" headerAlign='center' align='center' width="60"><spring:message code="page.standardConfigTemplateList.name7" /></div>
			<div field="creator" width="60" headerAlign='center' align="center"><spring:message code="page.standardConfigTemplateList.name8" /></div>
			<div field="CREATE_TIME_" width="100" dateFormat="yyyy-MM-dd HH:mm:ss" headerAlign='center' align="center"><spring:message code="page.standardConfigTemplateList.name9" /></div>
			<div field="relativePath" visible="false"></div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var url="${ctxPath}/standardManager/core/standardConfig/templateQuery.do?docName=";
    var isManager=whetherIsStandardManager(${currentUserRoles});
    var currentUserName="${currentUser.fullname}";

    if(!isManager&&currentUserName!='管理员') {
        $("#operateTool").hide();
	}

    var templateListGrid = mini.get("templateListGrid");
    templateListGrid.setUrl(url+mini.get("#docName").getValue());
    templateListGrid.load();
    templateListGrid.on("update",function(e){
        handGridButtons(e.sender.el);
    });

    function actionOperate(e) {
        var record = e.record;
        var templateName=record.templateName;
        if(!templateName) {
            templateName='';
        }
        var relativePath=record.relativePath;
        if(!relativePath) {
            relativePath='';
        }
        var s = '<span  title=' + standardConfigTemplateList_name + ' onclick="seeTemplate(\''+record.id+ '\')">' + standardConfigTemplateList_name + '</span>';
        if(currentUserName=='管理员') {
            s+='<span title=' + standardConfigTemplateList_name1 + ' onclick="deleteDoc(\''+record.id+'\',\''+templateName+'\',\''+relativePath+ '\')">' + standardConfigTemplateList_name1 + '</span>';
        } else if(isManager) {
            if(record.creator!='管理员') {
                s+='<span title=' + standardConfigTemplateList_name1 + ' onclick="deleteDoc(\''+record.id+'\',\''+templateName+'\',\''+relativePath+ '\')">' + standardConfigTemplateList_name1 + '</span>';
            }
        }
        return s;
    }

</script>
</body>
</html>