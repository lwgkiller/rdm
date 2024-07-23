$(function () {
    searchFrm();

});

//新增流程（后台根据配置的表单进行跳转）
function addXpsx() {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/XPSXFXPG/start.do";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (xpsxListGrid) {
                xpsxListGrid.reload()
            }
        }
    }, 1000);
}

//编辑行数据流程（后台根据配置的表单进行跳转）
function xpsxEdit(xpsxId, instId) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (xpsxListGrid) {
                xpsxListGrid.reload()
            }
        }
    }, 1000);
}

//明细（直接跳转到详情的业务controller）
function xpsxDetail(xpsxId, status) {
    var action = "detail";
    var url = jsUseCtxPath + "/zhgl/core/fxpg/editPage.do?action=" + action + "&xpsxId=" + xpsxId + "&status=" + status;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (xpsxListGrid) {
                xpsxListGrid.reload()
            }
        }
    }, 1000);
}

//跳转到任务的处理界面
function xpsxTask(taskId) {
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
                        if (xpsxListGrid) {
                            xpsxListGrid.reload();
                        }
                    }
                }, 1000);
            }
        }
    })
}

function exportJsmm() {
    var parent = $(".search-form");
    var inputAry = $("input", parent);
    var params = [];
    inputAry.each(function (i) {
        var el = $(this);
        var obj = {};
        obj.name = el.attr("name");
        if (!obj.name) return true;
        obj.value = el.val();
        params.push(obj);
    });
    $("#filter").val(mini.encode(params));
    var excelForm = $("#excelForm");
    excelForm.submit();
}

//是否是专利工程师
function whetherIsZlgcs(userRoles) {
    if(!userRoles) {
        return false;
    }
    for (var i = 0; i < userRoles.length; i++) {
        if (userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-LEADER'||userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-BELONG') {
            if(userRoles[i].NAME_.indexOf('专利工程师')!=-1) {
                return true;
            }
        }
    }
    return false;
}




function removeXpsx(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = xpsxListGrid.getSelecteds();
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
                if (r.status == 'DRAFTED') {
                    rowIds.push(r.id);
                    instIds.push(r.instId);
                }else if (r.status == 'DISCARD_END' &&(currentUserNo=='admin'|| currentUserId ==r.CREATE_BY_)){
                    rowIds.push(r.id);
                    instIds.push(r.instId);
                }  else {
                    existStartInst = true;
                    continue;
                }
            }
            if (existStartInst) {
                alert("仅草稿状态和作废数据可由本人删除");
                return;
            }
            if (rowIds.length > 0){
                _SubmitJson({
                    url: jsUseCtxPath + "/zhgl/core/fxpg/deleteXpsx.do",
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
