
//打开文件上传窗口
function openDocMgrUploadWindow() {
    mini.open({
        title: "文件上传",
        url: jsUseCtxPath + "/wwrz/core/file/fileUploadWindow.do?fileType=common",
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
    commonDocListGrid.setUrl(url+mini.get("#fileName").getValue());
    commonDocListGrid.load();
}
function fileInfoRenderer(e) {
    var record = e.record;
    var s = '';
    s += returnPreviewSpan(record.fileName, record.id, record.mainId, coverContent, "wwrzFileUrl");
    s += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
        'onclick="downFile(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">下载</span>';
    if(!permission) {
        s+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:silver;">删除</span>';
    } else {
        s+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="deleteFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
    }
    return s;
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

function deleteFile(record) {
    mini.confirm("确定删除？", "确定？",
        function (action) {
            if (action == "ok") {
                var url = jsUseCtxPath + "/wwrz/core/file/delFile.do";
                var data = {
                    mainId: record.mainId,
                    id: record.id,
                    fileName: record.fileName
                };
                $.post(
                    url,
                    data,
                    function (json) {
                        searchDoc();
                    });
            }
        }
    );
}
