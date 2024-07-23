var step = "";
var taskStatus = "";
$(function () {
    if (jxbzzbshId) {
        var url = jsUseCtxPath + "/serviceEngineering/core/jxbzzbsh/getJxbzzbshDetail.do?jxbzzbshId=" + jxbzzbshId;
        $.ajaxSettings.async = false;
        $.ajax({
            url: url,
            method: 'get',
            async: false,
            success: function (json) {
                jxbzzbshForm.setData(json);
                taskStatus = json.taskStatus;
            }
        });
        $.ajaxSettings.async = true;
    } else if (matObj.id) {
        mini.get("shipmentnotmadeId").setValue(matObj.id);
        mini.get("shipmentnotmadeId").setText(matObj.materialCode);
        mini.get("salesModel").setValue(matObj.salesModel);
        mini.get("productDepartmentId").setValue(matObj.departmentId);
        mini.get("productDepartmentId").setText(matObj.department);
        mini.get("versionType").setValue(matObj.versionType);
    } else if(changeType == "yes" && oldId){
        var url = jsUseCtxPath + "/serviceEngineering/core/jxbzzbsh/getOldJxbzzbshDetail.do";
        $.ajaxSettings.async = false;
        $.post(
            url,
            {jxbzzbshId: oldId},
            function (json) {
                jxbzzbshForm.setData(json);
                mini.get("oldId").setValue(oldId);
            });
        $.ajaxSettings.async = true;
    }
    //明细入口
    if (action == 'detail') {

        $("#detailToolBar").show();
        //非草稿放开流程信息查看按钮
        if (taskStatus && taskStatus != 'DRAFTED') {
            $("#processInfo").show();
        }
        if (wdzy == 'true' && !taskStatus) {
            jxbzzbshForm.setEnabled(false);
            mini.get("addFile").setEnabled(true);
            mini.get("shipmentnotmadeId").setEnabled(true);
            $("#saveOldData").show();
        } else if (wdzy == 'true' && taskStatus == 'SUCCESS_END') {
            jxbzzbshForm.setEnabled(false);
            mini.get("addFile").setEnabled(true);
        } else {
            jxbzzbshForm.setEnabled(false);
            mini.get("addFile").setEnabled(false);
        }
    } else if (action == 'task') {
        taskActionProcess();
    } else if (action == 'edit'&&(changeType != "yes"&&mini.get("oldId").getValue()=='')) {
        mini.get("productDepartmentId").setEnabled(false);
        mini.get("versionType").setEnabled(false);
        mini.get("salesModel").setEnabled(false);
        mini.get("gssNum").setEnabled(false);
        mini.get("testChange").setEnabled(false);
        mini.get("changeReason").setEnabled(false);
    }else if(action == 'edit'&&(changeType == "yes"||mini.get("oldId").getValue()!='')){
        jxbzzbshForm.setEnabled(false);
        mini.get("pinFour").setEnabled(true);
        mini.get("salesModel").setEnabled(true);
        mini.get("changeReason").setEnabled(true);
        mini.get("addFile").setEnabled(true);
    }else if(action=='upload'){
        jxbzzbshForm.setEnabled(false);
        mini.get("addFile").setEnabled(true);
    }
    if(changeType == "yes"||mini.get("oldId").getValue()!=''){
        $("#changeTr").show();
        $("#change").show();
        $("#nochange").hide();
    }
    if(mini.get("testChange").getValue()=='是'){
        $("#changeTr").show();
    }
    versionTypeChange();
});

