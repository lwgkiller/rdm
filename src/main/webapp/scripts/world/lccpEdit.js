var first = "";
var second = "";
var third = "";
var forth = "";
var bianzhi = "";
$(function () {
    if (lccpId) {
        var url = jsUseCtxPath + "/Lccp/getLccpDetail.do";
        $.post(
            url,
            {lccpId: lccpId},
            function (json) {
                formlccp.setData(json);
                mini.get("country").setValue(json.country);
                mini.get("country").setText(json.country);
            });
    }else {
        mini.get("res").setValue(currentUserId);
        mini.get("res").setText(currentUserName);
    }
    mini.get("addFile1").setEnabled(false);
    mini.get("addFile2").setEnabled(false);
    mini.get("addFile3").setEnabled(false);
    mini.get("addFile4").setEnabled(false);
    mini.get("ddNumber").setEnabled(false);
    //变更入口
    if (action == 'task') {
        taskActionProcess();
    } else if (action == "detail") {
        formlccp.setEnabled(false);
        $("#detailToolBar").show();
        if(status!='DRAFTED') {
            $("#processInfo").show();
        }
    }else if (action == "edit") {
        mini.get("addFile1").setEnabled(true);
    }
});

function saveLccpInProcess() {
    var formData = _GetFormJsonMini("formlccp");
    if (formData.SUB_ckFileListGrid) {
        delete formData.SUB_ckFileListGrid;
    }
    $.ajax({
        url: jsUseCtxPath + '/Lccp/saveLccp.do',
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
                    mini.alert(lccpEdit_sjbcsb+data.message);
                }
            }
        }
    });
}

function getData() {
    var formData = _GetFormJsonMini("formlccp");
    return formData;
}

function saveLccp(e) {
    var formValid = validFirst();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.saveDraft(e);
}

function startLccpProcess(e) {
    var formValid = validFirst();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    if (ckFileListGrid1.totalCount == 0){
        mini.alert(lccpEdit_scfj);
        return ;
    }
    window.parent.startProcess(e);
}

function projectApprove() {
    var formValid = validFirst();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.approve();
}

function lccpApprove() {
    var formValid = validFirst();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.approve();
}

function validFirst() {
    var ckMonth = $.trim(mini.get("ckMonth").getValue());
    if (!ckMonth) {
        return {"result": false, "message": lccpEdit_yf};
    }
    var model = $.trim(mini.get("model").getValue());
    if (!model) {
        return {"result": false, "message": lccpEdit_xsxh};
    }
    var region = $.trim(mini.get("region").getValue());
    if (!region) {
        return {"result": false, "message": lccpEdit_ckqy};
    }
    var country = $.trim(mini.get("country").getValue());
    if (!country) {
        return {"result": false, "message": lccpEdit_ckgj};
    }
    var res = $.trim(mini.get("res").getValue());
    if (!res) {
        return {"result": false, "message": lccpEdit_ddfzr};
    }
    var cpzg = $.trim(mini.get("cpzg").getValue());
    if (!cpzg) {
        return {"result": false, "message": lccpEdit_cpzg};
    }
    var dep = $.trim(mini.get("dep").getValue());
    if (!dep) {
        return {"result": false, "message": lccpEdit_zrbm};
    }
    var need = $.trim(mini.get("need").getValue());
    if (!need) {
        return {"result": false, "message": lccpEdit_scrl};
    }
    return {"result": true};
}

function lccpFirst() {
    if (ckFileListGrid1.totalCount == 0){
        mini.alert(lccpEdit_scfj);
        return ;
    }
    //检查通过
    window.parent.approve();
}
function lccpSecond() {
    if (ckFileListGrid2.totalCount == 0){
        mini.alert(lccpEdit_scfj);
        return ;
    }
    //检查通过
    window.parent.approve();
}
function lccpThird() {
    if (ckFileListGrid3.totalCount == 0){
        mini.alert(lccpEdit_scfj);
        return ;
    }
    //检查通过
    window.parent.approve();
}
//第四步检验为非必须上传附件
function lccpForth() {
    //检查通过
    window.parent.approve();
}

function processInfo() {
    var instId = $("#instId").val();
    var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: lccpEdit_lcslt,
        width: 800,
        height: 600
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
        if (nodeVars[i].KEY_ == 'bianzhi') {
            bianzhi  = nodeVars[i].DEF_VAL_;
        }
    }
    formlccp.setEnabled(false);
    if (first == 'yes') {
        mini.get("addFile1").setEnabled(true);
    }
    if (second == 'yes') {
        mini.get("addFile2").setEnabled(true);
    }
    if (third == 'yes') {
        mini.get("addFile3").setEnabled(true);
    }
    if (forth == 'yes') {
        mini.get("addFile4").setEnabled(true);
    }
    if (bianzhi == 'yes') {
        formlccp.setEnabled(true);
    }
}



