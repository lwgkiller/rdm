var isBianzhi="";
var iszlBianzhi="";
$(function () {
    if(rjzzId) {
        var url = jsUseCtxPath + "/zhgl/core/rjzz/getRjzzDetail.do";
        $.post(
            url,
            {rjzzId: rjzzId},
            function (json) {
                formRjzz.setData(json);
            });
    }
    if(!isZlgcsUser){
        mini.get("djTime").setEnabled(false);
        mini.get("djNum").setEnabled(false);
        mini.get("zsNum").setEnabled(false);
    }
    //明细入口
    if (action == 'detail') {
        if(isZlgcsUser){
            formRjzz.setEnabled(false);
            mini.get("djTime").setEnabled(true);
            mini.get("djNum").setEnabled(true);
            mini.get("zsNum").setEnabled(true);
            $("#detailToolBar").show();
            $("#saveRjzzZlgcs").show();
            if(status!='undefined') {
                $("#processInfo").show();
            }
        }else {
            formRjzz.setEnabled(false);
            mini.get("addFile").setEnabled(false);
            $("#detailToolBar").show();
            //非草稿放开流程信息查看按钮
            if(status!='undefined'&&status!='DRAFTED') {
                $("#processInfo").show();
            }
        }
    } else if(action=='task') {
        taskActionProcess();
    }
});

//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("formRjzz");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos=[];
    formData.vars=[{key:'rjmqc',val:formData.rjmqc}];
    return formData;
}

//保存草稿
function saveRjzz(e) {
    var rjmqc=$.trim(mini.get("rjmqc").getValue())
    if (!rjmqc) {
        mini.alert("请填写软件名全称！");
        return;
    }
    window.parent.saveDraft(e);
}

//检验表单是否必填
function validRjzz() {
    var rjmqc=$.trim(mini.get("rjmqc").getValue())
    if(!rjmqc) {
        return {"result": false, "message": "请填写软件名全称"};
    }
    var rjmjc=$.trim(mini.get("rjmjc").getValue())
    if(!rjmjc) {
        return {"result": false, "message": "请填写软件名简称"};
    }
    var zpsm=$.trim(mini.get("zpsm").getValue())
    if(!zpsm) {
        return {"result": false, "message": "请选择软件作品说明"};
    }
    var fbzt=$.trim(mini.get("fbzt").getValue())
    if(!fbzt) {
        return {"result": false, "message": "请选择发表状态"};
    }
    var rjbbh=$.trim(mini.get("rjbbh").getValue())
    if(!rjbbh) {
        return {"result": false, "message": "请填写软件版本号"};
    }
    var wcTime = $.trim(mini.get("wcTime").getValue())
    if (!wcTime) {
        return {"result": false, "message": '请选择开发完成日期'};
    }
    var rjflh=$.trim(mini.get("rjflh").getValue())
    if(!rjflh) {
        return {"result": false, "message": "请填写软件分类号"};
    }
    var rjkfIds=$.trim(mini.get("rjkfIds").getValue())
    if(!rjkfIds) {
        return {"result": false, "message": "请选择软件开发者"};
    }
    var ddsj=$.trim(mini.get("ddsj").getValue())
    if(!ddsj) {
        return {"result": false, "message": "请填写首次发表时间/地点"};
    }
    var zzqr=$.trim(mini.get("zzqr").getValue())
    if(!zzqr) {
        return {"result": false, "message": "请填写著作权人"};
    }
    var lxfs=$.trim(mini.get("lxfs").getValue())
    if(!lxfs) {
        return {"result": false, "message": "请填写联系人/联系方式"};
    }
    var rjgn=$.trim(mini.get("rjgn").getValue())
    if(!rjgn) {
        return {"result": false, "message": "请填写软件功能"};
    }
    var jstd=$.trim(mini.get("jstd").getValue())
    if(!jstd) {
        return {"result": false, "message": "请填写技术特点"};
    }
    var yjhj=$.trim(mini.get("yjhj").getValue())
    if(!yjhj) {
        return {"result": false, "message": "请填写硬件环境"};
    }
    var rjhj=$.trim(mini.get("rjhj").getValue())
    if(!rjhj) {
        return {"result": false, "message": "请填写软件环境"};
    }
    var bjyy=$.trim(mini.get("bjyy").getValue())
    if(!bjyy) {
        return {"result": false, "message": "请填写编程语言及版本"};
    }
    var dmhs=$.trim(mini.get("dmhs").getValue())
    if(!dmhs) {
        return {"result": false, "message": "请填写软件代码行数"};
    }
    var yycp=$.trim(mini.get("yycp").getValue())
    if(!yycp) {
        return {"result": false, "message": "请填写应用产品"};
    }
    return {"result": true};
}
//检验表单是否必填
function validRjzzZlgcs() {
    var djTime=$.trim(mini.get("djTime").getValue())
    if(!djTime) {
        return {"result": false, "message": "请选择登记时间"};
    }
    var djNum=$.trim(mini.get("djNum").getValue())
    if(!djNum) {
        return {"result": false, "message": "请填写登记号"};
    }
    var zsNum=$.trim(mini.get("zsNum").getValue())
    if(!zsNum) {
        return {"result": false, "message": "请填写证书号"};
    }
    return {"result": true};
}

