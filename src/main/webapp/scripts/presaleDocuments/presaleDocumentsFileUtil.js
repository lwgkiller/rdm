//..检查文件是否存在
function newProductTrialProduceCheckFile(businessId, businessType, businessDes) {
    var checkFileResult;
    $.ajax({
        url: jsUseCtxPath + '/tdmCommon/core/file/newProductTrialProduceCheckFile.do?businessId=' +
        businessId + '&businessType=' + businessType + '&businessDes=' + businessDes,
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (jsonResult) {
            checkFileResult = jsonResult;
        }
    });
    return checkFileResult;
}
//..打开文件列表窗口
function openNewProductTrialProduceFileWindow(businessId, businessType, action, coverContent, isSingle) {
    mini.open({
        title: "文件列表",
        url: jsUseCtxPath + "/tdmCommon/core/file/openNewProductTrialProduceFileWindow.do?businessId=" +
        businessId + "&businessType=" + businessType + "&action=" + action + "&coverContent=" + coverContent + "&isSingle=" + isSingle,
        width: 800,
        height: 600,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
        }
    });
}
//..打开文件上传窗口
function openFileUploadWindow(url, callback) {
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
            callback();
        }
    });
}
//..文件操作渲染系列
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
                        deleteFileCallback();
                    }
                });
            }
        }
    );
}