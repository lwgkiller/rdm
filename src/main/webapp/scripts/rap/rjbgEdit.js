var isCPZG = "";
var isDLFZR = "";
var isRJSCR = "";
var isGYFZR = "";
var isXFTZ = "";
var isZZBZY = "";
var isFWBZY = "";
var isZPCZY = "";
var isSCSX = "";
var isFWBSC = "";
var isZXCH = "";
var isSZZY = "";
var isBBH = "";
$(function () {
    if (rjbgId) {
        var url = jsUseCtxPath + "/environment/core/Rjbg/getRjbgDetail.do";
        $.post(
            url,
            {rjbgId: rjbgId},
            function (json) {
                formRjbg.setData(json);
            });
    }else {
        mini.get("applyName").setValue(currentUserId);
        mini.get("applyName").setText(currentUserName);
        mini.get("appDeptName").setValue(currentUserMainDepId);
        mini.get("appDeptName").setText(deptName);
    }
    formRjbg.setEnabled(false);
    mini.get("addSmFile").setEnabled(false);
    mini.get("addCnFile").setEnabled(false);
    mini.get("addScFile").setEnabled(false);
    mini.get("addScfFile").setEnabled(false);
    mini.get("addCnfFile").setEnabled(false);
    mini.get("addBbFile").setEnabled(false);
    mini.get("importScId").setEnabled(false);
    mini.get("importCnId").setEnabled(false);
    mini.get("editMsgSc").setEnabled(false);
    mini.get("editMsgCn").setEnabled(false);
    mini.get("downloadRj").setEnabled(false);
    mini.get("tzdCheck").setEnabled(true);
    mini.get("noticeNo").setEnabled(true);
    mini.get("reason").setEnabled(true);
    mini.get("wjModel").setEnabled(true);
    mini.get("dlName").setEnabled(true);
    mini.get("rjrName").setEnabled(true);
    //变更入口
    if (action == 'task') {
        taskActionProcess();
    } else if (action == "detail") {
        formRjbg.setEnabled(false);
        mini.get("addSmFile").setEnabled(false);
        $("#detailToolBar").show();
        if(status!='DRAFTED') {
            $("#processInfo").show();
        }
    }
});

function getData() {
    var formData = _GetFormJsonMini("formRjbg");
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    // formData.bos=[];
    // formData.vars=[{key:'companyName',val:formData.companyName}];
    return formData;
}

function saveRjbg(e) {
    var formValid = validCpzgRjbg();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.saveDraft(e);
}

function saveChange(rjbgId) {
    var formData = _GetFormJsonMini("formRjbg");
    $.ajax({
        url: jsUseCtxPath + '/environment/core/Rjbg/saveRjbg.do?rjbgId=' + rjbgId,
        type: 'post',
        async: false,
        data: mini.encode(formData),
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                var message = "";
                if (data.success) {
                    message = "数据变更成功";
                } else {
                    message = "数据变更失败" + data.message;
                }

                mini.showMessageBox({
                    title: "提示信息",
                    iconCls: "mini-messagebox-info",
                    buttons: ["ok"],
                    message: message,
                    callback: function (action) {
                        if (action == "ok") {
                            CloseWindow("ok");
                        }
                    }
                });
            }
        }
    });
}


function startRjbgProcess(e) {
    var formValid = validCpzgRjbg();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.startProcess(e);
}

function validCpzgRjbg() {
    var apply = $.trim(mini.get("applyName").getValue());
    if (!apply) {
        return {"result": false, "message": "请填写申请人"};
    }
    var appDept = $.trim(mini.get("appDeptName").getValue());
    if (!appDept) {
        return {"result": false, "message": "请填写申请部门"};
    }
    var reason = $.trim(mini.get("reason").getValue());
    if (!reason) {
        return {"result": false, "message": "请填写申请原因"};
    }
    var wjModel = $.trim(mini.get("wjModel").getValue());
    if (!wjModel) {
        return {"result": false, "message": "请填写销售型号"};
    }
    var dlName = $.trim(mini.get("dlName").getValue());
    if (!dlName) {
        return {"result": false, "message": "请选择会签相关负责人"};
    }
    var rjrName = $.trim(mini.get("rjrName").getValue());
    if (!rjrName) {
        return {"result": false, "message": "请选择动力工程师"};
    }
    var tzdCheck = $.trim(mini.get("tzdCheck").getValue());
    if(tzdCheck=="true"){
        var noticeNo = $.trim(mini.get("noticeNo").getValue());
        if (!noticeNo) {
            return {"result": false, "message": "请填写通知单号"};
        }
    }
    return {"result": true};
}

function rjbgCpzgApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (isCPZG == 'yes') {
        var formValid = validCpzgRjbg();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    //检查通过
    window.parent.approve();
}


function validRjscrRjbg() {
    var rjResId = $.trim(mini.get("rjResId").getValue());
    if (!rjResId) {
        return {"result": false, "message": "请选择软件版本"};
    }
    var gyName = $.trim(mini.get("gyName").getValue());
    if (!gyName) {
        return {"result": false, "message": "请选择工艺负责人"};
    }
    var zlName = $.trim(mini.get("zlName").getValue());
    if (!zlName) {
        return {"result": false, "message": "请选择质量部专员"};
    }
    var cnfFileListGrid = $.trim(mini.get("cnfFileListGrid").getData());
    if(cnfFileListGrid){
        var zzName = $.trim(mini.get("zzName").getValue());
        if (!zzName) {
            return {"result": false, "message": "请选择制造管理部专员"};
        }
    }
    var scfFileListGrid = $.trim(mini.get("scfFileListGrid").getData());
    if(scfFileListGrid){
        var fwName = $.trim(mini.get("fwName").getValue());
        if (!fwName) {
            return {"result": false, "message": "请选择服务部专员"};
        }
    }
    if (!cnfFileListGrid&&!scfFileListGrid) {
        return {"result": false, "message": "请上传发动机编号明细表"};
    }
    return {"result": true};
}

function rjbgRjscrApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (isRJSCR == 'yes') {
        var formValid = validRjscrRjbg();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    //检查通过
    window.parent.approve();
}

function validZxchRjbg() {
    var scfFileListGrid = $.trim(mini.get("scfFileListGrid").getData());
    if(scfFileListGrid){
        var scFileListGrid = $.trim(mini.get("scFileListGrid").getData());
        if (!scFileListGrid) {
            return {"result": false, "message": "请上传市场车号明细表"};
        }
    }
    var cnfFileListGrid = $.trim(mini.get("cnfFileListGrid").getData());
    if(cnfFileListGrid){
        var cnFileListGrid = $.trim(mini.get("cnFileListGrid").getData());
        if (!cnFileListGrid) {
            return {"result": false, "message": "请上传厂内车号明细表"};
        }
    }
    if(scfFileListGrid){
        var scFileListGrid = $.trim(mini.get("scFileListGrid").getData());
        if (!scFileListGrid) {
            return {"result": false, "message": "请上传市场车号明细表"};
        }
    }
    return {"result": true};
}

function rjbgZxchApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (isZXCH == 'yes') {
        var formValid = validZxchRjbg();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    //检查通过
    window.parent.approve();
}


function validGyfzrRjbg() {
    var smFileListGrid = $.trim(mini.get("smFileListGrid").getData());
    if (!smFileListGrid) {
        return {"result": false, "message": "请添加软件变更工艺指导书附件"};
    }
    return {"result": true};
}

function rjbgGyfzrApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (isGYFZR == 'yes') {
        var formValid = validGyfzrRjbg();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    //检查通过
    window.parent.approve();
}




function validZzbxfRjbg() {
    var zpName = $.trim(mini.get("zpName").getValue());
    if (!zpName) {
        return {"result": false, "message": "请选择装配厂专员"};
    }
    return {"result": true};
}

function rjbgZzbApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (isZZBZY == 'yes') {
        var formValid = validZzbxfRjbg();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    //检查通过
    window.parent.approve();
}

function validZpcRjbg() {
    var cnListGrid = $.trim(mini.get("cnListGrid").getData());
    if (!cnListGrid) {
        return {"result": false, "message": "请填写刷写记录"};
    }
    return {"result": true};
}

