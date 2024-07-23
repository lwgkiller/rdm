
function uploadJszbTemplate() {
    mini.open({
        title: "附件上传",
        url: jsUseCtxPath + "/zhgl/core/jszb/fileUploadWindow.do?jszbId=&ckId=",
        width: 650,
        height: 350,
        showModal:false,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            searchDoc();
        }
    });
}

function searchDoc() {
    commonDocListGrid.setUrl(url+mini.get("#docName").getValue());
    commonDocListGrid.load();
}
function returnJszbTemplatePreview(fileName,fileId,formId,coverContent) {
    var fileType=getFileType(fileName);
    var s='';
    if(fileType=='other'){
        s = '<span  title="预览" style="color: silver" >预览</span>';
    }else {
        var url='/zhgl/core/jszb/preview.do?fileType='+fileType;
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewJszbPdf(\'' +fileName+'\',\''+fileId+'\',\'\',\''+coverContent+'\',\''+url+'\',\''+fileType+ '\')">预览</span>';
    }
    return s;
}

function previewJszbPdf(fileName,fileId,formId, coverConent, url,fileType) {
    if(!fileName) {
        fileName='';
    }
    if(!fileId) {
        fileId='';
    }
    if(!formId) {
        formId='';
    }
    var previewUrl = jsUseCtxPath + url+"&fileName="+encodeURIComponent(fileName)+"&fileId="+fileId+"&formId="+formId;
    if(fileType!='pic') {
        window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent="+encodeURIComponent(coverConent)+"&file=" + encodeURIComponent(previewUrl));
    } else {
        window.open(previewUrl);
    }
}

function deleteJszbFile(record) {
    mini.confirm("确定删除？", "确定？",
        function (action) {
            if (action == "ok") {
                var url = jsUseCtxPath + "/zhgl/core/jszb/delJszbFile.do";
                var data = {
                    formId: record.jszbId,
                    id: record.id,
                    fileName: record.fileName
                };
                $.post(
                    url,
                    data,
                    function (json) {
                        searchDoc();
                    });
            }
        }
    );
}

//下载文档
function downJszbFile(record) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/zhgl/core/jszb/fileDownload.do");
    var inputFileName = $("<input>");
    inputFileName.attr("type", "hidden");
    inputFileName.attr("name", "fileName");
    inputFileName.attr("value", record.fileName);
    var fileId = $("<input>");
    fileId.attr("type", "hidden");
    fileId.attr("name", "fileId");
    fileId.attr("value", record.id);
    $("body").append(form);
    form.append(inputFileName);
    form.append(fileId);
    form.submit();
    form.remove();
}