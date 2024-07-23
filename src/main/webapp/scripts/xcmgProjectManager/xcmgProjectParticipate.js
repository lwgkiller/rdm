$(function () {
    $.ajax({
        url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/getProjectEditRule.do',
        type: 'get',
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                mini.get("#projectRole").load(data.memberRole);
                mini.get("#projectLevel").load(data.level);
                mini.get("#projectCategory").load(data.category);
            }
        }
    });

    if(userRoleStr=='self') {
        $("#userName").hide();
        $("#userDepId").hide();
    } else if(userRoleStr=='department') {
        $("#userDepId").hide();
    } else if(isGY=="true") {
        $("#userDepId").hide();
    }
});
function detailProjectRow(projectId,status) {
    var action = "detail";
    var url=jsUseCtxPath+"/xcmgProjectManager/core/xcmgProject/edit.do?action="+action+"&projectId=" + projectId+"&status="+status;
    var winObj = window.open(url);
    var loop = setInterval(function() {
        if(winObj.closed) {
            clearInterval(loop);
            if(participateListGrid){
                participateListGrid.reload()
            };
        }
    }, 1000);
}

function exportProjectParticipate(){
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