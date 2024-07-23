$(function () {
    searchFrm();
});


function addGyfk() {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/GYXXFK/start.do";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (gyfkListGrid) {
                gyfkListGrid.reload()
            }
        }
    }, 1000);
}

function gyfkEdit(gyfkId,instId,status) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId+ "&gyfkId=" + gyfkId+"&status="+status;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (gyfkListGrid) {
                gyfkListGrid.reload()
            }
        }
    }, 1000);
}

function gyfkDetail(gyfkId) {
    var action = "detail";
    var url = jsUseCtxPath + "/qualityfxgj/core/Gyfk/editPage.do?action=" + action + "&gyfkId=" + gyfkId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (gyfkListGrid) {
                gyfkListGrid.reload()
            }
        }
    }, 1000);
}
function gyfkTask(taskId) {
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
                        if (gyfkListGrid) {
                            gyfkListGrid.reload();
                        }
                    }
                }, 1000);
            }
        }
    })
}
function removeGyfk(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = gyfkListGrid.getSelecteds();
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
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                rowIds.push(r.gyfkId);
            }
            _SubmitJson({
                url: jsUseCtxPath + "/qualityfxgj/core/Gyfk/deleteGyfk.do",
                method: 'POST',
                showMsg:false,
                data: {ids: rowIds.join(',')},
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