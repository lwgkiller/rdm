var editForm = '';
var checkNote = '';
var isFirstNode = false;
var ZGFP = false;
var ZGFA = false;
var YYFC = false;
var RZZL = false;
var SJXH = false;
var RZZLFP = false;
var RZZLBZ = false;
var RZZLQR = false;
var RZZLCD = false;
var WJZC = false;
var RZZLWT = false;
var BGZSSC = false;
var YJZG = false;
var approveEditable = false;
var planEditable = false;
var docEditable = false;
$(function () {
    setButtonDisabled();
    if (action == 'task') {
        bpmPreTaskTipInForm();
        getProcessNodeVars();
        if (editForm != '1' && !isFirstNode) {
            applyForm.setEnabled(false);
        } else {
        }
        applyForm.setData(ApplyObj);
        setButtonStatus();
    } else if (action == 'detail') {
        $("#detailToolBar").show();
        if (status != 'DRAFTED') {
            $("#processInfo").show();
        }
        applyForm.setEnabled(false);
        applyForm.setData(ApplyObj);
    } else if (action == 'edit') {
        applyForm.setData(ApplyObj);
        setEditButtonStatus();
    }
    grid_problem.setData(ApplyObj.detailList);
    grid_document.setData(ApplyObj.docList);
    var year = new Date().getFullYear();
    mini.get('yearSelect').setValue(year);
    mini.get('isShowAll').setValue(0);
});

//流程实例图
function processInfo() {
    let instId = $("#instId").val();
    let url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: "流程图实例",
        width: 800,
        height: 600
    });
}

/**
 * 表单弹出事件控制
 * @param ck
 * @param id
 */
function toggleFieldSet(ck, id) {
    var dom = document.getElementById(id);
    if (ck.checked) {
        dom.className = "";
    } else {
        dom.className = "hideFieldset";
    }
}

function setEditButtonStatus() {
    mini.get('addProblemButton').setEnabled(true);
    mini.get('delProblemButton').setEnabled(true);
    mini.get('addProblemFile').setEnabled(true);
}

function setButtonStatus() {
    if (isFirstNode) {
        mini.get('addProblemButton').setEnabled(true);
        mini.get('delProblemButton').setEnabled(true);
        mini.get('addProblemFile').setEnabled(true);
    }
    if (YYFC) {
        mini.get('pass').setEnabled(true);
        mini.get('reStartDate').setEnabled(true);
        mini.get('reEndDate').setEnabled(true);
        mini.get('addProblemButton').setEnabled(true);
        mini.get('delProblemButton').setEnabled(true);
    }
    if (RZZL) {
        mini.get('docComplete').setEnabled(true);
        mini.get('addDocumentButton').setEnabled(true);
        mini.get('delDocumentButton').setEnabled(true);
    }
    if (SJXH) {
        mini.get('designModel').setEnabled(true);
    }

    if (RZZLQR) {
        // mini.get('techManagerIds').setEnabled(true);
    }
    if (RZZLCD) {
        mini.get('sendDate').setEnabled(true);
    }
    if (RZZLWT) {
        mini.get('docOk').setEnabled(true);
    }
    if (BGZSSC) {
        mini.get('addReportFile').setEnabled(true);
    }

}

