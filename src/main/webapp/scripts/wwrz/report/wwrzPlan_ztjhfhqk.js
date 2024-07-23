// 指定图表的配置项和数据
allChartOption = {
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
        }
    ],
    yAxis: [
        {
            type: 'value',
            name: '数量',
            // interval: 1,
            axisLabel: {
                formatter: '{value} 个'
            }
        },
        {
            type: 'value',
            name: '计划符合率（%）',
            axisLabel: {
                formatter: '{value} %'
            }
        }
    ],
    series: [
        {
            name: '计划提报',
            type: 'bar',
            color:'#409eff',
            data: [2, 4, 7, 23, 25, 76, 135, 162, 32, 20],
            label: {
                show: true,
                position: 'top',
            }
        },
        {
            name: '计划内执行',
            type: 'bar',
            color:'#50d51d',
            data: [2, 4, 7, 23, 25, 76, 135, 162, 32, 20],
            label: {
                show: true,
                position: 'top',
            }
        },
        {
            name: '计划外执行',
            type: 'bar',
            color:'#d54b4b',
            data: [2, 4, 7, 23, 25, 76, 135, 162, 32, 20],
            label: {
                show: true,
                position: 'top',
            }
        },
        {
            name: '计划符合率（%）',
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

function initAllChart() {
    var reportYear = '';
    if (mini.get("reportYear").getValue()) {
        reportYear = mini.get("reportYear").getText();
    }else{
        reportYear = new Date().getFullYear();
    }
    var deptId = '';
    if (mini.get("deptId").getValue()) {
        deptId = mini.get("deptId").getValue();
    }
    var resultData = getAllPlanData(reportYear,deptId);
    var xAxisData = [];
    var seriesData1 = [];
    var seriesData2 = [];
    var seriesData3 = [];
    var seriesData4 = [];
    for (var i = 0; i < resultData.length; i++) {
        xAxisData[i] = resultData[i].quarterName;
        seriesData1[i] = resultData[i].planNum;
        seriesData2[i] = resultData[i].planExeNum;
        seriesData3[i] = resultData[i].unPlanExeNum;
        seriesData4[i] = resultData[i].quartRate;
    }
    allChartOption.xAxis[0].data = xAxisData;
    allChartOption.series[0].data = seriesData1;
    allChartOption.series[1].data = seriesData2;
    allChartOption.series[2].data = seriesData3;
    allChartOption.series[3].data = seriesData4;
    allChart.setOption(allChartOption);
}


function getAllPlanData(reportYear,deptId) {
    var resultDate = {};
    $.ajax({
        url: jsUseCtxPath + '/wwrz/core/report/reportPlanData.do?reportYear=' + reportYear+'&deptId='+deptId,
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
