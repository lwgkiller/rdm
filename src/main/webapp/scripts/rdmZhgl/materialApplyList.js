$(function () {
    searchFrm();
    if(currentDay=='1'){
        mini.get('deptApply').setEnabled(false);
    }
});

//编辑行数据流程（后台根据配置的表单进行跳转）
function editApplyRow(applyId, instId) {
    var action = "detail";
    var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (applyListGrid) {
                applyListGrid.reload()
            }
            ;
        }
    }, 1000);
}

//删除记录
function removeApply(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = applyListGrid.getSelecteds();
    }

    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }

    mini.confirm("确定删除选中记录？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            //判断该行是否已经提交流程
            var ids = [];
            var instIds = [];
            var existStartInst = false;
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                if ((r.instStatus == 'DRAFTED' || r.instStatus == 'DISCARD_END') && r.CREATE_BY_ == currentUserId) {
                    ids.push(r.id);
                    instIds.push(r.INST_ID_);
                } else {
                    existStartInst = true;
                    continue;
                }
            }
            if (existStartInst) {
                alert("仅允许本人删除草稿或废止状态的数据");
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/rdmZhgl/core/material/delete.do",
                    method: 'POST',
                    data: {ids: ids.join(','), instIds: instIds.join(',')},
                    success: function (text) {
                        if (applyListGrid) {
                            applyListGrid.reload();
                        }
                    }
                });
            }
        }
    });
}

//跳转到任务的处理界面
function doApplyTask(taskId) {
    $.ajax({
        url: jsUseCtxPath + '/bpm/core/bpmTask/checkTaskLockStatus.do?taskId=' + taskId,
        success: function (result) {
            if (!result.success) {
                top._ShowTips({
                    msg: result.message
                });
            } else {
                var url = jsUseCtxPath + '/bpm/core/bpmTask/toStart.do?taskId=' + taskId;
                var winObj = window.open(url);
                var loop = setInterval(function () {
                    if (winObj.closed) {
                        clearInterval(loop);
                        if (applyListGrid) {
                            applyListGrid.reload()
                        }
                        ;
                    }
                }, 1000);
            }
        }
    })
}

//明细 的点击查看方法
function detailApply(id, status) {
    var action = "detail";
    var url = jsUseCtxPath + "/rdmZhgl/core/material/edit.do?action=" + action + "&id=" + id + "&status=" + status;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (applyListGrid) {
                applyListGrid.reload()
            }
            ;
        }
    }, 1000);
}

//新增提报审批流程
function doApply() {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/materialApply/start.do";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (applyListGrid) {
                applyListGrid.reload();
            }
        }
    }, 1000);
}
