$(function () {
    queryFieldChart();
});

function queryFieldChart() {
    var systemCategoryId=mini.get("systemCategoryId").getValue();
    var params=[];
    params.push({name:'systemCategoryId',value:systemCategoryId});
    var data={};
    data.filter=mini.encode(params);
    fieldListGrid.load(data);
}
