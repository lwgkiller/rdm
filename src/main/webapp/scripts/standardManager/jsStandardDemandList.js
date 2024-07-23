$(function () {
    searchFrm();
});

//新增标准申请
function addApply() {
    var title=jsStandardDemandList_jsbzxq;
    var url = jsUseCtxPath+"/bpm/core/bpmInst/JSBZXQFK/start.do";
    var winObj = window.open(url,title);
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

//编辑行数据流程（后台根据配置的表单进行跳转）
function editApplyRow(applyId,instId) {
    var title=jsStandardDemandList_bjjsbz;
    var width = getWindowSize().width;
    var height = getWindowSize().height;
    _OpenWindow({
        url: jsUseCtxPath+"/bpm/core/bpmInst/start.do?instId="+instId,
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
        mini.alert(jsStandardDemandList_qzsxz);
        return;
    }

    mini.confirm(jsStandardDemandList_qdsc, jsStandardDemandList_ts, function (action) {
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
                alert(jsStandardDemandList_jyxbr);
            }
            if(ids.length>0) {
                _SubmitJson({
                    url: jsUseCtxPath+"/standardManager/core/standardDemand/jsDemandDelete.do",
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
                    url: jsUseCtxPath+'/bpm/core/bpmTask/toStart.do?taskId='+taskId,
                    title: jsStandardDemandList_blsq,
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

//明细
function detailApplyRow(applyId, status) {
    var action = "detail";
    var url = jsUseCtxPath + "/standardManager/core/standardDemand/jsDemandEdit.do?action=" + action + "&formId=" + applyId + "&status=" + status;
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

