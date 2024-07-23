$(function () {
    searchFrm();
});


//新增流程（后台根据配置的表单进行跳转）
function addTotal() {
    var url = jsUseCtxPath + "/drbfm/total/totalDecomposePage.do";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (singleListGrid) {
                singleListGrid.reload();
            }
        }
    }, 1000);
}

//编辑行数据流程（后台根据配置的表单进行跳转）
function devTaskEdit(applyId, instId) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (singleListGrid) {
                singleListGrid.reload()
            }
        }
    }, 1000);
}

//明细（直接跳转到详情的业务controller）
function singleDetail(applyId, status) {
    var action = "detail";
    var url = jsUseCtxPath + "/drbfm/single/singleFramePage.do?action=" + action + "&singleId=" + applyId + "&status=" + status;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (singleListGrid) {
                singleListGrid.reload();
            }
        }
    }, 1000);
}

//跳转到任务的处理界面
function devTaskDo(taskId) {
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
                        if (singleListGrid) {
                            singleListGrid.reload();
                        }
                    }
                }, 1000);
            }
        }
    })
}

function removeApply(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = singleListGrid.getSelecteds();
    }
    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }
    mini.confirm("确定删除选中记录（仅作废状态且不包含“已完成”验证任务的可以删除）？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var rowIds = [];
            var instIds = [];
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                if(r.instStatus=='DISCARD_END' && r.CREATE_BY_==currentUserId) {
                    rowIds.push(r.id);
                    instIds.push(r.instId);
                }
            }

            if (rowIds.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/drbfm/single/deleteSingles.do",
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

function exportSingle() {
    var selectedRows=singleListGrid.getSelecteds();
    if(!selectedRows||selectedRows.length<=0) {
        mini.alert("请选择至少一条记录！");
        return;
    }
    var params = [];
    for(var index=0;index<selectedRows.length;index++) {
        params.push(selectedRows[index].id);
    }

    $("#filter").val(mini.encode(params));
    var excelForm = $("#excelForm");
    excelForm.submit();
}