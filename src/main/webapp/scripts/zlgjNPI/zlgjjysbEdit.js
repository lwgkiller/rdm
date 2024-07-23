var step = "";
var taskStatus = "";

$(function () {

    if (zlgjjysbId) {
        $.ajax({
            url: jsUseCtxPath + "/zlgjNPI/core/zlgjjysb/getZlgjjysbDetail.do?zlgjjysbId=" + zlgjjysbId,
            method: 'get',
            async: false,
            success: function (json) {
                zlgjjysbForm.setData(json);
                taskStatus = json.taskStatus;
            }
        });
    }
    fileListGrid.load();
    dcryGrid.load()
    //明细入口
    if (action == 'detail') {
        $("#detailToolBar").show();
        //非草稿放开流程信息查看按钮
        if (taskStatus != 'DRAFTED') {
            $("#processInfo").show();
        }
        zlgjjysbForm.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        mini.get("addDcry").setEnabled(false);
        mini.get("delDcry").setEnabled(false);
    } else if (action == 'task') {
        taskActionProcess();
    } else if (action == 'edit') {
        mini.get("addFile").setEnabled(true);
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
        var phoneNumber = mini.get("phoneNumber").getValue();
        if (!phoneNumber) {
            mini.get("phoneNumber").setValue(mobile);
        }
        mini.get("zlshshyj").setEnabled(false);
        mini.get("addDcry").setEnabled(false);
        mini.get("delDcry").setEnabled(false);
        mini.get("rating").setEnabled(false);
        mini.get("sectionChiefId").setEnabled(false);
        // mini.get("checkId").setEnabled(false);
        mini.get("ssqk").setEnabled(false);
        mini.get("sswcTime").setEnabled(false);
        mini.get("gjssqryj").setEnabled(false);
    }
    mini.get("CREATE_BY_").setEnabled(false);
    mini.get("applicationUnitId").setEnabled(false);
    mini.get("declareTime").setEnabled(false);
});

function toggleZlgjjysbFieldset(ck, id) {
    var dom = document.getElementById(id);
    if (ck.checked) {
        dom.className = "";
    } else {
        dom.className = "hideFieldset";
    }
}

//保存草稿
function saveZlgjjysb(e) {
    window.parent.saveDraft(e);
}

//启动流程
function startZlgjjysbProcess(e) {
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
    mini.get("ssqk").setEnabled(false);
    mini.get("sswcTime").setEnabled(false);
    mini.get("gjssqryj").setEnabled(false);
    if (step == 'bianZhi' || step == 'zlysh') {
        mini.get("sectionChiefId").setEnabled(false);
        mini.get("addDcry").setEnabled(false);
        mini.get("delDcry").setEnabled(false);
        mini.get("rating").setEnabled(false);
        mini.get("zlshshyj").setEnabled(false);
        if (step == 'zlysh') {
            mini.get("sectionChiefId").setEnabled(true);
            mini.get("checkId").setEnabled(true);
            mini.get("delDcry").setEnabled(true);
        }
    } else {
        mini.get("addDcry").setEnabled(false);
        mini.get("delDcry").setEnabled(false);
        zlgjjysbForm.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        if (step == 'djpd') {
            mini.get("rating").setEnabled(true);
            mini.get("zlshshyj").setEnabled(true);
        } else if (step == 'szrfpdcry') {
            mini.get("addDcry").setEnabled(true);
            mini.get("delDcry").setEnabled(true);
            dcryGrid.setAllowCellEdit(true);
        } else if (step == 'dcrysh') {
            dcryGrid.setAllowCellEdit(true);
        }
        else if (step == 'tbrqr') {
            mini.get("ssqk").setEnabled(true);
            mini.get("sswcTime").setEnabled(true);
            mini.get("gjssqryj").setEnabled(true);
        }
    }

}

//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("zlgjjysbForm");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos = [];
    formData.vars = [{key: 'projectName', val: formData.projectName}];
    return formData;
}


