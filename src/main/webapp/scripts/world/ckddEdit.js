var first = "";
var second = "";
var third = "";
var forth = "";
$(function () {
    mini.get("copyCkddDetail").setEnabled(false);
    if (ckddId) {
        var url = jsUseCtxPath + "/Ckdd/getCkddDetail.do";
        $.post(
            url,
            {ckddId: ckddId},
            function (json) {
                formCkdd.setData(json);
            });
    }else {
        mini.get("res").setValue(currentUserId);
        mini.get("res").setText(currentUserName);
    }
    //变更入口
    if (action == 'task') {
        taskActionProcess();
    } else if (action == "detail") {
        formCkdd.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        mini.get("addTime").setEnabled(false);
        mini.get("removeTime").setEnabled(false);
        mini.get("addCkddDetail").setEnabled(false);
        mini.get("removeCkddDetail").setEnabled(false);
        $("#detailToolBar").show();
        if(status!='DRAFTED') {
            $("#processInfo").show();
        }
    }
});

function saveCkddInProcess() {
    // var formValid = validJsmm();
    // if (!formValid.result) {
    //     mini.alert(formValid.message);
    //     return;
    // }
    var formData = _GetFormJsonMini("formCkdd");
    formData.ckdd=detailListGrid.getChanges();
    formData.time=timeListGrid.getChanges();
    if (formData.SUB_ckFileListGrid) {
        delete formData.SUB_ckFileListGrid;
    }
    $.ajax({
        url: jsUseCtxPath + '/Ckdd/saveCkdd.do',
        type: 'post',
        async: false,
        data:mini.encode(formData),
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                var message="";
                if(data.success) {
                    window.location.reload();
                } else {
                    mini.alert(ckddEdit_sjbcsb + data.message);
                }
            }
        }
    });
}

function saveDetailInProcess() {
    // var formValid = validJsmm();
    // if (!formValid.result) {
    //     mini.alert(formValid.message);
    //     return;
    // }
    var formData = _GetFormJsonMini("formCkdd");
    formData.ckdd=detailListGrid.getChanges();
    formData.time=timeListGrid.getChanges();
    if (formData.SUB_ckFileListGrid) {
        delete formData.SUB_ckFileListGrid;
    }
    $.ajax({
        url: jsUseCtxPath + '/Ckdd/saveCkdd.do',
        type: 'post',
        async: false,
        data:mini.encode(formData),
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                var message="";
                if(data.success) {
                    detailListGrid.reload();
                } else {
                    mini.alert(ckddEdit_sjbcsb + data.message);
                }
            }
        }
    });
}

function getData() {
    var formData = _GetFormJsonMini("formCkdd");
    formData.ckdd=detailListGrid.getChanges();
    formData.time=timeListGrid.getChanges();
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    // formData.bos=[];
    // formData.vars=[{key:'companyName',val:formData.companyName}];
    return formData;
}

function saveCkdd(e) {
    var formValid = validZero();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.saveDraft(e);
}

function validZero() {
    var ckMonth = $.trim(mini.get("ckMonth").getValue());
    if (!ckMonth) {
        return {"result": false, "message": ckddEdit_qtxyf};
    }
    var model = $.trim(mini.get("model").getValue());
    if (!model) {
        return {"result": false, "message": ckddEdit_cpxh};
    }
    var country = $.trim(mini.get("country").getValue());
    if (!country) {
        return {"result": false, "message": ckddEdit_ckgj};
    }
    var res = $.trim(mini.get("res").getValue());
    if (!res) {
        return {"result": false, "message": ckddEdit_ddfzr};
    }
    var cpzg = $.trim(mini.get("cpzg").getValue());
    if (!cpzg) {
        return {"result": false, "message": ckddEdit_cpzg};
    }
    var dep = $.trim(mini.get("dep").getValue());
    if (!dep) {
        return {"result": false, "message": ckddEdit_zrbm};
    }
    var ddNumber = $.trim(mini.get("ddNumber").getValue());
    if (!ddNumber) {
        return {"result": false, "message": ckddEdit_ddh};
    }
    var need = $.trim(mini.get("need").getValue());
    if (!need) {
        return {"result": false, "message": ckddEdit_xqsl};
    }
    return {"result": true};
}

