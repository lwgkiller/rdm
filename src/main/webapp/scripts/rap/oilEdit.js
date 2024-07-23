
$(function () {
    if(oilId) {
        var url = jsUseCtxPath + "/environment/core/Oil/getOilDetail.do";
        $.post(
            url,
            {oilId: oilId},
            function (json) {
                formOil.setData(json);
            });
    }
    if (action == 'detail') {
        formOil.setEnabled(false);
        mini.get("addFile").setEnabled(false);
    }
    if (action == 'edit') {
        $("#save").show();
        $("#submit").show();
    }
    if (action == 'task') {
        formOil.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        $("#approve").show();
        $("#reject").show();
    }
});

function submit() {
    var action = {action:'submit',oilId: oilId};
    var json=mini.encode(action);
    $.ajax({
        url: jsUseCtxPath + '/environment/core/Oil/saveOil.do',
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
    var action = {action:'reject',oilId: oilId};
    var json=mini.encode(action);
    $.ajax({
        url: jsUseCtxPath + '/environment/core/Oil/saveOil.do',
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

    var action = {action:'approve',oilId: oilId};
    var json=mini.encode(action);
    $.ajax({
        url: jsUseCtxPath + '/environment/core/Oil/saveOil.do',
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
    var formData =new mini.Form("formOil");
    var formValid = validOil();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    var data = formData.getData();
    var json = mini.encode(data);
        $.ajax({
            url: jsUseCtxPath + '/environment/core/Oil/saveOil.do',
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

function validOil() {
/*    var companyName=$.trim(mini.get("companyName").getValue())
    if(!companyName) {
        return {"result": false, "message": "请填写单位名称名称"};
    }*/
    var oilNation=$.trim(mini.get("oilNation").getValue())
    if(!oilNation) {
        return {"result": false, "message": "请填写国家区域"};
    }
    var oilType=$.trim(mini.get("oilType").getValue())
    if(!oilType) {
        return {"result": false, "message": "请选择油液类型"};
    }
    var oilDate=$.trim(mini.get("oilDate").getValue())
    if(!oilDate) {
        return {"result": false, "message": "请填写采集日期"};
    }
    var oilTest=$.trim(mini.get("oilTest").getValue())
    if(!oilTest) {
        return {"result": false, "message": "请填写检测机构"};
    }
    var testStandard=$.trim(mini.get("testStandard").getValue())
    if(!testStandard) {
        return {"result": false, "message": "请填写检测标准"};
    }
    var oilCompliance=$.trim(mini.get("oilCompliance").getValue())
    if(!oilCompliance) {
        return {"result": false, "message": "请填写合规性"};
    }

    // var fileListGrid=$.trim(mini.get("fileListGrid").getData());
    // if(!fileListGrid) {
    //     return {"result": false, "message": "请添加附件"};
    // }
    return {"result": true};
}


function fileupload() {
    var oilId = mini.get("oilId").getValue();
    if(!oilId){
        mini.alert("请先保存!");
        return;
    }
    mini.open({
        title: "添加附件",
        url: jsUseCtxPath + "/environment/core/Oil/openUploadWindow.do?oilId="+oilId,
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
function downLoadOilFile(fileName,fileId,formId) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + '/environment/core/Oil/oilPdfPreview.do?action=download');
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
function returnOilPreviewSpan(fileName,fileId,formId,coverContent) {
    var fileType=getFileType(fileName);
    var s='';
    if(fileType=='other'){
        s = '<span  title="预览" style="color: silver" >预览</span>';
    }else if(fileType=='pdf'){
        var url='/environment/core/Oil/oilPdfPreview';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url+ '\')">预览</span>';
    }else if(fileType=='office'){
        var url='/environment/core/Oil/oilOfficePreview';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url +'\')">预览</span>';
    }else if(fileType=='pic'){
        var url='/environment/core/Oil/oilImagePreview';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url + '\')">预览</span>';
    }
    return s;
}

