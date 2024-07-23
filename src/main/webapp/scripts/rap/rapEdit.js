
$(function () {
    if(rapId) {
        var url = jsUseCtxPath + "/environment/core/Rap/getRapDetail.do";
        $.post(
            url,
            {rapId: rapId},
            function (json) {
                formRap.setData(json);
            });
    }
    if (action == 'detail') {
        formRap.setEnabled(false);
        mini.get("addFile").setEnabled(false);
    }
    if (action == 'edit') {
        $("#save").show();
        $("#submit").show();
    }
    if (action == 'task') {
        formRap.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        $("#approve").show();
        $("#reject").show();
    }
});

function submit() {
    var action = {action:'submit',rapId: rapId};
    var json=mini.encode(action);
    $.ajax({
        url: jsUseCtxPath + '/environment/core/Rap/saveRap.do',
        type: 'POST',
        data: json,
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if(data) {
                mini.alert(data.message,"提示消息",function (action) {
                    if(action=='ok') {
                        CloseWindow();
                    }
                });
            }
        }
    });
}
function reject() {
    var action = {action:'reject',rapId: rapId};
    var json=mini.encode(action);
    $.ajax({
        url: jsUseCtxPath + '/environment/core/Rap/saveRap.do',
        type: 'POST',
        data: json,
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if(data) {
                mini.alert(data.message,"提示消息",function (action) {
                    if(action=='ok') {
                        CloseWindow();
                    }
                });
            }
        }
    });
}
function approve() {

    var action = {action:'approve',rapId: rapId};
    var json=mini.encode(action);
    $.ajax({
        url: jsUseCtxPath + '/environment/core/Rap/saveRap.do',
        type: 'POST',
        data: json,
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if(data) {
                mini.alert(data.message,"提示消息",function (action) {
                    if(action=='ok') {
                        CloseWindow();
                    }
                });
            }
        }
    });
}
function save() {
    var formData =new mini.Form("formRap");
    var formValid = validRap();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    var data = formData.getData();
    var json = mini.encode(data);
        $.ajax({
            url: jsUseCtxPath + '/environment/core/Rap/saveRap.do',
            type: 'POST',
            data: json,
            async: false,
            contentType: 'application/json',
            success: function (data) {
                if(data) {
                    mini.alert(data.message,"提示消息",function (action) {
                        if(action=='ok') {
                            CloseWindow();
                        }
                    });
                }
            }
        });
}

function validRap() {
/*    var companyName=$.trim(mini.get("companyName").getValue())
    if(!companyName) {
        return {"result": false, "message": "请填写单位名称名称"};
    }*/
    var rapName=$.trim(mini.get("rapName").getValue())
    if(!rapName) {
        return {"result": false, "message": "请填写法规/政策名称"};
    }
    var rapStatus=$.trim(mini.get("rapStatus").getValue())
    if(!rapStatus) {
        return {"result": false, "message": "请选择排放阶段"};
    }
    var rapArea=$.trim(mini.get("rapArea").getValue())
    if(!rapArea) {
        return {"result": false, "message": "请填写所属国家/省份"};
    }
    var rapDate=$.trim(mini.get("rapDate").getValue())
    if(!rapDate) {
        return {"result": false, "message": "请选择实施日期"};
    }
    // var fileListGrid=$.trim(mini.get("fileListGrid").getData());
    // if(!fileListGrid) {
    //     return {"result": false, "message": "请添加附件"};
    // }
    return {"result": true};
}


function fileupload() {
    var rapId = mini.get("rapId").getValue();
    if(!rapId){
        mini.alert("请先保存!");
        return;
    }
    mini.open({
        title: "添加附件",
        url: jsUseCtxPath + "/environment/core/Rap/openUploadWindow.do?rapId="+rapId,
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
function downLoadRapFile(fileName,fileId,formId) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + '/environment/core/Rap/rapPdfPreview.do?action=download');
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
function returnRapPreviewSpan(fileName,fileId,formId,coverContent) {
    var fileType=getFileType(fileName);
    var s='';
    if(fileType=='other'){
        s = '<span  title="预览" style="color: silver" >预览</span>';
    }else if(fileType=='pdf'){
        var url='/environment/core/Rap/rapPdfPreview';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url+ '\')">预览</span>';
    }else if(fileType=='office'){
        var url='/environment/core/Rap/rapOfficePreview';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url +'\')">预览</span>';
    }else if(fileType=='pic'){
        var url='/environment/core/Rap/rapImagePreview';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url + '\')">预览</span>';
    }
    return s;
}

