$(function () {
    mini.get("smallTypeId").setData(smallTypeList);
    searchFrm();
});

//新增流程（后台根据配置的表单进行跳转）
function addZlgj() {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/GYSWTGJ/start.do?type="+type;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (zlgjListGrid) {
                zlgjListGrid.reload();
            }
        }
    }, 1000);
}

//编辑行数据流程（后台根据配置的表单进行跳转）
function zlgjEdit(wtId, instId) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (zlgjListGrid) {
                zlgjListGrid.reload()
            }
        }
    }, 1000);
}

//明细（直接跳转到详情的业务controller）
function zlgjDetail(wtId, status) {
    var action = "detail";
    var url = jsUseCtxPath + "/zlgjNPI/core/gyswt/editPage.do?action=" + action + "&wtId=" + wtId + "&status=" + status;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (zlgjListGrid) {
                zlgjListGrid.reload()
            }
        }
    }, 1000);
}

//跳转到任务的处理界面
function zlgjTask(taskId) {
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
                        if (zlgjListGrid) {
                            zlgjListGrid.reload();
                        }
                    }
                }, 1000);
            }
        }
    })
}

function removeZlgj(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = zlgjListGrid.getSelecteds();
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
                if ((r.status == 'DRAFTED'||r.status == 'DISCARD_END')&&(currentUserNo=='admin'|| currentUserId ==r.CREATE_BY_)) {
                    rowIds.push(r.wtId);
                    instIds.push(r.instId);
                } else {
                    existStartInst = true;
                    continue;
                }
            }
            if (existStartInst) {
                mini.alert("仅草稿和作废状态的数据可删除！");
            }
            if (rowIds.length > 0){
                _SubmitJson({
                    url: jsUseCtxPath + "/zlgjNPI/core/gyswt/deleteZlgj.do",
                    method: 'POST',
                    showMsg:false,
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