//检验表单是否必填(编制)
function validBianZhi() {
    var checkResult = {};
    var projectName = $.trim(mini.get("projectName").getValue());
    if (!projectName) {
        return {"result": false, "message": '请输入项目名称'};
    }
    var phoneNumber = $.trim(mini.get("phoneNumber").getValue());
    if (!phoneNumber) {
        return {"result": false, "message": '请输入电话号码'};
    }
    if (step == 'zlysh') {
        var sectionChiefId = $.trim(mini.get("sectionChiefId").getValue());
        if (!sectionChiefId) {
            return {"result": false, "message": '请选择设计/工艺室主任'};
        }
        //保留以下代码，根据逻辑判断流程节点检验不正确，移至编制节点

        // var checkId = $.trim(mini.get("checkId").getValue());
        // if (!checkId) {
        //     return {"result": false, "message": '请选择质量审核人员'};
        // }else {
        //     var flag = judgeZlzb(checkId);
        //     if (flag == 'false'){
        //         return {"result": false, "message": '质量审核人员应从质量保证部选择'};
        //     }
        // }
    }

    var checkId = $.trim(mini.get("checkId").getValue());
    if (!checkId) {
        return {"result": false, "message": '请选择质量审核人员'};
    }else {
        var flag = judgeZlzb(checkId);
        if (!flag){
            return {"result": false, "message": '质量审核人员应从质量保证部选择'};
        }
    }

    var wtdl = mini.get("wtdl").getValue();
    if (!wtdl) {
        return {"result": false, "message": '请选择问题大类'};
    }
    var wtxl = mini.get("wtxl").getValue();
    if (!wtxl) {
        return {"result": false, "message": '请选择问题小类'};
    }
    var jxlb = mini.get("jxlb").getValue();
    if (!jxlb) {
        return {"result": false, "message": '请选择机型类别'};
    }
    var involvingModel = $.trim(mini.get("involvingModel").getValue());
    if (!involvingModel) {
        return {"result": false, "message": '请输入涉及机型'};
    }
    var problemDescription = $.trim(mini.get("problemDescription").getValue());
    if (!problemDescription) {
        return {"result": false, "message": '请输入问题简述'};
    }
    var reasonAnalysis = $.trim(mini.get("reasonAnalysis").getValue());
    if (!reasonAnalysis) {
        return {"result": false, "message": '请输入原因分析'};
    }
    var dcjyfa = $.trim(mini.get("dcjyfa").getValue());
    if (!dcjyfa) {
        return {"result": false, "message": '请输入对策建议方案'};
    }
    var suggestDepartmentId = mini.get("suggestDepartmentId").getValue();
    if (!suggestDepartmentId) {
        return {"result": false, "message": '请选择建议对策部门'};
    }
    checkResult.result = true;
    return checkResult;
}

