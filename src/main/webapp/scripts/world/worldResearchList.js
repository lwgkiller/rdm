$(function () {
    if(menuFlag=='hide'){
        listGrid.hideColumn("fileCategory");
        $("#fileCategory").css("display","none");
    }
});

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
        s = '<span  title=' + worldResearchList_yl + ' style="color: silver" >' + worldResearchList_yl + '</span>';
    } else if (fileType == 'pdf') {
        s = '<span  title=' + worldResearchList_yl + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileId + '\',\'' + fileName + '\',\'' + relativeFilePath + '\',\'' + coverContent + '\')">' + worldResearchList_yl + '</span>';
    } else if (fileType == 'office') {
        s = '<span  title=' + worldResearchList_yl + ' style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileId + '\',\'' + fileName + '\',\'' + relativeFilePath + '\',\'' + coverContent + '\')">' + worldResearchList_yl + '</span>';
    } else if (fileType == 'pic') {
        s = '<span  title=' + worldResearchList_yl + ' style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileId + '\',\'' + fileName + '\',\'' + relativeFilePath + '\',\'' + coverContent + '\')">' + worldResearchList_yl + '</span>';
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

function deleteFile(record) {
    mini.confirm(worldResearchList_qdsc, worldResearchList_qd,
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
                        searchFrm()
                    });
            }
        }
    );
}

//删除记录
function submit() {
    var rows = [];
    var ids = [];
    rows = listGrid.getSelecteds();
    if (rows.length <= 0) {
        mini.alert(worldResearchList_qxz);
        return;
    }
    if (rows.length > 0) {
        for (var i = 0; i < rows.length; i++) {
            var r = rows[i];
            if (currentUserId != r.CREATE_BY_) {
                mini.alert(worldResearchList_zycjr);
                return;
            }
            if (r.applyStatus != '0' && r.applyStatus != '2') {
                mini.alert(worldResearchList_zydtj);
                return;
            }
            ids.push(r.id);
        }
    }
    mini.confirm(worldResearchList_qdtj, worldResearchList_ts, function (action) {
        if (action != 'ok') {
            return;
        } else {
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/world/core/research/submit.do",
                    method: 'POST',
                    data: {ids: ids.join(',')},
                    success: function (text) {
                        searchFrm()
                    }
                });
            }
        }
    });
}
function task(id,status) {
    var param = {id: id,applyStatus:status};
    var url = __rootPath + "/world/core/research/audit.do";
    var resultData = ajaxRequest(url, "POST", false, param);
    if(resultData){
        mini.alert(resultData.message);
        searchFrm()
    }
}
function batchAudit(_applyStatus) {
    var rows = [];
    var ids = [];
    rows = listGrid.getSelecteds();
    if (rows.length <= 0) {
        mini.alert(worldResearchList_qxz);
        return;
    }
    if (rows.length > 0) {
        for (var i = 0; i < rows.length; i++) {
            var r = rows[i];
            if (r.applyStatus != '1' ) {
                mini.alert(worldResearchList_zyspz);
                return;
            }
            ids.push(r.id);
        }
    }
    if (ids.length > 0) {
        _SubmitJson({
            url: jsUseCtxPath + "/world/core/research/batchAudit.do",
            method: 'POST',
            data: {ids: ids.join(','),applyStatus:_applyStatus},
            success: function (text) {
                searchFrm()
            }
        });
    }
}
function downTemplate() {
    mini.open({
        title: worldResearchList_mbxz,
        url: jsUseCtxPath + "/world/core/research/templateListPage.do?fileType=0&menuType="+menuType+"&menuFlag="+menuFlag,
        width: 800,
        height: 400,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
        }
    });
}
