$(function () {

})
function onFileRenderer(e) {
    var record = e.record;
    var mainId = record.id;
    var s = '';
    s += '<span  title="内参附件" style="cursor: pointer;color: #0a7ac6" onclick="showFilePage(\'' + mainId + '\',\'manage\')">内参附件</span>';
    return s;
}
function showFilePage(mainId,fileType) {
    mini.open({
        title: "附件列表",
        url: jsUseCtxPath + "/portrait/manage/fileWindow.do?mainId=" + mainId+"&fileType="+fileType+"&editable=false",
        width: 1000,
        height: 600,
        showModal: false,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
        }
    });
}

function addRow() {
    var url = jsUseCtxPath + "/portrait/manage/editPage.do?action=add";
    mini.open({
        title: "新增",
        url: url,
        width: 1200,
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
    var url= jsUseCtxPath +"/portrait/manage/editPage.do?action="+action+"&id="+id;
    var title = "修改";
    if(action=='view'){
        title = "查看";
    }
    mini.open({
        title: title,
        url: url,
        width: 1200,
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
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/portrait/manage/remove.do",
                    method: 'POST',
                    data: {ids: ids.join(',')},
                    success: function (text) {
                        if (listGrid) {
                            listGrid.reload();
                        }
                    }
                });
            }

        }
    });
}

