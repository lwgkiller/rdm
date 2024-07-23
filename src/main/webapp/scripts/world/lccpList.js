$(function () {
    searchFrm();
});


function addLccp() {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/LCCP/start.do";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (lccpListGrid) {
                lccpListGrid.reload()
            }
        }
    }, 1000);
}

function lccpEdit(lccpId,instId,status) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId+ "&lccpId=" + lccpId+"&status="+status;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (lccpListGrid) {
                lccpListGrid.reload()
            }
        }
    }, 1000);
}

function lccpDetail(lccpId) {
    var action = "detail";
    var url = jsUseCtxPath + "/Lccp/editPage.do?action=" + action + "&lccpId=" + lccpId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (lccpListGrid) {
                lccpListGrid.reload()
            }
        }
    }, 1000);
}
function lccpTask(taskId) {
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
                        if (lccpListGrid) {
                            lccpListGrid.reload();
                        }
                    }
                }, 1000);
            }
        }
    })
}
function removelccp(lccpId) {
    mini.confirm(lccpList_qdscxzjl, lccpList_ts, function (action) {
        if (action != 'ok') {
            return;
        } else {
            _SubmitJson({
                url: jsUseCtxPath + "/Lccp/deleteLccp.do",
                method: 'POST',
                showMsg:false,
                data: {id: lccpId},
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