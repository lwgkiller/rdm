var step="";
$(function () {
    if(jxcshcId) {
        var url = jsUseCtxPath + "/serviceEngineering/core/jxcshc/getJxcshcDetail.do";
        $.post(
            url,
            {jxcshcId: jxcshcId},
            function (json) {
                formJxcshc.setData(json);
            });
    }
    fileListGrid.load();
    //明细入口
    if (action == 'detail') {
        formJxcshc.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        $("#detailToolBar").show();
        //非草稿放开流程信息查看按钮
        if(taskStatus!='DRAFTED') {
            $("#processInfo").show();
        }
    } else if(action=='task') {
        taskActionProcess();
    }
    if (isTest == "yes") {
        mini.get("versionType").setValue("wzb");
        mini.get("versionType").setEnabled(false)
    }
});

//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("formJxcshc");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos=[];
    formData.vars=[{key:'versionType',val:formData.versionType}];
    return formData;
}

//保存草稿
function saveJxcshc(e) {
    window.parent.saveDraft(e);
}

//检验表单是否必填(编制)
function validBianZhi() {
    var versionType=mini.get("versionType").getValue();
    if(!versionType) {
        return {"result": false, "message": jxcshcEdit_name};
    }
    if (fileListGrid.totalCount == 0) {
        return {"result": false, "message": jxcshcEdit_name1};
    }
    return {"result": true};
}


//启动流程
function startJxcshcProcess(e) {
    var bianZhiValid = validBianZhi();
    if (!bianZhiValid.result) {
        mini.alert(bianZhiValid.message);
        return;
    }
    window.parent.startProcess(e);
}

//流程中暂存信息（如编制阶段）
function saveJxcshcInProcess() {
    var bianZhiValid = validBianZhi();
    if (!bianZhiValid.result) {
        mini.alert(bianZhiValid.message);
        return;
    }
    var formData = _GetFormJsonMini("formJxcshc");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    $.ajax({
        url: jsUseCtxPath + '/serviceEngineering/core/jxcshc/saveJxcshc.do',
        type: 'post',
        async: false,
        data:mini.encode(formData),
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                var message="";
                if(data.success) {
                    message=jxcshcEdit_name2;
                } else {
                    message=jxcshcEdit_name3+data.message;
                }

                mini.alert(message,jxcshcEdit_name4,function () {
                    window.location.reload();
                });
            }
        }
    });
}

//流程中的审批或者下一步
function jxcshcApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (step == 'bianZhi') {
        var bianZhiValid = validBianZhi();
        if (!bianZhiValid.result) {
            mini.alert(bianZhiValid.message);
            return;
        }
    }
    //检查通过
    window.parent.approve();
}

function jxcshcProcessInfo() {
    var instId = $("#instId").val();
    var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: jxcshcEdit_name5,
        width: 800,
        height: 600
    });
}

function addJxcshcFile() {
    var jxcshcId=mini.get("id").getValue();
    if(!jxcshcId) {
        mini.alert(jxcshcEdit_name6);
        return;
    }
    mini.open({
        title: jxcshcEdit_name7,
        url: jsUseCtxPath + "/serviceEngineering/core/jxcshc/openJxcshcUploadWindow.do?masterId=" + jxcshcId,
        width: 800,
        height: 350,
        showModal:false,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            fileListGrid.load();
        }
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
        if (nodeVars[i].KEY_ == 'step') {
            step = nodeVars[i].DEF_VAL_;
        }
    }
    if(step == 'approve') {
        formJxcshc.setEnabled(false);
        mini.get("addFile").setEnabled(false);
    } else {
        if (isTest == "yes") {
            mini.get("versionType").setValue("wzb");
            mini.get("versionType").setEnabled(false)
        }
    }

}



function returnJxcshcPreviewSpan(fileName,fileId,formId,coverContent) {
    var fileType=getFileType(fileName);
    var downloadUrl =  "/serviceEngineering/core/jxcshc/jxcshcDownload.do";
    var s='';
    if(fileType=='other'){
        s = '<span  title=' + jxcshcEdit_name8 + ' style="color: silver" >' + jxcshcEdit_name8 + '</span>';
    }else if(fileType=='pdf'){
        var url='/serviceEngineering/core/jxcshc/jxcshcDownload.do';
        s = '<span  title=' + jxcshcEdit_name8 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+downloadUrl+ '\')">' + jxcshcEdit_name8 + '</span>';
    }else if(fileType=='office'){
        var url='/serviceEngineering/core/jxcshc/jxcshcOfficePreview.do';
        s = '<span  title=' + jxcshcEdit_name8 + ' style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url +'\')">' + jxcshcEdit_name8 + '</span>';
    }else if(fileType=='pic'){
        var url='/serviceEngineering/core/jxcshc/jxcshcImagePreview.do';
        s = '<span  title=' + jxcshcEdit_name8 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url + '\')">' + jxcshcEdit_name8 + '</span>';
    }
       s += '&nbsp;&nbsp;&nbsp;<span title=' + jxcshcEdit_name10 + ' style="color:#409EFF;cursor: pointer;" ' +
           'onclick="downLoadFile(\''+fileName+'\',\''+fileId+'\',\''+formId+'\',\''+downloadUrl+'\')">' + jxcshcEdit_name10 + '</span>';
    return s;
}

function operationRenderer(e) {
    var record = e.record;
    var cellHtml = '';
    cellHtml=returnJxcshcPreviewSpan(record.fileName,record.id,record.masterId,coverContent);
    //增加删除按钮
    if(action=='edit' || (action=='task' && step == 'bianZhi')) {
        var deleteUrl="/serviceEngineering/core/jxcshc/delUploadFile.do";
        cellHtml+='&nbsp;&nbsp;&nbsp;<span title=' + jxcshcEdit_name9 + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="deleteFile(\''+record.fileName+'\',\''+record.id+'\',\''+record.masterId+'\',\''+deleteUrl+'\')">' + jxcshcEdit_name9 + '</span>';
    }
    return cellHtml;
}