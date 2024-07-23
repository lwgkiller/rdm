//刷新项目级别划分页面
function refreshLevelDivide() {
    levelDivideListGrid.load();
}

//保存项目级别划分页面数据
function saveLevelDivide() {
    var changes = levelDivideListGrid.getChanges();
    var message = "数据保存成功";
    var needReload = true;
    if (changes.length > 0) {
        //校验数据正确性
        for (var i = 0; i < changes.length; i++) {
            if (!changes[i].minScore || !changes[i].maxScore) {
                message = "请填写级别‘" + changes[i].levelName + "’的必填项！";
                needReload = false;
                break;
            }
            if (changes[i].minScore > changes[i].maxScore) {
                message = "级别‘" + changes[i].levelName + "’的最低分需要小于等于最高分！";
                needReload = false;
                break;
            }
        }
        //更新数据
        if (needReload) {
            var changesData = mini.encode(changes);
            $.ajax({
                url: jsUseCtxPath + '/xcmgProjectManager/core/config/saveLevelDivide.do',
                type: 'POST',
                data: changesData,
                async: false,
                contentType: 'application/json',
                success: function (data) {
                    if (data && data.message) {
                        message = data.message;
                    }
                }
            });
        }
    }
    mini.showMessageBox({
        title: "提示信息",
        iconCls: "mini-messagebox-info",
        buttons: ["ok"],
        message: message,
        callback: function (action) {
            if (action == "ok" && needReload) {
                levelDivideListGrid.reload();
            }
        }
    });
}

//刷新项目标准分页面
function refreshStandardScore() {
    standardScoreListGrid.setUrl(url + mini.get("projectCategory").getValue());
    standardScoreListGrid.load();
}

//保存项目标准分页面数据
function saveStandardScore() {
    var changes = standardScoreListGrid.getChanges();
    var message = "数据保存成功";
    var needReload = true;
    if (changes.length > 0) {
        //校验数据正确性
        for (var i = 0; i < changes.length; i++) {
            if (!changes[i].score) {
                message = "请填写级别‘" + changes[i].levelName + "’的必填项！";
                needReload = false;
                break;
            }
        }
        //更新数据
        if (needReload) {
            var changesData = mini.encode(changes);
            $.ajax({
                url: jsUseCtxPath + '/xcmgProjectManager/core/config/saveStandardScore.do',
                type: 'POST',
                data: changesData,
                async: false,
                contentType: 'application/json',
                success: function (data) {
                    if (data && data.message) {
                        message = data.message;
                    }
                }
            });
        }
    }
    mini.showMessageBox({
        title: "提示信息",
        iconCls: "mini-messagebox-info",
        buttons: ["ok"],
        message: message,
        callback: function (action) {
            if (action == "ok" && needReload) {
                standardScoreListGrid.reload();
            }
        }
    });
}

//项目类别变化时，查询项目标准分
function queryStandardScoreByCategory() {
    if (standardScoreListGrid.getChanges().length > 0) {
        if (confirm("有未保存的数据，是否取消本次操作？")) {
            //恢复成之前的项目类别
            mini.get("projectCategory").setValue(categoryValue);
            return;
        }
    }
    //查询
    standardScoreListGrid.setUrl(url + mini.get("projectCategory").getValue());
    standardScoreListGrid.reload();
}

function queryDeliveryByCategory() {
    deliveryListGrid.setUrl(url + mini.get("projectCategory").getValue());
    deliveryListGrid.load();
}

function refreshDelivery() {
    deliveryListGrid.setUrl(url + mini.get("projectCategory").getValue());
    deliveryListGrid.load();
}

function removeDelivery() {
    if(!isManager) {
        mini.alert("没有操作权限!");
        return;
    }
    var rows = deliveryListGrid.getSelecteds();
    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }

    mini.confirm("确定删除选中记录？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var ids = [];
            for (var i = 0; i < rows.length; i++) {
                var r = rows[i];
                ids.push(r.deliveryId);
            }
            _SubmitJson({
                url: jsUseCtxPath + "/xcmgProjectManager/core/config/delDelivery.do",
                method: 'POST',
                data: {ids: ids.join(',')},
                success: function (text) {
                    deliveryListGrid.load();
                }
            });
        }
    });
}