//启动流程
function startRjzzProcess(e) {
    var formValid = validRjzz();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.startProcess(e);
}

//流程中暂存信息（如编制阶段）
function saveRjzzInProcess() {
    var formValid = validRjzz();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    var formData = _GetFormJsonMini("formRjzz");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    $.ajax({
        url: jsUseCtxPath + '/zhgl/core/rjzz/saveRjzz.do',
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
//专利工程师保存
function saveRjzzZlgcs() {
    var formValid = validRjzzZlgcs();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    var formData = _GetFormJsonMini("formRjzz");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    $.ajax({
        url: jsUseCtxPath + '/zhgl/core/rjzz/saveRjzz.do',
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
function rjzzApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (isBianzhi == 'yes') {
        var formValid = validRjzz();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    if (iszlBianzhi == 'yes') {
        var formValidzl = validRjzzZlgcs();
        if (!formValidzl.result) {
            mini.alert(formValidzl.message);
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

function addRjzzFile() {
    var rjzzId=mini.get("rjzzId").getValue();
    if(!rjzzId) {
        mini.alert('请先点击‘保存草稿’进行表单的保存！');
        return;
    }
    mini.open({
        title: "文件上传",
        url: jsUseCtxPath + "/zhgl/core/rjzz/fileUploadWindow.do?rjzzId="+rjzzId,
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
        if (nodeVars[i].KEY_ == 'iszlBianzhi') {
            iszlBianzhi = nodeVars[i].DEF_VAL_;
        }
    }
    if(isBianzhi!= 'yes') {
        formRjzz.setEnabled(false);
        mini.get("addFile").setEnabled(false);
    }
    if(iszlBianzhi == 'yes'){
        mini.get("djTime").setEnabled(true);
        mini.get("djNum").setEnabled(true);
        mini.get("zsNum").setEnabled(true);
    }
}



function returnRjzzPreviewSpan(fileName,fileId,formId,coverContent) {
    var fileType=getFileType(fileName);
    var s='';
    if(fileType=='other'){
        s = '<span  title="预览" style="color: silver" >预览</span>';
    }else if(fileType=='pdf'){
        var url='/zhgl/core/rjzz/rjzzPdfPreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url+ '\')">预览</span>';
    }else if(fileType=='office'){
        var url='/zhgl/core/rjzz/rjzzOfficePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url +'\')">预览</span>';
    }else if(fileType=='pic'){
        var url='/zhgl/core/rjzz/rjzzImagePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url + '\')">预览</span>';
    }

    return s;
}
//下载文档
function downRjzzLoadFile(record) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/zhgl/core/rjzz/fileDownload.do?action=download");
    var inputFileName = $("<input>");
    inputFileName.attr("type", "hidden");
    inputFileName.attr("name", "fileName");
    inputFileName.attr("value", record.fileName);
    var standardId = $("<input>");
    standardId.attr("type", "hidden");
    standardId.attr("name", "standardId");
    standardId.attr("value", record.rjzzId);
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
//是否是分管领导
function whetherIsFgld(userRoles) {
    if(!userRoles) {
        return false;
    }
    for (var i = 0; i < userRoles.length; i++) {
        if (userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-LEADER'||userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-BELONG') {
            if(userRoles[i].NAME_.indexOf('分管领导')!=-1) {
                return true;
            }
        }
    }
    return false;
}