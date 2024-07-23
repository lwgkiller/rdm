$(function () {
searchFrm();
});



function addNew(gjId) {
    if(!gjId) {
        gjId='';
    }
    mini.open({
        title: "新增",
        url: jsUseCtxPath + "/zlgjNPI/core/Gjll/edit.do?&gjId=" + gjId,
        width: 1050,
        height: 850,
        allowResize: true,
        onload: function () {
            searchFrm();
        },
        ondestroy:function () {
            searchFrm();
        }
    });
}

function gjllDetail(gjId) {
    var action = "detail";
    mini.open({
        title: "明细",
        url: jsUseCtxPath + "/zlgjNPI/core/Gjll/edit.do?action=" + action + "&gjId=" + gjId,
        width: 1050,
        height: 850,
        allowResize: true,
        onload: function () {
            searchFrm();
        }
    });
}

function gjllFile(gjId) {
    mini.open({
        title: "附件列表",
        url: jsUseCtxPath + "/zlgjNPI/core/Gjll/fileList.do?&gjId=" + gjId,
        width: 1050,
        height: 550,
        allowResize: true,
        onload: function () {
            searchFrm();
        }
    });
}
function removeGjll(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = gjllListGrid.getSelecteds();
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
                rowIds.push(r.gjId);
            }
            _SubmitJson({
                url: jsUseCtxPath + "/zlgjNPI/core/Gjll/deleteGjll.do",
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
