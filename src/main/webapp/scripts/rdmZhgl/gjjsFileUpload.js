
//下载文档
function downGjjsLoadFile(record) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/zhgl/core/gjjs/fileDownload.do?action=download");
    var inputFileName = $("<input>");
    inputFileName.attr("type", "hidden");
    inputFileName.attr("name", "fileName");
    inputFileName.attr("value", record.fileName);
    var standardId = $("<input>");
    standardId.attr("type", "hidden");
    standardId.attr("name", "standardId");
    standardId.attr("value", record.zId);
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
//删除文档
function deleteProjectFile(record) {
    mini.confirm("确定删除证书附件？", "确定？",
        function (action) {
            if (action == "ok") {
                var url = jsUseCtxPath + "/zhgl/core/gjjs/deleteGjjsFiles.do";
                var data = {
                    standardId: record.zId,
                    id: record.id,
                    fileName: record.fileName
                };
                $.post(
                    url,
                    data,
                    function (json) {
                        queryProjectFiles();
                    });
            }
        }
    );
}

/**
 * 获取附件类型
 * */
function getFileType(fileName) {
    var suffix="";
    var suffixIndex=fileName.lastIndexOf('.');
    if(suffixIndex!=-1) {
        suffix=fileName.substring(suffixIndex+1).toLowerCase();
    }
    var pdfArray = ['pdf'];
    if(pdfArray.indexOf(suffix)!=-1){
        return 'pdf';
    }
    var officeArray = ['doc','docx','ppt','txt','xlsx','xls','pptx'];
    if(officeArray.indexOf(suffix)!=-1){
        return 'office';
    }
    var picArray = ['jpg','jpeg','jif','bmp','png','tif','gif'];
    if(picArray.indexOf(suffix)!=-1){
        return 'pic';
    }
    return 'other';
}

function targetFileInfoRenderer(e) {
    var record = e.record;
    var fileName =record.fileName;
    var fileId =record.id;
    var formId=record.zId;
    var fileType=getFileType(fileName);
    var s ='';
    if(canOperateFile=="false") {
        if(fileType=='other'){
            s = '<span  title="预览" style="color: silver" >预览</span>';
        }else if(fileType=='pdf'){
            var url='/zhgl/core/gjjs/gjjsPdfPreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\''+',\''+url+ '\')">预览</span>';
        }else if(fileType=='office'){
            var url='/zhgl/core/gjjs/gjjsOfficePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url +'\')">预览</span>';
        }else if(fileType=='pic'){
            var url='/zhgl/core/gjjs/gjjsImagePreview.do';
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url + '\')">预览</span>';
        }
        s+='&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downGjjsLoadFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">下载</span>';

    }else{
        if(fileType=='other'){
            s+= '<span  title="预览" style="color: silver" >预览</span>';
        }else if(fileType=='pdf'){
            var url='/zhgl/core/gjjs/gjjsPdfPreview.do';
            s+= '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\''+',\''+url+ '\')">预览</span>';
        }else if(fileType=='office'){
            var url='/zhgl/core/gjjs/gjjsOfficePreview.do';
            s+= '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url +'\')">预览</span>';
        }else if(fileType=='pic'){
            var url='/zhgl/core/gjjs/gjjsImagePreview.do';
            s+= '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url + '\')">预览</span>';
        }
        s+='&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downGjjsLoadFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">下载</span>';
        s+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="deleteProjectFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';

    }


    return s;
}
