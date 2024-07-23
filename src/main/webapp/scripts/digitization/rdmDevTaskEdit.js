var nodeName="";
$(function () {
    if(applyId) {
        var url = jsUseCtxPath + "/devTask/core/getDevInfo.do?applyId="+applyId;
        $.get(
            url,
            function (json) {
                formDevTask.setData(json);
            });
    }
    //明细入口
    if (action == 'detail') {
        formDevTask.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        $("#detailToolBar").show();
        //非草稿放开流程信息查看按钮
        if(status!='DRAFTED') {
            $("#processInfo").show();
        }
        $("#xxbTechOpinionDiv").css("background-color","#F2F2F2");
    } else if(action=='task') {
        taskActionProcess();
    } else if(action=='edit') {
        editOrTaskEditProcess();
    }
});

//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("formDevTask");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    return formData;
}

//保存草稿
function saveDevTask(e) {
    var devType=$.trim(mini.get("devType").getValue());
    if (!devType) {
        mini.alert("请选择功能开发类型");
        return;
    }
    var applyName=$.trim(mini.get("applyName").getValue());
    if (!applyName) {
        mini.alert("请填写申请说明");
        return;
    }
    window.parent.saveDraft(e);
}

//检验表单是否必填
function validDevTask() {
    if(action == 'edit' || (action=='task' && (nodeName == 'bianzhi' || nodeName == 'creatorRespSH'))) {
        var devType=$.trim(mini.get("devType").getValue());
        if (!devType) {
            return {"result": false, "message": "请选择功能开发类型"};
        }
        var applyName=$.trim(mini.get("applyName").getValue());
        if (!applyName) {
            return {"result": false, "message": "请填写申请说明"};
        }
        var applyReason=$.trim(mini.get("applyReason").getValue());
        if(!applyReason) {
            return {"result": false, "message": "请填写需求详细描述"};
        }
        var applyValue=$.trim(mini.get("applyValue").getValue());
        if(!applyValue) {
            return {"result": false, "message": "请填写功能价值描述"};
        }
        var fileListGridData=fileListGrid.getData();
        if(!fileListGridData || fileListGridData.length==0) {
            return {"result": false, "message": "请在附件列表处上传需求或方案说明材料"};
        }

        if(action=='task' && nodeName == 'creatorRespSH') {
            var creatorRespOpinion=$.trim(mini.get("creatorRespOpinion").getValue());
            if(!creatorRespOpinion) {
                return {"result": false, "message": "请填写业务部门负责人意见"};
            }
        }
    }
    if(action=='task' && (nodeName == 'xxbJSAnalyse'||nodeName == 'xxbRespSH'||nodeName == 'taskAssign'||nodeName == 'taskDev')) {
        if(nodeName == 'xxbJSAnalyse'||nodeName == 'xxbRespSH') {
            var xxbTechOpinion=$.trim(mini.get("xxbTechOpinion").getValue());
            if (!xxbTechOpinion) {
                return {"result": false, "message": "请选择信息部技术分析意见"};
            }
            var xxbTechOpinionReason=$.trim(mini.get("xxbTechOpinionReason").getValue());
            if(!xxbTechOpinionReason) {
                return {"result": false, "message": "请填写信息部技术分析理由"};
            }
            var flowChange=$.trim(mini.get("flowChange").getValue());
            if(!flowChange) {
                return {"result": false, "message": "请选择是否涉及流程变更"};
            }
        }
        if(nodeName == 'taskAssign') {
            var planStartTime=$.trim(mini.get("planStartTime").getText());
            if (!planStartTime) {
                return {"result": false, "message": "请选择计划开始时间"};
            }
            var planEndTime=$.trim(mini.get("planEndTime").getText());
            if (!planEndTime) {
                return {"result": false, "message": "请选择计划结束时间"};
            }
            var gzlEvaluate=$.trim(mini.get("gzlEvaluate").getValue());
            if(!gzlEvaluate) {
                return {"result": false, "message": "请填写预估工作量（人天）"};
            }
            var devRespUserId=$.trim(mini.get("devRespUserId").getValue());
            if(!devRespUserId) {
                return {"result": false, "message": "请选择开发责任人"};
            }
        }
        if(nodeName == 'taskDev') {
            var finishDesc=$.trim(mini.get("finishDesc").getValue());
            if(!finishDesc) {
                return {"result": false, "message": "请填写完成情况描述"};
            }
        }

    }
    return {"result": true};
}

//启动流程
function startDevTaskProcess(e) {
    var formValid = validDevTask();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.startProcess(e);
}

