$(function () {
searchFrm();
});



function addNew(standardId) {
    if(!standardId) {
        standardId='';
    }
    var url = jsUseCtxPath + "/standardManager/core/NationStandardChangeController/edit.do?standardId="+ standardId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (nationStandardListGrid) {
                nationStandardListGrid.reload()
            }
        }
    }, 1000);
}

function StandardChangeDetail(standardId) {
    var action = "detail";
    var url = jsUseCtxPath + "/standardManager/core/NationStandardChangeController/edit.do?action=" + action + "&standardId=" + standardId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (nationStandardListGrid) {
                nationStandardListGrid.reload()
            }
        }
    }, 1000);
}
function removestandardChange(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = nationStandardListGrid.getSelecteds();
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
                rowIds.push(r.standardId);
            }
            _SubmitJson({
                url: jsUseCtxPath + "/standardManager/core/NationStandardChangeController/deletestandardChange.do",
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