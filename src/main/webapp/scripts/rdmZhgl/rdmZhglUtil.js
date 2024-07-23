function downLoadFile(fileName, fileId, formId, urlValue) {
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
    inputFormId.attr("name", "formId");
    inputFormId.attr("value", formId);
    $("body").append(form);
    form.append(inputFileName);
    form.append(inputFileId);
    form.append(inputFormId);
    form.submit();
    form.remove();
}

//删除文档
function deleteFile(fileName, fileId, formId, urlValue) {
    mini.confirm("确定删除？", "提示",
        function (action) {
            if (action == "ok") {
                var url = jsUseCtxPath + urlValue;
                var data = {
                    fileName: fileName,
                    id: fileId,
                    formId: formId
                };
                $.ajax({
                    url: url,
                    method: 'post',
                    contentType: 'application/json',
                    data: mini.encode(data),
                    success: function (json) {
                        if (fileListGrid) {
                            fileListGrid.load();
                        }
                    }
                });
            }
        }
    );
}

function previewPdf6(fileName, fileId, formId, coverConent, url, fileType) {
    if (!fileName) {
        fileName = '';
    }
    if (!fileId) {
        fileId = '';
    }
    if (!formId) {
        formId = '';
    }
    var previewUrl = jsUseCtxPath + url + "&fileName=" + encodeURIComponent(fileName) + "&fileId=" + fileId + "&formId=" + formId;
    if (fileType != 'pic') {
        var preWindow = window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverConent) + "&file=" + encodeURIComponent(previewUrl));
        preWindow.onload = function () {
            if (fileName == '') {
                fileName = "文件预览";
            }
            preWindow.document.title = fileName;
        };
    } else {
        var preWindow = window.open(previewUrl);
        preWindow.onload = function () {
            if (fileName == '') {
                fileName = "文件预览";
            }
            preWindow.document.title = fileName;
        };
    }
    var loop = setInterval(function () {
        if (preWindow.closed) {
            clearInterval(loop);
        }
        else {
            preWindow.document.title = fileName;
        }
    }, 1000);
}

function previewPdf(fileName, fileId, formId, coverConent, url) {
    if (!fileName) {
        fileName = '';
    }
    if (!fileId) {
        fileId = '';
    }
    if (!formId) {
        formId = '';
    }
    var previewUrl = jsUseCtxPath + url + "?action=preview&fileName=" + encodeURIComponent(fileName) + "&fileId=" + fileId + "&formId=" + formId;
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

function previewPic(fileName, fileId, formId, coverConent, url) {
    if (!fileName) {
        fileName = '';
    }
    if (!fileId) {
        fileId = '';
    }
    if (!formId) {
        formId = '';
    }
    var previewUrl = jsUseCtxPath + url + "?fileName=" + encodeURIComponent(fileName) + "&fileId=" + fileId + "&formId=" + formId;
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

function previewDoc(fileName, fileId, formId, coverConent, url, fileSize) {
    if (!fileName) {
        fileName = '';
    }
    if (!fileId) {
        fileId = '';
    }
    if (!formId) {
        formId = '';
    }
    if(!fileSize) {
        fileSize='';
    } else {
        var fileSizeArr=fileSize.split(' ');
        if(fileSizeArr.length==2) {
            if((fileSizeArr[1]=='GB' || fileSizeArr[1]=='TB'
                || fileSizeArr[1]=='PB'|| fileSizeArr[1]=='EB'
                || fileSizeArr[1]=='ZB'|| fileSizeArr[1]=='YB')||(fileSizeArr[1]=='MB' && fileSizeArr[0] > 50)) {
                mini.alert("文件超过50MB不允许预览，请下载后查看！");
                return;
            }
        }
    }
    var previewUrl = jsUseCtxPath + url + "?fileName=" + encodeURIComponent(fileName) + "&fileId=" + fileId + "&formId=" + formId;
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

function onProgressRenderer(e) {
    var record = e.record;
    var value = e.value;
    var uid = record._uid;
    var s = '<div class="progressbar">'
        + '<div class="progressbar-percent" style="width:' + value + '%;"></div>'
        + '<div class="progressbar-label">' + value + '%</div>'
        + '</div>';
    return s;
}

function bytesToSize(bytes) {
    if (bytes === 0) return '0 B';
    var k = 1024,
        sizes = ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'],
        i = Math.floor(Math.log(bytes) / Math.log(k));

    return (bytes / Math.pow(k, i)).toPrecision(3) + ' ' + sizes[i];
}

function getUserInfoById(userId) {
    var userInfo = "";
    if (userId) {
        $.ajax({
            url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/' + userId + '/getUserInfoById.do',
            type: 'get',
            async: false,
            contentType: 'application/json',
            success: function (data) {
                userInfo = data;
            }
        });
    }
    return userInfo;
}
