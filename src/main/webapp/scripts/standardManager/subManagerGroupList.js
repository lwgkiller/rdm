$(function () {
    querySystemCategoryInfos();
});

function querySystemCategoryInfos() {
    $.ajax({
        url: jsUseCtxPath+'/standardManager/core/standardSystem/queryCategory.do',
        success:function (data) {
            if(data) {
                mini.get("systemCategory").load(data);
                mini.get("systemCategory").setValue(systemCategoryValue);
                searchFrm();
            }
        }
    });
}

function addGroup() {
    var action = "add";
    let url = jsUseCtxPath + "/standardManager/core/subManagerGroup/groupEditPage.do?action="+action;
    var title = subManagerGroupList_xz;
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
function viewForm(id,createBy,action) {
    if(createBy!=currentUserId) {
        mini.alert(subManagerGroupList_fbrxg);
        return;
    }
    var url= jsUseCtxPath +"/standardManager/core/subManagerGroup/groupEditPage.do?action="+action+"&id="+id;
    var title = subManagerGroupList_xg;
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
function groupUserList(id,createBy) {
    if(createBy!=currentUserId) {
        mini.alert(subManagerGroupList_fbrck);
        return;
    }
    var url = jsUseCtxPath + "/standardManager/core/subManagerUser/groupUserListPage.do?groupId="+id;
    mini.open({
        title: subManagerGroupList_yhlb,
        url: url,
        width: 800,
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
    rows = groupListGrid.getSelecteds();
    if (rows.length <= 0) {
        mini.alert(subManagerGroupList_qzsxz);
        return;
    }
    mini.confirm(subManagerGroupList_qdqxxz, subManagerGroupList_tx, function (action) {
        if (action != 'ok') {
            return;
        } else {
            var ids = [];
            var existCannotDelete = false;
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                if(r.CREATE_BY_==currentUserId) {
                    ids.push(r.id);
                } else {
                    existCannotDelete=true;
                }
            }
            if (existCannotDelete) {
                mini.alert(subManagerGroupList_fbrcj);
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/standardManager/core/subManagerGroup/remove.do",
                    method: 'POST',
                    data: {ids: ids.join(',')},
                    success: function (text) {
                        if (groupListGrid) {
                            groupListGrid.reload();
                        }
                    }
                });
            }

        }
    });
}

function searchMyBelong() {
    mini.get("subManagerGroupName").setValue("");
    mini.get("systemCategory").setValue("");
    var params=[];
    params.push({name:'subManagerUserId',value:currentUserId});
    var data={};
    data.filter=mini.encode(params);
    data.pageIndex=groupListGrid.getPageIndex();
    data.pageSize=groupListGrid.getPageSize();
    data.sortField=groupListGrid.getSortField();
    data.sortOrder=groupListGrid.getSortOrder();
    groupListGrid.load(data);
}
