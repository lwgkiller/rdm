$(function () {
searchFrm();
});



function addNewClhb() {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/HBXXGK/start.do?type=new";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (wjListGrid) {
                wjListGrid.reload()
            }
        }
    }, 1000);
}
function addOldClhb() {
    var selectRow = wjListGrid.getSelected();
    if (!selectRow) {
        mini.alert("请至少选中一条记录");
        return;
    }
    var wjId = selectRow.wjId;
    var url = jsUseCtxPath + "/bpm/core/bpmInst/HBXXGK/start.do?type=old&oldWjId="+wjId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (wjListGrid) {
                wjListGrid.reload()
            }
        }
    }, 1000);
}
function clhbEdit(wjId,instId) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId+ "&wjId=" + wjId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (wjListGrid) {
                wjListGrid.reload()
            }
        }
    }, 1000);
}
function clhbChangeEdit(wjId,instId) {
    var url = jsUseCtxPath + "/environment/core/Wj/edit.do?action=change&wjId=" + wjId+"&status=" + status;
    var winObj = window.open(jsUseCtxPath + "/rdm/core/noFlowFormIframe?url="+encodeURIComponent(url));
    //    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (wjListGrid) {
                wjListGrid.reload()
            }
        }
    }, 1000);
}
function clhbDetail(wjId,status) {
    var action = "detail";
    var url = jsUseCtxPath + "/environment/core/Wj/edit.do?action="+action+"&wjId=" + wjId+"&status=" + status;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (wjListGrid) {
                wjListGrid.reload()
            }
        }
    }, 1000);
}

function clhbFile(wjId) {
    mini.open({
        title: "附件列表",
        url: jsUseCtxPath + "/environment/core/Wj/fileList.do?&wjId=" + wjId,
        width: 1050,
        height: 550,
        allowResize: true,
        onload: function () {
            wjListGrid.reload();
        }
    });
}

function clhbTask(taskId) {
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
                        if (wjListGrid) {
                            wjListGrid.reload();
                        }
                    }
                }, 1000);
            }
        }
    })
}

function removeclhb(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = wjListGrid.getSelecteds();
    }
    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }
    mini.confirm("确定删除选中记录？", "提示", function (action) {
        if (action != 'ok') {
            return;
        }else {
            var rowIds = [];
            var instIds = [];
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                rowIds.push(r.wjId);
                instIds.push(r.instId);
            }
            if (rowIds.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/environment/core/Wj/deletewj.do",
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