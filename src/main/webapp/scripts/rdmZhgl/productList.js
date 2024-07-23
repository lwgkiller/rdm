var editableFlag = true;
$(function () {
    var isHistoryCat = mini.get("isHistory");
    isHistoryCat.setValue('0');
    mini.get('belongYear').setValue(new Date().getFullYear());
    if(processStatus){
        mini.get('processStatus').setValue(processStatus);
    }
    if(important){
        mini.get('important').setValue(important);
    }
    if(belongYear){
        mini.get('belongYear').setValue(belongYear);
    }
    if(reportType){
        mini.get('belongYear').setValue("");
        mini.get('stage').setValue(reportType);
    }
    if(dateStart){
        mini.get('belongYear').setValue("");
        mini.get('reportStartDate').setValue(dateStart);
    }
    if(dateEnd){
        mini.get('belongYear').setValue("");
        mini.get('reportEndDate').setValue(dateEnd);
    }
    searchFrm();
})

function addForm() {
    let url = jsUseCtxPath + "/rdmZhgl/core/product/getEditPage.do?action=add";
    mini.open({
        title: "新增",
        url: url,
        width: 1200,
        height: 800,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            productGrid.reload();
        }
    });
}
function cellbeginedit(e) {
    if(common){
        productGrid.setAllowCellEdit(false);
        return;
    }
    var field = e.field;
    var id = e.record.id;
    var mainId = e.record.mainId;
    //如果填写样机完成转测试的完成时间，则要验证是否样机照片上传完整
    if (field == 'yjwczcs_date' && e.record.itemType == '4') {
        let postData = {"id": id};
        let url = jsUseCtxPath + '/rdmZhgl/core/product/verifyPic.do';
        let resultData = ajaxRequest(url, 'POST', false, postData);
        if (!resultData.success) {
            mini.alert(resultData.message);
            productGrid.updateRow(e.record, {yjwczcs_date: ''});
            return
        }
    }
    if (!permission) {
        if(field.indexOf('_date')>0){
            if ((e.record.itemType == '3'||e.record.itemType == '1')&&field!='ssrq_date') {
                mini.alert("产品专员编辑计划时间！");
                return;
            }else if(e.record.itemType == '4'){
                //判断是否是产品主管
                var productLeader = e.record.productLeader;
                if(currentUserId!=productLeader){
                    mini.alert("产品主管编辑完成日期！");
                    editableFlag = false;
                    return;
                }else{
                    editableFlag = false;
                    //先判断上市日期，和是否新销售型号是否填写
                    let postData = {"mainId": mainId};
                    let url = jsUseCtxPath + '/rdmZhgl/core/product/verifyMarket.do';
                    let resultData = ajaxRequest(url, 'POST', false, postData);
                    if (!resultData.success) {
                        mini.alert(resultData.message);
                        return
                    }
                    //判断是否已经关联真正的产品型谱
                    if(!e.record.productId) {
                        mini.alert("尚未关联正式的产品设计型号，请确保产品型谱中已维护设计型号后，联系计划管理员关联！");
                        e.editor.setEnabled(false);
                        return
                    }
                    isShow = false;
                    productGrid.setAllowCellEdit(false);
                    mini.confirm("填写完成日期需要审批，是否启动审批流程？", "提示", function (action) {
                        productGrid.setAllowCellEdit(true);
                        if (action != 'ok') {
                            return;
                        } else {
                            //先判断是否已经启动过流程
                            let postData = {"mainId": mainId,"item":field};
                            let postUrl = jsUseCtxPath + '/rdmZhgl/core/xpszFinish/verify.do';
                            let resultData = ajaxRequest(postUrl, 'POST', false, postData);
                            if (!resultData.success) {
                                mini.alert(resultData.message);
                                return
                            }
                            var url = jsUseCtxPath + "/bpm/core/bpmInst/XPSZ-WCRQSP/start.do?mainId="+mainId+"&item="+field;
                            window.open(url);
                        }
                    });
                }
            }
        }
    }
}
function cellendedit(e) {
    if(!editableFlag){
        return;
    }
    var field = e.field;
    var id = e.record.id;
    var date = e.editor.text;
    let postData = {"id": id, "field": field, "date": date, "itemType": e.record.itemType, "mainId": e.record.mainId};
    let url = jsUseCtxPath + '/rdmZhgl/core/product/updateDate.do';
    let resultData = ajaxRequest(url, 'POST', false, postData);
    if (resultData.success) {
        // productGrid.reload();
    }
}