//流程中暂存信息（如编制阶段）
function saveDevTaskInProcess() {
    var devType=$.trim(mini.get("devType").getValue());
    if (!devType) {
        mini.alert("请选择功能开发类型");
        return;
    }
    var applyName=$.trim(mini.get("applyName").getValue());
    if (!applyName) {
        mini.alert("请填写申请说明");
        return;
    }
    var formData = _GetFormJsonMini("formDevTask");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    $.ajax({
        url: jsUseCtxPath + '/devTask/core/saveDevInfo.do',
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

                mini.alert(message,"提示信息",function () {
                    window.location.reload();
                });
            }
        }
    });
}

//流程中的审批或者下一步
function devTaskApprove() {
    var formValid = validDevTask();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }

    //检查通过
    window.parent.approve();
}

function processInfo() {
    var instId = $("#instId").val();
    var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: "流程图实例",
        width: 800,
        height: 600
    });
}

function addDevTaskFile() {
    var applyId=mini.get("applyId").getValue();
    if(!applyId) {
        mini.alert('请先点击‘保存草稿’进行表单的保存！');
        return;
    }
    mini.open({
        title: "文件上传",
        url: jsUseCtxPath + "/devTask/core/fileUploadWindow.do?applyId="+applyId,
        width: 750,
        height: 450,
        showModal:false,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            if(fileListGrid) {
                fileListGrid.load();
            }
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
        if (nodeVars[i].KEY_ == 'nodeName') {
            nodeName = nodeVars[i].DEF_VAL_;
        }
    }
    editOrTaskEditProcess();
}

function editOrTaskEditProcess() {
    if(action=='edit') {
        mini.get("creatorRespOpinion").setEnabled(false);
        mini.get("xxbTechOpinion").setEnabled(false);
        mini.get("xxbTechOpinionReason").setEnabled(false);
        mini.get("xxbRespOpinion").setEnabled(false);
        mini.get("planStartTime").setEnabled(false);
        mini.get("planEndTime").setEnabled(false);
        mini.get("gzlEvaluate").setEnabled(false);
        mini.get("devRespUserId").setEnabled(false);
        mini.get("finishDesc").setEnabled(false);
        $("#xxbTechOpinionDiv").css("background-color","#F2F2F2");
    } else if(action=='task') {
        if(nodeName=='bianzhi') {
            mini.get("creatorRespOpinion").setEnabled(false);
            mini.get("xxbTechOpinion").setEnabled(false);
            mini.get("planStartTime").setEnabled(false);
            mini.get("planEndTime").setEnabled(false);
            mini.get("gzlEvaluate").setEnabled(false);
            mini.get("xxbTechOpinionReason").setEnabled(false);
            mini.get("xxbRespOpinion").setEnabled(false);
            mini.get("finishDesc").setEnabled(false);
            mini.get("devRespUserId").setEnabled(false);
            $("#xxbTechOpinionDiv").css("background-color","#F2F2F2");
        }
        if(nodeName=='creatorRespSH') {
            mini.get("xxbTechOpinion").setEnabled(false);
            mini.get("planStartTime").setEnabled(false);
            mini.get("planEndTime").setEnabled(false);
            mini.get("gzlEvaluate").setEnabled(false);
            mini.get("xxbTechOpinionReason").setEnabled(false);
            mini.get("xxbRespOpinion").setEnabled(false);
            mini.get("finishDesc").setEnabled(false);
            mini.get("devRespUserId").setEnabled(false);
            $("#xxbTechOpinionDiv").css("background-color","#F2F2F2");
        }
        if(nodeName=='xxbJSAnalyse') {
            mini.get("addFile").setEnabled(false);
            mini.get("devType").setEnabled(false);
            mini.get("applyName").setEnabled(false);
            mini.get("applyReason").setEnabled(false);
            mini.get("applyValue").setEnabled(false);
            mini.get("creatorRespOpinion").setEnabled(false);
            mini.get("xxbRespOpinion").setEnabled(false);
            mini.get("planStartTime").setEnabled(false);
            mini.get("planEndTime").setEnabled(false);
            mini.get("gzlEvaluate").setEnabled(false);
            //mini.get("devRespUserId").setEnabled(false);//放开，可以提前临时指定，便于派工
            mini.get("finishDesc").setEnabled(false);
            mini.get("flowChange").setEnabled(true);
        }
        if(nodeName=='xxbRespSH') {
            mini.get("addFile").setEnabled(false);
            mini.get("devType").setEnabled(false);
            mini.get("applyName").setEnabled(false);
            mini.get("applyReason").setEnabled(false);
            mini.get("applyValue").setEnabled(false);
            mini.get("creatorRespOpinion").setEnabled(false);
            mini.get("planStartTime").setEnabled(false);
            mini.get("planEndTime").setEnabled(false);
            mini.get("gzlEvaluate").setEnabled(false);
            mini.get("devRespUserId").setEnabled(false);
            mini.get("finishDesc").setEnabled(false);
        }
        if(nodeName=='fgldSP') {
            mini.get("addFile").setEnabled(false);
            mini.get("devType").setEnabled(false);
            mini.get("applyName").setEnabled(false);
            mini.get("applyReason").setEnabled(false);
            mini.get("applyValue").setEnabled(false);
            mini.get("creatorRespOpinion").setEnabled(false);
            mini.get("xxbRespOpinion").setEnabled(false);
            mini.get("xxbTechOpinion").setEnabled(false);
            mini.get("planStartTime").setEnabled(false);
            mini.get("planEndTime").setEnabled(false);
            mini.get("gzlEvaluate").setEnabled(false);
            mini.get("xxbTechOpinionReason").setEnabled(false);
            mini.get("devRespUserId").setEnabled(false);
            mini.get("finishDesc").setEnabled(false);
            $("#xxbTechOpinionDiv").css("background-color","#F2F2F2");
        }
        if(nodeName=='taskAssign') {
            mini.get("addFile").setEnabled(false);
            mini.get("devType").setEnabled(false);
            mini.get("applyName").setEnabled(false);
            mini.get("applyReason").setEnabled(false);
            mini.get("applyValue").setEnabled(false);
            mini.get("creatorRespOpinion").setEnabled(false);
            mini.get("xxbRespOpinion").setEnabled(false);
            mini.get("xxbTechOpinion").setEnabled(false);
            mini.get("xxbTechOpinionReason").setEnabled(false);
            mini.get("finishDesc").setEnabled(false);
            $("#xxbTechOpinionDiv").css("background-color","#F2F2F2");
        }
        if(nodeName=='taskDev') {
            mini.get("addFile").setEnabled(false);
            mini.get("devType").setEnabled(false);
            mini.get("applyName").setEnabled(false);
            mini.get("applyReason").setEnabled(false);
            mini.get("applyValue").setEnabled(false);
            mini.get("creatorRespOpinion").setEnabled(false);
            mini.get("xxbRespOpinion").setEnabled(false);
            mini.get("xxbTechOpinion").setEnabled(false);
            mini.get("planStartTime").setEnabled(false);
            mini.get("planEndTime").setEnabled(false);
            mini.get("gzlEvaluate").setEnabled(false);
            mini.get("xxbTechOpinionReason").setEnabled(false);
            mini.get("devRespUserId").setEnabled(false);
            $("#xxbTechOpinionDiv").css("background-color","#F2F2F2");
        }
        if(nodeName=='creatorConfirm') {
            mini.get("addFile").setEnabled(false);
            mini.get("devType").setEnabled(false);
            mini.get("applyName").setEnabled(false);
            mini.get("applyReason").setEnabled(false);
            mini.get("applyValue").setEnabled(false);
            mini.get("creatorRespOpinion").setEnabled(false);
            mini.get("xxbRespOpinion").setEnabled(false);
            mini.get("xxbTechOpinion").setEnabled(false);
            mini.get("planStartTime").setEnabled(false);
            mini.get("planEndTime").setEnabled(false);
            mini.get("gzlEvaluate").setEnabled(false);
            mini.get("xxbTechOpinionReason").setEnabled(false);
            mini.get("devRespUserId").setEnabled(false);
            mini.get("finishDesc").setEnabled(false);
            $("#xxbTechOpinionDiv").css("background-color","#F2F2F2");
        }
        if (nodeName == 'jySP') {
            formDevTask.setEnabled(false);
            mini.get("addFile").setEnabled(false);
            $("#xxbTechOpinionDiv").css("background-color","#F2F2F2");
        }
    }
}



function returnDevTaskPreviewSpan(fileName,fileId,formId,coverContent) {
    var fileType=getFileType(fileName);
    var s='';
    if(fileType=='other'){
        s = '<span  title="预览" style="color: silver" >预览</span>';
    }else if(fileType=='pdf'){
        var url='/devTask/core/devPdfPreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url+ '\')">预览</span>';
    }else if(fileType=='office'){
        var url='/devTask/core/devOfficePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url +'\')">预览</span>';
    }else if(fileType=='pic'){
        var url='/devTask/core/devImagePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url + '\')">预览</span>';
    }
    return s;
}