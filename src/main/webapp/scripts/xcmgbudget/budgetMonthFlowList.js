$(function () {
    if (currentUserRoles) {
        if(currentUserRoles.ysManager && currentUserRoles.ysManager=='yes') {
            //预算管理人员
            mini.get("flowYearMonth").setValue(lastMonth);
            //预算管理人员放开作废权限
            $("#discardB").attr("style","visibility:visible");

        } else if(currentUserRoles.role && currentUserRoles.role=='user_role_deptLeader') {
            //分管领导
            mini.get("flowYearMonth").setValue(lastMonth);
        }
    }
    //系统管理员放开作废权限
    if (currentUserNo=='admin'){
        $("#discardB").attr("style","visibility:visible");
    }
    // 查询列表
    searchFrm();
});


//新增流程（后台根据配置的表单进行跳转）
function addBudgetMonthFlow() {
    var date = new Date();
    var day = date.getDate();
    // if (!(1 <=day && day < 19)){
    //     mini.alert("仅当前月份19号之前可以提报下月预算！");
    //     return;
    // }
    var url = jsUseCtxPath + "/bpm/core/bpmInst/YDYSSB/start.do";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if(!winObj) {
            clearInterval(loop);
        } else if (winObj.closed) {
            clearInterval(loop);
            if (budgetMonthFlowListGrid) {
                budgetMonthFlowListGrid.reload();
            }
        }
    }, 1000);
}


//编辑行数据流程（后台根据配置的表单进行跳转）
function editBudgetMonthRow(instId) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if(!winObj) {
            clearInterval(loop);
        } else if (winObj.closed) {
            clearInterval(loop);
            if (budgetMonthFlowListGrid) {
                budgetMonthFlowListGrid.reload();
            }
        }
    }, 1000);
}

//删除记录
function rmBudgetMonthFlow(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = budgetMonthFlowListGrid.getSelecteds();
    }
    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }
    mini.confirm("删除当前流程，是否继续？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            //判断该行是否已经提交流程
            var ids = [];
            var instIds = [];
            var existStartInst = false;
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                if ((r.instStatus == 'DRAFTED'||r.instStatus == 'DISCARD_END')&& (currentUserId==r.CREATE_BY_||currentUserNo=='admin')) {
                    ids.push(r.budgetId);
                    instIds.push(r.instId);
                }
                else {
                    existStartInst = true;
                    continue;
                }
            }
            if (existStartInst) {
                mini.alert("仅草稿或废止状态数据可由本人删除！");
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/xcmgBudget/core/budgetMonthFlow/deleteBudgetMonthFlow.do",
                    method: 'POST',
                    data: {ids: ids.join(','), instIds: instIds.join(',')},
                    success: function (text) {
                        budgetMonthFlowListGrid.reload();
                    }
                });
            }
        }
    });
}

//明细（直接跳转到详情的业务controller）
function detailBudgetMonthRow(budgetId, status) {
    var action = "detail";
    var url = jsUseCtxPath + "/xcmgBudget/core/budgetMonthFlow/tabPage.do?action=" + action + "&id=" + budgetId + "&status=" + status;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if(!winObj) {
            clearInterval(loop);
        } else if (winObj.closed) {
            clearInterval(loop);
            if (budgetMonthFlowListGrid) {
                budgetMonthFlowListGrid.reload();
            }
        }
    }, 1000);
}

//修改（直接跳转到详情的业务controller）
function updateBudgetMonthRow(budgetId, status) {
    var action = "update";
    var url = jsUseCtxPath + "/xcmgBudget/core/budgetMonthFlow/tabPage.do?action=" + action + "&id=" + budgetId + "&status=" + status;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if(!winObj) {
            clearInterval(loop);
        } else if (winObj.closed) {
            clearInterval(loop);
            if (budgetMonthFlowListGrid) {
                budgetMonthFlowListGrid.reload();
            }
        }
    }, 1000);
}

//修改（直接跳转到详情的业务controller）
function baoxiao(budgetId, status) {
    var action = "baoxiao";
    var url = jsUseCtxPath + "/xcmgBudget/core/budgetMonthFlow/tabPage.do?action=" + action + "&id=" + budgetId + "&status=" + status;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if(!winObj) {
            clearInterval(loop);
        } else if (winObj.closed) {
            clearInterval(loop);
            if (budgetMonthFlowListGrid) {
                budgetMonthFlowListGrid.reload();
            }
        }
    }, 1000);
}

//跳转到任务的处理界面
function doBudgetMonthTask(taskId) {
    $.ajax({
        url: jsUseCtxPath + '/bpm/core/bpmTask/checkTaskLockStatus.do?taskId=' + taskId,
        success: function (result) {
            if (!result.success) {
                top._ShowTips({
                    msg: result.message
                });
            } else {
                handBudgetMonthTask(taskId);
            }
        }
    })
}

//流程任务的处理（后台根据流程方案对应的表单进行跳转）
function handBudgetMonthTask(taskId) {
    var url = jsUseCtxPath + '/bpm/core/bpmTask/toStart.do?taskId=' + taskId;
    var winObj = openNewWindow(url, "handTask");
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (budgetMonthFlowListGrid) {
                budgetMonthFlowListGrid.reload();
            }
        }
    }, 1000);

}
//流程作废
function discardBudgetMonthFlow(record){
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = budgetMonthFlowListGrid.getSelecteds();
    }
    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }
    mini.confirm("作废当前流程，是否继续？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            //判断该行是否已经提交流程
            var ids = [];
            var instIds = [];

            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                ids.push(r.budgetId);
                instIds.push(r.instId);
            }

            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/xcmgBudget/core/budgetMonth/discardMonthProcess.do",
                    method: 'POST',
                    data: {ids: ids.join(','), instIds: instIds.join(',')},
                    success: function (text) {
                        budgetMonthFlowListGrid.reload();
                    }
                });
            }
        }
    });
}
