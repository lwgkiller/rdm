$(function () {
    searchFrm();
});

function addZjhm() {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/ZJHM/start.do";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (zjhmListGrid) {
                zjhmListGrid.reload()
            }
        }
    }, 1000);
}
function zjhmEdit(projectId,instId) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId+ "&projectId=" + projectId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (zjhmListGrid) {
                zjhmListGrid.reload()
            }
        }
    }, 1000);
}


function zjhmDetail(projectId,status) {
    var action = "detail";
    var url = jsUseCtxPath + "/environment/core/Zjhm/editPage.do?action=" + action + "&projectId=" + projectId+ "&status=" + status;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (zjhmListGrid) {
                zjhmListGrid.reload()
            }
        }
    }, 1000);
}

function zjhmTask(taskId) {
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
                        if (zjhmListGrid) {
                            zjhmListGrid.reload();
                        }
                    }
                }, 1000);
            }
        }
    })
}

function removezjhm(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = zjhmListGrid.getSelecteds();
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
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                rowIds.push(r.projectId);
                instIds.push(r.instId);
            }
            _SubmitJson({
                url: jsUseCtxPath + "/environment/core/Zjhm/deleteZjhm.do",
                method: 'POST',
                showMsg:false,
                data: {ids: rowIds.join(','), instIds: instIds.join(',')},
                success: function (data) {
                    if (data) {
                        mini.alert(data.message);
                        searchFrm();
                    }
                }
            });
        }
    });
}