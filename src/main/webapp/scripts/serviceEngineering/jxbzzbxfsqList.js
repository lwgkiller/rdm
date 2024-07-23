$(function () {
    searchFrm();
});

function taskStatusRenderer(e) {
    var record = e.record;
    var taskStatus = record.taskStatus;
    var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
        {'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
        {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},
        {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
        {'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},
        {'key': 'PENDING', 'value': '挂起', 'css': 'gray'}
    ];
    return $.formatItemValue(arr,taskStatus);
}

//新增
function addJxbzzbxfsq() {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/JXBZZBXFSQ/start.do";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (jxbzzbxfsqGrid) {
                jxbzzbxfsqGrid.reload()
            }
        }
    }, 1000);
}

// 编辑
function editJxbzzbxfsq(jxbzzbxfsqId, instId) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (jxbzzbxfsqGrid) {
                jxbzzbxfsqGrid.reload()
            }
        }
    }, 1000);
}

//删除
function delJxbzzbxfsq(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = jxbzzbxfsqGrid.getSelecteds();
    }
    if (rows.length <= 0) {
        mini.alert(jxbzzbxfsqList_name4);
        return;
    }
    mini.confirm(jxbzzbxfsqList_name5, jxbzzbxfsqList_name6, function (action) {
        if (action != 'ok') {
            return;
        } else {
            var ids = [];
            var instIds = [];
            var existStartInst = false;
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                if (r.taskStatus == 'DRAFTED' || r.taskStatus == 'DISCARD_END') {
                    ids.push(r.id);
                    instIds.push(r.instId);
                } else {
                    existStartInst = true;
                    continue;
                }
            }
            if (existStartInst) {
                alert(jxbzzbxfsqList_name7);
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/serviceEngineering/core/jxbzzbxfsq/deleteJxbzzbxfsq.do",
                    method: 'POST',
                    data: {ids: ids.join(','),instIds: instIds.join(',')},
                    success: function (data) {
                        if (data) {
                            searchFrm();
                        }
                    }
                });
            }

        }
    });
}

//明细
function jxbzzbxfsqDetail(jxbzzbxfsqId) {
    var action = "detail";
    var url = jsUseCtxPath + "/serviceEngineering/core/jxbzzbxfsq/jxbzzbxfsqEditPage.do?action=" + action + "&jxbzzbxfsqId=" + jxbzzbxfsqId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (jxbzzbxfsqGrid) {
                jxbzzbxfsqGrid.reload()
            }
        }
    }, 1000);
}

//跳转到任务的处理界面
function jxbzzbxfsqTask(taskId) {
    $.ajax({
        url: jsUseCtxPath + '/bpm/core/bpmTask/checkTaskLockStatus.do?taskId=' + taskId,
        async:false,
        success: function (result) {
            if (!result.success) {
                top._ShowTips({
                    msg: result.message
                });
            } else {
                var url = jsUseCtxPath + '/bpm/core/bpmTask/toStart.do?taskId=' + taskId;
                var winObj = openNewWindow(url, "handTask");
                var loop = setInterval(function () {
                    if(!winObj) {
                        clearInterval(loop);
                    } else if (winObj.closed) {
                        clearInterval(loop);
                        if (jxbzzbxfsqGrid) {
                            jxbzzbxfsqGrid.reload();
                        }
                    }
                }, 1000);
            }
        }
    })
}

