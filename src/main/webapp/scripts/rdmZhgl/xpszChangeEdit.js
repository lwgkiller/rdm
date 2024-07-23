var editForm = '';
var isFirstNode = false;
$(function () {
    if (action == 'task') {
        bpmPreTaskTipInForm();
        getProcessNodeVars();
        if (editForm != '1' && !isFirstNode) {
            productApplyForm.setEnabled(false);
        }
        productApplyForm.setData(ApplyObj);
        searchList()
    } else if (action == 'detail') {
        $("#detailToolBar").show();
        productApplyForm.setEnabled(false);
        productApplyForm.setData(ApplyObj);
        searchList()
    } else if (action == 'edit') {
        productApplyForm.setData(ApplyObj);
        searchList()
    }
});

function searchList() {
    loadProductGrid();
}
function loadProductGrid() {
    if(ApplyObj.mainIds){
        var paramArray = [{name: "mainIds", value: ApplyObj.mainIds}];
        var data = {};
        data.filter = mini.encode(paramArray);
        productGrid.load(data);
    }
}
function verifyEndItem(e) {
    var endObj = e.selected;
    var sort = endObj.sort;
    var startId = startItemIdCat.getValue();
    if(!startId){
        mini.alert("请先选择开始节点！");
        return;
    }
    let postData = {"startId":startId,"sort":sort};
    let _url = jsUseCtxPath + '/rdmZhgl/core/productConfig/verifyItem.do';
    let resultData = ajaxRequest(_url,'POST',false,postData);
    if(resultData&&!resultData.success){
        mini.alert(resultData.message);
        return;
    }
}
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
    var formData = _GetFormJsonMini("productApplyForm");
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos = [];
    formData.vars = [];
    return formData;
}

//保存草稿或提交时数据是否有效
function draftOrStartValid() {
    let applyReason=$.trim(mini.get("reason").getValue());
    if(!applyReason) {
        return {"result": false, "message": "请填写申请理由"};
    }
    let startItemId=$.trim(mini.get("startItemId").getValue());
    if(!startItemId) {
        return {"result": false, "message": "请选择开始节点"};
    }
    let endItemId=$.trim(mini.get("endItemId").getValue());
    if(!endItemId) {
        return {"result": false, "message": "请选择结束节点"};
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

function applyChange(e) {
    mini.confirm("确定变更计划？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var id = mini.get("id").getValue();
            let postData = {"id":id};
            let _url = jsUseCtxPath + '/rdmZhgl/core/xpszChange/changePlan.do';
            let resultData = ajaxRequest(_url,'POST',false,postData);
            if(resultData){
                mini.alert(resultData.message);
                searchList()
                return;
            }
        }
    });

}
