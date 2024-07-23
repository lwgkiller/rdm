var stageName="";

$(function () {
    if(qbgzId) {
        var url = jsUseCtxPath + "/Info/Qbsj/getQbsjDetail.do";
        $.post(
            url,
            {qbgzId: qbgzId},
            function (json) {
                formQbgz.setData(json);
                if(json.bigTypeName) {
                    bigTypeValueChanged();
                }
            });
    }

    //变更入口
    if (action == 'edit') {

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
    var formValid = valid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.saveDraft(e);
}

function startQbgzProcess(e) {
    var formValid = valid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    var fileListData = fileListGrid.getData();
    if(!fileListData || fileListData.length==0) {
        mini.alert("请上传doc/docx格式的情报附件！");
        return;
    }
    window.parent.startProcess(e);
}

function valid() {
    var qbName = mini.get("qbName").getValue();
    if(!$.trim(qbName)) {
        return {"result": false, "message": "请填写情报名称！"};
    }
    var bigTypeName = mini.get("bigTypeName").getValue();
    if(!$.trim(bigTypeName)) {
        return {"result": false, "message": "请选择情报大类！"};
    }
    var qbTypeId = mini.get("qbTypeId").getValue();
    if(!$.trim(qbTypeId)) {
        return {"result": false, "message": "请选择情报小类！"};
    }
    var qbValue = mini.get("qbValue").getValue();
    if(!$.trim(qbValue)) {
        return {"result": false, "message": "请选择情报价值！"};
    }
    var qbSource = mini.get("qbSource").getValue();
    if(!$.trim(qbSource)) {
        return {"result": false, "message": "请选择情报来源！"};
    }
    var qbKKX = mini.get("qbKKX").getValue();
    if(!$.trim(qbKKX)) {
        return {"result": false, "message": "请选择情报可靠性！"};
    }
    var qbZQD = mini.get("qbZQD").getValue();
    if(!$.trim(qbZQD)) {
        return {"result": false, "message": "请选择情报准确度！"};
    }
    return {"result": true};
}

function qbgzApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (stageName == 'bianZhi' || stageName == 'qbgcsSH') {
        var formValid = valid();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        var fileListData = fileListGrid.getData();
        if(!fileListData || fileListData.length==0) {
            mini.alert("请上传doc/docx格式的情报附件！");
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
    var qbgzId=mini.get("id").getValue();
    if(!qbgzId) {
        mini.alert('请先点击‘保存草稿’进行表单的保存！');
        return;
    }
    mini.open({
        title: "添加附件",
        url: jsUseCtxPath + "/Info/Qbsj/openUploadWindow.do?qbgzId="+qbgzId,
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
        if (nodeVars[i].KEY_ == 'stageName') {
            stageName = nodeVars[i].DEF_VAL_;
        }
    }
    if(stageName == 'deptRespSH' || stageName == 'fgldSH') {
        formQbgz.setEnabled(false);
        mini.get("addFile").setEnabled(false);
    }
    if(stageName == 'qbgcsSH') {
        mini.get("qbName").setEnabled(false);
        mini.get("addFile").setEnabled(false);
    }
}

function returnQbsjPreviewSpan(fileName,fileId,formId,coverContent) {
    var fileType=getFileType(fileName);
    var s='';
    if(fileType=='other'){
        s = '<span  title="预览" style="color: silver" >预览</span>';
    }else if(fileType=='office'){
        var url='/Info/Qbsj/qbgzOfficePreview';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url +'\')">预览</span>';
    }
    return s;
}

function downLoadQbsjFile(fileName,fileId,formId) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + '/Info/Qbsj/qbgzPdfPreview.do?action=download');
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


function bigTypeValueChanged() {
    var bigTypeName = mini.get("bigTypeName").getValue();
    $.ajax({
        url: jsUseCtxPath + '/Info/Qbsj/querySmallTypeByBigType.do?bigTypeName=' + bigTypeName,
        type: 'post',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                mini.get("qbTypeId").setData(data);
            }
        }
    });
}