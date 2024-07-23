$(function () {
    searchFrm();
});
//新增流程（后台根据配置的表单进行跳转）
function addJszblc() {
    var url='';
    if(type=='招标流程') {
        url = jsUseCtxPath + "/bpm/core/bpmInst/JSZB-ZBLC/start.do?type="+ type;
    } else if(type=='询比价流程') {
        url = jsUseCtxPath + "/bpm/core/bpmInst/JSZB-XBJLC/start.do?type="+ type;
    } else if(type=='特批流程') {
        url = jsUseCtxPath + "/bpm/core/bpmInst/JSZB-TPLC/start.do?type="+ type;
    }
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (jszbListGrid) {
                jszbListGrid.reload()
            }
        }
    }, 1000);
}

//编辑行数据流程（后台根据配置的表单进行跳转）
function jszbEdit(jszbId, instId) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId+"&type="+type;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (jszbListGrid) {
                jszbListGrid.reload()
            }
        }
    }, 1000);
}
//明细（直接跳转到详情的业务controller）
function jszbDetail(jszbId,status) {
    var action = "detail";
    var url = jsUseCtxPath + "/zhgl/core/jszb/editPage.do?action=" + action + "&jszbId=" + jszbId + "&status=" + status+"&type="+type;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (jszbListGrid) {
                jszbListGrid.reload()
            }
        }
    }, 1000);
}

function jszbChange(jszbId) {
    var action = "change";
    var url = jsUseCtxPath + "/zhgl/core/jszb/editPage.do?action=" + action + "&jszbId=" + jszbId + "&type="+type;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (jszbListGrid) {
                jszbListGrid.reload()
            }
        }
    }, 1000);
}

//跳转到任务的处理界面
function jszbTask(taskId) {
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
                        if (jszbListGrid) {
                            jszbListGrid.reload();
                        }
                    }
                }, 1000);
            }
        }
    })
}

function removeJszb(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = jszbListGrid.getSelecteds();
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
                    rowIds.push(r.jszbId);
                    instIds.push(r.instId);
                }else if (r.status == 'DISCARD_END' &&(currentUserNo=='admin'|| currentUserId ==r.CREATE_BY_)){
                    rowIds.push(r.jszbId);
                    instIds.push(r.instId);
                }else {
                    existStartInst = true;
                    continue;
                }
            }
            if (existStartInst) {
                alert("仅草稿状态和作废状态的数据可由本人或管理员删除");
            }
            if (rowIds.length > 0){
                _SubmitJson({
                    url: jsUseCtxPath + "/zhgl/core/jszb/deleteJszb.do",
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

function jszbClearForm() {
    mini.get("zbbmName").setValue("");
    mini.get("jbrName").setValue("");
    mini.get("zbName").setValue("");
    mini.get("xmNum").setValue("");
    mini.get("status").setValue("");
    mini.get("lxTimeStartTime").setValue("");
    mini.get("lxTimeEndTime").setValue("");
    searchFrm();
}

function exportJszb() {
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