var editForm = '';
var isFirstNode = false;
//苏会杰要求开放文件修改权限
var isShenhe = false;
var isJiaodui = false;
$(function () {
    applyForm.setData(ApplyObj);
    if(ApplyObj.note){
        var remarkArray = JSON.parse(ApplyObj.note);
        itemListGrid.setData(remarkArray);
    }

    if (action == 'task') {
        bpmPreTaskTipInForm();
        getProcessNodeVars();
        if (!isFirstNode) {
           if((isShenhe || isJiaodui)){
               mini.get('addFile').setEnabled(true);
           }else {
               mini.get('addFile').setEnabled(false);
           }
            applyForm.setEnabled(false);
            mini.get('itemButtons').setEnabled(false);
            mini.get('addItem').setEnabled(false);
        }
    } else if (action == 'detail') {
        $("#detailToolBar").show();
        if (status != 'DRAFTED') {
            $("#processInfo").show();
        }
        applyForm.setEnabled(false);
        if(isPointRole == "true"){
            mini.get('addFile').setEnabled(true);
        }else{
            mini.get('addFile').setEnabled(false);
        }

        mini.get('itemButtons').setEnabled(false);
        mini.get('addItem').setEnabled(false);

    } else if (action == 'edit') {

        applyForm.setEnabled(true);
    } else if (action == 'copy') {
        mini.get('itemButtons').setEnabled(false);
        mini.get('designModel').setEnabled(false);
        mini.get('saleModel').setEnabled(false);
        mini.get('director').setEnabled(false);
        mini.get('materialCode').setEnabled(false);
        mini.get('yjUserIds').setEnabled(false);

        $("#toolBar").show();
        mini.get("fileStatus").setValue("编辑中");

    }else if (action == 'release') {
        mini.get('itemButtons').setEnabled(false);
        mini.get('designModel').setEnabled(false);
        mini.get('saleModel').setEnabled(false);
        mini.get('director').setEnabled(false);
        mini.get('materialCode').setEnabled(false);
        mini.get('yjUserIds').setEnabled(false);

        $("#toolBar").show();


    }



});



//流程实例图
function processInfo() {
    let instId = $("#instId").val();
    let url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: saleFileApplyEdit_name6,
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
        if (nodeVars[i].KEY_ == 'isShenhe') {
            isShenhe = true;
        }
        if (nodeVars[i].KEY_ == 'isJiaodui') {
            isJiaodui = true;
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
    //表格数据检验
    itemListGrid.validate();
    if(!itemListGrid.isValid()){
        return;
    }
    var data = itemListGrid.getData();
    if (data.length > 0) {
        applyForm.itemChangeData = data;
    }

    //检查过必填后，检查是否有重复
    // var duplicateValid = checkDuplicate();
    // if (!duplicateValid.result) {
    //     mini.alert(duplicateValid.message);
    //     return;
    // }

    window.parent.saveDraft(e);
}

//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("applyForm");
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos = [];
    formData.vars = [];
    return formData;
}

