$(function () {
    var nowDate = new Date();
    var beginDate = nowDate.getFullYear() + "-" + (nowDate.getMonth()+1) + "-01";
    var day = new Date(nowDate.getFullYear(), nowDate.getMonth()+1, 0);
    var endDate = nowDate.getFullYear() + "-" + (nowDate.getMonth()+1) + "-" + day.getDate();
    var startTime = mini.get("startTime");
    startTime.setValue(beginDate);
    var endTime = mini.get("endTime");
    endTime.setValue(endDate);
    searchFrm();
});



//明细
function detailProjectRow(projectId,status,projectDepName) {
    if(isGY && isGY=='true' && projectDepName!='工艺技术部') {
        mini.alert(xcmgScoreList_name);
        return;
    }
    var action = "detail";
    var url=jsUseCtxPath+"/xcmgProjectManager/core/xcmgProject/edit.do?action="+action+"&projectId=" + projectId+"&status="+status;
    var winObj = window.open(url);
    var loop = setInterval(function() {
        if(winObj.closed) {
            clearInterval(loop);
            if(scoreListGrid){
                scoreListGrid.reload()
            };
        }
    }, 1000);
}

//跳转到任务的处理界面
function doProjectTask(taskId) {
    $.ajax({
        url:jsUseCtxPath+'/bpm/core/bpmTask/checkTaskLockStatus.do?taskId='+taskId,
        async:false,
        success:function (result) {
            if(!result.success){
                top._ShowTips({
                    msg:result.message
                });
            }else{
                handProjectTask(taskId);
            }
        }
    })
}

function handProjectTask(taskId){
    var url=jsUseCtxPath+'/bpm/core/bpmTask/toStart.do?taskId='+taskId;
    var winObj = openNewWindow(url,"handTask");
    var loop = setInterval(function() {
        if(!winObj) {
            clearInterval(loop);
        } else if(winObj.closed) {
            clearInterval(loop);
            if(grid){
                grid.reload()
            };
        }
    }, 1000);

}

function onStatusRenderer(e) {
    var record = e.record;
    var status = record.status;

    var arr = [ {'key' : 'DRAFTED','value' : '草稿','css' : 'orange'},
        {'key' : 'RUNNING','value' : '运行中','css' : 'green'},
        {'key' : 'SUCCESS_END','value' : '成功结束','css' : 'blue'},
        {'key' : 'DISCARD_END','value' : '作废','css' : 'red'},
        {'key' : 'ABORT_END','value' : '异常中止结束','css' : 'red'},
        {'key' : 'PENDING','value' : '挂起','css' : 'gray'}
    ];

    return $.formatItemValue(arr,status);
}

//导出
function exportBtn(){
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

function showStageScores(projectId,userId) {
    var startTime="";
    var endTime="";
    if(mini.get("startTime").getText()) {
        startTime=mini.get("startTime").getText();
    }
    if(mini.get("endTime").getText()) {
        endTime=mini.get("endTime").getText();
    }
    mini.open({
        title: xcmgScoreList_name1,
        url: jsUseCtxPath + "/xcmgProjectManager/core/xcmgScore/projectStageScoreShow.do?projectId="+projectId+"&userId="+userId+"&startTime="+startTime+"&endTime="+endTime,
        width: 750,
        height: 350,
        allowResize: true,
        onload: function () {

        }
    });
}