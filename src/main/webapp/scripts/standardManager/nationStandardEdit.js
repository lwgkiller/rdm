
$(function () {
    if(standardId) {
        var url = jsUseCtxPath + "/standardManager/core/NationStandardChangeController/getstandardChangeDetail.do";
        $.post(
            url,
            {standardId: standardId},
            function (json) {
                formNationStandard.setData(json);
            });
    }
    if (action == 'detail') {
        formNationStandard.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        mini.get("release").setVisible(false);
    }
});


function release() {
    var formData =new mini.Form("formNationStandard");
    var formValid = validStandardChange();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    var data = formData.getData();
    var json = mini.encode(data);
        $.ajax({
            url: jsUseCtxPath + '/standardManager/core/NationStandardChangeController/saveStandardChange.do',
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

function validStandardChange() {
/*    var companyName=$.trim(mini.get("companyName").getValue())
    if(!companyName) {
        return {"result": false, "message": "请填写单位名称名称"};
    }*/
    var standardName=$.trim(mini.get("standardName").getValue())
    if(!standardName) {
        return {"result": false, "message": "请填写标准名称"};
    }
    var spNumber=$.trim(mini.get("spNumber").getValue())
    if(!spNumber) {
        return {"result": false, "message": "请填写项目/标准编号"};
    }
    var standardLv=$.trim(mini.get("standardLv").getValue())
    if(!standardLv) {
        return {"result": false, "message": "请选择标准级别"};
    }
    var joinDegree=$.trim(mini.get("joinDegree").getValue())
    if(!joinDegree) {
        return {"result": false, "message": "请选择参与程度"};
    }
    var enactoralter=$.trim(mini.get("enactoralter").getValue())
    if(!enactoralter) {
        return {"result": false, "message": "请选择是制定或修改"};
    }
    var standaStauts=$.trim(mini.get("standaStauts").getValue())
    if(!standaStauts) {
        return {"result": false, "message": "请选择标准状态"};
    }
    var releaseTime=$.trim(mini.get("releaseTime").getValue())
    if(!releaseTime) {
        return {"result": false, "message": "请选择（拟）发布时间"};
    }
    var belongGroup=$.trim(mini.get("belongGroup").getValue())
    if(!belongGroup) {
        return {"result": false, "message": "请填写所属团体"};
    }
    // var fileListGrid=$.trim(mini.get("fileListGrid").getData());
    // if(!fileListGrid) {
    //     return {"result": false, "message": "请添加附件"};
    // }
    return {"result": true};
}


function fileupload() {
    var standardId = mini.get("standardId").getValue();
    if(!standardId){
        mini.alert("请先保存!");
        return;
    }
    mini.open({
        title: "添加附件",
        url: jsUseCtxPath + "/standardManager/core/NationStandardChangeController/openUploadWindow.do?standardId="+standardId,
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
function downLoadstandardChangeFile(fileName,fileId,formId) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + '/standardManager/core/NationStandardChangeController/standardChangePdfPreview.do?action=download');
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
function returnstandardChangePreviewSpan(fileName,fileId,formId,coverContent) {
    var fileType=getFileType(fileName);
    var s='';
    if(fileType=='other'){
        s = '<span  title="预览" style="color: silver" >预览</span>';
    }else if(fileType=='pdf'){
        var url='/standardManager/core/NationStandardChangeController/standardChangePdfPreview';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url+ '\')">预览</span>';
    }else if(fileType=='office'){
        var url='/standardManager/core/NationStandardChangeController/standardChangeOfficePreview';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url +'\')">预览</span>';
    }else if(fileType=='pic'){
        var url='/standardManager/core/NationStandardChangeController/standardChangeImagePreview';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url + '\')">预览</span>';
    }
    return s;
}