function fjupload(loc) {
    var lccpId = mini.get("lccpId").getValue();
    var loc = loc;
    if (!lccpId) {
        mini.alert(lccpEdit_bccg);
        return;
    }
    mini.open({
        title: lccpEdit_tjfj,
        url: jsUseCtxPath + "/Lccp/openUploadWindow.do?lccpId="+lccpId +"&loc="+loc,
        width: 850,
        height: 550,
        showModal: false,
        allowResize: true,
        ondestroy: function () {
            if (ckFileListGrid1) {
                ckFileListGrid1.load();
            }
            if (ckFileListGrid2) {
                ckFileListGrid2.load();
            }
            if (ckFileListGrid3) {
                ckFileListGrid3.load();
            }
            if (ckFileListGrid4) {
                ckFileListGrid4.load();
            }
        }
    });
}

function returnlccpPreviewSpan(fileName, fileId, formId, coverContent) {
    var fileType = getFileType(fileName);
    var s = '';
    if (fileType == 'other') {
        s = '<span  title=' + lccpEdit_yl + ' style="color: silver" >' + lccpEdit_yl + '</span>';
    } else if (fileType == 'pdf') {
        var url = '/Lccp/lccpPdfPreview';
        s = '<span  title=' + lccpEdit_yl + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + lccpEdit_yl + '</span>';
    } else if (fileType == 'office') {
        var url = '/Lccp/lccpOfficePreview';
        s = '<span  title=' + lccpEdit_yl + ' style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + lccpEdit_yl + '</span>';
    } else if (fileType == 'pic') {
        var url = '/Lccp/lccpImagePreview';
        s = '<span  title=' + lccpEdit_yl + ' style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + lccpEdit_yl + '</span>';
    }
    return s;
}

function downLoadlccpFile(fileName, fileId, formId) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + '/Lccp/lccpPdfPreview.do?action=download');
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


function deletelccpFile(fileName,fileId,formId,urlValue) {
    mini.confirm(lccpEdit_qdsc, lccpEdit_qd,
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
                        if(ckFileListGrid1) {
                            ckFileListGrid1.load();
                        }
                        if(ckFileListGrid2) {
                            ckFileListGrid2.load();
                        }
                        if(ckFileListGrid3) {
                            ckFileListGrid3.load();
                        }
                        if(ckFileListGrid4) {
                            ckFileListGrid4.load();
                        }
                    }
                });
            }
        }
    );
}

//添加关联项目信息展开窗口
function addRelatedProject(){
    projectWindow.show();
    searchProcessData();
}
//关闭关联项目选址页面
function closeProjectWindow(){
    projectWindow.hide();
    mini.get("country_name").setValue();
    mini.get("english_name").setValue();
    mini.get("country_name").setText();
    mini.get("english_name").setText();
}
//国家名称查询
function searchProcessData() {
    var country_name=mini.get("country_name").getValue();
    var english_name=mini.get("english_name").getValue();
    var paramArray = [{name: "country_name", value: country_name},{name: "english_name", value: english_name}];
    var data = {};
    data.filter = mini.encode(paramArray);
    projectListGrid.load(data);
}
function cleanProcessData() {
    mini.get("country_name").setValue();
    mini.get("english_name").setValue();
    mini.get("country_name").setText();
    mini.get("english_name").setText();
    searchProcessData();
}
//国家名称写入
function choseRelatedProject(e){
    var row = projectListGrid.getSelected();
    var rows = projectListGrid.getSelecteds();
    if(rows==null){
        alert(lccpEdit_qzsxz);
        return;
    }
    var countries=[];
    for(var i=0;i<rows.length;i++){
        countries.push(rows[i].country_name);
    }
    var country_names =  countries.join(',');
    mini.get("country").setValue(country_names);
    mini.get("country").setText(country_names);

    closeProjectWindow();
}
function relatedCloseClick(){
    mini.get("country").setValue("");
    mini.get("country").setText("");
    mini.get("country_name").setValue("");
    mini.get("country_name").setText("");
    mini.get("english_name").setValue("");
    mini.get("english_name").setText("");
}

//导入模板下载
function downLoadModel() {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/Lccp/importTemplateDownload.do");
    $("body").append(form);
    form.submit();
    form.remove();
}