//保存草稿或提交时数据是否有效
function draftOrStartValid() {
    let fileType = $.trim(mini.get("fileType").getValue());
    if (!fileType) {
        return {"result": false, "message": saleFileApplyEdit_name7};
    }

    let language = $.trim(mini.get("language").getValue());
    if (!language) {
        return {"result": false, "message": saleFileApplyEdit_name10};
    }

    var yjUserIds =  $.trim(mini.get("yjUserIds").getValue());
    if (!yjUserIds) {
        return {"result": false, "message": "请选择流程中的会签人员"};
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
    //检查过必填后，检查是否有重复
    // checkDuplicate();
    // if (checkFlag) {
    //     return;
    // }
    //检查重复后要检查文件
    var fileListGridData = fileListGrid.getData();
    if (fileListGridData.length == 0) {
        mini.alert("请上传售前文件！");
        return;
    }

    // var fileType =$.trim(mini.get("fileType").getValue());
    // if (fileListGridData.length > 1) {
    //     if(fileType != "技术规格书"){
    //         mini.alert("请删除多余文件，只能上传一份售前文件！");
    //         return;
    //     }
    //
    // }

    window.parent.startProcess(e);
}

//审批或者下一步
function applyApprove() {
    var formValid = draftOrStartValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    //检查过必填后，检查是否有重复
    // checkDuplicate();
    // if (checkFlag) {
    //     return;
    // }
    window.parent.approve();
}

function addSaleFile() {

    var id = mini.get("id").getValue();
    if (!id) {
        mini.alert(saleFileApplyEdit_name14);
        return;
    }
    var fileModel ="sq";


    if(action =='copy'){
        fileModel ="fy";
    }

    mini.open({
        title: saleFileApplyEdit_name15,
        url: jsUseCtxPath + "/rdmZhgl/core/saleFileOMA/fileUploadWindow.do?applyId=" + applyId + "&fileModel="+fileModel,
        width: 750,
        height: 450,
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

function addExamineFile() {
    var applyId = mini.get("id").getValue();
    if (!applyId) {
        mini.alert(saleFileApplyEdit_name14);
        return;
    }
    mini.open({
        title: saleFileApplyEdit_name15,
        url: jsUseCtxPath + "/rdmZhgl/core/saleFileOMA/fileUploadWindow.do?applyId=" + applyId + "&fileModel=sp",
        width: 750,
        height: 450,
        showModal: false,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            if (examineListGrid) {
                examineListGrid.load();
            }
        }
    });
}

function returnSalePreviewSpan(fileName, fileId, formId, coverContent, fileSize) {
    var fileType = getFileType(fileName);
    var s = '';
    if (fileType == 'other') {
        s = '<span  title=' + saleFileApplyEdit_name16 + ' style="color: silver" >' + saleFileApplyEdit_name16 + '</span>';
    } else if (fileType == 'pdf') {
        var url = '/rdmZhgl/core/saleFileOMA/pdfPreview.do';
        s = '<span  title=' + saleFileApplyEdit_name16 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + saleFileApplyEdit_name16 + '</span>';
    } else if (fileType == 'office') {
        var url = '/rdmZhgl/core/saleFileOMA/officePreview.do';
        s = '<span  title=' + saleFileApplyEdit_name16 + ' style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\',\'' + fileSize + '\')">' + saleFileApplyEdit_name16 + '</span>';
    } else if (fileType == 'pic') {
        var url = '/rdmZhgl/core/saleFileOMA/imagePreview.do';
        s = '<span  title=' + saleFileApplyEdit_name16 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + saleFileApplyEdit_name16 + '</span>';
    }
    return s;
}

//..
function getFileType(fileName) {
    var suffix = "";
    var suffixIndex = fileName.lastIndexOf('.');
    if (suffixIndex != -1) {
        suffix = fileName.substring(suffixIndex + 1).toLowerCase();
    }
    var pdfArray = ['pdf'];
    if (pdfArray.indexOf(suffix) != -1) {
        return 'pdf';
    }
    var officeArray = ['doc', 'docx', 'ppt', 'txt', 'xlsx', 'xls', 'pptx'];
    if (officeArray.indexOf(suffix) != -1) {
        return 'office';
    }
    var picArray = ['jpg', 'jpeg', 'jif', 'bmp', 'png', 'tif', 'gif'];
    if (picArray.indexOf(suffix) != -1) {
        return 'pic';
    }
    return 'other';
}

//下载文档
function downFile(record) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/rdmZhgl/core/saleFileOMA/fileDownload.do?action=download");
    var inputFileName = $("<input>");
    inputFileName.attr("type", "hidden");
    inputFileName.attr("name", "fileName");
    inputFileName.attr("value", record.fileName);
    var mainId = $("<input>");
    mainId.attr("type", "hidden");
    mainId.attr("name", "applyId");
    mainId.attr("value", record.applyId);
    var fileId = $("<input>");
    fileId.attr("type", "hidden");
    fileId.attr("name", "fileId");
    fileId.attr("value", record.id);
    $("body").append(form);
    form.append(inputFileName);
    form.append(mainId);
    form.append(fileId);
    form.submit();
    form.remove();
}

/**
 * 关联设计型号弹窗
 */
function selectDesignModel() {
    spectrumWindow.show();
    searchDesignModel();
}

/**
 * 关联设计型号查询
 */
function searchDesignModel() {
    var queryParam = [];
    //其他筛选条件
    var designModel = $.trim(mini.get("searchDesignModel").getValue());
    if (designModel) {
        queryParam.push({name: "designModel", value: designModel});
    }
    var data = {};
    data.filter = mini.encode(queryParam);
    data.pageIndex = spectrumListGrid.getPageIndex();
    data.pageSize = spectrumListGrid.getPageSize();
    data.sortField = spectrumListGrid.getSortField();
    data.sortOrder = spectrumListGrid.getSortOrder();
    //查询
    spectrumListGrid.load(data);
}


/**
 * 产品型号确定按钮
 */
function okWindow() {
    var selectRow = spectrumListGrid.getSelected();
    if (!selectRow) {
        mini.alert("请选择产品型号！");
        return;
    }
    mini.get("designModel").setValue(selectRow.designModel);
    mini.get("designModel").setText(selectRow.designModel);
    mini.get("saleModel").setValue(selectRow.saleModel);
    mini.get("materialCode").setValue(selectRow.materialCode);
    mini.get("director").setValue(selectRow.productManagerId);
    mini.get("director").setText(selectRow.productManagerName);
    hideWindow()
}

/**
 * 产品型号关闭按钮
 */
function hideWindow() {
    spectrumWindow.hide();
    mini.get("searchDesignModel").setValue('');
    mini.get("searchDesignModel").setText('');
}

var checkFlag = false;
// true表示有重复 四个条件查重，要查id非自己的单据，不然草稿保存之后无法提交
function checkDuplicate() {
    var id = mini.get("id").getValue();
    var saleModel = mini.get("saleModel").getValue();
    var designModel = mini.get("designModel").getValue();
    var language = mini.get("language").getValue();
    var fileType = mini.get("fileType").getValue();
    var systemType = mini.get("systemType").getValue();
    $.ajax({
        url: jsUseCtxPath + '/rdmZhgl/core/saleFileOMA/checkApplyPermition.do?designModel='
            + designModel + '&saleModel=' + saleModel
            + '&language=' + language + '&fileType=' + fileType + '&systemType=' + systemType
            + '&id=' + id,
        async: false,
        success: function (result) {
            if (!result.success) {
                mini.alert(result.message);
                checkFlag = true;
            } else {
                checkFlag =  false;
            }
        },
    });
}