function setButtonDisabled() {
    mini.get('addProblemButton').setEnabled(false);
    mini.get('delProblemButton').setEnabled(false);
    mini.get('addDocumentButton').setEnabled(false);
    mini.get('delDocumentButton').setEnabled(false);
    mini.get('addReportFile').setEnabled(false);
    mini.get("docComplete").setEnabled(false);
    mini.get("docOk").setEnabled(false);
    // mini.get("techManagerIds").setEnabled(false);
    mini.get("sendDate").setEnabled(false);
    mini.get("reStartDate").setEnabled(false);
    mini.get("reEndDate").setEnabled(false);
    mini.get("addProblemFile").setEnabled(false);
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
        if (nodeVars[i].KEY_ == 'ZGFP') {
            ZGFP = true;
        }
        if (nodeVars[i].KEY_ == 'ZGFA') {
            ZGFA = true;
        }
        if (nodeVars[i].KEY_ == 'YYFC') {
            YYFC = true;
        }
        if (nodeVars[i].KEY_ == 'RZZL') {
            RZZL = true;
        }
        if (nodeVars[i].KEY_ == 'SJXH') {
            SJXH = true;
        }
        if (nodeVars[i].KEY_ == 'RZZLFP') {
            RZZLFP = true;
        }
        if (nodeVars[i].KEY_ == 'RZZLBZ') {
            RZZLBZ = true;
        }
        if (nodeVars[i].KEY_ == 'RZZLQR') {
            RZZLQR = true;
        }
        if (nodeVars[i].KEY_ == 'RZZLCD') {
            RZZLCD = true;
        }
        if (nodeVars[i].KEY_ == 'WJZC') {
            WJZC = true;
        }
        if (nodeVars[i].KEY_ == 'RZZLWT') {
            RZZLWT = true;
        }
        if (nodeVars[i].KEY_ == 'BGZSSC') {
            BGZSSC = true;
        }
        if (nodeVars[i].KEY_ == 'YJZG') {
            YJZG = true;
        }
        if (nodeVars[i].KEY_ == 'checkNote') {
            checkNote = nodeVars[i].DEF_VAL_;
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
    saveProblemData();
    window.parent.saveDraft(e);
}

//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("applyForm");
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos = [];
    formData.vars = [];
    formData.itemNames = mini.get('items').getText();
    formData.productLeaderName = mini.get('productLeader').getText();
    formData.itemNames = mini.get('items').getText();
    formData.vars = [{key: 'productModel', val: formData.productModel},
        {key: 'productLeaderName', val: formData.productLeaderName},
        {key: 'itemNames', val: formData.itemNames}];
    return formData;
}

