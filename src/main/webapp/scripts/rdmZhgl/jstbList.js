
$(function () {
    searchFrm();
});


function jstbSee(jstbId) {
    window.open(jsUseCtxPath + "/rdmZhgl/Jstb/see.do?jstbId=" + jstbId);
}
function moban() {
    mini.open({
        title: "模板下载",
        url: jsUseCtxPath + "/rdmZhgl/Jstb/fileList.do",
        width: 1050,
        height: 550,
        allowResize: true,
        onload: function () {
            searchFrm();
        }
    });
}
function addJstb() {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/JSGLTB/start.do";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (jstbListGrid) {
                jstbListGrid.reload()
            }
        }
    }, 1000);
}
function jstbEdit(jstbId,instId) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId+ "&jstbId=" + jstbId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (jstbListGrid) {
                jstbListGrid.reload()
            }
        }
    }, 1000);
}
function jstbChange(jstbId) {
    var action = "change";
    var url = jsUseCtxPath + "/rdmZhgl/Jstb/editPage.do?action=" + action + "&jstbId=" + jstbId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (jstbListGrid) {
                jstbListGrid.reload()
            }
        }
    }, 1000);
}
function jstbTask(taskId) {
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
                        if (jstbListGrid) {
                            jstbListGrid.reload();
                        }
                    }
                }, 1000);
            }
        }
    })
}

function removeJstb(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = jstbListGrid.getSelecteds();
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
                rowIds.push(r.jstbId);
            }
            _SubmitJson({
                url: jsUseCtxPath + "/rdmZhgl/Jstb/deleteJstb.do",
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
function cancelJstb(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = jstbListGrid.getSelecteds();
    }
    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }
    mini.confirm("确定作废？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var rowIds = [];
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                rowIds.push(r.jstbId);
            }
            _SubmitJson({
                url: jsUseCtxPath + "/rdmZhgl/Jstb/cancelJstb.do",
                method: 'POST',
                showMsg: false,
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