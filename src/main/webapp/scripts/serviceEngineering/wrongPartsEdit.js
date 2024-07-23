//..
$(function () {
    mini.get("#typeOfWrongPart").load(cjTypes);
    if (cjzgId) {
        var url = jsUseCtxPath + "/serviceEngineering/core/wrongParts/getCjzgDetail.do?cjzgId=" + cjzgId;
        $.ajax({
            url: url,
            method: 'get',
            async: false,
            success: function (json) {
                formCjzg.setData(json);
            }
        });
    }
    fileListGrid.load();
    //明细入口
    if (action == 'detail') {
        formCjzg.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        $("#detailToolBar").show();
        //非草稿放开流程信息查看按钮
        debugger;
        if (taskStatus && taskStatus != 'DRAFTED') {
            $("#processInfo").show();
        }
        if (isFwgcjswdzy == 'true' && !taskStatus) {
            mini.get("addFile").setEnabled(true);
        }
    } else if (action == 'task') {
        taskActionProcess();
    } else {//add或edit封住一些不用填写字段
        mini.get("typeOfWrongPart").setEnabled(false);
        mini.get("isHistory").setEnabled(false);
        mini.get("responsibleDepartmentId").setEnabled(false);
        mini.get("principalUserId").setEnabled(false);
        mini.get("XGSSRespUserId").setEnabled(false);
        mini.get("causeAnalysis").setEnabled(false);
        mini.get("involvedCar").setEnabled(false);
        mini.get("rectificationPlan").setEnabled(false);
        mini.get("promiseTime").setEnabled(false);
        mini.get("newRectificationMeasures").setEnabled(false);
        mini.get("riskWarning").setEnabled(false);
        mini.get("XGSSRectificationPlan").setEnabled(false);
    }
});
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
        if (nodeVars[i].KEY_ == 'codeName') {
            codeName = nodeVars[i].DEF_VAL_;
        }
    }
    if (codeName == 'A') {//整改报告编制:和action=='edit'一样
        mini.get("typeOfWrongPart").setEnabled(false);
        mini.get("isHistory").setEnabled(false);
        mini.get("responsibleDepartmentId").setEnabled(false);
        mini.get("principalUserId").setEnabled(false);
        mini.get("XGSSRespUserId").setEnabled(false);
        mini.get("causeAnalysis").setEnabled(false);
        mini.get("involvedCar").setEnabled(false);
        mini.get("rectificationPlan").setEnabled(false);
        mini.get("newRectificationMeasures").setEnabled(false);
        mini.get("riskWarning").setEnabled(false);
        mini.get("XGSSRectificationPlan").setEnabled(false);
    } else if (codeName == 'C') {//责任分配:责任部门|责任人
        formCjzg.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        mini.get("responsibleDepartmentId").setEnabled(true);
        mini.get("principalUserId").setEnabled(true);
    } else if (codeName == 'F') {//责任人预处理:原因分析|涉及车辆|历史车辆整改方案|预计完成时间|业务规则整改措施|后市场风险提示
        formCjzg.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        mini.get("causeAnalysis").setEnabled(true);
        mini.get("involvedCar").setEnabled(true);
        mini.get("rectificationPlan").setEnabled(true);
        mini.get("promiseTime").setEnabled(true);
        mini.get("newRectificationMeasures").setEnabled(true);
        mini.get("riskWarning").setEnabled(true);
    } else if (codeName == 'H') {//外部门分配责任人:责任人
        formCjzg.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        mini.get("principalUserId").setEnabled(true);
    } else if (codeName == 'I') {//非服务所责任人处理:原因分析|涉及车辆|历史车辆整改方案|业务规则整改措施|后市场风险提示
        formCjzg.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        mini.get("causeAnalysis").setEnabled(true);
        mini.get("involvedCar").setEnabled(true);
        mini.get("rectificationPlan").setEnabled(true);
        mini.get("newRectificationMeasures").setEnabled(true);
        mini.get("riskWarning").setEnabled(true);
    } else if (codeName == 'J') {//分配GSS责任人:XGSS整改人
        formCjzg.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        mini.get("XGSSRespUserId").setEnabled(true);
    } else if (codeName == 'K') {//GSS责任人处理:XGSS整改方案
        formCjzg.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        mini.get("XGSSRectificationPlan").setEnabled(true);
    } else if (codeName == 'L') {//整改确认:错件类型|是否遗留
        formCjzg.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        mini.get("typeOfWrongPart").setEnabled(true);
        mini.get("isHistory").setEnabled(true);
    } else {
        formCjzg.setEnabled(false);
        mini.get("addFile").setEnabled(false);
    }
}
//..
function getData() {
    var formData = _GetFormJsonMini("formCjzg");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos = [];
    formData.vars = [{key: 'wrongPartName', val: formData.wrongPartName}];
    return formData;
}
//..保存草稿
function saveCjzg(e) {
    window.parent.saveDraft(e);
}
//..整改报告编制
function validA() {
    var wrongPartName = $.trim(mini.get("wrongPartName").getValue());
    if (!wrongPartName) {
        return {"result": false, "message": wrongPartsEdit_name};
    }
    var salesModel = $.trim(mini.get("salesModel").getValue())
    if (!salesModel) {
        return {"result": false, "message": wrongPartsEdit_name1};
    }
    var designModel = $.trim(mini.get("designModel").getValue());
    if (!designModel) {
        return {"result": false, "message": "请输入设计型号"};
    }
    var machineCode = $.trim(mini.get("machineCode").getValue());
    if (!machineCode) {
        return {"result": false, "message": wrongPartsEdit_name2};
    }
    var agent = $.trim(mini.get("agent").getValue())
    if (!agent) {
        return {"result": false, "message": wrongPartsEdit_name3};
    }
    var problemDescription = $.trim(mini.get("problemDescription").getValue());
    if (!problemDescription) {
        return {"result": false, "message": wrongPartsEdit_name6};
    }
    if (fileListGrid.totalCount == 0) {
        return {"result": false, "message": wrongPartsEdit_name7};
    }
    return {"result": true};
}
//..责任分配:责任部门|责任人
function validC() {
    var responsibleDepartmentId = $.trim(mini.get("responsibleDepartmentId").getValue())
    if (!responsibleDepartmentId) {
        return {"result": false, "message": wrongPartsEdit_name5};
    }
    if (responsibleDepartmentId == fwgcsId) {//是服务所
        var principalUserId = $.trim(mini.get("principalUserId").getValue())
        if (!principalUserId) {
            return {"result": false, "message": "请选择责任人"};
        }
    }
    return {"result": true};
}
//..责任人预处理:原因分析|涉及车辆|历史车辆整改方案|预计完成时间|业务规则整改措施|后市场风险提示
function validF() {
    var causeAnalysis = $.trim(mini.get("causeAnalysis").getValue())
    if (!causeAnalysis) {
        return {"result": false, "message": wrongPartsEdit_name9};
    }
    var involvedCar = $.trim(mini.get("involvedCar").getValue())
    if (!involvedCar) {
        return {"result": false, "message": wrongPartsEdit_name10};
    }
    var rectificationPlan = $.trim(mini.get("rectificationPlan").getValue())
    if (!rectificationPlan) {
        return {"result": false, "message": wrongPartsEdit_name11};
    }
    var promiseTime = $.trim(mini.get("promiseTime").getValue())
    if (!promiseTime) {
        return {"result": false, "message": "请选择预计完成时间"};
    }
    var newRectificationMeasures = $.trim(mini.get("newRectificationMeasures").getValue())
    if (!newRectificationMeasures) {
        return {"result": false, "message": "请输入业务规则整改措施"};
    }
    var riskWarning = $.trim(mini.get("riskWarning").getValue())
    if (!riskWarning) {
        return {"result": false, "message": wrongPartsEdit_name13};
    }
    return {"result": true};
}
//..外部门分配责任人:责任人
function validH() {
    var principalUserId = $.trim(mini.get("principalUserId").getValue())
    if (!principalUserId) {
        return {"result": false, "message": "请选择责任人"};
    }
    return {"result": true};
}
//..责任人预处理:原因分析|涉及车辆|历史车辆整改方案|业务规则整改措施|后市场风险提示
function validI() {
    var causeAnalysis = $.trim(mini.get("causeAnalysis").getValue())
    if (!causeAnalysis) {
        return {"result": false, "message": wrongPartsEdit_name9};
    }
    var involvedCar = $.trim(mini.get("involvedCar").getValue())
    if (!involvedCar) {
        return {"result": false, "message": wrongPartsEdit_name10};
    }
    var rectificationPlan = $.trim(mini.get("rectificationPlan").getValue())
    if (!rectificationPlan) {
        return {"result": false, "message": wrongPartsEdit_name11};
    }
    var newRectificationMeasures = $.trim(mini.get("newRectificationMeasures").getValue())
    if (!newRectificationMeasures) {
        return {"result": false, "message": "请输入业务规则整改措施"};
    }
    var riskWarning = $.trim(mini.get("riskWarning").getValue())
    if (!riskWarning) {
        return {"result": false, "message": wrongPartsEdit_name13};
    }
    return {"result": true};
}
//..分配GSS责任人:XGSS整改人
function validJ() {
    var XGSSRespUserId = $.trim(mini.get("XGSSRespUserId").getValue())
    if (!XGSSRespUserId) {
        return {"result": false, "message": "请选择责XGSS整改人"};
    }
    return {"result": true};
}
//..GSS责任人处理:XGSS整改方案
function validK() {
    var XGSSRectificationPlan = $.trim(mini.get("XGSSRectificationPlan").getValue())
    if (!XGSSRectificationPlan) {
        return {"result": false, "message": wrongPartsEdit_name8};
    }
    return {"result": true};
}
//..整改确认:错件类型|是否遗留
function validL() {
    var typeOfWrongPart = $.trim(mini.get("typeOfWrongPart").getValue())
    if (!typeOfWrongPart) {
        return {"result": false, "message": wrongPartsEdit_name4};
    }
    var isHistory = $.trim(mini.get("isHistory").getValue())
    if (!isHistory) {
        return {"result": false, "message": "请选择是否历史遗留问题"};
    }
    return {"result": true};
}
//..提交
function startCjzgProcess(e) {
    var isValid = validA();
    if (!isValid.result) {
        mini.alert(isValid.message);
        return;
    }
    window.parent.startProcess(e);
}
//..
function saveCjzgInProcess() {
    var formData = _GetFormJsonMini("formCjzg");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    $.ajax({
        url: jsUseCtxPath + '/serviceEngineering/core/wrongParts/saveCjzg.do',
        type: 'post',
        async: false,
        data: mini.encode(formData),
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                var message = "";
                if (data.success) {
                    message = wrongPartsEdit_name14;
                } else {
                    message = wrongPartsEdit_name15 + data.message;
                }

                mini.alert(message, wrongPartsEdit_name16, function () {
                    window.location.reload();
                });
            }
        }
    });
}
//..
function cjzgApprove() {
    if (codeName == 'A') {//整改报告编制:和action=='edit'一样
        var isValid = validA();
        if (!isValid.result) {
            mini.alert(isValid.message);
            return;
        }
    }
    if (codeName == 'L') {//整改确认:错件类型|是否遗留
        var isValid = validL()
        if (!isValid.result) {
            mini.alert(isValid.message);
            return;
        }
    }
    if (codeName == 'C') {//责任分配:责任部门|责任人
        var isValid = validC()
        if (!isValid.result) {
            mini.alert(isValid.message);
            return;
        }
    }
    if (codeName == 'F') {//责任人预处理:原因分析|涉及车辆|历史车辆整改方案|预计完成时间|业务规则整改措施|后市场风险提示
        var isValid = validF()
        if (!isValid.result) {
            mini.alert(isValid.message);
            return;
        }
    }
    if (codeName == 'H') {//外部门分配责任人:责任人
        var isValid = validH()
        if (!isValid.result) {
            mini.alert(isValid.message);
            return;
        }
    }
    if (codeName == 'I') {//非服务所责任人处理:原因分析|涉及车辆|历史车辆整改方案|业务规则整改措施|后市场风险提示
        var isValid = validI()
        if (!isValid.result) {
            mini.alert(isValid.message);
            return;
        }
    }
    if (codeName == 'J') {//分配GSS责任人:XGSS整改人
        var isValid = validJ()
        if (!isValid.result) {
            mini.alert(isValid.message);
            return;
        }
    }
    if (codeName == 'K') {//GSS责任人处理:XGSS整改方案
        var isValid = validK()
        if (!isValid.result) {
            mini.alert(isValid.message);
            return;
        }
    }
    //检查通过
    window.parent.approve();
}
//..
function cjzgProcessInfo() {
    var instId = $("#instId").val();
    var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: wrongPartsEdit_name18,
        width: 800,
        height: 600
    });
}
//..
function addCjzgFile() {
    var cjzgId = mini.get("id").getValue();
    if (!cjzgId) {
        mini.alert(wrongPartsEdit_name19);
        return;
    }
    mini.open({
        title: wrongPartsEdit_name20,
        url: jsUseCtxPath + "/serviceEngineering/core/wrongParts/openCjzgUploadWindow.do?cjzgId=" + cjzgId,
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
//..
function operationRenderer(e) {
    var record = e.record;
    var cellHtml = '';
    cellHtml = returnCjzgPreviewSpan(record.fileName, record.id, record.wrongPartsId, coverContent);
    //增加删除按钮
    if (action == 'edit' || (action == 'task' && codeName == 'A') || (action == 'detail' && !taskStatus) || currentUserNo == 'admin') {
        var deleteUrl = "/serviceEngineering/core/wrongParts/delCjzgUploadFile.do";
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + wrongPartsEdit_name21 + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.wrongPartsId + '\',\'' + deleteUrl + '\')">' + wrongPartsEdit_name21 + '</span>';
    }
    return cellHtml;
}
//..
function returnCjzgPreviewSpan(fileName, fileId, formId, coverContent) {
    var fileType = getFileType(fileName);
    var downloadUrl = "/serviceEngineering/core/wrongParts/cjzgFileDownload.do";
    var s = '';
    if (fileType == 'other') {
        s = '<span  title=' + wrongPartsEdit_name22 + ' style="color: silver" >' + wrongPartsEdit_name22 + '</span>';
    } else if (fileType == 'pdf') {
        var url = '/serviceEngineering/core/wrongParts/cjzgFileDownload.do';
        s = '<span  title=' + wrongPartsEdit_name22 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + downloadUrl + '\')">' + wrongPartsEdit_name22 + '</span>';
    } else if (fileType == 'office') {
        var url = '/serviceEngineering/core/wrongParts/cjzgOfficePreview.do';
        s = '<span  title=' + wrongPartsEdit_name22 + ' style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + wrongPartsEdit_name22 + '</span>';
    } else if (fileType == 'pic') {
        var url = '/serviceEngineering/core/wrongParts/cjzgImagePreview.do';
        s = '<span  title=' + wrongPartsEdit_name22 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + wrongPartsEdit_name22 + '</span>';
    }
    s += '&nbsp;&nbsp;&nbsp;<span title=' + wrongPartsEdit_name22 + ' style="color:#409EFF;cursor: pointer;" ' +
        'onclick="downLoadFile(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + downloadUrl + '\')">' + wrongPartsEdit_name22 + '</span>';
    return s;
}