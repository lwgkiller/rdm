$(function () {
    // refreshAchievementType();
    zrTimeMeetingDelayInit();
});

function zrTimeMeetingDelayInit() {
    //今年1月1号到当前日期
    let nowDate = new Date();
    let nowYear = nowDate.getFullYear();
    let month = nowDate.getMonth() + 1;
    let day = nowDate.getDate();
    let startTime =nowYear + "-01-01";
    let endTime = nowYear + "-" + month + "-" + day;
    mini.get("zrRiskTimeFrom").setValue(startTime);
    mini.get("zrRiskTimeTo").setValue(endTime);
    queryZRMeetingDelayChartData();
}



// 指定图表的配置项和数据
var zrMeetingDelayOption = {
    title: {
        show: false,
        text: '会议纪要超期统计',
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
    color: ['#E0EAAA','#ff8c00','#E32121'],
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
                    var provideNumber = 4;
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
            name: '\n数\n量\n︵\n个\n︶',
            nameLocation: 'center',
            nameGap: 35,
            nameRotate: 0,
            position: 'left',
            minInterval:1
        }
    ],
    series: []
};
var zrMeetingDelaySumData = {
    name: '会议纪要超期总数',
    type: 'bar',
    stack: '会议纪要',
    label: {
        normal: {
            offset: ['50', '80'],
            show: true,
            position: 'insideBottom',
            formatter: '{c}',
            textStyle: {color: '#000'}
        }
    },
    itemStyle: {
        normal: {
            color: 'rgba(128, 128, 128, 0)'
        }
    },
    data: []
};

function queryZRMeetingDelayChartData() {
    var startTime=mini.get("zrRiskTimeFrom").getText();
    if (!startTime) {
        mini.alert("请选择开始时间");
        return;
    }
    var endTime=mini.get("zrRiskTimeTo").getText();
    if (!endTime) {
        mini.alert("请选择结束时间");
        return;
    }
    $.ajax({
        url: jsUseCtxPath + '/meeting/core/overview/queryZRMeetingDelay.do?startTime='+startTime+"&endTime="+endTime,
        contentType: 'application/json',
        success: function (resp) {
            if(!resp.success) {
                return;
            }
            let returnData = resp.data;
            if (returnData) {
                //对option进行赋值
                zrMeetingDelayOption.legend.data = returnData.legendData;
                zrMeetingDelayOption.xAxis[0].data = returnData.xAxisData;
                zrMeetingDelayOption.series = returnData.series;
                zrMeetingDelaySumData.data = returnData.sumData;
                zrMeetingDelayOption.series.push(zrMeetingDelaySumData);
                zrMeetingDelayBar.setOption(zrMeetingDelayOption);
            }
        }
    });
}

