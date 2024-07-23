// 指定图表的配置项和数据
gszhChartOption = {
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            type: 'shadow'
        }
    },
    legend: {
        data: ['计划', '未完成', '完成率(%)','平均值(%)']
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
            data: ['大挖', '中挖','小挖', '特挖研究所', '国际化', '智控', '标准所', '仿真', '测试', '服务工程', '工程中心','技术管理部'],
        }
    ],
    yAxis: [
        {
            type: 'value',
            name: '数量',
            axisLabel: {
                formatter: '{value} 个'
            }
        },
        {
            type: 'value',
            name: '完成率(%)',
            axisLabel: {
                formatter: '{value} %'
            }
        }
    ],
    series: [
        {
            name: '计划',
            type: 'bar',
            color:'green',
            data: [2, 4, 7, 23, 25, 76, 135, 162, 32, 20, 6, 3],
            label: {
                show: true,
                position: 'top',
            }
        },
        {
            name: '未完成',
            type: 'bar',
            color:'red',
            data: [2, 4, 7, 23, 25, 76, 135, 162, 32, 20, 6, 3],
            label: {
                show: true,
                position: 'top'
            }
        },
        {
            name: '完成率(%)',
            type: 'line',
            yAxisIndex: 1,
            data: [60, 70, 80, 30, 90,60,10,90,90,50,50,40 ],
            label: {
                show: true,
                position: 'top',
            }
        },
        {
            name: '平均值(%)',
            type: 'line',
            yAxisIndex: 1,
            data: [2, 15, 20, 20, 20,10,10,10,10,10,10,10 ]
        }
    ]
};
function initGsjhChart() {
    var yearMonth = '';
    if (mini.get("yearMonth").getValue()) {
        yearMonth = mini.get("yearMonth").getText();
    }
    var resultData = getGsjhData(yearMonth);
    var xAxisData = [];
    var seriesData1 = [];
    var seriesData2 = [];
    var seriesData3 = [];
    var seriesData4 = [];
    for(var i=0;i<resultData.length;i++){
        xAxisData[i] = resultData[i].deptName;
        seriesData1[i] = resultData[i].totalNum;
        seriesData2[i] = resultData[i].unFinishNum;
        seriesData3[i] = resultData[i].finishRate.toFixed(0);
        seriesData4[i] = resultData[i].avgFinishRate.toFixed(0);
    }
    gszhChartOption.xAxis[0].data = xAxisData;
    gszhChartOption.series[0].data = seriesData1;
    gszhChartOption.series[1].data = seriesData2;
    gszhChartOption.series[2].data = seriesData3;
    gszhChartOption.series[3].data = seriesData4;
    gsjhChart.setOption(gszhChartOption);
}

function getGsjhData(yearMonth) {
    var resultDate = {};
    $.ajax({
        url: jsUseCtxPath + '/rdmZhgl/core/ndkfjh/plan/reportGsjhData.do?yearMonth=' + yearMonth,
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
