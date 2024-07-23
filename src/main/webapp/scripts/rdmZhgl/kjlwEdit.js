var isBianzhi="";
var isbuzhangShenhe ="";
$(function () {
    if(kjlwId) {
        var url = jsUseCtxPath + "/zhgl/core/kjlw/getKjlwDetail.do";
        $.post(
            url,
            {kjlwId: kjlwId},
            function (json) {
                formKjlw.setData(json);
            });
    }
    $.ajaxSettings.async = false;
    //明细入口
    if (action == 'detail') {
        if(ifWriterUser || isZlgcsUser){
            formKjlw.setEnabled(false);
            mini.get("qkmc").setEnabled(true);
            mini.get("qNum").setEnabled(true);
            mini.get("yema").setEnabled(true);
            mini.get("fbTime").setEnabled(true);
            $("#detailToolBar").show();
            $("#saveKjlwWriter").show();
            if(status) {
                $("#processInfo").show();
            }
        }else {
            formKjlw.setEnabled(false);
            mini.get("addFile").setEnabled(false);
            $("#detailToolBar").show();
            //非草稿放开流程信息查看按钮
            if(status!='DRAFTED') {
                $("#processInfo").show();
            }
        }
    } else if(action=='task') {
        taskActionProcess();
    }
    $.ajaxSettings.async = true;
});

//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("formKjlw");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos=[];
    formData.vars=[{key:'kjlwName',val:formData.rjmqc}];
    return formData;
}

//保存草稿
function saveKjlw(e) {
    var kjlwName=$.trim(mini.get("kjlwName").getValue())
    if (!kjlwName) {
        mini.alert("请填写论文名称！");
        return;
    }
    window.parent.saveDraft(e);
}

//检验表单是否必填
function validKjlw() {
    var kjlwName=$.trim(mini.get("kjlwName").getValue())
    if(!kjlwName) {
        return {"result": false, "message": "请填写论文名称"};
    }
    var ifzlrz=$.trim(mini.get("ifzlrz").getValue())
    if(!ifzlrz) {
        return {"result": false, "message": "请选择是否申请专利或软著"};
    }
    var fbqk = $.trim(mini.get("fbqk").getValue())
    if (!fbqk) {
        return {"result": false, "message": '请填写拟发表期刊'};
    }
    var writerId=$.trim(mini.get("writerId").getValue())
    if(!writerId) {
        return {"result": false, "message": "请选择作者"};
    }
    var cqmc=$.trim(mini.get("cqmc").getValue())
    if(!cqmc) {
        return {"result": false, "message": "请填写知识产权及名称"};
    }
    var lyxm=$.trim(mini.get("lyxm").getValue())
    if(!lyxm) {
        return {"result": false, "message": "请填写来源项目"};
    }
    var zynr=$.trim(mini.get("zynr").getValue())
    if(!zynr) {
        return {"result": false, "message": "请填写主要内容"};
    }
    var fileData =fileListGrid.getData();
    if(fileData.length <= 0){
        return {"result": false, "message": "请添加附件"};
    }
    return {"result": true};
}
//检验表单是否必填
function validKjlwWriter() {
    var qkmc=$.trim(mini.get("qkmc").getValue())
    if(!qkmc) {
        return {"result": false, "message": "请填写期刊名称"};
    }
    var qNum=$.trim(mini.get("qNum").getValue())
    if(!qNum) {
        return {"result": false, "message": "请填写期号"};
    }
    var yema=$.trim(mini.get("yema").getValue())
    if(!yema) {
        return {"result": false, "message": "请填写页码"};
    }
    var fbTime=$.trim(mini.get("fbTime").getValue())
    if(!fbTime) {
        return {"result": false, "message": "请选择发表时间"};
    }
    return {"result": true};
}

//启动流程
function startKjlwProcess(e) {
    var formValid = validKjlw();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.startProcess(e);
}

//流程中暂存信息（如编制阶段）
function saveKjlwInProcess() {
    var formValid = validKjlw();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    var formData = _GetFormJsonMini("formKjlw");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    $.ajax({
        url: jsUseCtxPath + '/zhgl/core/kjlw/saveKjlw.do',
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
//作者保存
function saveKjlwWriter() {
    var formValid = validKjlwWriter();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    var formData = _GetFormJsonMini("formKjlw");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    $.ajax({
        url: jsUseCtxPath + '/zhgl/core/kjlw/saveKjlw.do',
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
function kjlwApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (isBianzhi == 'yes') {
        var formValid = validKjlw();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }

    //技术部长审核是否加密，如果是否的话不许通过
    if(isbuzhangShenhe == 'yes'){
        var ifsm = $.trim(mini.get("ifshemi").getValue());
        if(!ifsm) {
            mini.alert("请选择是否涉密!") ;
            return;
        }
        if(ifsm != "no"){
           mini.alert("如涉密请驳回至起点修改!") ;
           return;
        }

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

function addKjlwFile() {
    var kjlwId=mini.get("kjlwId").getValue();
    if(!kjlwId) {
        mini.alert('请先点击‘保存草稿’进行表单的保存！');
        return;
    }
    mini.open({
        title: "文件上传",
        url: jsUseCtxPath + "/zhgl/core/kjlw/fileUploadWindow.do?kjlwId="+kjlwId,
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
        if (nodeVars[i].KEY_ == 'isBianzhi') {
            isBianzhi = nodeVars[i].DEF_VAL_;
        }
        if(nodeVars[i].KEY_ == 'isbuzhangShenhe'){
            isbuzhangShenhe= nodeVars[i].DEF_VAL_;
        }
    }
    if(isBianzhi!= 'yes') {
        formKjlw.setEnabled(false);
        mini.get("addFile").setEnabled(false);
    }
    if(isbuzhangShenhe == 'yes'){
        mini.get("ifshemi").setEnabled(true);
    }

}



function returnKjlwPreviewSpan(fileName,fileId,formId,coverContent) {
    var fileType=getFileType(fileName);
    var s='';
    if(fileType=='other'){
        s = '<span  title="预览" style="color: silver" >预览</span>';
    }else if(fileType=='pdf'){
        var url='/zhgl/core/kjlw/kjlwPdfPreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url+ '\')">预览</span>';
    }else if(fileType=='office'){
        var url='/zhgl/core/kjlw/kjlwOfficePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url +'\')">预览</span>';
    }else if(fileType=='pic'){
        var url='/zhgl/core/kjlw/kjlwImagePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url + '\')">预览</span>';
    }

    return s;
}
//下载文档
function downKjlwLoadFile(record) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/zhgl/core/kjlw/fileDownload.do?action=download");
    var inputFileName = $("<input>");
    inputFileName.attr("type", "hidden");
    inputFileName.attr("name", "fileName");
    inputFileName.attr("value", record.fileName);
    var standardId = $("<input>");
    standardId.attr("type", "hidden");
    standardId.attr("name", "standardId");
    standardId.attr("value", record.kjlwId);
    var fileId = $("<input>");
    fileId.attr("type", "hidden");
    fileId.attr("name", "fileId");
    fileId.attr("value", record.id);
    $("body").append(form);
    form.append(inputFileName);
    form.append(standardId);
    form.append(fileId);
    form.submit();
    form.remove();
}

