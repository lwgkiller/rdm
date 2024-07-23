var currentUserInfo = "";
var projectCategoryData="";
$(function () {
    $.ajax({
        url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/getProjectEditRule.do',
        type: 'get',
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                mini.get("#projectSource").load(data.source);
                mini.get("#projectCategory").load(data.category);
                projectCategoryData=data.category;
                mini.get("#projectLevel").load(data.level);
                currentUserInfo = getUserInfoById(data.currentUserId);
                if(currentUserInfo.userNo == 'admin' || whetherIsProjectManager(currentUserInfo.userRoles) || whetherIsProjectManagerNotZSZX(currentUserInfo.userRoles)) {
                    mini.get("#setProgressRunBtn").show();
                    mini.get("#unSetProgressRunBtn").show();
                    $("#progressRunStatusFilter").show();
                }
                if(currentUserInfo.userNo == 'admin') {
                    // $("#splitBtn").show();
                }

            //    如果是从总览跳转的
                if(pointCategoryName) {
                    var categoryId='';
                    for(var index=0;index<data.category.length;index++) {
                        if(data.category[index].categoryName==pointCategoryName) {
                            categoryId=data.category[index].categoryId;
                            break;
                        }
                    }
                    mini.get("instStatus").setValue("SUCCESS_END,RUNNING");
                    mini.get("projectCategory").setValue(categoryId);
                    if(projectStartTime) {
                        mini.get("projectStartTime").setValue(projectStartTime);
                    }
                    if(projectEndTime) {
                        mini.get("projectEndTime").setValue(projectEndTime);
                    }
                }
                searchFrm();
            }
        }
    });

});


function openCreateInstWindow() {
    mini.get("selectProjectCategory").setData(projectCategoryData);
    createInstWindow.show();
}

function createInstCloseWindow() {
    mini.get("selectProjectCategory").setValue("");
    createInstWindow.hide();
}

function createInstOkWindow() {
    var categoryText= mini.get("selectProjectCategory").getText();
    if(!categoryText) {
        mini.alert("请选择项目类别!");
        return;
    }

    var solKey="KJXMGL";
    switch (categoryText) {
        case "产品研发（整机）类":
            solKey="KJXMGL_CPYFSJ";
            break;
        case "产品研发（零部件）类":
            solKey="KJXMGL_CPYFLBJ";
            break;
        case "产品研发（属具）类":
            solKey="KJXMGL_CPYFSHJ";
            break;
        case "产品研发（工艺）类":
        case "技术研发（工艺技术）类":
        case "工艺规划类":
            solKey="KJXMGL_GY";
            break;
        case "技术研发（产品技术）类":
        case "技术研发（测试技术）类":
        case "技术研发（仿真技术）类":
            solKey="KJXMGL_JSYF";
            break;
        case "信息技术（实施开发）类":
        case "信息技术（系统实施）类":
        case "信息技术（网络硬件实施）类":
        case "信息技术（体系建设及咨询）类":
            solKey="KJXMGL_XXJS";
            break;
        case "产学研（国重）类":
        case "产学研（其它）类":
        case "产学研（集团合作）类":
        case "产学研（高校合作）类":
        case "产学研（政府）类":
            solKey="KJXMGL_CXY";
            break;
        case "标准技术类":
        case "其他专项类":
        case "其他专项类(新版)":
        case "特殊项目类":
            solKey="KJXMGL_QT";
            break;
    }
    var categoryId= mini.get("selectProjectCategory").getValue();
    addProject(solKey,categoryId);
    createInstCloseWindow();
}

//新增流程（后台根据配置的表单进行跳转）
function addProject(solKey,categoryId) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/"+solKey+"/start.do?projectCategory="+categoryId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (projectListGrid) {
                projectListGrid.reload()
            }
            ;
        }
    }, 1000);
}

//编辑行数据流程（后台根据配置的表单进行跳转）
function editProjectRow(projectId, instId) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (projectListGrid) {
                projectListGrid.reload()
            }
            ;
        }
    }, 1000);
}

