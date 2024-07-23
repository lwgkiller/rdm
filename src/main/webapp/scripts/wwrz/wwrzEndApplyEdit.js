var editForm = '';
var isFirstNode = false;
var editMoney = false;
$(function () {
    if (action == 'task') {
        bpmPreTaskTipInForm();
        getProcessNodeVars();
        if (editForm != '1' && !isFirstNode) {
            applyForm.setEnabled(false);
        }else {
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
    }
    if(ApplyObj.mainId){
        loadApplyGrid();
    }
    if(editMoney){
        mini.get('money').setEnabled(true);
    }else{
        mini.get('money').setEnabled(false);
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
        if (nodeVars[i].KEY_ == 'editMoney') {
            editMoney = true;
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
    var remark=$.trim(mini.get("remark").getText());
    if(!remark) {
        return {"result": false, "message": "请填写废止原因"};
    }
    var projectStatus=$.trim(mini.get("projectStatus").getText());
    if(!projectStatus) {
        return {"result": false, "message": "请填写项目状态"};
    }
    if(editMoney){
        var money = $.trim(mini.get("money").getValue());
        if(!money) {
            return {"result": false, "message": "请填写预计费用"};
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
function onProductType(e) {
    var record = e.record;
    var certType = record.productType;
    var resultText = '';
    for (var i = 0; i < productTypeList.length; i++) {
        if (productTypeList[i].key_ == certType) {
            resultText = productTypeList[i].text;
            break
        }
    }
    return resultText;
}
function onCabForm(e) {
    var record = e.record;
    var testType = record.cabForm;
    var resultText = '';
    for (var i = 0; i < cabFormList.length; i++) {
        if (cabFormList[i].key_ == testType) {
            resultText = cabFormList[i].text;
            break
        }
    }
    return resultText;
}
//行功能按钮
function onActionRenderer(e) {
    var record = e.record;
    var applyId = record.id;
    var s = '<span  title="明细" style="cursor: pointer;color: #0a7ac6" onclick="detailApply(\'' + applyId + '\',\'' + record.instStatus + '\')">明细</span>';
    return s;
}//明细 的点击查看方法
function detailApply(id, status) {
    var action = "detail";
    var url = jsUseCtxPath + "/wwrz/core/test/edit.do?action=" + action + "&id=" + id + "&status=" + status;
   window.open(url);
}
