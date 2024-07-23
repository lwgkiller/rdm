// 指定图表的配置项和数据
var deptQuarterChartOption = {
    title: {
        show: false,
        text: '各所降本率统计',
        textStyle: {
            lineHeight: 80,
            height: 80,
        },
        textAlign: 'auto',
        textVerticalAlign: 'top'
    },
    tooltip: {
        trigger: 'axis',
        axisPointer: {            // 坐标轴指示器，坐标轴触发有效
            type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
        },
        formatter: function (params) {
            let res = '<p>' + params[0].name + '</p>';
            for (let i = 0; i< params.length; i++) {
                res += '<p><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[i].color + '"></span>';
                res += params[i].seriesName + '：' + params[i].data;
            }
            return res;
        }
    },
    color: ['#4f81bd','#1d8404', 'darkorange','green'],
    legend: {
        data: []
    },
    grid: {
        left: '3.5%',
        right: '3.5%',
        top: '15%',
        bottom: '2%',
        containLabel: true
    },
    xAxis: [
        {
            type: 'category',
            data: [],
            axisLabel: {
                show: true,
                interval: 0,
                rotate: 0,
                color: '#333',
                fontSize: 15,
                formatter: function (params) {
                    var newParamsName = "";
                    var paramsNameNumber = params.length;
                    var provideNumber = 6;
                    var rowNumber = Math.ceil(paramsNameNumber / provideNumber);
                    if (paramsNameNumber > provideNumber) {
                        for (var p = 0; p < rowNumber; p++) {
                            var tempStr = "";
                            var start = p * provideNumber;
                            var end = start + provideNumber;
                            if (p == rowNumber - 1) {
                                tempStr = params.substring(start, paramsNameNumber);
                            } else {
                                tempStr = params.substring(start, end) + "\n";
                            }
                            newParamsName += tempStr;
                        }

                    } else {
                        newParamsName = params;
                    }
                    return newParamsName
                }
            }
        }
    ],
    yAxis: [
        {
            type: 'value',
            name: '降\n本\n率\n%',
            nameLocation: 'center',
            nameGap: 35,
            nameRotate: 0,
            position: 'left',
            axisLabel: {
                formatter: '{value}%'
            }
        },
        {
            type: 'value',
            name: '完\n成\n率\n%',
            nameLocation: 'center',
            nameGap: 35,
            nameRotate: 0,
            position: 'right',
            axisLabel: {
                formatter: '{value}%'
            }
        }
    ],
    series: []
};

function initDeptQuarterReportChart() {
    // var resultDate = {};
    var year = mini.get('quarterYear').getValue();
    $.ajax({
        url: jsUseCtxPath + '/rdmZhgl/core/yfjb/getQuaData.do?year='+year,
        type: 'POST',
        async: false,
        contentType: 'application/json',
        success: function (resp) {
            if(!resp.success) {
                return;
            }
            let returnData = resp.data;
            if (returnData) {
                //对option进行赋值
                debugger
                deptQuarterChartOption.xAxis[0].data = returnData.xAxisData;
                deptQuarterChartOption.legend.data = returnData.legendData;

                deptQuarterChartOption.series[0] = returnData.seriesData1[0];
                deptQuarterChartOption.series[1] = returnData.seriesData2[0];
                deptQuarterChartOption.series[2] = returnData.seriesData3[0];
                deptQuarterChartOption.series[3] = returnData.seriesData4[0];

                deptQuarterChartOption.series[4] = returnData.seriesData1R[0];
                deptQuarterChartOption.series[5] = returnData.seriesData2R[0];
                deptQuarterChartOption.series[6] = returnData.seriesData3R[0];
                deptQuarterChartOption.series[7] = returnData.seriesData4R[0];

                deptQuarterChartOption.series[8] = returnData.seriesData1Rate[0];
                deptQuarterChartOption.series[9] = returnData.seriesData2Rate[0];
                deptQuarterChartOption.series[10] = returnData.seriesData3Rate[0];
                deptQuarterChartOption.series[11] = returnData.seriesData4Rate[0];
                quarterChart.setOption(deptQuarterChartOption);
            }
        }
    })
    // return resultDate;
}
