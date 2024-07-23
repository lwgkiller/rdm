
$(function () {
    if(gjId) {
        var url = jsUseCtxPath + "/zlgjNPI/core/Gjll/getGjllDetail.do";
        $.post(
            url,
            {gjId: gjId},
            function (json) {
                formGjll.setData(json);
            });
    }else {
        mini.get("zrrId").setValue(currentUserId);
        mini.get("zrrId").setText(currentUserName);
        var userId=mini.get("zrrId").getValue();
        if(!userId) {
            mini.get("ssbmId").setValue('');
            mini.get("ssbmId").setText('');
            return;
        }
        $.ajax({
            url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/' + userId + '/getUserInfoById.do',
            type: 'get',
            async: false,
            contentType: 'application/json',
            success: function (data) {
                mini.get("ssbmId").setValue(data.mainDepId);
                mini.get("ssbmId").setText(data.mainDepName);
            }
        });
    }
    if (action == 'detail') {
        formGjll.setEnabled(false);
        mini.get("addFile").setEnabled(false);
    }
    if (action == 'edit') {
        $("#save").show();
    }
});


function save() {
    var formValid = validGjll();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    var formData =new mini.Form("formGjll");
    var data = formData.getData();
    var json = mini.encode(data);
        $.ajax({
            url: jsUseCtxPath + '/zlgjNPI/core/Gjll/saveGjll.do',
            type: 'POST',
            data: json,
            async: false,
            contentType: 'application/json',
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            // window.close();
                            var url=jsUseCtxPath+"/zlgjNPI/core/Gjll/edit.do?gjId="+returnData.data+"&action=edit";
                            window.location.href=url;
                        }
                    });
                }
            }
        });
}

function validGjll() {
    var wtlx=$.trim(mini.get("wtlx").getValue());
    if(!wtlx) {
        return {"result": false, "message": "请选择问题类型"};
    }
    var jiXing=$.trim(mini.get("jiXing").getValue())
    if(!jiXing) {
        return {"result": false, "message": "请选择机型类别"};
    }
    var smallJiXing=$.trim(mini.get("smallJiXing").getValue())
    if(!smallJiXing) {
        return {"result": false, "message": "请填写机型"};
    }
    var gzlj=$.trim(mini.get("gzlj").getValue())
    if(!gzlj) {
        return {"result": false, "message": "请填写零部件"};
    }
    var wtms=$.trim(mini.get("wtms").getValue())
    if(!wtms) {
        return {"result": false, "message": "请填写问题描述"};
    }
    var cqcs=$.trim(mini.get("cqcs").getValue())
    if(!cqcs) {
        return {"result": false, "message": "请填写改进方案"};
    }
    var tzdh=$.trim(mini.get("tzdh").getValue())
    if(!tzdh) {
        return {"result": false, "message": "请填写标准化文件"};
    }
    var yjqhch=$.trim(mini.get("yjqhch").getValue())
    if(!yjqhch) {
        return {"result": false, "message": "请填写预计切换车号"};
    }
    // var fileListGrid=$.trim(mini.get("fileListGrid").getData());
    // if(!fileListGrid) {
    //     return {"result": false, "message": "请添加附件"};
    // }
    return {"result": true};
}


function fileupload() {
    var gjId = mini.get("gjId").getValue();
    if(!gjId){
        mini.alert("请先保存!");
        return;
    }
    mini.open({
        title: "添加附件",
        url: jsUseCtxPath + "/zlgjNPI/core/Gjll/openUploadWindow.do?gjId="+gjId,
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
function downLoadGjllFile(fileName,fileId,formId) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + '/zlgjNPI/core/Gjll/gjllPdfPreview.do?action=download');
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
function returnGjllPreviewSpan(fileName,fileId,formId,coverContent) {
    var fileType=getFileType(fileName);
    var s='';
    if(fileType=='other'){
        s = '<span  title="预览" style="color: silver" >预览</span>';
    }else if(fileType=='pdf'){
        var url='/zlgjNPI/core/Gjll/gjllPdfPreview';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url+ '\')">预览</span>';
    }else if(fileType=='office'){
        var url='/zlgjNPI/core/Gjll/gjllOfficePreview';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url +'\')">预览</span>';
    }else if(fileType=='pic'){
        var url='/zlgjNPI/core/Gjll/gjllImagePreview';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url + '\')">预览</span>';
    }
    return s;
}

