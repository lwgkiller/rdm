var editForm = '';
var isFirstNode = false;
var techLeader = false;
$(function () {
    if (action == 'task') {
        bpmPreTaskTipInForm();
        getProcessNodeVars();
        if (editForm != '1' && !isFirstNode) {
            applyForm.setEnabled(false);
        }else {
        }
        if(techLeader){
            mini.get('techLeaderUserId').setEnabled(true);
        }else{
            mini.get('techLeaderUserId').setEnabled(false);
        }
        applyForm.setData(ApplyObj);
    } else if (action == 'detail') {
        $("#detailToolBar").show();
        if(status!='DRAFTED') {
            $("#processInfo").show();
        }
        applyForm.setEnabled(false);
        applyForm.setData(ApplyObj);
    } else if (action == 'edit') {
        applyForm.setData(ApplyObj);
        if(!techLeader){
            mini.get('techLeaderUserId').setEnabled(false);
        }
    }
    if(ApplyObj.mainId){
        loadApplyGrid();
    }
});
//流程实例图
function processInfo() {
    let instId = $("#instId").val();
    let url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: "流程图实例",
        width: 800,
        height: 600
    });
}

function loadApplyGrid() {
    if(ApplyObj.mainId){
        var paramArray = [{name: "mainId", value: ApplyObj.mainId}];
        var data = {};
        data.filter = mini.encode(paramArray);
        applyListGrid.load(data);
    }
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
        if (nodeVars[i].KEY_ == 'techLeader') {
            techLeader = true;
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
    var formData = _GetFormJsonMini("applyForm");
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos = [];
    formData.vars = [];
    return formData;
}

//保存草稿或提交时数据是否有效
function draftOrStartValid() {
    let applyReason=$.trim(mini.get("applyReason").getText());
    if(!applyReason) {
        return {"result": false, "message": "请填写申请原因"};
    }
    if(techLeader){
        let techLeaderUserId=$.trim(mini.get("techLeaderUserId").getValue());
        if(!techLeaderUserId) {
            return {"result": false, "message": "请选择技术所长"};
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
