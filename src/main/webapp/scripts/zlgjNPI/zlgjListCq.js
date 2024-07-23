$(function () {
    if(create_startTimeStr){
        mini.get('create_startTime').setValue(create_startTimeStr);
    }
    if(create_endTimeStr){
        mini.get('create_endTime').setValue(create_endTimeStr);
    }
    if(deptNameStr){
        mini.get('deptName').setValue(deptNameStr);
        if(zrType=="第一责任人"){
            mini.get('ssbmId').setText(deptNameStr);
        }else if(zrType=="问题处理人"){
            mini.get('zrrDeptId').setText(deptNameStr);
        }else if(zrType=="流程处理人"){
            mini.get('currentProcessUserDeptId').setText(deptNameStr);
        }

    }
    if(type){
        mini.get('wtlx').setValue(type);
    }
    if (yzcd) {
        mini.get('yzcd').setValue(yzcd);
    }
    if(chartType=='1') {
        mini.get("jiean").setValue('no');
    }
    searchFrm();
    if (!isScwtUser && currentUserNo != 'admin' && type == 'SCWT') {
        mini.get("addZlgj").hide();
        mini.get("removeZlgj").hide();
    }
    if (!isHwwtUser && currentUserNo != 'admin' && type == 'HWWT') {
        mini.get("addZlgj").hide();
        mini.get("removeZlgj").hide();
    }
    if(type =='XPZDSY') {
        $("#tdmSylx_li").show();
    }
    if(type !='XPZDSY') {
        var tdmSylx = zlgjListGrid.getColumn('tdmSylx');
        zlgjListGrid.hideColumn(tdmSylx);
    }

});

//新增流程（后台根据配置的表单进行跳转）
function addZlgj() {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/ZLGJ/start.do?type=" + type;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (zlgjListGrid) {
                zlgjListGrid.reload();
            }
        }
    }, 1000);
}

//编辑行数据流程（后台根据配置的表单进行跳转）
function zlgjEdit(wtId, instId) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (zlgjListGrid) {
                zlgjListGrid.reload()
            }
        }
    }, 1000);
}
//变更
function zlgjChange(wtId, status) {
    var action = "change";
    var url = jsUseCtxPath + "/xjsdr/core/zlgj/editPage.do?action=" + action + "&wtId=" + wtId + "&status=" + status;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (zlgjListGrid) {
                zlgjListGrid.reload()
            }
        }
    }, 1000);
}
//明细（直接跳转到详情的业务controller）
function zlgjDetail(wtId, status) {
    var action = "detail";
    var url = jsUseCtxPath + "/xjsdr/core/zlgj/editPage.do?action=" + action + "&wtId=" + wtId + "&status=" + status;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (zlgjListGrid) {
                zlgjListGrid.reload()
            }
        }
    }, 1000);
}

//跳转到任务的处理界面
function zlgjTask(taskId) {
    $.ajax({
        url: jsUseCtxPath + '/bpm/core/bpmTask/checkTaskLockStatus.do?taskId=' + taskId,
        async: false,
        success: function (result) {
            if (!result.success) {
                top._ShowTips({
                    msg: result.message
                });
            } else {
                var url = jsUseCtxPath + '/bpm/core/bpmTask/toStart.do?taskId=' + taskId;
                var winObj = openNewWindow(url, "handTask");
                var loop = setInterval(function () {
                    if (!winObj) {
                        clearInterval(loop);
                    } else if (winObj.closed) {
                        clearInterval(loop);
                        if (zlgjListGrid) {
                            zlgjListGrid.reload();
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
    if (!userRoles) {
        return false;
    }
    for (var i = 0; i < userRoles.length; i++) {
        if (userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-LEADER' || userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-BELONG') {
            if (userRoles[i].NAME_.indexOf('专利工程师') != -1) {
                return true;
            }
        }
    }
    return false;
}


function removeZlgj(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = zlgjListGrid.getSelecteds();
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
                    rowIds.push(r.wtId);
                    instIds.push(r.instId);
                } else if ((r.status == 'DISCARD_END' && currentUserId == r.CREATE_BY_) || currentUserNo == 'admin') {
                    rowIds.push(r.wtId);
                    instIds.push(r.instId);
                } else {
                    existStartInst = true;
                    continue;
                }
            }
            if (existStartInst) {
                alert("仅草稿状态和作废数据可由本人删除");
            }
            if (rowIds.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/xjsdr/core/zlgj/deleteZlgj.do",
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
function copyZlgj(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = zlgjListGrid.getSelecteds();
    }
    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }
    var ifCopy = "yes";
    var wtId = rows[0].wtId;
    var url = jsUseCtxPath + "/bpm/core/bpmInst/ZLGJ/start.do?type=" + type + "&ifCopy=" + ifCopy + "&copyId=" + wtId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (zlgjListGrid) {
                zlgjListGrid.reload()
            }
        }
    }, 1000);
}

function exportWtsb() {
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


function callBackOut(wtId) {
    $.ajax({
        url: jsUseCtxPath + '/xjsdr/core/zlgj/callBackOut.do?wtId=' + wtId,
        async: false,
        success: function (result) {
            mini.alert(result.message);
        }
    })
}
