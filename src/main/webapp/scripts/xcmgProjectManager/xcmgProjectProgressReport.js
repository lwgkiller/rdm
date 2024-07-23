$(function () {
    if(clickDeptId) {
        mini.get("mainDepId").setValue(clickDeptId);
        mini.get("mainDepId").setText(clickDeptName);
    }
    searchFrm();
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


function onStatusRenderer(e) {
    var record = e.record;
    var status = record.status;

    var arr = [ {'key' : 'DRAFTED','value' : '草稿','css' : 'orange'},
        {'key' : 'RUNNING','value' : '运行中','css' : 'green'},
        {'key' : 'SUCCESS_END','value' : '成功结束','css' : 'blue'},
        {'key' : 'DISCARD_END','value' : '作废','css' : 'red'},
        {'key' : 'ABORT_END','value' : '异常中止结束','css' : 'red'},
        {'key' : 'PENDING','value' : '挂起','css' : 'gray'}
    ];

    return $.formatItemValue(arr,status);
}

function exportProjectProgress(){
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