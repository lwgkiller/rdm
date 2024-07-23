var step = "";
var taskStatus = "";

$(function () {
    if (manageImproveId) {
        $.ajax({
            url: jsUseCtxPath + "/zlgjNPI/core/manageImprove/getManageImproveDetail.do?manageImproveId=" + manageImproveId,
            method: 'get',
            async: false,
            success: function (json) {
                manageImproveForm.setData(json);
                taskStatus = json.taskStatus;
                if(json.coreTypeId) {
                    coreTypeValueChanged();
                }
            }
        });
    }
    wtFileListGrid.load();
    gjFileListGrid.load();
    //明细入口
    if (action == 'detail') {
        $("#detailToolBar").show();
        //非草稿放开流程信息查看按钮
        if (taskStatus != 'DRAFTED') {
            $("#processInfo").show();
        }
        mini.get("wtAddFile").setEnabled(false);
        mini.get("gjAddFile").setEnabled(false);
    } else if (action == 'task') {
        taskActionProcess();
    } else if (action == 'edit') {
        mini.get("gjAddFile").setEnabled(false);
        mini.get("wtAddFile").setEnabled(true);
        var createBy = mini.get("CREATE_BY_").getValue();
        if (!createBy) {
            mini.get("CREATE_BY_").setValue(currentUserId);
            mini.get("CREATE_BY_").setText(currentUserName);
        }
        var applicationUnitId = mini.get("applicationUnitId").getValue();
        if (!applicationUnitId) {
            mini.get("applicationUnitId").setValue(mainGroupId);
            mini.get("applicationUnitId").setText(mainGroupName);
        }

        mini.get("canOrNot").setEnabled(false);
        mini.get("noReason").setEnabled(false);
        mini.get("responseManId").setEnabled(false);
        mini.get("improveMethod").setEnabled(false);
        mini.get("doneFlag").setEnabled(false);
        mini.get("planFinishTime").setEnabled(false);
    }
    mini.get("CREATE_BY_").setEnabled(false);
    mini.get("applicationUnitId").setEnabled(false);

});
function toggleManageImproveFieldset(ck, id) {
    var dom = document.getElementById(id);
    if (ck.checked) {
        dom.className = "";
    } else {
        dom.className = "hideFieldset";
    }
}

//保存草稿
function saveManageImprove(e) {
    window.parent.saveDraft(e);
}

//流程引擎调用此方法进行表单数据的获取
// function getData() {
//     var formData = _GetFormJsonMini("manageImproveForm");
//     if (formData.SUB_fileListGrid) {
//         delete formData.SUB_fileListGrid;
//     }
//     //此处用于向后台产生流程实例时替换标题中的参数时使用
//     formData.bos = [];
//     formData.vars = [{key: 'projectName', val: formData.projectName}];
//     return formData;
// }

//检验表单是否必填(编制)
function validBianZhi() {

    var checkResult = {};
    var coreTypeId = $.trim(mini.get("coreTypeId").getValue());
    if (!coreTypeId) {
        return {"result": false, "message": '请选择核心流程'};
    }
    var subTypeId = $.trim(mini.get("subTypeId").getValue());
    if (!subTypeId) {
        return {"result": false, "message": '请选择子流程'};
    }

    var businessRequest = mini.get("businessRequest").getValue();
    if (!businessRequest) {
        return {"result": false, "message": '请选择业务需求'};
    }
    var problemType = mini.get("problemType").getValue();
    if (!problemType) {
        return {"result": false, "message": '请选择问题类型'};
    }
    var problemDescription = mini.get("problemDescription").getValue();
    if (!problemDescription) {
        return {"result": false, "message": '请输入问题描述'};
    }
    var improveDepartmentId = mini.get("improveDepartmentId").getValue();
    if (!improveDepartmentId) {
        return {"result": false, "message": '请选择建议实施部门'};
    }
    if (wtFileListGrid.totalCount == 0) {
        return {"result": false, "message": '请上传问题提报文件'};
    }
    if (step == 'dcbmfzrsh1') {
        var canOrNot = $.trim(mini.get("canOrNot").getValue());
        if (canOrNot== '采纳') {
            var responseManId = $.trim(mini.get("responseManId").getValue());
            if (!responseManId) {
                return {"result": false, "message": '请选择负责人'};
            }
        }else{
            var noReason = $.trim(mini.get("noReason").getValue());
            if (!noReason) {
                return {"result": false, "message": '请输入不采纳原因'};
            }
        }

    }
    if (step == 'fzrgj') {
        var improveMethod = $.trim(mini.get("improveMethod").getValue());
        if (!improveMethod) {
            return {"result": false, "message": '请输入改进对策'};
        }
        var planFinishTime = $.trim(mini.get("planFinishTime").getValue());
        if (!planFinishTime) {
            return {"result": false, "message": '请选择计划完成时间'};
        }
        var doneFlag = $.trim(mini.get("doneFlag").getValue());
        if (!doneFlag) {
            return {"result": false, "message": '请输入完成标志'};
        }
    }
    checkResult.result = true;
    return checkResult;
}
//启动流程
function startManageImproveProcess(e) {
    var bianZhiValid = validBianZhi();
    if (!bianZhiValid.result) {
        mini.alert(bianZhiValid.message);
        return;
    }
    window.parent.startProcess(e);
}

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
    mini.get("CREATE_BY_").setEnabled(false);
    mini.get("applicationUnitId").setEnabled(false);

    if (step == 'bianZhi') {
        mini.get("canOrNot").setEnabled(false);
        mini.get("noReason").setEnabled(false);
        mini.get("responseManId").setEnabled(false);
        mini.get("improveMethod").setEnabled(false);
        mini.get("planFinishTime").setEnabled(false);
        mini.get("doneFlag").setEnabled(false);
        mini.get("gjAddFile").setEnabled(false);

    } else {
        mini.get("coreTypeId").setEnabled(false);
        mini.get("subTypeId").setEnabled(false);
        mini.get("businessRequest").setEnabled(false);
        mini.get("problemType").setEnabled(false);
        mini.get("problemDescription").setEnabled(false);
        mini.get("improveSuggestion").setEnabled(false);
        mini.get("improveDepartmentId").setEnabled(false);
        mini.get("wtAddFile").setEnabled(false);
        mini.get("canOrNot").setEnabled(false);
        mini.get("noReason").setEnabled(false);
        mini.get("responseManId").setEnabled(false);
        mini.get("improveMethod").setEnabled(false);
        mini.get("planFinishTime").setEnabled(false);
        mini.get("doneFlag").setEnabled(false);
        mini.get("gjAddFile").setEnabled(false);

        if (step == 'dcbmfzrsh1') {
            mini.get("canOrNot").setEnabled(true);
            mini.get("responseManId").setEnabled(true);
        } else if (step == 'fzrgj') {
            mini.get("improveMethod").setEnabled(true);
            mini.get("planFinishTime").setEnabled(true);
            mini.get("doneFlag").setEnabled(true);
            mini.get("gjAddFile").setEnabled(true);
        }
    }

}

