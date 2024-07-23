
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>附件列表</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
			type="text/javascript"></script>
</head>
<body>
	<div class="mini-toolbar" id="fileButtons">
		<a id="uploadFile" class="mini-button"  style="margin-bottom: 5px" onclick="uploadMatPriceReviewFile">上传附件</a>
	</div>
	<div class="mini-fit" style="height: 100%;">
		<div id="fileListGrid" class="mini-datagrid" style="height: 99%" allowResize="false" url="${ctxPath}/matPriceReview/core/files.do?formId=${formId}"
			 idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false" autoload="true"
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
    var fileListGrid=mini.get("fileListGrid");
    var formId = "${formId}";
    var canOperateFile="${canOperateFile}";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
	$(function () {
        if(canOperateFile=='false'){
        	mini.get('uploadFile').setEnabled(false);
		}
	});


    function uploadMatPriceReviewFile() {
        mini.open({
            title: "附件上传",
            url: jsUseCtxPath + "/matPriceReview/core/openUploadWindow.do",
            width: 650,
            height: 350,
            showModal:false,
            allowResize: true,
            onload: function () {
                var iframe = this.getIFrameEl();
                var projectParams={};
                projectParams.formId=formId;
                var data = { projectParams: projectParams };  //传递上传参数
                iframe.contentWindow.SetData(data);
            },
            ondestroy: function (action) {
                fileListGrid.reload();
            }
        });
    }

    function fileInfoRenderer(e) {
            var record = e.record;
            var downloadUrl = '/matPriceReview/core/fileDownload.do';
        	var s=returnPreviewSpan(record.fileName,record.id,formId,coverContent);
            s+='&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downLoadFile(\''+record.fileName+'\',\''+record.id+'\',\''+formId+'\',\''+downloadUrl+'\')">下载</span>';
            if(canOperateFile=='false') {
                s+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:silver;">删除</span>';
            } else {
                var deleteUrl="/matPriceReview/core/deleteFiles.do";
                s+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                    'onclick="deleteFile(\''+record.fileName+'\',\''+record.id+'\',\''+formId+'\',\''+deleteUrl+'\')">删除</span>';
            }
            return s;
    }

    function returnPreviewSpan(fileName,fileId,formId,coverContent) {
        var fileType=getFileType(fileName);
        var s='';
        if(fileType=='other'){
            s = '<span  title="预览" style="color: silver" >预览</span>';
        }else {
            var url='/matPriceReview/core/preview.do?fileType='+fileType;
            debugger
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf6(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url+'\',\''+fileType+ '\')">预览</span>';
        }
        return s;
    }


</script>
<redxun:gridScript gridId="fileListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>
