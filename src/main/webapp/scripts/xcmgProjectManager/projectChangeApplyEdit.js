var editForm='';
var isFirstNode = false;
var selectLeader='';
$(function () {
    mini.get("isLeader").setEnabled(false);
    mini.get("isBigChange").setEnabled(false);
    if(action=='task') {
        bpmPreTaskTipInForm();
        getProcessNodeVars();
        if(editForm!='1'&&!isFirstNode) {
            projectChangeApply.setEnabled(false);
        }
        if(selectLeader=='yes') {
            mini.get("isLeader").setEnabled(true);
            mini.get("isBigChange").setEnabled(true);
        }
    }else if(action =='detail'){
        $("#detailToolBar").show();
        if(editForm!='1') {
            projectChangeApply.setEnabled(false);
        }
        $('.removeFile').hide();
        $('.mini-panel-toolbar').hide()
    }
});
function processInfo() {
    var instId = $("#instId").val();
    var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: projectChangeApplyEdit_name,
        width: 800,
        height: 600
    });
}

function getProcessNodeVars() {
    //获取环境变量
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
        if(nodeVars[i].KEY_ == 'selectLeader'){
            selectLeader = 'yes';
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
    var formData = _GetFormJsonMini("projectChangeApply");
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos=[];
    formData.vars=[{key:'projectName',val:formData.projectName}];
    return formData;
}

//保存草稿或提交时数据是否有效
function draftOrStartValid() {
    let currentCondition=$.trim(mini.get("currentCondition").getValue());
    if(!currentCondition) {
        return {"result": false, "message": projectChangeApplyEdit_name1};
    }
    let applyReason=$.trim(mini.get("reason").getValue());
    if(!applyReason) {
        return {"result": false, "message": projectChangeApplyEdit_name2};
    }
    let changeContent=$.trim(mini.get("changeContent").getValue());
    if(!changeContent) {
        return {"result": false, "message": projectChangeApplyEdit_name3};
    }
    let changeDesignDept=$.trim(mini.get("changeDesignDept").getValue());
    if(!changeDesignDept) {
        return {"result": false, "message": projectChangeApplyEdit_name4};
    }
    let adjustMeasure=$.trim(mini.get("adjustMeasure").getValue());
    if(!adjustMeasure) {
        return {"result": false, "message": projectChangeApplyEdit_name5};
    }


    return {"result": true};
}


function validLeader() {
    let isLeader=$.trim(mini.get("isLeader").getValue());
    if(!isLeader) {
        return {"result": false, "message": "请选择是否需要分管领导审批"};
    }
    let isBigChange=$.trim(mini.get("isBigChange").getValue());
    if(!isBigChange) {
        return {"result": false, "message": "请选择是否重大变更"};
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
    if (selectLeader == 'yes') {
        var formValid = validLeader();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    window.parent.approve();
}