//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("manageImproveForm");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos = [];
    formData.vars = [{key: 'projectName', val: formData.projectName}];
    return formData;
}

//流程中暂存信息（如编制阶段）
function saveManageImproveInProcess() {
    var bianZhiValid = validBianZhi();
    if (!bianZhiValid.result) {
        mini.alert(bianZhiValid.message);
        return;
    }
    var formData = _GetFormJsonMini("manageImproveForm");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    $.ajax({
        url: jsUseCtxPath + '/zlgjNPI/core/manageImprove/saveManageImproveForm.do',
        type: 'post',
        async: false,
        data: mini.encode(formData),
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                var message = "";
                if (data.success) {
                    message = "数据保存成功";
                } else {
                    message = "数据保存失败" + data.message;
                }
                mini.alert(message, "提示信息", function () {
                    window.location.reload();
                });
            }
        }
    });
}

//流程中的审批或者下一步
function approveManageImprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (step == 'bianZhi' || action=="edit"||step == 'dcbmfzrsh1'||step == 'fzrgj') {
        var formValid = validBianZhi();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    //检查通过
    window.parent.approve();
}

function manageImproveProcessInfo() {
    var instId = $("#instId").val();
    console.log(instId)
    var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: "流程图实例",
        width: 800,
        height: 600
    });
}



function returnManageImprovePreviewSpan(fileName, fileId, formId, coverContent) {
    var fileType = getFileType(fileName);
    var downloadUrl = "/zlgjNPI/core/manageImprove/manageImproveDownload.do";
    var s = '';
    if (fileType == 'other') {
        s = '<span  title="预览" style="color: silver" >预览</span>';
    } else if (fileType == 'pdf') {
        var url = '/zlgjNPI/core/manageImprove/manageImproveDownload.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + downloadUrl + '\')">预览</span>';
    } else if (fileType == 'office') {
        var url = '/zlgjNPI/core/manageImprove/manageImproveOfficePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
    } else if (fileType == 'pic') {
        var url = '/zlgjNPI/core/manageImprove/manageImproveImagePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
    }
    s += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
        'onclick="downLoadFile(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + downloadUrl + '\')">下载</span>';
    return s;
}

function coreTypeValueChanged() {

    var coreTypeId = mini.get("coreTypeId").getValue();
    $.ajax({
        url: jsUseCtxPath + '/zlgjNPI/core/manageImprove/querySubTypeByCoreType.do?coreTypeId=' + coreTypeId,
        type: 'post',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                mini.get("subTypeId").setData(data);
            }
        }
    });
}
function wtAddFile(){
    var id = mini.get("id").getValue();
    if (!id) {
        mini.alert('请先点击‘保存草稿’进行表单的保存！');
        return;
    }
    mini.open({
        title: "文件上传",
        url: jsUseCtxPath + "/zlgjNPI/core/manageImprove/openManageImproveUploadWindow.do?belongId=" + id+"&filetype=wenti",
        width: 800,
        height: 350,
        showModal: false,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            wtFileListGrid.load();
        }
    });
}
function gjAddFile(){
    var id = mini.get("id").getValue();
    if (!id) {
        mini.alert('请先点击‘保存草稿’进行表单的保存！');
        return;
    }
    mini.open({
        title: "文件上传",
        url: jsUseCtxPath + "/zlgjNPI/core/manageImprove/openManageImproveUploadWindow.do?belongId=" + id+"&filetype=gaijin",
        width: 800,
        height: 350,
        showModal: false,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            gjFileListGrid.load();
        }
    });
}
