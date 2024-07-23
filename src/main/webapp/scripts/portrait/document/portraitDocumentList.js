$(function () {

})
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
