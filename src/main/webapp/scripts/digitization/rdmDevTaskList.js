$(function () {
    searchFrm();
});


//新增流程（后台根据配置的表单进行跳转）
function addApply() {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/RDMGNKFSQ/start.do";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (devTaskListGrid) {
                devTaskListGrid.reload();
            }
        }
    }, 1000);
}

//编辑行数据流程（后台根据配置的表单进行跳转）
function devTaskEdit(applyId, instId) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (devTaskListGrid) {
                devTaskListGrid.reload()
            }
        }
    }, 1000);
}

//明细（直接跳转到详情的业务controller）
function devTaskDetail(applyId, status) {
    var action = "detail";
    var url = jsUseCtxPath + "/devTask/core/devEditPage.do?action=" + action + "&applyId=" + applyId + "&status=" + status;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (devTaskListGrid) {
                devTaskListGrid.reload();
            }
        }
    }, 1000);
}

//跳转到任务的处理界面
function devTaskDo(taskId) {
    $.ajax({
        url: jsUseCtxPath + '/bpm/core/bpmTask/checkTaskLockStatus.do?taskId=' + taskId,
        async: false,
        success: function (result) {
            if (!result.success) {
                top._ShowTips({
                    msg: result.message
                });
            } else {
                var url = jsUseCtxPath + '/bpm/core/bpmTask/toStart.do?taskId=' + taskId;
                var winObj = openNewWindow(url, "handTask");
                var loop = setInterval(function () {
                    if (!winObj) {
                        clearInterval(loop);
                    } else if (winObj.closed) {
                        clearInterval(loop);
                        if (devTaskListGrid) {
                            devTaskListGrid.reload();
                        }
                    }
                }, 1000);
            }
        }
    })
}

function removeApply(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = devTaskListGrid.getSelecteds();
    }
    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }
    mini.confirm("确定删除选中记录？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var rowIds = [];
            var instIds = [];
            var existStartInst = false;
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                if ((r.status == 'DRAFTED' && r.CREATE_BY_ == currentUserId) || currentUserNo == "admin") {
                    rowIds.push(r.applyId);
                    instIds.push(r.instId);
                } else {
                    existStartInst = true;
                    continue;
                }
            }
            if (existStartInst) {
                alert("仅草稿状态数据可由本人删除");
            }
            if (rowIds.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/devTask/core/deleteDevTask.do",
                    method: 'POST',
                    showMsg: false,
                    data: {ids: rowIds.join(','), instIds: instIds.join(',')},
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            searchFrm();
                        }
                    }
                });
            }
        }
    });
}