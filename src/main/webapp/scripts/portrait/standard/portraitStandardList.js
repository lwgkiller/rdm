$(function () {

})

function addRow() {
    let url = jsUseCtxPath + "/portrait/standard/standardEditPage.do?action=add";
    mini.open({
        title: "新增",
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
function viewForm(id,action) {
    var url= jsUseCtxPath +"/portrait/standard/standardEditPage.do?action="+action+"&id="+id;
    var title = "修改";
    if(action=='view'){
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
    rows = standardListGrid.getSelecteds();
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
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/portrait/standard/remove.do",
                    method: 'POST',
                    data: {ids: ids.join(',')},
                    success: function (text) {
                        if (standardListGrid) {
                            standardListGrid.reload();
                        }
                    }
                });
            }

        }
    });
}
//数据同步
function asyncData() {
    mini.confirm("确定同步数据？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            _SubmitJson({
                url: jsUseCtxPath + "/portrait/standard/asyncStandard.do",
                method: 'POST',
                success: function (data) {
                   mini.alert(data.message);
                   standardListGrid.reload();
                }
            });
        }
    });
}
