$(function () {
    if(action!='add'){
        infoForm.setData(applyObj);
        mini.get('id').setValue(applyObj.id);
    }
    if (action == 'view') {
        infoForm.setEnabled(false);
        $('#save').hide();
        mini.get('addProductButton').setEnabled(false);
        mini.get('delProductButton').setEnabled(false);
    }
    if(action=='edit'){
        if(permission=="false"){
            mini.get('planSource').setEnabled(false);
            mini.get('sourceName').setEnabled(false);
            mini.get('chargerMan').setEnabled(false);
            mini.get('manager').setEnabled(false);
            mini.get('responsor').setEnabled(false);
        }
    }
})
function saveData() {
    infoForm.validate();
    if (!infoForm.isValid()) {
        return;
    }
    var formData = infoForm.getData();
    var config = {
        url:  jsUseCtxPath+"/rdmZhgl/core/ndkfjh/planDetail/save.do",
        method: 'POST',
        data: formData,
        success: function (result) {
            //如果存在自定义的函数，则回调
            var result=mini.decode(result);
            if(result.success){
                CloseWindow("ok");
            }else{
            };
        }
    }
    _SubmitJson(config);
}

/**
 * 选择
 */
function selectSourceName(){
    var planSource = mini.get('planSource').getValue();
    if(!planSource){
        mini.alert("请先选择计划来源！");
        return
    }
    if(planSource=='lxxm'){
        selectProjectWindow.show();
        searchProject();
    }else if(planSource=='xpsz'){
        selectNewProductWindow.show();
        searchNewProduct();
    }else if(planSource=='tsdd'){
        selectSpecialOrderWindow.show();
        searchSpecialOrder();
    }
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
        mini.get("sourceName").setValue(selectRow.projectName);
        mini.get("sourceName").setText(selectRow.projectName);
        mini.get("sourceId").setValue(selectRow.projectId);
        mini.get("startDate").setValue(selectRow.startDate);
        mini.get("endDate").setValue(selectRow.endDate);
        mini.get("currentStage").setValue(selectRow.currentStage);
        mini.get("stageFinishDate").setValue(selectRow.stageFinishDate);
        mini.get("isDelay").setValue(selectRow.isDelay);
        mini.get("delayDays").setValue(selectRow.delayDays);
    }
    selectProjectHide();
}

function selectProjectHide() {
    selectProjectWindow.hide();
    mini.get("projectName").setValue('');
}
//查询新品试制项目
function searchNewProduct() {
    var queryParam = [];
    //其他筛选条件
    var productModel = $.trim(mini.get("productModel").getValue());
    if (productModel) {
        queryParam.push({name: "productModel", value: productModel});
    }
    var data = {};
    data.filter = mini.encode(queryParam);
    data.pageIndex = newProductListGrid.getPageIndex();
    data.pageSize = newProductListGrid.getPageSize();
    data.sortField = newProductListGrid.getSortField();
    data.sortOrder = newProductListGrid.getSortOrder();
    //查询
    newProductListGrid.load(data);
}
function selectNewProductOK() {
    var selectRow = newProductListGrid.getSelected();
    if (selectRow) {
        mini.get("sourceName").setValue(selectRow.productModel);
        mini.get("sourceName").setText(selectRow.productModel);
        mini.get("sourceId").setValue(selectRow.id);
        mini.get("startDate").setValue(selectRow.startDate);
        mini.get("endDate").setValue(selectRow.endDate);
        mini.get("currentStage").setValue(selectRow.currentStage);
        mini.get("stageFinishDate").setValue(selectRow.stageFinishDate);
        mini.get("isDelay").setValue(selectRow.isDelay);
        mini.get("delayDays").setValue(selectRow.delayDays);
    }
    selectNewProductHide();
}

function selectNewProductHide() {
    selectNewProductWindow.hide();
    mini.get("productModel").setValue('');
}
//查询特殊订单
function searchSpecialOrder() {
    var queryParam = [];
    //其他筛选条件
    var model = $.trim(mini.get("model").getValue());
    if (model) {
        queryParam.push({name: "model", value: model});
    }
    var data = {};
    data.filter = mini.encode(queryParam);
    data.pageIndex = specialOrderListGrid.getPageIndex();
    data.pageSize = specialOrderListGrid.getPageSize();
    data.sortField = specialOrderListGrid.getSortField();
    data.sortOrder = specialOrderListGrid.getSortOrder();
    //查询
    specialOrderListGrid.load(data);
}
function selectSpecialOrderOK() {
    var selectRow = specialOrderListGrid.getSelected();
    if (selectRow) {
        mini.get("sourceName").setValue(selectRow.model);
        mini.get("sourceName").setText(selectRow.model);
        mini.get("sourceId").setValue(selectRow.ckddId);
    }
    selectSpecialOrderHide();
}

function selectSpecialOrderHide() {
    selectSpecialOrderWindow.hide();
    mini.get("model").setValue('');
}
