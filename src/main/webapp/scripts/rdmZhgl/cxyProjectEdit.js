$(function () {
    if (cxyProjectId) {
        var url = jsUseCtxPath + "/zhgl/core/cxy/queryCxyProjectById.do?cxyProjectId=" + cxyProjectId;
        $.ajax({
            url: url,
            method: 'get',
            success: function (json) {
                cxyProjectForm.setData(json);
            }
        });
        fileListGrid.load();
    } else {
        mini.get("responsibleUserId").setValue(currentUserId);
        mini.get("responsibleUserId").setText(currentUserName);
        mini.get("responsibleUserDepId").setValue(currentUserMainGroupId);
        mini.get("responsibleUserDepId").setText(currentUserMainGroupName);
    }
    //不同场景的处理
    if (action == 'detail') {
        cxyProjectForm.setEnabled(false);
    } else if (action == 'edit' || action == 'add') {
        mini.get("saveCxyProjectDraft").show();
        mini.get("commitCxyProject").show();
        mini.get("fileButtons").show();
    } else if (action == 'feedback') {
        mini.get("feedbackCxyProject").show();
        mini.get("fileButtons").show();
        mini.get("projectDesc").setEnabled(false);
        mini.get("undertaker").setEnabled(false);
        mini.get("collaborator").setEnabled(false);
        mini.get("responsibleUserId").setEnabled(false);
        mini.get("beginTime").setEnabled(false);
        mini.get("endTime").setEnabled(false);
        mini.get("contractAmount").setEnabled(false);
        mini.get("paidContractAmount").setEnabled(false);
        mini.get("remark").setEnabled(false);
        mini.get("projectType").setEnabled(false);
        mini.get("researchDirection").setEnabled(false);
        mini.get("contractIndicators").setEnabled(false);
        mini.get("implementation").setEnabled(false);
        mini.get("projectProperties").setEnabled(false);

    }
});

function saveCxyProjectDraft() {
    var postData = {};
    postData.id = $("#id").val();
    postData.projectDesc = mini.get("projectDesc").getValue();
    postData.undertaker = mini.get("undertaker").getValue();
    postData.collaborator = mini.get("collaborator").getValue();
    postData.responsibleUserId = mini.get("responsibleUserId").getValue();
    postData.responsibleUserDepId = mini.get("responsibleUserDepId").getValue();
    postData.beginTime = mini.get("beginTime").getText();
    postData.endTime = mini.get("endTime").getText();
    postData.contractAmount = mini.get("contractAmount").getValue();
    postData.paidContractAmount = mini.get("paidContractAmount").getValue();
    postData.remark = $.trim(mini.get("remark").getValue());
    postData.projectType = mini.get("projectType").getValue();
    postData.researchDirection = mini.get("researchDirection").getValue();
    postData.contractIndicators = $.trim(mini.get("contractIndicators").getValue());
    postData.completedIndicators = $.trim(mini.get("completedIndicators").getValue());
    postData.completionRate = mini.get("completionRate").getValue();
    postData.delayReason = mini.get("delayReason").getValue();
    postData.isSubmit = "0";
    postData.isDelay = mini.get("isDelay").getValue();
    postData.responsibleName = mini.get("responsibleName").getValue();
    postData.responsibleDepName = mini.get("responsibleDepName").getValue();
    postData.implementation = mini.get("implementation").getValue();
    postData.projectProperties = mini.get("projectProperties").getValue();
    if (postData.implementation) {
        mini.alert("不需要填写执行情况");
        return;
    }
    $.ajax({
        url: jsUseCtxPath + "/zhgl/core/cxy/saveCxyProjectDataDraft.do",
        type: 'POST',
        contentType: 'application/json',
        data: mini.encode(postData),
        success: function (returnData) {
            if (returnData && returnData.message) {
                mini.alert(returnData.message, '提示', function () {
                    if (returnData.success) {
                        var url = jsUseCtxPath + "/zhgl/core/cxy/editPage.do?cxyProjectId=" + returnData.data + "&action=edit";
                        window.location.href = url;
                    }
                });
            }
        }
    });
}


