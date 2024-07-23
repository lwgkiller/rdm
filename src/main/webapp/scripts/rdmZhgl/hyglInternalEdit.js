//..
$(function () {
    if (meetingId) {
        var url = jsUseCtxPath + "/zhgl/core/hyglInternal/queryMeetingById.do?meetingId=" + meetingId;
        $.ajax({
            url: url,
            method: 'get',
            success: function (json) {
                meetingForm.setData(json);
            }
        });
        fileListGrid.load();
        planListGrid.load();
    } else {
        mini.get("meetingOrgDepId").setValue(currentUserMainGroupId);
        mini.get("meetingOrgDepId").setText(currentUserMainGroupName);
        mini.get("meetingOrgUserId").setValue(currentUserId);
        mini.get("meetingOrgUserId").setText(currentUserName);
    }
    //不同场景的处理
    if (action == 'detail') {
        meetingForm.setEnabled(false);
    } else if (action == 'edit' || action == 'add') {
        mini.get("saveMeetingDraft").show();
        mini.get("commitMeeting").show();
        mini.get("fileButtons").show();
        mini.get("planButtons").show();
    } else if (action == 'feedback') {
        mini.get("feedbackMeeting").show();
        mini.get("fileButtons").show();
        mini.get("planButtons").show();
        mini.get("meetingOrgDepId").setEnabled(false);
        mini.get("meetingOrgUserId").setEnabled(false);
        mini.get("meetingUserIds").setEnabled(false);
        mini.get("meetingModel").setEnabled(false);
        mini.get("meetingTheme").setEnabled(false);
        mini.get("sendNotice").setEnabled(false);
        mini.get("generationNo").setEnabled(false);
    }
});
//..
function saveMeetingDraft() {
    var meetingModelId = mini.get("meetingModel").getValue();
    if(!meetingModelId) {
        mini.alert("请选择“会议类型”");
        return;
    }
    var postData = {};
    postData.id = $("#id").val();
    postData.meetingNo = mini.get("meetingNo").getValue();
    postData.meetingOrgDepId = mini.get("meetingOrgDepId").getValue();
    postData.meetingOrgUserId = mini.get("meetingOrgUserId").getValue();
    postData.meetingUserIds = mini.get("meetingUserIds").getValue();
    postData.meetingTime = mini.get("meetingTime").getText();
    postData.meetingPlace = mini.get("meetingPlace").getValue();
    postData.meetingModelId = mini.get("meetingModel").getValue();
    postData.meetingTheme = mini.get("meetingTheme").getValue();
    postData.contentAndConclusion = $.trim(mini.get("contentAndConclusion").getValue());
    postData.planAndResult = $.trim(mini.get("planAndResult").getValue());
    postData.recordStatus = '草稿';
    postData.generationNo = mini.get("generationNo").checked;//todo:改了
    $.ajax({
        url: jsUseCtxPath + "/zhgl/core/hyglInternal/saveMeetingDataDraft.do",
        type: 'POST',
        contentType: 'application/json',
        data: mini.encode(postData),
        success: function (returnData) {
            if (returnData && returnData.message) {
                mini.alert(returnData.message, '提示', function () {
                    if (returnData.success) {
                        var url = jsUseCtxPath + "/zhgl/core/hyglInternal/editPage.do?meetingId=" + returnData.data + "&action=edit";
                        window.location.href = url;
                    }
                });
            }
        }
    });
}
//..
function commitMeeting() {
    var postData = {};
    postData.id = $("#id").val();
    postData.meetingNo = mini.get("meetingNo").getValue();
    postData.meetingOrgDepId = mini.get("meetingOrgDepId").getValue();
    postData.meetingOrgUserId = mini.get("meetingOrgUserId").getValue();
    postData.meetingUserIds = mini.get("meetingUserIds").getValue();
    postData.meetingTime = mini.get("meetingTime").getText();
    postData.meetingPlace = mini.get("meetingPlace").getValue();
    postData.meetingModelId = mini.get("meetingModel").getValue();
    var meetingModelData=mini.get("meetingModel").getSelected();
    if(meetingModelData) {
        postData.meetingModelDescp =meetingModelData.descp;
    }
    postData.meetingTheme = mini.get("meetingTheme").getValue();
    postData.contentAndConclusion = $.trim(mini.get("contentAndConclusion").getValue());
    postData.planAndResult = $.trim(mini.get("planAndResult").getValue());
    postData.recordStatus = '已提交';
    postData.sendNotice = mini.get("sendNotice").checked;
    postData.generationNo = mini.get("generationNo").checked;
    //检查必填项
    var checkResult = commitValidCheck(postData);
    if (!checkResult.success) {
        mini.alert(checkResult.reason);
        return;
    }

    $.ajax({
        url: jsUseCtxPath + "/zhgl/core/hyglInternal/commitMeetingData.do",
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
//..
function feedbackMeeting() {
    var postData = {};
    postData.id = $("#id").val();
    postData.meetingNo = mini.get("meetingNo").getValue();
    postData.meetingOrgDepId = mini.get("meetingOrgDepId").getValue();
    postData.meetingOrgUserId = mini.get("meetingOrgUserId").getValue();
    postData.meetingUserIds = mini.get("meetingUserIds").getValue();
    postData.meetingTime = mini.get("meetingTime").getText();
    postData.meetingPlace = mini.get("meetingPlace").getValue();
    postData.meetingModelId = mini.get("meetingModel").getValue();
    var meetingModelData=mini.get("meetingModel").getSelected();
    if(meetingModelData) {
        postData.meetingModelDescp =meetingModelData.descp;
    }
    postData.meetingTheme = mini.get("meetingTheme").getValue();
    postData.contentAndConclusion = $.trim(mini.get("contentAndConclusion").getValue());
    postData.planAndResult = $.trim(mini.get("planAndResult").getValue());
    postData.recordStatus = '已提交';
    postData.generationNo = mini.get("generationNo").checked;//todo:改了
    //检查必填项
    var checkResult = feedbackValidCheck(postData);
    if (!checkResult.success) {
        mini.alert(checkResult.reason);
        return;
    }

    $.ajax({
        url: jsUseCtxPath + "/zhgl/core/hyglInternal/feedbackMeetingData.do",
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
//..
function feedbackValidCheck(postData) {
    var checkResult = {};
    if (!postData.meetingTime) {
        checkResult.success = false;
        checkResult.reason = '请选择会议时间！';
        return checkResult;
    }
    if (!postData.meetingPlace) {
        checkResult.success = false;
        checkResult.reason = '请填写会议地点！';
        return checkResult;
    }
    checkResult.success = true;
    return checkResult;
}
//..
function commitValidCheck(postData) {
    var checkResult = {};
    if (!postData.meetingOrgDepId) {
        checkResult.success = false;
        checkResult.reason = '请选择部门！';
        return checkResult;
    }
    if (!postData.meetingOrgUserId) {
        checkResult.success = false;
        checkResult.reason = '请选择组织者！';
        return checkResult;
    }
    if (!postData.meetingUserIds) {
        checkResult.success = false;
        checkResult.reason = '请选择参会人员！';
        return checkResult;
    }
    if (!postData.meetingTime) {
        checkResult.success = false;
        checkResult.reason = '请选择会议时间！';
        return checkResult;
    }
    if (!postData.meetingPlace) {
        checkResult.success = false;
        checkResult.reason = '请填写会议地点！';
        return checkResult;
    }
    if (!postData.meetingModelId) {
        checkResult.success = false;
        checkResult.reason = '请选择会议类型！';
        return checkResult;
    }
    if (!postData.meetingTheme) {
        checkResult.success = false;
        checkResult.reason = '请填写会议主题！';
        return checkResult;
    }
    checkResult.success = true;
    return checkResult;
}
//..
function uploadMeetingFile() {
    var meetingId = mini.get("id").getValue();
    if (!meetingId) {
        mini.alert("请先点击‘保存草稿’进行表单创建！")
        return;
    }
    mini.open({
        title: "文件上传",
        url: jsUseCtxPath + "/zhgl/core/hyglInternal/openUploadWindow.do?meetingId=" + meetingId,
        width: 800,
        height: 350,
        showModal: false,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            if (fileListGrid) {
                fileListGrid.load();
            }
        }
    });
}
//..
function returnMeetingPreviewSpan(fileName, fileId, formId, coverContent) {
    var fileType = getFileType(fileName);
    var s = '';
    if (fileType == 'other') {
        s = '<span  title="预览" style="color: silver" >预览</span>';
    } else if (fileType == 'pdf') {
        var url = '/zhgl/core/hyglInternal/meetingPdfPreviewAndAllDownload.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
    } else if (fileType == 'office') {
        var url = '/zhgl/core/hyglInternal/meetingOfficePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
    } else if (fileType == 'pic') {
        var url = '/zhgl/core/hyglInternal/meetingImagePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
    }
    return s;
}
//..
function addMeetingPlan() {
    var newRow = {}
    newRow.id = "";
    newRow.meetingId = mini.get("id").getValue();
    newRow.meetingContent = "";
    newRow.meetingPlanRespUserIds = "";
    newRow.meetingPlanEndTime = "";
    newRow.meetingPlanCompletion = "";
    newRow.isComplete = "false";
    addRowGrid("planListGrid", newRow);
}
//..
function deleteMeetingPlan() {
    var row = planListGrid.getSelected();
    if (!row) {
        mini.alert("请选中一条记录");
        return;
    }
    if (row.id == "") {
        delRowGrid("planListGrid");
        return;
    }
    mini.confirm("确定删除选中记录？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var id = row.id;
            _SubmitJson({
                url: jsUseCtxPath + "/zhgl/core/hyglInternal/deleteOneMeetingPlan.do",
                method: 'POST',
                data: {id: id,meetingId:meetingId},
                success: function (returnData) {
                    if (returnData) {
                        if (returnData && returnData.message) {
                            mini.alert(returnData.message, '提示', function () {
                                if (returnData.success) {
                                    planListGrid.reload();
                                }
                            });
                        }
                    }
                }
            });
        }
    });
}
//..
function saveMeetingPlan() {
    var meetingId = mini.get("id").getValue();
    if (!meetingId) {
        mini.alert("请先点击‘保存草稿’进行计划创建！");
        return;
    }

    planListGrid.validate();
    if (!planListGrid.isValid()) {
        var error = planListGrid.getCellErrors()[0];
        planListGrid.beginEditCell(error.record, error.column);
        mini.alert(error.column.header + error.errorText);
        return;
    }

    var postData = planListGrid.data;
    var postDataJson = mini.encode(postData);
    $.ajax({
        url: jsUseCtxPath + "/zhgl/core/hyglInternal/saveMeetingPlan.do?meetingId="+meetingId,
        type: 'POST',
        contentType: 'application/json',
        data: postDataJson,
        success: function (returnData) {
            if (returnData && returnData.message) {
                mini.alert(returnData.message, '提示', function () {
                    if (returnData.success) {
                        planListGrid.reload();
                    }
                });
            }
        }
    });
    debugger;
}
//..
function isCompleteRenderer(e) {
    var record = e.record;
    var isComplete = record.isComplete;
    var arr = [{'key': 'true', 'value': '是', 'css': 'green'},
        {'key': 'false', 'value': '否', 'css': 'red'}];
    return $.formatItemValue(arr, isComplete);
}
//..
function onCellValidation(e) {
    if (e.field == 'meetingContent' && (!e.value || e.value == '')) {
        e.isValid = false;
        e.errorText = '不能为空！';
    }
    if (e.field == 'meetingPlanRespUserIds' && (!e.value || e.value == '')) {
        e.isValid = false;
        e.errorText = '不能为空！';
    }
    if (e.field == 'meetingPlanEndTime' && (!e.value || e.value == '')) {
        e.isValid = false;
        e.errorText = '不能为空！';
    }
}

