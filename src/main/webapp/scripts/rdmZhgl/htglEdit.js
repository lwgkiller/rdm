//..
$(function () {
    if (contractId) {
        var url = jsUseCtxPath + "/zhgl/core/htgl/queryContractById.do?contractId=" + contractId;
        $.ajax({
            url: url,
            method: 'get',
            success: function (json) {
                contractForm.setData(json);
                if (action == 'copy') {
                    mini.get("id").setValue("");
                    mini.get("contractNo").setValue("");
                } else {
                    fileListGrid.load();
                }
                if (json.signDate) {
                    mini.get("isDiscard").setEnabled(false);
                }
            }
        });

    } else {
        mini.get("signerUserId").setValue(currentUserId);
        mini.get("signerUserId").setText(currentUserName);
        mini.get("signerUserDepId").setValue(currentUserMainGroupId);
        mini.get("signerUserDepId").setText(currentUserMainGroupName);
    }
    //不同场景的处理
    if (action == 'detail') {
        contractForm.setEnabled(false);
    } else if (action == 'edit' || action == 'add') {
        mini.get("saveContract").show();
        mini.get("fileButtons").show();
    } else if (action == 'copy') {
        mini.get("saveContract").show();
        mini.get("fileButtons").show();
    }
});
//..
function saveContract() {
    var postData = {};
    postData.id = $("#id").val();
    postData.contractNo = mini.get("contractNo").getValue();
    postData.contractDesc = mini.get("contractDesc").getValue();
    postData.signerUserId = mini.get("signerUserId").getValue();
    postData.signerUserDepId = mini.get("signerUserDepId").getValue();
    postData.signYear = mini.get("signYear").getValue();
    postData.signMonth = mini.get("signMonth").getValue();
    postData.signDate = mini.get("signDate").getValue();
    postData.CAndTStatus = mini.get("CAndTStatus").getValue();
    postData.partA = mini.get("partA").getValue();
    postData.partB = mini.get("partB").getValue();
    postData.partC = mini.get("partC").getValue();
    postData.partD = mini.get("partD").getValue();
    postData.isSingHonest = mini.get("isSingHonest").getValue();
    postData.isRecord = mini.get("isRecord").getValue();
    postData.isFile = mini.get("isFile").getValue();
    postData.isDiscard = mini.get("isDiscard").getValue();
    //检查必填项
    var checkResult = saveValidCheck(postData);
    if (!checkResult.success) {
        mini.alert(checkResult.reason);
        return;
    }

    $.ajax({
        url: jsUseCtxPath + "/zhgl/core/htgl/saveContractData.do",
        type: 'POST',
        contentType: 'application/json',
        data: mini.encode(postData),
        success: function (returnData) {
            if (returnData && returnData.message) {
                mini.alert(returnData.message, htglEdit_name2, function () {
                    if (returnData.success) {
                        var url = jsUseCtxPath + "/zhgl/core/htgl/editPage.do?contractId=" +
                            returnData.data + "&action=edit";
                        window.location.href = url;
                    }
                });
            }
        }
    });
}
//..
function saveValidCheck(postData) {
    var checkResult = {};
    if (!postData.contractDesc) {
        checkResult.success = false;
        checkResult.reason = htglEdit_name3;
        return checkResult;
    }
    if (!postData.signerUserId) {
        checkResult.success = false;
        checkResult.reason = htglEdit_name4;
        return checkResult;
    }
    if (!postData.signerUserDepId) {
        checkResult.success = false;
        checkResult.reason = htglEdit_name5;
        return checkResult;
    }
    if (!postData.signYear) {
        checkResult.success = false;
        checkResult.reason = htglEdit_name6;
        return checkResult;
    }
    if (!postData.signMonth) {
        checkResult.success = false;
        checkResult.reason = htglEdit_name7;
        return checkResult;
    }
    if (!postData.partA) {
        checkResult.success = false;
        checkResult.reason = htglEdit_name8;
        return checkResult;
    }
    if (!postData.partB) {
        checkResult.success = false;
        checkResult.reason = htglEdit_name9;
        return checkResult;
    }
    checkResult.success = true;
    return checkResult;
}

