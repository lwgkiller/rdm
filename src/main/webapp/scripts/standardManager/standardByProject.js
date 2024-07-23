$(function (){
    searchFrm();
});

//清理查询条件
function clearGroupByProjectSearch(){
    mini.get("standardNumber").setValue("");
    mini.get("standardName").setValue("");
    mini.get("ProjectName").setValue("");
    mini.get("ProjectNumber").setValue("");
    mini.get("depName").setValue("");
    mini.get("respMan").setValue("");
    searchFrm();
}

//导出
function exportProjectReport(){
    var queryParam = [];
    var standardNumber = $.trim(mini.get("standardNumber").getValue());
    if (standardNumber) {
        queryParam.push({name: "standardNumber", value: standardNumber});
    }
    var standardName = $.trim(mini.get("standardName").getValue());
    if (standardName) {
        queryParam.push({name: "standardName", value: standardName});
    }
    var ProjectName = $.trim(mini.get("ProjectName").getValue());
    if (ProjectName) {
        queryParam.push({name: "ProjectName", value: ProjectName});
    }
    var ProjectNumber = $.trim(mini.get("ProjectNumber").getValue());
    if (ProjectNumber) {
        queryParam.push({name: "ProjectNumber", value: ProjectNumber});
    }
    var depName = $.trim(mini.get("depName").getValue());
    if (depName) {
        queryParam.push({name: "depName", value: depName});
    }
    var respMan = $.trim(mini.get("resMan").getValue());
    if (respMan) {
        queryParam.push({name: "respMan", value: respMan});
    }

    $("#filter").val(mini.encode(queryParam));
    var excelForm = $("#excelForm");
    excelForm.submit();
}