$(function () {
});
//行功能按钮
function onActionRenderer(e) {
    var record = e.record;
    var id = record.id;
    var s = '';
    if (currentUserId == record.CREATE_BY_ ) {
        s += '<span  title="编辑" onclick="viewForm(\'' + id + '\',\'edit\')">编辑</span>';
    } else {
        s += '<span  title="编辑" style="color: silver">编辑</span>';
    }

    return s;
}

function addItems() {
    var action = "add";
    let url = jsUseCtxPath + "/wwrz/core/company/editPage.do?action=" + action;
    var title = "新增";
    mini.open({
        title: title,
        url: url,
        width: 800,
        height: 600,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            searchFrm();
        }
    });
}

//查看
function viewForm(id, action) {
    var url = jsUseCtxPath + "/wwrz/core/company/editPage.do?action=" + action + "&id=" + id;
    var title = "修改";
    if (action == 'view') {
        title = "查看";
    }
    mini.open({
        title: title,
        url: url,
        width: 800,
        height: 600,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            searchFrm();
        }
    });
}

//删除记录
function removeRow() {
    var rows = [];
    rows = listGrid.getSelecteds();
    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }
    mini.confirm("确定取消选中记录？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var ids = [];
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                ids.push(r.id);
                var createBy = r.CREATE_BY_;
                if (createBy != currentUserId) {
                    mini.alert("只有创建人才可以删除！");
                    return;
                }
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/wwrz/core/company/remove.do",
                    method: 'POST',
                    data: {ids: ids.join(',')},
                    success: function (text) {
                        searchFrm();
                    }
                });
            }

        }
    });
}


function exportExcel() {
    var params = [];
    var parent = $(".search-form");
    var inputAry = $("input", parent);
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
