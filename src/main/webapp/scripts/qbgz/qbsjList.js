
$(function () {
    searchFrm();
});

function addQbsj() {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/QBSJ/start.do";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (qbsjListGrid) {
                qbsjListGrid.reload()
            }
        }
    }, 1000);
}
function qbsjEdit(qbgzId,instId) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId+ "&qbgzId=" + qbgzId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (qbsjListGrid) {
                qbsjListGrid.reload()
            }
        }
    }, 1000);
}

function qbsjDetail(qbgzId) {
    var action = "detail";
    var url = jsUseCtxPath + "/Info/Qbsj/editPage.do?action=" + action + "&qbgzId=" + qbgzId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (qbsjListGrid) {
                qbsjListGrid.reload()
            }
        }
    }, 1000);
}

function qbsjTask(taskId) {
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
                        if (qbsjListGrid) {
                            qbsjListGrid.reload();
                        }
                    }
                }, 1000);
            }
        }
    })
}

function removeQbsj(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = qbsjListGrid.getSelecteds();
    }
    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }
    mini.confirm("确定删除选中记录？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var ids = [];
            var instIds = [];
            var existStartInst = false;
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                if (r.status == 'DRAFTED') {
                    ids.push(r.id);
                    instIds.push(r.instId);
                } else if (r.status == 'DISCARD_END' && (currentUserNo == 'admin' || isQbzy)) {
                    ids.push(r.id);
                    instIds.push(r.instId);
                } else {
                    existStartInst = true;
                    continue;
                }
            }
            if (existStartInst) {
                alert("仅草稿状态数据可由本人删除");
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/Info/Qbsj/deleteQbsj.do",
                    method: 'POST',
                    showMsg: false,
                    data: {ids: ids.join(','), instIds: instIds.join(',')},
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
function downImportTemplate() {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/Info/Qbsj/importTemplateDownload.do");
    $("body").append(form);
    form.submit();
    form.remove();
}

function moban() {
    mini.open({
        title: "模板下载",
        url: jsUseCtxPath + "/Info/Qbsj/fileList.do",
        width: 1050,
        height: 550,
        allowResize: true,
        onload: function () {
            searchFrm();
        }
    });
}

function bigTypeValueChanged() {
    var bigTypeName = mini.get("bigTypeName").getValue();
    $.ajax({
        url: jsUseCtxPath + '/Info/Qbsj/querySmallTypeByBigType.do?bigTypeName=' + bigTypeName,
        type: 'post',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                mini.get("qbTypeId").setData(data);
            }
        }
    });
}

function myClearForm() {
    mini.get("qbTypeId").setData([]);
    clearForm();
}