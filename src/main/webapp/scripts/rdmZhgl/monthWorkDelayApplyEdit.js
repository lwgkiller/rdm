var editForm='';
var isFirstNode = false;
$(function () {
    if(action=='task') {
        bpmPreTaskTipInForm();
        getProcessNodeVars();
        if(editForm!='1' &&!isFirstNode) {
            planApplyForm.setEnabled(false);
        }
        planApplyForm.setData(ApplyObj);
    }else if(action =='detail'){
        $("#detailToolBar").show();
        planApplyForm.setEnabled(false);
        planApplyForm.setData(ApplyObj);
    }else if(action =='edit'){
        planApplyForm.setData(ApplyObj);
    }
    if(ApplyObj.planIds){
        $('#projectPlanDiv').show();
        $('#unProjectPlanDiv').hide();
        $('#unPlanTaskDiv').hide();
        loadPlanGrid();
    }else if(ApplyObj.unPlanIds){
        $('#projectPlanDiv').hide();
        $('#unProjectPlanDiv').show();
        $('#unPlanTaskDiv').hide();
        loadUnPlanGrid();
    }else if(ApplyObj.unPlanTaskIds){
        $('#projectPlanDiv').hide();
        $('#unProjectPlanDiv').hide();
        $('#unPlanTaskDiv').show();
        loadUnPlanTaskGrid();
    }
});
function searchList() {
    loadPlanGrid();
    loadUnPlanGrid();
}
function loadPlanGrid() {
    if(ApplyObj.planIds){
        var paramArray = [{name: "planIds", value: ApplyObj.planIds}];
        var data = {};
        data.filter = mini.encode(paramArray);
        planListGrid.load(data);
    }
}
function loadUnPlanGrid() {
    if(ApplyObj.unPlanIds){
        var paramArray = [{name: "unPlanIds", value: ApplyObj.unPlanIds}];
        var data = {};
        data.filter = mini.encode(paramArray);
        unPlanListGrid.load(data);
    }
}
function loadUnPlanTaskGrid() {
    if(ApplyObj.unPlanTaskIds){
        var paramArray = [{name: "unPlanTaskIds", value: ApplyObj.unPlanTaskIds}];
        var data = {};
        data.filter = mini.encode(paramArray);
        unPlanTaskListGrid.load(data);
    }
}

//流程实例图
function processInfo() {
    let instId = $("#instId").val();
    let url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: monthWorkDelayApplyEdit_name,
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
        if(nodeVars[i].KEY_ == 'isFirstNode'){
            isFirstNode = true;
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
    formData.bos=[];
    var yearMonth = mini.get('yearMonth').getText();
    formData.vars=[{key:'deptName',val:formData.deptName},{key:'yearMonth',val:yearMonth}];
    formData.yearMonth = yearMonth;
    return formData;
}

//保存草稿或提交时数据是否有效
function draftOrStartValid() {
    //根据部门id和月份判断是否已经提交过审批流程
    var reason = mini.get("reason").getValue();
    if(reason==''){
        return {"result": false,"message":monthWorkDelayApplyEdit_name1};
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
