$(function () {
    if (zId) {
        var url = jsUseCtxPath + "/zhgl/core/ryyd/getRyydBaseInfo.do";
        $.post(
            url,
            {zId: zId},
            function (json) {
                formRyyd.setData(json);
            });
    }
    //明细入口
    if (action == 'detail') {
        formRyyd.setEnabled(false);
        zjListGrid.setAllowCellEdit(false);
        jsListGrid.setAllowCellEdit(false);
        lzListGrid.setAllowCellEdit(false);
    }else if(action == 'edit'||action == 'add'){
        $("#saveBtn").show();
        $("#editMsgZj").show();
        $("#removeZj").show();
        $("#editMsgJs").show();
        $("#removeJs").show();
        $("#editMsgLz").show();
        $("#removeLz").show();

    }
});


//检验表单是否必填
function valfirstIdSzh() {
    var month = $.trim(mini.get("month").getValue())
    if (!month) {
        return {"result": false, "message": "请选择对应月份"};
    }

    return {"result": true};
}


//保存
function saveRyyd() {
    var formValfirstId = valfirstIdSzh();
    if (!formValfirstId.result) {
        mini.alert(formValfirstId.message);
        return;
    }
    var formData = _GetFormJsonMini("formRyyd");
    if(zjListGrid.getChanges().length > 0){
        formData.ryzj=zjListGrid.getChanges();
    }
    if(jsListGrid.getChanges().length > 0){
        formData.ryjs=jsListGrid.getChanges();
    }
    if(lzListGrid.getChanges().length > 0){
        formData.rylz=lzListGrid.getChanges();
    }

    $.ajax({
        url: jsUseCtxPath + '/zhgl/core/ryyd/saveRyydDetail.do',
        type: 'post',
        async: false,
        data: mini.encode(formData),
        contentType: 'application/json',
        success: function (returnObj) {
            if (returnObj) {
                var message = "";
                if (returnObj.success) {
                    message = "数据保存成功";
                    mini.alert(message, "提示信息", function () {
                        window.location.href = jsUseCtxPath + "/zhgl/core/ryyd/ryydEditPage.do?action=edit&zId=" + returnObj.data;
                    });
                } else {
                    message = "数据保存失败，" + returnObj.message;
                    mini.alert(message, "提示信息");
                }
            }
        }
    });
}

function addZjDetail() {
    var formId = mini.get("zId").getValue();
    if (!formId) {
        mini.alert('请先点击‘保存’进行表单的保存！');
        return;
    } else {
        var row = {};
        zjListGrid.addRow(row);
    }
}

function removeRyzj(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = zjListGrid.getSelecteds();
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
                rowIds.push(r.rId);
            }
            _SubmitJson({
                url: jsUseCtxPath + "/zhgl/core/ryyd/deleteRyydDetail.do",
                method: 'POST',
                showMsg:false,
                data: {ids: rowIds.join(',')},
                success: function (data) {
                    if (data) {
                        mini.alert(data.message);
                        zjListGrid.reload();
                    }
                }
            });
        }
    });
}

function addJsDetail() {
    var formId = mini.get("zId").getValue();
    if (!formId) {
        mini.alert('请先点击‘保存’进行表单的保存！');
        return;
    } else {
        var row = {};
        jsListGrid.addRow(row);
    }
}

function removeRyjs(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = jsListGrid.getSelecteds();
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
                rowIds.push(r.rId);
            }
            _SubmitJson({
                url: jsUseCtxPath + "/zhgl/core/ryyd/deleteRyydDetail.do",
                method: 'POST',
                showMsg:false,
                data: {ids: rowIds.join(',')},
                success: function (data) {
                    if (data) {
                        mini.alert(data.message);
                        jsListGrid.reload();
                    }
                }
            });
        }
    });
}

function addLzDetail() {
    var formId = mini.get("zId").getValue();
    if (!formId) {
        mini.alert('请先点击‘保存’进行表单的保存！');
        return;
    } else {
        var row = {};
        lzListGrid.addRow(row);
    }
}

function removeRylz(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = lzListGrid.getSelecteds();
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
                rowIds.push(r.rId);
            }
            _SubmitJson({
                url: jsUseCtxPath + "/zhgl/core/ryyd/deleteRyydDetail.do",
                method: 'POST',
                showMsg:false,
                data: {ids: rowIds.join(',')},
                success: function (data) {
                    if (data) {
                        mini.alert(data.message);
                        lzListGrid.reload();
                    }
                }
            });
        }
    });
}

