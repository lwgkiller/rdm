//打开变更项目的界面
function changeProject() {

    //查询变更流程状态
    var result = getChangeInfo();
    /*if(!result){
        return ;
    }

    var width = getWindowSize().width;
    var height = getWindowSize().height;
    var action = "change";

    _OpenWindow({
        url: jsUseCtxPath + "/xcmgProjectManager/core/xcmgProject/edit.do?action=" + action + "&projectId=" + projectId,
        title: "项目变更",
        width: width,
        height: height,
        showMaxButton: false,
        ondestroy: function (action) {
            location.reload(true);
        }
    });*/
}

function getChangeInfo() {
    var taskId = parent.window.taskId;
    var postData = {"taskId_":taskId};
    var url = jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/changeInfo.do';
    var resultData = ajaxRequest(url,'POST',false,postData);
    if (resultData&&resultData.length>0) {
        var changeFlowStatusData=toReturnFlowStatus(resultData);
        if(changeFlowStatusData.status=='RUNNING'){
            mini.alert("变更流程("+changeFlowStatusData.id+")正在审批中,请在“申请流程管理”--“项目变更流程”菜单中跟进，审批完成后联系项目管理人员进行变更实施！");
        }else if(changeFlowStatusData.status=='DRAFTED'){
            window.open(jsUseCtxPath+"/bpm/core/bpmInst/start.do?instId="+changeFlowStatusData.inst_id_+"&id="+changeFlowStatusData.id);
        }else if(changeFlowStatusData.status=='SUCCESS_END'){
            startChangeFlow(taskId,"当前已存在审批完成的变更流程"+changeFlowStatusData.id+",确定重新发起变更流程？");
        }else{
            startChangeFlow(taskId);
        }
    }else {
        startChangeFlow(taskId);
    }
}

function toReturnFlowStatus(changeInfos) {
    var runningData;
    var draftedData;
    var completeData;
    for(var i=0;i<changeInfos.length;i++) {
        if(changeInfos[i].status=='RUNNING') {
            runningData=changeInfos[i];
        }
        if(changeInfos[i].status=='DRAFTED') {
            draftedData=changeInfos[i];
        }
        if(changeInfos[i].status=='SUCCESS_END') {
            completeData=changeInfos[i];
        }
    }
    if(runningData) {
        return runningData;
    }
    if(draftedData){
        return draftedData;
    }
    if(completeData){
        return completeData;
    }
    return "";
}

function startChangeFlow(taskId,confirmContent) {
    if(!confirmContent) {
        confirmContent="确定发起变更流程？";
    }
    mini.confirm(confirmContent, "确定？",
        function (action) {
            if (action == "ok") {
                window.open(jsUseCtxPath+"/bpm/core/bpmInst/XMBGSQ/start.do?projectId="+projectId+"&taskId_="+taskId);
            }
        }
    );
}

function changeActionProcess() {
    $("#changeToolBar").show();
    mini.get("projectCategory").setEnabled(false);
    mini.get("projectSource").setEnabled(false);
    // mini.get("mainDepId").setEnabled(false);
    // mini.get("knotScoreId").setEnabled(false);
    mini.get("projectNumber").setEnabled(false);
    // mini.get("pointStandardScore").setEnabled(false);
    // mini.get("knotRatioId").setEnabled(false);
    $("#changeButtons").css("display","inline-block");
    if (action == 'change' && isProjectManager) {
        mini.get("selectStage").setEnabled(true);
        $('#newInput').show();
        getPassStage();
    } else {
        $('#newInput').hide();
    }
}

function saveChange() {
    var rows = grid_project_memberInfo.findRows();
    //项目成员信息部分数据校验
    for (var i = 0; i < rows.length; i++) {
        if (!rows[i].memberDeptId || !rows[i].roleId || !rows[i].userId) {
            mini.alert("请确保项目成员关键信息填写完整！");
            return;
        }
    }

    var plans = grid_project_plan.getData();
    if (!plans || plans.length == 0) {
        mini.alert('项目计划为空');
        return;
    }
    //保存变更
    var formData = getData();
    $.ajax({
        url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/save.do?changedRespManId='+changedRespManId+"&currentRespManId="+originalRespManId,
        type: 'post',
        async: false,
        data:mini.encode(formData),
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                var message="";
                if(data.success) {
                    message="数据保存成功";
                } else {
                    message="数据保存失败"+data.message;
                }
                mini.showMessageBox({
                    title: "提示信息",
                    iconCls: "mini-messagebox-info",
                    buttons: ["ok"],
                    message: message,
                    callback: function (action) {
                        if(action=="ok"){
                            window.location.reload();
                        }
                    }
                });
            }
        }
    });
}
