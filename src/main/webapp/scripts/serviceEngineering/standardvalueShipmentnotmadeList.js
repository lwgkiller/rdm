$(function () {
    searchFrm();
});


function completionRenderer(e) {
    var record = e.record;
    switch(record.betaCompletion) {
        case 'dzz':
            return '<span style="color:orange">待制作</span>';
        case 'zzing':
            return '<span style="color:#4BFF19">制作中</span>';
        case 'zzwc':
            return '<span style="color:#177BFF">制作完成</span>';
    }
}

function versionTypeRenderer(e) {
    var record = e.record;
    var arr = [{key:'cgb',value:'常规版'},{key:'csb',value:'测试版'},{key:'wzb',value:'完整版'}];
    return $.formatItemValue(arr,record.versionType);
}

function oncloseClick(e) {
    var obj = e.sender;
    obj.setText("");
    obj.setValue("");
}

//新增
function addStandardvalueShipmentnotmade() {
    var url = jsUseCtxPath + "/serviceEngineering/core/standardvalue/shipmentnotmade/toEditPage.do?action=add";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (standardvalueShipmentnotmadeListGrid) {
                standardvalueShipmentnotmadeListGrid.reload()
            }
        }
    }, 1000);
}

//删除
function delStandardvalueShipmentnotmade(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = grid.getSelecteds();
    }
    if (rows.length <= 0) {
        mini.alert(standardvalueShipmentnotmadeList_name3);
        return;
    }
    mini.confirm(standardvalueShipmentnotmadeList_name4, standardvalueShipmentnotmadeList_name5, function (action) {
        if (action != 'ok') {
            return;
        } else {
            var rowIds = [];
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                if(r.betaCompletion == 'dzz' && (currentUserNo=='admin'|| currentUserId==r.CREATE_BY_)) {
                    rowIds.push(r.id);
                }
            }
            if(rowIds.length<=0) {
                mini.alert(standardvalueShipmentnotmadeList_name6);
                return;
            }
            _SubmitJson({
                url: jsUseCtxPath + "/serviceEngineering/core/standardvalue/shipmentnotmade/deleteData.do",
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


// 编辑
function editStandardvalueShipmentnotmade(zzcsbId) {
    var url = jsUseCtxPath + "/serviceEngineering/core/standardvalue/shipmentnotmade/toEditPage.do?action=edit&zzcsbId=" + zzcsbId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (standardvalueShipmentnotmadeListGrid) {
                standardvalueShipmentnotmadeListGrid.reload()
            }
        }
    }, 1000);
}

//明细
function toStandardvalueShipmentnotmadeDetail(zzcsbId) {
    var action = "detail";
    var url = jsUseCtxPath + "/serviceEngineering/core/standardvalue/shipmentnotmade/toEditPage.do?action=detail&zzcsbId=" + zzcsbId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (standardvalueShipmentnotmadeListGrid) {
                standardvalueShipmentnotmadeListGrid.reload()
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
    clearUploadFile();
    searchFrm();
}


//导入模板下载
function downloadTemplate() {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/serviceEngineering/core/standardvalue/shipmentnotmade/downloadTemplate.do");
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
            mini.alert(standardvalueShipmentnotmadeList_name7);
        }
    }
}

//清空文件
function clearUploadFile() {
    $("#inputFile").val('');
    mini.get("fileName").setValue('');
}

//上传批量导入
function importStandardvalueShipmentnotmade() {
    var file = null;
    var fileList = $("#inputFile")[0].files;
    if (fileList && fileList.length > 0) {
        file = fileList[0];
    }
    if (!file) {
        mini.alert(standardvalueShipmentnotmadeList_name8);
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
        xhr.open('POST', jsUseCtxPath + '/serviceEngineering/core/standardvalue/shipmentnotmade/importStandardvalueShipmentnotmade.do', false);
        xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        var fd = new FormData();
        fd.append('importFile', file);
        xhr.send(fd);
    }
}

function startProcess(record) {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/JXBZZBSH/start.do";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (standardvalueShipmentnotmadeListGrid) {
                standardvalueShipmentnotmadeListGrid.reload()
            }
        }
    }, 1000);
}