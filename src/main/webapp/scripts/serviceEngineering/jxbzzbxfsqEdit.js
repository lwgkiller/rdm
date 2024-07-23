var step = "";
var taskStatus = "";

$(function () {
    if (jxbzzbxfsqId) {
        var url = jsUseCtxPath + "/serviceEngineering/core/jxbzzbxfsq/getJxbzzbxfsqDetail.do?jxbzzbxfsqId=" + jxbzzbxfsqId;
        $.ajax({
            url: url,
            method: 'get',
            async: false,
            success: function (json) {
                jxbzzbxfsqForm.setData(json);
                taskStatus = json.taskStatus;
                var fileListGridUrl = jsUseCtxPath + "/serviceEngineering/core/jxbzzbxfsq/queryJxbzzbxfsqFileList.do?jxbzzbshId=" + json.jxbzzbshId;
                fileListGrid.setUrl(fileListGridUrl);
                fileListGrid.load();
            }
        });
    }
    mini.get("applyDeptId").setEnabled(false);
    mini.get("CREATE_BY_").setEnabled(false);
    //明细入口
    if (action == 'detail') {

        $("#detailToolBar").show();
        //非草稿放开流程信息查看按钮
        if (taskStatus && taskStatus != 'DRAFTED') {
            $("#processInfo").show();
        }
        jxbzzbxfsqForm.setEnabled(false);
    } else if (action == 'task') {
        taskActionProcess();
    } else if (action == 'edit') {

        if (!mini.get("applyDeptId").getValue()) {
            mini.get("applyDeptId").setValue(mainGroupId);
            mini.get("applyDeptId").setText(mainGroupName);
        }
        if (!mini.get("CREATE_BY_").getValue()) {
            mini.get("CREATE_BY_").setValue(currentUserId);
            mini.get("CREATE_BY_").setText(currentUserName);
        }
    }
});

function taskActionProcess() {
    //获取上一环节的结果和处理人
    bpmPreTaskTipInForm();

    //获取环境变量
    if (!nodeVarsStr) {
        nodeVarsStr = "[]";
    }
    var nodeVars = $.parseJSON(nodeVarsStr);
    for (var i = 0; i < nodeVars.length; i++) {
        if (nodeVars[i].KEY_ == 'step') {
            step = nodeVars[i].DEF_VAL_;
        }
    }
    if (step == 'bianZhi') {
        mini.get("jxbzzbshId").setEnabled(true)
    } else {
        jxbzzbxfsqForm.setEnabled(false);
    }
}

//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("jxbzzbxfsqForm");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos = [];
    formData.vars = [{key: 'materialCode', val: formData.materialCode}];
    return formData;
}

//保存草稿
function saveJxbzzbxfsq(e) {
    var jxbzzbshId = $.trim(mini.get("jxbzzbshId").getValue());
    if (!jxbzzbshId) {
        mini.alert(jxbzzbxfsqEdit_name);
        return;
    }
    //@mh 增加50个物料号限制
    if (jxbzzbshId.split(",").length > 50) {
        mini.alert(jxbzzbxfsqEdit_name1);
        return;
    }
    window.parent.saveDraft(e);
}

//启动流程
function startJxbzzbxfsqProcess(e) {
    var bianZhiValid = validBianZhi();
    if (!bianZhiValid.result) {
        mini.alert(bianZhiValid.message);
        return;
    }
    mini.get("step").setValue("startProcess")
    window.parent.startProcess(e);
}

//检验表单是否必填(编制)
function validBianZhi() {
    var checkResult = {};
    var jxbzzbshId = mini.get("jxbzzbshId").getValue()
    if (!jxbzzbshId) {
        return {"result": false, "message": jxbzzbxfsqEdit_name2};
    }
    //@mh 增加50个物料号限制
    if (jxbzzbshId.split(",").length > 50) {
        return {"result": false, "message": jxbzzbxfsqEdit_name1};
    }
    checkResult.result = true;
    return checkResult;
}

//流程中暂存信息（如编制阶段）
function saveJxbzzbxfsqInProcess() {
    var bianZhiValid = validBianZhi();
    if (!bianZhiValid.result) {
        mini.alert(bianZhiValid.message);
        return;
    }
    var formData = _GetFormJsonMini("jxbzzbxfsqForm");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    $.ajax({
        url: jsUseCtxPath + '/serviceEngineering/core/jxbzzbxfsq/saveJxbzzbxfsq.do',
        type: 'post',
        async: false,
        data: mini.encode(formData),
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                var message = "";
                if (data.success) {
                    message = jxbzzbxfsqEdit_name3;
                } else {
                    message = jxbzzbxfsqEdit_name4 + data.message;
                }

                mini.alert(message, jxbzzbxfsqEdit_name5, function () {
                    window.location.reload();
                });
            }
        }
    });
}

