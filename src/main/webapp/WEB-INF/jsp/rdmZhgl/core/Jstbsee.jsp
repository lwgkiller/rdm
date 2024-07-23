<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>技术管理通报查看</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>

    <style>
/*        body,div,h2,span,p {
            padding:0;
            margin:0;
            color:#000;
            font-family:"宋体";
            background-color:#ecf5ff;
        }*/
        .new_body {
            margin:0 80px 0 80px;
            min-height:500px;height:99%;padding-bottom:5px;border:1px #ccc solid;
            background-color:#ecf5ff;
        }
        .new_body h2 {
            margin:20px 0 10px;height:30px;position:relative;line-height:30px;font-size:25px;
            font-family:"宋体";font-weight:bold;text-align:center;overflow:hidden;
        }
        .new_body .contentAuthor {
            margin:0 25px;border-top:2px #ccc ;
        }
        .new_body .author{
            display:block;height:15px;line-height:20px;font-size:15px;text-align:center;
            padding-top:5px;
            border-top:1px red solid;
        }
        .new_body p {
            line-height:25px;margin-top:10px;text-indent:2em;font-size:20px;
        }
    </style>
</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit"  style="width: 100%; height: 100%" showfooter="false" bodystyle="padding:0"
     showheader="false">
    <div class="new_body">
        <h2 id="title">
        </h2>
        <div class="contentAuthor">
            <span class="author" id="author"></span>
            <p id="content" style="height: 400px"></p>
        </div>
        <span style="font-weight: bold;font-size: 15px;display:block;border-bottom:1px rgba(156,156,156,0.38) solid;">附件列表：</span>
        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; margin-top: 5px;"
             allowResize="false" showHGridLines="false" showVGridLines="false"
             idField="id"  autoload="true" showColumns="false"
             url="${ctxPath}/rdmZhgl/Jstb/getJstbFileList.do?jstbId=${jstbId}"
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

</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var messageObj=${messageObj};
    var currentUserName="${currentUserName}";
    var currentTime="${currentTime}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";

    setData();

    function setData() {
        if(messageObj) {
            if(messageObj.jstbTitle) {
                $("#title").html(messageObj.jstbTitle);
            }
            var author="发布时间："+messageObj.CREATE_TIME_+" 编辑："+messageObj.creatorDepFullName+" "+messageObj.creator;
            $("#author").html(author);
            var finalContent='';
            if(messageObj.jstbContent) {
                finalContent+="&nbsp;&nbsp;&nbsp;&nbsp;"+messageObj.jstbContent
            }
            $("#content").html(finalContent);
        }
    }
    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml=returnJstbPreviewSpan(record.fileName,record.fileId,record.jstbId,coverContent);
        cellHtml+='&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadJsjlFile(\''+record.fileName+'\',\''+record.fileId+'\',\''+record.jstbId+'\')">下载</span>';
        return cellHtml;
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
    function downLoadJsjlFile(fileName,fileId,formId) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + '/rdmZhgl/Jstb/jstbPdfPreview.do?action=download');
        var inputFileName = $("<input>");
        inputFileName.attr("type", "hidden");
        inputFileName.attr("name", "fileName");
        inputFileName.attr("value", fileName);
        var inputjstbId = $("<input>");
        inputjstbId.attr("type", "hidden");
        inputjstbId.attr("name", "formId");
        inputjstbId.attr("value", formId);
        var inputFileId = $("<input>");
        inputFileId.attr("type", "hidden");
        inputFileId.attr("name", "fileId");
        inputFileId.attr("value", fileId);
        $("body").append(form);
        form.append(inputFileName);
        form.append(inputjstbId);
        form.append(inputFileId);
        form.submit();
        form.remove();
    }
    $(function () {
        $("#detailToolBar").show();
        //非草稿放开流程信息查看按钮
        if(messageObj.status!='草稿') {
            $("#processInfo").show();
        }
    });
    function processInfo() {
        var instId = messageObj.instId;
        if(!instId) {
            return;
        }
        var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
        _OpenWindow({
            url: url,
            max: true,
            title: "流程图实例",
            width: 800,
            height: 600
        });
    }

</script>
</body>
</html>
