
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
		<div id="productPicGrid" class="mini-datagrid" style="height: 99%" allowResize="false" url="${ctxPath}/zhgl/core/jszb/getJszbFileList.do?jszbId=${jszbId}&ckId=${ckId}"
			 idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false" autoload="true"
			 multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false" allowCellWrap="true" showVGridLines="true">
			<div property="columns">
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
    var jszbId = "${jszbId}";
    var ckId="${ckId}";
    var canOperateFile="${canOperateFile}";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";

	$(function () {
        if(canOperateFile=='false'){
        	mini.get('uploadFile').setEnabled(false);
		}
	});

    function uploadZlgjPic() {
        mini.open({
            title: "附件上传",
            url: jsUseCtxPath + "/zhgl/core/jszb/fileUploadWindow.do?jszbId="+jszbId+"&ckId="+ckId,
            width: 650,
            height: 350,
            showModal:false,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                productPicGrid.reload();
            }
        });
    }

    function fileInfoRenderer(e) {
            var record = e.record;
        	var s=returnPreviewSpan(record.fileName,record.id,record.jszbId,coverContent);
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

    function returnPreviewSpan(fileName,fileId,formId,coverContent) {
        var fileType=getFileType(fileName);
        var s='';
        if(fileType=='other'){
            s = '<span  title="预览" style="color: silver" >预览</span>';
        }else {
            var url='/zhgl/core/jszb/preview.do?fileType='+fileType;
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url+'\',\''+fileType+ '\')">预览</span>';
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
						var url = jsUseCtxPath + "/zhgl/core/jszb/delJszbFile.do";
						var data = {
                            formId: record.jszbId,
							id: record.id,
							fileName: record.fileName
						};
						$.post(
								url,
								data,
								function (json) {
                                    productPicGrid.reload();
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
		form.attr("action", jsUseCtxPath + "/zhgl/core/jszb/fileDownload.do");
		var inputFileName = $("<input>");
		inputFileName.attr("type", "hidden");
		inputFileName.attr("name", "fileName");
		inputFileName.attr("value", record.fileName);
        var mainId = $("<input>");
        mainId.attr("type", "hidden");
        mainId.attr("name", "mainId");
        mainId.attr("value", record.jszbId);
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