//保存草稿或提交时数据是否有效
function draftOrStartValid() {
    let productType = $.trim(mini.get("productType").getValue());
    if (!productType) {
        return {"result": false, "message": "请选择产品类型"};
    }

    let cabForm = $.trim(mini.get("cabForm").getValue());
    if (!cabForm) {
        return {"result": false, "message": "请选择司机室形式"};
    }

    let productLeader = $.trim(mini.get("productLeader").getValue());
    if (!productLeader) {
        return {"result": false, "message": "请选择产品主管"};
    }

    let applyNo = $.trim(mini.get("applyNo").getValue());
    if (!applyNo) {
        return {"result": false, "message": "请选择TDM申请单号"};
    }


    let productModel = $.trim(mini.get("productModel").getValue());
    if (!productModel) {
        return {"result": false, "message": "产品型号必填"};
    }
    let items = $.trim(mini.get("items").getValue());
    if (!items) {
        return {"result": false, "message": "请选择认证项目！"};
    }
    let startDate = $.trim(mini.get("startDate").getValue());
    if (!startDate) {
        return {"result": false, "message": "请选择测试开始日期！"};
    }
    let endDate = $.trim(mini.get("endDate").getValue());
    if (!endDate) {
        return {"result": false, "message": "请选择测试结束日期！"};
    }
    let pass = $.trim(mini.get("pass").getValue());
    if (!pass) {
        return {"result": false, "message": "请选择测试是否通过"};
    }
    if (ZGFP) {
        var problemList = grid_problem.getData();
        for (var i = 0; i < problemList.length; i++) {
            if (!problemList[i].charger) {
                return {"result": false, "message": "每条问题请选择负责人！"};
                break
            }
        }
    }
    if (ZGFA) {
        var problemList = grid_problem.getData();
        for (var i = 0; i < problemList.length; i++) {
            if (problemList[i].charger == currentUserId && !problemList[i].plan) {
                return {"result": false, "message": "请填写整改方案！"};
                break
            }
        }
    }
    if (YYFC) {
        var problemList = grid_problem.getData();
        for (var i = 0; i < problemList.length; i++) {
            if (!problemList[i].passed) {
                return {"result": false, "message": "请填写问题是否通过！"};
                break
            }
        }
    }
    if (RZZL) {
        let docComplete = $.trim(mini.get("docComplete").getValue());
        if (!docComplete) {
            return {"result": false, "message": "请选择资料是否已具备！"};
        }
        if(docComplete=='0'){
            var docList = grid_document.getData();
            if (docList.length < 1) {
                return {"result": false, "message": "请添加认证资料明细！"};
            }
        }
    }
    if (SJXH) {
        let designModel = $.trim(mini.get("designModel").getValue());
        if (!designModel) {
            return {"result": false, "message": "请填写设计型号！"};
        }
    }
    if (RZZLFP) {
        var docList = grid_document.getData();
        for (var i = 0; i < docList.length; i++) {
            if (!docList[i].charger) {
                return {"result": false, "message": "请选择认证资料负责人！"};
                break
            }
        }
    }
    //认证资料编制
    if (RZZLBZ) {
        var docList = grid_document.getData();
        for (var i = 0; i < docList.length; i++) {
            if (docList[i].charger == currentUserId) {
                if (!docList[i].used) {
                    return {"result": false, "message": "请选择本产品是否适用！"};
                    break
                } else if (docList[i].used == '是') {
                    //判断是否传认证资料
                    let postData = {"mainId": docList[i].id};
                    let _url = jsUseCtxPath + '/wwrz/core/file/fileList.do';
                    let resultData = ajaxRequest(_url, 'POST', false, postData);
                    if (resultData && !resultData.success) {
                        mini.alert(resultData.message);
                        return;
                    } else {
                        if (resultData.data.length < 1) {
                            return {"result": false, "message": "请上传认证资料！"};
                            break
                        }
                    }
                }
            }
        }
    }
    if (RZZLQR) {
        // mini.get('techManagerIds').setEnabled(true);
        // let techManagerIds = $.trim(mini.get("techManagerIds").getValue());
        // if (!techManagerIds) {
        //     return {"result": false, "message": "请选择转出审批主任"};
        // }
        // var num = techManagerIds.split(',').length - 1;
        // if (num != 1) {
        //     return {"result": false, "message": "请选择2个转出审批主任"};
        // }
    }
    if (RZZLCD) {
        let sendDate = $.trim(mini.get("sendDate").getValue());
        if (!sendDate) {
            return {"result": false, "message": "请添加传递时间！"};
        }
    }
    if (RZZLWT) {
        let docOk = $.trim(mini.get("docOk").getValue());
        if (!docOk) {
            return {"result": false, "message": "请选择是否有反馈资料问题！"};
        }
    }
    //样机整改 添加证明文件
    if (YJZG) {
        var problemList = grid_problem.getData();
        for (var i = 0; i < problemList.length; i++) {
            //判断是否传认证资料
            let postData = {"mainId": problemList[i].id,"fileType":"testApprove"};
            let _url = jsUseCtxPath + '/wwrz/core/file/fileListByParam.do';
            let resultData = ajaxRequest(_url, 'POST', false, postData);
            if (resultData && !resultData.success) {
                mini.alert(resultData.message);
                return;
            } else {
                if (resultData.data.length < 1) {
                    return {"result": false, "message": "请上传整改证明！"};
                    break
                }
            }
        }
    }
    if(checkNote=='yes'){
        checkNoteExist();
        if (!checkNoteResult.result) {
            return {"result": false, "message": checkNoteResult.message};
        }
    }
    return {"result": true};
}

var checkNoteResult = "";
function checkNoteExist() {
    var id = mini.get("id").getValue();
    $.ajax({
        url: jsUseCtxPath + '/wwrz/core/test/checkNote.do?id=' + id,
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if (!data.success) {
                checkNoteResult = {"result": false, "message": data.message};
            } else {
                checkNoteResult = {"result": true};
            }
        }
    });
}

