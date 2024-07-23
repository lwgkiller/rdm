$(function () {
searchFrm();
});



function addNew(oilId) {
    if(!oilId) {
        oilId='';
    }
    mini.open({
        title: "新增",
        url: jsUseCtxPath + "/environment/core/Oil/edit.do?&oilId=" + oilId,
        width: 1050,
        height: 550,
        allowResize: true,
        onload: function () {
            searchFrm();
        },
        ondestroy:function () {
            searchFrm();
        }
    });
}
function task(oilId) {
    if(!oilId) {
        oilId='';
    }
    var action = "task";
    mini.open({
        title: "办理",
        url: jsUseCtxPath + "/environment/core/Oil/edit.do?action=" + action + "&oilId=" + oilId,
        width: 1050,
        height: 550,
        allowResize: true,
        onload: function () {
            searchFrm();
        },
        ondestroy:function () {
            searchFrm();
        }
    });
}

function oilDetail(oilId) {
    var action = "detail";
    mini.open({
        title: "明细",
        url: jsUseCtxPath + "/environment/core/Oil/edit.do?action=" + action + "&oilId=" + oilId,
        width: 1050,
        height: 550,
        allowResize: true,
        onload: function () {
            searchFrm();
        }
    });
}

function oilFile(oilId) {
    mini.open({
        title: "附件列表",
        url: jsUseCtxPath + "/environment/core/Oil/fileList.do?&oilId=" + oilId,
        width: 1050,
        height: 550,
        allowResize: true,
        onload: function () {
            searchFrm();
        }
    });
}
function removeOil(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = oilListGrid.getSelecteds();
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
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                rowIds.push(r.oilId);
            }
            _SubmitJson({
                url: jsUseCtxPath + "/environment/core/Oil/deleteoil.do",
                method: 'POST',
                showMsg:false,
                data: {ids: rowIds.join(',')},
                success: function (data) {
                    if (data) {
                        mini.alert(data.message);
                        searchFrm();
                    }
                }
            });
        }
    });
}

function openImportWindow() {
    importWindow.show();
}
function closeImportWindow() {
    importWindow.hide();
    clearUploadFile();
    searchFrm();
}

//导入模板下载
function downImportOil() {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/environment/core/Oil/importTemplateDownload.do");
    $("body").append(form);
    form.submit();
    form.remove();
}

//触发文件选择
function uploadFile() {
    $("#inputFile").click();
}

//文件类型判断及文件名显示
function    getSelectFile() {
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
        xhr.open('POST', jsUseCtxPath + '/environment/core/Oil/importExcel.do', false);
        xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        var fd = new FormData();
        fd.append('importFile', file);
        xhr.send(fd);
    }
}