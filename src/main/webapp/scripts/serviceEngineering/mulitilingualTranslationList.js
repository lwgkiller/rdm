$(function () {
    if(isDYYFY == "false"){
        mini.get("importMaterialName").setEnabled(false);
        mini.get("deletedId").setEnabled(false);
        mini.get("addId").setEnabled(false);
    }
    showColumn();
    searchFrm();
});

function hideColumn() {
    var languageId = mini.get("languageId").getValue();
    if (languageId) {
        if (!languageId.match("english")) {
            var englishName = lbjfyListGrid.getColumn('englishName');
            lbjfyListGrid.hideColumn(englishName);
        }
        if (!languageId.match("russian")) {
            var russianName = lbjfyListGrid.getColumn('russianName');
            lbjfyListGrid.hideColumn(russianName);
        }
        if (!languageId.match("portuguese")) {
            var portugueseName = lbjfyListGrid.getColumn('portugueseName');
            lbjfyListGrid.hideColumn(portugueseName);
        }
        if (!languageId.match("germany")) {
            var germanyName = lbjfyListGrid.getColumn('germanyName');
            lbjfyListGrid.hideColumn(germanyName);
        }
        if (!languageId.match("spanish")) {
            var spanishName = lbjfyListGrid.getColumn('spanishName');
            lbjfyListGrid.hideColumn(spanishName);
        }
        if (!languageId.match("french")) {
            var frenchName = lbjfyListGrid.getColumn('frenchName');
            lbjfyListGrid.hideColumn(frenchName);
        }
        if (!languageId.match("italian")) {
            var italianName = lbjfyListGrid.getColumn('italianName');
            lbjfyListGrid.hideColumn(italianName);
        }
        if (!languageId.match("polish")) {
            var polishName = lbjfyListGrid.getColumn('polishName');
            lbjfyListGrid.hideColumn(polishName);
        }
        if (!languageId.match("turkish")) {
            var turkishName = lbjfyListGrid.getColumn('turkishName');
            lbjfyListGrid.hideColumn(turkishName);
        }
        if (!languageId.match("swedish")) {
            var swedishName = lbjfyListGrid.getColumn('swedishName');
            lbjfyListGrid.hideColumn(swedishName);
        }
        if (!languageId.match("danish")) {
            var danishName = lbjfyListGrid.getColumn('danishName');
            lbjfyListGrid.hideColumn(danishName);
        }
        if (!languageId.match("dutch")) {
            var dutchName = lbjfyListGrid.getColumn('dutchName');
            lbjfyListGrid.hideColumn(dutchName);
        }
        if (!languageId.match("slovenia")) {
            var sloveniaName = lbjfyListGrid.getColumn('sloveniaName');
            lbjfyListGrid.hideColumn(sloveniaName);
        }
        if (!languageId.match("romania")) {
            var romaniaName = lbjfyListGrid.getColumn('romaniaName');
            lbjfyListGrid.hideColumn(romaniaName);
        }
        if (!languageId.match("chineseT")) {
            var chineseTName = lbjfyListGrid.getColumn('chineseTName');
            lbjfyListGrid.hideColumn(chineseTName);
        }
    }
}

