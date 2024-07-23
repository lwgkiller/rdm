$(function () {
    searchFrm();
});

function versionTypeRenderer(e) {
    var record = e.record;
    var arr = [{key:'cgb',value:'常规版'},{key:'wzb',value:'完整版'}];
    return $.formatItemValue(arr,record.versionType);
}

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


//新增流程（后台根据配置的表单进行跳转）
function addJxcshc() {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/JXCSHC/start.do";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (jxcshcGrid) {
                jxcshcGrid.reload()
            }
        }
    }, 1000);
}

//编辑行数据流程（后台根据配置的表单进行跳转）
function jxcshcEdit(jxcshcId, instId) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (jxcshcGrid) {
                jxcshcGrid.reload()
            }
        }
    }, 1000);
}

//明细（直接跳转到详情的业务controller）
function jxcshcDetail(jxcshcId, taskStatus) {
    var action = "detail";
    var url = jsUseCtxPath + "/serviceEngineering/core/jxcshc/jxcshcEditPage.do?action=" + action + "&jxcshcId=" + jxcshcId + "&taskStatus=" + taskStatus;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (jxcshcGrid) {
                jxcshcGrid.reload()
            }
        }
    }, 1000);
}

function removeJxcshc(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = jxcshcGrid.getSelecteds();
    }
    if (rows.length <= 0) {
        mini.alert(jxcshcList_name4);
        return;
    }
    mini.confirm(jxcshcList_name5, jxcshcList_name6, function (action) {
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
                alert(jxcshcList_name7);
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/serviceEngineering/core/jxcshc/deleteJxcshc.do",
                    method: 'POST',
                    showMsg:false,
                    data: {ids: ids.join(','), instIds: instIds.join(',')},
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

//跳转到任务的处理界面
function jxcshcTask(taskId) {
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
                        if (jxcshcGrid) {
                            jxcshcGrid.reload();
                        }
                    }
                }, 1000);
            }
        }
    })
}


