$(function () {
    searchFrm();

});

// 编辑
function editManageImprove(instId) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (manageImproveGrid) {
                manageImproveGrid.reload()
            }
        }
    }, 1000);
}

//新增
function addManageImproveTb(){
    var url = jsUseCtxPath + "/bpm/core/bpmInst/GLGJJYTB/start.do";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (manageImproveGrid) {
                manageImproveGrid.reload()
            }
        }
    }, 1000);
}

//删除
function delManageImproveTb(record){
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = manageImproveGrid.getSelecteds();
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
                    url: jsUseCtxPath + "/zlgjNPI/core/manageImprove/deleteManageImprove.do",
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
function manageImproveDetail(manageImproveId){
    var action = "detail";
    var url = jsUseCtxPath + "/zlgjNPI/core/manageImprove/manageImproveEditPage.do?action=" + action + "&manageImproveId=" + manageImproveId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (manageImproveGrid) {
                manageImproveGrid.reload()
            }
        }
    }, 1000);
}
//跳转到任务的处理界面
function manageImproveTask(taskId){
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
                        if (manageImproveGrid) {
                            manageImproveGrid.reload();
                        }
                    }
                }, 1000);
            }
        }
    })

}

//删除
function delmanageImprove(record){
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = manageImproveGrid.getSelecteds();
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
                    url: jsUseCtxPath + "/zlgjNPI/core/manageImprove/deleteManageImprove.do",
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
