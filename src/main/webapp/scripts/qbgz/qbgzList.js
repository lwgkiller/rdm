
$(function () {
    searchFrm();
});



function addQbgz() {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/QBBG/start.do";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (qbgzListGrid) {
                qbgzListGrid.reload()
            }
        }
    }, 1000);
}
function qbgzEdit(qbgzId,instId) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId+ "&qbgzId=" + qbgzId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (qbgzListGrid) {
                qbgzListGrid.reload()
            }
        }
    }, 1000);
}
function qbgzChange(qbgzId) {
    var action = "change";
    var url = jsUseCtxPath + "/Info/Qbgz/editPage.do?action=" + action + "&qbgzId=" + qbgzId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (qbgzListGrid) {
                qbgzListGrid.reload()
            }
        }
    }, 1000);
}

function qbgzDetail(qbgzId) {
    var action = "detail";
    var url = jsUseCtxPath + "/Info/Qbgz/editPage.do?action=" + action + "&qbgzId=" + qbgzId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (qbgzListGrid) {
                qbgzListGrid.reload()
            }
        }
    }, 1000);
}

function qbgzTask(taskId) {
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
                        if (qbgzListGrid) {
                            qbgzListGrid.reload();
                        }
                    }
                }, 1000);
            }
        }
    })
}

function removeQbgz(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = qbgzListGrid.getSelecteds();
    }
    if (rows.length <= 0) {
        mini.alert(qbgzList_name5);
        return;
    }
    mini.confirm(qbgzList_name6, qbgzList_name7, function (action) {
        if (action != 'ok') {
            return;
        } else {
            var rowIds = [];
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                rowIds.push(r.qbgzId);
            }
            _SubmitJson({
                url: jsUseCtxPath + "/Info/Qbgz/deleteQbgz.do",
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
    form.attr("action", jsUseCtxPath + "/Info/Qbgz/importTemplateDownload.do");
    $("body").append(form);
    form.submit();
    form.remove();
}

function moban() {
    mini.open({
        title: qbgzList_name8,
        url: jsUseCtxPath + "/Info/Qbgz/fileList.do",
        width: 1050,
        height: 550,
        allowResize: true,
        onload: function () {
            searchFrm();
        }
    });
}