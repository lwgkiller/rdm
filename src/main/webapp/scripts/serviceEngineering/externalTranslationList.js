$(function () {
    searchFrm();
});




function clickEdit(applyId, instId) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId + "&applyId=" + applyId ;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (externalTranslationList) {
                externalTranslationList.reload()
            }
        }
    }, 1000);
}


function clickDetail(applyId, status, instId) {
    var url = jsUseCtxPath + "/serviceEngineering/core/externalTranslation/applyEditPage.do?action=detail&applyId=" + applyId + "&status=" + status + "&instId=" + instId ;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (externalTranslationList) {
                externalTranslationList.reload()
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
                        if (externalTranslationList) {
                            externalTranslationList.reload();
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
        rows = externalTranslationList.getSelecteds();
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
                if ((r.status == 'DRAFTED' && r.CREATE_BY_ == currentUserId) || currentUserNo == 'admin') {
                    rowIds.push(r.id);
                    instIds.push(r.instId);
                }
                else {
                    existStartInst = true;
                    continue;
                }
            }
            if (existStartInst) {
                mini.alert("仅草稿状态数据可由本人删除");
            }

            if (rowIds.length > 0) {

                _SubmitJson({
                    url: jsUseCtxPath + "/serviceEngineering/core/externalTranslation/deleteApply.do",
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
