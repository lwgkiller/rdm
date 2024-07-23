var first = "";
var second = "";
var third = "";
$(function () {
    if (jssbId) {
        var url = jsUseCtxPath + "/Jssb/getJssbDetail.do";
        $.post(
            url,
            {jssbId: jssbId},
            function (json) {
                formJssb.setData(json);
            });
    }else {
        mini.get("sbResId").setValue(currentUserId);
        mini.get("sbResId").setText(currentUserName);
        mini.get("sbDepId").setValue(currentUserMainDepId);
        mini.get("sbDepId").setText(deptName);
        mini.get("sbTime").setValue(currentTime);
    }
    mini.get("jdView").setEnabled(false);
    mini.get("spView").setEnabled(false);
    //变更入口
    if (action == 'task') {
        taskActionProcess();
    } else if (action == "detail") {
        formJssb.setEnabled(false);
        jszbListGrid.setAllowCellEdit(false);
        mini.get("addCnfFile").setEnabled(false);
        mini.get("editMsgZj").setEnabled(false);
        mini.get("removeZj").setEnabled(false);
        $("#detailToolBar").show();
        if(status!='DRAFTED') {
            $("#processInfo").show();
        }
    }
});

function getData() {
    var formData = _GetFormJsonMini("formJssb");
    formData.jszb=jszbListGrid.getChanges();
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    // formData.bos=[];
    // formData.vars=[{key:'companyName',val:formData.companyName}];
    return formData;
}

function saveJssb(e) {
    var formValid = validFirst();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.saveDraft(e);
}


function startJssbProcess(e) {
    var formValid = validFirst();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.startProcess(e);
}

function jssbApprove() {
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
    var apply = $.trim(mini.get("sbResId").getValue());
    if (!apply) {
        return {"result": false, "message": "请填写申报人员"};
    }
    var appDept = $.trim(mini.get("sbDepId").getValue());
    if (!appDept) {
        return {"result": false, "message": "请填写申报部门"};
    }
    var jsName = $.trim(mini.get("jsName").getValue());
    if (!jsName) {
        return {"result": false, "message": "请填写技术名称"};
    }
    var startTime = $.trim(mini.get("startTime").getValue());
    if (!startTime) {
        return {"result": false, "message": "请填写预计开始时间"};
    }
    var needTime = $.trim(mini.get("needTime").getValue());
    if (!needTime) {
        return {"result": false, "message": "请填写预计结束时间"};
    }
    var direction = $.trim(mini.get("direction").getValue());
    if (!direction) {
        return {"result": false, "message": "请选择专业方向"};
    }
    var szrCheck = $.trim(mini.get("szrCheck").getValue());
    if(szrCheck=="false"){
        var szrId = $.trim(mini.get("szrId").getValue());
        if (!szrId) {
            return {"result": false, "message": "请填写校对室主任"};
        }
    }
    var jsms = $.trim(mini.get("jsms").getValue());
    if (!jsms) {
        return {"result": false, "message": "请填写技术描述"};
    }
    var intro = $.trim(mini.get("intro").getValue());
    if (!intro) {
        return {"result": false, "message": "请填写技术简介"};
    }
    var innovate = $.trim(mini.get("innovate").getValue());
    if (!innovate) {
        return {"result": false, "message": "请填写创新点"};
    }
    var corepart = $.trim(mini.get("corepart").getValue());
    if (!corepart) {
        return {"result": false, "message": "请填写核心零部件"};
    }
    var property = $.trim(mini.get("property").getValue());
    if (!property) {
        return {"result": false, "message": "请填写产权分析"};
    }
    return {"result": true};
}