function addNew() {
    var wwrzId = mini.get("id").getValue();
    mini.open({
        title: "新增",
        url: jsUseCtxPath + "/wwrz/core/CE/edit.do?action=auto&wwrzId="+wwrzId,
        width: 1050,
        height: 550,
        allowResize: true,
        onload: function () {
            searchFrm();
        },
        ondestroy:function () {
            searchFrm();
        }
    });
}

//启动流程
function startApplyProcess(e) {
    var formValid = draftOrStartValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
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
    saveProblemData();
    saveDocumentData();
    window.parent.approve();
}

function addProblem() {
    var applyId = mini.get("id").getValue();
    if(!applyId){
        mini.alert("请先保存草稿");
        return
    }
    var row = {};
    grid_problem.addRow(row);
}

function delProblem() {
    var selecteds = grid_problem.getSelecteds();
    var deleteArr = [];
    for (var i = 0; i < selecteds.length; i++) {
        var row = selecteds[i];
        deleteArr.push(row);
    }
    grid_problem.removeRows(deleteArr);
}
function viewStandard() {
    var selecteds = grid_problem.getSelecteds();
    if(selecteds.length != 1){
        mini.alert("请选中一条记录进行查看！");
        return
    }
    var sectorId = selecteds[0].sectorId;
    let postData = {"sectorId": sectorId};
    let _url = jsUseCtxPath + '/wwrz/core/standard/standardFile.do';
    let resultData = ajaxRequest(_url, 'POST', false, postData);
    if (resultData && !resultData.success) {
        mini.alert(resultData.message);
        return;
    } else {
        var fileList = resultData.data;
        if(fileList.length<1){
            mini.alert("未上传章节附件！");
            return
        }else{
            var url = '/sys/core/commonInfo/imagePreview.do';
            var fileName = fileList[0].fileName;
            var fileType = getFileType(fileName);
            if(fileType!='pic'){
                mini.alert("章节附件请上传图片！");
                return
            }
            var fileId = fileList[0].id;
            var formId = fileList[0].mainId;
            var fileUrl = 'wwrzFileUrl';
            var previewUrl= jsUseCtxPath + url+"?fileName=" + encodeURIComponent(fileName)+"&fileId="+fileId+"&formId="+formId+"&fileUrl="+fileUrl;
            var title = "查看标准";
            mini.open({
                title: title,
                url: previewUrl,
                width: 1000,
                height: 800,
                showModal: true,
                allowResize: true,
                onload: function () {
                },
                ondestroy: function (action) {
                }
            });
        }
    }
}

function rectifyApprove(e) {
    var record = e.record;
    var detailId = record.id;
    var s = '';
    if(YJZG){
        approveEditable = true;
    }else{
        approveEditable = false;
    }
    if (detailId == '' || detailId == 'undefined' || detailId == undefined) {
        s += '<span  title="附件上传" style="color: grey"">整改证明</span>';
    } else {
        s += '<span  title="附件上传" style="cursor: pointer;color: #0a7ac6" onclick="showFilePage(\'' + detailId + '\',\'testApprove\',\'' + approveEditable + '\')">整改证明</span>';
    }
    return s;
}

function testPlan(e) {
    var record = e.record;
    var detailId = record.id;
    var s = '';
    if (ZGFA && currentUserId == record.charger) {
        planEditable = true;
    } else {
        planEditable = false;
    }
    if (detailId == '' || detailId == 'undefined' || detailId == undefined) {
        s += '<span  title="附件上传" style="color: grey"">方案附件</span>';
    } else {
        s += '<span  title="附件上传" style="cursor: pointer;color: #0a7ac6" onclick="showFilePage(\'' + detailId + '\',\'testPlan\',\'' + planEditable + '\')">方案附件</span>';
    }
    return s;
}

