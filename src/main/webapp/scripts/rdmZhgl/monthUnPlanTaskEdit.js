$(function () {
    if(action!='add'){
        planForm.setData(applyObj);
        mini.get('id').setValue(applyObj.id);
        loadGrid();
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
    if (action == 'add') {
        planForm.setData(applyObj);
        mini.get('responseMan').setEnabled(false);
    }
    mini.get('finishStatus').setEnabled(false);
    mini.get('isDelayApply').setEnabled(false);
    mini.get('remark').setEnabled(false);
    if(permission||isLeader){
        mini.get('responseMan').setEnabled(true);
    }
    if(action=='finish'){
        planForm.setEnabled(false);
        mini.get('finishStatus').setEnabled(true);
        mini.get('remark').setEnabled(true);
        mini.get('addItem').setEnabled(false);
        mini.get('addFile').setEnabled(true);
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
})
function remarkTip(e) {
    var obj = e.selected;
    if(obj.key_== 1){
        mini.alert(monthUnPlanTaskEdit_name);
    }else if(obj.key_== 0){
        mini.alert(monthUnPlanTaskEdit_name1);
        mini.get('addFile').setEnabled(true);
    }
}
function saveData() {
    if(!isAdmin&&action == 'add'){
        //判断是否已经在审批中
        let postData = {"yearMonth":mini.get("yearMonth").getText(),"deptId":mini.get("deptId").getValue(),"type":"2","action":"addUnplan"};
        let _url = jsUseCtxPath + '/rdmZhgl/core/monthWorkApply/isRunning.do';
        let resultData = ajaxRequest(_url,'POST',false,postData);
        if(resultData&&!resultData.success&&!isAdmin){
            mini.alert("当月流程已提交,不允许新增计划外任务");
            return;
        }
    }
    if(itemGrid.getData().length==0){
        mini.alert(monthUnPlanTaskEdit_name2);
        return;
    }
    planForm.validate();
    if (!planForm.isValid()) {
        return;
    }
    var asyncStatus = mini.get('asyncStatus').getValue();
    if(asyncStatus=='1'){
        var processRate = mini.get('processRate').getValue();
        var processStatus = mini.get('processStatus').getValue();
        if(!processRate){
            mini.alert(monthUnPlanTaskEdit_name3);
            return;
        }
        if(!processStatus){
            mini.alert(monthUnPlanTaskEdit_name4);
            return;
        }
    }

    if(action=='finish'){
        //判断完成情况是否填写
        //如果填写完成情况 则备注必填
        var finishStatus = mini.get('finishStatus').getValue();
        if(finishStatus){
            var remark = mini.get('remark').getValue();
            if(!remark){
                mini.alert(monthUnPlanTaskEdit_name5);
                return;
            }
            if(finishStatus=='0'){
                var isCompanyLevel = mini.get('isCompanyLevel').getValue();
                if(isCompanyLevel=='1'){
                    if(fileListGrid.getData().length==0){
                        mini.alert(monthUnPlanTaskEdit_name6);
                        return;
                    }
                }
            }
        }else{
            mini.alert(monthUnPlanTaskEdit_name7);
            return;
        }
    }
    if(judgePlanData()){
        return;
    }

    var formData = planForm.getData();
    var config = {
        url: jsUseCtxPath+"/rdmZhgl/core/monthUnPlanTask/save.do",
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

function addItem() {
    var newRow = {name: "New Row"};
    itemGrid.addRow(newRow, 0);
    itemGrid.beginEditCell(newRow, "workContent");
}
function removeItem() {
    var rows = itemGrid.getSelecteds();
    if (rows.length > 0) {
        mini.showMessageBox({
            title: monthUnPlanTaskEdit_name8,
            iconCls: "mini-messagebox-info",
            buttons: ["ok", "cancel"],
            message: monthUnPlanTaskEdit_name9,
            callback: function (action) {
                if (action == "ok") {
                    itemGrid.removeRows(rows, false);
                }
            }
        });
    } else {
        mini.alert(monthUnPlanTaskEdit_name10);
        return;
    }
}
function removeRow() {
    var rows = [];
    rows = itemGrid.getSelecteds();
    if (rows.length <= 0) {
        mini.alert(monthUnPlanTaskEdit_name10);
        return;
    }
    mini.confirm(monthUnPlanTaskEdit_name11, monthUnPlanTaskEdit_name12, function (action) {
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
                    url: jsUseCtxPath + "/rdmZhgl/core/monthUnPlanTask/removeItem.do",
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
//验证数据
function judgePlanData() {
    var data = itemGrid.getChanges();
    var flag = false;
    if (data.length > 0) {
        for (var i = 0; i < data.length; i++) {
            if (data[i]._state == 'removed') {
                continue;
            }
            if (!data[i].workContent || !data[i].finishFlag || !data[i].responseUserId) {
                message = monthUnPlanTaskEdit_name13;
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
    var message = monthUnPlanTaskEdit_name14;
    var needReload = true;
    var mainId = mini.get('id').getValue();
    if (data.length > 0) {
        for (var i = 0; i < data.length; i++) {
            data[i].mainId = mainId;
            if (data[i]._state == 'removed') {
                continue;
            }
            if (!data[i].workContent||!data[i].finishFlag ||!data[i].responseUserId) {
                message = monthUnPlanTaskEdit_name13;
                needReload = false;
                break;
            }
        }
        if (needReload) {
            var json = mini.encode(data);
            $.ajax({
                url: jsUseCtxPath + "/rdmZhgl/core/monthUnPlanTask/dealData.do",
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
        mini.alert(monthUnPlanTaskEdit_name15);
        mini.get('isCompanyLevel').setValue(e.oldValue);
    }
}
function addWorkFile() {
    mini.open({
        title: monthUnPlanTaskEdit_name16,
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
function operationRenderer(e) {
    var record = e.record;
    var s = '';
    s += returnPreviewSpan(record.fileName, record.id, record.applyId, coverContent,"monthWorkFileUrl");
    s+='&nbsp;&nbsp;&nbsp;<span title=' + monthUnPlanTaskEdit_name17 +' style="color:#409EFF;cursor: pointer;" ' +
        'onclick="downFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">' + monthUnPlanTaskEdit_name17 +'</span>';
    if(record.CREATE_BY_ != currentUserId) {
        s+='&nbsp;&nbsp;&nbsp;<span title=' + monthUnPlanTaskEdit_name18 +' style="color:silver;">' + monthUnPlanTaskEdit_name18 +'</span>';
    } else {
        s+='&nbsp;&nbsp;&nbsp;<span title=' + monthUnPlanTaskEdit_name18 +' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="deleteFile('+JSON.stringify(record).replace(/"/g, '&quot;')+')">' + monthUnPlanTaskEdit_name18 +'</span>';
    }
    return s;
}
function deleteFile(record) {
    mini.confirm(monthUnPlanTaskEdit_name19, monthUnPlanTaskEdit_name20,
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
    projectNameInput.attr("value", mini.get("taskName").getText());
    $("body").append(form);
    form.append(applyId);
    form.append(fileUrl);
    form.append(yearMonthInput);
    form.append(projectNameInput);
    form.submit();
    form.remove();
}