function taskActionProcess() {
    //获取上一环节的结果和处理人
    bpmPreTaskTipInForm();

    //获取环境变量
    if (!nodeVarsStr) {
        nodeVarsStr = "[]";
    }
    var nodeVars = $.parseJSON(nodeVarsStr);
    for (var i = 0; i < nodeVars.length; i++) {
        if (nodeVars[i].KEY_ == 'step') {
            step = nodeVars[i].DEF_VAL_;
        }
    }
    if (step == 'bianZhi') {
        mini.get("addFile").setEnabled(true);
        mini.get("shipmentnotmadeId").setEnabled(false);
        mini.get("productDepartmentId").setEnabled(false);
        mini.get("versionType").setEnabled(false);
        mini.get("salesModel").setEnabled(false);
        mini.get("gssNum").setEnabled(false);
        if(changeType == "yes"){
            jxbzzbshForm.setEnabled(false);
            mini.get("salesModel").setEnabled(true);
            mini.get("addFile").setEnabled(true);
            mini.get("changeReason").setEnabled(true);
        }
    } else if (step == 'gd') {
        jxbzzbshForm.setEnabled(false);
        mini.get("addFile").setEnabled(true);
        mini.get("gssNum").setEnabled(true);
    } else if (step == 'cpzgtx') {
        // 产品主管填写可以上传
        jxbzzbshForm.setEnabled(false);
        mini.get("addFile").setEnabled(true);
        mini.get("testChange").setEnabled(true);
        mini.get("changeReason").setEnabled(true);
    } else if (step == 'bzzbjd') {
        jxbzzbshForm.setEnabled(false);
        mini.get("addFile").setEnabled(false);
        mini.get("pinFour").setEnabled(true);
    }else {
        jxbzzbshForm.setEnabled(false);
        mini.get("addFile").setEnabled(false);
    }
}

//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("jxbzzbshForm");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos = [];
    formData.vars = [{key: 'materialCode', val: formData.materialCode}];
    return formData;
}

//保存草稿
function saveJxbzzbsh(e) {
    var salesModel = $.trim(mini.get("salesModel").getValue());
    if (!salesModel) {
        mini.alert(jxbzzbshEdit_name21);
        return;
    }
    window.parent.saveDraft(e);
}

//启动流程
function startJxbzzbshProcess(e) {
    var bianZhiValid = validBianZhi();
    if (!bianZhiValid.result) {
        mini.alert(bianZhiValid.message);
        return;
    }
    mini.get("step").setValue("startProcess")
    window.parent.startProcess(e);
}

//检验表单是否必填(编制)
function validBianZhi() {
    var checkResult = {};
    // var shipmentnotmadeId = mini.get("shipmentnotmadeId").getValue()
    // if (!shipmentnotmadeId) {
    //     return {"result": false, "message": "请选择物料编码"};
    // }
    // var productDepartmentId = mini.get("productDepartmentId").getValue();
    // if (!productDepartmentId) {
    //     return {"result": false, "message": "请选择产品所"};
    // }
    // var salesModel = $.trim(mini.get("salesModel").getValue());
    // if (!salesModel) {
    //     return {"result": false, "message": "请输入销售型号"};
    // }
    var productType = mini.get("productType").getValue();
    if (!productType) {
        return {"result": false, "message": jxbzzbshEdit_name};
    }
    if(changeType == "yes"||mini.get("oldId").getValue()!=''){
        var changeReason = mini.get("changeReason").getValue();
        if (!changeReason) {
            return {"result": false, "message": jxbzzbshEdit_name1};
        }
    }
    // var versionType = mini.get("versionType").getValue()
    // if (!versionType) {
    //     return {"result": false, "message": "请选择版本型号"};
    // }
    if (fileListGrid.totalCount == 0) {
        return {"result": false, "message": jxbzzbshEdit_name2};
    }
        var isupload = false;
        var fileList = fileListGrid.getData();
        for (var i = 0; i < fileList.length; i++) {
            var versionType = mini.get("versionType").getValue();
            if(versionType=='csb'){
                var cnVersionType='测试版';
            }else if(versionType=='cgb'){
                var cnVersionType='常规版';
            }else if(versionType=='wzb'){
                var cnVersionType='完整版';
            }
            if (fileList[i].fileVersionType==cnVersionType) {
                isupload = true;
            }
        }
        if (!isupload) {
            return {"result": false, "message": "请上传对应版本类型附件"};
        }
    var valid = checkShipmentnotmadeIdUnique();
    if (!valid&&(changeType != "yes"&&mini.get("oldId").getValue()=='')) {
        return {"result": false, "message": jxbzzbshEdit_name3};
    }
    var pinFour = mini.get("pinFour").getValue();
    if (!pinFour) {
        return {"result": false, "message": "请填写适用第四位PIN码"};
    }
    checkResult.result = true;
    return checkResult;
}

