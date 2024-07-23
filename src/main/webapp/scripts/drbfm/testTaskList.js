$(function () {
    searchFrm();
});


//新增流程（后台根据配置的表单进行跳转）
function addTestTask() {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/drbfmTestTask/start.do";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (testTaskListGrid) {
                testTaskListGrid.reload()
            }
        }
    }, 1000);
}


//编辑行数据流程（后台根据配置的表单进行跳转）
function testTaskEdit(applyId, instId,testType) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?action=edit&instId=" + instId;
    if (testType=='常规附带') {
        url=jsUseCtxPath + "/drbfm/testTask/testTaskEditPage.do?action=edit&applyId=" + applyId +"&testType="+testType;
    }
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (testTaskListGrid) {
                testTaskListGrid.reload()
            }
        }
    }, 1000);
}

//明细（直接跳转到详情的业务controller）
function testTaskDetail(applyId, status,testType) {
    var action = "detail";
    var url = jsUseCtxPath + "/drbfm/testTask/testTaskEditPage.do?action=" + action + "&applyId=" + applyId + "&status=" + status+"&testType="+testType;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (testTaskListGrid) {
                testTaskListGrid.reload();
            }
        }
    }, 1000);
}

//跳转到任务的处理界面
function testTaskDo(taskId) {
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
                        if (testTaskListGrid) {
                            testTaskListGrid.reload();
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
        rows = testTaskListGrid.getSelecteds();
    }
    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }
    mini.confirm("确定删除选中记录(仅草稿/作废状态，或常规附带类型的可由创建者删除)？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var rowIds = [];
            var instIds = [];
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                if((r.status=='DRAFTED'||r.status=='DISCARD_END' || r.testType=='常规附带') && r.CREATE_BY_==currentUserId) {
                    rowIds.push(r.id);
                    if(r.instId) {
                        instIds.push(r.instId);
                    }
                }
            }

            if (rowIds.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/drbfm/testTask/deleteTestTask.do",
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