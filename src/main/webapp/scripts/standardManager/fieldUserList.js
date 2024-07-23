$(function () {
    mini.get('fieldId').setValue(fieldId);
    searchFrm();
});
function getUserInfoById(userId) {
    var userInfo = "";
    $.ajax({
        url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/' + userId + '/getUserInfoById.do',
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            userInfo = data;
        }
    });
    return userInfo;
}

function addUser() {
    var row={fieldId:fieldId};
    fieldUserListGrid.addRow(row);
}


function removeUser() {
    var selecteds = fieldUserListGrid.getSelecteds();
    var deleteArr = [];
    for (var i = 0; i < selecteds.length; i++) {
        var row = selecteds[i];
        deleteArr.push(row);
    }
    fieldUserListGrid.removeRows(deleteArr);
}

function saveUser() {
    var data = fieldUserListGrid.getData();
    var message = "数据保存成功";
    if (data.length > 0) {
        var json = mini.encode(data);
        $.ajax({
            url: jsUseCtxPath+"/standardManager/core/standardField/fieldUserSave.do?fieldId="+fieldId,
            data: json,
            type: "post",
            contentType: 'application/json',
            async:false,
            success: function (result) {
                if (result && result.message) {
                    message = result.message;
                }
            }
        });
    }
    mini.showMessageBox({
        title: "提示信息",
        iconCls: "mini-messagebox-info",
        buttons: ["ok"],
        message: message,
        callback: function (action) {
            if (action == "ok") {
                fieldUserListGrid.reload();
            }
        }
    });
}
