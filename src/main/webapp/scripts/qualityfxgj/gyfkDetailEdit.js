
$(function () {
    if (gyxjId) {
        var url = jsUseCtxPath + "/qualityfxgj/core/Gyfk/getGyfkDetail.do";
        $.post(
            url,
            {gyxjId: gyxjId},
            function (json) {
                formGyfkDetail.setData(json);
            });
    }
    formGyfkDetail.setEnabled(false);
    mini.get("addFile").setEnabled(false);

    //变更入口
    if (action == "detail") {
        formGyfkDetail.setEnabled(false);
        mini.get("addFile").setEnabled(false);
    }else if(action=="add"){
        mini.get("addFile").setEnabled(true);
        mini.get("gytype").setEnabled(true);
        mini.get("problem").setEnabled(true);
        mini.get("model").setEnabled(true);
        mini.get("part").setEnabled(true);
        mini.get("involveModel").setEnabled(true);
        mini.get("applyId").setEnabled(true);
    }
    if(action=="edit"&&status=="DRAFTED"){
        mini.get("addFile").setEnabled(true);
        mini.get("gytype").setEnabled(true);
        mini.get("problem").setEnabled(true);
        mini.get("model").setEnabled(true);
        mini.get("part").setEnabled(true);
        mini.get("involveModel").setEnabled(true);
        mini.get("applyId").setEnabled(true);
    }
    if(action=="edit"&&status=="RUNNING"&&chuli) {
        mini.get("addFile").setEnabled(true);
        mini.get("method").setEnabled(true);
        mini.get("completion").setEnabled(true);
        mini.get("finishTime").setEnabled(true);
    }else if(action=="edit"&&status=="RUNNING"&&!chuli&&!zrr){
        mini.get("addFile").setEnabled(true);
        mini.get("gytype").setEnabled(true);
        mini.get("problem").setEnabled(true);
        mini.get("model").setEnabled(true);
        mini.get("part").setEnabled(true);
        mini.get("involveModel").setEnabled(true);
        mini.get("applyId").setEnabled(true);
    }
    if(action=="edit"&&status=="RUNNING"&&zrr){
        mini.get("resId").setEnabled(true);
    }
});

function fileupload() {
    var gyxjId = mini.get("gyxjId").getValue();
    if (!gyxjId) {
        mini.alert('请先点击‘保存草稿’进行表单的保存！');
        return;
    }
    mini.open({
        title: "添加附件",
        url: jsUseCtxPath + "/qualityfxgj/core/Gyfk/openUploadWindow.do?gyxjId=" + gyxjId,
        width: 850,
        height: 550,
        showModal: true,
        allowResize: true,
        ondestroy: function () {
            if (fileListGrid) {
                fileListGrid.load();
            }
        }
    });
}


function returnGyfkPreviewSpan(fileName, fileId, formId, coverContent) {
    var fileType = getFileType(fileName);
    var s = '';
    if (fileType == 'other') {
        s = '<span  title="预览" style="color: silver" >预览</span>';
    } else if (fileType == 'pdf') {
        var url = '/qualityfxgj/core/Gyfk/gyfkPdfPreview';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
    } else if (fileType == 'office') {
        var url = '/qualityfxgj/core/Gyfk/gyfkOfficePreview';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
    } else if (fileType == 'pic') {
        var url = '/qualityfxgj/core/Gyfk/gyfkImagePreview';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
    }
    return s;
}

function downLoadGyfkFile(fileName, fileId, formId) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + '/qualityfxgj/core/Gyfk/gyfkPdfPreview.do?action=download');
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


function validGyfkChuangjian() {
    var gytype = $.trim(mini.get("gytype").getValue())
    if (!gytype) {
        return {"result": false, "message": "请选择类型"};
    }
    var problem = $.trim(mini.get("problem").getValue())
    if (!problem) {
        return {"result": false, "message": "请填写问题描述"};
    }
    var model = $.trim(mini.get("model").getValue())
    if (!model) {
        return {"result": false, "message": "请填写机型"};
    }
    var apply = $.trim(mini.get("applyId").getValue())
    if (!apply) {
        return {"result": false, "message": "请填写发起人"};
    }
    return {"result": true};
}
function validGyfkChuli() {
    var finishTime = $.trim(mini.get("finishTime").getValue())
    if (!finishTime) {
        return {"result": false, "message": "请填写完成时间"};
    }
    var method = $.trim(mini.get("method").getValue())
    if (!method) {
        return {"result": false, "message": "请填写对策"};
    }
    var completion = $.trim(mini.get("completion").getValue())
    if (!completion) {
        return {"result": false, "message": "请填写完成情况"};
    }
    return {"result": true};
}
function validGyfkXz() {
    var resId = $.trim(mini.get("resId").getValue())
    if (!resId) {
        return {"result": false, "message": "请填写责任人"};
    }
    return {"result": true};
}

function savegyfkxj() {
    if(!chuli||status=="DRAFTED"){
        var formValid = validGyfkChuangjian();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    if(chuli&&status=="RUNNING"){
        var formValid = validGyfkChuli();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    if(zrr&&status=="RUNNING"){
        var formValid = validGyfkXz();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    var formData = new mini.Form("formGyfkDetail");
    var data = formData.getData();
    var json = mini.encode(data);
    $.ajax({
        url: jsUseCtxPath + '/qualityfxgj/core/Gyfk/saveGyfkDetail.do?belongGyfkId='+belongGyfkId,
        type: 'POST',
        data: json,
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                mini.alert(data.message, "提示消息", function (action) {
                    if (action == 'ok') {
                        var url= jsUseCtxPath+"/qualityfxgj/core/Gyfk/editGyfkDetail.do?" +
                            "gyxjId=" + data.data + "&action=edit&gyfkId="+belongGyfkId+"&isChuangjian="+isChuangjian+ "&isZZRXZ=" + isZZRXZ;
                        window.location.href=url;
                    }
                });
            }
        }
    });
}