function startCkddProcess(e) {
    var formValid = validFirst();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.startProcess(e);
}

function ckddApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (first == 'yes') {
        var formValid = validFirst();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    //检查通过
    window.parent.approve();
}

function validFirst() {
    var ckMonth = $.trim(mini.get("ckMonth").getValue());
    if (!ckMonth) {
        return {"result": false, "message": ckddEdit_qtxyf};
    }
    var model = $.trim(mini.get("model").getValue());
    if (!model) {
        return {"result": false, "message": ckddEdit_cpxh};
    }
    var country = $.trim(mini.get("country").getValue());
    if (!country) {
        return {"result": false, "message": ckddEdit_ckgj};
    }
    var res = $.trim(mini.get("res").getValue());
    if (!res) {
        return {"result": false, "message": ckddEdit_ddfzr};
    }
    var cpzg = $.trim(mini.get("cpzg").getValue());
    if (!cpzg) {
        return {"result": false, "message": ckddEdit_cpzg};
    }
    var dep = $.trim(mini.get("dep").getValue());
    if (!dep) {
        return {"result": false, "message": ckddEdit_zrbm};
    }
    var ddNumber = $.trim(mini.get("ddNumber").getValue());
    if (!ddNumber) {
        return {"result": false, "message": ckddEdit_ddh};
    }
    var need = $.trim(mini.get("need").getValue());
    if (!need) {
        return {"result": false, "message": ckddEdit_xqsl};
    }
    var gridData = timeListGrid.getData();
    if(gridData.length<1) {
        return {"result": false, "message": ckddEdit_qtxsjjl};
    }
    var timeListDataCheck=false;
    for(var index=0;index<gridData.length;index++) {
        if(gridData[index].timeType=='要求交货日期' && gridData[index].timeSelect && gridData[index].total) {
            timeListDataCheck = true;
        }
    }
    if(!timeListDataCheck) {
        return {"result": false, "message": ckddEdit_qtxsjjl};
    }
    var detail = detailListGrid.getData();
    if(detail.length<1) {
        return {"result": false, "message": ckddEdit_cppz};
    }
    for(var index=0;index<detail.length;index++) {
        if (!detail[index].config) {
            return {"result": false, "message": ckddEdit_cppz};
        }
    }
    return {"result": true};
}

function ckddSecond() {
    if (second == 'yes') {
        var detail = detailListGrid.getData();
        for (var i = 0; i < detail.length; i++) {
            if (detail[i].risk == undefined||detail[i].risk == "") {
                mini.alert(ckddEdit_fxd);
                return;
            }
            if (detail[i].solveId == undefined||detail[i].solveId == "") {
                mini.alert(ckddEdit_zrr);
                return;
            }
        }
    }

    saveCkddInProcess();
    window.parent.approve();
}




function ckddThird() {
    //编制阶段的下一步需要校验表单必填字段
    if (third == 'yes') {
        var detail = detailListGrid.getData();
        for (var i = 0; i < detail.length; i++) {
            if ((detail[i].measures == undefined||detail[i].measures == "")&&detail[i].solveId ==currentUserId) {
                mini.alert(ckddEdit_kzcs);
                return;
            }
            if ((detail[i].finishTime == undefined||detail[i].finishTime == "")&&detail[i].solveId ==currentUserId) {
                mini.alert(ckddEdit_wcsj);
                return;
            }
        }
    }
    //检查通过
    window.parent.approve();
}




function ckddForth() {
    //编制阶段的下一步需要校验表单必填字段
    // if (third == 'yes') {
    //     var formValid = validThird();
    //     if (!formValid.result) {
    //         mini.alert(formValid.message);
    //         return;
    //     }
    // }
    //检查通过
    window.parent.approve();
}

function processInfo() {
    var instId = $("#instId").val();
    var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: ckddEdit_lcslt,
        width: 800,
        height: 600
    });
}