function rjbgZpcApprove() {
    //编制阶段的下一步需要校验表单必填字段
    debugger
    if (isZPCZY == 'yes') {
        var formValid = validZpcRjbg();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    //检查通过
    window.parent.approve();
}

function validFwbRjbg() {
    var scListGrid = $.trim(mini.get("scListGrid").getData());
    if (!scListGrid) {
        return {"result": false, "message": "请填写刷写记录"};
    }
    return {"result": true};
}

function rjbgFwbApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (isFWBSC == 'yes') {
        var formValid = validFwbRjbg();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    //检查通过
    window.parent.approve();
}

function validXftzRjbg() {
    var zpName = $.trim(mini.get("zpName").getValue());
    if (!zpName) {
        return {"result": false, "message": "请选择装配厂专员"};
    }
    return {"result": true};
}

function validScsxRjbg() {
    var scListGrid = $.trim(mini.get("scListGrid").getData());
    var cnListGrid = $.trim(mini.get("cnListGrid").getData());
    if (!scListGrid&&!cnListGrid) {
        return {"result": false, "message": "请填写刷写记录"};
    }
    return {"result": true};
}

function rjbgScsxApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (isSCSX == 'yes') {
        var formValid = validScsxRjbg();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    //检查通过
    window.parent.approve();
}

function rjbgXftzApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (isXFTZ == 'yes' && !isFwb) {
        var formValid = validXftzRjbg();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    //检查通过
    window.parent.approve();
}

function validBbhRjbg() {
    var bbh = $.trim(mini.get("bbh").getValue());
    if (!bbh) {
        return {"result": false, "message": "请填写正式版本号"};
    }
    var bbFileListGrid = $.trim(mini.get("bbFileListGrid").getData());
    if (!bbFileListGrid) {
        return {"result": false, "message": "请添加标定信息附件"};
    }
    return {"result": true};
}

function rjbgBbApprove() {
    //编制阶段的下一步需要校验表单必填字段
    if (isBBH == 'yes') {
        var formValid = validBbhRjbg();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    //检查通过
    window.parent.approve();
}

function processInfo() {
    var instId = $("#instId").val();
    var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: "流程图实例",
        width: 800,
        height: 600
    });
}

function fileuploadSm() {
    var rjbgId = mini.get("rjbgId").getValue();
    if (!rjbgId) {
        mini.alert('请先点击‘保存草稿’进行表单的保存！');
        return;
    }
    mini.open({
        title: "添加附件",
        url: jsUseCtxPath + "/environment/core/Rjbg/openUploadWindow.do?fileType=sm&rjbgId=" + rjbgId,
        width: 850,
        height: 550,
        showModal: true,
        allowResize: true,
        ondestroy: function () {
            if (smFileListGrid) {
                smFileListGrid.load();
            }
        }
    });
}

function fileuploadBb() {
    var rjbgId = mini.get("rjbgId").getValue();
    if (!rjbgId) {
        mini.alert('请先点击‘保存草稿’进行表单的保存！');
        return;
    }
    mini.open({
        title: "添加附件",
        url: jsUseCtxPath + "/environment/core/Rjbg/openUploadWindow.do?fileType=bb&rjbgId=" + rjbgId,
        width: 850,
        height: 550,
        showModal: true,
        allowResize: true,
        ondestroy: function () {
            if (bbFileListGrid) {
                bbFileListGrid.load();
            }
        }
    });
}

function fileuploadCn() {
    var rjbgId = mini.get("rjbgId").getValue();
    if (!rjbgId) {
        mini.alert('请先点击‘保存草稿’进行表单的保存！');
        return;
    }
    mini.open({
        title: "添加附件",
        url: jsUseCtxPath + "/environment/core/Rjbg/openUploadWindow.do?fileType=cn&rjbgId=" + rjbgId,
        width: 850,
        height: 550,
        showModal: true,
        allowResize: true,
        ondestroy: function () {
            if (cnFileListGrid) {
                cnFileListGrid.load();
            }
        }
    });
}

function fileuploadSc() {
    var rjbgId = mini.get("rjbgId").getValue();
    if (!rjbgId) {
        mini.alert('请先点击‘保存草稿’进行表单的保存！');
        return;
    }
    mini.open({
        title: "添加附件",
        url: jsUseCtxPath + "/environment/core/Rjbg/openUploadWindow.do?fileType=sc&rjbgId=" + rjbgId,
        width: 850,
        height: 550,
        showModal: true,
        allowResize: true,
        ondestroy: function () {
            if (scFileListGrid) {
                scFileListGrid.load();
            }
        }
    });
}

