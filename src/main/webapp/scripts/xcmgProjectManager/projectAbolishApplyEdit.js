var editForm='';
var isFirstNode = false;
$(function () {
    if(action=='task') {
        bpmPreTaskTipInForm();
        getProcessNodeVars();
        if(editForm!='1' &&!isFirstNode) {
            projectAbolishApply.setEnabled(false);
        }
    }else if(action =='detail'){
        $("#detailToolBar").show();
        projectAbolishApply.setEnabled(false);
        $('.removeFile').hide();
        $('.mini-panel-toolbar').hide()
    }
});
//流程实例图
function processInfo() {
    let instId = $("#instId").val();
    let url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: projectAbolishApplyEdit_name,
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
        if(nodeVars[i].KEY_ == 'uploadShow'){
            if(nodeVars[i].DEF_VAL_ == 'false'){
                $('.removeFile').hide();
                $('.mini-panel-toolbar').hide()
            }else{
                $('.removeFile').show();
                $('.mini-panel-toolbar').show()
            }
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
    var formData = _GetFormJsonMini("projectAbolishApply");
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos=[];
    formData.vars=[{key:'projectName',val:formData.projectName}];
    return formData;
}

//保存草稿或提交时数据是否有效
function draftOrStartValid() {
    let applyReason=$.trim(mini.get("reason").getValue());
    if(!applyReason) {
        return {"result": false, "message": projectAbolishApplyEdit_name1};
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