function fileuploadSm() {
    var ckddId = mini.get("ckddId").getValue();
    if (!ckddId) {
        mini.alert(ckddEdit_qxdjbc);
        return;
    }
    mini.open({
        title: ckddEdit_tjfj,
        url: jsUseCtxPath + "/Ckdd/openUploadWindow.do?ckddId=" + ckddId,
        width: 850,
        height: 550,
        showModal: true,
        allowResize: true,
        ondestroy: function () {
            if (ckFileListGrid) {
                ckFileListGrid.load();
            }
        }
    });
}


function taskActionProcess() {
    //获取上一环节的结果和处理人
    bpmPreTaskTipInForm();

    //获取环境变量
    if (!nodeVarsStr) {
        nodeVarsStr = "[]";
    }
    var nodeVars = $.parseJSON(nodeVarsStr);
    for (var i = 0; i < nodeVars.length; i++) {
        if (nodeVars[i].KEY_ == 'first') {
            first = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'second') {
            second = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'third') {
            third  = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'forth') {
            forth  = nodeVars[i].DEF_VAL_;
        }
    }
    formCkdd.setEnabled(false);
    mini.get("addFile").setEnabled(false);
    mini.get("addTime").setEnabled(false);
    mini.get("removeTime").setEnabled(false);
    mini.get("addCkddDetail").setEnabled(false);
    mini.get("removeCkddDetail").setEnabled(false);
    mini.get("copyCkddDetail").setEnabled(false);
    if (first == 'yes') {
        mini.get("ckMonth").setEnabled(true);
        mini.get("model").setEnabled(true);
        mini.get("country").setEnabled(true);
        mini.get("res").setEnabled(true);
        mini.get("cpzg").setEnabled(true);
        mini.get("dep").setEnabled(true);
        mini.get("ddNumber").setEnabled(true);
        mini.get("need").setEnabled(true);
        mini.get("addFile").setEnabled(true);
        mini.get("addTime").setEnabled(true);
        mini.get("removeTime").setEnabled(true);
        mini.get("addCkddDetail").setEnabled(true);
        mini.get("removeCkddDetail").setEnabled(true);
    }
    if (second == 'yes') {
        mini.get("copyCkddDetail").setEnabled(true);
        mini.get("removeCkddDetail").setEnabled(true);
    }
    if (forth == 'yes') {
        mini.get("addTime").setEnabled(true);
        mini.get("removeTime").setEnabled(true);
    }
}



function fjupload() {
    var ckddId = mini.get("ckddId").getValue();
    if (!ckddId) {
        mini.alert(ckddEdit_qxdjbc);
        return;
    }
    mini.open({
        title: ckddEdit_tjfj,
        url: jsUseCtxPath + "/Ckdd/openUploadWindow.do?ckddId=" + ckddId,
        width: 850,
        height: 550,
        showModal: true,
        allowResize: true,
        ondestroy: function () {
            if (ckFileListGrid) {
                ckFileListGrid.load();
            }
        }
    });
}

function returnCkddPreviewSpan(fileName, fileId, formId, coverContent) {
    var fileType = getFileType(fileName);
    var s = '';
    if (fileType == 'other') {
        s = '<span  title=' + ckddEdit_yl + ' style="color: silver" >' + ckddEdit_yl + '</span>';
    } else if (fileType == 'pdf') {
        var url = '/Ckdd/ckddPdfPreview';
        s = '<span  title=' + ckddEdit_yl + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + ckddEdit_yl + '</span>';
    } else if (fileType == 'office') {
        var url = '/Ckdd/ckddOfficePreview';
        s = '<span  title=' + ckddEdit_yl + ' style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + ckddEdit_yl + '</span>';
    } else if (fileType == 'pic') {
        var url = '/Ckdd/ckddImagePreview';
        s = '<span  title=' + ckddEdit_yl + ' style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + ckddEdit_yl + '</span>';
    }
    return s;
}

