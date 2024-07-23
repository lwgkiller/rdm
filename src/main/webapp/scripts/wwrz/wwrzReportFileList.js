
function addReport() {
    var action = "add";
    let url = jsUseCtxPath + "/wwrz/core/file/getEditPage.do?action=" + action;
    var title = "新增";
    mini.open({
        title: title,
        url: url,
        width: 1200,
        height: 600,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            searchFrm();
        }
    });
}
//删除记录
function removeRow() {
    var rows = [];
    rows = listGrid.getSelecteds();
    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }
    mini.confirm("确定删除选中记录？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var ids = [];
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                ids.push(r.id);
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/wwrz/core/file/removeReportFile.do",
                    method: 'POST',
                    data: {ids: ids.join(',')},
                    success: function (text) {
                        searchFrm();
                    }
                });
            }
        }
    });
}
//删除记录
function batchInvalid() {
    var rows = [];
    rows = listGrid.getSelecteds();
    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }
    mini.confirm("确定作废选中记录？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var ids = [];
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                ids.push(r.id);
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/wwrz/core/file/batchInvalid.do",
                    method: 'POST',
                    data: {ids: ids.join(',')},
                    success: function (text) {
                        searchFrm();
                    }
                });
            }
        }
    });
}
function onValidStatus(e) {
    var record = e.record;
    var valid = record.valid;
    var _html = '';
    var color = '';
    var text = '';
    if (valid == '0') {
        color = '#2edfa3';
        text = "有效";
    } else if (valid == '1') {
        color = 'red';
        text = "作废";
    }
    _html = '<span style="color: ' + color + '">' + text + '</span>'
    return _html;
}

function operationRendererSq(e) {
    var record = e.record;
    var glNet = record.glNet;
    var isJszx = record.isJszx;
    var isApply = record.isApply;
    var isPass = record.isPass;
    var fileName = record.fileName;
    var cellHtml = '';
    if (glNet) {
        if(fileName){
            if (isApply && isPass ) {
                cellHtml += returnPreviewSpan(record.fileName, record.id, record.mainId, coverContent, "wwrzFileUrl");
            } else if (isApply && !isPass) {
                cellHtml += '<span title="预览" style="color:#409EFF;cursor: pointer;" ' +
                    'onclick="previewBefore(\'' + record.id + '\',\'' + isApply + '\')">预览</span>';
            } else {
                cellHtml += '<span title="预览" style="color:#409EFF;cursor: pointer;" ' +
                    'onclick="previewBefore(\'' + record.id + '\',\'' + isApply + '\')">预览</span>';
            }
        }else{
            cellHtml  += '<span  title="预览" style="color: silver" >预览</span>';
        }
        cellHtml += '&nbsp;&nbsp;&nbsp;<span  title="下载" style="color: silver">下载</span>';
    } else if (!isJszx) {
        if(fileName){
            if (isApply && isPass) {
                cellHtml += returnPreviewSpan(record.fileName, record.id, record.mainId, coverContent, "wwrzFileUrl");
            } else if (isApply && !isPass) {
                cellHtml += '<span title="预览" style="color:#409EFF;cursor: pointer;" ' +
                    'onclick="previewBefore(\'' + record.id + '\',\'' + isApply + '\')">预览</span>';
            } else {
                cellHtml += '<span title="预览" style="color:#409EFF;cursor: pointer;" ' +
                    'onclick="previewBefore(\'' + record.id + '\',\'' + isApply + '\')">预览</span>';
            }
        }else{
            cellHtml  += '<span  title="预览" style="color: silver" >预览</span>';
        }
        cellHtml += '&nbsp;&nbsp;&nbsp;<span  title="下载" style="color: silver">下载</span>';
    } else {
        cellHtml += returnPreviewSpan(record.fileName, record.id, record.mainId, coverContent, "wwrzFileUrl", isApply);
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downFile(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">下载</span>';
    }
    if (permission) {
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="编辑" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="editFile(\'' + record.id + '\')">编辑</span>';
    }
    return cellHtml;
}

function returnPreviewSpan(fileName, relativeFilePath, actionType, coverContent, fileUrl) {
    var fileType = getFileType(fileName);
    var s = '';
    if (fileType == 'other') {
        s = '<span  title="预览" style="color: silver" >预览</span>';
    } else if (fileType == 'pdf') {
        var url = '/sys/core/commonInfo/pdfPreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + relativeFilePath + '\',\'' + actionType + '\',\'' + coverContent + '\',\'' + url + '\',\'' + fileUrl + '\')">预览</span>';
    } else if (fileType == 'office') {
        var url = '/sys/core/commonInfo/officePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + relativeFilePath + '\',\'' + actionType + '\',\'' + coverContent + '\',\'' + url + '\',\'' + fileUrl + '\')">预览</span>';
    } else if (fileType == 'pic') {
        var url = '/sys/core/commonInfo/imagePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + relativeFilePath + '\',\'' + actionType + '\',\'' + coverContent + '\',\'' + url + '\',\'' + fileUrl + '\')">预览</span>';
    }
    return s;
}

function editFile(id) {
    var url = jsUseCtxPath + "/wwrz/core/file/getEditPage.do?id=" + id;
    var title = "编辑";
    mini.open({
        title: title,
        url: url,
        width: 800,
        height: 600,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            searchFrm();
        }
    });
}


