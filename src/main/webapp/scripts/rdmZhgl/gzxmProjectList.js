$(function () {
})

//新增审批流程
function doApply() {
    var productRows = listGrid.getSelecteds();
    var projectIdArry = [];
    if (productRows.length != 1) {
        mini.alert("请选择一个项目操作！");
        return;
    } else {
        for (var i = 0; i < productRows.length; i++) {
            projectIdArry.push(productRows[i].id);
            var reportUserId = productRows[i].reportUserId;
            if(currentUserId!=reportUserId){
                mini.alert("只有填报人可以提交审批！");
                return;
            }
        }
    }
    var url = jsUseCtxPath + "/bpm/core/bpmInst/GZXM-XMSP/start.do?projectIds=" + projectIdArry.join(',');
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
        }
    }, 1000);
}

function addForm() {
    let url = jsUseCtxPath + "/rdmZhgl/core/gzxm/project/getEditPage.do?action=add";
    mini.open({
        title: "新增",
        url: url,
        width: 1000,
        height: 600,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            listGrid.reload();
        }
    });
}

//修改
function editForm(id) {
    var url = jsUseCtxPath + "/rdmZhgl/core/gzxm/project/getEditPage.do?action=edit&&mainId=" + id;
    var title = "编辑";
    mini.open({
        title: title,
        url: url,
        width: 1000,
        height: 600,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            searchFrm();
        }
    });
}

//删除记录
function removeRow() {
    var rows = [];
    rows = listGrid.getSelecteds();
    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }
    mini.confirm("确定取消选中记录？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var ids = [];
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                if(currentUserId!=r.reportUserId&&currentUserId!=r.CREATE_BY_){
                    mini.alert("只有填报人或者创建人可以删除！");
                    return;
                }
                ids.push(r.id);
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/rdmZhgl/core/gzxm/project/remove.do",
                    method: 'POST',
                    data: {ids: ids.join(',')},
                    success: function (text) {
                        if (listGrid) {
                            listGrid.reload();
                        }
                    }
                });
            }
        }
    });
}
function taskPage(mainId,gzxmAdmin,isReporter) {
    var url = jsUseCtxPath + "/rdmZhgl/core/gzxm/project/projectTaskPage.do?mainId=" + mainId+"&gzxmAdmin="+gzxmAdmin+"&isReporter="+isReporter;
    var title = "项目任务列表";
    mini.open({
        title: title,
        url: url,
        width: 1800,
        height: 800,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
        }
    });
}
//导入
function openImportWindow() {
    importWindow.show();
}
function closeImportWindow() {
    importWindow.hide();
    clearUploadFile();
    searchFrm();
}

//导入模板下载
function downImportTemplate() {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/rdmZhgl/core/gzxm/project/importTemplateDownload.do");
    $("body").append(form);
    form.submit();
    form.remove();
}

//触发文件选择
function uploadFile() {
    $("#inputFile").click();
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

//清空文件
function clearUploadFile() {
    $("#inputFile").val('');
    mini.get("fileName").setValue('');
}

//上传批量导入
function importProduct() {
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
                    }
                }
            }
        };

        // 开始上传
        xhr.open('POST', jsUseCtxPath + '/rdmZhgl/core/gzxm/project/importExcel.do', false);
        xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        var fd = new FormData();
        fd.append('importFile', file);
        xhr.send(fd);
    }
}
function exportProjectTask(){
    var rows = listGrid.getSelecteds();
    if (rows.length != 1) {
        mini.alert("请选择一个项目进行导出任务！");
        return;
    }
    var params = [];
    params.push({name: 'mainId',value: rows[0].id});
    $("#filter").val(mini.encode(params));
    var excelForm = $("#excelForm");
    excelForm.attr("action", jsUseCtxPath + "/rdmZhgl/core/gzxm/project/exportProjectTask.do?mainId="+ rows[0].id);
    excelForm.submit();
}
