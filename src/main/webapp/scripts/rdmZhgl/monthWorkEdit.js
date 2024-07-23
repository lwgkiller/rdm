$(function () {
    if(action!='add'){
        var categoryId = applyObj.categoryId;
        var url = jsUseCtxPath+"/rdmZhgl/core/monthWork/stages.do?categoryId="+categoryId
        stageCombo.setUrl(url);
        //项目名称处理
        if(permission&&applyObj.CREATE_BY_!=currentUserId){
            var url = jsUseCtxPath+"/rdmZhgl/core/monthWork/personProjectList.do?type=all"
            projectCombo.setUrl(url);
            mini.get('projectCombo').setEnabled(false);
        }
        planForm.setData(applyObj);
        mini.get('id').setValue(applyObj.id);
        loadGrid();
    }else{
        planForm.setData(applyObj);
    }
    if (action == 'view') {
        planForm.setEnabled(false);
        $('#save').hide();
        $('#addItem').hide();
        $('#removeItem').hide();
        if(applyObj.finishStatus=='0'){
            $('#fileTr').show();
            $('#addFile').hide();
        }
    }
    mini.get('finishStatus').setEnabled(false);
    mini.get('isDelayApply').setEnabled(false);
    mini.get('remark').setEnabled(false);
    if(action=='finish'){
        planForm.setEnabled(false);
        mini.get('finishStatus').setEnabled(true);
        mini.get('remark').setEnabled(true);
        mini.get('addItem').setEnabled(false);
        mini.get('removeItem').setEnabled(false);
        $('#fileTr').show();
        mini.get('addFile').setEnabled(false);
        if(applyObj.finishStatus=='0'){
            $('#fileTr').show();
            mini.get('addFile').setEnabled(true);
        }
    }
    if(isAdmin){
        mini.get("deptId").setEnabled(true);
    }else{
        mini.get("deptId").setEnabled(false);
    }
    mini.get('stageCombo').setEnabled(false);
})

function setProjectInfo(e) {
    var projectObj = e.selected;
    mini.get('projectCode').setValue(projectObj.number);
    mini.get('projectCode').setValue(projectObj.number);
    var categoryId = projectObj.categoryId;
    var currentStageId = projectObj.currentStageId;
    var planStartTime = projectObj.planStartTime;
    var planEndTime = projectObj.planEndTime;
    if(planStartTime&&planEndTime){
        var startEndDate = planStartTime+'至'+planEndTime;
        mini.get('startEndDate').setValue(startEndDate);
    }
    mini.get('responseMan').setValue(projectObj.responseMan);
    mini.get('processRate').setValue(projectObj.progressNum+'%');
    var hasRisk = projectObj.hasRisk;
    var title='项目未延误';
    if(hasRisk==1) {
        title='项目延误时间未超过5天';
    } else if(hasRisk==2) {
        title='项目延误时间超过5天';
    }else if(hasRisk==3) {
        title='项目未启动或已停止';
    }else if(hasRisk==4) {
        title='项目未填写计划时间且延误超过30天';
    }
    mini.get('processStatus').setValue(title);
    var url = jsUseCtxPath+"/rdmZhgl/core/monthWork/stages.do?categoryId="+categoryId
    stageCombo.setUrl(url);
    stageCombo.setValue(currentStageId);
}
function remarkTip(e) {
    var obj = e.selected;
    if(obj.key_== 1){
        mini.get('isDelayApply').setEnabled(true);
        mini.get('addFile').setEnabled(false);
        mini.alert("请备注说明未完成原因");
    }else if(obj.key_== 0){
        mini.alert("请备注通知单号或工作完成证明");
        mini.get('addFile').setEnabled(true);
    }
}
function saveData() {
    if(itemGrid.getData().length==0){
        mini.alert("请添加工作条目！");
        return;
    }
    planForm.validate();
    if (!planForm.isValid()) {
        return;
    }
    if(action=='finish'){
        //判断完成情况是否填写
        //如果填写完成情况 则备注必填
        var finishStatus = mini.get('finishStatus').getValue();
        if(finishStatus){
            var remark = mini.get('remark').getValue();
            if(!remark){
                mini.alert('请填写备注信息！');
                return;
            }
            if(finishStatus=='0'){
                var isCompanyLevel = mini.get('isCompanyLevel').getValue();
                if(isCompanyLevel=='1'){
                    if(fileListGrid.getData().length==0){
                        mini.alert("请上传完成情况证明材料！");
                        return;
                    }
                }
            }
        }else{
            mini.alert('请填写完成情况！');
            return;
        }
    }

    if(judgePlanData()){
        return;
    }

    var formData = planForm.getData();
    var config = {
        url: jsUseCtxPath+"/rdmZhgl/core/monthWork/save.do",
        method: 'POST',
        data: formData,
        success: function (result) {
            //如果存在自定义的函数，则回调
            var result=mini.decode(result);
            if(result.success){
                mini.get('id').setValue(result.data.id);
                saveRow();
            }else{
            };
        }
    }
    _SubmitJson(config);
}
function removeRow() {
    var rows = [];
    rows = itemGrid.getSelecteds();
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
                ids.push(r.id);
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/rdmZhgl/core/monthWork/removeItem.do",
                    method: 'POST',
                    data: {ids: ids.join(',')},
                    success: function (text) {
                        if (itemGrid) {
                            itemGrid.reload();
                        }
                    }
                });
            }

        }
    });
}
function addItem() {
    var newRow = {name: "New Row"};
    itemGrid.addRow(newRow, 0);
    itemGrid.beginEditCell(newRow, "workContent");
}
function removeItem() {
    var rows = itemGrid.getSelecteds();
    if (rows.length > 0) {
        mini.showMessageBox({
            title: "提示信息！",
            iconCls: "mini-messagebox-info",
            buttons: ["ok", "cancel"],
            message: "是否确定删除！",
            callback: function (action) {
                if (action == "ok") {
                    itemGrid.removeRows(rows, false);
                }
            }
        });
    } else {
        mini.alert("请至少选中一条记录");
        return;
    }
}
//验证数据
function judgePlanData() {
    var data = itemGrid.getChanges();
    var flag = false;
    if (data.length > 0) {
        for (var i = 0; i < data.length; i++) {
            if (data[i]._state == 'removed') {
                continue;
            }
            if (!data[i].workContent || !data[i].finishFlag || !data[i].responseUserId ) {
                message = "请填写必填项！";
                flag = true;
                mini.alert(message);
                break;
            }
        }
    }
    return flag;
}

