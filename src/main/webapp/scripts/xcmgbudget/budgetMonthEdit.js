function saveBudgetMonth() {
    formProject.validate();
    if (!formProject.isValid()){
        return ;
    }
    var budgetMonthData=formProject.getData();
    budgetMonthData.operate=operate;
    budgetMonthData.fyMonthExpect=convert2Number(budgetMonthData.fyMonthExpect);
    var checkResult=checkIsNumber(budgetMonthData.fyMonthExpect);
    if(!checkResult) {
        mini.alert("费用预算请填写数字，无请填写0！");
        return;
    }
    budgetMonthData.zjMonthExpect=convert2Number(budgetMonthData.zjMonthExpect);
    var checkResult=checkIsNumber(budgetMonthData.zjMonthExpect);
    if(!checkResult) {
        mini.alert("资金预算请填写数字，无请填写0！");
        return;
    }
    if (budgetMonthData.fyMonthExpect != 0){
        if (budgetMonthData.fyDetail == "" || budgetMonthData.fyDetail == null){
            mini.get("fyDetail").setRequired(true);
            mini.alert("请填写费用说明！");
            return;
        }
    }
    if (budgetMonthData.zjMonthExpect !=0){
        if (budgetMonthData.zjDetail == "" || budgetMonthData.zjDetail == null){
            mini.get("zjDetail").setRequired(true);
            mini.alert("请填写资金说明！");
            return;
        }
    }

    $.ajax({
        url: jsUseCtxPath+"/xcmgBudget/core/budgetMonth/saveBudgetMonth.do",
        contentType: 'application/json',
        type:'post',
        data:mini.encode(budgetMonthData),
        success: function (data) {
            if (data) {
                mini.alert(data.message,"提示",function () {
                    CloseWindow();
                });
            }
        }
    });
}

function valuechanged(){
    var budgetId = mini.get("budgetId").getValue();
    if (budgetId == null || budgetId ==""){
        return ;
    }

    $.ajax({
        url: jsUseCtxPath + '/xcmgBudget/core/budgetMonth/getProjectListByBudgetId.do?budgetId='+budgetId,
        contentType: 'application/json',
        type:'get',
        success: function (result) {
            if (!result.success) {
                mini.alert(result.message);
            } else {
                var data = result.data[0];
                // mini.get("cwddh").setValue(data.cwddh);
                // mini.get("gbcwddh").setValue(data.gbcwddh);

                var datas=[];
                if (data.cwddh){
                    var item1={id:data.cwddh,text:data.cwddh};
                    datas.push(item1);
                }
                if (data.gbcwddh){
                    var item2={id:data.gbcwddh,text:data.gbcwddh};
                    datas.push(item2);
                }
                mini.get("glcwddh").setData(datas);
            }
        }
    });
}