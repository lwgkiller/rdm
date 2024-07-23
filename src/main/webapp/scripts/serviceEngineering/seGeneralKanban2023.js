//..装修手册个人指标
var zhuangXiuShouCeGeRenZhiBiaoRepuserIds = [];
var zhuangXiuShouCeGeRenZhiBiaoDelayRate = [];
var zhuangXiuShouCeGeRenZhiBiaoOption = {
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
            var res = '<p>责任人：' + params[0].name + '</p>';
            for (var i = params.length - 1; i >= 0; i--) {
                res += '<p><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[i].color + '"></span>';
                res += params[i].seriesName + '：' + Math.abs(params[i].data) + '（个）</p>';
            }
            var delayRateVal = zhuangXiuShouCeGeRenZhiBiaoDelayRate[params[0].dataIndex] * 100;
            delayRateVal = delayRateVal.toFixed(2);
            res += '<p style="margin-left: 12px">延误率：' + delayRateVal + '%</p>';
            return res;
        }
    },
    color: ['#1d8404', '#CD3333'],
    legend: {
        data: ['进度延误', '进度正常']
    },
    grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true,
        top: '8%',
        left: 'left'
    },
    xAxis: [
        {
            name: '数量（个）',
            nameLocation: 'center',
            nameGap: 20,
            nameRotate: 0,
            type: 'value',
            minInterval: 1,
            axisLabel: {
                formatter: function (value) {
                    return Math.abs(value);
                }
            }
        }
    ],
    yAxis: [
        {
            type: 'category',
            nameGap: 25,
            axisTick: {show: false},
            axisLabel: {
                margin: 20,
                show: true,
                interval: 0
            },
            data: []
        }
    ],
    series: [
        {
            name: '进度正常',
            type: 'bar',
            stack: '总量',
            //barWidth: 20,
            label: {
                normal: {
                    show: true,
                    position: 'right',
                    formatter: function (params) {
                        var value = params.value;
                        if (value == 0) {
                            return '';
                        }
                    }
                }
            },
            data: []
        },
        {
            name: '进度延误',
            type: 'bar',
            stack: '总量',
            //barWidth: 20,
            label: {
                normal: {
                    show: true,
                    position: 'left',
                    formatter: function (params) {
                        var value = params.value;
                        if (value != 0) {
                            return Math.abs(value);
                        } else {
                            return '';
                        }
                    }
                }
            },
            data: []
        }
    ]
};
function zhuangXiuShouCeGeRenZhiBiaoIni() {
    $.ajax({
        url: jsUseCtxPath + '/serviceEngineering/core/seGeneralKanban2023/zhuangXiuShouCeGeRenZhiBiaoIni.do',
        contentType: 'application/json',
        success: function (returnData) {
            if (returnData.success) {
                resultData = returnData.data;
                zhuangXiuShouCeGeRenZhiBiaoOption.yAxis[0].data = resultData.yAxis;
                zhuangXiuShouCeGeRenZhiBiaoOption.series[0].data = resultData.processOk;
                zhuangXiuShouCeGeRenZhiBiaoOption.series[1].data = resultData.processDelay;
                zhuangXiuShouCeGeRenZhiBiaoDelayRate = resultData.delayRate;
                zhuangXiuShouCeGeRenZhiBiaoRepuserIds = resultData.repUserIds;
                zhuangXiuShouCeGeRenZhiBiaoChat.setOption(zhuangXiuShouCeGeRenZhiBiaoOption);
            } else {
                mini.alert("加载失败:" + returnData.message);
            }
        }
    });
}
//..资料收集超期
var ziLiaoShouJiChaoQiOption = {
    tooltip: {
        trigger: 'axis',
        axisPointer: {            // 坐标轴指示器，坐标轴触发有效
            type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
        }
    },
    color: ['#0c7aa8'],
    legend: {
        data: ['超期']
    },
    grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true,
        top: '8%',
        left: 'left'
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
                fontSize: 10,
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
            name: '数\n量\n︵\n个\n︶',
            nameLocation: 'center',
            nameGap: 25,
            nameRotate: 0
        }
    ],
    series: []
};
function ziLiaoShouJiChaoQiIni() {
    ziLiaoShouJiChaoQiChat.setOption(ziLiaoShouJiChaoQiOption);
}
