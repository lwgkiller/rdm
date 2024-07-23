// 指定图表的配置项和数据
deptDelayChartOption = {
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            type: 'cross',
            crossStyle: {
                color: '#999'
            }
        }
    },
    toolbox: {
    },
    legend: {
        data: ['试制延期', '下发切换延期', '切换延期','试制延期率（%）','下发切换延期率（%）','切换延期率（%）']
    },
    xAxis: [
        {
            type: 'category',
            data: [],
            axisPointer: {
                type: 'shadow'
            },
            axisLabel: {
                interval: 0,
                rotate: 20
            },
        }
    ],
    yAxis: [
        {
            type: 'value',
            name: '延期数量',
            axisLabel: {
                formatter: '{value} 个'
            }
        },
        {
            type: 'value',
            name: '延期率（%）',
            min: 0,
            max: 100,
            interval: 10,
            axisLabel: {
                formatter: '{value} %'
            }
        }
    ],
    series: [
        {
            name: '试制延期',
            type: 'bar',
            data: []
        },
        {
            name: '下发切换延期',
            type: 'bar',
            data: []
        },
        {
            name: '切换延期',
            type: 'bar',
            data: []
        },
        {
            name: '试制延期率（%）',
            type: 'line',
            yAxisIndex: 1,
            data: []
        },
        {
            name: '下发切换延期率（%）',
            type: 'line',
            yAxisIndex: 1,
            data: []
        },
        {
            name: '切换延期率（%）',
            type: 'line',
            yAxisIndex: 1,
            data: []
        }
    ]
};

function initDeptDelayReportChart() {
    var resultData = getDeptDelayReportData();
    var deptNameArray = [];
    var totalNumArray = [];
    var productDelayArray = [];
    var changeNoticeDelayArray = [];
    var changeDelayArray = [];
    var productDelayRatioArray = [];
    var changeNoticeDelayRatioArray = [];
    var changeDelayRatioArray = [];
    for(var i=0;i<resultData.length;i++){
        deptNameArray[i] = resultData[i].deptName;
        totalNumArray[i] = resultData[i].totalSum;
        productDelayArray[i] = resultData[i].productDelayNum;
        changeNoticeDelayArray[i] = resultData[i].changeNoticeDelayNum;
        changeDelayArray[i] = resultData[i].changeDelayNum;
        productDelayRatioArray[i] = resultData[i].productDelayNumRatio.toFixed(0);
        changeNoticeDelayRatioArray[i] = resultData[i].changeNoticeDelayNumRatio.toFixed(0);
        changeDelayRatioArray[i] = resultData[i].changeDelayNumRatio.toFixed(0);
    }
    deptDelayChartOption.xAxis[0].data = deptNameArray;
    deptDelayChartOption.series[0].data = productDelayArray;
    deptDelayChartOption.series[1].data = changeNoticeDelayArray;
    deptDelayChartOption.series[2].data = changeDelayArray;
    deptDelayChartOption.series[3].data = productDelayRatioArray;
    deptDelayChartOption.series[4].data = changeNoticeDelayRatioArray;
    deptDelayChartOption.series[5].data = changeDelayRatioArray;
    delayChart.setOption(deptDelayChartOption);
}
function getDeptDelayReportData() {
    var resultDate = {};
    $.ajax({
        url: jsUseCtxPath + '/rdmZhgl/core/yfjb/reportDelayData.do',
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
