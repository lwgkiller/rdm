$(function () {
    var nowDate = new Date();
    var beginDate = nowDate.getFullYear() + "-" + (nowDate.getMonth()+1) + "-01";
    var day = new Date(nowDate.getFullYear(), nowDate.getMonth()+1, 0);
    var endDate = nowDate.getFullYear() + "-" + (nowDate.getMonth()+1) + "-" + day.getDate();
    var startTime = mini.get("startTime");
    startTime.setValue(beginDate);
    var endTime = mini.get("endTime");
    endTime.setValue(endDate);

    var projectStartTime = mini.get("projectStartTime");
    projectStartTime.setValue(beginDate);
    var projectEndTime = mini.get("projectEndTime");
    projectEndTime.setValue(endDate);
    $.ajax({
        url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/getProjectEditRule.do',
        type: 'get',
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                mini.get("#projectCategory").load(data.category);
            }
        }
    });
    searchFrm();
});

//导出
function exportBtn(){
    var params=[];
    var parent=$(".search-form");
    var inputAry=$("input",parent);
    inputAry.each(function(i){
        var el=$(this);
        var obj={};
        obj.name=el.attr("name");
        if(!obj.name) return true;
        obj.value=el.val();
        params.push(obj);
    });
    $("#filter").val(mini.encode(params));
    var excelForm = $("#excelForm");
    excelForm.submit();
}

function showProjectScores(userId) {
    var startTimeVal=mini.get("startTime").getText();
    if(!startTimeVal) {
        startTimeVal='';
    }
    var endTimeVal=mini.get("endTime").getText();
    if(!endTimeVal) {
        endTimeVal='';
    }
    var projectEndTimeVal=mini.get("projectEndTime").getText();
    if(!projectEndTimeVal) {
        projectEndTimeVal='';
    }
    var projectStartTimeVal=mini.get("projectStartTime").getText();
    if(!projectStartTimeVal) {
        projectStartTimeVal='';
    }
    var categoryId=mini.get("projectCategory").getValue();
    if(!categoryId) {
        categoryId='';
    }
    mini.open({
        title: xcmgProjectPersonScore_name,
        url: jsUseCtxPath + "/xcmgProjectManager/core/xcmgScore/projectScoreShow.do?userId="+userId+"&startTime="+startTimeVal+"&endTime="+endTimeVal
            +"&projectStartTime="+projectStartTimeVal+"&projectEndTime="+projectEndTimeVal+"&categoryId="+categoryId,
        width: 850,
        height: 350,
        allowResize: true,
        onload: function () {

        }
    });
}
