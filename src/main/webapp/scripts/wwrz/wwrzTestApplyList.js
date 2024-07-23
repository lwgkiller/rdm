$(function () {
    searchFrm();
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
                if ((r.status == 'DRAFTED' || r.status == 'DISCARD_END') && r.CREATE_BY_ == currentUserId) {
                    ids.push(r.id);
                    instIds.push(r.instId);
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
                    url: jsUseCtxPath + "/wwrz/core/test/delete.do",
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
    var url = jsUseCtxPath + "/wwrz/core/test/edit.do?action=" + action + "&id=" + id + "&status=" + status;
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
    var url = jsUseCtxPath + "/bpm/core/bpmInst/WwrzTest/start.do";
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

//新增废止审批流程
function doEndApply() {
    var rows = applyListGrid.getSelecteds();
    if (rows.length != 1) {
        mini.alert("请选择一条需要废止的申请！");
        return;
    }
    if(rows[0].CREATE_BY_ != currentUserId && rows[0].productLeader != currentUserId ){
        mini.alert("只有产品主管或者创建人可以申请废止！");
        return;
    }
    var mainId = rows[0].id;
    var instStatus = rows[0].status;
    if(instStatus!='RUNNING'){
        mini.alert("请选择一条运行中的项目进行废止申请！");
        return;
    }
    //判断是否已有审批单
    let postData = {"mainId": mainId};
    let postUrl = jsUseCtxPath + '/wwrz/core/end/isApply.do';
    let resultData = ajaxRequest(postUrl, 'POST', false, postData);
    if (resultData && resultData.length > 0) {
        mini.alert("已提交废止申请，请不要重复提交！");
        return;
    }
    var url = jsUseCtxPath + "/bpm/core/bpmInst/WwrzEndApply/start.do?mainId="+mainId;
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
function exportExcel(){
    var params=[];
    var parent=$(".search-form");
    var inputAry=$("input",parent);
    inputAry.each(function(i){
        var el=$(this);
        var obj={};
        obj.name=el.attr("name");
        if(!obj.name) return true;
        obj.value=el.val();
        params.push(obj);
    });
    $("#filter").val(mini.encode(params));
    var excelForm = $("#excelForm");
    excelForm.submit();
}
