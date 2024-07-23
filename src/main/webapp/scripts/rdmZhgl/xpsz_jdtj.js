$(function () {
    initJdtjChart();
});
// 指定图表的配置项和数据
jdtjChartOption = {
    legend: {},
    tooltip: {},
    dataset: {
        source: [
            ['product', '全部产品', '重点产品'],
            ['图纸归档', 63, 15],
            ['试制通知', 40, 8],
            ['样机完成', 35, 3],
            ['工业考核', 26, 2],
            ['小批量', 13, 2],
            ['上市', 2, 1]
        ]
    },
    xAxis: {type: 'category'},
    yAxis: {},
    series: [
        {type: 'bar', barWidth: '30px',color:'#6198ea',
            label: {
                show: true,
                position: 'top'
            },},
        {type: 'bar', barWidth: '30px',color:'#9e0420',
            label: {
                show: true,
                position: 'top'
            },},
    ]
};

function initJdtjChart() {
    var startYear = '';
    var endYear = '';
    if (mini.get("reportStartDate").getValue()) {
        startYear = mini.get("reportStartDate").getText();
    }
    if (mini.get("reportEndDate").getValue()) {
        endYear = mini.get("reportEndDate").getText();
    }
    var resultData1 = getJdtjData(startYear,endYear,"");
    var resultData2 = getJdtjData(startYear,endYear,"important");

    var sourceDate =  [
        ['product', '全部产品', '重点产品'],
        ['图纸归档', resultData1.tzgd, resultData2.tzgd],
        ['试制通知', resultData1.sztz, resultData2.sztz],
        ['样机完成', resultData1.yjwc, resultData2.yjwc],
        ['工业考核', resultData1.gykh, resultData2.gykh],
        ['小批量', resultData1.xpl, resultData2.xpl],
        ['上市', resultData1.ss, resultData2.ss]
    ]
    jdtjChartOption.dataset.source = sourceDate;
    jdtjChart.setOption(jdtjChartOption);
}
function getJdtjData(_startYear,_endYear,_isImportant) {
    var resultDate = {};
    $.ajax({
        url: jsUseCtxPath + '/rdmZhgl/core/product/reportJdtj.do?startYear='+_startYear+'&endYear='+_endYear+'&isImportant='+_isImportant,
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

