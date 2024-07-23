var editForm = '';
var isFirstNode = false;
var editable = true;
$(function () {
    if (action == 'task') {
        bpmPreTaskTipInForm();
        getProcessNodeVars();
        if (editForm != '1' && !isFirstNode) {
            applyForm.setEnabled(false);
            mini.get('addItem').setEnabled(false);
            mini.get('removeItem').setEnabled(false);
            mini.get('openImport').setEnabled(false);
            itemGrid.setAllowCellEdit(false);
            editable = false;
        }
        applyForm.setData(ApplyObj);
    } else if (action == 'detail') {
        $("#detailToolBar").show();
        applyForm.setEnabled(false);
        applyForm.setData(ApplyObj);
        mini.get('addItem').setEnabled(false);
        mini.get('removeItem').setEnabled(false);
        mini.get('openImport').setEnabled(false);
        itemGrid.setAllowCellEdit(false);
        editable = false;
    } else if (action == 'edit') {
        applyForm.setData(ApplyObj);
    }
    loadGrid();
});

//流程实例图
function processInfo() {
    let instId = $("#instId").val();
    let url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: gkgfApplyEdit_name,
        width: 800,
        height: 600
    });
}


//获取环境变量
function getProcessNodeVars() {
    if (!nodeVarsStr) {
        nodeVarsStr = "[]";
    }
    var nodeVars = $.parseJSON(nodeVarsStr);
    for (var i = 0; i < nodeVars.length; i++) {
        if (nodeVars[i].KEY_ == 'editForm') {
            editForm = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isFirstNode') {
            isFirstNode = true;
        }
    }
}



//保存草稿
function saveApplyInfo(e) {
    var formValid = draftOrStartValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.saveDraft(e);
}

//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("applyForm");
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos = [];
    formData.vars = [];
    var data = itemGrid.getChanges();
    if (data.length > 0) {
        formData.itemChangeData=data;
    }
    return formData;
}

//保存草稿或提交时数据是否有效
function draftOrStartValid() {
    let model=$.trim(mini.get("model").getValue());
    if(!model) {
        return {"result": false, "message": gkgfApplyEdit_name1};
    }
    var checkResult=itemGridCheck();
    if(!checkResult.result) {
        return checkResult;
    }
    return {"result": true};
}

//启动流程
function startApplyProcess(e) {
    var formValid = draftOrStartValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    if(itemGrid.getData().length==0){
        mini.alert(gkgfApplyEdit_name2);
        return;
    }
    window.parent.startProcess(e);
}