function showColumn() {
    var englishName = lbjfyListGrid.getColumn('englishName');
    lbjfyListGrid.showColumn(englishName);
    var russianName = lbjfyListGrid.getColumn('russianName');
    lbjfyListGrid.showColumn(russianName);
    var portugueseName = lbjfyListGrid.getColumn('portugueseName');
    lbjfyListGrid.showColumn(portugueseName);
    var germanyName = lbjfyListGrid.getColumn('germanyName');
    lbjfyListGrid.showColumn(germanyName);
    var spanishName = lbjfyListGrid.getColumn('spanishName');
    lbjfyListGrid.showColumn(spanishName);
    var frenchName = lbjfyListGrid.getColumn('frenchName');
    lbjfyListGrid.showColumn(frenchName);
    var italianName = lbjfyListGrid.getColumn('italianName');
    lbjfyListGrid.showColumn(italianName);
    var polishName = lbjfyListGrid.getColumn('polishName');
    lbjfyListGrid.showColumn(polishName);
    var turkishName = lbjfyListGrid.getColumn('turkishName');
    lbjfyListGrid.showColumn(turkishName);
    var swedishName = lbjfyListGrid.getColumn('swedishName');
    lbjfyListGrid.showColumn(swedishName);
    var danishName = lbjfyListGrid.getColumn('danishName');
    lbjfyListGrid.showColumn(danishName);
    var dutchName = lbjfyListGrid.getColumn('dutchName');
    lbjfyListGrid.showColumn(dutchName);
    var sloveniaName = lbjfyListGrid.getColumn('sloveniaName');
    lbjfyListGrid.showColumn(sloveniaName);
    var romaniaName = lbjfyListGrid.getColumn('romaniaName');
    lbjfyListGrid.showColumn(romaniaName);
    var chineseTName = lbjfyListGrid.getColumn('chineseTName');
    lbjfyListGrid.showColumn(chineseTName);
}


/**
 * 添加弹窗
 */
function openLbjEditWindow( action, chineseId) {
    var title = "";
    if (action == "add") {
        title = "零部件词汇添加"
    }else {
        title = "零部件词汇修改"
    }
    mini.open({
        title: title,
        url: jsUseCtxPath + "/yfgj/core/mulitilingualTranslation/edit.do?chineseId=" + chineseId + '&action'+ action ,
        width: 800,
        height: 600,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            searchLbjList();
        }
    });

}

function searchLbjList() {
    var queryParam = [];
    //其他筛选条件
    var materialName = $.trim(mini.get("materialName").getValue());
    if (materialName) {
        queryParam.push({name: "materialName", value: materialName});
    }
    var languageId = $.trim(mini.get("languageId").getValue());
    if (languageId) {
        queryParam.push({name: "languageId", value: languageId});
    }
    var data = {};
    data.filter = mini.encode(queryParam);
    data.pageIndex = lbjfyListGrid.getPageIndex();
    data.pageSize = lbjfyListGrid.getPageSize();
    data.sortField = lbjfyListGrid.getSortField();
    data.sortOrder = lbjfyListGrid.getSortOrder();
    //查询
    lbjfyListGrid.load(data);
}


/**
 * 零部件删除
 * @param record
 */
function removeLbj(record) {

    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = lbjfyListGrid.getSelecteds();
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
                rowIds.push(r.chineseId);
            }

            _SubmitJson({
                url: jsUseCtxPath + "/yfgj/core/mulitilingualTranslation/deleteLbj.do?",
                method: 'POST',
                showMsg: false,
                data: {ids: rowIds.join(',')},
                success: function (data) {
                    if (data) {
                        mini.alert(data.message);
                        lbjfyListGrid.load();
                    }
                }
            });
        }
    });
}

function exportMaterialName() {
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
    debugger;
    var languageList = $.trim(mini.get("languageId").getValue());
    var pageIndex = lbjfyListGrid.getPageIndex();
    var pageSize = lbjfyListGrid.getPageSize();
    $("#filter").val(mini.encode(params));
    $("#pageIndex").val(mini.encode(pageIndex));
    $("#pageSize").val(mini.encode(pageSize));
    $("#languageList").val(mini.encode(languageList));
    var excelForm = $("#excelForm");
    excelForm.submit();
}

//导入
function importMaterialName() {
    importWindow.show();
}

//上传批量导入
function importMaterial() {
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
        xhr.open('POST', jsUseCtxPath + '/yfgj/core/mulitilingualTranslation/importExcel.do', false);
        xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        var fd = new FormData();
        fd.append('importFile', file);
        xhr.send(fd);
    }
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
function downImportTemplate() {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/yfgj/core/mulitilingualTranslation/importTemplateDownload.do");
    $("body").append(form);
    form.submit();
    form.remove();
}