function validJd() {
    var checkResult = {};
    var pinFour = mini.get("pinFour").getValue();
    if (!pinFour) {
        return {"result": false, "message": "请填写适用第四位PIN码"};
    }
    checkResult.result = true;
    return checkResult;
}

function validCpzg() {
    var checkResult = {};
    var testChange = mini.get("testChange").getValue();
    if (!testChange) {
        return {"result": false, "message": "请填写测试版是否变更"};
    }
    var changeReason = mini.get("changeReason").getValue();
    if(testChange=='是'&&!changeReason){
        return {"result": false, "message": "请填写变更原因"};
    }
    if(testChange=='是'){
        var isupload = false;
        var fileList = fileListGrid.getData();
        for (var i = 0; i < fileList.length; i++) {
            if (fileList[i].fileVersionType=="测试版修订") {
                isupload = true;
            }
        }
        if (!isupload) {
            return {"result": false, "message": "请上传修订后的测试版附件"};
        }
    }
    checkResult.result = true;
    return checkResult;
}

function validGd() {
    var checkResult = {};
    var gssNum = mini.get("gssNum").getValue();
    if (!gssNum) {
        return {"result": false, "message": "请填写手册编码"};
    }
    checkResult.result = true;
    return checkResult;
}

//流程中暂存信息（如编制阶段）
function saveJxbzzbshInProcess() {
    var bianZhiValid = validBianZhi();
    if (!bianZhiValid.result) {
        mini.alert(bianZhiValid.message);
        return;
    }
    var formData = _GetFormJsonMini("jxbzzbshForm");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    $.ajax({
        url: jsUseCtxPath + '/serviceEngineering/core/jxbzzbsh/saveJxbzzbsh.do',
        type: 'post',
        async: false,
        data: mini.encode(formData),
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                var message = "";
                if (data.success) {
                    message = jxbzzbshEdit_name4;
                } else {
                    message = jxbzzbshEdit_name5 + data.message;
                }

                mini.alert(message, jxbzzbshEdit_name6, function () {
                    window.location.reload();
                });
            }
        }
    });
}

//流程中的审批或者下一步
function jxbzzbshApprove() {
    //编制阶段的下一步需要校验表单必填字段
    var valid = true;
    if (step == 'bianZhi') {
        valid = validBianZhi();
        if (!valid.result) {
            mini.alert(valid.message);
            return;
        }
    } else if (step == 'cpzgtx') {
        valid = validCpzg();
        if (!valid.result) {
            mini.alert(valid.message);
            return;
        }
        if (fileListGrid.totalCount == 0) {
            mini.alert(jxbzzbshEdit_name2);
            return;
        }
    }  else if (step == 'bzzbjd') {
        valid = validJd();
        if (!valid.result) {
            mini.alert(valid.message);
            return;
        }
        if (fileListGrid.totalCount == 0) {
            mini.alert(jxbzzbshEdit_name2);
            return;
        }
    }else if (step == 'gd') {
        valid = validGd();
        if (!valid.result) {
            mini.alert(valid.message);
            return;
        }
    }
    //检查通过
    window.parent.approve();
}

function jxbzzbshProcessInfo() {
    var instId = $("#instId").val();
    var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: jxbzzbshEdit_name7,
        width: 800,
        height: 600
    });
}


