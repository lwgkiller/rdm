// 指定图表的配置项和数据
zrszChartOption = {
    legend: {
        data: ['个数']
    },
    grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
    },
    xAxis: [
        {
            type: 'category',
            data: ['2013', '2014', '2015', '2016', '2017', '2018', '2019', '2020', '2021', '2022'],
        }
    ],
    yAxis: [
        {
            type: 'value',
            name: '数量',
            axisLabel: {
                formatter: '{value} 个'
            }
        }
    ],
    series: [
        {
            name: '数量',
            type: 'bar',
            color:'#409eff',
            data: [2, 4, 7, 23, 25, 76, 135, 162, 32, 20],
            label: {
                show: true,
                position: 'top',
            }
        },
    ]
};

function initReportChart() {
    var reportYear = '';
    if (mini.get("reportYear").getValue()) {
        reportYear = mini.get("reportYear").getText();
    }else{
        reportYear = new Date().getFullYear();
    }
    var resultData = getReportData(reportYear);
    var xAxisData = [];
    var seriesData1 = [];
    for (var i = 0; i < resultData.length; i++) {
        xAxisData[i] = resultData[i].reportYear;
        seriesData1[i] = resultData[i].dataValue;
    }
    zrszChartOption.xAxis[0].data = xAxisData;
    zrszChartOption.series[0].data = seriesData1;
    reportChart.setOption(zrszChartOption);
}


function getReportData(reportYear) {
    var resultDate = {};
    $.ajax({
        url: jsUseCtxPath + '/wwrz/core/report/reportData.do?reportYear=' + reportYear,
        type: 'POST',
        async: false,
        contentType: 'application/json',
        success: function (returnData) {
            if (returnData.success) {
                resultDate = returnData.data;
            } else {
                mini.alert(returnData.message);
            }
        }
    })
    return resultDate;
}
