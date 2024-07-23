/**
 * 本js存放一些抽象出来的公共方法
 * */
//ajax 请求公共方法
function ajaxRequest(_url,_requestType,_async,_param){
    var resultData;
    $.ajax({
        url: _url,
        type: _requestType,
        async: _async,
        data:mini.encode(_param),
        contentType: 'application/json',
        success: function (data) {
            resultData = data;
        },
        error: function(e) {
            console.log(e.status);
            console.log(e.responseText);
        }
    });
    return resultData;
}
/**
 * 获取配置文件信息
 * @param _key 配置在 app.properties 文件中的 值
 * */
function getProperties(_key) {
    var param = {key:_key};
    var url = __rootPath+"/xcmgProjectManager/core/xcmgProject/getSysProperties.do";
    var resultData = ajaxRequest(url,"POST",false,param);
    return resultData;
}
function getDics(dicKey) {
    let resultDate = [];
    $.ajax({
        async: false,
        url: __rootPath + '/sys/core/commonInfo/getDicItem.do?dicType=' + dicKey,
        type: 'GET',
        contentType: 'application/json',
        success: function (data) {
            if (data.code == 200) {
                resultDate = data.data;
            }
        }
    });
    return resultDate;
}
/**
 * 获取当前年月
 * */
function getCurrentYearMonth() {
    var year = new Date().getFullYear();
    var month = new Date().getMonth()+1;
    var yearMonth = '';
    if(month<10){
        yearMonth = year+'-0'+month;
    }else{
        yearMonth = year+'-'+month;
    }
    return yearMonth;
}
/**
 * 月份加减
 * */
//月份加减
function addMonths(yearMonthDay,monthNum){
    var arr=yearMonthDay.split('-');//2020-08-19或2020-08
    var year=parseInt(arr[0]);
    var month=parseInt(arr[1]);
    month=month+monthNum;
    if(month>12){//月份加
        var yearNum=parseInt((month-1)/12);
        month=month%12==0?12:month%12;
        year+=yearNum;
    }else if(month<=0){//月份减
        month=Math.abs(month);
        var yearNum=parseInt((month+12)/12);
        year-=yearNum;
    }
    month=month<10?"0"+month:month;
    return year+"-"+month;
}

function returnPreviewSpan(fileName,relativeFilePath,actionType,coverContent,fileUrl,tableName) {
    var fileType=getFileType(fileName);
    var s='';
    if(fileType=='other'){
        s = '<span  title="预览" style="color: silver" >预览</span>';
    }else if(fileType=='pdf'){
        var url = '/sys/core/commonInfo/pdfPreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' +fileName+'\',\''+relativeFilePath+'\',\''+actionType+'\',\''+coverContent+ '\',\''+url+ '\',\''+fileUrl+ '\',\''+tableName+ '\')">预览</span>';
    }else if(fileType=='office'){
        var url = '/sys/core/commonInfo/officePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' +fileName+'\',\''+relativeFilePath+'\',\''+actionType+'\',\''+coverContent +'\',\''+url+ '\',\''+fileUrl+ '\',\''+tableName+ '\')">预览</span>';
    }else if(fileType=='pic'){
        var url = '/sys/core/commonInfo/imagePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' +fileName+'\',\''+relativeFilePath+'\',\''+actionType+'\',\''+coverContent+ '\',\''+url+ '\',\''+fileUrl+ '\',\''+tableName+ '\')">预览</span>';
    }
    return s;
}
function bytesToSize(bytes) {
    if (bytes === 0) return '0 B';
    var k = 1024,
        sizes = ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'],
        i = Math.floor(Math.log(bytes) / Math.log(k));

    return (bytes / Math.pow(k, i)).toPrecision(3) + ' ' + sizes[i];
}
/**
 * 获取附件类型
 * */
function getFileType(fileName) {
    if(!fileName){
        return 'other';
    }
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
function previewDoc(fileName,fileId,formId, coverConent, url,fileUrl,tableName) {
    if(!fileName) {
        fileName='';
    }
    if(!fileId) {
        fileId='';
    }
    if(!formId) {
        formId='';
    }
    if(!tableName||tableName=='undefined') {
        tableName='';
    }
    var previewUrl = jsUseCtxPath + url+"?fileName=" + encodeURIComponent(fileName)+"&fileId="+fileId+"&formId="+formId+"&fileUrl="+fileUrl+"&tableName="+tableName;
    window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent="+encodeURIComponent(coverContent)+"&file=" + encodeURIComponent(previewUrl));
}
function previewPdf(fileName,fileId,formId, coverConent, url,fileUrl,tableName) {
    if(!fileName) {
        fileName='';
    }
    if(!fileId) {
        fileId='';
    }
    if(!formId) {
        formId='';
    }
    if(!tableName||tableName=='undefined') {
        tableName='';
    }
    var previewUrl = jsUseCtxPath + url+"?action=preview&fileName="+encodeURIComponent(fileName)+"&fileId="+fileId+"&formId="+formId+"&fileUrl="+fileUrl+"&tableName="+tableName;
    window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent="+encodeURIComponent(coverConent)+"&file=" + encodeURIComponent(previewUrl));
}

function previewPic(fileName,fileId,formId, coverConent, url,fileUrl,tableName) {
    if(!fileName) {
        fileName='';
    }
    if(!fileId) {
        fileId='';
    }
    if(!formId) {
        formId='';
    }
    if(!tableName||tableName=='undefined') {
        tableName='';
    }
    var previewUrl = jsUseCtxPath + url+"?fileName=" + encodeURIComponent(fileName)+"&fileId="+fileId+"&formId="+formId+"&fileUrl="+fileUrl+"&tableName="+tableName;
    window.open(previewUrl);
}
function deleteCommonFile(mainId,fileId,fileName,fileUrl,tableName) {
    mini.confirm("确定删除？", "确定？",
        function (action) {
            if (action == "ok") {
                var url = jsUseCtxPath + "/common/core/file/delFile.do";
                var data = {
                    mainId: mainId,
                    id: fileId,
                    fileName: fileName,
                    fileUrl:fileUrl,
                    tableName:tableName
                };
                $.post(
                    url,
                    data,
                    function (json) {
                        fileListGrid.load();
                    });
            }
        }
    );
}
//下载文档
function downCommonFile(fileName,mainId,fileId,fileUrl,tableName) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/common/core/file/fileDownload.do?action=download");
    var fileNameInput = $("<input>");
    fileNameInput.attr("type", "hidden");
    fileNameInput.attr("name", "fileName");
    fileNameInput.attr("value", fileName);
    var mainIdInput = $("<input>");
    mainIdInput.attr("type", "hidden");
    mainIdInput.attr("name", "formId");
    mainIdInput.attr("value", mainId);
    var fileIdInput = $("<input>");
    fileIdInput.attr("type", "hidden");
    fileIdInput.attr("name", "fileId");
    fileIdInput.attr("value", fileId);
    var fileUrlInput = $("<input>");
    fileUrlInput.attr("type", "hidden");
    fileUrlInput.attr("name", "fileUrl");
    fileUrlInput.attr("value", fileUrl);
    var tableNameInput = $("<input>");
    tableNameInput.attr("type", "hidden");
    tableNameInput.attr("name", "tableName");
    tableNameInput.attr("value", tableName);
    $("body").append(form);
    form.append(fileNameInput);
    form.append(mainIdInput);
    form.append(fileIdInput);
    form.append(fileUrlInput);
    form.append(tableNameInput);
    form.submit();
    form.remove();
}
