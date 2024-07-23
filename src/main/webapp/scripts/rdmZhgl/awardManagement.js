$(function () {
    searchFrm();
});

/**
 * 添加弹窗
 */
function openAmgEditWindow( action, awardId) {
    var title = "";
    if (action == "add") {
        title = "管理奖添加"
    }else {
        title = "管理奖修改"
    }
    mini.open({
        title: title,
        url: jsUseCtxPath + "/zhgl/core/awardManagement/edit.do?awardId=" + awardId + '&action='+ action ,
        width: 800,
        height: 600,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            searchAmgList();
        }
    });

}

function searchAmgList() {
    var queryParam = [];
    //其他筛选条件
    var prizewinner = $.trim(mini.get("prizewinner").getValue());
    if (prizewinner) {
        queryParam.push({name: "prizewinner", value: prizewinner});
    }
    var commendUnit = $.trim(mini.get("commendUnit").getValue());
    if (commendUnit) {
        queryParam.push({name: "commendUnit", value: commendUnit});
    }
    var awardType = $.trim(mini.get("awardType").getValue());
    if (awardType) {
        queryParam.push({name: "awardType", value: awardType});
    }
    var projectName = $.trim(mini.get("projectName").getValue());
    if (projectName) {
        queryParam.push({name: "projectName", value: projectName});
    }
    var rdTimeStart = $.trim(mini.get("rdTimeStart").getValue());
    if (rdTimeStart) {
        queryParam.push({name: "rdTimeStart", value: rdTimeStart});
    }
    var rdTimeEnd = $.trim(mini.get("rdTimeEnd").getValue());
    if (rdTimeEnd) {
        queryParam.push({name: "rdTimeEnd", value: rdTimeEnd});
    }
    var data = {};
    data.filter = mini.encode(queryParam);
    data.pageIndex = amgListGrid.getPageIndex();
    data.pageSize = amgListGrid.getPageSize();
    data.sortField = amgListGrid.getSortField();
    data.sortOrder = amgListGrid.getSortOrder();
    //查询
    amgListGrid.load(data);
}


/**
 * 删除
 * @param record
 */
function removeAmg(record) {

    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = amgListGrid.getSelecteds();
    }
    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }
    mini.confirm("确定删除选中记录？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var rowIds = [];
            var fileIds = [];
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                rowIds.push(r.id);
                fileIds.push(r.fileId);
            }

            _SubmitJson({
                url: jsUseCtxPath + "/zhgl/core/awardManagement/deleteAmg.do?",
                method: 'POST',
                showMsg: false,
                data: {ids: rowIds.join(','),fileIds: fileIds.join(',')},
                success: function (data) {
                    if (data) {
                        mini.alert(data.message);
                        amgListGrid.load();
                    }
                }
            });
        }
    });
}

function exportAmg() {
    var parent = $(".search-form");
    var inputAry = $("input", parent);
    var params = [];
    inputAry.each(function (i) {
        var el = $(this);
        var obj = {};
        obj.name = el.attr("name");
        if (!obj.name) return true;
        obj.value = el.val();
        params.push(obj);
    });
    $("#filter").val(mini.encode(params));
    var excelForm = $("#excelForm");
    excelForm.submit();
}

//导入
function importAmgMaterialName() {
    importWindow.show();
}

//上传批量导入
function importAmgMaterial() {
    var file = null;
    var fileList = $("#inputFile")[0].files;
    if (fileList && fileList.length > 0) {
        file = fileList[0];
    }
    if (!file) {
        mini.alert('请选择文件！');
        return;
    }
    //XMLHttpRequest方式上传表单
    var xhr = false;
    try {
        //尝试创建 XMLHttpRequest 对象，除 IE 外的浏览器都支持这个方法。
        xhr = new XMLHttpRequest();
    } catch (e) {
        //使用较新版本的 IE 创建 IE 兼容的对象（Msxml2.XMLHTTP）。
        xhr = ActiveXobject("Msxml12.XMLHTTP");
    }

    if (xhr.upload) {
        xhr.onreadystatechange = function (e) {
            if (xhr.readyState == 4) {
                if (xhr.status == 200) {
                    if (xhr.responseText) {
                        var returnObj = JSON.parse(xhr.responseText);
                        var message = '';
                        if (returnObj.message) {
                            message = returnObj.message;
                        }
                        mini.alert(message);
                        closeImportWindow();
                    }
                }
            }
        };

        // 开始上传
        xhr.open('POST', jsUseCtxPath + '/zhgl/core/awardManagement/importExcel.do', false);
        xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        var fd = new FormData();
        fd.append('importFile', file);
        xhr.send(fd);
    }
    // agpListGrid.load(data);
}

function closeImportWindow() {
    importWindow.hide();
    clearUploadFile();
    searchFrm();
}


//文件类型判断及文件名显示
function getSelectFile() {
    var fileList = $("#inputFile")[0].files;
    if (fileList && fileList.length > 0) {
        var fileNameSuffix = fileList[0].name.split('.').pop();
        if (fileNameSuffix == 'xls'||fileNameSuffix == 'xlsx') {
            mini.get("fileName").setValue(fileList[0].name);
        }
        else {
            clearUploadFile();
            mini.alert('请上传excel文件！');
        }
    }
}

//触发文件选择
function uploadFile() {
    $("#inputFile").click();
}

//清空文件
function clearUploadFile() {
    $("#inputFile").val('');
    mini.get("fileName").setValue('');
}

//导入模板下载
function downAmgImportTemplate() {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/zhgl/core/awardManagement/importTemplateDownload.do");
    $("body").append(form);
    form.submit();
    form.remove();
}

function returnAmgPreviewSpan(fileName, fileId, formId, coverContent) {
    var fileType = getFileType(fileName);
    var s = '';
    if (fileType == 'other') {
        s = '<span  title="预览" style="color: silver" >预览</span>';
    } else if (fileType == 'pdf') {
        var url = '/zhgl/core/awardManagement/amgPdfPreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
    } else if (fileType == 'office') {
        var url = '/zhgl/core/awardManagement/amgOfficePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
    } else if (fileType == 'pic') {
        var url = '/zhgl/core/awardManagement/amgImagePreview.do';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
    }
    return s;
}

function downLoadAmgFile(fileName, fileId, jsjlId) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/zhgl/core/awardManagement/fileDownload.do?action=download");
    var inputFileName = $("<input>");
    inputFileName.attr("type", "hidden");
    inputFileName.attr("name", "fileName");
    inputFileName.attr("value", fileName);
    var inputJsjlId = $("<input>");
    inputJsjlId.attr("type", "hidden");
    inputJsjlId.attr("name", "id");
    inputJsjlId.attr("value", jsjlId);
    var inputFileId = $("<input>");
    inputFileId.attr("type", "hidden");
    inputFileId.attr("name", "fileId");
    inputFileId.attr("value", fileId);
    $("body").append(form);
    form.append(inputFileName);
    form.append(inputJsjlId);
    form.append(inputFileId);
    form.submit();
    form.remove();

}
//是否是奖项荣誉专员
function whetherJxry(userRoles) {
    if(!userRoles) {
        return false;
    }
    for (var i = 0; i < userRoles.length; i++) {
        if (userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-LEADER'||userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-BELONG') {
            if(userRoles[i].NAME_.indexOf('奖项荣誉专员')!=-1) {
                return true;
            }
        }
    }
    return false;
}
$(function () {
    if(!whetherJxry(currentUserRoles)){
        mini.get("addId").hide();
        mini.get("deletedId").hide();
        mini.get("importMaterialName").hide();
    }
});