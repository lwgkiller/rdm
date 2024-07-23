<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>文件列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar" id="fileButtons">
    <a id="uploadFile" class="mini-button" style="margin-bottom: 5px" onclick="uploadFile">上传附件</a>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="fileListGrid" class="mini-datagrid" style="height: 99%" allowResize="false"
         idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
         multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false" allowCellWrap="true" showVGridLines="true">
        <div property="columns">
            <div field="id" headerAlign="left" visible="false">id</div>
            <div type="indexcolumn" headerAlign="center">序号</div>
            <div field="fileName" align="center" headerAlign="center">附件名</div>
            <div field="fileSize" align="center" headerAlign="center">附件大小</div>
            <div field="fileDesc" align="center" headerAlign="center" width="100">备注说明</div>
            <div field="action" width="100" headerAlign='center' align="center" renderer="fileRenderer">操作</div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var fileListGrid = mini.get("fileListGrid");
    var businessId = "${businessId}";
    var businessType = "${businessType}";
    var action = "${action}";
    var coverContent = "${coverContent}";
    var currentUserId = "${currentUserId}";
    //..
    var url = jsUseCtxPath + "/productDataManagement/core/attachedtoolsSpectrum/getFileListInfos.do?businessId=" +
        businessId + "&businessType=" + businessType;
    //..
    $(function () {
        if (action == 'detail') {
            mini.get('uploadFile').setEnabled(false);
        }
        fileListGrid.setUrl(url);
        fileListGrid.load();
    });
    //..
    function uploadFile() {
        var urlCut = "/productDataManagement/core/attachedtoolsSpectrum/fileUpload.do";
        url = "/productDataManagement/core/attachedtoolsSpectrum/openFileUploadWindow.do?businessId=" + businessId +
            "&businessType=" + businessType + "&urlCut=" + urlCut + "&fileType=other";
        openFileUploadWindow(jsUseCtxPath + url);
    }
    //..
    function openFileUploadWindow(url) {
        mini.open({
            title: "文件上传",
            url: url,
            width: 750,
            height: 450,
            showModal: false,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (returnData) {
                fileListGrid.load();
            }
        });
    }
    //..
    function fileRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        var previewUrls = {
            pdf: "/productDataManagement/core/attachedtoolsSpectrum/pdfPreview.do",
            office: "/productDataManagement/core/attachedtoolsSpectrum/officePreview.do",
            pic: "/productDataManagement/core/attachedtoolsSpectrum/imagePreview.do"
        };
        var downLoadUrl = "/productDataManagement/core/attachedtoolsSpectrum/pdfPreview.do";
        var deleteUrl = "/productDataManagement/core/attachedtoolsSpectrum/deleteFile.do";
        cellHtml = returnPreviewSpan(record.fileName, record.id, record.businessId, record.businessType, coverContent, previewUrls);
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.businessId + '\',\'' +
            record.businessType + '\',\'' + downLoadUrl + '\')">下载</span>';
        //增加删除按钮
        if (action == "edit") {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.businessId + '\',\'' +
                record.businessType + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }
    //..
    function returnPreviewSpan(fileName, fileId, businessId, businessType, coverContent, previewUrls) {
        var fileType = getFileType(fileName);
        var s = '';
        if (fileType == 'other') {
            s = '<span  title="预览" style="color: silver" >预览</span>';
        } else if (fileType == 'pdf') {
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId +
                '\',\'' + businessId + '\',\'' + businessType + '\',\'' + coverContent + '\',\'' + previewUrls.pdf + '\')">预览</span>';
        } else if (fileType == 'office') {
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId +
                '\',\'' + businessId + '\',\'' + businessType + '\',\'' + coverContent + '\',\'' + previewUrls.office + '\')">预览</span>';
        } else if (fileType == 'pic') {
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId +
                '\',\'' + businessId + '\',\'' + businessType + '\',\'' + coverContent + '\',\'' + previewUrls.pic + '\')">预览</span>';
        }
        return s;
    }
    //..
    function previewPdf(fileName, fileId, businessId, businessType, coverConent, url) {
        if (!fileName) {
            fileName = '';
        }
        if (!fileId) {
            fileId = '';
        }
        if (!businessId) {
            businessId = '';
        }
        if (!businessType) {
            businessType = '';
        }
        var previewUrl = jsUseCtxPath + url + "?action=preview&fileName=" + encodeURIComponent(fileName) + "&fileId=" + fileId + "&businessId=" + businessId + "&businessType=" + businessType;
        var preWindow = window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverConent) + "&file=" + encodeURIComponent(previewUrl));
        // mh 预览title原为pdf的描述，改为文件名或固定字符
        preWindow.onload = function () {
            if (fileName == '') {
                fileName = "文件预览";
            }
            preWindow.document.title = fileName;
        };
        var loop = setInterval(function () {
            if (preWindow.closed) {
                clearInterval(loop);
            }
            else {
                preWindow.document.title = fileName;
            }
        }, 1000);
    }
    //..
    function previewPic(fileName, fileId, businessId, businessType, coverConent, url) {
        if (!fileName) {
            fileName = '';
        }
        if (!fileId) {
            fileId = '';
        }
        if (!businessId) {
            businessId = '';
        }
        var previewUrl = jsUseCtxPath + url + "?fileName=" + encodeURIComponent(fileName) + "&fileId=" + fileId + "&businessId=" + businessId + "&businessType=" + businessType;
        var preWindow = window.open(previewUrl);
        preWindow.onload = function () {
            if (fileName == '') {
                fileName = "文件预览";
            }
            preWindow.document.title = fileName;
        };
        var loop = setInterval(function () {
            if (preWindow.closed) {
                clearInterval(loop);
            }
            else {
                preWindow.document.title = fileName;
            }
        }, 1000);
    }
    //..
    function previewDoc(fileName, fileId, businessId, businessType, coverConent, url) {
        if (!fileName) {
            fileName = '';
        }
        if (!fileId) {
            fileId = '';
        }
        if (!businessId) {
            businessId = '';
        }
        var previewUrl = jsUseCtxPath + url + "?fileName=" + encodeURIComponent(fileName) + "&fileId=" + fileId + "&businessId=" + businessId + "&businessType=" + businessType;
        var preWindow = window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverContent) + "&file=" + encodeURIComponent(previewUrl));
        preWindow.onload = function () {
            if (fileName == '') {
                fileName = "文件预览";
            }
            preWindow.document.title = fileName;
        };
        var loop = setInterval(function () {
            if (preWindow.closed) {
                clearInterval(loop);
            }
            else {
                preWindow.document.title = fileName;
            }
        }, 1000);
    }
    //..
    function downLoadFile(fileName, fileId, businessId, businessType, urlValue) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + urlValue);
        var inputFileName = $("<input>");
        inputFileName.attr("type", "hidden");
        inputFileName.attr("name", "fileName");
        inputFileName.attr("value", fileName);
        var inputFileId = $("<input>");
        inputFileId.attr("type", "hidden");
        inputFileId.attr("name", "fileId");
        inputFileId.attr("value", fileId);
        var inputFormId = $("<input>");
        inputFormId.attr("type", "hidden");
        inputFormId.attr("name", "businessId");
        inputFormId.attr("value", businessId);
        var inputBusinessType = $("<input>");
        inputBusinessType.attr("type", "hidden");
        inputBusinessType.attr("name", "businessType");
        inputBusinessType.attr("value", businessType);
        $("body").append(form);
        form.append(inputFileName);
        form.append(inputFileId);
        form.append(inputFormId);
        form.append(inputBusinessType);
        form.submit();
        form.remove();
    }
    //..
    function deleteFile(fileName, fileId, businessId, businessType, urlValue) {
        mini.confirm("确定删除？", "提示",
            function (action) {
                if (action == "ok") {
                    var url = jsUseCtxPath + urlValue;
                    var data = {
                        fileName: fileName,
                        id: fileId,
                        businessId: businessId,
                        businessType: businessType
                    };
                    $.ajax({
                        url: url,
                        method: 'post',
                        contentType: 'application/json',
                        data: mini.encode(data),
                        success: function (json) {
                            fileListGrid.load();
                        }
                    });
                }
            }
        );
    }
</script>
<redxun:gridScript gridId="fileListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