function testDoc(e) {
    var record = e.record;
    var detailId = record.id;
    var s = '';
    if ((RZZLBZ && currentUserId == record.charger) || RZZLQR || RZZLWT) {
        docEditable = true;
    } else {
        docEditable = false;
    }
    if (detailId == '' || detailId == 'undefined' || detailId == undefined) {
        s += '<span  title="附件上传" style="color: grey"">认证资料</span>';
    } else {
        s += '<span  title="附件上传" style="cursor: pointer;color: #0a7ac6" onclick="showFilePage(\'' + detailId + '\',\'testDoc\',\'' + docEditable + '\')">认证资料</span>';
    }
    return s;
}

function showFilePage(detailId, fileType, editable) {
    mini.open({
        title: "附件列表",
        url: jsUseCtxPath + "/wwrz/core/file/fileWindow.do?detailId=" + detailId + "&fileType=" + fileType + "&editable=" + editable,
        width: 1000,
        height: 500,
        showModal: false,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
        }
    });
}

function addDocument() {
    var row = {};
    grid_document.addRow(row);
}

function delDocument() {
    var selecteds = grid_document.getSelecteds();
    var deleteArr = [];
    for (var i = 0; i < selecteds.length; i++) {
        var row = selecteds[i];
        deleteArr.push(row);
    }
    grid_document.removeRows(deleteArr);
}

function genDocumentList(e) {
    var docType = e.selected.text;
    let postData = {"docType": docType};
    let _url = jsUseCtxPath + '/wwrz/core/doc/documentList.do';
    let resultData = ajaxRequest(_url, 'POST', false, postData);
    if (resultData && !resultData.success) {
        mini.alert(resultData.message);
        return;
    } else {
        var docList = resultData.data;
        for (var i = 0; i < docList.length; i++) {
            if(isExist(docList[i].docType,docList[i].docName)){
                continue;
            }
            var row = {docType: docList[i].docType, docName: docList[i].docName};
            grid_document.addRow(row);
        }
    }
    delDocument();
}
function isExist(docType,docName) {
    var flag = false;
    var documentList = grid_document.getData();
    for(var i=0;i<documentList.length;i++){
        if(documentList[i].docType&&documentList[i].docName){
            if(documentList[i].docType==docType&&documentList[i].docName==docName){
                flag = true
                break
            }
        }
    }
    return flag;
}
function beforeShowSector(e) {
    var rowNode = grid_problem.getSelected();
    var standardId = rowNode.standardId;
    if (!standardId) {
        mini.alert("请先选择绑定标准");
        return;
    }
    var bnc = mini.getByName("sectorCombo");
    var url = jsUseCtxPath + "/wwrz/core/standard/listSector.do?standardId=" + standardId
    bnc.setUrl(url);
}
function standardChange (_this) {
    grid_problem.updateRow(grid_problem.getSelected(), {sectorId: ''});
    grid_problem.updateRow(grid_problem.getSelected(), {sectorName: ''});
}
function beforeShowItem(e) {
    var rowNode = grid_document.getSelected();
    var docType = rowNode.docType;
    if (!docType) {
        mini.alert("请先选择类别");
        return;
    }
    var bnc = mini.getByName("itemCombo");
    var url = jsUseCtxPath + "/wwrz/core/doc/getDicDoc.do?docType=" + docType
    bnc.setUrl(url);
}

function addReportFile() {
    var applyId = mini.get("id").getValue();
    if (!applyId) {
        mini.alert('请先点击‘保存草稿’进行表单的保存！');
        return;
    }
    mini.open({
        title: "文件上传",
        url: jsUseCtxPath + "/wwrz/core/file/reportUploadWindow.do?detailId=" + applyId + "&fileType=report",
        width: 1250,
        height: 550,
        showModal: false,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            if (fileListGrid) {
                fileListGrid.load();
            }
        }
    });
}

function saveProblemData() {
    var data = grid_problem.getChanges();
    var needReload = true;
    var mainId = mini.get('id').getValue();
    if (data.length > 0) {
        for (var i = 0; i < data.length; i++) {
            data[i].mainId = mainId;
            if (data[i]._state == 'removed') {
                continue;
            }
        }
        if (needReload) {
            var json = mini.encode(data);
            $.ajax({
                url: jsUseCtxPath + "/wwrz/core/test/dealProblemData.do",
                data: json,
                type: "post",
                contentType: 'application/json',
                async: false,
                success: function (data) {
                    if (data && data.success) {
                    } else {
                        mini.alert(data.message);
                    }
                }
            });
        }
    }
}

