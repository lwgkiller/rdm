//明细
function zgzlDetail(gjzlId) {
    var action = "detail";
    var url = gjzlPath + "/zhgl/core/zlgl/gjzlPage.do?gjzlId=" + gjzlId+ "&action=" + action ;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (gjzlListGrid) {
                gjzlListGrid.reload()
            }
        }
    }, 1000);
}
//编辑
function zgzlkEdit(gjzlId) {
    var action = "edit";
    var url = gjzlPath + "/zhgl/core/zlgl/gjzlPage.do?gjzlId=" + gjzlId+ "&action=" + action ;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (gjzlListGrid) {
                gjzlListGrid.reload()
            }
        }
    }, 1000);
}
//新增
function addGjzl() {
    var url = gjzlPath + "/zhgl/core/zlgl/gjzlPage.do";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (gjzlListGrid) {
                gjzlListGrid.reload()
            }
        }
    }, 1000);
}
function removeGjzl(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = gjzlListGrid.getSelecteds();
    }
    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }
    mini.confirm("确定删除选中记录？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var rowIds = [];
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                rowIds.push(r.gjzlId);
            }

            _SubmitJson({
                url: gjzlPath + "/zhgl/core/zlgl/deleteGjzl.do",
                method: 'POST',
                showMsg:false,
                data: {ids: rowIds.join(',')},
                success: function (data) {
                    if (data) {
                        mini.alert(data.message);
                        searchFrm();
                    }
                }
            });
        }
    });
}
function exportGjzl() {
    var parent = $(".search-form");
    var inputAry = $("input", parent);
    var params = [];
    inputAry.each(function (i) {
        var el = $(this);
        var obj = {};
        obj.name = el.attr("name");
        if (!obj.name) return true;
        obj.value = el.val();
        params.push(obj);
    });
    $("#filter").val(mini.encode(params));
    var excelForm = $("#excelForm");
    excelForm.submit();
}
//是否是专利管理员
function whetherGjzl(userRoles) {
    if(!userRoles) {
        return false;
    }
    for (var i = 0; i < userRoles.length; i++) {
        if (userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-LEADER'||userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-BELONG') {
            if(userRoles[i].NAME_.indexOf('专利工程师')!=-1) {
                return true;
            }
        }
    }
    return false;
}
$(function () {
    if(!whetherGjzl(currentUserRoles)){
        mini.get("addGjzl").hide();
        mini.get("removeGjzl").hide();
        mini.get("exportGjzl").hide();
    }
    var type="当前状态";
    $.ajax({
        url: gjzlPath + '/zhgl/core/zlgl/enumListQuery.do?type='+type,
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                mini.get("#gjztId").load(data);
            }
        }
    });
    searchFrm();
});