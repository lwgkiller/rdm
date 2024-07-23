$(function () {
    mini.get("#typeOfWrongPart").load(cjTypes);
    mini.get("businessStatus").setData(mini.decode(nodeSetListWithName));
    searchFrm();
});
//..
function cjlxRenderer(e) {
    var record = e.record;
    return $.formatItemValue(cjTypes, record.typeOfWrongPart);
}
//..废弃
function statusRenderer(e) {
    var record = e.record;
    var status = record.status;
    return $.formatItemValue(cjStatus, status);
}
//..废弃
function isPostponeRenderer_obsolete(e) {
    var record = e.record;
    var str = "";
    if (!record.zrbmReceiptTime) {
        str += "<span class='green'>否</span>";
    } else if (record.zrbmReceiptTime && !record.gdReceiptTime) {
        var now = new Date().getTime();
        var timeDay = (now - record.zrbmReceiptTime) / (24 * 3600 * 1000);
        if (timeDay < 3) {
            str += "<span class='green'>否</span>";
        } else {
            str += "<span class='red'>是</span>";
        }
    } else {
        str += "<span class='green'>否</span>";
    }
    return str
}
//..
function isPostponeRenderer(e) {
    var record = e.record;
    var str = "";
    if (!record.promiseTime || !record.actualTime) {
        str += "<span class='green'>否</span>";
    } else {
        var actualTime = new Date(Date.parse(record.actualTime));
        var promiseTime = new Date(Date.parse(record.promiseTime));
        if (actualTime <= promiseTime) {
            str += "<span class='green'>否</span>";
        } else {
            str += "<span class='red'>是</span>";
        }
    }
    return str
}
//..
function isDelayRenderer(e) {
    if (e.value != null && e.value != "") {
        debugger;
        if (e.record.currentNodeBeginTime != null && e.record.currentNodeBeginTime != "" && e.record.businessStatus != "Z" &&
            new Date().subtract(e.record.currentNodeBeginTime) > 2 && e.record.status != 'DISCARD_END') {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;color: #ef1b01" >' + e.value + '</span>';
        }
        else {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
        }
        return html;
    }
}
//..
function taskStatusRenderer(e) {
    var record = e.record;
    var taskStatus = record.taskStatus;
    var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
        {'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
        {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},
        {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
        {'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},
        {'key': 'PENDING', 'value': '挂起', 'css': 'gray'}
    ];
    return $.formatItemValue(arr, taskStatus);
}
//..
function addCjzg() {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/CJZG/start.do";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (wrongPartsListGrid) {
                wrongPartsListGrid.reload()
            }
        }
    }, 1000);
}
//..
function cjzgEdit(cjzgId, instId) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (wrongPartsListGrid) {
                wrongPartsListGrid.reload()
            }
        }
    }, 1000);
}
//..
function cjzgDetail(cjzgId, taskStatus) {
    var action = "detail";
    var url = jsUseCtxPath + "/serviceEngineering/core/wrongParts/editPage.do?action=" + action + "&cjzgId=" + cjzgId + "&taskStatus=" + taskStatus;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (wrongPartsListGrid) {
                wrongPartsListGrid.reload()
            }
        }
    }, 1000);
}
//..
function cjzgTask(taskId) {
    $.ajax({
        url: jsUseCtxPath + '/bpm/core/bpmTask/checkTaskLockStatus.do?taskId=' + taskId,
        async: false,
        success: function (result) {
            if (!result.success) {
                top._ShowTips({
                    msg: result.message
                });
            } else {
                var url = jsUseCtxPath + '/bpm/core/bpmTask/toStart.do?taskId=' + taskId;
                var winObj = openNewWindow(url, "handTask");
                var loop = setInterval(function () {
                    if (!winObj) {
                        clearInterval(loop);
                    } else if (winObj.closed) {
                        clearInterval(loop);
                        if (wrongPartsListGrid) {
                            wrongPartsListGrid.reload();
                        }
                    }
                }, 1000);
            }
        }
    })
}
//..
function removeCjzg(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = wrongPartsListGrid.getSelecteds();
    }
    if (rows.length <= 0) {
        mini.alert(wrongPartsList_name3);
        return;
    }
    mini.confirm(wrongPartsList_name4, wrongPartsList_name5, function (action) {
        if (action != 'ok') {
            return;
        } else {
            var ids = [];
            var instIds = [];
            var existStartInst = false;
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                if (r.taskStatus == 'DRAFTED' || currentUserId == '1') {
                    ids.push(r.id);
                    instIds.push(r.instId);
                } else {
                    existStartInst = true;
                    continue;
                }
            }
            if (existStartInst) {
                alert(wrongPartsList_name8);
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/serviceEngineering/core/wrongParts/deleteData.do",
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
//..todo
function exportReview() {
    var params = [];
    var parent = $(".search-form");
    var inputAry = $("input", parent);
    inputAry.each(function (i) {
        var el = $(this);
        var obj = {};
        obj.name = el.attr("name");
        if (!obj.name) return true;
        obj.value = el.val();
        params.push(obj);
    });
    $("#filter").val(mini.encode(params));
    var excelForm = $("#excelForm");
    var url = jsUseCtxPath + "/serviceEngineering/core/wrongParts/exportWrongParts.do";
    excelForm.attr("action", url);
    excelForm.submit();
}