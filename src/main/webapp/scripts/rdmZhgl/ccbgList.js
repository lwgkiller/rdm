$(function () {
    // mini.get("editorUserId").setValue(currentUserId);
    // mini.get("editorUserId").setText(currentUserName);
    searchFrm();
});


//..（后台根据配置的表单进行跳转）
function addCcbg() {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/CCBG/start.do?";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (ccbgListGrid) {
                ccbgListGrid.reload()
            }
        }
    }, 1000);
}

//..编辑行数据流程（后台根据配置的表单进行跳转）
function ccbgEdit(ccbgId, instId) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (ccbgListGrid) {
                ccbgListGrid.reload()
            }
        }
    }, 1000);
}

//删除记录
function removeProject(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = projectListGrid.getSelecteds();
    }

    if (rows.length <= 0) {
        mini.alert(ccbgList_name5);
        return;
    }

    mini.confirm(ccbgList_name6, ccbgList_name7, function (action) {
        if (action != 'ok') {
            return;
        } else {
            //判断该行是否已经提交流程
            var ids = [];
            var instIds = [];
            var existStartInst = false;
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                if (r.status == 'DRAFTED') {
                    ids.push(r.projectId);
                    instIds.push(r.INST_ID_);
                } else if (r.status == 'DISCARD_END' && (currentUserInfo.userNo == 'admin' || whetherIsProjectManager(currentUserInfo.userRoles) || whetherIsProjectManagerNotZSZX(currentUserInfo.userRoles))) {
                    ids.push(r.projectId);
                    instIds.push(r.INST_ID_);
                } else {
                    existStartInst = true;
                    continue;
                }
            }
            if (existStartInst) {
                alert(ccbgList_name8);
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/xcmgProjectManager/core/xcmgProject/del.do",
                    method: 'POST',
                    data: {ids: ids.join(','), instIds: instIds.join(',')},
                    success: function (text) {
                        projectListGrid.reload();
                    }
                });
            }
        }
    });
}

//..明细（直接跳转到详情的业务controller）
function ccbgDetail(ccbgId, status) {
    var action = "detail";
    var url = jsUseCtxPath + "/zhgl/core/ccbg/editPage.do?action=" + action + "&ccbgId=" + ccbgId + "&status=" + status;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (ccbgListGrid) {
                ccbgListGrid.reload()
            }
        }
    }, 1000);
}

//..跳转到任务的处理界面
function ccbgTask(taskId) {
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
                        if (ccbgListGrid) {
                            ccbgListGrid.reload();
                        }
                    }
                }, 1000);
            }
        }
    })
}

//..
function removeCcbg(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = ccbgListGrid.getSelecteds();
    }
    if (rows.length <= 0) {
        mini.alert(ccbgList_name5);
        return;
    }
    mini.confirm(ccbgList_name6, ccbgList_name7, function (action) {
        if (action != 'ok') {
            return;
        } else {
            var rowIds = [];
            var instIds = [];
            var haveSomeRowsWrong = false;
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                if (r.status == 'DRAFTED' && r.CREATE_BY_ == currentUserId) {
                    rowIds.push(r.id);
                    instIds.push(r.INST_ID_);
                } else if (currentUserNo == 'admin') {
                    rowIds.push(r.id);
                    instIds.push(r.INST_ID_);
                }
                else {
                    haveSomeRowsWrong = true;
                    break;
                }
            }
            if (haveSomeRowsWrong) {
                mini.alert(ccbgList_name9);
                return;
            }
            _SubmitJson({
                url: jsUseCtxPath + "/zhgl/core/ccbg/deleteCcbg.do",
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
    });
}