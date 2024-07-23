var stageName="";
$(function () {
    initForm();
    //明细入口
    if (action == 'detail') {
        formJszb.setEnabled(false);
        $("#detailToolBar").show();
        //非草稿放开流程信息查看按钮
        if(status!='DRAFTED'&&status!="undefined") {
            $("#processInfo").show();
        }
    } else if(action=='task') {
        taskActionProcess();
    }else if(action=='edit'){

    }else if(action=='change'){
        $("#changeToolBar").show();
    }
});

//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("formJszb");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos=[];
    formData.vars=[{key:'jszxlb',val:formData.jszxlb}];
    return formData;
}

function saveChange() {
    //保存变更
    var formData = getData();

    $.ajax({
        url: jsUseCtxPath + '/zhgl/core/jszb/save.do',
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
                    message="数据保存失败，"+data.message;
                }

                mini.showMessageBox({
                    title: "提示信息",
                    iconCls: "mini-messagebox-info",
                    buttons: ["ok"],
                    message: message,
                    callback: function (action) {
                        if(action=="ok"){
                            if(data.success) {
                                window.location.reload();
                            }
                        }
                    }
                });
            }
        }
    });
}

//保存草稿
function saveJszb(e) {
    var zbbmId=$.trim(mini.get("zbbmId").getText());
    if (!zbbmId) {
        mini.alert("请选择特批部门！");
        return;
    }
    window.parent.saveDraft(e);
}


//启动流程
function startJszbProcess(e) {
    var formValid = commitProcessValidCheck();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    if(!jszbId) {
        mini.alert("请先点击“保存草稿”，并上传“立项评审”阶段的文件！");
        return;
    }
    //立项评审阶段的附件必填
    var  result = "";
    var  message = "";
    var gslclb=$.trim(mini.get("gslclb").getValue());
    $.ajax({
        url: jsUseCtxPath + '/zhgl/core/jszb/checkJszbFileRequired.do?jszbId=' + jszbId+ "&type="+gslclb+"&jdType=立项评审",
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            result = data.result;
            message = data.message;
        }
    });

    if (result == "false"){
        mini.alert(message);
        return;
    }
    window.parent.startProcess(e);
}

//流程中的审批或者下一步
function jszbApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (stageName == 'bianzhi') {
        var formValid = commitProcessValidCheck();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        if(!jszbId) {
            mini.alert("请先点击“保存草稿”，并上传“立项评审”阶段的文件！");
            return;
        }
        //立项评审阶段的附件必填
        var  result = "";
        var  message = "";
        var gslclb=$.trim(mini.get("gslclb").getValue());
        $.ajax({
            url: jsUseCtxPath + '/zhgl/core/jszb/checkJszbFileRequired.do?jszbId=' + jszbId+ "&type="+gslclb+"&jdType=立项评审",
            type: 'get',
            async: false,
            contentType: 'application/json',
            success: function (data) {
                result = data.result;
                message = data.message;
            }
        });

        if (result == "false"){
            mini.alert(message);
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

function operateJszbFile(ckId,ssjd) {
    if(!jszbId) {
        mini.alert('请先点击‘保存草稿’进行表单的保存！');
        return;
    }
    var canOperateFile = false;
    //能编辑文件的场景
    if(action=='edit'||action=='change') {
        canOperateFile=true;
    } else if(action=='task') {
        if(stageName=='bianzhi') {
            canOperateFile=true;
        } else if(stageName=='scqt' && ssjd !='立项评审') {
            canOperateFile=true;
        }
    }
    mini.open({
        title: "上传附件",
        url: jsUseCtxPath + "/zhgl/core/jszb/jszbFileWindow.do?jszbId=" + jszbId+ "&canOperateFile=" + canOperateFile
            + "&ckId="+ckId,
        width: 800,
        height: 600,
        showModal: false,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
        }
    });
}

function taskActionProcess() {
    //获取上一环节的结果和处理人
    bpmPreTaskTipInForm();

    var nodeVarsObj = getProcessNodeVars();
    stageName = nodeVarsObj.stageName;
    if(stageName == 'bianzhi') {

    }else if (stageName == 'scqt'){
        formJszb.setEnabled(false);
    } else {
        formJszb.setEnabled(false);
    }
}


function returnJszbPreviewSpan(fileName,fileId,formId,coverContent) {
    var fileType=getFileType(fileName);
    var s='';
    if(fileType=='other'){
        s = '<span  title="预览" style="color: silver" >预览</span>';
    }else if(fileType=='pdf'){
        var url='/zhgl/core/jszb/jszbPdfPreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url+ '\')">预览</span>';
    }else if(fileType=='office'){
        var url='/zhgl/core/jszb/jszbOfficePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url +'\')">预览</span>';
    }else if(fileType=='pic'){
        var url='/zhgl/core/jszb/jszbImagePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url + '\')">预览</span>';
    }

    return s;
}


function initForm() {
    $.ajaxSettings.async = false;
    if(jszbId) {
        var url = jsUseCtxPath + "/zhgl/core/jszb/getJszbDetail.do";
        $.post(
            url,
            {jszbId: jszbId},
            function (json) {
                formJszb.setData(json);
            });
    } else {
        mini.get("gslclb").setValue(type);
    }
    $.ajaxSettings.async = true;
}
function getProcessNodeVars() {
    var nodeVarsObj = {};
    //获取环境变量
    if (!nodeVarsStr) {
        nodeVarsStr = "[]";
    }
    var nodeVars = $.parseJSON(nodeVarsStr);
    for (var i = 0; i < nodeVars.length; i++) {
        nodeVarsObj[nodeVars[i].KEY_] = nodeVars[i].DEF_VAL_;
    }

    return nodeVarsObj;
}

//流程提交时检验表单是否必填
function commitProcessValidCheck() {
    var gslclb=$.trim(mini.get("gslclb").getValue());
    if(!gslclb) {
        return {"result": false, "message": "请选择流程类别"};
    }
    var zbbmId = $.trim(mini.get("zbbmId").getValue());
    if (!zbbmId) {
        return {"result": false, "message": "请选择特批部门"};
    }
    var jbrId=$.trim(mini.get("jbrId").getValue());
    if(!jbrId) {
        return {"result": false, "message": "请选择经办人"};
    }
    var zbName=$.trim(mini.get("zbName").getValue());
    if(!zbName) {
        return {"result": false, "message": "请填写特批项目名称"};
    }

    var zbjg=$.trim(mini.get("zbjg").getValue());
    if(!zbjg) {
        return {"result": false, "message": "请填写项目预算"};
    }

    var cgfs=$.trim(mini.get("cgfs").getValue());
    if(!cgfs) {
        return {"result": false, "message": "请选择拟采用采购方式"};
    }

    var yjFinishTime=$.trim(mini.get("yjFinishTime").getValue());
    if(!yjFinishTime) {
        return {"result": false, "message": "请选择预计完成时间"};
    }
    return {"result": true};
}