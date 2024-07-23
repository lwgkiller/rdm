var editForm = '';
var isFirstNode = false;
var FGLD = false;
var manager = false;
$(function () {
    if (action == 'task') {
        bpmPreTaskTipInForm();
        getProcessNodeVars();
        if (editForm != '1' && !isFirstNode) {
            planApplyForm.setEnabled(false);
        }
        planApplyForm.setData(ApplyObj);
        searchList()
    } else if (action == 'detail') {
        $("#detailToolBar").show();
        planApplyForm.setEnabled(false);
        planApplyForm.setData(ApplyObj);
        searchList()
    } else if (action == 'edit') {
        planApplyForm.setData(ApplyObj);
        searchList()
    }
});

function searchList() {
    loadPlanGrid();
    loadUnPlanGrid();
    loadUnPlanTaskGrid();
}

function loadPlanGrid() {
    var yearMonth = mini.get('yearMonth').getText();
    var deptId = mini.get('deptId').getValue();
    var paramArray = [
        {name: "yearMonth", value: yearMonth},
        {name: "deptId", value: deptId},
        {name: "apply", value: "apply"},
        {name: "FGLD", value: FGLD}
        ];
    var data = {};
    data.filter = mini.encode(paramArray);
    planListGrid.load(data);
}

function loadUnPlanGrid() {
    var yearMonth = mini.get('yearMonth').getText();
    var deptId = mini.get('deptId').getValue();
    var paramArray = [{name: "yearMonth", value: yearMonth}, {name: "deptId", value: deptId}, {
        name: "apply",
        value: "apply"
    }, {name: "FGLD", value: FGLD}];
    var data = {};
    data.filter = mini.encode(paramArray);
    unPlanListGrid.load(data);
}

function loadUnPlanTaskGrid() {
    var yearMonth = mini.get('yearMonth').getText();
    var deptId = mini.get('deptId').getValue();
    var paramArray = [{name: "yearMonth", value: yearMonth}, {name: "deptId", value: deptId}, {
        name: "apply", value: "apply"
    }, {name: "FGLD", value: FGLD}];
    var data = {};
    data.filter = mini.encode(paramArray);
    unPlanTaskListGrid.load(data);
}

//流程实例图
function processInfo() {
    let instId = $("#instId").val();
    let url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: monthWorkApplyEdit_name1,
        width: 800,
        height: 600
    });
}


//获取环境变量
function getProcessNodeVars() {
    if (!nodeVarsStr) {
        nodeVarsStr = "[]";
    }
    var nodeVars = $.parseJSON(nodeVarsStr);
    for (var i = 0; i < nodeVars.length; i++) {
        if (nodeVars[i].KEY_ == 'editForm') {
            editForm = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isFirstNode') {
            isFirstNode = true;
        }
        if (nodeVars[i].KEY_ == 'FGLD') {
            FGLD = true;
        }
        if (nodeVars[i].KEY_ == 'manager') {
            manager = true;
        }
    }
}


//保存草稿
function saveApplyInfo(e) {
    var formValid = draftOrStartValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.saveDraft(e);
}

//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("planApplyForm");
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos = [];
    var yearMonth = mini.get('yearMonth').getText();
    formData.vars = [{key: 'deptName', val: formData.deptName}, {key: 'yearMonth', val: yearMonth}];
    formData.yearMonth = yearMonth;
    return formData;
}

//保存草稿或提交时数据是否有效
function draftOrStartValid() {
    if (applyType == '1') {
        //判断是否都提交完成情况
        var unPlanList = unPlanListGrid.getData();
        var planList = planListGrid.getData();
        var unPlanTaskList = unPlanTaskListGrid.getData();
        var currentDeptId = mini.get('deptId').getValue();
        for (var i = 0; i < planList.length; i++) {
            if (planList[i].deptId == currentDeptId) {
                if (planList[i].finishStatus == '' || planList[i].finishStatus == undefined) {
                    return {"result": false, "message": "项目类计划有完成情况未填写！"};
                }
                if (planList[i].finishStatus == '1' && planList[i].isDelayApply != '1') {
                    return {
                        "result": false,
                        "message": "项目类计划：" + planList[i].projectName + "：项目未完成并未提交工作延期申请！"
                    };
                }
            }
        }
        for (var i = 0; i < unPlanList.length; i++) {
            if (unPlanList[i].deptId == currentDeptId) {
                if (unPlanList[i].finishStatus == '' || unPlanList[i].finishStatus == undefined) {
                    return {"result": false, "message": "非项目类计划有完成情况未填写！"};
                }
                if (unPlanList[i].finishStatus == '1' && unPlanList[i].isDelayApply != '1') {
                    return {
                        "result": false,
                        "message": "非项目类计划：" + unPlanList[i].taskName + "：项目未完成并未提交工作延期申请！"
                    };
                }
            }
        }
        for (var i = 0; i < unPlanTaskList.length; i++) {
            if (unPlanTaskList[i].deptId == currentDeptId) {
                if (unPlanTaskList[i].finishStatus == '' || unPlanTaskList[i].finishStatus == undefined) {
                    return {"result": false, "message": "项目外计划有完成情况未填写！"};
                }
                if (unPlanTaskList[i].finishStatus == '1' && unPlanTaskList[i].isDelayApply != '1') {
                    return {
                        "result": false,
                        "message": "计划外任务：" + unPlanTaskList[i].taskName + "：项目未完成并未提交工作延期申请！"
                    };
                }
            }
        }
    }
    if (applyType == '0') {
        //判断是否都提交完成情况
        var unPlanList = unPlanListGrid.getData();
        var planList = planListGrid.getData();
        var unPlanTaskList = unPlanTaskListGrid.getData();
        for (var i = 0; i < planList.length; i++) {
            if (planList[i].workContent == '未填写') {
                return {"result": false, "message": "有项目类工作内容未填写！负责人：" + planList[i].responseMan};
            }
        }
        for (var i = 0; i < unPlanList.length; i++) {
            if (unPlanList[i].workContent == '未填写') {
                return {"result": false, "message": "有非项目类工作内容未填写！负责人：" + unPlanList[i].responseMan};
            }
        }
        for (var i = 0; i < unPlanTaskList.length; i++) {
            if (unPlanTaskList[i].workContent == '未填写') {
                return {"result": false, "message": "有计划外工作内容未填写！负责人：" + unPlanTaskList[i].responseMan};
            }
        }
    }
    //根据部门id和月份判断是否已经提交过审批流程
    var id = mini.get("id").getValue();
    var deptId = mini.get("deptId").getValue();
    var yearMonth = mini.get("yearMonth").getText();
    if (id == '') {
        let postData = {"deptId": deptId, "yearMonth": yearMonth, "applyType": applyType};
        let url = jsUseCtxPath + '/rdmZhgl/core/monthWorkApply/isApply.do';
        let resultData = ajaxRequest(url, 'POST', false, postData);
        if (resultData && resultData.length > 0) {
            return {"result": false, "message": "本月计划已经提交，请不要重复提交！"};
        }
    }
    return {"result": true};
}

//启动流程
function startApplyProcess(e) {
    var formValid = draftOrStartValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.startProcess(e);
}

//审批或者下一步
function applyApprove() {
    var formValid = draftOrStartValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.approve();
}