function commitCxyProject() {
    var postData = {};
    postData.id = $("#id").val();
    postData.projectDesc = mini.get("projectDesc").getValue();
    postData.undertaker = mini.get("undertaker").getValue();
    postData.collaborator = mini.get("collaborator").getValue();
    postData.responsibleUserId = mini.get("responsibleUserId").getValue();
    postData.responsibleUserDepId = mini.get("responsibleUserDepId").getValue();
    postData.beginTime = mini.get("beginTime").getText();
    postData.endTime = mini.get("endTime").getText();
    postData.contractAmount = mini.get("contractAmount").getValue();
    postData.paidContractAmount = mini.get("paidContractAmount").getValue();
    postData.remark = $.trim(mini.get("remark").getValue());
    postData.projectType = mini.get("projectType").getValue();
    postData.researchDirection = mini.get("researchDirection").getValue();
    postData.contractIndicators = $.trim(mini.get("contractIndicators").getValue());
    postData.completedIndicators = $.trim(mini.get("completedIndicators").getValue());
    postData.completionRate = mini.get("completionRate").getValue();
    postData.delayReason = mini.get("delayReason").getValue();
    postData.isSubmit = mini.get("isSubmit").getValue();
    postData.isDelay = mini.get("isDelay").getValue();
    postData.responsibleName = mini.get("responsibleName").getValue();
    postData.responsibleDepName = mini.get("responsibleDepName").getValue();
    postData.implementation = mini.get("implementation").getValue();
    postData.projectProperties = mini.get("projectProperties").getValue();
    if (postData.implementation && postData.isSubmit == '0') {
        mini.alert("不需要填写执行情况");
        return;
    }
    //检查必填项
    var checkResult = commitValidCheck(postData);
    if (!checkResult.success) {
        mini.alert(checkResult.reason);
        return;
    }
    postData.isSubmit = '1';
    $.ajax({
        url: jsUseCtxPath + "/zhgl/core/cxy/commitCxyProjectData.do",
        type: 'POST',
        contentType: 'application/json',
        data: mini.encode(postData),
        success: function (returnData) {
            if (returnData && returnData.message) {
                mini.alert(returnData.message, '提示', function () {
                    if (returnData.success) {
                        window.close();
                    }
                });
            }
        }
    });
}


function feedbackCxyProject() {
    var postData = {};
    postData.id = $("#id").val();
    postData.projectDesc = mini.get("projectDesc").getValue();
    postData.undertaker = mini.get("undertaker").getValue();
    postData.collaborator = mini.get("collaborator").getValue();
    postData.responsibleUserId = mini.get("responsibleUserId").getValue();
    postData.responsibleUserDepId = mini.get("responsibleUserDepId").getValue();
    postData.beginTime = mini.get("beginTime").getText();
    postData.endTime = mini.get("endTime").getText();
    postData.contractAmount = mini.get("contractAmount").getValue();
    postData.paidContractAmount = mini.get("paidContractAmount").getValue();
    postData.remark = $.trim(mini.get("remark").getValue());
    postData.projectType = mini.get("projectType").getValue();
    postData.researchDirection = mini.get("researchDirection").getValue();
    postData.contractIndicators = $.trim(mini.get("contractIndicators").getValue());
    postData.completedIndicators = $.trim(mini.get("completedIndicators").getValue());
    postData.completionRate = mini.get("completionRate").getValue();
    postData.delayReason = mini.get("delayReason").getValue();
    postData.isSubmit = mini.get("isSubmit").getValue();
    postData.isDelay = mini.get("isDelay").getValue();
    postData.implementation = mini.get("implementation").getValue();
    postData.projectProperties = mini.get("projectProperties").getValue();
    //检查必填项
    var checkResult = feedbackValidCheck(postData);
    if (!checkResult.success) {
        mini.alert(checkResult.reason);
        return;
    }

    $.ajax({
        url: jsUseCtxPath + "/zhgl/core/cxy/feedbackCxyProjectData.do",
        type: 'POST',
        contentType: 'application/json',
        data: mini.encode(postData),
        success: function (returnData) {
            if (returnData && returnData.message) {
                mini.alert(returnData.message, '提示', function () {
                    if (returnData.success) {
                        window.close();
                    }
                });
            }
        }
    });
}


function feedbackValidCheck(postData) {
    var checkResult = {};
    if (!postData.completedIndicators) {
        checkResult.success = false;
        checkResult.reason = '请填写目前已完成指标！';
        return checkResult;
    }
    if (!postData.completionRate) {
        checkResult.success = false;
        checkResult.reason = '请填写项目完成率！';
        return checkResult;
    }
    checkResult.success = true;
    return checkResult;
}


