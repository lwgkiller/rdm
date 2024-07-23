$(function () {
    searchFrm();
});
/**
 * 添加弹窗
 */
function openZlghEditWindow( action, zlghId,type) {
    var title = "";
    if (action == "add") {
        title = "战略规划添加"
    }else {
        title = "战略规划修改"
    }
    mini.open({
        title: title,
        url: jsUseCtxPath + "/strategicPlanning/core/xgzlgh/edit.do?zlghId=" + zlghId + '&action='+ action + '&type='+ type,
        width: 900,
        height: 700,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            searchZlghList();
        }
    });

}

function searchZlghList() {
    var queryParam = [];
    //其他筛选条件
    var zlghName = $.trim(mini.get("zlghName").getValue());
    if (zlghName) {
        queryParam.push({name: "zlghName", value: zlghName});
    }
    var ghzgbmName = $.trim(mini.get("ghzgbmName").getValue());
    if (ghzgbmName) {
        queryParam.push({name: "ghzgbmName", value: ghzgbmName});
    }
    var ghfzrName = $.trim(mini.get("ghfzrName").getValue());
    if (ghfzrName) {
        queryParam.push({name: "ghfzrName", value: ghfzrName});
    }
    var ghnf = $.trim(mini.get("ghnf").getValue());
    if (ghnf) {
        queryParam.push({name: "ghnf", value: ghnf});
    }
    var data = {};
    data.filter = mini.encode(queryParam);
    data.pageIndex = zlghListGrid.getPageIndex();
    data.pageSize = zlghListGrid.getPageSize();
    data.sortField = zlghListGrid.getSortField();
    data.sortOrder = zlghListGrid.getSortOrder();
    //查询
    zlghListGrid.load(data);
}

/**
 * 删除
 */
function removeZlgh(record) {

    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = zlghListGrid.getSelecteds();
    }
    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }
    for (var i = 0, l = rows.length; i < l; i++) {
        if (rows[i].CREATE_BY_!=currentUserId){
            mini.alert("只有创建人才能删除");
            return;
        }
    }
    mini.confirm("确定删除选中记录？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var rowIds = [];
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                rowIds.push(r.zlghId);
            }

            _SubmitJson({
                url: jsUseCtxPath + "/strategicPlanning/core/xgzlgh/deleteZlgh.do?",
                method: 'POST',
                showMsg: false,
                data: {ids: rowIds.join(',')},
                success: function (data) {
                    if (data) {
                        mini.alert(data.message);
                        zlghListGrid.load();
                    }
                }
            });
        }
    });
}




//是否是奖项荣誉专员
function whetherJxry(userRoles) {
    if(!userRoles) {
        return false;
    }
    for (var i = 0; i < userRoles.length; i++) {
        if (userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-LEADER'||userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-BELONG') {
            if(userRoles[i].NAME_.indexOf('奖项荣誉专员')!=-1) {
                return true;
            }
        }
    }
    return false;
}
/*
$(function () {
    if(!whetherJxry(currentUserRoles)){
        mini.get("addId").hide();
        mini.get("deletedId").hide();
        mini.get("importMaterialName").hide();
    }
});*/
//查新报告附件
function addZlghFile(zlghId,action) {
    var canOperateFile = false;
    if (action=='edit'||action=='') {
        canOperateFile = true;
    }
    mini.open({
        title: "文件上传",
        url: jsUseCtxPath + "/strategicPlanning/core/xgzlgh/zlghUploadWindow.do?zlghId=" + zlghId+ "&action=" + action + "&canOperateFile=" + canOperateFile + "&coverContent=" + coverContent,
        width: 800,
        height: 600,
        showModal: false,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
        }
    });
}