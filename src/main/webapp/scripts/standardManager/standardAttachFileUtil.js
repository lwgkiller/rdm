//是否是指定体系类别的标准管理人员
function whetherIsPointStandardManager(systemCategoryId,userRoles) {
    for (var i = 0; i < userRoles.length; i++) {
        if (userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-LEADER'||userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-BELONG') {
            if(systemCategoryId=='GL'&&userRoles[i].NAME_.indexOf('管理标准管理人员')!=-1) {
                return true;
            }
            if(systemCategoryId=='JS'&&userRoles[i].NAME_.indexOf('技术标准管理人员')!=-1) {
                return true;
            }
            if(systemCategoryId=='NK'&&userRoles[i].NAME_.indexOf('内控标准管理人员')!=-1) {
                return true;
            }
            if(systemCategoryId=='GLBZFBSPRY'&&userRoles[i].NAME_.indexOf('管理标准附表审批人员')!=-1) {
                return true;
            }
        }
    }
    return false;
}
//是否是部门负责人
function whetherIsDepRespman(userDeps) {
    for (var i = 0; i < userDeps.length; i++) {
        if (userDeps[i].REL_TYPE_KEY_ == 'GROUP-USER-LEADER') {
            return true;
        }
    }
    return false;
}


//是否是分管领导
function whetherIsLeader(userRoles) {
    for (var i = 0; i < userRoles.length; i++) {
        if (userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-LEADER'||userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-BELONG') {
            if(userRoles[i].NAME_=='分管领导') {
                return true;
            }
        }
    }
    return false;
}


//是否是非挖掘机械研究院的项目管理人员
function whetherIsProjectManagerNotZSZX(userRoles) {
    for (var i = 0; i < userRoles.length; i++) {
        if (userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-LEADER'||userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-BELONG') {
            if(userRoles[i].NAME_!='技术中心项目管理人员'&&userRoles[i].NAME_.indexOf('项目管理人员')!=-1) {
                return true;
            }
        }
    }
    return false;
}

//是否是项目管理人员
function whetherIsProjectManager(userRoles) {
    for (var i = 0; i < userRoles.length; i++) {
        if (userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-LEADER'||userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-BELONG') {
            if(userRoles[i].NAME_.indexOf('重大项目管理人员')!=-1) {
                return true;
            }
        }
    }
    return false;
}

function getUserInfoById(userId) {
    var userInfo = "";
    $.ajax({
        url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/' + userId + '/getUserInfoById.do',
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            userInfo = data;
        }
    });
    return userInfo;
}

function bytesToSize(bytes) {
    if (bytes === 0) return '0 B';
    var k = 1024,
        sizes = ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'],
        i = Math.floor(Math.log(bytes) / Math.log(k));

    return (bytes / Math.pow(k, i)).toPrecision(3) + ' ' + sizes[i];
}

function previewPdf(fileName,fileId,standardId, coverConent) {
    if(!fileName) {
        fileName='';
    }
    if(!fileId) {
        fileId='';
    }
    if(!standardId) {
        standardId='';
    }
    var previewUrl = jsUseCtxPath + "/standardManager/core/standardFileInfos/fileDownload.do?action=preview&fileName="+encodeURIComponent(fileName)+"&fileId="+fileId+"&standardId="+standardId;
    window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent="+encodeURIComponent(coverConent)+"&file=" + encodeURIComponent(previewUrl));
}

function previewPic(fileName,fileId,standardId, coverConent) {
    if(!fileName) {
        fileName='';
    }
    if(!fileId) {
        fileId='';
    }
    if(!standardId) {
        standardId='';
    }
    var previewUrl = jsUseCtxPath + "/standardManager/core/standardFileInfos/imagePreview.do?fileName=" + encodeURIComponent(fileName)+"&fileId="+fileId+"&standardId="+standardId;
    window.open(previewUrl);
}

function previewDoc(fileName,fileId,standardId, coverConent) {
    if(!fileName) {
        fileName='';
    }
    if(!fileId) {
        fileId='';
    }
    if(!standardId) {
        standardId='';
    }
    var previewUrl = jsUseCtxPath + "/standardManager/core/standardFileInfos/officePreview.do?fileName=" + encodeURIComponent(fileName)+"&fileId="+fileId+"&standardId="+standardId;
    window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent="+encodeURIComponent(coverContent)+"&file=" + encodeURIComponent(previewUrl));
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

function returnPreviewSpan(fileName,fileId,standardId,coverContent) {
    var fileType=getFileType(fileName);
    var s='';
    if(fileType=='other'){
        s = '<span  title="预览" style="color: silver" >预览</span>';
    }else if(fileType=='pdf'){
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' +fileName+'\',\''+fileId+'\',\''+standardId+'\',\''+coverContent+ '\')">预览</span>';
    }else if(fileType=='office'){
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' +fileName+'\',\''+fileId+'\',\''+standardId+'\',\''+coverContent +'\')">预览</span>';
    }else if(fileType=='pic'){
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' +fileName+'\',\''+fileId+'\',\''+standardId+'\',\''+coverContent+ '\')">预览</span>';
    }
    return s;
}


//下载文档
function downProjectLoadFile(record) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/standardManager/core/standardFileInfos/fileDownload.do?action=download");
    var inputFileName = $("<input>");
    inputFileName.attr("type", "hidden");
    inputFileName.attr("name", "fileName");
    inputFileName.attr("value", record.fileName);
    var standardId = $("<input>");
    standardId.attr("type", "hidden");
    standardId.attr("name", "standardId");
    standardId.attr("value", record.standardId);
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
    mini.confirm("确定删除？", "确定？",
        function (action) {
            if (action == "ok") {
                var url = jsUseCtxPath + "/standardManager/core/standardFileInfos/deleteStandardFiles.do";
                var data = {
                    standardId: record.standardId,
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
