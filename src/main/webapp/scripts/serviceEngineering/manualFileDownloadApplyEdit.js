var stageName="";
$(function () {
    var url = jsUseCtxPath + "/serviceEngineering/core/manualFileDownloadApply/getJson.do";
    $.post(
        url,
        {id: applyId},
        function (json) {
            manualFileDownloadForm.setData(json);
        });
    if(action == 'detail') {
        manualFileDownloadForm.setEnabled(false);
        mini.get("demandListToolBar").hide();
        mini.get("fileListToolBar").hide();
        $("#detailToolBar").show();
        if (status != 'DRAFTED') {
            $("#processInfo").show();
        }
    } else if(action=='task') {
        taskActionProcess();
    }
});

function addManualFile() {
    //打开选择窗口，选择后插入本表中
    selectManualFileWindow.show();
    searchManualFile();
}

function selectFileOK() {
    var rows=manualFileListGrid.getSelecteds();
    if(rows.length>0) {
        for(var index=0;index<rows.length;index++) {
            var row=rows[index];
            if(row.cpzgName) {
                row.manualFileId=row.id;
                row.id='';
                demandGrid.addRow(row);
            }
        }
    }
    selectFileHide();
}

function selectFileHide() {
    mini.get('salesModel').setValue('');
    mini.get('designModel').setValue('');
    mini.get('materialCode').setValue('');
    mini.get('manualDescription').setValue('');
    mini.get('manualLanguage').setValue('');
    mini.get('manualCode').setValue('');
    mini.get('isCE').setValue('');
    selectManualFileWindow.hide();
}

function delManualFile() {
    var selecteds = demandGrid.getSelecteds();
    demandGrid.removeRows(selecteds);
}

function getData() {
    var formData = _GetFormJsonMini("manualFileDownloadForm");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    if (formData.SUB_demandGrid) {
        delete formData.SUB_demandGrid;
    }
    if(demandGrid.getChanges().length > 0){
        formData.changeDemandGrid=demandGrid.getChanges();
    }
    return formData;
}

//保存草稿
function saveDraft(e) {
    window.parent.saveDraft(e);
}

//发起流程
function startProcess(e) {
    var formValid = validApply();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.startProcess(e);
}

//下一步审批
function applyApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if(stageName=='start') {
        var formValid = validApply();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }

    //检查通过
    window.parent.approve();
}


//暂存信息
function saveInProcess() {
    var formData =getData();

    var json =mini.encode(formData);
    $.ajax({
        url: jsUseCtxPath + '/serviceEngineering/core/manualFileDownloadApply/saveInProcess.do',
        type: 'post',
        async: false,
        data:json,
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                var message="";
                if(data.success) {
                    window.location.reload();
                } else {
                    mini.alert(manualFileDownloadApplyEdit_name3+data.message);
                }
            }
        }
    });
}


function validApply() {
    var demandGridData = demandGrid.getData();
    if(demandGridData.length==0) {
        return {"result": false, "message": manualFileDownloadApplyEdit_name4};
    }
    /*var fileListGridData = fileListGrid.getData();
    if(fileListGridData.length==0) {
        return {"result": false, "message": "请上传需求通知附件"};
    }*/
    return {"result": true};
}

function processInfo() {
    var instId = $("#instId").val();
    var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: manualFileDownloadApplyEdit_name5,
        width: 800,
        height: 600
    });
}


function taskActionProcess() {
    //获取上一环节的结果和处理人
    bpmPreTaskTipInForm();

    //获取环境变量
    if (!nodeVarsStr) {
        nodeVarsStr = "[]";
    }
    var nodeVars = $.parseJSON(nodeVarsStr);
    for (var i = 0; i < nodeVars.length; i++) {
        if (nodeVars[i].KEY_ == 'stageName') {
            stageName = nodeVars[i].DEF_VAL_;
        }
    }
    if(stageName!='start') {
        manualFileDownloadForm.setEnabled(false);
        mini.get("demandListToolBar").hide();
        mini.get("fileListToolBar").hide();
    }

}


function uploadFile() {
    var applyId = mini.get("id").getValue();
    if (!applyId) {
        mini.alert(manualFileDownloadApplyEdit_name6);
        return;
    }
    mini.open({
        title: manualFileDownloadApplyEdit_name7,
        url: jsUseCtxPath + "/serviceEngineering/core/manualFileDownloadApply/openUploadWindow.do",
        width: 900,
        height: 350,
        showModal:false,
        allowResize: true,
        onload: function () {
            var iframe = this.getIFrameEl();
            var passParams={};
            passParams.applyId = applyId;
            var data = { passParams: passParams };  //传递上传参数
            iframe.contentWindow.SetData(data);
        },
        ondestroy: function (action) {
            fileListGrid.load();
        }
    });
}

function refreshFile() {
    fileListGrid.reload();
}

function searchManualFile() {
    var paramArray = [];
    paramArray.push({name:"salesModel",value:mini.get('salesModel').getValue()});
    paramArray.push({name:"designModel",value:mini.get('designModel').getValue()});
    paramArray.push({name:"materialCode",value:mini.get('materialCode').getValue()});
    paramArray.push({name:"manualDescription",value:mini.get('manualDescription').getValue()});
    paramArray.push({name:"manualLanguage",value:mini.get('manualLanguage').getValue()});
    paramArray.push({name:"manualCode",value:mini.get('manualCode').getValue()});
    paramArray.push({name:"isCE",value:mini.get('isCE').getValue()});
    paramArray.push({name:"manualStatus",value:"可打印"});
    var data = {};
    data.filter = mini.encode(paramArray);
    manualFileListGrid.load(data);
}