
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>改进计划完成证明材料</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
	<div class="mini-toolbar" id="fileButtons">
		<a id="uploadFile" class="mini-button"  style="margin-bottom: 5px" onclick="uploadCompleteFile">上传文件</a>
	</div>
	<div class="mini-fit" style="height: 100%;">
		<div id="fileInfoGrid" class="mini-datagrid" style="height: 100%;" allowResize="false"
			 idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
			 multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false" allowCellWrap="false" showVGridLines="true">
			<div property="columns">
				<div field="id"  headerAlign="left" visible="false">id</div>
				<div type="indexcolumn" headerAlign="center">序号</div>
				<div field="fileName" align="center" headerAlign="center" width="150">文件名</div>
				<div field="fileSize" align="center"  headerAlign="center"  width="60" >文件大小</div>
				<div field="creator" align="center"  headerAlign="center" width="80" >创建人</div>
				<div field="CREATE_TIME_" align="center" dateFormat="yyyy-MM-dd" headerAlign="center"  width="120">创建时间</div>
				<div renderer="fileInfoRenderer" align="center" headerAlign="center" width="100" >操作</div>
			</div>
		</div>
	</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var fileListGrid=mini.get("fileInfoGrid");
    var detailId = "${detailId}";
    var formId = "${formId}";
    var canEditFile="${canEditFile}";
    //文件预览的水印
    var currentUserName="${currentUserName}";
    var currentTime="${currentTime}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";
	var url=jsUseCtxPath+"/zhgl/core/zlgj/getGjjhFileList.do?belongDetailId="+detailId;
    var currentUserId="${currentUserId}";

	$(function () {
		if(canEditFile=='false') {
		    mini.get("uploadFile").setEnabled(false);
		}
        fileListGrid.setUrl(url);
        fileListGrid.load();
    });

    function fileInfoRenderer(e) {
        var downloadUrl='/zhgl/core/zlgj/gjjhDownload.do';
        var deleteUrl='/zhgl/core/zlgj/delGjjhFile.do';
            var record = e.record;
            var s = returnGjjhPreviewSpan(record.fileName,record.id,formId,coverContent);
        	s+='&nbsp;&nbsp;&nbsp;<span  title="下载"  style="color:#409EFF;cursor: pointer;" ' +
			'onclick="downLoadFile(\'' +record.fileName+'\',\''+record.id+'\',formId,\''+downloadUrl+ '\')">下载</span>';
            if(canEditFile!='false' && record.CREATE_BY_==currentUserId) {
                s+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;"' +
					'onclick="deleteFile(\''+record.fileName+'\',\''+record.id+'\',formId,\''+deleteUrl+'\')">删除</span>';
			}
            return s;
    }

    function returnGjjhPreviewSpan(fileName,fileId,formId,coverContent) {
        var fileType=getFileType(fileName);
        var s='';
        if(fileType=='other'){
            s = '<span  title="预览" style="color: silver" >预览</span>';
        }else if(fileType=='pdf'){
            var url='/zhgl/core/zlgj/gjjhDownload.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url+ '\')">预览</span>';
        }else if(fileType=='office'){
            var url='/zhgl/core/zlgj/gjjhOfficePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url +'\')">预览</span>';
        }else if(fileType=='pic'){
            var url='/zhgl/core/zlgj/gjjhImagePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url + '\')">预览</span>';
        }
        return s;
    }

    function uploadCompleteFile() {
        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/zhgl/core/zlgj/gjjhFileUploadWindow.do?detailId="+detailId+"&formId="+formId,
            width: 650,
            height: 350,
            showModal:false,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                fileListGrid.setUrl(url);
                fileListGrid.load();
            }
        });
    }

</script>
<redxun:gridScript gridId="fileInfoGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>
