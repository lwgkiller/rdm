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
    <a id="addFile" class="mini-button" onclick="fileupload()">添加模板</a>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 100%"
         allowResize="false"
         idField="id" autoload="true"
         url="${ctxPath}/world/core/research/getListData.do?fileType=0&menuType=${menuType}"
         multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
        <div property="columns">
            <div type="indexcolumn" align="center" width="20">序号</div>
            <div field="fileName" width="140" headerAlign="center" align="center">模板名称</div>
            <div field="fileSize" width="80" headerAlign="center" align="center">模板大小</div>
            <div field="fileDesc" width="80" headerAlign="center" align="center">模板描述</div>
            <div field="action" width="100" headerAlign='center' align="center" renderer="fileRenderer">操作
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var fileListGrid = mini.get("#fileListGrid");
    var currentUserId = "${currentUser.userId}";
    var currentTime="${currentTime}";
    var currentUserName="${currentUserName}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";
    var menuType = "${menuType}";
    var menuFlag = "${menuFlag}";
    var permission = ${permission};
    if (!permission) {
        mini.get('addFile').setEnabled(false);
    }
    function fileRenderer(e) {
        var record = e.record;
        var fileId = record.id;
        var fileName = record.fileName;
        var cellHtml = '';
        cellHtml = returnPreviewSpan(fileId,fileName,menuType,coverContent);
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downFile(\''+fileName+'\',\''+fileId+'\',\''+menuType+'\')">下载</span>';
        if (currentUserId == record.CREATE_BY_) {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                ' onclick="deleteFile(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return cellHtml;
    }
    /**
     * 获取附件类型
     * */
    function getFileType(fileName) {
        var suffix = "";
        var suffixIndex = fileName.lastIndexOf('.');
        if (suffixIndex != -1) {
            suffix = fileName.substring(suffixIndex + 1).toLowerCase();
        }
        var pdfArray = ['pdf'];
        if (pdfArray.indexOf(suffix) != -1) {
            return 'pdf';
        }
        var officeArray = ['doc', 'docx', 'ppt', 'txt', 'xlsx', 'xls', 'pptx'];
        if (officeArray.indexOf(suffix) != -1) {
            return 'office';
        }
        var picArray = ['jpg', 'jpeg', 'jif', 'bmp', 'png', 'tif', 'gif'];
        if (picArray.indexOf(suffix) != -1) {
            return 'pic';
        }
        return 'other';
    }

    function returnPreviewSpan(fileId, fileName, relativeFilePath, coverContent) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileId + '\',\'' + fileName + '\',\'' + relativeFilePath + '\',\'' + coverContent + '\')">预览</span>';
        } else if (fileType == 'office') {
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileId + '\',\'' + fileName + '\',\'' + relativeFilePath + '\',\'' + coverContent + '\')">预览</span>';
        } else if (fileType == 'pic') {
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileId + '\',\'' + fileName + '\',\'' + relativeFilePath + '\',\'' + coverContent + '\')">预览</span>';
        }
        return s;
    }

    function previewPdf(field, fileName, relativeFilePath, coverConent) {
        if (!fileName) {
            fileName = '';
        }
        if (!relativeFilePath) {
            relativeFilePath = '';
        }
        var previewUrl = jsUseCtxPath + "/world/core/research/downOrPdfPreview.do?action=preview&fileName=" + encodeURIComponent(fileName) + "&relativeFilePath=" + relativeFilePath + "&fileId=" + field;
        window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverConent) + "&file=" + encodeURIComponent(previewUrl));
    }

    function previewPic(field, fileName, relativeFilePath) {
        if (!fileName) {
            fileName = '';
        }
        if (!relativeFilePath) {
            relativeFilePath = '';
        }
        var previewUrl = jsUseCtxPath + "/world/core/research/imagePreview.do?fileName=" + encodeURIComponent(fileName) + "&relativeFilePath=" + relativeFilePath + "&fileId=" + field;
        window.open(previewUrl);
    }

    function previewDoc(field, fileName, relativeFilePath, coverContent) {
        if (!fileName) {
            fileName = '';
        }
        if (!relativeFilePath) {
            relativeFilePath = '';
        }
        var previewUrl = jsUseCtxPath + "/world/core/research/officePreview.do?fileName=" + encodeURIComponent(fileName) + "&relativeFilePath=" + relativeFilePath + "&fileId=" + field;
        window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverContent) + "&file=" + encodeURIComponent(previewUrl));
    }

    function downFile(fileName, fileId, relativeFilePath) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + '/world/core/research/downOrPdfPreview.do?action=download');
        var inputFileName = $("<input>");
        inputFileName.attr("type", "hidden");
        inputFileName.attr("name", "fileName");
        inputFileName.attr("value", fileName);
        var inputFileId = $("<input>");
        inputFileId.attr("type", "hidden");
        inputFileId.attr("name", "fileId");
        inputFileId.attr("value", fileId);
        var relativeFilePathInput = $("<input>");
        relativeFilePathInput.attr("type", "hidden");
        relativeFilePathInput.attr("name", "relativeFilePath");
        relativeFilePathInput.attr("value", relativeFilePath);
        $("body").append(form);
        form.append(inputFileName);
        form.append(inputFileId);
        form.append(relativeFilePathInput);
        form.submit();
        form.remove();
    }
    function fileupload() {
        mini.open({
            title: "模板上传",
            url: jsUseCtxPath + "/world/core/research/openTemplateWindow.do?fileType=0&menuType="+menuType+"&menuFlag="+menuFlag,
            width: 800,
            height: 400,
            showModal:false,
            allowResize: true,
            onload: function () {
                var iframe = this.getIFrameEl();
                var uploadParams={};
                uploadParams.standardId = '';
                uploadParams.scene='decodeGj';
                uploadParams.menuType = menuType;
                uploadParams.fileType = '0';
                var data = { projectParams: uploadParams };  //传递上传参数
                iframe.contentWindow.SetData(data);
            },
            ondestroy: function (action) {
                fileListGrid.load();
            }
        });
    }
    function deleteFile(record) {
        mini.confirm("确定删除？", "确定？",
            function (action) {
                if (action == "ok") {
                    var url = jsUseCtxPath + "/world/core/research/deleteFiles.do";
                    var data = {
                        menuType: menuType,
                        id: record.id,
                        fileName: record.fileName
                    };
                    $.post(
                        url,
                        data,
                        function (json) {
                            fileListGrid.load();
                        });
                }
            }
        );
    }
</script>
</body>
</html>
