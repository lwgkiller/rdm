$(function () {
    if(action!='add'){
        planForm.setData(applyObj);
        mini.get('id').setValue(applyObj.id);
        loadGrid();
    }
    if (action == 'view') {
        planForm.setEnabled(false);
        $('#save').hide();
    }
})
function saveData() {
    planForm.validate();
    if (!planForm.isValid()) {
        return;
    }
    if(judgePlanData()){
        return;
    }

    var formData = planForm.getData();
    var config = {
        url: jsUseCtxPath+"/rdmZhgl/core/productConfig/save.do",
        method: 'POST',
        data: formData,
        success: function (result) {
            //如果存在自定义的函数，则回调
            var result=mini.decode(result);
            if(result.success){
                mini.get('id').setValue(result.data.id);
                saveRow();
            }else{
            };
        }
    }
    _SubmitJson(config);
}

function addItem() {
    var newRow = {name: "New Row"};
    itemGrid.addRow(newRow, 0);
    itemGrid.beginEditCell(newRow, "workContent");
}
function removeItem() {
    var rows = itemGrid.getSelecteds();
    if (rows.length > 0) {
        mini.showMessageBox({
            title: "提示信息！",
            iconCls: "mini-messagebox-info",
            buttons: ["ok", "cancel"],
            message: "是否确定删除！",
            callback: function (action) {
                if (action == "ok") {
                    itemGrid.removeRows(rows, false);
                }
            }
        });
    } else {
        mini.alert("请至少选中一条记录");
        return;
    }
}
//验证数据
function judgePlanData() {
    var data = itemGrid.getChanges();
    var flag = false;
    if (data.length > 0) {
        for (var i = 0; i < data.length; i++) {
            if (data[i]._state == 'removed') {
                continue;
            }
            if (!data[i].itemName || !data[i].sort ) {
                message = "请填写必填项！";
                flag = true;
                mini.alert(message);
                break;
            }
        }
    }
    return flag;
}

function saveRow() {
    var data = itemGrid.getChanges();
    var needReload = true;
    var mainId = mini.get('id').getValue();
    if (data.length > 0) {
        for (var i = 0; i < data.length; i++) {
            data[i].mainId = mainId;
            if (data[i]._state == 'removed') {
                continue;
            }
        }
        if (needReload) {
            var json = mini.encode(data);
            $.ajax({
                url: jsUseCtxPath + "/rdmZhgl/core/productConfig/dealData.do",
                data: json,
                type: "post",
                contentType: 'application/json',
                async: false,
                success: function (text) {
                    if (text && text.message) {
                        loadGrid();
                    }
                }
            });
        }
    }
}
function loadGrid() {
    var mainId = mini.get('id').getValue();
    var paramArray = [{name: "mainId", value: mainId}];
    var data = {};
    data.filter = mini.encode(paramArray);
    data.mainId = mainId;
    itemGrid.load(data);
}
function removeRow() {
    var rows = itemGrid.getSelecteds();
    if (rows.length > 0) {
        mini.showMessageBox({
            title: "提示信息！",
            iconCls: "mini-messagebox-info",
            buttons: ["ok", "cancel"],
            message: "是否确定删除！",
            callback: function (action) {
                if (action == "ok") {
                    itemGrid.removeRows(rows, false);
                }
            }
        });

    } else {
        mini.alert("请至少选中一条记录");
        return;
    }
}
