
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>交付物文件</title>
	<%@include file="/commons/list.jsp"%>
</head>
<body>
	<div class="mini-toolbar" id="fileButtons">
		<a id="uploadFile" class="mini-button"  style="margin-bottom: 5px" onclick="uploadFile">上传附件</a>
	</div>
	<div class="mini-fit" style="height: 100%;">
		<div id="fileGrid" class="mini-datagrid" style="height: 99%" allowResize="false"
			 idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
			 multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false" allowCellWrap="true" showVGridLines="true">
			<div property="columns">
				<div field="id"  headerAlign="left" visible="false">id</div>
				<div type="indexcolumn" headerAlign="center">序号</div>
				<div field="fileName" align="center" headerAlign="center" >文件名称</div>
				<div field="fileDesc" align="center" headerAlign="center" >文件说明</div>
				<div field="creator" align="center"  headerAlign="center" >上传人</div>
				<div field="CREATE_TIME_" align="center" dateFormat="yyyy-MM-dd HH:mm:ss" headerAlign="center" >上传时间</div>
				<div renderer="fileInfoRenderer" align="center" headerAlign="center" >操作</div>
			</div>
		</div>
	</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var fileGrid=mini.get("fileGrid");
	var currentUserId = "${currentUser.userId}";
    var taskId = "${taskId}";
	var editable = "${editable}";
	var gzxmAdmin = ${gzxmAdmin};
	var isReporter = ${isReporter};
	var fileType = "${fileType}";
	var currentUserName="${currentUser.fullname}";
	var currentTime="${currentTime}";
	var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
	var url=jsUseCtxPath+"/rdmZhgl/core/gzxm/project/files.do";
	$(function () {
        queryProjectFiles();
        debugger;
        if(editable=='false'&&!gzxmAdmin&&!isReporter){
        	mini.get('uploadFile').setEnabled(false);
		}
	});

	function queryProjectFiles() {
        fileGrid.setUrl(url+"?taskId="+taskId+"&fileType="+fileType);
        fileGrid.load();
    }

    function uploadFile() {
        mini.open({
            title: "上传附件",
            url: jsUseCtxPath + "/rdmZhgl/core/gzxm/project/openUploadWindow.do",
            width: 650,
            height: 350,
            showModal:false,
            allowResize: true,
            onload: function () {
                var iframe = this.getIFrameEl();
                var projectParams={};
                projectParams.taskId=taskId;
				projectParams.fileType=fileType;
                var data = { projectParams: projectParams };  //传递上传参数
                iframe.contentWindow.SetData(data);
            },
            ondestroy: function (action) {
                queryProjectFiles();
            }
        });
    }

    function fileInfoRenderer(e) {
            var record = e.record;
            var s= "";
		    s += returnPreviewSpan(record.fileName, record.id, record.taskId, coverContent,"gzxmFileUrl");
            s+='&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">下载</span>';
            if(record.CREATE_BY_ != currentUserId) {
                s+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:silver;">删除</span>';
            } else {
                s+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                    'onclick="deleteFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
            }
            return s;
    }
	function deleteFile(record) {
		mini.confirm("确定删除？", "确定？",
				function (action) {
					if (action == "ok") {
						var url = jsUseCtxPath + "/rdmZhgl/core/gzxm/project/deleteFiles.do";
						var data = {
							taskId: record.taskId,
							id: record.id,
							fileName: record.fileName
						};
						$.post(
								url,
								data,
								function (json) {
									queryProjectFiles();
								});
					}
				}
		);
	}
	//下载文档
	function downFile(record) {
		var form = $("<form>");
		form.attr("style", "display:none");
		form.attr("target", "");
		form.attr("method", "post");
		form.attr("action", jsUseCtxPath + "/sys/core/commonInfo/fileDownload.do?action=download");
		var inputFileName = $("<input>");
		inputFileName.attr("type", "hidden");
		inputFileName.attr("name", "fileName");
		inputFileName.attr("value", record.fileName);
		var taskId = $("<input>");
		taskId.attr("type", "hidden");
		taskId.attr("name", "formId");
		taskId.attr("value", record.taskId);
		var fileId = $("<input>");
		fileId.attr("type", "hidden");
		fileId.attr("name", "fileId");
		fileId.attr("value", record.id);
		var fileUrl = $("<input>");
		fileUrl.attr("type", "hidden");
		fileUrl.attr("name", "fileUrl");
		fileUrl.attr("value", "gzxmFileUrl");
		$("body").append(form);
		form.append(inputFileName);
		form.append(taskId);
		form.append(fileId);
		form.append(fileUrl);
		form.submit();
		form.remove();
	}
</script>
<redxun:gridScript gridId="fileGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>
