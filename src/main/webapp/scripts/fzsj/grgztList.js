$(function () {
    searchFrm();
});

function taskResourceRenderer(e) {
    var record = e.record;
    var taskResource = record.taskResource;
    var arr = [{key:'xpyf',value:'新品研发'},{key:'zyOrYycp',value:'在研/预研产品'},
        {key:'scgj',value:'市场改进'},{key:'hxjsyj',value:'核心技术研究'}];
    return $.formatItemValue(arr,taskResource);
}

function prototypeStateRenderer(e) {
    var record = e.record;
    var prototypeState = record.prototypeState;
    var arr = [{key:'yyj',value:'有样机'},{key:'wyj',value:'无样机'}];
    return $.formatItemValue(arr,prototypeState);
}

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
    return $.formatItemValue(arr,taskStatus);
}

//新增
function addGrgzt() {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/FZSJ/start.do";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (grgztGrid) {
                grgztGrid.reload()
            }
        }
    }, 1000);
}

// 编辑
function editGrgzt(instId) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (grgztGrid) {
                grgztGrid.reload()
            }
        }
    }, 1000);
}

//删除
function delGrgzt(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = grgztGrid.getSelecteds();
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
                if (r.taskStatus == 'DRAFTED' || r.taskStatus == 'DISCARD_END') {
                    ids.push(r.id);
                    instIds.push(r.instId);
                } else {
                    existStartInst = true;
                    continue;
                }
            }
            if (existStartInst) {
                alert("仅草稿态数据可删除");
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/fzsj/core/fzsj/deleteFzsj.do",
                    method: 'POST',
                    data: {ids: ids.join(','),instIds: instIds.join(',')},
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
function toGrgztDetail(fzsjId) {
    var action = "detail";
    var url = jsUseCtxPath + "/fzsj/core/fzsj/fzsjEditPage.do?action=" + action + "&fzsjId=" + fzsjId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (grgztGrid) {
                grgztGrid.reload()
            }
        }
    }, 1000);
}

//跳转到任务的处理界面
function grgztTask(taskId) {
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
                        if (grgztGrid) {
                            grgztGrid.reload();
                        }
                    }
                }, 1000);
            }
        }
    })
}

