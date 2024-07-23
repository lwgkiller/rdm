$(function () {
    searchFrm();
});

function addApply() {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/BZZXXZC/start.do";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (doCheckGridList) {
                doCheckGridList.reload();
            }
        }
    }, 1000);
}


function removeApply(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = doCheckGridList.getSelecteds();
    }
    if (rows.length <= 0) {
        mini.alert(standardDoCheckApplyList_qzsxz);
        return;
    }
    mini.confirm(standardDoCheckApplyList_qdsc, standardDoCheckApplyList_ts, function (action) {
        if (action != 'ok') {
            return;
        } else {
            var rowIds = [];
            var instIds = [];
            var existStartInst = false;
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                if(r.status=='DRAFTED' && (r.CREATE_BY_==currentUserId ||currentUserNo=='admin')) {
                    rowIds.push(r.id);
                    instIds.push(r.instId);
                }
                else {
                    existStartInst = true;
                    continue;
                }
            }
            if (existStartInst) {
                mini.alert(standardDoCheckApplyList_jcgzt);
            }
            if (rowIds.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/standard/core/doCheck/deleteApply.do",
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

function doCheckEdit(applyId,instId) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId+ "&applyId=" + applyId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (doCheckGridList) {
                doCheckGridList.reload()
            }
        }
    }, 1000);
}


function doCheckDetail(applyId,status) {
    //判断是否有权限查看
    $.ajax({
        url: jsUseCtxPath + '/standard/core/doCheck/judgeCanSee.do?id=' + applyId,
        async:false,
        success: function (result) {
            if (!result.success) {
                mini.alert(standardDoCheckApplyList_wckqx);
            } else {
                var action = "detail";
                var url = jsUseCtxPath + "/standard/core/doCheck/applyEditPage.do?action=detail&applyId=" + applyId+"&status="+status;
                var winObj = window.open(url);
                var loop = setInterval(function () {
                    if (winObj.closed) {
                        clearInterval(loop);
                        if (doCheckGridList) {
                            doCheckGridList.reload()
                        }
                    }
                }, 1000);
            }
        }
    });


}

function doCheckTask(taskId) {
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
                        if (doCheckGridList) {
                            doCheckGridList.reload();
                        }
                    }
                }, 1000);
            }
        }
    })
}

function exportStandardDoCheckList(){
    var queryParam = [];
    var standardNumber = $.trim(mini.get("standardNumber").getValue());
    if (standardNumber) {
        queryParam.push({name: "standardNumber", value: standardNumber});
    }
    var standardName = $.trim(mini.get("standardName").getValue());
    if (standardName) {
        queryParam.push({name: "standardName", value: standardName});
    }
    var firstWriterName = $.trim(mini.get("firstWriterName").getValue());
    if (firstWriterName) {
        queryParam.push({name: "firstWriterName", value: firstWriterName});
    }
    var djrUserName = $.trim(mini.get("djrUserName").getValue());
    if (djrUserName) {
        queryParam.push({name: "djrUserName", value: djrUserName});
    }
    var createYear = $.trim(mini.get("createYear").getValue());
    if (createYear) {
        queryParam.push({name: "createYear", value: createYear});
    }
    var zcStatus = $.trim(mini.get("zcStatus").getValue());
    if (zcStatus) {
        queryParam.push({name: "zcStatus", value: zcStatus});
    }
    var checkResult = $.trim(mini.get("checkResult").getValue());
    if (checkResult) {
        queryParam.push({name: "checkResult", value: checkResult});
    }
    var status = $.trim(mini.get("status").getValue());
    if (status) {
        queryParam.push({name: "status", value: status});
    }
    $("#filter").val(mini.encode(queryParam));
    var excelForm = $("#excelForm");
    excelForm.submit();
}
