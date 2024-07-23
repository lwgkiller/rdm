var LJ_STATUS = "";
var completeStatus = [{'key': '是', 'value': 'yes'}, {'key': '否', 'value': 'no'}];

$(function () {
    var url = jsUseCtxPath + "/serviceEngineering/core/partsAtlasFileDownloadApply/getJson.do";
    $.post(
        url,
        {id: applyId},
        function (json) {
            partsAtlasFileDownloadForm.setData(json);
        });
    if (action == 'detail') {
        mini.get("demandListToolBar").hide();
        $("#detailToolBar").show();
        if (status != 'DRAFTED') {
            $("#processInfo").show();
        }
        if (status == "SUCCESS_END") {
            demandGrid.showColumn("op");
            demandGrid.showColumn("selectid");
            if (isTCGL) {
                //zwt
                mini.get("complete").setEnabled(true);
                $("#saveInfo").show();
            }
        }

    } else if (action == 'task') {
        mini.get("demandListToolBar").hide();
        demandGrid.setAllowCellEdit(false);

        taskActionProcess();
    }
});


function getPartsAtlasId() {
    //打开选择窗口，选择后插入本表中
    selectPartsAtlasFileWindow.show();

    // 初始化搜索参数车号+语言
    var row = demandGrid.getSelected();
    var vinCode = row.vinCode;
    var languageType = row.languageType;
    var fileType = row.fileType;
    mini.get("vinCode").setValue(vinCode);
    mini.get("languageType").setValue(languageType);
    mini.get("fileType").setValue(fileType);
    searchPartsAtlas();
}

function clearButtonEdit(e) {
    var obj = e.sender;
    obj.setValue("");
    obj.setText("");
    var objrow = demandGrid.getSelected();
    demandGrid.updateRow(objrow, {partsAtlasId: '', partsAtlasName: ''});

}

function selectAtlasOK() {
    var objrow = demandGrid.getSelected();
    var rows = partsAtlasGrid.getSelected();
    if (rows) {
        demandGrid.updateRow(objrow, {partsAtlasId: rows.id, partsAtlasName: rows.partsAtlasName,fileName:rows.fileName});
    } else {
        mini.alert("请选择一条数据！");
        return
    }
    selectPartsAtlasFileWindow.hide();


}

function searchBoxClear() {
    mini.get("vinCode").setValue("");
    mini.get("languageType").setValue("");
    mini.get("salesModel").setValue("");
    mini.get("designModel").setValue("");
    mini.get("partsAtlasName").setValue("");
    mini.get("fileType").setValue("");

}

function selectAtlasCancel() {
    searchBoxClear();
    selectPartsAtlasFileWindow.hide();
}

function delDataRow() {
    var selecteds = demandGrid.getSelecteds();
    demandGrid.removeRows(selecteds);
}

function getData() {
    var formData = _GetFormJsonMini("partsAtlasFileDownloadForm");
    if (formData.SUB_fileListGrid) {
        delete formData.SUB_fileListGrid;
    }
    if (formData.SUB_demandGrid) {
        delete formData.SUB_demandGrid;
    }
    if (demandGrid.getChanges().length > 0) {
        formData.changeDemandGrid = demandGrid.getChanges();
    }
    return formData;
}

//保存草稿
function saveDraft(e) {
    window.parent.saveDraft(e);
}

//发起流程
function startProcess(e) {
    //@mh 必填字段验证 整机编号 图册语言
    var formValid = validApply();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.startProcess(e);
}

//下一步审批
function applyApprove() {
    //编制阶段及关联零件图册的下一步需要校验表单必填字段
    if (LJ_STATUS == '5' || LJ_STATUS == "1") {
        var formValid = validApply();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        if(LJ_STATUS == '5'){
            var complete = mini.get("complete").getValue();
            if(!complete) {
                mini.alert("请选择“是否完备”！");
                return;
            }
        }
    }

    //检查通过
    window.parent.approve();
}

function saveInDetail() {
    var complete = mini.get("complete").getValue();
    if(!complete) {
        mini.alert("请选择“是否完备”！");
        return;
    }
    saveInProcess();
}

