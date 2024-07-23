// 指定图表的配置项和数据
rzProjectChartOption = {
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            type: 'cross',
            crossStyle: {
                color: '#999'
            }
        }
    },
    legend: {
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
            axisLabel: {
                interval: 0,
                rotate: 40
            },
        }
    ],
    yAxis: [
        {
            type: 'value',
            name: '数量',
            interval: 1,
            axisLabel: {
                formatter: '{value} 个'
            }
        },
        {
            type: 'value',
            name: '项目占比（%）',
            axisLabel: {
                formatter: '{value} %'
            }
        }
    ],
    series: [
        {
            name: '项目数量',
            type: 'bar',
            color:'#409eff',
            data: [2, 4, 7, 23, 25, 76, 135, 162, 32, 20],
            label: {
                show: true,
                position: 'top',
            }
        },
        {
            name: '已签订合同项目数量',
            type: 'bar',
            color:'#50d51d',
            data: [2, 4, 7, 23, 25, 76, 135, 162, 32, 20],
            label: {
                show: true,
                position: 'top',
            }
        },
        {
            name: '项目占比（%）',
            type: 'line',
            color:'#3124d5',
            yAxisIndex: 1,
            label: {
                show: true,
                position: 'top',
                formatter: '{c}%'
            },
            data: []
        }
    ]
};

function initRzProjectNumChart() {
    var reportYear = '';
    if (mini.get("reportYear").getValue()) {
        reportYear = mini.get("reportYear").getText();
    }else{
        reportYear = new Date().getFullYear();
    }
    var resultData = getProjectNumData(reportYear);
    var xAxisData = [];
    var seriesData1 = [];
    var seriesData2 = [];
    var seriesData3 = [];
    for (var i = 0; i < resultData.length; i++) {
        xAxisData[i] = resultData[i].itemName;
        seriesData1[i] = resultData[i].projectNum;
        seriesData2[i] = resultData[i].signNum;
        seriesData3[i] = resultData[i].rate;
    }
    rzProjectChartOption.xAxis[0].data = xAxisData;
    rzProjectChartOption.series[0].data = seriesData1;
    rzProjectChartOption.series[1].data = seriesData2;
    rzProjectChartOption.series[2].data = seriesData3;
    projectNumChart.setOption(rzProjectChartOption);
}


function getProjectNumData(reportYear) {
    var resultDate = {};
    $.ajax({
        url: jsUseCtxPath + '/wwrz/core/report/reportRzProjectData.do?reportYear=' + reportYear,
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
