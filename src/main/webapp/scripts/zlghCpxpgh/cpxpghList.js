var rendererData = [{key:'yes',value: '√'}, {key:'no', value:'×'}];
$(function () {
    searchFrm();
});

function productStatusRenderer(e) {
    var record = e.record;
    var arr = [{key:'lc',value:'量产'},{key:'kfz',value:'开发中'},{key:'ztkf',value:'暂停开发'},{key:'tc',value:'停产'}];
    return $.formatItemValue(arr, record.productStatus);
}

function archivedFileRenderer(e) {
    var record = e.record;
    return $.formatItemValue(rendererData, record.archivedFile);
}

function hbxxgkRenderer(e) {
    var record = e.record;
    return $.formatItemValue(rendererData, record.hbxxgk);
}

function cpxssyztRenderer(e) {
    var record = e.record;
    return $.formatItemValue(rendererData, record.cpxssyzt);
}

function cbscRenderer(e) {
    var record = e.record;
    return $.formatItemValue(rendererData, record.cbsc);
}

function cptsztRenderer(e) {
    var record = e.record;
    return $.formatItemValue(rendererData, record.cptszt);
}

function cpcskhztRenderer(e) {
    var record = e.record;
    return $.formatItemValue(rendererData, record.cpcskhzt);
}

function cpckrzztRenderer(e) {
    var record = e.record;
    var arr = [{key:'tuv',value: '北美TUV'}, {key:'ce', value:'CE'}, {key:'eac', value:'俄罗斯EAC'}];
    return $.formatItemValue(arr, record.cpckrzzt);
}

function cpxpghTaskStatusRenderer(e) {
    var record = e.record;
    var taskStatus = record.taskStatus;
    var arr = [
        {'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
        {'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
        {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},
        {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
        {'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},
        {'key': 'PENDING', 'value': '挂起', 'css': 'gray'}
    ];

    return $.formatItemValue(arr,taskStatus);
}

function cpxpghDel(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = cpxpghGrid.getSelecteds();
    }
    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }

    mini.confirm("确定删除选中记录？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var ids = [];
            var instIds = [];
            var existStartInst = false;
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                if (r.taskStatus == 'DRAFTED') {
                    ids.push(r.id);
                    instIds.push(r.instId);
                } else {
                    existStartInst = true;
                    continue;
                }
            }
            if (existStartInst) {
                alert("仅草稿状态数据可由本人删除");
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/strategicplanning/core/cpxpgh/deleteCpxpgh.do",
                    method: 'POST',
                    data: {ids: ids.join(','), instIds: instIds.join(',')},
                    showMsg: false,
                    success: function (data) {
                        if (data.success) {
                            mini.alert(data.message,"提示", function () {
                                searchFrm();
                            })
                        }
                    }
                });
            }
        }
    });
}

//新增流程（后台根据配置的表单进行跳转）
function cpxpghAdd() {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/CPXPGH/start.do";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (cpxpghGrid) {
                cpxpghGrid.reload()
            }
        }
    }, 1000);
}

//编辑行数据流程（后台根据配置的表单进行跳转）
function cpxpghEdit(cpxpghId, instId) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (cpxpghGrid) {
                cpxpghGrid.reload()
            }
        }
    }, 1000);
}

//明细（直接跳转到详情的业务controller）
function cpxpghDetail(cpxpghId, taskStatus) {
    var action = "detail";
    var url = jsUseCtxPath + "/strategicplanning/core/cpxpgh/cpxpghEditPage.do?action=" + action + "&cpxpghId=" + cpxpghId + "&taskStatus=" + taskStatus;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (cpxpghGrid) {
                cpxpghGrid.reload()
            }
        }
    }, 1000);
}

//点击办理是跳转
function cpxpghTask(taskId) {
    $.ajax({
        url: jsUseCtxPath + '/bpm/core/bpmTask/checkTaskLockStatus.do?taskId=' + taskId,
        success: function (result) {
            if (!result.success) {
                top._ShowTips({
                    msg: result.message
                });
            } else {
                var url = jsUseCtxPath + '/bpm/core/bpmTask/toStart.do?taskId=' + taskId;
                var winObj = openNewWindow(url, "handTask");
                var loop = setInterval(function () {
                    if (winObj.closed) {
                        clearInterval(loop);
                        if (cpxpghGrid) {
                            cpxpghGrid.reload();
                        }
                    }
                }, 1000);
            }
        }
    })
}

