<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>列表管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-fit" style="height: 100%;">
    <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 100%"
         allowResize="false"
         idField="id" autoload="true"
         url="${ctxPath}/qualityfxgj/core/Gyfk/getGyfkFileList.do?belongDetailId=${gyxjId}"
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
    var gyxjId = "${gyxjId}";
    var currentTime="${currentTime}";
    var currentUserName="${currentUserName}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";


    function returnRapPreviewSpan(fileName,fileId,formId,coverContent) {
        var fileType=getFileType(fileName);
        var s='';
        if(fileType=='other'){
            s = '<span  title="预览" style="color: silver" >预览</span>';
        }else if(fileType=='pdf'){
            var url='/qualityfxgj/core/Gyfk/gyfkPdfPreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url+ '\')">预览</span>';
        }else if(fileType=='office'){
            var url='/qualityfxgj/core/Gyfk/gyfkOfficePreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url +'\')">预览</span>';
        }else if(fileType=='pic'){
            var url='/qualityfxgj/core/Gyfk/gyfkImagePreview';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url + '\')">预览</span>';
        }
        return s;
    }
    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml=returnRapPreviewSpan(record.fileName,record.fileId,record.belongDetailId,coverContent);
        cellHtml+='&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadgyfkFile(\''+record.fileName+'\',\''+record.fileId+'\',\''+record.belongDetailId+'\')">下载</span>';
        return cellHtml;
    }
    function downLoadgyfkFile(fileName,fileId,formId) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + '/qualityfxgj/core/Gyfk/gyfkPdfPreview.do?action=download');
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
</script>
</body>
</html>