function saveRow() {
    var data = itemGrid.getChanges();
    var message = "数据保存成功";
    var needReload = true;
    var mainId = mini.get('id').getValue();
    if (data.length > 0) {
        for (var i = 0; i < data.length; i++) {
            data[i].mainId = mainId;
            if (data[i]._state == 'removed') {
                continue;
            }
            if (!data[i].workContent||!data[i].finishFlag ||!data[i].responseUserId) {
                message = "请填写必填项！";
                needReload = false;
                break;
            }
        }
        if (needReload) {
            var json = mini.encode(data);
            $.ajax({
                url: jsUseCtxPath + "/rdmZhgl/core/monthWork/dealData.do",
                data: json,
                type: "post",
                contentType: 'application/json',
                async: false,
                success: function (text) {
                    if (text.success) {
                        loadGrid();
                    }
                }
            });
        }
    }
}
function loadGrid() {
    var mainId = mini.get('id').getValue();
    var paramArray = [{name: "mainId", value: mainId}];
    var data = {};
    data.filter = mini.encode(paramArray);
    data.mainId = mainId;
    itemGrid.load(data);
}

function onPlanLevel(e) {
    var obj = e.selected;
    var asyncStatus = mini.get('asyncStatus').getValue();
    if(asyncStatus=='1'&&obj.value=='3'){
        mini.alert("本计划级别不允许为内控级！");
        mini.get('isCompanyLevel').setValue(e.oldValue);
    }
}
function addWorkFile() {
    mini.open({
        title: "文件上传",
        url: jsUseCtxPath + "/rdmZhgl/core/monthWork/files/fileUploadWindow.do?applyId=" +  mini.get('id').getValue(),
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
function fileDown() {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/rdmZhgl/core/monthWork/fileDownload.do?action=download");
    var applyId = $("<input>");
    applyId.attr("type", "hidden");
    applyId.attr("name", "applyId");
    applyId.attr("value", mini.get("id").getValue());
    var fileUrl = $("<input>");
    fileUrl.attr("type", "hidden");
    fileUrl.attr("name", "fileUrl");
    fileUrl.attr("value", "monthWorkFileUrl");
    var yearMonthInput = $("<input>");
    yearMonthInput.attr("type", "hidden");
    yearMonthInput.attr("name", "yearMonth");
    yearMonthInput.attr("value", mini.get("yearMonth").getText());
    var projectNameInput = $("<input>");
    projectNameInput.attr("type", "hidden");
    projectNameInput.attr("name", "projectName");
    projectNameInput.attr("value", mini.get("projectCombo").getText());
    $("body").append(form);
    form.append(applyId);
    form.append(fileUrl);
    form.append(yearMonthInput);
    form.append(projectNameInput);
    form.submit();
    form.remove();
}
function operationRenderer(e) {
    var record = e.record;
    var s = '';
    s += returnPreviewSpan(record.fileName, record.id, record.applyId, coverContent,"monthWorkFileUrl");
    s+='&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
        'onclick="downFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">下载</span>';
    if(record.CREATE_BY_ != currentUserId) {
        s+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:silver;">删除</span>';
    } else {
        s+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="deleteFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
    }
    return s;
}
function deleteFile(record) {
    mini.confirm("确定删除？", "确定？",
        function (action) {
            if (action == "ok") {
                var url = jsUseCtxPath + "/rdmZhgl/core/monthWork/files/delFile.do";
                var data = {
                    applyId: record.applyId,
                    id: record.id,
                    fileName: record.fileName
                };
                $.post(
                    url,
                    data,
                    function (json) {
                        if (fileListGrid) {
                            fileListGrid.load();
                        }
                    });
            }
        }
    );
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
    detailId.attr("value", record.applyId);
    var fileId = $("<input>");
    fileId.attr("type", "hidden");
    fileId.attr("name", "fileId");
    fileId.attr("value", record.id);
    var fileUrl = $("<input>");
    fileUrl.attr("type", "hidden");
    fileUrl.attr("name", "fileUrl");
    fileUrl.attr("value", "monthWorkFileUrl");
    $("body").append(form);
    form.append(inputFileName);
    form.append(detailId);
    form.append(fileId);
    form.append(fileUrl);
    form.submit();
    form.remove();
}