//流程中的审批或者下一步
function jxbzzbxfsqApprove() {
    //编制阶段的下一步需要校验表单必填字段
    var valid = true;
    if (step == 'bianZhi') {
        valid = validBianZhi();
        if (!valid.result) {
            mini.alert(valid.message);
            return;
        }
    }
    //检查通过
    window.parent.approve();
}

function jxbzzbxfsqProcessInfo() {
    var instId = $("#instId").val();
    var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: jxbzzbxfsqEdit_name6,
        width: 800,
        height: 600
    });
}


function returnJxbzzbxfsqPreviewSpan(fileName, fileId, formId, coverContent, createBy) {
    var fileType = getFileType(fileName);
    var downloadUrl = "/serviceEngineering/core/jxbzzbxfsq/jxbzzbxfsqDownload.do";
    var s = '';
    if (fileType == 'other') {
        s = '<span  title= ' + jxbzzbxfsqEdit_name7 + ' style="color: silver" >' + jxbzzbxfsqEdit_name7 + '</span>';
    } else if (fileType == 'pdf') {
        var url = '/serviceEngineering/core/jxbzzbxfsq/jxbzzbxfsqDownload.do';
        s = '<span  title=' + jxbzzbxfsqEdit_name7 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + downloadUrl + '\')">' + jxbzzbxfsqEdit_name7 + '</span>';
    } else if (fileType == 'office') {
        s = '<span  title=' + jxbzzbxfsqEdit_name7 + ' style="color: silver" >' + jxbzzbxfsqEdit_name7 + '</span>';
    } else if (fileType == 'pic') {
        var url = '/serviceEngineering/core/jxbzzbxfsq/jxbzzbxfsqImagePreview.do';
        s = '<span  title=' + jxbzzbxfsqEdit_name7 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + jxbzzbxfsqEdit_name7 + '</span>';
    }
    var createBy = mini.get("CREATE_BY_").getValue();
    if (taskStatus == 'SUCCESS_END' && currentUserId == createBy) {
        s += '&nbsp;&nbsp;&nbsp;<span title=' + jxbzzbxfsqEdit_name8 + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + downloadUrl + '\')">' + jxbzzbxfsqEdit_name8 + '</span>';
    }
    if (taskStatus == 'DRAFTED' && createBy == currentUserId) {
        s += '&nbsp;&nbsp;&nbsp;<span title=' + jxbzzbxfsqEdit_name9 + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="deleteJxFile(\'' + formId + '\')">' + jxbzzbxfsqEdit_name9 + '</span>';
    }
    return s;
}

function deleteJxFile(id) {
    var selectRow = fileListGrid.getSelected();
    fileListGrid.removeRow(selectRow);
    var fileList = fileListGrid.getData();
    var value = '';
    var text = '';
    for (var i = 0; i < fileList.length; i++) {
        if ((i + 1) != fileList.length) {
            value += fileList[i].masterId + ',';
            text += fileList[i].materialCode + ',';
        } else {
            value += fileList[i].masterId;
            text += fileList[i].materialCode;
        }
    }
    mini.get("jxbzzbshId").setValue(value);
    mini.get("jxbzzbshId").setText(text);
    var fileListGridUrl = jsUseCtxPath + "/serviceEngineering/core/jxbzzbxfsq/queryJxbzzbxfsqFileList.do?jxbzzbshId=" + value;
    fileListGrid.setUrl(fileListGridUrl);
    fileListGrid.load();
}


function operationRenderer(e) {
    var record = e.record;
    var cellHtml = '';
    cellHtml = returnJxbzzbxfsqPreviewSpan(record.fileName, record.id, record.masterId, coverContent);
    return cellHtml;
}


function onButtonEdit(e) {
    var oldValue = mini.get("jxbzzbshId").getValue();
    var oldText = mini.get("jxbzzbshId").getText();
    var btnEdit = e.sender;
    mini.open({
        url: jsUseCtxPath + "/serviceEngineering/core/jxbzzbxfsq/openJxbzzbxfsqDialog.do",
        title: jxbzzbxfsqEdit_name10,
        width: 1100,
        height: 600,
        ondestroy: function (action) {
            if (action == "ok") {
                var iframe = this.getIFrameEl();
                var data = iframe.contentWindow.getJxbzzbxfsqMaterialCode();
                data = mini.clone(data);
                var value = '';
                var text = '';
                for (var i = 0; i < data.length; i++) {
                    if (oldValue.indexOf(data[i].id) < 0) {
                        value += data[i].id + ',';
                        text += data[i].materialCode + ',';
                    }
                }
                if (oldValue.length > 0) {
                    value = oldValue + ',' + value;
                    text = oldText + ',' + text;
                }
                value = value.substring(0, value.length - 1);
                text = text.substring(0, text.length - 1);
                btnEdit.setValue(value);
                btnEdit.setText(text);
                var fileListGridUrl = jsUseCtxPath + "/serviceEngineering/core/jxbzzbxfsq/queryJxbzzbxfsqFileList.do?jxbzzbshId=" + value;
                fileListGrid.setUrl(fileListGridUrl);
                fileListGrid.load();
            }
        }
    });
}
