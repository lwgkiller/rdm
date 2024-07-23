$(function () {
    searchFrm();
});


function versionTypeRenderer(e) {
    var record = e.record;
    var arr = [{key:'cgb',value:'常规版'},{key:'wzb',value:'完整版'}];
    return $.formatItemValue(arr,record.versionType);
}

function productTypeRenderer(e) {
    var record = e.record;
    var arr = [
        {key:'lunWa',value:'轮挖'},
        {key:'teWa',value:'特挖'},
        {key:'dianWa',value:'电挖'},
        {key:'lvWa',value:'履挖'}
    ];
    return $.formatItemValue(arr,record.productType);
}

function priorityRenderer(e) {
    var record = e.record;
    var value = record.priority;
    var resultText = '';
    for (var i = 0; i < priorityList.length; i++) {
        if (priorityList[i].key_ == value) {
            resultText = priorityList[i].text;
            break
        }
    }
    return resultText;
}

function passBackRenderer(e) {
    var record = e.record;
    var arr = [{key:'hcwc',value:'回传完成'},{key:'hcz',value:'回传中'},{key:'whc',value:'未回传'}];
    return $.formatItemValue(arr,record.passBack);
}

function oncloseClick(e) {
    var obj = e.sender;
    obj.setText("");
    obj.setValue("");
}

//新增
function addJxxqxf() {
    var url = jsUseCtxPath + "/serviceEngineering/core/jxxqxf/jxxqxfEditPage.do?action=add";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (jxxqxfGrid) {
                jxxqxfGrid.reload()
            }
        }
    }, 1000);
}

//删除
function delJxxqxf(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = grid.getSelecteds();
    }
    if (rows.length <= 0) {
        mini.alert(jxxqxfList_name3);
        return;
    }
    mini.confirm(jxxqxfList_name4, jxxqxfList_name5, function (action) {
        if (action != 'ok') {
            return;
        } else {
            var rowIds = [];
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                if(currentUserNo=='admin'|| currentUserId==r.CREATE_BY_) {
                    rowIds.push(r.id);
                }
            }
            if(rowIds.length<=0) {
                mini.alert(jxxqxfList_name6);
                return;
            }
            _SubmitJson({
                url: jsUseCtxPath + "/serviceEngineering/core/jxxqxf/deleteJxxqxf.do",
                method: 'POST',
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


// 编辑
function editJxxqxf(jxxqxfId) {
    var url = jsUseCtxPath + "/serviceEngineering/core/jxxqxf/jxxqxfEditPage.do?action=edit&jxxqxfId=" + jxxqxfId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (jxxqxfGrid) {
                jxxqxfGrid.reload()
            }
        }
    }, 1000);
}

//明细
function toJxxqxfDetail(jxxqxfId) {
    var action = "detail";
    var url = jsUseCtxPath + "/serviceEngineering/core/jxxqxf/jxxqxfEditPage.do?action=detail&jxxqxfId=" + jxxqxfId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (jxxqxfGrid) {
                jxxqxfGrid.reload()
            }
        }
    }, 1000);
}


//导入
function openImportWindow() {
    importWindow.show();
}
function closeWindow() {
    importWindow.hide();
    mini.get("issueDepartmentId").setValue('');
    mini.get("issueDepartmentId").setText('');
    mini.get("importVersionType").setValue('');
    clearUploadFile();
    searchFrm();
}

function importVaild(formData) {
    var checkResult={};

    if(!formData.issueDepartmentId) {
        checkResult.success=false;
        checkResult.reason=jxxqxfList_name7;
        return checkResult;
    }
    if(!formData.issueDepartmentId) {
        checkResult.success=false;
        checkResult.reason=jxxqxfList_name7;
        return checkResult;
    }
    if(!formData.versionType) {
        checkResult.success=false;
        checkResult.reason=jxxqxfList_name8;
        return checkResult;
    }
    checkResult.success=true;
    return checkResult;
}

//导入模板下载
function downloadTemplate() {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/serviceEngineering/core/jxxqxf/downloadTemplate.do");
    $("body").append(form);
    form.submit();
    form.remove();
}

//触发文件选择
function uploadFile() {
    $("#inputFile").click();
}

//文件类型判断及文件名显示
function validFile() {
    var fileList = $("#inputFile")[0].files;
    if (fileList && fileList.length > 0) {
        var fileNameSuffix = fileList[0].name.split('.').pop();
        if (fileNameSuffix == 'xls'||fileNameSuffix == 'xlsx') {
            mini.get("fileName").setValue(fileList[0].name);
        }
        else {
            clearUploadFile();
            mini.alert(jxxqxfList_name9);
        }
    }
}

//清空文件
function clearUploadFile() {
    $("#inputFile").val('');
    mini.get("fileName").setValue('');
}

//上传批量导入
function importJxxqxf() {
    var formData = {};
    formData.issueDepartmentId = mini.get("issueDepartmentId").getValue();
    formData.issueDepartment = mini.get("issueDepartmentId").getText();
    formData.versionType = mini.get("importVersionType").getValue();
    var valid = importVaild(formData);
    if(!valid.success) {
        mini.alert(valid.reason);
        return;
    }
    var file = null;
    var fileList = $("#inputFile")[0].files;
    if (fileList && fileList.length > 0) {
        file = fileList[0];
    }
    if (!file) {
        mini.alert(jxxqxfList_name10);
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
        xhr.open('POST', jsUseCtxPath + '/serviceEngineering/core/jxxqxf/importJxxqxfExcel.do', false);
        xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        var fd = new FormData();
        fd.append('importFile', file);
        fd.append('issueDepartmentId',formData.issueDepartmentId);
        fd.append('issueDepartment',formData.issueDepartment);
        fd.append('versionType',formData.versionType);
        xhr.send(fd);
    }
}
function exportJxxqxf() {
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

function clearQueryForm() {
    mini.get("passBacks").setValue('');
    clearForm();
}