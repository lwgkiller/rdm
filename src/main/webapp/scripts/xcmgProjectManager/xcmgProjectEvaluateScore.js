$(function () {
    if(clickDeptId) {
    //    总览跳转
        mini.get("mainDepId").setValue(clickDeptId);
        mini.get("mainDepId").setText(clickDeptName);
        mini.get("projectStartTime").setValue(projectStartTime);
        mini.get("projectEndTime").setValue(projectEndTime);
    } else{
        var nowYear=new Date().getFullYear();
        mini.get("evaluateScoreStartTime").setValue(nowYear+"-01-01");
        mini.get("evaluateScoreEndTime").setValue(nowYear+"-12-31");
    }
    $.ajax({
        url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/getProjectEditRule.do',
        type: 'get',
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                mini.get("#projectCategory").load(data.category);
                mini.get("#projectLevel").load(data.level);
            }
        }
    });
    searchFrm();
});

//明细
function detailProjectRow(projectId,status) {
    var action = "detail";
    var url=jsUseCtxPath+"/xcmgProjectManager/core/xcmgProject/edit.do?action="+action+"&projectId=" + projectId+"&status="+status;
    var winObj = window.open(url);
    var loop = setInterval(function() {
        if(winObj.closed) {
            clearInterval(loop);
            if(progressListGrid){
                progressListGrid.reload()
            };
        }
    }, 1000);
}

function exportEvaluateScore(){
    var parent=$(".search-form");
    var inputAry=$("input",parent);
    var params=[];
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