$(function () {
    searchFrm();
});
//新增流程（后台根据配置的表单进行跳转）
function addKjlw() {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/KJLW/start.do";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (kjlwListGrid) {
                kjlwListGrid.reload()
            }
        }
    }, 1000);
}
//编辑行数据流程（后台根据配置的表单进行跳转）
function kjlwEdit(rjzzId, instId) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (kjlwListGrid) {
                kjlwListGrid.reload()
            }
        }
    }, 1000);
}
//明细（直接跳转到详情的业务controller）
function kjlwDetail(kjlwId,status,writerId) {
    var action = "detail";
    var url = jsUseCtxPath + "/zhgl/core/kjlw/editPage.do?action=" + action + "&kjlwId=" + kjlwId + "&status=" + status +"&writerId=" + writerId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (kjlwListGrid) {
                kjlwListGrid.reload()
            }
        }
    }, 1000);
}
//跳转到任务的处理界面
function kjlwTask(taskId) {
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
                        if (kjlwListGrid) {
                            kjlwListGrid.reload();
                        }
                    }
                }, 1000);
            }
        }
    })
}

function exportKjlw() {
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

function removeKjlw(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = kjlwListGrid.getSelecteds();
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
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                rowIds.push(r.kjlwId);
                instIds.push(r.instId);
            }

            _SubmitJson({
                url: jsUseCtxPath + "/zhgl/core/kjlw/deleteKjlw.do",
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
    });
}