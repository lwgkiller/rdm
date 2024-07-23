
//打开文件上传窗口
function openDocMgrUploadWindow() {
    if(!isManager&&currentUserName!='管理员') {
        mini.alert(xcmgDocMgrList_name2);
        return;
    }
    mini.open({
        title: xcmgDocMgrList_name3,
        url: jsUseCtxPath + "/xcmgProjectManager/core/fileUpload/xcmgDocMgrUploadWindow.do",
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

function openH5UploadWindow() {
    if(!isManager&&currentUserName!='管理员') {
        mini.alert(xcmgDocMgrList_name2);
        return;
    }
    mini.open({
        title: xcmgDocMgrList_name3,
        url: jsUseCtxPath + "/xcmgProjectManager/core/fileUpload/h5UploadWindow.do",
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
    commonDocListGrid.setUrl(url+mini.get("#docName").getValue());
    commonDocListGrid.load();
}

//下载文档
function downLoadDoc(fileName,relativeFilePath) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/xcmgProjectManager/core/fileUpload/fileDownload.do?action=download");
    var inputFileName = $("<input>");
    inputFileName.attr("type", "hidden");
    inputFileName.attr("name", "fileName");
    inputFileName.attr("value", fileName);
    var inputRelativeFilePath = $("<input>");
    inputRelativeFilePath.attr("type", "hidden");
    inputRelativeFilePath.attr("name", "relativeFilePath");
    inputRelativeFilePath.attr("value", relativeFilePath);
    var actionType = $("<input>");
    actionType.attr("type", "hidden");
    actionType.attr("name", "actionType");
    actionType.attr("value", "common");
    $("body").append(form);
    form.append(inputFileName);
    form.append(inputRelativeFilePath);
    form.append(actionType);
    form.submit();
    form.remove();
}

//删除文档
function deleteDoc(id,fileName,relativeFilePath) {
    mini.confirm(xcmgDocMgrList_name5, xcmgDocMgrList_name6,
        function (action) {
            if (action == "ok") {
                $.ajaxSettings.async = false;
                var url = jsUseCtxPath + "/xcmgProjectManager/core/fileUpload/xcmgDocMgrDelete.do";
                var data = {
                    id: id,
                    relativeFilePath: relativeFilePath,
                    fileName: fileName
                };
                $.post(
                    url,
                    data,
                    function (json) {
                        searchDoc();
                    });
                $.ajaxSettings.async = true;
            }
        }
    );
}