function addJxbzzbshFile() {
    var id = mini.get("id").getValue();
    if (!id) {
        mini.alert(jxbzzbshEdit_name8);
        return;
    }
    var productType = mini.get("productType").getValue();
    var versionType = mini.get("versionType").getValue();
    var salesModel = $.trim(mini.get("salesModel").getValue());
    if (!productType) {
        mini.alert(jxbzzbshEdit_name);
        return;
    }
    if (!versionType) {
        mini.alert(jxbzzbshEdit_name9);
        return;
    }
    if (!salesModel) {
        mini.alert(jxbzzbshEdit_name10);
        return;
    }
    var applicationNumber = mini.get("applicationNumber").getValue();
    if (!applicationNumber) {
        mini.alert(jxbzzbshEdit_name11);
        return;
    }
    mini.open({
        title: jxbzzbshEdit_name12,
        url: jsUseCtxPath + "/serviceEngineering/core/jxbzzbsh/openJxbzzbshUploadWindow.do?masterId=" + id +
        "&productType=" + productType + "&versionType=" + versionType +
        "&applicationNumber=" + applicationNumber + "&salesModel=" + salesModel,
        width: 800,
        height: 350,
        showModal: false,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            fileListGrid.load();
        }
    });
}

function returnJxbzzbshPreviewSpan(fileName, fileId, formId, coverContent) {
    var fileType = getFileType(fileName);
    var downloadUrl = "/serviceEngineering/core/jxbzzbsh/jxbzzbshDownload.do";
    var s = '';
    if (fileType == 'other') {
        s = '<span  title=' + jxbzzbshEdit_name13 + ' style="color: silver" >' + jxbzzbshEdit_name13 + '</span>';
    } else if (fileType == 'pdf') {
        var url = '/serviceEngineering/core/jxbzzbsh/jxbzzbshDownload.do';
        s = '<span  title=' + jxbzzbshEdit_name13 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + downloadUrl + '\')">' + jxbzzbshEdit_name13 + '</span>';
    } else if (fileType == 'office') {
        s = '<span  title=' + jxbzzbshEdit_name13 + ' style="color: silver" >' + jxbzzbshEdit_name13 + '</span>';
    } else if (fileType == 'pic') {
        var url = '/serviceEngineering/core/jxbzzbsh/jxbzzbshImagePreview.do';
        s = '<span  title=' + jxbzzbshEdit_name13 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + jxbzzbshEdit_name13 + '</span>';
    }
    if (action == 'upload' ||action == 'edit' || action == 'task' || (action == 'detail' && isFWGCS == true)) {
        s += '&nbsp;&nbsp;&nbsp;<span title=' + jxbzzbshEdit_name14 + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + downloadUrl + '\')">' + jxbzzbshEdit_name14 + '</span>';
    }
    return s;
}

function operationRenderer(e) {
    var record = e.record;
    var cellHtml = '';
    cellHtml = returnJxbzzbshPreviewSpan(record.fileName, record.id, record.masterId, coverContent);
    //编辑、产品主管填写、标准值表审核、编制、文档专员通过明细进来可以删除
    if ((action == 'upload' ||action == 'edit' || (action == 'task' && (step == 'bianZhi' || step == 'cpzgtx' || step == 'bzzbsh')) ||
        (action == 'detail' && wdzy == 'true'))&&record.masterId==jxbzzbshId
    ) {
        var deleteUrl = "/serviceEngineering/core/jxcshc/delUploadFile.do";
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + jxbzzbshEdit_name15 + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.masterId + '\',\'' + deleteUrl + '\')">' + jxbzzbshEdit_name15 + '</span>';
    }
    return cellHtml;
}

//下载模板
function downloadJxbzzbshTemplate() {
    var productType = mini.get("productType").getValue();
    var versionType = mini.get("versionType").getValue();
    if (!productType) {
        mini.alert(jxbzzbshEdit_name);
        return;
    }
    if (!versionType) {
        mini.alert(jxbzzbshEdit_name9);
        return;
    }
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/serviceEngineering/core/jxbzzbsh/downloadJxbzzbshTemplate.do?productType=" + productType + "&versionType=" + versionType);
    $("body").append(form);
    form.submit();
    form.remove();
}

