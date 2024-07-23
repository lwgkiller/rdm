var isBianzhi="";
var isZYSH="";
$(function () {
    if(qbgzId) {
        var url = jsUseCtxPath + "/Info/Qbgz/getQbgzDetail.do";
        $.post(
            url,
            {qbgzId: qbgzId},
            function (json) {
                formQbgz.setData(json);
            });
    }
    mini.get("qbLevel").setEnabled(false);
    //变更入口
    if (action == 'change') {
        $("#changeToolBar").show();
        mini.get("qbLevel").setEnabled(true);
    } else if(action=='task') {
        taskActionProcess();
    }else if(action == 'detail'){
        formQbgz.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        $("#detailToolBar").show();
        if (status != 'DRAFTED') {
            $("#processInfo").show();
        }
    }
});
function getData() {
    var formData = _GetFormJsonMini("formQbgz");
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    // formData.bos=[];
    // formData.vars=[{key:'companyName',val:formData.companyName}];
    return formData;
}

function saveQbgz(e) {
    var companyName = mini.get("companyName").getValue();
    if(!$.trim(companyName)) {
        mini.alert(qbgzEdit_name2);
        return;
    }
    var projectName = mini.get("projectName").getValue();
    if(!$.trim(projectName)) {
        mini.alert(qbgzEdit_name3);
        return;
    }
    var qbgzType = mini.get("qbgzType").getValue();
    if(!$.trim(qbgzType)) {
        mini.alert(qbgzEdit_name4);
        return;
    }
    window.parent.saveDraft(e);
}
function saveChange(qbgzId) {
    var formValid = validZysh();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    var formData = _GetFormJsonMini("formQbgz");
    $.ajax({
        url: jsUseCtxPath + '/Info/Qbgz/saveQbgz.do?qbgzId=' + qbgzId,
        type: 'post',
        async: false,
        data:mini.encode(formData),
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                var message="";
                if(data.success) {
                    message=qbgzEdit_name5;
                } else {
                    message=qbgzEdit_name6+data.message;
                }

                mini.showMessageBox({
                    title: qbgzEdit_name7,
                    iconCls: "mini-messagebox-info",
                    buttons: ["ok"],
                    message: message,
                    callback: function (action) {
                        if(action=="ok"){
                            CloseWindow("ok");
                        }
                    }
                });
            }
        }
    });
}
function validQbgz() {
    var companyName=$.trim(mini.get("companyName").getValue())
    if(!companyName) {
        return {"result": false, "message": qbgzEdit_name8};
    }
    var projectName=$.trim(mini.get("projectName").getValue())
    if(!projectName) {
        return {"result": false, "message": qbgzEdit_name9};
    }
    var qbgzType=$.trim(mini.get("qbgzType").getValue())
    if(!qbgzType) {
        return {"result": false, "message": qbgzEdit_name10};
    }
    var qbName=$.trim(mini.get("qbName").getValue())
    if(!qbName) {
        return {"result": false, "message": qbgzEdit_name11};
    }
    var qbContent=$.trim(mini.get("qbContent").getValue())
    if(!qbContent) {
        return {"result": false, "message": qbgzEdit_name12};
    }
    var qbComment=$.trim(mini.get("qbComment").getValue())
    if(!qbComment) {
        return {"result": false, "message": qbgzEdit_name13};
    }
    var fileListGrid=$.trim(mini.get("fileListGrid").getData());
    if(!fileListGrid) {
        return {"result": false, "message": qbgzEdit_name14};
    }
    return {"result": true};
}
function startQbgzProcess(e) {
    var formValid = validQbgz();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.startProcess(e);
}

function qbgzApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (isBianzhi == 'yes') {
        var formValid = validQbgz();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    //检查通过
    window.parent.approve();
}
function validZysh() {
    var qbLevel=$.trim(mini.get("qbLevel").getValue())
    if(!qbLevel) {
        return {"result": false, "message": qbgzEdit_name15};
    }

    return {"result": true};
}


function zyshApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (isZYSH == 'yes') {
        var formValid = validZysh();
        if (!formValid.result) {
            mini.alert(formValid.message);
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
        title: qbgzEdit_name16,
        width: 800,
        height: 600
    });
}
function fileupload() {
    var qbgzId=mini.get("qbgzId").getValue();
    if(!qbgzId) {
        mini.alert(qbgzEdit_name17);
        return;
    }
    mini.open({
        title: qbgzEdit_name18,
        url: jsUseCtxPath + "/Info/Qbgz/openUploadWindow.do?qbgzId="+qbgzId,
        width: 850,
        height: 550,
        showModal:true,
        allowResize: true,
        ondestroy: function () {
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
        if (nodeVars[i].KEY_ == 'isZYSH') {
            isZYSH = nodeVars[i].DEF_VAL_;
        }

    }
    if(isBianzhi!= 'yes') {
        formQbgz.setEnabled(false);
        mini.get("addFile").setEnabled(false);
    }
    if(isZYSH=='yes') {
        mini.get("qbLevel").setEnabled(true);
    } else {
        mini.get("qbLevel").setEnabled(false);
    }
}

function returnQbgzPreviewSpan(fileName,fileId,formId,coverContent) {
    var fileType=getFileType(fileName);
    var s='';
    if(fileType=='other'){
        s = '<span  title=' + qbgzEdit_name19 + ' style="color: silver" >' + qbgzEdit_name19 + '</span>';
    }else if(fileType=='pdf'){
        var url='/Info/Qbgz/qbgzPdfPreview';
        s = '<span  title=' + qbgzEdit_name19 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url+ '\')">' + qbgzEdit_name19 + '</span>';
    }else if(fileType=='office'){
        var url='/Info/Qbgz/qbgzOfficePreview';
        s = '<span  title=' + qbgzEdit_name19 + ' style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url +'\')">' + qbgzEdit_name19 + '</span>';
    }else if(fileType=='pic'){
        var url='/Info/Qbgz/qbgzImagePreview';
        s = '<span  title=' + qbgzEdit_name19 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url + '\')">' + qbgzEdit_name19 + '</span>';
    }
    return s;
}

function downLoadstandardChangeFile(fileName,fileId,formId) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + '/Info/Qbgz/qbgzPdfPreview.do?action=download');
    var inputFileName = $("<input>");
    inputFileName.attr("type", "hidden");
    inputFileName.attr("name", "fileName");
    inputFileName.attr("value", fileName);
    var inputstandardId = $("<input>");
    inputstandardId.attr("type", "hidden");
    inputstandardId.attr("name", "formId");
    inputstandardId.attr("value", formId);
    var inputFileId = $("<input>");
    inputFileId.attr("type", "hidden");
    inputFileId.attr("name", "fileId");
    inputFileId.attr("value", fileId);
    $("body").append(form);
    form.append(inputFileName);
    form.append(inputstandardId);
    form.append(inputFileId);
    form.submit();
    form.remove();
}