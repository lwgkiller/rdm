$(function () {
    querySystemCategoryInfos();
});

function querySystemCategoryInfos() {
    $.ajax({
        url: jsUseCtxPath+'/standardManager/core/standardSystem/queryCategory.do',
        success:function (data) {
            if(data) {
                mini.get("systemCategory").load(data);
            }
        }
    });
}

//新增
function addField() {
    var action = "add";
    let url = jsUseCtxPath + "/standardManager/core/standardField/fieldEditPage.do";
    var title = fieldList_xz;
    mini.open({
        title: title,
        url: url,
        width: 800,
        height: 500,
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
function editField(id,createBy) {
    if(createBy!=currentUserId && currentUserNo != 'admin') {
        mini.alert(fieldList_na1);
        return;
    }
    var action = "edit";
    var url= jsUseCtxPath +"/standardManager/core/standardField/fieldEditPage.do?id="+id;
    var title = fieldList_na2;
    mini.open({
        title: title,
        url: url,
        width: 1000,
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
function fieldUserMg(id,createBy) {
    if(createBy!=currentUserId && currentUserNo != 'admin') {
        mini.alert(fieldList_na3);
        return;
    }
    var url = jsUseCtxPath + "/standardManager/core/standardField/fieldUserListPage.do?fieldId="+id;
    mini.open({
        title: fieldList_na4,
        url: url,
        width: 1000,
        height: 600,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
        }
    });
}
//删除记录
function removeRow() {
    var rows = [];
    rows = fieldListGrid.getSelecteds();
    if (rows.length <= 0) {
        mini.alert(fieldList_na5);
        return;
    }
    mini.confirm(fieldList_na6, fieldList_na7, function (action) {
        if (action != 'ok') {
            return;
        } else {
            var ids = [];
            var existCannotDelete = false;
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                if(r.CREATE_BY_==currentUserId) {
                    ids.push(r.fieldId);
                } else {
                    existCannotDelete=true;
                }
            }
            if (existCannotDelete) {
                mini.alert(fieldList_na8);
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/standardManager/core/standardField/remove.do",
                    method: 'POST',
                    data: {ids: ids.join(',')},
                    success: function (text) {
                        if (fieldListGrid) {
                            fieldListGrid.reload();
                        }
                    }
                });
            }

        }
    });
}

function searchMyBelong() {
    mini.get("fieldName").setValue("");
    mini.get("systemCategory").setValue("");
    var params=[];
    params.push({name:'fieldUserId',value:currentUserId});
    var data={};
    data.filter=mini.encode(params);
    data.sortField=fieldListGrid.getSortField();
    data.sortOrder=fieldListGrid.getSortOrder();
    fieldListGrid.load(data);
}
