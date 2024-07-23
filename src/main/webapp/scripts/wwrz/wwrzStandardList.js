$(function () {
})
function addForm() {
    let url = jsUseCtxPath + "/wwrz/core/standard/getEditPage.do?action=add";
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
            searchFrm()
        }
    });
}
//修改
function editForm(id,action) {
    var url = jsUseCtxPath + "/wwrz/core/standard/getEditPage.do?action="+action+"&&id=" + id;
    var title = "编辑";
    if(action=='view'){
        title = '查看';
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
                ids.push(r.mainId);
                var createBy = r.CREATE_BY_;
                if (createBy != currentUserId ) {
                    mini.alert("只有创建人才可以删除！");
                    return;
                }
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/wwrz/core/standard/remove.do",
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
function sectorAttach(e) {
    var record = e.record;
    var detailId = record.id;
    var s = '';
    var editable = true;
    var createBy = record.CREATE_BY_;
    if(createBy != currentUserId){
        editable = false;
    }
    if (detailId == '' || detailId == 'undefined' || detailId == undefined) {
        s += '<span  title="附件上传" style="color: grey"">章节附件</span>';
    } else {
        s += '<span  title="附件上传" style="cursor: pointer;color: #0a7ac6" onclick="showFilePage(\'' + detailId + '\',\'sectorAttach\',\'' + editable + '\')">章节附件</span>';
    }
    return s;
}
function showFilePage(detailId, fileType, editable) {
    mini.open({
        title: "附件列表",
        url: jsUseCtxPath + "/wwrz/core/file/fileWindow.do?detailId=" + detailId + "&fileType=" + fileType + "&editable=" + editable,
        width: 1000,
        height: 500,
        showModal: false,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
        }
    });
}
