$(function () {
    searchFrm();
});


function versionTypeRenderer(e) {
    var record = e.record;
    var arr = [{key: 'cgb', value: '常规版'}, {key: 'csb', value: '测试版'}, {key: 'wzb', value: '完整版'}];
    return $.formatItemValue(arr, record.versionType);
}

function productTypeRenderer(e) {
    var record = e.record;
    var arr = [
        {key:'lunWa',value:'轮挖'},
        {key:'teWa',value:'特挖'},
        {key:'dianWa',value:'电挖'},
        {key:'teWa',value:'电挖'},
        {key:'lvWa',value:'履挖'}
    ];
    return $.formatItemValue(arr, record.productType);
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
    if (!taskStatus) {
        return "<span class='blue'>成功结束</span>";
    }
    return $.formatItemValue(arr, taskStatus);
}

//新增
function addJxbzzbsh() {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/JXBZZBSH/start.do";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (jxbzzbshGrid) {
                jxbzzbshGrid.reload()
            }
        }
    }, 1000);
}

//变更
function changeJxbzzbsh() {
    var selectRow = jxbzzbshGrid.getSelecteds();
    if (selectRow.length <= 0) {
        mini.alert(jxbzzbshList_name5);
        return;
    }
    if (selectRow.length>1) {
        mini.alert(jxbzzbshList_name6);
        return;
    }
    if(selectRow[0].taskStatus&&selectRow[0].taskStatus!="SUCCESS_END"){
        mini.alert(jxbzzbshList_name7);
        return;
    }
    if(selectRow[0].note!="最新版本"){
        mini.alert(jxbzzbshList_name8);
        return;
    }
    var id = selectRow[0].id;
    var url = jsUseCtxPath + "/bpm/core/bpmInst/JXBZZBSH/start.do?change=yes&oldId="+id;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (jxbzzbshGrid) {
                jxbzzbshGrid.reload();
            }
        }
    }, 1000);
}
//编辑
function editJxbzzbsh(jxbzzbshId, instId) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (jxbzzbshGrid) {
                jxbzzbshGrid.reload()
            }
        }
    }, 1000);
}

function uploadJxbzzbsh(jxbzzbshId) {
    var action = "upload";
    var url = jsUseCtxPath + "/serviceEngineering/core/jxbzzbsh/jxbzzbshEditPage.do?action=" + action + "&jxbzzbshId=" + jxbzzbshId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (jxbzzbshGrid) {
                jxbzzbshGrid.reload()
            }
        }
    }, 1000);
}

//删除
function delJxbzzbsh(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = jxbzzbshGrid.getSelecteds();
    }
    if (rows.length <= 0) {
        mini.alert(jxbzzbshList_name9);
        return;
    }
    mini.confirm(jxbzzbshList_name10, jxbzzbshList_name11, function (action) {
        if (action != 'ok') {
            return;
        } else {
            var ids = [];
            var instIds = [];
            var existStartInst = false;
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                if (r.taskStatus == 'DRAFTED' || r.taskStatus == 'DISCARD_END' || currentUserNo == 'admin') {
                    ids.push(r.id);
                    instIds.push(r.instId);
                } else {
                    existStartInst = true;
                    continue;
                }
            }
            if (existStartInst) {
                alert(jxbzzbshList_name12);
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/serviceEngineering/core/jxbzzbsh/deleteJxbzzbsh.do",
                    method: 'POST',
                    data: {ids: ids.join(','), instIds: instIds.join(',')},
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
function toJxbzzbshDetail(jxbzzbshId, taskStatus) {
    var action = "detail";
    var url = jsUseCtxPath + "/serviceEngineering/core/jxbzzbsh/jxbzzbshEditPage.do?action=" + action + "&jxbzzbshId=" + jxbzzbshId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (jxbzzbshGrid) {
                jxbzzbshGrid.reload()
            }
        }
    }, 1000);
}

//跳转到任务的处理界面
function jxbzzbshTask(taskId) {
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
                        if (jxbzzbshGrid) {
                            jxbzzbshGrid.reload();
                        }
                    }
                }, 1000);
            }
        }
    })
}

