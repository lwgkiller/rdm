$(function () {
    searchBudgetMonth();
    if (action == "baoxiao"){
        getBxStatus();
        $("#bxStatus").attr("style","display:''");
    }
    if (budgetType=='fxml'){
        // mini.get("glcwddh")
        budgetMonthListGrid.hideColumn("glcwddh");
    }
});

function searchBudgetMonth() {
    resetBtn();
    $.ajax({
        url: jsUseCtxPath+"/xcmgBudget/core/budgetMonth/queryList.do?budgetType="+budgetType+"&budgetId="+budgetId,
        success: function (result) {
            if (!result.success) {
                top._ShowTips({
                    msg: result.message
                });
            } else {
                budgetMonthListGrid.setData(result.data);
            }
        }
    });
}

function editBudgetMonth(record,act) {
    if(!record || !act) {
        return;
    }
    var operate ='';
    if (act=='edit'){
        operate ='edit';
    }
    if (act=='copy'){
        operate ='copy';
    }
    if (act=='del'){
        var id = record.mid;
        if (typeof(id) == "undefined")
        {
            id = "";
        }
        mini.confirm("是否删除该条预算信息？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                //删除行数据
                delBudgetRow(id);
            }
        });
        return;
    }
    if (budgetType=='xml' && projectId==""){
        mini.alert("请先选择关联项目！");
        return;
    }
    // if (!record.cwddh && !)

    mini.open({
        title: "月度预算编辑",
        url: jsUseCtxPath + "/xcmgBudget/core/budgetMonth/editPage.do",
        width: 900,
        height: 370,
        showModal: true,
        allowResize: true,
        onload: function () {
            var iframe = this.getIFrameEl();
            record.subjectMonthId=record.id;
            record.id=record.mid;
            record.deptId=deptId;
            record.deptName=deptName;
            record.yearMonth=yearMonth;
            record.bigTypeId=bigTypeId;
            record.bigTypeName=bigTypeName;
            record.budgetId=budgetId;
            record.budgetType=budgetType;
            record.operate=operate;
            var data = { recordKey: record };  //传递上传参数
            iframe.contentWindow.setData(data);
        },
        ondestroy: function (action) {
            searchBudgetMonth();
        }
    });
}

function resetBtn() {
    budgetMonthListGrid.setData([]);
}

function checkIsNumber(value) {
    if(!value) {
        return false;
    }
    var parseValue=parseFloat(value);
    if(parseValue!=value) {
        return false;
    }
    return true;
}

function OnCellBeginEditDetail(e) {
    var record = e.record, field = e.field;
    if (field == "rate" && (record.zjOrFy=='zj' ||!record.zjOrFy )) {
        e.cancel = true;
    }
}

function showBudgetMonthProcess(subjectCode,subjectId,monthFyExpect,monthZjExpect) {
    var statusName=$("#statusName").html();
    if(!statusName || statusName!='批复通过') {
        mini.alert("当前状态不是“批复通过”，不能操作！");
        return;
    }
    var date = new Date();
    var ym = date.format('yyyy-MM');

    if (!(ym == yearMonth)){
        mini.alert("仅当月预算可以提报费用报销！");
        return;
    }
    if (subjectCode == '5300000013'){
        mini.alert("差旅费无需提报费用报销！");
        return;
    }
    mini.get("yearMonthFilter").setValue(yearMonth);
    mini.get("subjectIdFilter").setValue(subjectId);
    mini.get("budgetIdFilter").setValue(budgetId);
    mini.get("monthFyExpect").setValue(monthFyExpect);
    mini.get("monthZjExpect").setValue(monthZjExpect);
    searchProcessData();
    budgetMonthProcessWindow.show();
}

function detailBudgetMonthProcess(subjectCode,subjectId,monthFyExpect,monthZjExpect) {
    if (subjectCode == '5300000013'){
        mini.alert("差旅费无需提报费用报销！");
        return;
    }
    mini.get("yearMonthFilter").setValue(yearMonth);
    mini.get("subjectIdFilter").setValue(subjectId);
    mini.get("budgetIdFilter").setValue(budgetId);
    mini.get("monthFyExpect").setValue(monthFyExpect);
    mini.get("monthZjExpect").setValue(monthZjExpect);
    searchProcessData();
    budgetMonthProcessWindow.show();
    processListGrid.disable();
    mini.get("addProcessData").disable();
    mini.get("rmProcessData").disable();
    mini.get("saveProcessData").disable();
}

