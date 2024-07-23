$(function () {
    searchFrm();
});

function addTask() {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/HWJDSYXFK/start.do";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (worldFtinessImproveList) {
                worldFtinessImproveList.reload()
            }
        }
    }, 1000);
}



function clickEdit(applyId, instId) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId + "&applyId=" + applyId ;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (worldFtinessImproveList) {
                worldFtinessImproveList.reload()
            }
        }
    }, 1000);
}




function clickDetail(applyId, status, instId) {
    var url = jsUseCtxPath + "/world/core/fitnessImprove/applyEditPage.do?action=detail&applyId=" + applyId + "&status=" + status + "&instId=" + instId ;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (worldFtinessImproveList) {
                worldFtinessImproveList.reload()
            }
        }
    }, 1000);
}

function clickTask(taskId) {
    $.ajax({
        url: jsUseCtxPath + '/bpm/core/bpmTask/checkTaskLockStatus.do?taskId=' + taskId ,
        async: false,
        success: function (result) {
            if (!result.success) {
                top._ShowTips({
                    msg: result.message
                });
            } else {
                var url = jsUseCtxPath + '/bpm/core/bpmTask/toStart.do?taskId=' + taskId ;
                var winObj = openNewWindow(url, "handTask");
                var loop = setInterval(function () {
                    if (!winObj) {
                        clearInterval(loop);
                    } else if (winObj.closed) {
                        clearInterval(loop);
                        if (worldFtinessImproveList) {
                            worldFtinessImproveList.reload();
                        }
                    }
                }, 1000);
            }
        }
    })
}

function clickRemove(record) {

    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = worldFtinessImproveList.getSelecteds();
    }
    if (rows.length <= 0) {
        mini.alert(worldFitnessImproveList_qzs);
        return;
    }
    mini.confirm(worldFitnessImproveList_qdsc, worldFitnessImproveList_ts, function (action) {
        if (action != 'ok') {
            return;
        } else {
            var rowIds = [];
            var instIds = [];
            var existStartInst = false;
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                if (r.status == 'DRAFTED' && (r.CREATE_BY_ == currentUserId || currentUserNo == 'admin')) {
                    rowIds.push(r.id);
                    instIds.push(r.instId);
                }
                else {
                    existStartInst = true;
                    continue;
                }
            }
            if (existStartInst) {
                mini.alert(worldFitnessImproveList_cgzt);
            }

            if (rowIds.length > 0) {

                _SubmitJson({
                    url: jsUseCtxPath + "/world/core/fitnessImprove/deleteApply.do",
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
