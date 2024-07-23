$(function () {
    searchFrm();
});


function addCkdd() {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/CKDD/start.do";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (ckddListGrid) {
                ckddListGrid.reload()
            }
        }
    }, 1000);
}

function ckddEdit(ckddId,instId,status) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId+ "&ckddId=" + ckddId+"&status="+status;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (ckddListGrid) {
                ckddListGrid.reload()
            }
        }
    }, 1000);
}

function ckddDetail(ckddId) {
    var action = "detail";
    var url = jsUseCtxPath + "/Ckdd/editPage.do?action=" + action + "&ckddId=" + ckddId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (ckddListGrid) {
                ckddListGrid.reload()
            }
        }
    }, 1000);
}
function ckddTask(taskId) {
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
                        if (ckddListGrid) {
                            ckddListGrid.reload();
                        }
                    }
                }, 1000);
            }
        }
    })
}
function removeCkdd(ckddId) {
    mini.confirm(ckddList_qdbg, ckddList_ts, function (action) {
        if (action != 'ok') {
            return;
        } else {
            _SubmitJson({
                url: jsUseCtxPath + "/Ckdd/deleteCkdd.do",
                method: 'POST',
                showMsg:false,
                data: {id: ckddId},
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