function closeProcessData() {
    budgetMonthProcessWindow.hide();
    mini.get("subjectIdFilter").setValue('');
    mini.get("monthZjExpect").setValue('');
    mini.get("monthFyExpect").setValue('');
    mini.get("yearMonthFilter").setValue('');
    mini.get("subjectIdFilter").setValue('');
    mini.get("moneyStatus").setValue();
    mini.get("moneyStatus").setText();
    processListGrid.enable();
    mini.get("addProcessData").enable();
    mini.get("rmProcessData").enable();
    mini.get("saveProcessData").enable();
}

function cleanProcessData() {
    mini.get("moneyStatus").setValue();
    mini.get("moneyStatus").setText();
    searchProcessData();
}

function getBxStatus(){
    $.ajax({
        url: epm_url+"xcmgBudget/core/budgetMonthRDM/judgeBxStatus.do?flowYearMonth="+yearMonth,
        success: function (result) {
            if (!result.success) {
                if (result.data == "success"){
                    $("#deptCurrentStatusName").html("<span id='statusName' style='font-size: 17px' class='blue'>"+"预算审批中"+"</span>");
                }else {
                    $("#deptCurrentStatusName").html("<span id='statusName' style='font-size: 17px' class='blue'>"+"系统异常"+"</span>");
                }
            } else {
                $("#deptCurrentStatusName").html("<span id='statusName' style='font-size: 17px' class='blue'>"+"批复通过"+"</span>");
            }
        }
    });
}

function searchProcessData() {
    var postData={};
    postData.yearMonth=mini.get("yearMonthFilter").getValue();
    postData.subjectId=mini.get("subjectIdFilter").getValue();
    postData.budgetId=mini.get("budgetIdFilter").getValue();
    postData.zjOrFy=mini.get("moneyStatus").getValue();
    $.ajax({
        url: jsUseCtxPath + '/xcmgBudget/core/budgetMonth/queryMonthProcess.do',
        contentType: 'application/json',
        type:'post',
        data:mini.encode(postData),
        success: function (result) {
            if (!result.success) {
                mini.alert(result.message);
            } else {
                processListGrid.setData(result.data);
            }
        }
    });
}

//新增
function addProcessData() {
    var row={rate:"1.0",hasConfirm:'未确认'};
    processListGrid.addRow(row);
}
//移除
function rmProcessData() {
    var selecteds = processListGrid.getSelecteds();
    var cannotDelete=false;
    for(var index=0;index<selecteds.length;index++) {
        if(selecteds[index].hasConfirm && selecteds[index].hasConfirm=='已确认') {
            cannotDelete=true;
        } else {
            processListGrid.removeRow(selecteds[index]);
        }
    }
    if(cannotDelete) {
        mini.alert("已确认数据不允许删除！");
    }
}

