var isBianzhi="";
$(function () {
    if(jsmmId) {
        var url = jsUseCtxPath + "/zhgl/core/jsmm/getJsmmDetail.do";
        $.post(
            url,
            {jsmmId: jsmmId},
            function (json) {
                formJsmm.setData(json);
            });
    }
    //明细入口
    if (action == 'detail') {
        formJsmm.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        $("#detailToolBar").show();
        //非草稿放开流程信息查看按钮
        if(status!='DRAFTED') {
            $("#processInfo").show();
        }
    } else if(action=='task') {
        taskActionProcess();
    }
});

//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("formJsmm");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos=[];
    formData.vars=[{key:'jsmmName',val:formData.jsmmName}];
    return formData;
}

//保存草稿
function saveJsmm(e) {
    var jsmmName=$.trim(mini.get("jsmmName").getValue())
    if (!jsmmName) {
        mini.alert("请填写技术秘密名称");
        return;
    }
    window.parent.saveDraft(e);
}

//检验表单是否必填
function validJsmm() {
    var jsmmName=$.trim(mini.get("jsmmName").getValue())
    if(!jsmmName) {
        return {"result": false, "message": "请填写技术秘密名称"};
    }
    var jsfx=$.trim(mini.get("jsfx").getValue())
    if(!jsfx) {
        return {"result": false, "message": "请填写技术研究方向"};
    }
    var jscb=$.trim(mini.get("jscb").getValue())
    if(!jscb) {
        return {"result": false, "message": "请填写技术研发成本"};
    }
    var readUserIds = $.trim(mini.get("readUserIds").getValue())
    if (!readUserIds) {
        return {"result": false, "message": '请选择可见范围人员'};
    }
    var projectNameNumber=$.trim(mini.get("projectNameNumber").getValue())
    if(!projectNameNumber) {
        return {"result": false, "message": "请填写所属项目名称及编号"};
    }
    var finishUserIds = $.trim(mini.get("finishUserIds").getValue())
    if (!finishUserIds) {
        return {"result": false, "message": '请选择完成人员'};
    }
    var projectInfo=$.trim(mini.get("projectInfo").getValue())
    if(!projectInfo) {
        return {"result": false, "message": "请填写所属项目基本情况"};
    }
    var jsxjx=$.trim(mini.get("jsxjx").getValue())
    if(!jsxjx) {
        return {"result": false, "message": "请填写技术先进性"};
    }
    var mmx=$.trim(mini.get("mmx").getValue())
    if(!mmx) {
        return {"result": false, "message": "请填写秘密性"};
    }
    var cygx=$.trim(mini.get("cygx").getValue())
    if(!cygx) {
        return {"result": false, "message": "请填写产业贡献"};
    }
    return {"result": true};
}

//启动流程
function startJsmmProcess(e) {
    var formValid = validJsmm();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.startProcess(e);
}

//流程中暂存信息（如编制阶段）
function saveJsmmInProcess() {
    var formValid = validJsmm();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    var formData = _GetFormJsonMini("formJsmm");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    $.ajax({
        url: jsUseCtxPath + '/zhgl/core/jsmm/saveJsmm.do',
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
function jsmmApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (isBianzhi == 'yes') {
        var formValid = validJsmm();
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

function addJsmmFile() {
    var jsmmId=mini.get("jsmmId").getValue();
    if(!jsmmId) {
        mini.alert('请先点击‘保存草稿’进行表单的保存！');
        return;
    }
    mini.open({
        title: "文件上传",
        url: jsUseCtxPath + "/zhgl/core/jsmm/fileUploadWindow.do?jsmmId="+jsmmId,
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
    }
    if(isBianzhi!= 'yes') {
        formJsmm.setEnabled(false);
        mini.get("addFile").setEnabled(false);
    }
}



function returnJsmmPreviewSpan(fileName,fileId,formId,coverContent) {
    var fileType=getFileType(fileName);
    var s='';
    if(fileType=='other'){
        s = '<span  title="预览" style="color: silver" >预览</span>';
    }else if(fileType=='pdf'){
        var url='/zhgl/core/jsmm/jsmmPdfPreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url+ '\')">预览</span>';
    }else if(fileType=='office'){
        var url='/zhgl/core/jsmm/jsmmOfficePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url +'\')">预览</span>';
    }else if(fileType=='pic'){
        var url='/zhgl/core/jsmm/jsmmImagePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url + '\')">预览</span>';
    }
    return s;
}