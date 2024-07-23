var editForm='';

$(function () {
    projectFileApply.setData(ApplyObj);
    if(action=='task') {
        bpmPreTaskTipInForm();
        getProcessNodeVars();
        if(editForm!='1') {
            projectFileApply.setEnabled(false);
        }
        if(editForm=='1') {
            $('#addFile').show();
            $('#delFile').show();
            $('#tipInfo').show();
        }
    }else if(action =='detail'){
        $("#detailToolBar").show();
        projectFileApply.setEnabled(false);
    }else if(action == 'edit'){
        $('#addFile').show();
        $('#delFile').show();
        $('#tipInfo').show();
    }else if(action == 'change'){
        $('#addFile').show();
        $('#delFile').show();
        $('#tipInfo').show();
    }
    reload();
});
function reload() {
    let data = {};
    data.recordId = ApplyObj.id;
    //查询
    fileListGrid.load(data);
    var fileList = fileListGrid.getData();
    setTimeout(function () {
        var fileIds = "";
        var fileList = fileListGrid.getData();
        for(var i=0;i<fileList.length;i++){
            fileIds += fileList[i].id+",";
        }
        if(fileList.length>1){
            fileIds = fileIds.substring(0,fileIds.length-1);
        }
        mini.get("fileIds").setValue(fileIds);
    },100)
}
//流程实例图
function processInfo() {
    let instId = $("#instId").val();
    let url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: projectFileApplyEdit_name2,
        width: 800,
        height: 600
    });
}

//获取环境变量
function getProcessNodeVars() {
    if (!nodeVarsStr) {
        nodeVarsStr = "[]";
    }
    var nodeVars = $.parseJSON(nodeVarsStr);
    for (var i = 0; i < nodeVars.length; i++) {
        if (nodeVars[i].KEY_ == 'isStartNode') {
            editForm = nodeVars[i].DEF_VAL_;
        }
    }
}


//保存草稿
function saveApplyInfo(e) {
    var formValid = draftOrStartValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.saveDraft(e);
}

//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("projectFileApply");
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos=[];
    formData.vars=[{key:'projectName',val:formData.projectName}];
    return formData;
}

//保存草稿或提交时数据是否有效
function draftOrStartValid() {
    return {"result": true};
}
//启动流程
function startApplyProcess(e) {
    var formValid = draftOrStartValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.startProcess(e);
}
//审批或者下一步
function applyApprove() {
    var formValid = draftOrStartValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.approve();
}

//作废并删除
function discardAndDel() {
    window.parent.discardInstAndDel(function () {
        _SubmitJson({
            url: jsUseCtxPath+"/xcmgProjectManager/core/xcmgProjectFile/delete.do",
            method: 'POST',
            sync: false,
            data: {ids: mini.get("id").getValue(),instIds:mini.get("instId").getValue()},
            success: function (text) {
                if(applyListGrid) {
                    applyListGrid.reload();
                }
            }
        });
    });
}

//移除文档
function unboundProjectApprovalFile(record) {
    mini.confirm(projectFileApplyEdit_name3, projectFileApplyEdit_name4,
        function (action) {
            if (action == "ok") {
                $.ajaxSettings.async = false;
                var url = jsUseCtxPath + "/xcmgProjectManager/core/xcmgProjectFile/delFile.do";
                var data = {
                    fileId: record.id,
                    id: ApplyObj.id,
                };
                $.post(
                    url,
                    data,
                    function (json) {
                        reload();
                    });
                $.ajaxSettings.async = true;
            }
        }
    );
}
function addFile() {
    let url = jsUseCtxPath + "/xcmgProjectManager/core/xcmgProjectFile/fileListPage.do?projectId=" + ApplyObj.projectId+"&stageId="+ApplyObj.stageId+"&recordId="+ApplyObj.id;
    mini.open({
        title: projectFileApplyEdit_name5,
        url: url,
        width: 800,
        height: 600,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            reload();
        }
    });
}

//删除记录
function delFile(record) {
    var rows =[];
    if(record) {
        rows.push(record);
    } else {
        rows = fileListGrid.getSelecteds();
    }

    if (rows.length <= 0) {
        mini.alert(projectFileApplyEdit_name6);
        return;
    }
    mini.confirm(projectFileApplyEdit_name7, projectFileApplyEdit_name8, function (action) {
        if (action != 'ok') {
            return;
        } else {
            //判断该行是否已经提交流程
            var ids = [];
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                ids.push(r.id);
            }
            if(ids.length>0) {
                _SubmitJson({
                    url: jsUseCtxPath+"/xcmgProjectManager/core/xcmgProjectFile/delOrgFiles.do",
                    method: 'POST',
                    data: {ids: ids.join(','),applyId:ApplyObj.id},
                    success: function (text) {
                        if(fileListGrid) {
                            fileListGrid.reload();
                        }
                    }
                });
            }
        }
    });
}
