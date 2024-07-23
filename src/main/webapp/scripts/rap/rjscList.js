$(function () {
searchFrm();
});


function rjscDetail(rjId) {
    var action = "detail";
    mini.open({
        title: "明细",
        url: jsUseCtxPath + "/environment/core/Rjsc/edit.do?action=" + action + "&rjId=" + rjId,
        width: 1050,
        height: 550,
        allowResize: true,
        onload: function () {
            searchFrm();
        }
    });
}
function addNew(rjId) {
    if(!rjId) {
        rjId='';
    }
    mini.open({
        title: "新增",
        url: jsUseCtxPath + "/environment/core/Rjsc/edit.do?&rjId=" + rjId,
        width: 1050,
        height: 550,
        allowResize: true,
        onload: function () {
            searchFrm();
        },
        ondestroy:function () {
            searchFrm();
        }
    });
}

function removeRjsc(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = rjscListGrid.getSelecteds();
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
                rowIds.push(r.rjId);
            }
            _SubmitJson({
                url: jsUseCtxPath + "/environment/core/Rjsc/deleterjsc.do",
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