//删除记录
function removeProject(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = projectListGrid.getSelecteds();
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
                if (r.status == 'DRAFTED') {
                    ids.push(r.projectId);
                    instIds.push(r.INST_ID_);
                } else if (r.status == 'DISCARD_END' && (currentUserInfo.userNo == 'admin' || whetherIsProjectManager(currentUserInfo.userRoles) || whetherIsProjectManagerNotZSZX(currentUserInfo.userRoles))) {
                    ids.push(r.projectId);
                    instIds.push(r.INST_ID_);
                } else {
                    existStartInst = true;
                    continue;
                }
            }
            if (existStartInst) {
                alert("仅草稿状态数据可由本人删除");
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/xcmgProjectManager/core/xcmgProject/del.do",
                    method: 'POST',
                    data: {ids: ids.join(','), instIds: instIds.join(',')},
                    success: function (text) {
                        projectListGrid.reload();
                    }
                });
            }
        }
    });
}

//明细（直接跳转到详情的业务controller）
function detailProjectRow(projectId, status, detailHideRoleRatio,categoryId) {
    var action = "detail";
    var url = '';
    if(categoryId=='02'){
        url = jsUseCtxPath + "/xcmgProjectManager/core/xcmgProject/productEditPage.do?action=" + action + "&projectId=" + projectId
            + "&status=" + status+"&detailHideRoleRatio="+detailHideRoleRatio;
    }else{
        url = jsUseCtxPath + "/xcmgProjectManager/core/xcmgProject/edit.do?action=" + action + "&projectId=" + projectId
            + "&status=" + status+"&detailHideRoleRatio="+detailHideRoleRatio;
    }
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (projectListGrid) {
                projectListGrid.reload()
            }
            ;
        }
    }, 1000);
}

//跳转到任务的处理界面
function doProjectTask(taskId) {
    $.ajax({
        url: jsUseCtxPath + '/bpm/core/bpmTask/checkTaskLockStatus.do?taskId=' + taskId,
        async:false,
        success: function (result) {
            if (!result.success) {
                top._ShowTips({
                    msg: result.message
                });
            } else {
                handProjectTask(taskId);
            }
        }
    })
}

//流程任务的处理（后台根据流程方案对应的表单进行跳转）
function handProjectTask(taskId) {
    var url = jsUseCtxPath + '/bpm/core/bpmTask/toStart.do?taskId=' + taskId;
    var winObj = openNewWindow(url, "handTask");
    var loop = setInterval(function () {
        if(!winObj) {
            clearInterval(loop);
        } else if (winObj.closed) {
            clearInterval(loop);
            if (grid) {
                grid.reload()
            }
            ;
        }
    }, 1000);

}

