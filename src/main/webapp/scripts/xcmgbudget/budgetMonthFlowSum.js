$(function () {
    mini.get("yearMonth").setText(yearMonth);
    searchBudgetMonth();
});

function searchBudgetMonth() {
    budgetMonthListSumGrid.setData([]);
    var yearMonth = mini.get("yearMonth").getText();
    if(!yearMonth) {
        return;
    }
    $.ajax({
        url: jsUseCtxPath+"/xcmgBudget/core/budgetMonth/querySumList.do?yearMonth="+yearMonth,
        success: function (result) {
            if (!result.success) {
                top._ShowTips({
                    msg: result.message
                });
            } else {
                budgetMonthListSumGrid.setData(result.data);
            }
        }
    });
}

function editBudgetMonth(record) {
    if(!record) {
        return;
    }

    var yearMonth = mini.get("yearMonth").getText();
    // var subjectId = mini.get("yearMonth").getText();

    mini.open({
        title: "月度预算明细查看",
        url: jsUseCtxPath + "/xcmgBudget/core/budgetMonth/editSumPage.do",
        width: 1400,
        height: 650,
        showModal: true,
        allowResize: true,
        onload: function () {
            var iframe = this.getIFrameEl();
            record.subjectMonthId=record.id;
            record.id=record.mid;
            record.yearMonth=yearMonth;
            record.budgetId=budgetId;
            record.budgetType=budgetType;
            var data = { recordKey: record };  //传递上传参数
            iframe.contentWindow.setData(data);
        },
        ondestroy: function (action) {
            searchBudgetMonth();
        }
    });
}

function resetBtn() {
    mini.get("yearMonth").setText();
    budgetMonthListSumGrid.setData([]);
}

function submitToEPM(){
    var yearMonth = mini.get("yearMonth").getText();
    if(!yearMonth) {
        mini.alert("请选择申报月份！");
        return;
    }

    //系统信息校验，当月还有正在进行的流程时。提示
    $.ajax({
        url: jsUseCtxPath+"/xcmgBudget/core/budgetMonthFlow/judgeBudgetMonthStatus.do?yearMonth="+yearMonth,
        success: function (result) {
            if (!result.success) {
               mini.confirm(yearMonth+"仍有"+result.total+"笔审批中的预算申请金额，是否继续？","提示",function(action){
                   if(action!='ok'){
                       return;
                   }else {
                       submitToEPMProcess();
                   }
               });
            }else {
                submitToEPMProcess();
            }
        }
    });

}

function submitToEPMProcess(){
    var yearMonth = mini.get("yearMonth").getText();
    $.ajax({
        url: epm_url+"xcmgBudget/core/budgetMonthRDM/judgeBudgetStatus.do?flowYearMonth="+yearMonth,
        success: function (result) {
            if (!result.success) {
                mini.alert(result.message);
            } else {
                if (result.data == 'add'){
                    mini.confirm("确定提交"+yearMonth+"月度预算信息？","提示",function(action){
                        if(action!='ok'){
                            return;
                        }else{
                            sumitDataToEPM('add');
                        }
                    });
                }
                if (result.data == 'update'){
                    mini.confirm(yearMonth+"月度预算信息已存在,会覆盖当前信息，是否继续？","提示",function(action){
                        if(action!='ok'){
                            return;
                        }else{
                            sumitDataToEPM('update');
                        }
                    });
                }

            }
        }
    });
}

function sumitDataToEPM(action){
    var yearMonth = mini.get("yearMonth").getText();
    var dataGrid = budgetMonthListSumGrid.getData();
    // var json =mini.encode(dataGrid);
    $.ajax({
        url: epm_url+"xcmgBudget/core/budgetMonthRDM/createOrUpdateBudget.do",
        type: 'post',
        async: false,
        data: mini.encode({dataList: dataGrid, yearMonth: yearMonth,action: action}),
        // data: mini.encode(dataGrid),
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                if(data.success) {
                    mini.alert(data.message);
                } else {
                    mini.alert(data.message);
                }
            }
        }
    });
}