//暂存信息
function saveInProcess() {
    var formData = getData();

    var json = mini.encode(formData);
    $.ajax({
        url: jsUseCtxPath + '/serviceEngineering/core/partsAtlasFileDownloadApply/saveInProcess.do',
        type: 'post',
        async: false,
        data: json,
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                var message = "";
                if (data.success) {
                    window.location.reload();
                } else {
                    mini.alert("数据保存失败" + data.message);
                }
            }
        }
    });
}

function validApply() {
    var demandGridData = demandGrid.getData();
    if (demandGridData.length == 0) {
        mini.alert("请填写零件图册信息");
        return;
    }
    for (var i = 0; i < demandGridData.length; i++) {
        if (demandGridData[i].vinCode == undefined || demandGridData[i].vinCode == "") {
            return {"result": false, "message": "请填写整机编号"};
        }
        if (demandGridData[i].languageType == undefined || demandGridData[i].languageType == "") {
            return {"result": false, "message": "请选择图册语言"};
        }
        if (demandGridData[i].fileType == undefined || demandGridData[i].fileType == "") {
            return {"result": false, "message": "请选择文件类型"};
        }
    }
    return {"result": true};
}

function processInfoM() {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: "流程图实例",
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

        if (nodeVars[i].KEY_ == 'LJ_STATUS') {
            LJ_STATUS = nodeVars[i].DEF_VAL_;
        }
    }
    //上传零件图册
    if (LJ_STATUS == '5') {
        // demandGrid.setEnabled(true);
        demandGrid.setAllowCellEdit(true);
        demandGrid.showColumn("op");
        demandGrid.showColumn("selectid");
        //zwt
        mini.get("complete").enable(true);

    }
    if (LJ_STATUS == '1') {
        mini.get("demandListToolBar").show();
        demandGrid.setAllowCellEdit(true);


    }

}

//搜索零件图册库
function searchPartsAtlas() {

    var paramArray = [];
    paramArray.push({name: "salesModel", value: mini.get('salesModel').getValue()});
    paramArray.push({name: "designModel", value: mini.get('designModel').getValue()});
    paramArray.push({name: "vinCode", value: mini.get('vinCode').getValue()});
    paramArray.push({name: "partsAtlasName", value: mini.get('partsAtlasName').getValue()});
    paramArray.push({name: "languageType", value: mini.get('languageType').getValue()});
    paramArray.push({name: "fileType", value: mini.get('fileType').getValue()});
    var data = {};
    data.filter = mini.encode(paramArray);
    partsAtlasGrid.load(data);
}

function clearSearch() {
    searchBoxClear();
    var paramArray = [];
    paramArray.push({name: "salesModel", value: ""});
    paramArray.push({name: "designModel", value: ""});
    paramArray.push({name: "vinCode", value: ""});
    paramArray.push({name: "partsAtlasName", value: ""});
    paramArray.push({name: "languageType", value: ""});
    paramArray.push({name: "fileType", value: ""});
    var data = {};
    data.filter = mini.encode(paramArray);
    partsAtlasGrid.load(data);
}

function addDataRow() {
    var row = {"text": "new context"}
    demandGrid.addRow(row, 0);
    demandGrid.beginEditCell(row, "TextName");


}

//控制列编辑权限
function OnCellBeginEdit(e) {
    var field = e.field;
    // 非申请编制阶段，将部分字段设不可编辑
    if (action == "task" && LJ_STATUS != 1) {
        if (field == "vinCode" || field == "languageType" || field == "salesModel" || field == "fileType" || field == "applyReason" || field == "customerInfo") {
            e.cancel = true;
        }
    }
    // 編輯关闭权限，如果是零件图册上传人员，可以编辑
    if (action == 'detail') {
        if (field == "vinCode" || field == "languageType" || field == "salesModel" || field == "fileType" || field == "applyReason" || field == "customerInfo" || field == "partsAtlasId") {
            e.cancel = true;
        }
        if (isTCGL && e.field == "partsAtlasId") {
            e.cancel = false;
        }

    }


}