//流程中暂存信息（如编制阶段）
function saveZlgjjysbInProcess() {
    var bianZhiValid = validBianZhi();
    if (!bianZhiValid.result) {
        mini.alert(bianZhiValid.message);
        return;
    }
    var formData = _GetFormJsonMini("zlgjjysbForm");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    $.ajax({
        url: jsUseCtxPath + '/zlgjNPI/core/zlgjjysb/saveZlgjjysb.do',
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
function approveZlgjjysb() {
    //编制阶段的下一步需要校验表单必填字段
    if (step == 'szrfpdcry') {
        var gridData = dcryGrid.data
        if (gridData.length == 0) {
            mini.alert("请添加对策人员");
            return;
        }
        var arr = gridData.filter(function (value, index, arrary) {
            return value.CREATE_BY_ == currentUserId;
        });
        if (arr.length == 0) {
            mini.alert("请添加对策人员");
            return;
        }
        dcryGrid.validate();
        if (!dcryGrid.isValid()) {
            var error = dcryGrid.getCellErrors()[0];
            mini.alert("请选择对策人员");
            return;
        }

        mini.get("step").setValue("szrfpdcry");
    } else if (step == 'dcrysh') {
        dcryGrid.validate();
        var data = dcryGrid.getData();
        if (!dcryGrid.isValid()) {
            var error = dcryGrid.getCellErrors()[0];
            mini.alert(error.errorText);
            return;
        }
        if (data.length > 0) {
            for (var i = 0, l = data.length; i < l; i++) {
                if (data[i].dcryId == currentUserId && data[i].implementationStatus == 'yss' && !data[i].auditSuggest) {
                    mini.alert("请填写意见（更改通知单号）");
                    return;
                }
                // if (data[i].dcryId == currentUserId  && !data[i].ssContent) {
                //     mini.alert("请填写实施内容描述");
                //     return;
                // }
            }
        }
        mini.get("step").setValue("dcrysh");
    } else if (step == "djpd") {
        var rating = mini.get("rating").getValue();
        if (!rating) {
            mini.alert("请选择等级评定");
            return;
        }
    } else if (step == "tbrqr") {
        var ssqk = mini.get("ssqk").getValue();
        if (!ssqk) {
            mini.alert("请选择实施情况确认");
            return;
        }
        var sswcTime = mini.get("sswcTime").getValue();
        if (ssqk == '已实施' && !sswcTime) {
            mini.alert("请选择已实施完成时间");
            return;
        }

    } else {
        var bianZhiValid = validBianZhi();
        if (!bianZhiValid.result) {
            mini.alert(bianZhiValid.message);
            return;
        }
    }
    //检查通过
    window.parent.approve();
}

function zlgjjysbProcessInfo() {
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


function operationRenderer(e) {
    var record = e.record;
    var cellHtml = '';
    cellHtml = returnZlgjjysbPreviewSpan(record.fileName, record.id, record.zlgjjysbId, coverContent);
    //编辑、产品主管填写、编制可以删除
    if (action == 'edit' || (action == 'task' && (step == 'bianZhi' || step == 'zlysh'))) {
        var deleteUrl = "/zlgjNPI/core/zlgjjysb/delZlgjjysbFileById.do";
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.zlgjjysbId + '\',\'' + deleteUrl + '\')">删除</span>';
    }
    return cellHtml;
}

function returnZlgjjysbPreviewSpan(fileName, fileId, formId, coverContent) {
    var fileType = getFileType(fileName);
    var downloadUrl = "/zlgjNPI/core/zlgjjysb/zlgjjysbDownload.do";
    var s = '';
    if (fileType == 'other') {
        s = '<span  title="预览" style="color: silver" >预览</span>';
    } else if (fileType == 'pdf') {
        var url = '/zlgjNPI/core/zlgjjysb/zlgjjysbDownload.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + downloadUrl + '\')">预览</span>';
    } else if (fileType == 'office') {
        var url = '/zlgjNPI/core/zlgjjysb/zlgjjysbOfficePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
    } else if (fileType == 'pic') {
        var url = '/zlgjNPI/core/zlgjjysb/zlgjjysbImagePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
    }
    s += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
        'onclick="downLoadFile(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + downloadUrl + '\')">下载</span>';
    return s;
}

function addZlgjjysbFile() {
    var id = mini.get("id").getValue();
    if (!id) {
        mini.alert('请先点击‘保存草稿’进行表单的保存！');
        return;
    }
    mini.open({
        title: "文件上传",
        url: jsUseCtxPath + "/zlgjNPI/core/zlgjjysb/openZlgjjysbUploadWindow.do?zlgjjysbId=" + id,
        width: 800,
        height: 350,
        showModal: false,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            fileListGrid.load();
        }
    });
}

function dcryGridCellBeginEdit(e) {
    var record = e.record, field = e.field;
    if (step == 'szrfpdcry') {
        if (record.CREATE_BY_ == currentUserId && field == 'dcryId') {
            e.cancel = false;
        } else {
            e.cancel = true;
        }
    } else {
        if (record.dcryId == currentUserId && (field == 'implementationStatus' || field == 'auditSuggest'
        ||field == 'ssContent')) {
            e.cancel = false;
        } else {
            e.cancel = true;
        }
    }
}

function dcryGridCellValidation(e) {
    var record = e.record, field = e.field;
    if (step == 'szrfpdcry') {
        if (record.CREATE_BY_ == currentUserId && field == 'dcryId') {
            if (!e.value) {
                e.isValid = false;
                e.errorText = "请选择对策人员";
            }
        }
    } else {
        if (record.dcryId == currentUserId && field == 'implementationStatus') {
            if (!e.value) {
                e.isValid = false;
                e.errorText = "请选择实施状态";
            }
        }
        // if (record.dcryId == currentUserId && field == 'ssContent') {
        //     if (!e.value) {
        //         e.isValid = false;
        //         e.errorText = "请填写实施内容描述";
        //     }
        // }
    }

}

function addDcry() {
    var row = {
        CREATE_BY_: currentUserId,
        creator: currentUserName,
        dcryId: '',
        dcryName: '',
        auditSuggest: '',
        implementationStatus: '',
        zlgjjysbId: zlgjjysbId
    };
    dcryGrid.addRow(row);
}

function deleteDcry() {
    var row = dcryGrid.getSelected();
    if (!row) {
        mini.alert("请选择一条记录");
        return;
    }
    if (step != 'zlysh') {
        if (row.CREATE_BY_ != currentUserId) {
            mini.alert("仅自己创建的可删除");
            return;
        }
    }
    if (row.id) {
        $.ajax({
            url: jsUseCtxPath + '/zlgjNPI/core/zlgjjysb/deleteDcryById.do?id=' + row.id,
            async: false,
            success: function (result) {
            }
        })
    }
    delRowGrid("dcryGrid");
}

function zlgjjysbAppeal() {
    var projectName = mini.get("projectName").getValue();
    var declareTime = mini.get("declareTime").getText();
    var creator = mini.get("CREATE_BY_").getText();
    $.ajax({
        url: jsUseCtxPath + '/zlgjNPI/core/zlgjjysb/zlgjjysbAppeal.do?projectName=' + projectName + '&declareTime=' + declareTime + '&creator=' + creator,
        async: false,
        success: function (result) {
            if (result) {
                mini.alert(result.message)
            }
        }
    })
}

function judgeZlzb(userId){
    let fl = ""
    $.ajax({
        url: jsUseCtxPath + '/zlgjNPI/core/zlgjjysb/judgeZlbzb.do?userId=' + userId,
        async: false,
        success: function (flag) {
            fl = flag;
        }
    })
    return fl;
}