function fileuploadScf() {
    var rjbgId = mini.get("rjbgId").getValue();
    if (!rjbgId) {
        mini.alert('请先点击‘保存草稿’进行表单的保存！');
        return;
    }
    mini.open({
        title: "添加附件",
        url: jsUseCtxPath + "/environment/core/Rjbg/openUploadWindow.do?fileType=scf&rjbgId=" + rjbgId,
        width: 850,
        height: 550,
        showModal: true,
        allowResize: true,
        ondestroy: function () {
            if (scfFileListGrid) {
                scfFileListGrid.load();
            }
        }
    });
}

function fileuploadSoft() {
    var rjbgId = mini.get("rjbgId").getValue();
    if (!rjbgId) {
        mini.alert('请先点击‘保存草稿’进行表单的保存！');
        return;
    }
    mini.open({
        title: "添加附件",
        url: jsUseCtxPath + "/environment/core/Rjbg/openUploadWindow.do?fileType=cnf&rjbgId=" + rjbgId,
        width: 850,
        height: 550,
        showModal: true,
        allowResize: true,
        ondestroy: function () {
            if (cnfFileListGrid) {
                cnfFileListGrid.load();
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
        if (nodeVars[i].KEY_ == 'isCPZG') {
            isCPZG = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isDLFZR') {
            isDLFZR = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isRJSCR') {
            isRJSCR = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isGYFZR') {
            isGYFZR = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isXFTZ') {
            isXFTZ = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isFWBZY') {
            isFWBZY = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isZZBZY') {
            isZZBZY = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isZPCZY') {
            isZPCZY = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isSCSX') {
            isSCSX = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isFWBSC') {
            isFWBSC = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isBBH') {
            isBBH = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isZXCH') {
            isZXCH = nodeVars[i].DEF_VAL_;
        }
    }
    formRjbg.setEnabled(false);
    mini.get("addSmFile").setEnabled(false);
    mini.get("importScId").setEnabled(false);
    mini.get("importCnId").setEnabled(false);
    mini.get("editMsgSc").setEnabled(false);
    mini.get("editMsgCn").setEnabled(false);
    mini.get("downloadRj").setEnabled(false);
    if (isCPZG == 'yes') {

        mini.get("reason").setEnabled(true);
        mini.get("wjModel").setEnabled(true);
        mini.get("dlName").setEnabled(true);
        mini.get("rjrName").setEnabled(true);
        mini.get("tzdCheck").setEnabled(true);
        mini.get("noticeNo").setEnabled(true);
    }

    if (isRJSCR == 'yes') {
        mini.get("rjResId").setEnabled(true);
        mini.get("addCnfFile").setEnabled(true);
        mini.get("addScfFile").setEnabled(true);
        mini.get("gyName").setEnabled(true);
        mini.get("zlName").setEnabled(true);
        mini.get("zzName").setEnabled(true);
        mini.get("fwName").setEnabled(true);
    }
    if (isGYFZR == 'yes') {
        mini.get("addSmFile").setEnabled(true);
    }
    if (isXFTZ == 'yes' && !isFwb) {
        mini.get("zpName").setEnabled(true);
    }
    if (isFWBSC == 'yes') {
        mini.get("importScId").setEnabled(true);
        mini.get("editMsgSc").setEnabled(true);
        mini.get("downloadRj").setEnabled(true);
    }
    if (isZZBZY == 'yes') {
        mini.get("zpName").setEnabled(true);
    }
    if (isZPCZY == 'yes') {
        mini.get("importCnId").setEnabled(true);
        mini.get("editMsgCn").setEnabled(true);
        mini.get("downloadRj").setEnabled(true);
    }
    if (isSCSX == 'yes' && !isFwb) {
        mini.get("importCnId").setEnabled(true);
        mini.get("editMsgCn").setEnabled(true);
        mini.get("downloadRj").setEnabled(true);
    }else if(isSCSX == 'yes' && isFwb) {
        mini.get("importScId").setEnabled(true);
        mini.get("editMsgSc").setEnabled(true);
        mini.get("downloadRj").setEnabled(true);
    }
    if (isBBH == 'yes') {
        mini.get("bbh").setEnabled(true);
        mini.get("addBbFile").setEnabled(true);
    }
    if (isZXCH == 'yes'&& isZzb) {
        mini.get("addCnFile").setEnabled(true);
    }else if (isZXCH == 'yes'&& !isZzb) {
        mini.get("addScFile").setEnabled(true);
    }
}



function returnRjbgPreviewSpan(fileName, fileId, formId, coverContent) {
    var fileType = getFileType(fileName);
    var s = '';
    if (fileType == 'other') {
        s = '<span  title="预览" style="color: silver" >预览</span>';
    } else if (fileType == 'pdf') {
        var url = '/environment/core/Rjbg/rjbgPdfPreview';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
    } else if (fileType == 'office') {
        var url = '/environment/core/Rjbg/rjbgOfficePreview';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
    } else if (fileType == 'pic') {
        var url = '/environment/core/Rjbg/rjbgImagePreview';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
    }
    return s;
}

function downLoadRjbgFile(fileName, fileId, formId) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + '/environment/core/Rjbg/rjbgPdfPreview.do?action=download');
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

function onSelectRjResCloseClick() {
    mini.get("rjResId").setValue('');
    mini.get("rjResId").setText('');
}

/**
 * 选择控制程序
 */
function selectRjRes() {
    selectRjResWindow.show();
    searchRjRes();
}

//查询
function searchRjRes() {
    var queryParam = [];
    //其他筛选条件
    var rjName = $.trim(mini.get("rjName").getValue());
    if (rjName) {
        queryParam.push({name: "rjName", value: rjName});
    }
    var rjNo = $.trim(mini.get("rjNo").getValue());
    if (rjNo) {
        queryParam.push({name: "rjNo", value: rjNo});
    }
    var data = {};
    data.filter = mini.encode(queryParam);
    data.pageIndex = rjResListGrid.getPageIndex();
    data.pageSize = rjResListGrid.getPageSize();
    data.sortField = rjResListGrid.getSortField();
    data.sortOrder = rjResListGrid.getSortOrder();
    //查询
    rjResListGrid.load(data);
}

function onRowDblClick() {
    selectRjResOK();
}

function selectRjResOK() {
    var selectRow = rjResListGrid.getSelected();
    if (selectRow) {
        mini.get("rjResId").setValue(selectRow.rjId);
        mini.get("rjResId").setText(selectRow.rjName);
    }
    selectRjResHide();
}

function selectRjResHide() {
    selectRjResWindow.hide();
    mini.get("rjName").setValue('');
    mini.get("rjNo").setValue('');
}
function deleteSmFile(fileName,fileId,formId,urlValue) {
    mini.confirm("确定删除？", "确定？",
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
                        if(smFileListGrid) {
                            smFileListGrid.load();
                        }
                        if(cnfFileListGrid) {
                            cnfFileListGrid.load();
                        }
                        if(scfFileListGrid) {
                            scfFileListGrid.load();
                        }
                        if(bbFileListGrid) {
                            bbFileListGrid.load();
                        }
                        if(cnFileListGrid) {
                            cnFileListGrid.load();
                        }
                        if(scFileListGrid) {
                            scFileListGrid.load();
                        }
                    }
                });
            }
        }
    );
}
function openImportWindowCn() {
    importWindowCn.show();
}
function closeImportWindowCn() {
    importWindowCn.hide();
    clearUploadFileCn();
    cnListGrid.reload();
}

