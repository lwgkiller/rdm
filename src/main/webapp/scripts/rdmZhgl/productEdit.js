$(function () {
    if (action != 'add') {
        productForm.setData(applyObj);
        mini.get('id').setValue(applyObj.id);
        if(mini.get('productId').getValue()) {
            mini.get('productModel').setEnabled(false);
        }
    }
    if (action == 'view') {
        productForm.setEnabled(false);
        $('#save').hide();
    }
})

function saveData() {
    productForm.validate();
    if (!productForm.isValid()) {
        return;
    }
    var formData = productForm.getData();
    var config = {
        url: jsUseCtxPath + "/rdmZhgl/core/product/save.do",
        method: 'POST',
        data: formData,
        success: function (result) {
            //如果存在自定义的函数，则回调
            var result = mini.decode(result);
            if (result.success) {
                mini.get('id').setValue(result.data.id);
            } else {
            }
            ;
        }
    }
    _SubmitJson(config);
}

function selectSourceName() {
    selectProjectWindow.show();
    searchProject();
}
//查询立项项目
function searchProject() {
    var queryParam = [];
    //其他筛选条件
    var projectName = $.trim(mini.get("projectName").getValue());
    if (projectName) {
        queryParam.push({name: "projectName", value: projectName});
    }
    var data = {};
    data.filter = mini.encode(queryParam);
    data.pageIndex = projectListGrid.getPageIndex();
    data.pageSize = projectListGrid.getPageSize();
    data.sortField = projectListGrid.getSortField();
    data.sortOrder = projectListGrid.getSortOrder();
    //查询
    projectListGrid.load(data);
}
function selectProjectOK() {
    var selectRow = projectListGrid.getSelected();
    if (selectRow) {
        mini.get("projectId").setValue(selectRow.projectId);
        mini.get("projectId").setText(selectRow.projectName);
    }
    selectProjectHide();
}

function selectProjectHide() {
    selectProjectWindow.hide();
    mini.get("projectName").setValue('');
}