function downLoadCkddFile(fileName, fileId, formId) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + '/Ckdd/ckddPdfPreview.do?action=download');
    var inputFileName = $("<input>");
    inputFileName.attr("type", "hidden");
    inputFileName.attr("name", "fileName");
    inputFileName.attr("value", fileName);
    var inputstandardId = $("<input>");
    inputstandardId.attr("type", "hidden");
    inputstandardId.attr("name", "formId");
    inputstandardId.attr("value", formId);
    var inputFileId = $("<input>");
    inputFileId.attr("type", "hidden");
    inputFileId.attr("name", "fileId");
    inputFileId.attr("value", fileId);
    $("body").append(form);
    form.append(inputFileName);
    form.append(inputstandardId);
    form.append(inputFileId);
    form.submit();
    form.remove();
}


function deleteCkddFile(fileName,fileId,formId,urlValue) {
    mini.confirm(ckddEdit_qdsc, ckddEdit_qd,
        function (action) {
            if (action == "ok") {
                var url = jsUseCtxPath + urlValue;
                var data = {
                    fileName: fileName,
                    id: fileId,
                    formId: formId
                };
                $.ajax({
                    url:url,
                    method:'post',
                    contentType: 'application/json',
                    data:mini.encode(data),
                    success:function (json) {
                        if(ckFileListGrid) {
                            ckFileListGrid.load();
                        }
                    }
                });
            }
        }
    );
}
function addCkddDetail() {
    var formId = mini.get("ckddId").getValue();
    if (!formId) {
        mini.alert(ckddEdit_qxdjbcjx);
        return;
    } else {
        var row = {};
        detailListGrid.addRow(row);
    }
}

function removeCkddDetail() {
    var selected = detailListGrid.getSelected();
    if (!selected) {
        mini.alert(ckddEdit_zsytjl);
        return;
    }
    if (selected.CREATE_BY_ && selected.CREATE_BY_!=currentUserId) {
        mini.alert(ckddEdit_fbrtj);
        return;
    }
    detailListGrid.removeRow(selected);
    saveDetailInProcess();
}

function copyCkddDetail() {
    var selected = detailListGrid.getSelected();
    if (!selected) {
        mini.alert(ckddEdit_zsytjl);
        return;
    }
    var row = {};
    detailListGrid.updateRow(row, {config: selected.config});
    detailListGrid.addRow(row,detailListGrid.indexOf(selected)+1);
    saveDetailInProcess();
}

function removeCkddDetail1(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = detailListGrid.getSelecteds();
    }
    if (rows.length <= 0) {
        mini.alert(ckddEdit_zsytjl);
        return;
    }
    mini.confirm(ckddEdit_qdscxzjl, ckddEdit_ts, function (action) {
        if (action != 'ok') {
            return;
        } else {
            var rowIds = [];
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                rowIds.push(r.spId);
            }
            _SubmitJson({
                url: jsUseCtxPath + "/Ckdd/deleteDetail.do",
                method: 'POST',
                showMsg:false,
                data: {ids: rowIds.join(',')},
                success: function (data) {
                    if (data) {
                        mini.alert(data.message);
                        detailListGrid.reload();
                    }
                }
            });
        }
    });
}
function addTime() {
    var formId = mini.get("ckddId").getValue();
    if (!formId) {
        mini.alert(ckddEdit_qxdjbcjx);
        return;
    } else {
        var row = {};
        timeListGrid.addRow(row);
    }
}

function removeTime() {
    var selecteds = timeListGrid.getSelecteds();
    var deleteArr = [];
    for (var i = 0; i < selecteds.length; i++) {
        var row = selecteds[i];
        deleteArr.push(row);
    }
    timeListGrid.removeRows(deleteArr);
}

function removeTime1(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = timeListGrid.getSelecteds();
    }
    if (rows.length <= 0) {
        mini.alert(ckddEdit_zsytjl);
        return;
    }
    mini.confirm(ckddEdit_qdscxzjl, ckddEdit_ts, function (action) {
        if (action != 'ok') {
            return;
        } else {
            var rowIds = [];
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                rowIds.push(r.timeId);
            }
            _SubmitJson({
                url: jsUseCtxPath + "/Ckdd/deleteTime.do",
                method: 'POST',
                showMsg:false,
                data: {ids: rowIds.join(',')},
                success: function (data) {
                    if (data) {
                        mini.alert(data.message);
                        timeListGrid.reload();
                    }
                }
            });
        }
    });
}