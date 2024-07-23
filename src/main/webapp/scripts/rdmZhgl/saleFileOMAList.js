$(function () {

})
function returnSalePreviewSpan(fileName, fileId, formId, coverContent, fileSize) {
    var fileType = getFileType(fileName);
    var s = '';
    if (fileType == 'other') {
        s = '<span  title=' + saleFilesList_name1 + ' style="color: silver" >' + saleFilesList_name1 + '</span>';
    } else if (fileType == 'pdf') {
        var url = '/rdmZhgl/core/saleFile/pdfPreview.do';
        s = '<span  title=' + saleFilesList_name1 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + saleFilesList_name1 + '</span>';
    } else if (fileType == 'office') {
        var url = '/rdmZhgl/core/saleFile/officePreview.do';
        s = '<span  title=' + saleFilesList_name1 + ' style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url+ '\',\'' + fileSize + '\')">' + saleFilesList_name1 + '</span>';
    } else if (fileType == 'pic') {
        var url = '/rdmZhgl/core/saleFile/imagePreview.do';
        s = '<span  title=' + saleFilesList_name1 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + saleFilesList_name1 + '</span>';
    }
    return s;
}
//下载文档
function downFile(record) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/rdmZhgl/core/saleFile/fileDownload.do?action=download");
    var inputFileName = $("<input>");
    inputFileName.attr("type", "hidden");
    inputFileName.attr("name", "fileName");
    inputFileName.attr("value", record.fileName);
    var mainId = $("<input>");
    mainId.attr("type", "hidden");
    mainId.attr("name", "applyId");
    mainId.attr("value", record.applyId);
    var fileId = $("<input>");
    fileId.attr("type", "hidden");
    fileId.attr("name", "fileId");
    fileId.attr("value", record.id);
    $("body").append(form);
    form.append(inputFileName);
    form.append(mainId);
    form.append(fileId);
    form.submit();
    form.remove();
}
//..
function getFileType(fileName) {
    var suffix = "";
    var suffixIndex = fileName.lastIndexOf('.');
    if (suffixIndex != -1) {
        suffix = fileName.substring(suffixIndex + 1).toLowerCase();
    }
    var pdfArray = ['pdf'];
    if (pdfArray.indexOf(suffix) != -1) {
        return 'pdf';
    }
    var officeArray = ['doc', 'docx', 'ppt', 'txt', 'xlsx', 'xls', 'pptx'];
    if (officeArray.indexOf(suffix) != -1) {
        return 'office';
    }
    var picArray = ['jpg', 'jpeg', 'jif', 'bmp', 'png', 'tif', 'gif'];
    if (picArray.indexOf(suffix) != -1) {
        return 'pic';
    }
    return 'other';
}