function jssbSecond() {
    //编制阶段的下一步需要校验表单必填字段
    if (second == 'yes') {
        var formValid = validSecond();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    //检查通过
    window.parent.reject();
}


function validSecond() {
    var jdView = $.trim(mini.get("jdView").getValue());
    if (!jdView) {
        return {"result": false, "message": "请填写驳回原因"};
    }
    return {"result": true};
}

function jssbThird() {
    //编制阶段的下一步需要校验表单必填字段
    if (third == 'yes') {
        var formValid = validThird();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
    }
    //检查通过
    window.parent.reject();
}


function validThird() {
    var spView = $.trim(mini.get("spView").getValue());
    if (!spView) {
        return {"result": false, "message": "请填写驳回原因"};
    }
    return {"result": true};
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
    var jssbId = mini.get("jssbId").getValue();
    if (!jssbId) {
        mini.alert('请先点击‘保存草稿’进行表单的保存！');
        return;
    }
    mini.open({
        title: "添加附件",
        url: jsUseCtxPath + "/Jssb/openUploadWindow.do?jssbId=" + jssbId,
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
    }
    formJssb.setEnabled(false);
    mini.get("addCnfFile").setEnabled(false);
    mini.get("editMsgZj").setEnabled(false);
    mini.get("removeZj").setEnabled(false);
    if (first == 'yes') {
        mini.get("jsName").setEnabled(true);
        mini.get("sbDepId").setEnabled(true);
        mini.get("sbResId").setEnabled(true);
        mini.get("sbTime").setEnabled(true);
        mini.get("startTime").setEnabled(true);
        mini.get("needTime").setEnabled(true);
        mini.get("direction").setEnabled(true);
        mini.get("jsms").setEnabled(true);
        mini.get("intro").setEnabled(true);
        mini.get("innovate").setEnabled(true);
        mini.get("corepart").setEnabled(true);
        mini.get("property").setEnabled(true);
        mini.get("szrId").setEnabled(true);
        mini.get("szrCheck").setEnabled(true);
        mini.get("addCnfFile").setEnabled(true);
        mini.get("editMsgZj").setEnabled(true);
        mini.get("removeZj").setEnabled(true);
    }
    if (second == 'yes') {
        mini.get("jdView").setEnabled(true);
    }
    if (third == 'yes') {
        mini.get("spView").setEnabled(true);
    }
}



function cxupload() {
    var jssbId = mini.get("jssbId").getValue();
    if (!jssbId) {
        mini.alert('请先点击‘保存草稿’进行表单的保存！');
        return;
    }
    mini.open({
        title: "添加附件",
        url: jsUseCtxPath + "/Jssb/openUploadWindow.do?jssbId=" + jssbId,
        width: 850,
        height: 550,
        showModal: true,
        allowResize: true,
        ondestroy: function () {
            if (cxdFileListGrid) {
                cxdFileListGrid.load();
            }
        }
    });
}

function returnJssbPreviewSpan(fileName, fileId, formId, coverContent) {
    var fileType = getFileType(fileName);
    var s = '';
    if (fileType == 'other') {
        s = '<span  title="预览" style="color: silver" >预览</span>';
    } else if (fileType == 'pdf') {
        var url = '/Jssb/jssbPdfPreview';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
    } else if (fileType == 'office') {
        var url = '/Jssb/jssbOfficePreview';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
    } else if (fileType == 'pic') {
        var url = '/Jssb/jssbImagePreview';
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">预览</span>';
    }
    return s;
}

function downLoadJssbFile(fileName, fileId, formId) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + '/Jssb/jssbPdfPreview.do?action=download');
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
                        if(cxdFileListGrid) {
                            cxdFileListGrid.load();
                        }
                    }
                });
            }
        }
    );
}
function addJszbDetail() {
    var formId = mini.get("jssbId").getValue();
    if (!formId) {
        mini.alert('请先点击‘保存’进行表单的保存！');
        return;
    } else {
        var row = {};
        jszbListGrid.addRow(row);
    }
}

function removeJszb() {
    var selecteds = jszbListGrid.getSelecteds();
    var deleteArr = [];
    for (var i = 0; i < selecteds.length; i++) {
        var row = selecteds[i];
        deleteArr.push(row);
    }
    jszbListGrid.removeRows(deleteArr);
}

