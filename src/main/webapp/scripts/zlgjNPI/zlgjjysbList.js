$(function () {
    searchFrm();
    if (!isGJJYZY && currentUserNo != 'admin') {
        mini.get("lockAddBtn").hide();
        mini.get("unlockAddBtn").hide();
    }
    if (currentUserNo != crmToZlgjjyAgent && currentUserNo != 'admin') {
        mini.get("callBackTest").hide();
        mini.get("zlshshyjMock").hide();
        zlgjjysbGrid.hideColumn("callBackResult");
    }
});

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

//新增
function addZlgjjysb() {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/ZLGJJYSB/start.do";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (zlgjjysbGrid) {
                zlgjjysbGrid.reload()
            }
        }
    }, 1000);
}

// 编辑
function editZlgjjysb(instId) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (zlgjjysbGrid) {
                zlgjjysbGrid.reload()
            }
        }
    }, 1000);
}

//删除
function delZlgjjysb(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = zlgjjysbGrid.getSelecteds();
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
                if (r.taskStatus == 'DRAFTED' || r.taskStatus == 'DISCARD_END' || currentUserNo == 'admin') {
                    ids.push(r.id);
                    instIds.push(r.instId);
                } else {
                    existStartInst = true;
                    continue;
                }
            }

            if (existStartInst) {
                alert("仅草稿或作废状态数据可由创建者本人删除");
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/zlgjNPI/core/zlgjjysb/deleteZlgjjysb.do",
                    method: 'POST',
                    data: {ids: ids.join(','), instIds: instIds.join(',')},
                    success: function (data) {
                        if (data) {
                            searchFrm();
                        }
                    }
                });
            }

        }
    });
}

//明细
function toZlgjjysbDetail(zlgjjysbId) {
    var action = "detail";
    var url = jsUseCtxPath + "/zlgjNPI/core/zlgjjysb/zlgjjysbEditPage.do?action=" + action + "&zlgjjysbId=" + zlgjjysbId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (zlgjjysbGrid) {
                zlgjjysbGrid.reload()
            }
        }
    }, 1000);
}

//跳转到任务的处理界面
function zlgjjysbTask(taskId) {
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
                        if (zlgjjysbGrid) {
                            zlgjjysbGrid.reload();
                        }
                    }
                }, 1000);
            }
        }
    })
}

//导出
function exportZlgjjysb() {
    var parent = $(".search-form");
    var inputAry = $("input", parent);
    var params = [];
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
    excelForm.submit();
}

