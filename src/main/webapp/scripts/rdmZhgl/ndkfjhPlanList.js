$(function () {
    var column = listGrid.getColumn("process");
    var currentDate = new Date();
    /**
     * 如果是5号之前则展示上个月，5号之后展示本月
     * */
    var year = currentDate.getFullYear();
    var day = currentDate.getDate();
    var month = currentDate.getMonth();
    var showDate='';
    if(day<5){
        showDate = '('+month+'月5日）';
    }else{
        showDate = '('+(month+1)+'月5日）';
    }
    mini.get('planYear').setValue(year);
    listGrid.updateColumn(column,{header:'当前进度'+showDate});
    searchFrm()
})
function addForm() {
    let url = jsUseCtxPath + "/rdmZhgl/core/ndkfjh/budget/getEditPage.do?action=add";
    mini.open({
        title: "新增",
        url: url,
        width: 1200,
        height: 1000,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            searchFrm()
        }
    });
}
//修改
function editForm(id,action,permission) {
    var url = jsUseCtxPath + "/rdmZhgl/core/ndkfjh/planDetail/editPage.do?action="+action+"&&id=" + id+"&&permission="+permission;
    var title = "编辑";
    if(action=='view'){
        title = '查看';
    }
    mini.open({
        title: title,
        url: url,
        width: 1000,
        height: 800,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            searchFrm();
        }
    });
}
function openEditWindow() {
    yearSelectedWindow.show();
}
function closeEditWindow() {
    yearSelectedWindow.hide();
}

function asyncBudgetPlan() {
    yearForm.validate();
    if (!yearForm.isValid()) {
        mini.alert("年份必须选择");
        return;
    }
    var formData = yearForm.getData();
    mini.confirm("确定同步预算计划？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var config = {
                url: jsUseCtxPath + "/rdmZhgl/core/ndkfjh/planDetail/asyncBudgetPlan.do",
                method: 'POST',
                data: formData,
                success: function (result) {
                    //如果存在自定义的函数，则回调
                    var result = mini.decode(result);
                    if (result.success) {
                        closeEditWindow();
                        searchFrm();
                    } else {
                    }
                    ;
                }
            }
            _SubmitJson(config);
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
                ids.push(r.id);
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/rdmZhgl/core/ndkfjh/budget/remove.do",
                    method: 'POST',
                    data: {ids: ids.join(',')},
                    success: function (text) {
                        searchFrm();
                    }
                });
            }
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
    form.attr("action", jsUseCtxPath + "/rdmZhgl/core/ndkfjh/budget/importTemplateDownload.do");
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
    var budgetYear = mini.get("reportYear").getValue();
    if (!budgetYear) {
        mini.alert('请选择年份！');
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
        xhr.open('POST', jsUseCtxPath + '/rdmZhgl/core/ndkfjh/budget/importExcel.do?budgetYear='+budgetYear, false);
        xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        var fd = new FormData();
        fd.append('importFile', file);
        xhr.send(fd);
    }
}

