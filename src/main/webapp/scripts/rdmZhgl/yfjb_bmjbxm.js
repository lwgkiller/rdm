// 指定图表的配置项和数据
deptReportChartOption = {
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
        data: ['降本项目数', '累计已切换', '切换率（%）']
    },
    xAxis: [
        {
            type: 'category',
            data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
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
            name: '项目数',
            axisLabel: {
                formatter: '{value} 个'
            }
        },
        {
            type: 'value',
            name: '切换率（%）',
            axisLabel: {
                formatter: '{value} %'
            }
        }
    ],
    series: [
        {
            name: '降本项目数',
            type: 'bar',
            data: [
                2.0, 4.9, 7.0, 23.2, 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3
            ]
        },
        {
            name: '累计已切换',
            type: 'bar',
            data: [
                2.6, 5.9, 9.0, 26.4, 28.7, 70.7, 175.6, 182.2, 48.7, 18.8, 6.0, 2.3
            ]
        },
        {
            name: '切换率（%）',
            type: 'line',
            yAxisIndex: 1,
            data: [2.0, 2.2, 3.3, 4.5, 6.3, 10.2, 20.3, 23.4, 23.0, 16.5, 12.0, 6.2]
        }
    ]
};

function initDeptReportChart() {
    var resultData = getDeptReportData();
    var deptNameArray = [];
    var totalNumArray = [];
    var changeNumArray = [];
    var ratioArray = [];
    for(var i=0;i<resultData.length;i++){
        deptNameArray[i] = resultData[i].deptName;
        totalNumArray[i] = resultData[i].totalSum;
        changeNumArray[i] = resultData[i].changeNum;
        ratioArray[i] = resultData[i].ratio.toFixed(0);
    }
    deptReportChartOption.xAxis[0].data = deptNameArray;
    deptReportChartOption.series[0].data = totalNumArray;
    deptReportChartOption.series[1].data = changeNumArray;
    deptReportChartOption.series[2].data = ratioArray;
    deptReportChart.setOption(deptReportChartOption);
}
function getDeptReportData() {
    var resultDate = {};
    $.ajax({
        url: jsUseCtxPath + '/rdmZhgl/core/yfjb/reportDeptProjectData.do',
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
