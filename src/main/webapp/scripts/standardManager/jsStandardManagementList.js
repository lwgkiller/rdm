$(function () {
    searchFrm();
});

//新增标准申请
function addApply() {

    var title = jsStandardManagementList_jsbzdt;

    var url = jsUseCtxPath + "/bpm/core/bpmInst/JSBZDTGL/start.do";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (standardListGrid) {
                standardListGrid.reload()
            }
            ;
        }
    }, 1000);
    // if (isZJConform) {
    // } else {
    //     mini.alert(jsStandardManagementList_ZJConform);
    //     return;
    // }
}

//编辑行数据流程（后台根据配置的表单进行跳转）
function editApplyRow(applyId, instId) {
    var title = jsStandardManagementList_bjjs;
    var width = getWindowSize().width;
    var height = getWindowSize().height;
    _OpenWindow({
        url: jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId,
        title: title,
        width: width,
        height: height,
        showMaxButton: true,
        showModal: true,
        allowResize: true,
        ondestroy: function (action) {
            if (standardListGrid) {
                standardListGrid.reload();
            }
        }
    });
}

//删除记录
function removeApply(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = standardListGrid.getSelecteds();
    }

    if (rows.length <= 0) {
        mini.alert(jsStandardManagementList_qzs);
        return;
    }

    mini.confirm(jsStandardManagementList_qdsc, jsStandardManagementList_ts, function (action) {
        if (action != 'ok') {
            return;
        } else {
            //判断该行是否已经提交流程
            var ids = [];
            var instIds = [];
            var existStartInst = false;
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                if (r.instStatus == 'DRAFTED' && r.CREATE_BY_ == currentUserId) {
                    ids.push(r.id);
                    instIds.push(r.instId);
                } else {
                    existStartInst = true;
                    continue;
                }
            }
            if (existStartInst) {
                alert(jsStandardManagementList_yyxbr);
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/standardManager/core/standardManagement/jsStandardDelete.do",
                    method: 'POST',
                    data: {ids: ids.join(','), instIds: instIds.join(',')},
                    success: function (text) {
                        if (standardListGrid) {
                            standardListGrid.reload();
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
                        if (standardListGrid) {
                            standardListGrid.reload()
                        }
                        ;
                    }
                }, 1000);
            }
        }
    })
}

//明细
function detailApplyRow(feedbackId, status) {
    var action = "detail";
    var url = jsUseCtxPath + "/standardManager/core/standardDemand/jsDemandEdit.do?action=" + action + "&formId=" + feedbackId + "&status=" + status;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (standardListGrid) {
                standardListGrid.reload()
            }
            ;
        }
    }, 1000);
}

//明细
function detailStandardRow(feedbackId, status) {
    var action = "detail";
    var url = jsUseCtxPath + "/standardManager/core/standardManagement/jsStandardManagementEdit.do?action=" + action + "&formId=" + feedbackId + "&status=" + status;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (standardListGrid) {
                standardListGrid.reload()
            }
            ;
        }
    }, 1000);
}

$(function () {
    // @mh 放开新增权限
    // if(!isBzrzs && !canAddApply && currentUserNo != 'admin'){
    //     mini.get("bzxdAddApply").hide();
    // }
    if (currentUserNo != 'admin') {
        mini.get("bzxdRemoveApply").hide();
    }
});