function commitValidCheck(postData) {
    var checkResult = {};
    if (!postData.responsibleUserId) {
        checkResult.success = false;
        checkResult.reason = '请选择项目负责人！';
        return checkResult;
    }
    if (!postData.beginTime) {
        checkResult.success = false;
        checkResult.reason = '请选择开始时间！';
        return checkResult;
    }
    if (!postData.endTime) {
        checkResult.success = false;
        checkResult.reason = '请选择结束时间！';
        return checkResult;
    }
    if (!postData.projectType) {
        checkResult.success = false;
        checkResult.reason = '请选择项目所属类型！';
        return checkResult;
    }
    if (!postData.researchDirection) {
        checkResult.success = false;
        checkResult.reason = '请选择技术研究方向！';
        return checkResult;
    }
    if (!postData.projectProperties) {
        checkResult.success = false;
        checkResult.reason = '请选择项目性质！';
        return checkResult;
    }
    checkResult.success = true;
    return checkResult;
}


function uploadCxyProjectFile() {
    var cxyProjectId = mini.get("id").getValue();
    if (!cxyProjectId) {
        mini.alert("请先点击‘保存草稿’进行表单创建！")
        return;
    }
    mini.open({
        title: "文件上传",
        url: jsUseCtxPath + "/zhgl/core/cxypojfile/openUploadWindow.do",
        width: 800,
        height: 350,
        showModal: false,
        allowResize: true,
        onload: function () {
            var iframe = this.getIFrameEl();
            var passParams = {};
            passParams.cxyProjectId = cxyProjectId;
            var data = {passParams: passParams};  //传递上传参数
            iframe.contentWindow.SetData(data);
        },
        ondestroy: function (action) {
            fileListGrid.load();
        }
    });
}


function returncxyProjectPreviewSpan(fileName, fileId, cxyProjectId) {
    var fileType = getFileType(fileName);
    var s = '';
    if (fileType == 'other') {
        s = '<span  title="预览" style="color: silver" >预览</span>';
    } else if (fileType == 'pdf') {
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + cxyProjectId + '\',\'' + coverContent + '\')">预览</span>';
    } else if (fileType == 'office') {
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + cxyProjectId + '\',\'' + coverContent + '\')">预览</span>';
    } else if (fileType == 'pic') {
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + cxyProjectId + '\',\'' + coverContent + '\')">预览</span>';
    }
    return s;
}


function downLoadCxyProjectFile(fileName, fileId, cxyProjectId) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/zhgl/core/cxypojfile/fileDownload.do?action=download");
    var inputFileName = $("<input>");
    inputFileName.attr("type", "hidden");
    inputFileName.attr("name", "fileName");
    inputFileName.attr("value", fileName);
    var inputJsjlId = $("<input>");
    inputJsjlId.attr("type", "hidden");
    inputJsjlId.attr("name", "cxyProjectId");
    inputJsjlId.attr("value", cxyProjectId);
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


function deleteCxyProjectFile(record) {
    mini.confirm("确定删除？", "确定？",
        function (action) {
            if (action == "ok") {
                var url = jsUseCtxPath + "/zhgl/core/cxypojfile/delete.do";
                var data = {
                    fileName: record.fileName,
                    id: record.id,
                    cxyProjectId: record.cxyProjectId
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


function previewPdf(fileName, fileId, cxyProjectId, coverConent) {
    if (!fileName) {
        fileName = '';
    }
    if (!fileId) {
        fileId = '';
    }
    if (!cxyProjectId) {
        cxyProjectId = '';
    }
    var previewUrl = jsUseCtxPath + "/zhgl/core/cxypojfile/fileDownload.do?action=preview&fileName=" + encodeURIComponent(fileName) + "&fileId=" + fileId + "&cxyProjectId=" + cxyProjectId;
    window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverConent) + "&file=" + encodeURIComponent(previewUrl));
}


function previewPic(fileName, fileId, cxyProjectId, coverConent) {
    if (!fileName) {
        fileName = '';
    }
    if (!fileId) {
        fileId = '';
    }
    if (!cxyProjectId) {
        cxyProjectId = '';
    }
    var previewUrl = jsUseCtxPath + "/zhgl/core/cxypojfile/imagePreview.do?fileName=" + encodeURIComponent(fileName) + "&fileId=" + fileId + "&cxyProjectId=" + cxyProjectId;
    window.open(previewUrl);
}


function previewDoc(fileName, fileId, cxyProjectId, coverConent) {
    if (!fileName) {
        fileName = '';
    }
    if (!fileId) {
        fileId = '';
    }
    if (!cxyProjectId) {
        cxyProjectId = '';
    }
    var previewUrl = jsUseCtxPath + "/zhgl/core/cxypojfile/officePreview.do?fileName=" + encodeURIComponent(fileName) + "&fileId=" + fileId + "&cxyProjectId=" + cxyProjectId;
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