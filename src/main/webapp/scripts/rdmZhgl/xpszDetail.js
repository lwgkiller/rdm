var editForm = '';
$(function () {
    searchList()
});
function searchList() {
    loadProductGrid();
}
function loadProductGrid() {
    if(ApplyObj.mainIds){
        var paramArray = [{name: "mainIds", value: ApplyObj.mainIds}];
        var data = {};
        data.filter = mini.encode(paramArray);
        productGrid.load(data);
    }
}
