$(function () {
    searchFrm();
});

//新增标准申请
function addApply() {
    var title=standardApplyList_xzylsq;
    if(applyCategoryId=='download') {
        title=standardApplyList_xzxzsq;
    }
    var width = getWindowSize().width;
    var height = getWindowSize().height;
    _OpenWindow({
        url: jsUseCtxPath+"/bpm/core/bpmInst/BZGLSQ/start.do?standardApplyCategoryId="+applyCategoryId,
        title: title,
        width: width,
        height: height,
        showMaxButton:true,
        showModal: true,
        allowResize: true,
        ondestroy: function () {
            if(applyListGrid) {
                applyListGrid.reload();
            }
        }
    });
}

//编辑行数据流程（后台根据配置的表单进行跳转）
function editApplyRow(applyId,instId) {
    var title=standardApplyList_bjylsq;
    if(applyCategoryId=='download') {
        title=standardApplyList_bjxzsq;
    }
    var width = getWindowSize().width;
    var height = getWindowSize().height;
    _OpenWindow({
        url: jsUseCtxPath+"/bpm/core/bpmInst/start.do?instId="+instId+"&standardApplyCategoryId="+applyCategoryId,
        title: title,
        width: width,
        height: height,
        showMaxButton:true,
        showModal: true,
        allowResize: true,
        ondestroy: function (action) {
            if(applyListGrid) {
                applyListGrid.reload();
            }
        }
    });
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
        mini.alert(standardApplyList_qzs);
        return;
    }

    mini.confirm(standardApplyList_qdsc, standardApplyList_ts, function (action) {
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
                    ids.push(r.applyId);
                    instIds.push(r.INST_ID_);
                } else {
                    existStartInst=true;
                    continue;
                }
            }
            if(existStartInst) {
                alert(standardApplyList_qyxbrsc);
            }
            if(ids.length>0) {
                _SubmitJson({
                    url: jsUseCtxPath+"/standardManager/core/standardApply/applyDel.do",
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
                var width = getWindowSize().width;
                var height = getWindowSize().height;
                _OpenWindow({
                    url: jsUseCtxPath+'/bpm/core/bpmTask/toStart.do?taskId='+taskId+"&standardApplyCategoryId="+applyCategoryId,
                    title: standardApplyList_blsq,
                    width: width,
                    height: height,
                    showMaxButton:true,
                    showModal: true,
                    allowResize: true,
                    ondestroy: function (action) {
                        applyListGrid.reload();
                    }
                });
            }
        }
    })
}

