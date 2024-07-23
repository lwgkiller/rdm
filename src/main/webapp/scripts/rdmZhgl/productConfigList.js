$(function () {
})



function addForm() {
    let url = jsUseCtxPath + "/rdmZhgl/core/productConfig/getEditPage.do?action=add";
    mini.open({
        title: "新增",
        url: url,
        width: 1200,
        height: 800,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            planListGrid.reload();
        }
    });
}

//修改
function editForm() {
    //先判断 是项目计划还是非项目计划
    var rows = [];
    rows = planListGrid.getSelecteds();
    if (rows.length != 1) {
        mini.alert("请选中一条记录进行操作！");
        return;
    }
    var mainId = rows[0].mainId;
    var url = jsUseCtxPath + "/rdmZhgl/core/productConfig/getEditPage.do?action=edit&&mainId=" + mainId;
    var title = "编辑";
    mini.open({
        title: title,
        url: url,
        width: 1200,
        height: 800,
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
    rows = planListGrid.getSelecteds();
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
                ids.push(r.mainId);
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/rdmZhgl/core/productConfig/remove.do",
                    method: 'POST',
                    data: {ids: ids.join(',')},
                    success: function (text) {
                        if (planListGrid) {
                            planListGrid.reload();
                        }
                    }
                });
            }
        }
    });
}
