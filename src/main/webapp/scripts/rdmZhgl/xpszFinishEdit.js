var editForm = '';
var isFirstNode = false;
var tableName = 'xpsz_finishapply';
var fileUrl = 'commonFileUrl';
$(function () {
    if (action == 'task') {
        bpmPreTaskTipInForm();
        getProcessNodeVars();
        if (editForm != '1' && !isFirstNode) {
            productApplyForm.setEnabled(false);
            mini.get('addFile').setEnabled(false);
        }
        productApplyForm.setData(ApplyObj);
        searchList()
    } else if (action == 'detail') {
        $("#detailToolBar").show();
        productApplyForm.setEnabled(false);
        productApplyForm.setData(ApplyObj);
        mini.get('addFile').setEnabled(false);
        searchList()
    } else if (action == 'edit') {
        productApplyForm.setData(ApplyObj);
        searchList()
    }
    var item = mini.get('item').getValue();
    if(item=='jblczt_date'){
        $('#produceNoticeTr').show()
    }else{
        $('#produceNoticeTr').hide()
    }
});

function searchList() {
    loadProductGrid();
}

function loadProductGrid() {
    if(ApplyObj.mainId){
        var paramArray = [{name: "mainIds", value: ApplyObj.mainId}];
        var data = {};
        data.filter = mini.encode(paramArray);
        productGrid.load(data);
    }
}

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
    var formData = _GetFormJsonMini("productApplyForm");
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos = [];
    formData.vars = [];
    return formData;
}

//保存草稿或提交时数据是否有效
function draftOrStartValid() {
    //根据部门id和月份判断是否已经提交过审批流程
    var finishDate = mini.get("finishDate").getValue()
    if (!finishDate) {
        return {"result": false, "message": "请选择完成日期"};
    }
    var item = mini.get('item').getValue();
    if(item=='jblczt_date'){
        var produceNotice = mini.get("produceNotice").getValue()
        if (!produceNotice) {
            return {"result": false, "message": "请填写量产通知编号！"};
        }
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
    if(fileListGrid.getData().length<1){
        mini.alert("请上传证明材料！");
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
function addFile() {
    var mainId = mini.get("id").getValue();
    if (!mainId) {
        mini.alert('请先点击‘保存草稿’进行表单的保存！');
        return;
    }
    mini.open({
        title: "文件上传",
        url: jsUseCtxPath + "/common/core/file/fileUploadWindow.do?mainId=" + mainId+"&tableName="+tableName,
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
function operationRendererSq(e) {
    var record = e.record;
    var cellHtml = '';
    cellHtml = returnPreviewSpan(record.fileName, record.id, record.mainId, coverContent, fileUrl,tableName);
    cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
        'onclick="downCommonFile(\'' + record.fileName + '\',\'' + record.mainId + '\',\'' + record.id + '\',\''+fileUrl+'\',\''+tableName+'\')">下载</span>';
    //增加删除按钮
    if (action == 'edit' || (action == 'task' && isFirstNode)) {
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="deleteCommonFile(\'' + record.mainId + '\',\'' + record.id + '\',\'' + record.fileName + '\',\''+fileUrl+'\',\''+tableName+'\')">删除</span>';
    }
    return cellHtml;
}

