
$(function () {
    searchFrm();
});


function addRjbg() {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/FDJRJBG/start.do";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (rjbgListGrid) {
                rjbgListGrid.reload()
            }
        }
    }, 1000);
}
function rjbgEdit(rjbgId,instId) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId+ "&rjbgId=" + rjbgId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (rjbgListGrid) {
                rjbgListGrid.reload()
            }
        }
    }, 1000);
}


function rjbgDetail(rjbgId,status) {
    var action = "detail";
    var url = jsUseCtxPath + "/environment/core/Rjbg/editPage.do?action=" + action + "&rjbgId=" + rjbgId+ "&status=" + status;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (rjbgListGrid) {
                rjbgListGrid.reload()
            }
        }
    }, 1000);
}

function rjbgTask(taskId) {
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
                        if (rjbgListGrid) {
                            rjbgListGrid.reload();
                        }
                    }
                }, 1000);
            }
        }
    })
}

function removeRjbg(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = rjbgListGrid.getSelecteds();
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
                rowIds.push(r.rjbgId);
            }
            _SubmitJson({
                url: jsUseCtxPath + "/environment/core/Rjbg/deleteRjbg.do",
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
function downImportTemplate() {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/environment/core/Rjbg/importTemplateDownload.do");
    $("body").append(form);
    form.submit();
    form.remove();
}
function sendmail() {
    $.ajax({
        url: jsUseCtxPath + '/environment/core/Rjbg/sendemail.do',
        type: 'post',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                mini.alert(data.message, "提示消息", function (action) {
                    if (action == 'ok') {
                        CloseWindow();
                    }
                });
            }
        }
    });

}