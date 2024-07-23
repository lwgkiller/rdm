var editForm = '';
var isFirstNode = false;
var selectGroupLeader = false;
$(function () {
    if (action == 'task') {
        bpmPreTaskTipInForm();
        getProcessNodeVars();
        if (editForm != '1' && !isFirstNode) {
            applyForm.setEnabled(false);
        }
        applyForm.setData(ApplyObj);
    } else if (action == 'detail') {
        $("#detailToolBar").show();
        applyForm.setEnabled(false);
        applyForm.setData(ApplyObj);
    } else if (action == 'edit') {
        applyForm.setData(ApplyObj);
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
        if (nodeVars[i].KEY_ == 'selectGroupLeader') {
            selectGroupLeader = true;
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
    var applyReason=$.trim(mini.get("reason").getValue());
    if(!applyReason) {
        return {"result": false, "message": "请填写申请理由"};
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
function editInfo(e) {
    var id = mini.get("mainId").getValue();
    var url = jsUseCtxPath + "/rdmZhgl/core/yfjb/getEditPage.do?type=apply&&action=edit&&id=" + id;
    var title = "编辑";
    mini.open({
        title: title,
        url: url,
        width: 1200,
        height: 1000,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
        }
    });
}
//行功能按钮
function onActionRenderer(e) {
    var s = '';
    var processStatus = e.record.processStatus;
    if(processStatus=='tq'){
        e.rowStyle = 'background-color:green';
    }else if(processStatus=='yh'){
        e.rowStyle = 'background-color:red';
    }

    return s;
}
function toFixNum(e) {
    if(e.value){
        return parseFloat(e.value).toFixed(2);
    }
}
function onProcessStatus(e) {
    var processStatus = e.record.processStatus;
    if(processStatus=='tq'){
        e.rowStyle = 'background-color:green';
    }else if(processStatus=='yh'){
        e.rowStyle = 'background-color:red';
    }
}
function onJbfs(e) {
    var record = e.record;
    var resultValue = record.costType;
    var resultText = '';
    for (var i = 0; i < jbfsList.length; i++) {
        if (jbfsList[i].key_ == resultValue) {
            resultText = jbfsList[i].text;
            break
        }
    }
    return resultText;
}
function onReplace(e) {
    var record = e.record;
    var resultValue = record.isReplace;
    var resultText = '';
    for (var i = 0; i < replaceList.length; i++) {
        if (replaceList[i].key_ == resultValue) {
            resultText = replaceList[i].text;
            break
        }
    }
    return resultText;
}
function onMajor(e) {
    var record = e.record;
    var resultValue = record.major;
    var resultText = '';
    for (var i = 0; i < majorList.length; i++) {
        if (majorList[i].key_ == resultValue) {
            resultText = majorList[i].text;
            break
        }
    }
    return resultText;
}
function onStatus(e) {
    var record = e.record;
    var resultValue = record.infoStatus;
    var resultText = '';
    for (var i = 0; i < statusList.length; i++) {
        if (statusList[i].key_ == resultValue) {
            resultText = statusList[i].text;
            break
        }
    }
    var _html = '';
    var color = '';
    if(resultValue=='1'){
        color = '#cdcd62'
    }else if(resultValue=='2'){
        color = 'green';
    }else if(resultValue=='3'){
        color = 'red';
    }else if(resultValue=='4'){
        color = '#3b0df0';
    }
    _html = '<span style="color: '+color+'">'+resultText+'</span>'
    return _html;
}
function onHidePrice() {
    return '***';
}
function process(e) {
    var record = e.record;
    var id = record.id;
    var mainId = record.id;
    var response = record.response;
    var s = '';
    s = '<a href="#" style="color:#44cef6;text-decoration:underline;" ' +
        'onclick="showFilePage(\'' + mainId + '\',\'' + response + '\')">项目进度</a>';
    return s;
}
function showFilePage(mainId,response) {
    var url = jsUseCtxPath + "/rdmZhgl/core/yfjb/getProcessPage.do?mainId=" + mainId+"&response="+response;
    var title = "降本项目进度跟踪";
    mini.open({
        title: title,
        url: url,
        width: 1200,
        height: 800,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
        }
    });
}
function renderMember(e) {
    var record = e.record;
    var id = record.id;
    var mainId = record.id;
    var response = record.response;
    var s = '';
    s = '<a href="#" style="color:#44cef6;text-decoration:underline;" ' +
        'onclick="showMemberPage(\'' + mainId + '\',\'' + response + '\')">项目成员</a>';
    return s;
}
function showMemberPage(mainId,response) {
    var url = jsUseCtxPath + "/rdmZhgl/core/yfjb/memberPage.do?mainId=" + mainId+"&response="+response;
    var title = "项目成员维护";
    mini.open({
        title: title,
        url: url,
        width: 1200,
        height: 800,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
        }
    });
}