function saveDocumentData() {
    var data = grid_document.getChanges();
    var needReload = true;
    var mainId = mini.get('id').getValue();
    if (data.length > 0) {
        for (var i = 0; i < data.length; i++) {
            data[i].mainId = mainId;
            if (data[i]._state == 'removed') {
                continue;
            }
        }
        if (needReload) {
            var json = mini.encode(data);
            $.ajax({
                url: jsUseCtxPath + "/wwrz/core/test/dealDocumentData.do",
                data: json,
                type: "post",
                contentType: 'application/json',
                async: false,
                success: function (data) {
                    if (data && data.success) {
                    } else {
                        mini.alert(data.message);
                    }
                }
            });
        }
    }
}

function addTestProblemFile() {
    var applyId = mini.get("id").getValue();
    if (!applyId) {
        mini.alert('请先点击‘保存草稿’进行表单的保存！');
        return;
    }
    mini.open({
        title: "文件上传",
        url: jsUseCtxPath + "/wwrz/core/file/fileUploadWindow.do?detailId=" + applyId + "&fileType=testProblem",
        width: 750,
        height: 450,
        showModal: false,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            if (testProblemListGrid) {
                testProblemListGrid.load();
            }
        }
    });
}

function fileDown() {
    var id = mini.get('id').getValue();
    if (!id) {
        mini.alert("无认证资料！");
        return;
    }
    let postData = {"applyId": id};
    let _url = jsUseCtxPath + '/wwrz/core/doc/docListByApplyId.do';
    let resultData = ajaxRequest(_url, 'POST', false, postData);
    if (resultData && !resultData.success) {
        mini.alert(resultData.message);
        return;
    } else {
        var docList = resultData.data;
        if (docList.length < 1) {
            mini.alert("无认证资料！");
            return;
        }
    }


    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/wwrz/core/file/fileDownload.do?action=download");
    var applyId = $("<input>");
    applyId.attr("type", "hidden");
    applyId.attr("name", "applyId");
    applyId.attr("value", mini.get("id").getValue());
    var fileUrl = $("<input>");
    fileUrl.attr("type", "hidden");
    fileUrl.attr("name", "fileUrl");
    fileUrl.attr("value", "wwrzFileUrl");
    $("body").append(form);
    form.append(applyId);
    form.append(fileUrl);
    form.submit();
    form.remove();
}

//下载文档
function downFile(record) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/sys/core/commonInfo/fileDownload.do?action=download");
    var inputFileName = $("<input>");
    inputFileName.attr("type", "hidden");
    inputFileName.attr("name", "fileName");
    inputFileName.attr("value", record.fileName);
    var detailId = $("<input>");
    detailId.attr("type", "hidden");
    detailId.attr("name", "formId");
    detailId.attr("value", record.mainId);
    var fileId = $("<input>");
    fileId.attr("type", "hidden");
    fileId.attr("name", "fileId");
    fileId.attr("value", record.id);
    var fileUrl = $("<input>");
    fileUrl.attr("type", "hidden");
    fileUrl.attr("name", "fileUrl");
    fileUrl.attr("value", "wwrzFileUrl");
    $("body").append(form);
    form.append(inputFileName);
    form.append(detailId);
    form.append(fileId);
    form.append(fileUrl);
    form.submit();
    form.remove();
}

function deleteFile(record) {
    mini.confirm("确定删除？", "确定？",
        function (action) {
            if (action == "ok") {
                var url = jsUseCtxPath + "/wwrz/core/file/delFile.do";
                var data = {
                    mainId: record.mainId,
                    id: record.id,
                    fileName: record.fileName
                };
                $.post(
                    url,
                    data,
                    function (json) {
                        fileListGrid.load();
                    });
            }
        }
    );
}
