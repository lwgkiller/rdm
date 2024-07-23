var isFirstNode = false;
$(function () {
    if (action == 'task') {
        bpmPreTaskTipInForm();
        getProcessNodeVars();
        if (!isFirstNode) {
            applyForm.setEnabled(false);
            setButtonStatus(false);
        } else {
            if (!permission &&!techAdmin) {
                mini.get("title").setEnabled(false);
                mini.get("remark").setEnabled(false);
            }
        }
        applyForm.setData(ApplyObj);
    } else if (action == 'detail') {
        $("#detailToolBar").show();
        applyForm.setEnabled(false);
        applyForm.setData(ApplyObj);
        setButtonStatus(false);
    } else if (action == 'edit') {
        applyForm.setData(ApplyObj);
        if (!permission &&!techAdmin) {
            mini.get("title").setEnabled(false);
            mini.get("remark").setEnabled(false);
        }
    }
    loadItemGrid();
});
function setButtonStatus(flag) {
    mini.get('addButton').setEnabled(flag);
    mini.get('delButton').setEnabled(flag);
    itemGrid.setAllowCellEdit(flag);
}
function loadItemGrid() {
    var mainId = mini.get('id').getValue();
    var paramArray = [{name: "mainId", value: mainId}];
    var userName=$.trim(mini.get("userName").getValue());
    if(userName) {
        paramArray.push({name: "userName", value: userName});
    }
    var applyTime=$.trim(mini.get("applyTime").getText());
    if(applyTime) {
        paramArray.push({name: "applyTime", value: applyTime});
    }

    var data = {};
    data.filter = mini.encode(paramArray);
    data.mainId = mainId;
    var url=jsUseCtxPath+"/rdmZhgl/core/applyDetail/itemList.do";
    if((action == 'task' && !isFirstNode) || action == 'detail') {
        url+="?scene=addNoApply"
    }
    itemGrid.setUrl(url);
    itemGrid.load(data);
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
        if (nodeVars[i].KEY_ == 'isFirstNode') {
            isFirstNode = true;
        }
    }
}



//保存草稿
function saveApplyInfo(e) {
    var formValid = draftValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    //判断流程是否已经启动，启动后不允许保存
    var instId=mini.get("instId").getValue();
    if(instId) {
        $.ajax({
            url: jsUseCtxPath + '/rdmZhgl/core/delayApply/judgeApplyStart.do?instId='+instId,
            async: false,
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    if(data.success) {
                        mini.alert("申请单已由人资专员提交，无法保存！","提示",function () {
                            CloseWindow();
                        });
                    } else {
                        window.parent.saveDraft(e);
                    }
                }
            }
        });
    } else {
        window.parent.saveDraft(e);
    }
}

//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("applyForm");
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos=[];
    formData.vars=[{key:'deptName',val:formData.deptName}];
    var data = itemGrid.getChanges();
    if (data.length > 0) {
        formData.itemChangeData=data;
    }
    return formData;
}

//保存草稿时数据是否有效
function draftValid() {
    let title=$.trim(mini.get("title").getValue());
    if(!title) {
        return {"result": false, "message": "请填写申请单标题"};
    }
    var checkResult=itemGridCheck();
    if(!checkResult.result) {
        return checkResult;
    }
    return {"result": true};
}
//提交时数据是否有效
function startValid() {
    if (!permission&&!techAdmin) {
        mini.alert("非人资专员不允许操作！");
        return
    }
    let title=$.trim(mini.get("title").getValue());
    if(!title) {
        return {"result": false, "message": "请填写申请单标题"};
    }
    if (itemGrid.getData().length <= 0) {
        return {"result": false, "message": "请添加休息日加班人员明细"};
    }
    var checkResult=itemGridCheck();
    if(!checkResult.result) {
        return checkResult;
    }
    return {"result": true};
}

function itemGridCheck() {
    var result={result:true};
    var data = itemGrid.getChanges();
    if (data.length > 0) {
        for (var i = 0; i < data.length; i++) {
            if (data[i]._state == 'removed') {
                continue;
            }
            if (!data[i].userId||!data[i].workDate ||!data[i].reason||!data[i].workTime) {
                result.message = "请填写加班明细表中的必填项！";
                result.result = false;
                return result;
            }
        }
    }
    return result;
}

//启动流程
function startApplyProcess(e) {
    var formValid = startValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    mini.confirm("提交后不可修改，请确保本部门所有人员已提报加班，确定继续？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            window.parent.startProcess(e);
        }
    });

}

//审批或者下一步
function applyApprove() {
    if(isFirstNode) {
        var formValid = startValid();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        mini.confirm("提交后不可修改，请确保本部门所有人员已提报加班，确定继续？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                window.parent.approve();
            }
        });
    } else {
        window.parent.approve();
    }
}

// 人资专员填写的保存草稿
function saveDelayApplyInProcess() {
    var formValid = draftValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    var formData = _GetFormJsonMini("applyForm");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    var data = itemGrid.getChanges();
    if (data.length > 0) {
        formData.itemChangeData=data;
    }
    $.ajax({
        url: jsUseCtxPath + '/rdmZhgl/core/delayApply/saveDelayApply.do',
        type: 'post',
        async: false,
        data:mini.encode(formData),
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                var message="";
                if(data.success) {
                    message="数据保存成功";
                } else {
                    message="数据保存失败"+data.message;
                }

                mini.alert(message,"提示信息",function () {
                    window.location.reload();
                });
            }
        }
    });
}

