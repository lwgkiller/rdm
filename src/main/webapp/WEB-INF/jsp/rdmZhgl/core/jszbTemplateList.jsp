
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>公共文档管理</title>
	<%@include file="/commons/list.jsp"%>
    <script src="${ctxPath}/scripts/rdmZhgl/jszbTemplateList.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
	<ul class="toolBtnBox">
		<li style="float: left">
			<a id="uploadBtn" class="mini-button" iconCls="icon-upload" onclick="uploadJszbTemplate()" plain="true">上传文件</a>
		</li>
		<span class="separator"></span>
		<li >
			<span class="text" style="width:auto">文档名称: </span><input class="mini-textbox" id="docName" />
			<a class="mini-button" iconCls="icon-search" onclick="searchDoc()" plain="true">查询</a>
		</li>
	</ul>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="commonDocListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
		  idField="id" allowAlternating="true" showPager="false" multiSelect="false">
		<div property="columns">
			<div cellCls="actionIcons" width="60" headerAlign="center" align="center" renderer="actionOperate" cellStyle="padding:0;">操作</div>
			<div field="fileName" headerAlign='center' align='center' width="160" >文件名称</div>
			<div field="fileSize" headerAlign='center' align='center' width="60">文件大小</div>
			<div field="creator" width="80" headerAlign='center' align="center">创建人</div>
			<div field="CREATE_TIME_" width="100" dateFormat="yyyy-MM-dd" headerAlign='center' align="center">创建时间</div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var url="${ctxPath}/zhgl/core/jszbTemplate/fileList.do?fileName=";
    var currentUserName="${currentUser.fullname}";
    var currentUserId="${currentUserId}";
    var currentTime="${currentTime}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";

    var commonDocListGrid = mini.get("commonDocListGrid");
    commonDocListGrid.setUrl(url+mini.get("#docName").getValue());
    commonDocListGrid.load();
    //操作栏
    commonDocListGrid.on("update",function(e){
        handGridButtons(e.sender.el);
    });
    function actionOperate(e) {
        var record = e.record;
        var s=returnJszbTemplatePreview(record.fileName,record.id,record.jszbId,coverContent);
        s+='&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downJszbFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">下载</span>';
        if(currentUserId==record.CREATE_BY_) {
            s+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteJszbFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
        }
        return s;
    }


</script>

</body>
</html>