//修改
function editForm() {
    var rows = [];
    rows = productGrid.getSelecteds();
    if (rows.length != 1) {
        mini.alert("请选中一条记录进行操作！");
        return;
    }
    var mainId = rows[0].mainId;
    var url = jsUseCtxPath + "/rdmZhgl/core/product/getEditPage.do?action=edit&&mainId=" + mainId;
    var title = "编辑";
    mini.open({
        title: title,
        url: url,
        width: 1200,
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
//批量修改页面
function batchEditPlanDate() {
    var rows = [];
    rows = productGrid.getSelecteds();
    if (rows.length != 1) {
        mini.alert("请选中一条记录进行操作！");
        return;
    }
    var mainId = rows[0].mainId;
    var url = jsUseCtxPath + "/rdmZhgl/core/product/batchEditDatePage.do?action=edit&&mainId=" + mainId;
    var title = "编辑";
    mini.open({
        title: title,
        url: url,
        width: 1200,
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

function submitForm() {
    var rows = [];
    rows = productGrid.getSelecteds();
    if (rows.length != 1) {
        mini.alert("请选中一条记录进行操作！");
        return;
    }
}

//删除记录
function removeRow() {
    var rows = [];
    rows = productGrid.getSelecteds();
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
                ids.push(r.mainId);
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/rdmZhgl/core/product/remove.do",
                    method: 'POST',
                    data: {ids: ids.join(',')},
                    success: function (text) {
                        if (productGrid) {
                            productGrid.reload();
                        }
                    }
                });
            }
        }
    });
}

function modelPic(e) {
    var record = e.record;
    var id = record.id;
    var mainId = record.mainId;
    var s = '';
    if (record.itemType == '4') {
        s = '<a href="#" style="color:#44cef6;text-decoration:underline;" ' +
            'onclick="showFilePage(\'' + id + '\',\'' + mainId + '\')">样机照片</a>';
    }
    return s;
}

function showFilePage(id, mainId) {
    mini.open({
        title: "样机照片",
        url: jsUseCtxPath + "/rdmZhgl/core/product/fileWindow.do?id=" + id + "&mainId=" + mainId + "&coverContent=" + coverContent,
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

//新增提报审批流程
function doMonthApply() {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/XPSZ-YDTBLC/start.do";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
        }
    }, 1000);
}
//新增变更审批流程
function doChangeApply() {
    var productRows = productGrid.getSelecteds();
    var mainIds = [];
    if (productRows.length != 1) {
        mini.alert("请先选一条项目！");
        return;
    } else {
        for (var i = 0; i < productRows.length; i++) {
            mainIds.push(productRows[i].mainId);
        }
    }
    var url = jsUseCtxPath + "/bpm/core/bpmInst/XPSZ-BGLC/start.do?mainIds=" + mainIds.join(',');
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
        }
    }, 1000);
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
    form.attr("action", jsUseCtxPath + "/rdmZhgl/core/product/importTemplateDownload.do");
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
    var belongYear = mini.get('yearSelect').getText();
    if(!belongYear){
        mini.alert('请选择所属年份！');
        return;
    }
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
        xhr.open('POST', jsUseCtxPath + '/rdmZhgl/core/product/importExcel.do?belongYear='+belongYear, false);
        xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        var fd = new FormData();
        fd.append('importFile', file);
        xhr.send(fd);
    }
}


function exportBtn(){
    var params=[];
    var parent=$(".search-form");
    var inputAry=$("input",parent);
    inputAry.each(function(i){
        var el=$(this);
        var obj={};
        obj.name=el.attr("name");
        if(!obj.name) return true;
        obj.value=el.val();
        params.push(obj);
    });
    $("#filter").val(mini.encode(params));
    var excelForm = $("#excelForm");
    excelForm.submit();
}
function changeRecord() {
    var productRows = productGrid.getSelecteds();
    var mainId = '';
    if (productRows.length != 1) {
        mini.alert("请先选一条项目！");
        return;
    } else {
        mainId = productRows[0].mainId;
    }
    mini.open({
        title: "变更记录",
        url: jsUseCtxPath + "/rdmZhgl/core/product/recordChange.do?mainId=" + mainId ,
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
function onProjectName(e) {
    var s = '';
    var record = e.record;
    var projectId = record.projectId;
    if(projectId){
            s = '<span   style="cursor: pointer;color: #0a7ac6" onclick="openProject(\'' + projectId + '\')">'+record.projectName+'</span>';
    }
    return s;
}
function openProject(projectId) {
        var action = "detail";
        var url = jsUseCtxPath+"/xcmgProjectManager/core/xcmgProject/edit.do?action="+action+"&projectId=" + projectId;
        window.open(url);
}