//保存
function saveProcessData() {
    var changes=processListGrid.getChanges();
    if(!changes || changes.length==0) {
        mini.alert("没有需要保存的数据！");
        return;
    }
    for(var index=0;index<changes.length;index++) {
        if(changes[index]._state=="removed") {
            continue;
        }
        if(!changes[index].zjOrFy) {
            mini.alert("金额类型不能为空！");
            return;
        }
        if(!changes[index].moneyNumBeforeRate) {
            mini.alert("税前金额不能为空！");
            return;
        }
        if(!checkIsNumber(changes[index].moneyNumBeforeRate)) {
            mini.alert("税前金额请填写数字！");
            return;
        }
        if(!changes[index].rate) {
            mini.alert("税率不能为空！");
            return;
        }
        if(!checkIsNumber(changes[index].rate)) {
            mini.alert("税率请填写数字！");
            return;
        }
        if(changes[index].rate<1 || changes[index].rate>=2){
            mini.alert("税率填写有误！");
            return;
        }
        if(!changes[index].moneyDesc) {
            mini.alert("金额描述不能为空！");
            return;
        }
        if(!changes[index].moneyNum) {
            changes[index].moneyNum=(parseFloat(changes[index].moneyNumBeforeRate)/parseFloat(changes[index].rate)).toFixed(2);
        }
        if(changes[index].moneyNumBeforeRate) {
            changes[index].moneyNumBeforeRate=parseFloat(changes[index].moneyNumBeforeRate).toFixed(2);
        }
    }
    var totalData=processListGrid.getData();
    if(totalData && totalData.length>0) {
        //检查税后金额总和是否超过预算1%
        //同科目类资金费用做累加，用于单科目多条目的报销逻辑
        var monthZjExpect=0;
        var monthFyExpect=0;
        var listData = budgetMonthListGrid.getData();
        var subjectId = mini.get("subjectIdFilter").getValue();
        if(listData && listData.length>0) {
            for(var index=0;index<listData.length;index++) {
                if (listData[index].id == subjectId){
                    monthZjExpect += listData[index].zjMonthExpect;
                    monthFyExpect += listData[index].fyMonthExpect;
                }
            }
        }
        // var monthZjExpect=mini.get("monthZjExpect").getValue();
        // var monthFyExpect=mini.get("monthFyExpect").getValue();
        var totalZjMoneyNum=0.0;
        var totalFyMoneyNum=0.0;
        for(var index=0;index<totalData.length;index++) {
            if(totalData[index].zjOrFy=='zj'){
                totalZjMoneyNum+=parseFloat(totalData[index].moneyNumBeforeRate);
            }else if(totalData[index].zjOrFy=='fy'){
                totalFyMoneyNum+=parseFloat(totalData[index].moneyNum);
            }else if(totalData[index].zjOrFy=='dh'){
                totalFyMoneyNum+=parseFloat(totalData[index].moneyNum);
                totalZjMoneyNum+=parseFloat(totalData[index].moneyNumBeforeRate);
            }
        }
        var outFyPercent=((totalFyMoneyNum-monthFyExpect)*100/monthFyExpect).toFixed(2);
        var outZjPercent=((totalZjMoneyNum-monthZjExpect)*100/monthZjExpect).toFixed(2);
        if(totalZjMoneyNum>monthZjExpect && outZjPercent>0) {
            if(isYsglry) {
                // mini.confirm("税前金额总和超出资金预算"+outZjPercent+"%，确定保存？", "提示", function (action) {
                mini.confirm("税前金额总和超出资金预算，确定保存？", "提示", function (action) {
                    if (action != 'ok') {
                        return;
                    } else {
                        //保存
                        judgeSaveProcessData(changes);
                    }
                });
            } else {
                mini.alert("税前金额总和超出资金预算，请修改后保存或联系技术管理部操作！");
            }
        }else if(totalFyMoneyNum>monthFyExpect && outFyPercent>0) {
            if(isYsglry) {
                // mini.confirm("税后金额总和超出费用预算"+outFyPercent+"%，确定保存？", "提示", function (action) {
                mini.confirm("税后金额总和超出费用预算，确定保存？", "提示", function (action) {
                    if (action != 'ok') {
                        return;
                    } else {
                        //保存
                        judgeSaveProcessData(changes);
                    }
                });
            } else {
                mini.alert("税后金额总和超出费用预算，请修改后保存或联系技术管理部操作！");
            }
        } else {
            //保存
            judgeSaveProcessData(changes);
        }
    } else {
        //保存
        judgeSaveProcessData(changes);
    }
}

//直接访问EPM的后端，查询检验EPM中已盖章的数据，是否能保存
function judgeSaveProcessData(changes){
    var postData={};
    postData.data=changes;
    $.ajax({
        url: epm_url+'xcmgBudget/core/budgetMonthRDM/checkMonthProcess.do',
        contentType: 'application/json',
        type:'post',
        data:mini.encode(postData),
        success: function (result) {
            if (!result.success){
                mini.alert("含有已盖章状态金额，请修改后保存或联系经营管理部操作！");
                searchProcessData();
            }else {
                doSaveProcessData(result.data);
            }
        }
    });
}

//数据保存至RPM，后存入EPM
function doSaveProcessData(changes) {
    var postData={};
    postData.data=changes;
    postData.budgetId=mini.get("budgetIdFilter").getValue();
    postData.yearMonth=mini.get("yearMonthFilter").getValue();
    postData.subjectId=mini.get("subjectIdFilter").getValue();
    $.ajax({
        url: jsUseCtxPath + '/xcmgBudget/core/budgetMonth/saveMonthProcess.do',
        contentType: 'application/json',
        type:'post',
        data:mini.encode(postData),
        success: function (result) {
            doSaveProcessDataToEPM(result.data);
        }
    });
}
//数据同步至EPM
function doSaveProcessDataToEPM(changes) {
    var postData={};
    postData.data=changes;
    postData.yearMonth=mini.get("yearMonthFilter").getValue();
    postData.subjectId=mini.get("subjectIdFilter").getValue();
    $.ajax({
        url: epm_url+'xcmgBudget/core/budgetMonthRDM/saveMonthProcess.do',
        contentType: 'application/json',
        type:'post',
        data:mini.encode(postData),
        success: function (result) {
            mini.alert(result.message);
            searchProcessData();
        }
    });
}

function addBudgetRow(record){
    var rows = [];
    if (record) {
        rows.push(record);
    }
    budgetMonthListGrid.addRow(rows);
}

function delBudgetRow(id){
    //项目类型发生改变，删除全部项目相关的财务数据
    $.ajax({
        url: jsUseCtxPath + '/xcmgBudget/core/budgetMonthFlow/deleteBudgetMonthById.do?id='+id,
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (result) {
            if (!result.success){
                mini.alert(result.message);
            }else {
                mini.alert(result.message);
                searchBudgetMonth();
            }
        }
    });
}