//导入模板下载
function downImport() {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/environment/core/Rjbg/importTemplateDownload.do");
    $("body").append(form);
    form.submit();
    form.remove();
}

//触发文件选择
function uploadFileCn() {
    $("#inputFileCn").click();
}

//文件类型判断及文件名显示
function    getSelectFileCn() {
    var fileList = $("#inputFileCn")[0].files;
    if (fileList && fileList.length > 0) {
        var fileNameSuffix = fileList[0].name.split('.').pop();
        if (fileNameSuffix == 'xls'||fileNameSuffix == 'xlsx') {
            mini.get("fileNameCn").setValue(fileList[0].name);
        }
        else {
            clearUploadFile();
            mini.alert('请上传excel文件！');
        }
    }
}

//清空文件
function clearUploadFileCn() {
    $("#inputFileCn").val('');
    mini.get("fileNameCn").setValue('');
}

//上传批量导入
function importProductCn() {
    var rjbgId = mini.get("rjbgId").getValue();
    var file = null;
    var fileList = $("#inputFileCn")[0].files;
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
        xhr.open('POST', jsUseCtxPath + '/environment/core/Rjbg/importExcelCn.do?rjbgId='+ rjbgId, false);
        xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        var fd = new FormData();
        fd.append('importFile', file);
        xhr.send(fd);
    }
}
function openImportWindowSc() {
    importWindowSc.show();
}
function closeImportWindowSc() {
    importWindowSc.hide();
    clearUploadFileSc();
    scListGrid.reload();
}
function uploadFileSc() {
    $("#inputFileSc").click();
}

