
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>公共文档管理</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/xcmgProjectManager/xcmgDocMgrList.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
	<ul class="toolBtnBox">
		<li style="float: left">
            <a id="uploadBtn" class="mini-button" iconCls="icon-upload" onclick="openDocMgrUploadWindow()" plain="true"><spring:message code="page.xcmgDocMgrList.name" /></a>
            <p style="display: inline-block;color: #888;font-size:15px;vertical-align: middle">（<image src="${ctxPath}/styles/images/warn.png" style="margin-right:5px;vertical-align: middle;height: 15px"/><spring:message code="page.xcmgDocMgrList.name1" />）</p>
		</li>
		<span class="separator"></span>
		<li >
			<span class="text" style="width:auto"><spring:message code="page.xcmgDocMgrList.name2" />: </span><input class="mini-textbox" id="docName" />
			<a class="mini-button" iconCls="icon-search" onclick="searchDoc()" plain="true"><spring:message code="page.xcmgDocMgrList.name3" /></a>
		</li>
	</ul>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="commonDocListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
		  idField="id" allowAlternating="true" showPager="false" multiSelect="false">
		<div property="columns">
			<div cellCls="actionIcons" width="60" headerAlign="center" align="center" renderer="actionOperate" cellStyle="padding:0;"><spring:message code="page.xcmgDocMgrList.name4" /></div>
			<div field="fileName" headerAlign='center' align='center' width="160" ><spring:message code="page.xcmgDocMgrList.name5" /></div>
			<div field="fileSize" headerAlign='center' align='center' width="60"><spring:message code="page.xcmgDocMgrList.name6" /></div>
			<div field="creator" width="80" headerAlign='center' align="center"><spring:message code="page.xcmgDocMgrList.name7" /></div>
			<div field="CREATE_TIME_" width="100" dateFormat="yyyy-MM-dd HH:mm:ss" headerAlign='center' align="center"><spring:message code="page.xcmgDocMgrList.name8" /></div>
			<div field="relativeFilePath" visible="false"></div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var url="${ctxPath}/xcmgProjectManager/core/fileUpload/xcmgDocMgrList.do?docName=";
    var isManager=whetherIsProjectManager(${currentUserRoles});
    var currentUserName="${currentUser.fullname}";
    var currentTime="${currentTime}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>" + xzxgwjjxyxgs;

    var commonDocListGrid = mini.get("commonDocListGrid");
    commonDocListGrid.setUrl(url+mini.get("#docName").getValue());
    commonDocListGrid.load();
    //操作栏
    commonDocListGrid.on("update",function(e){
        handGridButtons(e.sender.el);
    });
    function actionOperate(e) {
        var record = e.record;
        var fileName=record.fileName;
        if(!fileName) {
            fileName='';
        }
        var relativeFilePath=record.relativeFilePath;
        if(!relativeFilePath) {
            relativeFilePath='';
        }
        var s = '';
		s+=returnPreviewSpan(fileName,relativeFilePath,'common',coverContent,record.fileSize);
        s+='<span  title=' + xcmgDocMgrList_name + ' onclick="downLoadDoc(\'' +fileName+'\',\''+relativeFilePath+ '\')">' + xcmgDocMgrList_name + '</span>';
        if(currentUserName=='管理员') {
            s+='<span title=' + xcmgDocMgrList_name1 + ' onclick="deleteDoc(\''+record.id+'\',\''+fileName+'\',\''+relativeFilePath+ '\')">' + xcmgDocMgrList_name1 + '</span>';
        } else if(isManager) {
            if(record.creator!='管理员') {
                s+='<span title=' + xcmgDocMgrList_name1 + ' onclick="deleteDoc(\''+record.id+'\',\''+fileName+'\',\''+relativeFilePath+ '\')">' + xcmgDocMgrList_name1 + '</span>';
            }
        }
        return s;
    }


</script>

</body>
</html>