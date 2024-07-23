$(function () {
    var type1="专利类型";
    $.ajax({
        url: zgzlPath + '/zhgl/core/zlgl/enumListQuery.do?type='+type1,
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                mini.get("#zllxId").load(data);
            }
        }
    });
    var type2="案件状态";
    $.ajax({
        url: zgzlPath + '/zhgl/core/zlgl/enumListQuery.do?type='+type2,
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                mini.get("#gnztId").load(data);
            }
        }
    });
    // mini.get('sysLastUpdateTime').setValue(lastUpdateTime)
    searchFrm();

});
//明细
function zgzlDetail(zgzlId) {
    var action = "detail";
    var url = zgzlPath + "/zhgl/core/zlgl/zgzlPage.do?zgzlId=" + zgzlId+ "&action=" + action ;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (zgzlListGrid) {
                zgzlListGrid.reload()
            }
        }
    }, 1000);
}
//编辑
function zgzlkEdit(zgzlId) {
    var action = "edit";
    var url = zgzlPath + "/zhgl/core/zlgl/zgzlPage.do?zgzlId=" + zgzlId+ "&action=" + action ;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (zgzlListGrid) {
                zgzlListGrid.reload()
            }
        }
    }, 1000);
}
//变更
function zgzlChange(zgzlId) {
    var action = "change";
    var url = zgzlPath + "/zhgl/core/zlgl/zgzlPage.do?zgzlId=" + zgzlId+ "&action=" + action ;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (zgzlListGrid) {
                zgzlListGrid.reload()
            }
        }
    }, 1000);
}
//新增
function addZgzl() {
    var url = zgzlPath + "/zhgl/core/zlgl/zgzlPage.do";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (zgzlListGrid) {
                zgzlListGrid.reload()
            }
        }
    }, 1000);
}
function removeZgzl(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = zgzlListGrid.getSelecteds();
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
                rowIds.push(r.zgzlId);
            }

            _SubmitJson({
                url: zgzlPath + "/zhgl/core/zlgl/deleteZgzl.do",
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
function exportZgzl() {
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
//是否是专利管理员
function whetherZgzl(userRoles) {
    if(!userRoles) {
        return false;
    }
    for (var i = 0; i < userRoles.length; i++) {
        if (userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-LEADER'||userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-BELONG') {
            if(userRoles[i].NAME_.indexOf('专利工程师')!=-1) {
                return true;
            }
        }
    }
    return false;
}
$(function () {
    if(!whetherZgzl(currentUserRoles)){
        mini.get("addZgzl").hide();
        mini.get("removeZgzl").hide();
        mini.get("importId").hide();
        mini.get("saveTime").hide();
        mini.get("sysLastUpdateTime").setEnabled(false)
    }
});
//导入
function openImportWindow() {
    importWindow.show();
}
function closeImportWindow() {
    importWindow.hide();
    clearUploadFile();
    searchFrm();
}
//清空文件
function clearUploadFile() {
    $("#inputFile").val('');
    mini.get("fileName").setValue('');
}
//导入模板下载
function downImportTemplate() {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", zgzlPath + "/zhgl/core/zlgl/importTemplateDownload.do");
    $("body").append(form);
    form.submit();
    form.remove();
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
//上传批量导入
function importProduct() {
    var file = null;
    var upType="zgzl";
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
                            zgzlListGrid.reload();
                        }
                        mini.alert(messearchFrmsage);
                    }
                }
            }
        };searchFrm

        // 开始上传
        xhr.open('POST', zgzlPath + '/zhgl/core/zlgl/importExcel.do', false);
        xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        var fd = new FormData();
        fd.append('importFile', file);
        fd.append('upType',upType);
        xhr.send(fd);
        zgzlListGrid.reload();
        closeImportWindow()
    }
}
function saveTime() {
    var postData = {};

    var lastSaveTime = mini.get("sysLastUpdateTime").getFormValue();
    var alias = "ZLTZ_LAST_TIME";
    postData.lastSaveTime = lastSaveTime;
    postData.alias = alias;

    //未选时间alert
    if(!lastSaveTime){
        mini.alert('请选择最后更新时间！');
    }else{

        $.ajax({
            url: zgzlPath + "/zhgl/core/zlgl/saveTime.do",
            type: 'POST',
            contentType: 'application/json',
            data: mini.encode(postData),
            success: function () {
                mini.alert("最后更新时间"+lastSaveTime+"保存成功");
            }
        });


    }








}