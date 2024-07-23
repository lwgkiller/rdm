
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>委外认证-公共文档管理</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/wwrz/wwrzCommonFileList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
	<ul class="toolBtnBox">
		<li style="float: left">
			<a id="uploadBtn" class="mini-button" iconCls="icon-upload" onclick="openDocMgrUploadWindow()" plain="true">上传文件</a>
		</li>
		<span class="separator"></span>
		<li >
			<span class="text" style="width:auto">文件名称: </span><input class="mini-textbox" id="fileName" />
			<a class="mini-button" iconCls="icon-search" onclick="searchDoc()" plain="true">查询</a>
		</li>
	</ul>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="commonDocListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"  sortField="indexSort" sortOrder="asc"
		  idField="id" allowAlternating="true" showPager="false" multiSelect="false"  allowCellSelect="true"
		 allowCellEdit="true"  editNextOnEnterKey="true" editNextRowCell="true" oncellbeginedit="cellbeginedit" oncellendedit="cellendedit"
	>
		<div property="columns">
			<div renderer="fileInfoRenderer" align="center" width="80" headerAlign="center">操作</div>
			<div field="fileName" headerAlign='center' sortField="fileName"   allowSort="true" align='left' width="160" >文件名称</div>
			<div field="fileSize" headerAlign='center' align='center' width="60">文件大小</div>
			<div field="indexSort"  sortField="indexSort" allowSort="true"   headerAlign='center' align='center' width="60">排序号
				<input property="editor" class="mini-spinner" allowLimitValue="false"  allowNull="true"/>
			</div>
			<div field="creator" width="80" headerAlign='center' align="center">创建人</div>
			<div field="CREATE_TIME_" width="100" dateFormat="yyyy-MM-dd HH:mm:ss" headerAlign='center' align="center">创建时间</div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var url="${ctxPath}/wwrz/core/file/commonFiles.do?fileType=common&fileName=";
    var currentUserName="${currentUser.fullname}";
    var currentTime="${currentTime}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";
	var permission = ${permission};
    var commonDocListGrid = mini.get("commonDocListGrid");
    commonDocListGrid.setUrl(url+mini.get("#fileName").getValue());
    commonDocListGrid.load();
    if(!permission){
    	mini.get('uploadBtn').setEnabled(false);
	}

	function cellbeginedit(e) {
		var field = e.field;
		if(field!='indexSort'){
			return
		}
		if(!permission){
			return
		}
	}
	function cellendedit(e) {
		var field = e.field;
		var id = e.record.id;
		var dataValue = e.value;
		postData = {"id": id, "dataValue": dataValue};
		var url = jsUseCtxPath + '/wwrz/core/file/updateIndexSort.do';
		var resultData = ajaxRequest(url, 'POST', false, postData);
		if (resultData.success) {
			searchDoc()
		}else{
			mini.alert(resultData.message);
		}
	}
</script>
</body>
</html>