//新增或者编辑行数据
function openDeliveryWindow(categoryId,categoryName,deliveryId) {
    if(!isManager&&currentUserNo!='admin') {
        mini.alert("没有操作权限!");
        return;
    }
    var title="新增交付物明细";
    if(deliveryId) {
        title="编辑交付物明细";
    }
    else {
        deliveryId="";
    }
    _OpenWindow({
        url: jsUseCtxPath+"/xcmgProjectManager/core/config/deliveryEdit?categoryId="+categoryId+"&categoryName="+categoryName+"&deliveryId="+deliveryId,
        title: title,
        width: 760,
        height: 400,
        showMaxButton:false,
        allowResize: true,
        ondestroy: function (action) {
            refreshDelivery();
        }
    });
}

function saveDelivery() {
    var formData = _GetFormJsonMini("formDelivery");
    if(!$.trim(formData.deliveryName)) {
        mini.alert("请填写交付物名称");
        return;
    }
    if(!$.trim(formData.stageId)) {
        mini.alert("请选择项目阶段");
        return;
    }
    $.ajax({
        url: jsUseCtxPath + '/xcmgProjectManager/core/config/saveDelivery.do',
        type: 'post',
        async:false,
        data:mini.encode(formData),
        contentType: 'application/json',
        success: function (data) {
            CloseWindow('ok');
        }
    });
}

function stageChanged() {
    var stageId=mini.get("stageInput").getValue();
    for(var i=0;i<stageInfos.length;i++) {
        if(stageInfos[i].stageId==stageId) {
            mini.get("stageNoInput").setValue(stageInfos[i].stageNo);
        }
    }
}

function refreshMemRoleRatio() {
    memRoleRatioListGrid.load();
}

function saveMemRoleRatio() {
    var changes = memRoleRatioListGrid.getChanges();
    var message = "数据保存成功";
    var needReload = true;
    if (changes.length > 0) {
        //校验数据正确性
        for (var i = 0; i < changes.length; i++) {
            if (!changes[i].minRatio || !changes[i].maxRatio) {
                message = "请填写角色‘" + changes[i].roleName + "’的必填项！";
                needReload = false;
                break;
            }
            if (changes[i].minRatio > changes[i].maxRatio) {
                message = "角色‘" + changes[i].roleName + "’的系数最低分需要小于等于系数最高分！";
                needReload = false;
                break;
            }
        }
        //更新数据
        if (needReload) {
            var changesData = mini.encode(changes);
            $.ajax({
                url: jsUseCtxPath + '/xcmgProjectManager/core/config/saveMemRoleRatio.do',
                type: 'POST',
                data: changesData,
                async: false,
                contentType: 'application/json',
                success: function (data) {
                    if (data && data.message) {
                        message = data.message;
                    }
                }
            });
        }
    }
    mini.showMessageBox({
        title: "提示信息",
        iconCls: "mini-messagebox-info",
        buttons: ["ok"],
        message: message,
        callback: function (action) {
            if (action == "ok" && needReload) {
                memRoleRatioListGrid.reload();
            }
        }
    });
}
function refreshMemRoleRank() {
    memRoleRankListGrid.load();
}

function saveMemRoleRank() {
    var changes = memRoleRankListGrid.getChanges();
    var message = "数据保存成功";
    var needReload = true;
    if (changes.length > 0) {
        //校验数据正确性
        for (var i = 0; i < changes.length; i++) {
            if (!changes[i].minRankKey) {
                message = "请选择最低职级要求！";
                needReload = false;
                break;
            }
        }
        //更新数据
        if (needReload) {
            var changesData = mini.encode(changes);
            $.ajax({
                url: jsUseCtxPath + '/xcmgProjectManager/core/config/saveMemRoleRank.do',
                type: 'POST',
                data: changesData,
                async: false,
                contentType: 'application/json',
                success: function (data) {
                    if (data && data.message) {
                        message = data.message;
                    }
                }
            });
        }
    }
    mini.showMessageBox({
        title: "提示信息",
        iconCls: "mini-messagebox-info",
        buttons: ["ok"],
        message: message,
        callback: function (action) {
            if (action == "ok" && needReload) {
                memRoleRankListGrid.reload();
            }
        }
    });
}