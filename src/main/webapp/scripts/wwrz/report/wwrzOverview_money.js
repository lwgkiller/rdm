// 指定图表的配置项和数据
projectMoneyChartOption = {
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
        data: ['合同金额','已支付金额','费用占比（%）']
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
            name: '金额',
            axisLabel: {
                formatter: '{value} 万元'
            }
        },
        {
            type: 'value',
            name: '费用占比（%）',
            axisLabel: {
                formatter: '{value} %'
            }
        }
    ],
    series: [
        {
            name: '合同金额',
            type: 'bar',
            color:'#409eff',
            data: [2, 4, 7, 23, 25, 76, 135, 162, 32, 20],
            label: {
                show: true,
                position: 'top',
            }
        },
        {
            name: '已支付金额',
            type: 'bar',
            color:'#50d51d',
            data: [2, 4, 7, 23, 25, 76, 135, 162, 32, 20],
            label: {
                show: true,
                position: 'top',
            }
        },
        {
            name: '费用占比（%）',
            type: 'line',
            yAxisIndex: 1,
            color:'#3124d5',
            data: [],
            label: {
                show: true,
                position: 'top',
                formatter: '{c}%'
            },
        }
    ]
};

function initProjectMoneyChart() {
    var reportYear = '';
    if (mini.get("reportYear").getValue()) {
        reportYear = mini.get("reportYear").getText();
    }else{
        reportYear = new Date().getFullYear();
    }
    var resultData = getProjectMoneyData(reportYear);
    var xAxisData = [];
    var seriesData1 = [];
    var seriesData2 = [];
    var seriesData3 = [];
    for (var i = 0; i < resultData.length; i++) {
        xAxisData[i] = resultData[i].itemName;
        seriesData1[i] = resultData[i].totalMoney;
        seriesData2[i] = resultData[i].payMoney;
        seriesData3[i] = resultData[i].rate;
    }
    projectMoneyChartOption.xAxis[0].data = xAxisData;
    projectMoneyChartOption.series[0].data = seriesData1;
    projectMoneyChartOption.series[1].data = seriesData2;
    projectMoneyChartOption.series[2].data = seriesData3;
    moneyChart.setOption(projectMoneyChartOption);
}


function getProjectMoneyData(reportYear) {
    var resultDate = {};
    $.ajax({
        url: jsUseCtxPath + '/wwrz/core/report/reportMoneyData.do?reportYear=' + reportYear,
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