function previewBefore(fileId, isApply) {
    if (isApply == 'true') {
        mini.alert("您已经提交过申请，请等待审批通过！");
        return
    }
    //跳转到新增下载申请界面
    mini.confirm("当前操作会创建一个预览申请单，审批完成后可在此处预览，确定继续？", "权限不足", function (action) {
        if (action == "ok") {
            addApply(fileId);
        }
    })
}

function addApply(fileId) {
    var title = "新增预览申请";
    var width = getWindowSize().width;
    var height = getWindowSize().height;
    _OpenWindow({
        url: jsUseCtxPath + "/bpm/core/bpmInst/wwrzReportPrview/start.do?fileId=" + fileId,
        title: title,
        width: width,
        height: height,
        showMaxButton: true,
        showModal: true,
        allowResize: true,
        ondestroy: function () {
            searchFrm()
        }
    });
}


//下载文档
function downFile(record) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/sys/core/commonInfo/fileDownload.do?action=download");
    var inputFileName = $("<input>");
    inputFileName.attr("type", "hidden");
    inputFileName.attr("name", "fileName");
    inputFileName.attr("value", record.fileName);
    var detailId = $("<input>");
    detailId.attr("type", "hidden");
    detailId.attr("name", "formId");
    detailId.attr("value", record.mainId);
    var fileId = $("<input>");
    fileId.attr("type", "hidden");
    fileId.attr("name", "fileId");
    fileId.attr("value", record.id);
    var fileUrl = $("<input>");
    fileUrl.attr("type", "hidden");
    fileUrl.attr("name", "fileUrl");
    fileUrl.attr("value", "wwrzFileUrl");
    $("body").append(form);
    form.append(inputFileName);
    form.append(detailId);
    form.append(fileId);
    form.append(fileUrl);
    form.submit();
    form.remove();
}


function exportExcel() {
    var params = [];
    var parent = $(".search-form");
    var inputAry = $("input", parent);
    inputAry.each(function (i) {
        var el = $(this);
        var obj = {};
        obj.name = el.attr("name");
        if (!obj.name) return true;
        obj.value = el.val();
        params.push(obj);
    });
    $("#filter").val(mini.encode(params));
    var excelForm = $("#excelForm");
    excelForm.submit();
}


//导入
function openImportWindow() {
    importWindow.show();
}
function closeImportWindow() {
    importWindow.hide();
    clearUploadFile();
    searchFrm();
}

//导入模板下载
function downImportTemplate() {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/wwrz/core/file/importTemplateDownload.do");
    $("body").append(form);
    form.submit();
    form.remove();
}

//触发文件选择
function uploadFile() {
    $("#inputFile").click();
}

//文件类型判断及文件名显示
function  getSelectFile() {
    var fileList = $("#inputFile")[0].files;
    if (fileList && fileList.length > 0) {
        var fileNameSuffix = fileList[0].name.split('.').pop();
        if (fileNameSuffix == 'xls'||fileNameSuffix == 'xlsx') {
            mini.get("fileName").setValue(fileList[0].name);
        }
        else {
            clearUploadFile();
            mini.alert('请上传excel文件！');
        }
    }
}

//清空文件
function clearUploadFile() {
    $("#inputFile").val('');
    mini.get("fileName").setValue('');
}

//上传批量导入
function importDocument() {
    var file = null;
    var fileList = $("#inputFile")[0].files;
    if (fileList && fileList.length > 0) {
        file = fileList[0];
    }
    if (!file) {
        mini.alert('请选择文件！');
        return;
    }
    //XMLHttpRequest方式上传表单
    var xhr = false;
    try {
        //尝试创建 XMLHttpRequest 对象，除 IE 外的浏览器都支持这个方法。
        xhr = new XMLHttpRequest();
    } catch (e) {
        //使用较新版本的 IE 创建 IE 兼容的对象（Msxml2.XMLHTTP）。
        xhr = ActiveXobject("Msxml12.XMLHTTP");
    }

    if (xhr.upload) {
        xhr.onreadystatechange = function (e) {
            if (xhr.readyState == 4) {
                if (xhr.status == 200) {
                    if (xhr.responseText) {
                        var returnObj = JSON.parse(xhr.responseText);
                        var message = '';
                        if (returnObj.message) {
                            message = returnObj.message;
                        }
                        mini.alert(message);
                    }
                }
            }
        };

        // 开始上传
        xhr.open('POST', jsUseCtxPath + '/wwrz/core/file/importExcel.do', false);
        xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        var fd = new FormData();
        fd.append('documentImportFile', file);
        xhr.send(fd);
    }
}
