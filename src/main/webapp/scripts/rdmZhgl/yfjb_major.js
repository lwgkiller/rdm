// 指定图表的配置项和数据
majorChartOption = {
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
        data: ['专业项目数']
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
            name: '专业项目数',
            axisLabel: {
                formatter: '{value} 个'
            }
        }
    ],
    series: [
        {
            name: '专业项目数',
            type: 'bar',
            data: [
                2.0, 4.9, 7.0, 23.2, 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3
            ]
        }
    ]
};

function initMajorChart() {
    var resultData = getMajorData();
    var majorArray = [];
    var totalNumArray = [];
    for(var i=0;i<resultData.length;i++){
        majorArray[i] = resultData[i].majorName;
        totalNumArray[i] = resultData[i].totalSum;
    }
    majorChartOption.xAxis[0].data = majorArray;
    majorChartOption.series[0].data = totalNumArray;
    majorReport.setOption(majorChartOption);
}
function getMajorData() {
    var resultDate = {};
    $.ajax({
        url: jsUseCtxPath + '/rdmZhgl/core/yfjb/reportMajorData.do',
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
