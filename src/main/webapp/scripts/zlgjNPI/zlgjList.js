$(function () {
    if (create_startTime) {
        mini.get('create_startTime').setValue(create_startTime);
    }
    if (create_endTime) {
        mini.get('create_endTime').setValue(create_endTime);
    }
    if (deptName) {
        mini.get('ssbmId').setText(deptName);
        mini.get('deptName').setValue(deptName);
    }
    searchFrm();
    /*if (!isScwtUser && currentUserNo != 'admin' && wtlxtype == 'SCWT') {
        mini.get("addZlgj").hide();
        mini.get("removeZlgj").hide();
    }
    if (!isHwwtUser && currentUserNo != 'admin' && wtlxtype == 'HWWT') {
        mini.get("addZlgj").hide();
        mini.get("removeZlgj").hide();
    }*/
    if (wtlxtype == 'XPZDSY') {
        $("#tdmSylx_li").show();
    }
    if (wtlxtype != 'XPZDSY') {
        var tdmSylx = zlgjListGrid.getColumn('tdmSylx');
        zlgjListGrid.hideColumn(tdmSylx);
    }
    if (wtlxtype != 'LBJSY') {
        $("#lbjUl").hide();
        $("#zlgjUl").show();
        var componentCategory = zlgjListGrid.getColumn('componentCategory');
        var componentName = zlgjListGrid.getColumn('componentName');
        var componentModel = zlgjListGrid.getColumn('componentModel');
        var materialCode = zlgjListGrid.getColumn('materialCode');
        var testType = zlgjListGrid.getColumn('testType');
        var sampleType = zlgjListGrid.getColumn('sampleType');
        var machineModel = zlgjListGrid.getColumn('machineModel');
        var lbjgys = zlgjListGrid.getColumn('lbjgys');
        var testRounds = zlgjListGrid.getColumn('testRounds');
        var laboratory = zlgjListGrid.getColumn('laboratory');
        var testLeader = zlgjListGrid.getColumn('testLeader');
        var nonconformingDescription = zlgjListGrid.getColumn('nonconformingDescription');
        var improvementSuggestions = zlgjListGrid.getColumn('improvementSuggestions');
        var testConclusion = zlgjListGrid.getColumn('testConclusion');
        zlgjListGrid.hideColumn(nonconformingDescription);
        zlgjListGrid.hideColumn(improvementSuggestions);
        zlgjListGrid.hideColumn(testConclusion);
        zlgjListGrid.hideColumn(componentCategory);
        zlgjListGrid.hideColumn(componentName);
        zlgjListGrid.hideColumn(componentModel);
        zlgjListGrid.hideColumn(materialCode);
        zlgjListGrid.hideColumn(testType);
        zlgjListGrid.hideColumn(sampleType);
        zlgjListGrid.hideColumn(machineModel);
        zlgjListGrid.hideColumn(lbjgys);
        zlgjListGrid.hideColumn(testRounds);
        zlgjListGrid.hideColumn(laboratory);
        zlgjListGrid.hideColumn(testLeader);
    }else {
        $("#zlgjUl").hide();
        $("#lbjUl").show();
        var jiXing = zlgjListGrid.getColumn('jiXing');
        var smallJiXing = zlgjListGrid.getColumn('smallJiXing');
        var zjbh = zlgjListGrid.getColumn('zjbh');
        var gzlj = zlgjListGrid.getColumn('gzlj');
        var wtms = zlgjListGrid.getColumn('wtms');
        var sggk = zlgjListGrid.getColumn('sggk');
        zlgjListGrid.hideColumn(sggk);
        zlgjListGrid.hideColumn(zjbh);
        zlgjListGrid.hideColumn(gzlj);
        zlgjListGrid.hideColumn(wtms);
        zlgjListGrid.hideColumn(jiXing);
        zlgjListGrid.hideColumn(smallJiXing);
    }
});

//新增流程（后台根据配置的表单进行跳转）
function addZlgj() {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/ZLGJ/start.do?wtlxtype=" + wtlxtype;
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
        mini.alert(zlgjList_name6);
        return;
    }
    mini.confirm(zlgjList_name7, zlgjList_name8, function (action) {
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
                alert(zlgjList_name9);
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
        mini.alert(zlgjList_name6);
        return;
    }
    var ifCopy = "yes";
    var wtId = rows[0].wtId;
    var url = jsUseCtxPath + "/bpm/core/bpmInst/ZLGJ/start.do?wtlxtype=" + wtlxtype + "&ifCopy=" + ifCopy + "&copyId=" + wtId;
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
