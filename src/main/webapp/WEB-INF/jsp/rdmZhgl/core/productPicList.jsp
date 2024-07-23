
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>样机照片</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
	<div class="mini-toolbar" id="fileButtons">
		<a id="uploadFile" class="mini-button"  style="margin-bottom: 5px" onclick="uploadProductPic">上传样机照片</a>
	</div>
	<div class="mini-fit" style="height: 100%;">
		<div id="productPicGrid" class="mini-datagrid" style="height: 99%" allowResize="false"
			 idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
			 multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false" allowCellWrap="true" showVGridLines="true">
			<div property="columns">
				<div field="id"  headerAlign="left" visible="false">id</div>
				<div type="indexcolumn" headerAlign="center">序号</div>
				<div field="fileName" align="center" headerAlign="center" >照片名</div>
				<div field="fileType" align="center"  headerAlign="center" renderer="onPicType" >照片类型</div>
				<div field="fileDesc" align="center" headerAlign="center" >描述</div>
				<div field="creator" align="center"  headerAlign="center" >上传人</div>
				<div field="CREATE_TIME_" align="center" dateFormat="yyyy-MM-dd HH:mm:ss" headerAlign="center" >上传时间</div>
				<div renderer="fileInfoRenderer" align="center" headerAlign="center" >操作</div>
			</div>
		</div>
	</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var productPicGrid=mini.get("productPicGrid");
    var id = "${id}";
	var editable = "${editable}";
   	var coverContent="${coverContent}";
	var url=jsUseCtxPath+"/rdmZhgl/core/product/files.do";
	var picTypeList = getDics("XPSZ-ZPFX");
	var delRight = ${delRight};
	$(function () {
        queryProjectFiles();
        if(editable=='false'){
        	mini.get('uploadFile').setEnabled(false);
		}
	});

	function queryProjectFiles() {
        productPicGrid.setUrl(url+"?mainId="+id);
        productPicGrid.load();
    }

    function uploadProductPic() {
        mini.open({
            title: "照片上传",
            url: jsUseCtxPath + "/rdmZhgl/core/product/openUploadWindow.do",
            width: 650,
            height: 350,
            showModal:false,
            allowResize: true,
            onload: function () {
                var iframe = this.getIFrameEl();
                var projectParams={};
                projectParams.mainId=id;
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
            var s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' +record.fileName+'\',\''+record.id+'\',\''+record.mainId+'\',\''+coverContent+ '\')">预览</span>';
            s+='&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">下载</span>';
            if(!delRight) {
                s+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:silver;">删除</span>';
            } else {
                s+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                    'onclick="deleteFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
            }
            return s;
    }
	function previewPic(fileName,fileId,mainId) {
		if(!fileName) {
			fileName='';
		}
		if(!fileId) {
			fileId='';
		}
		if(!mainId) {
			mainId='';
		}
		var previewUrl = jsUseCtxPath + "/rdmZhgl/core/product/imagePreview.do?fileName=" + encodeURIComponent(fileName)+"&fileId="+fileId+"&mainId="+mainId;
		window.open(previewUrl);
	}
	function onPicType(e) {
		var record = e.record;
		var value = record.picType;
		var resultText = '';
		for (var i = 0; i < picTypeList.length; i++) {
			if (picTypeList[i].key_ == value) {
				resultText = picTypeList[i].text;
				break
			}
		}
		return resultText;
	}
	function deleteFile(record) {
		mini.confirm("确定删除？", "确定？",
				function (action) {
					if (action == "ok") {
						var url = jsUseCtxPath + "/rdmZhgl/core/product/deleteFiles.do";
						var data = {
							mainId: record.mainId,
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
		form.attr("action", jsUseCtxPath + "/rdmZhgl/core/product/fileDownload.do?action=download");
		var inputFileName = $("<input>");
		inputFileName.attr("type", "hidden");
		inputFileName.attr("name", "fileName");
		inputFileName.attr("value", record.fileName);
		var mainId = $("<input>");
		mainId.attr("type", "hidden");
		mainId.attr("name", "mainId");
		mainId.attr("value", record.mainId);
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
