var isBianzhi="";
$(function () {
    if(jstbId) {
        var url = jsUseCtxPath + "/rdmZhgl/Jstb/getJstbDetail.do";
        $.post(
            url,
            {jstbId: jstbId},
            function (json) {
                formJstb.setData(json);
            });
    }
    //变更入口
    if (action == 'change') {
        $("#changeToolBar").show();
        //非草稿放开流程信息查看按钮
    } else if(action=='task') {
        taskActionProcess();
    }
});
function getData() {
    var formData = _GetFormJsonMini("formJstb");
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos=[];
    formData.vars=[{key:'jstbTitle',val:formData.jstbTitle}];
    return formData;
}

function saveJstb(e) {
    var title = mini.get("jstbTitle").getValue();
    if(!$.trim(title)) {
        mini.alert('请填写通知标题！');
        return;
    }
    var relatedDeptIds = mini.get("depSelectId").getValue();
    if(!$.trim(relatedDeptIds)) {
        mini.alert('请选择发放部门！');
        return;
    }
    window.parent.saveDraft(e);
}
function saveChange(jstbId) {
    var formData = _GetFormJsonMini("formJstb");
    $.ajax({
        url: jsUseCtxPath + '/rdmZhgl/Jstb/saveJstb.do?jstbId=' + jstbId,
        type: 'post',
        async: false,
        data:mini.encode(formData),
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                var message="";
                if(data.success) {
                    message="数据变更成功";
                } else {
                    message="数据变更失败"+data.message;
                }

                mini.showMessageBox({
                    title: "提示信息",
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
function validJstb() {
    var jstbTitle=$.trim(mini.get("jstbTitle").getValue())
    if(!jstbTitle) {
        return {"result": false, "message": "请填写技术管理通报名称"};
    }
    var jstbContent=$.trim(mini.get("jstbContent").getValue())
    if(!jstbContent) {
        return {"result": false, "message": "请填写技术管理通报内容"};
    }
    var fileListGrid=$.trim(mini.get("fileListGrid").getData());
    if(!fileListGrid) {
        return {"result": false, "message": "请添加附件"};
    }
    return {"result": true};
}
function startJstbProcess(e) {
    var formValid = validJstb();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.startProcess(e);
}

function jstbApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (isBianzhi == 'yes') {
        var formValid = validJstb();
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
        title: "流程图实例",
        width: 800,
        height: 600
    });
}
function fileupload() {
    var jstbId=mini.get("jstbId").getValue();
    if(!jstbId) {
        mini.alert('请先点击‘保存草稿’进行表单的保存！');
        return;
    }
    mini.open({
        title: "添加附件",
        url: jsUseCtxPath + "/rdmZhgl/Jstb/openUploadWindow.do?jstbId="+jstbId,
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
    }
    if(isBianzhi!= 'yes') {
        formJstb.setEnabled(false);
        mini.get("addFile").setEnabled(false);
    }
}

function returnJstbPreviewSpan(fileName,fileId,formId,coverContent) {
    var fileType=getFileType(fileName);
    var s='';
    if(fileType=='other'){
        s = '<span  title="预览" style="color: silver" >预览</span>';
    }else if(fileType=='pdf'){
        var url='/rdmZhgl/Jstb/jstbPdfPreview';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url+ '\')">预览</span>';
    }else if(fileType=='office'){
        var url='/rdmZhgl/Jstb/jstbOfficePreview';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url +'\')">预览</span>';
    }else if(fileType=='pic'){
        var url='/rdmZhgl/Jstb/jstbImagePreview';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url + '\')">预览</span>';
    }
    return s;
}

