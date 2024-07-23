
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>先锋党员文档管理</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
	<ul class="toolBtnBox">
		<li style="float: left">
			<a id="uploadBtn" class="mini-button"  style="display: none" iconCls="icon-upload" onclick="openWdmbUploadWindow()" plain="true">上传文件</a>
		</li>
		<span class="separator"></span>
		<li >
			<span class="text" style="width:auto">文档名称: </span><input class="mini-textbox" id="docName" />
			<a class="mini-button" iconCls="icon-search" onclick="searchDoc()" plain="true">查询</a>
            <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="myClearForm()">清空查询</a>
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
            <div field="fileDesc" width="100" headerAlign="center">文件说明</div>
			<div field="creator" width="80" headerAlign='center' align="center">创建人</div>
			<div field="CREATE_TIME_" width="100" dateFormat="yyyy-MM-dd" headerAlign='center' align="center">创建时间</div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var url="${ctxPath}/xfdy/core/file/xfdyFileList.do?docName=";
    var currentUserName="${currentUser.fullname}";
    var currentUserId="${currentUserId}";
    var currentTime="${currentTime}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";
    var isXfdy =${isXfdy};
    if (isXfdy) {
        mini.get("uploadBtn").show();
    }

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
        var fileId=record.id;
        var s = '';
		s+=returnPreviewQrsWdmb(fileName,fileId,'common',coverContent);
        s+='<span  title="下载" onclick="downLoadDocQrs(\'' +fileName+'\',\''+fileId+ '\')">下载</span>';
        if(currentUserId==record.CREATE_BY_) {
            s+='<span title="删除" onclick="deleteDocQrs(\''+record.id+'\',\''+fileName+'\')">删除</span>';
        }
        return s;
    }


    //打开文件上传窗口
    function openWdmbUploadWindow() {
        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/xfdy/core/file/xfdyUploadWindow.do",
            width: 750,
            height: 450,
            showModal:false,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                searchDoc();
            }
        });
    }

    function searchDoc() {
        commonDocListGrid.setUrl(url+mini.get("#docName").getValue());
        commonDocListGrid.load();
    }

    function myClearForm() {
        mini.get("#docName").setValue('');
        commonDocListGrid.setUrl(url);
        commonDocListGrid.load();
    }

    //下载文档
    function downLoadDocQrs(fileName,fileId) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/xfdy/core/file/fileDownload.do?action=download");
        var inputFileName = $("<input>");
        inputFileName.attr("type", "hidden");
        inputFileName.attr("name", "fileName");
        inputFileName.attr("value", fileName);
        var inputFileId = $("<input>");
        inputFileId.attr("type", "hidden");
        inputFileId.attr("name", "fileId");
        inputFileId.attr("value", fileId);
        $("body").append(form);
        form.append(inputFileName);
        form.append(inputFileId);
        form.submit();
        form.remove();
    }

    //删除文档
    function deleteDocQrs(id,fileName) {
        mini.confirm("确定删除？", "确定？",
            function (action) {
                if (action == "ok") {
                    $.ajaxSettings.async = false;
                    var url = jsUseCtxPath + "/xfdy/core/file/fileDelete.do";
                    var data = {
                        id: id,
                        fileName: fileName
                    };
                    $.post(
                        url,
                        data,
                        function (json) {
                            searchDoc();
                        });
                    $.ajaxSettings.async = true;
                }
            }
        );
    }
    function returnPreviewQrsWdmb(fileName,fileId,actionType,coverContent) {
        var fileType=getFileType(fileName);
        var s='';
        if(fileType=='other'){
            s = '<span  title="预览" style="color: silver" >预览</span>';
        }else if(fileType=='pdf'){
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdfQrs(\'' +fileName+'\',\''+fileId+'\',\''+actionType+'\',\''+coverContent+ '\')">预览</span>';
        }else if(fileType=='office'){
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDocQrs(\'' +fileName+'\',\''+fileId+'\',\''+actionType+'\',\''+coverContent +'\')">预览</span>';
        }else if(fileType=='pic'){
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPicQrs(\'' +fileName+'\',\''+fileId+'\',\''+actionType+'\',\''+coverContent+ '\')">预览</span>';
        }
        return s;
    }
    function previewPdfQrs(fileName,fileId,actionType, coverConent) {
        if(!fileName) {
            fileName='';
        }
        if(!fileId) {
            fileId='';
        }
        if(!actionType) {
            actionType='';
        }
        var previewUrl = jsUseCtxPath + "/xfdy/core/file/fileDownload.do?action=preview&fileName="+encodeURIComponent(fileName)+"&actionType="+actionType+"&fileId="+fileId;
        window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent="+encodeURIComponent(coverConent)+"&file=" + encodeURIComponent(previewUrl));
    }
    function previewPicQrs(fileName,fileId,actionType) {
        if(!fileName) {
            fileName='';
        }
        if(!fileId) {
            fileId='';
        }
        if(!actionType) {
            actionType='';
        }
        var previewUrl = jsUseCtxPath + "/xfdy/core/file/imagePreview.do?fileName=" + encodeURIComponent(fileName)+"&actionType="+actionType+"&fileId="+fileId;
        window.open(previewUrl);
    }

    function previewDocQrs(fileName,fileId,actionType,coverContent) {
        if(!fileName) {
            fileName='';
        }
        if(!fileId) {
            fileId='';
        }
        if(!actionType) {
            actionType='';
        }
        var previewUrl = jsUseCtxPath + "/xfdy/core/file/officePreview.do?fileName=" + encodeURIComponent(fileName)+"&actionType="+actionType+"&fileId="+fileId;
        window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent="+encodeURIComponent(coverContent)+"&file=" + encodeURIComponent(previewUrl));
    }
</script>

</body>
</html>