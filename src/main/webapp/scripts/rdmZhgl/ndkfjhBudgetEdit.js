$(function () {
    if(action!='add'){
        infoForm.setData(applyObj);
        mini.get('id').setValue(applyObj.id);
        grid_product.setData(applyObj.detailList);
    }
    if (action == 'view') {
        infoForm.setEnabled(false);
        $('#save').hide();
        mini.get('addProductButton').setEnabled(false);
        mini.get('delProductButton').setEnabled(false);
        grid_product.setAllowCellEdit(false);
    }
})
function saveData() {
    infoForm.validate();
    if (!infoForm.isValid()) {
        return;
    }
    var formData = _GetFormJsonMini("infoForm");
    formData.productData = grid_product.getChanges();
    var json =mini.encode(formData);
    $.ajax({
        url:  jsUseCtxPath+"/rdmZhgl/core/ndkfjh/budget/save.do",
        type: 'post',
        async: false,
        data:json,
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                var message="";
                if(data.success) {
                    message="数据保存成功";
                } else {
                    message="数据保存失败"+data.message;
                }
                mini.alert(message,"提示信息",function () {
                    CloseWindow("ok");
                });
            }
        }
    });
}
function addProduct() {
    var row={};
    grid_product.addRow(row);
}
function delProduct() {
    var selecteds = grid_product.getSelecteds();
    var deleteArr = [];
    for (var i = 0; i < selecteds.length; i++) {
        var row = selecteds[i];
        deleteArr.push(row);
    }
    grid_product.removeRows(deleteArr);
}
