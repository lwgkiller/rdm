$(function () {
    searchFrm();
    $.ajax({
        url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/getProjectEditRule.do',
        type: 'get',
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                mini.get("#projectCategory").load(data.category);
                mini.get("#projectLevel").load(data.level);
            }
        }
    });
});

//编辑行数据流程（后台根据配置的表单进行跳转）
function editApplyRow(applyId,instId) {
    var url=jsUseCtxPath+"/bpm/core/bpmInst/start.do?instId="+instId;
    var winObj = window.open(url);
    var loop = setInterval(function() {
        if(winObj.closed) {
            clearInterval(loop);
            if(applyListGrid){
                applyListGrid.reload()
            };
        }
    }, 1000);
}

//删除记录
function removeApply(record) {
    var rows =[];
    if(record) {
        rows.push(record);
    } else {
        rows = applyListGrid.getSelecteds();
    }

    if (rows.length <= 0) {
        mini.alert(projectChangeApplyList_name4);
        return;
    }

    mini.confirm(projectChangeApplyList_name5, projectChangeApplyList_name6, function (action) {
        if (action != 'ok') {
            return;
        } else {
            //判断该行是否已经提交流程
            var ids = [];
            var instIds=[];
            var existStartInst=false;
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                if((r.instStatus=='DRAFTED'||r.instStatus=='DISCARD_END')&&r.CREATE_BY_ == currentUserId) {
                    ids.push(r.id);
                    instIds.push(r.INST_ID_);
                } else {
                    existStartInst=true;
                    continue;
                }
            }
            if(existStartInst) {
                alert(projectChangeApplyList_name7);
            }
            if(ids.length>0) {
                _SubmitJson({
                    url: jsUseCtxPath+"/xcmgProjectManager/core/xcmgProjectChange/changeApplyDel.do",
                    method: 'POST',
                    data: {ids: ids.join(','),instIds:instIds.join(',')},
                    success: function (text) {
                        if(applyListGrid) {
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
        url:jsUseCtxPath+'/bpm/core/bpmTask/checkTaskLockStatus.do?taskId='+taskId,
        success:function (result) {
            if(!result.success){
                top._ShowTips({
                    msg:result.message
                });
            }else{
                var url=jsUseCtxPath+'/bpm/core/bpmTask/toStart.do?taskId='+taskId;
                var winObj = window.open(url);
                var loop = setInterval(function() {
                    if(winObj.closed) {
                        clearInterval(loop);
                        if(applyListGrid){
                            applyListGrid.reload()
                        };
                    }
                }, 1000);
            }
        }
    })
}
function myDetailRow(pkId){
    _OpenWindow({
        url: __rootPath+"/bpm/core/bpmInst/inform.do?instId=" + pkId,
        title: projectChangeApplyList_name8,
        max:"true"
    });
}
function detailChangeApply(id,status) {
    var action = "detail";
    var url=jsUseCtxPath+"/xcmgProjectManager/core/xcmgProjectChange/edit.do?action="+action+"&id=" + id+"&status="+status;
    var winObj = window.open(url);
    var loop = setInterval(function() {
        if(winObj.closed) {
            clearInterval(loop);
            if(applyListGrid){
                applyListGrid.reload()
            };
        }
    }, 1000);
}

//明细
function detailProjectRow(projectId,status) {
    var action = "detail";
    var url=jsUseCtxPath+"/xcmgProjectManager/core/xcmgProject/edit.do?action="+action+"&projectId=" + projectId+"&status="+status;
    var winObj = window.open(url);
    var loop = setInterval(function() {
        if(winObj.closed) {
            clearInterval(loop);
            if(applyListGrid){
                applyListGrid.reload()
            };
        }
    }, 1000);
}