function onButtonEdit(e) {
    var btnEdit = e.sender;
    mini.open({
        url: jsUseCtxPath + "/serviceEngineering/core/jxbzzbsh/openDialog.do",
        title: jxbzzbshEdit_name16,
        width: 900,
        height: 500,
        ondestroy: function (action) {
            if (action == "ok") {
                var iframe = this.getIFrameEl();
                var data = iframe.contentWindow.getMaterialCode();
                data = mini.clone(data);
                if (data) {
                    btnEdit.setValue(data.id);
                    btnEdit.setText(data.materialCode);
                    mini.get("salesModel").setValue(data.salesModel);
                    mini.get("productDepartmentId").setValue(data.departmentId);
                    mini.get("productDepartmentId").setText(data.department);
                    mini.get("versionType").setValue(data.versionType);
                    mini.get("pinFour").setValue(data.pinFour);
                    if ( data.versionType == 'wzb') {
                        $("#testChangeTr").show();
                        var fileListGridUrl = jsUseCtxPath + "/serviceEngineering/core/jxbzzbsh/queryTestFileList.do?materialCode=" + data.materialCode;
                        fileListGrid.setUrl(fileListGridUrl);
                        fileListGrid.load();
                    } else {
                        $("#testChangeTr").hide();
                    }
                }
            }
        }
    });
}

//校验是否重复
function checkShipmentnotmadeIdUnique() {
    var valid = true;
    var postData = {};
    postData.id = mini.get("id").getValue();
    postData.shipmentnotmadeId = mini.get("shipmentnotmadeId").getValue();
    $.ajax({
        url: jsUseCtxPath + '/serviceEngineering/core/jxbzzbsh/checkShipmentnotmadeIdUnique.do',
        type: 'post',
        async: false,
        data: mini.encode(postData),
        contentType: 'application/json',
        success: function (data) {
            if (!data.success) {
                valid = false;
            }
        }
    });
    return valid
}
//作废
function discardJxbzzbshInst() {
    if (!confirm(jxbzzbshEdit_name17)) {
        return;
    }

    var postData = {};
    postData.shipmentnotmadeId = mini.get("shipmentnotmadeId").getValue();
    var instId = mini.get("instId").getValue();
    _SubmitJson({
        url: __rootPath + '/bpm/core/bpmInst/discardInst.do',
        data: {
            instId: instId
        },
        method: 'POST',
        success: function () {
            $.ajax({
                url: jsUseCtxPath + '/serviceEngineering/core/jxbzzbsh/discardJxbzzbshInst.do',
                type: 'post',
                async: false,
                data: mini.encode(postData),
                contentType: 'application/json',
                success: function (data) {
                    if (data.success) {
                        mini.alert(jxbzzbshEdit_name18, jxbzzbshEdit_name19, function () {
                            window.parent.CloseWindow();
                        })
                    }
                }
            });
        }
    })

}

function saveOldData() {
    var shipmentnotmadeId = mini.get("shipmentnotmadeId").getValue()
    if (!shipmentnotmadeId) {
        mini.alert(jxbzzbshEdit_name20);
        return;
    }

    var applicationNumber = mini.get("applicationNumber").getValue();
    if (applicationNumber && fileListGrid.totalCount == 0) {
        mini.alert(jxbzzbshEdit_name2);
        return;
    }
    var formData = _GetFormJsonMini("jxbzzbshForm");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    $.ajax({
        url: jsUseCtxPath + '/serviceEngineering/core/jxbzzbsh/saveOldData.do',
        type: 'post',
        async: false,
        data: mini.encode(formData),
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                var message = "";
                if (data.success) {
                    message = jxbzzbshEdit_name4;
                } else {
                    message = jxbzzbshEdit_name5 + data.message;
                }

                mini.alert(message, jxbzzbshEdit_name6, function () {
                    window.location.reload();
                });
            }
        }
    });
}