//文件类型判断及文件名显示
function    getSelectFileSc() {
    var fileList = $("#inputFileSc")[0].files;
    if (fileList && fileList.length > 0) {
        var fileNameSuffix = fileList[0].name.split('.').pop();
        if (fileNameSuffix == 'xls'||fileNameSuffix == 'xlsx') {
            mini.get("fileNameSc").setValue(fileList[0].name);
        }
        else {
            clearUploadFile();
            mini.alert('请上传excel文件！');
        }
    }
}

//清空文件
function clearUploadFileSc() {
    $("#inputFileSc").val('');
    mini.get("fileNameSc").setValue('');
}

//上传批量导入
function importProductSc() {
    var rjbgId = mini.get("rjbgId").getValue();
    var file = null;
    var fileList = $("#inputFileSc")[0].files;
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
        xhr.open('POST', jsUseCtxPath + '/environment/core/Rjbg/importExcelSc.do?rjbgId='+ rjbgId, false);
        xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        var fd = new FormData();
        fd.append('importFile', file);
        xhr.send(fd);
    }
}
function addNewCn(cnsxId) {
    if(!cnsxId) {
        cnsxId='';
    }
    var rjbgId = mini.get("rjbgId").getValue();
    mini.open({
        title: "新增厂内刷写记录",
        url: jsUseCtxPath + "/environment/core/Rjbg/editCn.do?rjbgId=" + rjbgId+"&cnsxId="+cnsxId,
        width: 1050,
        height: 400,
        allowResize: true,
        onload: function () {
            cnListGrid.reload();
        },
        ondestroy:function () {
            cnListGrid.reload();
        }
    });
}
function cnsxDetail(cnsxId) {
    var action = "detail";
    mini.open({
        title: "新增厂内刷写记录",
        url: jsUseCtxPath + "/environment/core/Rjbg/editCn.do?action=" + action + "&cnsxId=" + cnsxId,
        width: 1050,
        height: 400,
        allowResize: true,
        onload: function () {
            cnListGrid.reload();
        },
        ondestroy:function () {
            cnListGrid.reload();
        }
    });
}
function removeRjbgCn(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = cnListGrid.getSelecteds();
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
                rowIds.push(r.cnsxId);
            }
            _SubmitJson({
                url: jsUseCtxPath + "/environment/core/Rjbg/deleteRjbgCn.do",
                method: 'POST',
                showMsg:false,
                data: {ids: rowIds.join(',')},
                success: function (data) {
                    if (data) {
                        mini.alert(data.message);
                        cnListGrid.reload();
                    }
                }
            });
        }
    });
}
function addNewSc(scsxId) {
    if(!scsxId) {
        scsxId='';
    }
    var rjbgId = mini.get("rjbgId").getValue();
    mini.open({
        title: "新增市场刷写记录",
        url: jsUseCtxPath + "/environment/core/Rjbg/editSc.do?rjbgId=" + rjbgId+"&scsxId="+scsxId,
        width: 1050,
        height: 400,
        allowResize: true,
        onload: function () {
            scListGrid.reload();
        },
        ondestroy:function () {
            scListGrid.reload();
        }
    });
}
function scsxDetail(scsxId) {
    var action = "detail";
    mini.open({
        title: "新增市场刷写记录",
        url: jsUseCtxPath + "/environment/core/Rjbg/editSc.do?action=" + action + "&scsxId=" + scsxId,
        width: 1050,
        height: 400,
        allowResize: true,
        onload: function () {
            scListGrid.reload();
        },
        ondestroy:function () {
            scListGrid.reload();
        }
    });
}
function removeRjbgSc(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = scListGrid.getSelecteds();
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
                rowIds.push(r.scsxId);
            }
            _SubmitJson({
                url: jsUseCtxPath + "/environment/core/Rjbg/deleteRjbgSc.do",
                method: 'POST',
                showMsg: false,
                data: {ids: rowIds.join(',')},
                success: function (data) {
                    if (data) {
                        mini.alert(data.message);
                        scListGrid.reload();
                    }
                }
            });
        }
    });
}