function onStatusRenderer(e) {
    var record = e.record;
    var status = record.status;

    var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
        {'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
        {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},
        {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
        {'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},
        {'key': 'PENDING', 'value': '挂起', 'css': 'gray'}
    ];

    return $.formatItemValue(arr, status);
}

function exportProject() {
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

//打开进度追赶窗口
function setProgressRunWindow() {
    var rows = projectListGrid.getSelecteds();
    if (!rows || rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }
    progressRunWindow.show();
    mini.get("scoreGetTime").setValue("actualEndTime");
    var nowDate = new Date();
    var nowDateStr = nowDate.getFullYear() + "-" + (nowDate.getMonth() + 1) + '-' + nowDate.getDate();
    mini.get("defineTimeId").setValue(nowDateStr);
    mini.get("defineTimeId").hide();
}

//将所选的项目设置为进度追赶（不会进行延误扣分，积分发放时间用设置的时间，记录审计表）
function setProgressRun() {
    var rows = projectListGrid.getSelecteds();
    var projectIds = [];
    for (var i = 0; i < rows.length; i++) {
        projectIds.push(rows[i].projectId);
    }
    var projectIdStr = projectIds.join(',');
    var scoreGetTime = mini.get("scoreGetTime").getValue();
    if (scoreGetTime == 'defineTime') {
        scoreGetTime = mini.get("defineTimeId").getText();
    }
    var data = {projectIdStr: projectIdStr, scoreGetTime: scoreGetTime, action: "1"};
    $.ajax({
        url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/changeProgressRunStatus.do',
        type: 'POST',
        data:mini.encode(data),
        contentType: 'application/json',
        success: function (returnData) {
            if (returnData) {
                mini.alert(returnData)
            }
            closeSetProgressRunWindow();
        }
    });

}

function scoreGetTimeChange() {
    var scoreGetTime = mini.get("scoreGetTime").getValue();
    if (scoreGetTime != 'defineTime') {
        mini.get("defineTimeId").hide();
    } else {
        mini.get("defineTimeId").show();
    }
}

//关闭进度追赶窗口
function closeSetProgressRunWindow() {
    progressRunWindow.hide();

    searchFrm();
}

//将所选项目取消进度追赶（转变为普通项目管理，记录审计表）
function undoSetProgressRun() {
    var rows = projectListGrid.getSelecteds();
    if (!rows || rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }
    mini.confirm("确定结束进度追赶？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var rows = projectListGrid.getSelecteds();
            var projectIds = [];
            for (var i = 0; i < rows.length; i++) {
                projectIds.push(rows[i].projectId);
            }
            var projectIdStr = projectIds.join(',');
            var scoreGetTime = null;
            var data = {projectIdStr: projectIdStr, scoreGetTime: scoreGetTime, action: "0"};
            $.ajax({
                url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/changeProgressRunStatus.do',
                type: 'POST',
                data:mini.encode(data),
                contentType: 'application/json',
                success: function (returnData) {
                    if (returnData) {
                        mini.alert(returnData)
                    }
                }
            });
        }
    });
}

//交付物管理
function editProjectDelivery(record) {
    var categoryId = record.categoryId;
    var currentUserProjectRoleName="";
    //只有项目当前处理人是项目负责人的情况下，并且当前用户是项目成员，才允许操作
    if(record.status =='RUNNING' && record.allTaskUserIds.indexOf(record.respManId)!=-1) {
    //    判断当前登录人是否为该项目成员
        $.ajax({
            url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/judgeProjectMember.do?projectId='+record.projectId,
            type: 'get',
            contentType: 'application/json',
            success: function (data) {
                if (!data || !data.result) {
                    mini.alert('只有当前登录用户为项目成员，且项目状态为“运行中”、当前任务处理人为项目负责人时，才允许操作交付物！');
                    return;
                } else {
                    currentUserProjectRoleName=data.roleName;
                    var action = "editDelivery";
                    var url = "";
                    if(categoryId=='02'){
                        url = jsUseCtxPath + "/xcmgProjectManager/core/xcmgProject/productEditPage.do?action=" + action + "&projectId=" + record.projectId + "&status=" + record.status+"&currentUserProjectRoleName="+currentUserProjectRoleName;
                    }else{
                        url = jsUseCtxPath + "/xcmgProjectManager/core/xcmgProject/edit.do?action=" + action + "&projectId=" + record.projectId + "&status=" + record.status+"&currentUserProjectRoleName="+currentUserProjectRoleName;
                    }
                    var winObj = window.open(url);
                    var loop = setInterval(function () {
                        if (winObj.closed) {
                            clearInterval(loop);
                            if (projectListGrid) {
                                projectListGrid.reload()
                            }
                            ;
                        }
                    }, 1000);
                }
            }
        });
    } else {
        mini.alert('只有当前登录用户为项目成员，且项目状态为“运行中”、当前任务处理人为项目负责人时，才允许操作交付物！');
        return;
    }
}

function sendDelayNotice() {
    $.ajax({
        url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/scheduleTest.do',
        type: 'get',
        contentType: 'application/json',
        success: function (data) {

        }
    });
}
function splitUserDelivery() {
    var postData = {year:'2022'};
    var _url = jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/splitUserDelivery.do';
    var resultData = ajaxRequest(_url, 'POST', false, postData);
    if (resultData) {
        mini.alert(resultData.message);
    }
}