//审批或者下一步
function applyApprove() {
    var formValid = draftOrStartValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.approve();
}
function addItem() {
    var newRow = {name: "New Row"};
    itemGrid.addRow(newRow, 0);
    itemGrid.beginEditCell(newRow, "workType");
}
function removeItem() {
    var rows = itemGrid.getSelecteds();
    if (rows.length > 0) {
        mini.showMessageBox({
            title: gkgfApplyEdit_name3,
            iconCls: "mini-messagebox-info",
            buttons: ["ok", "cancel"],
            message: gkgfApplyEdit_name4,
            callback: function (action) {
                if (action == "ok") {
                    itemGrid.removeRows(rows, false);
                }
            }
        });
    } else {
        mini.alert(gkgfApplyEdit_name5);
        return;
    }
}
//验证数据
function itemGridCheck() {
    var result={result:true};
    var data = itemGrid.getChanges();
    if (data.length > 0) {
        for (var i = 0; i < data.length; i++) {
            if (data[i]._state == 'removed') {
                continue;
            }
            if (!data[i].workType || !data[i].tool || !data[i].workItem ) {
                result.message = gkgfApplyEdit_name6;
                result.result = false;
                return result;
            }
        }
    }
    return result;
}
function loadGrid() {
    var applyId = mini.get('id').getValue();
    var paramArray = [{name: "applyId", value: applyId}];
    var data = {};
    data.filter = mini.encode(paramArray);
    data.applyId = applyId;
    itemGrid.load(data);
}
function picAttach(e) {
    var record = e.record;
    var detailId = record.id;
    var s = '';
    if(detailId==''||detailId=='undefined'||detailId==undefined){
        s += '<span  title=' + gkgfApplyEdit_name7 + ' style="color: grey"">' + gkgfApplyEdit_name7 + '</span>';
    }else{
        s += '<span  title=' + gkgfApplyEdit_name7 + ' style="cursor: pointer;color: #0a7ac6" onclick="showFilePage(\'' + detailId + '\',\'pic\')">' + gkgfApplyEdit_name7 + '</span>';
    }
    return s;
}
function videoAttach(e) {
    var record = e.record;
    var detailId = record.id;
    var s = '';
    if(detailId==''||detailId=='undefined'||detailId==undefined){
        s += '<span  title=' + gkgfApplyEdit_name7 + ' style="color: grey"">' + gkgfApplyEdit_name7 + '</span>';
    }else{
        s += '<span  title=' + gkgfApplyEdit_name7 + ' style="cursor: pointer;color: #0a7ac6" onclick="showFilePage(\'' + detailId + '\',\'video\')">' + gkgfApplyEdit_name7 + '</span>';
    }
    return s;
}
function reportAttach(e) {
    var record = e.record;
    var detailId = record.id;
    var s = '';
    if(detailId==''||detailId=='undefined'||detailId==undefined){
        s += '<span  title=' + gkgfApplyEdit_name8 + ' style="color: grey"">' + gkgfApplyEdit_name8 + '</span>';
    }else{
        s += '<span  title=' + gkgfApplyEdit_name8 + ' style="cursor: pointer;color: #0a7ac6" onclick="showFilePage(\'' + detailId + '\',\'report\')">' + gkgfApplyEdit_name8 + '</span>';
    }
    return s;
}
function showFilePage(detailId,fileType) {
    mini.open({
        title: gkgfApplyEdit_name9,
        url: jsUseCtxPath + "/gkgf/core/apply/fileWindow.do?detailId=" + detailId+"&fileType="+fileType+"&editable="+editable,
        width: 1000,
        height: 500,
        showModal: false,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            // searchDoc();
        }
    });
}


//导入
function openImportDialog() {
    importWindow.show();
}
function closeWindow() {
    importWindow.hide();
    clearImportFile();
}

//导入模板下载
function downTemplate() {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/gkgf/core/apply/templateDownload.do");
    $("body").append(form);
    form.submit();
    form.remove();
}

//触发文件选择
function uploadImportFile() {
    $("#inputImportFile").click();
}

//文件类型判断及文件名显示
function getImportFile() {
    var fileList = $("#inputImportFile")[0].files;
    if (fileList && fileList.length > 0) {
        var fileNameSuffix = fileList[0].name.split('.').pop();
        if (fileNameSuffix == 'xls'||fileNameSuffix == 'xlsx') {
            mini.get("importFileName").setValue(fileList[0].name);
        }
        else {
            clearImportFile();
            mini.alert(gkgfApplyEdit_name10);
        }
    }
}

//清空文件
function clearImportFile() {
    $("#inputImportFile").val('');
    mini.get("importFileName").setValue('');
}

//上传批量导入
function importData() {
    var file = null;
    var fileList = $("#inputImportFile")[0].files;
    if (fileList && fileList.length > 0) {
        file = fileList[0];
    }
    if (!file) {
        mini.alert(gkgfApplyEdit_name11);
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
                        if(returnObj.data){
                            itemGrid.setData(returnObj.data)
                        }
                        mini.alert(message);
                    }
                }
            }
        };

        // 开始上传
        xhr.open('POST', jsUseCtxPath + '/gkgf/core/apply/importExcel.do', false);
        xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        var fd = new FormData();
        fd.append('importFile', file);
        xhr.send(fd);
    }
}
