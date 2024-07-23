function searchBudgetMonth() {
    budgetSubjectListSumGrid.setData([]);
    var yearMonth = mini.get("yearMonth").getValue();
    var subjectId = mini.get("subjectMonthId").getValue();
    if(!yearMonth) {
        return;
    }
    $.ajax({
        url: jsUseCtxPath+"/xcmgBudget/core/budgetMonth/queryBudgetSubjectList.do?yearMonth="+yearMonth+"&subjectId="+subjectId,
        success: function (result) {
            if (!result.success) {
                top._ShowTips({
                    msg: result.message
                });
            } else {
                budgetSubjectListSumGrid.setData(result.data);
            }
        }
    });
}

//修改（直接跳转到详情的业务controller）
function updateBudgetMonthRow(budgetId, status) {
    var action = "update";
    var url = jsUseCtxPath + "/xcmgBudget/core/budgetMonthFlow/tabPage.do?action=" + action + "&id=" + budgetId + "&status=" + status;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if(!winObj) {
            clearInterval(loop);
        } else if (winObj.closed) {
            clearInterval(loop);
            if (budgetSubjectListSumGrid) {
                searchBudgetMonth();
            }
        }
    }, 1000);
}