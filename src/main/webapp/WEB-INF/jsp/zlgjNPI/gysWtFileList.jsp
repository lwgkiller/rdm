
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>图片</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
			type="text/javascript"></script>
</head>
<body>
	<div class="mini-toolbar" id="fileButtons">
		<a id="uploadFile" class="mini-button"  style="margin-bottom: 5px" onclick="uploadZlgjPic">上传附件</a>
	</div>
	<div class="mini-fit" style="height: 100%;">
		<div id="productPicGrid" class="mini-datagrid" style="height: 99%" allowResize="false"
			 idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
			 multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false" allowCellWrap="true" showVGridLines="true">
			<div property="columns">
				<div field="id"  headerAlign="left" visible="false">id</div>
				<div type="indexcolumn" headerAlign="center">序号</div>
				<div field="fileName" align="center" headerAlign="center" >附件名</div>
				<div field="fileSize" align="center" headerAlign="center" >附件大小</div>
				<div field="creator" align="center"  headerAlign="center" >上传人</div>
				<div field="CREATE_TIME_" align="center" dateFormat="yyyy-MM-dd" headerAlign="center" >上传时间</div>
				<div renderer="fileInfoRenderer" align="center" headerAlign="center" >操作</div>
			</div>
		</div>
	</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var productPicGrid=mini.get("productPicGrid");
    var wtId = "${wtId}";
    var faId="${faId}";
    var coverContent="${coverContent}";
    var canOperateFile="${canOperateFile}";
	var url=jsUseCtxPath+"/zlgjNPI/core/gyswt/files.do?wtId="+wtId +"&faId="+ faId;
	$(function () {
        queryFiles();
        if(canOperateFile=='false'){
        	mini.get('uploadFile').setEnabled(false);
		}
	});

	function queryFiles() {
        productPicGrid.setUrl(url);
        productPicGrid.load();
    }

    function uploadZlgjPic() {
        mini.open({
            title: "附件上传",
            url: jsUseCtxPath + "/zlgjNPI/core/gyswt/openUploadWindow.do",
            width: 650,
            height: 350,
            showModal:false,
            allowResize: true,
            onload: function () {
                var iframe = this.getIFrameEl();
                var projectParams={};
                projectParams.wtId=wtId;
                projectParams.faId=faId;
                var data = { projectParams: projectParams };  //传递上传参数
                iframe.contentWindow.SetData(data);
            },
            ondestroy: function (action) {
                queryFiles();
            }
        });
    }

    function fileInfoRenderer(e) {
            var record = e.record;
        	var s=returnZlgjPreviewSpan(record.fileName,record.id,record.wtId,coverContent);
            s+='&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">下载</span>';
            if(canOperateFile=='false') {
                s+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:silver;">删除</span>';
            } else {
                s+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                    'onclick="deleteFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
            }
            return s;
    }

    function returnZlgjPreviewSpan(fileName,fileId,wtId,coverContent) {
        var fileType=getFileType(fileName);
        var s='';
        if(fileType=='other'){
            s = '<span  title="预览" style="color: silver" >预览</span>';
        }else {
            var url='/zlgjNPI/core/gyswt/preview.do?fileType='+fileType;
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' +fileName+'\',\''+fileId+'\',\''+wtId+'\',\''+coverContent+'\',\''+url+'\',\''+fileType+ '\')">预览</span>';
        }
        return s;
    }

    function previewPdf(fileName,fileId,formId, coverConent, url,fileType) {
        if(!fileName) {
            fileName='';
        }
        if(!fileId) {
            fileId='';
        }
        if(!formId) {
            formId='';
        }
        var previewUrl = jsUseCtxPath + url+"&fileName="+encodeURIComponent(fileName)+"&fileId="+fileId+"&formId="+formId;
        if(fileType!='pic') {
            window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent="+encodeURIComponent(coverConent)+"&file=" + encodeURIComponent(previewUrl));
        } else {
            window.open(previewUrl);
        }
	}

	function deleteFile(record) {
		mini.confirm("确定删除？", "确定？",
				function (action) {
					if (action == "ok") {
						var url = jsUseCtxPath + "/zlgjNPI/core/gyswt/deleteFiles.do";
						var data = {
                            wtId: record.wtId,
							id: record.id,
							fileName: record.fileName
						};
						$.post(
								url,
								data,
								function (json) {
                                    queryFiles();
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
		form.attr("action", jsUseCtxPath + "/zlgjNPI/core/gyswt/fileDownload.do?action=download");
		var inputFileName = $("<input>");
		inputFileName.attr("type", "hidden");
		inputFileName.attr("name", "fileName");
		inputFileName.attr("value", record.fileName);
        var mainId = $("<input>");
        mainId.attr("type", "hidden");
        mainId.attr("name", "mainId");
        mainId.attr("value", record.wtId);
		var fileId = $("<input>");
		fileId.attr("type", "hidden");
		fileId.attr("name", "fileId");
		fileId.attr("value", record.id);
		$("body").append(form);
		form.append(inputFileName);
		form.append(mainId);
		form.append(fileId);
		form.submit();
		form.remove();
	}
</script>
<redxun:gridScript gridId="productPicGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>
