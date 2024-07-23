var editForm='';

$(function () {
    if(action=='task') {
        bpmPreTaskTipInForm();
        getProcessNodeVars();
        if(editForm!='1') {
            formStandardApply.setEnabled(false);
        }
    }
});


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
    }
}



function selectStandard(){
    selectStandardWindow.show();
    $.ajax({
        url:jsUseCtxPath+'/standardManager/core/standardSystem/queryCategory.do',
        success:function (data) {
            if(data) {
                mini.get("standardSystemCategory").load(data);
                mini.get("standardSystemCategory").setValue(data[0].systemCategoryId);
                searchStandard();
            }
        }
    });

}

//查询标准
function searchStandard() {
    var queryParam = [];
    //其他筛选条件
    var standardNumber = $.trim(mini.get("filterNumberId").getValue());
    if (standardNumber) {
        queryParam.push({name: "standardNumber", value: standardNumber});
    }
    queryParam.push({name: "systemCategoryId", value: $.trim(mini.get("standardSystemCategory").getValue())});
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
    okWindow();
}

function okWindow() {
    var selectRow = standardListGrid.getSelected();
    if (selectRow) {
        mini.get("standardSelectId").setText(selectRow.standardName);
        mini.get("standardSelectId").setValue(selectRow.id);
        mini.get("standardNumber").setValue(selectRow.standardNumber);
        mini.get("categoryName").setValue(selectRow.categoryName);
        mini.get("systemName").setValue(selectRow.systemName);
        mini.get("systemCategoryId").setValue(selectRow.systemCategoryId);
    }
    hideWindow();
}

function hideWindow() {
    selectStandardWindow.hide();
    mini.get("standardSystemCategory").setValue('');
    mini.get("filterNumberId").setValue('');
}


//保存草稿
function saveStandardApply(e) {
    var formValid = draftOrStartValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.saveDraft(e);
}

//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("formStandardApply");
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos=[];
    formData.vars=[{key:'applyCategoryName',val:formData.applyCategoryName}];
    return formData;
}

//保存草稿或提交时数据是否有效
function draftOrStartValid() {
    var standardSelectId=$.trim(mini.get("standardSelectId").getValue());
    var standardSelectName=$.trim(mini.get("standardSelectId").getText());
    if(!standardSelectId||!standardSelectName) {
        return {"result": false, "message": standardApplyEdit_name};
    }
    var applyReason=$.trim(mini.get("applyReason").getValue());
    if(!applyReason) {
        return {"result": false, "message": standardApplyEdit_name1};
    }

    return {"result": true};
}

//启动流程
function startStandardApplyProcess(e) {
    var formValid = draftOrStartValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.startProcess(e);
}

//审批或者下一步
function standardApplyApprove() {
    var formValid = draftOrStartValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.approve();
}