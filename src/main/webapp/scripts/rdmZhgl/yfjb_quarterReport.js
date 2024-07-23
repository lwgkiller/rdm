// 指定图表的配置项和数据
deptQuarterChartOption = {
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
        data: ['一季度预计（%）', '二季度预计（%）', '三季度预计（%）', '四季度预计（%）','一季度实际（%）', '二季度实际（%）', '三季度实际（%）', '四季度实际（%）','一季度完成率（%）','二季度完成率（%）','三季度完成率（%）', '四季度完成率（%）']
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
            name: '降本率（%）',
            min: 0,
            max: 2,
            interval: 10,
            axisLabel: {
                formatter: '{value} %'
            }
        },
        {
            type: 'value',
            name: '完成率（%）',
            min: 0,
            max: 1000,
            interval: 100,
            axisLabel: {
                formatter: '{value} %'
            }
        }
    ],
    series: [
        {
            name: '一季度预计（%）',
            type: 'bar',
            data: []
        },
        {
            name: '二季度预计（%）',
            type: 'bar',
            data: []
        },
        {
            name: '三季度预计（%）',
            type: 'bar',
            data: []
        },
        {
            name: '四季度预计（%）',
            type: 'bar',
            data: []
        },
        {
            name: '一季度实际（%）',
            type: 'bar',
            data: []
        },
        {
            name: '二季度实际（%）',
            type: 'bar',
            data: []
        },
        {
            name: '三季度实际（%）',
            type: 'bar',
            data: []
        },
        {
            name: '四季度实际（%）',
            type: 'bar',
            data: []
        },
        {
            name: '一季度完成率（%）',
            type: 'line',
            yAxisIndex: 1,
            data: []
        },
        {
            name: '二季度完成率（%）',
            type: 'line',
            yAxisIndex: 1,
            data: []
        },
        {
            name: '三季度完成率（%）',
            type: 'line',
            yAxisIndex: 1,
            data: []
        },
        {
            name: '四季度完成率（%）',
            type: 'line',
            yAxisIndex: 1,
            data: []
        }
    ]
};

function initDeptQuarterReportChart() {
    var resultData = getDeptQuarterReportData();
    var deptNameArray = [];
    var quarter1RealArray = [];
    var quarter2RealArray = [];
    var quarter3RealArray = [];
    var quarter4RealArray = [];

    var quarter1PlanArray = [];
    var quarter2PlanArray = [];
    var quarter3PlanArray = [];
    var quarter4PlanArray = [];

    var quarter1RateArray = [];
    var quarter2RateArray = [];
    var quarter3RateArray = [];
    var quarter4RateArray = [];
    for(var i=0;i<resultData.length;i++){
        deptNameArray[i] = resultData[i].deptName;
        quarter1RealArray[i] = resultData[i].realTimeRateOne;
        quarter1PlanArray[i] = resultData[i].planTimeRateOne;
        quarter1RateArray[i] = resultData[i].rateOne;

        quarter2RealArray[i] = resultData[i].realTimeRateTwo;
        quarter2PlanArray[i] = resultData[i].planTimeRateTwo;
        quarter2RateArray[i] = resultData[i].rateTwo;

        quarter3RealArray[i] = resultData[i].realTimeRateThree;
        quarter3PlanArray[i] = resultData[i].planTimeRateThree;
        quarter3RateArray[i] = resultData[i].rateThree;

        quarter4RealArray[i] = resultData[i].realTimeRateFour;
        quarter4PlanArray[i] = resultData[i].planTimeRateFour;
        quarter4RateArray[i] = resultData[i].rateFour;

    }
    deptQuarterChartOption.xAxis[0].data = deptNameArray;
    deptQuarterChartOption.series[0].data = quarter1PlanArray;
    deptQuarterChartOption.series[1].data = quarter2PlanArray;
    deptQuarterChartOption.series[2].data = quarter3PlanArray;
    deptQuarterChartOption.series[3].data = quarter4PlanArray;

    deptQuarterChartOption.series[4].data = quarter1RealArray;
    deptQuarterChartOption.series[5].data = quarter2RealArray;
    deptQuarterChartOption.series[6].data = quarter3RealArray;
    deptQuarterChartOption.series[7].data = quarter4RealArray;

    deptQuarterChartOption.series[8].data = quarter1RateArray;
    deptQuarterChartOption.series[9].data = quarter2RateArray;
    deptQuarterChartOption.series[10].data = quarter3RateArray;
    deptQuarterChartOption.series[11].data = quarter4RateArray;
    quarterChart.setOption(deptQuarterChartOption);
}
function getDeptQuarterReportData() {
    var resultDate = {};
    var year = mini.get('quarterYear').getValue();
    $.ajax({
        url: jsUseCtxPath + '/rdmZhgl/core/yfjb/getQuarterData.do?year='+year,
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
