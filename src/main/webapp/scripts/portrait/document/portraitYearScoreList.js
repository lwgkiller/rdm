$(function () {
    let currentYear = new Date().getFullYear();
    yearCombobox.setValue(currentYear);
    searchFrm()
})

function openEditWindow() {
    yearSelectedWindow.show();
}
function closeEditWindow() {
    yearSelectedWindow.hide();
}

function genYearScore() {
    yearForm.validate();
    if (!yearForm.isValid()) {
        mini.alert("年份必须选择");
        return;
    }
    var formData = yearForm.getData();
    mini.confirm("确定生成年度绩效？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var config = {
                url: jsUseCtxPath + "/portrait/yearScore/genYearScore.do",
                method: 'POST',
                data: formData,
                success: function (result) {
                    //如果存在自定义的函数，则回调
                    var result = mini.decode(result);
                    if (result.success) {
                        closeEditWindow();
                        searchFrm();
                    } else {
                    }
                    ;
                }
            }
            _SubmitJson(config);
        }
    });
}








//查看
function viewForm(userId,action) {
    var reportYear = mini.get('reportYear').getValue();
    var url= jsUseCtxPath +"/portrait/document/documentDetail.do?userId="+userId+"&reportYear="+reportYear;
    window.open(url);
}
//查看
function viewPortrait(userId,action) {
    var reportYear = mini.get('reportYear').getValue();
    var url= jsUseCtxPath +"/portrait/document/personShowPage.do?userId="+userId+"&reportYear="+reportYear;
    window.open(url);
}
//删除记录
function removeRow() {
    var rows = [];
    rows = noticeListGrid.getSelecteds();
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
                    url: jsUseCtxPath + "/portrait/notice/remove.do",
                    method: 'POST',
                    data: {ids: ids.join(',')},
                    success: function (text) {
                        if (noticeListGrid) {
                            noticeListGrid.reload();
                        }
                    }
                });
            }

        }
    });
}
