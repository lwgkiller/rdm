var isBianzhi = "";
var theName = "";
var theDepName = "";
//..
$(function () {
    if (ccbgId) {
        var url = jsUseCtxPath + "/zhgl/core/ccbg/getCcbgDetail.do";
        $.post(
            url,
            {ccbgId: ccbgId},
            function (json) {
                formCcbg.setData(json);
                //暂存原始编辑者，编辑部门名称
                theName = json.FULLNAME_;
                theDepName = json.editorUserDeptName;
                //@lwgkiller：此处只是记录编制者和其部门，一般和创建者一样，但也允许非创建者编辑
                mini.get("editorUserId").setValue(currentUserId);
                mini.get("editorUserId").setText(currentUserName);
                mini.get("editorUserDeptId").setValue(currentUserMainGroupId);
                mini.get("editorUserDeptId").setText(currentUserMainGroupName);
                if (action == 'detail') {
                    formCcbg.setEnabled(false);
                    mini.get("addFile").setEnabled(false);
                    $("#detailToolBar").show();
                    //非草稿放开流程信息查看按钮
                    if (status != 'DRAFTED') {
                        $("#processInfo").show();
                    }
                    //只是浏览，强行将原始编辑者，编辑部门名称复位，id并没有变，因为不用提交
                    mini.get("editorUserId").setText(theName);
                    mini.get("editorUserDeptId").setText(theDepName);
                } else if (action == 'task') {
                    taskActionProcess();
                }
            });
    } else {
        //@lwgkiller：此处只是记录编制者和其部门，一般和创建者一样，但也允许非创建者编辑
        mini.get("editorUserId").setValue(currentUserId);
        mini.get("editorUserId").setText(currentUserName);
        mini.get("editorUserDeptId").setValue(currentUserMainGroupId);
        mini.get("editorUserDeptId").setText(currentUserMainGroupName);
    }
});

//..流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("formCcbg");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    formData.bos = [];
    return formData;
}

//..保存草稿
function saveCcbg(e) {
    window.parent.saveDraft(e);
}

//..检验表单是否必填
function validCcbg() {
    var level1 = $.trim(mini.get("level1").getValue())
    if (!level1) {
        return {"result": false, "message": ccbgEdit_name1};
    }
    var level2 = $.trim(mini.get("level2").getValue())
    if (!level2) {
        return {"result": false, "message": ccbgEdit_name2};
    }
    var remark = $.trim(mini.get("remark").getValue())
    if (!remark) {
        return {"result": false, "message": ccbgEdit_name3};
    }
    var memberUserIds = $.trim(mini.get("memberUserIds").getValue())
    if (!memberUserIds) {
        return {"result": false, "message": ccbgEdit_name4};
    }
    var beginTime = $.trim(mini.get("beginTime").getValue())
    if (!beginTime) {
        return {"result": false, "message": ccbgEdit_name5};
    }
    var endTime = $.trim(mini.get("endTime").getValue())
    if (!endTime) {
        return {"result": false, "message": ccbgEdit_name6};
    }
    var trip = $.trim(mini.get("trip").getValue())
    if (!trip) {
        return {"result": false, "message": ccbgEdit_name7};
    }
    var primaryCoverage = $.trim(mini.get("primaryCoverage").getValue())
    if (!primaryCoverage) {
        return {"result": false, "message": ccbgEdit_name8};
    }
    var summaryAndProposal = $.trim(mini.get("summaryAndProposal").getValue())
    if (!summaryAndProposal) {
        return {"result": false, "message": ccbgEdit_name9};
    }
    if (fileListGrid.totalCount == 0) {
        return {"result": false, "message": ccbgEdit_name10};
    }
    return {"result": true};
}

//..启动流程
function startCcbgProcess(e) {
    var formValid = validCcbg();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.startProcess(e);
}

//..流程中暂存信息（如编制阶段）
function saveCcbgInProcess() {
    var formValid = validCcbg();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    var formData = _GetFormJsonMini("formCcbg");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    $.ajax({
        url: jsUseCtxPath + '/zhgl/core/ccbg/saveCcbg.do',
        type: 'post',
        async: false,
        data: mini.encode(formData),
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                var message = "";
                if (data.success) {
                    message = ccbgEdit_name11;
                } else {
                    message = ccbgEdit_name12 + data.message;
                }

                mini.alert(message, ccbgEdit_name13, function () {
                    window.location.reload();
                });
            }
        }
    });
}

//..流程中的审批或者下一步
function ccbgApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (isBianzhi == 'yes') {
        var formValid = validCcbg();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }

    //检查通过
    window.parent.approve();
}
//..
function ccbgProcessInfo() {
    var instId = $("#INST_ID_").val();
    var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: ccbgEdit_name14,
        width: 800,
        height: 600
    });
}
//..
function addCcbgFile() {
    var ccbgId = mini.get("id").getValue();
    if (!ccbgId) {
        mini.alert(ccbgEdit_name15);
        return;
    }
    mini.open({
        title: ccbgEdit_name16,
        url: jsUseCtxPath + "/zhgl/core/ccbg/fileUploadWindow.do?ccbgId=" + ccbgId,
        width: 750,
        height: 450,
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
function taskActionProcess() {
    //获取上一环节的结果和处理人
    bpmPreTaskTipInForm();

    //获取环境变量
    if (!nodeVarsStr) {
        nodeVarsStr = "[]";
    }
    var nodeVars = $.parseJSON(nodeVarsStr);
    for (var i = 0; i < nodeVars.length; i++) {
        if (nodeVars[i].KEY_ == 'isBianzhi') {
            isBianzhi = nodeVars[i].DEF_VAL_;
        }
    }
    if (isBianzhi != 'yes') {
        formCcbg.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        //只是非编制的办理，强行将原始编辑者，编辑部门名称复位，id并没有变，因为不用提交
        mini.get("editorUserId").setText(theName);
        mini.get("editorUserDeptId").setText(theDepName);
    }
}

//..
function returnCcbgPreviewSpan(fileName, fileId, formId, coverContent) {
    var fileType = getFileType(fileName);
    var s = '';
    if (fileType == 'other') {
        s = '<span  title=' + ccbgEdit_name17 + ' style="color: silver" >' + ccbgEdit_name17 + '</span>';
    } else if (fileType == 'pdf') {
        var url = '/zhgl/core/ccbg/ccbgPdfPreview.do';
        s = '<span  title=' + ccbgEdit_name17 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + ccbgEdit_name17 + '</span>';
    } else if (fileType == 'office') {
        var url = '/zhgl/core/ccbg/ccbgOfficePreview.do';
        s = '<span  title=' + ccbgEdit_name17 + ' style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + ccbgEdit_name17 + '</span>';
    } else if (fileType == 'pic') {
        var url = '/zhgl/core/ccbg/ccbgImagePreview.do';
        s = '<span  title=' + ccbgEdit_name17 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + ccbgEdit_name17 + '</span>';
    }
    return s;
}
//..
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