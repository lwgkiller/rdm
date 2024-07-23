$(function () {
    if(action=='detail') {
        standardDemandApply.setEnabled(false);
        $("#detailToolBar").show();
        if(formStatus!='DRAFTED') {
            $("#processInfo").show();
        }
    } else if(action=='task') {
        bpmPreTaskTipInForm();
        var nodeVarsObj=getProcessNodeVars();
        if(nodeVarsObj.nodeTask=='编制') {
            mini.get("standardUserOpinion").setEnabled(false);
            mini.get("doDeptRespId").setEnabled(false);
            mini.get("acceptStatus").setEnabled(false);
        } else if(nodeVarsObj.nodeTask=='反馈') {
            mini.get("applyUserPhone").setEnabled(false);
            mini.get("problemDesc").setEnabled(false);
            mini.get("feedbackType").setEnabled(false);
            mini.get("problemStandardId").setEnabled(false);
        } else {
            standardDemandApply.setEnabled(false);
        }
    } else if(action=='edit') {
        mini.get("standardUserOpinion").setEnabled(false);
        mini.get("doDeptRespId").setEnabled(false);
        mini.get("acceptStatus").setEnabled(false);
    }
    if(standardDemandObj) {
        standardDemandApply.setData(standardDemandObj);
        if(standardDemandObj.feedbackType=='problem') {
            $("#standardTd1").show();
            $("#standardTd2").show();
        }
    }

});



function getProcessNodeVars() {
    var nodeVarsObj={};
    //获取环境变量
    if (!nodeVarsStr) {
        nodeVarsStr = "[]";
    }
    var nodeVars = $.parseJSON(nodeVarsStr);
    for (var i = 0; i < nodeVars.length; i++) {
        nodeVarsObj[nodeVars[i].KEY_]= nodeVars[i].DEF_VAL_;
    }

    return nodeVarsObj;
}




//保存草稿
function saveStandardDemand(e) {
    var formValid = draftOrStartValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.saveDraft(e);
}

//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("standardDemandApply");
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos=[];
    formData.vars=[];
    return formData;
}

//保存草稿或提交时数据是否有效
function draftOrStartValid() {
    var applyUserPhone=$.trim(mini.get("applyUserPhone").getValue());
    if(!applyUserPhone || applyUserPhone.length != 11) {
        return {"result": false, "message": jsStandardDemandEdit_name};
    }
    var problemDesc=$.trim(mini.get("problemDesc").getText());
    if(!problemDesc || problemDesc.length>800) {
        return {"result": false, "message": jsStandardDemandEdit_name1};
    }
    var feedbackType=mini.get("feedbackType").getValue();
    if(feedbackType=='problem') {
        var problemStandardId=mini.get("problemStandardId").getValue();
        if(!problemStandardId) {
            return {"result": false, "message": jsStandardDemandEdit_name2};
        }
    }
    return {"result": true};
}

//下一步时数据是否有效
function approveValid() {
    var nodeVarsObj=getProcessNodeVars();
    if(nodeVarsObj.nodeTask=='编制') {
        return draftOrStartValid();
    } else if(nodeVarsObj.nodeTask=='反馈') {
        var doDeptRespId=mini.get("doDeptRespId").getValue();
        if(!doDeptRespId) {
            return {"result": false, "message": jsStandardDemandEdit_name3};
        }
        var acceptStatus=mini.get("acceptStatus").getValue();
        if(!acceptStatus) {
            return {"result": false, "message": jsStandardDemandEdit_name4};
        }
        var standardUserOpinion=$.trim(mini.get("standardUserOpinion").getValue());
        if(!standardUserOpinion || standardUserOpinion.length >800) {
            return {"result": false, "message": jsStandardDemandEdit_name5};
        }
    }

    return {"result": true};
}

//启动流程
function startStandardDemandProcess(e) {
    var formValid = draftOrStartValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.startProcess(e);
}

//审批或者下一步
function standardDemandApprove() {
    var formValid = approveValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.approve();
}

function processInfo() {
    var instId = $("#instId").val();
    var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: jsStandardDemandEdit_name6,
        width: 800,
        height: 600
    });
}

function selectStandard(){
    selectStandardWindow.show();
    searchStandard();
}
//查询标准
function searchStandard() {
    var queryParam = [];
    //其他筛选条件
    var standardNumber = $.trim(mini.get("filterStandardNumberId").getValue());
    if (standardNumber) {
        queryParam.push({name: "standardNumber", value: standardNumber});
    }
    var standardName = $.trim(mini.get("filterStandardNameId").getValue());
    if (standardName) {
        queryParam.push({name: "standardName", value: standardName});
    }
    queryParam.push({name: "systemCategoryId", value: 'JS'});
    var data = {};
    data.filter = mini.encode(queryParam);
    data.pageIndex = standardListGrid.getPageIndex();
    data.pageSize = standardListGrid.getPageSize();
    data.sortField = standardListGrid.getSortField();
    data.sortOrder = standardListGrid.getSortOrder();
    //查询
    standardListGrid.load(data);
}

function onRowDblClick() {
    selectStandardOK();
}

function selectStandardOK() {
    var selectRow = standardListGrid.getSelected();
    if (selectRow) {
        mini.get("problemStandardId").setValue(selectRow.id);
        mini.get("problemStandardId").setText(selectRow.standardNumber+'('+selectRow.standardName+')');
    }
    selectStandardHide();
}

function selectStandardHide() {
    selectStandardWindow.hide();
    mini.get("filterStandardNumberId").setValue('');
    mini.get("filterStandardNameId").setValue('');
}

function onSelectStandardCloseClick(inputScene) {
    mini.get("problemStandardId").setValue('');
    mini.get("problemStandardId").setText('');
}

function typeChange() {
    var feedbackType=mini.get("feedbackType").getValue();
    if(feedbackType=='problem') {
        $("#standardTd1").show();
        $("#standardTd2").show();
    } else{
        $("#standardTd1").hide();
        $("#standardTd2").hide();
        mini.get("problemStandardId").setValue('');
        mini.get("problemStandardId").setText('');
    }
}
