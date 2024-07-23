<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>列表管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div>
    <a id="addFile" class="mini-button" onclick="fileupload()">添加附件</a>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 100%"
         allowResize="false"
         idField="id" autoload="true"
         url="${ctxPath}/rdmZhgl/Jstb/getJstbFileList.do?jstbId=1"
         multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
        <div property="columns">
            <div type="indexcolumn" align="center" width="20">序号</div>
            <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
            <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
            <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer">操作
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var fileListGrid = mini.get("#fileListGrid");
    var isJSGLBUser =${isJSGLBUser};
    var jstbId = "${jstbId}";
    var currentTime="${currentTime}";
    var currentUserName="${currentUserName}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";

    if (!isJSGLBUser) {
        mini.get("addFile").setEnabled(false);
    }
    function returnJstbPreviewSpan(fileName,fileId,formId,coverContent) {
        var fileType=getFileType(fileName);
        var s='';
        if(fileType=='other'){
            s = '<span  title="预览" style="color: silver" >预览</span>';
        }else if(fileType=='pdf'){
            var url='/rdmZhgl/Jstb/jstbPdfPreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url+ '\')">预览</span>';
        }else if(fileType=='office'){
            var url='/rdmZhgl/Jstb/jstbOfficePreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url +'\')">预览</span>';
        }else if(fileType=='pic'){
            var url='/rdmZhgl/Jstb/jstbImagePreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url + '\')">预览</span>';
        }
        return s;
    }
    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml=returnJstbPreviewSpan(record.fileName,record.fileId,record.jstbId,coverContent);
        cellHtml+='&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadJstbFile(\''+record.fileName+'\',\''+record.fileId+'\',\''+record.jstbId+'\')">下载</span>';
        var deleteUrl="/rdmZhgl/Jstb/delJstbFile.do"
        if(isJSGLBUser){
            cellHtml+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\''+record.fileName+'\',\''+record.fileId+'\',\''+record.jstbId+'\',\''+deleteUrl+'\')">删除</span>';
        }

        return cellHtml;
    }
    function downLoadJstbFile(fileName,fileId,formId) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + '/rdmZhgl/Jstb/jstbPdfPreview.do?action=download');
        var inputFileName = $("<input>");
        inputFileName.attr("type", "hidden");
        inputFileName.attr("name", "fileName");
        inputFileName.attr("value", fileName);
        var inputstandardId = $("<input>");
        inputstandardId.attr("type", "hidden");
        inputstandardId.attr("name", "formId");
        inputstandardId.attr("value", formId);
        var inputFileId = $("<input>");
        inputFileId.attr("type", "hidden");
        inputFileId.attr("name", "fileId");
        inputFileId.attr("value", fileId);
        $("body").append(form);
        form.append(inputFileName);
        form.append(inputstandardId);
        form.append(inputFileId);
        form.submit();
        form.remove();
    }
    function fileupload() {
        mini.open({
            title: "添加附件",
            url: jsUseCtxPath + "/rdmZhgl/Jstb/openUploadWindow.do?jstbId=1",
            width: 850,
            height: 550,
            showModal:true,
            allowResize: true,
            ondestroy: function () {
                if(fileListGrid) {
                    fileListGrid.load();
                }
            }
        });
    }
</script>
</body>
</html>
