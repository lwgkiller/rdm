
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<%--交付物审批表单中的文件列表--%>
	<title>交付物列表</title>
	<%@include file="/commons/list.jsp"%>
</head>
<body>
<div class="mini-toolbar">
	<ul class="toolBtnBox">
		<li>
			<a class="mini-button"  plain="true" onclick="addRow()">添加到审批单</a>
		</li>
		<p style="display: inline-block;color: #888;font-size:15px;vertical-align: middle">
			（<image src="${ctxPath}/styles/images/warn.png" style="margin-right:5px;vertical-align: middle;height: 15px"/>
			下表列出的是审批单关联的项目在该阶段需要审批但尚未提交的文件，如果需要新增、删除或替换文件，请在项目详情页面中‘文件归档’处操作）</p>
	</ul>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
		 url="${ctxPath}/xcmgProjectManager/core/xcmgProjectFile/getStageFileList.do" idField="id" showPager="false"
		 multiSelect="true" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
		<div property="columns">
			<div type="checkcolumn" width="20"></div>
			<div type="indexcolumn" align="center"  width="20">序号</div>
			<div field="fileName"  width="140" headerAlign="center" align="center" allowSort="false" >文件名称</div>
			<div field="deliveryName"  sortField="categoryName"  width="80" headerAlign="center" align="center" allowSort="true">文件类别</div>
			<div field="fileSize"  sortField="levelName"  width="80" headerAlign="center" align="center" allowSort="true">文件大小</div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var fileListGrid=mini.get("fileListGrid");
    var projectId = "${projectId}";
	var stageId = "${stageId}";
	var recordId = "${recordId}";
	$(function () {
		reload();
	})
	function reload() {
		let data = {};
		data.projectId = projectId;
		data.stageId = stageId;
		data.recordId = recordId;
		//查询
		fileListGrid.load(data);
	}
	function addRow() {
		var rows =[];
		rows = fileListGrid.getSelecteds();
		var fileIds = ""
		if (rows.length <= 0) {
			mini.alert("请至少选中一条记录");
			return;
		}else{
			for(var i=0;i<rows.length;i++){
				fileIds += rows[i].id+",";
			}
            fileIds=fileIds.substring(0,fileIds.length-1);
		}
		$.ajaxSettings.async = false;
		var url = jsUseCtxPath + "/xcmgProjectManager/core/xcmgProjectFile/addFile.do";
		var data = {
			fileIds: fileIds,
			id: recordId,
		};
		$.post(
				url,
				data,
				function (json) {
					if(json.success){
						mini.alert(json.message);
						reload();
					}else{
						mini.alert(json.message);
					}
				});
		$.ajaxSettings.async = true;
	}
</script>
<redxun:gridScript gridId="fileListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>