//..
function uploadContractFile() {
    var contractId = mini.get("id").getValue();
    if (!contractId) {
        mini.alert(htglEdit_name10)
        return;
    }
    mini.open({
        title: htglEdit_name11,
        url: jsUseCtxPath + "/zhgl/core/contractfile/openUploadWindow.do",
        width: 800,
        height: 350,
        showModal: false,
        allowResize: true,
        onload: function () {
            var iframe = this.getIFrameEl();
            var passParams = {};
            passParams.contractId = contractId;
            var data = {passParams: passParams};  //传递上传参数
            iframe.contentWindow.SetData(data);
        },
        ondestroy: function (action) {
            fileListGrid.load();
        }
    });
}

//..
function returnContractPreviewSpan(fileName, fileId, contractId) {
    var fileType = getFileType(fileName);
    var s = '';
    if (fileType == 'other') {
        s = '<span  title=' + htglEdit_name12 + ' style="color: silver" >' + htglEdit_name12 + '</span>';
    } else if (fileType == 'pdf') {
        s = '<span  title=' + htglEdit_name12 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + contractId + '\',\'' + coverContent + '\')">' + htglEdit_name12 + '</span>';
    } else if (fileType == 'office') {
        s = '<span  title=' + htglEdit_name12 + ' style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + contractId + '\',\'' + coverContent + '\')">' + htglEdit_name12 + '</span>';
    } else if (fileType == 'pic') {
        s = '<span  title=' + htglEdit_name12 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + contractId + '\',\'' + coverContent + '\')">' + htglEdit_name12 + '</span>';
    }
    return s;
}

//..
function downLoadContractFile(fileName, fileId, contractId) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/zhgl/core/contractfile/fileDownload.do?action=download");
    var inputFileName = $("<input>");
    inputFileName.attr("type", "hidden");
    inputFileName.attr("name", "fileName");
    inputFileName.attr("value", fileName);
    var inputJsjlId = $("<input>");
    inputJsjlId.attr("type", "hidden");
    inputJsjlId.attr("name", "contractId");
    inputJsjlId.attr("value", contractId);
    var inputFileId = $("<input>");
    inputFileId.attr("type", "hidden");
    inputFileId.attr("name", "fileId");
    inputFileId.attr("value", fileId);
    $("body").append(form);
    form.append(inputFileName);
    form.append(inputJsjlId);
    form.append(inputFileId);
    form.submit();
    form.remove();
}

//..
function deleteContractFile(record) {
    mini.confirm(htglEdit_name13, htglEdit_name14,
        function (action) {
            if (action == "ok") {
                var url = jsUseCtxPath + "/zhgl/core/contractfile/delete.do";
                var data = {
                    fileName: record.fileName,
                    id: record.id,
                    contractId: record.contractId
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

//..
function previewPdf(fileName, fileId, contractId, coverConent) {
    if (!fileName) {
        fileName = '';
    }
    if (!fileId) {
        fileId = '';
    }
    if (!contractId) {
        contractId = '';
    }
    var previewUrl = jsUseCtxPath + "/zhgl/core/contractfile/fileDownload.do?action=preview&fileName=" + encodeURIComponent(fileName) + "&fileId=" + fileId + "&contractId=" + contractId;
    window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverConent) + "&file=" + encodeURIComponent(previewUrl));
}

//..
function previewPic(fileName, fileId, contractId, coverConent) {
    if (!fileName) {
        fileName = '';
    }
    if (!fileId) {
        fileId = '';
    }
    if (!contractId) {
        contractId = '';
    }
    var previewUrl = jsUseCtxPath + "/zhgl/core/contractfile/imagePreview.do?fileName=" + encodeURIComponent(fileName) + "&fileId=" + fileId + "&contractId=" + contractId;
    window.open(previewUrl);
}

//..
function previewDoc(fileName, fileId, contractId, coverConent) {
    if (!fileName) {
        fileName = '';
    }
    if (!fileId) {
        fileId = '';
    }
    if (!contractId) {
        contractId = '';
    }
    var previewUrl = jsUseCtxPath + "/zhgl/core/contractfile/officePreview.do?fileName=" + encodeURIComponent(fileName) + "&fileId=" + fileId + "&contractId=" + contractId;
    window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverContent) + "&file=" + encodeURIComponent(previewUrl));
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