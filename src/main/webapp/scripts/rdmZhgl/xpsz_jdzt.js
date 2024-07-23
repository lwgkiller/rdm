$(function () {
    mini.get('reportYear').setValue(new Date().getFullYear());
    initJdztChart();
});
// 指定图表的配置项和数据
jdztChartOption = {
    legend: {},
    tooltip: {},
    dataset: {
        source: [
            ['product', '正常', '轻微落后','严重滞后'],
            ['全部产品', 63, 15,2],
            ['重点产品', 40, 8,1]
        ]
    },
    xAxis: {type: 'category'},
    yAxis: {},
    series: [
        {type: 'bar', barWidth: '50px',color:'#6198ea',
            label: {
                show: true,
                position: 'top'
            }},
        {type: 'bar', barWidth: '50px',color:'darkgoldenrod',
            label: {
                show: true,
                position: 'top'
            }},
        {type: 'bar', barWidth: '50px',color:'red',
            label: {
                show: true,
                position: 'top'
            }},
    ]
};
function initJdztChart() {
    var reportYear = '';
    if(mini.get("reportYear").getValue()) {
        reportYear=mini.get("reportYear").getValue();
    }
    var resultData1 = getJdztData(reportYear,"");
    var resultData2 = getJdztData(reportYear,"important");

    var sourceDate =  [
        ['product', '正常', '轻微落后','严重滞后'],
        ['全部产品', resultData1.blue, resultData1.yellow,resultData1.red],
        ['重点产品', resultData2.blue, resultData2.yellow,resultData2.red]
    ]
    jdztChartOption.dataset.source = sourceDate;
    jdztChart.setOption(jdztChartOption);
}
function getJdztData(_reportYear,_isImportant) {
    var resultDate = {};
    $.ajax({
        url: jsUseCtxPath + '/rdmZhgl/core/product/reportJdzt.do?reportYear='+_reportYear+'&isImportant='+_isImportant,
        type: 'POST',
        async:false,
        contentType: 'application/json',
        success: function (returnData) {
            if (returnData.success) {
                resultDate = returnData.data;
            }else{
                mini.alert(returnData.message);
            }
        }
    })
    return resultDate;
}

